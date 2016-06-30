/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.windows;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.FillLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import icetone.core.utils.UIDUtil;

/**
 *
 * @author t0neg0d
 */
public class Panel extends Element {

	public Panel() {
		this(Screen.get());
	}

	public Panel(LayoutManager layoutManager) {
		this(Screen.get(), layoutManager);
	}

	public Panel(ElementManager screen, LayoutManager layoutManager) {
		this(screen);
		setLayoutManager(layoutManager);
	}

	public Panel(ElementManager screen, String UID) {
		this(screen, UID, Vector2f.ZERO);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Panel(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Panel(Vector2f position) {
		this(Screen.get(), position);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Panel(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Panel(Vector2f position, Vector2f dimensions) {
		this(Screen.get(), position, dimensions);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Panel(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Panel control
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
	 *            The default image to use for the Panel
	 */
	public Panel(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Panel(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Panel control
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
	public Panel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Panel control
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
	 *            The default image to use for the Panel
	 */
	public Panel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		layoutManager = new FillLayout();

		setIsBringToFrontOnClick(true);
		setIsMovable(true);
		setIsResizable(true);
		setLockToParentBounds(true);
		setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		setTextPadding(screen.getStyle("Window").getVector4f("textPadding"));

		populateEffects("Window");
		addClippingLayer(this);
	}

	@Override
	public void onInitialized() {
		if (getPreferredDimensions() == null && LUtil.LAYOUT_SIZE.equals(getOrgDimensions()))
			sizeToContent();
	}

	@Deprecated
	public void pack(boolean reposition) {
		sizeToContent();
	}

}
