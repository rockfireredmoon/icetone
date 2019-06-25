package icetone.effects;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;

import icetone.core.Position;

public abstract class AbstractPositionedEffect extends Effect {

	protected Vector2f diff = new Vector2f();
	protected EffectDirection effectDir = EffectDirection.Top;
	protected Vector2f destination;
	protected Vector2f originalOrigin;
	protected Position originalPosition;
	protected Vector2f originalPixelPosition;
	protected Vector2f originalSize;
	protected Vector2f originalScale;
	protected Quaternion originalRotation;
	protected Vector2f bounds;
	protected Vector2f destinationUnits;
	protected float originalElementRotation;

	protected AbstractPositionedEffect(float duration) {
		this.duration = duration;
	}

	@Override
	public IEffect setEffectManager(EffectManager effectManager) {
		super.setEffectManager(effectManager);
		if (element != null) {
			originalOrigin = element.getOrigin().clone();
			originalPosition = element.getPosition().clone();
			originalPixelPosition = element.getPixelPosition().clone();
			originalScale = element.getScale().clone();
			originalSize = element.getDimensions().clone();
			originalRotation = element.getLocalRotation().clone();
			originalElementRotation = element.getRotation();
			bounds = element.getParentContainer().getDimensions();
		}
		return this;
	}

	public Effect setEffectDirection(EffectDirection effectDir) {
		this.effectDir = effectDir;
		return this;
	}

	public Effect setEffectDestination(Vector2f destination) {
		this.destination = destination;
		return this;
	}

	public Vector2f getEffectDestination() {
		return this.destination;
	}

	public EffectDirection getEffectDirection() {
		return this.effectDir;
	}

	protected void initPositions() {
		def.set(element.getPixelPosition().clone());
		diff.set(element.getWidth() / 2, element.getHeight() / 2);
	}

	// Effect methods
	protected void initSlides() {
		def.set(element.getPixelPosition().clone());
		if (effectDir == EffectDirection.Bottom) {
			diff.set(0, element.getAbsoluteHeight());
		} else if (effectDir == EffectDirection.Top) {
			diff.set(0, element.getScreen().getHeight() - element.getAbsoluteY());
		} else if (effectDir == EffectDirection.Left) {
			diff.set(element.getAbsoluteWidth(), 0);
		} else if (effectDir == EffectDirection.Right) {
			diff.set(element.getScreen().getWidth() - element.getAbsoluteX(), 0);
		}
	}
}
