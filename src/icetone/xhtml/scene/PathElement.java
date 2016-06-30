package icetone.xhtml.scene;

import java.util.Collection;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;

import icetone.core.Element;
import icetone.core.ElementManager;

/**
 * A rectangle wrapped in an {@link Element}. Allows the rectangle to be added as an
 * ordinary Tonegod element.
 */
public class PathElement extends AbstractMeshElement {

    public PathElement(ElementManager screen, Vector2f location, Collection<Vector2f> points, ColorRGBA color) {
        this(screen, location, points, color, 1);
    }

    public PathElement(ElementManager screen, Vector2f location, Collection<Vector2f> points, ColorRGBA color, float lineWidth) {
        this(screen, location, points, color, lineWidth, false);
    }
        
    public PathElement(ElementManager screen, Vector2f location, Collection<Vector2f> points, ColorRGBA color, float lineWidth, boolean close) {
        super(screen,
                location, getBounds(points));
        Path l1 = new Path(points, close);
        l1.setLineWidth(lineWidth);
        Geometry geom = new Geometry(getUID() + ":geom", l1);
        meshMaterial.setColor("Color", color);
        geom.setMaterial(meshMaterial);
        attachChild(geom);
    }

    public static Vector2f getBounds(Collection<Vector2f> a) {
        Vector2f b = new Vector2f();
        for (Vector2f i : a) {
            b.x = Math.max(i.x, b.x);
            b.y = Math.max(i.y, b.y);
        }
        return b;
    }

}
