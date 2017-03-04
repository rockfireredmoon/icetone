package icetone.effects;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class SpinInEffect extends AbstractPositionedEffect {

	public SpinInEffect(float duration) {
		super(duration);
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return super.isConflict(effect) || effect instanceof SpinEffect || effect instanceof SpinOutEffect;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			initPositions();
			Vector2f inc = new Vector2f(diff.x * pass, diff.y * pass);
			element.setPosition(def.add(diff.subtract(inc)));
			element.setLocalScale(pass);
			element.show();
			init = true;
		} else if (isActive) {
			Vector2f inc = new Vector2f(diff.x * pass, diff.y * pass);
			element.setPosition(def.add(diff.subtract(inc)));
			element.setLocalScale(pass);
			element.setLocalRotation(element.getLocalRotation().fromAngles(0, 0, 360 * FastMath.DEG_TO_RAD * pass));
		}
		if (pass >= 1.0) {
			element.setPosition(def);
			element.setLocalScale(pass);
			element.setLocalRotation(element.getLocalRotation().fromAngles(0, 0, 0));
			isActive = false;
		}
		updatePass(tpf);
	}

}
