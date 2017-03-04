package icetone.effects;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

public class SpinEffect extends AbstractPositionedEffect {

	public SpinEffect(float duration) {
		super(duration);
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return super.isConflict(effect) || effect instanceof SpinInEffect || effect instanceof SpinOutEffect;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			initPositions();
			init = true;
		} else if (isActive) {
			element.setLocalRotation(element.getLocalRotation().fromAngles(0, 0, 360 * FastMath.DEG_TO_RAD * pass));
			element.center();
			element.move(element.getWidth() / 2f, element.getHeight() / 2f, 0);
		}
		if (pass >= 1.0) {
			element.setLocalRotation(element.getLocalRotation().fromAngles(0, 0, 0));
			isActive = false;
		}
		updatePass(tpf);
	}

	@Override
	public void onDeactivate() {
		if (reset) {
			element.setLocalRotation(originalRotation);
			element.setOrigin(originalOrigin);
		}
	}

}
