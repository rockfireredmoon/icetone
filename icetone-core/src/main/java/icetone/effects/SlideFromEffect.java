package icetone.effects;

import com.jme3.math.Vector2f;

public class SlideFromEffect extends AbstractPositionedEffect {

	public SlideFromEffect(float duration) {
		super(duration);
	}

	public SlideFromEffect(float duration, Vector2f destination) {
		super(duration);
		setEffectDestination(destination);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			Vector2f pp = element.getPixelPosition();
			diff.set(destination.getX() - pp.x, destination.getY() - pp.y);
			init = true;
		}

		Vector2f inc = new Vector2f(diff.x * ( 1f - pass), diff.y * ( 1f - pass)).add(originalOrigin);
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
