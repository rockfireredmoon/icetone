/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.menuing;

import java.util.List;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.lists.AbstractList;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.event.MouseUIMotionEvent;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;
import icetone.listeners.KeyboardListener;
import icetone.listeners.MouseMovementListener;

/**
 *
 * @author t0neg0d
 */
public class Menu<O> extends AbstractList<O, MenuItem<O>> implements MouseMovementListener, KeyboardListener {
	private Element highlight;
	protected ColorRGBA highlightColor;
	protected int currentMenuItemIndex = -1;
	protected int currentHighlightIndex = 0;
	protected float menuOverhang;
	protected boolean hasSubMenus = false;
	protected Menu<O> caller;

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public Menu(ElementManager screen, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("Menu").getVector4f("resizeBorders"), screen.getStyle("Menu").getString("defaultImg"),
				isScrollable);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public Menu(ElementManager screen, Vector2f position, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE,
				screen.getStyle("Menu").getVector4f("resizeBorders"), screen.getStyle("Menu").getString("defaultImg"),
				isScrollable);
	}

	/**
	 * Creates a new instance of the Menu control
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
	 */
	public Menu(ElementManager screen, Vector2f position, Vector2f dimensions, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable);
	}

	/**
	 * Creates a new instance of the Menu control
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
	 */
	public Menu(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, isScrollable);
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
	 */
	public Menu(ElementManager screen, String UID, Vector2f position, boolean isScrollable) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable);
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
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public Menu(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, boolean isScrollable) {
		this(screen, UID, position, dimensions, screen.getStyle("Menu").getVector4f("resizeBorders"),
				screen.getStyle("Menu").getString("defaultImg"), isScrollable);
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
	 */
	public Menu(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, boolean isScrollable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, isScrollable, "Menu");

		setLayoutManager(new MenuLayout());

		menuOverhang = screen.getStyle("Menu").getFloat("menuOverhang");
		highlightColor = screen.getStyle("Menu").getColorRGBA("highlightColor");

		highlight = new Element(screen, UID + ":Highlight");
		highlight.setIgnoreMouse(true);
		if (screen.getStyle("Menu").getColorRGBA("highlightColor") != null) {
			highlight.getElementMaterial().setColor("Color", screen.getStyle("Menu").getColorRGBA("highlightColor"));
		} else {
			highlight.borders.set(screen.getStyle("Menu").getVector4f("highlightResizeBorders"));
			highlight.setTexture(screen.getStyle("Menu").getString("highlightImg"));
		}
		addChild(highlight);
		highlight.hide();

		bindPressed(evt -> {
			currentMenuItemIndex = currentHighlightIndex;
			evt.setConsumed();
			currentMenuItemIndex = currentHighlightIndex;
			evt.setConsumed();
		});

		bindReleased(evt -> {
			boolean hasSubMenu = false;

			if (currentMenuItemIndex > -1 && currentMenuItemIndex < listItems.size())
				this.handleMenuItemClick(listItems.get(currentMenuItemIndex), currentMenuItemIndex,
						listItems.get(currentMenuItemIndex).getValue());

			if (!hasSubMenu) {
				this.hideAllSubmenus(true);
				if (Screen.isAndroid())
					screen.handleAndroidMenuState(this);
			}

			evt.setConsumed();
		});
	}

	@Override
	public void addListItem(String caption, O value, boolean isToggleItem, boolean isToggled, boolean pack) {
		MenuItem<O> item = new MenuItem<O>(this, caption, value, isToggleItem, isToggled);
		addListItem(item);
	}

	@Override
	public void insertListItem(int index, String caption, O value, boolean isToggleItem, boolean isToggled,
			boolean pack) {
		MenuItem<O> item = new MenuItem<O>(this, caption, value, isToggleItem, isToggled);
		insertListItem(index, item, pack);
	}

	/**
	 * Adds a MenuItem to the Menu
	 * 
	 * @param caption
	 *            The display caption of the MenuItem
	 * @param value
	 *            The value to associate with the MenuItem
	 * @param subMenu
	 *            The associated Sub-Menu that should be displayed with
	 *            thisMenuItem. null if N/A
	 */
	public void addMenuItem(String caption, O value, Menu<O> subMenu) {
		addMenuItem(caption, value, subMenu, false, false);
	}

	/**
	 * Adds a MenuItem to the Menu
	 * 
	 * @param caption
	 *            The display caption of the MenuItem
	 * @param value
	 *            The value to associate with the MenuItem
	 * @param subMenu
	 *            The associated Sub-Menu that should be displayed with
	 *            thisMenuItem. null if N/A
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the MenuItem is true
	 */
	public void addMenuItem(String caption, O value, Menu<O> subMenu, boolean isToggleItem) {
		addMenuItem(caption, value, subMenu, isToggleItem, false);
	}

	/**
	 * Adds a MenuItem to the Menu
	 * 
	 * @param caption
	 *            The display caption of the MenuItem
	 * @param value
	 *            The value to associate with the MenuItem
	 * @param subMenu
	 *            The associated Sub-Menu that should be displayed with
	 *            thisMenuItem. null if N/A
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the MenuItem is true
	 * @param isToggled
	 *            Sets the default state of the added CheckBox
	 */
	public void addMenuItem(String caption, O value, Menu<O> subMenu, boolean isToggleItem, boolean isToggled) {
		addMenuItem(caption, value, subMenu, isToggleItem, isToggled, true);
	}

	public void addMenuItem(String caption, O value, Menu<O> subMenu, boolean isToggleItem, boolean isToggled,
			boolean pack) {
		addListItem(new MenuItem<O>(this, caption, value, subMenu, isToggleItem, isToggled));
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
	 * @param subMenu
	 *            The associated Sub-Menu that should be displayed with
	 *            thisMenuItem. null if N/A
	 */
	public void insertMenuItem(int index, String caption, O value, Menu<O> subMenu) {
		insertMenuItem(index, caption, value, subMenu, false, false);
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
	 * @param subMenu
	 *            The associated Sub-Menu that should be displayed with
	 *            thisMenuItem. null if N/A
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the MenuItem is true
	 */
	public void insertMenuItem(int index, String caption, O value, Menu<O> subMenu, boolean isToggleItem) {
		insertMenuItem(index, caption, value, subMenu, isToggleItem, false);
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
	 * @param subMenu
	 *            The associated Sub-Menu that should be displayed with
	 *            thisMenuItem. null if N/A
	 * @param isToggleItem
	 *            Adds a toggleable CheckBox to the MenuItem is true
	 * @param isToggled
	 *            Sets the default state of the added CheckBox
	 */

	public void insertMenuItem(int index, String caption, O value, Menu<O> subMenu, boolean isToggleItem,
			boolean isToggled) {
		insertMenuItem(index, caption, value, subMenu, true);
	}

	public void insertMenuItem(int index, String caption, O value, Menu<O> subMenu, boolean isToggleItem,
			boolean isToggled, boolean pack) {
		insertMenuItem(index, new MenuItem<O>(this, caption, value, subMenu, isToggleItem, isToggled), pack);
	}

	public void insertMenuItem(int index, MenuItem<O> item) {
		insertMenuItem(index, item, true);
	}

	public void insertMenuItem(int index, MenuItem<O> item, boolean pack) {
		insertListItem(index, item, pack);
	}

	/**
	 * Remove the MenuItem at the provided index
	 * 
	 * @param index
	 *            int
	 * 
	 * @deprecated use {@link #removeMenuItem(int)}
	 */
	public void removeMenuItem(int index) {
		removeListItem(index);
	}

	/**
	 * Remove the first MenuItem that contains the provided value
	 * 
	 * @param value
	 *            Object
	 * @deprecated use {@link #removeListItem(Object)}
	 */
	public void removeMenuItem(Object value) {
		removeListItemWithValue(value);
	}

	/**
	 * Remove the first MenuItem that contains the provided caption
	 * 
	 * @param value
	 *            Object
	 * @deprecated use {@link #removeListItemWithCaption(String)}
	 */
	public void removeMenuItemWithCaption(String caption) {
		removeListItemWithCaption(caption);
	}

	/**
	 * Removes the first MenuItem in the Menu
	 * 
	 * @deprecated use {@link #removeFirstListItem()}
	 */
	public void removeFirstMenuItem() {
		removeFirstListItem();
	}

	/**
	 * Removes the last MenuItem in the Menu
	 * 
	 * @deprecated use {@link #removeLastListItem()}
	 */
	public void removeLastMenuItem() {
		removeLastListItem();
	}

	/**
	 * Remove all menu items
	 * 
	 * @deprecated use {@link #removeAllMenuItems()}
	 */
	public void removeAllMenuItems() {
		removeAllListItems();
	}

	/**
	 * Defines the number of pixels this Menu should overhang it's parent Menu
	 * when called as a Sub-Menu
	 * 
	 * @param menuOverhang
	 */
	public void setMenuOverhang(float menuOverhang) {
		this.menuOverhang = menuOverhang;
	}

	/**
	 * Returns the number of pixels this Menu should overhang it's parent Menu
	 * when called as a Sub-Menu
	 * 
	 * @return float
	 */
	public float getMenuOverhang() {
		return this.menuOverhang;
	}

	/**
	 * Validates flags for: contains subMenus, toggle checkboxes, etc
	 */
	public void validateSettings() {
		super.validateSettings();
		hasSubMenus = false;
		for (MenuItem<O> mi : listItems) {
			if (mi.subMenu != null)
				hasSubMenus = true;
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

		for (MenuItem<O> mi : listItems) {
			// currentHeight += menuItemHeight;

			if (mi.getSubMenu() != null) {
				this.addSubmenuArrow(index);
			}
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
	 * Returns a list of all MenuItems associated with this menu
	 * 
	 * @return List<MenuItem>
	 */
	public List<MenuItem<O>> getMenuItems() {
		return this.listItems;
	}

	/**
	 * Returns the MenuItem at the provided index
	 * 
	 * @param index
	 *            int Index of the MenuItem
	 * @return MenuItem
	 */
	public MenuItem<O> getMenuItem(int index) {
		return getListItem(index);
	}

	/**
	 * Shows the Menu
	 * 
	 * @param caller
	 *            Menu The Parent Menu that is calling the menu. null if not
	 *            called by another Menu
	 * @param x
	 *            float The x coord to display the Menu at
	 * @param y
	 *            float the Y coord to display the Menu at
	 */
	public void showMenu(Menu<O> caller, float x, float y) {
		this.caller = caller;
		if (caller != null) {
			if (x < 0)
				x = 0;
			else if (x + getWidth() > screen.getWidth()) {
				x = caller.getAbsoluteX() - getWidth() + menuOverhang;
				if (x < 0)
					x = 0;
			}
			if (y < 0)
				y = 0;
			else if (y + getHeight() > screen.getHeight())
				y -= getAbsoluteHeight() - screen.getHeight();
		} else {
			if (x < 0)
				x = 0;
			else if (x + getWidth() > screen.getWidth())
				x = screen.getWidth() - getWidth();
			if (y < 0)
				y = 0;
			else if (y + getHeight() > screen.getHeight())
				y = screen.getHeight() - getHeight();
		}
		this.moveTo(x, y);
		// Effect effect = getEffect(Effect.EffectEvent.Show);
		// if (effect != null)
		// if (effect.getEffectType() == Effect.EffectType.FadeIn)
		// this.propagateEffect(effect, false);
		// else
		// screen.getEffectManager().applyEffect(effect);
		// else
		this.show();
	}

	/**
	 * Hides the menu
	 */
	public void hideMenu() {
		Effect effect = getEffect(Effect.EffectEvent.Hide);
		if (effect != null)
			if (effect.getEffectType() == Effect.EffectType.FadeOut)
				this.propagateEffect(effect, true);
			else
				screen.getEffectManager().applyEffect(effect);
		else
			this.hide();
	}

	/**
	 * Sets the highlight Element's current position to the Y position of the
	 * supplied MenuItem index
	 * 
	 * @param index
	 *            int
	 */
	public void setSelectedIndex(int index) {
		currentHighlightIndex = index;
		dirtyLayout(false);
		layoutChildren();
		MenuItem<O> item = listItems.get(index);
		float y = LUtil.getY(item.getElement());
		float sy = Math.abs(LUtil.getY(scrollableArea));
		if (y + item.getElement().getHeight() > sy + getScrollBoundsHeight()) {
			scrollYTo(-y + getScrollBoundsHeight() - item.getElement().getHeight());
		} else {
			if (y < sy) {
				scrollYTo(-y);
			}
		}
	}

	public int getSelectedIndex() {
		return currentHighlightIndex;
	}

	public Element getHighlight() {
		return highlight;
	}

	@Override
	public void onMouseMove(MouseUIMotionEvent evt) {
		if (!Screen.isAndroid()) {
			int ex = evt.getX() - (int) scrollableArea.getAbsoluteX();
			int ey = (int) evt.getY() - (int) LUtil.getAbsoluteY(scrollableArea);
			List<Element> elementList = getScrollableArea().getElementList();
			for (int i = 0; i < elementList.size(); i++) {
				Element el = elementList.get(i);
				float elY = LUtil.getY(el);
				if (ex >= el.getX() && ex < el.getX() + el.getWidth() && ey >= elY && ey < elY + el.getHeight()) {
					setSelectedIndex(i);
					break;
				}
			}
		}
	}

	@Override
	public void controlHideHook() {
		super.controlHideHook();
		highlight.removeFromParent();
		currentMenuItemIndex = -1;
	}

	@Override
	public void onKeyPress(KeyInputEvent evt) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_UP) {
			setSelectedIndex(Math.max(currentHighlightIndex - 1, 0));
			((MenuLayout) layoutManager).repositionHighlight();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
			setSelectedIndex(Math.min(currentHighlightIndex + 1, listItems.size() - 1));
			((MenuLayout) layoutManager).repositionHighlight();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
			if (currentHighlightIndex >= 0 && currentHighlightIndex < listItems.size())
				handleMenuItemClick(listItems.get(currentHighlightIndex), currentHighlightIndex,
						listItems.get(currentHighlightIndex).getValue());
			// hideMenu();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_ESCAPE) {
			hideMenu();
			evt.setConsumed();
		}
	}

	public void scrollToItem(int index) {
		scrollYTo(LUtil.getAbsoluteY(listItems.get(index).getElement()));
	}

	protected void hideAllSubmenus(boolean upChain) {
		for (MenuItem<O> mi : listItems) {
			if (mi.getSubMenu() != null)
				mi.getSubMenu().hideMenu();
		}
		if (caller != null && upChain) {
			caller.hideAllSubmenus(upChain);
		}
	}

	/**
	 * Abstract method for handling menu item selection
	 * 
	 * @param index
	 *            Index of MenuItem clicked
	 * @param value
	 *            String value of MenuItem clicked
	 */
	protected void onMenuItemClicked(int index, O value, boolean isToggled) {
	}

	private void addSubmenuArrow(int index) {
		// Element elArrow = new Element(screen, getUID() + ":Arrow:" + index,
		// new Vector2f(getWidth() - menuItemHeight - getMPWidth(),
		// -(menuItems.size() * menuItemHeight) + (menuItemHeight + (index *
		// menuItemHeight) + (menuPadding.z))),
		// new Vector2f(menuItemHeight, menuItemHeight), new Vector4f(0, 0, 0,
		// 0),
		// screen.getStyle("Common").getString("arrowRight"));
		// elArrow.setScaleEW(false);
		// elArrow.setScaleNS(false);
		// elArrow.setDocking(Docking.SE);
		// elArrow.setIsResizable(false);
		// elArrow.setIsMovable(false);
		// elArrow.setIgnoreMouse(true);
		// elArrow.addClippingLayer(this);
		// elArrow.setTextClipPadding(this.getMenuPadding());
		//
		// addScrollableContent(elArrow);
		//
		// if (!getIsVisible())
		// elArrow.hide();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onScrollContentHook(ScrollDirection direction) {
		super.onScrollContentHook(direction);
		((MenuLayout) getLayoutManager()).repositionHighlight();
	}

	private void handleMenuItemClick(MenuItem<O> menuItem, int menuItemIndex, O value) {
		if (menuItem.getIsToggleItem())
			menuItem.setIsToggled(!menuItem.getIsToggled());
		onMenuItemClicked(menuItemIndex, value, menuItem.getIsToggled());
		if (!Screen.isAndroid())
			hide();
	}

	class MenuLayout extends AbstractListLayout {

		@Override
		public void layout(Element childElement) {
			super.layout(childElement);
			repositionHighlight();
		}

		protected void repositionHighlight() {
			if (currentHighlightIndex > -1 && currentHighlightIndex < listItems.size()) {
				MenuItem<O> m = (MenuItem<O>) listItems.get(currentHighlightIndex);
				Element el = m.getElement();
				if (el != null) {
					highlight.show();
					LUtil.setBounds(highlight, textPadding.x, LUtil.getY(el) + LUtil.getY(scrollableArea),
							getScrollBoundsWidth(), el.getHeight());
				}
			} else {
				highlight.hide();
			}
		}

	}
}
