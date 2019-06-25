package icetone.core.layout.mig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import icetone.controls.text.ToolTip;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.ElementContainer;
import icetone.text.FontSpec;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

public final class TonegodGUIScreenWrapper extends AbstractWrapper<ElementContainer<?, ?>> implements ContainerWrapper {

	public TonegodGUIScreenWrapper(BaseScreen screen) {
		super(screen, screen);
	}

	@Override
	public ComponentWrapper[] getComponents() {
		Collection<BaseElement> cons = screen.getElements();
		List<ComponentWrapper> elementCons = new ArrayList<ComponentWrapper>();
		for (BaseElement s : cons) {
			if (!(s instanceof ToolTip)) {
				int t = MigLayout.checkType(s);
				if (t == TYPE_CONTAINER) {
					elementCons.add(new TonegodGUIContainerWrapper(s));
				} else {
					elementCons.add(new TonegodGUIComponentWrapper(s));
				}
			}
		}
		return elementCons.toArray(new ComponentWrapper[elementCons.size()]);
	}

	@Override
	public int getComponentCount() {
		return screen.getElements().size();
	}

	@Override
	public Object getLayout() {
		return screen.getLayoutManager();
	}

	@Override
	public final boolean isLeftToRight() {
		return true;
	}

	@Override
	public int getComponetType(boolean disregardScrollPane) {
		return TYPE_CONTAINER;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return (int) screen.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) screen.getHeight();
	}

	@Override
	public int getScreenLocationX() {
		return 0;
	}

	@Override
	public int getScreenLocationY() {
		return 0;
	}

	@Override
	public int getMinimumWidth(int i) {
		return (int) screen.getWidth();
	}

	@Override
	public int getMinimumHeight(int i) {
		return (int) screen.getHeight();
	}

	@Override
	public int getPreferredWidth(int i) {
		return (int) screen.getWidth();
	}

	@Override
	public int getPreferredHeight(int i) {
		return (int) screen.getHeight();
	}

	@Override
	public int getMaximumWidth(int i) {
		return (int) screen.getWidth();
	}

	@Override
	public int getMaximumHeight(int i) {
		return (int) screen.getHeight();
	}

	@Override
	public void setBounds(int i, int i1, int i2, int i3) {
		// Cannot set bounds of screen
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public ContainerWrapper getParent() {
		return null;
	}

	@Override
	public int[] getVisualPadding() {
		return null;
	}

	@Override
	FontSpec getFont() {
		return BaseElement.calcFont(screen);
	}
}
