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
package icetone.css;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.w3c.dom.css.CSSPrimitiveValue;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.BaseElement;
import icetone.effects.BatchEffect;
import icetone.effects.Effect;
import icetone.effects.Effect.EffectDirection;
import icetone.effects.EffectFactory;
import icetone.effects.EffectQueue;
import icetone.effects.IEffect;
import icetone.effects.Interpolation;

public class CssEffect implements EffectFactory {

	private float duration;
	private List<String> effects = new LinkedList<>();
	private float delay;
	private EffectDirection direction = EffectDirection.Top;
	private String uri;
	private Interpolation interpolation;
	private int iterations;
	private ColorRGBA blendColor;
	private Vector2f destination;
	private boolean reverse;
	private Vector2f destinationUnits = new Vector2f(CSSPrimitiveValue.CSS_PX, CSSPrimitiveValue.CSS_PX);

	public ColorRGBA getBlendColor() {
		return blendColor;
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public void setBlendColor(ColorRGBA blendColor) {
		this.blendColor = blendColor;
	}

	public Vector2f getDestinationUnits() {
		return destinationUnits;
	}

	public void setDestinationUnits(Vector2f destinationUnits) {
		this.destinationUnits = destinationUnits;
	}

	public Vector2f getDestination() {
		return destination;
	}

	public void setDestination(Vector2f destination) {
		this.destination = destination;
	}

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public float getDuration() {
		return duration;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public EffectDirection getDirection() {
		return direction;
	}

	public void setDirection(EffectDirection direction) {
		this.direction = direction;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Interpolation getInterpolation() {
		return interpolation;
	}

	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}

	public List<String> getEffects() {
		return effects;
	}

	@Override
	public IEffect createEffect(BaseElement el) {
		IEffect buildEffect = buildEffect(el);
		if (delay > 0) {
			EffectQueue q = new EffectQueue();
			q.addEffect(buildEffect, delay);
			buildEffect = q;
		}
		return buildEffect;
	}

	protected IEffect buildEffect(BaseElement el) {
		for (CssEffectFactory f : ServiceLoader.load(CssEffectFactory.class)) {
			if (effects.size() == 1) {
				Effect effect = f.createEffect(this, el, effects.get(0));
				if (effect != null)
					return configureEffect(el, effect);
			} else {
				BatchEffect fx = new BatchEffect();
				for (String n : effects) {
					Effect effect = f.createEffect(this, el, n);
					if (effect != null)
						fx.addEffect(configureEffect(el, effect));
				}
				return fx;
			}
		}
		throw new IllegalArgumentException("Invalid effects " + effects);

	}

	protected IEffect configureEffect(BaseElement el, Effect effect) {

		if (interpolation != null)
			effect.setInterpolation(interpolation);

		if (el.isDestroyOnHide())
			effect.setDestroyOnHide(true);

		effect.setReverse(reverse);
		effect.setIterations(iterations < 0 ? Integer.MAX_VALUE : iterations);

		return effect;
	}
}
