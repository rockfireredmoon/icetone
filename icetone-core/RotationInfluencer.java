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
public class RotationInfluencer extends InfluencerBase {
	private boolean isEnabled = true;
	private float maxRotationSpeed;
	private boolean useRandomStartAngle = true;
	private boolean useFixedRotationSpeed = false;
	private boolean rotateFromEmitterPosition = false;
	private boolean rotateToVelocity = false;
	private Vector2f tempV2a = new Vector2f();
	private Vector2f tempV2b = new Vector2f();
	
	public RotationInfluencer(ElementEmitter emitter) {
		super(emitter);
	}
	
	@Override
	public void update(ElementParticle particle, float tpf) {
		if (isEnabled) {
			if (rotateFromEmitterPosition) {
				tempV2a.set(
					emitter.getPositionX(),
					emitter.getPositionY()
				);
				tempV2b.set(particle.position);
				particle.angle = getRotationBetween(
					tempV2a, tempV2b
				)+90;
			} else if (rotateToVelocity) {
				particle.angle = getRotationFromVelocity(particle.velocity);
			} else {
				if (particle.rotateDir)
					particle.angle += particle.rotateSpeed * tpf * FastMath.RAD_TO_DEG;
				else
					particle.angle -= particle.rotateSpeed * tpf * FastMath.RAD_TO_DEG;
			}
		}
	}
	
	@Override
	public void initialize(ElementParticle particle) {
		if (useRandomStartAngle)
			particle.angle = FastMath.rand.nextFloat()*360;
		else
			particle.angle = 0;
		if (!useFixedRotationSpeed)
			particle.rotateSpeed = FastMath.rand.nextFloat()*maxRotationSpeed;
		else
			particle.rotateSpeed = maxRotationSpeed;
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public void setRotateFromEmitterPosition(boolean rotateFromEmitterPosition) {
		this.rotateFromEmitterPosition = rotateFromEmitterPosition;
		if (this.rotateToVelocity) this.rotateToVelocity = false;
	}
	
	public void setRotateToVelocity(boolean rotateToVelocity) {
		this.rotateToVelocity = rotateToVelocity;
		if (this.rotateFromEmitterPosition) this.rotateFromEmitterPosition = false;
	}
	
	public void setUseFixedRotationSpeed(boolean useFixedRotationSpeed) {
		this.useFixedRotationSpeed = useFixedRotationSpeed;
	}
	
	public boolean getUseFixedRotationSpeed() {
		return this.useFixedRotationSpeed;
	}
	
	public void setMaxRotationSpeed(float maxRotationSpeed) {
		this.maxRotationSpeed = maxRotationSpeed;
	}
	
	public float getMaxRotationSpeed() { return maxRotationSpeed; }
	
	public void setUseRandomStartAngle(boolean useRandomStartAngle) {
		this.useRandomStartAngle = useRandomStartAngle;
	}
	
	public boolean getUseRandomStartAngle() { return useRandomStartAngle; }
	
	private float getRotationBetween(Vector2f v1, Vector2f v2) {
		float deltaY = v2.y - v1.y;
		float deltaX = v2.x - v1.x;
		
		return FastMath.atan2(deltaY,deltaX) * FastMath.RAD_TO_DEG;
	}
	
	private float getRotationFromVelocity(Vector2f velocity) {
		tempV2a.set(velocity).normalizeLocal();
		float angle = FastMath.atan2(tempV2a.y, tempV2a.x)*FastMath.RAD_TO_DEG;
		angle += 90;
		return angle;
		
	}
	
	@Override
	public RotationInfluencer clone() {
		RotationInfluencer clone = new RotationInfluencer(emitter);
		clone.setMaxRotationSpeed(maxRotationSpeed);
		clone.setRotateFromEmitterPosition(rotateFromEmitterPosition);
		clone.setRotateToVelocity(rotateToVelocity);
		clone.setUseFixedRotationSpeed(useFixedRotationSpeed);
		clone.setUseRandomStartAngle(useRandomStartAngle);
		clone.setIsEnabled(isEnabled);
		return clone;
	}
}
