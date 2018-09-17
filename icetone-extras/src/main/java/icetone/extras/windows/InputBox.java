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

import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Size;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;

/**
 * A specialised frame that has a single input box and an OK and Cancel button.
 *
 * @author rockfire
 * @author t0neg0d
 */
public abstract class InputBox extends Frame {

	private PushButton btnOk;
	private PushButton btnCancel;
	private boolean cancelOnReturn;

	protected TextField input;
	protected BaseElement buttons;
	protected Form form;

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param size
	 *            type of window
	 * @param closeable
	 *            closeable
	 */
	public InputBox(BaseScreen screen, boolean closeable) {
		this(screen, null, null, null, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param closeable
	 *            closeable
	 */
	public InputBox(BaseScreen screen, Vector2f position, boolean closeable) {
		this(screen, null, position, null, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the AlertBox window
	 * @param closeable
	 *            closeable
	 */
	public InputBox(BaseScreen screen, String UID, Vector2f position, Size dimensions, boolean closeable) {
		super(screen, UID, position, dimensions, closeable);

		form = new Form(screen);
		getContentArea().setLayoutManager(new MigLayout(screen, "wrap 1", "[fill, grow]", "[grow][]"));

		// Dialog
		input = new TextField(screen) {
			@Override
			public void onKeyRelease(KeyInputEvent evt) {
				super.onKeyRelease(evt);
				if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
					onEnterPressed(evt, input.getText());
				}
			}
		};
		getContentArea().addElement(input);

		// Button Bar
		buttons = new BaseElement(screen);
		buttons.setLayoutManager(new FlowLayout(4, BitmapFont.Align.Center));
		createButtons(buttons);
		getContentArea().addElement(buttons, "growx");

		//
		input.focus();
	}

	public boolean isCancelOnReturn() {
		return cancelOnReturn;
	}

	public void setCancelOnReturn(boolean cancelOnReturn) {
		this.cancelOnReturn = cancelOnReturn;
	}

	protected void onEnterPressed(KeyInputEvent evt, String text) {
		// By default act is OK was pressed
		if (cancelOnReturn) {
			onButtonCancelPressed(null, true);
		} else {
			onButtonOkPressed(null, text, true);
		}
	}

	/**
	 * Sets the text of the Cancel button
	 *
	 * @param text
	 *            String
	 */
	public void setButtonCancelText(String text) {
		btnCancel.setText(text);
	}

	/**
	 * Abstract method for handling Cancel button click event
	 *
	 * @param evt
	 *            MouseButtonEvent
	 * @param toggled
	 *            boolean
	 */
	public abstract void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled);

	/**
	 * Sets the tooltip text to display when mouse hovers over the Cancel button
	 *
	 * @param tip
	 *            String
	 */
	public void setToolTipCancelButton(String tip) {
		this.btnCancel.setToolTipText(tip);
	}

	public void createButtons(BaseElement buttons) {
		btnOk = new PushButton(screen, "Ok") {
			{
				setStyleClass("ok");
			}
		};
		btnOk.onMouseReleased(evt -> onButtonOkPressed(evt, input.getText(), true));
		buttons.addElement(btnOk);
		form.addFormElement(btnOk);

		btnCancel = new PushButton(screen, "Cancel") {
			{
				setStyleClass("cancel");
			}
		};
		btnCancel.onMouseReleased(evt -> onButtonCancelPressed(evt, true));
		btnCancel.setText("Cancel");
		buttons.addElement(btnCancel);
		form.addFormElement(btnCancel);
	}

	/**
	 * Sets the message to display in the AlertBox
	 *
	 * @param text
	 *            String The message
	 */
	public void setMsg(String text) {
		input.setText(text);
	}

	@Override
	public void controlShowHook() {
		screen.setKeyboardFocus(input);
	}

	/**
	 * Sets the text of the Ok button
	 *
	 * @param text
	 *            String
	 */
	public void setButtonOkText(String text) {
		btnOk.setText(text);
	}

	/**
	 * Abstract method for handling Ok button click event
	 *
	 * @param evt
	 *            MouseButtonEvent
	 * @param toggled
	 *            boolean
	 */
	public abstract void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled);

	/**
	 * Sets the tooltip text to display when mouse hovers over the Ok button
	 *
	 * @param tip
	 *            String
	 */
	public void setToolTipOkButton(String tip) {
		this.btnOk.setToolTipText(tip);
	}
}
