package icetone.xhtml;

import org.xhtmlrenderer.extend.FontContext;

public class XHTMLFontContext implements FontContext {

    private final XHTMLCanvas canvas;

    public XHTMLFontContext(XHTMLCanvas canvas) {
        this.canvas = canvas;
    }

    public XHTMLCanvas getCanvas() {
        return canvas;
    }
}
