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

import com.jme3.font.BitmapFont.Align;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.AbstractToggleButton;
import icetone.controls.buttons.PushButton;
import icetone.controls.buttons.ToggleButton;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Layout.LayoutType;
import icetone.core.Size;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public class Window extends Element {
	protected Element dragBar;
	protected Element contentArea;
	protected PushButton close;
	protected AbstractToggleButton collapse;
	protected Vector4f dbIndents = new Vector4f();
	
	private boolean useCollapse = false;
	private float winDif = 0;
	private boolean useClose = false;

	/**
	 * Creates a new instance of the Window control
	 */
	public Window() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param title
	 */
	public Window(String title) {
		this();
		setTitle(title);
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param title
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Window(String title, BaseScreen screen) {
		this(screen);
		setTitle(title);
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Window(BaseScreen screen) {
		this(screen, Vector2f.ZERO);
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Window(BaseScreen screen, Vector2f position) {
		this(screen, null, position, null);
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public Window(BaseScreen screen, Vector2f position, Size dimensions) {
		this(screen, null, position, dimensions);
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param styleId
	 *            ID for CSS and element matching
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Window(BaseScreen screen, String UID, Vector2f position) {
		this(screen, UID, position, null);
	}

	/**
	 * Creates a new instance of the Window control
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
	public Window(BaseScreen screen, String UID, Vector2f position, Size dimensions) {
		super(screen, UID, position, dimensions);
	}

	@Override
	protected void configureStyledElement() {

		layoutManager = new BorderLayout();

		// setAsContainerOnly();
		setResizable(true);
		setMovable(true);
		setLockToParentBounds(true);
		setKeyboardFocusRoot(true);

		setBringToFrontOnClick(true);

		dragBar = new Element(screen) {
			{
				styleClass = "dragbar";
				layoutManager = new FlowLayout().setAlign(Align.Right);
			}
		};
		dragBar.setResizable(true);
		dragBar.setResizeS(false);
		dragBar.setResizeE(false);
		dragBar.setResizeW(false);
		dragBar.setMovable(true);
		dragBar.setAffectParent(true);

		addElement(dragBar, Border.NORTH);

		close = new PushButton(screen) {
			{
				styleClass = "close icon";
			}
		};
		close.onMouseReleased(evt -> hide());

		collapse = new ToggleButton(screen) {
			{
				styleClass = "collapse";
			}
		};
		collapse.setState(false);
		collapse.onChange(evt -> {
			if (evt.getNewValue()) {
				winDif = Window.this.getHeight();
				setHeight(dragBar.getHeight());
				Window.this.setResizeN(false);
				Window.this.setResizeS(false);
			} else {
//				contentArea.show();
//				contentArea.setMaxDimensions(null);
				setHeight(winDif);
				Window.this.setResizeN(Window.this.isResizable());
				Window.this.setResizeS(Window.this.isResizable());
			}
		});

		contentArea = new Element(screen) {
			{
				styleClass = "content";
				layoutManager = new FlowLayout();
			}
		};
		contentArea.setResizable(true);
		contentArea.setResizeN(false);
		contentArea.setAffectParent(true);
		contentArea.setIgnoreMouse(false);
		contentArea.setMovable(false);

		addElement(contentArea, Border.CENTER);
		
		setUseCloseButton(true);
		setUseCollapseButton(true);

	}

	/**
	 * Returns a pointer to the Element used as a window dragbar
	 * 
	 * @return Element
	 */
	public BaseElement getDragBar() {
		return this.dragBar;
	}

	/**
	 * Returns the drag bar height
	 * 
	 * @return float
	 */
	public float getDragBarHeight() {
		return dragBar.getHeight();
	}

	/**
	 * Sets the Window title text
	 * 
	 * @param title
	 *            String
	 */
	public Window setTitle(String title) {
		dragBar.setText(title);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * Enables/disables the Window dragbar
	 * 
	 * @param isMovable
	 *            boolean
	 */
	public Window setWindowIsMovable(boolean isMovable) {
		this.dragBar.setMovable(isMovable);
		return this;
	}

	/**
	 * Returns if the Window dragbar is currently enabled/disabled
	 * 
	 * @return boolean
	 */
	public boolean getWindowIsMovable() {
		return this.dragBar.isMovable();
	}

	public Window addWindowContent(BaseElement el) {
		contentArea.addElement(el);
		contentArea.addClippingLayer(contentArea);
		return this;
	}

	public Window removeWindowContent(BaseElement el) {
		contentArea.removeElement(el);
		return this;
	}

	public BaseElement getContentArea() {
		return contentArea;
	}

	public Window setUseCloseButton(boolean use) {
		if (this.useClose != use) {
			this.useClose = use;
			if (use) {
				dragBar.addElement(close);
			} else {
				dragBar.removeElement(close);
			}
		}
		return this;
	}

	public Window setUseCollapseButton(boolean use) {
		if (this.useCollapse != use) {
			this.useCollapse = use;
			if (use)
				dragBar.addElement(collapse);
			else
				dragBar.removeElement(collapse);
		}
		return this;
	}

}
