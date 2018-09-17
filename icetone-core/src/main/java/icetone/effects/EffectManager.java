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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;

/**
 *
 * @author t0neg0d
 */
public class EffectManager implements Control {
	private BaseScreen screen;
	private List<IEffect> currentEffects = new ArrayList<>();
	private List<IEffect> toApply = new LinkedList<>();
	private boolean runningEffect;

	public EffectManager(BaseScreen screen) {
		this.screen = screen;
	}

	public void applyEffect(IEffect effect) {
		if (effect != null) {
			if (runningEffect) {
				toApply.add(effect);
				Collections.sort(toApply);
			}
			else {
				Collections.sort(currentEffects);
				currentEffects.add(effect);
			}
			effect.setEffectManager(this);
			update(0);
		}
	}

	@Override
	public void update(float tpf) {
		runningEffect = true;
		try {
			for (Iterator<IEffect> it = currentEffects.iterator(); it.hasNext();) {
				IEffect effect = it.next();
				if (effect.getIsActive())
					effect.update(tpf);
				else {
					it.remove();
					break;
				}
			}
		} catch (ConcurrentModificationException cme) {
			// If the effect itself causes ANY kind of layout (including clip
			// updates),
			// this can happen if something has requested a full layout
		} finally {
			runningEffect = false;
		}
		if (!toApply.isEmpty()) {
			currentEffects.addAll(toApply);
			Collections.sort(currentEffects);
			toApply.clear();
		}
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
	}

	@Override
	public void read(JmeImporter im) throws IOException {
	}

	public void stopAllFor(BaseElement el, EffectChannel channel) {
		for (IEffect e : currentEffects) {
			if (matches(el, channel, e)) {
				e.setIsActive(false);
			}
			for (IEffect c : e.getEffects()) {
				if (matches(el, channel, c))
					e.setIsActive(false);
			}
		}
		for (IEffect e : toApply) {
			if (matches(el, channel, e)) {
				e.setIsActive(false);
			}
			for (IEffect c : e.getEffects()) {
				if (matches(el, channel, c))
					e.setIsActive(false);
			}
		}
	}

	protected boolean matches(BaseElement el, EffectChannel channel, IEffect e) {
		return (el == null || el.equals(e.getElement()))
				&& (channel == null || (e.getChannel() != null && e.getChannel().equals(channel)));
	}

	public List<IEffect> getEffectsFor(BaseElement el, EffectChannel channel) {
		List<IEffect> l = new ArrayList<>();
		addEffectsFor(l, el, channel);
		return l;
	}

	public BaseScreen getScreen() {
		return screen;
	}

	protected void addEffectsFor(List<IEffect> list, BaseElement el, EffectChannel channel) {
		addEffectsFor(list, el, channel, currentEffects);
		addEffectsFor(list, el, channel, toApply);
	}

	public boolean hasEffectFor(BaseElement el, EffectChannel channel) {
		if (hasEffectFor(el, channel, currentEffects))
			return true;
		if (hasEffectFor(el, channel, toApply))
			return true;
		return false;
	}

	protected boolean hasEffectFor(BaseElement el, EffectChannel channel, List<IEffect> currentEffects) {
		for (IEffect e : currentEffects) {
			if (matches(el, channel, e)) {
				return true;
			}
			for (IEffect c : e.getEffects()) {
				if (matches(el, channel, c))
					return true;
			}
		}
		return false;
	}

	protected void addEffectsFor(List<IEffect> list, BaseElement el, EffectChannel channel, List<IEffect> currentEffects) {
		for (IEffect e : currentEffects) {
			if (matches(el, channel, e)) {
				list.add(e);
			}
			for (IEffect c : e.getEffects()) {
				if (matches(el, channel, c))
					list.add(c);
			}
		}
	}
}
