package icetone.xhtml;

import java.awt.Rectangle;

import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.util.Configuration;

import icetone.core.ElementContainer;

/**
 * Adapts flying saucer's text rendering to the 'canvas' ({@link XHTMLCanvas}).
 */
public class XHTMLTextRenderer implements TextRenderer {

	private float scale = 1f;
	private ElementContainer<?, ?> container;

	public XHTMLTextRenderer(ElementContainer<?, ?> container) {
		scale = Configuration.valueAsFloat("xr.text.scale", 1.0f);
		this.container = container;
	}

	@Override
	public void setup(FontContext context) {
	}

	@Override
	public FSFontMetrics getFSFontMetrics(FontContext context, FSFont font, String string) {
		return new XHTMLFontMetricsAdapter((XHTMLFontContext) context, (XHTMLFSFont) font);
	}

	@Override
	public int getWidth(FontContext context, FSFont font, String string) {
		return Math.round(((XHTMLFSFont) font).getFontInfo().getLineWidth(string));
	}

	@Override
	public float getFontScale() {
		return scale;
	}

	@Override
	public int getSmoothingLevel() {
		return 0;
	}

	@Override
	public void setFontScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void setSmoothingLevel(int level) {
	}

	@Override
	public void setSmoothingThreshold(float fontsize) {
	}

	@Override
	public void drawGlyphVector(OutputDevice outputDevice, FSGlyphVector fsGlyphVector, float x, float y) {
		XHTMLGlyphVector vector = ((XHTMLGlyphVector) fsGlyphVector);
		XHTMLCanvas gc = ((XHTMLOutputDevice) outputDevice).getCanvas();
		XHTMLFSFont fnt = gc.getDrawFont();
		gc.setDrawFont(vector.getFont());
		gc.drawText(vector.getString(), (int) x, (int) y);
		gc.setDrawFont(fnt);
	}

	@Override
	public void drawString(OutputDevice outputDevice, String string, float x, float y) {
		XHTMLCanvas gc = ((XHTMLOutputDevice) outputDevice).getCanvas();
		gc.drawText(string, (int) x, (int) y);
	}

	@Override
	public void drawString(OutputDevice outputDevice, String string, float x, float y, JustificationInfo info) {
		// TODO handle justification
		drawString(outputDevice, string, x, y);
	}

	@Override
	public Rectangle getGlyphBounds(OutputDevice outputDevice, FSFont font, FSGlyphVector fsGlyphVector, int index,
			float x, float y) {
		XHTMLGlyphVector vector = ((XHTMLGlyphVector) fsGlyphVector);
		XHTMLCanvas gc = ((XHTMLOutputDevice) outputDevice).getCanvas();
		return vector.getGlyphPixelBounds(index, gc, x, y);
	}

	@Override
	public float[] getGlyphPositions(OutputDevice outputDevice, FSFont font, FSGlyphVector fsGlyphVector) {
		throw new UnsupportedOperationException();
	}

	@Override
	public FSGlyphVector getGlyphVector(OutputDevice outputDevice, FSFont font, String string) {
		return new XHTMLGlyphVector(outputDevice, (XHTMLFSFont) font, string);
	}
}
