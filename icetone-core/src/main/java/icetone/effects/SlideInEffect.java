package icetone.effects;

public class SlideInEffect extends AbstractPositionedEffect {

	public SlideInEffect(float duration, EffectDirection direction) {
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
			element.setOrigin(0, (originalPosition.y + originalSize.y) * -(1f- pass));
			break;
		case Bottom:
			element.setOrigin(0, (bounds.y - originalPosition.y) * (1f - pass));
			break;
		case Left:
			element.setOrigin(-((originalPosition.x + originalSize.x ) * (1f - pass)), 0);
			break;
		case Right:
			element.setOrigin(((bounds.x - originalPosition.x) * (1f - pass)), 0);
			break;
		}
		updatePass(tpf);
	}

	@Override
	public void onDeactivate() {
		if (reset) {
			element.setOrigin(originalOrigin);
		}
	}
}
