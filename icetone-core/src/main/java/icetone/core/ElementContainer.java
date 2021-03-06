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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Layout.LayoutType;
import icetone.core.event.mouse.MouseButtonListener;
import icetone.core.event.mouse.MouseMovementListener;
import icetone.core.event.mouse.MouseUIWheelListener;
import icetone.css.StyleManager.CursorType;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontSpec;

public interface ElementContainer<T extends ElementContainer<?, ?>, ET extends UIEventTarget> {

	T addMouseButtonListener(MouseButtonListener<ET> l);

	T addMouseMovementListener(MouseMovementListener<ET> l);

	T addMouseWheelListener(MouseUIWheelListener<ET> l);

	T removeMouseButtonListener(MouseButtonListener<ET> l);

	T removeMouseMovementListener(MouseMovementListener<ET> l);

	T removeMouseWheelListener(MouseUIWheelListener<ET> l);

	T onMouseMoved(MouseMovementListener<ET> l);

	T onMouseWheel(MouseUIWheelListener<ET> l);

	T onMousePressed(MouseButtonListener<ET> l);

	T onMousePressed(MouseButtonListener<ET> l, int button);

	T onMouseReleased(MouseButtonListener<ET> l);

	T onMouseReleased(MouseButtonListener<ET> l, int button);

	List<BaseElement> getElements();

	ElementContainer<?, ?> getParentContainer();

	T showElement(BaseElement layer);

	T addElement(BaseElement element);

	T addElement(BaseElement element, Object constraints);

	T showElement(BaseElement element, Object constraints);

	T attachElement(BaseElement child);

	T removeElement(BaseElement element);

	long getLayoutCounter();

	float getWidth();

	float getHeight();

	Layout<?, ?> getLayoutManager();

	T setLayoutManager(Layout<?, ?> layout);

	void dirtyLayout(boolean doChildren, LayoutType... layoutType);

	void layoutChildren();

	Vector2f getDimensions();

	Vector4f getTextPadding();

	Vector4f getMargin();

	Vector2f getTotalPadding();

	Vector2f getTotalPaddingOffset();

	void applyZOrder();

	float getIndent();

	FocusCycle getFocusCycle();

	Vector2f getPixelPosition();

	Position getPosition();

	Vector2f getAbsolute();

	Vector4f getAllPadding();

	boolean isKeyboardFocusRoot();

	CursorType getCursor();

	T setCursor(CursorType cursor);

	BaseScreen getScreen();

	ThemeInstance getThemeInstance();

	T setThemeInstance(ThemeInstance theme);

	FontSpec getFont();

	ColorRGBA getFontColor();

	boolean isVisibilityAllowed();

	void resetStyling();

	boolean isInheritsStyles();

	float getElementAlpha();

	boolean isHeirarchyInitializing();

}
