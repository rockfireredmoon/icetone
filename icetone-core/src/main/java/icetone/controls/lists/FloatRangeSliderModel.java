package icetone.controls.lists;

/**
 * {@link SliderModel} implementation that uses a range of floats.
 */
public class FloatRangeSliderModel implements SliderModel<Float> {

	private float min, max, value, step;

	public FloatRangeSliderModel(float min, float max, float value) {
		this(min, max, value, 1f);
	}

	public FloatRangeSliderModel(float min, float max, float value, float step) {
		this.min = min;
		this.max = max;
		this.value = value;
		this.step = step;
	}

	@Override
	public Float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	@Override
	public Float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	@Override
	public Float getValue() {
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
	public Float getStep() {
		return step;
	}

	@Override
	public void setValue(Float value) {
		this.value = Math.min(Math.max(value, min), max);
	}

	@Override
	public Float getAsRatioOfRange(float rat, boolean lockToStep) {
		float range = max - min;
		float progress = range * rat;
		float r = progress + min;
		if (lockToStep) {
			float stepSize = range / (range / step );
			r = (Math.round(progress / stepSize)) * stepSize;
		}
		return r;

	}

}