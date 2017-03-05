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
import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Element;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;

/**
 * Extension of {@link Frame} that takes a row of buttons at the bottom of the
 * dialog window. Suitable for building various types of popup confirmation
 * dialogs.
 * 
 * @author rockfire
 * @author t0neg0d
 * @param <T>
 *            type of content element
 */
public abstract class ButtonWindow<T extends Element> extends Frame {

	protected PushButton btnOk;
	protected Form form;
	protected T contentArea;

	public ButtonWindow(BaseScreen screen, boolean closeable) {
		super(screen, null, null, null, closeable);

		form = new Form(screen);
		getContentArea().setLayoutManager(new MigLayout(screen, "wrap 1", "[fill, grow]", "[fill, grow][shrink 0]"));

		// Dialog
		contentArea = createContent();
		getContentArea().addElement(contentArea);

		// Button Bar
		Element buttons = new Element(screen, new FlowLayout(BitmapFont.Align.Center)) {
			{
				setStyleClass("dialog-buttons");
			}
		};
		createButtons(buttons);
		getContentArea().addElement(buttons, "growx");
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
	 * Sets the tooltip text of the Ok button
	 *
	 * @param text
	 *            String
	 */
	public void setButtonOkTooltipText(String text) {
		btnOk.setToolTipText(text);
	}

	/**
	 * Abstract method for handling Ok button click event
	 *
	 * @param evt
	 *            MouseButtonEvent
	 * @param toggled
	 *            boolean
	 */
	public abstract void onButtonOkPressed(MouseButtonEvent evt, boolean toggled);

	/**
	 * Sets the tooltip text to display when mouse hovers over the Ok button
	 *
	 * @param tip
	 *            String
	 */
	public void setToolTipOkButton(String tip) {
		this.btnOk.setToolTipText(tip);
	}

	protected abstract T createContent();

	protected void createButtons(BaseElement buttons) {
		btnOk = new PushButton(screen, "Ok");
		btnOk.onMouseReleased(evt -> onButtonOkPressed(evt, true));
		buttons.addElement(btnOk);
		form.addFormElement(btnOk);
	}
}
