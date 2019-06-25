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

import icetone.core.BaseElement;
import icetone.core.Size;
import icetone.xhtml.controls.XHTMLFormControl;
import icetone.xhtml.scene.LineElement;
import icetone.xhtml.scene.OvalElement;
import icetone.xhtml.scene.PathElement;
import icetone.xhtml.scene.RectangleElement;

/**
 * Acts as a 'canvas', on which text and various other primitives are drawn. All
 * content extends {@link BaseElement}, and is added to the
 * {@link XHTMLRenderer} which is a scroll panel. Amongst other things, this
 * means elements outside the clipbounds are automatically removed from the
 * scene when not visibile.
 * <p>
 *
 * @author rockfire
 */
public class XHTMLCanvas {

	private XHTMLFSFont drawFont;
	private ColorRGBA defaultBg = ColorRGBA.White;
	private ColorRGBA defaultFg = ColorRGBA.Black;
	private ColorRGBA bg = defaultBg;
	private ColorRGBA fg = defaultFg;
	private List<Spatial> other = new ArrayList<Spatial>();
	private final XHTMLRenderer renderer;
	private Vector2f translate = new Vector2f();
	private float strokeWidth = 1;
	private BaseElement clipLayer;

	public XHTMLCanvas(XHTMLRenderer renderer) {
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

	public void setDrawFont(XHTMLFSFont drawFont) {
		this.drawFont = drawFont;
	}

	public XHTMLFSFont getDrawFont() {
		return drawFont;
	}

	public float getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(float lineWidth) {
		this.strokeWidth = lineWidth;
	}

	public void drawText(String string, int x, int y) {
		BaseElement tex = createText(string);
		if (clipLayer != null) {
			tex.addClippingLayer(clipLayer);
		}
		y -= tex.getTextElement().getDimensions().y;
		tex.setPosition(x + translate.x, y + translate.y);
		configureForDebug(tex.getTextElement().getMaterial(), "text");
		renderer.addScrollableContent(tex);
	}

	public BaseElement createText(String string) {
		BaseElement tex = new BaseElement(renderer.getScreen());

		final int lineHeight = (int) drawFont.getFontInfo().getTextLineHeight(string);
		final int lineWidth = (int) drawFont.getFontInfo().getLineWidth(string);

		tex.setAsContainerOnly();
		tex.setIgnoreGlobalAlpha(true);
		tex.setDimensions(lineWidth, lineHeight);
		tex.setIgnoreMouse(true);
		tex.setIgnoreTouch(true);
		tex.setFont(drawFont.getFontSpec());
		tex.setTextVAlign(BitmapFont.VAlign.Center);
		tex.setTextWrap(LineWrapMode.NoWrap);

		try {
			tex.setText(string);
		} catch (Exception e) {
			XRLog.render(Level.SEVERE, "Failed to render text.", e);
		}
		tex.setFontColor(fg);
		return tex;
	}

	public void drawControl(final XHTMLFormControlReplacementElement formReplaced, int x, int y) {
		XHTMLFormControl swtControl = formReplaced.getControl();
		final BaseElement el = swtControl.getUIElement();
		formReplaced.setLocation(x, y);
		renderer.removeScrollableContent(el);
		renderer.addScrollableContent(el);
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		LineElement lineEl = new LineElement(renderer.getScreen(), getTranslatedPosition(x1, y1),
				getTranslatedPosition(x2, y2), fg, strokeWidth);
		lineEl.setIgnoreGlobalAlpha(true);
		configureForDebug(lineEl, "other");
		if (clipLayer != null) {
			lineEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(lineEl);
	}

	public void fillRectangle(int x, int y, int width, int height) {
		BaseElement el = new BaseElement(renderer.getScreen(), getTranslatedPosition(x, y), new Size(width, height),
				Vector4f.ZERO, null);
		el.getMaterial().setColor("Color", bg);
		el.setIgnoreMouse(true);
		el.setIgnoreGlobalAlpha(true);
		if (clipLayer != null) {
			el.addClippingLayer(clipLayer);
		}
		configureForDebug(el, "other");
		renderer.addScrollableContent(el);
	}

	public void drawPath(int x, int y, List<Vector2f> pa) {
		PathElement pe = new PathElement(renderer.getScreen(), getTranslatedPosition(x, y), pa, fg, strokeWidth, true);
		pe.setIgnoreGlobalAlpha(true);
		configureForDebug(pe, "other");
		if (clipLayer != null) {
			pe.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(pe);
	}

	public void drawRectangle(int x, int y, int width, int height) {
		RectangleElement rectEl = new RectangleElement(renderer.getScreen(), getTranslatedPosition(x, y),
				new Vector2f(width, height), fg, strokeWidth);
		rectEl.setIgnoreGlobalAlpha(true);
		configureForDebug(rectEl, "other");
		if (clipLayer != null) {
			rectEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(rectEl);
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
				new Vector2f(width / 2f, height / 2f), fg, strokeWidth);
		circleEl.setIgnoreGlobalAlpha(true);
		configureForDebug(circleEl, "other");
		if (clipLayer != null) {
			circleEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(circleEl);
	}

	public void fillOval(int x, int y, int width, int height) {
		// TODO fill
		OvalElement circleEl = new OvalElement(renderer.getScreen(), getTranslatedPosition(x, y),
				new Vector2f(width / 2f, height / 2f), fg, strokeWidth);
		circleEl.setIgnoreGlobalAlpha(true);
		configureForDebug(circleEl, "other");
		if (clipLayer != null) {
			circleEl.addClippingLayer(clipLayer);
		}
		renderer.addScrollableContent(circleEl);
	}

	protected Vector2f getTranslatedPosition(int x, int y) {
		return new Vector2f(x + translate.x, y + translate.y);
	}

	public void drawImage(Image img, int dx, int dy, int dw, int dh) {
		BaseElement el = new BaseElement(renderer.getScreen(), getTranslatedPosition(dx, dy), new Size(dw, dh),
				Vector4f.ZERO, null);

		el.setIgnoreGlobalAlpha(true);
		el.setIgnoreMouse(true);
		el.setIgnoreTouch(true);
		el.setDimensions(dw, dh);
		el.setTexture(img);
		configureForDebug(el, "image");
		if (clipLayer != null) {
			el.addClippingLayer(clipLayer);
		}

		//
		renderer.addScrollableContent(el);
	}

	public XHTMLRenderer getRenderer() {
		return renderer;
	}

	public void removeAllContent() {
		renderer.invalidate();
		renderer.getScrollableArea().removeAllChildren();
		for (Spatial o : other) {
			o.removeFromParent();
		}
		renderer.validate();
		other.clear();
	}

	public void translate(double tx, double ty) {
		translate.set((float) tx, (float) ty);
	}

	public void setClip(Vector4f bounds) {
		if (bounds == null) {
			clipLayer = null;
		} else {
			clipLayer = new BaseElement(renderer.getScreen(), getTranslatedPosition((int) bounds.x, (int) bounds.y),
					new Size(bounds.z, bounds.w), Vector4f.ZERO, null);
			clipLayer.setAsContainerOnly();
			renderer.addScrollableContent(clipLayer);
		}
	}

	private void configureForDebug(BaseElement el, String type) {
		configureForDebug(el.getMaterial(), type);
	}

	private void configureForDebug(Material el, String type) {
		if (Configuration.isTrue("xr.renderer.debug." + type + "-wireframe", false)) {
			el.getAdditionalRenderState().setWireframe(true);
		}
	}
}
