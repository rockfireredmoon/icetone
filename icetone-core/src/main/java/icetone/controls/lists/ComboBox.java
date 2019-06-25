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

import com.jme3.input.KeyInput;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.menuing.AutoHide;
import icetone.controls.menuing.Menu;
import icetone.controls.menuing.MenuItem;
import icetone.controls.text.TextField;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.ToolKit;
import icetone.core.event.ChangeSupport;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.event.keyboard.KeyboardUIEvent;
import icetone.core.event.keyboard.UIKeyboardListener;
import icetone.core.utils.ClassUtil;
import icetone.core.utils.MathUtil;

/**
 * A 'Combo Box' control that before any user interaction appears as a text
 * field (optionally editable) with a button that exposes a menu item items.
 * 
 * @author t0neg0d
 * @author Emerald Icemoon
 * @param <I>
 */
public class ComboBox<I> extends Element {

	class SelectLayout extends AbstractGenericLayout<ComboBox<I>, Void> {

		@Override
		protected Vector2f calcPreferredSize(ComboBox<I> parent) {
			Vector2f ps = super.calcPreferredSize(parent);

			if (menu != null) {
				for (MenuItem<?> mi : menu.getMenuItems()) {
					ps.x = Math.max(ps.x, mi.calcPreferredSize().x);
				}
			}

			Vector2f tps = textField.calcPreferredSize();
			ps = MathUtil.largest(ps, tps);

			Vector2f bps = btnArrowDown.calcPreferredSize();
			ps.addLocal(bps.x + parent.getIndent(), 0);

			return ps.addLocal(ps.y, 0);
		}

		@Override
		protected void onLayout(ComboBox<I> childElement) {
			Vector2f dim = childElement.getDimensions();
			Vector4f pad = childElement.getAllPadding();
			textField.setBounds(pad.x, pad.z, dim.x - dim.y - pad.x, dim.y - pad.z - pad.w);
			btnArrowDown.setBounds(dim.x - dim.y - pad.y + childElement.getIndent(), pad.z, dim.y - pad.z - pad.w,
					dim.y - pad.z - pad.w);
			super.onLayout(childElement);
		}
	}

	private final class ComboMenu extends Menu<I> implements AutoHide {
		private ComboMenu(BaseScreen screen) {
			super(screen);
			setSelectOnHighlight(selectOnHighlight);
			setKeyboardFocusable(true);
			setKeyboardFocusRoot(true);
		}

		@Override
		public Vector2f calcPreferredSize() {
			Vector2f min = super.calcPreferredSize();
			min.x = Math.max(min.x, ComboBox.this.getWidth() - btnArrowDown.getWidth() - ComboBox.this.getIndent());
			return min;
		}

		@Override
		public void destroy() {
			if (isShowing())
				hide();
			if (getParentContainer() != null && getParentContainer().getElements().contains(this))
				getParentContainer().removeElement(this);
		}
	}

	protected Button btnArrowDown;
	protected ChangeSupport<ComboBox<I>, I> changeSupport;

	protected boolean selectEnabled = true;
	protected float visibleRowCount = 10;

	private Menu<I> menu = null;
	private TextField textField;
	private int selectedIndex = -1;
	private UIKeyboardListener keyListener;
	private UIKeyboardListener highlightListener;
	private BaseElement wasFocus;
	private boolean preventClose;
	private boolean selectOnHighlight;

	/**
	 * Creates a new instance of the ComboBox control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public ComboBox(BaseScreen screen) {
		super(screen);
	}

	@SafeVarargs
	public ComboBox(BaseScreen screen, I... values) {
		this(screen);
		checkMenu();
		menu.invalidate();
		for (int i = 0; i < values.length; i++) {
			if (i == 0 && !(values[i] instanceof String))
				setEditable(false);
			addComboItem(String.valueOf(values[i]), values[i]);
		}
		menu.validate();
		onElementEvent(evt -> {
			if (menu != null)
				screen.removeElement(menu);
		}, Type.CLEANUP);
	}

	@SafeVarargs
	public ComboBox(I... values) {
		this(BaseScreen.get(), values);
	}

	public ComboBox<I> addChangeListener(UIChangeListener<ComboBox<I>, I> listener) {
		if (changeSupport != null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	/**
	 * Adds a new list item to the drop-down list associated with this control
	 * 
	 * @param caption The String to display as the list item
	 * @param value   A String value to associate with this list item
	 */
	public ComboBox<I> addComboItem(String caption, I value) {
		checkMenu();
		menu.addMenuItem(caption, value);
		refreshSelectedIndex();
		return this;
	}

	/**
	 * Adds a new menu item to the drop-down list associated with this control
	 * 
	 * @param item item
	 */
	public ComboBox<I> addComboItem(MenuItem<I> item) {
		checkMenu();
		menu.addMenuItem(item);
		refreshSelectedIndex();
		return this;
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
	 * Returns a List of all values
	 * 
	 * @return List<I>
	 */
	public List<I> getValues() {
		List<MenuItem<I>> items = getListItems();
		List<I> l = new ArrayList<>(items.size());
		for (MenuItem<I> m : items)
			l.add(m.getValue());
		return l;
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
	 * Returns the object representing the current selected List Item
	 * 
	 * @return MenuITem
	 */
	public MenuItem<I> getSelectedListItem() {
		checkMenu();
		return getSelectIndex() == -1 ? null : this.menu.getMenuItem(getSelectIndex());
	}

	/**
	 * Returns the value of the selected item or null if nothing is selected
	 * 
	 * @return MenuITem
	 */
	public I getSelectedValue() {
		checkMenu();
		return getSelectIndex() == -1 ? null : this.menu.getMenuItem(getSelectIndex()).getValue();
	}

	/**
	 * Returns the current selected index
	 * 
	 * @return selectedIndex
	 */
	public int getSelectIndex() {
		return selectedIndex;
	}

	public TextField getTextField() {
		return textField;
	}

	/**
	 * Hides the ComboBox drop-down list
	 */
	public void hideDropDownList() {
		if (menu != null && menu.isShowing())
			this.menu.hide();
	}

	/**
	 * Inserts a new List Item at the specified index
	 * 
	 * @param index   - List index to insert new List Item
	 * @param caption - Caption for new List Item
	 * @param value   - Object to store as value
	 */
	public ComboBox<I> insertListItem(int index, String caption, I value) {
		checkMenu();
		menu.insertMenuItem(caption, value, index);
		refreshSelectedIndex();
		return this;
	}

	public boolean isEditable() {
		return textField.isEditable();
	}

	public ComboBox<I> onChange(UIChangeListener<ComboBox<I>, I> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	/**
	 * Removes all list items
	 */
	public void removeAllListItems() {
		if (menu != null) {
			if (selectedIndex != -1) {
				setSelectedIndex(-1);
			}
			menu.removeAllMenuItems();
		}
	}

	public ComboBox<I> removeChangeListener(UIChangeListener<ComboBox<I>, I> listener) {
		if (changeSupport != null)
			changeSupport.removeListener(listener);
		return this;
	}

	/**
	 * Removes the List Item at the specified index
	 * 
	 * @param index
	 */
	public void removeListItem(int index) {
		if (menu != null) {
			menu.removeMenuItem(index);
		}
	}

	/**
	 * Removes the first instance of a list item with the specified value
	 * 
	 * @param value
	 */
	public void removeListItem(I value) {
		if (menu != null) {
			menu.removeMenuItemWithValue(value);
		}
	}

	/**
	 * Removes the first instance of a list item with the specified caption
	 * 
	 * @param caption
	 */
	public void removeListItem(String caption) {
		if (menu != null) {
			menu.removeMenuItemWithText(caption);
		}
	}

	public ComboBox<I> setEditable(boolean editable) {
		if (editable != textField.isEditable()) {
			textField.setEditable(editable);
			textField.setSelectable(editable);
			textField.setHoverable(editable);
			textField.setUseParentPseudoStyles(!editable);
			setHoverable(!editable);
			setFocusRootOnly(!editable);
			setupKeyListeners();
			dirtyLayout(true, LayoutType.all);
			layoutChildren();
		}
		return this;
	}

	@Override
	public BaseElement setEnabled(boolean isEnabled) {
		super.setEnabled(isEnabled);
		selectEnabled = isEnabled;
		this.btnArrowDown.setEnabled(isEnabled);
		return this;
	}

	@Override
	public BaseElement setText(String text) {
		textField.setText(text);
		return textField;
	}

	@Override
	public String getText() {
		return textField.getText();
	}

	public ComboBox<I> setSelectedByCaption(String caption) {
		MenuItem<I> mItem = null;
		checkMenu();
		for (MenuItem<I> mi : menu.getMenuItems()) {
			if (mi.getText().equals(caption)) {
				mItem = mi;
				break;
			}
		}

		if (mItem != null) {
			setSelectedIndex(menu.getMenuItems().indexOf(mItem));
		}
		return this;
	}

	public ComboBox<I> setSelectedByValue(I value) {
		MenuItem<I> mItem = null;
		checkMenu();
		for (MenuItem<I> mi : menu.getMenuItems()) {
			if (Objects.equals(mi.getValue(), value)) {
				mItem = mi;
				break;
			}
		}
		if (mItem != null) {
			setSelectedIndex(menu.getMenuItems().indexOf(mItem));
		}
		return this;
	}

	/**
	 * Selects the List Item at the specified index
	 * 
	 * @param selectedIndex
	 */
	public ComboBox<I> setSelectedIndex(int selectedIndex) {
		if (validateListSize()) {
			if (selectedIndex < -1)
				selectedIndex = -1;
			else if (selectedIndex > menu.getMenuItems().size() - 1)
				selectedIndex = menu.getMenuItems().size() - 1;
			setSelectedWithCallback(selectedIndex);
		}
		return this;
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
			map.add(item.getText());
		}
		Collections.sort(map);
		for (String caption : map) {
			int index;
			for (MenuItem<I> mi : currentList) {
				if (mi.getText().equals(caption)) {
					index = currentList.indexOf(mi);
					finalList.add(mi);
					menu.removeMenuItem(index);
					currentList.remove(mi);
					break;
				}
			}
		}
		for (MenuItem<I> mi : finalList) {
			addComboItem(mi.getText(), mi.getValue());
		}
	}

	public boolean isSelectOnHighlight() {
		return selectOnHighlight;
	}

	public void setSelectOnHighlight(boolean selectOnHighlight) {
		this.selectOnHighlight = selectOnHighlight;
		if (menu != null)
			menu.setSelectOnHighlight(selectOnHighlight);
	}

	/**
	 * Sorts drop-down list by true numeric values. This should only be used with
	 * lists that start with numeric values
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
			String tempCaption = item.getText();
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
				String tempCaption = mi.getText();
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
			addComboItem(mi.getText(), mi.getValue());
		}
	}

	public ComboBox<I> unbindChanged(UIChangeListener<ComboBox<I>, I> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add((isEditable() ? "Editable" : "ReadOnly") + ClassUtil.getMainClassName(getClass()));
		return l;
	}

	protected void checkMenu() {
		if (menu == null) {
			menu = new ComboMenu(screen);
			menu.onElementEvent(evt -> {
				if (wasFocus != null) {
					wasFocus.focus();
					wasFocus = null;
				}
			}, Type.HIDDEN);
			menu.onChanged(evt -> {
				if (!evt.getSource().isAdjusting() && !evt.isTemporary()) {
					MenuItem<I> newValue = evt.getNewValue();
					if (newValue == null)
						selectionChanged(-1, evt.isTemporary());
					else
						selectionChanged(menu.getMenuItems().indexOf(newValue), evt.isTemporary());
				}
			});
			menu.onKeyboardFocusLost(evt -> {
				if (menu.isVisible() && (evt.getOther() == null || !evt.getOther().isDescendantOf(menu)))
					menu.hide();
			});
			menu.setStyleClass("combo-menu");
		}
	}

	@Override
	protected void configureStyledElement() {

		super.configureStyledElement();

		textField = new TextField() {
			@Override
			protected void onKeyPress(KeyboardUIEvent evt) {
				if (selectEnabled) {
					super.onKeyPress(evt);
				}
			}
		};
		setHoverable(false);
		textField.setHoverable(true);
		setHoverable(false);
		setFocusRootOnly(false);

		layoutManager = new SelectLayout();

		btnArrowDown = new Button(screen) {
			{
				setStyleClass("menu-button");
			}
		};
		btnArrowDown.onMouseReleased(evt -> {
			ToolKit.get().execute(() -> showTrigger());
		});

		addElement(textField);
		addElement(btnArrowDown);

		highlightListener = new UIKeyboardListener() {

			@Override
			public void onKey(KeyboardUIEvent evt) {
				if (evt.isPressed()) {
					if (!isEditable() && evt.getKeyCode() == KeyInput.KEY_SPACE) {
						wasFocus = textField;
						showMenu();
						menu.focus();
					} else if (isEditable() && evt.getKeyChar() > 31) {
						int miIndexOf = 0;
						int strIndex = -1;
						wasFocus = textField;
						showMenu();
						for (MenuItem<I> mi : menu.getMenuItems()) {
							String str = text != null && isEditable() ? text.toLowerCase()
									: Character.toString(evt.getKeyChar()).toLowerCase();
							strIndex = mi.getText().toLowerCase().indexOf(str);
							if (strIndex == 0) {
								setSelectedIndex(miIndexOf);
								break;
							}
							miIndexOf++;
						}
						if (miIndexOf > -1 && miIndexOf < menu.getMenuItems().size() - 1)
							handleHighlight(miIndexOf);
					}

				}
			}
		};

		textField.onKeyboardFocusGained(evt -> {
			dirtyLayout(false, LayoutType.styling);
			layoutChildren();
		});
		textField.onKeyboardFocusLost(evt -> {
			if (preventClose) {
				preventClose = false;
				return;
			}
			hideDropDownList();
			dirtyLayout(false, LayoutType.styling);
			layoutChildren();
		});

		keyListener = new UIKeyboardListener() {

			@Override
			public void onKey(KeyboardUIEvent evt) {
				checkMenu();
				if (validateListSize()) {
					if (evt.getKeyCode() == KeyInput.KEY_UP) {
						if (evt.isPressed() && menu.getSelectedIndex() > 0) {
							setSelectedWithCallback(menu.getSelectedIndex() - 1);
						}
						evt.setConsumed();
					} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
						if (evt.isPressed() && menu.getSelectedIndex() < menu.getMenuItems().size() - 1) {
							setSelectedWithCallback(menu.getSelectedIndex() + 1);
						}
						evt.setConsumed();
					} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
						if (evt.isPressed()) {
							updateSelected();
							focus();
						}
						evt.setConsumed();
					} else if (evt.getKeyCode() == KeyInput.KEY_ESCAPE) {
						if (evt.isPressed() && menu.isShowing()) {
							menu.hide();
							focus();
						}
						evt.setConsumed();
					}
				}
			}
		};

		setupKeyListeners();

		onMouseReleased((evt) -> {
			if (!isEditable()) {
				showTrigger();
			}
		});

	}

	@Override
	protected void onPsuedoStateChange() {
		/*
		 * TODO only do this if any children are using parent pseudo styles. Button does
		 * the same thing
		 */
		dirtyLayout(true, LayoutType.styling);
	}

	protected void setupKeyListeners() {
		textField.removeKeyboardListener(highlightListener);
		textField.removeNavigationKeyListener(keyListener);
		removeNavigationKeyListener(keyListener);
		setKeyboardFocusable(false);
		textField.setKeyboardFocusable(true);
		textField.addNavigationKeyListener(keyListener);
		textField.addKeyboardListener(highlightListener);
	}

	protected void selectionChanged(int selectedIndex, boolean temporary) {
		if (this.selectedIndex != selectedIndex) {
			MenuItem<I> wasItem = this.selectedIndex == -1 ? null : menu.getMenuItem(this.selectedIndex);
			this.selectedIndex = selectedIndex;
			menu.setSelectedIndex(selectedIndex);
			MenuItem<I> selectedListItem = getSelectedListItem();
			setText(selectedListItem == null ? null : selectedListItem.getText());
			if (selectedListItem == null)
				menu.getScrollPanel().scrollToTop();
			else
				menu.getScrollPanel().scrollYTo(selectedListItem);
			if (changeSupport != null && !temporary)
				changeSupport.fireEvent(new UIChangeEvent<ComboBox<I>, I>(this,
						wasItem == null ? null : wasItem.getValue(), getSelectedValue()).setTemporary(temporary));
		}
	}

	protected void setSelected(int index) {
		menu.setSelectedIndex(index);
		setText(menu.getSelectedItem().getText());
	}

	protected void setSelectedWithCallback(int index) {
		if (index != this.selectedIndex) {
			selectionChanged(index, false);
		}
	}

	protected void showMenu() {
		menu.sizeToContent();
		menu.showMenu(this);
	}

	protected void showTrigger() {
		if (validateListSize()) {
			if (!menu.isShowing()) {
				wasFocus = screen.getKeyboardFocus();
				showMenu();
				handleHighlight(getSelectIndex());
				preventClose = true;
				menu.focus();
			} else {
				menu.hide();
				textField.focus();
			}
		} else
			textField.focus();
	}

	/**
	 * Returns false if list is empty, true if list contains List Items
	 * 
	 * @return boolean
	 */
	protected boolean validateListSize() {
		if (menu == null)
			return false;
		else if (menu.getMenuItems().isEmpty())
			return false;
		else
			return true;
	}

	private void handleHighlight(int index) {
		checkMenu();
		if (menu.isShowing())
			menu.setSelectedIndex(index);
	}

	private void refreshSelectedIndex() {
		if (menu.getMenuItems().isEmpty()) {
			this.selectedIndex = -1;
			setText("");
		} else if (selectedIndex != menu.getSelectedIndex()) {
			this.selectedIndex = menu.getSelectedIndex();
			MenuItem<I> selectedListItem = getSelectedListItem();
			setText(selectedListItem == null ? null : selectedListItem.getText());
			if (selectedListItem == null)
				menu.getScrollPanel().scrollToTop();
			else
				menu.getScrollPanel().scrollYTo(selectedListItem);
		}
	}

	private void updateSelected() {
		setSelectedWithCallback(menu.getSelectedIndex());
		checkMenu();
		if (menu.isShowing())
			menu.hide();
	}
}
