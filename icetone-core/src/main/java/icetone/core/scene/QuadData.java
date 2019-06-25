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

import java.util.Map;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

/**
 *
 * @author t0neg0d
 */
public class QuadData implements SceneElement {
	public SceneElement element;
	public QuadData parent;
	public String key;
	private TextureRegion region;
	public int userIndex;
	public int index;
	private Vector2f position = new Vector2f(0f, 0f);
	private Vector2f initPosition = new Vector2f(0f, 0f);
	private float z = 1;
	private Vector2f dimensions = new Vector2f();
	private Vector2f initDimensions = new Vector2f();
	private Vector2f scale = new Vector2f();
	private float rotation = 0f;
	private Vector2f origin = new Vector2f(0f, 0f);
	private boolean visible = true;
	private Vector2f borders = new Vector2f(4f, 4f);
	private Vector2f tcOffset = new Vector2f();
	private ColorRGBA color = new ColorRGBA(1f, 1f, 1f, 1f);
	private Vector2f skew = new Vector2f(0f, 0f);

	public QuadData(SceneElement element, String quadKey, TextureRegion region, float x, float y, float width,
			float height, Vector2f origin) {
		this.element = element;
		this.key = quadKey;
		this.position.set(x, y);
		this.initPosition.set(x, y);
		this.dimensions.set(width, height);
		this.initDimensions.set(width, height);
		this.scale.set(1f, 1f);
		this.origin.set(origin);
		this.region = region;
	}

	public void setTextureRegion(TextureRegion region) {
		this.region = region;
		element.getMesh().buildTexCoords = true;
	}

	public TextureRegion getTextureRegion() {
		return this.region;
	}

	public void hide() {
		if (visible) {
			initDimensions.set(dimensions);
			dimensions.set(0f, 0f);
			element.getMesh().buildPosition = true;
			visible = false;
		}
	}

	public void show() {
		if (!visible) {
			dimensions.set(initDimensions);
			element.getMesh().buildPosition = true;
			visible = true;
		}
	}

	public boolean getIsVisible() {
		return visible;
	}

	@Override
	public ColorRGBA getColor() {
		return this.color;
	}

	@Override
	public float getColorR() {
		return this.color.r;
	}

	@Override
	public float getColorG() {
		return this.color.g;
	}

	@Override
	public float getColorB() {
		return this.color.b;
	}

	@Override
	public float getColorA() {
		return this.color.a;
	}

	@Override
	public Vector2f getTCOffset() {
		return this.tcOffset;
	}

	@Override
	public float getTCOffsetX() {
		return this.tcOffset.x;
	}

	@Override
	public float getTCOffsetY() {
		return this.tcOffset.y;
	}

	@Override
	public void setColor(ColorRGBA color) {
		this.color.set(color);
		element.getMesh().buildColor = true;
	}

	@Override
	public void setColorR(float r) {
		this.color.r = r;
		element.getMesh().buildColor = true;
	}

	@Override
	public void setColorG(float g) {
		this.color.g = g;
		element.getMesh().buildColor = true;
	}

	@Override
	public void setColorB(float b) {
		this.color.b = b;
		element.getMesh().buildColor = true;
	}

	@Override
	public void setColorA(float a) {
		this.color.a = a;
		element.getMesh().buildColor = true;
	}

	@Override
	public void setTCOffsetX(float x) {
		this.tcOffset.x = x;
		element.getMesh().buildTexCoords = true;
	}

	@Override
	public void setTCOffsetY(float y) {
		this.tcOffset.y = y;
		element.getMesh().buildTexCoords = true;
	}

	@Override
	public void setSkew(Vector2f skew) {
		this.skew.set(skew);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setSkew(float x, float y) {
		this.skew.set(x, y);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setSkewX(float x) {
		this.skew.setX(x);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setSkewY(float y) {
		this.skew.setY(y);
		element.getMesh().buildPosition = true;
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
	public void setPositionX(float x) {
		this.position.x = x;
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setPositionY(float y) {
		this.position.y = y;
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setPositionZ(float z) {
		this.z = z;
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setPosition(float x, float y) {
		this.position.set(x, y);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setPosition(Vector2f pos) {
		this.position.set(pos);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setRotation(float rotation) {
		this.rotation = rotation;
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setScaleX(float scaleX) {
		this.scale.x = scaleX;
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setScaleY(float scaleY) {
		this.scale.y = scaleY;
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setScale(float x, float y) {
		this.scale.set(x, y);
		element.getMesh().buildPosition = true;

	}

	@Override
	public void setScale(Vector2f scale) {
		this.scale.set(scale);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setOrigin(float x, float y) {
		this.origin.set(x, y);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setOrigin(Vector2f origin) {
		this.origin.set(origin);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setOriginX(float originX) {
		this.origin.x = originX;
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setOriginY(float originY) {
		this.origin.y = originY;
		element.getMesh().buildPosition = true;
	}

	@Override
	public Vector2f getPosition() {
		return position;
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
	public Vector2f getDimensions() {
		return this.dimensions;
	}

	@Override
	public float getWidth() {
		return this.dimensions.x;
	}

	@Override
	public float getHeight() {
		return this.dimensions.y;
	}

	@Override
	public void setDimensions(Vector2f dim) {
		this.dimensions.set(dim);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setDimensions(float w, float h) {
		this.dimensions.set(w, h);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setWidth(float w) {
		this.dimensions.setX(w);
		element.getMesh().buildPosition = true;
	}

	@Override
	public void setHeight(float h) {
		this.dimensions.setY(h);
		element.getMesh().buildPosition = true;
	}

	public void setBorders(Vector2f borders) {
		this.borders.set(borders);
	}

	public void setBorders(float x, float y) {
		this.borders.set(x, y);
	}

	public Vector2f getBorders() {
		return borders;
	}

	@Override
	public SceneMesh getMesh() {
		return element.getMesh();
	}

	@Override
	public Map<String, QuadData> getQuads() {
		return element.getQuads();
	}

	public void update(float tpf) {
	}

	@Override
	public SceneElement getSceneParent() {
		return parent;
	}

}
