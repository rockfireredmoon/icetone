package icetone.xhtml;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.XRLog;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;

import icetone.controls.text.Label;
import icetone.controls.text.TextElement;
import icetone.core.Element;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.xhtml.controls.TGGFormControl;
import icetone.xhtml.scene.LineElement;
import icetone.xhtml.scene.OvalElement;
import icetone.xhtml.scene.PathElement;
import icetone.xhtml.scene.RectangleElement;

/**
 * Acts as a 'canvas', on which text and various other primitives are drawn. All
 * content
 * extends {@link Element}, and is added to the {@link TGGRenderer} which is a
 * scroll
 * panel. Amongst other things, this means elements outside the clipbounds are
 * automatically removed from the scene when not visibile.
 * <p>
 *
 */
public class TGGCanvas {

	private TGGFSFont drawFont;
	private ColorRGBA defaultBg = ColorRGBA.White;
	private ColorRGBA defaultFg = ColorRGBA.Black;
	private ColorRGBA bg = defaultBg;
	private ColorRGBA fg = defaultFg;
	private List<Spatial> other = new ArrayList<Spatial>();
	private final TGGRenderer renderer;
	private Vector2f translate = new Vector2f();
	private float strokeWidth = 1;
	private Element clipLayer;

	public TGGCanvas(TGGRenderer renderer) {
		this.renderer = renderer;
	}

	public void reset() {
		strokeWidth = 1;
		bg = defaultBg;
		fg = defaultFg;
		translate.set(0, 0);
		removeAllContent();
	}

	public ColorRGBA getDefaultBg() {
		return defaultBg;
	}

	public void setDefaultBg(ColorRGBA defaultBg) {
		this.defaultBg = defaultBg;
	}

	public ColorRGBA getDefaultFg() {
		return defaultFg;
	}

	public void setDefaultFg(ColorRGBA defaultFg) {
		this.defaultFg = defaultFg;
	}

	public void dispose() {
	}

	public void setDrawFont(TGGFSFont drawFont) {
		this.drawFont = drawFont;
	}

	public TGGFSFont getDrawFont() {
		return drawFont;
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float lineWidth) {
		this.strokeWidth = lineWidth;
	}

	public void drawText(String string, int x, int y) {
		drawAnimText(string, x, y);
//		 drawStdText(string, x, y);
	}

//	public void drawStdText(String string, int x, int y) {
//
//		final int lineHeight = drawFont.getLineHeight(string);
//		final int lineWidth = drawFont.getLineWidth(string);
//		Label tex = new Label(renderer.getScreen(), new Vector2f(lineWidth, lineHeight));
//		tex.setUID("Label-" + UIDUtil.getUID());
//		// tex.setScaleEW(false);
//		// tex.setScaleNS(false);
//		// tex.setDocking(null);
//
//		tex.setIgnoreGlobalAlpha(true);
//		tex.setDimensions(lineWidth, lineHeight);
//		tex.setIgnoreMouse(true);
//		tex.setIgnoreTouch(true);
//		tex.setFontSize(drawFont.getSize2D());
//		tex.setTextVAlign(BitmapFont.VAlign.Center);
//		y -= lineHeight;
//		LUtil.setPosition(tex, x + translate.x, y + translate.y);
//		tex.setTextWrap(LineWrapMode.NoWrap);
//		try {
//			tex.setText(string);
//		} catch (Exception e) {
//			XRLog.render(Level.SEVERE, "Failed to render text.", e);
//		}
//		tex.setFontColor(fg);
//		if (clipLayer != null) {
//			tex.addClippingLayer(clipLayer);
//		}
//		configureForDebug(tex.getMaterial(), "text");
//		renderer.addScrollableContent(tex, false);
//	}

	public void drawAnimText(String string, int x, int y) {
		TextElement tex = createText(string);
		if (clipLayer != null) {
			tex.addClippingLayer(clipLayer);
		}
		y -= tex.getAnimText().getBounds().y;
		LUtil.setPosition(tex, x + translate.x, y + translate.y);
		configureForDebug(tex.getAnimText().getMaterial(), "text");
		renderer.addScrollableContent(tex, false);
	}

	public TextElement createText(String string) {
		TextElement tex = new TextElement(renderer.getScreen(), drawFont.getBitmapFont()) {
			@Override
			public void onUpdate(float tpf) {
			}

			@Override
			public void onEffectStart() {
			}

			@Override
			public void onEffectStop() {
			}
		};

		// Don't want it to clip itself, or descending glyphs get chopped
		tex.removeClippingLayer(tex);

		tex.setUID("Text-" + UIDUtil.getUID());

		final int lineHeight = drawFont.getLineHeight(string);
		final int lineWidth = drawFont.getLineWidth(string);

		tex.setIgnoreGlobalAlpha(true);
		tex.setDimensions(lineWidth, lineHeight);
		tex.setIgnoreMouse(true);
		tex.setIgnoreTouch(true);
		tex.setFontSize(drawFont.getSize2D());
		tex.getAnimText().setBounds(lineWidth, lineHeight);
		tex.setTextVAlign(BitmapFont.VAlign.Center);
		tex.setTextWrap(LineWrapMode.NoWrap);

		if (drawFont.isBold()) {
			string = "<b>" + string + "</b>";
		} else if (drawFont.isItalic()) {
			string = "<i>" + string + "</i>";
		} else if (drawFont.isUnderline()) {
			string = "<u>" + string + "</u>";
		} else {
			tex.getAnimText().setParseTags(false);
		}

		try {
			tex.setText(string);
		} catch (Exception e) {
			XRLog.render(Level.SEVERE, "Failed to render text.", e);
		}
		tex.setFontColor(fg);
		return tex;
	}

	public void drawControl(final TGGFormControlReplacementElement formReplaced, int x, int y) {
		TGGFormControl swtControl = formReplaced.getControl();
		final Element el = swtControl.getUIElement();
		formReplaced.setLocation(x, y);
		if (el.getElementParent() != null) {
			el.getElementParent().removeChild(el);
		}
		renderer.addScrollableContent(el, false);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
//		System.out.println("drawLine " + x1 + "," + y1 + "," + x2 + "," + y2);
//		try {
//			throw new Exception();
//		}
//		catch(Exception e) {
//			e.printStackTrace(System.out);
//		}
		LineElement lineEl = new LineElement(renderer.getScreen(), getTranslatedPosition(x1, y1), getTranslatedPosition(x2, y2), fg,
				strokeWidth);
		lineEl.setUID("Line-" + UIDUtil.getUID());
		lineEl.setIgnoreGlobalAlpha(true);
		configureForDebug(lineEl, "other");
		if (clipLayer != null) {
			lineEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(lineEl, false);
	}

	public void fillRectangle(int x, int y, int width, int height) {
		Element el = new Element(renderer.getScreen(), UIDUtil.getUID(), getTranslatedPosition(x, y), new Vector2f(width, height),
				Vector4f.ZERO, null);
		el.setUID("Rect-" + UIDUtil.getUID());
		el.getElementMaterial().setColor("Color", bg);
		el.setScaleEW(false);
		el.setScaleNS(false);
		el.setDocking(null);
		el.setIgnoreMouse(true);
		el.setIgnoreGlobalAlpha(true);
		if (clipLayer != null) {
			el.addClippingLayer(clipLayer);
		}
		configureForDebug(el, "other");
		renderer.addScrollableContent(el, false);
	}

	public void drawPath(int x, int y, List<Vector2f> pa) {
		PathElement pe = new PathElement(renderer.getScreen(), getTranslatedPosition(x, y), pa, fg, strokeWidth, true);
		pe.setUID("Path-" + UIDUtil.getUID());
		pe.setIgnoreGlobalAlpha(true);
		configureForDebug(pe, "other");
		if (clipLayer != null) {
			pe.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(pe, false);
	}

	public void drawRectangle(int x, int y, int width, int height) {
		System.out.println("Drawing rect " + x + " " + y + " " + width + " " + height + " " + fg);
		RectangleElement rectEl = new RectangleElement(renderer.getScreen(), getTranslatedPosition(x, y),
				new Vector2f(width, height), fg, strokeWidth);
		rectEl.setUID("Rect-" + UIDUtil.getUID());
		rectEl.setIgnoreGlobalAlpha(true);
		configureForDebug(rectEl, "other");
		if (clipLayer != null) {
			rectEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(rectEl, false);
	}

	public void setBackground(ColorRGBA bg) {
		this.bg = bg;
	}

	public void setForeground(ColorRGBA fg) {
		this.fg = fg;
	}

	public ColorRGBA getBackground() {
		return bg;
	}

	public ColorRGBA getForeground() {
		return fg;
	}

	public void drawOval(int x, int y, int width, int height) {
		OvalElement circleEl = new OvalElement(renderer.getScreen(), getTranslatedPosition(x, y),
				new Vector2f((float) width / 2f, (float) height / 2f), fg, strokeWidth);
		circleEl.setUID("Oval-" + UIDUtil.getUID());
		circleEl.setIgnoreGlobalAlpha(true);
		configureForDebug(circleEl, "other");
		if (clipLayer != null) {
			circleEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(circleEl, false);
	}

	public void fillOval(int x, int y, int width, int height) {
		// TODO fill
		OvalElement circleEl = new OvalElement(renderer.getScreen(), getTranslatedPosition(x, y),
				new Vector2f((float) width / 2f, (float) height / 2f), fg, strokeWidth);
		circleEl.setUID("FillOval-" + UIDUtil.getUID());
		circleEl.setIgnoreGlobalAlpha(true);
		configureForDebug(circleEl, "other");
		if (clipLayer != null) {
			circleEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(circleEl, false);
	}

	protected Vector2f getTranslatedPosition(int x, int y) {
		return new Vector2f(x + translate.x, y + translate.y);
	}

	public void drawImage(Image img, int dx, int dy, int dw, int dh) {
		Element el = new Element(renderer.getScreen(), UIDUtil.getUID(), getTranslatedPosition(dx, dy), new Vector2f(dw, dh),
				Vector4f.ZERO, null);

		el.setIgnoreGlobalAlpha(true);
		el.setUID("Img-" + UIDUtil.getUID());
		el.setIgnoreMouse(true);
		el.setIgnoreTouch(true);
		el.setDimensions(dw, dh);
		el.setTexture(img);
		configureForDebug(el, "image");
		if (clipLayer != null) {
			el.addClippingLayer(clipLayer);
		}

		//
		renderer.addScrollableContent(el, false);
	}

	public TGGRenderer getRenderer() {
		return renderer;
	}

	public void removeAllContent() {
		for (icetone.core.Element e : new ArrayList<icetone.core.Element>(renderer.getScrollableArea().getElements())) {
			// Don't remove content without requesting no reshape, or things get
			// messed up
			renderer.removeScrollableContent(e, false);
		}
		for (Spatial o : other) {
			o.removeFromParent();
		}
		other.clear();
	}

	public void translate(double tx, double ty) {
		translate.set((float) tx, (float) ty);
	}

	public void setClip(Vector4f bounds) {
		if (bounds == null) {
			clipLayer = null;
		} else {
			clipLayer = new Element(renderer.getScreen(), UIDUtil.getUID(), getTranslatedPosition((int) bounds.x, (int) bounds.y),
					new Vector2f(bounds.z, bounds.w), Vector4f.ZERO, null);
			clipLayer.setUID("Clip-" + UIDUtil.getUID());
			clipLayer.setAsContainerOnly();
			renderer.addScrollableContent(clipLayer, false);
		}
	}

	private void configureForDebug(Element el, String type) {
		configureForDebug(el.getMaterial(), type);
	}

	private void configureForDebug(Material el, String type) {
		if (Configuration.isTrue("xr.renderer.debug." + type + "-wireframe", false)) {
			el.getAdditionalRenderState().setWireframe(true);
		}
	}
}
