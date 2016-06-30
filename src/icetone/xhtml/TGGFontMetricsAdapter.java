package icetone.xhtml;

import org.xhtmlrenderer.render.FSFontMetrics;

/**
 * Adaptrs JME font metrics to flysaucer's.
 */
public class TGGFontMetricsAdapter implements FSFontMetrics {

    private final TGGFSFont font;

    public TGGFontMetricsAdapter(TGGFontContext context, TGGFSFont font) {
        this.font = font;
        TGGCanvas gc = ((TGGFontContext) context).getCanvas();
        // TODO not sure about this
        gc.setDrawFont(font);
    }

    public float getAscent() {
        return font.getAscent();
    }

    public float getDescent() {
        return font.getDescent();
    }

    public float getStrikethroughOffset() {
        return -getAscent() / 4;
    }

    public float getStrikethroughThickness() {
        return Math.max(1, ((float) font.getBitmapFont().getCharSet().getLineHeight() * font.getFontScale()) / 20);
    }

    public float getUnderlineOffset() {
        return 1;
    }

    public float getUnderlineThickness() {
        return Math.max(1, ((float) font.getBitmapFont().getCharSet().getLineHeight() * font.getFontScale()) / 20);
    }
}
