package icetone.xhtml;

import org.xhtmlrenderer.render.FSFontMetrics;

/**
 * Adaptrs JME font metrics to flysaucer's.
 */
public class XHTMLFontMetricsAdapter implements FSFontMetrics {

    private final XHTMLFSFont font;

    public XHTMLFontMetricsAdapter(XHTMLFontContext context, XHTMLFSFont font) {
        this.font = font;
        XHTMLCanvas gc = context.getCanvas();
        // TODO not sure about this
        gc.setDrawFont(font);
    }

    @Override
	public float getAscent() {
        return font.getAscent();
    }

    @Override
	public float getDescent() {
        return font.getDescent();
    }

    @Override
	public float getStrikethroughOffset() {
        return -getAscent() / 4;
    }

    @Override
	public float getStrikethroughThickness() {
        return Math.max(1, (font.getBitmapFont().getCharSet().getLineHeight() * font.getFontScale()) / 20);
    }

    @Override
	public float getUnderlineOffset() {
        return 0;
    }

    @Override
	public float getUnderlineThickness() {
        return Math.max(1, (font.getBitmapFont().getCharSet().getLineHeight() * font.getFontScale()) / 20);
    }
}
