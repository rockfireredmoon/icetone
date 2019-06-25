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
import com.jme3.math.Vector4f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;

public class GridLayout extends AbstractGenericLayout<ElementContainer<?, ?>, Object> {

	private final int w;
	private final int h;

	public GridLayout(int w, int h) {
		this.w = w;
		this.h = h;
	}

	@Override
	protected Vector2f calcMinimumSize(ElementContainer<?, ?> target) {
		float indent = target.getIndent();
		Iterator<BaseElement> el = target.getElements().iterator();
		float y = 0;
		float x = 0;
		for (int i = 0; el.hasNext() && i < h; i++) {
			float my = 0;
			float mx = 0;
			for (int j = 0; el.hasNext() && j < w; j++) {
				Vector2f thisSize = el.next().calcMinimumSize();
				if (j > 0)
					x += indent;
				mx += thisSize.x;
				my = Math.max(my, thisSize.y);
			}
			y += my;
			if (i > 0)
				y += indent;
			x = Math.max(x, mx);
		}
		return new Vector2f(x, y).addLocal(target.getTotalPadding());
	}

	@Override
	protected Vector2f calcMaximumSize(ElementContainer<?, ?> target) {
		Iterator<BaseElement> el = target.getElements().iterator();
		float indent = target.getIndent();
		double y = 0;
		double x = 0;
		for (int i = 0; el.hasNext() && i < h; i++) {
			float my = 0;
			float mx = 0;
			for (int j = 0; el.hasNext() && j < w; j++) {
				Vector2f thisSize = el.next().calcMaximumSize();
				if (j > 0)
					x += indent;
				mx += thisSize.x;
				my = Math.max(my, thisSize.y);
			}
			y += my;
			if (i > 0)
				y += indent;
			x = Math.max(x, mx);
		}
		Vector2f pad = target.getTotalPadding();
		y += pad.y;
		x += pad.x;
		return new Vector2f((float) Math.min(Float.MAX_VALUE, x), (float) Math.min(Float.MAX_VALUE, y));
	}

	@Override
	protected Vector2f calcPreferredSize(ElementContainer<?, ?> target) {
		Iterator<BaseElement> el = target.getElements().iterator();
		float y = 0;
		float x = 0;
		float indent = target.getIndent();
		for (int i = 0; el.hasNext() && i < h; i++) {
			float my = 0;
			float mx = 0;
			for (int j = 0; el.hasNext() && j < w; j++) {
				Vector2f thisSize = el.next().calcPreferredSize();
				mx += thisSize.x;
				if (j > 0)
					x += indent;
				my = Math.max(my, thisSize.y);
			}
			if (i > 0)
				y += indent;
			y += my;
			x = Math.max(x, mx);
		}
		return new Vector2f(x, y).addLocal(target.getTotalPadding());
	}

	@Override
	protected void onLayout(ElementContainer<?, ?> target) {
		Vector4f pad = target.getAllPadding();
		float indent = target.getIndent();
		float tw = target.getWidth() - pad.x - pad.y - ((float) w * (Math.max(0, indent - 1)));
		float th = target.getHeight() - pad.z - pad.w - ((float) h * (Math.max(0, indent - 1)));
		float ew = (tw / w);
		float eh = (th / h);
		float oy = pad.z;
		float ox = pad.x;
		Iterator<BaseElement> el = target.getElements().iterator();
		float yy = oy;
		for (int y = 0; el.hasNext() && y < h; y++) {
			float xx = ox;
			for (int x = 0; el.hasNext() && x < w; x++) {
				el.next().setBounds(xx, yy, ew, eh);
				xx += ew + indent;
			}
			yy += eh + indent;
		}
	}
}
