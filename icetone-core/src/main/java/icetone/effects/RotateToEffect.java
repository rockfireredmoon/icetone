package icetone.effects;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

import icetone.controls.scrolling.ScrollViewport;
import icetone.core.Layout.LayoutType;

public class RotateToEffect extends AbstractPositionedEffect {

	private float diff;

	public RotateToEffect(float duration) {
		super(duration);
	}

	public RotateToEffect(float duration, float destination) {
		super(duration);
		setEffectDestination(destination);
	}

	public RotateToEffect setEffectDestination(float destination) {
		setEffectDestination(new Vector2f(destination, 0));
		return this;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			float start = element.getRotation();
			float diff = destination.x - start;

			if (diff < 0) {
				/*
				 * Going anticlockwise, if the distance is shorter going clockwise, do that
				 * instead
				 */
				float adiff = 360 - start + destination.x;
				if (adiff < FastMath.abs(diff))
					diff = adiff;
			} else {
				/*
				 * Going clockwise, if the distance is shorter going anticlockwise, do that
				 * instead
				 */
				float adiff = 360 - destination.x + start;
				if (adiff < FastMath.abs(diff))
					diff = -adiff;
			}
			init = true;
		}

		element.setRotation(FastMath.floor(diff * pass));
		updatePass(tpf);
	}

	@Override
	protected void onDeactivate() {
		if (reset) {
			element.setRotation(originalElementRotation);
		} else {
			element.setPosition(destination);
			if (element instanceof ScrollViewport) {
				element.dirtyLayout(true, LayoutType.styling);
				((ScrollViewport) element).scrollContent(null);
			}
		}
	}

}
