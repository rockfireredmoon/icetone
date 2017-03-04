/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2017, Emerald Icemoon
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

import java.util.Objects;
import java.util.concurrent.Callable;

import com.jme3.math.Vector2f;

import icetone.controls.text.ToolTip;
import icetone.core.event.MouseMovementListener;
import icetone.core.event.MouseUIMotionEvent;
import icetone.core.utils.Alarm.AlarmTask;

/**
 * Monitors mouse and touch event, decides when to popup a tooltip, and deals
 * with the removing of that tooltip. It has several different modes of
 * operation, which are set using {@link ToolTipManager#setMode(Mode)}, and 3
 * different types of delay. The defaults are setup to act like traditional
 * desktop tooltips.
 * <p>
 * One special mode is {@link Mode#DETACHABLE}, which allows the standard shared
 * tooltips to be dragged. If you want to give this behaviour to your own custom
 * tooltips, look at what {@link ToolTipManager#createSharedToolTip}.
 */
public class ToolTipManager implements ToolTipProvider, MouseMovementListener<UIEventTarget> {

	public enum Mode {
		FOLLOW, STATIC, STICKY, DETACHABLE
	}

	private BaseElement currentMouseFocusElement;
	private ToolTipProvider currentProvider;
	private Vector2f mouseXY;
	private ElementManager<UIEventTarget> screen;
	private ToolTip sharedTooltip;
	private BaseElement toolTip;
	private boolean useToolTips = true;
	private Mode mode = Mode.STATIC;
	private float showDelay = 2f;
	private float idleHideDelay = 10f;
	private float hideDelay = 0.5f;
	private AlarmTask popupTask;
	private AlarmTask idleHideTask;
	private AlarmTask hideTask;
	private BaseElement visibleTip, waitingToHide;

	public void cleanup() {
		screen.removeMouseMovementListener(this);
	}

	public float getHideDelay() {
		return hideDelay;
	}

	/**
	 * Sets how long to wait before actually hiding a tip once the mouse has
	 * left the component. Note, this is ignore when {@link Mode} is
	 * {@link Mode#FOLLOW}.
	 * 
	 * @param hideDelay
	 */
	public void setHideDelay(float hideDelay) {
		this.hideDelay = hideDelay;
	}

	public float getShowDelay() {
		return showDelay;
	}

	public void setShowDelay(float showDelay) {
		this.showDelay = showDelay;
	}

	public float getIdleHideDelay() {
		return idleHideDelay;
	}

	public void setIdleHideDelay(float idleHideDelay) {
		this.idleHideDelay = idleHideDelay;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		hideToolTip();
		sharedTooltip = null;
	}

	public ToolTipProvider createDefaultToolTipProvider(BaseElement element) {
		return this;
	}

	public BaseElement createToolTip(Vector2f mouseXY, BaseElement el) {
		if (el.getToolTipText() != null && el.getToolTipText().length() > 0) {
			if (sharedTooltip == null) {
				sharedTooltip = createSharedToolTip();
				sharedTooltip.setMouseFocusable(mode == Mode.STICKY || mode == Mode.DETACHABLE);
				if (mode == Mode.DETACHABLE) {
					sharedTooltip.setMovable(true);
				}

			}
		} else {
			return null;
		}
		return sharedTooltip;
	}

	protected ToolTip createSharedToolTip() {
		return new ToolTip(screen) {

			boolean detached = false;

			@Override
			public void controlMoveHook() {
				if (!isAdjusting() && mode == Mode.DETACHABLE && !detached) {
					detached = true;
					cancelHideTask();
					cancelIdleHideTask();
					sharedTooltip = null;
					visibleTip = null;
					toolTip = null;
					onMouseReleased((evt) -> {
						if (evt.getClicks() == 2) {
							setDestroyOnHide(true);
							hide();
						}
					});
				}
			}
		};
	}

	public BaseElement getToolTipFocus() {
		return this.currentMouseFocusElement;
	}

	/**
	 * Returns if ToolTips are enabled/disabled
	 * 
	 * @return boolean
	 */
	public boolean getUseToolTips() {
		return useToolTips;
	}

	public void hideToolTip() {
		removeToolTip(toolTip);
		toolTip = null;
	}

	public void init(ElementManager<UIEventTarget> screen) {
		this.screen = screen;
		screen.addMouseMovementListener(this);
	}

	@Override
	public void onMouseMove(MouseUIMotionEvent<UIEventTarget> evt) {
		updateToolTipLocation();
	}

	public void removeToolTipFor(BaseElement element) {
		if (getUseToolTips()) {
			if (getToolTipFocus() == element)
				hideToolTip();
			else if (getToolTipFocus() != null) {
				if (element.getElements().contains(getToolTipFocus()))
					hideToolTip();
			}
		}

	}

	/**
	 * Enables/disables the use of ToolTips
	 * 
	 * @param useToolTips
	 *            boolean
	 */
	public void setUseToolTips(boolean useToolTips) {
		if (this.useToolTips != useToolTips) {
			this.useToolTips = useToolTips;
			updateToolTipLocation();
		}
	}

	public void updateToolTipLocation() {

		if (useToolTips) {
			BaseElement mouseFocusElement = screen.getMouseFocusElement();

			if ((mode == Mode.STICKY || mode == Mode.DETACHABLE) && mouseFocusElement != null
					&& mouseFocusElement.equals(visibleTip)) {
				toolTip = visibleTip;
				cancelHideTask();
				restartIdleHideTask();
				return;
			}

			if (mouseFocusElement != null && screen.getApplication().getInputManager().isCursorVisible()) {
				ToolTipProvider toolTipProvider = mouseFocusElement.getToolTipProvider();
				if (toolTipProvider == null) {
					toolTipProvider = createDefaultToolTipProvider(mouseFocusElement);
				}

				if (toolTipProvider != null) {
					if (toolTip == null || currentProvider == null || !toolTipProvider.equals(currentProvider)
							|| !currentMouseFocusElement.equals(mouseFocusElement)) {

						/*
						 * Focused element has changed or tooltip needs to be
						 * reshown on the same component after it was hidden
						 */
						BaseElement lastToolTip = visibleTip;

						currentProvider = toolTipProvider;
						toolTip = currentProvider.createToolTip(mouseXY, mouseFocusElement);
						if (toolTip != null) {

							if (Objects.equals(lastToolTip, toolTip)) {
								popupTooltip();
							} else {
								if (lastToolTip != null) {
									destroyTooltip(lastToolTip);
									popupTooltip();
								} 

								if (showDelay > 0)
									popupTask = ToolKit.get().getAlarm().timed(new Callable<Void>() {
										public Void call() {
											popupTooltip();
											return null;
										}
									}, showDelay);
								else
									popupTooltip();
							}
						} else {
							removeToolTip(lastToolTip);
						}
						currentMouseFocusElement = mouseFocusElement;
					} else {

						if (toolTip != null && mouseFocusElement != null
								&& !Objects.equals(toolTip.getText(), mouseFocusElement.getToolTipText())) {
							/* Focussed element is same but text has changed */
							toolTip.setText(mouseFocusElement.getToolTipText());
							toolTip.sizeToContent();
						}

						cancelHideTask();
						restartIdleHideTask();

						/* Just moved */
						if (mode == Mode.FOLLOW)
							setToolTipLocation(toolTip);
					}
					screen.applyZOrder();
				} else if (toolTip != null) {
					removeToolTip(toolTip);
					toolTip = null;
				}
			} else {

				/* Just moved */
				if (mode == Mode.FOLLOW) {
					if (waitingToHide != null && waitingToHide.isVisible()) {
						cancelHideTask();
						destroyTooltip(waitingToHide);
					}
					// else
					// setToolTipLocation(waitingToHide);
				}

				if (toolTip != null) {
					removeToolTip(toolTip);
					toolTip = null;
				}
			}
		} else {
			if (toolTip != null) {
				removeToolTip(toolTip);
				toolTip = null;
			}
		}
	}

	private void cancelPopupTask() {
		if (popupTask != null) {
			popupTask.cancel();
			popupTask = null;
		}
	}

	private void cancelIdleHideTask() {
		if (idleHideTask != null) {
			idleHideTask.cancel();
			idleHideTask = null;
		}
	}

	private void cancelHideTask() {
		if (hideTask != null) {
			hideTask.cancel();
			hideTask = null;
			waitingToHide = null;
		}
	}

	private void restartIdleHideTask() {
		cancelIdleHideTask();
		if (idleHideDelay > 0) {

			idleHideTask = ToolKit.get().getAlarm().timed(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					destroyTooltip(toolTip);
					ToolTipManager.this.toolTip = null;
					return null;
				}
			}, idleHideDelay);
		}
	}

	private void restartHideTask(BaseElement toolTip) {
		cancelHideTask();
		waitingToHide = toolTip;
		if (hideDelay > 0) {
			hideTask = ToolKit.get().getAlarm().timed(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					destroyTooltip(toolTip);
					waitingToHide = null;
					return null;
				}
			}, hideDelay);
		}
	}

	protected void popupTooltip() {
		// The tooltip impl. might have added itself to the
		// screen
		setToolTipLocation(toolTip);

		/*
		 * The toolTip might have become null when its location was set as the
		 * result of dragging starting
		 */
		if (toolTip != null) {
			visibleTip = toolTip;
			if (!screen.getElements().contains(toolTip)) {
				screen.showElement(toolTip);
			} else {
				toolTip.bringToFront();
				toolTip.show();
			}

			restartIdleHideTask();
		}
	}

	private void removeToolTip(BaseElement toolTip) {
		cancelPopupTask();
		cancelIdleHideTask();
		if (toolTip != null) {
			if (hideDelay > 0 && mode != Mode.FOLLOW)
				restartHideTask(toolTip);
			else
				destroyTooltip(toolTip);
		}
	}

	private void destroyTooltip(BaseElement toolTip) {
		if (toolTip != null) {
			toolTip.setDestroyOnHide(true);
			toolTip.hide();
			if(toolTip == visibleTip)
				visibleTip = null;
		}
	}

	private void setToolTipLocation(BaseElement toolTip) {
		if (toolTip != null) {
			toolTip.runAdjusting(() -> {
				Vector2f touchXY = screen.getTouchXY();
				Vector2f mouseXY = screen.getMouseXY();
				float nextX = (ToolKit.isAndroid()) ? touchXY.x - (toolTip.getWidth() / 2)
						: mouseXY.x - (toolTip.getWidth() / 2);
				if (nextX < 0)
					nextX = 0;
				else if (nextX + toolTip.getWidth() > screen.getWidth())
					nextX = screen.getWidth() - toolTip.getWidth();
				float nextY = ((ToolKit.isAndroid()) ? touchXY.y + 40 : mouseXY.y + 40);
				if (nextY < 0)
					nextY = (ToolKit.isAndroid()) ? touchXY.y + 5 : mouseXY.y + 5;
				// TODO no idea if this makes any difference

				toolTip.moveTo((int) nextX, (int) nextY);

				/*
				 * Prevent the tooltip from being under the mouse when at the
				 * bottom of the screen
				 */
				nextX = toolTip.getX();
				nextY = toolTip.getY();
				if (mouseXY.y >= nextY && mouseXY.y <= nextY + toolTip.getHeight() && mouseXY.x >= nextX
						&& mouseXY.x <= nextX + toolTip.getWidth()) {
					if (mouseXY.y >= nextY && mouseXY.y <= nextY + toolTip.getHeight()) {
						if (mouseXY.y > screen.getHeight() / 2f)
							nextY = mouseXY.y - 10 - toolTip.getHeight();
						else
							nextY = mouseXY.y + 10;
					}
					toolTip.moveTo((int) nextX, (int) nextY);
				}
			});
		}
	}
}
