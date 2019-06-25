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
package icetone.core.scene;

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
import com.jme3.texture.Texture;

import icetone.core.BaseScreen;
import icetone.core.ToolKit;

public class DefaultSceneElement extends Node implements SceneElement {

	protected Vector4f clippingPosition = null;
	protected ColorRGBA color = new ColorRGBA();
	protected Vector2f dimensions = new Vector2f(0, 0);
	protected Material mat = null;
	protected SceneMesh mesh;
	protected Vector2f origin = new Vector2f(0, 0);
	protected Vector2f position = new Vector2f(0, 0);
	protected Map<String, QuadData> quads = new LinkedHashMap<>();
	protected float rotation;
	protected Vector2f scale = new Vector2f(1, 1);
	protected BaseScreen screen;
	protected Vector2f skew = new Vector2f(0, 0);
	protected Texture tex;
	protected boolean useClip;
	protected Map<String, TextureRegion> uvs = new HashMap<>();

	protected float z = 1;
	protected float zOrder = -1f;
	private Geometry geom;

	private List<QuadData> tempQuads = new LinkedList<>();

	private float zOrderStepMinor = 0.00001f;

	public DefaultSceneElement(BaseScreen screen) {
		this.screen = screen;
		this.mesh = new SceneMesh(this);
	}

	public QuadData addQuad(String quadKey, String regionKey, Vector2f position, Vector2f origin) {
		if (zOrder == -1)
			zOrder = getPositionZ();
		Vector2f pos = new Vector2f(position).subtract(origin);

		TextureRegion textureRegion = uvs.get(regionKey);
		QuadData qd = createQuad(quadKey, textureRegion, pos.x, pos.y, textureRegion.getRegionWidth(),
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

		QuadData qd = createQuad(quadKey, uvs.get(regionKey), pos.x, pos.y, uvs.get(regionKey).getRegionWidth(),
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

	public QuadData addQuad(String quadKey, Vector2f size, Vector2f position, Vector2f origin) {
		if (zOrder == -1)
			zOrder = getPositionZ();
		Vector2f pos = new Vector2f(position).subtract(origin);

		QuadData qd = createQuad(quadKey, null, pos.x, pos.y, size.x, size.y, origin);
		quads.put(quadKey, qd);
		qd.setPositionZ(zOrder);
		zOrder += zOrderStepMinor;
		flagForUpdate();
		return qd;
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
	public Vector2f getDimensions() {
		return this.dimensions;
	}

	@Override
	public float getHeight() {
		return dimensions.y;
	}

	public boolean getIsInitialized() {
		return this.mesh.init;
	}

	public Material getMaterial() {
		return this.mat;
	}

	public ColorRGBA getMaterialColor() {
		if (mat != null)
			return (ColorRGBA) mat.getParam("Color").getValue();
		return null;
	}

	public SceneMesh getMesh() {
		return mesh;
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
		update();
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
		update();
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

	public void setElementMaterial(Material mat) {
		this.mat = mat;
	}

	public void setHeight(float h) {
		this.dimensions.setY(h);
		mesh.buildPosition = true;
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

	@Override
	public SceneElement getSceneParent() {
		return getParent() instanceof DefaultSceneElement ? (DefaultSceneElement) getParent() : null;
	}

	public void setTexture(Texture tex) {
		this.tex = tex;
		if (mat != null)
			mat.setTexture("ColorMap", tex);
	}

	public void setUseClip(boolean useClip) {
		this.useClip = useClip;
		resetClipState();
		update();
	}

	public void update() {
		if (mesh.updateCol)
			geom.updateModelBound();
	}
	
	public void setWidth(float w) {
		this.dimensions.setX(w);
		mesh.buildPosition = true;
	}

	protected QuadData createQuad(String quadKey, TextureRegion textureRegion, float x, float y, float regionWidth,
			float regionHeight, Vector2f origin) {
		return new QuadData(this, quadKey, textureRegion, x, y, regionWidth, regionHeight, origin);
	}

	protected void resetClipState() {
		boolean wantClip = wantsClip();
		if (wantClip) {
			mat.setVector4("Clipping", clippingPosition);
			mat.setBoolean("UseClipping", true);
		} else {
			mat.setVector4("Clipping", new Vector4f(0, 0, 0, 0));
			mat.setBoolean("UseClipping", false);
		}
	}

	protected boolean wantsClip() {
		return useClip && clippingPosition != null && !clippingPosition.equals(Vector4f.ZERO);
	}
}
