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
			element.getElementMaterial().setBoolean("UseEffect", true);
			element.getElementMaterial().setBoolean("EffectFade", false);
			element.getElementMaterial().setBoolean("EffectPulse", false);
			element.getElementMaterial().setBoolean("EffectPulseColor", true);
			element.getElementMaterial().setBoolean("EffectImageSwap", false);
			element.getElementMaterial().setColor("EffectColor", getBlendColor());
			init = true;
		}
		element.getElementMaterial().setFloat("EffectStep", pass);
	}

}
