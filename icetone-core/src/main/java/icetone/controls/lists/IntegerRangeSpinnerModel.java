package icetone.controls.lists;

/**
 * Spinner model that takes a range of integers. This doesn't need to use
 * BigDecimal :)
 */
public class IntegerRangeSpinnerModel implements SpinnerModel<Integer> {

	private int min = 0;
	private int max = 100;
	private int incr = 1;
	private int value = 0;
	private boolean started;

	public IntegerRangeSpinnerModel() {
	}

	public IntegerRangeSpinnerModel(int min, int max, int incr, int value) {
		this.min = min;
		this.max = max;
		this.incr = incr;
		this.value = value;
	}

	public float getMin() {
		return min;
	}

	public void setRange(int min, int max, int incr, int value) {
		this.min = min;
		this.max = max;
		this.incr = incr;
		this.value = value;
		started = false;
	}

	@Override
	public Integer getNextValue() {
		if (value >= max) {
			return null;
		}
		value += incr;
		if (value > max) {
			value = max;
		}
		return value;
	}

	@Override
	public Integer getPreviousValue() {
		if (value <= min) {
			return null;
		}
		value -= incr;
		if (value < min) {
			value = min;
		}
		return value;
	}

	@Override
	public Integer getCurrentValue() {
		return value;
	}

	@Override
	public void wind(boolean forward) {
		if (forward) {
			value = max;
		} else {
			value = min;
		}
	}

	@Override
	public void setValueFromString(String stringValue) {
		value = Integer.parseInt(stringValue);
	}

	@Override
	public int getLargestValueSize(String formatter) {
		return Math.max(String.format(formatter, min).length(), String.format(formatter, max).length());
	}
}