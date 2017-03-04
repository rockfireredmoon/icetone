package icetone.controls.text;


public class AutocompleteItem<V extends Object> implements Comparable<AutocompleteItem<V>> {
	private String text;
	private V value;

	public AutocompleteItem(String text, V value) {
		this.text = text;
		this.value = value;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		AutocompleteItem<V> other = (AutocompleteItem<V>) obj;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public int compareTo(AutocompleteItem<V> o) {
		return text.compareTo(o.text);
	}
}