package icetone.text;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import icetone.core.BaseScreen;
import icetone.css.StyleManager.ThemeInstance;

public interface TextEngine {

	FontInfo createInfo(FontSpec spec, ThemeInstance theme);
	
	TextElement createTextElement(FontSpec spec, BaseScreen screen, ThemeInstance theme, Node parent);

	void init(AssetManager assetManager);

	boolean isFont(FontSpec afont, ThemeInstance theme);
	
	int getPriority();
}
