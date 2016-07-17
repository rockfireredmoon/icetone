/*
 * Copyright (c) 2013-2014 Emerald Icemoon All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package icetone.core.layout;

import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.ElementManager;

public class FillLayout extends AbstractLayout {

	public void remove(Element child) {
	}

	public void constrain(Element comp, Object constraints) {
	}

	public Vector2f minimumSize(Element target) {
		Vector2f d = new Vector2f(0, 0);
		for (Element c : target.getElements()) {
			Vector2f s = LUtil.getMinimumSize(c);
			if (s.x > d.x || s.y > d.y) {
				d = s;
			}
		}
		d.x += target.getTextPaddingVec().x + target.getTextPaddingVec().y;
		d.y += target.getTextPaddingVec().z + target.getTextPaddingVec().w;
		return d;

	}

	public Vector2f maximumSize(Element target) {
		Vector2f d = new Vector2f(Short.MAX_VALUE, Short.MAX_VALUE);
		for (Element c : target.getElements()) {
			Vector2f s = LUtil.getMaximumSize(c);
			if (s.x < d.x || s.y < d.y) {
				d = s;
			}
		}
		return d;
	}

	public Vector2f preferredSize(Element target) {
		Vector2f d = new Vector2f(0, 0);
		for (Element c : target.getElements()) {
			Vector2f s = LUtil.getPreferredSize(c);
			if (s.x > d.x || s.y > d.y) {
				d = s;
			}
		}
		d.x += target.getTextPaddingVec().x + target.getTextPaddingVec().y;
		d.y += target.getTextPaddingVec().z + target.getTextPaddingVec().w;
		return d;
	}

	@Override
	public void layoutScreen(ElementManager screen) {
		Vector2f sz = LUtil.getScreenSize(screen);
		int top = (int) 0;
		int bottom = (int) sz.y;
		int left = (int) 0;
		int right = (int) sz.x;
		for (Element el : screen.getElements()) {
			LUtil.setBounds(el, left, top, right - left, bottom - top);
		}
	}

	public void layout(Element target) {
		int top = (int) target.getTextPaddingVec().z;
		int bottom = (int) (target.getHeight() - target.getTextPaddingVec().w);
		int left = (int) target.getTextPaddingVec().x;
		int right = (int) (target.getWidth() - target.getTextPaddingVec().y);
		int w = right - left;
		int h = bottom - top;
		for (Element el : target.getElements()) {
			int tw = right - left;
			int th = bottom - top;
			if (el.getMaxDimensions() != null) {
				if (tw > el.getMaxDimensions().x) {
					tw = (int) el.getMaxDimensions().x;
				}
				if (th > el.getMaxDimensions().y) {
					th = (int) el.getMaxDimensions().y;
				}
			}
			LUtil.setBounds(el, (int) ((w - tw) / 2f) + left, (int) ((h - th) / 2f) + top, tw, th);
		}
	}
}
