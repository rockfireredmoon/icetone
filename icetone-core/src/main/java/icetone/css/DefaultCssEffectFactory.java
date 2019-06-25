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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.BaseElement;
import icetone.effects.BlinkEffect;
import icetone.effects.ColorSwapEffect;
import icetone.effects.ConcertinaInEffect;
import icetone.effects.ConcertinaOutEffect;
import icetone.effects.DesaturateEffect;
import icetone.effects.Effect;
import icetone.effects.Effect.EffectDirection;
import icetone.effects.FadeInEffect;
import icetone.effects.FadeOutEffect;
import icetone.effects.ImageFadeInEffect;
import icetone.effects.ImageFadeOutEffect;
import icetone.effects.ImageSwapEffect;
import icetone.effects.PulseColorEffect;
import icetone.effects.PulseEffect;
import icetone.effects.RotateToEffect;
import icetone.effects.SaturateEffect;
import icetone.effects.ScrollToEffect;
import icetone.effects.SlideFromEffect;
import icetone.effects.SlideInEffect;
import icetone.effects.SlideOutEffect;
import icetone.effects.SlideToEffect;
import icetone.effects.SpinEffect;
import icetone.effects.SpinInEffect;
import icetone.effects.SpinOutEffect;
import icetone.effects.ZoomInEffect;
import icetone.effects.ZoomOutEffect;

public class DefaultCssEffectFactory extends AbstractCssEffectFactory {

	@Override
	public Effect createEffect(CssEffect effect, BaseElement el, String name) {
		float duration = effect.getDuration();
		String imageUri = effect.getUri();
		ColorRGBA blendColor = effect.getBlendColor();
		EffectDirection direction = effect.getDirection();
		switch (name.trim()) {
		case "ColorSwap":
			if (effect.getBlendColor() == null)
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
			return new ImageFadeInEffect(duration, createImage(effect, el));
		case "ImageFadeOut":
			if (imageUri == null)
				throw new IllegalArgumentException("Image required for image fade out effect.");
			return new ImageFadeOutEffect(duration, createImage(effect, el));
		case "ImageSwap":
			if (imageUri == null)
				throw new IllegalArgumentException("Image required for image swap effect.");
			return new ImageSwapEffect(duration, createImage(effect, el));
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
			return new SlideToEffect(duration, getActualDestination(effect, el));
		case "SlideFrom":
			return new SlideFromEffect(duration, getActualDestination(effect, el));
		case "ScrollTo":
			return new ScrollToEffect(duration);
		case "RotateTo":
			Vector2f dst = getActualDestination(effect, el);
			return new RotateToEffect(duration, dst == null ? 0 : dst.x);
		case "PulseColor":
			if (blendColor == null)
				throw new IllegalArgumentException("Color required for pulse color effect.");
			return new PulseColorEffect(duration, blendColor);
		case "Pulse":
			if (imageUri == null)
				throw new IllegalArgumentException("Image required for pulse effect.");
			return new PulseEffect(duration, createImage(effect, el));
		}
		return null;
	}
}
