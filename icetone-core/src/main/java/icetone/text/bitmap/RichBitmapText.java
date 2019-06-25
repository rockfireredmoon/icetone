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
package icetone.text.bitmap;

import java.util.LinkedList;
import java.util.Objects;

import com.jme3.font.BitmapCharacter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import icetone.core.BaseScreen;
import icetone.core.scene.DefaultSceneElement;
import icetone.core.scene.QuadData;
import icetone.core.scene.TextureRegion;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontSpec;
import icetone.text.TextElement;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class RichBitmapText extends DefaultSceneElement implements TextElement {
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

	protected BitmapFont bitmapFont;

	private Vector2f align = new Vector2f();
	private BitmapCharacter bc, bcSpc;
	private Character c;
	private char[] characters;
	private float lineWidth = 0, lineHeight = 0;
	private FontSpec font;
	private ColorRGBA fontColor = new ColorRGBA(1f, 1f, 1f, 1f);
	private boolean hasLines = false;
	private int imgHeight;
	private int italicSIndex = 0, italicEIndex = 0;
	private Vector4f[] letterPositions;
	private QuadData[] letters;
	private QuadData line;
	private int lineCount = 0;
	private DefaultSceneElement lineDisplay;
	private QuadData[] lines;
	private int lineSIndex = 0;
	private Vector4f margin = new Vector4f();
	private boolean needsRetext = false;
	private boolean needsRewrap = false;
	private boolean needsAlign = false;
	private boolean needsClip = false;
	private BitmapFont originalFont;
	private Vector2f originOffset = new Vector2f(Vector2f.ZERO);
	private boolean parseTags = false;
	private boolean placeWord = false;
	private Vector2f pos = new Vector2f();
	private QuadData qd;
	private float size = 1;
	private float skewSize = 3;
	private LinkedList<RichBitmapText.Tag> tags = new LinkedList<>();
	private String text = "";
	private Align textAlign = Align.Left;
	private Align currentAlign = textAlign;
	private VAlign textVAlign = VAlign.Top;
	private LineWrapMode textWrap = LineWrapMode.NoWrap;
	private ThemeInstance theme;
	private TextureRegion tr;
	private boolean ul = false;
	private float underlineOffset = -3;
	private float underlineSize = 1;
	private int unwrappedLineCount;
	private Node was;
	private int wordSIndex = 0, wordEIndex = 0;
	private float x = 0, y = 0, lnWidth = 0, wordWidth = 0;

	private boolean needsMeshUpdate;

	public RichBitmapText(BaseScreen screen, FontSpec font, ThemeInstance theme) {
		super(screen);
		this.theme = theme;
		this.setScale(1, 1);
		this.setPosition(0, 0);
		this.setOrigin(0, 0);
		lineDisplay = new DefaultSceneElement(screen);
		lineDisplay.setMaterialColor(ColorRGBA.White);
		setFont(font);
		setDimensions(getWidth(), getHeight());
		initialize();
	}

	@Override
	public FontSpec getFont() {
		return font;
	}

	@Override
	public Vector4f[] getLetterPositions() {
		return letterPositions;
	}

	public int getLineCount() {
		return lineCount;
	}

	public DefaultSceneElement getLineDisplay() {
		return this.lineDisplay;
	}

	@Override
	public float getLineWidth() {
		return lineWidth;
	}

	public Vector4f getMargin() {
		return margin;
	}

	public float getSkewSize() {
		return skewSize;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public Align getTextAlign() {
		return this.textAlign;
	}

	public VAlign getTextVAlign() {
		return this.textVAlign;
	}

	public LineWrapMode getTextWrap() {
		return this.textWrap;
	}

	@Override
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

	public int getUnwrappedLineCount() {
		return unwrappedLineCount;
	}

	public boolean isParseTags() {
		return parseTags;
	}

	public int length() {
		return text.length();
	}

	@Override
	public void setDimensions(Vector2f dim) {
		if (!dim.equals(dimensions)) {
			needsRewrap = true;
			super.setDimensions(dim);
		}

	}

	@Override
	public void setFixedLineHeight(float lineHeight) {
		if (lineHeight != this.lineHeight) {
			this.lineHeight = lineHeight;
			needsRewrap = true;
		}
	}

	protected void setFont(BitmapFont font) {

		if (!Objects.equals(this.originalFont, font)) {
			this.originalFont = font;

			this.bitmapFont = new BitmapFont();
			this.bitmapFont.setCharSet(font.getCharSet());
			Material[] pages = new Material[font.getPageSize()];
			for (int i = 0; i < pages.length; i++) {
				pages[i] = font.getPage(i).clone();
			}
			this.bitmapFont.setPages(pages);
			Texture bfTexture = (Texture) this.bitmapFont.getPage(0).getParam("ColorMap").getValue();
			setTexture(bfTexture);
			imgHeight = bfTexture.getImage().getHeight();
			needsRetext = true;
		}
	}

	@Override
	public void setFont(FontSpec font) {
		if (!Objects.equals(font, this.font)) {
			this.font = font;
			BitmapFontInfo info = theme.getFontInfo(font);
			setFont(info.getBitmapFont());
			float fontSize = font.getSize();
			if (fontSize == -1)
				fontSize = bitmapFont.getPreferredSize();
			this.size = fontSize / bitmapFont.getPreferredSize();
			needsRetext = true;
		}
	}

	@Override
	public void setFontColor(ColorRGBA color) {
		for (QuadData quad : quads.values()) {
			quad.setColor(color);
		}
		if(hasLines) {
			for (QuadData quad : lineDisplay.getQuads().values()) {
				quad.setColor(color);
			}
		}
		needsMeshUpdate = true;
		fontColor.set(color);
	}

	public RichBitmapText setMargin(float w, float e, float n, float s) {
		this.margin.set(w, e, n, s);
		needsRewrap = true;
		return this;
	}

	@Override
	public void setMargin(Vector4f offset) {
		this.margin.set(offset);
		needsRewrap = true;
	}

	@Override
	public void setOriginOffset(float x, float y) {
		if (x != this.originOffset.x || y != this.originOffset.y) {
			this.originOffset.set(x, y);
			needsAlign = true;
		}
	}

	public void setParseTags(boolean parseTags) {
		if (parseTags != this.parseTags) {
			this.parseTags = parseTags;
			needsRetext = true;
		}
	}

	public void setSkewSize(float skewSize) {
		if (skewSize != this.skewSize) {
			this.skewSize = skewSize;
			needsRewrap = true;
		}
	}

	@Override
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
	 * Set the text. After the text is set, the line count will either be zero if
	 * the text is empty, or unwrapped line count. If the text contains line
	 * separators (\n), each will be a new line.
	 * 
	 * @param text
	 */
	@Override
	public final void setText(String text) {
		if (!Objects.equals(text, this.text)) {
			this.text = text;
			characters = null;
			needsRetext = true;
		}
	}

	@Override
	public void setTextAlign(Align textAlign) {
		if (!Objects.equals(textAlign, this.textAlign)) {
			this.textAlign = textAlign;
			currentAlign = textAlign;
			needsRewrap = true;
		}
	}

	@Override
	public void setTextVAlign(VAlign textVAlign) {
		if (!Objects.equals(textVAlign, this.textVAlign)) {
			this.textVAlign = textVAlign;
			needsAlign = true;
		}
	}

	@Override
	public void setTextWrap(LineWrapMode textWrap) {
		if (!Objects.equals(textWrap, this.textWrap)) {
			this.textWrap = textWrap;
			needsRewrap = true;
		}
	}

	public void setUnderlineOffset(float underlineOffset) {
		if (underlineOffset != this.underlineOffset) {
			this.underlineOffset = underlineOffset;
			needsRetext = true;
		}
	}

	public void setUnderlineSize(float underlineSize) {
		if (underlineSize != this.underlineSize) {
			this.underlineSize = underlineSize;
			needsRetext = true;
		}
	}

	@Override
	public void updateTextState(boolean force) {
		if (needsRetext || force) {
			needsRetext = false;
			needsRewrap = true;
			lineCount = 0;
			hasLines = false;
			clearQuads(lineDisplay);

			this.uvs.clear();
			this.quads.clear();
			bcSpc = bitmapFont.getCharSet().getCharacter('i');
			lineWidth = 0;
			float thisLineWidth = 0;
			String processedText = stripTags(text);

			lineCount++;
			letterPositions = new Vector4f[processedText.length()];
			letters = new QuadData[processedText.length()];
			lines = new QuadData[processedText.length()];

			for (int i = 0; i < processedText.length(); i++) {
				c = processedText.charAt(i);
				if (c == '\n') {
					lineCount++;
					lineWidth = Math.max(lineWidth, thisLineWidth);
					thisLineWidth = 0;
					letterPositions[i] = new Vector4f(thisLineWidth, 0, 99, 99);
				} else {
					bc = getBitmapChar(c);
					if (bc != null) {
						letterPositions[i] = new Vector4f(thisLineWidth, 0, 99, 99);

						pos.set(lineWidth, bitmapFont.getCharSet().getBase() - bc.getHeight() - bc.getYOffset() * size);
						int bcw = bc.getWidth() + (int) font.getCharacterSpacing();
						align.set(bcw * size / 2, bc.getHeight() * size / 2);

						tr = addTextureRegion(String.valueOf(c.hashCode()), bc.getX(),
								imgHeight - bc.getY() - bc.getHeight(), bcw, bc.getHeight());
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
							lines[i] = line;
						}
						thisLineWidth += bc.getXAdvance() * size;
					}
				}
			}
			lineWidth = Math.max(lineWidth, thisLineWidth);

			setOrigin((getWidth() / 2) + originOffset.x, (getHeight() / 2) + originOffset.y);

			if (processedText.length() > 0) {
				if (was != null) {
					was.attachChild(this);
					was = null;
				}
				mesh.initialize();
				needsMeshUpdate = true;
			} else {
				if (getParent() != null) {
					was = getParent();
					removeFromParent();
				}
			}

			characters = processedText.toCharArray();

			if (hasLines) {
				initElement(lineDisplay);
				needsMeshUpdate = true;
			} else if (lineDisplay.getParent() != null) {
				lineDisplay.removeFromParent();
			}
			unwrappedLineCount = lineCount;
		}

		if (needsRewrap || force) {
			needsRewrap = false;
			needsAlign = true;
			needsClip = true;

			switch (textWrap) {
			case Character:
				wrapTextToCharacter(dimensions.x - margin.x - margin.y);
				break;
			case Word:
				wrapTextToWord(dimensions.x - margin.x - margin.y);
				break;
			case NoWrap:
				wrapTextNoWrap();
				break;
			case Clip:
				wrapTextNoWrap();
				break;
			}
		}

		if (needsAlign)
			alignToBounds();

		if (needsClip) {
			resetClipState();
		}

		if (needsMeshUpdate) {
			needsMeshUpdate = false;
			if (mesh != null) {
				mesh.update(0);
				mesh.updateBound();
			}
			if (lines != null) {
				lineDisplay.getMesh().update(0);
				lineDisplay.update();
			}
		}
	}

	@Override
	protected boolean wantsClip() {
		return (useClip || textWrap == LineWrapMode.Clip) && clippingPosition != null
				&& !clippingPosition.equals(Vector4f.ZERO);
	}

	@Override
	protected void resetClipState() {
		needsClip = false;
		super.resetClipState();
		boolean wantClip = useClip && clippingPosition != null && !clippingPosition.equals(Vector4f.ZERO);
		if (lineDisplay.getParent() != null) {
			if (wantClip) {
				lineDisplay.getMaterial().setVector4("Clipping", clippingPosition);
				lineDisplay.getMaterial().setBoolean("UseClipping", true);
			} else {
				lineDisplay.getMaterial().setBoolean("UseClipping", false);
			}
		}
		if (bitmapFont != null) {
			if (wantClip) {
				for (int i = 0; i < bitmapFont.getPageSize(); i++) {
					bitmapFont.getPage(i).setVector4("Clipping", clippingPosition);
					bitmapFont.getPage(i).setBoolean("UseClipping", true);
				}
			} else {
				for (int i = 0; i < bitmapFont.getPageSize(); i++) {
					bitmapFont.getPage(i).setBoolean("UseClipping", false);
				}
			}
		}
	}

	protected void wrapTextNoWrap() {
		int i = 0;
		x = 0;
		int yoff = (int)(getBase() / 2f);
		y = -yoff * size;
		// y = -getFontOffset();
		lnWidth = 0;
		lineSIndex = 0;
		lineWidth = 0;
		bcSpc = getBitmapChar('i');
		lineCount = 1;
		boolean underline = font.isUnderline();
		letterPositions = new Vector4f[characters.length];

		float charw;
		float lh = getLineHeight();
		for (char c : characters) {
			letterPositions[i] = new Vector4f(lnWidth, (y + yoff) * -1, 0, lh);

			if (c == '\n') {
				lineCount++;
				letterPositions[i].z = bcSpc.getXAdvance() * size;
				letterPositions[i].x = x;
				letterPositions[i].y = (y + yoff) * -1;
				x = 0;
				y -= lh;
				updateLineForAlignment(currentAlign, lineSIndex, i, dimensions.x - margin.x - margin.y, lnWidth);
				lineWidth = Math.max(lnWidth, lineWidth);
				lnWidth = 0;
				lineSIndex = i + 1;

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

					float offset = getBase() * size;
					offset -= (bc.getHeight() * size);
					offset -= (bc.getYOffset() * size);

					// if (c != ' ') {
					QuadData quad = letters[i];
					if (quad != null) {
						if (font.isItalic())
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
									if (lastLine != null && lastLine.getWidth() != 0) {
										if (line.getPositionX() > lastLine.getPositionX()) {
											lastLine.setWidth(line.getPositionX() - lastLine.getPositionX());
										}
									}
								}
							}
						}
					}

					x += charw;
					letterPositions[i].z = charw;
					lnWidth += charw;

//					if (x >= wrapWidth) {
//						break;
//					}
				}
			}
			i++;
		}

		completeWrap(i);
	}

	protected void wrapTextToCharacter(float width) {
		float wrapWidth = width;

		float x = 0;
		int yoff = (int)(getBase() / 2f);
		y = -yoff * size;
		BitmapCharacter bc;
		bcSpc = getBitmapChar('i');
		int i = 0;
		float charw;
		lineWidth = 0;
		lineCount = 1;
		lineSIndex = 0;
		boolean italic = font.isItalic();
		boolean underline = font.isUnderline();
		letterPositions = new Vector4f[characters.length];
		lnWidth = 0;
		float lh = getLineHeight();
		for (char c : characters) {
			letterPositions[i] = new Vector4f(x, (y + yoff) * -1, 0, lh);
			if (c == '\n') {
				lineCount++;
				letterPositions[i].z = bcSpc.getXAdvance() * size;
				letterPositions[i].x = x;
				letterPositions[i].y = (y + yoff) * -1;
				x = 0;
				y -= lh;
				updateLineForAlignment(currentAlign, lineSIndex, i, dimensions.x - margin.x - margin.y, lnWidth);
				lineWidth = Math.max(lnWidth, lineWidth);
				lnWidth = 0;
				lineSIndex = i + 1;
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
					if (x + charw > wrapWidth) {
						updateLineForAlignment(currentAlign, lineSIndex, i, width, lnWidth);
						lineWidth = Math.max(lnWidth, lineWidth);
						lnWidth = 0;
						x = 0;
						y -= lh;
						lineCount++;
						lineSIndex = i;
					}
					qd = letters[i];
					if (qd != null) {
						if (italic) {
							qd.setSkew(skewSize * size, 0);
						}
						qd.setPositionX(processPosition(x));
						qd.setPositionY(processPosition(
								getBase() - bc.getHeight() - bc.getYOffset() + y));

						if (hasLines) {
							line = lines[i];
							line.setPosition(x, y - (underlineOffset * size));
							line.setDimensions(qd.getWidth(), (underlineSize * size < 1) ? 1 : underlineSize * size);
							if (!ul && !underline)
								line.setWidth(0);
							else {
								if (i - 1 > -1) {
									QuadData lastLine = lines[i - 1];
									if (lastLine != null && lastLine.getWidth() != 0) {
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

	protected void wrapTextToWord(float width) {
		// ??
		// float scaled = width * getScale().x;
		// float diff = scaled - width;
		// width -= diff;
		bcSpc = getBitmapChar('i');
		wordSIndex = 0;
		wordEIndex = 0;
		lineSIndex = 0;
		x = 0;
		int yoff = (int)(getBase() / 2f);
		y = -yoff * size;
		lnWidth = 0;
		lineWidth = 0;
		wordWidth = 0;
		placeWord = false;
		int i = 0;
		lineCount = 1;
		boolean italic = font.isItalic();
		boolean underline = font.isUnderline();
		letterPositions = new Vector4f[characters.length];
		float lh = getLineHeight();
		if (characters.length > 0) {
			for (char c : characters) {
				letterPositions[i] = new Vector4f(lnWidth, (y + yoff) * -1, 0, lh);
				if (c == '\n') {
					if (wordWidth > 0)
						placeWord(i, width);
					lineCount++;
					letterPositions[i].z = bcSpc.getXAdvance() * size;
					letterPositions[i].x = x;
					letterPositions[i].y = (y + yoff) * -1;
					x = 0;
					y -= lh;
					updateLineForAlignment(currentAlign, lineSIndex, i, dimensions.x - margin.x - margin.y, lnWidth);
					lineWidth = Math.max(lnWidth, lineWidth);
					lnWidth = 0;
					lineSIndex = i + 1;
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
							float offset = getBase() * size;
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

							if (i + 1 < characters.length) {
								char ch = characters[i + 1];
								if (ch == ' ') {
									placeWord = true;
								}
							} else if (i + 1 >= characters.length) {
								placeWord = true;
							}

							// if (lnWidth + wordWidth > width) {
							// updateLineForAlignment(currentAlign, lineSIndex,
							// wordSIndex, width, lnWidth);
							// }

							if (placeWord) {
								placeWord(i, width);
							}

							if (hasLines) {
								if (i - 1 > -1) {
									QuadData lastLine = lines[i - 1];
									if (lastLine != null && lastLine.getWidth() != 0) {
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
	
	protected float getBase() {
		return Math.min(bitmapFont.getCharSet().getBase(), bitmapFont.getCharSet().getLineHeight());
	}

	protected void clearQuads(DefaultSceneElement el) {
		el.getQuads().clear();
		if (!el.getChildren().isEmpty())
			el.detachAllChildren();
	}

	protected void completeWrap(int i) {
		lineWidth = Math.max(lnWidth, lineWidth);

		updateLineForAlignment(currentAlign, lineSIndex, i, dimensions.x - margin.x - margin.y, lnWidth);

//		Vector2f rotSize = rotation == 0 ? dimensions
//				: MathUtil.rotatedBounds(dimensions, rotation * FastMath.RAD_TO_DEG);
//		setOrigin(rotSize.x / 2, rotSize.y / 2);
//
//		setOrigin(0, 0);
		mesh.update(0);
		mesh.updateBound();
		lineDisplay.update();
		alignToBounds();
	}

	protected BitmapCharacter getBitmapChar(char ch) {
		BitmapCharacter character = bitmapFont.getCharSet().getCharacter(ch);
		if (character == null) {
			character = bitmapFont.getCharSet().getCharacter('?');
			if (character == null) {
				character = bitmapFont.getCharSet().getCharacter(' ');
				if (character == null) {
					character = bitmapFont.getCharSet().getCharacter('X');
					if (character == null) {
						character = bitmapFont.getCharSet().getCharacter(0);
						if (character == null) {
							character = bitmapFont.getCharSet().getCharacter(0xf2b9);
							if (character == null)
								throw new IllegalArgumentException(
										"Cannot find any character or fallback character for " + ch);
						}
					}
				}
			}
		}
		return character;
	}

	protected float getFontOffset() {
		float fontOffset = (bitmapFont.getCharSet().getLineHeight() / 2f) * size;
		return fontOffset;
	}

	protected void initElement(DefaultSceneElement el) {
		el.initialize();
		if (getMaterial().getParam("Clipping") != null) {
			el.getMaterial().setVector4("Clipping", (Vector4f) getMaterial().getParam("Clipping").getValue());
			el.getMaterial().setBoolean("UseClipping", (Boolean) getMaterial().getParam("UseClipping").getValue());
		}
		el.update();
		if (el.getParent() == null)
			attachChild(el);
	}

	private void alignToBounds() {
		float height = dimensions.y;
		float fontOffset = getFontOffset();

		float totalHeight = getTotalHeight();
		switch (textVAlign) {
		case Top:
			setPositionY(processPosition(-fontOffset + height - margin.z));
			if (hasLines)
				lineDisplay.setOriginY(0);
			break;
		case Center:
			setPositionY(processPosition(-fontOffset + ((totalHeight - margin.z + margin.w) / 2f) + (height / 2f)));
			if (hasLines)
				lineDisplay.setOriginY(0);
			break;
		case Bottom:
			switch (textWrap) {
			case NoWrap:
			case Clip:
				setPositionY(processPosition(-fontOffset + totalHeight + margin.w));
				if (hasLines)
					lineDisplay.setOriginY(0);
				break;
			default:
				setPositionY(processPosition(-fontOffset + totalHeight + margin.w));
				if (hasLines)
					lineDisplay.setOriginY(0);
				break;
			}
			break;
		}
		if (characters != null && characters.length != 0) {
			try {
				mesh.update(0);
				mesh.updateBound();
			} catch (IndexOutOfBoundsException ioobe) {
				ioobe.printStackTrace();
			}
		}

		setPositionX(margin.x);
		needsAlign = false;
		lineDisplay.update();
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
		int yoff = (int)(getBase() / 2f);
		if (lnWidth + wordWidth <= width) {
			for (int w = wordSIndex; w <= wordEIndex; w++) {
				QuadData quad = letters[w];
				if (quad != null) {
					quad.setPositionX(processPosition(quad.getPositionX() + lnWidth));
					letterPositions[w].x = quad.getPositionX();
					if (hasLines)
						lines[w].setPositionX(processPosition(quad.getPositionX()));
				}
			}
			lnWidth += wordWidth;
		} else {
			updateLineForAlignment(currentAlign, lineSIndex, wordSIndex, width, lnWidth);

			float lh = getLineHeight();
			y -= lh;
			float off = 0;

			lineSIndex = wordSIndex;
			for (int w = wordSIndex; w <= wordEIndex; w++) {
				QuadData quad = letters[w];
				if (quad != null) {
					if (w == wordSIndex && characters[w] == ' ' && w < characters.length - 1
							&& letters[w + 1] != null) {
						off = -letters[w + 1].getPositionX();
						wordWidth += off;
						continue;
					}
					quad.setPositionX(processPosition(quad.getPositionX() + off));

					letterPositions[w].y = (quad.getPositionY() * -1) + yoff;
					quad.setPositionY(processPosition(quad.getPositionY() - lh));
					letterPositions[w].x = quad.getPositionX();
					if (hasLines)
						lines[w].setPositionY(processPosition(y - (underlineOffset * size)));
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

	private float processPosition(float pos) {
		return Math.round(pos);
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
					RichBitmapText.TagType type = getTagType(tagName);
					RichBitmapText.Tag tag = new RichBitmapText.Tag(sIndex, type);
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
		if (font.isUnderline())
			hasLines = true;
		return text;
	}

	private void updateLineForAlignment(Align textAlign, int head, int tail, float width, float lnWidth) {
		if (tail == letters.length - 1)
			tail = letters.length;

		float diff = width - lnWidth;

		switch (textAlign) {
		case Right:
			for (int xi = head; xi < tail; xi++) {
				if (letters[xi] != null) {
					letters[xi].setPositionX(processPosition(letters[xi].getPositionX() + diff));
					if (hasLines)
						lines[xi].setPositionX(processPosition(lines[xi].getPositionX() + diff));
				}
			}
			break;
		case Center:
			for (int xi = head; xi < tail; xi++) {
				if (letters[xi] != null) {
					letters[xi].setPositionX(processPosition(letters[xi].getPositionX() + (diff / 2f)));
					if (hasLines)
						lines[xi].setPositionX(processPosition(lines[xi].getPositionX() + (diff / 2f)));
				}
			}
			break;
		default:
			break;
		}
	}


	protected float getLineHeight() {
		if (lineHeight == 0)
			return bitmapFont.getCharSet().getLineHeight() * size;
		return lineHeight;
	}
}
