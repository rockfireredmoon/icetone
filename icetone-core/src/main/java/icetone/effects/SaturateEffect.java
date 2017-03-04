package icetone.effects;

public class SaturateEffect extends Effect {

	public SaturateEffect(float duration) {
		this.duration = duration;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			element.getElementMaterial().setBoolean("UseEffect", true);
			element.getElementMaterial().setBoolean("EffectFade", false);
			element.getElementMaterial().setBoolean("EffectPulse", false);
			element.getElementMaterial().setBoolean("EffectSaturate", true);
			init = true;
		}
		if (pass >= 1.0) {
			disableShaderEffect();
			isActive = false;
		} else {
			element.getElementMaterial().setFloat("EffectStep", Math.max(0, 1 - pass));
		}
		updatePass(tpf);
	}

}
