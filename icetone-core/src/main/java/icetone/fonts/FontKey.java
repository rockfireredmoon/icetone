/**
 * Original class from JMonkeyEngine forums by Marcin Roguski
 * (https://hub.jmonkeyengine.org/t/truetype-font-loader)
 * Unknown license (BSD assumed)
 * 
 * Additions and fixes for 'ICETONE' GUI
 * 
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
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

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.jme3.asset.AssetKey;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.font.BitmapFont;

import icetone.fonts.FontSpec.FontStyle;

/**
 * 
 * A key to find fonts in asset manager.
 * 
 * @author Marcin Roguski
 * @author rockfire
 */

public class FontKey extends AssetKey<BitmapFont> {

	/**
	 * 
	 * The type of the font. available values: Font.TRUETYPE_FONT,
	 * 
	 * Font.TYPE1_FONT
	 * 
	 */
	protected int type;

	/** The size of the font (should be greater than zero of course). */
	protected int size;

	/** The color of the font. */
	protected Color color;

	/**
	 * 
	 * The style of the font; available values: Font.ITALIC, Font.PLAIN,
	 * 
	 * Font.BOLD.
	 * 
	 */
	protected int style;

	/**
	 * 
	 * The amount of pixels to be added above the ascent line. Available units:
	 * 
	 * %, px.
	 * 
	 */
	protected String aboveAscentBuffer;

	/**
	 * 
	 * The amount of pixels to be added below the descent line. Available units:
	 * 
	 * %, px.
	 * 
	 */
	protected String belowDescentBuffer;

	/**
	 * 
	 * Constructor with the aseet name. The constructor does not validate the
	 * 
	 * given data. If the data is wrong it will arise exceptions during font
	 * 
	 * creation. The aboveAscent and belowDescent buffers are both set to 0.
	 * 
	 * @param fontName
	 * 
	 *            the name of the font asset
	 * 
	 * @param type
	 * 
	 *            the type of the font; available values: Font.TRUETYPE_FONT,
	 * 
	 *            Font.TYPE1_FONT
	 * 
	 * @param color
	 * 
	 *            the color of the font
	 * 
	 * @param size
	 * 
	 *            the size of the font (should be greater than zero of course)
	 * 
	 * @param style
	 * 
	 *            the style of the font; available values: Font.ITALIC,
	 *            Font.PLAIN,
	 * 
	 *            Font.BOLD
	 * 
	 */
	public FontKey(String fontName, int type, Color color, int size, int style) {

		super(fontName);

		this.type = type;
		this.color = color;
		this.size = size;
		this.style = style;
	}

	/**
	 * 
	 * Constructor with the {@link FontSpec}
	 * 
	 * @param fontSpec
	 * 
	 *            the font spec
	 * 
	 */
	public FontKey(FontSpec spec) {

		super(spec.getPath());

		this.type = spec.getType().equals(FontSpec.FontType.TTF) ? Font.TRUETYPE_FONT : Font.TYPE1_FONT;
		this.color = Color.white;
		this.size = (int) spec.getSize();

		List<FontStyle> sty = Arrays.asList(spec.getStyles());
		if (sty.contains(FontStyle.BOLD))
			this.style |= Font.BOLD;
		if (sty.contains(FontStyle.ITALIC))
			this.style |= Font.ITALIC;
	}

	/**
	 * 
	 * This method returns the type of the font.
	 * 
	 * @return the type of the font
	 * 
	 */

	public int getType() {
		return type;
	}

	/**
	 * 
	 * This method returns the color of the font.
	 * 
	 * @return the color of the font
	 * 
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * 
	 * This method returns the size of the font.
	 * 
	 * @return the size of the font
	 * 
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 
	 * This method returns the style of the font.
	 * 
	 * @return the style of the font
	 * 
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * 
	 * This method returns the amount of pixels to be added above the ascent
	 * 
	 * line.
	 * 
	 * @return the amount of pixels to be added above the ascent line
	 * 
	 */
	public String getAboveAscentBuffer() {
		return aboveAscentBuffer;
	}

	/**
	 * 
	 * This method returns the amount of pixels to be added below the descent
	 * 
	 * line.
	 * 
	 * @return the amount of pixels to be added below the descent line
	 * 
	 */
	public String getBelowDescentBuffer() {
		return belowDescentBuffer;
	}

	@Override

	public void write(JmeExporter ex) throws IOException {

		super.write(ex);

		OutputCapsule oc = ex.getCapsule(this);
		oc.write(type, "type", Font.TRUETYPE_FONT);
		oc.write(size, "size", 18);
		oc.write(style, "style", Font.PLAIN);
		oc.write(new float[] { color.getRed(), color.getGreen(), color.getBlue() }, "color", null);

	}

	@Override

	public void read(JmeImporter im) throws IOException {

		super.read(im);

		InputCapsule ic = im.getCapsule(this);

		type = ic.readInt("type", Font.TRUETYPE_FONT);
		size = ic.readInt("size", 18);
		style = ic.readInt("style", Font.PLAIN);

		float[] colorTable = ic.readFloatArray("color", new float[] { 0.0f, 0.0f, 0.0f });
		color = new Color(colorTable[0], colorTable[1], colorTable[2]);

	}

	@Override

	public int hashCode() {

		final int prime = 31;

		int result = super.hashCode();

		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + size;
		result = prime * result + style;
		result = prime * result + type;

		return result;

	}

	@Override

	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!super.equals(obj)) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		FontKey other = (FontKey) obj;

		if (color == null) {
			if (other.color != null) {
				return false;
			}

		} else if (!color.equals(other.color)) {
			return false;
		}

		if (size != other.size) {
			return false;
		}

		if (style != other.style) {
			return false;
		}

		if (type != other.type) {
			return false;
		}

		return true;

	}

	public static boolean isTTF(String fontPath) {
		return fontPath.toLowerCase().endsWith(".ttf");
	}

}