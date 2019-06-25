package icetone.text.bitmap;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.plugins.BitmapFontLoader;
import com.jme3.scene.Node;

import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontInfo;
import icetone.text.FontSpec;
import icetone.text.TextElement;
import icetone.text.TextEngine;

public class RichBitmapTextEngine implements TextEngine {

	final static Logger LOG = Logger.getLogger(RichBitmapTextEngine.class.getName());

	@Override
	public FontInfo createInfo(FontSpec spec, ThemeInstance theme) {
		if (LOG.isLoggable(Level.INFO))
			LOG.info(String.format("Loading font %s for theme %s", spec, theme));
		String path = spec.getPath();
		BitmapFont bmf = ToolKit.get().getApplication().getAssetManager().loadAsset(new AssetKey<BitmapFont>(path));
		return new BitmapFontInfo(spec, bmf, this);
	}

	@Override
	public TextElement createTextElement(FontSpec spec, BaseScreen screen, ThemeInstance theme, Node parent) {
		RichBitmapText text = new RichBitmapText(screen, spec, theme);
		if (parent != null)
			parent.attachChild(text);
		return text;
	}

	@Override
	public void init(AssetManager assetManager) {
		assetManager.unregisterLoader(BitmapFontLoader.class);
		assetManager.registerLoader(BitmapFontLoaderX.class, "fnt");
	}

	@Override
	public boolean isFont(FontSpec afont, ThemeInstance theme) {
		return afont.getPath() != null && afont.getPath().toLowerCase().endsWith(".fnt");
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
