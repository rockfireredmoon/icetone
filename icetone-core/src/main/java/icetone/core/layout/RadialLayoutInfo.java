package icetone.core.layout;

import com.jme3.math.FastMath;

public class RadialLayoutInfo {
	private float angle;
	private float inset = Float.MIN_VALUE;

	public RadialLayoutInfo(String constraints) {
		for (String attr : constraints.split(",")) {
			if (attr.endsWith("rad")) {
				angle = Float.parseFloat(attr.substring(0, attr.length() - 3));
			} else if (attr.endsWith("deg")) {
				angle = Float.parseFloat(attr.substring(0, attr.length() - 3)) * FastMath.DEG_TO_RAD;
			} else if (attr.endsWith("px")) {
				inset = Float.parseFloat(attr.substring(0, attr.length() - 2));
			} else
				angle = Float.parseFloat(attr);
		}
	}

	public RadialLayoutInfo(float angle) {
		this.angle = angle;
	}

	public float getInset() {
		return inset;
	}

	public void setInset(float inset) {
		this.inset = inset;
	}

	public float getAngle() {
		return angle;
	}

	public RadialLayoutInfo setAngle(float angle) {
		this.angle = angle;
		return this;
	}
}