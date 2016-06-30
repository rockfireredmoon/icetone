/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.text;

import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class Label extends Element {
	public Label() {
		this(Screen.get(), Vector2f.ZERO, LUtil.LAYOUT_SIZE);
	}

	public Label(String text) {
		this(text, Screen.get());
	}

	public Label(String text, ElementManager screen) {
		this(screen);
		setText(text);
	}

	public Label(ElementManager screen) {
		this(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE);
	}

	public Label(ElementManager screen, String uid, String text, String defaultImg) {
		this(screen, uid, defaultImg);
		setText(text);
	}

	public Label(ElementManager screen, String UID) {
		this(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE);
	}

	public Label(ElementManager screen, String UID, String defaultImg) {
		this(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, defaultImg);
	}

	public Label(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
	}

	public Label(ElementManager screen, String UID, Vector2f dimensions) {
		this(screen, UID, Vector2f.ZERO, dimensions);
	}

	public Label(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Label control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Label(ElementManager screen, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, dimensions, screen.getStyle("Label").getVector4f("resizeBorders"),
				screen.getStyle("Label").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Label control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Label(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Label").getVector4f("resizeBorders"),
				screen.getStyle("Label").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Label control
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
	 *            The default image to use for the Label
	 */
	public Label(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Label control
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
	public Label(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("Label").getVector4f("resizeBorders"),
				screen.getStyle("Label").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Label control
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
	public Label(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		// Load default font info
		setFont(screen.getStyle("Font").getString(screen.getStyle("Label").getString("fontName")));
		setFontColor(screen.getStyle("Label").getColorRGBA("fontColor"));
		setFontSize(screen.getStyle("Label").getFloat("fontSize"));
		setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Label").getString("textAlign")));
		setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Label").getString("textVAlign")));
		setTextWrap(LineWrapMode.valueOf(screen.getStyle("Label").getString("textWrap")));
		setTextPadding(screen.getStyle("Label").getFloat("textPadding"));
		setTextClipPadding(screen.getStyle("Label").getFloat("textPadding"));

		setIsResizable(false);

		layoutManager = new LabelLayout();

		setIgnoreMouse(true);
		setMinDimensions(Vector2f.ZERO);
	}

	public List<ClippingDefine> getClippingLayers() {
		return clippingLayers;
	}

	class LabelLayout extends AbstractTextLayout {


	}
}
