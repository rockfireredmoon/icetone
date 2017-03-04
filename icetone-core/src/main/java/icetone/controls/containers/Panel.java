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
package icetone.controls.containers;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.ElementManager;
import icetone.core.Layout;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.Element;
import icetone.core.layout.FlowLayout;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class Panel extends Element {

	public Panel() {
		this(BaseScreen.get());
	}

	public Panel(Layout<?, ?> layoutManager) {
		this(BaseScreen.get(), layoutManager);
	}

	public Panel(ElementManager<?> screen, Layout<?, ?> layoutManager) {
		this(screen);
		setLayoutManager(layoutManager);
	}

	public Panel(ElementManager<?> screen, String UID) {
		this(screen, UID, Vector2f.ZERO);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Panel(ElementManager<?> screen) {
		this(screen, Vector2f.ZERO);
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
		this(BaseScreen.get(), position);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Panel(ElementManager<?> screen, Vector2f position) {
		this(screen, null, position, null);
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
	public Panel(Vector2f position, Size dimensions) {
		this(BaseScreen.get(), position, dimensions);
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
	public Panel(ElementManager<?> screen, Vector2f position, Size dimensions) {
		this(screen, null, position, dimensions);
	}


	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param styleId
	 *            ID for CSS and element matching
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Panel(ElementManager<?> screen, String styleId, Vector2f position) {
		this(screen, styleId, position, null);
	}

	/**
	 * Creates a new instance of the Panel control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param styleId
	 *            ID for CSS and element matching
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Panel(ElementManager<?> screen, String styleId, Vector2f position, Size dimensions) {
		super(screen, styleId, position, dimensions);
	}

	@Override
	protected void configureStyledElement() {
		super.configureStyledElement();
		layoutManager = new FlowLayout();
		setBringToFrontOnClick(true);
		setMovable(true);
		setResizable(true);
		setLockToParentBounds(true);
		setKeyboardFocusRoot(true);
		addClippingLayer(this, null);
	}

}
