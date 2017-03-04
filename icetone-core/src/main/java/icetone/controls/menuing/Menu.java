/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
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
package icetone.controls.menuing;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.input.KeyInput;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.controls.extras.Separator;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ZPriority;
import icetone.core.event.ChangeSupport;
import icetone.core.event.KeyboardUIEvent;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.layout.FillLayout;
import icetone.core.layout.WrappingLayout;
import icetone.core.utils.MathUtil;

/**
 * Standard Menu component suitable for use with {@link MenuItem} and
 * {@link MenuBar}.
 * 
 * @author rockfire
 */
public class Menu<O> extends Element implements AutoHide {

	class MenuLayout extends WrappingLayout {
		MenuLayout() {
			setOrientation(Orientation.HORIZONTAL);
			setEqualSizeCells(true);
			setWidth(1);
			setFill(false);
		}
	}

	protected ChangeSupport<Menu<O>, MenuItem<O>> changeSupport;
	protected float childMenuGap = 8f;
	protected MenuItem<O> childMenusItem;
	protected BitmapFont.Align direction = BitmapFont.Align.Right;
	protected float menuHeight = -1f;
	protected Menu<O> showingChildMenu;
	private BaseElement anchor;
	private Element arrowElement;
	private Menu<O> caller;
	private Element inner;
	private boolean preventDeselect;
	private final ScrollPanel scroller;
	private boolean selectOnHighlight = false;
	private String title;
	private float leftGutterWidth;
	private float rightGutterWidth;
	private boolean forceRightGutter;
	private boolean forceLeftGutter;

	public Menu() {
		this(BaseScreen.get(), null);
	}

	public Menu(ElementManager<?> screen) {
		this(screen, null);
	}

	public Menu(ElementManager<?> screen, String title) {
		super(screen);

		this.title = title;

		setLayoutManager(new FillLayout());

		inner = new Element(screen);
		inner.setStyleClass("inner");
		inner.setLayoutManager(new FillLayout());
		addElement(inner);

		arrowElement = new Element(screen);
		arrowElement.setStyleClass("submenu-arrow");

		scroller = new ScrollPanel(screen);
		scroller.setHorizontalScrollBarMode(ScrollBarMode.Never);
		scroller.setScrollContentLayout(new MenuLayout());

		inner.addElement(scroller, "growx, growy");

		setDestroyOnHide(true);
		setMouseFocusable(true);
		setIgnoreMouseWheel(false);
		setPriority(ZPriority.POPUP);
		setLockToParentBounds(true);
		onKeyboardPressed(evt -> {
			handleMenuKeyEvent(evt);
		});

		onMouseWheel(evt -> {
			switch (evt.getDirection()) {
			case up:
				scroller.scrollYBy(scroller.getTrackIncrement());
				evt.setConsumed();
				break;
			case down:
				scroller.scrollYBy(-scroller.getTrackIncrement());
				evt.setConsumed();
				break;
			default:
				break;
			}
		});
	}

	public Menu(String title) {
		this(BaseScreen.get(), title);
	}

	public Menu<O> addChangeListener(UIChangeListener<Menu<O>, MenuItem<O>> listener) {
		if (changeSupport != null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public Menu<O> addMenuItem(BaseElement el) {
		return addMenuItem(null, el, null);
	}

	public Menu<O> addMenuItem(O value) {
		return addMenuItem(String.valueOf(value), value);
	}

	@SuppressWarnings("unchecked")
	public Menu<O> addMenuItem(String caption, BaseElement itemElement, O value) {
		if (itemElement != null)
			itemElement.setIgnoreMouseMovement(true);

		if (caption == null && itemElement instanceof Menu) {
			caption = ((Menu<O>) itemElement).getTitle();
		}

		final MenuItem<O> zMenuItem = new MenuItem<O>(screen, caption, itemElement, value);
		if (itemElement instanceof Separator)
			zMenuItem.setSelectable(false);
		return addMenuItemElement(zMenuItem);
	}

	public Menu<O> addMenuItem(String caption, O value) {
		return addMenuItem(caption, null, value);
	}

	public Menu<O> addMenuItemElement(MenuItem<O> item) {
		scroller.addScrollableContent(item);
		if (getSelectedIndex() == -1) {
			setSelectedItem(item, !selectOnHighlight);
		}
		return this;
	}

	public Menu<O> addSeparator() {
		return addMenuItem(null, new Separator(screen, Orientation.HORIZONTAL), null);
	}

	public void close() {
		if (caller != null) {
			caller.close();
		} else {
			hideThisAndChildren();
		}
	}

	@Override
	public void controlHideHook() {
		super.controlHideHook();

		if (showingChildMenu != null && showingChildMenu.isVisible()) {
			final Menu<O> fShowingChildMenu = showingChildMenu;
			if (screen.getElements().contains(fShowingChildMenu)) {
				screen.removeElement(fShowingChildMenu);
				fShowingChildMenu.controlHideHook();
			}
		}
		if (caller != null) {
			caller.childHidden();
			caller = null;
		}
	}

	public float getChildMenuGap() {
		return childMenuGap;
	}

	@SuppressWarnings("unchecked")
	public float getLeftGutterWidth() {
		if (forceLeftGutter)
			return leftGutterWidth;
		Vector2f width = new Vector2f();
		for (BaseElement e : scroller.getScrollableArea().getElements()) {
			if (e instanceof MenuItem && ((MenuItem<O>) e).getItemElement() != null
					&& !(((MenuItem<O>) e).getItemElement() instanceof Menu)
					&& !(((MenuItem<O>) e).getItemElement() instanceof Separator)) {
				BaseElement el = ((MenuItem<O>) e).getItemElement();
				if (forceLeftGutter) {
					width.x = leftGutterWidth;
					break;
				}
				width = MathUtil.max(el.calcPreferredSize(), width);
			}
		}
		return width.x;
	}

	public float getMenuHeight() {
		return menuHeight;
	}

	@SuppressWarnings("unchecked")
	public MenuItem<O> getMenuItem(int index) {
		return (MenuItem<O>) scroller.getScrollableArea().getElements().get(index);
	}

	@SuppressWarnings("unchecked")
	public List<MenuItem<O>> getMenuItems() {
		List<MenuItem<O>> mi = new ArrayList<MenuItem<O>>();
		for (BaseElement e : scroller.getScrollableArea().getElements()) {
			mi.add((MenuItem<O>) e);
		}
		return mi;
	}

	public MenuItem<O> getMenuItemWithText(String value) {
		for (MenuItem<O> i : getMenuItems()) {
			if (Objects.equals(i.getText(), value)) {
				return i;
			}
		}
		return null;
	}

	public MenuItem<O> getMenuItemWithValue(O value) {
		for (MenuItem<O> i : getMenuItems()) {
			if (Objects.equals(i.getValue(), value)) {
				return i;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public float getRightGutterWidth() {
		if (forceRightGutter)
			return rightGutterWidth;
		for (BaseElement e : scroller.getScrollableArea().getElements()) {
			if (e instanceof MenuItem && ((MenuItem<O>) e).getItemElement() instanceof Menu) {
				if (forceRightGutter) {
					return rightGutterWidth;
				}
				return arrowElement.calcPreferredSize().x;
			}
		}
		return 0;
	}

	public ScrollPanel getScrollPanel() {
		return scroller;
	}

	@SuppressWarnings("unchecked")
	public int getSelectedIndex() {
		int idx = 0;
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			MenuItem<O> it = (MenuItem<O>) el;
			if (it.isSelected())
				return idx;
			idx++;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public MenuItem<O> getSelectedItem() {
		int idx = getSelectedIndex();
		return idx == -1 ? null : (MenuItem<O>) scroller.getScrollableArea().getElements().get(idx);
	}

	@SuppressWarnings("unchecked")
	public O getSelectedValue() {
		int idx = getSelectedIndex();
		return idx == -1 ? null : ((MenuItem<O>) scroller.getScrollableArea().getElements().get(idx)).getValue();
	}

	public String getTitle() {
		return title;
	}

	public Menu<O> insertMenuItem(BaseElement el, int index) {
		return insertMenuItem(null, el, null, index);
	}

	public Menu<O> insertMenuItem(O value, int index) {
		return insertMenuItem(String.valueOf(value), value, index);
	}

	@SuppressWarnings("unchecked")
	public Menu<O> insertMenuItem(String caption, BaseElement itemElement, O value, int index) {
		if (itemElement != null)
			itemElement.setIgnoreMouseMovement(true);

		if (caption == null && itemElement instanceof Menu) {
			caption = ((Menu<O>) itemElement).getTitle();
		}

		final MenuItem<O> zMenuItem = new MenuItem<O>(screen, caption, itemElement, value);
		if (itemElement instanceof Separator)
			zMenuItem.setSelectable(false);
		return insertMenuItemElement(zMenuItem, index);
	}

	public Menu<O> insertMenuItem(String caption, O value, int index) {
		return insertMenuItem(caption, null, value, index);
	}

	public Menu<O> insertMenuItemElement(MenuItem<O> item, int index) {
		scroller.insertScrollableContent(item, index);
		if (getSelectedIndex() == -1) {
			setSelectedItem(item, !selectOnHighlight);
		}
		return this;
	}

	public boolean isDestroyOnHide() {
		if (caller != null) {
			return caller.isDestroyOnHide();
		}
		return super.isDestroyOnHide();
	}

	/**
	 * Get whether or not to fire a change event when an item is merely hovered
	 * over. You probably want this type of behaviour in a menubar type control,
	 * but not in a combobox menu.
	 * 
	 * @return select on highlight
	 */
	public boolean isSelectOnHighlight() {
		return selectOnHighlight;
	}

	public Menu<O> onChanged(UIChangeListener<Menu<O>, MenuItem<O>> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public void removeAllMenuItems() {
		scroller.getScrollableArea().removeAllChildren();
		layoutChildren();
	}

	public Menu<O> removeChangeListener(UIChangeListener<Menu<O>, MenuItem<O>> listener) {
		if (changeSupport != null)
			changeSupport.removeListener(listener);
		return this;
	}

	@SuppressWarnings("unchecked")
	public void removeMenuItem(int index) {
		removeMenuItem((MenuItem<O>) scroller.getScrollableArea().getElements().get(index));
	}

	public void removeMenuItem(MenuItem<O> item) {
		scroller.getScrollableArea().removeElement(item);
		layoutChildren();
	}

	public void removeMenuItemWithText(String caption) {
		MenuItem<O> i = getMenuItemWithText(caption);
		if (i != null) {
			removeMenuItem(i);
		}
	}

	public void removeMenuItemWithValue(O value) {
		MenuItem<O> i = getMenuItemWithValue(value);
		if (i != null) {
			removeMenuItem(i);
		}
	}

	public void setChildMenuGap(float childMenuGap) {
		this.childMenuGap = childMenuGap;
	}

	public void setMenuHeight(float menuHeight) {
		this.menuHeight = menuHeight;
	}

	public Menu<O> setSelectedIndex(int index) {
		setSelectedIndex(index, false);
		return this;
	}

	public Menu<O> setSelectedItem(MenuItem<O> item) {
		setSelectedItem(item, false);
		return this;
	}

	/**
	 * Set whether or not to fire a change event when an item is merely hovered
	 * over. You probably want this type of behaviour in a menubar type control,
	 * but not in a combobox menu.
	 * 
	 * @return select on highlight
	 */
	public void setSelectOnHighlight(boolean selectOnHighlight) {
		this.selectOnHighlight = selectOnHighlight;
	}

	public Menu<O> setTitle(String title) {
		this.title = title;
		if (getElementParent() instanceof MenuBar) {
			((MenuBar) getElementParent()).buttonForMenu(this).setText(title);
		}
		return this;
	}

	/**
	 * Shows the Menu anchored around another element using the text alignment
	 * configured on this element (the menu itself usually has no text so this
	 * is OK).
	 * 
	 * @param anchor
	 *            the element to anchor the menu around
	 */
	public void showMenu(BaseElement anchor) {
		showMenu(anchor, getTextVAlign(), getTextAlign(), getIndent());
	}

	/**
	 * Shows the Menu
	 * 
	 * @param anchor
	 *            the element to anchor the menu around
	 * 
	 * @param vertical
	 *            vertical alignment
	 * @param align
	 *            horizontal alignment
	 */
	public void showMenu(BaseElement anchor, VAlign vertical, Align align, float offset) {
		float x = 0;
		float y = 0;
		this.anchor = anchor;

		Vector2f pref = calcPreferredSize();
		if (anchor != null) {
			if (vertical == null && anchor != null) {
				if (anchor.getAbsoluteY() > screen.getHeight() / 2f)
					vertical = VAlign.Top;
				else
					vertical = VAlign.Bottom;
			}

			switch (vertical) {
			case Top:
				y = anchor.getAbsoluteY() - pref.y - offset;
				if (y < 0) {
					// Bottom instead
					y = anchor.getAbsoluteHeight() + offset;
				}
				break;
			case Center:
				y = anchor.getAbsoluteY() - (int) (pref.y / 2f);
				break;
			case Bottom:
				y = anchor.getAbsoluteHeight() + offset;
				if (y + pref.y > getParentContainer().getHeight()) {
					// Top instead
					y = anchor.getAbsoluteY() - pref.y - offset;
				}
				break;
			}

			if (align == null && anchor != null) {
				if (anchor.getAbsoluteX() > screen.getWidth() / 2f)
					align = Align.Right;
				else
					align = Align.Left;
			}

			switch (align) {
			case Left:

				switch (vertical) {
				case Top:
				case Bottom:
					x = anchor.getAbsoluteX();
					if (x + pref.x > getParentContainer().getWidth()) {
						x = anchor.getAbsoluteWidth() - pref.x;
					}
					break;
				default:
					x = anchor.getAbsoluteX() - pref.x - offset;
					if (x < 0) {
						// Right instead
						x = anchor.getAbsoluteWidth() + offset;
					}
					break;
				}

				break;
			case Center:
				x = (int) (anchor.getAbsoluteX() + (anchor.getWidth() / 2f) - (pref.x / 2f));
				break;
			case Right:

				switch (vertical) {
				case Top:
				case Bottom:
					x = anchor.getAbsoluteWidth() - pref.x;
					if (x + pref.x > getParentContainer().getWidth()) {
						x = anchor.getAbsoluteWidth();
					}
					break;
				default:
					x = anchor.getAbsoluteWidth() + offset;
					if (x + pref.x > getParentContainer().getWidth()) {
						// Top instead
						x = anchor.getAbsoluteX() - pref.x + offset;
					}
					break;
				}

				break;
			}
		}

		moveTo(x, y);

		if (caller == null && (getParentContainer() == null || !getParentContainer().getElements().contains(this)))
			/*
			 * Convenience behaviour to add to screen if it is not already there
			 */
			if (screen != null)
				screen.showElement(this);
			else
				throw new IllegalStateException("Screen must be known.");
		else
			show();
	}

	public void showMenu(Menu<O> caller, float x, float y) {

		this.caller = caller;
		this.anchor = caller;

		// setPosition(x, y);

		Vector2f pref = calcPreferredSize();

		float ny = Math.max(0, y);
		float nx = Math.max(0, x);

		// If the new of this menu would take it offscreen, restrict it, and
		// reverse
		// the direction of future menus
		if (direction == BitmapFont.Align.Right && pref.x + nx > screen.getWidth()) {
			nx = screen.getWidth() - pref.x;
			direction = BitmapFont.Align.Left;
		} else if (direction == BitmapFont.Align.Left && nx < 0) {
			nx = 0;
			direction = BitmapFont.Align.Right;
		}

		if (ny < screen.getHeight() / 2f) {
			// If this leaves the menu hanging below the edge of the screen
			// shift it up
			if (ny + pref.y > screen.getHeight()) {
				ny = 0;
			}

			// If this still leaves the menu hanging below the edge of the
			// screen
			// reduce its size
			if (ny + pref.y > screen.getHeight()) {
				pref.y = screen.getHeight();
			}
		} else {
			// Shift the menu down by its height so it 'hangs' from the
			// activation
			// point
			ny -= pref.y;

			// If this pushes the menu past the edge of the screen, shift it
			// back
			// towards to the top
			if (ny < 0) {
				ny = 0;
			}

			// If this leaves the menu hanging below the edge of the screen
			// again,
			// reduce the size
			if (ny + pref.y > screen.getHeight()) {
				pref.y = screen.getHeight() - ny;
			}
		}

		// Postion, size, layout and show
		setBounds(nx, ny, pref.x, pref.y);

		// scroller.scrollToTop();
		if (caller == null && (getParentContainer() == null || !getParentContainer().getElements().contains(this)))
			/*
			 * Convenience behaviour to add to screen if it is not already there
			 */
			screen.showElement(this);
		else
			show();
		// focus();

		// bringAllToFront();
	}

	public Menu<O> unbindChanged(UIChangeListener<Menu<O>, MenuItem<O>> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	protected void bringAllToFront() {
		bringToFront();
		if (caller != null)
			caller.bringToFront();
	}

	protected void childHidden() {
		if (childMenusItem != null && !preventDeselect) {
			childMenusItem.setSelected(false);
		}
		preventDeselect = false;
		childMenusItem = null;
		showingChildMenu = null;
	}

	@SuppressWarnings("unchecked")
	protected void handleMenuKeyEvent(KeyboardUIEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_UP) {
			int idx = getSelectedIndex();
			for (int i = 0; i < scroller.getScrollableArea().getElements().size(); i++) {
				idx--;
				if (idx < 0)
					idx = scroller.getScrollableArea().getElements().size() - 1;
				MenuItem<O> newSel = (MenuItem<O>) scroller.getScrollableArea().getElements().get(idx);
				if (newSel.isSelectable()) {
					newSel.showChildMenu(newSel.getItemElement(), newSel.getY());
					setSelectedIndex(idx, !selectOnHighlight);
					evt.setConsumed();
					return;
				}
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {

			int idx = getSelectedIndex();
			for (int i = 0; i < scroller.getScrollableArea().getElements().size(); i++) {
				idx++;
				if (idx >= scroller.getScrollableArea().getElements().size())
					idx = 0;
				MenuItem<O> newSel = (MenuItem<O>) scroller.getScrollableArea().getElements().get(idx);
				if (newSel.isSelectable()) {
					newSel.showChildMenu(newSel.getItemElement(), newSel.getY());
					setSelectedIndex(idx, !selectOnHighlight);
					evt.setConsumed();
					return;
				}
			}

		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {

			int idx = getSelectedIndex();
			if (idx != -1) {
				MenuItem<O> newSel = (MenuItem<O>) scroller.getScrollableArea().getElements().get(idx);
				if (newSel.getItemElement() instanceof Menu) {
					Menu<O> submenu = (Menu<O>) newSel.getItemElement();
					if (!submenu.getMenuItems().isEmpty())
						submenu.setSelectedIndex(0, isSelectOnHighlight());
					submenu.focus();
					evt.setConsumed();
				}
			}

		} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
			MenuItem<O> sel = getSelectedItem();
			if (sel != null && sel.isEnabled())
				itemSelected(this, sel, true, false);
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_ESCAPE || evt.getKeyCode() == KeyInput.KEY_LEFT) {
			if (anchor != null && anchor instanceof Menu) {
				setSelectedIndex(-1);
				Menu<O> parAnchor = (Menu<O>) anchor;
				parAnchor.preventDeselect = true;
				int idx = parAnchor.indexOfItemElement(this);
				if (idx != -1) {
					parAnchor.setSelectedIndex(idx);
				}
				hide();
				if (parAnchor.anchor instanceof Button
						&& ((Button) parAnchor.anchor).getElementParent() instanceof MenuBar) {
					((Button) parAnchor.anchor).focus();
				} else
					parAnchor.focus();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_ESCAPE && isVisible()) {
				hide();
				evt.setConsumed();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void hideThisAndChildren() {
		hide();
		for (BaseElement e : scroller.getScrollableArea().getElements()) {
			MenuItem<O> z = (MenuItem<O>) e;
			if (z.getItemElement() != null && z.getItemElement() instanceof Menu) {
				((Menu<O>) z.getItemElement()).hideThisAndChildren();
			}
		}
		if (caller != null) {
			caller.bringAllToFront();
		}
	}

	protected void itemSelected(Menu<O> originator, MenuItem<O> item, boolean hide, boolean temporary) {

		// Bubble up to the caller by default if there is one
		if (caller != null) {
			caller.itemSelected(originator, item, hide, temporary);
		}

		if (hide)
			hideThisAndChildren();

		if (this.equals(originator)) {
			if (changeSupport != null) {
				changeSupport
						.fireEvent(new UIChangeEvent<Menu<O>, MenuItem<O>>(this, null, item).setTemporary(temporary));
			}
		}
	}

	protected void parseLayoutData(String layoutData) {
		leftGutterWidth = Float.MIN_VALUE;
		rightGutterWidth = Float.MIN_VALUE;
		forceLeftGutter = false;
		forceRightGutter = false;
		if (layoutData != null) {
			for (String l : layoutData.split(",")) {
				String[] a = l.split("=");
				if (a.length > 0) {
					if (a[0].equals("left-gutter-width") && a.length > 1)
						leftGutterWidth = Float.parseFloat(a[1]);
					else if (a[0].equals("right-gutter-width") && a.length > 1)
						rightGutterWidth = Float.parseFloat(a[1]);
					else if (a[0].equals("left-gutter-width") && a.length > 1)
						leftGutterWidth = Float.parseFloat(a[1]);
					else if (a[0].equals("force-left-gutter") && a.length == 1)
						forceLeftGutter = true;
					else if (a[0].equals("force-right-gutter") && a.length == 1)
						forceRightGutter = true;
					else
						LOG.warning(String.format("Invalid layout-data '%s' for %s", l, toString()));
				}
			}
		}
		dirtyLayout(false, LayoutType.boundsChange());
	}

	@SuppressWarnings("unchecked")
	void setSelectedIndex(int index, boolean temporary) {
		MenuItem<O> sel = getSelectedItem();
		MenuItem<O> newSel = index == -1 ? null : (MenuItem<O>) scroller.getScrollableArea().getElements().get(index);
		if (!Objects.equals(sel, newSel)) {
			if (sel != null)
				sel.setSelected(false);
			if (newSel != null && newSel.isSelectable() && newSel.isEnabled()) {
				newSel.setSelected(true);
				if (selectOnHighlight || !temporary)
					itemSelected(this, newSel, false, temporary);
			}
		}
	}

	void setSelectedItem(MenuItem<O> item, boolean temporary) {
		int idx = item == null ? -1 : scroller.getScrollableArea().getElements().indexOf(item);
		setSelectedIndex(idx, temporary);
	}

	private int indexOfItemElement(BaseElement element) {
		List<MenuItem<O>> menuItems = getMenuItems();
		for (int i = 0; i < menuItems.size(); i++) {
			if (Objects.equals(menuItems.get(i).getItemElement(), element)) {
				return i;
			}
		}
		return -1;
	}
}
