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

package icetone.controls.extras.android;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public abstract class Joystick extends Element implements Control {
	private ButtonAdapter thumb;
	private Vector2f origin = new Vector2f();
	private boolean springback = true;
	private Vector2f centerVec = new Vector2f();
	private float maxDistance;
	private float deltaX, deltaY;
	private Spatial spatial;
	private Vector2f tempV2 = new Vector2f();
	
	public Joystick(ElementManager screen, Vector2f position, int size) {
		super(screen, UIDUtil.getUID(),
			position, new Vector2f(size, size), new Vector4f(0,0,0,0),
			screen.getStyle("Common").getString("blankImg")
		);
		setIsMovable(false);
		setIsResizable(false);
		setScaleEW(false);
		setScaleNS(false);
		setDocking(Docking.SW);
		
		maxDistance = getDimensions().x/2;
		
		Texture texBG = screen.createNewTexture("icetone/style/atlasdef/android/joystick_bg.png");
		setTextureAtlasImage(texBG, "x=0|y=0|w=128|h=128");
		
		tempV2.set(getWidth()/2,getHeight()/2);
		
		thumb = new ButtonAdapter(screen, UIDUtil.getUID(),
			new Vector2f(getWidth()/2-(tempV2.x/2), getHeight()/2-(tempV2.y/2)),
			tempV2,
			new Vector4f(0,0,0,0),
			screen.getStyle("Common").getString("blankImg")
		) {
			@Override
			public void controlMoveHook() {
				if (getPosition().distance(origin) > maxDistance)
					setPosition(getPosition().subtract(centerVec).normalize().mult(maxDistance).add(centerVec));
				deltaX = (getPosition().x-centerVec.x);
				deltaX /= maxDistance;
				deltaY = (getPosition().y-centerVec.x);
				deltaY /= maxDistance;
			}
			
			@Override
			public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean isToggled) {
				
			}
			
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
				setPosition(origin);
				deltaX = (getPosition().x-centerVec.x);
				deltaX /= maxDistance;
				deltaY = (getPosition().y-centerVec.x);
				deltaY /= maxDistance;
			}
		};
		thumb.setDockS(true);
		thumb.setDockW(true);
		thumb.setIsMovable(true);
		thumb.clearAltImages();
		thumb.removeEffect(Effect.EffectEvent.Hover);
		thumb.removeEffect(Effect.EffectEvent.Press);
		thumb.removeEffect(Effect.EffectEvent.GetFocus);
		thumb.removeEffect(Effect.EffectEvent.LoseFocus);
		
		origin.set(thumb.getPosition());
		
		Texture texThumb = screen.createNewTexture("icetone/style/atlasdef/android/joystick_thumb.png");
		thumb.setTextureAtlasImage(texThumb, "x=0|y=0|w=32|h=32");
		
		addChild(thumb);
		
		float dist = (size/2);
		dist -= tempV2.x/2;
		centerVec.set(dist,dist);
		
		addControl(this);
	}

	public ButtonAdapter getThumb() { return this.thumb; }
	
	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
	}

	@Override
	public void update(float tpf) {
		onUpdate(tpf, deltaX, deltaY);
	}
	
	public abstract void onUpdate(float tpf, float deltaX, float deltaY);
	
	@Override
	public void render(RenderManager rm, ViewPort vp) {  }
}
