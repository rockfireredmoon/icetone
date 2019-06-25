package icetone.effects;

import com.jme3.texture.Texture;

public class ImageFadeInEffect extends AbstractImageEffect {

	public ImageFadeInEffect(float duration, Texture blendImage) {
		super(duration, blendImage);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			element.getMaterial().setBoolean("UseEffect", true);
			element.getMaterial().setBoolean("EffectFade", false);
			element.getMaterial().setBoolean("EffectPulse", true);
			element.getMaterial().setBoolean("EffectPulseColor", false);
			element.getMaterial().setBoolean("EffectImageSwap", false);
			element.getMaterial().setTexture("EffectMap", blendImage);
			if (element.isAtlasTextureInUse())
				element.getMaterial().setVector2("OffsetTexCoord", blendImageOffset);
			init = true;
		}
		element.getMaterial().setFloat("EffectStep", pass);
		updatePass(tpf);
	}

}
