package icetone.effects;

import com.jme3.math.ColorRGBA;

public abstract class AbstractColorEffect extends Effect {

	private ColorRGBA blendColor;

	public AbstractColorEffect(float duration, ColorRGBA blendColor) {
		this.duration = duration;
		this.blendColor = blendColor;
	}

	public ColorRGBA getBlendColor() {
		return blendColor;
	}

	public void setBlendColor(ColorRGBA blendColor) {
		this.blendColor = blendColor;
	}
}
