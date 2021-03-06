package icetone.core;

import com.jme3.math.Vector2f;

public class Measurement implements Cloneable {

	public enum Unit {
		PX, PERCENT, AUTO, ZOOM, FIT
	}

	public float x;
	public float y;
	public Unit xUnit;
	public Unit yUnit;

	public Measurement() {
		this(Unit.AUTO);
	}

	public Measurement(Vector2f vec) {
		this();
		if(vec == null) {
			this.x = 0;
			this.y = 0;
			xUnit = Unit.AUTO;
			yUnit = Unit.AUTO;
		}
		else {
			this.x = vec.x;
			this.y = vec.y;
			xUnit = Unit.PX;
			yUnit = Unit.PX;
		}
			
	}

	public Measurement(Unit unit) {
		this(unit, unit);
	}

	public Measurement(Unit xUnit, Unit yUnit) {
		this(0, 0, xUnit, yUnit);
	}

	public Measurement(float x, float y) {
		super();
		this.x = x;
		this.y = y;

		xUnit = Unit.PX;
		yUnit = Unit.PX;
	}

	public Measurement(float x, float y, Unit xUnit, Unit yUnit) {
		super();
		this.x = x;
		this.y = y;
		this.xUnit = xUnit;
		this.yUnit = yUnit;
	}

	public Measurement(float x, float y, Unit unit) {
		this(x, y, unit, unit);
	}

	public float getX() {
		return x;
	}

	public Measurement setX(float x) {
		this.x = x;
		return this;
	}

	public float getY() {
		return y;
	}

	public Measurement setY(float y) {
		this.y = y;
		return this;
	}

	public Unit getxUnit() {
		return xUnit;
	}

	public Measurement setxUnit(Unit xUnit) {
		this.xUnit = xUnit;
		return this;
	}

	public Unit getyUnit() {
		return yUnit;
	}

	public Measurement setyUnit(Unit yUnit) {
		this.yUnit = yUnit;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + ((xUnit == null) ? 0 : xUnit.hashCode());
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + ((yUnit == null) ? 0 : yUnit.hashCode());
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
		Measurement other = (Measurement) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (xUnit != other.xUnit)
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (yUnit != other.yUnit)
			return false;
		return true;
	}

	public Measurement set(float x, float y, Unit xUnit, Unit yUnit) {
		this.x = x;
		this.y = y;
		this.xUnit = xUnit;
		this.yUnit = yUnit;
		return this;
	}

	public Measurement set(Vector2f position) {
		this.x = position.x;
		this.y = position.y;
		return this;
	}

	public Measurement setUnits(Unit unit) {
		this.xUnit = unit;
		this.yUnit = unit;
		return this;
	}

	@Override
	public String toString() {
		return "Measurement [x=" + x + ", y=" + y + ", xUnit=" + xUnit + ", yUnit=" + yUnit + "]";
	}

	public Vector2f toVector2f() {
		return new Vector2f(x, y);
	}

}
