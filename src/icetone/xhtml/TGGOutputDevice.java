package icetone.xhtml;

import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.render.AbstractOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.BorderPainter;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.TextDecoration;
import org.xhtmlrenderer.util.XRLog;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;

/**
 * Adapts flying saucer's drawing operations (except text) to the 'canvas'.
 */
public class TGGOutputDevice extends AbstractOutputDevice {

	private final TGGCanvas canvas;
	private Area clip = null;
	private Stroke stroke = null;

	public TGGOutputDevice(TGGCanvas canvas) {
		this.canvas = canvas;
	}

	/**
	 * Get the canvas the output device will 'draw' on.
	 *
	 * @return canvas
	 */
	public TGGCanvas getCanvas() {
		return canvas;
	}

	@Override
    public void drawTextDecoration(
            RenderingContext c, InlineLayoutBox iB, TextDecoration decoration) {
        setColor(iB.getStyle().getColor());

        Rectangle edge = iB.getContentAreaEdge(iB.getAbsX(), iB.getAbsY(), c);

        fillRect(edge.x, iB.getAbsY() + decoration.getOffset(),
                    edge.width, decoration.getThickness());
//        fillRect(edge.x, iB.getAbsY(),
//              edge.width, decoration.getThickness());
    }

	@Override
	public void clip(Shape s) {
		if (s == null) {
			return;
		}
		if (clip == null) {
			setClip(s);
		} else {
			Area a = new Area(clip);
			a.intersect(new Area(s));
			setClip(a);
		}
	}

	@Override
	public void setClip(Shape s) {
		if (s == null) {
			canvas.setClip(null);
		} else if (s instanceof Rectangle) {
			Rectangle r = (Rectangle) s;
			canvas.setClip(new Vector4f(r.x, r.y, r.width, r.height));
		} else if (s instanceof Area) {
			// TODO not really ideal
			Rectangle r = s.getBounds();
			canvas.setClip(new Vector4f(r.x, r.y, r.width, r.height));
		} else {
			XRLog.render(Level.WARNING, "Unknown clipping shape " + s.getClass());
		}
		clip = (s == null ? null : new Area(s));
	}

	@Override
	public Shape getClip() {
		return clip;
	}

	@Override
	public void draw(Shape s) {

		// TODO actually fill instead of just draw lines
		if (s instanceof Polygon) {
			Polygon p = (Polygon) s;
			List<Vector2f> pa = new ArrayList<Vector2f>();
			Vector2f min = new Vector2f(p.getBounds().x, p.getBounds().y);
			for (int i = 0; i < p.xpoints.length; i++) {
				final Vector2f v = new Vector2f(p.xpoints[i] - min.x, p.ypoints[i] - min.y);
				pa.add(v);
			}
			canvas.drawPath((int) min.x, (int) min.y, pa);
		} else if (s instanceof Area) {
			Area a = (Area) s;
			draw(a.getBounds());
		} else {
			XRLog.render(Level.WARNING, "Unsupported fill shape. " + s.getClass());
		}

	}

	@Override
	public void drawBorderLine(Shape shape, int side, int lineWidth, boolean solid) {

		int adj = solid ? 1 : 0;
		int offset = (lineWidth / 2);
		
		if (shape instanceof Area) {
			shape = shape.getBounds();
		}

		if (shape instanceof Rectangle) {
			Rectangle bounds = (Rectangle) shape;
			int x = bounds.x;
			int y = bounds.y;
			int w = bounds.width;
			int h = bounds.height;


			if (side == BorderPainter.TOP) {
				drawLine(x, y + offset, x + w - adj, y + offset);
			} else if (side == BorderPainter.LEFT) {
				drawLine(x + offset, y, x + offset, y + h - adj);
			} else if (side == BorderPainter.RIGHT) {
				if (lineWidth % 2 != 0) {
					offset += 1;
				}
				drawLine(x + w - offset, y, x + w - offset, y + h - adj);
			} else if (side == BorderPainter.BOTTOM) {
				if (lineWidth % 2 != 0) {
					offset += 1;
				}
				drawLine(x, y + h - offset, x + w - adj, y + h - offset);
			}
		} else if (shape instanceof Path2D) {
			Path2D p2d = (Path2D)shape;
			float[] data = new float[6];
			int type = 0;
			float startX = 0;
			float startY = 0;
			int thisX = 0;
			int thisY = 0;
			for(PathIterator it = p2d.getPathIterator(null); !it.isDone(); ) {
				it.next();
				type = it.currentSegment(data);
				System.out.println("PATH: " + type + " D0: " + data[0] + " D1: " + data[1] + " D2: " + data[2] + " D3: " + data[3] + " D4: " + data[4] + " D5: " + data[5] );
				switch(type) {
				case PathIterator.SEG_MOVETO:
					startX = data[0];
					startY = data[1];
					break;
				case PathIterator.SEG_LINETO:
					thisX = (int)data[0];
					thisY = (int)data[1];
					
					drawLine((int)startX + offset, (int)startY + offset, thisX + offset - adj, thisY + offset - adj);
					
					// TODO is this right?
					break;
				case PathIterator.SEG_CLOSE:
					drawLine(thisX + offset, thisY + offset, (int)startX + offset - adj, (int)startY + offset - adj);

					// TODO is this right?
					thisX = (int)startX;
					thisY = (int)startY;
					break;
				default:
					XRLog.render(Level.WARNING, "Unsupported path segment type. " + type);
					break;
				}
			}
		} else {
			XRLog.render(Level.WARNING, "Unsupported draw border line shape. " + shape.getClass());
		}
	}

	@Override
	public void drawImage(FSImage image, int x, int y) {
		Image img = ((TGGFSImage) image).getImage();
		if (img == null) {
			int width = image.getWidth();
			int height = image.getHeight();
			ColorRGBA oldBG = canvas.getBackground();
			ColorRGBA oldFG = canvas.getForeground();
			canvas.setBackground(ColorRGBA.Black);
			canvas.setForeground(ColorRGBA.White);
			canvas.setBackground(ColorRGBA.randomColor());
			canvas.setForeground(ColorRGBA.randomColor());
			canvas.fillRectangle(x, y, width, height);
			canvas.drawRectangle(x, y, width, height);
			canvas.drawLine(x, y, x + width - 1, y + height - 1);
			canvas.drawLine(x, y + height - 1, x + width - 1, y);
			canvas.setBackground(oldBG);
			canvas.setForeground(oldFG);
		} else {
			canvas.drawImage(img, x, y, image.getWidth(), image.getHeight());
		}
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		canvas.drawOval(x, y, width, height);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		canvas.drawRectangle(x, y, width, height);
	}

	@Override
	public void fill(Shape s) {
		// TODO actually fill instead of just draw lines
		if (s instanceof Polygon) {
			Polygon p = (Polygon) s;
			List<Vector2f> pa = new ArrayList<Vector2f>();
			Vector2f min = new Vector2f(p.getBounds().x, p.getBounds().y);
			for (int i = 0; i < p.xpoints.length; i++) {
				final Vector2f v = new Vector2f(p.xpoints[i] - min.x, p.ypoints[i] - min.y);
				pa.add(v);
			}
			canvas.drawPath((int) min.x, (int) min.y, pa);
		} else if (s instanceof Area) {
			// TODO newer FS uses this instead of rectangle
			Rectangle b = s.getBounds();
			fillRect(b.x, b.y, b.width, b.height);
		} else {
			Rectangle b = s.getBounds();
			fillRect(b.x, b.y, b.width, b.height);
			XRLog.render(Level.WARNING, "Unsupported fill shape. " + s.getClass());
		}
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		canvas.fillOval(x, y, width, height);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		canvas.fillRectangle(x, y, width, height);
	}

	@Override
	public void paintReplacedElement(RenderingContext c, BlockBox box) {
		ReplacedElement replaced = box.getReplacedElement();
		java.awt.Point location = replaced.getLocation();
		if (replaced instanceof TGGImageReplacedElement) {
			drawImage(((TGGImageReplacedElement) replaced).getImage(), location.x, location.y);
		} else if (replaced instanceof TGGFormControlReplacementElement) {
			canvas.drawControl((TGGFormControlReplacementElement) replaced, (int) location.x, (int) location.y);
		}
	}

	@Override
	public void setFont(FSFont font) {
		canvas.setDrawFont((TGGFSFont) font);
	}

	@Override
	public void setColor(FSColor color) {
		if (color instanceof FSRGBColor) {
			FSRGBColor rgb = (FSRGBColor) color;
			ColorRGBA col = new ColorRGBA((float) rgb.getRed() / 255f, (float) rgb.getGreen() / 255f, (float) rgb.getBlue() / 255f,
					1f);
			canvas.setBackground(col);
			canvas.setForeground(col);
		} else {
			throw new RuntimeException("internal error: unsupported color class " + color.getClass().getName());
		}

	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public void setStroke(Stroke s) {
		stroke = s;
		if (s != null && s instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) s;
			canvas.setStrokeWidth(bs.getLineWidth());
		} else {
			if (s != null) {
				XRLog.render(Level.WARNING, "Unsupported stroke. " + s.getClass());
			}
			canvas.setStrokeWidth(1);
		}
	}

	@Override
	public void translate(double tx, double ty) {
		canvas.translate(tx, ty);
	}

	@Override
	public Object getRenderingHint(Key key) {
		return null;
	}

	@Override
	public void setRenderingHint(Key key, Object value) {
	}

	@Override
	public void drawSelection(RenderingContext c, InlineText inlineText) {
	}

	@Override
	public boolean isSupportsSelection() {
		return true;
	}

	@Override
	public boolean isSupportsCMYKColors() {
		return false;
	}

	@Override
	protected void drawLine(int x1, int y1, int x2, int y2) {
		canvas.drawLine(x1, y1, x2, y2);
	}

}
