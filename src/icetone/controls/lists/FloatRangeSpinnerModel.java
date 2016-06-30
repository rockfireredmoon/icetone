package icetone.controls.lists;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Spinner model that takes a range of floats. This uses {@link BigDecimal}
 * internally to avoid float rounding problems with small step sizes (e.g.
 * 0.1)
 */
public class FloatRangeSpinnerModel implements SpinnerModel<Float> {

	private BigDecimal min = new BigDecimal(0f);
	private BigDecimal max = new BigDecimal(100f);
	private BigDecimal incr = new BigDecimal(1f);
	private BigDecimal value = new BigDecimal("0.0");
	private boolean started;
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

	public void setRange(float min, float max, float incr, float value) {
		this.min = new BigDecimal(String.valueOf(min));
		this.max = new BigDecimal(String.valueOf(max));
		this.incr = new BigDecimal(String.valueOf(incr));
		this.value = new BigDecimal(String.valueOf(value));
		if (precision != -1) {
			this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
		started = false;
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
	public void setValueFromString(String stringValue) {
		value = new BigDecimal(stringValue);
		if (precision != -1) {
			this.value.setScale(precision, BigDecimal.ROUND_HALF_UP);
		}
	}

	@Override
	public int getLargestValueSize(String formatter) {
		return Math.max(String.format(formatter, min).length(), String.format(formatter, max).length());
	}
}