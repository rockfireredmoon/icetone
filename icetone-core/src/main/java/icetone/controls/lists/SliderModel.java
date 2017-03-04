package icetone.controls.lists;

/**
 * Interface for providing data to the slider.
 */
public interface SliderModel<T extends Number> {
	/**
	 * Get the minimum the value of this model may be. It is up to the model
	 * implementation to clamp to this value if required.
	 * 
	 * @return maximum value.
	 */
	T getMin();

	/**
	 * Get the maximum the value of this model may be. It is up to the model
	 * implementation to clamp to this value if required.
	 * 
	 * @return maximum value.
	 */
	T getMax();

	/**
	 * Get the current value to represent as a position on the slider.
	 * 
	 * @return current value
	 */
	T getValue();

	/**
	 * Used {@link #setLockToStep(boolean)} is set to <code>true</code>,
	 * determines
	 * how big the step is from one valid value to the next.
	 * 
	 * @return step value
	 */
	T getStep();

	/**
	 * Step to either the next value or the previous one. The value will
	 * be increased or decreased by the step amount.
	 * 
	 * @param forward
	 *            step forward (or <code>false</code> to step back)
	 */
	void step(boolean forward);

	/**
	 * Set the value. It is up to the implementation how it interprets this
	 * value, be it as a position in a list or an actual value.
	 * <p>
	 * Do not invoke directly, the Slider calls this to update the model
	 * value.
	 * 
	 * @param value
	 *            value of slider
	 */
	void setValue(T indexToValue);

	/**
	 * Set the value as a ratio of the range, optionally locking the 
	 * value to the closest step
	 * 
	 * @param rat
	 * @param lockToStep
	 */
	T getAsRatioOfRange(float rat, boolean lockToStep);
}