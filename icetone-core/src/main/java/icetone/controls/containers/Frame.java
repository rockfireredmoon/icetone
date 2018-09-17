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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.core.AbstractGenericLayout;
import icetone.core.Borders;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.BaseScreen;
import icetone.core.Layout;
import icetone.core.Layout.LayoutType;
import icetone.core.Position;
import icetone.core.PseudoStyles;
import icetone.core.Size;
import icetone.core.Element;
import icetone.core.layout.DefaultLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.css.CssEvent;
import icetone.css.CssProcessor.PseudoStyle;
import icetone.framework.core.AnimText;

public class Frame extends Element {

	public interface Listener {

		void closed(Frame window);

		// Will only be caught by global listeners
		void created(Frame window);

		void destroyed(Frame window);

		void opened(Frame window);

		void selected(Frame window);

		void stateChanged(Frame window, State oldState, State newState);

		void windowTitleChanged(Frame window, String oldTitle, String newTitle);
	}

	public enum State {

		MAXIMIZED, MINIMIZED, NORMAL
	}

	protected abstract class AbstractFrameLayout extends AbstractGenericLayout<Frame, Object> {

		@Override
		protected Vector2f calcMaximumSize(Frame parent) {
			Vector2f maximumSize = content.calcMaximumSize();
			Vector2f dbmin = dragBar.calcPreferredSize();
			Vector2f dbleftmin = dragLeft.calcPreferredSize();
			Vector2f dbrightmin = dragRight.calcPreferredSize();
			Vector2f accpref = accessories.calcPreferredSize();
			float dbh = Math.max(accpref.y, Math.max(Math.max(dbmin.y, dbleftmin.y), dbrightmin.y));
			maximumSize.addLocal(parent.getTotalPadding());
			return maximumSize.add(new Vector2f(0, dbh));
		}

		@Override
		protected Vector2f calcMinimumSize(Frame parent) {
			Vector2f min = new Vector2f();
			Vector2f dbPref = dragBar.calcPreferredSize();
			Vector2f dbLeftPref = dragLeft.calcPreferredSize();
			Vector2f dbRightPref = dragRight.calcPreferredSize();
			Vector2f trayMin = buttonTray.calcMinimumSize();
			Vector2f contMin = content.calcMinimumSize();
			Vector2f accpref = accessories.calcMinimumSize();
			min.x = Math.max((parent.getIndent() * 4) + accpref.x + dbLeftPref.x + dbRightPref.x
					+ (Math.max(trayMin.x, dbPref.x)), contMin.x);
			min.y = Math.max(accpref.y, Math.max(trayMin.y, Math.max(dbPref.y, Math.max(dbLeftPref.y, dbRightPref.y))))
					+ contMin.y;
			min.addLocal(parent.getTotalPadding());
			return min;
		}

		@Override
		protected Vector2f calcPreferredSize(Frame parent) {
			Vector2f pref = new Vector2f();
			Vector2f dbPref = dragBar.calcPreferredSize();
			Vector2f dbLeftPref = dragLeft.calcPreferredSize();
			Vector2f dbRightPref = dragRight.calcPreferredSize();
			Vector2f contPref = content.calcPreferredSize();
			Vector2f accPref = accessories.calcPreferredSize();
			Vector2f trayPref = buttonTray.calcPreferredSize();
			pref.x = Math.max(
					(parent.getIndent() * 4) + accPref.x + dbLeftPref.x + dbRightPref.x + trayPref.x + dbPref.x,
					contPref.x);
			pref.y = Math.max(accPref.x,
					Math.max(trayPref.y, Math.max(dbPref.y, Math.max(dbLeftPref.y, dbRightPref.y)))) + contPref.y;
			pref.addLocal(parent.getTotalPadding());
			return pref;
		}

		@Override
		protected void onLayout(Frame parent) {
			onBeforeContentLayout();

			Vector4f margin = parent.getMargin();
			Vector2f dbPref = dragBar.calcPreferredSize();
			Vector2f dbLeftPref = dragLeft.calcPreferredSize();
			Vector2f dbRightPref = dragRight.calcPreferredSize();
			Vector2f buttonPref = buttonTray.calcPreferredSize();
			Vector2f trayPref = buttonTray.calcPreferredSize();
			Vector2f accPref = accessories.calcPreferredSize();
			Vector4f dbPadding = dragBar.getAllPadding();
			float dbh = Math.max(trayPref.y, Math.max(dbPref.y, Math.max(dbLeftPref.y, dbRightPref.y)));

			dragLeft.setBounds(margin.x, margin.z, dbLeftPref.x, dbh);
			accessories.setBounds(margin.x + dbLeftPref.x + parent.getIndent(), margin.z, accPref.x, dbh);
			dragBar.setBounds(dbLeftPref.x + margin.x + (parent.getIndent() * 2), margin.z,
					parent.getWidth() - dbRightPref.x - dbLeftPref.x - margin.x - margin.y, dbh);
			buttonTray.setBounds(
					parent.getWidth() - dbRightPref.x - buttonPref.x - getIndent() - margin.y + margin.x
							+ (parent.getIndent() * 3),
					dbPadding.z + margin.z, buttonPref.x, dbh - dbPadding.z - dbPadding.w);
			dragRight.setBounds(parent.getWidth() - dbRightPref.x - margin.y + (parent.getIndent() * 4), margin.z,
					dbRightPref.x, dbh);
			onLayout(parent, dbh);

			onContentLayout();
		}

		protected abstract void onLayout(Frame parent, float dbh);

	}

	protected class DefaultFrameLayout extends AbstractFrameLayout {

		@Override
		public void onLayout(Frame parent, float dbh) {
			Vector4f padding = parent.getAllPadding();
			Vector4f margin = parent.getMargin();
			content.setBounds(margin.x, dbh + margin.y, parent.getWidth() - padding.x - padding.y,
					parent.getHeight() - dbh - padding.z - padding.w);
		}

	}

	public final static CssEvent MAXIMIZE = new CssEvent("maximize");

	public final static CssEvent MINIMIZE = new CssEvent("minimize");

	public final static CssEvent RESTORE = new CssEvent("restore");

	private static List<Listener> globalListeners = new ArrayList<Listener>();

	private static List<Frame> windows = new ArrayList<Frame>();

	public static void addGlobalListener(Listener l) {
		globalListeners.add(l);
	}

	public static List<Frame> getWindows() {
		return Collections.unmodifiableList(windows);
	}

	public static void removeGlobalListener(Listener l) {
		globalListeners.remove(l);
	}

	protected Element content;
	protected Element dragBar;

	private Element accessories;
	private Vector2f beforeMax;
	private Position beforeMaxPos;
	private Element buttonTray;
	private Button closeButton;
	private Element dragLeft;
	private Element dragRight;
	private Element contentLeft;
	private Element contentRight;
	private Element contentBottom;
	private List<Listener> listeners = new ArrayList<Listener>();
	private boolean managedHint = true;
	private boolean maximizable;
	private boolean hasMenu;
	private Button maximizeButton;
	private boolean minimizable;
	private Button minimizeButton;
	private boolean selected;
	private State state = State.NORMAL;
	private boolean wasDestroyOnHide;

	private Element menuButton;

	public Frame() {
		this(BaseScreen.get(), false);
	}

	public Frame(BaseScreen screen) {
		this(screen, false);
	}

	/**
	 * Creates a new instance of the Frame control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param closeable
	 *            show the close icon
	 */
	public Frame(BaseScreen screen, boolean closeable) {
		this(screen, null, Vector2f.ZERO, closeable);
	}

	/**
	 * Creates a new instance of the Frame control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param styleId
	 *            ID for CSS and element matching
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param closeable
	 *            show the close icon
	 */
	public Frame(BaseScreen screen, String styleId, Vector2f position, boolean closeable) {
		this(screen, styleId, position, null, closeable);
	}

	/**
	 * Creates a new instance of the Frame control
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
	 * @param closeable
	 *            show the close icon
	 */
	public Frame(BaseScreen screen, String styleId, Vector2f position, Size dimensions, boolean closeable) {
		super(screen, styleId);
		if (position != null)
			setPosition(position);
		if (dimensions != null)
			setPreferredDimensions(dimensions);
		setCloseable(closeable);
		setLayoutManager(new DefaultFrameLayout());
	}

	/**
	 * Creates a new instance of the Frame control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param closeable
	 *            show the close icon
	 */
	public Frame(BaseScreen screen, Vector2f position, boolean closeable) {
		this(screen, null, position, null, closeable);
	}

	/**
	 * Creates a new instance of the Frame control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param closeable
	 *            show the close icon
	 */
	public Frame(BaseScreen screen, Vector2f position, Size dimensions, boolean closeable) {
		this(screen, null, position, dimensions, closeable);
	}

	public Frame(Layout<?, ?> contentLayoutManager) {
		this(BaseScreen.get(), true);
		setContentLayoutManager(contentLayoutManager);
	}

	public Frame addContent(BaseElement child) {
		content.addElement(child);
		return this;
	}

	public void addListener(Listener l) {
		listeners.add(l);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		if (isActuallyDestroyOnHide()) {
			destroy();
		}
	}

	@Override
	public final void controlHideHook() {
		onControlHideHook();
	}

	@Override
	public final void controlResizeHook() {
		onBeforeControlResizeHook();
		super.controlResizeHook();
		onControlResizeHook();
	}

	@Override
	public void destroy() {
		super.destroy();
		windows.remove(this);
		fireDestroyed();
	}

	@Override
	public boolean detachFromParent() {
		try {
			return super.detachFromParent();
		} finally {
			if (isDestroyOnHide() && getParentContainer() != null
					&& getParentContainer().getElements().contains(this)) {
				getParentContainer().removeElement(this);
			}
			// windows.remove(this);
		}
	}

	@Override
	public BaseElement getAbsoluteParent() {
		return this;
	}

	public Element getAccessories() {
		return accessories;
	}

	public Element getButtonTray() {
		return buttonTray;
	}

	/**
	 * Get the content element. Add content to this.
	 *
	 * @return content element
	 */
	public Element getContentArea() {
		return content;
	}

	/**
	 * Returns a pointer to the Element used as a window dragbar
	 *
	 * @return Element
	 */
	public Element getDragBar() {
		return this.dragBar;
	}

	public boolean isSelected() {
		return selected;
	}

	@Override
	public Size getMaxDimensions() {
		if (state == State.MAXIMIZED)
			return new Size(screen.getWidth(), screen.getHeight());
		return super.getMaxDimensions();
	}

	@Override
	public Size getMinDimensions() {
		if (state == State.MAXIMIZED)
			return new Size(screen.getWidth(), screen.getHeight());
		return super.getMinDimensions();
	}

	@Override
	public Size getPreferredDimensions() {
		if (state == State.MAXIMIZED)
			return new Size(screen.getWidth(), screen.getHeight());
		return super.getPreferredDimensions();
	}

	@Override
	public PseudoStyles getPseudoStyles() {
		PseudoStyles ps = super.getPseudoStyles();
		if (isSelected()) {
			ps = PseudoStyles.get(ps).addStyle(PseudoStyle.active);
		}
		return ps;
	}

	public State getState() {
		return state;
	}

	public String getTitle() {
		return getDragBar().getText();
	}

	/**
	 * Returns if the Window dragbar is currently enabled/disabled
	 *
	 * @return boolean
	 */
	public boolean getWindowIsMovable() {
		return this.dragBar.isMovable();
	}

	@Override
	public BaseElement hide() {
		boolean showing = isActive();
		super.hide();
		if (showing) {
			fireClosed();
			// if (isActuallyDestroyOnHide()) {
			// screen.removeElement(this);
			// windows.remove(this);
			// fireDestroyed();
			// }
		}
		return this;
	}

	protected void createMenu(BaseElement anchor) {
	}

	public boolean isHasMenu() {
		return hasMenu;
	}

	public boolean isManagedHint() {
		return managedHint;
	}

	public boolean isMaximizable() {
		return maximizable;
	}

	public boolean isMinimizable() {
		return minimizable;
	}

	public Frame maximize() {
		if (state != State.NORMAL) {
			throw new IllegalArgumentException("Not normal.");
		}
		if (minimizeButton != null) {
			minimizeButton.setEnabled(false);
		}
		beforeMaxPos = getPosition().clone();
		beforeMax = getDimensions().clone();
		setBounds(0, 0, screen.getWidth(), screen.getHeight());
		setPosition(0, 0);
		state = State.MAXIMIZED;
		screen.dirtyLayout(false, LayoutType.boundsChange());
		screen.layoutChildren();
		return this;
	}

	public Frame minimize() {
		if (state == State.MINIMIZED) {
			throw new IllegalArgumentException("Already minimized.");
		}
		wasDestroyOnHide = destroyOnHide;
		setDestroyOnHide(false);
		setSelected(false);
		hide(MINIMIZE, LayoutType.clipping, LayoutType.styling);
		updateState(State.MINIMIZED);
		return this;
	}

	@Override
	public void movedToFrontHook() {
		super.movedToFrontHook();
		setSelected(true);
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).created(this);
		}
		setSelected(true);
	}

	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	public void restore() {
		if (state == State.MINIMIZED) {
			setDestroyOnHide(wasDestroyOnHide);
			show(RESTORE, LayoutType.clipping, LayoutType.styling);
			updateState(State.NORMAL);
			if (minimizeButton != null) {
				minimizeButton.setEnabled(true);
			}
		} else if (state == State.MAXIMIZED) {
			setBounds(beforeMaxPos, beforeMax);
			state = State.NORMAL;
			screen.dirtyLayout(false, LayoutType.boundsChange());
			screen.layoutChildren();
			if (minimizeButton != null) {
				minimizeButton.setEnabled(true);
			}
		} else {
			throw new IllegalArgumentException("Already restored.");
		}
	}

	public Frame setCloseable(boolean closeable) {
		if (closeable && closeButton == null) {
			closeButton = new Button(screen) {
				{
					setStyleClass("close");
				}
			};
			closeButton.onMouseReleased(evt -> {
				if (canClose()) {
					hide();
					onCloseWindow();
				}
			});
			buttonTray.addElement(closeButton);
		} else if (!closeable && closeButton != null) {
			buttonTray.removeElement(closeButton);
			closeButton = null;
		}
		return this;
	}

	public Frame setContentLayoutManager(Layout<?, ?> layout) {
		content.setLayoutManager(layout);
		return this;
	}

	@Override
	public BaseElement setMovable(boolean isMovable) {
		super.setMovable(isMovable);
		dragBar.setMovable(isMovable);
		dragLeft.setMovable(isMovable);
		dragRight.setMovable(isMovable);
		contentLeft.setMovable(isMovable);
		content.setMovable(isMovable);
		contentRight.setMovable(isMovable);
		return this;
	}

	@Override
	public BaseElement setResizable(boolean isResizable) {
		super.setResizable(isResizable);
		dragBar.setResizable(isResizable);
		dragLeft.setResizable(isResizable);
		dragRight.setResizable(isResizable);
		contentLeft.setResizable(isResizable);
		content.setResizable(isResizable);
		contentRight.setResizable(isResizable);
		return this;
	}

	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			if (selected) {
				fireSelected();
			}
			dirtyLayout(true, LayoutType.reset);
			layoutChildren();
		}
	}

	public void setManagedHint(boolean managedHint) {
		this.managedHint = managedHint;
	}

	public Frame setMaximizable(boolean maximizable) {
		if (this.maximizable != maximizable) {
			this.maximizable = maximizable;
			rebuildWindowButtons();
		}
		return this;
	}

	public Frame setHasMenu(boolean hasMenu) {
		if (this.hasMenu != hasMenu) {
			this.hasMenu = hasMenu;
			if (hasMenu) {
				menuButton = new Button(screen) {
					{
						setStyleClass("frame-menu");
					}
				};
				menuButton.onMouseReleased((evt) -> {
					createMenu(menuButton);
				});
				accessories.addElement(menuButton);
			} else {
				accessories.removeElement(menuButton);
			}
			rebuildWindowButtons();
		}
		return this;
	}

	public Frame setMinimizable(boolean minimizable) {
		if (this.minimizable != minimizable) {
			this.minimizable = minimizable;
			rebuildWindowButtons();
		}
		return this;
	}

	public Frame setTitle(String title) {
		getDragBar().setText(title);
		layoutChildren();
		return this;
	}

	/**
	 * Sets the Window title text
	 *
	 * @param title
	 *            String
	 */
	public Frame setWindowTitle(String title) {
		String oldTitle = dragBar.getText();
		if (!Objects.equals(oldTitle, title)) {
			dragBar.setText(title);
			for (int i = globalListeners.size() - 1; i >= 0; i--) {
				globalListeners.get(i).windowTitleChanged(this, oldTitle, title);
			}
			for (int i = listeners.size() - 1; i >= 0; i--) {
				listeners.get(i).windowTitleChanged(this, oldTitle, title);
			}
		}
		return this;
	}

	@Override
	public BaseElement show() {
		setSelected(true);
		super.show();
		return this;
	}

	protected boolean canClose() {
		return true;
	}

	@Override
	protected void configureStyledElement() {

		windows.add(this);


		dragLeft = new Element(screen) {
			{
				setStyleClass("left");
				setMovable(false);
				setResizable(false);
				setResizeS(false);
				setResizeE(false);
				setAffectParent(true);
			}
		};
		dragLeft.addClippingLayer(this, null);
		addElement(dragLeft);

		dragRight = new Element(screen) {
			{
				setStyleClass("right");
				setMovable(false);
				setResizable(false);
				setResizeS(false);
				setResizeW(false);
				setAffectParent(true);
			}
		};
		dragRight.addClippingLayer(this, null);
		addElement(dragRight);

		dragBar = new Element(screen) {
			{
				setStyleClass("dragbar");
			}
		};
		dragBar.setLayoutManager(new DefaultLayout() {

			@Override
			protected Vector4f calcTextOffset(ElementContainer<?, ?> container, AnimText textElement,
					Vector4f textPadding) {
				float lineWidth = textElement.getLineWidth();
				float offsetx = 0;
				if (textElement.getTextAlign() == Align.Center) {
					offsetx = (int) ((textElement.getWidth() - lineWidth) / 2f);
				} else if (textElement.getTextAlign() == Align.Right) {
					offsetx = (int) (textElement.getWidth() - lineWidth);
				}
				float dif = dragBar.getWidth() - buttonTray.getWidth() - lineWidth - offsetx;
				if (dif < 0) {
					return new Vector4f(textPadding).setX(dif * 2);
				}
				return textPadding;
			}

		});
		dragBar.setResizable(false);
		dragBar.setResizeS(false);
		dragBar.setResizeE(false);
		dragBar.setResizeW(false);
		dragBar.setMovable(true);
		dragBar.setAffectParent(true);
		addElement(dragBar);

		accessories = new Element(screen) {
			{
				setStyleClass("accessories");
				setIgnoreMouse(true);
			}
		};
		accessories.setLayoutManager(new FlowLayout(BitmapFont.Align.Left));
		accessories.addClippingLayer(this, null);
		addElement(accessories, null);

		buttonTray = new Element(screen) {
			{
				setStyleClass("buttons");
			}
		};
		buttonTray.setLayoutManager(new FlowLayout(BitmapFont.Align.Right));
		buttonTray.addClippingLayer(this, null);
		addElement(buttonTray);

		content = new Element(screen) {
			{
				setStyleClass("content");
			}

			@Override
			public void resize(float x, float y, Borders dir) {
				Frame.this.resize(x, y, dir);
			}
		};
		content.setResizeN(false);
		content.setAffectParent(true);
		content.setLayoutManager(new MigLayout());
		content.setIgnoreMouse(false);
		content.setMovable(false);
		content.setResizable(false);

		addElement(content);

		contentLeft = new Element(screen) {
			{
				setStyleClass("content-left");
			}

			@Override
			public void resize(float x, float y, Borders dir) {
				Frame.this.resize(x, y, dir);
			}
		};
		contentLeft.setResizeN(false);
		contentLeft.setResizeS(false);
		contentLeft.setResizeE(false);
		contentLeft.setAffectParent(true);
		contentLeft.setLayoutManager(new MigLayout());
		contentLeft.setIgnoreMouse(false);
		contentLeft.setMovable(false);
		contentLeft.setResizable(false);

		contentRight = new Element(screen) {
			{
				setStyleClass("content-right");
			}

			@Override
			public void resize(float x, float y, Borders dir) {
				Frame.this.resize(x, y, dir);
			}
		};
		contentRight.setResizeN(false);
		contentRight.setResizeS(false);
		contentRight.setResizeW(false);
		contentRight.setAffectParent(true);
		contentRight.setLayoutManager(new MigLayout());
		contentRight.setIgnoreMouse(false);
		contentRight.setMovable(false);
		contentRight.setResizable(false);

		setAffectZOrder(true);
		setResizable(false);
		setLockToParentBounds(true);
		setMovable(true);
		setKeyboardFocusRoot(true);
		setBringToFrontOnClick(true);

		dragBar.onMouseReleased(l -> {
			if (screen.getClickCount() == 2) {
				sizeToContent();
			}
		});
	}

	// protected void checkBounds() {
	// Vector2f minSize = LUtil.getMinimumSize(this);
	// if (getWidth() < minSize.x) {
	// setWidth(minSize.x);
	// }
	// if (getHeight() < minSize.y) {
	// setHeight(minSize.y);
	// }
	//
	// if (getX() < 0) {
	// setX(0);
	// } else if (getX() + getWidth() > screen.getWidth()) {
	// setX(screen.getWidth() - getWidth());
	// }
	// if (getY() < 0) {
	// setY(0);
	// } else if (getY() + getHeight() > screen.getHeight()) {
	// setY(screen.getHeight() - getHeight());
	// }
	// }

	protected boolean isActive() {
		return isVisible() || (state == State.MINIMIZED);
	}

	protected void onBeforeContentLayout() {
		// For subclasses to override. Called before content is laid out
	}

	protected void onBeforeControlResizeHook() {
		// For subclasses to override. Called before window resize
	}

	protected void onCloseWindow() {
		// For subclasses to override. Called when window is manually closed
	}

	protected void onContentLayout() {
		// For subclasses to override. Called after content is laid out
	}

	protected void onControlHideHook() {
		// For subclasses to override. Called on window hide
	}

	protected void onControlResizeHook() {
		// For subclasses to override. Called on window resize
	}

	private void fireClosed() {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).closed(this);
		}
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).closed(this);
		}
	}

	private void fireDestroyed() {
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).destroyed(this);
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).destroyed(this);
		}
	}

	private void fireSelected() {
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).selected(this);
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).selected(this);
		}
	}

	private boolean isActuallyDestroyOnHide() {
		return destroyOnHide || (state == State.MINIMIZED && wasDestroyOnHide);
	}

	private void rebuildWindowButtons() {
		buttonTray.invalidate();
		buttonTray.removeAllChildren();
		if (minimizable) {
			minimizeButton = new Button(screen) {
				{
					setStyleClass("minimize");
				}
			};
			minimizeButton.onMouseReleased(evt -> {
				if (getState() != State.MINIMIZED)
					minimize();
			});
			buttonTray.addElement(minimizeButton);
		}
		if (maximizable) {
			maximizeButton = new Button(screen) {
				{
					setStyleClass("maximize");
				}
			};
			maximizeButton.onMouseReleased(evt -> {
				if (state.equals(State.MAXIMIZED)) {
					restore();
				} else {
					maximize();
				}
			});
			buttonTray.addElement(maximizeButton);
		}
		if (closeButton != null) {
			buttonTray.addElement(closeButton);
		}
		buttonTray.validate();
	}

	private void updateState(State newState) {
		State oldState = state;
		state = newState;
		for (int i = globalListeners.size() - 1; i >= 0; i--) {
			globalListeners.get(i).stateChanged(this, oldState, state);
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).stateChanged(this, oldState, state);
		}
	}
}
