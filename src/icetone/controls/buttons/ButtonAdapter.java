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
package icetone.controls.buttons;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
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
public class ButtonAdapter extends Button {

	/**
	 * Creates a new instance of the Button control
	 */
	public ButtonAdapter() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ButtonAdapter(ElementManager screen, String text) {
		this(screen, -1, -1, null, text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ButtonAdapter(String text) {
		this(-1, -1, null, text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ButtonAdapter(float iconWidth, float iconHeight, String texturePath) {
		this(iconWidth, iconHeight, texturePath, null);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ButtonAdapter(String texturePath, String text) {
		this(-1, -1, texturePath, text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ButtonAdapter(float iconWidth, float iconHeight, String texturePath, String text) {
		this(Screen.get(), iconWidth, iconHeight, texturePath, text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ButtonAdapter(ElementManager screen, float iconWidth, float iconHeight, String texturePath, String text) {
		this(screen);
		if (texturePath != null)
			setButtonIcon(iconWidth, iconHeight, texturePath);
		if (text != null)
			setText(text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public ButtonAdapter(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("Button").getVector4f("resizeBorders"),
				screen.getStyle("Button").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public ButtonAdapter(ElementManager screen, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, dimensions, screen.getStyle("Button").getVector4f("resizeBorders"),
				screen.getStyle("Button").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public ButtonAdapter(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Button").getVector4f("resizeBorders"),
				screen.getStyle("Button").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Button
	 */
	public ButtonAdapter(ElementManager screen, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Button control
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
	 *            The default image to use for the Button
	 */
	public ButtonAdapter(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public ButtonAdapter(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Button").getVector4f("resizeBorders"),
				screen.getStyle("Button").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Button control
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
	public ButtonAdapter(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("Button").getVector4f("resizeBorders"),
				screen.getStyle("Button").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Button control
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
	 *            The default image to use for the Button
	 */
	public ButtonAdapter(ElementManager screen, String UID, Vector4f resizeBorders, String defaultImg) {
		this(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Button control
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
	 *            The default image to use for the Button
	 */
	public ButtonAdapter(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);
	}

	@Override
	public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
	}

	@Override
	public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {
	}

	@Override
	public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
	}

	@Override
	public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {
	}

	@Override
	public void onButtonFocus(MouseMotionEvent evt) {
	}

	@Override
	public void onButtonLostFocus(MouseMotionEvent evt) {
	}
}
