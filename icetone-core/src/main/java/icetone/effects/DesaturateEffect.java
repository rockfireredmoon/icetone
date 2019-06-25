package icetone.effects;

public class DesaturateEffect extends Effect {

	public DesaturateEffect(float duration) {
		super(duration);
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
		element.getMaterial().setFloat("EffectStep", pass);
		if (pass >= 1.0) {
			isActive = false;
		}
		updatePass(tpf);
	}

}
