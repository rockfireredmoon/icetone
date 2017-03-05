package icetone.xhtml.scene;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Size;

/**
 * A {@link Line} wrapped in an {@link BaseElement}. Allows a line to be added as an
 * ordinary Tonegod element.
 */
public class LineElement extends AbstractMeshElement {

	public LineElement(BaseScreen screen, Vector2f p1, Vector2f p2, ColorRGBA color) {
		this(screen, p1, p2, color, 1f);
	}

	public LineElement(BaseScreen screen, Vector2f p1, Vector2f p2, ColorRGBA color, float lineWidth) {
		super(screen, p1, new Size(p2.subtract(p1)));

		Line l1 = new Line(new Vector3f(0, 0, 0), new Vector3f(p2.x - p1.x, p2.y - p1.y, 0));
		l1.setLineWidth(lineWidth);
		Geometry geom = new Geometry("A shape", l1);

		meshMaterial.setColor("Color", color);
		geom.setMaterial(meshMaterial);
		attachChild(geom);
	}
}
