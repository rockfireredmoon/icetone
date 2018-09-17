/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2017, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.fonts;

import java.util.Arrays;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;

public class FontSpec {

	public final static FontSpec DEFAULT = new FontSpec();

	public enum FontType {
		BITMAP, TTF, TYPE1;

		public boolean isTTF() {
			switch (this) {
			case TTF:
			case TYPE1:
				return true;
			default:
				return false;
			}
		}
	}

	public enum FontStyle {
		BOLD, ITALIC, UNDERLINE
	}

	private final FontType type;
	private final String family;
	private final String path;
	private final FontStyle[] styles;
	private final float size;

	FontSpec() {
		styles = new FontStyle[0];
		type = FontType.BITMAP;
		path = null;
		family = null;
		size = -1f;
	}

	/**
	 * Build a new @{link {@link FontSpec} for this element based on the current
	 * one and a new path, size and family name.
	 * 
	 * @param fontPath
	 *            asset path
	 * @param family
	 *            family
	 * @param size
	 *            size
	 * @return new spec
	 */
	public FontSpec(String fontPath, String family, float size) {
		this(fontPath, family, size,
				fontPath == null ? FontType.BITMAP : (FontKey.isTTF(fontPath) ? FontType.TTF : FontType.BITMAP));
	}

	public FontSpec(String path, String family, float size, FontType type, FontStyle... styles) {
		this.type = type;
		this.path = path;
		this.family = family;
		this.size = size;
		this.styles = styles;
	}

	public String getFamily() {
		return family;
	}

	public String getPath() {
		return path;
	}

	public FontStyle[] getStyles() {
		return styles;
	}

	public FontType getType() {
		return type;
	}

	public float getSize() {
		return size;
	}

	public BitmapFont load(AssetManager mgr) {
		if (type.isTTF()) {
			return mgr.loadAsset(new FontKey(this));
		} else {
			return mgr.loadFont(path);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + Float.floatToIntBits(size);
		result = prime * result + Arrays.hashCode(styles);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FontSpec other = (FontSpec) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (Float.floatToIntBits(size) != Float.floatToIntBits(other.size))
			return false;
		if (!Arrays.equals(styles, other.styles))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FontSpec [type=" + type + ", family=" + family + ", path=" + path + ", styles="
				+ Arrays.toString(styles) + ", size=" + size + "]";
	}
}
