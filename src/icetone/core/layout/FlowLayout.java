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

import icetone.core.Element;

/**
 * A layout manager that flows either left to right, or top the bottom.
 * Preferred sizes
 * will be used to determine child size. The size of the gaps between elements
 * may be
 * specified, also children may be made to fill all available space in opposite
 * direction
 * to the flow (i.e. will grow horizontally when flowing vertically and vice
 * versa).
 */
public class FlowLayout extends AbstractLayout {

	private int gap;
	private BitmapFont.Align align = Align.Center;
	private BitmapFont.VAlign valign = VAlign.Center;
	private Element.Orientation orientation = Element.Orientation.HORIZONTAL;
	private boolean fill;
	private boolean equalSizeCells;
	private Vector4f margin;

	public FlowLayout() {
		this(0, BitmapFont.Align.Center);
	}

	public BitmapFont.Align getAlign() {
		return align;
	}

	public void setAlign(BitmapFont.Align align) {
		this.align = align;
	}

	public BitmapFont.VAlign getValign() {
		return valign;
	}

	public void setValign(BitmapFont.VAlign valign) {
		this.valign = valign;
	}

	public FlowLayout(int gap, BitmapFont.Align align) {
		this.orientation = Element.Orientation.HORIZONTAL;
		this.gap = gap;
		this.align = align;
	}

	public FlowLayout(int gap, BitmapFont.VAlign valign) {
		this.orientation = Element.Orientation.VERTICAL;
		this.gap = gap;
		this.valign = valign;
	}

	public boolean isEqualSizeCells() {
		return equalSizeCells;
	}

	public void setEqualSizeCells(boolean equalSizeCells) {
		this.equalSizeCells = equalSizeCells;
	}

	public boolean isFill() {
		return fill;
	}

	public FlowLayout setFill(boolean fill) {
		this.fill = fill;
		return this;
	}

	public Element.Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Element.Orientation orientation) {
		this.orientation = orientation;
	}

	public Vector4f getMargin() {
		return margin;
	}

	public void setMargin(Vector4f margin) {
		this.margin = margin;
	}

	public int getGap() {
		return gap;
	}

	public FlowLayout setGap(int gap) {
		this.gap = gap;
		return this;
	}

	public void remove(Element child) {
	}

	public void constrain(Element comp, Object constraints) {
	}

	public Vector2f minimumSize(Element target) {
		Vector2f minSize = new Vector2f();
		Iterator<Element> el = target.getElements().iterator();
		if (equalSizeCells) {
			int count = 0;
			float max = 0;
			while (el.hasNext()) {
				Vector2f thisSize = LUtil.getMinimumSize(el.next());
				if (orientation == Element.Orientation.HORIZONTAL) {
					max = Math.max(max, thisSize.x);
					minSize.y = Math.max(minSize.y, thisSize.y);
				} else {
					max = Math.max(max, thisSize.y);
					minSize.x = Math.max(minSize.x, thisSize.x);
				}
				count++;
			}
			if (orientation == Element.Orientation.HORIZONTAL) {
				minSize.set(((max + gap) * count) - (count > 0 ? gap : 0) + minSize.x, minSize.y);
			} else {
				minSize.set(minSize.x, ((max + gap) * count) - (count > 0 ? gap : 0) + minSize.y);
			}
		} else {
			while (el.hasNext()) {
				Vector2f thisSize = LUtil.getMinimumSize(el.next());
				if (orientation == Element.Orientation.HORIZONTAL) {
					minSize.x += thisSize.x + (el.hasNext() ? gap : 0);
					minSize.y = Math.max(minSize.y, thisSize.y);
				} else {
					minSize.y += thisSize.y + (el.hasNext() ? gap : 0);
					minSize.x = Math.max(minSize.x, thisSize.x);
				}
			}
		}
		Vector4f padding = target.getTextPaddingVec();
		minSize.addLocal(padding.x + padding.y, padding.z + padding.w);
		if (margin != null) {
			minSize.addLocal(margin.y + margin.z, margin.x + margin.w);
		}
		return minSize;
	}

	public Vector2f maximumSize(Element target) {
		Vector2f maxSize = new Vector2f();
		Iterator<Element> el = target.getElements().iterator();
		if (equalSizeCells) {
			int count = 0;
			float max = 0;
			while (el.hasNext()) {
				Vector2f thisSize = LUtil.getMaximumSize(el.next());
				if (orientation == Element.Orientation.HORIZONTAL) {
					max = Math.max(max, thisSize.x);
					maxSize.y = Math.max(maxSize.y, thisSize.y);
				} else {
					max = Math.max(max, thisSize.y);
					maxSize.x = Math.max(maxSize.x, thisSize.x);
				}
				count++;
			}
			if (orientation == Element.Orientation.HORIZONTAL) {
				maxSize.set(((max + gap) * count) - (count > 0 ? gap : 0) + maxSize.x, maxSize.y);
			} else {
				maxSize.set(maxSize.x, ((max + gap) * count) - (count > 0 ? gap : 0) + maxSize.y);
			}
		} else {
			while (el.hasNext()) {
				Vector2f thisSize = LUtil.getMaximumSize(el.next());
				if (orientation == Element.Orientation.HORIZONTAL) {
					maxSize.x += thisSize.x + (el.hasNext() ? gap : 0);
					maxSize.y = Math.max(maxSize.y, thisSize.y);
				} else {
					maxSize.y += thisSize.y + (el.hasNext() ? gap : 0);
					maxSize.x = Math.max(maxSize.x, thisSize.x);
				}

			}
		}
		Vector4f padding = target.getTextPaddingVec();
		maxSize.addLocal(padding.x + padding.y, padding.z + padding.w);
		if (margin != null) {
			maxSize.addLocal(margin.y + margin.z, margin.x + margin.w);
		}
		return maxSize;
	}

	public Vector2f preferredSize(Element target) {
		Vector2f prefSize = calcPreferred(target);

		Vector4f padding = target.getTextPaddingVec();
		prefSize.addLocal(padding.x + padding.y, padding.z + padding.w);
		if (margin != null) {
			prefSize.addLocal(margin.y + margin.z, margin.x + margin.w);
		}
		
		return prefSize;
	}

	protected Vector2f calcPreferred(Element target) {
		Vector2f prefSize = new Vector2f();
		Iterator<Element> el = target.getElements().iterator();
		if (equalSizeCells) {
			int count = 0;
			float max = 0;
			while (el.hasNext()) {
				Vector2f thisSize = LUtil.getPreferredSize(el.next());
				if (orientation == Element.Orientation.HORIZONTAL) {
					max = Math.max(max, thisSize.x);
					prefSize.y = Math.max(prefSize.y, thisSize.y);
				} else {
					max = Math.max(max, thisSize.y);
					prefSize.x = Math.max(prefSize.x, thisSize.x);
				}
				count++;
			}
			if (orientation == Element.Orientation.HORIZONTAL) {
				prefSize.set(((max + gap) * count) - (count > 0 ? gap : 0) + prefSize.x, prefSize.y);
			} else {
				prefSize.set(prefSize.x, ((max + gap) * count) - (count > 0 ? gap : 0) + prefSize.y);
			}
		} else {
			while (el.hasNext()) {
				final Element cel = el.next();
				Vector2f thisSize = LUtil.getPreferredSize(cel).clone();
				if (orientation == Element.Orientation.HORIZONTAL) {
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

	public void layout(Element target) {
		Vector2f availableSpace = target.getDimensions();
		if (availableSpace.equals(LUtil.LAYOUT_SIZE)) {
			availableSpace = Vector2f.ZERO;
		}

		Vector4f padding = target.getTextPaddingVec();
		float tw = (availableSpace.x - padding.x - padding.y);
		float th = (availableSpace.y - padding.z - padding.w);
		if (margin != null) {
			tw -= margin.y + margin.z;
			th -= margin.x + margin.w;
		}

		Vector2f pref = calcPreferred(target);

		if (orientation == Element.Orientation.HORIZONTAL) {
			float x = 0;
			switch (align) {
			case Left:
				x = padding.x + (margin == null ? 0 : margin.y);
				break;
			case Center:
				x = Math.round((tw - pref.x) / 2f) + padding.x + (margin == null ? 0 : margin.y);
				break;
			case Right:
				x = tw - pref.x - padding.y - (margin == null ? 0 : margin.y);
				break;
			}
			final Collection<Element> elements = target.getElements();

			float fixedW = -1;
			if (equalSizeCells) {
				for (Element el : elements) {
					Vector2f prefSize = LUtil.getPreferredSize(el);
					fixedW = Math.max(prefSize.x, fixedW);
				}
			}

			Iterator<Element> el = elements.iterator();
			while (el.hasNext()) {
				Element e = el.next();
				Vector2f prefSize = LUtil.getPreferredSize(e);
				if (fill) {
					prefSize.y = th;
				}
				if (fixedW > -1) {
					prefSize.x = fixedW;
				}
				float y = 0;
				switch(valign) {
				case Top:
					y = padding.z + ( margin == null ? 0 : margin.x );
					break;
				case Bottom:
					y = th - padding.z - prefSize.y;
					break;
				default:
					y = Math.round(((th - prefSize.y) / 2f) + padding.z + (margin == null ? 0 : margin.x));
					break;
				}
				LUtil.setBounds(e, x, y, prefSize.x, prefSize.y);
				x += prefSize.x + gap;
			}
		} else {
			float y = 0;
			switch (valign) {
			case Top:
				y = padding.z + (margin == null ? 0 : margin.x);
				break;
			case Center:
				y = padding.w + Math.round((th - pref.y) / 2f);
				break;
			case Bottom:
				y = th - padding.w - pref.y - (margin == null ? 0 : margin.w);
				break;
			}
			final Collection<Element> elements = target.getElements();
			float fixedH = -1;
			if (equalSizeCells) {
				for (Element el : elements) {
					Vector2f prefSize = LUtil.getPreferredSize(el);
					fixedH = Math.max(prefSize.y, fixedH);
				}
			}

			Iterator<Element> el = elements.iterator();
			while (el.hasNext()) {
				Element e = el.next();
				Vector2f prefSize = LUtil.getPreferredSize(e);
				if (fill) {
					prefSize.x = tw;
				}
				if (fixedH > -1) {
					prefSize.y = fixedH;
				}
				float x = Math.round(((tw - prefSize.x) / 2f) + padding.x + (margin == null ? 0 : margin.y));
				LUtil.setBounds(e, x, y, prefSize.x, prefSize.y);
				y += prefSize.y + gap;
			}
		}
	}
}
