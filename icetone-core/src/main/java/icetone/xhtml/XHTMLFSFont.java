package icetone.xhtml;

import org.xhtmlrenderer.render.FSFont;

import icetone.core.BaseScreen;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontInfo;
import icetone.text.FontSpec;

/**
 * {@link FSFont} implemtation that bridges JME fonts with flying saucer's.
 */
public class XHTMLFSFont implements FSFont {

	private final FontSpec fontSpec;
	private final BaseScreen screen;
	private final ThemeInstance theme;
	private final FontInfo fontInfo;

	public XHTMLFSFont(BaseScreen screen, ThemeInstance theme, FontSpec fontSpec) {
		this.screen = screen;
		this.fontSpec = fontSpec;
		this.theme = theme;
		fontInfo = theme.getFontInfo(fontSpec);
	}

	@Override
	public float getSize2D() {
		return fontSpec.getSize();
	}

	public FontInfo getFontInfo() {
		return fontInfo;
	}

	public FontSpec getFontSpec() {
		return fontSpec;
	}

	@Override
	public XHTMLFSFont clone() {
		return new XHTMLFSFont(screen, theme, fontSpec.clone());
	}

}
