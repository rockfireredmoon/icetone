package icetone.controls.lists;

public class SelectListItem<O> extends AbstractListItem<O, SelectList<O>> {

	public SelectListItem(SelectList<O> menu, String caption, O value, boolean isToggleItem, boolean isToggled) {
		super(menu, caption, value, isToggleItem, isToggled);
	}

}
