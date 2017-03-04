package icetone.effects;

public class ZoomInEffect extends AbstractPositionedEffect {

	public ZoomInEffect(float duration) {
		super(duration);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			initPositions();
			init = true;
		}
		if (isActive) {
			element.setOrigin((originalSize.x / 2f) * (1f-pass), (originalSize.y / 2f) * -(1f-pass));
			element.setScale(pass);
		}
		updatePass(tpf);
	}

	@Override
	public void onDeactivate() {
		if (reset) {
			element.setOrigin(originalOrigin);
			element.setScale(originalScale);
		}
	}
}
