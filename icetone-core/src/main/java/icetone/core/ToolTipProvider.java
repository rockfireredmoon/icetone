package icetone.core;

import com.jme3.math.Vector2f;

public interface ToolTipProvider {

    BaseElement createToolTip(Vector2f mouseXY, BaseElement el);
}
