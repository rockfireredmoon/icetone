/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.util;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.text.Label;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class ToolTip extends Label {

	/**
	 * Creates a new instance of the Label control
	 * 
	 */
	public ToolTip() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the Label control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ToolTip(ElementManager screen) {
		this(screen, UIDUtil.getUID());
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
	public ToolTip(ElementManager screen, String UID) {
		this(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("ToolTip").getVector4f("resizeBorders"),
				screen.getStyle("ToolTip").getString("defaultImg"));
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
	public ToolTip(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("ToolTip").getVector4f("resizeBorders"),
				screen.getStyle("ToolTip").getString("defaultImg"));
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
	public ToolTip(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		// this.setIsResizable(true);

		// Load default font info
		setFontByKey("ToolTip", "fontName");
		setFontColor(screen.getStyle("ToolTip").getColorRGBA("fontColor"));
		setFontSize(screen.getStyle("ToolTip").getFloat("fontSize"));
		setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("ToolTip").getString("textAlign")));
		setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("ToolTip").getString("textVAlign")));
		setTextWrap(LineWrapMode.valueOf(screen.getStyle("ToolTip").getString("textWrap")));
		setTextPaddingByKey("ToolTip", "textPadding");
		setTextClipPaddingByKey("ToolTip", "textPadding");

		// this.move(0, 0, 20);
		setPriority(ZPriority.TOOLTIP);
//		setAlwaysOnTop(true);
	}

	// public void useBackGroundColor() {
	// this.getElementMaterial().setColor("Color",
	// screen.getStyle("ToolTip").getColorRGBA("bgColor"));
	// this.getElementMaterial().setTexture("ColorMap", null);
	// }
	//
	// @Override
	// public void show() {
	// isVisible = true;
	// isClipped = wasClipped;
	// if ((Boolean) getElementMaterial().getParam("UseClipping").getValue())
	// getElementMaterial().setBoolean("UseClipping", false);
	// }
	//
	// @Override
	// public void hide() {
	// if (isVisible)
	// wasVisible = isVisible;
	// isVisible = false;
	// isClipped = true;
	// clippingBounds.set(0, 0, 0, 0);
	// getElementMaterial().setVector4("Clipping", clippingBounds);
	// if (!(Boolean) getElementMaterial().getParam("UseClipping").getValue())
	// getElementMaterial().setBoolean("UseClipping", true);
	// }

}
