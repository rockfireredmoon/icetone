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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;

/**
 * @author t0neg0d
 * @author rockfire
 */
public class ButtonGroup<S extends Button> {

	protected List<S> buttons = new ArrayList<>();
	protected int selectedIndex = -1;
	private S selected = null;
	private ChangeSupport<ButtonGroup<S>, Button> changeSupport = new ChangeSupport<>();

	public void onChange(UIChangeListener<ButtonGroup<S>, Button> changeListener) {
		changeSupport.bind(changeListener);
	}

	public void unbind(UIChangeListener<ButtonGroup<S>, Button> changeListener) {
		changeSupport.unbind(changeListener);
	}

	public void addChangeListener(UIChangeListener<ButtonGroup<S>, Button> changeListener) {
		changeSupport.addListener(changeListener);
	}

	public void removeChangeListener(UIChangeListener<ButtonGroup<S>, Button> changeListener) {
		changeSupport.removeListener(changeListener);
	}

	@SuppressWarnings("unchecked")
	public ButtonGroup<S> addButton(Button button) {
		if (button.getButtonGroup() == this)
			return this;
		buttons.add((S) button);
		button.setButtonGroup(this);
		if (selectedIndex == -1)
			setSelected(0);
		return this;
	}

	/**
	 * Sets the current selected Radio Button to the Button associated with the
	 * provided index
	 * 
	 * @param index
	 */
	public ButtonGroup<S> setSelected(int index) {
		if (this.selectedIndex != index) {
			S was = this.selected;

			if (selected != null)
				selected.disarm();

			if (index >= 0 && index < buttons.size()) {
				S rb = buttons.get(index);
				this.selected = rb;
				this.selectedIndex = index;
				selected.arm();
				changeSupport.fireEvent(new UIChangeEvent<ButtonGroup<S>, Button>(this, was, rb));
			} else if (index == -1) {
				this.selected = null;
				this.selectedIndex = -1;
				changeSupport.fireEvent(new UIChangeEvent<ButtonGroup<S>, Button>(this, was, null));
			}
		}
		return this;
	}

	/**
	 * Sets the current selected Button to the Button instance provided
	 * 
	 * @param button
	 */
	@SuppressWarnings("unchecked")
	public ButtonGroup<S> setSelected(Button button) {
		if (!Objects.equals(button, selected)) {
			if (selected != null)
				selected.disarm();
			S was = selected;
			selected = (S) button;
			if (selected == null)
				selectedIndex = -1;
			else {
				selectedIndex = buttons.indexOf(button);
				selected.arm();
			}
			changeSupport.fireEvent(new UIChangeEvent<ButtonGroup<S>, Button>(this, was, selected));
		}
		return this;
	}

	public S getSelected() {
		return this.selected;
	}

	public ButtonGroup<S> removeButton(S removeButton) {
		buttons.remove(removeButton);
		return this;
	}

	public List<S> getButtons() {
		return Collections.unmodifiableList(buttons);
	}

}
