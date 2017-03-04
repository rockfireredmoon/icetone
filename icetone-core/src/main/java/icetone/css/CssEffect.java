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

import org.w3c.dom.css.CSSPrimitiveValue;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Texture;

import icetone.core.BaseElement;
import icetone.effects.BatchEffect;
import icetone.effects.BlinkEffect;
import icetone.effects.ColorSwapEffect;
import icetone.effects.ConcertinaInEffect;
import icetone.effects.ConcertinaOutEffect;
import icetone.effects.DesaturateEffect;
import icetone.effects.Effect;
import icetone.effects.Effect.EffectDirection;
import icetone.effects.EffectFactory;
import icetone.effects.EffectQueue;
import icetone.effects.FadeInEffect;
import icetone.effects.FadeOutEffect;
import icetone.effects.IEffect;
import icetone.effects.ImageFadeInEffect;
import icetone.effects.ImageFadeOutEffect;
import icetone.effects.ImageSwapEffect;
import icetone.effects.PulseColorEffect;
import icetone.effects.PulseEffect;
import icetone.effects.SaturateEffect;
import icetone.effects.SlideFromEffect;
import icetone.effects.SlideInEffect;
import icetone.effects.SlideOutEffect;
import icetone.effects.SlideToEffect;
import icetone.effects.SpinEffect;
import icetone.effects.SpinInEffect;
import icetone.effects.SpinOutEffect;
import icetone.effects.ZoomInEffect;
import icetone.effects.ZoomOutEffect;
import icetone.framework.animation.Interpolation;

public class CssEffect implements EffectFactory {

	private float duration;
	private List<String> effects = new LinkedList<>();
	private float delay;
	private EffectDirection direction = EffectDirection.Top;
	private String imageUri;
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

	public String getImageUri() {
		return imageUri;
	}

	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
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
		if (effects.size() == 1)
			return configureEffect(el, createEffect(el, effects.get(0)));
		else {
			BatchEffect fx = new BatchEffect();
			for (String n : effects) {
				fx.addEffect(configureEffect(el, createEffect(el, n)));
			}
			return fx;
		}
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

	private Effect createEffect(BaseElement el, String n) {
		switch (n.trim()) {
		case "ColorSwap":
			if (blendColor == null)
				throw new IllegalArgumentException("Color required for color swap effect.");
			return new ColorSwapEffect(duration, blendColor);
		case "Desaturate":
			return new DesaturateEffect(duration);
		case "FadeIn":
			return new FadeInEffect(duration);
		case "FadeOut":
			return new FadeOutEffect(duration);
		case "Blink":
			return new BlinkEffect(duration);
		case "ImageFadeIn":
			if (imageUri == null)
				throw new IllegalArgumentException("Image required for image fade in effect.");
			return new ImageFadeInEffect(duration, createImage(el));
		case "ImageFadeOut":
			if (imageUri == null)
				throw new IllegalArgumentException("Image required for image fade out effect.");
			return new ImageFadeOutEffect(duration, createImage(el));
		case "ImageSwap":
			if (imageUri == null)
				throw new IllegalArgumentException("Image required for image swap effect.");
			return new ImageSwapEffect(duration, createImage(el));
		case "Saturate":
			return new SaturateEffect(duration);
		case "SlideIn":
			if (direction == null)
				throw new IllegalArgumentException("Direction required for slide in effect.");
			return new SlideInEffect(duration, direction);
		case "SlideOut":
			return new SlideOutEffect(duration, direction);
		case "Spin":
			return new SpinEffect(duration);
		case "SpinIn":
			return new SpinInEffect(duration);
		case "SpinOut":
			return new SpinOutEffect(duration);
		case "ZoomIn":
			return new ZoomInEffect(duration);
		case "ZoomOut":
			return new ZoomOutEffect(duration);
		case "ConcertinaIn":
			return new ConcertinaInEffect(duration, direction);
		case "ConcertinaOut":
			return new ConcertinaOutEffect(duration, direction);
		case "SlideTo":
			return new SlideToEffect(duration, getActualDestination(el));
		case "SlideFrom":
			return new SlideFromEffect(duration, getActualDestination(el));
		case "PulseColor":
			if (blendColor == null)
				throw new IllegalArgumentException("Color required for pulse color effect.");
			return new PulseColorEffect(duration, blendColor);
		case "Pulse":
			if (imageUri == null)
				throw new IllegalArgumentException("Image required for pulse effect.");
			return new PulseEffect(duration, createImage(el));
		}
		throw new IllegalArgumentException("Invalid effect " + n);
	}

	protected Vector2f getActualDestination(BaseElement el) {
		if (destination == null)
			throw new IllegalArgumentException("Destination required for slide effect.");
		Vector2f adest = new Vector2f(destination);
		if (destinationUnits.x == CSSPrimitiveValue.CSS_PERCENTAGE)
			adest.x = (el.getParentContainer().getWidth() / 100f) * destination.x;
		if (destinationUnits.y == CSSPrimitiveValue.CSS_PERCENTAGE)
			adest.y = (el.getParentContainer().getHeight() / 100f) * destination.y;
		return adest;
	}

	protected Texture createImage(BaseElement el) {
		Texture color = el.getScreen().getApplication().getAssetManager().loadTexture(imageUri);
		color.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		color.setMagFilter(Texture.MagFilter.Nearest);
		return color;
	}
}
