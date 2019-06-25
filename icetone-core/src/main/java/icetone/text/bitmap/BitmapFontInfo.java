package icetone.text.bitmap;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;

import icetone.text.FontInfo;
import icetone.text.FontSpec;
import icetone.text.TextEngine;

public class BitmapFontInfo extends FontInfo {

	private final BitmapFont bitmapFont;

	public BitmapFontInfo(FontSpec spec, BitmapFont bitmapFont, TextEngine textEngine) {
		super(spec, textEngine);
		this.bitmapFont = bitmapFont;
	}

	public BitmapFont getBitmapFont() {
		return bitmapFont;
	}

	@Override
	public float getPreferredSize() {
		return bitmapFont.getPreferredSize();
	}

	@Override
	public float getLineWidth(String string) {
		return bitmapFont.getLineWidth(string) * getScale();
	}

	@Override
	public float getTotalLineHeight() {
		return bitmapFont.getCharSet().getLineHeight() * getScale();
	}

	@Override
	public float getAscent() {
		return Math.min(bitmapFont.getCharSet().getBase() * getScale(), getTotalLineHeight());
	}

	@Override
	public float getDescent() {
		return Math.max(0,  getTotalLineHeight() - (bitmapFont.getCharSet().getBase() * getScale()));
	}

	@Override
	public float getTextLineHeight(String string) {
		BitmapText eval = new BitmapText(getBitmapFont());
		float size = getSpec().getSize();
		eval.setSize(size == -1 ? getBitmapFont().getPreferredSize() : size);
		eval.setLineWrapMode(LineWrapMode.NoWrap);
		eval.setBox(null);
		eval.setText(string);
		return eval.getLineHeight();
	}

	@Override
	public String getEngine() {
		return RichBitmapTextEngine.class.getSimpleName();
	}

	protected float getScale() {
		return getSpec().getSize() == -1 ? 1 : getSpec().getSize() / getPreferredSize();
	}
}
