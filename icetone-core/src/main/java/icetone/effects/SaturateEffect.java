package icetone.effects;

public class SaturateEffect extends Effect {

	public SaturateEffect(float duration) {
		this.duration = duration;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			element.getMaterial().setBoolean("UseEffect", true);
			element.getMaterial().setBoolean("EffectFade", false);
			element.getMaterial().setBoolean("EffectPulse", false);
			element.getMaterial().setBoolean("EffectSaturate", true);
			init = true;
		}
		if (pass >= 1.0) {
			disableShaderEffect();
			isActive = false;
		} else {
			element.getMaterial().setFloat("EffectStep", Math.max(0, 1 - pass));
		}
		updatePass(tpf);
	}

}
