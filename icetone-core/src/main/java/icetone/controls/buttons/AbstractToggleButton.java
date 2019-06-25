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
package icetone.controls.buttons;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.PseudoStyles;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.css.CssProcessor.PseudoStyle;

/**
 * @author rockfire
 *
 */
public abstract class AbstractToggleButton extends StatefulButton<Boolean> {

	{
		state = false;
		setStyleClass("toggle");
	}

	public AbstractToggleButton() {
		super();
	}

	public AbstractToggleButton(BaseScreen screen, String styleId) {
		super(screen, styleId);
	}

	public AbstractToggleButton(BaseScreen screen) {
		super(screen);
	}

	public AbstractToggleButton(String texturePath, String text) {
		super(texturePath, text);
	}

	public AbstractToggleButton(String text) {
		super(text);
	}

	public AbstractToggleButton setIsToggled(boolean isToggled) {
		setState(isToggled);
		return this;
	}

	public boolean getIsToggled() {
		return state == null ? false : state;
	}

	@Override
	protected void processMouseButtonReleased(MouseUIButtonEvent<BaseElement> evt) {
		if (evt.getClicks() == 1)
			if (buttonGroup == null)
				setState(!getIsToggled());
			else
				buttonGroup.setSelected(this);
	}

	@Override
	public PseudoStyles getPseudoStyles() {
		PseudoStyles ps = super.getPseudoStyles();
		if (state != null && state) {
			ps = PseudoStyles.get(ps).addStyle(PseudoStyle.link);
		}
		return ps;
	}

	@Override
	protected void arm() {
		setState(true);
	}

	@Override
	protected void disarm() {
		setState(false);
	}
}
