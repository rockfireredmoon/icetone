package icetone.effects;

import com.jme3.math.ColorRGBA;

public class PulseColorEffect extends AbstractColorEffect {
	protected boolean direction = true;

	public PulseColorEffect(float duration, ColorRGBA blendColor) {
		super(duration, blendColor);
	}

	public boolean isDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
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
			element.getMaterial().setBoolean("EffectPulse", false);
			element.getMaterial().setBoolean("EffectPulseColor", true);
			element.getMaterial().setBoolean("EffectImageSwap", false);
			element.getMaterial().setColor("EffectColor", getBlendColor());
			init = true;
		}
		element.getMaterial().setFloat("EffectStep", pass);
	}

}
