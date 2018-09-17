package icetone.core;

import icetone.core.Measurement.Unit;

public class Position extends Measurement {

	public static final Position TOP_LEFT = new Position(0, 0, Unit.PX, Unit.PX);
	public static final Position AUTO = new Position(0, 0, Unit.AUTO, Unit.AUTO);

	public Position() {
		super();
	}

	public Position(float x, float y, Unit xUnit, Unit yUnit) {
		super(x, y, xUnit, yUnit);
	}

	public Position(float x, float y, Unit unit) {
		super(x, y, unit);
	}

	public Position(float x, float y) {
		super(x, y);
	}

	public Position(Unit xUnit, Unit yUnit) {
		super(xUnit, yUnit);
	}

	public Position(Unit unit) {
		super(unit);
	}

	@Override
	public Position clone() {
		return new Position(x, y, xUnit, yUnit);
	}
}
