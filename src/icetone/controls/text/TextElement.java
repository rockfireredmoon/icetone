/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.text;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.framework.core.AnimText;

/**
 *
 * @author t0neg0d
 */
public abstract class TextElement extends Element implements Control {
	AnimText animText;
	String teText = "";
	boolean useTextClipping = false;
	// LineWrapMode wrapMode = LineWrapMode.NoWrap;
	// VAlign vAlign = VAlign.Top;
	// Align hAlign = Align.Left;

	int qdIndex = 0;
	private float lineHeight;

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public TextElement(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, screen.getDefaultGUIFont());
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public TextElement(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, screen.getDefaultGUIFont());
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public TextElement(ElementManager screen, BitmapFont font) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, font);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public TextElement(ElementManager screen, Vector2f position, BitmapFont font) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, font);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public TextElement(ElementManager screen, Vector2f position, Vector2f dimensions, BitmapFont font) {
		this(screen, UIDUtil.getUID(), position, dimensions, Vector4f.ZERO, null, font);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 */
	public TextElement(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			BitmapFont font) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, font);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public TextElement(ElementManager screen, String UID, Vector2f position, BitmapFont font) {
		this(screen, UID, position, screen.getStyle("Label").getVector2f("defaultSize"), Vector4f.ZERO, null, font);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public TextElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, BitmapFont font) {
		this(screen, UID, position, dimensions, Vector4f.ZERO, null, font);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 */
	public TextElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, BitmapFont font) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
		setIsResizable(false);
		setIsMovable(false);
		setIgnoreMouse(true);
		addClippingLayer(this);

		textWrap = LineWrapMode.NoWrap;

		BitmapFont teFont = (font == null) ? getFont() : font;

		animText = new AnimText(screen.getApplication().getAssetManager(), teFont);
		animText.setBounds(dimensions);
		animText.setScale(1, 1);
		animText.setOrigin(0, 0);
		attachChild(animText);

		setLayoutManager(new TextElementLayout());
	}

	public AnimText getAnimText() {
		return this.animText;
	}

	@Override
	public String getText() {
		return this.teText;
	}

	@Override
	public void setText(String text) {
		this.teText = text;
		animText.setText(text);
		animText.setPositionY(getHeight() - animText.getLineHeight());
		setTextWrap(textWrap);
	}

	@Override
	public void controlMoveHook() {
		animText.getMaterial().setVector4("Clipping", getClippingBounds());
		// animText.getMaterial().setBoolean("UseClipping", true);
	}

	@Override
	public void controlResizeHook() {
		if (this.getIsResizable()) {
			// animText.setPositionY(getHeight()-animText.getLineHeight());
			animText.setBounds(getDimensions());
			switch (textWrap) {
			case Character:
				animText.wrapTextToCharacter(getWidth());
				break;
			case Word:
				animText.wrapTextToWord(getWidth());
				break;
			}
			setTextAlign(textAlign);
			setTextVAlign(textVAlign);

			updateClippingLayers();
		}

	}
	// public void updateClippingLayers() {
	// System.err.println("updating clipping " + getClippingBounds() + " for " +
	// getDimensions() + " at " + getPosition());
	// super.updateClippingLayers();
	// }

	public void setLineWrapMode(LineWrapMode textWrap) {
		setTextWrap(textWrap);
	}

	@Override
	public void setTextWrap(LineWrapMode textWrap) {
		this.textWrap = textWrap;
		animText.setTextWrap(textWrap);
		switch (textWrap) {
		case Character:
			animText.wrapTextToCharacter(getWidth());
			break;
		case Word:
			animText.wrapTextToWord(getWidth());
			break;
		case NoWrap:
			animText.wrapTextNoWrap();
			break;
		case Clip:
			animText.wrapTextNoWrap();
			setUseTextClipping(true);
			break;
		}
		animText.update(0);
	}

	public void setFixedLineHeight(float lineHeight) {
		this.lineHeight = lineHeight;
		dirtyLayout(false);
		layoutChildren();
	}

	public float getFixedLineHeight() {
		return lineHeight;
	}

	public void setUseTextClipping(boolean clip) {
		useTextClipping = clip;
		animText.getMaterial().setVector4("Clipping", getClippingBounds());
		animText.getMaterial().setBoolean("UseClipping", clip);
	}

	public void setSize(float size) {
		setFontSize(size);
	}

	@Override
	public float getFontSize() {
		return size;
	}

	float size;

	@Override
	public void setFontSize(float fontSize) {
		this.size = fontSize;
		animText.setFontSize(fontSize);
		setTextWrap(textWrap);
	}

	public void setColor(ColorRGBA color) {
		setFontColor(color);
	}

	@Override
	public void setFontColor(ColorRGBA fontColor) {
		animText.setFontColor(fontColor);
		setTextWrap(textWrap);
	}

	public void setFont(BitmapFont font) {
		this.animText.setFont(font);
		setText(teText);
		setUseTextClipping(useTextClipping);
		setTextVAlign(textVAlign);
	}

	public void setSubStringColor(String subString, ColorRGBA color) {
		this.setSubStringColor(subString, color, false, 1);
	}

	public void setSubStringColor(String subString, ColorRGBA color, boolean allInstances) {
		this.setSubStringColor(subString, color, allInstances, 1);
	}

	public void setSubStringColor(String subString, ColorRGBA color, boolean allInstances, int... whichInstances) {
		animText.setSubStringColor(subString, color, allInstances, whichInstances);
	}

	public void setAlpha(float alpha) {
		animText.setAlpha(alpha);
		animText.update(0);
	}

	public Element setAlignment(Align textAlign) {
		setTextAlign(textAlign);
		return this;
	}

	@Override
	public Element setTextAlign(Align textAlign) {
		this.textAlign = textAlign;
		animText.setTextAlign(textAlign);
		return this;
	}

	public Element setVerticalAlignment(VAlign textVAlign) {
		setTextVAlign(textVAlign);
		return this;
	}

	@Override
	public Element setTextVAlign(VAlign textVAlign) {
		this.textVAlign = textVAlign;
		animText.setTextVAlign(textVAlign);
		return this;
	}

	public void startEffect() {
		onEffectStart();
		this.addControl(this);
	}

	public void stopEffect() {
		this.removeControl(TextElement.class);
		onEffectStop();
		update(0);
	}

	public abstract void onUpdate(float tpf);

	public abstract void onEffectStart();

	public abstract void onEffectStop();

	@Override
	protected void validateClipSettings() {
		super.validateClipSettings();
		/*
		if (this.useTextClipping) {
			if (!clippingLayers.isEmpty()) {
				if (!(Boolean)animText.getMaterial().getParam("UseClipping").getValue())
					animText.getMaterial().setBoolean("UseClipping", true);
			} else {
				if ((Boolean)animText.getMaterial().getParam("UseClipping").getValue())
					animText.getMaterial().setBoolean("UseClipping", false);
			}
			animText.getMaterial().setVector4("Clipping", clippingBounds);
		}
		*/
	}

	@Override
	public void update(float tpf) {
		onUpdate(tpf);
		updateAnimText(tpf);
	}

	private void updateAnimText(float tpf) {
		animText.update(tpf);
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	}

	@Override
	public void updateLocalClippingLayer() {
		super.updateLocalClippingLayer();

		// And replaced with this (resizable is false for me)
		if (animText != null) {
			animText.getMaterial().setVector4("Clipping", getClippingBounds());
			animText.getMaterial().setBoolean("UseClipping", true);
			if (animText.getLineDisplay().getParent() != null) {
				animText.getLineDisplay().getMaterial().setVector4("Clipping", getClippingBounds());
				animText.getLineDisplay().getMaterial().setBoolean("UseClipping", true);
			}
			animText.update(0);
		}
	}

	public void setBox(Rectangle rectangle) {
		// TODO Auto-generated method stub

	}

	public float getLineHeight() {
		return animText.getLineHeight();
	}

	public int getLineCount() {
		return animText.getLineCount();
	}

	public float getLineWidth() {
		return animText.getLineWidth();
	}

	class TextElementLayout extends AbstractLayout {

		@Override
		public Vector2f minimumSize(Element parent) {
			return Vector2f.ZERO;
		}

		@Override
		public Vector2f maximumSize(Element parent) {
			return LUtil.DEFAULT_MAX_SIZE;
		}

		@Override
		public Vector2f preferredSize(Element parent) {
			Vector2f p = new Vector2f();
			p.x = animText.getLineWidth() + parent.getTextPaddingVec().x + parent.getTextPaddingVec().y;
			if (lineHeight == 0) {
				p.y = animText.getBoundHeight() + parent.getTextPaddingVec().z + parent.getTextPaddingVec().w;
			} else {
				p.y = ((lineHeight > 0 ? lineHeight : animText.getLineHeight()) * Math.max(1, animText.getLineCount()))
						+ parent.getTextPaddingVec().z + parent.getTextPaddingVec().w;
			}
			System.out.println("text pref: " + p + " (lh: " + animText.getLineHeight() + " lc : "
					+ animText.getLineCount() + "()");
			return p;
		}

		@Override
		public void layout(Element parent) {
			animText.setOffset(parent.getTextPaddingVec().x, parent.getTextPaddingVec().z - parent.getTextPaddingVec().w);
			animText.setBounds(parent.getWidth() - parent.getTextPaddingVec().x - parent.getTextPaddingVec().y,
					lineHeight > 0 ? lineHeight : parent.getHeight() - parent.getTextPaddingVec().z);
			switch (textWrap) {
			case Character:
				animText.wrapTextToCharacter(animText.getBoundsX());
				break;
			case Word:
				animText.wrapTextToWord(animText.getBoundsX());
				break;
			case NoWrap:
				animText.wrapTextNoWrap();
				break;
			case Clip:
				animText.wrapTextNoWrap();
				setUseTextClipping(true);
				break;
			}
		}

		@Override
		public void constrain(Element child, Object constraints) {
		}

		@Override
		public void remove(Element child) {
		}

	}
}
