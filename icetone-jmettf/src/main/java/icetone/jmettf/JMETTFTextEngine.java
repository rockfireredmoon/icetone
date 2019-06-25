package icetone.jmettf;

import java.util.Arrays;
import java.util.List;

import com.atr.jme.font.asset.TrueTypeLoader;
import com.atr.jme.font.util.Style;
import com.jme3.asset.AssetManager;

import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontSpec;
import icetone.text.TextEngine;
import icetone.text.TextStyle;

public abstract class JMETTFTextEngine implements TextEngine {

	public static final String ANTI_ALIAS = "anti-alias";
	public static final String OUTLINE_WIDTH = "outlineWidth";
	public static final String OUTLINE = "outline";

	@Override
	public boolean isFont(FontSpec afont, ThemeInstance theme) {
		return afont.getPath() != null && afont.getPath().toLowerCase().endsWith(".ttf");
	}

	@Override
	public void init(AssetManager assetManager) {
		assetManager.registerLoader(TrueTypeLoader.class, "ttf");
	}

	protected Style toStyle(TextStyle[] styles) {
		if (styles == null)
			return Style.Plain;
		List<TextStyle> s = Arrays.asList(styles);
		if (s.contains(TextStyle.ITALIC))
			if (s.contains(TextStyle.BOLD))
				return Style.BoldItalic;
			else
				return Style.Italic;
		else if (s.contains(TextStyle.BOLD))
			return Style.Bold;
		else
			return Style.Plain;
	}
}
