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
package icetone.controls.menuing;

import java.util.Objects;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.extras.Separator;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;
import icetone.core.PseudoStyles;
import icetone.core.Element;
import icetone.core.event.MouseUIFocusEvent.FocusEventType;
import icetone.css.CssProcessor.PseudoStyle;
import icetone.framework.core.AnimText;

/**
 * A single item in a {@link Menu}. Each item carries a 'value' whose type is
 * determined by the generic parameter. You may either set text for the item, or
 * another control.
 *
 * @param <O>
 *            type of value
 */
public class MenuItem<O> extends Element {
	protected static class ZMenuItemLayout<OO> extends AbstractGenericLayout<MenuItem<OO>, Object> {

		@Override
		public Vector2f preferredSize(MenuItem<OO> container) {
			Menu<OO> menu = container.getMenu();
			float lgw = 0, rgw = 0;
			if (menu != null) {
				lgw = menu.getLeftGutterWidth();
				if (lgw > 0)
					lgw += container.getIndent();
				rgw = menu.getRightGutterWidth();
				if (rgw > 0)
					rgw += container.getIndent();
			}
			return super.preferredSize(container).addLocal(lgw + rgw, 0);
		}

		@Override
		protected Vector4f calcTextOffset(MenuItem<OO> container, AnimText textElement, Vector4f textPadding) {
			Menu<OO> menu = container.getMenu();
			Vector4f to = super.calcTextOffset(container, textElement, textPadding);
			to = to.add(menu.getLeftGutterWidth(), menu.getRightGutterWidth(), 0, 0);
			return to;
		}

		@Override
		protected void onLayout(MenuItem<OO> item) {
			Vector4f pad = item.getAllPadding();
			if (item.itemElement != null) {
				Vector2f pref = item.itemElement.calcPreferredSize();
				if (item.itemElement instanceof Separator)
					item.itemElement.setBounds(pad.x, pad.z + (int) ((item.getHeight() - pref.y - pad.z - pad.w) / 2f),
							item.getWidth() - pad.x - pad.y, pref.y);
				else if (!(item.itemElement instanceof Menu))
					item.itemElement.setBounds(pad.x, pad.z + (int) ((item.getHeight() - pref.y - pad.z - pad.w) / 2f),
							pref.x, pref.y);
			}
			if (item.arrowElement != null) {
				Vector2f pref = item.arrowElement.calcPreferredSize();
				item.arrowElement.setBounds(item.getWidth() - pref.x - pad.y,
						pad.z + (int) ((item.getHeight() - pref.y - pad.z - pad.w) / 2f), pref.x, pref.y);
			}
		}

	}

	private O value;
	private BaseElement itemElement;
	private boolean selected;
	private BaseElement arrowElement;

	public MenuItem(ElementManager<?> screen, String caption, BaseElement itemElement, O value) {
		super(screen);

		setLayoutManager(new ZMenuItemLayout<O>());
		setItemElement(itemElement);
		setIgnoreTouch(false);
		setText(caption);
		this.value = value;

		onFocus(evt -> {
			if (evt.getEventType() == FocusEventType.gained) {
				showChildMenu(MenuItem.this.itemElement, evt.getY());
				getMenu().setSelectedItem(this, true);
			} else {
				Menu<O> parentMenu = getMenu();
				if (getMenu().isSelectOnHighlight()
						&& (parentMenu.childMenusItem == null || !parentMenu.childMenusItem.equals(this))) {
					parentMenu.setSelectedItem(null, true);
					evt.setConsumed();
				}
			}
		});

		onMouseReleased(evt -> {
			if (!(getItemElement() instanceof Menu)) {
				getMenu().itemSelected(getMenu(), this, getItemElement() == null, false);
				evt.setConsumed();
			}
		});
		addMouseButtonListener(evt -> {
			if (getItemElement() != null && getTextElement() != null) {
				getScreen().getEventCaster().fireMouseButtonEvent(getItemElement(), evt);
			}
		});

	}

	public void setItemElement(BaseElement itemElement) {
		if (!Objects.equals(itemElement, this.itemElement)) {
			if (this.itemElement != null) {
				if (this.itemElement instanceof Menu)
					removeElement(arrowElement);
				else
					removeElement(this.itemElement);
			}
			this.itemElement = itemElement;
			if (itemElement != null && !(itemElement instanceof Menu))
				addElement(itemElement);
			else if (itemElement instanceof Menu) {
				addElement(arrowElement = new Element(screen).setStyleClass("submenu-arrow"));
			}

		}
	}

	public BaseElement getItemElement() {
		return itemElement;
	}

	@SuppressWarnings("unchecked")
	protected void showChildMenu(BaseElement itemElement, float y) {
		Menu<O> parentMenu = getMenu();

		/* If already the shown menu, don't change anything */
		if (itemElement != null && itemElement instanceof Menu
				&& Objects.equals(parentMenu.showingChildMenu, itemElement)) {
			return;
		}

		if (parentMenu.showingChildMenu != null) {
			screen.removeElement(parentMenu.showingChildMenu);
			parentMenu.showingChildMenu.controlHideHook();
		}
		if (itemElement != null && itemElement instanceof Menu) {
			Menu<O> submenu = (Menu<O>) itemElement;
			getScreen().addElement(submenu);
			parentMenu.childMenusItem = this;
			parentMenu.showingChildMenu = submenu;
			boolean wasFocusable = submenu.isKeyboardFocusable();
			try {
				submenu.setKeyboardFocusable(false);
				if (parentMenu.direction == BitmapFont.Align.Right) {
					submenu.showMenu(getMenu(), getAbsoluteX() + getWidth() + getMenu().getChildMenuGap(),
							y - (getHeight() / 2f));
				} else {
					submenu.showMenu(getMenu(), getAbsoluteX() - getWidth() - getMenu().getChildMenuGap(),
							y - (getHeight() / 2f));
				}
			} finally {
				submenu.setKeyboardFocusable(wasFocusable);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Menu<O> getMenu() {
		return (Menu<O>) getParent(Menu.class);
	}

	public O getValue() {
		return value;
	}

	public MenuItem<O> setValue(O value) {
		this.value = value;
		return this;
	}

	public boolean isSelectable() {
		return isMouseFocusable();
	}

	public boolean isSelected() {
		return selected;
	}

	public MenuItem<O> setSelectable(boolean selectable) {
		setMouseFocusable(selectable);
		return this;
	}

	public MenuItem<O> setSelected(boolean selected) {
		if (selected != this.selected) {
			if (selected && !isMouseFocusable()) {
				throw new IllegalStateException("Item is not selectable.");
			}
			this.selected = selected;
			dirtyLayout(true, LayoutType.styling);
			layoutChildren();
		}
		return this;
	}

	@Override
	public PseudoStyles getPseudoStyles() {
		PseudoStyles pseudoStyles = super.getPseudoStyles();
		if (selected)
			pseudoStyles = PseudoStyles.get(pseudoStyles).addStyle(PseudoStyle.active);
		return pseudoStyles;
	}

}