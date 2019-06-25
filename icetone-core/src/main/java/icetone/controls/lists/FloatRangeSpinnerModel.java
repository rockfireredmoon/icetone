package icetone.controls.lists;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * Spinner model that takes a range of floats. This uses {@link BigDecimal}
 * internally to avoid float rounding problems with small step sizes (e.g. 0.1)
 */
public class FloatRangeSpinnerModel extends AbstractSpinnerModel<Float> {

	private BigDecimal min = new BigDecimal(0f);
	private BigDecimal max = new BigDecimal(100f);
	private BigDecimal incr = new BigDecimal(1f);
	private BigDecimal value = new BigDecimal("0.0");
	private int precision = -1;

	/**
	 * Default contructor, min = 0, max = 100, incr = 1, value = 0
	 */
	public FloatRangeSpinnerModel() {
	}

	public FloatRangeSpinnerModel(float min, float max, float incr, float value) {
		this.min = new BigDecimal(String.valueOf(min));
		this.max = new BigDecimal(String.valueOf(max));
		this.incr = new BigDecimal(String.valueOf(incr));
		this.value = new BigDecimal(String.valueOf(value));
	}

	public void setPrecision(int precision) {
		this.precision = precision;
		this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Adjust the value so it is the closest mulitple of the increment.
	 */
	public void snapToIncr() {
		BigDecimal[] vals = this.value.divideAndRemainder(this.incr);
		this.value = vals[0].multiply(incr);
		if (precision != -1) {
			this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
	}

	public float getMin() {
		return min.floatValue();
	}

	public FloatRangeSpinnerModel setRange(float min, float max, float incr, float value) {
		this.min = new BigDecimal(String.valueOf(min));
		this.max = new BigDecimal(String.valueOf(max));
		this.incr = new BigDecimal(String.valueOf(incr));
		this.value = new BigDecimal(String.valueOf(value));
		if (precision != -1) {
			this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
		fireChanged();
		return this;
	}

	public FloatRangeSpinnerModel setMin(float min) {
		BigDecimal newMin = new BigDecimal(String.valueOf(min));
		if (!this.min.equals(newMin)) {
			this.min = newMin;
			if (value.compareTo(this.min) < 0)
				value = this.min;
			fireChanged();
		}
		return this;
	}

	public FloatRangeSpinnerModel setMax(float max) {
		BigDecimal newMax = new BigDecimal(String.valueOf(max));
		if (!this.max.equals(newMax)) {
			this.max = newMax;
			if (value.compareTo(this.max) > 0)
				value = this.max;
			fireChanged();
		}
		return this;
	}

	@Override
	public Float getNextValue() {
		value = value.add(incr);
		if (value.floatValue() > max.floatValue()) {
			value = max;
			return null;
		}
		if (precision != -1) {
			this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
		return value.floatValue();
	}

	@Override
	public Float getPreviousValue() {
		value = value.subtract(incr);
		if (value.floatValue() < min.floatValue()) {
			value = min;
			return null;
		}
		if (precision != -1) {
			this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
		return value.round(MathContext.UNLIMITED).floatValue();
	}

	@Override
	public Float getCurrentValue() {
		return value.floatValue();
	}

	@Override
	public void wind(boolean forward) {
		if (forward) {
			value = max;
		} else {
			value = min;
		}
		snapToIncr();
	}

	@Override
	public SpinnerModel<Float> setCurrentValue(Float value) {
		BigDecimal newVal = BigDecimal.valueOf(value);
		if (!Objects.equals(this.value, newVal)) {
			this.value = newVal;
			fireChanged();
		}
		return this;
	}

	@Override
	public SpinnerModel<Float> setValueFromString(String stringValue) {
		value = new BigDecimal(stringValue);
		if (precision != -1) {
			this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
		return this;
	}

	@Override
	public int getLargestValueSize(String formatter) {
		return Math.max(String.format(formatter, min).length(), String.format(formatter, max).length());
	}
}