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
package icetone.extras.controls;

import java.util.ArrayList;
import java.util.List;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.PushButton;
import icetone.controls.buttons.ToggleButton;
import icetone.controls.containers.Panel;
import icetone.core.BaseScreen;
import icetone.core.Size;

/**
 *
 * @author t0neg0d
 */
public class Keyboard extends Panel {
	private static enum KeyType {
		NUMERIC, ALPHA, SYMBOL, OTHER
	}

	private boolean shiftKey = false;
	private boolean symbolKey = false;

	private List<KeyboardKey> keys = new ArrayList<>();

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Keyboard(BaseScreen screen) {
		this(screen, null, new Vector2f(0, screen.getHeight() - (screen.getHeight() * 0.45f)),
				new Size(screen.getWidth(), (screen.getHeight() * 0.45f)));
	}

	public Keyboard(BaseScreen screen, Vector2f position, Size dimensions) {
		this(screen, null, position, dimensions);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param styleId
	 *            A unique id for styling and matching
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	private Keyboard(BaseScreen screen, String UID, Vector2f position, Size dimensions) {
		super(screen, UID, position, dimensions);

		setMovable(false);
		setResizable(false);
		setKeyboardFocusable(false);

		float nWidth = getWidth() * 0.1f - (110 * 0.1f);
		float nX = nWidth + 10;
		;
		float nHeight = getHeight() * 0.2f - (60 * 0.2f);
		float nY = nHeight + 10;
		float xGap = 10;

		KeyboardKey key = null;

		for (int r = 0; r < 2; r++) {
			for (int i = 0; i < 10; i++) {
				KeyType type = KeyType.valueOf(getString("R" + r + "K" + i + "KeyType"));
				String label = getString("R" + r + "K" + i + "DefaultLabel");
				String shiftlabel = getString("R" + r + "K" + i + "ShiftLabel");
				String symbollabel = getString("R" + r + "K" + i + "SymbolLabel");
				symbollabel = validateSymbol(symbollabel);

				key = new KeyboardKey(type, KeyInput.KEY_UNLABELED, label.charAt(0), label);
				key.setShift(type, KeyInput.KEY_UNLABELED, shiftlabel.charAt(0), shiftlabel);
				key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, symbollabel.charAt(0), symbollabel);
				key.setPosition(xGap + (nX * i), 10 + (nY * r));
				key.setDimensions(nWidth, nHeight);
				key.createButton();
				keys.add(key);
				addElement(key.getButton());
			}
		}

		// Row 3 - Alpha
		xGap = 10 + (nWidth * 0.5f);

		int r = 2;
		for (int i = 0; i < 9; i++) {
			KeyType type = KeyType.valueOf(getString("R" + r + "K" + i + "KeyType"));
			String label = getString("R" + r + "K" + i + "DefaultLabel");
			String shiftlabel = getString("R" + r + "K" + i + "ShiftLabel");
			String symbollabel = getString("R" + r + "K" + i + "SymbolLabel");
			symbollabel = validateSymbol(symbollabel);

			key = new KeyboardKey(type, KeyInput.KEY_UNLABELED, label.charAt(0), label);
			key.setShift(type, KeyInput.KEY_UNLABELED, shiftlabel.charAt(0), shiftlabel);
			key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, symbollabel.charAt(0), symbollabel);
			key.setPosition(xGap + (nX * i), 10 + (nY * r));
			key.setDimensions(nWidth, nHeight);
			key.createButton();
			keys.add(key);
			addElement(key.getButton());
		}

		r = 3;
		for (int i = 0; i < 7; i++) {
			KeyType type = KeyType.valueOf(getString("R" + r + "K" + i + "KeyType"));
			String label = getString("R" + r + "K" + i + "DefaultLabel");
			String shiftlabel = getString("R" + r + "K" + i + "ShiftLabel");
			String symbollabel = getString("R" + r + "K" + i + "SymbolLabel");
			symbollabel = validateSymbol(symbollabel);

			key = new KeyboardKey(type, KeyInput.KEY_UNLABELED, label.charAt(0), label);
			key.setShift(type, KeyInput.KEY_UNLABELED, shiftlabel.charAt(0), shiftlabel);
			key.setSymbol(KeyType.SYMBOL, KeyInput.KEY_UNLABELED, symbollabel.charAt(0), symbollabel);
			key.setPosition(xGap + (nX * (i + 1)), 10 + (nY * r));
			key.setDimensions(nWidth, nHeight);
			key.createButton();
			keys.add(key);
			addElement(key.getButton());
		}

		// Fixed Function Keys
		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_LSHIFT, '^', getString("ShiftLabel"));
		key.setPosition(10, 10 + (nY * 3));
		key.setDimensions(nWidth + (nWidth * 0.5f), nHeight);
		key.createShiftButton();
		keys.add(key);
		addElement(key.getButton());

		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_BACK, '^', getString("BackspaceLabel"));
		key.setPosition(xGap + (nX * 8), 10 + (nY * 3));
		key.setDimensions(nWidth + (nWidth * 0.5f), nHeight);
		key.createBackButton();
		keys.add(key);
		addElement(key.getButton());

		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_UNLABELED, '^', getString("SymbolLabel"));
		key.setPosition(10, 10 + (nY * 4));
		key.setDimensions(nWidth + (nWidth * 0.5f), nHeight);
		key.createSymbolButton();
		keys.add(key);
		addElement(key.getButton());

		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_SPACE, ' ', getString("SpacebarLabel"));
		key.setPosition(xGap + (nX), 10 + (nY * 4));
		key.setDimensions(nWidth + (nX * 4), nHeight);
		key.createSpaceButton();
		keys.add(key);
		addElement(key.getButton());

		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_UNLABELED, ',', ",");
		key.setPosition(xGap + (nX * 6), 10 + (nY * 4));
		key.setDimensions(nWidth, nHeight);
		key.createButton();
		keys.add(key);
		addElement(key.getButton());

		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_UNLABELED, '.', ".");
		key.setPosition(xGap + (nX * 7), 10 + (nY * 4));
		key.setDimensions(nWidth, nHeight);
		key.createButton();
		keys.add(key);
		addElement(key.getButton());

		key = new KeyboardKey(KeyType.OTHER, KeyInput.KEY_RETURN, '^', getString("EnterLabel"));
		key.setPosition(xGap + (nX * 8), 10 + (nY * 4));
		key.setDimensions(nWidth + (nWidth * 0.5f), nHeight);
		key.createEnterButton();
		keys.add(key);
		addElement(key.getButton());
	}

	private String getString(String string) {
		return string;
	}

	public boolean isShiftKey() {
		return shiftKey;
	}

	public boolean isSymbolKey() {
		return symbolKey;
	}

	public void setUseIcons(boolean useIcons) {
		if (useIcons) {
			KeyboardKey shift = getFunctionKey(KeyInput.KEY_LSHIFT);
			shift.getButton().getButtonIcon().addStyleClass("icon-shift");
			shift.getButton().setText("");
			KeyboardKey ret = getFunctionKey(KeyInput.KEY_RETURN);
			shift.getButton().getButtonIcon().addStyleClass("icon-return");
			ret.getButton().setText("");
			KeyboardKey bs = getFunctionKey(KeyInput.KEY_BACK);
			shift.getButton().getButtonIcon().addStyleClass("icon-backspace");
			bs.getButton().setText("");
			KeyboardKey space = getFunctionKey(KeyInput.KEY_SPACE);
			shift.getButton().getButtonIcon().addStyleClass("icon-space");
			space.getButton().setText("");
		} else {
			KeyboardKey shift = getFunctionKey(KeyInput.KEY_LSHIFT);
			shift.getButton().getButtonIcon().removeStyleClass("icon-shift");
			shift.getButton().setText(shift.label);
			KeyboardKey ret = getFunctionKey(KeyInput.KEY_RETURN);
			shift.getButton().getButtonIcon().removeStyleClass("icon-return");
			ret.getButton().setText(ret.label);
			KeyboardKey bs = getFunctionKey(KeyInput.KEY_BACK);
			shift.getButton().getButtonIcon().removeStyleClass("icon-backspace");
			bs.getButton().setText(bs.label);
			KeyboardKey space = getFunctionKey(KeyInput.KEY_SPACE);
			shift.getButton().getButtonIcon().removeStyleClass("icon-space");
			space.getButton().setText(space.label);
		}
	}

	private KeyboardKey getFunctionKey(int keyCode) {
		KeyboardKey ret = null;
		for (KeyboardKey xKey : keys) {
			if (xKey.getKeyCode() == keyCode) {
				ret = xKey;
				break;
			}
		}
		return ret;
	}

	private String validateSymbol(String symbol) {
		if (symbol.equals("amp"))
			symbol = "&";
		else if (symbol.equals("lt"))
			symbol = "<";
		else if (symbol.equals("gt"))
			symbol = ">";
		else if (symbol.equals("bslash"))
			symbol = "\\";
		else if (symbol.equals("quot"))
			symbol = "\"";
		return symbol;
	}

	public void setGlobalShift(boolean shift) {
		this.shiftKey = shift;
		if (!symbolKey) {
			for (KeyboardKey key : keys) {
				if (key.getKeyType() == KeyType.ALPHA)
					key.setShift(shift);
			}
		}
	}

	public void setGlobalSymbol(boolean symbol) {
		this.symbolKey = symbol;
		for (KeyboardKey key : keys) {
			if (key.getKeyType() == KeyType.ALPHA || key.getKeyType() == KeyType.NUMERIC)
				key.setSymbol(symbol);
		}
	}

	private class KeyboardKey {
		KeyType type, shiftType, symbolType;
		int keyCode, shiftKeyCode, symbolKeyCode;
		char character, shiftCharacter, symbolCharacter;
		String label, shiftLabel, symbolLabel;
		float x, y, w, h;
		boolean shift = false;
		boolean symbol = false;
		ToggleButton button;

		public KeyboardKey(KeyType type, int code, char character, String label) {
			this.type = type;
			this.keyCode = code;
			this.character = character;
			this.label = label;
		}

		public void setShift(KeyType type, int code, char character, String label) {
			this.shiftType = type;
			this.shiftKeyCode = code;
			this.shiftCharacter = character;
			this.shiftLabel = label;
		}

		public void setSymbol(KeyType type, int code, char character, String label) {
			this.symbolType = type;
			this.symbolKeyCode = code;
			this.symbolCharacter = character;
			this.symbolLabel = label;
		}

		public void setPosition(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public void setDimensions(float w, float h) {
			this.w = w;
			this.h = h;
		}

		public KeyType getKeyType() {
			return type;
		}

		public int getKeyCode() {
			return keyCode;
		}

		public char getCharacter() {
			return character;
		}

		public String getLabel() {
			return label;
		}

		public PushButton createButton() {
			PushButton button = new PushButton(screen) {

				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					if (symbol) {
						nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, true, false);
					} else if (shift) {
						nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, true, false);
					} else {
						nEvt = new KeyInputEvent(keyCode, character, true, false);
					}
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.onMousePressed(evt -> {
				KeyInputEvent nEvt = null;
				if (symbol) {
					nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, true, false);
				} else if (shift) {
					nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, true, false);
				} else {
					nEvt = new KeyInputEvent(keyCode, character, true, false);
				}
				nEvt.setTime(System.currentTimeMillis());
				screen.onKeyEvent(nEvt);
			});
			button.onMouseReleased(evt -> {
				KeyInputEvent nEvt = null;
				if (symbol) {
					nEvt = new KeyInputEvent(symbolKeyCode, symbolCharacter, false, false);
				} else if (shift) {
					nEvt = new KeyInputEvent(shiftKeyCode, shiftCharacter, false, false);
				} else {
					nEvt = new KeyInputEvent(keyCode, character, false, false);
				}
				nEvt.setTime(System.currentTimeMillis());
				screen.onKeyEvent(nEvt);
			});
			button.setText(label);
			button.setKeyboardFocusable(false);
			return button;
		}

		public Button createShiftButton() {
			ToggleButton button = new ToggleButton(screen);
			button.onChange(evt -> setGlobalShift(evt.getNewValue()));
			button.setText(label);
			button.setKeyboardFocusable(false);
			return button;
		}

		public Button createSymbolButton() {
			ToggleButton button = new ToggleButton(screen);
			button.onChange(evt -> setGlobalSymbol(evt.getNewValue()));
			button.setText(label);
			button.setKeyboardFocusable(false);
			return button;
		}

		public PushButton createBackButton() {
			PushButton button = new PushButton(screen) {
				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_BACK, symbolCharacter, true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.onMousePressed(evt -> {
				KeyInputEvent nEvt = null;
				nEvt = new KeyInputEvent(KeyInput.KEY_BACK, symbolCharacter, true, false);
				nEvt.setTime(System.currentTimeMillis());
				screen.onKeyEvent(nEvt);
			});
			button.onMouseReleased(evt -> {
				KeyInputEvent nEvt = null;
				nEvt = new KeyInputEvent(KeyInput.KEY_BACK, symbolCharacter, false, false);
				nEvt.setTime(System.currentTimeMillis());
				screen.onKeyEvent(nEvt);
			});
			button.setText(label);
			button.setKeyboardFocusable(false);
			button.setInterval(15);
			return button;
		}

		public Button createSpaceButton() {
			PushButton button = new PushButton(screen) {

				@Override
				public void onButtonStillPressedInterval() {
					KeyInputEvent nEvt = null;
					nEvt = new KeyInputEvent(KeyInput.KEY_S, ' ', true, false);
					nEvt.setTime(System.currentTimeMillis());
					screen.onKeyEvent(nEvt);
				}
			};
			button.onMousePressed(evt -> {
				KeyInputEvent nEvt = null;
				nEvt = new KeyInputEvent(KeyInput.KEY_S, ' ', true, false);
				nEvt.setTime(System.currentTimeMillis());
				screen.onKeyEvent(nEvt);
			});
			button.onMouseReleased(evt -> {
				KeyInputEvent nEvt = null;
				nEvt = new KeyInputEvent(KeyInput.KEY_S, ' ', false, false);
				nEvt.setTime(System.currentTimeMillis());
				screen.onKeyEvent(nEvt);
			});
			button.setText(label);
			button.setKeyboardFocusable(false);
			// button.setInterval(15);
			return button;
		}

		public Button createEnterButton() {
			PushButton button = new PushButton(screen);
			button.onMouseReleased(evt -> Keyboard.this.hide());
			button.setText(label);
			button.setKeyboardFocusable(false);
			return button;
		}

		public Button getButton() {
			return this.button;
		}

		public void setShift(boolean shift) {
			this.shift = shift;
			if (shift)
				button.setText(shiftLabel);
			else
				button.setText(label);
		}

		public void setSymbol(boolean symbol) {
			this.symbol = symbol;
			if (symbol)
				button.setText(symbolLabel);
			else {
				if (shift)
					button.setText(shiftLabel);
				else
					button.setText(label);
			}
		}
	}
}
