package icetone.controls.lists;

/**
 * Use this interface to provide the values to use in the spinner.
 * Implementations must provide next, previous and current value so in general
 * would keep a pointer and a list or range of values.
 */
public interface SpinnerModel<T> {

	/**
	 * Listener invoked when the model changes in some way.
	 */
	public interface SpinnerModelListener {
		/**
		 * Invoked when the model changes in some way.
		 */
		void modelChanged();
	}

	/**
	 * Add a listener to the list of those to be notified when the model changes in
	 * some way and the component should adjust its to take this into account, e.g.
	 * a maximum value changing, or the list of available values changing.
	 * 
	 * @param listener listener
	 */
	void addListener(SpinnerModelListener listener);

	/**
	 * Remove a listener from the list of those to be notified when the model
	 * changes in some way and the component should adjust its to take this into
	 * account, e.g. a maximum value changing, or the list of available values
	 * changing.
	 * 
	 * @param listener listener
	 */
	void removeListener(SpinnerModelListener listener);

	/**
	 * Get the next value in the sequence. If the end of the sequence has been
	 * reached then <code>null</code> should be returned.
	 */
	T getNextValue();

	/**
	 * Get the previous value in sequence. If the pointer is currently at the first
	 * value in the sequence, the <code>null</code> should be returned.
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
	 * Wind the spinner to either the start or the end of sequence. To go forward,
	 * supply <code>true</code> as the argument.
	 * 
	 * @param forward wind forward to end (or back to start if <code>false</code>)
	 */
	void wind(boolean forward);

	/**
	 * Set the value of the model given a string
	 * 
	 * @param stringValue value
	 */
	SpinnerModel<T> setValueFromString(String stringValue);

	/**
	 * Set the value of the model
	 * 
	 * @param value value
	 */
	SpinnerModel<T> setCurrentValue(T value);

	/**
	 * Get the size of the largest value in characters.
	 * 
	 * @param forammter formatter string
	 */
	int getLargestValueSize(String formatter);
}