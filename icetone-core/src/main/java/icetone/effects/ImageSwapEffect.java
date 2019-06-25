package icetone.effects;

import com.jme3.texture.Texture;

public class ImageSwapEffect extends AbstractImageEffect {

	public ImageSwapEffect(float duration, Texture blendImage) {
		super(duration, blendImage);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			disableShaderEffect();
			element.getMaterial().setTexture("ColorMap", blendImage);
			element.getMaterial().setFloat("EffectStep", 1.0f);
			// element.getElementMaterial().setBoolean("UseEffectTexCoords",
			// true);
			element.getMaterial().setBoolean("EffectImageSwap", true);
			if (element.isAtlasTextureInUse())
				element.getMaterial().setVector2("OffsetTexCoord", blendImageOffset);
			init = true;
			isActive = false;
		}
		updatePass(tpf);
	}

}
