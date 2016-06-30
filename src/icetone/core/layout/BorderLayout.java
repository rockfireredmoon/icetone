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

import icetone.core.Element;
import icetone.core.ElementManager;

public class BorderLayout extends AbstractLayout {

	private int hgap;
	private int vgap;
	private Element north;
	private Element west;
	private Element east;
	private Element south;
	private List<Element> center = new ArrayList<Element>();

	public enum Border {

		NORTH, SOUTH, EAST, WEST, CENTER;
	}

	public BorderLayout() {
		this(0, 0);
	}

	public BorderLayout(int hgap, int vgap) {
		this.hgap = hgap;
		this.vgap = vgap;
	}

	public int getHgap() {
		return hgap;
	}

	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	public int getVgap() {
		return vgap;
	}

	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	public void remove(Element el) {
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

	public void constrain(Element comp, Object constraints) {
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

	public Vector2f minimumSize(Element target) {
		Vector2f dim = new Vector2f(0, 0);

		if (east != null) {
			Vector2f d = LUtil.getMinimumSize(east);
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (west != null) {
			Vector2f d = LUtil.getMinimumSize(west);
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (center != null) {
			Vector2f d = Vector2f.ZERO;
			for (Element c : center) {
				Vector2f s = LUtil.getMinimumSize(c);
				if (s.x > d.x || s.y > d.y) {
					d = s;
				}
			}
			dim.x += d.x;
			dim.y = Math.max(d.y, dim.y);
		}
		if (north != null) {
			Vector2f d = LUtil.getMinimumSize(north);
			dim.x = Math.max(d.x, dim.x);
			dim.y += d.y + vgap;
		}
		if (south != null) {
			Vector2f d = LUtil.getMinimumSize(south);
			dim.x = Math.max(d.x, dim.x);
			dim.y += d.y + vgap;
		}

		Vector4f textPaddingVec = target.getTextPaddingVec();
		dim.x += textPaddingVec.x + textPaddingVec.y;
		dim.y += textPaddingVec.z + textPaddingVec.w;

		return dim;

	}

	public Vector2f maximumSize(Element target) {
		return null;
	}

	public Vector2f preferredSize(Element target) {
		Vector2f dim = new Vector2f(0, 0);

		boolean ltr = false;
		Element c;

		if (center != null) {
			Vector2f d = Vector2f.ZERO;
			for (Element ch : center) {
				Vector2f s = LUtil.getBoundPreferredSize(ch);
				if (s.x > d.x || s.y > d.y) {
					d = s;
				}
			}
			dim.x += d.x;
			dim.y = Math.max(d.y, dim.y);
		}
		if (east != null) {
			Vector2f d = LUtil.getBoundPreferredSize(east);
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (west != null) {
			Vector2f d = LUtil.getBoundPreferredSize(west);
			dim.x += d.x + hgap;
			dim.y = Math.max(d.y, dim.y);
		}
		if (north != null) {
			Vector2f d = LUtil.getBoundPreferredSize(north);
			dim.x = Math.max(d.x, dim.x);
			dim.y += d.y + vgap;
		}
		if (south != null) {
			Vector2f d = LUtil.getBoundPreferredSize(south);
			dim.x = Math.max(d.x, dim.x);
			dim.y += d.y + vgap;
		}

		Vector4f textPaddingVec = target.getTextPaddingVec();
		dim.x += textPaddingVec.x + textPaddingVec.y;
		dim.y += textPaddingVec.z + textPaddingVec.w;

		return dim;
	}

	@Override
	public void layoutScreen(ElementManager screen) {
		doSetBounds(new Vector2f(screen.getWidth(), screen.getHeight()), new Vector2f(0, 0));
	}

	public void layout(Element target) {
		Vector4f textPaddingVec = target.getTextPaddingVec();
		doSetBounds(
				new Vector2f(target.getWidth() - textPaddingVec.x - textPaddingVec.y,
						target.getHeight() - textPaddingVec.w - textPaddingVec.w),
				new Vector2f(textPaddingVec.x, textPaddingVec.z));
	}

	private void doSetBounds(Vector2f size, Vector2f position) {

		if (south != null) {
			Vector2f d = LUtil.getBoundPreferredSize(south);
			LUtil.setBounds(south, position.x, size.y - d.y + position.y, size.x, d.y);
			size.y -= d.y + vgap;
		}
		if (north != null) {
			Vector2f d = LUtil.getBoundPreferredSize(north);
			LUtil.setBounds(north, position.x, position.y, size.x, d.y);
			position.y += d.y;
			size.y -= d.y + vgap;
		}
		if (east != null) {
			Vector2f d = LUtil.getBoundPreferredSize(east);
			LUtil.setBounds(east, size.x - d.x + position.x, position.y, d.x, size.y);
			size.x -= d.x - hgap;
		}
		if (west != null) {
			Vector2f d = LUtil.getBoundPreferredSize(west);
			LUtil.setBounds(west, position.x, position.y, d.x, size.y);
			position.x += d.x + hgap;
			size.x -= d.x + hgap;
		}
		if (center != null) {
			for (Element ch : center) {
				LUtil.setBounds(ch, position.x, position.y, size.x, size.y);
			}
		}
	}
}
