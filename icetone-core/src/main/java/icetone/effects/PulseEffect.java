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
	}

}
