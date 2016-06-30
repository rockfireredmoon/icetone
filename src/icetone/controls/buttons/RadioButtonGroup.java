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
import java.util.List;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.utils.UIDUtil;

/**
 * @author t0neg0d
 */
public class RadioButtonGroup {
	private ElementManager screen;
	private String UID;
	protected List<Button> radioButtons = new ArrayList<>();
	protected int selectedIndex = -1;
	private Button selected = null;

	public RadioButtonGroup() {
		this(Screen.get());
	}

	public RadioButtonGroup(ElementManager screen) {
		this.screen = screen;
		this.UID = UIDUtil.getUID();
	}

	public RadioButtonGroup(ElementManager screen, String UID) {
		this.screen = screen;
		this.UID = UID;
	}

	/**
	 * Returns the String unique ID of the RadioButtonGroup
	 * 
	 * @return String
	 */
	public String getUID() {
		return this.UID;
	}

	/**
	 * Adds any Button or extended class and enables the Button's Radio state
	 * 
	 * @param button
	 */
	public void addButton(Button button) {
		button.setRadioButtonGroup(this);
		radioButtons.add(button);

		if (selectedIndex == 0)
			setSelected(0);
	}

	/**
	 * Sets the current selected Radio Button to the Button associated with the
	 * provided index
	 * 
	 * @param index
	 */
	public void setSelected(int index) {
		if (this.selectedIndex != index) {
			if (index >= 0 && index < radioButtons.size()) {
				Button rb = radioButtons.get(index);
				this.selected = rb;
				this.selectedIndex = index;
				for (Button rb2 : radioButtons) {
					if (rb2 != this.selected)
						rb2.setIsToggled(false);
					else
						rb2.setIsToggled(true);
				}
				onSelect(selectedIndex, rb);
			}
		}
	}

	/**
	 * Sets the current selected Radio Button to the Button instance provided
	 * 
	 * @param button
	 */
	public void setSelected(Button button) {
		if (this.selected != button) {
			this.selected = button;
			this.selectedIndex = radioButtons.indexOf(button);
			for (Button rb : radioButtons) {
				if (rb != this.selected) {
					if (rb.getIsToggled())
						rb.setIsToggled(false);
				} else
					rb.setIsToggled(true);
			}
			onSelect(selectedIndex, button);
		}
	}

	public Button getSelected() {
		return this.selected;
	}

	/**
	 * Event method for change in selected Radio Button
	 * 
	 * @param index
	 *            The index of the selected button
	 * @param value
	 *            The selected button instance
	 */
	public void onSelect(int index, Button value) {
	}

	/**
	 * An alternate way to add all Radio Buttons as children to the provided
	 * Element
	 * 
	 * @param element
	 *            The element to add Radio Button's to. null = Screen
	 */
	public void setDisplayElement(Element element) {
		for (Button rb : radioButtons) {
			if (screen.getElementById(rb.getUID()) == null) {
				if (element != null) {
					element.addChild(rb);
				} else {
					screen.addElement(rb);
				}
			}
		}
	}

	public void removeButton(Button removeButton) {
		radioButtons.remove(removeButton);
	}

}
