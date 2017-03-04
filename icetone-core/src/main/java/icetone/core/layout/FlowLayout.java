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

import java.util.Collection;
import java.util.Iterator;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Orientation;

/**
 * A layout manager that flows either left to right, or top the bottom.
 * Preferred sizes will be used to determine child size. The size of the gaps
 * between elements may be specified, also children may be made to fill all
 * available space in opposite direction to the flow (i.e. will grow
 * horizontally when flowing vertically and vice versa).
 */
public class FlowLayout extends AbstractGenericLayout<ElementContainer<?,?>, Object> {

	private int gap = -1;
	private BitmapFont.Align align = Align.Center;
	private BitmapFont.VAlign valign = VAlign.Center;
	private Orientation orientation = Orientation.HORIZONTAL;
	private boolean fill;
	private boolean equalSizeCells;
	private float overlap;

	public FlowLayout() {
		this(-1, BitmapFont.Align.Center);
	}

	public BitmapFont.Align getAlign() {
		return align;
	}

	public FlowLayout setAlign(BitmapFont.Align align) {
		this.align = align;
		return this;
	}

	public BitmapFont.VAlign getValign() {
		return valign;
	}

	public FlowLayout setValign(BitmapFont.VAlign valign) {
		this.valign = valign;
		return this;
	}

	public FlowLayout(Orientation orientation) {
		this(-1, BitmapFont.Align.Center);
		this.orientation = orientation;
	}

	public FlowLayout(BitmapFont.Align align) {
		this(-1, align);
	}

	public FlowLayout(int gap, BitmapFont.Align align) {
		this.orientation = Orientation.HORIZONTAL;
		this.gap = gap;
		this.align = align;
	}

	public FlowLayout(int gap, BitmapFont.VAlign valign) {
		this.orientation = Orientation.VERTICAL;
		this.gap = gap;
		this.valign = valign;
	}

	public FlowLayout(int gap) {
		this(gap, Align.Center);
	}

	public float getOverlap() {
		return overlap;
	}

	public void setOverlap(float overlap) {
		this.overlap = overlap;
	}

	public boolean isEqualSizeCells() {
		return equalSizeCells;
	}

	public FlowLayout setEqualSizeCells(boolean equalSizeCells) {
		this.equalSizeCells = equalSizeCells;
		return this;
	}

	public boolean isFill() {
		return fill;
	}

	public FlowLayout setFill(boolean fill) {
		this.fill = fill;
		return this;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public FlowLayout setOrientation(Orientation orientation) {
		this.orientation = orientation;
		return this;
	}

	public int getGap() {
		return gap;
	}

	public FlowLayout setGap(int gap) {
		this.gap = gap;
		return this;
	}

	float calcGap(ElementContainer<?,?> el) {
		return gap == -1 ? el.getIndent() : gap;
	}

	@Override
	protected Vector2f calcMinimumSize(ElementContainer<?,?> target) {
		if (target.getElements().isEmpty())
			return null;
		Vector2f minSize = new Vector2f();
		float gap = calcGap(target);
		Iterator<BaseElement> el = target.getElements().iterator();
		if (equalSizeCells) {
			int count = 0;
			float max = 0;
			while (el.hasNext()) {
				Vector2f thisSize = el.next().calcMinimumSize();
				if (orientation == Orientation.HORIZONTAL) {
					max = Math.max(max, thisSize.x);
					minSize.y = Math.max(minSize.y, thisSize.y);
				} else {
					max = Math.max(max, thisSize.y);
					minSize.x = Math.max(minSize.x, thisSize.x);
				}
				count++;
			}
			if (orientation == Orientation.HORIZONTAL) {
				minSize.set(((max + getGap()) * count) - (count > 0 ? gap : 0) + minSize.x, minSize.y);
			} else {
				minSize.set(minSize.x, ((max + gap) * count) - (count > 0 ? gap : 0) + minSize.y);
			}
		} else {
			while (el.hasNext()) {
				Vector2f thisSize = el.next().calcMinimumSize();
				if (orientation == Orientation.HORIZONTAL) {
					minSize.x += thisSize.x + (el.hasNext() ? gap : 0);
					minSize.y = Math.max(minSize.y, thisSize.y);
				} else {
					minSize.y += thisSize.y + (el.hasNext() ? gap : 0);
					minSize.x = Math.max(minSize.x, thisSize.x);
				}
			}
		}
		minSize.addLocal(target.getTotalPadding());
		return minSize;
	}

	@Override
	protected Vector2f calcPreferredSize(ElementContainer<?,?> target) {
		if (target.getElements().isEmpty())
			return calcMinimumSize(target);
		return calcPreferred(target).addLocal(target.getTotalPadding());
	}

	protected Vector2f calcPreferred(ElementContainer<?,?> target) {
		Vector2f prefSize = new Vector2f();
		float gap = calcGap(target);
		Iterator<BaseElement> el = target.getElements().iterator();
		if (equalSizeCells) {
			int count = 0;
			float max = 0;
			while (el.hasNext()) {
				Vector2f thisSize = el.next().calcPreferredSize();
				if (orientation == Orientation.HORIZONTAL) {
					max = Math.max(max, thisSize.x);
					prefSize.y = Math.max(prefSize.y, thisSize.y);
				} else {
					max = Math.max(max, thisSize.y);
					prefSize.x = Math.max(prefSize.x, thisSize.x);
				}
				count++;
			}
			if (orientation == Orientation.HORIZONTAL) {
				prefSize.set(((max + gap) * count) - (count > 0 ? gap : 0) + prefSize.x, prefSize.y);
			} else {
				prefSize.set(prefSize.x, ((max + gap) * count) - (count > 0 ? gap : 0) + prefSize.y);
			}
		} else {
			while (el.hasNext()) {
				final BaseElement cel = el.next();
				Vector2f thisSize = cel.calcPreferredSize();
				if (orientation == Orientation.HORIZONTAL) {
					prefSize.x += thisSize.x + (el.hasNext() ? gap : 0);
					prefSize.y = Math.max(prefSize.y, thisSize.y);
				} else {
					prefSize.y += thisSize.y + (el.hasNext() ? gap : 0);
					prefSize.x = Math.max(prefSize.x, thisSize.x);
				}
			}
		}
		return prefSize;
	}

	@Override
	protected void onLayout(ElementContainer<?,?> target) {
		Vector2f availableSpace = target.getDimensions();

		float gap = calcGap(target);
		Vector4f padding = target.getAllPadding();
		float tw = (availableSpace.x - padding.x - padding.y);
		float th = (availableSpace.y - padding.z - padding.w);

		int c = 0;
		Vector2f pref = calcPreferred(target);

		if (orientation == Orientation.HORIZONTAL) {

			final Collection<BaseElement> elements = target.getElements();
			float fixedW = -1;
			float totalPrefWidth = 0;
			if (equalSizeCells) {
				for (BaseElement el : elements) {
					Vector2f prefSize = el.calcPreferredSize();
					fixedW = Math.max(prefSize.x, fixedW);
				}
				totalPrefWidth = Math.max(0, ((fixedW + gap) * elements.size()) - gap);
			} else
				totalPrefWidth = pref.x;

			float factor = 1;
			if (tw < totalPrefWidth)
				factor = tw / totalPrefWidth;

			float x = 0;
			switch (align) {
			case Left:
				x = padding.x;
				break;
			case Center:
				if (factor == 1)
					x = Math.round((tw - pref.x) / 2f) + padding.x;
				else
					x = padding.x;
				break;
			case Right:
				x = availableSpace.x - pref.x - padding.y;
				break;
			}

			Iterator<BaseElement> el = elements.iterator();
			while (el.hasNext()) {
				if (c == 1)
					x -= overlap;
				BaseElement e = el.next();
				Vector2f prefSize = e.calcPreferredSize();
				if (fill || prefSize.y > th) {
					prefSize.y = th;
				}
				if (fixedW > -1) {
					prefSize.x = fixedW;
				}
				float y = 0;
				switch (valign) {
				case Top:
					y = padding.z;
					break;
				case Bottom:
					y = th - padding.z - prefSize.y;
					break;
				default:
					y = Math.round(((th - prefSize.y) / 2f) + padding.z);
					break;
				}

				e.setBounds(x, y, prefSize.x * factor, prefSize.y);
				x += (prefSize.x + gap) * factor;
				c++;
			}
		} else {
			final Collection<BaseElement> elements = target.getElements();
			float fixedH = -1;
			float totalPrefHeight = 0;
			if (equalSizeCells) {
				for (BaseElement el : elements) {
					Vector2f prefSize = el.calcPreferredSize();
					fixedH = Math.max(prefSize.y, fixedH);
				}
				totalPrefHeight = Math.max(0, ((fixedH + gap) * elements.size()) - gap);
			} else
				totalPrefHeight = pref.y;

			float factor = 1;
			if (th < totalPrefHeight)
				factor = th / totalPrefHeight;

			float y = 0;
			switch (valign) {
			case Top:
				y = padding.z;
				break;
			case Center:
				if (factor == 1)
					y = padding.z + Math.round((th - pref.y) / 2f);
				else
					y = padding.z;
				break;
			case Bottom:
				y = th - padding.w - pref.y;
				break;
			}

			Iterator<BaseElement> el = elements.iterator();
			while (el.hasNext()) {
				if (c == 1) {
					y -= overlap;
				}
				BaseElement e = el.next();
				Vector2f prefSize = e.calcPreferredSize();
				if (fill || prefSize.x > tw) {
					prefSize.x = tw;
				}
				if (fixedH > -1) {
					prefSize.y = fixedH;
				}
				float x = Math.round(((tw - prefSize.x) / 2f) + padding.x);
				e.setBounds(x, y, prefSize.x, prefSize.y * factor);
				y += (prefSize.y + gap) * factor;
				c++;
			}
		}
	}
}
