package icetone.controls.lists;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Spinner model that takes a list of strings (similar to {@link OLDSpinner}
 * .
 */
public class StringRangeSpinnerModel extends ArrayList<String> implements SpinnerModel<String> {

	private int pointer = 0;

	public StringRangeSpinnerModel() {
	}

	/**
	 * Create spinner from list of strings.
	 * 
	 * @param values
	 *            varargs of values
	 */
	public StringRangeSpinnerModel(String... values) {
		addAll(Arrays.asList(values));
	}

	public void setInitialValue(String value) {
		pointer = indexOf(value);
	}

	@Override
	public String getNextValue() {
		if (pointer + 1 < size()) {
			pointer++;
			return get(pointer);
		}
		return null;
	}

	@Override
	public String getPreviousValue() {
		if (pointer > 0) {
			pointer--;
			return get(pointer);
		}
		return null;
	}

	@Override
	public String getCurrentValue() {
		if (pointer < size()) {
			return get(pointer);
		}
		return null;
	}

	@Override
	public void wind(boolean forward) {
		if (forward && size() > 0) {
			pointer = size() - 1;
		} else {
			pointer = 0;
		}
	}

	@Override
	public void setValueFromString(String stringValue) {
		pointer = Math.max(0, indexOf(stringValue));
	}

	@Override
	public int getLargestValueSize(String formatter) {
		int m = 0;
		for(String v : this) {
			m = Math.max(String.format(formatter, v).length(), m);
		}
		return m;
	}
}