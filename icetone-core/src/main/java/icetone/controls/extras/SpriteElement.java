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

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

import icetone.core.ElementManager;
import icetone.core.Size;
import icetone.core.Element;

/**
 *
 * @author t0neg0d
 */
public class SpriteElement extends Element implements Control {

	private Spatial spatial;
	private boolean isEnabled = true;
	private boolean useInterval = true;
	private float framesPerSecond = 4;
	protected float trackInterval = (1 / framesPerSecond), currentTrack = 0;
	private Texture sprite;
	private int spriteCols, spriteRows;
	private float imgWidth, imgHeight, spriteWidth, spriteHeight;
	private int currentIndex = 0;
	private int[] frames;
	private int currentFramesIndex = 0;
	private List<Vector4f> sprites = new ArrayList<>();

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public SpriteElement(ElementManager<?> screen) {
		this(screen, null);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public SpriteElement(ElementManager<?> screen, Vector2f position) {
		this(screen, null, position);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SpriteElement(ElementManager<?> screen, Vector2f position, Size dimensions) {
		this(screen, null, position, dimensions);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param styleId
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public SpriteElement(ElementManager<?> screen, String styleId, Vector2f position) {
		this(screen, styleId, position, null);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param styleId
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public SpriteElement(ElementManager<?> screen, String styleId, Vector2f position, Size dimensions) {
		super(screen, styleId, position, dimensions);
	}

	public void setSprite(String imgPath, int rows, int cols, float framesPerSecond) {
		sprite = screen.getApplication().getAssetManager().loadTexture(imgPath);
		sprite.setMagFilter(Texture.MagFilter.Bilinear);
		sprite.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		sprite.setWrap(Texture.WrapMode.Repeat);

		setSprite(sprite, rows, cols, framesPerSecond);
	}

	public void setSprite(Texture sprite, int rows, int cols, float framesPerSecond) {
		this.sprite = sprite;

		this.spriteRows = rows;
		this.spriteCols = cols;

		Image img = sprite.getImage();
		imgWidth = img.getWidth();
		imgHeight = img.getHeight();

		spriteWidth = imgWidth / cols;
		spriteHeight = imgHeight / rows;

		sprites.clear();
		for (int y = rows - 1; y > -1; y--) {
			for (int x = 0; x < cols; x++) {
				sprites.add(new Vector4f(x * spriteWidth, y * spriteHeight, spriteWidth, spriteHeight));
			}
		}

		setAtlas(sprites.get(currentIndex));
		this.setTexture(sprite);

		this.useInterval = true;
		this.framesPerSecond = framesPerSecond;
		this.trackInterval = 1 / framesPerSecond;

		setEnabled(true);
	}

	public void Elemment(boolean isEnabled) {
		super.setEnabled(isEnabled);
		if (isEnabled)
			this.addControl(this);
		else
			this.removeControl(this);
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	public int getSpriteRowCount() {
		return this.spriteRows;
	}

	public int getSpriteColCount() {
		return this.spriteCols;
	}

	public float getSpriteWidth() {
		return this.spriteWidth;
	}

	public float getSpriteHeight() {
		return this.spriteHeight;
	}

	public void setFrames(int[] frames) {
		this.frames = frames;
		if (frames != null)
			setAtlas(sprites.get(frames[currentFramesIndex]));
		else
			setAtlas(sprites.get(currentIndex));
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public void update(float tpf) {
		if (isEnabled) {
			if (useInterval) {
				currentTrack += tpf;
				if (currentTrack >= trackInterval) {
					updateSprite();
					currentTrack -= trackInterval;
				}
			}
		}
	}

	private void updateSprite() {
		if (frames == null) {
			currentIndex++;
			if (currentIndex == sprites.size())
				currentIndex = 0;
			setAtlas(sprites.get(currentIndex));
		} else {
			currentFramesIndex++;
			if (currentFramesIndex == frames.length)
				currentFramesIndex = 0;
			setAtlas(sprites.get(frames[currentFramesIndex]));
		}
		updateSpriteHook();
	}

	public void updateSpriteHook() {
	}

	public void setCurrentFrame(int row, int col) {
		int frameIndex = spriteRows * row;
		frameIndex += col;
		setCurrentFrame(frameIndex);
	}

	public void setCurrentFrame(int frameIndex) {
		if (frameIndex >= 0 && frameIndex < sprites.size()) {
			setAtlas(sprites.get(frameIndex));
		}
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {

	}

}
