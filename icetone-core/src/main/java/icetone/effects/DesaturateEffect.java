package icetone.effects;

public class DesaturateEffect extends Effect {

	public DesaturateEffect(float duration) {
		super(duration);
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
		element.getElementMaterial().setFloat("EffectStep", pass);
		if (pass >= 1.0) {
			isActive = false;
		}
		updatePass(tpf);
	}

}
