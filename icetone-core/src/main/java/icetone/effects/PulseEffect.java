package icetone.effects;

import com.jme3.texture.Texture;

public class PulseEffect extends AbstractImageEffect {

	protected boolean direction = true;

	public PulseEffect(float duration, Texture image) {
		super(duration, image);
	}

	@Override
	public void update(float tpf) {
		if (pass >= 1.0f) {
			pass = 1.0f;
			direction = false;
		} else if (pass <= 0.0f) {
			pass = 0.0f;
			direction = true;
		}
		if (direction)
			time += tpf;
		else
			time -= tpf;
		pass = interpolation.apply(time / getDuration());
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
		element.getElementMaterial().setFloat("EffectStep", pass);
	}

}
