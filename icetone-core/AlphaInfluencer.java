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
package icetone.controls.extras.emitter;

import com.jme3.math.Vector2f;

import icetone.controls.extras.emitter.ElementEmitter.ElementParticle;
import icetone.framework.animation.Interpolation;

/**
 *
 * @author t0neg0d
 */
public class AlphaInfluencer extends InfluencerBase {
	private boolean isEnabled = true;
	private float startAlpha = 1.0f;
	private float endAlpha = 0.01f;
	private Interpolation interpolation = Interpolation.linear;
	private Vector2f tempV2a = new Vector2f();
	private Vector2f tempV2b = new Vector2f();
	
	public AlphaInfluencer(ElementEmitter emitter) {
		super(emitter);
	}
	
	@Override
	public void update(ElementParticle particle, float tpf) {
		if (isEnabled) {
			tempV2a.set(startAlpha, startAlpha);
			tempV2b.set(endAlpha, endAlpha);
			tempV2a.interpolate(tempV2b, interpolation.apply(particle.blend));
			
			particle.color.set(
				particle.color.r,
				particle.color.g,
				particle.color.b,
				tempV2a.x
			);
		}
	}

	@Override
	public void initialize(ElementParticle particle) {
		particle.color.set(
				particle.color.r,
				particle.color.g,
				particle.color.b,
				startAlpha
			);
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public void setStartAlpha(float startAlpha) {
		this.startAlpha = startAlpha;
	}
	
	public void setEndAlpha(float endAlpha) {
		this.endAlpha = endAlpha;
	}
	
	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
	}
	
	@Override
	public AlphaInfluencer clone() {
		AlphaInfluencer clone = new AlphaInfluencer(emitter);
		clone.setStartAlpha(startAlpha);
		clone.setEndAlpha(endAlpha);
		clone.setInterpolation(interpolation);
		clone.setIsEnabled(isEnabled);
		return clone;
	}
}
