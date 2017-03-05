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
package icetone.extras.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.event.ChangeSupport;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class Console extends Panel {

	public class OutputMessage {

		private String msg;
		private String type;

		public OutputMessage(String msg, String type) {
			this.msg = msg;
			this.type = type;
		}

		public String getMsg() {
			return this.msg;
		}

		public String getType() {
			return type;
		}
	}

	public static final String TYPE_ERROR = "error";
	public static final String TYPE_STANDARD = "standard";

	private PushButton btnCommandExecute;
	private ChangeSupport<Console, String> changeSupport = new ChangeSupport<>();
	private Form chatForm;
	private boolean clearInputOnHide = true;
	private List<String> commandHistory = new ArrayList<String>();
	private TextField commandInput;
	private int currentHistoryIndex;
	private List<Label> displayMessages = new ArrayList<>();
	private boolean hideOnLoseFocus;
	private int outputHistorySize = 500;
	private ScrollPanel saConsoleArea;
	private int sendKey;

	private String textBeforeSearch;

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Console(BaseScreen screen) {
		this(screen, -1);
	}

	/**
	 * Creates a new instance of the XConsole control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param float
	 *            the number of pixels to drop down from the top of the screen
	 */
	public Console(BaseScreen screen, float height) {
		super(screen);

		if (height > -1) {
			prefDimensions = new Size(screen.getWidth(), height);
		}

		setMovable(false);
		setResizable(true);

		chatForm = new Form(screen);

		saConsoleArea = new ScrollPanel(screen);
		saConsoleArea.setStyleClass("console-area");
		saConsoleArea.setHorizontalScrollBarMode(ScrollBarMode.Never);

		final WrappingLayout wrapLayout = (WrappingLayout) saConsoleArea.getScrollContentLayout();
		wrapLayout.setOrientation(Orientation.HORIZONTAL);
		wrapLayout.setFill(true);

		saConsoleArea.setResizable(false);

		// Input
		commandInput = new TextField(screen);
		commandInput.onKeyboardReleased(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_ESCAPE) {
				onEscape();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
				previousHistory();
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
				nextHistory();
				evt.setConsumed();
			} else if (evt.getKeyCode() == sendKey) {
				if (commandInput.getText().length() > 0) {
					sendMsg();
				}
				evt.setConsumed();
			}
		});
		commandInput.onKeyboardFocusLost(evt -> {
			if (hideOnLoseFocus && (evt.getOther() == null
					|| (evt.getOther() != Console.this && !evt.getOther().isDescendantOf(Console.this)))) {
				hide();
				evt.setConsumed();
			}
		});
		chatForm.addFormElement(commandInput);

		// Execute
		btnCommandExecute = new PushButton(screen, "Execute") {
			{
				setStyleClass("fancy");
			}
		};
		btnCommandExecute.onMouseReleased(evt -> sendMsg());
		chatForm.addFormElement(btnCommandExecute);

		// This
		setLayoutManager(new MigLayout(screen, "wrap 2, fill", "[grow][]", "[grow][shrink 0]"));
		addElement(saConsoleArea, "span 2, wrap, growx, growy");
		addElement(commandInput, "growx");
		addElement(btnCommandExecute);
		addClippingLayer(this);
		setResizeE(false);
		setResizeW(false);

		// Focus on show
		onElementEvent(evt -> commandInput.focus(), Type.SHOWN);

	}

	public void onChange(UIChangeListener<Console, String> changeListener) {
		changeSupport.bind(changeListener);
	}

	public void unbind(UIChangeListener<Console, String> changeListener) {
		changeSupport.unbind(changeListener);
	}

	public void addChangeListener(UIChangeListener<Console, String> changeListener) {
		changeSupport.addListener(changeListener);
	}

	public void removeChangeListener(UIChangeListener<Console, String> changeListener) {
		changeSupport.removeListener(changeListener);
	}

	/**
	 * Clear the command history
	 */
	public void clearHistory() {
		commandHistory.clear();
	}

	/**
	 * Clear the current text input. Will also reset any current history search.
	 *
	 * @param string
	 */
	public void clearInput() {
		commandInput.setText("");
		currentHistoryIndex = -1;
		textBeforeSearch = null;
	}

	public TextField getChatInput() {
		return this.commandInput;
	}

	/**
	 * Get the console area.
	 *
	 * @return console area
	 */
	public ScrollPanel getConsoleArea() {
		return saConsoleArea;
	}

	/**
	 * Get the maximum number of output messages that will be kept
	 *
	 * @return output history size
	 */
	public int getOutputHistorySize() {
		return outputHistorySize;
	}

	@Override
	public BaseElement hide() {
		if (clearInputOnHide) {
			commandInput.setText("");
		}
		return super.hide();
	}

	public boolean isClearInputOnHide() {
		return clearInputOnHide;
	}

	/**
	 * Get whether the console should be hidden which focus is lost from the
	 * text field. Default value <code>false</code>.
	 *
	 * @return hide on lose focus
	 */
	public boolean isHideOnLoseFocus() {
		return hideOnLoseFocus;
	}

	public void nextHistory() {
		if (commandHistory.size() > 0) {
			if (textBeforeSearch != null) {
				currentHistoryIndex++;
				if (currentHistoryIndex >= commandHistory.size()) {
					// Scrolled back to originally typed type
					commandInput.setText(textBeforeSearch);
					textBeforeSearch = null;
					currentHistoryIndex = -1;
				} else {
					commandInput.setText(commandHistory.get(currentHistoryIndex));
				}
			}
		}
	}

	/**
	 * Invoked when the text entry field is 'escaped' from.
	 *
	 */
	public void onEscape() {
		commandInput.defocus();
	}

	/**
	 * Call this method to display a standard message
	 *
	 * @param msg
	 *            The String message to display
	 */
	public void output(String msg) {
		output(TYPE_STANDARD, msg);
	}

	/**
	 * Call this method to display a message
	 *
	 * @param type
	 *            The type of message. There must be a style with the same name
	 *            in your style map.
	 * @param msg
	 *            The String message to display
	 */
	public void output(String type, String msg) {
		BaseElement wasFocussed = screen.getKeyboardFocus();
		final OutputMessage outputMessage = new OutputMessage(msg, type);
		if (saConsoleArea.getScrollableArea().getElements().size() > outputHistorySize) {
			saConsoleArea.getScrollableArea().removeElement(saConsoleArea.getScrollableArea().getElements().get(0));
		}
		addMessage(outputMessage);
		saConsoleArea.scrollToBottom();
		if (wasFocussed != null && wasFocussed.equals(commandInput)) {
			screen.setKeyboardFocus(commandInput);
		}
	}

	/**
	 * Call this method to display an error message
	 *
	 * @param msg
	 *            The String message to display
	 */
	public void outputError(String msg) {
		output(TYPE_ERROR, msg);
	}

	public void previousHistory() {
		if (commandHistory.size() > 0) {
			if (textBeforeSearch == null) {
				textBeforeSearch = commandInput.getText();
				currentHistoryIndex = commandHistory.size();
			}
			currentHistoryIndex--;
			if (currentHistoryIndex < 0) {
				currentHistoryIndex = 0;
			}
			commandInput.setText(commandHistory.get(currentHistoryIndex));
		}
	}

	public void setClearInputOnHide(boolean clearInputOnHide) {
		this.clearInputOnHide = clearInputOnHide;
	}

	/**
	 * Sets the keyboard key code to execute commands (in place of the execute
	 * button)
	 *
	 * @param executeKey
	 *            key code to execute command
	 * @see KeyInputEvent
	 */
	public void setExecuteKey(int sendKey) {
		this.sendKey = sendKey;
	}

	/**
	 * GSt whether the console should be hidden which focus is lost from the
	 * text field. Default value <code>false</code>.
	 *
	 * @param hideOnLoseFocus
	 *            hide on lose focus
	 */
	public void setHideOnLoseFocus(boolean hideOnLoseFocus) {
		this.hideOnLoseFocus = hideOnLoseFocus;
	}

	/**
	 * Set the maximum number of output messages that will be kept
	 *
	 * @return output history size
	 */
	public void setOutputHistorySize(int outputHistorySize) {
		this.outputHistorySize = outputHistorySize;
	}

	/**
	 * Set the Execute button text
	 *
	 * @param executeButtonText
	 *            Execute button text
	 */
	public void setTextExecuteButton(String executeButtonText) {
		btnCommandExecute.setText(executeButtonText);
	}

	/**
	 * Sets the ToolTip text to display for mouse focus of the Execute button
	 *
	 * @param tip
	 */
	public void setToolTipExecuteButton(String tip) {
		this.btnCommandExecute.setToolTipText(tip);
	}

	/**
	 * Sets the ToolTip text to display for mouse focus of the TextField input
	 *
	 * @param tip
	 */
	public void setToolTipTextInput(String tip) {
		this.commandInput.setToolTipText(tip);
	}

	private void addMessage(OutputMessage m) {
		Label l = createMessageLabel(m);
		displayMessages.add(l);
		saConsoleArea.addScrollableContent(l);
	}

	private Label createMessageLabel(OutputMessage cm) {
		Label l = new Label(screen);
		l.setTextWrap(LineWrapMode.Word);
		l.setText(cm.getMsg());
		l.setStyleClass("color-" + cm.getType());
		return l;
	}

	private void sendMsg() {
		final String input = commandInput.getText();
		if (input.length() > 0) {

			// Update history and reset history scrolling back to the end
			commandHistory.add(input);
			currentHistoryIndex = -1;
			textBeforeSearch = null;

			// Inform
			changeSupport.fireEvent(new UIChangeEvent<Console, String>(this, null, input));

			// Reset and focus
			commandInput.setText("");
			commandInput.focus();
		}
	}
}
