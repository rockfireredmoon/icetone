package icetone.xhtml.scene;

import java.util.Arrays;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.ElementManager;

/**
 * A rectangle wrapped in an {@link Element}. Allows the rectangle to be added as an
 * ordinary Tonegod element.
 */
public class RectangleElement extends PathElement {

    public RectangleElement(ElementManager screen, Vector2f location, Vector2f dimensions, ColorRGBA color) {
        this(screen, location, dimensions, color, 1);
    }

    public RectangleElement(ElementManager screen, Vector2f location, Vector2f dimensions, ColorRGBA color, float lineWidth) {
        super(screen,
                location, Arrays.asList(new Vector2f(0, 0),
                new Vector2f(dimensions.x - 1, 0),
                new Vector2f(dimensions.x - 1, dimensions.y - 1),
                new Vector2f(0, dimensions.y - 1)), color, lineWidth);
    }
}
