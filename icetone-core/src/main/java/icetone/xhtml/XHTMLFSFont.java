package icetone.xhtml;

import org.xhtmlrenderer.render.FSFont;

import com.jme3.font.BitmapFont;

import icetone.core.BaseScreen;
import icetone.framework.core.AnimText;

/**
 * {@link FSFont} implemtation that bridges JME fonts with flying saucer's.
 */
public class XHTMLFSFont implements FSFont {

	private final BitmapFont bitmapFont;
	private float size;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private float fontScale;
	private final String fontFamily;
	private BaseScreen screen;

	public XHTMLFSFont(BaseScreen screen, String fontFamily, BitmapFont bitmapFont, float size) {
		if (fontFamily == null || fontFamily.length() == 0) {
			throw new IllegalArgumentException();
		}
		this.screen = screen;
		this.bitmapFont = bitmapFont;
		this.fontFamily = fontFamily;
		setSize(size);
	}

	public float getAscent() {
		return bitmapFont.getCharSet().getBase() * fontScale;
	}

	public float getDescent() {
		return (bitmapFont.getCharSet().getLineHeight() - bitmapFont.getCharSet().getBase()) * fontScale;
	}

	public int getLineWidth(String text) {
		return getLineWidth(text, bitmapFont, fontScale);
	}

	public int getLineHeight(String text) {
		return getLineHeight(text, bitmapFont, fontScale);
	}

	protected int getLineWidth(String text, BitmapFont fnt, float fs) {
		// TODO shared animtext
		AnimText txt = new AnimText(screen, fnt);
		txt.setFontSize(fs * fnt.getPreferredSize());
		txt.setText(text);
		return Math.round(txt.getLineWidth());

		// if (text.equals(" ")) {
		// text = "x";
		// }

		// return (int) (fnt.getLineWidth(text) * fs);
	}

	protected int getLineHeight(String text, BitmapFont fnt, float fs) {

		// TODO shared animtext
		AnimText txt = new AnimText(screen, fnt);
		txt.setFontSize(fs * fnt.getPreferredSize());
		txt.setText(text);
		return Math.round(txt.getLineHeight());

		// BitmapText eval = new BitmapText(fnt);
		// eval.setSize(fs * fnt.getPreferredSize());
		// eval.setLineWrapMode(LineWrapMode.NoWrap);
		// eval.setBox(null);
		// eval.setText(text);
		// return (int) eval.getLineHeight();
	}

	public float getFontScale() {
		return fontScale;
	}

	public float getFontScale(BitmapFont fnt, float fontSize) {
		return fontSize / fnt.getPreferredSize();
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	@Override
	public float getSize2D() {
		return size;
	}

	public BitmapFont getBitmapFont() {
		return bitmapFont;
	}

	@Override
	public XHTMLFSFont clone() {
		XHTMLFSFont fnt = new XHTMLFSFont(screen, fontFamily, bitmapFont, size);
		fnt.bold = bold;
		fnt.italic = italic;
		fnt.underline = underline;
		return fnt;
	}

	@Override
	public String toString() {
		return "TGGFSFont{" + "_font=" + bitmapFont + ", _size=" + size + ", bold=" + bold + ", italic=" + italic
				+ ", underline=" + underline + ", fontScale=" + fontScale + '}';
	}

	public final void setSize(float size) {
		this.size = size;
		fontScale = getFontScale(bitmapFont, size);
	}

	public String getFontFamily() {
		return fontFamily;
	}
}
