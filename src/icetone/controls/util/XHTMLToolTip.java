/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.util;

import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.PageBox;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.text.XHTMLLabel;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.xhtml.TGGUserAgent;

/**
 *
 * @author t0neg0d
 */
public class XHTMLToolTip extends XHTMLLabel {

	/**
	 * Creates a new instance of the Label control
	 * 
	 */
	public XHTMLToolTip() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the Label control
	 * 
	 * @param text
	 *            tooltip texxt
	 */
	public XHTMLToolTip(String text) {
		this();
		setText(text);
	}

	/**
	 * Creates a new instance of the Label control
	 * 
	 * @param text
	 *            tooltip texxt
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public XHTMLToolTip(String text, ElementManager screen) {
		this(screen, UIDUtil.getUID());
		setText(text);
	}

	/**
	 * Creates a new instance of the Label control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public XHTMLToolTip(ElementManager screen) {
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
	public XHTMLToolTip(ElementManager screen, String UID) {
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
	public XHTMLToolTip(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
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
	public XHTMLToolTip(ElementManager screen, String UID, Vector2f position, Vector2f dimensions,
			Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, new TGGUserAgent(screen));

		// this.setIsResizable(true);

		// Load default font info
		// setFontByKey("ToolTip", "fontName");
		// setFontColor(screen.getStyle("ToolTip").getColorRGBA("fontColor"));
		// setFontSize(screen.getStyle("ToolTip").getFloat("fontSize"));
		// setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("ToolTip").getString("textAlign")));
		// setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("ToolTip").getString("textVAlign")));
		// setTextWrap(LineWrapMode.valueOf(screen.getStyle("ToolTip").getString("textWrap")));
		// setTextPaddingByKey("ToolTip", "textPadding");
		// setTextClipPaddingByKey("ToolTip", "textPadding");

		// this.move(0, 0, 20);
		setPriority(ZPriority.TOOLTIP);
		// setMinDimensions(new Vector2f(300, 1));
		// setAlwaysOnTop(true);
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

	// public Vector2f getPreferredViewportSize() {
	// return new Vector2f(screen.getWidth(), screen.getHeight());
	// }

	protected java.awt.Rectangle getInitialExtents(LayoutContext c) {
		PageBox first = Layer.createPageBox(c, "first");
		return new java.awt.Rectangle(0, 0, first.getContentWidth(c), first.getContentHeight(c));
	}
}
