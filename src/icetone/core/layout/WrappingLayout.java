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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;

/**
 * A layout manager that flows either left to right or top to bottom, wrapping
 * at the edge
 * of the parent.
 */
public class WrappingLayout extends AbstractLayout {

	private int gap;
	// private Vector4f margin;
	private Element.Orientation orientation = Element.Orientation.HORIZONTAL;
	private boolean equalSizeCells;
	private boolean fill;
	private BitmapFont.Align align = BitmapFont.Align.Left;
	private BitmapFont.VAlign vAlign = BitmapFont.VAlign.Top;
	private int width;

	public WrappingLayout() {
		this(0);
	}

	public WrappingLayout(int gap) {
		this.gap = gap;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Element.Orientation getOrientation() {
		return orientation;
	}

	public boolean isEqualSizeCells() {
		return equalSizeCells;
	}

	public boolean isFill() {
		return fill;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}

	public LayoutManager setEqualSizeCells(boolean equalSizeCells) {
		this.equalSizeCells = equalSizeCells;
		return this;
	}

	public WrappingLayout setOrientation(Element.Orientation orientation) {
		this.orientation = orientation;
		return this;
	}

	public int getGap() {
		return gap;
	}

	public WrappingLayout setGap(int gap) {
		this.gap = gap;
		return this;
	}

	public void remove(Element child) {
	}

	public void constrain(Element comp, Object constraints) {
	}

	public Vector2f minimumSize(Element target) {
		return null;
	}

	public Vector2f maximumSize(Element target) {
		return LUtil.DEFAULT_MAX_SIZE;
	}

	class Flow {

		List<List<Element>> flow = new ArrayList<List<Element>>();
		List<List<Vector2f>> sizes = new ArrayList<List<Vector2f>>();
		List<Float> rowSize = new ArrayList<Float>();
		final Vector2f off;
		final Vector2f size;
		final int totGap;

		Flow(Element target, Vector2f dimensions) {
			totGap = (Math.max(target.getElements().size() - 1, 0) * gap);
			Vector4f padding = target.getTextPaddingVec();
			Vector2f availableSpace = dimensions == null ? null : dimensions.subtract(padding.y + padding.z, padding.x + padding.w);
			size = new Vector2f();
			float x = 0;
			float rh = 0;
			float y = 0;
			off = new Vector2f(padding.y, padding.x);

			// First, if we are using equal size cells, iterate them all to get
			// the size
			// to use

			// If equal size cells, first get cell size to use
			Vector2f prefSize = null;
			if (equalSizeCells) {
				prefSize = new Vector2f();
				for (Element e : target.getElements()) {
					Vector2f thisSize = LUtil.getBoundPreferredSize(e);
					prefSize.x = Math.max(prefSize.x, thisSize.x);
					prefSize.y = Math.max(prefSize.y, thisSize.y);
				}
			}

			// Next build the flow so we can get row/column height/widths so
			// adjacent
			// cells can be aligned within that space
			List<Element> thisRow = new ArrayList<Element>();
			List<Vector2f> sizeRow = new ArrayList<Vector2f>();
			for (Element e : target.getElements()) {
				Vector2f thisSize = prefSize == null ? LUtil.getBoundPreferredSize(e) : prefSize.clone();
				if (orientation.equals(Element.Orientation.HORIZONTAL)) {
					if (fill && availableSpace != null) {
						thisSize.x = availableSpace.x;
					}
					if (x > 0 && ((width > 0 && thisRow.size() >= width)
							|| (availableSpace != null && thisSize.x + x > availableSpace.x))) {
						flow.add(thisRow);
						sizes.add(sizeRow);
						rh = getRowHeight(sizeRow);
						thisRow = new ArrayList<Element>();
						sizeRow = new ArrayList<Vector2f>();
						rowSize.add(rh);
						size.y += rh + gap;
						x = 0;
						rh = 0;
					}
					thisRow.add(e);
					sizeRow.add(thisSize);
					x += thisSize.x;
					size.x = Math.max(size.x, x);
					x += gap;
				} else {
					if (fill && availableSpace != null) {
						thisSize.y = availableSpace.y;
					}
					if (y > 0 && ((width > 0 && thisRow.size() >= width)
							|| (availableSpace != null && thisSize.y + y > availableSpace.y))) {
						flow.add(thisRow);
						sizes.add(sizeRow);
						rh = getRowWidth(sizeRow);
						thisRow = new ArrayList<Element>();
						sizeRow = new ArrayList<Vector2f>();
						rowSize.add(rh);
						size.x += rh + gap;
						y = 0;
						rh = 0;
					}
					thisRow.add(e);
					sizeRow.add(thisSize);
					y += thisSize.y;
					size.y = Math.max(size.y, y);
					y += gap;
				}
			}
			if (!thisRow.isEmpty()) {
				flow.add(thisRow);
				sizes.add(sizeRow);

				if (orientation.equals(Element.Orientation.HORIZONTAL)) {
					rh = getRowHeight(sizeRow);
					size.y += rh;
				} else {
					rh = getRowWidth(sizeRow);
					size.x += rh;
				}
				rowSize.add(rh);
			}
		}

		@Override
		public String toString() {
			return "Flow{" + "flow=" + flow + ", sizes=" + sizes + ", rowSize=" + rowSize + ", off=" + off + ", size=" + size
					+ ", totGap=" + totGap + '}';
		}

		private float getRowHeight(List<Vector2f> sizeRow) {
			float rh = 0;
			for (Vector2f s : sizeRow) {
				rh = Math.max(rh, s.y);
			}
			return rh;
		}

		private float getRowWidth(List<Vector2f> sizeRow) {
			float rw = 0;
			for (Vector2f s : sizeRow) {
				rw = Math.max(rw, s.x);
			}
			return rw;
		}
	}

	public Vector2f preferredSize(Element target) {
		final Vector2f size = new Flow(target, target.getElementParent() == null ? target.getDimensions()
				: target.getElementParent().getDimensions()).size;
		addBordersAndMargin(size, target);
		return size;
	}

	public void layout(Element target) {
		Flow flow = new Flow(target, target.getDimensions());

		// Now layout each row
		Iterator<Float> heightIt = flow.rowSize.iterator();
		Iterator<List<Vector2f>> sizeIt = flow.sizes.iterator();
		float x = 0;
		float y = 0;
		float rh = 0;
		for (List<Element> l : flow.flow) {
			Iterator<Vector2f> sizes = sizeIt.next().iterator();
			rh = heightIt.next();
			if (orientation.equals(Element.Orientation.HORIZONTAL)) {
				x = 0;
			} else {
				y = 0;
			}
			for (Element e : l) {
				Vector2f sz = sizes.next();
				if (orientation.equals(Element.Orientation.HORIZONTAL)) {
					switch (vAlign) {
					case Top:
						LUtil.setBounds(e, x + flow.off.x, y + flow.off.y, sz.x, sz.y);
						break;
					case Bottom:
						LUtil.setBounds(e, x + flow.off.x, y + flow.off.y + (rh - sz.y), sz.x, sz.y);
						break;
					case Center:
						LUtil.setBounds(e, x + flow.off.x, y + flow.off.y + ((rh - sz.y) / 2), sz.x, sz.y);
						break;
					}
					if (sizes.hasNext()) {
						x += gap;
					}
					x += sz.x;
				} else {
					switch (align) {
					case Left:
						LUtil.setBounds(e, x + flow.off.x, y + flow.off.y, sz.x, sz.y);
						break;
					case Right:
						LUtil.setBounds(e, x + flow.off.x + (rh - sz.x), y + flow.off.y, sz.x, sz.y);
						break;
					case Center:
						LUtil.setBounds(e, x + flow.off.x + ((rh - sz.x) / 2), y + flow.off.y, sz.x, sz.y);
						break;
					}
					if (sizes.hasNext()) {
						y += gap;
					}
					y += sz.y;
				}
			}
			if (orientation.equals(Element.Orientation.HORIZONTAL)) {
				y += rh;
				if (sizeIt.hasNext()) {
					y += gap;
				}
			} else {
				x += rh;
				if (sizeIt.hasNext()) {
					x += gap;
				}
			}
		}

		// if (orientation.equals(Element.Orientation.HORIZONTAL)) {
		// target.setHeight(y);
		// }
	}

	private void addBordersAndMargin(Vector2f minSize, Element target) {
		Vector4f padding = target.getTextPaddingVec();
		minSize.addLocal(padding.x + padding.y, padding.z + padding.w);
	}

	public void setVAlign(BitmapFont.VAlign vAlign) {
		this.vAlign = vAlign;
	}

	public void setAlign(BitmapFont.Align align) {
		this.align = align;
	}

	public VAlign getVAlign() {
		return vAlign;
	}

	public Align getAlign() {
		return align;
	}
}
