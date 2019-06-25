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

import com.jme3.math.Vector2f;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ZPriority;
import icetone.core.event.DragEvent;
import icetone.core.event.DragEvent.DragEventType;
import icetone.core.event.DragListener;
import icetone.core.event.DragSupport;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.effects.EffectList;
import icetone.effects.RunEffect;
import icetone.effects.SlideFromEffect;

/**
 *
 * @author t0neg0d
 */
public class DragElement extends Element {

	public enum DragMode {
		AutoCopy, AutoMove, Copy, Move
	}

	private boolean useSpringBack = true;
	private boolean useSpringBackEffect = false;
	private boolean useLockToDropElementEffect = false;
	private BaseElement parentDroppable = null;
	private ZPriority priorityBeforeDrag;
	private boolean wasClippingEnabled;
	private boolean unclipOnDrag;
	private BaseElement wasParentedBy;
	private BaseElement draggingElement;
	private Object wasConstraints;
	protected DragSupport dragSupport;
	private boolean animating;
	private int originalIndex;
	private boolean dragged;
	private DragMode dragMode = DragMode.Move;
	private boolean copy;

	public DragElement() {
		this(BaseScreen.get());
	}

	public DragElement(BaseScreen screen) {
		this(screen, null);
	}

	public DragElement(BaseScreen screen, String styleId) {
		super(screen, styleId);
		setDragDropDragElement(true);
		setUnclipOnDrag(true);
		onMousePressed((evt) -> {
			if (isDragDropDragElement()) {

				if (dragSupport != null) {
					DragEvent<BaseElement> dev = new DragEvent<BaseElement>(evt, this, 0, DragEventType.prepare,
							(BaseElement) null, dragMode);
					dragSupport.fireEvent(dev);
					if (dev.isConsumed())
						return;
				}
				dragged = true;
				wasClippingEnabled = isClippingEnabled();
				if (unclipOnDrag) {
					setClippingEnabled(false);
				}

				switch (dragMode) {
				case AutoCopy:
					copy = !evt.isCtrl() && !evt.isShift();
					break;
				case AutoMove:
					copy = evt.isShift();
					break;
				case Move:
					copy = false;
					break;
				default:
					copy = true;
					break;
				}

				if (copy) {
					draggingElement = createCopy(evt);
					if (draggingElement == null)
						copy = false;
					else {
						wasParentedBy = null;
						priorityBeforeDrag = null;
						draggingElement.setPriority(ZPriority.DRAG);
						draggingElement.sizeToContent();
						draggingElement.setPosition(getAbsolute());
						draggingElement.onMouseReleased((e) -> {
							finishDrag(evt);
						});
						screen.addElement(draggingElement);
						evt.setTargetElement(draggingElement);
					}
				}
				if (!copy) {
					wasConstraints = getParentContainer().getLayoutManager() == null ? null
							: getParentContainer().getLayoutManager().constraints(this);
					originalIndex = getParentContainer() == null ? 0 : getParentContainer().getElements().indexOf(this);
					priorityBeforeDrag = getPriority();
					draggingElement = this;
					setPriority(ZPriority.DRAG);

					/*
					 * If the element is not already in the Screen, then remove it from it's current
					 * parent and add to the screen. To prevent
					 */
					if (getElementParent() != null) {
						wasParentedBy = getElementParent();
						Vector2f abs = getAbsolute();
						getElementParent().removeElement(this);
						screen.addElement(this);
						setPosition(abs);
					}
				}

				if (dragSupport != null) {
					DragEvent<BaseElement> dev = new DragEvent<BaseElement>(evt, this, 0, DragEventType.start,
							draggingElement, copy ? DragMode.Copy : DragMode.Move);
					dragSupport.fireEvent(dev);
				}
			}

		}, MouseUIButtonEvent.LEFT);

		onMouseReleased((evt) -> {
			finishDrag(evt);
		}, MouseUIButtonEvent.LEFT);
	}

	protected void finishDrag(MouseUIButtonEvent<BaseElement> evt) {
		if (dragged) {

			BaseElement dropEl = getScreen().getDropElement();
			boolean success = false;
			if (dragSupport != null) {
				DragEvent<BaseElement> dragEvt = new DragEvent<BaseElement>(evt, this, 0, DragEventType.end,
						draggingElement, copy ? DragMode.Copy : DragMode.Move);
				dragEvt.setTarget(dropEl);
				dragSupport.fireEvent(dragEvt);
				success = dragEvt.isConsumed();
			}

			if (unclipOnDrag) {
				draggingElement.setClippingEnabled(wasClippingEnabled);
			}

			if (success) {
				finishDrop(dropEl, true);
			} else {
				abortDrop();
			}
		}
	}

	protected BaseElement createCopy(MouseUIButtonEvent<BaseElement> evt) {
		/* For subclasses to override and build to element to actually drag */
		return null;
	}

	public DragMode getDragMode() {
		return dragMode;
	}

	public void setDragMode(DragMode dragMode) {
		this.dragMode = dragMode;
	}

	/**
	 * Enables/disables the use of the SlideTo Effect when centering within the drop
	 * element.
	 * 
	 * @param useLockToDropElementEffect boolean
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
	 * Enables/disables springback to original position when dropped outside of a
	 * valid drop element
	 * 
	 * @param useSpringBack boolean
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
	 * @param useSpringBackEffect boolean
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

	public BaseElement getParentDroppable() {
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

	public void bindToDroppable(BaseElement dropEl) {
		if (dropEl == null || dropEl.isDragDropDropElement()) {
			finishDrop(dropEl, false);
		}
	}

	public DragElement unbind(DragListener<BaseElement> listener) {
		if (dragSupport != null)
			dragSupport.unbind(listener);
		return this;
	}

	public DragElement onStart(DragListener<BaseElement> listener) {
		if (dragSupport == null)
			dragSupport = new DragSupport();
		dragSupport.bind(listener, DragEventType.start);
		return this;
	}

	public DragElement onComplete(DragListener<BaseElement> listener) {
		if (dragSupport == null)
			dragSupport = new DragSupport();
		dragSupport.bind(listener, DragEventType.complete);
		return this;
	}

	public DragElement onAborted(DragListener<BaseElement> listener) {
		if (dragSupport == null)
			dragSupport = new DragSupport();
		dragSupport.bind(listener, DragEventType.aborted);
		return this;
	}

	public DragElement onEnd(DragListener<BaseElement> listener) {
		if (dragSupport == null)
			dragSupport = new DragSupport();
		dragSupport.bind(listener, DragEventType.end);
		return this;
	}

	public DragElement on(DragListener<BaseElement> listener) {
		if (dragSupport == null)
			dragSupport = new DragSupport();
		dragSupport.bind(listener);
		return this;
	}

	public DragElement addDragListener(DragListener<BaseElement> listener) {
		if (dragSupport != null)
			dragSupport = new DragSupport();
		dragSupport.addListener(listener);
		return this;
	}

	public DragElement removeDragListener(DragListener<BaseElement> listener) {
		if (dragSupport != null)
			dragSupport.removeListener(listener);
		return this;
	}

	@Override
	public boolean isIgnoreMouseLeftButton() {
		return animating || super.isIgnoreMouseLeftButton();
	}

	private void handleSuccess(BaseElement dropEl) {
		if (dropEl != null) {
			if (parentDroppable != null && parentDroppable != dropEl) {
				parentDroppable.removeElement(draggingElement);
				parentDroppable = null;
			}
			parentDroppable = dropEl;
			BaseElement p = getElementParent();
			if (p != dropEl) {
				if (p != null) {
					p.removeElement(draggingElement);
				} else {
					screen.removeElement(draggingElement);
				}
				dropEl.addElement(draggingElement);
			}
		}
	}

	private void finishDrop(BaseElement dropEl, boolean events) {
		Vector2f absDropLoc = getAbsolute();
		handleSuccess(dropEl);
		if (useLockToDropElementEffect) {
			animating = true;
			Vector2f dest = absDropLoc.subtract(getParentContainer().getAbsolute());
			screen.getEffectManager()
					.applyEffect(new EffectList(new SlideFromEffect(.15f, dest).setElement(this), new RunEffect(() -> {
						animating = false;
						handleSuccess(dropEl);
						cleanUpDrop();
						if (dragSupport != null && events)
							dragSupport.fireEvent(new DragEvent<BaseElement>(DragElement.this, DragEventType.complete,
									draggingElement, copy ? DragMode.Copy : DragMode.Move));
					})));
			return;
		}
		cleanUpDrop();
		if (dragSupport != null && events)
			dragSupport.fireEvent(new DragEvent<BaseElement>(this, DragEventType.complete, draggingElement,
					copy ? DragMode.Copy : DragMode.Move));
	}

	private void abortDrop() {
		if (useSpringBack) {
			Vector2f absDropLoc = getPixelPosition().clone();
			if (wasParentedBy != null) {
				screen.removeElement(draggingElement);
				wasParentedBy.insertChild(draggingElement, wasConstraints, originalIndex);
				wasParentedBy = null;
				wasConstraints = null;
				sizeToContent();
			}
			if (useSpringBackEffect) {
				animating = true;
				Vector2f dest = absDropLoc.subtract(getParentContainer().getAbsolute());
				screen.getEffectManager().applyEffect(
						new EffectList(new SlideFromEffect(.25f, dest).setElement(this), new RunEffect(() -> {
							animating = false;
							if (dragSupport != null)
								dragSupport.fireEvent(new DragEvent<BaseElement>(DragElement.this,
										DragEventType.aborted, draggingElement, copy ? DragMode.Copy : DragMode.Move));
							cleanUpDrop();
						})));
				return;
			}
		}
		cleanUpDrop();
		if (dragSupport != null)
			dragSupport.fireEvent(new DragEvent<BaseElement>(this, DragEventType.aborted, draggingElement,
					copy ? DragMode.Copy : DragMode.Move));
	}

	private void cleanUpDrop() {
		if (priorityBeforeDrag != null)
			setPriority(priorityBeforeDrag);
		dragged = false;
	}
}
