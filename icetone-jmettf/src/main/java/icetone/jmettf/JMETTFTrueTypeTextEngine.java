package icetone.jmettf;

import com.atr.jme.font.TrueTypeFont;
import com.atr.jme.font.asset.TrueTypeKeyMesh;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontInfo;
import icetone.text.FontSpec;
import icetone.text.TextElement;

public class JMETTFTrueTypeTextEngine extends JMETTFTextEngine {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public FontInfo createInfo(FontSpec spec, ThemeInstance theme) {
		TrueTypeFont<?, ?> ttf;
		AssetManager assetManager = ToolKit.get().getApplication().getAssetManager();
		String path = spec.getPath();
		float size = spec.getSize();
		if (size == -1)
			size = JMETTFTextElement.DEFAULT_FONT_SIZE;
		ttf = (TrueTypeFont) assetManager
				.loadAsset(new TrueTypeKeyMesh(path, toStyle(spec.getStyles()), (int) (size < 1 ? 1 : size)));
		return new JMETTFFontInfo(spec, this, ttf, JMETTFTrueTypeTextEngine.class.getSimpleName());
	}

	@Override
	public TextElement createTextElement(FontSpec spec, BaseScreen screen, ThemeInstance theme, Node parent) {
		JMETTFFontInfo info = theme.getFontInfo(spec);
		return new JMETTFTrueTypeTextElement(theme, info, spec, parent);
	}

	@Override
	public int getPriority() {
		return 200;
	}

}
