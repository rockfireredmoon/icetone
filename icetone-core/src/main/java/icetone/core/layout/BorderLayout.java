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
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;

public class BorderLayout extends AbstractGenericLayout<ElementContainer<?,?>, Object> {

	private float hgap;
	private float vgap;
	private BaseElement north;
	private BaseElement west;
	private BaseElement east;
	private BaseElement south;
	private List<BaseElement> center = new ArrayList<BaseElement>();

	public BorderLayout() {
		this(Float.MIN_VALUE, Float.MIN_VALUE);
	}

	public BorderLayout(float hgap, float vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	public float getHgap() {
		return hgap;
	}

	public void setHgap(float hgap) {
		this.hgap = hgap;
	}

	public float getVgap() {
		return vgap;
	}

	public void setVgap(float vgap) {
		this.vgap = vgap;
	}

	@Override
	protected void onRemove(BaseElement el) {
		if (el.equals(north)) {
			north = null;
		} else if (el.equals(south)) {
			south = null;
		} else if (el.equals(west)) {
			west = null;
		} else if (el.equals(east)) {
			east = null;
		} else {
			center.remove(el);
		}
	}

	@Override
	protected void onConstrain(BaseElement comp, Object constraints) {
		if (constraints instanceof Border) {
			switch ((Border) constraints) {
			case NORTH:
				north = comp;
				break;
			case SOUTH:
				south = comp;
				break;
			case WEST:
				west = comp;
				break;
			case EAST:
				east = comp;
				break;
			default:
				center.add(comp);
				break;
			}
		} else {
			center.add(comp);
		}
	}
	
	protected float calcHgap(ElementContainer<?,?> target) {
		return hgap == Float.MIN_VALUE ? target.getIndent() : hgap;
	}
	
	protected float calcVgap(ElementContainer<?,?> target) {
		return hgap == Float.MIN_VALUE ? target.getIndent() : hgap;
	}

	@Override
	protected Vector2f calcMinimumSize(ElementContainer<?,?> target) {
		Vector2f dim = new Vector2f(0, 0);
		float hgap = calcHgap(target);
		float vgap = calcVgap(target);

		if (east != null) {
			Vector2f d = east.calcMinimumSize();
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (west != null) {
			Vector2f d = west.calcMinimumSize();
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (center != null) {
			Vector2f d = new Vector2f(0,0);
			for (BaseElement c : center) {
				Vector2f s = c.calcMinimumSize();
				if (s.x > d.x) {
					d.x = s.x;
				}
				if (s.y > d.y) {
					d.y = s.y;
				}
			}
			dim.x += d.x;
			dim.y = Math.max(d.y, dim.y);
		}
		if (north != null) {
			Vector2f d = north.calcMinimumSize();
			dim.x = Math.max(d.x, dim.x);
			dim.y += d.y + vgap;
		}
		if (south != null) {
			Vector2f d = south.calcMinimumSize();
			dim.x = Math.max(d.x, dim.x);
			dim.y += d.y + vgap;
		}

		Vector4f textPaddingVec = target.getAllPadding();
		dim.x += textPaddingVec.x + textPaddingVec.y;
		dim.y += textPaddingVec.z + textPaddingVec.w;

		return dim;

	}

	@Override
	protected Vector2f calcMaximumSize(ElementContainer<?,?> target) {
		return null;
	}

	@Override
	protected Vector2f calcPreferredSize(ElementContainer<?,?> target) {
		Vector2f dim = new Vector2f(0, 0);
		float hgap = calcHgap(target);
		float vgap = calcVgap(target);

		if (center != null) {
			Vector2f d = new Vector2f(0,0);
			for (BaseElement ch : center) {
				Vector2f s = ch.calcPreferredSize();
				if (s.x > d.x) {
					d.x = s.x;
				}
				if (s.y > d.y) {
					d.y = s.y;
				}
			}
			dim.x += d.x;
			dim.y = Math.max(d.y, dim.y);
		}
		if (east != null) {
			Vector2f d = east.calcPreferredSize();
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (west != null) {
			Vector2f d = west.calcPreferredSize();
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (north != null) {
			Vector2f d = north.calcPreferredSize();
			dim.x = Math.max(d.x, dim.x);
			if (center != null || east != null || west != null) 
				dim.y += d.y + vgap;
		}
		if (south != null) {
			Vector2f d = south.calcPreferredSize();
			dim.x = Math.max(d.x, dim.x);
			if (center != null || east != null || west != null || ( north != null && center == null && east == null && west == null))
				dim.y += d.y + vgap;
		}

		Vector4f textPaddingVec = target.getAllPadding();
		dim.x += textPaddingVec.x + textPaddingVec.y;
		dim.y += textPaddingVec.z + textPaddingVec.w;

		return dim;
	}

	@Override
	protected void onLayout(ElementContainer<?,?> target) {
		Vector4f textPaddingVec = target.getAllPadding();
		Vector2f size = target.getDimensions().subtract(target.getTotalPadding());
		Vector2f position = new Vector2f(textPaddingVec.x, textPaddingVec.z);
		float hgap = calcHgap(target);
		float vgap = calcVgap(target);

		if (south != null) {
			Vector2f d = south.calcPreferredSize();
			south.setBounds(position.x, size.y - d.y + position.y, size.x, d.y);
			size.y -= d.y;
			if (center != null || east != null || west != null || ( north != null && center == null && east == null && west == null))
				size.y -= vgap;
		}
		if (north != null) {
			Vector2f d = north.calcPreferredSize();
			north.setBounds(position.x, position.y, size.x, d.y);
			size.y -= d.y;
			position.y += d.y;
			if (center != null || east != null || west != null) {
				size.y -= vgap;
				position.y += vgap;
			}
		}
		if (east != null) {
			Vector2f d = east.calcPreferredSize();
			east.setBounds(size.x - d.x + position.x, position.y, d.x, size.y);
			size.x -= d.x;
			if(center != null || ( west != null && center == null ) )
				size.x -= hgap;
		}
		if (west != null) {
			Vector2f d = west.calcPreferredSize();
			west.setBounds(position.x, position.y, d.x, size.y);
			position.x += d.x;
			size.x -= d.x;
			if(center != null) {
				position.x += hgap;
				size.x -= hgap;
			}
		}
		if (center != null) {
			for (BaseElement ch : center) {
				ch.setBounds(position.x, position.y, size.x, size.y);
			}
		}
	}
}
