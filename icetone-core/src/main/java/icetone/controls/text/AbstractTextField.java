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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import icetone.controls.text.AbstractTextField.PasswordFormatter;
import icetone.controls.text.AbstractTextField.TextProcessor;
import icetone.controls.text.AbstractTextField.UpperCaseProcessor;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.StyledContainer;
import icetone.core.Layout.LayoutType;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeListener;

/**
 * Abstract class for text input type controls, such as {@link TextField} and
 * {@link TextArea}. Provides features common across these controls such as
 * insert modes, copy paste support and more.
 *
 */
public abstract class AbstractTextField extends Element implements TextInput {

	protected final class TextAreaCaret extends Element {
		{
			styleClass = "caret";
			useParentPseudoStyles = true;
			setIgnoreMouse(true);
		}

		protected TextAreaCaret(BaseScreen screen) {
			super(screen);
		}

//		@SuppressWarnings("unchecked")
//		public Vector2f calcUnboundedPreferredSize() {
//			/*
//			 * This is very similar to BaseElement.calcUnboundedPreferredSize() except the
//			 * height is based on the height of the current caret character instead of the
//			 * parent height.  
//			 */
//			Vector2f pref = null;
//			ElementContainer<?, ?> container = getParentContainer();
//			if (container != null) {
//				Vector2f csz = new Vector2f(container.getDimensions().x,
//						TextArea.this.getFixedLineHeight() == 0
//								? getThemeInstance().getFontInfo(BaseElement.calcFont(this)).getTotalLineHeight()
//								: TextArea.this.getFixedLineHeight()).subtractLocal(getTotalPadding());
//				Vector2f layoutMax = null;
//
//				if (prefDimensions != null) {
//					pref = new Vector2f();
//
//					switch (prefDimensions.xUnit) {
//					case AUTO:
//						layoutMax = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).preferredSize(this);
//						if (layoutMax != null)
//							pref.x = layoutMax.x;
//						break;
//					case PX:
//						pref.x = prefDimensions.x;
//						break;
//					case PERCENT:
//						pref.x = csz.x * (prefDimensions.x / 100f);
//						break;
//					default:
//						throw new UnsupportedOperationException(
//								String.format("Unit X type of %s is not supported.", prefDimensions.xUnit));
//					}
//
//					switch (prefDimensions.yUnit) {
//					case AUTO:
//						if (layoutMax == null)
//							layoutMax = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).preferredSize(this);
//						if (layoutMax != null)
//							pref.y = layoutMax.y;
//						break;
//					case PX:
//						pref.y = prefDimensions.y;
//						break;
//					case PERCENT:
//						pref.y = csz.y * (prefDimensions.y / 100f);
//						break;
//					default:
//						throw new UnsupportedOperationException(
//								String.format("Unit Y type of %s is not supported.", prefDimensions.yUnit));
//					}
//				} else {
//					pref = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).preferredSize(this);
//				}
//			}
//			if (pref == null) {
//				pref = Vector2f.ZERO;
//			}
//			return pref.clone();
//		}
	}

	/**
	 * Interface to implement to provide either a parser or a formatter to apply to
	 * text input. Parsers check the text for validity and possibly reformat it (the
	 * value that can be retrieved by {@link #getText}. Formatters allow a different
	 * value to be displayed to what is actually stored. This can be used for
	 * example to provide password masking ({@link AbstractTextField.PasswordFormatter}).
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

	/**
	 * Formatter or parser that turns all text to upper case.
	 */
	public static class UpperCaseProcessor implements TextProcessor {
		@Override
		public String processText(String input) {
			return input.toUpperCase();
		}
	}

	protected ChangeSupport<AbstractTextField, String> changeSupport;

	private List<Mode> allowedModes = Arrays.asList(Mode.values());
	private Element caret;
	private int characterLength;
	private boolean copy = true, paste = true, cut = true;
	private boolean editable = true;
	private List<AbstractTextField.TextProcessor> formatters = new ArrayList<>();
	private int maxLength = 0;
	private Mode mode = Mode.INSERT;
	private BaseElement overlay;
	private List<AbstractTextField.TextProcessor> parsers = new ArrayList<>();

	public AbstractTextField() {
	}

	public AbstractTextField(BaseScreen screen) {
		super(screen);
	}

	public AbstractTextField(String text) {
		super(text);
	}

	public AbstractTextField addTextFormatter(AbstractTextField.TextProcessor textProcessor) {
		formatters.add(textProcessor);
		return this;
	}

	/**
	 * Text parsers are used to validate keyboard input and can be used for a
	 * variety of things. See {@link AbstractTextField.UpperCaseProcessor}
	 * 
	 * @param textProcessor
	 * @return
	 */
	public AbstractTextField addTextParser(AbstractTextField.TextProcessor textProcessor) {
		parsers.add(textProcessor);
		return this;
	}

	/**
	 * Get which text insert modes are enabled
	 * 
	 * @return allowed modes
	 */
	public List<Mode> getAllowedModes() {
		return allowedModes;
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
	 * Get the preferred width in characters.
	 * 
	 * @param characterLength
	 */
	@Override
	public int getCharacterWidth() {
		return characterLength;
	}

	/**
	 * Returns the maximum number of characters the content may be.
	 * 
	 * @return maximum length of text content
	 */
	@Override
	public int getMaxLength() {
		return this.maxLength;
	}

	/**
	 * Get the current text insertion mode
	 * 
	 * @return mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Get the element that provides that overlay, used for focus highlights.
	 * 
	 * @return overlay element
	 */
	public BaseElement getOverlay() {
		return overlay;
	}

	public List<AbstractTextField.TextProcessor> getTextFormatters() {
		return formatters;
	}

	public List<AbstractTextField.TextProcessor> getTextParsers() {
		return parsers;
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
	 * Returns if the Cut feature is enabled/disabled
	 * 
	 * @return cut
	 */
	public boolean isAllowCut() {
		return this.cut;
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
	 * Get if the text is actually editable or not. A non-editable text field may
	 * still be selected and copy key may be used.
	 * 
	 * @return editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Add a listener to be invoked when the text content changes.
	 * 
	 * @param listener listener to add
	 * @return this for chaining
	 */
	public AbstractTextField onChange(UIChangeListener<AbstractTextField, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	/**
	 * Remove a listener that is invoked when the text content changes.
	 * 
	 * @param listener listener to remove
	 * @return this for chaining
	 */
	public AbstractTextField removeChangeListener(UIChangeListener<AbstractTextField, String> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
	}

	/**
	 * Enables/disables the use of the Copy text feature
	 * 
	 * @param copy boolean
	 * @return this for chaining
	 */
	public AbstractTextField setAllowCopy(boolean copy) {
		this.copy = copy;
		return this;
	}

	/**
	 * Enables/disables both the Copy and Paste feature
	 * 
	 * @param copyAndPaste whether copy and paster are enabled or not
	 * @return this for chaining
	 */
	public AbstractTextField setAllowCopyAndPaste(boolean copyAndPaste) {
		this.copy = copyAndPaste;
		this.paste = copyAndPaste;
		return this;
	}

	/**
	 * Enables/disables the use of the Cut text feature
	 * 
	 * @param copy boolean
	 * @return this for chaining
	 */
	public AbstractTextField setAllowCut(boolean cut) {
		this.cut = cut;
		return this;
	}

	/**
	 * Set which text insert modes are enabled
	 * 
	 * @param allowedModes allowed modes
	 * @return this for chaining
	 */
	public AbstractTextField setAllowedModes(Mode... allowedModes) {
		List<Mode> l = Arrays.asList(allowedModes);
		if (!Objects.equals(this.allowedModes, l)) {
			this.allowedModes = l;
			if (!l.contains(mode) && !l.isEmpty()) {
				setMode(l.get(0));
			}
		}
		return this;
	}

	/**
	 * Eanbles/disables use of the Paste text feature
	 * 
	 * @param paste boolean
	 * @return this for chaining
	 */
	public AbstractTextField setAllowPaste(boolean paste) {
		this.paste = paste;
		return this;
	}

	/**
	 * Set the preferred width in characters.
	 * 
	 * @param characterLength
	 * @return this for chaining
	 */
	public AbstractTextField setCharacterLength(int length) {
		if (length != this.characterLength) {
			this.characterLength = length;
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
		return this;
	}

	/**
	 * Get if the text is actually editable or not. A non-editable text field may
	 * still be selected and copy key may be used.
	 * 
	 * @param editable editable
	 * @return this for chaining
	 */
	public AbstractTextField setEditable(boolean editable) {
		if (editable != this.editable) {
			this.editable = editable;
			if (!editable && caret.isShowing())
				deactivateCaret();
			else if (editable && !caret.isShowing())
				activateCaret();
			dirtyLayout(true, LayoutType.all);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Set the maximum length in characters the text content may be. A value of zero
	 * indicates there is no limit.
	 * 
	 * @param maxLength maximum length of input or zero for no limit
	 * @return this for chaining
	 */
	public AbstractTextField setMaxLength(int maxLength) {
		if (maxLength != this.maxLength) {
			this.maxLength = maxLength;
			if (maxLength > 0 && text.length() > maxLength) {
				setText(text.substring(0, maxLength));
			}
		}
		return this;
	}

	/**
	 * Set the current text insertion mode
	 * 
	 * @param mode mode
	 * @return this for chaining
	 */
	public AbstractTextField setMode(Mode mode) {
		if (!Objects.equals(mode, this.mode)) {
			this.mode = mode;
			setCaretStyles();
			dirtyLayout(true, LayoutType.children);
			layoutChildren();
		}
		return this;
	}

	protected String onValidationError(String text, IllegalArgumentException ae) {
		return null;
	}

	protected void activateCaret() {
		if (editable && !caret.isShowing() && isKeyboardFocussed()) {
			if (caret.getElementParent() == null)
				showElement(caret);
			else
				caret.show();
			if (overlay.getElementParent() == null)
				showElement(overlay);
			else
				overlay.show();
		}
	}

	@Override
	protected final void configureStyledElement() {

		setTextOnTop(true);
		setHoverable(true);
		setKeyboardFocusable(true);

		configureStyledTextElement();

		/* Overlay */
		overlay = new StyledContainer(screen) {
			{
				styleClass = "overlay";
				useParentPseudoStyles = true;
			}
		};
		overlay.hide();
		overlay.setIgnoreMouse(true);
		addElement(overlay);

		/* Caret */
		caret = new TextAreaCaret(screen);
		setCaretStyles();
		caret.hide();
		addElement(caret);

		postConfigureStyledTextElement();
	}

	protected void configureStyledTextElement() {
	}

	protected void deactivateCaret() {
		if (caret != null && caret.isShowing())
			caret.hide();
		if (overlay != null && overlay.isShowing())
			overlay.hide();
	}

	protected void postConfigureStyledTextElement() {
	}

	protected void setCaretStyles() {
		if (Mode.OVERWRITE.equals(getMode())) {
			caret.removeStyleClass("insert-mode");
			caret.addStyleClass("overwrite-mode");
		} else {
			caret.removeStyleClass("overwrite-mode");
			caret.addStyleClass("insert-mode");
		}
	}
}
