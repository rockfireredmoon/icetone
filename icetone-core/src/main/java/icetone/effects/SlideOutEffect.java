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
			element.setOrigin(0, (originalPosition.y + originalSize.y) * -pass);
			break;
		case Bottom:
			element.setOrigin(0, (bounds.y - originalPosition.y) * pass);
			break;
		case Left:
			element.setOrigin((originalPosition.x + originalSize.x) * -pass, 0);
			break;
		case Right:
			element.setOrigin((bounds.x - originalPosition.x) * pass, 0);
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
