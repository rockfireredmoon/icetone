package icetone.jmettf;

import com.atr.jme.font.TrueTypeBMP;
import com.atr.jme.font.shape.TrueTypeContainer;
import com.atr.jme.font.util.AtlasListener;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import icetone.css.CssUtil;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontSpec;

public class JMETTFBitmapTextElement extends JMETTFTextElement implements AtlasListener {

	public JMETTFBitmapTextElement(ThemeInstance theme, JMETTFFontInfo fontInfo, FontSpec font, Node parent) {
		super(theme, fontInfo, font, parent);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void setFontInfo(JMETTFFontInfo fontInfo) {
		if (this.fontInfo != null)
			((TrueTypeBMP) this.fontInfo.getTrueTypeFont()).removeAtlasListener(this);
		((TrueTypeBMP) fontInfo.getTrueTypeFont()).addAtlasListener(this);
		super.setFontInfo(fontInfo);
	}

	@SuppressWarnings("rawtypes")
	protected TrueTypeContainer createSpatial() {
		if (font.getProperties() != null && font.getProperties().containsKey(JMETTFTextEngine.OUTLINE)) {
			return ((TrueTypeBMP) fontInfo.getTrueTypeFont()).getFormattedText(getStringContainer(), color,
					CssUtil.toColor(font.getProperties().get(JMETTFTextEngine.OUTLINE)));
		} else
			return super.createSpatial();
	}

	@Override
	public void mod(AssetManager assetManager, int oldWidth, int oldHeight, int newWidth, int newHeight,
			@SuppressWarnings("rawtypes") TrueTypeBMP font) {
		// The atlas texture has been modified, update the texture on your text
		// materials to the new atlas texture here.
		getMaterial().setTexture("Texture", font.getAtlas());

		if (oldWidth != newWidth || oldHeight != newHeight) {
			  //Update your geometries here as the texture has been resized and the UV coordinates have changed. For example TrueTypeContainer.updateGeometry(); or TrueTypeNode.updateGeometry();
			needsGeomUpdate = true;
		}
	}
}
