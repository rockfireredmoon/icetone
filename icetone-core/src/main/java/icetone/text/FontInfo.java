package icetone.text;

import com.jme3.math.Vector2f;

import icetone.core.BaseElement;

public abstract class FontInfo {

	private FontSpec spec;
	private TextEngine factory;

	public FontInfo(FontSpec spec, TextEngine factory) {
		this.spec = spec;
		this.factory = factory;
	}

	public FontSpec getSpec() {
		return spec;
	}

	public TextEngine getFactory() {
		return factory;
	}

	public void setSpec(FontSpec spec) {
		this.spec = spec;
	}

	@Deprecated
	/* TODO: Do we really need this? */
	public abstract float getPreferredSize();

	public abstract float getLineWidth(String string);

	public abstract float getTextLineHeight(String string);

	/**
	 * The number of pixels above the baseline that a character can extend. Note
	 * some characters may extend greater than this amount.
	 * 
	 * @return ascent
	 */
	public abstract float getAscent();

	/**
	 * The line height from baseline to baseline.
	 * 
	 * @return line height
	 */
	public abstract float getTotalLineHeight();

	/**
	 * The number of pixels below the baseline that a character can extend. Note
	 * some characters may extend greater than this amount.
	 * 
	 * @return descent
	 */
	public abstract float getDescent();

	public Vector2f getTextTotalSize(BaseElement el, String text, float maxWidth) {
		if (text == null || text.length() == 0)
			return new Vector2f(0, el.getFixedLineHeight() == -1 ? getTotalLineHeight() : el.getFixedLineHeight());
		else {
			TextElement tel = factory.createTextElement(spec, el.getScreen(), el.getThemeInstance(), null);
			tel.setTextWrap(el.getTextWrap());
			tel.setFixedLineHeight(el.getFixedLineHeight());
			tel.setDimensions(new Vector2f(maxWidth, Short.MAX_VALUE));
			tel.setText(text.length() < 1 ? "Xg" : text);
			tel.updateTextState(false);
			return new Vector2f(tel.getLineWidth(), tel.getTotalHeight());
		}

	}

	public abstract String getEngine();

	@Override
	public String toString() {
		return "FontInfo [spec=" + spec + ", factory=" + factory + ", getPreferredSize()=" + getPreferredSize()
				+ ", getAscent()=" + getAscent() + ", getLineHeight()=" + getTotalLineHeight()
				+ ", getDescent()=" + getDescent() + ", getEngine()="
				+ getEngine() + "]";
	}

}
