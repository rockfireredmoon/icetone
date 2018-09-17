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

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author t0neg0d
 */
public class EffectQueue extends AbstractEffect {
	private List<EffectQueueItem> queue = new LinkedList<>();
	private EffectQueueItem currentEffectItem = null;
	private float updateTime = 0;
	private float targetTime = 0;
	private boolean effectSet = false;
	private boolean effectStarted = false;

	public EffectQueue() {

	}

	public void addEffect(IEffect effect, float delayTime) {
		EffectQueueItem item = new EffectQueueItem(effect, delayTime);
		queue.add(item);
	}

	@Override
	public void update(float tpf) {
		if (isActive) {
			if (!effectSet) {
				currentEffectItem = queue.remove(0);
				targetTime = currentEffectItem.getDelay();
				updateTime = 0;
				effectSet = true;
				effectStarted = false;
			} else {
				if (!effectStarted) {
					if (updateTime < targetTime) {
						updateTime += tpf / targetTime;
					} else {
						// currentEffectItem.getEffect().getElement().getScreen().updateZOrder(currentEffectItem.getEffect().getElement());
						effectManager.applyEffect(currentEffectItem.getEffect());
						effectStarted = true;
					}
				} else {
					if (currentEffectItem.getEffect() != null) {
						if (!currentEffectItem.getEffect().getIsActive()) {
							effectSet = false;
							effectStarted = false;
							if (queue.isEmpty()) {
								isActive = false;
								// effectManager.removeEffectQueue(this);
							}
						}
					} else {
						if (!currentEffectItem.getEffect().getIsActive()) {
							effectSet = false;
							effectStarted = false;
							if (queue.isEmpty()) {
								isActive = false;
								// effectManager.removeEffectQueue(this);
							}
						}
					}
				}
			}
		}
	}

	public class EffectQueueItem {
		private IEffect effect = null;
		private float delay;

		public EffectQueueItem(IEffect effect, float delay) {
			this.effect = effect;
			this.delay = delay;
		}

		public IEffect getEffect() {
			return this.effect;
		}

		public float getDelay() {
			return this.delay;
		}
	}

}
