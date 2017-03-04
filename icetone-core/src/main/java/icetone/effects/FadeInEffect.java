package icetone.effects;

public class FadeInEffect extends Effect implements AlphaEffect {

	private float alpha;

	public FadeInEffect(float duration) {
		this.duration = duration;
	}

	@Override
	public IEffect setEffectManager(EffectManager effectManager) {
		super.setEffectManager(effectManager);
		alpha = element.getLocalAlpha();
		return this;
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return super.isConflict(effect) || effect instanceof AlphaEffect;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			disableShaderEffect();
			init = true;
		}
		element.setLocalAlpha(pass * alpha);
		updatePass(tpf);
	}

	@Override
	protected void onDeactivate() {
		if (reset) {
			element.setLocalAlpha(alpha);
		}
	}

}
