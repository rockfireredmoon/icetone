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

package icetone.extras.controls;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;

import icetone.controls.buttons.PushButton;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.event.ElementEvent.Type;
import icetone.core.Element;

/**
 *
 * @author t0neg0d
 */
public abstract class Joystick extends Element implements Control {
	private PushButton thumb;
	private Vector2f origin = new Vector2f();
	private Vector2f centerVec = new Vector2f();
	private float maxDistance;
	private float deltaX, deltaY;
	private Vector2f tempV2 = new Vector2f();

	public Joystick(BaseScreen screen, Vector2f position, int size) {
		super(screen, position, new Size(size, size));
		setMovable(false);
		setResizable(false);

		maxDistance = getDimensions().x / 2;

		Texture texBG = screen.getApplication().getAssetManager()
				.loadTexture("icetone/style/atlasdef/android/joystick_bg.png");
		setTexture(texBG);
		setAtlas(new Vector4f(0, 0, 128, 128));

		tempV2.set(getWidth() / 2, getHeight() / 2);

		thumb = new PushButton(screen);
		thumb.onElementEvent(evt -> {
			if (evt.getSource().getPixelPosition().distance(origin) > maxDistance)
				evt.getSource().setPosition(evt.getSource().getPixelPosition().subtract(centerVec).normalize()
						.mult(maxDistance).add(centerVec));
			deltaX = (getPixelPosition().x - centerVec.x);
			deltaX /= maxDistance;
			deltaY = (getPixelPosition().y - centerVec.x);
			deltaY /= maxDistance;
		}, Type.MOVED);
		thumb.setPosition(new Vector2f(getWidth() / 2 - (tempV2.x / 2), getHeight() / 2 - (tempV2.y / 2)));
		thumb.setPreferredDimensions(new Size(tempV2));
		thumb.onMouseReleased(evt -> {
			setPosition(origin);
			deltaX = (getPixelPosition().x - centerVec.x);
			deltaX /= maxDistance;
			deltaY = (getPixelPosition().y - centerVec.x);
			deltaY /= maxDistance;
		});
		thumb.setMovable(true);

		origin.set(thumb.getPixelPosition());

		Texture texThumb = screen.createNewTexture("icetone/style/atlasdef/android/joystick_thumb.png");
		thumb.setTexture(texThumb);
		thumb.setAtlas(new Vector4f(0, 0, 32, 32));

		addElement(thumb);

		float dist = (size / 2);
		dist -= tempV2.x / 2;
		centerVec.set(dist, dist);

		addControl(this);
	}

	public PushButton getThumb() {
		return this.thumb;
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void update(float tpf) {
		onUpdate(tpf, deltaX, deltaY);
	}

	public abstract void onUpdate(float tpf, float deltaX, float deltaY);

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	}
}
