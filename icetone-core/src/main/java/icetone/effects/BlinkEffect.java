package icetone.effects;

import com.jme3.math.FastMath;

public class BlinkEffect extends Effect implements AlphaEffect {

	private float alpha;

	public BlinkEffect(float duration) {
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
		element.setLocalAlpha(FastMath.abs(pass - 0.5f) * 2 * alpha);
		updatePass(tpf);
	}

	@Override
	protected void onDeactivate() {
		if (reset) {
			element.setLocalAlpha(alpha);
		}
		destroyOrHide();
	}

}
