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
package icetone.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author t0neg0d
 */
public class Form implements FocusCycle {
	private BaseScreen screen;
	private List<BaseElement> elements = new ArrayList<>();
	private int nextIndex = 0;
	private int currentTabIndex = 0;

	List<Character> gbSpecialCharacters;
	List<Character> gbNumeric;
	List<Character> gbAlpha;

	public Form() {
		this(BaseScreen.get());
	}

	public Form(BaseScreen screen) {
		this.screen = screen;
	}

	/**
	 * Adds the specified element and assigns it a Tab Order
	 * 
	 * @param element
	 *            Element to add to form
	 */
	public BaseElement addFormElement(BaseElement element) {
		if (elements.contains(element))
			throw new IllegalArgumentException("Already in this form.");
		elements.add(element);
		element.setTabIndex(nextIndex);
		element.setForm(this);
		nextIndex++;
		return element;
	}

	public boolean hasFormElement(BaseElement element) {
		return elements.contains(element);
	}

	/**
	 * Returns the element containing the specified ID, or null if it does not
	 * exist
	 * 
	 * @param styleID
	 *            ID
	 * @return Element
	 */
	public BaseElement getFormElementByID(String styleId) {
		BaseElement ret = null;
		for (BaseElement el : elements) {
			if (el.getStyleId().equals(styleId)) {
				ret = el;
				break;
			}
		}
		return ret;
	}

	/**
	 * Removes the specified element if it exists
	 * 
	 * @param element
	 */
	public void removeFormElement(BaseElement element) {
		int newIndex = 0;
		elements.remove(element);
		Object[] elArray = elements.toArray();
		elements.clear();
		for (int i = 0; i < elArray.length; i++) {
			((BaseElement) elArray[i]).setTabIndex(newIndex);
			elements.add((BaseElement) elArray[i]);
			newIndex++;
		}
	}

	@Override
	public void setFocusCycleElement(BaseElement element) {
		currentTabIndex = element.getTabIndex();
	}

	@Override
	public void tabNext() {
		currentTabIndex++;
		if (currentTabIndex == elements.size())
			currentTabIndex = 0;
		boolean elementFound = false;
		for (BaseElement el : elements) {
			if (el.getTabIndex() == currentTabIndex) {
				screen.setKeyboardFocus(el);
				elementFound = true;
			}
		}
		if (!elementFound) {
			screen.resetKeyboardFocus(null);
			tabNext();
		}
	}

	@Override
	public void tabPrev() {
		currentTabIndex--;
		if (currentTabIndex == -1)
			currentTabIndex = elements.size() - 1;
		boolean elementFound = false;
		for (BaseElement el : elements) {
			if (el.getTabIndex() == currentTabIndex) {
				screen.setKeyboardFocus(el);
				elementFound = true;
			}
		}
		if (!elementFound) {
			screen.resetKeyboardFocus(null);
			tabPrev();
		}
	}

}
