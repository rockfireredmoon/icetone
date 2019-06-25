package icetone.jmettf;

import com.atr.jme.font.TrueTypeFont;

import icetone.text.FontInfo;
import icetone.text.FontSpec;
import icetone.text.TextEngine;

public final class JMETTFFontInfo extends FontInfo {
	private final TrueTypeFont<?, ?> ttf;
	private String engine;

	public JMETTFFontInfo(FontSpec spec, TextEngine factory, TrueTypeFont<?, ?> ttf, String engine) {
		super(spec, factory);
		this.ttf = ttf;
		this.engine = engine;
	}

	public TrueTypeFont<?, ?> getTrueTypeFont() {
		return ttf;
	}

	@Override
	public float getTextLineHeight(String string) {
		return ttf.getVisualLineHeight(string);
	}

	@Override
	public float getPreferredSize() {
		return ttf.getActualPointSize();
	}

	@Override
	public float getLineWidth(String string) {
		return ttf.getLineWidth(string, 0) + (getSpec().getCharacterSpacing() * (float)string.length());
	}

	@Override
	public float getTotalLineHeight() { 
		return ttf.getScaledLineHeight();
	}
 
	@Override
	public float getAscent() {
		//return ttf.getActualAscender() - ttf.getActualLineHeight();
		return ttf.getActualAscender();
	}

	@Override
	public float getDescent() {
		return ttf.getActualDescender();
	}

	@Override
	public String getEngine() {
		return engine;
	}
}