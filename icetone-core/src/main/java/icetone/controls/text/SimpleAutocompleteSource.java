package icetone.controls.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SimpleAutocompleteSource<V extends Object> implements AutocompleteSource<V> {
	
	private Collection<V> values;

	public SimpleAutocompleteSource(V... values) {
		this(Arrays.asList(values));
	}
	
	public SimpleAutocompleteSource(Collection<V> values) {
		this.values = values;
	}

	@Override
	public List<AutocompleteItem<V>> getItems(String text) {
		List<AutocompleteItem<V>> l = new ArrayList<>();
		for(V v : values) {
			if(text != null && String.valueOf(v).toLowerCase().startsWith(text.toLowerCase())) {
				l.add(new AutocompleteItem<V>(String.valueOf(v), v));
			}
		}
		return l;
	}
}