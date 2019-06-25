package icetone.xhtml;

import org.xhtmlrenderer.render.FSFontMetrics;

/**
 * Adapts Icetone font metrics to flysaucer's.
 */
public class XHTMLFontMetricsAdapter implements FSFontMetrics {

	private final XHTMLFSFont font;

	public XHTMLFontMetricsAdapter(XHTMLFontContext context, XHTMLFSFont font) {
		this.font = font;
	}

	@Override
	public float getAscent() {
		return font.getFontInfo().getAscent();
	}

	@Override
	public float getDescent() {
		return font.getFontInfo().getDescent();
	}

	@Override
	public float getStrikethroughOffset() {
		return -getAscent() / 4;
	}

	@Override
	public float getStrikethroughThickness() {
		return Math.max(1, (font.getFontInfo().getTextLineHeight("Xg")) / 20);
	}

	@Override
	public float getUnderlineOffset() {
		return 0;
	}

	@Override
	public float getUnderlineThickness() {
		return Math.max(1, (font.getFontInfo().getTextLineHeight("Xg")) / 20);
	}
}
