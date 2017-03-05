package icetone.extras.chooser;

import java.util.prefs.Preferences;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;

import icetone.core.BaseScreen;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.extras.chooser.ChooserPanel.ChooserView;
import icetone.extras.windows.PersistentWindow;
import icetone.extras.windows.SaveType;

/**
 * The {@link AbstractChooserDialog} provides a window for selecting one or more
 * entries from a hierarchical list of strings. The hierarchy is determined by
 * the '/' in the paths supplied.
 * <p>
 * The window is split into two parts, the left hand side lists the available
 * 'folders', i.e. all of the parents derived from the list of provided resource
 * path strings. The right hand side is used to list the resources in the
 * folder. The presentation for this is provided by an implementor of
 * {@link ChooserView}.
 */
public abstract class AbstractChooserDialog<I> extends PersistentWindow {

	protected final ChooserView<I> view;
	protected final ChooserPanel<I> panel;
	protected boolean chosen;
	protected ChooserModel<I> resources;
	protected ChangeSupport<AbstractChooserDialog<I>, I> changeSupport;

	public AbstractChooserDialog(final BaseScreen screen, String styleId, String title,
			ChooserModel<I> resources, Preferences pref, ChooserView<I> view) {
		super(screen, styleId, VAlign.Center, Align.Right, null, true, SaveType.SIZE, pref);
		this.view = view;
		this.resources = resources;
		panel = createPanel();
		panel.onChange((evt) -> {
			choose(evt.getNewValue(), evt.getOldValue(), evt.isTemporary());
		});
		setWindowTitle(title);
		setDestroyOnHide(true);
		setResizable(true);
	}

	public AbstractChooserDialog<I> addChangeListener(UIChangeListener<AbstractChooserDialog<I>, I> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public AbstractChooserDialog<I> onChange(UIChangeListener<AbstractChooserDialog<I>, I> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public AbstractChooserDialog<I> removeChangeListener(UIChangeListener<AbstractChooserDialog<I>, I> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
	}

	public AbstractChooserDialog<I> unbindChanged(UIChangeListener<AbstractChooserDialog<I>, I> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	public ChooserPanel<I> getChooser() {
		return panel;
	}

	public void setResources(ChooserModel<I> resources) {
		this.resources = resources;
		panel.setResources(resources);
	}

	protected abstract ChooserPanel<I> createPanel();

	public ChooserView<I> getView() {
		return panel.getView();
	}

	public I getSelected() {
		return panel.getSelected();
	}

	@Override
	protected final void onCloseWindow() {
		super.onCloseWindow();
		if (!chosen) {
			choose(null, getSelected(), false);
		} else {
			chosen = false;
		}
	}

	public void setSelectedFile(I file) {
		panel.setSelectedFile(file);
	}

	public final void setFolder(I folder) {
		panel.setFolder(folder);
	}

	protected void choose(I file, I old, boolean temporary) {
		if (changeSupport != null) {
			changeSupport.fireEvent(new UIChangeEvent<AbstractChooserDialog<I>, I>(this, old, file).setTemporary(temporary));
		}
	}
}
