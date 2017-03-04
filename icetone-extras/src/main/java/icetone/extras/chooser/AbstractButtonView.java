package icetone.extras.chooser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.jme3.font.BitmapFont.Align;

import icetone.controls.buttons.SelectableItem;
import icetone.controls.scrolling.ScrollBar;
import icetone.controls.scrolling.ScrollPanel;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout;
import icetone.core.Size;
import icetone.core.Element;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.WrappingLayout;

/**
 */
public abstract class AbstractButtonView<I> implements ChooserPanel.ChooserView<I> {

	protected float previewSize = 64;
	protected Element scrollContent;
	protected ScrollPanel scrollPanel;
	protected ScrollBar vScrollBar;
	protected final ElementManager<?> screen;
	protected ChooserPanel<I> chooser;
	protected I cwd;
	private Map<I, SelectableItem> items = Collections.synchronizedMap(new HashMap<I, SelectableItem>());
	private String styleClass;
	private boolean rebuilding;

	public AbstractButtonView(String styleClass, ElementManager<?> screen) {
		this.screen = screen;
		this.styleClass = styleClass;
	}

	public ChooserPanel<I> getChooser() {
		return chooser;
	}

	public BaseElement createView(ChooserPanel<I> chooser) {
		this.chooser = chooser;
		scrollPanel = new ScrollPanel(screen) {
			{
				setStyleClass("chooser-view " + AbstractButtonView.this.styleClass);
			};
		};
		scrollPanel.setPreferredDimensions(new Size(400, 400));
		scrollPanel.setScrollContentLayout(createLayout());
		scrollContent = scrollPanel.getScrollableArea();
		vScrollBar = scrollPanel.getVerticalScrollBar();
		return scrollPanel;
	}

	public void setIsEnabled(boolean enabled) {
		scrollPanel.setEnabled(enabled);
	}

	protected Layout<?, ?> createLayout() {
		return new WrappingLayout().setEqualSizeCells(true);
	}

	public void rebuild(I cwd, Collection<I> filesNames) {
		if (rebuilding) {
			throw new IllegalStateException("Already rebuilding");
		}
		rebuilding = true;
		this.cwd = cwd;
		items.clear();
		screen.getApplication().enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				chooser.busy();
				return null;
			}
		});

		for (I s : filesNames) {
			Thread.yield();
			final SelectableItem uib = new SelectableItem(screen);
			uib.setLayoutManager(new FlowLayout(0, Align.Left));
			configureButton(uib, s);
			items.put(s, uib);
		}
		rebuilding = false;
		screen.getApplication().enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				scrollPanel.invalidate();
				scrollContent.removeAllChildren();
				List<SelectableItem> is = new ArrayList<>();
				synchronized (items) {
					is.addAll(items.values());
				}
				for (SelectableItem it : is)
					scrollPanel.addScrollableContent(it);
				scrollPanel.validate();
				chooser.idle();
				return null;
			}
		});
	}

	public void select(I file) {
		for (Map.Entry<I, SelectableItem> en : items.entrySet()) {
			if (en.getKey().equals(file)) {
				en.getValue().runAdjusting(() -> en.getValue().setIsToggled(true));
			} else {
				en.getValue().runAdjusting(() -> en.getValue().setIsToggled(false));
			}
		}
	}

	protected void configureButton(SelectableItem item, I path) {

		if (path.equals(chooser.getSelected())) {
			item.setIsToggled(true);
		}
		item.onMouseHeld(evt -> {
			chooser.setSelectedFile(path);
			chooser.choose(path);
		});
		item.onMouseReleased(evt -> {
			if (evt.getClicks() == 2) {
				chooser.choose(path);
			}
		});
		item.onChange((evt) -> {
			if (!evt.getSource().isAdjusting())
				chooser.setSelectedFile(path);
		});
	}

}
