package icetone.effects;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class SpinOutEffect extends AbstractPositionedEffect {

	public SpinOutEffect(float duration) {
		super(duration);
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return super.isConflict(effect) || effect instanceof SpinEffect || effect instanceof SpinInEffect;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			initPositions();
			Vector2f inc = new Vector2f(diff.x * pass, diff.y * pass);
			element.setPosition(def.subtract(inc));
			element.setLocalScale(1 - pass);
			init = true;
		} else if (isActive) {
			Vector2f inc = new Vector2f(diff.x * pass, diff.y * pass);
			element.setPosition(def.subtract(inc));
			element.setLocalScale(1 - pass);
			element.setLocalRotation(
					element.getLocalRotation().fromAngles(0, 0, 360 * FastMath.DEG_TO_RAD * (1.0f - pass)));
		}
		if (pass >= 1.0) {
			destroyOrHide();
		}
		updatePass(tpf);
	}

}
