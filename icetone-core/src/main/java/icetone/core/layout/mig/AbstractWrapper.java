package icetone.core.layout.mig;

import static net.miginfocom.layout.ComponentWrapper.TYPE_UNSET;

import com.jme3.font.BitmapFont;

import icetone.core.ElementContainer;
import icetone.core.ElementManager;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.PlatformDefaults;

public abstract class AbstractWrapper<C extends ElementContainer<?, ?>> {

	private final C wrappedComponent;

	protected final ElementManager screen;

	private int compType = TYPE_UNSET;

	protected AbstractWrapper(C wrappedComponent, ElementManager screen) {
		this.wrappedComponent = wrappedComponent;
		this.screen = screen;
	}

	public int getComponentType(boolean disregardScrollPane) {
		return getComponetType(disregardScrollPane);
	}

	public int getContentBias() {
		return -1;
	}

	public int getComponetType(boolean disregardScrollPane) {
		if (compType == TYPE_UNSET) {
			compType = MigLayout.checkType(wrappedComponent);
		}

		return compType;
	}

	@Override
	public final int hashCode() {
		return wrappedComponent.hashCode();
	}

	public final C getComponent() {
		return wrappedComponent;
	}

	@Override
	public final boolean equals(Object o) {
		if (o == null || o instanceof ComponentWrapper == false) {
			return false;
		}

		return getComponent().equals(((ComponentWrapper) o).getComponent());
	}

	public final int getHorizontalScreenDPI() {
		// TODO
		return PlatformDefaults.getDefaultDPI();
	}

	public final int getVerticalScreenDPI() {
		// TODO
		return PlatformDefaults.getDefaultDPI();
	}

	public final int getScreenWidth() {
		return (int) screen.getWidth();
	}

	public final int getScreenHeight() {
		return (int) screen.getHeight();
	}

	public final boolean hasBaseline() {
		return false;
	}

	public final String getLinkId() {
		return null;
	}

	public final int getBaseline(int width, int height) {
		return -1;
	}

	public int getLayoutHashCode() {
		int h = Long.valueOf(screen == null ? 0 : screen.getLayoutCounter()).hashCode();
		h ^= getComponent().hashCode();
		return h;
	}

	public final float getPixelUnitFactor(boolean isHor) {
		switch (PlatformDefaults.getLogicalPixelBase()) {
		case PlatformDefaults.BASE_FONT_SIZE:
			BitmapFont bmf = getFont();
			if (bmf == null) {
				return 1f;
			}
			float f = isHor ? bmf.getLineWidth("W") / 5f : bmf.getPreferredSize() / 13f;
			return f;
		case PlatformDefaults.BASE_SCALE_FACTOR:
			Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
			if (s != null) {
				return s.floatValue();
			}
			return (isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI())
					/ (float) PlatformDefaults.getDefaultDPI();

		default:
			return 1f;
		}
	}

	public final void paintDebugOutline() {
		// TODO
	}

	public final void paintDebugCell(int x, int y, int width, int height) {
	}

	public void paintDebugOutline(boolean showVisualPadding) {
	}

	abstract BitmapFont getFont();
}
