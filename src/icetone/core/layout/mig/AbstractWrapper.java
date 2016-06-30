package icetone.core.layout.mig;

import static net.miginfocom.layout.ComponentWrapper.TYPE_UNSET;

import com.jme3.font.BitmapFont;

import icetone.core.ElementManager;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.PlatformDefaults;

public abstract class AbstractWrapper {
    
    private final Object wrappedComponent;
    
    protected final ElementManager screen;
    
    private int compType = TYPE_UNSET;
    
    protected AbstractWrapper(Object wrappedComponent, ElementManager screen) {
        this.wrappedComponent = wrappedComponent;
        this.screen = screen;
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

    public final Object getComponent() {
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
        return (int)screen.getWidth();
    }

    public final int getScreenHeight() {
        return (int)screen.getHeight();
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

    

    public final float getPixelUnitFactor(boolean isHor) {
        switch (PlatformDefaults.getLogicalPixelBase()) {
            case PlatformDefaults.BASE_FONT_SIZE:
                BitmapFont bmf = getFont();
                if(bmf == null) {
                    return 1f;
                }
                float f = isHor ? bmf.getLineWidth("W") / 5f : bmf.getPreferredSize() / 13f;
                return f;
            case PlatformDefaults.BASE_SCALE_FACTOR:
                Float s = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
                if (s != null) {
                    return s.floatValue();
                }
                return (isHor ? getHorizontalScreenDPI() : getVerticalScreenDPI()) / (float) PlatformDefaults.getDefaultDPI();

            default:
                return 1f;
        }
    }

    public final void paintDebugOutline() {
        // TODO
    }

    public final void paintDebugCell(int x, int y, int width, int height) {
    }
    
    abstract BitmapFont getFont();
}
