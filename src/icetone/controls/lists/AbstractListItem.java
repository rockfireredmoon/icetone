package icetone.controls.lists;

import icetone.controls.buttons.CheckBox;
import icetone.core.Element;

/**
 * @author t0neg0d
 */
public abstract class AbstractListItem<O, M extends AbstractList<O, ?>> {
	protected AbstractList<O, ?> menu;
	protected String caption;
	protected O value;
	protected boolean isToggleItem = false;
	protected boolean isToggled = false;
	protected Element element;

	public AbstractListItem(AbstractList<O, ?> menu, String caption, O value, boolean isToggleItem, boolean isToggled) {
		this.menu = menu;
		this.caption = caption;
		this.value = value;
		this.isToggleItem = isToggleItem;
		this.isToggled = isToggled;
	}

	/**
	 * Returns the Menu that owns this MenuItem
	 * 
	 * @return Menu
	 */
	public AbstractList<O, ?> getMenu() {
		return this.menu;
	}

	/**
	 * Returns the MenuItem's caption
	 * 
	 * @return String
	 */
	public String getCaption() {
		return this.caption;
	}
	

	/**
	 * Returns the value associated with this MenuItem
	 * 
	 * @return Object
	 */
	public O getValue() {
		return this.value;
	}

	/**
	 * Sets if the MenuItem should be toggleable
	 * 
	 * @param isToggleItem
	 *            boolean
	 */
	public void setIsToggleItem(boolean isToggleItem) {
		this.isToggleItem = isToggleItem;
	}

	/**
	 * Returns true if the MenuItem is set to toggleable
	 * 
	 * @return boolean
	 */
	public boolean getIsToggleItem() {
		return this.isToggleItem;
	}

	/**
	 * Toggles/Untoggles the MenuItem
	 * 
	 * @param isToggled
	 *            boolean
	 */
	public void setIsToggled(boolean isToggled) {
		this.isToggled = isToggled;
		if (element != null && element instanceof CheckBox) {
			((CheckBox)element).setIsChecked(isToggled);
		}
	}

	/**
	 * Returns if the MenuItem is currently toggled
	 * 
	 * @return
	 */
	public boolean getIsToggled() {
		return this.isToggled;
	}

	/**
	 * For internal use. DO NOT CALL THIS!
	 * 
	 * @param toggle
	 *            CheckBox
	 */
	public void setElement(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}
}
