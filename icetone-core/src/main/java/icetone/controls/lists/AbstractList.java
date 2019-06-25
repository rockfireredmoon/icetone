/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.controls.lists;

import java.util.ArrayList;
import java.util.List;

import icetone.controls.buttons.CheckBox;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout;
import icetone.core.Orientation;
import icetone.core.PseudoStyles;
import icetone.core.ZPriority;
import icetone.core.layout.WrappingLayout;

/**
 * An abstract control for use by sub-classes that provide a list of some sort
 * (probably selected).
 *
 * @author Emerald Icemoon
 * @author t0neg0d
 */
public abstract class AbstractList<O, L extends AbstractListItem<?, ? extends AbstractList<O, ?>>> extends ScrollPanel {

	protected List<L> listItems = new ArrayList<L>();
	protected BaseElement callerElement;
	protected boolean hasToggleItems = false;

	/**
	 * Creates a new instance of this abstract list control
	 * 
	 * @param screen       The screen control the Element is to be added to
	 * @param isScrollable Boolean defining if the menu is a scrollable list
	 */
	public AbstractList(BaseScreen screen, boolean isScrollable) {
		super(screen);
		if (!isScrollable) {
			setHorizontalScrollBarMode(ScrollBarMode.Never);
			setVerticalScrollBarMode(ScrollBarMode.Never);
		}
	}

	@Override
	protected void configureScrolledElement() {

		scrollableArea.setLayoutManager(createListLayout());
		scrollableArea.setIgnoreMouseButtons(true);

		innerBounds.setIgnoreMouseButtons(true);

		setLockToParentBounds(true);

		this.priority = ZPriority.MENU;

		scrollableArea.onScrollEvent(evt -> {
			/*
			 * Scrolling can happen on selection events so it's pretty common. We don't want
			 * to lose focus on for example filter text fields that might rebuild combo
			 * boxes. So, only set focus back if the current focus is this menus vertical
			 * scrollbar.
			 * 
			 * This was added to give focus back to the combo box after it was scrolled by
			 * the USER
			 */
			if (screen.getKeyboardFocus() == getVerticalScrollBar())
				screen.setKeyboardFocus(this);
		});

	}

	protected Layout<?, ?> createListLayout() {
		WrappingLayout wl = new WrappingLayout();
		wl.setOrientation(Orientation.HORIZONTAL);
		wl.setEqualSizeCells(true);
		wl.setWidth(1);
		wl.setFill(true);
		return wl;
	}

	/**
	 * Adds an item to the list
	 * 
	 * @param value The value to associate with the item
	 */
	public AbstractList<O, L> addListItem(O value) {
		return addListItem(String.valueOf(value), value);
	}

	/**
	 * Adds an item to the list
	 * 
	 * @param caption The display caption of the item
	 * @param value   The value to associate with the item
	 */
	public AbstractList<O, L> addListItem(String caption, O value) {
		return addListItem(caption, value, false, false);
	}

	/**
	 * Adds an item to the list
	 * 
	 * @param caption      The display caption of the item
	 * @param value        The value to associate with the item
	 * @param isToggleItem Adds a toggleable CheckBox to the item
	 */
	public abstract AbstractList<O, L> addListItem(String caption, O value, boolean isToggleItem, boolean isToggled);

	public AbstractList<O, L> addListItem(L item) {
		this.listItems.add(item);
		validateSettings();
		if (item.getIsToggleItem()) {
			addCheckBox(-1, item);
		} else {
			addItem(-1, item);
		}
		return this;
	}

	/**
	 * Inserts a new MenuItem at the provided index
	 * 
	 * @param index   The index to insert into
	 * @param caption The display caption of the MenuItem
	 * @param value   The value to associate with the MenuItem
	 */
	public void insertListItem(int index, String caption, O value) {
		insertListItem(index, caption, value, false, false);
	}

	/**
	 * Inserts a new item at the provided index
	 * 
	 * @param index        The index to insert into
	 * @param caption      The display caption of the item
	 * @param value        The value to associate with the item
	 * @param isToggleItem Adds a toggleable CheckBox to the item
	 */
	public void insertListItem(int index, String caption, O value, boolean isToggleItem) {
		insertListItem(index, caption, value, isToggleItem, false);
	}

	/**
	 * Inserts a new item at the provided index
	 * 
	 * @param index        The index to insert into
	 * @param caption      The display caption of the item
	 * @param value        The value to associate with the item
	 * @param isToggleItem Adds a toggleable CheckBox to the item
	 * @param isToggled    Sets the default state of the added CheckBox
	 */
	public abstract void insertListItem(int index, String caption, O value, boolean isToggleItem, boolean isToggled);

	public void insertListItem(int index, L item) {
		this.listItems.add(index, item);
		validateSettings();
		if (item.getIsToggleItem()) {
			addCheckBox(index, item);
		} else {
			addItem(index, item);
		}
	}

	/**
	 * Remove the item at the provided index
	 * 
	 * @param index int
	 */
	public void removeListItem(int index) {
		if (index >= 0 && index < listItems.size()) {
			validateSettings();
			listItems.remove(index);
			removeScrollableContent(index);
		}
	}

	/**
	 * Remove the first item that contains the provided value
	 * 
	 * @param value Object
	 */
	public void removeListItemWithValue(Object value) {
		if (!listItems.isEmpty()) {
			int index = -1;
			int count = 0;
			for (L mi : listItems) {
				if (mi.getValue() == value) {
					index = count;
					break;
				}
				count++;
			}
			removeListItem(index);
		}
	}

	/**
	 * Remove the first item that contains the provided caption
	 * 
	 * @param value Object
	 */
	public void removeListItemWithCaption(String caption) {
		if (!listItems.isEmpty()) {
			int index = -1;
			int count = 0;
			for (L mi : listItems) {
				if (mi.getCaption().equals(caption)) {
					index = count;
					break;
				}
				count++;
			}
			removeListItem(index);
		}
	}

	/**
	 * Removes the first item in the list
	 */
	public void removeFirstListItem() {
		removeListItem(0);
	}

	/**
	 * Removes the last item in the list
	 */
	public void removeLastListItem() {
		if (!listItems.isEmpty()) {
			removeListItem(listItems.size() - 1);
		}
	}

	public void removeAllListItems() {
		if (!listItems.isEmpty()) {
			listItems.clear();
			validateSettings();
			removeAllScrollableContent();
		}
	}

	/**
	 * Validates flags for: contains toggle checkboxes, etc
	 */
	protected void validateSettings() {
		hasToggleItems = false;
		for (L mi : listItems) {
			if (mi.isToggleItem)
				hasToggleItems = true;
		}
	}

	/**
	 * Notifies the Menu that is has been called by an Element that is expecting
	 * notification of menu item clicks
	 * 
	 * @param el Element
	 */
	public final void setCallerElement(BaseElement el) {
		this.callerElement = el;
	}

	/**
	 * Returns the current Element waiting notification
	 * 
	 * @return
	 */
	public BaseElement getCallerElement() {
		return this.callerElement;
	}

	/**
	 * Returns a list of all items associated with this menu
	 * 
	 * @return List<ListItem>
	 */
	public List<L> getListItems() {
		return this.listItems;
	}

	/**
	 * Returns the item at the provided index
	 * 
	 * @param index int Index of the item
	 * @return item
	 */
	public L getListItem(int index) {
		return this.listItems.get(index);
	}

	protected void addItem(int index, L mi) {
		Label label = new Label(screen, mi.caption) {
			{
				styleClass = "item";
			}

			@Override
			public PseudoStyles getPseudoStyles() {
				return getItemPseudoStyles(mi, super.getPseudoStyles());
			}
		};
		mi.setElement(label);

		if (index == -1)
			addScrollableContent(label);
		else
			insertScrollableContent(label, index);
	}

	protected PseudoStyles getItemPseudoStyles(L mi, PseudoStyles pseudoStyles) {
		return pseudoStyles;
	}

	protected void addCheckBox(int index, L mi) {
		CheckBox checkbox = new CheckBox(screen) {
			{
				styleClass = "item";
			}

			@Override
			public PseudoStyles getPseudoStyles() {
				return getItemPseudoStyles(mi, super.getPseudoStyles());
			}
		};
		mi.setElement(checkbox);
		checkbox.setIgnoreMouse(true);
		checkbox.addClippingLayer(this, null);

		if (index == -1)
			addScrollableContent(checkbox);
		else
			insertScrollableContent(checkbox, index);

		if (mi.getIsToggled())
			checkbox.setChecked(mi.getIsToggled());

//		if (!isVisible())
//			checkbox.hide();
	}

}
