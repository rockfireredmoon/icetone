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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Texture2D;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.menuing.Menu;
import icetone.controls.menuing.MenuItem;
import icetone.controls.text.TextField;
import icetone.controls.text.TextFieldLayout;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 * A 'Combo Box' control that before any user interaction appears as a text
 * field (optionally editable) with a button that exposes a menu item items.
 * 
 * @author t0neg0d
 * @author Emerald Icemoon
 * @param <I>
 */
public class ComboBox<I> extends TextField {

	protected ButtonAdapter btnArrowDown;
	protected float visibleRowCount = 10;
	protected boolean selectEnabled = true;

	private Menu<I> menu = null;
	private float btnHeight;
	private int selectedIndex = -1;
	private I selectedValue;
	private String selectedCaption;

	private int hlIndex;
	private I hlValue;
	private String hlCaption;

	private int ssIndex;

	@SafeVarargs
	public ComboBox(I... values) {
		this(Screen.get(), values);
	}

	@SafeVarargs
	public ComboBox(ElementManager screen, I... values) {
		this(screen);
		for (I i : values)
			addListItem(String.valueOf(i), i, false, false);
		pack(false);
	}

	public ComboBox(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
	}

	public ComboBox(ElementManager screen, String UID) {
		this(screen, UID, Vector2f.ZERO);
	}

	public ComboBox(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ComboBox(ElementManager screen) {
		this(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, screen.getStyle("ComboBox").getVector4f("resizeBorders"),
				screen.getStyle("ComboBox").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public ComboBox(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle("ComboBox").getVector4f("resizeBorders"),
				screen.getStyle("ComboBox").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public ComboBox(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("ComboBox").getVector4f("resizeBorders"),
				screen.getStyle("ComboBox").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the ComboBox
	 */
	public ComboBox(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public ComboBox(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("ComboBox").getVector4f("resizeBorders"),
				screen.getStyle("ComboBox").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public ComboBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("ComboBox").getVector4f("resizeBorders"),
				screen.getStyle("ComboBox").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the ComboBox
	 */
	public ComboBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		layoutManager = new SelectLayout();
		setMinDimensions(Vector2f.ZERO);

		btnHeight = getHeight();

		setWidth(getWidth() - btnHeight);

		// layoutHints.setElementPadX(btnHeight);

		btnArrowDown = new ButtonAdapter(screen, UID + ":ArrowDown", Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("ComboBox").getVector4f("buttonResizeBorders"),
				screen.getStyle("ComboBox").getString("buttonImg")) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (validateListSize()) {
					if (!menu.getIsVisible()) {
						menu.setMinDimensions(getElementParent().getContainerDimensions());
						menu.sizeToContent();
						float elY = LUtil.getAbsoluteY(getElementParent());
						if (elY + menu.getHeight() + getElementParent().getHeight() > screen.getHeight()) {
							menu.showMenu((Menu<I>) null, getElementParent().getAbsoluteX(), screen.getHeight() - elY);
						} else {
							if (Element.NEW_YFLIPPING)
								menu.showMenu((Menu<I>) null, getElementParent().getAbsoluteX(),
										elY + getElementParent().getHeight());
							else
								menu.showMenu((Menu<I>) null, getElementParent().getAbsoluteX(),
										screen.getHeight() - elY - menu.getHeight() - getElementParent().getHeight());
						}
						handleHightlight(getSelectIndex());
						screen.setTabFocusElement(menu);
					} else {
						menu.hide();
						screen.setTabFocusElement(ComboBox.this);
					}
				} else
					screen.setTabFocusElement(ComboBox.this);
			}
		};
		btnArrowDown.setButtonHoverInfo(screen.getStyle("ComboBox").getString("buttonHoverImg"), null);
		btnArrowDown.setButtonPressedInfo(screen.getStyle("ComboBox").getString("buttonPressedImg"), null);
		btnArrowDown.setButtonIcon(18, 18, screen.getStyle("Common").getString("arrowDown"));
		this.addChild(btnArrowDown);

		setFontSize(screen.getStyle("ComboBox").getFloat("fontSize"));
		setFontColor(screen.getStyle("ComboBox").getColorRGBA("fontColor"));
		setTextAlign(Align.valueOf(screen.getStyle("ComboBox").getString("textAlign")));
		setTextVAlign(VAlign.valueOf(screen.getStyle("ComboBox").getString("textVAlign")));
		setTextWrap(LineWrapMode.valueOf(screen.getStyle("ComboBox").getString("textWrap")));

		setTextPadding(screen.getStyle("ComboBox").getVector4f("textPadding"));
		setEditable(false);
		// addClippingLayer(this);
	}

	/**
	 * Adds a new list item to the drop-down list associated with this control
	 * 
	 * @param caption
	 *            The String to display as the list item
	 * @param value
	 *            A String value to associate with this list item
	 */
	public void addListItem(String caption, I value) {
		addListItem(caption, value, true);
	}

	public void addListItem(String caption, I value, boolean callback) {
		addListItem(caption, value, callback, true);
	}

	public void addListItem(String caption, I value, boolean callback, boolean pack) {
		checkMenu();
		menu.setFontSize(fontSize);
		menu.getScrollableArea().setFontSize(fontSize);
		menu.addMenuItem(caption, value, null, false, false, pack);

		if (pack)
			pack(callback);
	}

	/**
	 * Inserts a new List Item at the specified index
	 * 
	 * @param index
	 *            - List index to insert new List Item
	 * @param caption
	 *            - Caption for new List Item
	 * @param value
	 *            - Object to store as value
	 */
	public void insertListItem(int index, String caption, I value) {
		if (menu != null) {
			menu.insertMenuItem(index, caption, value, null);
			pack(true);
			refreshSelectedIndex();
		}
	}

	/**
	 * Removes the List Item at the specified index
	 * 
	 * @param index
	 */
	public void removeListItem(int index) {
		if (menu != null) {
			menu.removeMenuItem(index);
			pack(true);
			refreshSelectedIndex();
		}
	}

	/**
	 * Removes the first instance of a list item with the specified caption
	 * 
	 * @param caption
	 */
	public void removeListItem(String caption) {
		if (menu != null) {
			menu.removeMenuItem(caption);
		}
	}

	/**
	 * Removes the first instance of a list item with the specified value
	 * 
	 * @param value
	 */
	public void removeListItem(Object value) {
		if (menu != null) {
			menu.removeMenuItem(value);
		}
	}

	/**
	 * Removes all list items
	 */
	public void removeAllListItems() {
		if (menu != null) {
			menu.removeAllMenuItems();
		}
		selectedIndex = -1;
	}

	public Vector2f getPreferredDimensions() {
		return prefDimensions == null ? (getOrgDimensions().equals(LUtil.LAYOUT_SIZE) ? null : getOrgDimensions()) : prefDimensions;
	}

	public void setCaretPositionToStart() {
		caretIndex = 0;
		head = 0;
		tail = 0;
		setCaretPosition(getAbsoluteX());
	}

	/**
	 * Method needs to be called once last list item has been added. This
	 * eventually
	 * will be updated to automatically be called when a new item is added to,
	 * instert into
	 * the list or an item is removed from the list.
	 */
	public void pack(boolean callbacks) {
		if (menu != null && menu.getMenuItems().size() > 0) {
			if (selectedIndex == -1) {
				if (callbacks)
					setSelectedIndexWithCallback(0);
				else
					setSelectedIndex(0);
			} else {
				if (menu != null && selectedIndex > menu.getMenuItems().size() - 1) {
					menu.scrollToItem(selectedIndex);
					refreshSelectedIndex();
				}
			}
		}
		if (menu != null) {
			menu.pack();
		}
		dirtyLayout(false);
		layoutChildren();
	}

	/**
	 * Returns false if list is empty, true if list contains List Items
	 * 
	 * @return boolean
	 */
	public boolean validateListSize() {
		if (menu == null)
			return false;
		else if (menu.getMenuItems().isEmpty())
			return false;
		else
			return true;
	}

	public void setSelectedByCaption(String caption, boolean useCallback) {
		MenuItem<I> mItem = null;
		checkMenu();
		for (MenuItem<I> mi : menu.getMenuItems()) {
			if (mi.getCaption().equals(caption)) {
				mItem = mi;
				break;
			}
		}

		if (mItem != null) {
			if (useCallback)
				setSelectedIndexWithCallback(menu.getMenuItems().indexOf(mItem));
			else
				setSelectedIndex(menu.getMenuItems().indexOf(mItem));
		}
	}

	public void setSelectedByValue(I value, boolean useCallback) {
		MenuItem<I> mItem = null;
		checkMenu();
		for (MenuItem<I> mi : menu.getMenuItems()) {
			if (Objects.equals(mi.getValue(), value)) {
				mItem = mi;
				break;
			}
		}
		if (mItem != null) {
			if (useCallback)
				setSelectedIndexWithCallback(menu.getMenuItems().indexOf(mItem));
			else
				setSelectedIndex(menu.getMenuItems().indexOf(mItem));
		}
	}

	/**
	 * Selects the List Item at the specified index and call the onChange event
	 * 
	 * @param selectedIndex
	 */
	public void setSelectedIndexWithCallback(int selectedIndex) {
		if (validateListSize()) {
			if (selectedIndex < 0)
				selectedIndex = 0;
			else if (selectedIndex > menu.getMenuItems().size() - 1)
				selectedIndex = menu.getMenuItems().size() - 1;

			MenuItem<I> mi = menu.getMenuItem(selectedIndex);
			String caption = mi.getCaption();
			I value = mi.getValue();
			setSelectedWithCallback(selectedIndex, caption, value);
		}
	}

	/**
	 * Selects the List Item at the specified index
	 * 
	 * @param selectedIndex
	 */
	public void setSelectedIndex(int selectedIndex) {
		if (validateListSize()) {
			if (selectedIndex < 0)
				selectedIndex = 0;
			else if (selectedIndex > menu.getMenuItems().size() - 1)
				selectedIndex = menu.getMenuItems().size() - 1;

			MenuItem<I> mi = menu.getMenuItem(selectedIndex);
			String caption = mi.getCaption();
			I value = mi.getValue();
			setSelected(selectedIndex, caption, value);
		}
	}

	/**
	 * Hides the ComboBox drop-down list
	 */
	public void hideDropDownList() {
		if (menu != null)
			this.menu.hideMenu();
	}

	@Override
	public void controlKeyPressHook(KeyInputEvent evt, String text) {
		checkMenu();
		if (validateListSize()) {
			if (evt.getKeyCode() != KeyInput.KEY_UP && evt.getKeyCode() != KeyInput.KEY_DOWN
					&& evt.getKeyCode() != KeyInput.KEY_RETURN) {
				int miIndexOf = 0;
				int strIndex = -1;
				for (MenuItem<I> mi : menu.getMenuItems()) {
					strIndex = mi.getCaption().toLowerCase().indexOf(text.toLowerCase());
					if (strIndex == 0) {
						ssIndex = miIndexOf;
						hlIndex = ssIndex;
						hlCaption = menu.getMenuItem(miIndexOf).getCaption();
						hlValue = menu.getMenuItem(miIndexOf).getValue();
						menu.scrollToItem(miIndexOf);
						break;
					}
					miIndexOf++;
				}
				if (miIndexOf > -1 && miIndexOf < menu.getMenuItems().size() - 1)
					handleHightlight(miIndexOf);
				if (screen.getElementById(menu.getUID()) == null)
					screen.addElement(menu);
				if (!menu.getIsVisible() && evt.getKeyCode() != KeyInput.KEY_LSHIFT && evt.getKeyCode() != KeyInput.KEY_RSHIFT)
					menu.showMenu(null, getAbsoluteX(), getAbsoluteY() - menu.getHeight());
			} else {
				if (evt.getKeyCode() == KeyInput.KEY_UP) {
					if (hlIndex > 0) {
						hlIndex--;
						hlCaption = menu.getMenuItem(hlIndex).getCaption();
						hlValue = menu.getMenuItem(hlIndex).getValue();
						menu.scrollToItem(hlIndex);
						handleHightlight(hlIndex);
						setSelectedWithCallback(hlIndex, hlCaption, hlValue);
					}
				} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
					if (hlIndex < menu.getMenuItems().size() - 1) {
						hlIndex++;
						hlCaption = menu.getMenuItem(hlIndex).getCaption();
						hlValue = menu.getMenuItem(hlIndex).getValue();
						menu.scrollToItem(hlIndex);
						handleHightlight(hlIndex);
						setSelectedWithCallback(hlIndex, hlCaption, hlValue);
					}
				}
				if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
					updateSelected();
				}
			}
		}
	}

	/**
	 * Returns the current selected index
	 * 
	 * @return selectedIndex
	 */
	public int getSelectIndex() {
		return this.selectedIndex;
	}

	/**
	 * Returns the value of the selected item or null if nothing is selected
	 * 
	 * @return MenuITem
	 */
	public I getSelectedValue() {
		checkMenu();
		return selectedIndex == -1 ? null : this.menu.getMenuItem(selectedIndex).getValue();
	}

	/**
	 * Returns the object representing the current selected List Item
	 * 
	 * @return MenuITem
	 */
	public MenuItem<I> getSelectedListItem() {
		checkMenu();
		return selectedIndex == -1 ? null : this.menu.getMenuItem(selectedIndex);
	}

	/**
	 * Returns the object representing the list item at the specified index
	 * 
	 * @param index
	 * @return MenuItem
	 */
	public MenuItem<I> getListItemByIndex(int index) {
		checkMenu();
		return this.menu.getMenuItem(index);
	}

	/**
	 * Returns a List of all ListItems
	 * 
	 * @return List<MenuItem>
	 */
	public List<MenuItem<I>> getListItems() {
		checkMenu();
		return menu.getMenuItems();
	}

	/**
	 * Returns a pointer to the dropdown list (Menu)
	 * 
	 * @return DDList
	 */
	public Menu<I> getMenu() {
		checkMenu();
		return this.menu;
	}

	/**
	 * Sorts the associated drop-down list alphanumerically
	 */
	public void sortList() {
		List<MenuItem<I>> orgList = menu.getMenuItems();
		List<MenuItem<I>> currentList = new ArrayList<>();
		List<MenuItem<I>> finalList = new ArrayList<>();
		List<String> map = new ArrayList<>();
		for (MenuItem<I> item : orgList) {
			currentList.add(item);
			map.add(item.getCaption());
		}
		Collections.sort(map);
		for (String caption : map) {
			int index;
			for (MenuItem<I> mi : currentList) {
				if (mi.getCaption().equals(caption)) {
					index = currentList.indexOf(mi);
					finalList.add(mi);
					menu.removeMenuItem(index);
					currentList.remove(mi);
					break;
				}
			}
		}
		for (MenuItem<I> mi : finalList) {
			addListItem(mi.getCaption(), mi.getValue());
		}
	}

	/**
	 * Sorts drop-down list by true numeric values. This should only be used
	 * with lists that start with numeric values
	 */
	public void sortListNumeric() {
		checkMenu();
		List<MenuItem<I>> orgList = menu.getMenuItems();
		List<MenuItem<I>> currentList = new ArrayList<>();
		List<MenuItem<I>> finalList = new ArrayList<>();
		List<Integer> map = new ArrayList<>();
		for (MenuItem<I> item : orgList) {
			currentList.add(item);

			boolean NaN = true;
			String tempCaption = item.getCaption();
			while (NaN && tempCaption.length() != 0) {
				try {
					Integer.parseInt(tempCaption);
					NaN = false;
				} catch (Exception ex) {
					tempCaption = tempCaption.substring(0, tempCaption.length() - 2);
				}
			}
			map.add(Integer.parseInt(tempCaption));
		}
		Collections.sort(map);
		for (Integer caption : map) {
			int index;
			for (MenuItem<I> mi : currentList) {
				boolean NaN = true;
				String tempCaption = mi.getCaption();
				while (NaN && tempCaption.length() != 0) {
					try {
						Integer.parseInt(tempCaption);
						NaN = false;
					} catch (Exception ex) {
						tempCaption = tempCaption.substring(0, tempCaption.length() - 2);
					}
				}
				if (Integer.parseInt(tempCaption) == caption) {
					index = currentList.indexOf(mi);
					finalList.add(mi);
					menu.removeMenuItem(index);
					currentList.remove(mi);
					break;
				}
			}
		}
		for (MenuItem<I> mi : finalList) {
			addListItem(mi.getCaption(), mi.getValue());
		}
	}

	@Override
	public void controlTextFieldResetTabFocusHook() {
		// DDList.hideMenu();
	}

	@Override
	public void controlCleanupHook() {
		if (menu != null)
			screen.removeElement(menu);
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		super.setIsEnabled(isEnabled);
		selectEnabled = isEnabled;
		this.btnArrowDown.setIsEnabled(isEnabled);
	}

	@Override
	public void onKeyPress(KeyInputEvent evt) {
		if (selectEnabled) {
			super.onKeyPress(evt);
		}
	}

	protected void setSelectedWithCallback(int index, String caption, I value) {
		this.hlIndex = index;
		this.selectedIndex = index;
		this.selectedCaption = caption;
		this.selectedValue = value;
		setText(selectedCaption);
		menu.scrollToItem(index);
		onChange(selectedIndex, selectedValue);
	}

	protected void setSelected(int index, String caption, I value) {
		this.hlIndex = index;
		this.selectedIndex = index;
		this.selectedCaption = caption;
		this.selectedValue = value;
		setText(selectedCaption);
		menu.scrollToItem(index);
	}

	/**
	 * Abstract event method called when a list item is selected/navigated to.
	 * 
	 * @param selectedIndex
	 * @param value
	 */
	protected void onChange(int selectedIndex, I value) {
	}

	protected void checkMenu() {
		if (menu == null) {
			menu = new Menu<I>(screen, true) {
				@SuppressWarnings("unchecked")
				@Override
				public void onMenuItemClicked(int index, I value, boolean isToggled) {
					((ComboBox<I>) getCallerElement()).setSelectedWithCallback(index, menu.getMenuItem(index).getCaption(), value);
					screen.setTabFocusElement(((ComboBox<I>) getCallerElement()));
					hide();
				}
			};
			menu.setCallerElement(this);
			screen.addElement(menu, null, true);
			// menu.setPreferredSize(new Vector2f(getWidth() + btnHeight,
			// menu.getMenuItemHeight() * 5));
		}
	}

	private void refreshSelectedIndex() {
		if (menu != null) {
			if (selectedIndex > menu.getMenuItems().size() - 1)
				this.setSelectedIndexWithCallback(menu.getMenuItems().size() - 1);
			// if (!DDList.getMenuItems().isEmpty())
			// this.setSelectedIndex(selectedIndex);
			// else
			if (menu.getMenuItems().isEmpty())
				setText("");
		} else {
			setText("");
		}
	}

	private void updateSelected() {
		setSelectedWithCallback(hlIndex, hlCaption, hlValue);
		checkMenu();
		if (menu.getIsVisible())
			menu.hide();
	}

	private void handleHightlight(int index) {
		checkMenu();
		if (menu.getIsVisible())
			menu.setSelectedIndex(index);
	}

	class SelectLayout extends TextFieldLayout {

		public void layout(Element childElement) {
			// updateTextElement();
			// centerTextVertically();
			// setCaretPosition(getAbsoluteX() + caretX);

			setActualDimensions(getContainerDimensions().x - getContainerDimensions().y, getContainerDimensions().y);
			btnArrowDown.setPosition(getContainerDimensions().x - getContainerDimensions().y, 0);
			btnArrowDown.setDimensions(getContainerDimensions().y, getContainerDimensions().y);

			float btnSize = btnArrowDown.getHeight() / 2;
			btnArrowDown.setButtonIcon(btnSize, btnSize,
					((Texture2D) btnArrowDown.getButtonIcon().getElementMaterial().getParam("ColorMap").getValue()).getName());

			super.layout(childElement);

			// // TODO seems a bit crap
			// setText(getText());
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f ps = super.preferredSize(parent);

			if (menu != null) {
				for (MenuItem<?> mi : menu.getMenuItems()) {
					if (mi.getElement() != null) {
						ps.x = Math.max(ps.x, LUtil.getPreferredWidth(mi.getElement()));
					}
				}
			}

			return ps.addLocal(ps.y, 0);
		}
	}
}
