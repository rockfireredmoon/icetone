package icetone.xhtml;

import org.xhtmlrenderer.extend.FontContext;

public class TGGFontContext implements FontContext {

    private final TGGCanvas canvas;

    public TGGFontContext(TGGCanvas canvas) {
        this.canvas = canvas;
    }

    public TGGCanvas getCanvas() {
        return canvas;
    }
}
