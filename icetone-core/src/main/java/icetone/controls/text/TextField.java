/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016-2018, Emerald Icemoon
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

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector4f;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Screen;
import icetone.core.StyledContainer;
import icetone.core.ToolKit;
import icetone.core.UIEventTarget;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.keyboard.KeyboardUIEvent;
import icetone.core.event.mouse.MouseMovementListener;
import icetone.core.event.mouse.MouseUIMotionEvent;
import icetone.core.utils.Alarm.AlarmTask;
import icetone.core.utils.ClassUtil;
import icetone.css.CssExtensions;
import icetone.text.FontInfo;

/**
 * A control that may be used to enter a single row of text (for multiple line
 * input see {@link TextArea}). The text field may be set with custom validation
 * or transformation rules, provides a selection range and responds to platform
 * specific copy/cut/paste key sequences and other common keys.
 * <p>
 * Other features include insert/overwrite text insertion modes, click and drag
 * range selection, click caret positioning.
 * <p>
 * The control consists of 3 child elements, all of which are removed from the
 * scene when not in use leaving the only the text field node itself. The
 * <strong>overlay</strong> is used to provide focus highlights, the
 * <strong>caret</string> to provide the range and <code>range</code> to provide
 * selection ranges. These may all be styled individually.
 *
 * @author t0neg0d
 * @author rockfire
 */
public class TextField extends AbstractTextField implements MouseMovementListener<UIEventTarget> {

	public static enum Type {
		ALPHA, ALPHA_NOSPACE, ALPHANUMERIC, ALPHANUMERIC_NOSPACE, DEFAULT, EXCLUDE_CUSTOM, EXCLUDE_SPECIAL,
		INCLUDE_CUSTOM, NUMERIC
	}

	protected int caretIndex = 0;
	protected int head = 0;
	protected int rangeHead = -1, rangeTail = -1;
	protected int tail = 0;
	protected List<String> textModel = new ArrayList<>();
	protected int visibleHead = -1, visibleTail = -1;
	private boolean isEnabled = true;
	private boolean isPressed = false;
	private boolean needsTextUpdate;
	private String nextChar;
	private boolean selectable = true;
	private BaseElement range;
	private AlarmTask scrollTimerTask;
	private Type type = Type.DEFAULT;
	private boolean valid;
	private String validateAlpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
	private String validateAlphaNoSpace = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String validateCustom = "";
	private String validateNumeric = "0123456789.-";
	private String validateSpecChar = "`~!@#$%^&*()-_=+[]{}\\|;:'\",<.>/?";

	/**
	 * Constructor for when using the default {@link Screen} only.
	 */
	public TextField() {
		this("");
	}

	/**
	 * Constructor.
	 * 
	 * @param screen screen this control will be a descendant of
	 */
	public TextField(BaseScreen screen) {
		super(screen);
		setText("");
	}

	/**
	 * Constructor for when using the default {@link Screen} only.
	 * 
	 * @param text initial text
	 */
	public TextField(String text) {
		super(BaseScreen.get());
		setText(text);
	}

	/**
	 * Constructor.
	 * 
	 * @param text   initial text
	 * @param screen screen
	 */
	public TextField(String text, BaseScreen screen) {
		super(screen);
		setText(text);
	}

	@Override
	public String formatText(String text) {
		for (AbstractTextField.TextProcessor p : getTextFormatters()) {
			text = p.processText(text);
		}
		calcHeadAndTail(text);
		return super.formatText(text.substring(head, tail));
	}

	@Override
	public int getLength() {
		return getText() == null ? 0 : getText().length();
	}

	/**
	 * Get the element that provides that selection range.
	 * 
	 * @return range element
	 */
	public BaseElement getRange() {
		return range;
	}

	/**
	 * Return the currently selected text or an empty string if nothing is selected.
	 * 
	 * @return selected text
	 */
	public String getSelectedText() {
		return text == null || rangeHead == rangeTail ? ""
				: ((rangeHead < rangeTail) ? text.substring(rangeHead, rangeTail)
						: text.substring(rangeTail, rangeHead));
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		l.add((isEditable() ? "Editable" : "") + ClassUtil.getMainClassName(getClass()));
		return l;
	}

	/**
	 * Returns the current {@link Type} used to enforce certain character types
	 * allowed in the input
	 * 
	 * @return type of text input allowed
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Get whether a range of text may be selected from the content. This is done
	 * using either click-and-drag or using Shift+Cursor, Shift+Ctrl+Cursor and
	 * other key combinations.
	 * 
	 * @return selectable
	 */
	public boolean isSelectable() {
		return selectable;
	}

	/**
	 * Set whether a range of text may be selected from the content. This is done
	 * using either click-and-drag or using Shift+Cursor, Shift+Ctrl+Cursor and
	 * other key combinations.
	 * 
	 * @param selectable selectable
	 */
	public TextField setSelectable(boolean selectable) {
		if (selectable != this.selectable) {
			this.selectable = selectable;
			if (!selectable)
				resetTextRange();
			layoutChildren();
		}
		return this;
	}

	@Override
	public void onMouseMove(MouseUIMotionEvent<UIEventTarget> evt) {
		Vector4f padding = getAllPadding().add(getTextClipPadding());
		float availableWidth = getWidth() - (padding.x + padding.y);
		float relx = evt.getX() - getAbsoluteX() - padding.x;
		if (relx <= 0) {
			relx = 0;
			if (scrollTimerTask == null)
				startScrollTimer(-1, ToolKit.get().getConfiguration().getRepeatDelay());
		} else if (relx > availableWidth) {
			relx = availableWidth;
			if (scrollTimerTask == null)
				startScrollTimer(1, ToolKit.get().getConfiguration().getRepeatDelay());
		} else if (scrollTimerTask != null) {
			cancelScrollTimer();
		} else {
			setCaretPositionByXNoRange(relx - indent);
			if (caretIndex >= 0)
				setTextRangeEnd(caretIndex);
			else
				setTextRangeEnd(0);
			layoutChildren();
		}
	}

	/**
	 * Sets the selection range to cover all text
	 * 
	 * @return this for chaining
	 */
	public TextField selectTextRangeAll() {
		setTextRangeStart(0);
		setTextRangeEnd(text.length());
		setCaretIndex(text.length());
		layoutChildren();
		return this;
	}

	/**
	 * Sets the selected text range to head-tail or tail-head depending on the
	 * provided indexes. Selects nothing if either of the provided indexes are out
	 * of range
	 * 
	 * @param head The start or end index of the desired text range
	 * @param tail The end or start index of the desired text range
	 * @return this for chaining
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
		if (nTail > text.length())
			nTail = text.length();

		this.setTextRangeStart(nHead);
		this.setTextRangeEnd(nTail);
		setCaretIndex(nTail);
		layoutChildren();
		return this;
	}

	/**
	 * Sets the current text range to the first instance of the provided string, if
	 * found
	 * 
	 * @param s The String to search for
	 * @return this for chaining
	 */
	public TextField selectTextRangeBySubstring(String s) {
		int idx = text.indexOf(s);
		if (idx != -1) {
			setTextRangeStart(idx);
			int tail = idx + s.length();
			setTextRangeEnd(tail);
			setCaretIndex(tail);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Resets the current text range
	 * 
	 * @return this for chaining
	 */
	public TextField selectTextRangeNone() {
		this.resetTextRange();
		layoutChildren();
		return this;
	}

	/**
	 * Sets the caret position to the end of the TextField's text
	 * 
	 * @return this for chaining
	 */
	public TextField setCaretPositionToEnd() {
		setCaretIndex(text.length());
		resetTextRange();
		layoutChildren();
		return this;
	}

	/**
	 * Set the text cursor to the start of the field and remove any selection range.
	 * 
	 * @return this for chaining
	 */
	public TextField setCaretPositionToStart() {
		setCaretIndex(0);
		head = 0;
		dirtyLayout(false, LayoutType.text);
		layoutChildren();
		return this;
	}

	/**
	 * Sets a custom validation rule for the TextField.
	 * 
	 * @param grabBag list of character to either allow or disallow as input
	 * @return this for chaining
	 */
	public TextField setCustomValidation(String grabBag) {
		validateCustom = grabBag;
		return this;
	}

	/**
	 * Set the content of this text field.
	 * 
	 * @param text text
	 * @return this for chaining
	 */
	@Override
	public BaseElement setText(String text) {
		resetTextRange();
		indent = 0;
		buildModel(text);
		setCaretIndex(0);
		needsTextUpdate = true;
		super.setText(text);
		return this;
	}

	/**
	 * Set the current {@link Type} used to enforce certain character types allowed
	 * in the input
	 * 
	 * @param type type of text input allowed
	 * @return this for chaining
	 */
	public TextField setType(Type type) {
		this.type = type;
		return this;
	}

	protected void buildModel(String text) {
		textModel.clear();
		if (text != null) {
			for (int i = 0; i < text.length(); i++) {
				textModel.add(String.valueOf(text.charAt(i)));
			}
		}
	}

	protected void calcHeadAndTail(String text) {
		// Check indexes
		if (needsTextUpdate) {

			FontInfo fontInfo = getThemeInstance().getFontInfo(BaseElement.calcFont(this));
			Vector4f padding = getAllPadding().add(getTextClipPadding());
			float availableWidth = getWidth() - (padding.x + padding.y);
			int textLen = text == null ? 0 : text.length();

			if (!isInStyleHierarchy() || textLen == 0) {
				/* Optimisation, if nothing is to be displayed */
				head = 0;
				caretIndex = 0;
				tail = textLen;
				setTextRange(-1, -1);
				indent = 0;
				return;
			}

			needsTextUpdate = false;
			float lw = availableWidth;

			if (fontInfo.getLineWidth(text) < availableWidth) {
				head = 0;
				tail = text.length();
				indent = 0;
			} else {
				/* Sanity check */
				if (head < 0)
					head = 0;
				if (head > text.length())
					head = text.length();
				if (tail < head)
					tail = head;

				/* Adjust head or tail depending on the direction of the caret */
				if (caretIndex <= head && head > 0) {
					indent = 0;
					head = caretIndex;
					for (tail = head + 1; tail < text.length(); tail++) {
						lw = fontInfo.getLineWidth(text.substring(head, tail));
						if (lw >= availableWidth) {
							break;
						}
					}
					if (head > 0 && lw > availableWidth && tail < text.length()) {
						tail++;
					}
				} else if (caretIndex >= tail && tail > 0) {
					tail = caretIndex;
					for (head = tail - 1; head >= 0 && head < text.length();) {
						lw = fontInfo.getLineWidth(text.substring(head, Math.min(text.length(), tail)));
						if (lw >= availableWidth) {
							break;
						}
						head--;
					}
					if (getMode() == Mode.OVERWRITE) {
						head++;
					}
					indent = availableWidth - lw;
				} else {
					if (indent < 0) {
						for (head = tail - 1; head >= 0;) {
							lw = fontInfo.getLineWidth(text.substring(head, Math.min(tail, text.length())));
							if (lw >= availableWidth) {
								break;
							}
							head--;
						}
						indent = availableWidth - lw;
					} else {
						indent = 0;
						for (tail = head + 1; tail < text.length(); tail++) {
							lw = fontInfo.getLineWidth(text.substring(head, tail));
							if (lw > availableWidth) {
								break;
							}
						}
					}
				}
			}

		}
		if (head < 0)
			head = 0;
		if (tail > text.length())
			tail = text.length();
	}

	protected void cancelScrollTimer() {
		if (scrollTimerTask != null) {
			scrollTimerTask.cancel();
			scrollTimerTask = null;
		}
	}

	protected void clearBoundsCache() {
		super.clearBoundsCache();
		needsTextUpdate = true;
	}

	@Override
	protected void configureStyledTextElement() {

		layoutManager = new TextFieldLayout();

		/* Range */
		range = new StyledContainer(screen) {
			{
				styleClass = "range";
				useParentPseudoStyles = true;
			}
		};
		range.setIgnoreMouse(true);
		range.hide();
		addElement(range);

		onMousePressed(evt -> {
			isPressed = true;
			if (this.isEnabled) {
				switch (evt.getClicks()) {
				case 1:
					resetTextRange();
					float x = evt.getRel().x - indent - getCaret().getIndent();
					setCaretPositionByXNoRange(x);
					setTextRangeStart(caretIndex);
					layoutChildren();
					screen.addMouseMovementListener(this);
					break;
				}
			}
		});

		onMouseReleased(evt -> {
			if (isEnabled) {
				if (isPressed) {
					if (evt.getClicks() == 2) {
						selectTextRangeDoubleClick();
					} else if (evt.getClicks() == 3) {
						selectTextRangeTripleClick();
					} else {
						float x = evt.getRel().x - indent - getCaret().getIndent();
						setCaretPositionByXNoRange(x);
						if (caretIndex >= 0)
							setTextRangeEnd(caretIndex);
						else
							setTextRangeEnd(0);
					}
					layoutChildren();
				}
			}
			isPressed = false;
			screen.removeMouseMovementListener(this);
			cancelScrollTimer();
		});

		onKeyboard(evt -> {
			if (evt.isPressed())
				onKeyPress(evt);
			else
				onKeyRelease(evt);
		});

		onKeyboardFocusGained(evt -> {
			setTextRangeStart(caretIndex);
			activateCaret();
			layoutChildren();
		});
		onKeyboardFocusLost(evt -> deactivateCaret());
	}

	protected void onKeyPress(KeyboardUIEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_F1 || evt.getKeyCode() == KeyInput.KEY_F2
				|| evt.getKeyCode() == KeyInput.KEY_F3 || evt.getKeyCode() == KeyInput.KEY_F4
				|| evt.getKeyCode() == KeyInput.KEY_F5 || evt.getKeyCode() == KeyInput.KEY_F6
				|| evt.getKeyCode() == KeyInput.KEY_F7 || evt.getKeyCode() == KeyInput.KEY_F8
				|| evt.getKeyCode() == KeyInput.KEY_F9 || evt.getKeyCode() == KeyInput.KEY_CAPITAL
				|| evt.getKeyCode() == KeyInput.KEY_ESCAPE || evt.getKeyCode() == KeyInput.KEY_TAB) {
		} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
		} else if (evt.getKeyCode() == KeyInput.KEY_DELETE && isEditable()) {
			if (rangeHead != -1 && rangeTail != -1)
				editTextRangeText("");
			else {
				if (caretIndex < text.length()) {
					textModel.remove(caretIndex);
					updateText();
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_BACK && isEditable()) {
			if (rangeHead != -1 && rangeTail != -1) {
				editTextRangeText("");
			} else {
				if (caretIndex > 0) {
					textModel.remove(caretIndex - 1);
					if (caretIndex >= tail && head > 0) {
						head--;
					}
					updateText();
					setCaretIndex(caretIndex - 1);
					layoutChildren();
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
			if (!evt.isShift())
				resetTextRange();
			if (caretIndex > -1) {
				if (ToolKit.isMac()) {
					if (evt.isMeta()) {
						setCaretIndex(0);
						dirtyLayout(false, LayoutType.text);
						if (evt.isShift())
							setTextRangeEnd(caretIndex);
						else {
							resetTextRange();
							setTextRangeStart(caretIndex);
						}
						layoutChildren();
						return;
					}
				}

				if ((ToolKit.isMac() && !evt.isAlt()) || (ToolKit.isWindows() && !evt.isCtrl())
						|| (ToolKit.isUnix() && !evt.isCtrl()) || (ToolKit.isSolaris() && !evt.isCtrl()))
					setCaretIndex(caretIndex - 1);
				else {
					int cIndex = caretIndex;
					if (cIndex > 0)
						if (text.charAt(cIndex - 1) == ' ')
							cIndex--;
					int index = 0;
					if (cIndex > 0)
						index = text.substring(0, cIndex).lastIndexOf(' ') + 1;
					if (index < 0)
						index = 0;
					setCaretIndex(index);
				}
				if (caretIndex < 0)
					setCaretIndex(0);
				if (caretIndex < head) {
					dirtyLayout(false, LayoutType.text);
					head = caretIndex;
				}

				if (!evt.isShift())
					setTextRangeStart(caretIndex);
			}
			layoutChildren();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
			if (!evt.isShift())
				resetTextRange();
			if (caretIndex <= text.length()) {
				if (ToolKit.isMac()) {
					if (evt.isMeta()) {
						setCaretIndex(text.length());
						if (evt.isShift())
							setTextRangeEnd(caretIndex);
						else {
							resetTextRange();
							setTextRangeStart(caretIndex);
						}
						layoutChildren();
						return;
					}
				}

				int was = caretIndex;

				if ((ToolKit.isMac() && !evt.isAlt()) || (ToolKit.isWindows() && !evt.isCtrl())
						|| (ToolKit.isUnix() && !evt.isCtrl()) || (ToolKit.isSolaris() && !evt.isCtrl()))
					setCaretIndex(caretIndex + 1);
				else {
					int cIndex = caretIndex;
					if (cIndex < text.length())
						if (text.charAt(cIndex) == ' ')
							cIndex++;
					int index;
					if (cIndex < text.length()) {
						index = text.substring(cIndex, text.length()).indexOf(' ');
						if (index == -1)
							index = text.length();
						else
							index += cIndex;
					} else {
						index = text.length();
					}
					setCaretIndex(index);
				}

				if (!evt.isShift() && was != caretIndex) {
					setTextRangeStart(caretIndex);
				}
			}
			layoutChildren();
			evt.setConsumed();
		} else if ((evt.getKeyCode() == KeyInput.KEY_END || evt.getKeyCode() == KeyInput.KEY_NEXT
				|| evt.getKeyCode() == KeyInput.KEY_DOWN)) {
			setCaretIndex(textModel.size());
			if (evt.isShift())
				setTextRangeEnd(caretIndex);
			else {
				resetTextRange();
				setTextRangeStart(caretIndex);
			}
			layoutChildren();
			evt.setConsumed();
		} else if ((evt.getKeyCode() == KeyInput.KEY_HOME || evt.getKeyCode() == KeyInput.KEY_PRIOR
				|| evt.getKeyCode() == KeyInput.KEY_UP)) {
			setCaretIndex(0);
			if (evt.isShift())
				setTextRangeEnd(caretIndex);
			else {
				resetTextRange();
				setTextRangeStart(caretIndex);
			}
			layoutChildren();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_INSERT) {
			setMode(getMode() == Mode.INSERT ? Mode.OVERWRITE : Mode.INSERT);
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_A && evt.isCtrl()) {
			setCaretIndex(0);
			selectTextRangeAll();
			evt.setConsumed();
		} else {
			if (evt.isCtrl()) {
				if (evt.getKeyCode() == KeyInput.KEY_X) {
					if (isAllowCut())
						ToolKit.get().setClipboardText(getSelectedText());
					editTextRangeText("");
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_C) {
					if (isAllowCopy())
						ToolKit.get().setClipboardText(getSelectedText());
					evt.setConsumed();
				} else if (evt.getKeyCode() == KeyInput.KEY_V && isEditable()) {
					if (isAllowPaste())
						editTextRangeText(ToolKit.get().getClipboardText());
					evt.setConsumed();
				}
			} else {
				if (isEnabled && isEditable() && evt.getKeyChar() > 0) {
					if (rangeHead != -1 && rangeTail != -1) {
						editTextRangeText("");
					}
					if (!evt.isShift())
						resetTextRange();
					nextChar = String.valueOf(evt.getKeyChar());
					valid = true;
					if (getMaxLength() > 0) {
						if (getText().length() >= getMaxLength())
							valid = false;
					}
					if (valid) {
						if (type == Type.DEFAULT) {
							addText(caretIndex, nextChar);
						} else if (type == Type.ALPHA) {
							if (validateAlpha.indexOf(nextChar) != -1) {
								addText(caretIndex, nextChar);
							}
						} else if (type == Type.ALPHA_NOSPACE) {
							if (validateAlpha.indexOf(nextChar) != -1) {
								addText(caretIndex, nextChar);
							}
						} else if (type == Type.NUMERIC) {
							if (validateNumeric.indexOf(nextChar) != -1) {
								addText(caretIndex, nextChar);
							}
						} else if (type == Type.ALPHANUMERIC) {
							if (validateAlpha.indexOf(nextChar) != -1 || validateNumeric.indexOf(nextChar) != -1) {
								addText(caretIndex, nextChar);
							}
						} else if (type == Type.ALPHANUMERIC_NOSPACE) {
							if (validateAlphaNoSpace.indexOf(nextChar) != -1
									|| validateNumeric.indexOf(nextChar) != -1) {
								addText(caretIndex, nextChar);
							}
						} else if (type == Type.EXCLUDE_SPECIAL) {
							if (validateSpecChar.indexOf(nextChar) == -1) {
								addText(caretIndex, nextChar);
							}
						} else if (type == Type.EXCLUDE_CUSTOM) {
							if (validateCustom.indexOf(nextChar) == -1) {
								addText(caretIndex, nextChar);
							}
						} else if (type == Type.INCLUDE_CUSTOM) {
							if (validateCustom.indexOf(nextChar) != -1) {
								addText(caretIndex, nextChar);
							}
						}
					}
					if (!evt.isShift()) {
						setTextRangeStart(caretIndex);
					}
					layoutChildren();
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

	protected void addText(int idx, String character) {
		if (getMode() == Mode.OVERWRITE && idx < textModel.size()) {
			textModel.set(idx, character);
		} else {
			textModel.add(idx, character);
		}
		updateText();
		setCaretIndex(idx + 1);
	}

	protected void onKeyRelease(KeyInputEvent evt) {
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

	@Override
	protected void onPsuedoStateChange() {
		/*
		 * TODO only do this if any children are using parent pseudo styles. Button does
		 * the same thing
		 */
		dirtyLayout(true, LayoutType.styling);
	}

	protected void setCaretIndex(int caretIndex) {
		if (text == null || caretIndex < 0)
			caretIndex = 0;
		else if (caretIndex > text.length())
			caretIndex = text.length();
		if (this.caretIndex != caretIndex) {
//			if (caretIndex > tail) {
//				head += caretIndex - tail;
//				dirtyLayout(false, LayoutType.text);
//			} else if (caretIndex < head) {
//				head -= head - caretIndex;
//				dirtyLayout(false, LayoutType.text);
//			} else if (indent != 0 && caretIndex > 0 && caretIndex == head) {
//				head--;
//				dirtyLayout(false, LayoutType.text);
//			}
			this.caretIndex = caretIndex;
			needsTextUpdate = true;
			dirtyLayout(false, LayoutType.text, LayoutType.children);
		}
	}

	protected void startScrollTimer(int scrollCharacters, float wait) {
		scrollTimerTask = ToolKit.get().getAlarm().timed(() -> {
			int actualChars = scrollCharacters;
			if (actualChars < 0 && caretIndex + actualChars < 0)
				actualChars = -caretIndex;
			else if (actualChars > 0 && caretIndex + actualChars > text.length())
				actualChars = text.length() - caretIndex;

			int newCaretIndex = caretIndex + actualChars;
			setCaretIndex(newCaretIndex);
			setTextRangeEnd(caretIndex);
			needsTextUpdate = true;
			layoutChildren();
			if (actualChars != 0)
				startScrollTimer(scrollCharacters, ToolKit.get().getConfiguration().getRepeatInterval());
		}, wait);

	}

	protected void updateText() {

		String text = String.join("", textModel);
		try {

			for (AbstractTextField.TextProcessor p : getTextParsers()) {
				text = p.processText(text);
			}
		} catch (IllegalArgumentException iae) {
			// Processors can throw this
			String res = onValidationError(text, iae);
			if (res != null)
				text = res;

		}

		if (!Objects.equals(text, this.text)) {
			String was = this.text;
			needsTextUpdate = true;
			this.text = text;
			getCssState().addAllCssDeclaration(new PropertyDeclaration(CssExtensions.TEXT,
					new PropertyValue(CSSPrimitiveValue.CSS_STRING, text, text), false, StylesheetInfo.USER));
			if (text.equals(""))
				removeTextElement();
			else if (textElement == null)
				createText();
			if (changeSupport != null)
				changeSupport.fireEvent(new UIChangeEvent<AbstractTextField, String>(this, was, text));
		}
	}

	private void editTextRangeText(String insertText) {
		int start, end;
		if (rangeHead != -1 && rangeTail != -1) {
			start = rangeHead;
			end = rangeTail;
			if (start < 0)
				start = 0;
			else if (start > text.length())
				start = text.length();
			if (end < 0)
				end = 0;
			else if (end > text.length())
				end = text.length();
			resetTextRange();
		} else {
			start = caretIndex;
			if (start < 0)
				start = 0;
			end = caretIndex;
		}
		String newText;
		if (end > start) {
			newText = text.substring(0, start) + insertText + text.substring(end, text.length());
		} else {
			newText = text.substring(0, end) + insertText + text.substring(start, text.length());
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
			default:
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
		}

		if (getMaxLength() != 0 && newText.length() > getMaxLength()) {
			newText = newText.substring(0, getMaxLength());
		}

		int testIndex = start == end ? start + insertText.length() : (start < end ? start : end);

		buildModel(newText);
		dirtyLayout(false, LayoutType.children);
		updateText();

		setCaretIndex(testIndex);
	}

	private void resetTextRange() {
		setTextRange(-1, -1);
	}

	private void selectTextRangeDoubleClick() {
		if (!text.equals("")) {
			int end;
			if (text.substring(caretIndex, text.length()).indexOf(' ') != -1)
				end = caretIndex + text.substring(caretIndex, text.length()).indexOf(' ');
			else
				end = caretIndex + text.substring(caretIndex, text.length()).length();
			int start = text.substring(0, caretIndex).lastIndexOf(' ') + 1;
			if (start == -1)
				start = 0;
			setCaretIndex(start);
			setTextRangeStart(start);
			setTextRangeEnd(end);
			dirtyLayout(false, LayoutType.text);
			layoutChildren();
		}
	}

	private void selectTextRangeTripleClick() {
		if (!text.equals("")) {
			setCaretIndex(text.length());
			setTextRangeStart(0);
			setTextRangeEnd(text.length());
			layoutChildren();
		}
	}

	private int getCaretPositionForXNoRange(float x) {

		int caretIndex = -1;
		if (textElement != null) {
			Vector4f[] letterPositions = textElement.getLetterPositions();
			if (letterPositions.length > 0) {
				if (x < 0)
					x = 0;
				else if (x > getWidth())
					x = getWidth();
				for (int i = 0; i < letterPositions.length; i++) {
					Vector4f pos = letterPositions[i];
					Vector4f ppos = i > 0 ? letterPositions[i - 1] : Vector4f.ZERO;
					if (x < pos.x + (pos.z / 2f) && x >= ppos.x + (ppos.z / 2f)) {
						caretIndex = i + head;
						break;
					}
				}
			}
			if (caretIndex == -1)
				// caretIndex = text.length();
				caretIndex = tail + 1;
			if (caretIndex > text.length())
				caretIndex = text.length();
		}
		if (caretIndex < 0)
			caretIndex = 0;
		if (text != null && caretIndex > text.length())
			caretIndex = text.length();
		return caretIndex;
	}

	private void setCaretPositionByXNoRange(float x) {
		caretIndex = getCaretPositionForXNoRange(x);
		setCaretIndex(caretIndex);
		layoutChildren();
	}

	private void setTextRangeEnd(int end) {
		if (end < 0)
			end = 0;
		if (end > text.length())
			end = text.length();
		setTextRange(rangeHead, end);
	}

	private void setTextRangeStart(int start) {
		if (start < 0)
			start = 0;
		if (start > text.length())
			start = text.length();
		setTextRange(start, rangeTail);
	}

	private void setTextRange(int rangeHead, int rangeTail) {
		if (!selectable) {
			rangeHead = -1;
			rangeTail = -1;
		}
		if (rangeHead != this.rangeHead || rangeTail != this.rangeTail) {
			this.rangeHead = rangeHead;
			this.rangeTail = rangeTail;
			if (rangeHead < 1 && rangeTail < 1) {
				if (range != null && range.isShowing())
					range.hide();
			} else {
				if (range.getElementParent() == null)
					showElement(range);
				else
					range.show();
			}
			needsTextUpdate = true;
			dirtyLayout(false, LayoutType.children);
		}
	}

}
