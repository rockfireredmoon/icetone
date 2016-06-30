package icetone.controls.menuing;

import icetone.controls.lists.AbstractListItem;

/**
 * @author t0neg0d
 */
public class MenuItem<O> extends AbstractListItem<O, Menu<O>> {

	protected Menu<O> subMenu;

	public MenuItem(Menu<O> menu, String caption, O value, boolean isToggleItem, boolean isToggled) {
		super(menu, caption, value, isToggleItem, isToggled);
	}

	public MenuItem(Menu<O> menu, String caption, O value, Menu<O> subMenu, boolean isToggleItem,
			boolean isToggled) {
		super(menu, caption, value, isToggleItem, isToggled);
		this.subMenu = subMenu;
	}

	/**
	 * Returns the SubMenu set for this MenuItem
	 * 
	 * @return Menu
	 */
	public Menu<O> getSubMenu() {
		return this.subMenu;
	}
}
