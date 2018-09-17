package icetone.effects;

public class SlideOutEffect extends AbstractPositionedEffect {

	public SlideOutEffect(float duration, EffectDirection direction) {
		super(duration);
		setEffectDirection(direction == null ? EffectDirection.Top : direction);
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			initSlides();
			init = true;
		}
		switch (effectDir) {
		case Top:
			element.setOrigin(originalOrigin.x, originalOrigin.y + ((originalPixelPosition.y + originalSize.y) * -pass));
			break;
		case Bottom:
			element.setOrigin(originalOrigin.x, originalOrigin.y + ((bounds.y - originalPixelPosition.y) * pass));
			break;
		case Left:
			element.setOrigin(originalOrigin.x + ((originalPixelPosition.x + originalSize.x) * -pass), originalOrigin.y);
			break;
		case Right:
			element.setOrigin(originalOrigin.x + ((bounds.x - originalPixelPosition.x) * pass), originalOrigin.y);
			break;
		}
		updatePass(tpf);
	}

	@Override
	public void onDeactivate() {
		if (reset) {
			element.setOrigin(originalOrigin);
			destroyOrHide();
		}
	}
}
