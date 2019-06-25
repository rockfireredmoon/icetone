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
package icetone.effects;

import com.jme3.math.Vector2f;

import icetone.core.BaseElement;
import icetone.core.ElementContainer;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public abstract class Effect extends AbstractEffect implements Cloneable {

	public enum EffectDirection {
		Top, Bottom, Left, Right
	}

	protected float pass = 0.0f;
	protected float time = 0.0f;
	protected boolean localActive = true;
	protected boolean init = false;
	protected boolean destroyOnHide = false;
	protected Vector2f def = new Vector2f();
	private boolean callHide = false;
	protected Interpolation interpolation = Interpolation.linear;
	protected float duration;
	protected boolean reset = true;
	protected boolean reverse;
	private int iterations;

	public Effect() {
		this(0);
	}

	public Effect(float duration) {
		this.duration = duration;
	}

	public boolean isReset() {
		return reset;
	}

	public Effect setReset(boolean reset) {
		this.reset = reset;
		return this;
	}

	public float getDuration() {
		return duration;
	}

	public Effect setDuration(float duration) {
		this.duration = duration;
		return this;
	}

	public boolean getIsDestroyOnHide() {
		return this.destroyOnHide;
	}

	@Override
	protected void onDeactivate() {
		disableShaderEffect();
	}

	public Effect setCallHide(boolean callHide) {
		this.callHide = callHide;
		return this;
	}

	public void setDestroyOnHide(boolean destroyOnHide) {
		this.destroyOnHide = destroyOnHide;
	}

	public Effect setInterpolation(Interpolation interpolation) {
		this.interpolation = null;
		this.interpolation = interpolation;
		return this;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int getIterations() {
		return iterations;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public Interpolation getInterpolation() {
		return this.interpolation;
	}

	protected void updatePass(float tpf) {
		time += tpf;
		pass = interpolation.apply(time / duration);
		if (pass >= 1.0) {
			if (iterations < 1) {
				pass = 1.0f;
				setIsActive(false);
			} else {
				if (iterations != Integer.MAX_VALUE)
					iterations--;
				time = 0;
				pass = 0;
			}
		}
	}

	private void destroyElement() {
		ElementContainer<?, ?> container = element.getParentContainer();
		if (container != null)
			container.removeElement(element);
		for (BaseElement el : element.getElements()) {
			el.childHide();
		}
	}

	public void resetShader() {
		element.getMaterial().setBoolean("UseEffect", false);
		element.getMaterial().setBoolean("EffectFade", false);
		element.getMaterial().setBoolean("EffectPulse", false);
		element.getMaterial().setBoolean("EffectSaturate", false);
		element.getMaterial().setTexture("EffectMap", null);
		element.getMaterial().setFloat("EffectStep", 0.0f);
	}

	@Override
	public Effect clone() {
		throw new UnsupportedOperationException();
	}

	protected void disableShaderEffect() {
		if (element != null) {
			element.getMaterial().setBoolean("UseEffect", false);
			element.getMaterial().setBoolean("EffectFade", false);
			element.getMaterial().setBoolean("EffectPulse", false);
			element.getMaterial().setBoolean("EffectPulseColor", false);
			element.getMaterial().setBoolean("EffectSaturate", false);
			element.getMaterial().setBoolean("EffectImageSwap", false);
		}
		// element.getElementMaterial().setBoolean("UseEffectTexCoords", false);
	}

	protected void destroyOrHide() {
		if (!destroyOnHide) {
			if (callHide) {
				if (element.isVisible())
					element.hide();
				else if (element.getParent() != null) {
					for (BaseElement el : element.getElements()) {
						el.childHide();
					}
					element.detachFromParent();
				}
			}
			// element.setPosition(def);
			element.setLocalScale(1);
			element.setLocalRotation(element.getLocalRotation().fromAngles(0, 0, 0));
		} else if(callHide) {
			destroyElement();
		}
		isActive = false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [isActive=" + isActive + ", iterations=" + iterations + ", element="
				+ element + ", channel=" + channel + ", getEffects()=" + getEffects() + "]";
	}
}
