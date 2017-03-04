package icetone.controls.lists;

/**
 * {@link SliderModel} implementation that uses a range of integers.
 */
public class IntegerRangeSliderModel implements SliderModel<Integer> {

	private int min, max, value, step;

	public IntegerRangeSliderModel(int min, int max, int value) {
		this(min, max, value, 1);
	}

	public IntegerRangeSliderModel(int min, int max, int value, int step) {
		this.min = min;
		this.max = max;
		this.value = value;
		this.step = step;
	}

	@Override
	public Integer getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	@Override
	public Integer getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public void step(boolean forward) {
		if (forward) {
			value += step;
			if (value > max) {
				value = max;
			}
		} else {
			value -= step;
			if (value < min) {
				value = min;
			}
		}
	}

	@Override
	public Integer getStep() {
		return step;
	}

	@Override
	public void setValue(Integer value) {
		this.value = Math.min(Math.max(value, min), max);
	}

	@Override
	public Integer getAsRatioOfRange(float rat, boolean lockToStep) {
		int range = max - min;
		double progress = (double)range * rat;
		double r = progress + min;
		if (lockToStep) {
			double stepSize = range / (range / step);
			r = (Math.round(progress / stepSize)) * stepSize;
		}
		return ((Double)r).intValue();
	}

}