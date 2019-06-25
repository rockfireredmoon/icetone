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
package icetone.controls.containers;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.OSRBridge;
import icetone.core.Size;
import icetone.core.ToolKit;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.mouse.HoverEvent.HoverEventType;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.core.event.mouse.MouseUIWheelEvent.Direction;
import icetone.core.layout.FillLayout;

/**
 *
 * @author t0neg0d
 */
public class OSRViewPort extends Element {
	private OSRBridge bridge;
	private boolean rotateEnabled = true;
	private boolean useLeftMouseRotate = false;
	private boolean zoomEnabled = true;
	private boolean enabled = false;
	private boolean mouseLook = false;
	private int lastX = 0, lastY = 0;
	private Element elOverlay;

	/**
	 * Creates a new instance of the OSRViewPort control
	 * 
	 * @param screen        The screen control the Element is to be added to
	 * @param position      A Vector2f containing the x/y position of the Element
	 * @param dimensions    A Vector2f containing the width/height dimensions of the
	 *                      Element
	 * @param resizeBorders A Vector4f containg the border information used when
	 *                      resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg    The default image to use for the Element
	 */
	public OSRViewPort(BaseScreen screen, Size dimensions) {
		super(screen, dimensions);

		elOverlay = new Element(screen);
		elOverlay.setStyleClass("overlay");
		elOverlay.setResizable(true);
		elOverlay.setMovable(false);
		elOverlay.setIgnoreMouse(true);

		setLayoutManager(new FillLayout());
		addElement(elOverlay);

		onMousePressed(evt -> {
			if (ToolKit.isAndroid()) {
				this.enabled = true;
				setHovering(true);
			}
			if (rotateEnabled && useLeftMouseRotate) {
				mouseLook = true;
				if (!ToolKit.isAndroid())
					screen.getApplication().getInputManager().setCursorVisible(false);
				bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
			}
			evt.setConsumed();
			if (ToolKit.isAndroid()) {
				this.enabled = true;
				setHovering(true);
			}
			if (rotateEnabled && useLeftMouseRotate) {
				mouseLook = true;
				if (!ToolKit.isAndroid())
					screen.getApplication().getInputManager().setCursorVisible(false);
				bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
			}
			evt.setConsumed();
		});

		onMouseReleased(evt -> {
			if (rotateEnabled && useLeftMouseRotate) {
				mouseLook = false;
				if (!ToolKit.isAndroid())
					screen.getApplication().getInputManager().setCursorVisible(true);
				bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
			}
			evt.setConsumed();
		});

		onMousePressed(evt -> {
			if (rotateEnabled && !useLeftMouseRotate) {
				mouseLook = true;
				if (!ToolKit.isAndroid())
					screen.getApplication().getInputManager().setCursorVisible(false);
				bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
			}
			evt.setConsumed();
		}, MouseUIButtonEvent.RIGHT);

		onMouseReleased(evt -> {
			if (ToolKit.isAndroid()) {
				if (!mouseLook)
					this.enabled = false;
				setHovering(false);
			}
			if (rotateEnabled && !useLeftMouseRotate) {
				mouseLook = false;
				if (!ToolKit.isAndroid())
					screen.getApplication().getInputManager().setCursorVisible(true);
				bridge.getChaseCamera().onAction("ChaseCamToggleRotate", evt.isPressed(), bridge.getCurrentTPF());
			}
			evt.setConsumed();
		}, MouseUIButtonEvent.RIGHT);

		onHover(evt -> {
			if (evt.getEventType() == HoverEventType.enter) {
				this.enabled = true;
				evt.setConsumed();
			} else if (!mouseLook) {
				this.enabled = false;
				evt.setConsumed();
			}
		});

		onMouseMoved(evt -> {
			if (mouseLook) {
				if (enabled) {
					if (evt.getY() > lastY)
						bridge.getChaseCamera().onAnalog("ChaseCamUp", -evt.getDY() * (bridge.getCurrentTPF() / 2),
								bridge.getCurrentTPF());
					else
						bridge.getChaseCamera().onAnalog("ChaseCamDown", evt.getDY() * (bridge.getCurrentTPF() / 2),
								bridge.getCurrentTPF());
					if (evt.getX() > lastX)
						bridge.getChaseCamera().onAnalog("ChaseCamMoveRight",
								evt.getDX() * (bridge.getCurrentTPF() / 2), bridge.getCurrentTPF());
					else
						bridge.getChaseCamera().onAnalog("ChaseCamMoveLeft",
								-evt.getDX() * (bridge.getCurrentTPF() / 2), bridge.getCurrentTPF());
				}
				lastX = evt.getX();
				lastY = evt.getY();
				evt.setConsumed();
			}
		});

		onMouseWheel(evt -> {
			if (evt.getDirection() == Direction.up) {
				if (zoomEnabled) {
					if (enabled) {
						bridge.getChaseCamera().onAnalog("ChaseCamZoomIn",
								evt.getDeltaWheel() * (bridge.getCurrentTPF() / 4), bridge.getCurrentTPF());
					}
				}
				evt.setConsumed();
			} else if (evt.getDirection() == Direction.down) {
				if (zoomEnabled) {
					if (enabled) {
						bridge.getChaseCamera().onAnalog("ChaseCamZoomIn",
								evt.getDeltaWheel() * (bridge.getCurrentTPF() / 4), bridge.getCurrentTPF());
					}
				}
				evt.setConsumed();
			}
		});

		onElementEvent(evt -> {
			bridge.getViewPort().setEnabled(false);
		}, Type.HIDDEN);

		onElementEvent(evt -> {
			bridge.getViewPort().setEnabled(true);
		}, Type.SHOWN);
	}

	/**
	 * Creates an instance of the OSRBridge class for rendering an off-screen
	 * ViewPort
	 * 
	 * @param root   The Node containing the scene to render
	 * @param width  The render width
	 * @param height The render height
	 */
	public void setOSRBridge(Node root, int width, int height) {

		bridge = new OSRBridge(screen.getApplication().getRenderManager(), width, height, root);
		addOSRBridge(bridge);
		bridge.getChaseCamera().setDragToRotate(true);
		bridge.getChaseCamera().setHideCursorOnRotate(false);
	}

	/**
	 * Sets the rotation control to respond to the left mouse button
	 * 
	 * @param useLeftMouseRotate
	 */
	public void setLeftMouseButtonRotation(boolean useLeftMouseRotate) {
		this.useLeftMouseRotate = useLeftMouseRotate;
	}

	/**
	 * Sets the background color of the OSRViewPort (default is transparent)
	 * 
	 * @param color
	 */
	public void setBackgroundColor(ColorRGBA color) {
		bridge.getViewPort().setBackgroundColor(color);
	}

	/**
	 * Sets the default distance between the camera and the focus node
	 * 
	 * @param distance
	 */
	public void setCameraDistance(float distance) {
		bridge.getChaseCamera().setDefaultDistance(distance);
	}

	/**
	 * Sets the default horizontal rotation of the camera.
	 * 
	 * @param angleInRads
	 */
	public void setCameraHorizonalRotation(float angleInRads) {
		bridge.getChaseCamera().setDefaultHorizontalRotation(angleInRads);
	}

	/**
	 * Sets the default vertical rotation of the camera
	 * 
	 * @param angleInRads
	 */
	public void setCameraVerticalRotation(float angleInRads) {
		bridge.getChaseCamera().setDefaultVerticalRotation(angleInRads);
	}

	/**
	 * Sets the minumum zoom distance between the camera and the focus node
	 * 
	 * @param distance
	 */
	public void setCameraMinDistance(float distance) {
		bridge.getChaseCamera().setMinDistance(distance);
	}

	/**
	 * Sets the maximum zoom distance between the camera and the focus node
	 */
	public void setCameraMaxDistance(float distance) {
		bridge.getChaseCamera().setMaxDistance(distance);
	}

	/**
	 * Sets the minimum limit for vertical camera rotation
	 * 
	 * @param angleInRads
	 */
	public void setCameraMinVerticalRotation(float angleInRads) {
		bridge.getChaseCamera().setMinVerticalRotation(angleInRads);
	}

	/**
	 * Sets the maximum limit for vertical camera rotation
	 * 
	 * @param angleInRads
	 */
	public void setCameraMaxVerticalRotation(float angleInRads) {
		bridge.getChaseCamera().setMaxVerticalRotation(angleInRads);
	}

	/**
	 * Enables/disables camera rotation control for the OSRViewPort
	 * 
	 * @param rotateEnabled
	 */
	public void setUseCameraControlRotate(boolean rotateEnabled) {
		this.rotateEnabled = rotateEnabled;
	}

	/**
	 * Enables/disables camera zoom control for the OSRViewPort
	 * 
	 * @param zoomEnabled
	 */
	public void setUseCameraControlZoom(boolean zoomEnabled) {
		this.zoomEnabled = zoomEnabled;
	}

	/**
	 * Returns the OSRBridge instance for the OSRViewPort
	 * 
	 * @return OSRBridge
	 */
	public OSRBridge getOSRBridge() {
		return this.bridge;
	}

}
