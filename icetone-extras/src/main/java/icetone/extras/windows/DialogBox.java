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

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.PushButton;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;

/**
 * Extension to {@link AlertBox} that adds an additional 'Cancel' button.
 *
 * @author rockfire
 * @author t0neg0d
 */
public abstract class DialogBox extends AlertBox {

	private PushButton btnCancel;

	public DialogBox(BaseScreen screen, boolean closeable) {
		super(screen, closeable);
	}

	public DialogBox(BaseScreen screen, Vector2f position, boolean closeable) {
		super(screen, closeable);
		setPosition(position);
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

	@Override
	public void createButtons(BaseElement buttons) {
		super.createButtons(buttons);
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
}
