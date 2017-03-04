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
import com.jme3.math.Vector4f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;

public class FillLayout extends AbstractGenericLayout<ElementContainer<?,?>, Object> {

	@Override
	protected Vector2f calcMinimumSize(ElementContainer<?,?> target) {
		Vector2f d = new Vector2f(0, 0);
		for (BaseElement c : target.getElements()) {
			Vector2f s = c.calcMinimumSize();
			if (s.x > d.x) {
				d.x = s.x;
			}
			if (s.y > d.y) {
				d.y = s.y;
			}
		}
		d.addLocal(target.getTotalPadding());
		return d;

	}

	@Override
	protected Vector2f calcPreferredSize(ElementContainer<?,?> target) {
		Vector2f d = new Vector2f(0, 0);
		for (BaseElement c : target.getElements()) {
			Vector2f s = c.calcPreferredSize();
			if (s.x > d.x) {
				d.x = s.x;
			}
			if (s.y > d.y) {
				d.y = s.y;
			}
		}
		d.addLocal(target.getTotalPadding());
		return d;
	}

	@Override
	protected void onLayout(ElementContainer<?,?> target) {
		Vector4f padding = target.getAllPadding();
		int top = (int) padding.z;
		int bottom = (int) (target.getHeight() - target.getAllPadding().w);
		int left = (int) target.getAllPadding().x;
		int right = (int) (target.getWidth() - target.getAllPadding().y);
		int w = right - left;
		int h = bottom - top;
		for (BaseElement el : target.getElements()) {
			int tw = right - left;
			int th = bottom - top;
			Vector2f maxDimensions = el.calcMaximumSize();
			if (maxDimensions != null) {
				if (tw > maxDimensions.x) {
					tw = (int) maxDimensions.x;
				}
				if (th > maxDimensions.y) {
					th = (int) maxDimensions.y;
				}
			}
			el.setBounds((int) ((w - tw) / 2f) + left, (int) ((h - th) / 2f) + top, tw, th);
		}
	}
}
