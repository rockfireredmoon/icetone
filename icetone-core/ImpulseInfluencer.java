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

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;

import icetone.controls.extras.emitter.ElementEmitter.ElementParticle;

/**
 *
 * @author t0neg0d
 */
public class ImpulseInfluencer extends InfluencerBase {
	private boolean isEnabled = true;
	private Vector2f temp = new Vector2f();
	private Vector2f temp2 = new Vector2f();
	private float variationStrength = 0.35f;

	public ImpulseInfluencer(ElementEmitter emitter) {
		super(emitter);
	}

	@Override
	public void update(ElementParticle particle, float tpf) {
		if (isEnabled) {
			float incX = FastMath.nextRandomFloat();
			if (FastMath.rand.nextBoolean())
				incX = -incX;
			float incY = FastMath.nextRandomFloat();
			if (FastMath.rand.nextBoolean())
				incY = -incY;
			temp.set(particle.velocity).addLocal(incX, incY);
			particle.velocity.interpolate(temp, (variationStrength));
		}
	}

	@Override
	public void initialize(ElementParticle particle) {

	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}

	public void setVariationStrength(float variationStrength) {
		this.variationStrength = variationStrength;
	}

	@Override
	public ImpulseInfluencer clone() {
		ImpulseInfluencer clone = new ImpulseInfluencer(emitter);
		clone.setVariationStrength(variationStrength);
		clone.setIsEnabled(isEnabled);
		return clone;
	}
}
