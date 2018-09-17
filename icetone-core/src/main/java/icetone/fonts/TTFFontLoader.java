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
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.font.BitmapCharacter;
import com.jme3.font.BitmapCharacterSet;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

/**
 * 
 * This class loads the font stored in TrueType file format.
 * 
 * @author Marcin Roguski
 * 
 */

public class TTFFontLoader implements AssetLoader {

	private static final int MAX_WIDTH = 2048;

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		if (assetInfo.getKey() instanceof FontKey) {
			Graphics2D graphics = this.getGraphics();
			FontKey fontKey = (FontKey) assetInfo.getKey();
			Font font = this.getFont(assetInfo);
			graphics.setFont(font);
			FontData fontData = this.calculateFontData(graphics, fontKey);
			Texture t = this.prepareTexture(fontData, graphics, fontKey.getColor());
			this.setPagesForFont(assetInfo, fontData.bitmapFont, t);
			return fontData.bitmapFont;
		} else {
			throw new IllegalArgumentException("The given asset key should be of type: " + FontKey.class.getName());
		}
	}

	/**
	 * Creates the AWT graphics object to render the fonts.
	 * 
	 * @return AWT graphics object
	 */
	protected Graphics2D getGraphics() {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		return gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT).createGraphics();
	}

	/**
	 * This method loads AWT type font from the stream indicated by assetInfo.
	 * 
	 * @param assetInfo
	 * 
	 *            the information about font asset
	 * 
	 * @return AWT type font
	 * 
	 * @throws IOException
	 * 
	 *             an exception is thrown if there are problems with the stream
	 * 
	 *             itself or with the font type (it should be TrueType)
	 */
	protected Font getFont(AssetInfo assetInfo) throws IOException {
		try {
			FontKey fontKey = (FontKey) assetInfo.getKey();
			return Font.createFont(fontKey.getType(), assetInfo.openStream()).deriveFont(fontKey.getStyle(),
					fontKey.getSize());
		} catch (FontFormatException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * This method reads all available fonts and prepares the basic data about
	 * its characters.
	 * 
	 * @param graphics
	 *            the graphics object that will later prepare the image of the
	 *            fonts
	 * 
	 * @param fontKey
	 *            the key containing essential font information
	 * 
	 * @return the basic data about the font
	 */

	protected FontData calculateFontData(Graphics2D graphics, FontKey fontKey) {
		Font font = graphics.getFont();
		FontMetrics fontMetrics = graphics.getFontMetrics();
		int glyphsAmount = font.getNumGlyphs(), i = 0;
		int missingGlyphCode = font.getMissingGlyphCode();
		char[] chars = new char[] { 0 };
		FontData result = new FontData();
		result.bitmapFont = new BitmapFont();
		result.bitmapFont.setCharSet(new BitmapCharacterSet());
		result.bitmapFont.getCharSet().setRenderedSize(font.getSize());
		result.charList = new ArrayList<>();
		int y = 0;
		int rowHeight = 0;
		int rowWidth = 0;
		while (i < glyphsAmount) {
			GlyphVector gv = font.createGlyphVector(graphics.getFontRenderContext(), chars);
			int glyphCode = gv.getGlyphCode(0);
			GlyphMetrics gm = gv.getGlyphMetrics(0);
			if (glyphCode != missingGlyphCode && glyphCode < glyphsAmount
					&& result.bitmapFont.getCharSet().getCharacter((int) chars[0]) == null) {

				BitmapCharacter bitmapCharacter = new BitmapCharacter();
				bitmapCharacter.setWidth(Character.isWhitespace(chars[0]) ? (int) Math.ceil(gm.getAdvanceX())
						: (int) Math.ceil(gm.getBounds2D().getWidth()));
				bitmapCharacter.setXAdvance((int) Math.ceil(gm.getAdvanceX()));
				result.bitmapFont.getCharSet().addCharacter((int) chars[0], bitmapCharacter);
				
				rowWidth += bitmapCharacter.getWidth() + (int) Math.ceil(Math.abs(gm.getLSB()));// LSB
																											// only
																											// affects
																											// the
																											// render
																											// image
																											// width
				if(rowWidth > MAX_WIDTH) {
					y += rowHeight;
					rowWidth = 0;
					rowHeight = 0;
				}
				else
					result.imageWidth = Math.max(rowWidth, result.imageWidth);

				rowHeight = (int) Math
						.ceil(Math.max(rowHeight, gv.getGlyphVisualBounds(0).getBounds2D().getHeight()));
				
				result.charList.add(new CharacterData(chars[0], gm.getLSB(), bitmapCharacter, y));
				++i;

			} else if (Character.isWhitespace(chars[0])) {
				++i;
			}

			chars[0] = (char) (chars[0] + 1);
		}
		y += rowHeight;
		result.imageHeight = y;

		int ascentBuffer = this.parseBuffer(fontKey.getAboveAscentBuffer(), result.imageHeight);
		int descentBuffer = this.parseBuffer(fontKey.getAboveAscentBuffer(), result.imageHeight);

		result.imageHeight += ascentBuffer + descentBuffer;
		result.bitmapFont.getCharSet().setBase(fontMetrics.getMaxAscent() + ascentBuffer);// we
																							// move
																							// the
																							// base
																							// down
																							// by
																							// the
																							// value
																							// of
																							// ascentBuffer

		result.bitmapFont.getCharSet()
				.setLineHeight(fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + ascentBuffer + descentBuffer);
		result.bitmapFont.getCharSet().setRenderedSize(font.getSize());
		return result;

	}

	/**
	 * This method parses the buffer value.
	 * 
	 * The value should be a numeric value that ends in '%', 'px' or nothing.
	 * 
	 * 'px' is a default measure unit.
	 * 
	 * @param buffer
	 *            the buffer value to be parsed
	 * 
	 * @param imageHeight
	 *            the height of the image; required if the buffer value
	 * 
	 *            is given in percents
	 * 
	 * @return an integer value of buffer height in pixels
	 * 
	 */
	protected int parseBuffer(String buffer, int imageHeight) {
		if (buffer == null || buffer.isEmpty()) {
			return 0;
		}

		if (buffer.endsWith("%")) {
			String value = buffer.split("\\%")[0];
			float percent = Float.parseFloat(value);
			if (percent != 0.0f) {
				percent = 1.0f / percent;
			}
			return (int) Math.round(imageHeight * percent);
		}
		else if (buffer.endsWith("px")) {
			String value = buffer.split("px")[0];
			return (int) Float.parseFloat(value);
		}
		return 0;
	}

	/**
	 * This method prepares the jme texture for the font. It also sets the rest
	 * 
	 * of data required by font data.
	 * 
	 * @param fontData
	 * 
	 *            the data of the font
	 * 
	 * @param graphics
	 * 
	 *            the object that renders the font
	 * 
	 * @return font's texture
	 * 
	 */

	protected Texture prepareTexture(FontData fontData, Graphics2D graphics, Color fontColor) {

		BufferedImage bufferedImage = graphics.getDeviceConfiguration().createCompatibleImage(fontData.imageWidth,
				fontData.imageHeight, Transparency.TRANSLUCENT);

		// prepare the graphics object to render the font

		Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
		imageGraphics.setFont(graphics.getFont());
		imageGraphics.setColor(fontColor);
		imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// we need to mirror the image vertically (do not know why, but the
		// engine redners it up-side down :smile: )

		AffineTransform at = new AffineTransform();
		at.scale(1.0, -1.0);
		at.translate(0.0, -bufferedImage.getHeight());
		imageGraphics.transform(at);

		// render each font and fill the x and y positions data and its height
		int xPos = 0;
		int base = fontData.bitmapFont.getCharSet().getBase();
		char[] chars = new char[1];
		for (CharacterData characterData : fontData.charList) {
			chars[0] = characterData.character;
			characterData.bitmapCharacter.setXOffset(characterData.lsb);
			characterData.bitmapCharacter.setX(xPos);
			characterData.bitmapCharacter.setY(characterData.y);
			characterData.bitmapCharacter.setHeight(bufferedImage.getHeight());

			// move the char so that when it is rendered it won't hit other
			// characters
			xPos -= characterData.lsb;
			imageGraphics.drawChars(chars, 0, 1, xPos, base + characterData.y);
			xPos += characterData.lsb + characterData.bitmapCharacter.getWidth() + characterData.widthDelta;
			if(xPos >= fontData.imageWidth) {
				xPos = 0;
			}
		}

		// set the size of the bitmap
		fontData.bitmapFont.getCharSet().setHeight(bufferedImage.getHeight());
		fontData.bitmapFont.getCharSet().setWidth(bufferedImage.getWidth());

		// create the texture
		Image image = new AWTLoader().load(bufferedImage, false);
		Texture texture = new Texture2D(image);
		texture.setMagFilter(Texture.MagFilter.Bilinear);
		texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
		return texture;

	}

	/**
	 * 
	 * This method sets the pages for font. For now only one page is supported.
	 * 
	 * @param assetInfo
	 * 
	 *            the information about the font asset
	 * 
	 * @param bitmapFont
	 * 
	 *            the result bitmap font where the data will be stored
	 * 
	 * @param textures
	 * 
	 *            a list of textures to apply to the bitmap font pages (one
	 *            texture
	 * 
	 *            per page)
	 * 
	 */
	protected void setPagesForFont(AssetInfo assetInfo, BitmapFont bitmapFont, Texture... textures) {
		Material[] pages = new Material[textures.length];
		MaterialDef spriteMat = (MaterialDef) assetInfo.getManager()
				.loadAsset(new AssetKey<MaterialDef>("icetone/shaders/Unshaded.j3md"));

		for (int i = 0; i < textures.length; ++i) {
			Material mat = new Material(spriteMat);
			mat.setTexture("ColorMap", textures[i]);
			// mat.setColor("Color", ColorRGBA.White);
			// mat.setBoolean("VertexColor", true);
			mat.getAdditionalRenderState().setBlendMode(BlendMode.AlphaAdditive);
			pages[i] = mat;
		}

		bitmapFont.setPages(pages);
	}

	/**
	 * 
	 * Internal data aggregator class. It stores the result BitmapFont class. It
	 * 
	 * also contains a list of characters data (calculated first and then passed
	 * 
	 * to the method that renders the font texture). It also contains the size
	 * 
	 * of the result image.
	 * 
	 * @author Marcin Roguski
	 * 
	 */

	protected static class FontData {

		public BitmapFont bitmapFont;
		public List<CharacterData> charList;
		public int imageWidth;
		public int imageHeight;

	}

	/**
	 * 
	 * A class containing the data of a single character. The data contained:
	 * 
	 * the character itself, the left (top) side bearing of the glyph (see
	 * 
	 * java.awt.font.GlyphMetrics.getLSB()), the bitmap character that will be
	 * 
	 * stored in the output data
	 * 
	 * @author Marcin Roguski
	 * 
	 */
	protected static class CharacterData {

		public char character;

		public int y;
		
		public int lsb;

		public int widthDelta;

		public BitmapCharacter bitmapCharacter;

		public CharacterData(char character, float lsb, BitmapCharacter bitmapCharacter, int y) {

			this.character = character;
			this.y = y;
			this.lsb = (int) Math.floor(lsb);// that is the distace the
												// character will be moved
												// before rendering

			this.widthDelta = (int) Math.ceil(Math.abs(lsb));// that is width
																// modification
																// caused by lsb

			this.bitmapCharacter = bitmapCharacter;

		}

	}

}
