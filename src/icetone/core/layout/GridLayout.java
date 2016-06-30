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

import java.util.Iterator;

import com.jme3.math.Vector2f;

import icetone.core.Element;

public class GridLayout extends AbstractLayout {

	private final int w;
	private final int h;

	public GridLayout(int w, int h) {
		this.w = w;
		this.h = h;
	}

	public void remove(Element child) {
	}

	public void constrain(Element comp, Object constraints) {
	}

	public Vector2f minimumSize(Element target) {
		Iterator<Element> el = target.getElements().iterator();
		float y = 0;
		float x = 0;
		for (int i = 0; el.hasNext() && i < h; i++) {
			float my = 0;
			float mx = 0;
			for (int j = 0; el.hasNext() && j < w; j++) {
				Vector2f thisSize = LUtil.getMinimumSize(el.next());
				mx += thisSize.x;
				my = Math.max(my, thisSize.y);
			}
			y += my;
			x = Math.max(x, mx);
		}
		y += target.borders.x + target.borders.w;
		x += target.borders.y + target.borders.z;
		return new Vector2f(x, y);
	}

	public Vector2f maximumSize(Element target) {
		Iterator<Element> el = target.getElements().iterator();
		double y = 0;
		double x = 0;
		for (int i = 0; el.hasNext() && i < h; i++) {
			float my = 0;
			float mx = 0;
			for (int j = 0; el.hasNext() && j < w; j++) {
				Vector2f thisSize = LUtil.getMaximumSize(el.next());
				mx += thisSize.x;
				my = Math.max(my, thisSize.y);
			}
			y += my;
			x = Math.max(x, mx);
		}
		y += target.borders.x + target.borders.w;
		x += target.borders.y + target.borders.z;
		return new Vector2f((float) Math.min(Float.MAX_VALUE, x), (float) Math.min(Float.MAX_VALUE, y));
	}

	public Vector2f preferredSize(Element target) {
		Iterator<Element> el = target.getElements().iterator();
		float y = 0;
		float x = 0;
		for (int i = 0; el.hasNext() && i < h; i++) {
			float my = 0;
			float mx = 0;
			for (int j = 0; el.hasNext() && j < w; j++) {
				Vector2f thisSize = LUtil.getBoundPreferredSize(el.next());
				mx += thisSize.x;
				my = Math.max(my, thisSize.y);
			}
			y += my;
			x = Math.max(x, mx);
		}
		y += target.borders.x + target.borders.w;
		x += target.borders.y + target.borders.z;
		return new Vector2f(x, y);
	}

	public void layout(Element target) {
		float tw = (float) (target.getWidth() - target.borders.y - target.borders.z);
		float th = (float) (target.getHeight() - target.borders.x - target.borders.w);
		float ew = ((float) tw / (float) w);
		float eh = ((float) th / (float) h);
		float oy = target.borders.w;
		float ox = target.borders.y;
		Iterator<Element> el = target.getElements().iterator();
		float yy = oy;
		for (int y = 0; el.hasNext() && y < h; y++) {
			float xx = ox;
			for (int x = 0; el.hasNext() && x < w; x++) {
				LUtil.setBounds(el.next(), xx, yy, ew, eh);
				xx += ew;
			}
			yy += eh;
		}
		LUtil.layoutChildren(target);
	}
}
