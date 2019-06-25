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

import java.util.List;

/**
 * A default {@link FocusCycle} implementation that should suffice for most
 * needs. Focus will be cycled using the order the elements were added to their
 * container. When the cycle reaches the end of the container, it will ascend to
 * it's parent and start traversing the next sibling until the end of that
 * containers is reached. And so on.
 * <p>
 * This traversal can be halted by setting
 * {@link BaseElement#setKeyboardFocusRoot(boolean)} to <code>true</code>. By
 * default, controls such as {@link Panel} and {@link Frame} all
 * do this.
 */
public class DefaultFocusCycle implements FocusCycle {

	private BaseScreen screen;

	public DefaultFocusCycle(BaseScreen screen) {
		this.screen = screen;
	}

	@Override
	public void setFocusCycleElement(BaseElement element) {
	}

	@Override
	public void tabNext() {
		BaseElement el = screen.getKeyboardFocus();
		BaseElement orginalFocus = screen.getKeyboardFocus();
		ElementContainer<?,?> parent = el.getParentContainer();

		while (true) {
			if (parent == null) {
				parent = screen;
			}
			List<BaseElement> children = parent.getElements();
			int idx = children.indexOf(el);
			for (int i = 0; i < children.size(); i++) {
				idx++;
				if (idx == children.size()) {
					if (parent instanceof BaseElement) {
						el = (BaseElement) parent;
						break;
					} else
						idx = 0;
				}
				el = children.get(idx);
				if (el != orginalFocus) {
					if (checkFocusable(el)) {
						el.focus();
						return;
					}

					BaseElement c = firstKeyboardFocusable(el);
					if (c != null) {
						c.focus();
						return;
					}
				}
			}

			// Tried all the children in the same level, move up to the parent
			if (parent == screen) {
				// Give up
				return;
			}

			if (children.isEmpty() || !parent.isKeyboardFocusRoot())
				parent = parent.getParentContainer();
		}

	}

	@Override
	public void tabPrev() {
		BaseElement el = screen.getKeyboardFocus();
		BaseElement orginalFocus = screen.getKeyboardFocus();
		ElementContainer<?,?> parent = el.getParentContainer();

		int start = -1;
		while (true) {

			if (parent == null) {
				parent = screen;
			}

			List<BaseElement> children = parent.getElements();
			int idx = start == -1 ? children.indexOf(el) : start;
			for (int i = 0; i < children.size(); i++) {
				idx--;
				if (idx < 0) {
					if (parent instanceof BaseElement) {
						el = (BaseElement) parent;
						break;
					} else
						idx = children.size() - 1;
				}
				el = children.get(idx);
				if (el != orginalFocus) {
					if (checkFocusable(el)) {
						el.focus();
						return;
					}

					BaseElement c = lastKeyboardFocusable(el);
					if (c != null) {
						c.focus();
						return;
					}
				}
			}

			// Tried all the children in the same level, move up to the parent
			if (parent == screen) {
				// Give up
				return;
			}

			if (children.isEmpty() || !parent.isKeyboardFocusRoot() || !(parent instanceof BaseElement)) {
				parent = parent.getParentContainer();
			} else {
				start = parent.getElements().size();
			}
		}
	}
	
	protected boolean checkFocusable(BaseElement el) {
		return el.isKeyboardFocusable() && el.isEnabled();
	}

	protected BaseElement firstKeyboardFocusable(BaseElement el) {
		if (checkFocusable(el))
			return el;
		for (BaseElement e : el.getElements()) {
			BaseElement f = firstKeyboardFocusable(e);
			if (f != null)
				return f;
		}
		return null;
	}

	protected BaseElement lastKeyboardFocusable(BaseElement el) {
		if (checkFocusable(el))
			return el;
		for (int i = el.getElements().size() - 1; i >= 0; i--) {
			BaseElement e = el.getElements().get(i);
			BaseElement f = lastKeyboardFocusable(e);
			if (f != null)
				return f;
		}
		return null;
	}
}
