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

import icetone.controls.extras.emitter.ElementEmitter.ElementParticle;
import icetone.framework.core.TextureRegion;

/**
 *
 * @author t0neg0d
 */
public class SpriteInfluencer extends InfluencerBase {
	public static enum AnimOrder {
		SequentialAll, SequentialAllOverLife, SequentialDefinedOrder, SequentialDefinedOrderOverLife, RandomAll, RandomAllOverLife, RandomDefinedOrder, RandomDefinedOrderOverLife, SingleImage
	}

	private boolean isEnabled = true;
	private boolean randomStartImage = false;
	private AnimOrder animOrder = AnimOrder.SingleImage;
	private boolean spriteOrderSet = false;
	private int[] spriteOrder = new int[] { 0 };
	private int currentIndex = 0;
	private float targetInterval = 1f;
	private float currentInterval = 0f;
	private float fps = 4f;

	public SpriteInfluencer(ElementEmitter emitter) {
		super(emitter);
	}

	@Override
	public void update(ElementParticle particle, float tpf) {
		if (animOrder != AnimOrder.SingleImage) {
			currentIndex = (Integer) particle.getData("currentIndex");
			currentInterval = (Float) particle.getData("currentInterval");
			targetInterval = (Float) particle.getData("targetInterval");
			currentInterval += tpf;
			if (currentInterval >= targetInterval) {
				switch (animOrder) {
				case SequentialAll:
				case SequentialAllOverLife:
					currentIndex++;
					if (currentIndex == emitter.particles.getTextureRegions().size())
						currentIndex = 0;
					particle.putData("currentIndex", currentIndex);
					particle.particle.setTextureRegion(particle.particle.element.getTextureRegion("sprite" + currentIndex));
					break;
				case SequentialDefinedOrder:
				case SequentialDefinedOrderOverLife:
					currentIndex++;
					if (currentIndex == spriteOrder.length)
						currentIndex = 0;
					particle.putData("currentIndex", currentIndex);
					particle.particle
							.setTextureRegion(particle.particle.element.getTextureRegion("sprite" + spriteOrder[currentIndex]));
					break;
				case RandomAll:
				case RandomAllOverLife:
					particle.particle.setTextureRegion(getRandomSprite(particle));
					break;
				case RandomDefinedOrder:
				case RandomDefinedOrderOverLife:
					particle.particle.setTextureRegion(getRandomSpriteFromRange(particle));
					break;
				}
				currentInterval -= targetInterval;
			}
			// if (animOrder == AnimOrder.SequentialAllOverLife)
			// if (particle.particle.userIndex == 0)
			// System.out.println(currentIndex);
			particle.putData("currentInterval", currentInterval);
		}
	}

	@Override
	public void initialize(ElementParticle particle) {
		particle.putData("currentInterval", 0f);
		targetInterval = 1f / emitter.getSpritesPerSecond();
		switch (animOrder) {
		case SequentialAll:
		case SequentialAllOverLife:
			if (animOrder == AnimOrder.SequentialAllOverLife)
				targetInterval = particle.life / emitter.particles.getTextureRegions().size();
			else
				targetInterval = 1f / fps;
			particle.putData("targetInterval", targetInterval);
			if (!randomStartImage) {
				currentIndex = 0;
				particle.putData("currentIndex", currentIndex);
				particle.particle.setTextureRegion(particle.particle.element.getTextureRegion("sprite" + currentIndex));
			} else {
				particle.particle.setTextureRegion(getRandomSprite(particle));
			}
			break;
		case SequentialDefinedOrder:
		case SequentialDefinedOrderOverLife:
			if (animOrder == AnimOrder.SequentialDefinedOrderOverLife)
				targetInterval = particle.life / spriteOrder.length;
			else
				targetInterval = 1f / fps;
			particle.putData("targetInterval", targetInterval);
			if (!randomStartImage) {
				currentIndex = 0;
				particle.putData("currentIndex", currentIndex);
				particle.particle
						.setTextureRegion(particle.particle.element.getTextureRegion("sprite" + spriteOrder[currentIndex]));
			} else {
				particle.particle.setTextureRegion(getRandomSpriteFromRange(particle));
			}
			break;
		case RandomAll:
		case RandomAllOverLife:
			if (animOrder == AnimOrder.RandomAllOverLife)
				targetInterval = particle.life / spriteOrder.length;
			else
				targetInterval = 1f / fps;
			particle.putData("targetInterval", targetInterval);
			particle.particle.setTextureRegion(getRandomSprite(particle));
			break;
		case RandomDefinedOrder:
		case RandomDefinedOrderOverLife:
			if (animOrder == AnimOrder.RandomAllOverLife)
				targetInterval = particle.life / spriteOrder.length;
			else
				targetInterval = 1f / fps;
			particle.putData("targetInterval", targetInterval);
			particle.particle.setTextureRegion(getRandomSpriteFromRange(particle));
			break;
		case SingleImage:
			targetInterval = 1;
			particle.putData("targetInterval", targetInterval);
			if (!randomStartImage) {
				currentIndex = 0;
				particle.putData("currentIndex", currentIndex);
				particle.particle
						.setTextureRegion(particle.particle.element.getTextureRegion("sprite" + spriteOrder[currentIndex]));
			} else {
				if (spriteOrderSet)
					particle.particle.setTextureRegion(getRandomSpriteFromRange(particle));
				else
					particle.particle.setTextureRegion(getRandomSprite(particle));
			}
			break;
		}
	}

	@Override
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public boolean getIsEnabled() {
		return this.isEnabled;
	}

	private TextureRegion getRandomSprite(ElementParticle particle) {
		currentIndex = FastMath.nextRandomInt(0, emitter.particles.getTextureRegions().size() - 1);
		particle.putData("currentIndex", currentIndex);
		return emitter.particles.getTextureRegion("sprite" + currentIndex);
	}

	private TextureRegion getRandomSpriteFromRange(ElementParticle particle) {
		currentIndex = FastMath.nextRandomInt(0, spriteOrder.length - 1);
		particle.putData("currentIndex", currentIndex);
		return emitter.particles.getTextureRegion("sprite" + spriteOrder[currentIndex]);
	}

	public boolean getRandomStartImage() {
		return randomStartImage;
	}

	public void setRandomStartImage(boolean randomStartImage) {
		this.randomStartImage = randomStartImage;
	}

	public AnimOrder getAnimOrder() {
		return animOrder;
	}

	public void setAnimOrder(AnimOrder animOrder) {
		this.animOrder = animOrder;
	}

	public int[] getSpriteOrder() {
		return spriteOrder;
	}

	public void setSpriteOrder(int[] spriteOrder) {
		this.spriteOrder = spriteOrder;
		this.spriteOrderSet = true;
	}

	public void setTargetFPS(int fps) {
		this.fps = fps;
	}

	@Override
	public SpriteInfluencer clone() {
		SpriteInfluencer clone = new SpriteInfluencer(emitter);
		clone.setAnimOrder(animOrder);
		clone.setRandomStartImage(randomStartImage);
		clone.setSpriteOrder(spriteOrder);
		clone.setTargetFPS((int) fps);
		clone.setIsEnabled(isEnabled);
		return clone;
	}
}
