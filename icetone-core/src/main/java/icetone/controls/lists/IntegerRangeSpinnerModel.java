package icetone.controls.lists;

import java.util.Objects;

/**
 * Spinner model that takes a range of integers. This doesn't need to use
 * BigDecimal :)
 */
public class IntegerRangeSpinnerModel extends AbstractSpinnerModel<Integer> {

	private int min = 0;
	private int max = 100;
	private int incr = 1;
	private int value = 0;

	public IntegerRangeSpinnerModel() {
	}

	public IntegerRangeSpinnerModel(int min, int max, int incr, int value) {
		this.min = min;
		this.max = max;
		this.incr = incr;
		this.value = value;
	}

	public IntegerRangeSpinnerModel setMin(int min) {
		if (min != this.min) {
			this.min = min;
			if (value < min)
				value = min;
			fireChanged();
		}
		return this;
	}

	public IntegerRangeSpinnerModel setMax(int max) {
		if (max != this.max) {
			this.max = max;
			if (value > max)
				value = max;
			fireChanged();
		}
		return this;
	}

	public float getMin() {
		return min;
	}

	public IntegerRangeSpinnerModel setRange(int min, int max, int incr, int value) {
		this.min = min;
		this.max = max;
		this.incr = incr;
		this.value = value;
		fireChanged();
		return this;
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
	public SpinnerModel<Integer> setValueFromString(String stringValue) {
		int value = Integer.parseInt(stringValue);
		if (!Objects.equals(value, this.value)) {
			this.value = value;
			fireChanged();
		}
		return this;
	}

	@Override
	public int getLargestValueSize(String formatter) {
		return Math.max(String.format(formatter, min).length(), String.format(formatter, max).length());
	}

	@Override
	public SpinnerModel<Integer> setCurrentValue(Integer value) {
		if (!Objects.equals(value, this.value)) {
			this.value = value;
			fireChanged();
		}
		return this;
	}
}