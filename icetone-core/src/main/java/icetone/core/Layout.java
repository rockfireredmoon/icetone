/*
 * Copyright (c) 2013-2014 Emerald Icemoon All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package icetone.core;

import com.jme3.math.Vector2f;

/**
 * Interface to be implemented by layout managers. For nearly all purposes,
 * instead of implementing this directly, use {@link AbstractGenericLayout}.
 */
public interface Layout<C extends ElementContainer<?, ?>, O extends Object> {

	final static LayoutType[] TEXT_CHANGE = new LayoutType[] { LayoutType.text, LayoutType.alpha, LayoutType.clipping,
			LayoutType.location, LayoutType.styling };

	final static LayoutType[] BOUNDS_CHANGE = new LayoutType[] { LayoutType.background, LayoutType.text,
			LayoutType.children, LayoutType.clipping, LayoutType.location, LayoutType.rotation };

	final static LayoutType[] CONTENT = new LayoutType[] { LayoutType.styling, LayoutType.alpha, LayoutType.background, LayoutType.text,
			LayoutType.children, LayoutType.clipping, LayoutType.location, LayoutType.rotation, LayoutType.zorder,
			LayoutType.effects };

	final static LayoutType[] ALL_EXCEPT_ALL = new LayoutType[] { LayoutType.reset, LayoutType.alpha, LayoutType.background,
			LayoutType.text, LayoutType.children, LayoutType.clipping, LayoutType.location, LayoutType.rotation,
			LayoutType.zorder, LayoutType.effects };

	public enum LayoutType {
		reset, styling, alpha, background, text, children, clipping, location, zorder, rotation, effects, all, items;

		public boolean requiresReady() {
			switch (this) {
			/*
			 * TODO: Check this is really OK. So far, it seems to be just require this on
			 * reset styles, but there might still be some edge cases I have missed.
			 * 
			 * 
			 * With these enabled, it breaks things like IceClient that add an element to a
			 * parent container, then the parent to another, then use an effect to show the
			 * element. It also helps slightly with initialising performance.
			 * 
			 * One case that required this was when you need to calculate preferred sizes of
			 * controls based on the CSS theme (e.g. ColorPaletteTab and ColorWheelTab). In
			 * such cases, the right solution is to apply the CSS directly before accessing
			 * any style dependent attributes:-
			 * 
			 * Element.getCssState().applyCss();
			 */

//			case all: 
//			case styling:
			case reset:
				return false;
			default:
				return true;
			}
		}

		public boolean affectsBounds() {
			switch (this) {
			case all:
			case children:
			case background:
			case location:
			case text:
			case rotation:
				return true;
			default:
				return false;
			}
		}

		public static LayoutType[] contentChange() {
			return CONTENT;
		}

		public static LayoutType[] boundsChange() {
			return BOUNDS_CHANGE;
		}

		public static LayoutType[] allExceptAll() {
			return ALL_EXCEPT_ALL;
		}

		public static LayoutType[] text() {
			return TEXT_CHANGE;
		}
	}

	public final static Object EXCLUDE_FROM_LAYOUT = new Object();
	public final static String DEFAULT_LAYOUT = "";

	/**
	 * Get the minimum size used for this layout. <code>null</code> may be returned
	 * to indicate no minimum size. <strong>Do not directly manipulate returned
	 * vectors directly as they may be references to constants. </strong>
	 * <p>
	 * Normally you would not call this in your own code. Instead use
	 * {@link BaseElement#calcMinimumSize()}.
	 * </p>
	 *
	 * @param parent layout owner
	 * @return minimum size
	 */
	Vector2f minimumSize(C parent);

	/**
	 * Get the maximum size used for this layout. <code>null</code> may be returned
	 * to indicate no maximum size. <strong>Do not directly manipulate returned
	 * vectors directly as they may be references to constants. </strong>
	 * <p>
	 * Do not call this in your own code. Instead use
	 * {@link BaseElement#calcMaximumSize()}.
	 * </p>
	 *
	 * @param parent layout owner
	 * @return maxium size
	 */
	Vector2f maximumSize(C parent);

	/**
	 * Get the preferred size used for this layout. <code>null</code> may be
	 * returned to indicate no preferred size. <strong>Do not directly manipulate
	 * returned vectors directly as they may be references to constants. </strong>
	 * <p>
	 * Normally you would not call this in your own code. Instead use
	 * {@link BaseElement#calcPreferredSize()}.
	 * </p>
	 *
	 * @param parent layout owner
	 * @return preferred size
	 */
	Vector2f preferredSize(C parent);

	/**
	 * Layout the provided container.
	 *
	 * @param container  container to layout
	 * @param layoutType layout type
	 */
	void layout(C container, LayoutType layoutType);

	/**
	 * Configure a child's constraints. The type will be depend on the layout
	 * manager.
	 *
	 * @param child       child
	 * @param constraints constraints
	 */
	void constrain(BaseElement child, O constraints);

	/**
	 * Get a child's constraint. The type will be depend on the layout manager.
	 *
	 * @param child child
	 * @return constraints
	 */
	O constraints(BaseElement child);

	/**
	 * Called when a child is removed from it's contain. If the layout maintains any
	 * kind of caching or other state for the child, this is the signal it can be
	 * removed.
	 *
	 * @param child child removed
	 * @return any constrains the element had
	 */
	O remove(BaseElement child);

	/**
	 * Constraints may be provided through CSS as a string. This method should parse
	 * the string and return a constraints instance
	 * 
	 * @param constraintsString constraints
	 */
	O parseConstraints(String constraintsString);

	/**
	 * Get whether the layout manager will position this particular element (or
	 * indeed any element) or if that should be done manually
	 * 
	 * @param element element
	 */
	boolean positionsElement(BaseElement element);

}
