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

import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.ui.Picture;

import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.Element;

public class BusySpinner extends Element {

	public static final float DEFAULT_SPINNER_SPEED = 10f;
	
	private boolean hideWhenIdle = true;
	private final Spatial p;
	private float speed;
	private AbstractControl spin;

	public BusySpinner(BaseScreen screen) {
		this(screen, null);
	}

	public BusySpinner(BaseScreen screen, Size size) {
		super(screen, size);
		Element el = new Element(screen).addStyleClass("busy-spinner");
		Picture p = new Picture("busy");
		Material mat = el.getMaterial().clone();
		if (size == null) {
			size = new Size(el.calcPreferredSize());
		}
		setMinDimensions(size);
		setPreferredDimensions(size);
		setMaxDimensions(size);
		p.setMaterial(mat);
		p.setWidth(size.x);
		p.setHeight(size.y);
		this.p = p;
		attachChild(p);
	}

	public BusySpinner setSpeed(final float speed) {
		this.speed = speed;
		checkState();
		return this;
	}

	public float getSpeed() {
		return speed;
	}

	public boolean isHideWhenIdle() {
		return hideWhenIdle;
	}

	public void setHideWhenIdle(boolean hideWhenIdle) {
		this.hideWhenIdle = hideWhenIdle;
		checkState();
	}

	protected void checkState() {
		if (this.speed > 0 && spin == null) {
			if (hideWhenIdle)
				show();

			p.addControl(spin = new AbstractControl() {
				private float rot;

				@Override
				public Control cloneForSpatial(Spatial paramSpatial) {
					return null;
				}

				@Override
				protected void controlRender(RenderManager rm, ViewPort vp) {
				}

				@Override
				protected void controlUpdate(float tpf) {
					rot -= tpf * speed;
					if (rot < 0) {
						rot = FastMath.TWO_PI - tpf;
					}
					spatial.rotate(0, 0, tpf * speed);
					spatial.setLocalTranslation(0, 0, 0);
					spatial.rotate(0, 0, tpf * speed);
					spatial.center();
					spatial.move(getDimensions().x / 2f, getDimensions().y / 2f, 0);
				}
			});
		} else if (speed == 0 && spin != null) {
			if (hideWhenIdle)
				hide();
			p.removeControl(spin);
			spin = null;
		}
	}
}
