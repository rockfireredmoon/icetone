package icetone.effects;

import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;

public abstract class AbstractImageEffect extends Effect {

	protected Texture blendImage;
	protected Vector2f blendImageOffset;

	public AbstractImageEffect(float duration, Texture blendImage) {
		super(duration);
		this.blendImage = blendImage;
	}

	public void setBlendImage(Texture blendImage) {
		this.blendImage = blendImage;
	}

	public void setBlendImageOffset(Vector2f blendImageOffset) {
		this.blendImageOffset = blendImageOffset;
	}

	public Texture getBlendImage() {
		return blendImage;
	}

	public Vector2f getBlendImageOffset() {
		return blendImageOffset;
	}
}
