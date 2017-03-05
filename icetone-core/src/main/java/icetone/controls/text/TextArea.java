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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.ToolKit;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.ChangeSupport;
import icetone.core.event.KeyboardUIEvent;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.layout.WrappingLayout;
import icetone.core.undo.UndoManager;
import icetone.core.undo.UndoableCommand;
import icetone.core.utils.BitmapTextUtil;
import icetone.core.utils.MathUtil;
import icetone.framework.core.AnimText;

/**
 * A multi-line, scrollable text entry component that has more features than a
 * simple {@link TextField}. This control handles selection (multiple lines) and
 * a simple but flexible system for either validating or processing text input.
 * It also provides styling capability for the caret and selection block,
 * something not possible with a {@link TextField}, but this is at the expense
 * of up to 4 more nodes in the scene graph per text area.
 * 
 * @author rockfire
 */
public class TextArea extends Element implements TextInput {

	/**
	 * Parser that only allows strings that comply with a {@link DateFormat}.
	 */
	public static class DateFormatProcessor implements TextProcessor {

		private DateFormat fmt;

		public DateFormatProcessor(DateFormat fmt) {
			this.fmt = fmt;
		}

		@Override
		public String processText(String input) {
			try {
				Date d = fmt.parse(input);
				return fmt.format(d);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Invalid format.");
			}
		}

	}

	/**
	 * Parser or formatter that turns text into lower case.
	 */
	public static class LowerCaseProcessor implements TextProcessor {
		@Override
		public String processText(String input) {
			return input.toLowerCase();
		}
	}

	/**
	 * Parser that only allows numbers that comply with a {@link NumberFormat}.
	 */
	public static class NumberFormatProcessor implements TextProcessor {

		private NumberFormat fmt;

		public NumberFormatProcessor(NumberFormat fmt) {
			this.fmt = fmt;
		}

		@Override
		public String processText(String input) {
			try {
				Number n = fmt.parse(input);
				return fmt.format(n);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Invalid format.");
			}
		}

	}

	/**
	 * Formatter that masks a password.
	 */
	public static class PasswordFormatter implements TextProcessor {

		private final char echoChar;

		public PasswordFormatter() {
			this('*');
		}

		public PasswordFormatter(char echoChar) {
			this.echoChar = echoChar;
		}

		@Override
		public String processText(String input) {
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < input.length(); i++) {
				b.append(echoChar);
			}
			return b.toString();
		}
	}

	public enum ReturnMode {
		IGNORE, NEWLINE_ON_CTRL_RETURN, NEWLINE_ON_RETURN, NEWLINE_ON_SHIFT_RETURN
	}

	/**
	 * Interface to implement to provide either a parser or a formatter to apply
	 * to text input. Parsers check the text for validity and possibly reformat
	 * it (the value that can be retrieved by {@link #getText}. Formatters allow
	 * a different value to be displayed to what is actually stored. This can be
	 * used for example to provide password masking ({@link PasswordFormatter}).
	 *
	 */
	public interface TextProcessor {
		/**
		 * Parse or format text
		 * 
		 * @param input
		 * @return output
		 */
		String processText(String input);
	}

	/**
	 * Formatter or parser that turns all text to upper case.
	 */
	public static class UpperCaseProcessor implements TextProcessor {
		@Override
		public String processText(String input) {
			return input.toUpperCase();
		}
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
			if (rows == 0 || getScrollParent() != null) {
				return MathUtil.largest(super.calcTextSize(parent, inWidth), defaultCalcTextSize(parent, inWidth));
			} else {
				String str = parent.getText();
				if (columns > 0) {
					str = "";
					for (int i = 0; i < columns; i++) {
						str += "W";
					}
				}
				Vector2f sz = BitmapTextUtil.getTextTotalSize(parent, str, Short.MAX_VALUE);
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

			if (caret != null && caret.getElementParent() != null && childElement.getTextElement() != null) {
				AnimText txt = childElement.getTextElement();
				Vector4f pos = null;
				if (selStart == 0)
					pos = new Vector4f(0, 0, 0, txt.getLineHeight());
				else {
					pos = txt.getLetterPositions()[selStart - 1];
				}
				Vector2f pref = caret.calcPreferredSize();
				caret.setBounds(pos.x + pos.z + padding.x, padding.z + pos.y + (pref.y - pos.w) / 2f, pref.x, pref.y);

				if (selStart != selEnd) {
					Vector4f rpad1 = range.getAllPadding();
					Vector4f rpad2 = range2.getAllPadding();
					Vector4f rpad3 = range3.getAllPadding();
					Vector4f start = txt.getLetterPositions()[selStart];
					Vector4f end = txt.getLetterPositions()[selEnd - 1];
					float lh = getTextElement().getLineHeight();

					Vector4f r1 = new Vector4f(start.x - rpad1.x + padding.x, start.y + padding.z, 0,
							start.w + rpad1.z + rpad1.w);
					if (start.y == end.y) {
						// Start and end are on same lines
						r1.z = end.x + end.z - start.x + rpad1.x + rpad1.y;
					} else {
						// Start and end are on different lines, run to end of
						// line
						r1.z = getWidth() - rpad1.x - rpad1.y - start.x;
					}

					range.setBounds(r1);
					range.show();

					if (start.y != end.y) {
						Vector4f r2 = new Vector4f(-rpad1.x + padding.x, r1.y + lh, 0, 0);
						// Start and end are on different lines

						if (start.y == end.y - lh) {
							// End is on this row
							r2.z = end.x + end.z + rpad2.x + rpad2.y;
							r2.w = end.w + rpad2.z + rpad2.w;
						} else {
							// End is at least next row away
							r2.z = getWidth() - rpad1.x - rpad1.y;
							r2.w = end.y - r1.y - lh + rpad2.z + rpad2.w + rpad3.z + rpad2.z;
						}

						range2.setBounds(r2);
						range2.show();

						if (start.y != end.y - lh) {
							// End is at least on 2nd row
							Vector4f r3 = new Vector4f(-rpad1.x + padding.x, end.y + rpad3.z + rpad2.z + rpad2.w,
									end.x + end.z + rpad3.x + rpad3.y, end.w + rpad3.z + rpad3.w);

							range3.setBounds(r3);
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
	}

	class TextScrollLayout extends WrappingLayout {
		TextScrollLayout() {
			setOrientation(Orientation.HORIZONTAL);
			setEqualSizeCells(true);
			setWidth(1);
			setFill(false);
		}
	}

	private Element caret;
	private ChangeSupport<TextArea, String> changeSupport;
	private int columns;
	private boolean copy = true, paste = true;
	private boolean editable = true;
	private List<TextProcessor> formatters = new ArrayList<>();
	private int maxLength = Integer.MAX_VALUE;
	private int maxRows = Integer.MAX_VALUE;
	private boolean mouseDown;
	private List<TextProcessor> parsers = new ArrayList<>();
	private Element range;
	private Element range2;
	private Element range3;
	private ReturnMode returnMode = ReturnMode.NEWLINE_ON_RETURN;
	private int rows;
	private int selectionEnd;
	private int selectionStart;
	private UndoManager undoManager = new UndoManager();

	{
		setLayoutManager(new TextAreaLayout());
		setKeyboardFocusable(true);
		setMouseFocusable(true);
		setTextOnTop(true);
		// setTextWrap(LineWrapMode.Character);
		setText("");
		setTextVAlign(VAlign.Top);

		onMousePressed(evt -> {
			mouseDown = true;
			if (evt.getClicks() == 2) {
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
		onKeyboardPressed(evt -> keyPressed(evt));
		onKeyboardReleased(evt -> keyReleased(evt));
	}

	public TextArea() {
		super();
	}

	public TextArea(BaseScreen screen) {
		super(screen);
	}

	public TextArea(BaseScreen screen, String UID) {
		super(screen, UID);
	}

	public TextArea(String text) {
		super(text);
	}

	public TextArea addChangeListener(UIChangeListener<TextArea, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public TextArea addTextFormatter(TextProcessor textProcessor) {
		formatters.add(textProcessor);
		return this;
	}

	/**
	 * Text parsers are used to validate keyboard input and can be used for a
	 * variety of things. See {@link UpperCaseProcessor}
	 * 
	 * @param textProcessor
	 * @return
	 */
	public TextArea addTextParser(TextProcessor textProcessor) {
		parsers.add(textProcessor);
		return this;
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
		for (TextProcessor p : formatters) {
			text = p.processText(text);
		}
		return text;
	}

	@Override
	public BaseElement getCaret() {
		return caret;
	}

	@Override
	public int getCharacterWidth() {
		return columns;
	}

	/**
	 * Get the index of the character in the text given a relative coordinate in
	 * the element.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return character index
	 */
	public int getIndexForPoint(float x, float y) {
		Vector4f c;
		Vector4f[] l = getTextElement().getLetterPositions();
		for (int i = 0; i < l.length; i++) {
			c = l[i];
			if (x >= c.x && x < c.x + c.z && y >= c.y && y < c.y + c.w) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getLength() {
		return getTextElement() == null ? 0 : getTextElement().getText().length();
	}

	@Override
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * Get the maximum number of rows of <strong>input</strong> text, i.e. how
	 * many line separators are there, not how many lines of text are actually
	 * displayed after wrapping. Further input will not be accepted being this
	 * length and existing text will be trimmed.
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
	 * Get the currently selected text, or an empty string if nothing is
	 * selected.
	 * 
	 * @return selection
	 */
	public String getSelection() {
		return text.substring(selectionStart, selectionEnd);
	}

	public int getSelectionEnd() {
		return getTextElement() == null ? 0 : selectionEnd;
	}

	public int getSelectionStart() {
		return getTextElement() == null ? 0 : selectionStart;
	}

	public List<TextProcessor> getTextFormatters() {
		return formatters;
	}

	public List<TextProcessor> getTextParsers() {
		return parsers;
	}

	public UndoManager getUndoManager() {
		return undoManager;
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
	 * Returns if the Paste feature is enabled/disabled
	 * 
	 * @return paste
	 */
	public boolean isAllowPaste() {
		return this.paste;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isSelected() {
		return getSelectionStart() != getSelectionEnd();
	}

	public TextArea moveCaretToPoint(float x, float y) {
		int i = getIndexForPoint(x, y);
		if (i == -1)
			select(text.length(), text.length());
		else
			select(i, i);
		return this;
	}

	public TextArea onChange(UIChangeListener<TextArea, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public String parseText(String text) {
		if (text.length() > maxLength)
			throw new IllegalArgumentException(String.format(
					"Too long. There are %d characters, but a maximum of %d is allowed.", text.length(), maxLength));

		if (BitmapTextUtil.getUnwrappedLineCount(text) > maxRows) {
			throw new IllegalArgumentException(
					String.format("Too many rows. There are %d, but a maximum of %d is allowed.",
							textElement.getUnwrappedLineCount(), maxRows));
		}

		for (TextProcessor p : parsers) {
			text = p.processText(text);
		}
		return text;
	}

	public TextArea removeChangeListener(UIChangeListener<TextArea, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
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

	/**
	 * Enables/disables the use of the Copy text feature
	 * 
	 * @param copy
	 *            boolean
	 */
	public TextArea setAllowCopy(boolean copy) {
		this.copy = copy;
		return this;
	}

	/**
	 * Enables/disables both the Copy and Paste feature
	 * 
	 * @param copyAndPaste
	 *            boolean
	 */
	public TextArea setAllowCopyAndPaste(boolean copyAndPaste) {
		this.copy = copyAndPaste;
		this.paste = copyAndPaste;
		return this;
	}

	/**
	 * Eanbles/disables use of the Paste text feature
	 * 
	 * @param paste
	 *            boolean
	 */
	public TextArea setAllowPaste(boolean paste) {
		this.paste = paste;
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

	public TextArea setCharacterLength(int columns) {
		this.columns = columns;
		return this;
	}

	public TextArea setEditable(boolean editable) {
		this.editable = editable;
		return this;
	}

	public TextArea setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		setText(getText());
		return this;
	}

	/**
	 * Set the maximum number of rows of <strong>input</strong> text, i.e. how
	 * many line separators are there, not how many lines of text are actually
	 * displayed after wrapping. Further input will not be accepted being this
	 * length and existing text will be trimmed.
	 * 
	 * @param maxRows
	 *            max rows
	 */
	public TextArea setMaxRows(int maxRows) {
		this.maxRows = maxRows;
		return this;
	}

	/**
	 * Set how the RETURN key is handled.
	 * 
	 * @param returnMode
	 *            return mode
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
		getTextElement().rewrap();
		return this;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	protected void activateCaret() {
		if (caret == null) {
			caret = new Element(screen);
			caret.setStyleClass("caret");
		}
		if (caret.getElementParent() == null)
			showElement(caret);
		else
			caret.show();
	}

	protected void configureParentScroller() {
		ScrollPanel scrollPanel = getScrollParent();
		if (scrollPanel != null) {
			// switch (getTextWrap()) {
			// case Word:
			// case Character:
			scrollPanel.setHorizontalScrollBarMode(ScrollBarMode.Never);
			// break;
			// default:
			// scrollPanel.setHorizontalScrollBarMode(ScrollBarMode.Auto);
			// break;
			// }
			scrollPanel.setKeyboardFocusable(true);
			scrollPanel.setMouseFocusable(true);
			scrollPanel.setVerticalScrollBarMode(ScrollBarMode.Auto);
			scrollPanel.setScrollContentLayout(new TextScrollLayout());
			scrollPanel.onKeyboardFocusGained(evt -> activateCaret());
			scrollPanel.getScrollableArea().setUseParentPseudoStyles(true);
			scrollPanel.getScrollBounds().setUseParentPseudoStyles(true);
			scrollPanel.onKeyboardFocusLost(evt -> deactivateCaret());
			scrollPanel.onKeyboardPressed(evt -> keyPressed(evt));
			scrollPanel.onKeyboardReleased(evt -> keyReleased(evt));
			scrollPanel.onElementEvent(e -> dirtyLayout(false, LayoutType.boundsChange()), Type.RESIZE);
		}
	}

	protected void deactivateCaret() {
		if (caret != null && caret.isShowing())
			caret.hide();
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
				changeSupport.fireEvent(new UIChangeEvent<TextArea, String>(this, v, this.text));
			}
		}
	}

	protected Vector2f getCaretPosition() {
		int selStart = getSelectionStart();
		Vector4f padding = getAllPadding();
		if (caret != null && caret.getElementParent() != null && getTextElement() != null) {
			AnimText txt = getTextElement();
			Vector4f pos = null;
			if (selStart == 0)
				pos = new Vector4f(padding.x, padding.z, 0, txt.getLineHeight());
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

	protected ScrollPanel getScrollParent() {
		return elementParent == null || elementParent.getElementParent() == null
				|| !(elementParent.getElementParent().getElementParent() instanceof ScrollPanel) ? null
						: (ScrollPanel) elementParent.getElementParent().getElementParent();
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
			if (copy)
				ToolKit.get().setClipboardText(getSelection());

			if (evt.getKeyCode() == KeyInput.KEY_X) {
				deselect();
				setText(text.substring(0, start) + text.substring(end));
			}

			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_V && editable) {
			if (paste) {
				String clipboardText = ToolKit.get().getClipboardText();
				if (clipboardText != null) {
					deselect();
					if (isSelected()) {
						setText(text.substring(0, start) + clipboardText + text.substring(end));
					} else {
						setText(text.substring(0, start) + clipboardText + text.substring(start));
					}
					setCaretPosition(start + clipboardText.length());
				}
			}
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_BACK) {
			if (editable) {
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
			if (editable) {
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
			if (!evt.isShift())
				setCaretPosition(0);
			else
				setSelectionStart(0);
			layoutChanged();
			evt.setConsumed();
		} else if (evt.getKeyCode() == KeyInput.KEY_END) {
			if (start < text.length()) {
				if (!evt.isShift())
					setCaretPosition(getLength());
				else
					setSelectionEnd(getLength());
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
				if (!isSelected()) {
					Vector4f pos = getLetterPos(selectionStart).clone();
					pos.y -= getTextElement().getLineHeight();
					if (pos.y > 0) {
						int idx = getIndexForPoint(pos.x, pos.y);
						if (idx != -1)
							setCaretPosition(idx);
						else {
							/*
							 * Nothing directly above caret, find last character
							 * on line
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

								setCaretPosition(idx);
							} else
								setCaretPosition(0);
						}
						layoutChanged();
						evt.setConsumed();
					}
				}
			}
		} else if (evt.getKeyCode() == KeyInput.KEY_DOWN && maxRows > 1 && !evt.isCtrl()) {
			if (text.length() > 0) {
				if (!isSelected()) {
					Vector4f pos = getLetterPos(selectionStart).clone();
					pos.y += getTextElement().getLineHeight();
					if (pos.y < getTextElement().getTotalHeight()) {
						int idx = getIndexForPoint(pos.x, pos.y);
						if (idx != -1)
							setCaretPosition(idx + 1);
						else {
							/*
							 * Nothing directly below caret, find last character
							 * on line
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

								setCaretPosition(idx);
							} else
								setCaretPosition(text.length());
						}
					} else
						setCaretPosition(text.length());
					layoutChanged();
					evt.setConsumed();
				}
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
			if (editable) {
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
					
					if(keyChar == '\t') {
						// TODO handling of tabs in AnimText is needed first
						return;
					}
					
					if(keyChar == 0x1b) {
						// TODO handling of tabs in AnimText is needed first
						return;
					}

					if (isSelected()) {
						setText(text.substring(0, start) + keyChar + text.substring(end));
					} else {
						setText(text.substring(0, start) + keyChar + text.substring(start));
					}
					setCaretPosition(start + 1);
					evt.setConsumed();
				}
			}
		}
	}

	protected void keyReleased(KeyboardUIEvent evt) {
	}

	protected void layoutChanged() {
		dirtyLayout(false, LayoutType.children);
		updateScroller();
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		configureParentScroller();
	}

	protected String onValidationError(String text, IllegalArgumentException ae) {
		return null;
	}

	protected void updateScroller() {
		ScrollPanel scrollPanel = getScrollParent();
		if (scrollPanel != null) {
			scrollPanel.dirtyScrollContent();
			Vector2f caretPosition = getCaretPosition();
			Vector2f caretSize = caret.calcPreferredSize();

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
