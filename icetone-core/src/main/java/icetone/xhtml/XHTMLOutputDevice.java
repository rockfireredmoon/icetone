/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
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
import org.xhtmlrenderer.extend.FSGlyphVector;
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
public class XHTMLOutputDevice extends AbstractOutputDevice {

	private final XHTMLCanvas canvas;
	private Area clip = null;
	private Stroke stroke = null;
	private XHTMLRenderer renderer;

	public XHTMLOutputDevice(XHTMLRenderer renderer, XHTMLCanvas canvas) {
		this.canvas = canvas;
		this.renderer = renderer;
	}

	/**
	 * Get the canvas the output device will 'draw' on.
	 *
	 * @return canvas
	 */
	public XHTMLCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void drawTextDecoration(RenderingContext c, InlineLayoutBox iB, TextDecoration decoration) {
		setColor(iB.getStyle().getColor());
		Rectangle edge = iB.getContentAreaEdge(iB.getAbsX(), iB.getAbsY(), c);
		fillRect(edge.x, iB.getAbsY() + decoration.getOffset(), edge.width, decoration.getThickness());
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
			Path2D p2d = (Path2D) shape;
			float[] data = new float[6];
			int type = 0;
			float startX = 0;
			float startY = 0;
			int thisX = 0;
			int thisY = 0;
			for (PathIterator it = p2d.getPathIterator(null); !it.isDone();) {
				it.next();
				type = it.currentSegment(data);
				switch (type) {
				case PathIterator.SEG_MOVETO:
					startX = data[0];
					startY = data[1];
					break;
				case PathIterator.SEG_LINETO:
					thisX = (int) data[0];
					thisY = (int) data[1];

					drawLine((int) startX + offset, (int) startY + offset, thisX + offset - adj, thisY + offset - adj);

					// TODO is this right?
					break;
				case PathIterator.SEG_CLOSE:
					drawLine(thisX + offset, thisY + offset, (int) startX + offset - adj, (int) startY + offset - adj);

					// TODO is this right?
					thisX = (int) startX;
					thisY = (int) startY;
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
		Image img = ((XHTMLFSImage) image).getImage();
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
		if (replaced instanceof XHTMLImageReplacedElement) {
			drawImage(((XHTMLImageReplacedElement) replaced).getImage(), location.x, location.y);
		} else if (replaced instanceof XHTMLFormControlReplacementElement) {
			canvas.drawControl((XHTMLFormControlReplacementElement) replaced, location.x, location.y);
		}
	}

	@Override
	public void setFont(FSFont font) {
		canvas.setDrawFont((XHTMLFSFont) font);
	}

	@Override
	public void setColor(FSColor color) {
		if (color instanceof FSRGBColor) {
			FSRGBColor rgb = (FSRGBColor) color;
			ColorRGBA col = new ColorRGBA(rgb.getRed() / 255f, rgb.getGreen() / 255f,
					rgb.getBlue() / 255f, 1f);
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
		if (inlineText.isSelected()) {
			InlineLayoutBox iB = inlineText.getParent();
			String text = inlineText.getSubstring();
			if (text != null && text.length() > 0) {
				FSFont font = iB.getStyle().getFSFont(c);
				FSGlyphVector glyphVector = c.getTextRenderer().getGlyphVector(c.getOutputDevice(), font,
						inlineText.getSubstring());

				Rectangle start = c.getTextRenderer().getGlyphBounds(c.getOutputDevice(), font, glyphVector,
						inlineText.getSelectionStart(), iB.getAbsX() + inlineText.getX(),
						iB.getAbsY() + iB.getBaseline());

				Rectangle end = c.getTextRenderer().getGlyphBounds(c.getOutputDevice(), font, glyphVector,
						inlineText.getSelectionEnd() - 1, iB.getAbsX() + inlineText.getX(),
						iB.getAbsY() + iB.getBaseline());

				// Graphics2D graphics = getGraphics();
				double scaleX = 1f;
				boolean allSelected = (text.length() == inlineText.getSelectionEnd() - inlineText.getSelectionStart());
				int startX = (inlineText.getSelectionStart() == inlineText.getStart())
						? iB.getAbsX() + inlineText.getX() : (int) Math.round(start.x / scaleX);
				int endX = (allSelected) ? startX + inlineText.getWidth()
						: (int) Math.round((end.x + end.width) / scaleX);

				ColorRGBA bg = canvas.getBackground();
				ColorRGBA fg = canvas.getForeground();
				canvas.setBackground(renderer.getSelectionBackgroundColor());
				canvas.setForeground(renderer.getSelectionBackgroundColor());
				fillRect(startX, iB.getAbsY(), endX - startX, iB.getHeight());

				XHTMLFSFont fn = canvas.getDrawFont();
				canvas.setDrawFont((XHTMLFSFont) iB.getStyle().getFSFont(c));
				canvas.setForeground(renderer.getSelectionForegroundColor());
				// setColor(Color.WHITE); // FIXME
				// setFont(iB.getStyle().getFSFont(c));
				//
				drawSelectedText(c, inlineText, iB, glyphVector);

				canvas.setBackground(bg);
				canvas.setForeground(fg);
				canvas.setDrawFont(fn);
			}
		}
	}

	private void drawSelectedText(RenderingContext c, InlineText inlineText, InlineLayoutBox iB,
			FSGlyphVector glyphVector) {
		XHTMLGlyphVector vector = ((XHTMLGlyphVector) glyphVector);

		// TODO jusiity

		// // We'd like to draw only the characters that are actually selected,
		// but
		// // unfortunately vector.getGlyphPixelBounds() doesn't give us
		// accurate
		// // results with the result that text can appear to jump around as
		// it's
		// // selected. To work around this, we draw the whole string, but move
		// // non-selected characters offscreen.
		// for (int i = 0; i < inlineText.getSelectionStart(); i++) {
		// vector.setGlyphPosition(i, new Point2D.Float(-100000, -100000));
		// }
		// for (int i = inlineText.getSelectionEnd(); i <
		// inlineText.getSubstring().length(); i++) {
		// vector.setGlyphPosition(i, new Point2D.Float(-100000, -100000));
		// }
		// if (inlineText.getParent().getStyle().isTextJustify()) {
		// JustificationInfo info =
		// inlineText.getParent().getLineBox().getJustificationInfo();
		// if (info != null) {
		// String string = inlineText.getSubstring();
		// float adjust = 0.0f;
		// for (int i = inlineText.getSelectionStart(); i <
		// inlineText.getSelectionEnd(); i++) {
		// char ch = string.charAt(i);
		// if (i != 0) {
		// Point2D point = vector.getGlyphPosition(i);
		// vector.setGlyphPosition(i, new Point2D.Double(point.getX() + adjust,
		// point.getY()));
		// }
		// if (ch == ' ' || ch == '\u00a0' || ch == '\u3000') {
		// adjust += info.getSpaceAdjust();
		// } else {
		// adjust += info.getNonSpaceAdjust();
		// }
		// }
		//
		// }
		// }
		c.getTextRenderer().drawGlyphVector(c.getOutputDevice(), glyphVector, iB.getAbsX() + inlineText.getX(),
				iB.getAbsY() + iB.getBaseline());
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
