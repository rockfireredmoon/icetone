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

package icetone.controls.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;
import icetone.core.BaseScreen;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.ToolKit;
import icetone.core.event.ChangeSupport;
import icetone.core.event.KeyboardUIEvent;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.utils.ClassUtil;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class TextField extends Element implements Control, TextInput {

	public static enum Type {
		DEFAULT, ALPHA, ALPHA_NOSPACE, NUMERIC, ALPHANUMERIC, ALPHANUMERIC_NOSPACE, EXCLUDE_SPECIAL, EXCLUDE_CUSTOM, INCLUDE_CUSTOM
	};

	private String validateAlpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
	private String validateAlphaNoSpace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String validateNumeric = "0123456789.-";
	private String validateSpecChar = "`~!@#$%^&*()-_=+[]{}\\|;:'\",<.>/?";
	private String validateCustom = "";
	private BaseElement caret;
	private BaseElement overlay;
	private Material caretMat;
	protected int caretIndex = 0, head = 0, tail = 0;
	protected int rangeHead = -1, rangeTail = -1;
	protected int visibleHead = -1, visibleTail = -1;
	protected List<String> textModel = new ArrayList<>();
	protected String finalText = "", visibleText = "", textRangeText = "";
	protected BitmapText widthTest;
	protected float caretX = 0;
	private Type type = Type.DEFAULT;
	private boolean isEnabled = true;
	private boolean forceUpperCase = false, forceLowerCase = false;
	private int maxLength = 0;
	private String nextChar;
	private boolean valid;
	private boolean copy = true, paste = true;
	private boolean isPressed = false;
	protected boolean editable = true;
	private int characterLength;
	private ChangeSupport<TextField, String> changeSupport;

	public TextField() {
		this("");
	}

	public TextField(String text) {
		super(BaseScreen.get());
		setText(text);
	}

	public TextField(String text, ElementManager<?> screen) {
		super(screen);
		setText(text);
	}

	public TextField(ElementManager<?> screen, String styleId) {
		super(screen, styleId);
		setText("");
	}

	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public TextField(ElementManager<?> screen) {
		super(screen);
		setText("");
	}

	@Override
	protected void configureStyledElement() {

		layoutManager = new TextFieldLayout();

		/* Caret */
		caret = new Element(screen) {
			{
				styleClass = "caret";
			}
		};

		caretMat = caret.getMaterial().clone();
		caretMat.setBoolean("IsTextField", true);
		caretMat.setTexture("ColorMap", null);
		caretMat.setColor("Color", getFontColor());

		caret.setLocalMaterial(caretMat);
		caret.setIgnoreMouse(true);
		addElement(caret);

		/* Overlay */
		overlay = new StyledContainer(screen) {
			{
				styleClass = "overlay";
				useParentPseudoStyles = true;
			}
		};
		overlay.setIgnoreMouse(true);
		addElement(overlay);

		setMouseFocusable(true);
		onMousePressed(evt -> {
			if (this.isEnabled) {

				isPressed = true;

				switch (evt.getClicks()) {
				case 1:
					resetTextRange();
					setCaretPositionByXNoRange(evt.getX());
					if (caretIndex >= 0)
						setTextRangeStart(caretIndex);
					else
						setTextRangeStart(0);
					break;
				}
			}
		});

		onMouseReleased(evt -> {
			if (isEnabled) {
				if (isPressed) {
					isPressed = false;
					if (evt.getClicks() == 2) {
						selectTextRangeDoubleClick();
					} else if (evt.getClicks() == 3) {
						selectTextRangeTripleClick();
					} else {
						setCaretPositionByXNoRange(evt.getX());
						if (caretIndex >= 0)
							setTextRangeEnd(caretIndex);
						else
							setTextRangeEnd(0);
					}
				}
			}
		});

		onKeyboardFocusGained(evt -> {
			getVisibleText();
			setTextRangeStart(caretIndex);
			if (isEnabled) {
				caret.getMaterial().setFloat("LastUpdate",
						ToolKit.get().getApplication().getTimer().getTimeInSeconds());
				caret.getMaterial().setBoolean("HasTabFocus", true);
			}

			if (isEnabled && !this.controls.contains(this)) {
				addControl(this);
			}
		});

		onKeyboardFocusLost(evt -> {
			caret.getMaterial().setFloat("LastUpdate", ToolKit.get().getApplication().getTimer().getTimeInSeconds());
			caret.getMaterial().setBoolean("HasTabFocus", false);
			if (isEnabled && this.controls.contains(this)) {
				removeControl(this);
			}
		});

		onKeyboard(evt -> {
			if (evt.isPressed())
				onKeyPress(evt);
			else
				onKeyRelease(evt);
		});

		setKeyboardFocusable(true);
	}

	public TextField addChangeListener(UIChangeListener<TextField, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public TextField onChange(UIChangeListener<TextField, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public TextField removeChangeListener(UIChangeListener<TextField, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
	}

	public BaseElement getOverlay() {
		return overlay;
	}

	public void setCaretPositionToStart() {
		caretIndex = 0;
		head = 0;
		tail = 0;
		setCaretPosition(0);
	}

	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add((isEditable() ? "Editable" : "") + ClassUtil.getMainClassName(getClass()));
		return l;
	}

	// Validation
	/**
	 * Sets the TextField.Type of the text field. This can be used to enfoce
	 * rules on the inputted text
	 * 
	 * @param type
	 *            Type
	 */
	public TextField setType(Type type) {
		this.type = type;
		return this;
	}

	/**
	 * Returns the current Type of the TextField
	 * 
	 * @return Type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Sets a custom validation rule for the TextField.
	 * 
	 * @param grabBag
	 *            String A list of character to either allow or diallow as input
	 */
	public TextField setCustomValidation(String grabBag) {
		validateCustom = grabBag;
		return this;
	}

	/**
	 * Attempts to parse an int from the inputted text of the TextField
	 * 
	 * @return int
	 * @throws NumberFormatException
	 */
	public int parseInt() throws NumberFormatException {
		return Integer.parseInt(getText());
	}

	/**
	 * Attempts to parse a float from the inputted text of the TextField
	 * 
	 * @return float
	 * @throws NumberFormatException
	 */
	public float parseFloat() throws NumberFormatException {
		return Float.parseFloat(getText());
	}

	/**
	 * Attempts to parse a short from the inputted text of the TextField
	 * 
	 * @return short
	 * @throws NumberFormatException
	 */
	public short parseShort() throws NumberFormatException {
		return Short.parseShort(getText());
	}

	/**
	 * Attempts to parse a double from the inputted text of the TextField
	 * 
	 * @return double
	 * @throws NumberFormatException
	 */
	public double parseDouble() throws NumberFormatException {
		return Double.parseDouble(getText());
	}

	/**
	 * Attempts to parse a long from the inputted text of the TextField
	 * 
	 * @return long
	 * @throws NumberFormatException
	 */
	public long parseLong() throws NumberFormatException {
		return Long.parseLong(getText());
	}

	public void onKeyPress(KeyboardUIEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_F1 || evt.getKeyCode() == KeyInput.KEY_F2
				|| evt.getKeyCode() == KeyInput.KEY_F3 || evt.getKeyCode() == KeyInput.KEY_F4
				|| evt.getKeyCode() == KeyInput.KEY_F5 || evt.getKeyCode() == KeyInput.KEY_F6
				|| evt.getKeyCode() == KeyInput.KEY_F7 || evt.getKeyCode() == KeyInput.KEY_F8
				|| evt.getKeyCode() == KeyInput.KEY_F9 || evt.getKeyCode() == KeyInput.KEY_CAPITAL
				|| evt.getKeyCode() == KeyInput.KEY_ESCAPE || evt.getKeyCode() == KeyInput.KEY_TAB) {
		} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
		} else if (evt.getKeyCode() == KeyInput.KEY_DELETE && editable) {
			if (rangeHead != -1 && rangeTail != -1)
				editTextRangeText("");
			else {
				if (caretIndex < finalText.length()) {
					textModel.remove(caretIndex);
					updateText();
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_BACK && editable) {
			if (rangeHead != -1 && rangeTail != -1) {
				editTextRangeText("");
			} else {
				if (caretIndex > 0) {
					textModel.remove(caretIndex - 1);
					caretIndex--;
					updateText();
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
			if (!evt.isShift())
				resetTextRange();
			if (caretIndex > -1) {
				if (ToolKit.isMac()) {
					if (evt.isMeta()) {
						caretIndex = 0;
						getVisibleText();
						if (evt.isShift())
							setTextRangeEnd(caretIndex);
						else {
							resetTextRange();
							setTextRangeStart(caretIndex);
						}
						return;
					}
				}

				if ((ToolKit.isMac() && !evt.isAlt()) || (ToolKit.isWindows() && !evt.isCtrl())
						|| (ToolKit.isUnix() && !evt.isCtrl()) || (ToolKit.isSolaris() && !evt.isCtrl()))
					caretIndex--;
				else {
					int cIndex = caretIndex;
					if (cIndex > 0)
						if (finalText.charAt(cIndex - 1) == ' ')
							cIndex--;
					int index = 0;
					if (cIndex > 0)
						index = finalText.substring(0, cIndex).lastIndexOf(' ') + 1;
					if (index < 0)
						index = 0;
					caretIndex = index;
				}
				if (caretIndex < 0)
					caretIndex = 0;

				if (!evt.isShift())
					setTextRangeStart(caretIndex);
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
			if (!evt.isShift())
				resetTextRange();
			if (caretIndex <= textModel.size()) {
				if (ToolKit.isMac()) {
					if (evt.isMeta()) {
						caretIndex = textModel.size();
						getVisibleText();
						if (evt.isShift())
							setTextRangeEnd(caretIndex);
						else {
							resetTextRange();
							setTextRangeStart(caretIndex);
						}
						return;
					}
				}

				if ((ToolKit.isMac() && !evt.isAlt()) || (ToolKit.isWindows() && !evt.isCtrl())
						|| (ToolKit.isUnix() && !evt.isCtrl()) || (ToolKit.isSolaris() && !evt.isCtrl()))
					caretIndex++;
				else {
					int cIndex = caretIndex;
					if (cIndex < finalText.length())
						if (finalText.charAt(cIndex) == ' ')
							cIndex++;
					int index;
					if (cIndex < finalText.length()) {
						index = finalText.substring(cIndex, finalText.length()).indexOf(' ');
						if (index == -1)
							index = finalText.length();
						else
							index += cIndex;
					} else {
						index = finalText.length();
					}
					caretIndex = index;
				}
				if (caretIndex > finalText.length())
					caretIndex = finalText.length();

				if (!evt.isShift()) {
					if (caretIndex < textModel.size())
						setTextRangeStart(caretIndex);
					else
						setTextRangeStart(textModel.size());
				}
			}
			evt.setConsumed();
		} else if ((evt.getKeyCode() == KeyInput.KEY_END || evt.getKeyCode() == KeyInput.KEY_NEXT
				|| evt.getKeyCode() == KeyInput.KEY_DOWN)) {
			caretIndex = textModel.size();
			getVisibleText();
			if (evt.isShift())
				setTextRangeEnd(caretIndex);
			else {
				resetTextRange();
				setTextRangeStart(caretIndex);
			}
			evt.setConsumed();
		} else if ((evt.getKeyCode() == KeyInput.KEY_HOME || evt.getKeyCode() == KeyInput.KEY_PRIOR
				|| evt.getKeyCode() == KeyInput.KEY_UP)) {
			caretIndex = 0;
			getVisibleText();
			if (evt.isShift())
				setTextRangeEnd(caretIndex);
			else {
				resetTextRange();
				setTextRangeStart(caretIndex);
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_A && evt.isCtrl()) {
			caretIndex = 0;
			getVisibleText();
			selectTextRangeAll();
			evt.setConsumed();
		} else {
			if (evt.isCtrl()) {
				if (evt.getKeyCode() == KeyInput.KEY_C) {
					if (copy)
						ToolKit.get().setClipboardText(textRangeText);
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_V && editable) {
					if (paste)
						this.pasteTextInto();
					evt.setConsumed();
				}
			} else {
				if (isEnabled && editable && evt.getKeyChar() > 0) {
					if (rangeHead != -1 && rangeTail != -1) {
						editTextRangeText("");
					}
					if (!evt.isShift())
						resetTextRange();
					nextChar = String.valueOf(evt.getKeyChar());
					if (forceUpperCase)
						nextChar = nextChar.toUpperCase();
					else if (forceLowerCase)
						nextChar = nextChar.toLowerCase();
					valid = true;
					if (maxLength > 0) {
						if (getText().length() >= maxLength)
							valid = false;
					}
					if (valid) {
						if (type == Type.DEFAULT) {
							textModel.add(caretIndex, nextChar);
							caretIndex++;
							updateText();
						} else if (type == Type.ALPHA) {
							if (validateAlpha.indexOf(nextChar) != -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						} else if (type == Type.ALPHA_NOSPACE) {
							if (validateAlpha.indexOf(nextChar) != -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						} else if (type == Type.NUMERIC) {
							if (validateNumeric.indexOf(nextChar) != -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						} else if (type == Type.ALPHANUMERIC) {
							if (validateAlpha.indexOf(nextChar) != -1 || validateNumeric.indexOf(nextChar) != -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						} else if (type == Type.ALPHANUMERIC_NOSPACE) {
							if (validateAlphaNoSpace.indexOf(nextChar) != -1
									|| validateNumeric.indexOf(nextChar) != -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						} else if (type == Type.EXCLUDE_SPECIAL) {
							if (validateSpecChar.indexOf(nextChar) == -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						} else if (type == Type.EXCLUDE_CUSTOM) {
							if (validateCustom.indexOf(nextChar) == -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						} else if (type == Type.INCLUDE_CUSTOM) {
							if (validateCustom.indexOf(nextChar) != -1) {
								textModel.add(caretIndex, nextChar);
								caretIndex++;
								updateText();
							}
						}
					}
					if (!evt.isShift()) {
						if (caretIndex < textModel.size())
							setTextRangeStart(caretIndex);
						else
							setTextRangeStart(textModel.size());
					}
					evt.setConsumed();
				}
			}
		}

		if (evt.isShift() && (evt.getKeyCode() == KeyInput.KEY_LEFT || evt.getKeyCode() == KeyInput.KEY_RIGHT)) {
			setTextRangeEnd(caretIndex);
			evt.setConsumed();
		}

		// centerTextVertically();
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();

	}

	public void onKeyRelease(KeyInputEvent evt) {
		switch (evt.getKeyCode()) {
		case KeyInput.KEY_ESCAPE:
		case KeyInput.KEY_RETURN:
		case KeyInput.KEY_TAB:
			break;
		default:
			evt.setConsumed();
			break;
		}
	}

	/**
	 * Internal use - NEVER USE THIS!!
	 */
	protected void getTextFieldText() {
		String ret = "";
		for (String s : textModel) {
			ret += s;
		}
		finalText = ret;
	}

	@Override
	public BaseElement setText(String s) {
		caretIndex = 0;
		textModel.clear();
		if (s != null) {
			for (int i = 0; i < s.length(); i++) {
				textModel.add(caretIndex, String.valueOf(s.charAt(i)));
				caretIndex++;
			}
		}
		super.setText(s);
		return this;
	}

	@Override
	public String formatText(String text) {
		return super.formatText(getVisibleText());
	}

	protected void updateText() {
		String text = String.join("", textModel);
		if (!Objects.equals(text, this.text)) {
			String was = this.text;
			super.setText(text);
			if (changeSupport != null)
				changeSupport.fireEvent(new UIChangeEvent<TextField, String>(this, was, text));
		}
	}

	/**
	 * Returns the visible portion of the TextField's text
	 * 
	 * @return String
	 */
	protected String getVisibleText() {
		getTextFieldText();

		widthTest = new BitmapText(calcFont(), false);
		widthTest.setBox(null);
		widthTest.setSize(calcFontSize());

		int index1 = 0, index2;

		widthTest.setText(finalText);
		if (head == -1 || tail == -1 || widthTest.getLineWidth() < getWidth()) {
			head = 0;
			tail = finalText.length();
			if (head != tail && head != -1 && tail != -1)
				visibleText = finalText.substring(head, tail);
			else
				visibleText = "";
		} else {
			Vector4f padding = getAllPadding();
			if (caretIndex < head) {
				head = caretIndex;
				index2 = caretIndex;
				if (index2 == caretIndex && caretIndex != textModel.size()) {
					index2 = caretIndex + 1;
					widthTest.setText(finalText.substring(caretIndex, index2));
					while (widthTest.getLineWidth() < getWidth() - (padding.x + padding.y)) {
						if (index2 == textModel.size())
							break;
						widthTest.setText(finalText.substring(caretIndex, index2 + 1));
						if (widthTest.getLineWidth() < getWidth() - (padding.x + padding.y)) {
							index2++;
						}
					}
				}
				if (index2 != textModel.size())
					index2++;
				tail = index2;
				if (head != tail && head != -1 && tail != -1)
					visibleText = finalText.substring(head, tail);
				else
					visibleText = "";
			} else if (caretIndex > tail) {
				tail = caretIndex;
				index2 = caretIndex;
				if (index2 == caretIndex && caretIndex != 0) {
					index2 = caretIndex - 1;
					widthTest.setText(finalText.substring(index2, caretIndex));
					while (widthTest.getLineWidth() < getWidth() - (padding.x + padding.y)) {
						if (index2 == 0)
							break;
						widthTest.setText(finalText.substring(index2 - 1, caretIndex));
						if (widthTest.getLineWidth() < getWidth() - (padding.x + padding.y)) {
							index2--;
						}
					}
				}
				head = index2;
				if (head != tail && head != -1 && tail != -1)
					visibleText = finalText.substring(head, caretIndex);
				else
					visibleText = "";
			} else {
				index2 = tail;
				if (index2 > finalText.length())
					index2 = finalText.length();
				if (tail != head) {
					widthTest.setText(finalText.substring(head, index2));
					if (widthTest.getLineWidth() > getWidth() - (padding.x + padding.y)) {
						while (widthTest.getLineWidth() > getWidth() - (padding.x + padding.y)) {
							if (index2 == head)
								break;
							widthTest.setText(finalText.substring(head, index2 - 1));
							if (widthTest.getLineWidth() > getWidth() - (padding.x + padding.y)) {
								index2--;
							}
						}
					} else if (widthTest.getLineWidth() < getWidth() - (padding.x + padding.y)) {
						while (widthTest.getLineWidth() < getWidth() - (padding.x + padding.y)
								&& index2 < finalText.length()) {
							if (index2 == head)
								break;
							widthTest.setText(finalText.substring(head, index2 + 1));
							if (widthTest.getLineWidth() < getWidth() - (padding.x + padding.y)) {
								index2++;
							}

						}
					}
				}
				tail = index2;
				if (head != tail && head != -1 && tail != -1)
					visibleText = finalText.substring(head, tail);
				else
					visibleText = "";
			}
		}

		String testString = "";
		widthTest.setText(".");
		float fixWidth = widthTest.getLineWidth();
		boolean useFix = false;

		if (!finalText.equals("")) {
			try {
				testString = finalText.substring(head, caretIndex);
				if (testString.charAt(testString.length() - 1) == ' ') {
					testString += ".";
					useFix = true;
				}
			} catch (Exception ex) {
			}
		}

		widthTest.setText(testString);
		float nextCaretX = widthTest.getLineWidth();
		if (useFix)
			nextCaretX -= fixWidth;

		caretX = nextCaretX;
		setCaretPosition(caretX);

		return visibleText;
	}

	@Override
	protected void onKeyboardOrMouseFocusChanged() {
		/*
		 * TODO only do this if any children are using parent pseudo styles.
		 * Button does the same thing
		 */
		dirtyLayout(true, LayoutType.styling);
	}

	/**
	 * For internal use - do not call this method
	 * 
	 * @param caretX
	 *            float
	 */
	protected void setCaretPosition(float caretX) {
		if (textElement != null) {
			if (isKeyboardFocussed()) {
				caret.getMaterial().setFloat("CaretX", caretX + getAllPadding().x + getAbsoluteX() + getCaretOffset());
				caret.getMaterial().setFloat("LastUpdate",
						ToolKit.get().getApplication().getTimer().getTimeInSeconds());
			}
		}
	}

	private void setCaretPositionByXNoRange(float x) {
		int index1 = visibleText.length();
		if (visibleText.length() > 0) {
			String testString = "";
			widthTest.setText(".");
			float fixWidth = widthTest.getLineWidth();
			boolean useFix = false;

			widthTest.setSize(calcFontSize());
			widthTest.setText(visibleText.substring(0, index1));
			while (caret.getAbsoluteX() + widthTest.getLineWidth() > (x + getAllPadding().x)) {
				if (index1 > 0) {
					index1--;
					testString = visibleText.substring(0, index1);
					widthTest.setText(testString);
				} else {
					break;
				}
			}

			try {
				testString = finalText.substring(head, caretIndex);
				if (testString.charAt(testString.length() - 1) == ' ') {
					testString += ".";
					useFix = true;
				}
			} catch (Exception ex) {
			}

			widthTest.setText(testString);
			float nextCaretX = widthTest.getLineWidth();
			if (useFix)
				nextCaretX -= fixWidth;

			caretX = nextCaretX;
		}
		caretIndex = head + index1;
		setCaretPosition(caretX);
	}

	/**
	 * Sets the caret position to the end of the TextField's text
	 */
	public TextField setCaretPositionToEnd() {
		int index1 = visibleText.length();
		if (visibleText.length() > 0) {
			widthTest.setText(visibleText.substring(0, index1));
			caretX = widthTest.getLineWidth();
		}
		caretIndex = head + index1;
		setCaretPosition(caretX);
		resetTextRange();
		return this;
	}

	private void pasteTextInto() {
		editTextRangeText(ToolKit.get().getClipboardText());
	}

	/**
	 * Get the caret element
	 * 
	 * @return caret
	 */
	@Override
	public BaseElement getCaret() {
		return caret;
	}

	/**
	 * Enables/disables the use of the Copy text feature
	 * 
	 * @param copy
	 *            boolean
	 */
	public TextField setAllowCopy(boolean copy) {
		this.copy = copy;
		return this;
	}

	/**
	 * Returns if the Copy feature is enabled/disabled
	 * 
	 * @return copy
	 */
	public boolean isAllowCopy() {
		return this.copy;
	}

	/**
	 * Eanbles/disables use of the Paste text feature
	 * 
	 * @param paste
	 *            boolean
	 */
	public TextField setAllowPaste(boolean paste) {
		this.paste = paste;
		return this;
	}

	/**
	 * Returns if the Paste feature is enabled/disabled
	 * 
	 * @return paste
	 */
	public boolean isAllowPaste() {
		return this.paste;
	}

	/**
	 * Enables/disables both the Copy and Paste feature
	 * 
	 * @param copyAndPaste
	 *            boolean
	 */
	public TextField setAllowCopyAndPaste(boolean copyAndPaste) {
		this.copy = copyAndPaste;
		this.paste = copyAndPaste;
		return this;
	}

	/**
	 * Forces all text input to uppercase
	 * 
	 * @param forceUpperCase
	 *            boolean
	 */
	public TextField setForceUpperCase(boolean forceUpperCase) {
		this.forceUpperCase = forceUpperCase;
		this.forceLowerCase = false;
		return this;
	}

	/**
	 * Returns if the TextField is set to force uppercase
	 * 
	 * @return boolean
	 */
	public boolean getForceUpperCase() {
		return this.forceUpperCase;
	}

	/**
	 * Forces all text input to lowercase
	 * 
	 * @return boolean
	 */
	public TextField setForceLowerCase(boolean forceLowerCase) {
		this.forceLowerCase = forceLowerCase;
		this.forceUpperCase = false;
		return this;
	}

	/**
	 * Returns if the TextField is set to force lowercase
	 * 
	 * @return boolean
	 */
	public boolean getForceLowerCase() {
		return this.forceLowerCase;
	}

	/**
	 * Get the preferred width in characters.
	 * 
	 * @param characterLength
	 */
	@Override
	public int getCharacterWidth() {
		return characterLength;
	}

	/**
	 * Set the preferred width in characters.
	 * 
	 * @param characterLength
	 */
	public TextField setCharacterLength(int length) {
		this.characterLength = length;
		return this;
	}

	/**
	 * Set the maximum character limit for the TextField. 0 = unlimited
	 * 
	 * @param maxLength
	 *            int
	 */
	public TextField setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		return this;
	}

	/**
	 * Returns the maximum limit of character allowed for this TextField
	 * 
	 * @return int
	 */
	@Override
	public int getMaxLength() {
		return this.maxLength;
	}

	public int getLength() {
		return getText() == null ? 0 : getText().length();
	}

	public boolean isEditable() {
		return editable;
	}

	public TextField setEditable(boolean editable) {
		this.editable = editable;
		if (caret != null)
			caret.setVisible(editable);
		return this;
	}

	@Override
	protected final void onBeforeLayout() {
		onBeforeTextLayout();
	}

	@Override
	protected final void onAfterStyledLayout() {
		onAfterTextLayout();
	}

	protected void onBeforeTextLayout() {
	}

	protected void onAfterTextLayout() {
	}

	private void stillPressedInterval() {
		if (screen.getMouseXY().x > getAbsoluteWidth() && caretIndex < finalText.length())
			caretIndex++;
		else if (screen.getMouseXY().x < getAbsoluteX() && caretIndex > 0)
			caretIndex--;
		setText(getVisibleText());
		setCaretPositionByXNoRange(screen.getMouseXY().x);
		if (caretIndex >= 0)
			this.setTextRangeEnd(caretIndex);
		else
			this.setTextRangeEnd(0);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	// Text Range methods
	/**
	 * Sets the current text range to all text within the TextField
	 */
	public TextField selectTextRangeAll() {
		setTextRangeStart(0);
		setTextRangeEnd(finalText.length());
		caretIndex = finalText.length();
		getVisibleText();
		return this;
	}

	/**
	 * Resets the current text range
	 */
	public TextField selectTextRangeNone() {
		this.resetTextRange();
		return this;
	}

	/**
	 * Sets the current text range to the first instance of the provided string,
	 * if found
	 * 
	 * @param s
	 *            The String to search for
	 */
	public TextField selectTextRangeBySubstring(String s) {
		int head = finalText.indexOf(s);
		if (head != -1) {
			setTextRangeStart(head);
			int tail = head + s.length();
			setTextRangeEnd(tail);
			caretIndex = tail;
			getVisibleText();
		}
		return this;
	}

	/**
	 * Sets the selected text range to head-tail or tail-head depending on the
	 * provided indexes. Selects nothing if either of the provided indexes are
	 * out of range
	 * 
	 * @param head
	 *            The start or end index of the desired text range
	 * @param tail
	 *            The end or start index of the desired text range
	 */
	public TextField selectTextRangeByIndex(int head, int tail) {
		int nHead = head;
		int nTail = tail;
		if (head > tail) {
			nHead = tail;
			nTail = head;
		}
		if (nHead < 0)
			nHead = 0;
		if (nTail > finalText.length())
			nTail = finalText.length();

		this.setTextRangeStart(nHead);
		this.setTextRangeEnd(nTail);
		caretIndex = nTail;
		getVisibleText();
		return this;
	}

	protected float getCaretOffset() {
		return 0;
	}

	private void selectTextRangeDoubleClick() {
		if (!finalText.equals("")) {
			int end;
			if (finalText.substring(caretIndex, finalText.length()).indexOf(' ') != -1)
				end = caretIndex + finalText.substring(caretIndex, finalText.length()).indexOf(' ');
			else
				end = caretIndex + finalText.substring(caretIndex, finalText.length()).length();
			int start = finalText.substring(0, caretIndex).lastIndexOf(' ') + 1;
			if (start == -1)
				start = 0;
			setTextRangeStart(start);
			caretIndex = end;
			setText(getVisibleText());
			setTextRangeEnd(end);
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
	}

	private void selectTextRangeTripleClick() {
		if (!finalText.equals("")) {
			caretIndex = finalText.length();
			setText(getVisibleText());
			setTextRangeStart(0);
			setTextRangeEnd(finalText.length());
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
	}

	private void setTextRangeStart(int head) {
		if (!visibleText.equals("")) {
			rangeHead = head;
		}
	}

	private void setTextRangeEnd(int tail) {
		if (!visibleText.equals("") && rangeHead != -1) {
			widthTest.setSize(calcFontSize());

			widthTest.setText(".");
			float diff = widthTest.getLineWidth();

			float rangeX;

			if (rangeHead - this.head <= 0) {
				widthTest.setText("");
				rangeX = widthTest.getLineWidth();
			} else if (rangeHead - this.head < visibleText.length()) {
				widthTest.setText(visibleText.substring(0, rangeHead - this.head));
				float width = widthTest.getLineWidth();
				if (widthTest.getText().length() > 0) {
					if (widthTest.getText().charAt(widthTest.getText().length() - 1) == ' ') {
						widthTest.setText(widthTest.getText() + ".");
						width = widthTest.getLineWidth() - diff;
					}
				}
				rangeX = width;
			} else {
				widthTest.setText(visibleText);
				rangeX = widthTest.getLineWidth();
			}

			if (rangeHead >= this.head)
				rangeX = getAbsoluteX() + rangeX + getAllPadding().x;
			else
				rangeX = getAllPadding().x;

			rangeTail = tail;
			if (tail - this.head <= 0)
				widthTest.setText("");
			else if (tail - this.head < visibleText.length())
				widthTest.setText(visibleText.substring(0, tail - this.head));
			else
				widthTest.setText(visibleText);

			textRangeText = (rangeHead < rangeTail) ? finalText.substring(rangeHead, rangeTail)
					: finalText.substring(rangeTail, rangeHead);

			float rangeW = getAllPadding().y;

			if (rangeTail <= this.tail) {
				float width = widthTest.getLineWidth();
				if (widthTest.getText().length() > 0) {
					if (widthTest.getText().charAt(widthTest.getText().length() - 1) == ' ') {
						widthTest.setText(widthTest.getText() + ".");
						width = widthTest.getLineWidth() - diff;
					}
				}
				rangeW = getAbsoluteX() + width + getAllPadding().y;
			}

			if (rangeHead > rangeTail) {

				caret.getMaterial().setFloat("TextRangeStart", rangeW + getCaretOffset());
				caret.getMaterial().setFloat("TextRangeEnd", rangeX + getCaretOffset());
			} else {
				caret.getMaterial().setFloat("TextRangeStart", rangeX + getCaretOffset());
				caret.getMaterial().setFloat("TextRangeEnd", rangeW + getCaretOffset());
			}

			caret.getMaterial().setBoolean("ShowTextRange", true);
		}
	}

	private void resetTextRange() {
		textRangeText = "";
		rangeHead = -1;
		rangeTail = -1;
		caret.getMaterial().setFloat("TextRangeStart", 0);
		caret.getMaterial().setFloat("TextRangeEnd", 0);
		caret.getMaterial().setBoolean("ShowTextRange", false);
	}

	private void editTextRangeText(String insertText) {
		int head, tail;
		if (rangeHead != -1 && rangeTail != -1) {
			head = rangeHead;
			tail = rangeTail;
			if (head < 0)
				head = 0;
			else if (head > finalText.length())
				head = finalText.length();
			if (tail < 0)
				tail = 0;
			else if (tail > finalText.length())
				tail = finalText.length();
			resetTextRange();
		} else {
			head = caretIndex - 1;
			if (head == -1)
				head = 0;
			tail = caretIndex;
		}
		String newText;
		int tempIndex;
		if (tail > head) {
			newText = finalText.substring(0, head) + insertText + finalText.substring(tail, finalText.length());
			tempIndex = head + insertText.length();
		} else {
			newText = finalText.substring(0, tail) + insertText + finalText.substring(head, finalText.length());
			tempIndex = tail + insertText.length();
		}

		try {
			newText = newText.replace("\r", "");
		} catch (Exception ex) {
		}

		try {
			newText = newText.replace("\n", "");
		} catch (Exception ex) {
		}

		if (this.type != Type.DEFAULT) {
			String grabBag = "";
			switch (type) {
			case EXCLUDE_CUSTOM:
				grabBag = validateCustom;
				break;
			case EXCLUDE_SPECIAL:
				grabBag = validateSpecChar;
				break;
			case ALPHA:
				grabBag = validateAlpha;
				break;
			case ALPHA_NOSPACE:
				grabBag = validateAlphaNoSpace;
				break;
			case NUMERIC:
				grabBag = validateNumeric;
				break;
			case ALPHANUMERIC:
				grabBag = validateAlpha + validateNumeric;
				break;
			case ALPHANUMERIC_NOSPACE:
				grabBag = validateAlphaNoSpace + validateNumeric;
				break;
			}
			if (this.type == Type.EXCLUDE_CUSTOM || this.type == Type.EXCLUDE_SPECIAL) {
				for (int i = 0; i < grabBag.length(); i++) {
					try {
						String ret = newText.replace(String.valueOf(grabBag.charAt(i)), "");
						if (ret != null)
							newText = ret;
					} catch (Exception ex) {
					}
				}
			} else {
				String ret = newText;
				for (int i = 0; i < newText.length(); i++) {
					try {
						int index = grabBag.indexOf(String.valueOf(newText.charAt(i)));
						if (index == -1) {
							String temp = ret.replace(String.valueOf(String.valueOf(newText.charAt(i))), "");
							if (temp != null)
								ret = temp;
						}
					} catch (Exception ex) {
					}
				}
				if (!ret.equals(""))
					newText = ret;
			}
			tempIndex = newText.length();
		}

		if (maxLength != 0 && newText.length() > maxLength) {
			newText = newText.substring(0, maxLength);
			tempIndex = maxLength;
		}

		int testIndex = (head > tail) ? tail : head;

		setText(newText);

		caretIndex = testIndex;
	}

	// Control methods
	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
	}

	@Override
	public void update(float tpf) {
		if (isPressed) {
			stillPressedInterval();
		}
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	}
}
