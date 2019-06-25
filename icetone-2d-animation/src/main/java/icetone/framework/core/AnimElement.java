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
package icetone.framework.core;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.UIEventTarget;
import icetone.core.event.mouse.MouseButtonListener;
import icetone.core.event.mouse.MouseButtonSupport;
import icetone.core.event.mouse.MouseMovementListener;
import icetone.core.event.mouse.MouseMovementSupport;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.core.scene.DefaultSceneElement;
import icetone.core.scene.QuadData;
import icetone.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public abstract class AnimElement extends DefaultSceneElement implements Animatable, UIEventTarget {
	public static enum ZOrderEffect {
		Both, Child, None, Self
	}

	public List<TemporalAction> actions = new ArrayList<>();
	protected Object dataStruct;
	protected String elementKey;
	protected boolean ignoreMouse = false;
	protected boolean isMovable = false;
	protected MouseButtonSupport mouseButtonSupport;
	protected MouseMovementSupport mouseMovementSupport;
	protected AnimLayer parentLayer = null;
	protected Spatial spatial;
	protected ZOrderEffect zOrderEffect = ZOrderEffect.Child;
	private boolean ignoreLeftMouseButton;
	private boolean ignoreMouseMovement;
	private boolean ignoreRightMouseButton;
	private Vector2f worldPosition = new Vector2f();
	private float worldRotation = 0;

	public AnimElement(BaseScreen screen) {
		super(screen);
	}

	@Override
	public void addAction(TemporalAction action) {
		action.setTransformable(this);
		actions.add(action);
	}

	@Override
	public float getAbsoluteX() {
		// TODO absolute X
		return getPositionX();
	}

	@Override
	public float getAbsoluteY() {
		// TODO absolute Y
		return getPositionY();
	}

	@Override
	public boolean getContainsAction(TemporalAction action) {
		return actions.contains(action);
	}
	// </editor-fold>

	@SuppressWarnings("unchecked")
	public <T extends Object> T getDataStruct() {
		return (T) dataStruct;
	}

	public abstract void animElementUpdate(float tpf);

	public String getElementKey() {
		return elementKey;
	}

	@Override
	public UIEventTarget getEventTargetParent() {
		return null;
	}

	@Override
	public boolean getIgnoreMouse() {
		return this.ignoreMouse;
	}

	@Override
	public boolean isIgnoreMouseLeftButton() {
		return ignoreLeftMouseButton;
	}

	@Override
	public boolean isIgnoreMouseMovement() {
		return ignoreMouseMovement;
	}

	@Override
	public boolean isIgnoreMouseRightButton() {
		return ignoreLeftMouseButton;
	}

	@Override
	public boolean getIsMovable() {
		return this.isMovable;
	}

	@Override
	public MouseButtonSupport getMouseButtonSupport() {
		return mouseButtonSupport;
	}

	@Override
	public MouseMovementSupport getMouseMovementSupport() {
		return mouseMovementSupport;
	}

	public AnimLayer getParentLayer() {
		return this.parentLayer;
	}

	public Vector2f getQuadWorldPosition(AnimQuadData qd) {
		setWorldTransforms(qd);
		return worldPosition;
	}

	public void addQuadAction(String quadKey, TemporalAction action) {
		((AnimQuadData)quads.get(quadKey)).addAction(action);
	}

	public float getQuadWorldRotation(AnimQuadData qd) {
		setWorldTransforms(qd);
		return worldRotation;
	}

	public ZOrderEffect getZOrderEffect() {
		return this.zOrderEffect;
	}

	public boolean isIgnoreLeftMouseButton() {
		return ignoreLeftMouseButton;
	}

	public boolean isIgnoreRightMouseButton() {
		return ignoreRightMouseButton;
	}

	public <T extends Object> void setDataStruct(T dataStruct) {
		this.dataStruct = dataStruct;
	}

	public void setElementKey(String key) {
		this.elementKey = key;
	}

	public void setIgnoreLeftMouseButton(boolean ignoreLeftMouseButton) {
		this.ignoreLeftMouseButton = ignoreLeftMouseButton;
	}

	@Override
	public void setIgnoreMouse(boolean ignoreMouse) {
		this.ignoreMouse = ignoreMouse;
	}

	public void setIgnoreMouseMovement(boolean ignoreMouseMovement) {
		this.ignoreMouseMovement = ignoreMouseMovement;
	}

	public void setIgnoreRightMouseButton(boolean ignoreRightMouseButton) {
		this.ignoreRightMouseButton = ignoreRightMouseButton;
	}

	@Override
	public void setIsMovable(boolean isMovable) {
		this.isMovable = isMovable;
	}

	public void setParentLayer(AnimLayer layer) {
		this.parentLayer = layer;
	}

	public void setQuadParent(String key, String parentKey) {
		AnimQuadData qd = (AnimQuadData)getQuads().get(key);
		qd.parent = quads.get(parentKey);
		qd.setPositionX(qd.getPositionX() - qd.parent.getPositionX());
		qd.setPositionY(qd.getPositionY() - qd.parent.getPositionY());
	}

	public void setZOrderEffect(ZOrderEffect zOrderEffect) {
		this.zOrderEffect = zOrderEffect;
	}

	public void update(float tpf) {
		// mesh.update(tpf);
		// if (mesh.updateCol)
		// geom.updateModelBound();

		for (TemporalAction a : actions) {
			a.act(tpf);
			if (a.getTime() >= a.getDuration() && a.getAutoRestart()) {
				a.restart();
			}
		}
		for (TemporalAction a : actions) {
			if (a.getTime() >= a.getDuration()) {
				actions.remove(a);
				break;
			}
		}
		animElementUpdate(tpf);

		mesh.update(tpf);
		update();
	}

	public AnimElement addMouseButtonListener(MouseButtonListener<BaseElement> l) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport();
		mouseButtonSupport.addListener(l);
		return this;
	}

	public AnimElement addMouseMovementListener(MouseMovementListener<BaseElement> l) {
		if (mouseMovementSupport == null)
			mouseMovementSupport = new MouseMovementSupport();
		mouseMovementSupport.addListener(l);
		return this;
	}

	public AnimElement onMousePressed(MouseButtonListener<BaseElement> l, int button) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport();
		mouseButtonSupport.bindPressed(l, button);
		return this;
	}

	public AnimElement onMouseReleased(MouseButtonListener<BaseElement> l) {
		onMouseReleased(l, MouseUIButtonEvent.LEFT);
		return this;
	}

	public AnimElement onMouseReleased(MouseButtonListener<BaseElement> l, int button) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport();
		mouseButtonSupport.bindReleased(l, button);
		return this;
	}

	public AnimElement removeMouseButtonListener(MouseButtonListener<BaseElement> l) {
		if (mouseButtonSupport != null)
			mouseButtonSupport.removeListener(l);
		return this;
	}

	public AnimElement removeMouseMovementListener(MouseMovementListener<BaseElement> l) {
		if (mouseMovementSupport != null)
			mouseMovementSupport.removeListener(l);
		return this;
	}

	// Potential Additions
	private void setWorldTransforms(AnimQuadData qd) {
		AnimElement a = this;
		QuadData p = qd.parent;
		worldPosition.set(0, 0);
		worldPosition.subtractLocal(qd.getOrigin());
		worldPosition.multLocal(qd.getScale());
		worldPosition.set(mesh.rot(worldPosition, qd.getRotation()));
		worldPosition.addLocal(qd.getOrigin());
		worldPosition.addLocal(qd.getPosition());
		worldRotation = qd.getRotation();
		while (p != null) {
			worldPosition.subtractLocal(p.getOrigin());
			worldPosition.multLocal(p.getScale());
			worldPosition.set(mesh.rot(worldPosition, p.getRotation()));
			worldPosition.addLocal(p.getOrigin());
			worldPosition.addLocal(p.getPosition());
			worldRotation += p.getRotation();
			p = p.parent;
		}
		while (a != null) {
			worldPosition.subtractLocal(a.getOrigin());
			worldPosition.multLocal(a.getScale());
			worldPosition.set(mesh.rot(worldPosition, a.getRotation()));
			worldPosition.addLocal(a.getOrigin());
			worldPosition.addLocal(a.getPosition());
			worldRotation += a.getRotation();
			if (a.getParent() instanceof AnimElement)
				a = (AnimElement) a.getParent();
			else
				a = null;
		}
		if (clippingPosition != null) {
			if (worldPosition.x > clippingPosition.x)
				clippingPosition.x = worldPosition.x;
			if (worldPosition.y > clippingPosition.y)
				clippingPosition.y = worldPosition.y;
			if (worldPosition.x + qd.getWidth() < clippingPosition.z)
				clippingPosition.z = worldPosition.x + qd.getWidth();
			if (worldPosition.y + qd.getHeight() < clippingPosition.w)
				clippingPosition.w = worldPosition.y + qd.getHeight();
		}
	}
}
