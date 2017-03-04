/**
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
package icetone.framework.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapCharacter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import icetone.core.ElementManager;
import icetone.core.utils.MathUtil;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class AnimText extends AnimElement {
	public class Tag {
		public Align align = null;
		public boolean close = false;
		public int index = 0;
		public TagType type;

		public Tag(int index, TagType type) {
			this.index = index;
			this.type = type;
		}
	}

	public static enum TagType {
		Bold, Italic, NewLine, Paragraph, StrikeThrough, Underline
	}

	public enum TextStyle {
		bold, italic, underline
	}

	protected BitmapFont font;
	BitmapCharacter bc, bcSpc;
	AnimElement lineDisplay;
	Vector4f margin = new Vector4f();
	// Temp vars
	private Vector2f align = new Vector2f();
	private Character c;
	private char[] characters;
	private float fadeDuration = 0, fadeCounter = 0, alpha = 1, lineWidth = 0, lineHeight = -1;
	private boolean fadeIn = false;
	private boolean fadeOut = false;
	private ColorRGBA fontColor = new ColorRGBA(1f, 1f, 1f, 1f);

	private float fontSize;
	// Formatting
	private boolean hasLines = false;
	private int imgHeight;
	private int italicSIndex = 0, italicEIndex = 0;
	private Vector4f[] letterPositions;

	private QuadData[] letters;

	private QuadData line;
	private int lineCount = 0;
	private QuadData[] lines;
	private int lineSIndex = 0;
	private boolean parseTags = false;
	private boolean placeWord = false;
	private Vector2f pos = new Vector2f();

	private QuadData qd;
	private float size = 1;
	private float skewSize = 3;
	private LinkedList<AnimText.Tag> tags = new LinkedList<>();

	private String text;
	private Align textAlign = Align.Left;
	private Align currentAlign = textAlign;
	private Set<TextStyle> textStyles = Collections.emptySet();
	private VAlign textVAlign = VAlign.Top;
	private LineWrapMode textWrap = LineWrapMode.NoWrap;
	private TextureRegion tr;
	// TextureRegion trLine;
	private boolean ul = false;
	private float underlineOffset = -3;
	private float underlineSize = 1;
	private Node was;
	private int wordSIndex = 0, wordEIndex = 0;

	private float x = 0, y = 0, lnWidth = 0, wordWidth = 0;
	private int unwrappedLineCount;
	private BitmapFont originalFont;

	public AnimText(ElementManager<?> screen, BitmapFont font) {
		super(screen);
		this.text = "";
		this.setScale(1, 1);
		this.setPosition(0, 0);
		this.setOrigin(0, 0);
		// nl = System.getProperty("line.separator").toCharArray();
		this.fontSize = font.getPreferredSize();
		this.size = 1;

		// Texture tex =
		// assetManager.loadTexture("icetone/style/def/TextField/text_field_x.png");

		lineDisplay = new AnimElement(screen) {
			@Override
			public void animElementUpdate(float tpf) {
			}
		};
		lineDisplay.setMaterialColor(ColorRGBA.White);
		// lineDisplay.setTexture(tex);
		// trLine =
		// lineDisplay.addTextureRegion("trLine", 4, 4, 6, 6);

		setFont(font);
		setText(text);

		setBounds(getWidth(), getHeight());
		initialize();
		// lineDisplay.setDimensions(bounds);
	}

	@Override
	public void animElementUpdate(float tpf) {
		if (fadeIn) {
			fadeCounter += tpf;
			float percent = fadeCounter / fadeDuration;
			if (percent >= 1) {
				percent = 1;
				for (QuadData quad : quads.values())
					quad.setColorA(percent * alpha);
				fadeCounter = 0;
				fadeIn = false;
			} else {
				for (QuadData quad : quads.values()) {
					quad.setColorA(percent * alpha);
					if (percent * alpha <= 0f)
						quad.setColorA(0.01f);
				}
			}
		} else if (fadeOut) {
			fadeCounter += tpf;
			float percent = 1 - (fadeCounter / fadeDuration);
			if (percent <= 0) {
				percent = 0.01f;
				for (QuadData quad : quads.values())
					quad.setColorA(percent * alpha);
				fadeCounter = 0;
				fadeOut = false;
			} else {
				for (QuadData quad : quads.values())
					quad.setColorA(percent * alpha);
			}
		}
	}

	public void fadeTextIn(float duration) {
		fadeIn = true;
		fadeDuration = duration;
	}

	public void fadeTextOut(float duration) {
		fadeOut = true;
		fadeDuration = duration;
	}

	public float getBoundHeight() {
		return ((BoundingBox) this.mesh.getBound()).getYExtent() * 2f;
	}

	public Vector2f getBounds() {
		return this.dimensions;
	}

	public float getBoundsX() {
		return this.dimensions.x;
	}

	public float getBoundsY() {
		return this.dimensions.y;
	}

	public BitmapFont getFont() {
		return this.font;
	}

	public float getFontSize() {
		return fontSize;
	}

	public Vector4f[] getLetterPositions() {
		return letterPositions;
	}

	public int getLineCount() {
		return lineCount;
	}

	public AnimElement getLineDisplay() {
		return this.lineDisplay;
	}

	public float getLineHeight() {
		if (lineHeight == -1)
			return font.getCharSet().getLineHeight() * size;
		return lineHeight;
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public Vector4f getMargin() {
		return margin;
	}

	public QuadData getQuadDataAt(int index) {
		return (QuadData) quads.values().toArray()[index];
	}

	public float getSkewSize() {
		return skewSize;
	}

	public String getText() {
		return this.text;
	}

	public Align getTextAlign() {
		return this.textAlign;
	}

	public Set<TextStyle> getTextStyles() {
		return textStyles;
	}

	public VAlign getTextVAlign() {
		return this.textVAlign;
	}

	public LineWrapMode getTextWrap() {
		return this.textWrap;
	}

	public float getTotalHeight() {
		return getLineHeight() * lineCount;
	}

	public float getTotalWidth() {
		return dimensions.x;
	}

	public float getUnderlineOffset() {
		return underlineOffset;
	}

	public float getUnderlineSize() {
		return underlineSize;
	}

	public boolean isAnimating() {
		return fadeIn || fadeOut;
	}

	public boolean isParseTags() {
		return parseTags;
	}

	public int length() {
		return text.length();
	}

	public void resetFade(float finalAlpha) {
		fadeIn = false;
		fadeOut = false;
		for (QuadData quad : quads.values())
			quad.setColorA(finalAlpha);
	}

	public AnimText setAlpha(float alpha) {
		for (QuadData quad : quads.values())
			quad.setColorA(alpha);
		update(0);
		return this;
	}

	public final void setBounds(float x, float y) {
		this.dimensions.set(x, y);
	}

	public void setBounds(Vector2f dimensions) {
		this.dimensions.set(dimensions);
	}

	@Override
	public void setClippingBounds(float x, float y, float z, float w) {
		super.setClippingBounds(x, y, z, w);
		if (lineDisplay.getParent() != null) {
			lineDisplay.getMaterial().setVector4("Clipping", clippingPosition);
			lineDisplay.getMaterial().setBoolean("UseClipping", true);
		}
	}

	@Override
	public void setClippingBounds(Vector4f clip) {
		if (lineDisplay.getParent() != null) {
			if (clip == null) {
				lineDisplay.getMaterial().setBoolean("UseClipping", false);
			} else {
				lineDisplay.getMaterial().setVector4("Clipping", clip);
				lineDisplay.getMaterial().setBoolean("UseClipping", true);
			}
		}
		super.setClippingBounds(clip);
	}

	public void setFont(BitmapFont font) {

		if (!Objects.equals(this.originalFont, font)) {
			this.originalFont = font;
			
			this.font = new BitmapFont();
			this.font.setCharSet(font.getCharSet());
			Material[] pages = new Material[font.getPageSize()];
			for (int i = 0; i < pages.length; i++) {
				pages[i] = font.getPage(i).clone();
			}
			this.font.setPages(pages);
			
			this.size = this.fontSize / font.getPreferredSize();
			
			Texture bfTexture = (Texture) this.font.getPage(0).getParam("ColorMap").getValue();
			setTexture(bfTexture);
			imgHeight = bfTexture.getImage().getHeight();
		}
	}

	public void setFontColor(ColorRGBA color) {
		for (QuadData quad : quads.values()) {
			quad.setColor(color);
		}
		fontColor.set(color);
		alpha = color.a;
	}

	public void setFontSize(float size) {
		this.fontSize = size;
		this.size = size / font.getPreferredSize();
	}

	public void setLineHeight(float lineHeight) {
		this.lineHeight = lineHeight;
	}

	public AnimText setMargin(float w, float e, float n, float s) {
		this.margin.set(w, e, n, s);
		updateForAlign();
		return this;
	}

	public void setMargin(Vector4f offset) {
		this.margin.set(offset);
		updateForAlign();
		alignToBoundsV();
	}

	public AnimText setMaxAlpha(float alpha) {
		this.alpha = alpha;
		return this;
	}

	public void setParseTags(boolean parseTags) {
		this.parseTags = parseTags;
		textStyles = Collections.emptySet();
		setText(getText());
	}

	public void setSkewSize(float skewSize) {
		this.skewSize = skewSize;
		setText(getText());
	}

	public void setSubStringColor(int startIndex, int endIndex, ColorRGBA color) {
		for (int i = startIndex; i < endIndex; i++) {
			if (letters[i] != null)
				letters[i].setColor(color);
		}
	}

	public void setSubStringColor(String subString, ColorRGBA color, boolean allInstances, int... whichInstances) {
		String temp = text;
		int lastIndex = 0;
		int sIndex = 1;
		int count = 1;
		while (sIndex != -1) {
			sIndex = temp.indexOf(subString);
			if (sIndex != -1) {
				int eIndex = sIndex + subString.length();
				boolean valid = false;
				if (!allInstances) {
					for (int c : whichInstances) {
						if (c == count) {
							valid = true;
							break;
						}
					}
					if (valid) {
						for (int i = sIndex + lastIndex; i < eIndex + lastIndex; i++) {
							if (letters[i] != null)
								letters[i].setColor(color);
						}
					}
					count++;
				} else {
					for (int i = sIndex + lastIndex; i < eIndex + lastIndex; i++) {
						letters[i].setColor(color);
					}
				}
				lastIndex += eIndex;
				temp = temp.substring(eIndex, temp.length());
			}
		}
	}

	/**
	 * Set the text. After the text is set, the line count will either be zero
	 * if the text is empty, or unwrapped line count. If the text contains line
	 * separators (\n), each will be a new line.
	 * 
	 * @param text
	 */
	public final void setText(String text) {

		lineCount = 0;
		hasLines = false;
		clearQuads(lineDisplay);

		this.uvs.clear();
		this.quads.clear();
		bcSpc = font.getCharSet().getCharacter('i');
		lineWidth = 0;
		float thisLineWidth = 0;

		text = stripTags(text);

		lineCount++;
		letterPositions = new Vector4f[text.length()];
		letters = new QuadData[text.length()];

		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			if (c == '\n') {
				lineCount++;
				lineWidth = Math.max(lineWidth, thisLineWidth);
				thisLineWidth = 0;
				letterPositions[i] = new Vector4f(thisLineWidth, 0, 99, 99);
			} else {
				bc = getBitmapChar(c);
				if (bc != null) {
					letterPositions[i] = new Vector4f(thisLineWidth, 0, 99, 99);

					pos.set(lineWidth, font.getCharSet().getBase() - bc.getHeight() - bc.getYOffset() * size);
					align.set(bc.getWidth() * size / 2, bc.getHeight() * size / 2);

					tr = addTextureRegion(String.valueOf(c.hashCode()), bc.getX(),
							imgHeight - bc.getY() - bc.getHeight(), bc.getWidth(), bc.getHeight());
					tr.flip(false, true);
					qd = addQuad(String.valueOf(i), String.valueOf(c.hashCode()), pos, align);
					letters[i] = qd;
					qd.setDimensions(qd.getTextureRegion().getRegionWidth() * size,
							qd.getTextureRegion().getRegionHeight() * size);
					qd.setColor(fontColor);
					qd.userIndex = i;
					if (hasLines) {
						line = lineDisplay.addQuad(String.valueOf(i), qd.getDimensions(), pos, align);
						line.setDimensions(bc.getXAdvance() * size,
								(underlineSize * size < 1) ? 1 : underlineSize * size);
						line.setColor(fontColor);
					}
					thisLineWidth += bc.getXAdvance() * size;
				}
			}
		}
		lineWidth = Math.max(lineWidth, thisLineWidth);

		setOrigin(getWidth() / 2, getHeight() / 2);

		if (text.length() > 0) {
			if (was != null) {
				was.attachChild(this);
				was = null;
			}
			mesh.initialize();
			mesh.update(0);
			mesh.updateBound();
		} else {
			if (getParent() != null) {
				was = getParent();
				removeFromParent();
			}
		}

		characters = text.toCharArray();

		this.text = text;

		if (hasLines) {
			initElement(lineDisplay);
		} else if (lineDisplay.getParent() != null) {
			lineDisplay.removeFromParent();
		}
		lines = lineDisplay.getQuads().values().toArray(new QuadData[0]);
		unwrappedLineCount = lineCount;
	}

	public int getUnwrappedLineCount() {
		return unwrappedLineCount;
	}

	protected BitmapCharacter getBitmapChar(char ch) {
		BitmapCharacter character = font.getCharSet().getCharacter(ch);
		if (character == null) {
			character = font.getCharSet().getCharacter('?');
			if (character == null) {
				character = font.getCharSet().getCharacter(' ');
				if (character == null) {
					character = font.getCharSet().getCharacter('X');
					if (character == null) {
						throw new IllegalArgumentException("Cannot find any character or fallback character for " + ch);
					}
				}
			}
		}
		return character;
	}

	public void setTextAlign(Align textAlign) {
		this.textAlign = textAlign;
		currentAlign = textAlign;
		rewrap();
	}

	public void setTextStyles(Set<TextStyle> textStyles) {
		this.textStyles = textStyles;
		parseTags = false;
		setText(getText());
	}

	public void setTextVAlign(VAlign textVAlign) {
		this.textVAlign = textVAlign;
		if (textVAlign != null) {
			alignToBoundsV();
		}
	}

	public void setTextWrap(LineWrapMode textWrap) {
		if (!Objects.equals(textWrap, this.textWrap)) {
			this.textWrap = textWrap;
			rewrap();
		}
	}

	public void setUnderlineOffset(float underlineOffset) {
		this.underlineOffset = underlineOffset;
		setText(getText());
	}

	public void setUnderlineSize(float underlineSize) {
		this.underlineSize = underlineSize;
		setText(getText());
	}

	public void wrapTextNoWrap() {
		int i = 0;
		x = 0;
		int yoff = font.getCharSet().getBase() / 2;
		y = -yoff * size;
		// y = -getFontOffset();
		lnWidth = 0;
		lineWidth = 0;
		bcSpc = getBitmapChar('i');
		lineCount = 1;
		boolean underline = textStyles.contains(TextStyle.underline);
		letterPositions = new Vector4f[text.length()];

		for (char c : characters) {
			letterPositions[i] = new Vector4f(lnWidth, (y + yoff) * -1, 0, getLineHeight());

			if (c == '\n') {
				lineCount++;
				x = 0;
				y -= getLineHeight();
				letterPositions[i].z = bcSpc.getXAdvance() * size;
				letterPositions[i].x = x;
				letterPositions[i].y = (y + yoff) * -1;
				lineWidth = Math.max(lnWidth, lineWidth);
				lnWidth = 0;

			} else {
				bc = getBitmapChar(c);

				if (bc != null) {
					for (Tag t : tags) {
						if (t.index == i && t.type != null) {
							switch (t.type) {
							case Italic:
								if (t.close) {
									italicEIndex = i;
									formatItalic();
								} else {
									italicSIndex = i;
								}
								break;
							case Underline:
								if (t.close) {
									ul = false;
								} else {
									ul = true;
								}
								break;
							default:
								break;
							}
						}
					}

					float offset = font.getCharSet().getBase() * size;
					offset -= (bc.getHeight() * size);
					offset -= (bc.getYOffset() * size);

					// if (c != ' ') {
					QuadData quad = letters[i];
					if (quad != null) {
						if (textStyles.contains(TextStyle.italic))
							quad.setSkew(skewSize * size, 0);
						quad.setPosition(x, offset + y);
						quad.setDimensions(quad.getTextureRegion().getRegionWidth() * size,
								quad.getTextureRegion().getRegionHeight() * size);

						if (hasLines) {
							line = lines[i];
							line.setPosition(x, y - (underlineOffset * size));
							line.setDimensions(quad.getWidth(), (underlineSize * size < 1) ? 1 : underlineSize * size);
							if (!ul && !underline)
								line.setWidth(0);
							else {
								if (i - 1 > -1) {
									QuadData lastLine = lines[i - 1];
									if (lastLine.getWidth() != 0) {
										if (line.getPositionX() > lastLine.getPositionX()) {
											lastLine.setWidth(line.getPositionX() - lastLine.getPositionX());
										}
									}
								}
							}
						}
					}

					x += bc.getXAdvance() * size;
					letterPositions[i].z = bc.getXAdvance() * size;
					lnWidth += bc.getXAdvance() * size;
				}
			}
			i++;
		}

		completeWrap(i);
	}

	public void wrapTextToCharacter(float width) {
		float scaled = width * getScale().x;
		float diff = scaled - width;
		width -= diff;
		int lineIndex = 0;
		float x = 0;
		int yoff = font.getCharSet().getBase() / 2;
		y = -yoff * size;
		BitmapCharacter bc;
		bcSpc = getBitmapChar('i');
		int i = 0;
		float charw;
		lineWidth = 0;
		lineCount = 1;
		boolean italic = textStyles.contains(TextStyle.italic);
		boolean underline = textStyles.contains(TextStyle.underline);
		letterPositions = new Vector4f[text.length()];
		for (char c : characters) {
			letterPositions[i] = new Vector4f(x, (y + yoff) * -1, 0, getLineHeight());
			if (c == '\n') {
				lineCount++;
				x = 0;
				y -= getLineHeight();
				letterPositions[i].z = bcSpc.getXAdvance() * size;
				letterPositions[i].x = x;
				letterPositions[i].y = (y + yoff) * -1;
				lineWidth = Math.max(lnWidth, lineWidth);
				lnWidth = 0;
			} else {
				bc = getBitmapChar(c);
				if (bc != null) {

					for (Tag t : tags) {
						if (t.index == i && t.type != null) {
							switch (t.type) {
							case Italic:
								if (t.close) {
									italicEIndex = i;
									formatItalic();
								} else {
									italicSIndex = i;
								}
								break;
							case Underline:
								if (t.close) {
									ul = false;
								} else {
									ul = true;
								}
								break;
							default:
								break;
							}
						}
					}

					charw = bc.getXAdvance() * size;
					if (x + charw > width) {
						updateLineForAlignment(lineIndex, i, lnWidth);
						lineWidth = Math.max(lnWidth, lineWidth);
						lnWidth = 0;
						x = 0;
						y -= getLineHeight();
						lineCount++;
					}
					qd = letters[i];
					if (qd != null) {
						if (italic) {
							qd.setSkew(skewSize * size, 0);
						}
						qd.setPositionX(x);
						qd.setPositionY(font.getCharSet().getBase() - bc.getHeight() - bc.getYOffset() + y);

						if (hasLines) {
							line = lines[i];
							line.setPosition(x, y - (underlineOffset * size));
							line.setDimensions(qd.getWidth(), (underlineSize * size < 1) ? 1 : underlineSize * size);
							if (!ul && !underline)
								line.setWidth(0);
							else {
								if (i - 1 > -1) {
									QuadData lastLine = lines[i - 1];
									if (lastLine.getWidth() != 0) {
										if (line.getPositionX() > lastLine.getPositionX()) {
											lastLine.setWidth(line.getPositionX() - lastLine.getPositionX());
										}
									}
								}
							}
						}

						lnWidth += charw;
						letterPositions[i].z = charw;
						x += charw;
					}
				}
			}
			i++;
		}

		completeWrap(i);
	}

	public void wrapTextToWord(float width) {
		bcSpc = getBitmapChar('i');
		wordSIndex = 0;
		wordEIndex = 0;
		lineSIndex = 0;
		x = 0;
		int yoff = font.getCharSet().getBase() / 2;
		y = -yoff * size;
		lnWidth = 0;
		lineWidth = 0;
		wordWidth = 0;
		placeWord = false;
		int i = 0;
		lineCount = 1;
		boolean italic = textStyles.contains(TextStyle.italic);
		boolean underline = textStyles.contains(TextStyle.underline);
		letterPositions = new Vector4f[text.length()];
		if (characters.length > 0) {
			for (char c : characters) {
				letterPositions[i] = new Vector4f(lnWidth, (y + yoff) * -1, 0, getLineHeight());
				if (c == '\n') {
					if (wordWidth > 0)
						placeWord(i, width);
					lineCount++;
					x = 0;
					y -= getLineHeight();
					letterPositions[i].z = bcSpc.getXAdvance() * size;
					letterPositions[i].x = x;
					letterPositions[i].y = (y + yoff) * -1;
					lineWidth = Math.max(lnWidth, lineWidth);
					lnWidth = 0;
				} else {
					bc = getBitmapChar(c);
					if (bc != null) {
						wordEIndex = i;
						for (Tag t : tags) {
							if (t.index == i) {
								switch (t.type) {
								case NewLine:
									formatNewLine(i, width);
									break;
								case Paragraph:
									formatParagraph(i, t, width);
									break;
								case Italic:
									if (t.close) {
										italicEIndex = i;
										formatItalic();
									} else {
										italicSIndex = i;
									}
									break;
								case Underline:
									if (t.close) {
										ul = false;
									} else {
										ul = true;
									}
									break;
								default:
									break;
								}
							}
						}
						QuadData quad = letters[i];
						if (quad != null) {
							if (italic) {
								quad.setSkew(skewSize * size, 0);
							}
							float offset = font.getCharSet().getBase() * size;
							offset -= (bc.getHeight() * size);
							offset -= (bc.getYOffset() * size);
							quad.setPosition(x, offset + y);
							quad.setDimensions(quad.getTextureRegion().getRegionWidth() * size,
									quad.getTextureRegion().getRegionHeight() * size);
							quad.setOrigin(quad.getWidth() / 2, quad.getHeight() / 2);

							if (hasLines) {
								line = lines[i];
								line.setPosition(x, y - (underlineOffset * size));
								line.setDimensions(quad.getWidth(),
										(underlineSize * size < 1) ? 1 : underlineSize * size);
								if (!underline && !ul)
									line.setWidth(0);
							}

							x += bc.getXAdvance() * size;
							letterPositions[i].z = bc.getXAdvance() * size;
							wordWidth += bc.getXAdvance() * size;

							if (i + 1 < text.length()) {
								char ch = text.charAt(i + 1);
								if (ch == ' ') {
									placeWord = true;
								}
							} else if (i + 1 >= text.length()) {
								placeWord = true;
							}
							if (placeWord) {
								placeWord(i, width);
							}

							if (hasLines) {
								if (i - 1 > -1) {
									QuadData lastLine = lines[i - 1];
									if (lastLine.getWidth() != 0) {
										if (line.getPositionX() > lastLine.getPositionX()) {
											lastLine.setWidth(line.getPositionX() - lastLine.getPositionX());
										}
									}
								}
							}
						}

					}
				}
				i++;
			}
			wordEIndex = letters.length - 1;
			for (Tag t : tags) {
				if (t.index == i) {
					switch (t.type) {
					case NewLine:
						formatNewLine(i, width);
						break;
					case Paragraph:
						formatParagraph(i, t, width);
						break;
					case Italic:
						if (t.close) {
							italicEIndex = i;
							formatItalic();
						} else {
							italicSIndex = i;
						}
						break;
					default:
						break;
					}
				}
			}
		}

		completeWrap(i);
	}

	protected void clearQuads(AnimElement el) {
		el.getQuads().clear();
		if (!el.getChildren().isEmpty())
			el.detachAllChildren();
	}

	protected void completeWrap(int i) {
		lineWidth = Math.max(lnWidth, lineWidth);

		updateLineForAlignment(0, i, lineWidth);
		updateForAlign();

		Vector2f rotSize = rotation == 0 ? dimensions
				: MathUtil.rotatedBounds(dimensions, rotation * FastMath.RAD_TO_DEG);
		setOrigin(rotSize.x / 2, rotSize.y / 2);

		setOrigin(0, 0);
		mesh.update(0);
		mesh.updateBound();
		lineDisplay.update(0);
		if (textVAlign != null) {
			alignToBoundsV();
		}
	}

	protected float getFontOffset() {
		float fontOffset = (font.getCharSet().getLineHeight() / 2f) * size;
		return fontOffset;
	}

	protected void initElement(AnimElement el) {
		el.initialize();
		if (getMaterial().getParam("Clipping") != null) {
			el.getMaterial().setVector4("Clipping", (Vector4f) getMaterial().getParam("Clipping").getValue());
			el.getMaterial().setBoolean("UseClipping", (Boolean) getMaterial().getParam("UseClipping").getValue());
		}
		el.update(0);
		if (el.getParent() == null)
			attachChild(el);
	}

	public void rewrap() {
		switch (textWrap) {
		case Character:
			wrapTextToCharacter(dimensions.x);
			break;
		case Word:
			wrapTextToWord(dimensions.x);
			break;
		case NoWrap:
			wrapTextNoWrap();
			break;
		case Clip:
			wrapTextNoWrap();
			break;
		}
	}

	private void alignToBoundsV() {
		float height = dimensions.y;
		float fontOffset = getFontOffset();

		float totalHeight = getTotalHeight();
		switch (textVAlign) {
		case Top:
			setPositionY(-fontOffset + height - margin.z);
			if (hasLines)
				lineDisplay.setOriginY(0);
			break;
		case Center:
			setPositionY(Math.round(-fontOffset + ((totalHeight - margin.z + margin.w) / 2f) + (height / 2f)));
			if (hasLines)
				lineDisplay.setOriginY(0);
			break;
		case Bottom:
			switch (textWrap) {
			case NoWrap:
			case Clip:
				setPositionY(-fontOffset + totalHeight + margin.w);
				if (hasLines)
					lineDisplay.setOriginY(0);
				break;
			default:
				setPositionY(-fontOffset + totalHeight + margin.w);
				if (hasLines)
					lineDisplay.setOriginY(0);
				break;
			}
			break;
		}
		if (this.text != null && !this.text.equals("")) {
			try {
				mesh.update(0);
				mesh.updateBound();
			} catch (IndexOutOfBoundsException ioobe) {
				ioobe.printStackTrace();
			}
		}
		lineDisplay.update(0);
	}

	private void formatItalic() {
		for (int xi = italicSIndex; xi < italicEIndex; xi++) {
			letters[xi].setSkew(skewSize * size, 0);
		}
	}

	private void formatNewLine(int i, float width) {
		placeWord(i, width);
		updateLineForAlignment(currentAlign, lineSIndex, wordEIndex, width, lnWidth);
		lineSIndex = wordEIndex;
		lnWidth = wordWidth;
		x = 0;
		wordWidth = 0;
		lineCount++;
		y -= getLineHeight();
	}

	private void formatParagraph(int i, Tag t, float width) {
		placeWord(i, width);
		updateLineForAlignment(currentAlign, lineSIndex, wordEIndex, width, lnWidth);
		lineSIndex = wordEIndex;
		x = 0;
		lnWidth = wordWidth;
		wordWidth = 0;
		y -= getLineHeight() * 2;
		if (!t.close) {
			currentAlign = t.align;
		} else {
			currentAlign = textAlign;
		}
		lineCount += 2;
	}

	private TagType getTagType(String tagName) {
		if (tagName.equals("<u>") || tagName.equals("</u>"))
			return TagType.Underline;
		else if (tagName.equals("<i>") || tagName.equals("</i>"))
			return TagType.Italic;
		else if (tagName.equals("<strike>") || tagName.equals("</strike>"))
			return TagType.StrikeThrough;
		else if (tagName.indexOf("<b>") != -1 || tagName.indexOf("</b>") != -1)
			return TagType.Bold;
		else if (tagName.indexOf("</br>") != -1 || tagName.indexOf("<br>") != -1 || tagName.indexOf("<br/>") != -1)
			return TagType.NewLine;
		else if (tagName.indexOf("<p") != -1 || tagName.indexOf("</p>") != -1)
			return TagType.Paragraph;
		else
			return null;
	}

	private void placeWord(int i, float width) {
		int yoff = font.getCharSet().getBase() / 2;
		if (lnWidth + wordWidth <= width) {
			for (int w = wordSIndex; w <= wordEIndex; w++) {
				QuadData quad = letters[w];
				if (quad != null) {
					quad.setPositionX(quad.getPositionX() + lnWidth);
					letterPositions[w].x = quad.getPositionX();
					if (hasLines)
						lines[w].setPositionX(quad.getPositionX());
				}
			}
			lnWidth += wordWidth;
		} else {
			updateLineForAlignment(currentAlign, lineSIndex, wordSIndex, width, lnWidth);

			y -= getLineHeight();
			float xoff = 0;

			for (int w = wordSIndex; w <= wordEIndex; w++) {
				QuadData quad = letters[w];
				if (quad != null) {
					if (w == wordSIndex && text.charAt(w) == ' ')
						xoff = -quad.getDimensions().x;
					else
						quad.setPositionX(quad.getPositionX() + xoff);

					letterPositions[w].y = (quad.getPositionY() * -1) + yoff;
					quad.setPositionY(quad.getPositionY() - (getLineHeight()));
					letterPositions[w].x = quad.getPositionX();
					if (hasLines)
						lines[w].setPositionY(y - (underlineOffset * size));
				}
			}

			lnWidth = wordWidth;
			lineCount++;
		}
		x = 0;
		wordSIndex = i + 1;
		wordWidth = 0;
		placeWord = false;
	}

	private String stripTags(String text) {
		tags.clear();
		if (parseTags) {
			int sIndex = 0, eIndex = 0;

			sIndex = text.indexOf("<");
			while (sIndex > -1) {
				eIndex = text.indexOf(">");
				if (eIndex > -1) {
					String tagName = text.substring(sIndex, eIndex + 1);
					AnimText.TagType type = getTagType(tagName);
					AnimText.Tag tag = new AnimText.Tag(sIndex, type);
					if (tagName.indexOf("</") != -1)
						tag.close = true;
					if (type != null) {
						switch (type) {
						case Paragraph:
							if (tagName.indexOf("align") != -1) {
								tag.align = Align
										.valueOf(tagName.substring(tagName.indexOf("=") + 1, tagName.indexOf(">")));
							}
							break;
						case Underline:
							hasLines = true;
							break;
						default:
							break;
						}
					}

					tags.add(tag);
					text = text.replaceFirst(tagName, "");
				} else {
					break;
				}
				sIndex = text.indexOf("<");
			}
		}
		if (textStyles.contains(TextStyle.underline))
			hasLines = true;
		return text;
	}

	private void updateForAlign() {
		switch (textAlign) {
		case Left:
			setPositionX(margin.x);
			break;
		case Center:
			setPositionX(margin.x + ((dimensions.x - margin.x - margin.y) / 2));
			break;
		case Right:
			setPositionX(dimensions.x - margin.y);
			break;
		}
	}

	private void updateLineForAlignment(Align textAlign, int head, int tail, float width, float lnWidth) {
		if (tail == letters.length - 1)
			tail = letters.length;
		switch (textAlign) {
		case Right:
			for (int xi = head; xi < tail; xi++) {
				letters[xi].setPositionX(letters[xi].getPositionX() + (width - lnWidth));
				if (hasLines)
					lines[xi].setPositionX(lines[xi].getPositionX() + (width - lnWidth));
			}
			break;
		case Center:
			for (int xi = head; xi < tail; xi++) {
				letters[xi].setPositionX(letters[xi].getPositionX() + ((width / 2) - (lnWidth / 2)));
				if (hasLines)
					lines[xi].setPositionX(lines[xi].getPositionX() + ((width / 2) - (lnWidth / 2)));
			}
			break;
		default:
			break;
		}
	}

	private void updateLineForAlignment(int head, int tail, float width) {
		switch (textAlign) {
		case Right:
			for (int xi = head; xi < tail; xi++) {
				if (letters[xi] != null) {
					letters[xi].setPositionX(letters[xi].getPositionX() - width);
					if (hasLines)
						lines[xi].setPositionX(lines[xi].getPositionX() - width);
				}
			}
			break;
		case Center:
			for (int xi = head; xi < tail; xi++) {
				if (letters[xi] != null) {
					letters[xi].setPositionX(letters[xi].getPositionX() - (width / 2));
					if (hasLines)
						lines[xi].setPositionX(lines[xi].getPositionX() - (width / 2));
				}
			}
			break;
		default:
			break;
		}
	}
}
