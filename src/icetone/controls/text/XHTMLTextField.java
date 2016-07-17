/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.text;

import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.listeners.KeyboardListener;
import icetone.listeners.MouseFocusListener;
import icetone.listeners.TabFocusListener;
import icetone.xhtml.TGGUserAgent;

/**
 *
 * @author t0neg0d
 */
public class XHTMLTextField extends XHTMLLabel
		implements Control, KeyboardListener, TabFocusListener, MouseFocusListener, TextInput {

	private Element caret;

	public XHTMLTextField() {
		this("");
	}

	public XHTMLTextField(String text) {
		this(Screen.get());
		setText(text);
	}

	public XHTMLTextField(ElementManager screen, String UID) {
		this(screen, UID, LUtil.LAYOUT_SIZE, screen.getStyle("TextField").getVector4f("resizeBorders"),
				screen.getStyle("TextField").getString("defaultImg"));
	}

	public XHTMLTextField(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), dimensions, resizeBorders, defaultImg);
	}

	public XHTMLTextField(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		this(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public XHTMLTextField(ElementManager screen) {
		this(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, screen.getStyle("TextField").getVector4f("resizeBorders"),
				screen.getStyle("TextField").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public XHTMLTextField(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, screen.getStyle("TextField").getVector2f("defaultSize"),
				screen.getStyle("TextField").getVector4f("resizeBorders"),
				screen.getStyle("TextField").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public XHTMLTextField(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("TextField").getVector4f("resizeBorders"),
				screen.getStyle("TextField").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the TextField control
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
	 *            The default image to use for the TextField
	 */
	public XHTMLTextField(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the TextField control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public XHTMLTextField(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, screen.getStyle("TextField").getVector2f("defaultSize"),
				screen.getStyle("TextField").getVector4f("resizeBorders"),
				screen.getStyle("TextField").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the TextField control
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
	public XHTMLTextField(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("TextField").getVector4f("resizeBorders"),
				screen.getStyle("TextField").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the TextField control
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
	public XHTMLTextField(ElementManager screen, String UID, Vector2f position, Vector2f dimensions,
			Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, new TGGUserAgent(screen));
		caret = new Element(screen);
	}

	@Override
	public Element getCaret() {
		return caret;
	}

	@Override
	public int getMaxLength() {
		// TOD
		return 20;
	}

	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTabFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetTabFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKeyPress(KeyInputEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(float tpf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
		// TODO Auto-generated method stub

	}
}
