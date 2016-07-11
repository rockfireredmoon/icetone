package icetone.core;

import com.jme3.math.Vector2f;

public interface ToolTipProvider {

    Element createToolTip(Vector2f mouseXY, Element el);
}
