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

import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

import icetone.controls.buttons.PushButton;
import icetone.css.CssEvent;
import icetone.css.CssEventTrigger;
import icetone.effects.IEffect;

public interface StyledNode<T extends ElementContainer<?, ?>, ET extends UIEventTarget>
		extends ElementContainer<T, ET> {

	/**
	 * It may be sometimes useful parent to parent a node to a particular
	 * {@link Element} for styling purposes, but without this element actually being
	 * added to it. This can be used for determining preferred sizes when CSS styles
	 * are a factor (e.g. with text, select-able items, anything with fixed sizes or
	 * padding and more).
	 * <p>
	 * For such cases, this method should be override to return a different
	 * {@link Element}, or the concrete classes setter (if it has one) should be
	 * used.
	 * <p>
	 * The default behavior should be to return the same element as
	 * {@link #getParentContainer()}.
	 * 
	 * @return parent to use for styling, defaults to {@link #getParentContainer()}.
	 */

	ElementContainer<?, ?> getStyledParentContainer();

	/**
	 * Get the list of style names this node may be known as. This is analogous to
	 * an an HTML tag, and is usually the Java class name of the concrete control
	 * (e.g. {@link PushButton} is known as 'PushButton').
	 * 
	 * @return style class names
	 */
	List<String> getStyleClassNames();

	/**
	 * Get the CSS 'class' attribute value. This can be multiple class names, each
	 * separated by a space. As with HTML/CSS the same classes can be used across
	 * multiple nodes.
	 * 
	 * @return style class
	 */
	String getStyleClass();

	/**
	 * Get the CSS pseudo-styles for this node in current state.
	 * 
	 * @return pseudo styles
	 */
	PseudoStyles getPseudoStyles();

	/**
	 * Get the CSS style ID for this node. This is typically unique in the scene,
	 * although there is no hard requirement for it to be so.
	 * 
	 * @return style ID
	 */
	String getStyleId();

	/**
	 * Get any additional CSS declarations for this element. As with CSS, the
	 * declaration is separated by a ';'
	 * 
	 * @return additional CSS
	 */
	String getCss();

	/**
	 * Not usually called directly (it is called by {@link CssState} when it
	 * processes stylesheets), this method should apply the provided CSS style to
	 * the node.
	 * 
	 * @param decl CSS declaration
	 */
	void applyCss(PropertyDeclaration decl);

	/**
	 * Get the {@link CssState} object that is processing CSS for this node.
	 * 
	 * @return css state
	 */
	CssState getCssState();

	/**
	 * Get if the node is currently in a enabled state.
	 * 
	 * @return disabled
	 */
	boolean isEnabled();

	/**
	 * Get a list of the currently active {@link CssEvent}s.
	 * 
	 * @return active css events
	 */
	List<CssEventTrigger<? extends IEffect>> getActiveEvents();

}
