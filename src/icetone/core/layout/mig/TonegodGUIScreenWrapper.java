package icetone.core.layout.mig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;

import icetone.controls.util.ToolTip;
import icetone.core.Element;
import icetone.core.Screen;
import icetone.core.layout.LayoutAware;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

public final class TonegodGUIScreenWrapper extends AbstractWrapper implements ContainerWrapper {


    public TonegodGUIScreenWrapper(Screen screen) {
        super(screen, screen);
    }

    public ComponentWrapper[] getComponents() {
        Collection<Element> cons = screen.getElements();
        List<ComponentWrapper> elementCons = new ArrayList<ComponentWrapper>();
        for (Element s : cons) {
            if (!(s instanceof ToolTip)) {
                int t = MigLayout.checkType(s);
                if (t == TYPE_CONTAINER) {
                    elementCons.add(new TonegodGUIContainerWrapper((Element) s));
                } else {
                    elementCons.add(new TonegodGUIComponentWrapper((Element) s));
                }
            }
        }
        return elementCons.toArray(new ComponentWrapper[elementCons.size()]);
    }

    public int getComponentCount() {
        return screen.getElements().size();
    }

    public Object getLayout() {
        return screen instanceof LayoutAware ? ((LayoutAware) screen).getLayoutManager() : null;
    }

    public final boolean isLeftToRight() {
        return true;
    }

    @Override
    public int getComponetType(boolean disregardScrollPane) {
        return TYPE_CONTAINER;
    }

    public int getLayoutHashCode() {
        Vector2f sz = new Vector2f(screen.getWidth(), screen.getHeight());
        int h = (int) sz.x + ((int) sz.y << 12);
        h += 1324511;
        if (isLeftToRight()) {
            h |= (1 << 26);
        }
        return h;
    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

    public int getWidth() {
        return (int) screen.getWidth();
    }

    public int getHeight() {
        return (int) screen.getHeight();
    }

    public int getScreenLocationX() {
        return 0;
    }

    public int getScreenLocationY() {
        return 0;
    }

    public int getMinimumWidth(int i) {
        return (int) screen.getWidth();
    }

    public int getMinimumHeight(int i) {
        return (int) screen.getHeight();
    }

    public int getPreferredWidth(int i) {
        return (int) screen.getWidth();
    }

    public int getPreferredHeight(int i) {
        return (int) screen.getHeight();
    }

    public int getMaximumWidth(int i) {
        return (int) screen.getWidth();
    }

    public int getMaximumHeight(int i) {
        return (int) screen.getHeight();
    }

    public void setBounds(int i, int i1, int i2, int i3) {
        // Cannot set bounds of screen
    }

    public boolean isVisible() {
        return true;
    }

    public ContainerWrapper getParent() {
        return null;
    }

    public int[] getVisualPadding() {
        return null;
    }

    @Override
    BitmapFont getFont() {
        return null;
    }
}
