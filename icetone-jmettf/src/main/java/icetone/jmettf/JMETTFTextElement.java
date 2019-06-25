package icetone.jmettf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.atr.jme.font.glyph.Glyph;
import com.atr.jme.font.shape.TrueTypeContainer;
import com.atr.jme.font.util.StringContainer;
import com.atr.jme.font.util.StringContainer.WrapMode;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;

import icetone.core.utils.StringUtil;
import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontSpec;
import icetone.text.TextElement;

public class JMETTFTextElement implements TextElement {
	public static final float DEFAULT_FONT_SIZE = 16f;

	protected TrueTypeContainer spatial;
	protected boolean needsPositionUpdate = true;
	protected boolean needsGetLines = true;
	protected boolean needsSpatialUpdate = true;
	protected boolean needsSizeUpdate = true;
	protected boolean needsClip = true;
	protected boolean needsGeomUpdate = true;
	protected FontSpec font;
	protected JMETTFFontInfo fontInfo;
	protected ColorRGBA color = ColorRGBA.White;
	protected final ThemeInstance theme;

	private final Node parent;
	private StringContainer stringContainer;
	private Rectangle textBox;
	private Vector2f dimensions = new Vector2f();
	private Vector2f position = new Vector2f();
	private String text = "";
	private boolean clip = true;
	private LineWrapMode textWrap = LineWrapMode.NoWrap;
	private VAlign textVAlign = VAlign.Top;
	private Align textAlign = Align.Left;
	private Vector4f margin = new Vector4f();
	private Vector4f clippingBounds = Vector4f.ZERO;
	private Vector4f[] letterPositions;

	private float spaceWidth;

	public JMETTFTextElement(ThemeInstance theme, JMETTFFontInfo fontInfo, FontSpec font, Node parent) {
		this.theme = theme;
		setFontInfo(fontInfo);
		this.font = font;
		this.parent = parent;
	}

	void recreateSpatial() {
		if (spatial != null)
			spatial.removeFromParent();

		spatial = createSpatial();

//		BlurTextBMP bt = new BlurTextBMP(ToolKit.get().getApplication().getAssetManager(),
//				ToolKit.get().getApplication().getRenderManager(), getStringContainer(), 5, 1, 2.5f,
//				new ColorRGBA(0.02f, 0.4f, 1, 1));
//		Texture2D tex = bt.render();		
//		getMaterial().setTexture("Texture", tex);

		if (parent != null)
			parent.attachChild(spatial);
		needsPositionUpdate = true;
		needsClip = true;
	}

	protected TrueTypeContainer createSpatial() {
		return fontInfo.getTrueTypeFont().getFormattedText(getStringContainer(), color);
	}

	void recreateTextBox() {
		Vector2f dimbox = dimensions.clone();
		if (dimbox.x > 4096)
			dimbox.x = 4096;
		if (dimbox.y > 4096)
			dimbox.y = 4096;
		textBox = new Rectangle(0, 0, dimbox.x - margin.x + margin.y, dimbox.y - margin.z - margin.w);
		getStringContainer().setTextBox(textBox);
		needsGetLines = true;
		needsPositionUpdate = true;
		needsClip = true;
	}

	StringContainer getStringContainer() {
		if (stringContainer == null) {
			stringContainer = new StringContainer(fontInfo.getTrueTypeFont(), text);
			stringContainer.setWrapMode(toWrapMode(textWrap));
			stringContainer.setVerticalAlignment(toVerticalAlignment(textVAlign));
			stringContainer.setAlignment(toAlignment(textAlign));
			stringContainer.setKerning((int) font.getCharacterSpacing());
		}
		return stringContainer;
	}

	@Override
	public void setUseClip(boolean clip) {
		if (clip != this.clip) {
			this.clip = clip;
			needsClip = true;
		}
	}

	@Override
	public void setTextWrap(LineWrapMode textWrap) {
		if (!Objects.equals(textWrap, this.textWrap)) {
			this.textWrap = textWrap;
			getStringContainer().setWrapMode(toWrapMode(textWrap));
			needsGetLines = true;
		}
	}

	@Override
	public void setTextVAlign(VAlign textVAlign) {
		if (!Objects.equals(textVAlign, this.textVAlign)) {
			this.textVAlign = textVAlign;
			getStringContainer().setVerticalAlignment(toVerticalAlignment(textVAlign));
			needsGetLines = true;
		}
	}

	@Override
	public void setTextAlign(Align textAlign) {
		if (!Objects.equals(textAlign, this.textAlign)) {
			this.textAlign = textAlign;
			getStringContainer().setAlignment(toAlignment(textAlign));
			needsGetLines = true;
		}
	}

	@Override
	public void setText(String text) {
		if (!Objects.equals(text, this.text)) {
			/*
			 * If the number of lines changes then we definitely need to resize the text box
			 * too
			 */
			int linesBefore = this.text == null ? 0 : StringUtil.count('\n', this.text);
			this.text = text;
			int linesAfter = this.text == null ? 0 : StringUtil.count('\n', this.text);
			if (linesBefore != linesAfter) {
				stringContainer = null;
				needsSizeUpdate = true;
				needsSpatialUpdate = true;
			} else {
				needsGetLines = true;
				getStringContainer().setText(text);
			}
		}
	}

	@Override
	public void setSubStringColor(int i, int length, ColorRGBA fontColor) {
	}

	@Override
	public void setScale(float x, float y) {
	}

	@Override
	public void setRotation(float tr) {
	}

	@Override
	public void setOriginOffset(float x, float y) {
	}

	@Override
	public void setMargin(Vector4f margin) {
		if (!margin.equals(this.margin)) {
			this.margin.set(margin);
			needsSizeUpdate = true;
		}
	}

	@Override
	public void setFixedLineHeight(float f) {
		// Can't support
	}

	@Override
	public void setFontColor(ColorRGBA color) {
		if (!Objects.equals(color, this.color)) {
			this.color = color;
			needsSpatialUpdate = true;
		}
	}

	@Override
	public void setFont(FontSpec font) {
		if (!Objects.equals(font, this.font)) {
			this.font = font;
			if (font.getSize() == -1)
				font = font.deriveFromSize(DEFAULT_FONT_SIZE);
			setFontInfo(theme.getFontInfo(font));
			spaceWidth = -1;
			getStringContainer().setFont(fontInfo.getTrueTypeFont());
			getStringContainer().setKerning((int) font.getCharacterSpacing());
			needsGetLines = true;
			needsSpatialUpdate = true;
		}
	}

	protected float getSpaceWidth() {
		if (spaceWidth == -1) {
			spaceWidth = fontInfo.getLineWidth(" ");
		}
		return spaceWidth;
	}

	protected void setFontInfo(JMETTFFontInfo fontInfo) {
		this.fontInfo = fontInfo;
	}

	@Override
	public void setDimensions(Vector2f dimensions) {
		if (!dimensions.equals(this.dimensions)) {
			this.dimensions.set(dimensions);
			needsSizeUpdate = true;
		}
	}

	@Override
	public void setClippingBounds(Vector4f clippingBounds) {
		if (!clippingBounds.equals(this.clippingBounds)) {
			this.clippingBounds = clippingBounds;
			needsClip = true;
		}
	}

	@Override
	public void updateTextState(boolean force) {

		if (force || needsSizeUpdate) {
			recreateTextBox();
			needsSizeUpdate = false;
		}

		if (force || needsSpatialUpdate) {
			recreateSpatial();
			needsSpatialUpdate = false;
		}

		if (force || needsGetLines) {
			Glyph[][] glyphs = stringContainer.getLines();
			List<Vector4f> pos = new ArrayList<>(text.length());
			float x = 0;
			float y = 0;
			char[] chars = stringContainer.getText().toCharArray();
			int rowIdx = 0;
			int colIdx = 0;
			for(char c : chars) {
				if(c == '\n') {
					pos.add(new Vector4f(x, y, getSpaceWidth(),
							fontInfo.getTrueTypeFont().getScaledLineHeight()));
					x = 0;
					y += fontInfo.getTrueTypeFont().getScaledLineHeight();
					colIdx = 0;
					rowIdx++;
				}
				else {
					if(rowIdx >= glyphs.length)
						break;
					
					Glyph[] row = glyphs[rowIdx];
					Glyph col;
					if(colIdx == row.length) {
						colIdx = 0;
						x = 0;
						y += fontInfo.getTrueTypeFont().getScaledLineHeight();
						rowIdx++;
					}
					if(colIdx >= row.length)
						col = null;
					else 
						col= row[colIdx];

					float w = (col.getXAdvance() + font.getCharacterSpacing()) * fontInfo.getTrueTypeFont().getScale();
					pos.add(new Vector4f(x, y, w, fontInfo.getTrueTypeFont().getScaledLineHeight()));
					x += w;
					colIdx++;
				}
			}
			
			letterPositions = pos.toArray(new Vector4f[0]);
			needsGeomUpdate = true;
			needsGetLines = false;
		}

		if (force || needsGeomUpdate) {
			needsGeomUpdate = false;
			if (spatial != null)
				spatial.updateGeometry();
		}

		if (force || needsPositionUpdate) {
			if (spatial != null) {
				spatial.setLocalTranslation(position.x + margin.x, dimensions.y - margin.z, 0);
			}
			needsPositionUpdate = false;
		}

		if (force || needsClip) {
			needsClip = false;

			boolean wantClip = clip && !clippingBounds.equals(Vector4f.ZERO);
			if (wantClip) {
				getMaterial().setVector4("Clipping", clippingBounds);
				getMaterial().setBoolean("UseClipping", true);
			} else {
				getMaterial().setVector4("Clipping", Vector4f.ZERO);
				getMaterial().setBoolean("UseClipping", false);
			}
		}

//		if (force || needsAlpha) {
//			needsAlpha = false;
//			getMaterial().setFloat("GlobalAlpha", alpha);
//		}
	}

	@Override
	public boolean removeFromParent() {
		return spatial.removeFromParent();
	}

	@Override
	public float getTotalHeight() {
		return getStringContainer().getTextHeight();
	}

	@Override
	public Align getTextAlign() {
		return textAlign;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Node getParent() {
		if (spatial == null)
			recreateSpatial();
		return spatial.getParent();
	}

	@Override
	public Material getMaterial() {
		if (spatial == null)
			recreateSpatial();
		return spatial.getMaterial();
	}

	@Override
	public Vector3f getLocalTranslation() {
		if (spatial == null)
			recreateSpatial();
		return spatial.getLocalTranslation();
	}

	@Override
	public float getLineWidth() {
		return getStringContainer().getTextWidth();
	}

	@Override
	public Vector4f[] getLetterPositions() {
		if (letterPositions == null) {
			needsGetLines = true;
			updateTextState(false);
		}
		return letterPositions;
	}

	@Override
	public Vector2f getDimensions() {
		return dimensions;
	}

	protected StringContainer.VAlign toVerticalAlignment(VAlign textVAlign) {
		switch (textVAlign) {
		case Center:
			return StringContainer.VAlign.Center;
		case Bottom:
			return StringContainer.VAlign.Bottom;
		default:
			return StringContainer.VAlign.Top;
		}
	}

	protected StringContainer.Align toAlignment(Align textAlign) {
		switch (textAlign) {
		case Center:
			return StringContainer.Align.Center;
		case Right:
			return StringContainer.Align.Right;
		default:
			return StringContainer.Align.Left;
		}
	}

	protected WrapMode toWrapMode(LineWrapMode textWrap) {
		switch (textWrap) {
		case Character:
			return WrapMode.Char;
		case Clip:
			/*
			 * TODO hmm... 'Clip' means ellipses to JMETTF. This breaks multiple \n
			 * separated lines in things like TextArea. We need to support CSS text overflow
			 * attributes to do this properly
			 */
			return WrapMode.NoWrap;
		case Word:
			return WrapMode.Word;
		default:
			return WrapMode.NoWrap;
		}
	}

	@Override
	public FontSpec getFont() {
		return font;
	}
}