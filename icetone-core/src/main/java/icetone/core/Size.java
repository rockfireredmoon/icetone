package icetone.core;

import com.jme3.math.Vector2f;

public class Size extends Measurement {

	public final static Size MAX_SIZE = new Size(Float.MAX_VALUE, Float.MAX_VALUE);
	public final static Size AUTO = new Size();
	public final static Size FIT = new Size(Unit.FIT);
	public final static Size ZOOM = new Size(Unit.ZOOM);
	public final static Size FILL = new Size(100, 100, Unit.PERCENT);
	public final static Size ZERO = new Size(0, 0);

	public Size() {
		super();
	}

	public Size(Vector2f size) {
		super(size);
	}

	public Size(float x, float y, Unit xUnit, Unit yUnit) {
		super(x, y, xUnit, yUnit);
	}

	public Size(float x, float y, Unit unit) {
		super(x, y, unit);
	}

	public Size(float x, float y) {
		super(x, y);
	}

	public Size(Unit xUnit, Unit yUnit) {
		super(xUnit, yUnit);
	}

	public Size(Unit unit) {
		super(unit);
	}

	@Override
	public Size clone() {
		return new Size(x, y, xUnit, yUnit);
	}
}
