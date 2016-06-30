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

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.CheckBox;
import icetone.controls.menuing.AutoHide;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.listeners.TabFocusListener;

/**
 * An abstract control for use by sub-classes that provide a list of some sort
 * (probably selected).
 *
 * @author Emerald Icemoon
 * @author t0neg0d
 */
public abstract class AbstractList<O, L extends AbstractListItem<?, ? extends AbstractList<O, ?>>> extends ScrollPanel
		implements AutoHide, TabFocusListener {

	protected List<L> listItems = new ArrayList<L>();
	protected Element callerElement;
	protected boolean hasToggleItems = false;
	protected String styleName;

	/**
	 * Creates a new instance of this abstract list control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 * @param styleName
	 *            style name
	 */
	public AbstractList(ElementManager screen, boolean isScrollable, String styleName) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable, styleName);
	}

	/**
	 * Creates a new instance of this abstract list control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public AbstractList(ElementManager screen, Vector2f position, boolean isScrollable, String styleName) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable, styleName);
	}

	/**
	 * Creates a new instance of this abstract list control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 * @param styleName
	 *            style name
	 */
	public AbstractList(ElementManager screen, Vector2f position, Vector2f dimensions, boolean isScrollable, String styleName) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable, styleName);
	}

	/**
	 * Creates a new instance of the this abstract list control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 * @param styleName
	 *            style name
	 */
	public AbstractList(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			boolean isScrollable, String styleName) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, isScrollable, styleName);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 * @param styleName
	 *            style name
	 */
	public AbstractList(ElementManager screen, String UID, Vector2f position, boolean isScrollable, String styleName) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable, styleName);
	}

	/**
	 * Creates a new instance of this abstract list control
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
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 * @param styleName
	 *            style name
	 */
	public AbstractList(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, boolean isScrollable,
			String styleName) {
		this(screen, UID, position, dimensions, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable, styleName);
	}

	/**
	 * Creates a new instance of this abstract list control
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
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 * @param styleName
	 *            style name
	 */
	public AbstractList(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, boolean isScrollable, String styleName) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		this.styleName = styleName;

		setLayoutManager(new AbstractListLayout());

		setTextPaddingByKey(styleName, "textPadding");
		setTextClipPaddingByKey(styleName, "textPadding");
		scrollableArea.setTextPaddingByKey(styleName + "#ScrollableArea", "textPadding");
		scrollableArea.setTextClipPaddingByKey(styleName + "#ScrollableArea", "textPadding");

		setHorizontalScrollBarMode(ScrollBarMode.Never);
		if (!isScrollable) {
			setVerticalScrollBarMode(ScrollBarMode.Never);
		}

		FlowLayout fl = new FlowLayout();
		fl.setOrientation(Orientation.VERTICAL);
		fl.setAlign(Align.Left);
		fl.setFill(true);

		scrollableArea.setLayoutManager(fl);
		scrollableArea.setIgnoreMouseFocus(true);
		scrollableArea.setIgnoreMouseButtons(true);

		innerBounds.setIgnoreMouseFocus(true);
		innerBounds.setIgnoreMouseButtons(true);

		setKeepWithinScreenBounds(true);
		setLockToParentBounds(true);
		setPriority(ZPriority.MENU);

		populateEffects(styleName);
	}

	/**
	 * Adds an item to the list
	 * 
	 * @param caption
	 *            The display caption of the item
	 * @param value
	 *            The value to associate with the item
	 */
	public void addListItem(String caption, O value) {
		addMenuItem(caption, value, false, false);
	}

	/**
	 * Adds an item to the list
	 * 
	 * @param caption
	 *            The display caption of the item
	 * @param value
	 *            The value to associate with the item
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the item
	 */
	public void addListItem(String caption, O value, boolean isToggleItem) {
		addMenuItem(caption, value, isToggleItem, false);
	}

	/**
	 * Adds an item to the list
	 * 
	 * @param caption
	 *            The display caption of the item
	 * @param value
	 *            The value to associate with the item
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the item is true
	 * @param isToggled
	 *            Sets the default state of the added CheckBox
	 */
	public void addMenuItem(String caption, O value, boolean isToggleItem, boolean isToggled) {
		addListItem(caption, value, isToggleItem, isToggled, true);
	}

	public abstract void addListItem(String caption, O value, boolean isToggleItem, boolean isToggled, boolean pack);

	public void addListItem(L item) {
		addListItem(item, true);
	}

	public void addListItem(L item, boolean pack) {
		this.listItems.add(item);
		validateSettings();
		if (pack)
			pack();
	}

	/**
	 * Inserts a new MenuItem at the provided index
	 * 
	 * @param index
	 *            The index to insert into
	 * @param caption
	 *            The display caption of the MenuItem
	 * @param value
	 *            The value to associate with the MenuItem
	 */
	public void insertListItem(int index, String caption, O value) {
		insertListItem(index, caption, value, false, false);
	}

	/**
	 * Inserts a new item at the provided index
	 * 
	 * @param index
	 *            The index to insert into
	 * @param caption
	 *            The display caption of the item
	 * @param value
	 *            The value to associate with the item
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the item
	 */
	public void insertListItem(int index, String caption, O value, boolean isToggleItem) {
		insertListItem(index, caption, value, isToggleItem, false);
	}

	/**
	 * Inserts a new item at the provided index
	 * 
	 * @param index
	 *            The index to insert into
	 * @param caption
	 *            The display caption of the item
	 * @param value
	 *            The value to associate with the item
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the item
	 * @param isToggled
	 *            Sets the default state of the added CheckBox
	 */

	public void insertListItem(int index, String caption, O value, boolean isToggleItem, boolean isToggled) {
		insertListItem(index, caption, value, true);
	}

	public abstract void insertListItem(int index, String caption, O value, boolean isToggleItem, boolean isToggled, boolean pack);

	public void insertListItem(int index, L item) {
		insertListItem(index, item, true);
	}

	public void insertListItem(int index, L item, boolean pack) {
		this.listItems.add(index, item);
		validateSettings();
		if (pack)
			pack();
	}

	/**
	 * Remove the item at the provided index
	 * 
	 * @param index
	 *            int
	 */
	public void removeListItem(int index) {
		if (index >= 0 && index < listItems.size()) {
			listItems.remove(index);
			validateSettings();
			pack();
		}
	}

	/**
	 * Remove the first item that contains the provided value
	 * 
	 * @param value
	 *            Object
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
	 * @param value
	 *            Object
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
			pack();
		}
	}

	/**
	 * Validates flags for: contains toggle checkboxes, etc
	 */
	public void validateSettings() {
		hasToggleItems = false;
		for (L mi : listItems) {
			if (mi.isToggleItem)
				hasToggleItems = true;
		}
	}

	/**
	 * Forces the Menu to rebuild all MenuItems. This does not need to be
	 * called, however it will not effect anything negatively if it is.
	 */
	public void pack() {
		// String finalString = "";

		// menuItemHeight = BitmapTextUtil.getTextLineHeight(this, "Xg");

		scrollableArea.removeAllChildren();
		// scrollableArea.setHeight(menuItemHeight);

		int index = 0;
		// float currentHeight = 0;
		// float width = menuItemHeight*3;
		// boolean init = true;

		// String leftSpacer = " ";
		// String rightSpacer = "";
		//
		// if (callerElement == null) leftSpacer = " ";
		// else if (hasToggleItems) leftSpacer = " ";
		// if (hasSubMenus) rightSpacer = " ";

		for (L mi : listItems) {
			// currentHeight += menuItemHeight;

			if (mi.getIsToggleItem()) {
				this.addCheckBox(index, mi);
			} else {
				mi.setElement(null);
				addItem(index, mi);
			}
			index++;
		}

		// ? maybe
		dirtyLayout(false);
		layoutChildren();
	}

	/**
	 * Notifies the Menu that is has been called by an Element that is expecting
	 * notification of menu item clicks
	 * 
	 * @param el
	 *            Element
	 */
	public final void setCallerElement(Element el) {
		this.callerElement = el;
	}

	/**
	 * Returns the current Element waiting notification
	 * 
	 * @return
	 */
	public Element getCallerElement() {
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
	 * @param index
	 *            int Index of the item
	 * @return item
	 */
	public L getListItem(int index) {
		return this.listItems.get(index);
	}

	@Override
	public void setTabFocus() {
		screen.setKeyboardElement(this);
	}

	@Override
	public void resetTabFocus() {
		if (screen.getKeyboardElement() == this)
			screen.setKeyboardElement(null);
	}

	protected void applyListItemStyles(Element label) {
		label.setFont(screen.getStyle("Font").getString(screen.getStyle(styleName + "#Item").getString("fontName")));
		label.setFontColor(screen.getStyle(styleName + "#Item").getColorRGBA("fontColor"));
		label.setFontSize(screen.getStyle(styleName + "#Item").getFloat("fontSize"));
		label.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle(styleName + "#Item").getString("textAlign")));
		label.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle(styleName + "#Item").getString("textVAlign")));
		label.setTextWrap(LineWrapMode.valueOf(screen.getStyle(styleName + "#Item").getString("textWrap")));
		label.setTextPadding(screen.getStyle(styleName + "#Item").getVector4f("textPadding"));
		label.setTextClipPaddingByKey(styleName + "#Item", "textPadding");
	}

	protected void addItem(int index, L mi) {
		Label label = new Label(mi.caption, screen);
		applyListItemStyles(label);
		addScrollableContent(label, false, null);
		mi.setElement(label);
	}

	protected void addCheckBox(int index, L mi) {
		CheckBox checkbox = new CheckBox(screen);
		checkbox.setIgnoreMouse(true);
		checkbox.addClippingLayer(this);
		applyListItemStyles(checkbox);
		mi.setElement(checkbox);

		addScrollableContent(checkbox);

		if (mi.getIsToggled())
			checkbox.setIsChecked(mi.getIsToggled());

		if (!getIsVisible())
			checkbox.hide();
	}

	@Override
	public void onScrollContentHook(ScrollDirection direction) {
		super.onScrollContentHook(direction);

		/* Scrolling can happen on selection events so it's pretty common. We don't want to lose focus on
		 * for example filter text fields that might rebuild combo boxes. So, only set focus back if the current
		 * focus is this menus vertical scrollbar.
		 * 
		 * This was added to give focus back to the combo box after it was scrolled by the USER
		 */
		if (screen.getTabFocusElement() == getVerticalScrollBar())
			screen.setTabFocusElement(this);
	}

	public static class AbstractListLayout extends ScrollPanelLayout {

		@Override
		public void layout(Element childElement) {
			super.layout(childElement);
		}

	}
}
