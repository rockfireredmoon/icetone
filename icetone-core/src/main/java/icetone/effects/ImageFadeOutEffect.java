package icetone.effects;

import com.jme3.texture.Texture;

public class ImageFadeOutEffect extends AbstractImageEffect {

	public ImageFadeOutEffect(float duration, Texture blendImage) {
		super(duration, blendImage);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			element.getElementMaterial().setBoolean("UseEffect", true);
			element.getElementMaterial().setBoolean("EffectFade", false);
			element.getElementMaterial().setBoolean("EffectPulse", true);
			element.getElementMaterial().setBoolean("EffectPulseColor", false);
			element.getElementMaterial().setBoolean("EffectImageSwap", false);
			element.getElementMaterial().setTexture("EffectMap", blendImage);
			if (element.isAtlasTextureInUse())
				element.getElementMaterial().setVector2("OffsetTexCoord", blendImageOffset);
			init = true;
		}
		element.getElementMaterial().setFloat("EffectStep", 1.0f - pass);
		updatePass(tpf);
	}

}
