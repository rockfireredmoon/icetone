package icetone.effects;

public class ConcertinaOutEffect extends AbstractPositionedEffect {
	private EffectDirection direction;

	public ConcertinaOutEffect(float duration, EffectDirection direction) {
		super(duration);
		this.direction = direction;
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return super.isConflict(effect) || effect instanceof ConcertinaInEffect;
	}

	@Override
	public void update(float tpf) {
		if (!init) {
			initPositions();
			init = true;
		}
		if (isActive) {
			switch (direction) {
			case Top:
			case Bottom:
				element.setOrigin(0, (originalSize.y / 2f) * -pass);
				element.setScale(1, 1 - pass);
				break;
			default:
				element.setOrigin((originalSize.x / 2f) * pass, 0);
				element.setScale(1 - pass, 1);
				break;
			}
		}
		updatePass(tpf);
	}

	public EffectDirection getDirection() {
		return direction;
	}

	public void setDirection(EffectDirection direction) {
		this.direction = direction;
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
