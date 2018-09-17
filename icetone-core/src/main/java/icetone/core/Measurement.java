package icetone.core;

import com.jme3.math.Vector2f;

import icetone.core.Measurement.Unit;

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
		this(vec.x, vec.y);
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

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Unit getxUnit() {
		return xUnit;
	}

	public void setxUnit(Unit xUnit) {
		this.xUnit = xUnit;
	}

	public Unit getyUnit() {
		return yUnit;
	}

	public void setyUnit(Unit yUnit) {
		this.yUnit = yUnit;
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
