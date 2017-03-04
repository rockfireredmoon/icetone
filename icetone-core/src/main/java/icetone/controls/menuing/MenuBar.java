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

import java.util.Map;
import java.util.WeakHashMap;

import com.jme3.input.KeyInput;

import icetone.controls.buttons.Button;
import icetone.core.ElementManager;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;

/**
 * A menu bar is a (by default) a horizontal container made especially for
 * {@link Menu} controls. Use {@link #addMenu(Menu)}, and a button will be
 * created on the bar the opens the menu.
 *
 */
public class MenuBar extends Element {

	private Map<Menu<?>, Button> menuButtons = new WeakHashMap<>();
	private Menu<?> showing;

	public MenuBar() {
		this(BaseScreen.get());
	}

	public MenuBar(ElementManager<?> screen) {
		super(screen);
		setLayoutManager(new MigLayout(screen, "ins 0"));
	}

	/**
	 * Add a menu to the bar. It will not immediately be visible, instead a
	 * button is added to the bar which will open the menu when activated.
	 * 
	 * @param menu
	 *            menu
	 * @return this
	 */
	public MenuBar addMenu(Menu<?> menu) {
		menu.addStyleClass("menu-bar-menu");
		Button button = new Button(screen, menu.getTitle() == null ? "Menu" : menu.getTitle()) {
			{
				setStyleClass("menu-bar-button");
			}
		};
		button.onFocusGained((evt) -> {
			if (showing != null && showing != menu && showing.isVisible()) {
				screen.setKeyboardFocus(button);
				showing.hide();
				showing = menu;
				if (menu.getScreen() == null)
					screen.attachElement(menu);
				menu.showMenu(evt.getElement());
			}
		});
		button.onKeyboardPressed(evt -> {
			menu.handleMenuKeyEvent(evt);

			if (!evt.isConsumed()) {
				if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
					int idx = MenuBar.this.getElements().indexOf(button);
					if (idx + 1 < MenuBar.this.getElements().size()) {
						Button nextButton = (Button) MenuBar.this.getElements().get(idx + 1);
						Menu<?> nextMenu = menuForButton(nextButton);
						if (nextMenu != null) {
							if (showing != null)
								showing.hide();
							showing = nextMenu;
							nextButton.focus();
							nextMenu.showMenu(nextButton);
							evt.setConsumed();
						}

					}
				} else if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
					int idx = MenuBar.this.getElements().indexOf(button);
					if (idx > 0) {
						Button prevButton = (Button) MenuBar.this.getElements().get(idx - 1);
						Menu<?> prevMenu = menuForButton(prevButton);
						if (prevMenu != null) {
							if (showing != null)
								showing.hide();
							showing = prevMenu;
							prevButton.focus();
							prevMenu.showMenu(prevButton);
							evt.setConsumed();
						}
					}
				}
			}
		});
		button.onMouseReleased((evt) -> {
			showing = menu;
			if (menu.getScreen() == null)
				screen.attachElement(menu);
			menu.showMenu(evt.getElement());
		});
		menuButtons.put(menu, button);
		addElement(button);
		return this;
	}

	Menu<?> menuForButton(Button b) {
		for (Map.Entry<Menu<?>, Button> en : menuButtons.entrySet()) {
			if (b.equals(en.getValue()))
				return en.getKey();
		}
		return null;
	}

	Button buttonForMenu(Menu<?> menu) {
		return menuButtons.get(menu);
	}
}
