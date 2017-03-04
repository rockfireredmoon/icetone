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

import java.util.Collections;
import java.util.List;

import icetone.core.BaseElement;

/**
 *
 * @author rockfire
 */
public abstract class AbstractEffect implements IEffect {
	protected boolean isActive = true;
	protected BaseElement element;
	protected EffectManager effectManager;
	protected EffectChannel channel;
	protected int priority = 50;

	public AbstractEffect() {
	}
	
	@Override
	public int compareTo(IEffect other) {
		int i = channel == null ? 1  : ( other.getChannel() == null ? -1 : channel.compareTo(other.getChannel()) );
		return i == 0 ? priority - other.getPriority() : i;
	}

	public int getPriority() {
		return priority;
	}

	public IEffect setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	@Override
	public IEffect setChannel(EffectChannel channel) {
		this.channel = channel;
		return this;
	}

	@Override
	public EffectChannel getChannel() {
		return channel;
	}

	@Override
	public IEffect setEffectManager(EffectManager effectManager) {
		this.effectManager = effectManager;
		return this;
	}

	@Override
	public boolean getIsActive() {
		return isActive;
	}

	@Override
	public boolean isConflict(IEffect effect) {
		return effect.getClass().equals(getClass());
	}

	@Override
	public IEffect setIsActive(boolean isActive) {
		if (isActive != this.isActive) {
			this.isActive = isActive;
			if(!isActive)
				onDeactivate();
		}
		return this;
	}
	
	protected void onDeactivate() {
	}

	@Override
	public IEffect setElement(BaseElement element) {
		this.element = element;
		return this;
	}

	@Override
	public BaseElement getElement() {
		return element;
	}

	@Override
	public List<IEffect> getEffects() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [isActive=" + isActive + ", element=" + element + ", channel=" + channel
				+ ", getEffects()=" + getEffects() + "]";
	}
}
