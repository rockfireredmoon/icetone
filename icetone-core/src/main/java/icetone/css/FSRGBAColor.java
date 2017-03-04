/*
 * {{{ header & license
 * Copyright (c) 2007 Wisconsin Court System
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package icetone.css;

import org.xhtmlrenderer.css.parser.FSRGBColor;

public class FSRGBAColor extends FSRGBColor {
	public static final FSRGBAColor TRANSPARENT = new FSRGBAColor(0, 0, 0, 0);
	public static final FSRGBAColor RED = new FSRGBAColor(255, 0, 0, 1);
	public static final FSRGBAColor GREEN = new FSRGBAColor(0, 255, 0, 1);
	public static final FSRGBAColor BLUE = new FSRGBAColor(0, 0, 255, 1);

	private int _alpha;

	public FSRGBAColor(int red, int green, int blue, int alpha) {
		super(red, green, blue);
		if (alpha < 0 || alpha > 255) {
			throw new IllegalArgumentException();
		}
		_alpha = alpha;
	}

	public FSRGBAColor(int color) {
		this(((color & 0xff0000) >> 16), ((color & 0x00ff00) >> 8), color & 0xff, ((color & 0xff0000) >> 24));
	}

	public int getAlpha() {
		return _alpha;
	}

	@Override
	public String toString() {
		return String.format("%02x%02x%02x%02x", getRed(), getGreen(), getBlue(), _alpha);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof FSRGBAColor))
			return false;

		FSRGBAColor that = (FSRGBAColor) o;

		if (getBlue() != that.getBlue())
			return false;
		if (getGreen() != that.getGreen())
			return false;
		if (getRed() != that.getGreen())
			return false;
		if (_alpha != that._alpha)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = getRed();
		result = 31 * result + getGreen();
		result = 31 * result + getBlue();
		result = 31 * result + _alpha;
		return result;
	}

}
