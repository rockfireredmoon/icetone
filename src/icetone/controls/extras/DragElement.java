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

package icetone.controls.extras;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;
import icetone.listeners.MouseButtonListener;

/**
 *
 * @author t0neg0d
 */
public abstract class DragElement extends Element implements MouseButtonListener {

	private Vector2f originalPosition;
	private boolean useSpringBack = false;
	private boolean useSpringBackEffect = false;
	private boolean lockToDropElementCenter = false;
	private boolean useLockToDropElementEffect = false;
	private boolean isEnabled = true;
	private Element parentDroppable = null;

	private Effect slideTo;
	private ZPriority priorityBeforeDrag;
	private boolean wasClippingEnabled;
	private boolean unclipOnDrag;
	private Vector2f wasRelative;
	private Element wasParentedBy;

	public DragElement() {
		this(Screen.get());
	}

	public DragElement(ElementManager screen) {
		this(screen, Vector4f.ZERO, null);
	}

	public DragElement(ElementManager screen, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, resizeBorders, defaultImg);
	}

	public DragElement(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	public DragElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions,
			Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		this.originalPosition = getPosition().clone();
		originalPosition.setY(screen.getHeight() - originalPosition.getY() - getHeight());
		this.setIsMovable(true);
		this.setIsDragDropDragElement(true);
		this.setScaleNS(false);
		this.setScaleEW(false);
		setUnclipOnDrag(true);

		// this.setFontSize(screen.getStyle("Button").getFloat("fontSize"));
		// this.setFontColor(screen.getStyle("Button").getColorRGBA("fontColor"));
		// this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Button").getString("textVAlign")));
		// this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Button").getString("textAlign")));
		// this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Button").getString("textWrap")));

	}

	/**
	 * Returns the DragElement's original position
	 * 
	 * @return Vector2f originalPosition
	 */
	public Vector2f getOriginalPosition() {
		return this.originalPosition;
	}

	/**
	 * Enables/disables the DragElement
	 * 
	 * @param isEnabled
	 *            boolean
	 */
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
		if (isEnabled)
			this.setIsMovable(true);
		else
			this.setIsMovable(false);
	}

	/**
	 * Returns if the DragElement is current enabled/disabled
	 * 
	 * @return boolean isEnabled
	 */
	public boolean getIsEnabled() {
		return this.isEnabled;
	}

	/**
	 * Set whether or not the DragElement should center itself within the drop
	 * element
	 * 
	 * @param lockToDropElementCenter
	 *            boolean
	 */
	public void setUseLockToDropElementCenter(boolean lockToDropElementCenter) {
		this.lockToDropElementCenter = lockToDropElementCenter;
	}

	/**
	 * Returns if the DragElement should center itself within the drop element
	 * 
	 * @return boolean
	 */
	public boolean getUseLockToDropElementCenter() {
		return this.lockToDropElementCenter;
	}

	/**
	 * Enables/disables the use of the SlideTo Effect when centering within the
	 * drop element.
	 * 
	 * @param useLockToDropElementEffect
	 *            boolean
	 */
	public void setUseLockToDropElementEffect(boolean useLockToDropElementEffect) {
		this.useLockToDropElementEffect = useLockToDropElementEffect;
	}

	/**
	 * Returns if the SlideTo Effect is enabled/disabled when centering within a
	 * drop element
	 * 
	 * @return
	 */
	public boolean getUseLockToDropElementEffect() {
		return this.useLockToDropElementEffect;
	}

	/**
	 * Enables/disables springback to original position when dropped outside of
	 * a valid drop element
	 * 
	 * @param useSpringBack
	 *            boolean
	 */
	public void setUseSpringBack(boolean useSpringBack) {
		this.useSpringBack = useSpringBack;
	}

	/**
	 * Returns if springback is enabled for springback to original position when
	 * dropped outside of a valid drop element
	 * 
	 * @return boolean
	 */
	public boolean getUseSpringBack() {
		return this.useSpringBack;
	}

	/**
	 * Enables/disables the use of SlideTo Effect when springback is enabled
	 * 
	 * @param useSpringBackEffect
	 *            boolean
	 */
	public void setUseSpringBackEffect(boolean useSpringBackEffect) {
		this.useSpringBackEffect = useSpringBackEffect;
	}

	/**
	 * Returns if SpringBack Effects are enabled/disabled
	 * 
	 * @return boolean
	 */
	public boolean getUseSpringBackEffect() {
		return this.useSpringBackEffect;
	}

	public Element getParentDroppable() {
		return this.parentDroppable;
	}

	public void clearParentDroppable() {
		this.parentDroppable = null;
	}

	public boolean isUnclipOnDrag() {
		return unclipOnDrag;
	}

	public void setUnclipOnDrag(boolean unclipOnDrag) {
		this.unclipOnDrag = unclipOnDrag;
	}

	public void bindToDroppable(Element dropEl) {
		if (dropEl.getIsDragDropDropElement()) {
			handleSuccess(dropEl);
			centerToDropElement(dropEl);
		}
	}

	@Override
	public void onMouseButton(MouseUIButtonEvent evt) {// This ensures
														// "spring back"
														// works properly
		if (evt.isLeft()) {
			if (evt.isPressed()) {
				originalPosition.set(getPosition());
				priorityBeforeDrag = getPriority();

				wasClippingEnabled = getIsClippingEnabled();
				if (unclipOnDrag) {
					setIsClippingEnabled(false);
				}

				setPriority(ZPriority.DRAG);

				/*
				 * If the element is not already in the Screen, then remove it
				 * from it's current parent and add to the screen. To prevent
				 */
				if (getElementParent() != null) {
					wasParentedBy = getElementParent();
					wasRelative = getPosition().clone();
					Vector2f abs = getAbsolute();
					System.err.println("ABS: " + abs);
					getElementParent().removeChild(this);
					screen.addElement(this);
					setPosition(abs);
				}

				onDragStart(evt);
			} else if (evt.isReleased()) {
				Element dropEl = screen.getDropElement();
				boolean success = onDragEnd(evt, dropEl);
				setPriority(priorityBeforeDrag);
				if (unclipOnDrag) {
					setIsClippingEnabled(wasClippingEnabled);
				}

				if (wasParentedBy != null) {
					screen.removeElement(this);
					setPosition(wasRelative);
					wasParentedBy.addChild(this);
					wasParentedBy = null;
					wasRelative = null;
				}

				if (success) {
					handleSuccess(dropEl);
					centerToDropElement(dropEl);
				} else {
					springBack();
				}
			}
		}
	}

	private void handleSuccess(Element dropEl) {
		if (parentDroppable != null && parentDroppable != dropEl) {
			parentDroppable.removeChild(this);
			parentDroppable = null;
		}
		parentDroppable = dropEl;
		Vector2f pos = new Vector2f(getAbsoluteX(), getAbsoluteY());
		Element p = getElementParent();
		if (p != dropEl) {
			if (p != null) {
				p.removeChild(this);
			} else {
				screen.removeElement(this);
			}
			float nextY = (pos.y - dropEl.getAbsoluteY());
			nextY = -nextY;
			setPosition(pos.x - dropEl.getAbsoluteX(), nextY);
			dropEl.addChild(this);
			// this.setZOrder(screen.getZOrderStepMinor());
		}
	}

	private void centerToDropElement(Element dropEl) {
		if (lockToDropElementCenter) {
			Vector2f destination = new Vector2f((dropEl.getWidth() / 2) - (getWidth() / 2),
					(dropEl.getHeight() / 2) - (getHeight() / 2));
			if (useLockToDropElementEffect) {
				slideTo = new Effect(Effect.EffectType.SlideTo, Effect.EffectEvent.Release, .15f);
				slideTo.setElement(this);
				slideTo.setEffectDestination(destination);
				screen.getEffectManager().applyEffect(slideTo);
			} else {
				setPosition(destination);
			}
			originalPosition = destination.clone();
		}
	}

	private void springBack() {
		if (useSpringBack) {
			Vector2f destination = originalPosition.clone();
			if (useSpringBackEffect) {
				slideTo = new Effect(Effect.EffectType.SlideTo, Effect.EffectEvent.Release, .15f);
				slideTo.setElement(this);
				slideTo.setEffectDestination(destination);
				screen.getEffectManager().applyEffect(slideTo);
			} else {
				setPosition(destination);
			}
		}
	}

	public abstract void onDragStart(MouseButtonEvent evt);

	public abstract boolean onDragEnd(MouseButtonEvent evt, Element dropElement);
}
