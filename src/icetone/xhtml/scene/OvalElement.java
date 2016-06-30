package icetone.xhtml.scene;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import icetone.core.ElementManager;

/**
 * A circle wrapped in an {@link Element}. Allows the circle to be added as an ordinary
 * Tonegod element.
 *
 * <a
 * href="http://hub.jmonkeyengine.org/forum/topic/drawing-a-simple-circle-in-jme3/page/2/">See
 * JME forums.</a>
 */
public class OvalElement extends AbstractMeshElement {

    public OvalElement(ElementManager screen, Vector2f p1, Vector2f radius, ColorRGBA color) {
        this(screen, p1, radius, color, 1);
    }

    public OvalElement(ElementManager screen, Vector2f p1, Vector2f radius, ColorRGBA color, float lineWidth) {
        super(screen,
                p1, new Vector2f(radius.x * 2f, radius.y * 2f));
        Oval2d l1 = new Oval2d(new Vector3f(radius.x, radius.y, 0f), radius, 16);
        l1.setLineWidth(lineWidth);
        Geometry geom = new Geometry(getUID() + ":geom", l1);
        meshMaterial.setColor("Color", color);
        geom.setMaterial(meshMaterial);
        attachChild(geom);
    }
}
