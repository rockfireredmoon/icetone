package icetone.controls.text;

import java.util.List;

public interface AutocompleteSource<V extends Object> {
	List<AutocompleteItem<V>> getItems(String text);
}