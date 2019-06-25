package icetone.jmettf;

import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.asset.TrueTypeKeyBMP;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontInfo;
import icetone.text.FontSpec;
import icetone.text.TextElement;

public class JMETTFBitmapTextEngine extends JMETTFTextEngine {

	@SuppressWarnings({ "unchecked" })
	@Override
	public FontInfo createInfo(FontSpec spec, ThemeInstance theme) {
		AssetManager assetManager = ToolKit.get().getApplication().getAssetManager();
		String path = spec.getPath();
		float size = spec.getSize();
		if (size == -1)
			size = JMETTFBitmapTextElement.DEFAULT_FONT_SIZE;
		int outline = 0;
		if (spec.getProperties() != null && spec.getProperties().containsKey(JMETTFTextEngine.OUTLINE_WIDTH))
			outline = Integer.parseInt(spec.getProperties().get(JMETTFTextEngine.OUTLINE_WIDTH));
		return new JMETTFFontInfo(spec, this,
				(TrueTypeFont<?, ?>) assetManager
						.loadAsset(new TrueTypeKeyBMP(path, toStyle(spec.getStyles()), (int) size, outline)),
				JMETTFBitmapTextEngine.class.getSimpleName());
	}

	@Override
	public TextElement createTextElement(FontSpec spec, BaseScreen screen, ThemeInstance theme, Node parent) {
		JMETTFFontInfo info = theme.getFontInfo(spec);
		return new JMETTFBitmapTextElement(theme, info, spec, parent);
	}

	@Override
	public int getPriority() {
		return 100;
	}
}
