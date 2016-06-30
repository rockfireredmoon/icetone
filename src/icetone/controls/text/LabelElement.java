/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.text;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class LabelElement extends TextElement {

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public LabelElement(ElementManager screen, String text) {
		this(screen);
		setText(text);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param text
	 *            The label texxt
	 */
	public LabelElement(String text) {
		this(Screen.get(), text);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public LabelElement(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
	}

	/**
	 * Creates a new instance of the TextElement control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public LabelElement(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
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
	public LabelElement(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, Vector4f.ZERO, null);
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
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the TextLabel's background
	 */
	public LabelElement(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
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
	public LabelElement(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
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
	public LabelElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, Vector4f.ZERO, null);
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
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the TextLabel's background
	 */
	public LabelElement(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg,
				screen.getApplication().getAssetManager().loadFont(screen.getStyle("Font").getString("defaultFont")));
		// if (defaultImg == null)
		// setAsContainerOnly();

		setFontSize(screen.getStyle("Label").getFloat("fontSize"));
		setFontColor(screen.getStyle("Label").getColorRGBA("fontColor"));
		setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Label").getString("textAlign")));
		setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Label").getString("textVAlign")));
		setTextWrap(LineWrapMode.valueOf(screen.getStyle("Label").getString("textWrap")));
		setIsResizable(false);
		setIsMovable(false);
		setUseTextClipping(true);
	}

	@Override
	public void onUpdate(float tpf) {
	}

	@Override
	public void onEffectStart() {
	}

	@Override
	public void onEffectStop() {
	}

}
