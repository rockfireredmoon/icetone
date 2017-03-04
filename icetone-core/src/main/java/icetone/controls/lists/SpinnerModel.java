package icetone.controls.lists;

/**
 * Use this interface to provide the values to use in the spinner.
 * Implementations
 * must provide next, previous and current value so in general would keep a
 * pointer
 * and a list or range of values.
 */
public interface SpinnerModel<T> {
	/**
	 * Get the next value in the sequence. If the end of the sequence has
	 * been
	 * reached then <code>null</code> should be returned.
	 */
	T getNextValue();

	/**
	 * Get the previous value in sequence. If the pointer is currently at
	 * the first value in the sequence, the <code>null</code> should be
	 * returned.
	 * 
	 * @return
	 */
	T getPreviousValue();

	/**
	 * Get the current value.
	 * 
	 * @return value
	 */
	T getCurrentValue();

	/**
	 * Wind the spinner to either the start or the end of sequence. To go
	 * forward,
	 * supply <code>true</code> as the argument.
	 * 
	 * @param forward
	 *            wind forward to end (or back to start if
	 *            <code>false</code>)
	 */
	void wind(boolean forward);

	/**
	 * Get the number of characters in the largest value. This is used 
	 * 
	 * @param stringValue
	 */
	void setValueFromString(String stringValue);
	
	/**
	 * Get the size of the largest value in characters.
	 * 
	 * @param forammter formatter string
	 */
	int getLargestValueSize(String formatter);
}