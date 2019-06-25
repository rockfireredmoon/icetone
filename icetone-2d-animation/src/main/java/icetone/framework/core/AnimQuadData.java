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

import com.jme3.math.Vector2f;

import icetone.core.scene.QuadData;
import icetone.core.scene.TextureRegion;
import icetone.framework.animation.TemporalAction;

/**
 *
 * @author t0neg0d
 */
public class AnimQuadData extends QuadData implements Animatable {
	public List<TemporalAction> actions = new ArrayList<>();
	private boolean ignoreMouse = false;
	private boolean isMovable = false;

	public AnimQuadData(AnimElement element, String quadKey, TextureRegion region, float x, float y, float width,
			float height, Vector2f origin) {
		super(element, quadKey, region, x, y, width, height, origin);
	}

	@Override
	public void addAction(TemporalAction action) {
		action.setTransformable(this);
		actions.add(action);
	}

	public void update(float tpf) {
		for (TemporalAction a : actions) {
			a.act(tpf);
			if (a.getTime() >= a.getDuration() && a.getAutoRestart())
				a.restart();
		}

		for (TemporalAction a : actions) {
			if (a.getTime() >= a.getDuration()) {
				actions.remove(a);
				break;
			}
		}
	}

	@Override
	public void setIgnoreMouse(boolean ignoreMouse) {
		this.ignoreMouse = ignoreMouse;
	}

	@Override
	public void setIsMovable(boolean isMovable) {
		this.isMovable = isMovable;
	}

	@Override
	public boolean getIgnoreMouse() {
		return this.ignoreMouse;
	}

	@Override
	public boolean getIsMovable() {
		return this.isMovable;
	}

	@Override
	public boolean getContainsAction(TemporalAction action) {
		return actions.contains(action);
	}

	public Object dataStruct;

	public <T extends Object> void setDataStruct(T dataStruct) {
		this.dataStruct = dataStruct;
	}

	public <T extends Object> T getDataStruct() {
		return (T) dataStruct;
	}
}
