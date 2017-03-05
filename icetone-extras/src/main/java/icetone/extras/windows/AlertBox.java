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

import com.jme3.font.BitmapFont.Align;
import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.text.XHTMLLabel;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.layout.ScreenLayoutConstraints;

/**
 * A specialised dialog window suitable for popup alerts. The message contents
 * of the dialog can contain XHTMML and there are 3 different types of alert
 * messages (which may be styled individually). By default a single button is
 * added.
 * 
 * @author rockfire
 * @author t0neg0d
 */
public abstract class AlertBox extends ButtonWindow<XHTMLLabel> {

	public enum AlertType {

		ERROR, INFORMATION, PROGRESS
	}

	public static AlertBox alert(BaseScreen screen, String title, String text, final AlertType alert) {

		final AlertBox dialog = new AlertBox(screen, true) {
			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
				hide();
			}

			@Override
			public void createButtons(BaseElement buttons) {
				if (!alert.equals(AlertType.PROGRESS)) {
					super.createButtons(buttons);
				}
			}
		};
		dialog.addStyleClass("alert-" + alert.name().toLowerCase());
		dialog.setDestroyOnHide(true);
		dialog.setWindowTitle(title);
		if (!alert.equals(AlertType.PROGRESS)) {
			dialog.setButtonOkText("Close");
		}
		dialog.setText(text);
		dialog.setResizable(false);
		dialog.setMovable(false);
		dialog.setModal(true);
		screen.showElement(dialog, ScreenLayoutConstraints.center);
		return dialog;
	}

	{
		setResizable(false);
		setMovable(false);
		setAlign(Align.Center);
	}

	public AlertBox(BaseScreen screen, boolean closeable) {
		super(screen, closeable);
	}

	@Override
	protected XHTMLLabel createContent() {
		XHTMLLabel label = new XHTMLLabel(screen) {
			@Override
			public Align getAlign() {
				return AlertBox.this.getAlign();
			}
		};
		label.setStyleId("alert-message");
		return label;
	}

	/**
	 * Sets the message to display in the AlertBox
	 *
	 * @param text
	 *            String The message
	 */
	@Deprecated
	public void setMsg(String text) {
		setText(text);
	}

	@Override
	public BaseElement setText(String text) {
		contentArea.setText(text);
		return this;
	}

	/**
	 * Returns the ScrollArea containing the window message text.
	 *
	 * @return
	 */
	public XHTMLLabel getTextArea() {
		return this.contentArea;
	}
}
