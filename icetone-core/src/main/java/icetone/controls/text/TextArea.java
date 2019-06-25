/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2018, Emerald Icemoon
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

import java.util.Objects;

import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.scrolling.ScrollPanel;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.ToolKit;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.keyboard.KeyboardUIEvent;
import icetone.core.undo.UndoManager;
import icetone.core.undo.UndoableCommand;
import icetone.core.utils.MathUtil;
import icetone.text.FontInfo;
import icetone.text.TextElement;

/**
 * A control that may be used to enter a multiple rows of text (for single line
 * input see {@link TextField}) with optional scrolling when wrapped in a
 * {@link TextAreaScrollPanel}. The text field may be set with custom validation
 * or transformation rules, provides a selection range and responds to platform
 * specific copy/cut/paste key sequences and other common keys.
 * <p>
 * Other features include insert/overwrite text insertion modes, click and drag
 * range selection, click caret positioning.
 * <p>
 * The control consists of 3-5 child elements, all of which are removed from the
 * scene when not in use leaving the only the text field node itself. The
 * <strong>overlay</strong> is used to provide focus highlights, the
 * <strong>caret</string> to provide the caret and <code>range</code> to provide
 * between 1 and 3 selection range blocks. These may all be styled individually.
 * <p>
 * In order to enter more text than is possible within the normal bounds of the
 * control, it may be added to a {@link TextAreaScrollPanel}. This then provides
 * both vertical and horizontal scrolling in a manner you would expect with any
 * other UI toolkit.
 *
 * @author rockfire
 */
public class TextArea extends AbstractTextField {

	public enum ReturnMode {
		IGNORE, NEWLINE_ON_CTRL_RETURN, NEWLINE_ON_RETURN, NEWLINE_ON_SHIFT_RETURN
	}

	class TextAreaLayout extends AbstractTextInputLayout<TextArea> {
		@Override
		protected Vector2f calcPreferredSize(TextArea container) {
			Vector2f calcPreferredSize = super.calcPreferredSize(container);
			ScrollPanel scrollParent = getScrollParent();
			if (calcPreferredSize != null && scrollParent != null) {
				Vector2f spref = scrollParent.getDimensions().subtract(scrollParent.getTotalPadding())
						.subtract(scrollParent.getScrollBounds().getTotalPadding())
						.subtract(scrollParent.getScrollableArea().getTotalPadding())
						.subtract(scrollParent.getVerticalScrollBar().calcPreferredSize().x, 0);
				calcPreferredSize = MathUtil.largest(calcPreferredSize, spref);
			}
			return calcPreferredSize;
		}

		@Override
		protected Vector2f calcTextSize(TextArea parent, float inWidth) {
			if (rows == 0) {
				return MathUtil.largest(super.calcTextSize(parent, inWidth), defaultCalcTextSize(parent, inWidth));
			} else {
				String str = parent.getText();
				if (getCharacterWidth() > 0) {
					str = "";
					for (int i = 0; i < getCharacterWidth(); i++) {
						str += "W";
					}
				}
				Vector2f sz = getThemeInstance().getFontInfo(BaseElement.calcFont(parent)).getTextTotalSize(parent, str,
						Short.MAX_VALUE);
				sz.y = Math.max(sz.y, Math.max(1, rows) * sz.y);
				return sz;
			}
		}

		@Override
		protected void onLayout(TextArea childElement) {
			int selStart = getSelectionStart();
			int selEnd = getSelectionEnd();
			int textLen = getLength();
			if (selEnd < selStart) {
				int x = selEnd;
				selEnd = selStart;
				selStart = x;
			}
			if (selStart > textLen)
				selStart = textLen;
			if (selEnd > textLen)
				selEnd = textLen;

			Vector4f padding = childElement.getAllPadding();
			Vector2f cp = childElement.getCaret().calcPreferredSize();

			float caretX = 0;
			float caretH = 0;
			float caretY = 0;
			float caretW = cp.x;
			Vector4f[] lp = null;
			TextElement tel = childElement.getTextElement();
			FontInfo fontInfo;
			if (childElement.isTextElement()) {
				fontInfo = childElement.getThemeInstance().getFontInfo(tel.getFont());
				lp = tel.getLetterPositions();
				if (lp.length > 0) {
					int caretIndex = selStart;
					if (caretIndex < 0)
						caretIndex = 0;
					if (caretIndex > lp.length)
						caretIndex = lp.length;

					if (caretIndex >= lp.length) {
						String text = tel.getText();
						if (text.charAt(caretIndex - 1) == '\n') {
							caretY = lp[lp.length - 1].y + fontInfo.getTotalLineHeight();
							caretX = 0;
						} else {
							caretX = lp[lp.length - 1].x + lp[lp.length - 1].z + tel.getFont().getCharacterSpacing();
							caretY = lp[lp.length - 1].y - (fontInfo.getDescent() / 2f);
						}
					} else {
						caretX = lp[caretIndex].x;
						caretY = lp[caretIndex].y - (fontInfo.getDescent() / 2f);
					}

					if (childElement.getMode() == Mode.OVERWRITE)
						caretW = lp[caretIndex >= lp.length ? lp.length - 1 : caretIndex].z;
				}
			} else {
				fontInfo = childElement.getThemeInstance().getFontInfo(BaseElement.calcFont(childElement));
				if (childElement.getMode() == Mode.OVERWRITE) {
					caretW = fontInfo.getLineWidth("S");
				}
				caretY = -(fontInfo.getDescent() / 2f);
			}
			caretH = fontInfo.getTotalLineHeight();
			float x = childElement.getCaret().getIndent() + childElement.getIndent() + padding.x + caretX - (cp.x / 2f)
					- 1;
			childElement.getCaret().setBounds(x, caretY + padding.z, caretW, caretH);
			childElement.getOverlay().setBounds(0, 0, childElement.getWidth(), childElement.getHeight());

			if (childElement.isTextElement() && selStart != selEnd) {
				Vector4f rpad1 = Vector4f.ZERO;
				Vector4f rpad2 = Vector4f.ZERO;
				Vector4f rpad3 = Vector4f.ZERO;
				Vector4f start = tel.getLetterPositions()[selStart];
				Vector4f end = tel.getLetterPositions()[selEnd - 1];

				Vector4f r1 = new Vector4f(start.x - rpad1.x + padding.x, start.y + padding.z - rpad1.z, 0,
						start.w + rpad1.z + rpad1.w);
				if (start.y == end.y) {
					// Start and end are on same lines
					r1.z = end.x + end.z - start.x + rpad1.x + rpad1.y;
				} else {
					// Start and end are on different lines, run to end of
					// line
					r1.z = getWidth() - rpad1.x - rpad1.y - start.x - padding.x - padding.y;
				}

				range.setBounds(r1);
				range.show();

				if (start.y != end.y) {
					Vector4f r2 = new Vector4f(-rpad1.x + padding.x, r1.y + r1.w, 0, 0);
					// Start and end are on different lines

					if (start.y == end.y - fontInfo.getTotalLineHeight()) {
						// End is on this row
						r2.z = end.x + end.z + rpad2.x + rpad2.y;
						r2.w = end.w + rpad2.z + rpad2.w;
					} else {
						// End is at least next row away
						r2.z = getWidth() - rpad1.x - rpad1.y - padding.x - padding.y;
						r2.w = end.y + padding.z - rpad3.z - r2.y;
					}

					range2.setBounds(r2);
					range2.show();

					if (start.y != end.y - fontInfo.getTotalLineHeight()) {
						range3.setBounds(-rpad1.x + padding.x, end.y + padding.z - rpad3.z,
								end.x + end.z + rpad3.x + rpad3.y, end.w + rpad3.z + rpad3.w);
						range3.show();
					} else {
						range3.hide();
					}

				} else {
					range2.hide();
					range3.hide();
				}
			} else if (range != null) {
				range.hide();
				range2.hide();
				range3.hide();
			}
		}
	}

	public static int getUnwrappedLineCount(String text) {
		int i = text.split("\n").length;
		if (text.endsWith("\n"))
			i++;
		return i;
	}

	private int maxRows = Integer.MAX_VALUE;
	private boolean mouseDown;
	private Element range;
	private Element range2;
	private Element range3;
	private ReturnMode returnMode = ReturnMode.NEWLINE_ON_RETURN;
	private int rows;
	private int selectionEnd;
	private int selectionStart;

//	private UndoManager undoManager = new UndoManager();
	private UndoManager undoManager;

	public TextArea() {
		super();
	}

	public TextArea(BaseScreen screen) {
		super(screen);
	}

	public TextArea(String text) {
		super(text);
	}

	@Override
	protected void configureStyledTextElement() {
		setLayoutManager(new TextAreaLayout());
		setIgnoreMouseWheel(true);
		setTextVAlign(VAlign.Top);

		onMousePressed(evt -> {
			mouseDown = true;
			if (evt.getClicks() > 2) {
				selectAll();
			}
			else if (evt.getClicks() == 2) {
				selectWordAt(evt.getRelx(), evt.getRely());
			} else {
				moveCaretToPoint(evt.getRelx(), evt.getRely());
			}
		});
		onMouseReleased(evt -> {
			mouseDown = false;
		});
		onMouseMoved(evt -> {
			if (mouseDown) {
				int i = getIndexForPoint(evt.getRelx(), evt.getRely());
				if (i != -1)
					setSelectionEnd(i);
				else if (i == -1 && selectionEnd > selectionStart)
					// TODO should be index of current row for y position
					setSelectionEnd(text.length());
				else if (i == -1 && selectionStart > selectionEnd)
					// TODO should be index of current row for y position
					setSelectionStart(text.length());
			}
		});
		onKeyboardFocusGained(evt -> activateCaret());
		onKeyboardFocusLost(evt -> deactivateCaret());
		onNavigationKey(evt -> {
			if (evt.isPressed())
				keyPressed(evt);
		});
		onElementEvent(evt -> {
			configureParentScroller();
		}, Type.INITIALIZED);
	}

	/**
	 * Remove the selection and return the caret to the start of the selection.
	 * 
	 * @return this
	 */
	public TextArea deselect() {
		select(selectionStart, selectionStart);
		return this;
	}

	@Override
	public String formatText(String text) {
		for (AbstractTextField.TextProcessor p : getTextFormatters()) {
			text = p.processText(text);
		}
		return text;
	}

	/**
	 * Get the index of the character in the text given a relative coordinate in the
	 * element.
	 * 
	 * @param x x
	 * @param y y
	 * @return character index
	 */
	public int getIndexForPoint(float x, float y) {
		Vector4f p = getAllPadding();
		x -= p.x;
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;

		int caretIndex = -1;
		if (textElement != null) {
			Vector4f[] letterPositions = textElement.getLetterPositions();
			if (letterPositions.length > 0) {
				for (int i = 0; i < letterPositions.length; i++) {
					Vector4f pos = letterPositions[i];
					Vector4f ppos = i > 0 ? letterPositions[i - 1] : Vector4f.ZERO;
					if (ppos.x > pos.x)
						/*
						 * If the click is at the start of the line, the previous character will be on
						 * the previous line
						 */
						ppos = ppos.clone().setX(-ppos.z / 2f);

					if (i < letterPositions.length - 1) {
						Vector4f npos = letterPositions[i + 1];
						if (npos.x < pos.x) {
							/*
							 * If the next character is on the next line, then the width we test again is
							 * effectively infinite
							 */
							pos = pos.clone().setZ(Short.MAX_VALUE);
						}
					}

					if (((i > 0 && text.charAt(i) == '\n' && text.charAt(i - 1) == '\n')
							|| (x < pos.x + (pos.z / 2f) && x >= ppos.x + (ppos.z / 2f)))
							&& (y >= pos.y && y < pos.y + pos.w)) {
						caretIndex = i;
						break;
					}
				}
			}
			if (caretIndex == -1)
				caretIndex = text.length();
			else if (caretIndex > text.length())
				caretIndex = text.length();
		}
		if (caretIndex < 0)
			caretIndex = 0;
		return caretIndex;
	}

	@Override
	public int getLength() {
		return isTextElement() ? getTextElement().getText().length() : 0;
	}

	/**
	 * Get the maximum number of rows of <strong>input</strong> text, i.e. how many
	 * line separators are there, not how many lines of text are actually displayed
	 * after wrapping. Further input will not be accepted being this length and
	 * existing text will be trimmed.
	 * 
	 * @return max rows
	 */
	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Get how the RETURN key is handled.
	 * 
	 * @return return mode
	 */
	public ReturnMode getReturnMode() {
		return returnMode;
	}

	/**
	 * Get the preferred number of rows, used for initial sizing. This does not
	 * restrict how many rows are allowed to be input or wrapped (see
	 * {@link #setMaxRows(int)} for that).
	 * 
	 * @return preferred visual rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Get the currently selected text, or an empty string if nothing is selected.
	 * 
	 * @return selection
	 */
	public String getSelection() {
		return selectionStart < selectionEnd ? text.substring(selectionStart, selectionEnd)
				: text.substring(selectionEnd, selectionStart);
	}

	public int getSelectionEnd() {
		return isTextElement() ? selectionEnd : 0;
	}

	public int getSelectionStart() {
		return isTextElement() ? selectionStart : 0;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public boolean isSelected() {
		return getSelectionStart() != getSelectionEnd();
	}

	public TextArea moveCaretToPoint(float x, float y) {
		int i = getIndexForPoint(x, y);
		if (i == -1)
			if (text == null)
				select(0, 0);
			else
				select(text.length(), text.length());
		else
			select(i, i);
		return this;
	}

	public String parseText(String text) {
		if (text == null)
			text = "";
		if (getMaxLength() > 0 && text.length() > getMaxLength())
			throw new IllegalArgumentException(
					String.format("Too long. There are %d characters, but a maximum of %d is allowed.", text.length(),
							getMaxLength()));

		if (getUnwrappedLineCount(text) > maxRows) {
			throw new IllegalArgumentException(
					String.format("Too many rows. There are %d, but a maximum of %d is allowed.",
							getUnwrappedLineCount(text), maxRows));
		}

		for (AbstractTextField.TextProcessor p : getTextParsers()) {
			text = p.processText(text);
		}
		return text;
	}

	public TextArea select(int selectionStart, int selectionEnd) {
		if (selectionStart != this.selectionStart || selectionEnd != this.selectionEnd) {
			if (selectionStart > text.length())
				selectionStart = text.length();
			if (selectionEnd > text.length())
				selectionEnd = text.length();

			this.selectionStart = selectionStart;
			this.selectionEnd = selectionEnd;
			int s = selectionStart;
			int e = selectionEnd;
			if (e < s) {
				e = selectionStart;
				s = selectionEnd;
			}

			if (s != e && (range == null || !range.isShowing())) {
				if (range == null) {
					invalidate();
					range = new Element(screen) {
						{
							setStyleClass("range range-top");
						}
					};
					attachElement(range);
					range2 = new Element(screen) {
						{
							setStyleClass("range range-middle");
						}
					};
					attachElement(range2);
					range3 = new Element(screen) {
						{
							setStyleClass("range range-middle");
						}
					};
					attachElement(range3);
					validate();
				} else {
					dirtyLayout(false, LayoutType.boundsChange());
				}
			} else if (s == e && range != null && range.isShowing()) {
				range.hide();
				range2.hide();
				range3.hide();
			}
			dirtyLayout(false, LayoutType.children);
			textElement.setSubStringColor(0, text.length(), getFontColor());
			if (s != e) {
				textElement.setSubStringColor(s, e, range.getFontColor());
			}
			layoutChildren();

		}
		return this;
	}

	public TextArea selectAll() {
		select(0, text.length());
		return this;
	}

	public TextArea selectWordAt(float x, float y) {

		int idx = getIndexForPoint(x, y);

		if (idx == -1)
			return this;

		int i;
		int wordStart = 0;
		for (i = idx; i >= 0 && i < text.length(); i--) {
			if (text.charAt(i) == ' ') {
				wordStart = i + 1;
				break;
			}
		}

		int wordEnd = text.length();
		for (i = idx; i < text.length(); i++) {
			if (text.charAt(i) == ' ') {
				wordEnd = i;
				break;
			}
		}

		select(wordStart, wordEnd);

		return this;
	}

	public TextArea setCaretPosition(int pos) {
		select(pos, pos);
		return this;
	}

	public TextArea setCaretPositionToEnd() {
		setCaretPosition(text.length());
		return this;
	}

	public TextArea setCaretPositionToStart() {
		setCaretPosition(0);
		return this;
	}

	/**
	 * Set the maximum number of rows of <strong>input</strong> text, i.e. how many
	 * line separators are there, not how many lines of text are actually displayed
	 * after wrapping. Further input will not be accepted being this length and
	 * existing text will be trimmed.
	 * 
	 * @param maxRows max rows
	 */
	public TextArea setMaxRows(int maxRows) {
		this.maxRows = maxRows;
		return this;
	}

	/**
	 * Set how the RETURN key is handled.
	 * 
	 * @param returnMode return mode
	 * @return this
	 */
	public TextArea setReturnMode(ReturnMode returnMode) {
		this.returnMode = returnMode;
		return this;
	}

	public TextArea setRows(int rows) {
		this.rows = rows;
		return this;
	}

	public TextArea setSelectionEnd(int selectionEnd) {
		select(getSelectionStart(), selectionEnd);
		return this;
	}

	public TextArea setSelectionStart(int selectionStart) {
		select(selectionStart, getSelectionEnd());
		return this;
	}

	@SuppressWarnings("serial")
	@Override
	public BaseElement setText(String text) {
		if (undoManager != null) {
			undoManager.storeAndExecute(new UndoableCommand() {
				private int caret = -1;
				private String was;

				@Override
				public void doCommand() {
					if (caret == -1)
						caret = getSelectionStart();
					else
						select(caret, caret);
					was = TextArea.this.text;
					doSetText(text);
				}

				@Override
				public void undoCommand() {
					int wcaret = caret;
					select(caret, caret);
					caret = wcaret;
					doSetText(was);
				}
			});
		} else {
			doSetText(text);
		}
		return this;
	}

	@Override
	public BaseElement setTextWrap(LineWrapMode wrap) {
		super.setTextWrap(wrap);
		sizeToContent();
		configureParentScroller();
		if (getTextElement() != null)
			getTextElement().updateTextState(false);
		return this;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	protected void configureParentScroller() {
		TextAreaScrollPanel scrollPanel = getScrollParent();
		if (scrollPanel != null) {
			scrollPanel.onKeyboardFocusGained(evt -> activateCaret());
			scrollPanel.onKeyboardFocusLost(evt -> deactivateCaret());
			scrollPanel.onNavigationKey(evt -> {
				if (evt.isPressed())
					keyPressed(evt);
			});
			scrollPanel.onElementEvent(e -> dirtyLayout(false, LayoutType.boundsChange()), Type.RESIZE);
		}
	}

	protected void doSetText(String text) {
		String v = this.text;
		try {
			String parsed = parseText(text);
			if (selectionStart > parsed.length())
				selectionStart = parsed.length();
			if (selectionEnd > parsed.length())
				selectionEnd = parsed.length();
			super.setText(parsed);
		} catch (IllegalArgumentException iae) {
			// Processors can throw this
			String res = onValidationError(text, iae);
			if (res != null)
				super.setText(res);

		}
		if (!Objects.equals(v, this.text)) {
			updateScroller();
			if (changeSupport != null) {
				changeSupport.fireEvent(new UIChangeEvent<AbstractTextField, String>(this, v, this.text));
			}
		}
	}

	protected Vector2f getCaretPosition() {
		int selStart = getSelectionStart();
		Vector4f padding = getAllPadding();
		BaseElement caret = getCaret();
		if (caret != null && caret.getElementParent() != null && isTextElement()) {
			TextElement txt = getTextElement();
			Vector4f pos = null;
			if (selStart == 0)
				pos = new Vector4f(padding.x, padding.z, 0,
						getThemeInstance().getFontInfo(txt.getFont()).getTotalLineHeight());
			else {
				pos = txt.getLetterPositions()[selStart - 1];
			}
			Vector2f pref = caret.calcPreferredSize();
			return new Vector2f(pos.x + pos.z, pos.y + (pref.y - pos.w) / 2f);
		}
		return Vector2f.ZERO;
	}

	protected Vector4f getLetterPos(int index) {
		if (text.length() == 0)
			return Vector4f.ZERO;
		if (index >= text.length() && text.length() > 0) {
			Vector4f p = getTextElement().getLetterPositions()[index - 1].clone();
			p.x += p.z + 1;
			return p;
		} else
			return getTextElement().getLetterPositions()[index];
	}

	protected TextAreaScrollPanel getScrollParent() {
		TextAreaScrollPanel scrollPanel = elementParent == null || elementParent.getElementParent() == null
				|| !(elementParent.getElementParent().getElementParent() instanceof TextAreaScrollPanel) ? null
						: (TextAreaScrollPanel) elementParent.getElementParent().getElementParent();
		return scrollPanel;
	}

	protected void keyPressed(KeyboardUIEvent evt) {

		int start = selectionStart;
		int end = selectionEnd;
		if (end < start) {
			end = selectionStart;
			start = selectionEnd;
		}

		if (evt.getKeyCode() == KeyInput.KEY_Z && evt.isCtrl() && undoManager != null) {
			if (undoManager.isUndoAvailable())
				undoManager.undo();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_Y && evt.isCtrl() && evt.isShift() && undoManager != null) {
			if (undoManager.isRedoAvailable())
				undoManager.redo();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_A && evt.isCtrl()) {
			selectAll();
			evt.setConsumed();
		} else if ((evt.getKeyCode() == KeyInput.KEY_C || evt.getKeyCode() == KeyInput.KEY_X) && evt.isCtrl()) {
			if (isAllowCopy() || isAllowCut())
				ToolKit.get().setClipboardText(getSelection());

			if (evt.getKeyCode() == KeyInput.KEY_X && isAllowCut()) {
				deselect();
				setText(text.substring(0, start) + text.substring(end));
			}

			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_V && evt.isCtrl() && isEditable()) {
			if (isAllowPaste()) {
				String clipboardText = ToolKit.get().getClipboardText();
				if (clipboardText != null) {
					deselect();
					if (text == null)
						setText(clipboardText);
					else if (isSelected()) {
						setText(text.substring(0, start) + clipboardText + text.substring(end));
					} else {
						setText(text.substring(0, start) + clipboardText + text.substring(start));
					}
					setCaretPosition(start + clipboardText.length());
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_INSERT) {
			setMode(getMode() == Mode.INSERT ? Mode.OVERWRITE : Mode.INSERT);
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_BACK) {
			if (isEditable()) {
				if (start == end) {
					if (start > 0) {
						setCaretPosition(start - 1);
						setText(text.substring(0, start - 1) + text.substring(start));
					}
				} else {
					deselect();
					setText(text.substring(0, start) + text.substring(end));
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_DELETE) {
			if (isEditable()) {
				if (start == end) {
					if (start < getLength()) {
						setText(text.substring(0, start) + text.substring(start + 1));
					}
				} else {
					deselect();
					setText(text.substring(0, start) + text.substring(end));
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_HOME) {
			if (!evt.isShift()) {
				if (evt.isCtrl())
					setCaretPosition(0);
				else {
					setCaretPosition(
							Math.min(text.length(), Math.max(0, text.lastIndexOf('\n', Math.max(0, start - 1)) + 1)));
				}
			} else {
				if (evt.isCtrl())
					setSelectionEnd(0);
				else
					setSelectionEnd(
							Math.min(text.length(), Math.max(0, text.lastIndexOf('\n', Math.max(0, start - 1)) + 1)));
			}
			layoutChanged();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_END) {
			if (start < text.length()) {
				int idx = text.indexOf('\n', Math.min(text.length(), start + 1));
				if (idx == -1)
					idx = text.length();
				if (!evt.isShift()) {
					if (evt.isCtrl())
						setCaretPosition(getLength());
					else {
						setCaretPosition(Math.max(0, idx));
					}
				} else {
					if (evt.isCtrl())
						setSelectionEnd(getLength());
					else
						setSelectionEnd(Math.max(0, idx));
				}
			}
			layoutChanged();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_LEFT) {
			if (start > 0) {
				if (evt.isCtrl()) {
					for (int i = selectionEnd - 1; i > -1; i--) {
						if (i == 0 || text.charAt(i - 1) == ' ') {
							if (evt.isShift())
								setSelectionEnd(i);
							else
								setCaretPosition(i);
							break;
						}
					}
				} else if (!evt.isShift())
					setCaretPosition(start - 1);
				else {
					setSelectionEnd(selectionEnd - 1);
				}

			} else if (end > 0) {
				setCaretPosition(start);
			}
			layoutChanged();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_UP && maxRows > 1 && !evt.isCtrl()) {
			if (text.length() > 0) {
				Vector4f pos = getLetterPos(selectionEnd).clone();
				pos.y -= getThemeInstance().getFontInfo(getTextElement().getFont()).getTotalLineHeight();
				if (pos.y >= 0) {
					int idx = getIndexForPoint(pos.x, pos.y);
					if (idx != -1)
						if (evt.isShift())
							setSelectionEnd(idx);
						else
							setCaretPosition(idx);
					else {
						/*
						 * Nothing directly above caret, find last character on line
						 */
						idx = getIndexForPoint(getTotalPaddingOffset().x, pos.y);
						if (idx != -1) {
							Vector4f first = getLetterPos(idx);
							for (int i = idx; i < text.length(); i++) {
								Vector4f p = getLetterPos(i);
								if (first.y != p.y) {
									// Now on next line
									break;
								} else
									idx = i + 1;
							}

							if (evt.isShift())
								setSelectionEnd(idx);
							else
								setCaretPosition(idx);
						} else if (evt.isShift())
							setSelectionEnd(0);
						else
							setCaretPosition(0);
					}
					layoutChanged();
					evt.setConsumed();
				}
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_DOWN && maxRows > 1 && !evt.isCtrl()) {
			if (text.length() > 0) {
				Vector4f pos = getLetterPos(selectionEnd).clone();
				pos.y += getThemeInstance().getFontInfo(getTextElement().getFont()).getTotalLineHeight();
				if (pos.y <= getTextElement().getTotalHeight()) {
					int idx = getIndexForPoint(pos.x, pos.y);
					if (idx != -1)
						if (evt.isShift())
							setSelectionEnd(idx + 1);
						else
							setCaretPosition(idx + 1);
					else {
						/*
						 * Nothing directly below caret, find last character on line
						 */
						idx = getIndexForPoint(getTotalPaddingOffset().x, pos.y);
						if (idx != -1) {
							Vector4f first = getLetterPos(idx);
							for (int i = idx; i < text.length(); i++) {
								Vector4f p = getLetterPos(i);
								if (first.y != p.y) {
									// Now on next line
									break;
								} else
									idx = i + 1;
							}

							if (evt.isShift())
								setSelectionEnd(idx);
							else
								setCaretPosition(idx);
						} else if (evt.isShift())
							setSelectionEnd(text.length());
						else
							setCaretPosition(text.length());
					}
				} else if (evt.isShift())
					setSelectionEnd(text.length());
				else
					setCaretPosition(text.length());
				layoutChanged();
				evt.setConsumed();
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_RIGHT) {
			if (start < text.length()) {
				if (evt.isCtrl()) {
					for (int i = selectionEnd + 1; i <= text.length(); i++) {
						if (i == text.length() || text.charAt(i) == ' ') {
							if (evt.isShift())
								setSelectionEnd(i);
							else
								setCaretPosition(i);
							break;
						}
					}
				} else if (!evt.isShift())
					setCaretPosition(start + 1);
				else {
					setSelectionEnd(selectionEnd + 1);
				}
			}
			layoutChanged();
			evt.setConsumed();
		} else {
			if (isEditable()) {
				char keyChar = evt.getKeyChar();

				if (keyChar > 0) {

					if (keyChar == '\r') {
						switch (returnMode) {
						case IGNORE:
							return;
						case NEWLINE_ON_CTRL_RETURN:
							if (evt.isCtrl())
								keyChar = '\n';
							else
								return;
							break;
						case NEWLINE_ON_SHIFT_RETURN:
							if (evt.isShift())
								keyChar = '\n';
							else
								return;
							break;
						default:
							keyChar = '\n';
							break;
						}
					}

					if (keyChar == '\t') {
						// TODO handling of tabs in AnimText is needed first
						return;
					}

					if (keyChar == 0x1b) {
						// TODO handling of escape in AnimText is needed first
						return;
					}

					if (text == null)
						setText(String.valueOf(keyChar));
					else if (isSelected()) {
						setText(text.substring(0, start) + keyChar + text.substring(end));
					} else {
						if (getMode() == Mode.INSERT || start == text.length())
							setText(text.substring(0, start) + keyChar + text.substring(start));
						else
							setText(text.substring(0, start) + keyChar + text.substring(start + 1));
					}
					setCaretPosition(start + 1);
					evt.setConsumed();
				}
			}
		}
	}

	protected void layoutChanged() {
		dirtyLayout(false, LayoutType.children);
		updateScroller();
	}

	protected void updateScroller() {
		TextAreaScrollPanel scrollPanel = getScrollParent();
		if (scrollPanel != null) {
			scrollPanel.dirtyScrollContent();
			Vector2f caretPosition = getCaretPosition();
			Vector2f caretSize = getCaret().calcPreferredSize();

			float b = scrollPanel.getScrollBoundsWidth() - scrollPanel.getScrollableArea().getTotalPadding().x;
			float u = scrollPanel.getScrollableArea().getX() * -1;
			float r = b + u;
			if (caretPosition.x < u) {
				scrollPanel.scrollXBy(u - caretPosition.x);
			} else if (caretPosition.x > r) {
				scrollPanel.scrollXBy(r - caretPosition.x);
			}

			b = scrollPanel.getScrollBoundsHeight() - scrollPanel.getScrollableArea().getTotalPadding().y;
			u = scrollPanel.getScrollableArea().getY() * -1;
			r = b + u - caretSize.y;

			if (caretPosition.y < u) {
				scrollPanel.scrollYBy(caretPosition.y - u);
			} else if (caretPosition.y > r) {
				scrollPanel.scrollYBy(caretPosition.y - r);
			}
		} else
			layoutChildren();
	}
}
