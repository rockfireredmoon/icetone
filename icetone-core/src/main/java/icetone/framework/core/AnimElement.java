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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.core.UIEventTarget;
import icetone.core.event.MouseButtonListener;
import icetone.core.event.MouseButtonSupport;
import icetone.core.event.MouseMovementListener;
import icetone.core.event.MouseMovementSupport;
import icetone.core.event.MouseUIButtonEvent;
import icetone.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public abstract class AnimElement extends Node implements Transformable, UIEventTarget {
	public static enum ZOrderEffect {
		Both, Child, None, Self
	}

	public List<TemporalAction> actions = new ArrayList<>();
	protected Vector4f clippingPosition = null;
	protected ColorRGBA color = new ColorRGBA();
	protected Object dataStruct;
	protected Vector2f dimensions = new Vector2f(0, 0);
	protected String elementKey;
	protected boolean ignoreMouse = false;
	protected boolean isMovable = false;
	protected Material mat = null;
	protected AnimElementMesh mesh;
	protected MouseButtonSupport mouseButtonSupport;
	protected MouseMovementSupport mouseMovementSupport;
	protected Vector2f origin = new Vector2f(0, 0);
	protected AnimLayer parentLayer = null;
	protected Vector2f position = new Vector2f(0, 0);
	protected Map<String, QuadData> quads = new LinkedHashMap<>();
	protected float rotation;
	protected Vector2f scale = new Vector2f(1, 1);
	protected BaseScreen screen;
	protected Vector2f skew = new Vector2f(0, 0);
	protected Spatial spatial;
	protected Texture tex;
	protected boolean useClip;
	protected Map<String, TextureRegion> uvs = new HashMap<>();
	protected float z = 1;
	protected float zOrder = -1f;
	protected ZOrderEffect zOrderEffect = ZOrderEffect.Child;
	private Geometry geom;
	private boolean ignoreLeftMouseButton;
	private boolean ignoreMouseMovement;
	private boolean ignoreRightMouseButton;
	private List<QuadData> tempQuads = new LinkedList<>();
	private Vector2f worldPosition = new Vector2f();
	private float worldRotation = 0;
	private float zOrderStepMinor = 0.00001f;

	public AnimElement(BaseScreen screen) {
		this.screen = screen;
		mesh = new AnimElementMesh(this);
	}

	@Override
	public void addAction(TemporalAction action) {
		action.setTransformable(this);
		actions.add(action);
	}

	public QuadData addQuad(String quadKey, Vector2f size, Vector2f position, Vector2f origin) {
		if (zOrder == -1)
			zOrder = getPositionZ();
		Vector2f pos = new Vector2f(position).subtract(origin);

		QuadData qd = new QuadData(this, quadKey, null, pos.x, pos.y, size.x,
				size.y, origin);
		quads.put(quadKey, qd);
		qd.setPositionZ(zOrder);
		zOrder += zOrderStepMinor;
		flagForUpdate();
		return qd;
	}

	public QuadData addQuad(String quadKey, String regionKey, Vector2f position, Vector2f origin) {
		if (zOrder == -1)
			zOrder = getPositionZ();
		Vector2f pos = new Vector2f(position).subtract(origin);

		TextureRegion textureRegion = uvs.get(regionKey);
		QuadData qd = new QuadData(this, quadKey, textureRegion, pos.x, pos.y, textureRegion.getRegionWidth(),
				textureRegion.getRegionHeight(), origin);
		quads.put(quadKey, qd);
		qd.setPositionZ(zOrder);
		zOrder += zOrderStepMinor;
		flagForUpdate();
		return qd;
	}

	public QuadData addQuad(String quadKey, String regionKey, Vector2f position, Vector2f origin, String parentKey) {
		if (zOrder == -1)
			zOrder = getPositionZ();
		Vector2f pos = new Vector2f(position).subtract(origin);

		QuadData qd = new QuadData(this, quadKey, uvs.get(regionKey), pos.x, pos.y, uvs.get(regionKey).getRegionWidth(),
				uvs.get(regionKey).getRegionHeight(), origin);
		qd.parent = quads.get(parentKey);
		// qd.setPositionX(qd.getPositionX()-qd.parent.getPositionX());
		// qd.setPositionY(qd.getPositionY()-qd.parent.getPositionY());
		qd.setPositionZ(zOrder);
		zOrder += zOrderStepMinor;
		quads.put(quadKey, qd);
		flagForUpdate();
		return qd;
	}

	public void addQuadAction(String quadKey, TemporalAction action) {
		quads.get(quadKey).addAction(action);
	}

	public TextureRegion addTextureRegion(String regionKey, int x, int y, int w, int h) {
		TextureRegion tr = new TextureRegion(tex, x, y, w, h);
		tr.flip(false, true);
		uvs.put(regionKey, tr);
		return tr;
	}

	public void addTextureRegion(String regionKey, TextureRegion tr) {
		uvs.put(regionKey, tr);
	}

	public abstract void animElementUpdate(float tpf);

	public void bringQuadToFront(QuadData quad) {
		quads.remove(quad.key);
		quads.put(quad.key, quad);
		resetZOrder();
	}

	public void centerQuads() {
		float totalWidth = 0, totalHeight = 0;
		for (QuadData q : quads.values()) {
			if (q.getPositionX() + q.getTextureRegion().regionWidth > totalWidth)
				totalWidth = q.getPositionX() + q.getTextureRegion().regionWidth;
			if (q.getPositionY() + q.getTextureRegion().regionHeight > totalHeight)
				totalHeight = q.getPositionY() + q.getTextureRegion().regionHeight;
		}
		for (QuadData q : quads.values()) {
			if (q.parent == null) {
				q.setPositionX(q.getPositionX() - (totalWidth / 2));
				q.setPositionY(q.getPositionY() - (totalHeight / 2));
			}
		}
	}

	public void deallocateBuffers() {
		mesh.deallocateBuffers();
	}

	public void flagForUpdate() {
		mesh.buildPosition = true;
		mesh.buildTexCoords = true;
		mesh.buildColor = true;
		mesh.buildIndices = true;
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

	public Vector4f getClippingPosition() {
		return clippingPosition;
	}

	@Override
	public ColorRGBA getColor() {
		return color;
	}

	@Override
	public float getColorA() {
		return color.g;
	}

	@Override
	public float getColorB() {
		return color.b;
	}

	@Override
	public float getColorG() {
		return color.g;
	}

	@Override
	public float getColorR() {
		return color.r;
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

	@Override
	public Vector2f getDimensions() {
		return this.dimensions;
	}

	public String getElementKey() {
		return elementKey;
	}

	@Override
	public UIEventTarget getEventTargetParent() {
		return null;
	}

	@Override
	public float getHeight() {
		return dimensions.y;
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

	public boolean getIsInitialized() {
		return this.mesh.init;
	}

	@Override
	public boolean getIsMovable() {
		return this.isMovable;
	}

	public Material getMaterial() {
		return this.mat;
	}

	public ColorRGBA getMaterialColor() {
		if (mat != null)
			return (ColorRGBA) mat.getParam("Color").getValue();
		return null;
	}

	@Override
	public MouseButtonSupport getMouseButtonSupport() {
		return mouseButtonSupport;
	}

	@Override
	public MouseMovementSupport getMouseMovementSupport() {
		return mouseMovementSupport;
	}

	@Override
	public Vector2f getOrigin() {
		return this.origin;
	}

	@Override
	public float getOriginX() {
		return this.origin.x;
	}

	@Override
	public float getOriginY() {
		return this.origin.y;
	}

	public AnimLayer getParentLayer() {
		return this.parentLayer;
	}

	@Override
	public Vector2f getPosition() {
		return this.position;
	}

	@Override
	public float getPositionX() {
		return position.x;
	}

	@Override
	public float getPositionY() {
		return position.y;
	}

	@Override
	public float getPositionZ() {
		return z;
	}

	public QuadData getQuad(int index) {
		return this.quads.values().toArray(new QuadData[0])[index];
	}

	public QuadData getQuad(String key) {
		return this.quads.get(key);
	}

	public Map<String, QuadData> getQuads() {
		return this.quads;
	}

	public Vector2f getQuadWorldPosition(QuadData qd) {
		setWorldTransforms(qd);
		return worldPosition;
	}

	public float getQuadWorldRotation(QuadData qd) {
		setWorldTransforms(qd);
		return worldRotation;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public Vector2f getScale() {
		return scale;
	}

	@Override
	public float getScaleX() {
		return scale.x;
	}

	@Override
	public float getScaleY() {
		return scale.y;
	}

	@Override
	public BaseScreen getScreen() {
		return screen;
	}

	@Override
	public Vector2f getSkew() {
		return this.skew;
	}

	@Override
	public float getSkewX() {
		return skew.x;
	}

	@Override
	public float getSkewY() {
		return skew.y;
	}

	@Override
	public Vector2f getTCOffset() {
		return null;
	}

	@Override
	public float getTCOffsetX() {
		return 0;
	}

	@Override
	public float getTCOffsetY() {
		return 0;
	}

	public Texture getTexture() {
		return tex;
	}

	public TextureRegion getTextureRegion(String regionKey) {
		return uvs.get(regionKey);
	}

	public Map<String, TextureRegion> getTextureRegions() {
		return this.uvs;
	}

	public Map<String, TextureRegion> getUVs() {
		return this.uvs;
	}

	@Override
	public float getWidth() {
		return dimensions.x;
	}

	public ZOrderEffect getZOrderEffect() {
		return this.zOrderEffect;
	}

	public void initialize() {
		flagForUpdate();
		mesh.initialize();

		useClip = false;
		if (mat != null) {
			useClip = (Boolean) getMaterial().getParam("UseClipping").getValue();
		}
		mat = new Material(ToolKit.get().getApplication().getAssetManager(), "icetone/shaders/Unshaded.j3md");
		mat.setTexture("ColorMap", tex);

		// TODO ... not sure what to do here. If not true, we cant have coloured
		// text, if true,
		// 'outline' fonts look weird. Probably need to do some stuff in the
		// shader?
		mat.setBoolean("VertexColor", true);

		resetClipState();
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

		geom = new Geometry();
		geom.setMesh(mesh);

		attachChild(geom);
		setMaterial(mat);

		flagForUpdate();
		geom.updateModelBound();
	}

	public boolean isIgnoreLeftMouseButton() {
		return ignoreLeftMouseButton;
	}

	public boolean isIgnoreRightMouseButton() {
		return ignoreRightMouseButton;
	}

	public boolean isUseClip() {
		return useClip;
	}

	public void moveQuad(String quadKey, float z) {
		QuadData q = quads.get(quadKey);
		q.setPositionZ(z);
	}

	public void moveQuad(String quadKey, float x, float y) {
		QuadData q = quads.get(quadKey);
		q.setPositionX(x);
		q.setPositionY(y);
	}

	public void resetZOrder() {
		zOrder = getPositionZ();
		for (QuadData qd : quads.values()) {
			qd.setPositionZ(zOrder);
			zOrder -= zOrderStepMinor;
		}
		mesh.buildPosition = true;
		mesh.buildTexCoords = true;
		mesh.buildColor = true;
	}

	public void rotateQuad(String quadKey, int rotation) {
		quads.get(quadKey).setRotation(rotation);
	}

	public void scaleQuad(String quadKey, float scaleX, float scaleY) {
		QuadData q = quads.get(quadKey);
		q.setScaleX(scaleX);
		q.setScaleY(scaleY);
	}

	public void sendQuadToBack(QuadData quad) {
		tempQuads.clear();
		for (QuadData qd : quads.values()) {
			if (qd != quad)
				tempQuads.add(qd);
		}
		quads.clear();
		quads.put(quad.key, quad);
		for (QuadData qd : tempQuads) {
			quads.put(qd.key, qd);
		}
		resetZOrder();
	}

	public void setClippingBounds(float x, float y, float z, float w) {
		if (clippingPosition == null)
			clippingPosition = new Vector4f();
		clippingPosition.set(x, y, z, w);
		resetClipState();
		update(0);
	}

	public void setClippingBounds(Vector4f clip) {
		if (clip == null) {
			clippingPosition = new Vector4f();
		} else {
			if (clippingPosition == null)
				clippingPosition = new Vector4f(clip);
			else
				clippingPosition.set(clip);
		}
		resetClipState();
		update(0);
	}

	@Override
	public void setColor(ColorRGBA color) {
		this.color.set(color);
		mesh.buildColor = true;
	}

	@Override
	public void setColorA(float a) {
		this.color.a = a;
		mesh.buildColor = true;
	}

	@Override
	public void setColorB(float b) {
		this.color.b = b;
		mesh.buildColor = true;
	}

	@Override
	public void setColorG(float g) {
		this.color.g = g;
		mesh.buildColor = true;
	}

	@Override
	public void setColorR(float r) {
		this.color.r = r;
		mesh.buildColor = true;
	}

	public <T extends Object> void setDataStruct(T dataStruct) {
		this.dataStruct = dataStruct;
	}

	@Override
	public void setDimensions(float w, float h) {
		this.dimensions.set(w, h);
		mesh.buildPosition = true;
	}

	@Override
	public void setDimensions(Vector2f dim) {
		this.dimensions.set(dim);
		mesh.buildPosition = true;
	}

	public void setElementKey(String key) {
		this.elementKey = key;
	}

	public void setElementMaterial(Material mat) {
		this.mat = mat;
	}

	@Override
	public void setHeight(float h) {
		this.dimensions.setY(h);
		mesh.buildPosition = true;
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

	public void setMaterialColor(ColorRGBA col) {
		if (mat != null)
			mat.setColor("Color", col);
	}

	@Override
	public void setOrigin(float x, float y) {
		this.origin.set(x, y);
		mesh.buildPosition = true;
	}

	@Override
	public void setOrigin(Vector2f origin) {
		this.origin.set(origin);
		mesh.buildPosition = true;
	}

	@Override
	public void setOriginX(float originX) {
		this.origin.setX(originX);
		mesh.buildPosition = true;
	}

	@Override
	public void setOriginY(float originY) {
		this.origin.setY(originY);
		mesh.buildPosition = true;
	}

	public void setParentLayer(AnimLayer layer) {
		this.parentLayer = layer;
	}

	@Override
	public void setPosition(float x, float y) {
		this.position.set(x, y);
		mesh.buildPosition = true;
	}

	@Override
	public void setPosition(Vector2f pos) {
		this.position.set(pos);
		mesh.buildPosition = true;
	}

	// <editor-fold desc="TRANSFORMABLE">
	@Override
	public void setPositionX(float x) {
		this.position.x = x;
		mesh.buildPosition = true;
	}

	@Override
	public void setPositionY(float y) {
		this.position.y = y;
		mesh.buildPosition = true;
	}

	@Override
	public void setPositionZ(float z) {
		this.z = z;
		zOrder = z;
		for (QuadData qd : quads.values()) {
			qd.setPositionZ(zOrder);
			zOrder -= zOrderStepMinor;
		}
		mesh.buildPosition = true;
	}

	public void setQuadParent(String key, String parentKey) {
		QuadData qd = getQuads().get(key);
		qd.parent = quads.get(parentKey);
		qd.setPositionX(qd.getPositionX() - qd.parent.getPositionX());
		qd.setPositionY(qd.getPositionY() - qd.parent.getPositionY());
	}

	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
		mesh.buildPosition = true;
	}

	@Override
	public void setScale(float x, float y) {
		this.scale.set(x, y);
		mesh.buildPosition = true;

	}

	@Override
	public void setScale(Vector2f scale) {
		this.scale.set(scale);
		mesh.buildPosition = true;
	}

	@Override
	public void setScaleX(float scaleX) {
		this.scale.x = scaleX;
		mesh.buildPosition = true;
	}

	@Override
	public void setScaleY(float scaleY) {
		this.scale.y = scaleY;
		mesh.buildPosition = true;
	}

	@Override
	public void setSkew(float x, float y) {
		this.skew.set(x, y);
		mesh.buildPosition = true;
	}

	@Override
	public void setSkew(Vector2f skew) {
		this.skew.set(skew);
		mesh.buildPosition = true;
	}

	@Override
	public void setSkewX(float x) {
		this.skew.setX(x);
		mesh.buildPosition = true;
	}

	@Override
	public void setSkewY(float y) {
		this.skew.setY(y);
		mesh.buildPosition = true;
	}

	@Override
	public void setTCOffsetX(float x) {

	}

	@Override
	public void setTCOffsetY(float y) {

	}

	public void setTexture(Texture tex) {
		this.tex = tex;
		if (mat != null)
			mat.setTexture("ColorMap", tex);
	}

	public void setUseClip(boolean useClip) {
		this.useClip = useClip;
		resetClipState();
		update(0);
	}

	@Override
	public void setWidth(float w) {
		this.dimensions.setX(w);
		mesh.buildPosition = true;
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
		if (mesh.updateCol)
			geom.updateModelBound();
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

	protected void resetClipState() {
		boolean wantClip = useClip && clippingPosition != null && !clippingPosition.equals(Vector4f.ZERO);
		if (wantClip) {
			mat.setVector4("Clipping", clippingPosition);
			mat.setBoolean("UseClipping", true);
		} else {
			mat.setVector4("Clipping", new Vector4f(0, 0, 0, 0));
			mat.setBoolean("UseClipping", false);
		}
	}

	// Potential Additions
	private void setWorldTransforms(QuadData qd) {
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
