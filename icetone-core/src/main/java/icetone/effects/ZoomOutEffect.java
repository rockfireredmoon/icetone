package icetone.effects;

public class ZoomOutEffect extends AbstractPositionedEffect {

	public ZoomOutEffect(float duration) {
		super(duration);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			initPositions();
			init = true;
		}
		if (isActive) {
			element.setOrigin((originalSize.x / 2f) * pass, (originalSize.y / 2f) * -pass);
			element.setScale(1 - pass);
		}
		updatePass(tpf);
	}

	@Override
	public void onDeactivate() {
		if (reset) {
			element.setOrigin(originalOrigin);
			element.setScale(originalScale);
			destroyOrHide();
		}
	}

}
