package icetone.effects;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

import icetone.controls.scrolling.ScrollViewport;
import icetone.core.Layout.LayoutType;

public class ScrollToEffect extends AbstractPositionedEffect {

	public ScrollToEffect(float duration) {
		super(duration);
	}

	public ScrollToEffect(float duration, Vector2f destination) {
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

		Vector2f inc = new Vector2f(FastMath.floor(diff.x * pass), FastMath.floor(diff.y * pass))
				.add(originalPixelPosition);
		element.setPosition(inc);
		if (element instanceof ScrollViewport) {
			((ScrollViewport) element).scrollContent(null);
		}
		updatePass(tpf);
	}

	@Override
	protected void onDeactivate() {
		if (reset) {
			element.setPosition(originalPosition);
		} else {
			element.setPosition(destination);
			if (element instanceof ScrollViewport) {
				element.dirtyLayout(true, LayoutType.styling);
				((ScrollViewport) element).scrollContent(null);
			}
		}
	}

}
