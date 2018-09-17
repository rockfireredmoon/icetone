package icetone.core;

import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.Vector4f;

public class ClippingDefine {
	private BaseElement owner;
	private Vector4f clip = null;
	private Vector4f tempV4 = new Vector4f();

	public ClippingDefine(BaseElement owner) {
		this.owner = owner;
		if (owner == null) {
			throw new IllegalArgumentException("Owner may not be null.");
		}
	}

	public ClippingDefine(BaseElement owner, Vector4f clip) {
		this.owner = owner;
		this.clip = clip == null ? null : new Vector4f(clip);
		if (owner == null) {
			throw new IllegalArgumentException("Owner may not be null.");
		}
	}

	public Vector4f getClipping() {
		float absoluteX = owner.getAbsoluteX();

		BaseElement e = owner;
		float originY = 0;
		float originX = 0;
		while (e != null) {
			if (e.getValign() == VAlign.Bottom)
				originY += e.origin.y;
			else
				originY -= e.origin.y;
			originX += e.origin.x;
			e = e.getElementParent();
		}

		if (clip == null) {
			tempV4.setX(absoluteX + originX);
			tempV4.setY(owner.screen.getHeight() - owner.getAbsoluteHeight() + originY);
			tempV4.setZ(tempV4.getX() + owner.getWidth());
			tempV4.setW(tempV4.getY() + owner.getHeight());
		} else {
			tempV4.setX(absoluteX + originX + clip.x);
			tempV4.setY(owner.screen.getHeight() - owner.getAbsoluteHeight() + originY + clip.y);
			tempV4.setZ(tempV4.getX() + clip.z);
			tempV4.setW(tempV4.getY() + clip.w);
		}
		return tempV4;
	}

	public BaseElement getElement() {
		return owner;
	}

	public void setClipping(Vector4f tempV4) {
		this.tempV4 = tempV4;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clip == null) ? 0 : clip.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClippingDefine other = (ClippingDefine) obj;
		if (clip == null) {
			if (other.clip != null)
				return false;
		} else if (!clip.equals(other.clip))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClippingDefine [owner=" + owner + ", clip=" + clip + "]";
	}
}