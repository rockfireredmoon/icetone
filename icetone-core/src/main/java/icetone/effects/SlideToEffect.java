package icetone.effects;

import com.jme3.math.Vector2f;

public class SlideToEffect extends AbstractPositionedEffect {

	public SlideToEffect(float duration) {
		super(duration);
	}

	public SlideToEffect(float duration, Vector2f destination) {
		super(duration);
		setEffectDestination(destination);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			Vector2f pp = element.getPixelPosition();
			diff.set(destination.getX() - pp.x,
					destination.getY() - pp.y);
			init = true;
		}

		Vector2f inc = new Vector2f(diff.x * pass, diff.y * pass).add(originalOrigin);
		element.setOrigin(inc);
		updatePass(tpf);
	}

	@Override
	protected void onDeactivate() {
		if (reset) {
			element.setOrigin(originalOrigin);
		}
	}

}
