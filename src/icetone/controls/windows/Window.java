/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.controls.windows;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;

/**
 *
 * @author t0neg0d
 */
public class Window extends Element {
	protected Element dragBar;
	protected Element contentArea;
	protected ButtonAdapter close, collapse;
	private boolean useShowSound, useHideSound;
	private String showSound, hideSound;
	private float showSoundVolume, hideSoundVolume;
	protected Vector4f dbIndents = new Vector4f();
	private boolean useClose = false, useCollapse = false, isCollapsed = false;
	private float winDif = 0;

	/**
	 * Creates a new instance of the Window control
	 */
	public Window() {
		this(Screen.get());
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Window(ElementManager screen) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Window(ElementManager screen, Vector2f position) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
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
	public Window(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
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
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Window
	 */
	public Window(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Window control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public Window(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Window control
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
	public Window(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, screen.getStyle("Window").getVector4f("resizeBorders"),
				screen.getStyle("Window").getString("defaultImg"));
	}

	/**
	 * Creates a new instance of the Window control
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
	public Window(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, Vector4f.ZERO, null);
		setAsContainerOnly();
		setLockToParentBounds(true);

		// layoutManager = new MigLayout("gap 0, ins 0, wrap 1", "[fill, grow]",
		// "[][fill, grow]");
		setLayoutManager(new BorderLayout());

		this.setIsBringToFrontOnClick(true);
		// this.setIsBringToFrontOnClick(true);
		// this.setIsResizable(true);
		this.setClipPadding(screen.getStyle("Window").getFloat("clipPadding"));
		this.setMinDimensions(screen.getStyle("Window").getVector2f("minSize"));
		// this.setClippingLayer(this);

		dbIndents.set(screen.getStyle("Window#Dragbar").getVector4f("indents"));

		// dragBar = new Element(screen, UID + ":DragBar", new
		// Vector2f(dbIndents.y, dbIndents.x),
		// new Vector2f(getWidth() - dbIndents.y - dbIndents.z,
		// screen.getStyle("Window#Dragbar").getFloat("defaultControlSize")),
		// screen.getStyle("Window#Dragbar").getVector4f("resizeBorders"),
		// screen.getStyle("Window#Dragbar").getString("defaultImg"));

		dragBar = new Element(screen, UID + ":DragBar", Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("Window#Dragbar").getVector4f("resizeBorders"),
				screen.getStyle("Window#Dragbar").getString("defaultImg"));
		float cs = screen.getStyle("Window#Dragbar").getFloat("defaultControlSize");
		dragBar.setPreferredDimensions(new Vector2f(cs, cs));
		dragBar.setLayoutManager(new FlowLayout());
		dragBar.setFontSize(screen.getStyle("Window#Dragbar").getFloat("fontSize"));
		dragBar.setFontColor(screen.getStyle("Window#Dragbar").getColorRGBA("fontColor"));
		dragBar.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Window#Dragbar").getString("textAlign")));
		dragBar.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Window#Dragbar").getString("textVAlign")));
		dragBar.setTextPosition(0, 0);
		dragBar.setTextPaddingByKey("Window#Dragbar", "textPadding");
		dragBar.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Window#Dragbar").getString("textWrap")));
		dragBar.setIsResizable(false);
		dragBar.setIsMovable(true);
		dragBar.setEffectParent(true);
		dragBar.addClippingLayer(this);

		addChild(dragBar, BorderLayout.Border.NORTH);

		float buttonHeight = (dragBar.getHeight() <= 25) ? 18 : dragBar.getHeight() - 6;
		// buttonHeight -= 2;

		close = new ButtonAdapter(screen, Vector2f.ZERO, new Vector2f(buttonHeight, buttonHeight)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				hideWindow();
			}
		};
		close.setText("X");
		close.setDocking(Docking.SE);

		collapse = new ButtonAdapter(screen, Vector2f.ZERO, new Vector2f(buttonHeight, buttonHeight)) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (!isCollapsed) {
					isCollapsed = true;
					contentArea.hide();
					winDif = Window.this.getHeight();
					Window.this.setHeight(getDragBarHeight() + (dbIndents.y * 2));
					winDif -= Window.this.getHeight();
					dragBar.setY(Window.this.getHeight() - dragBar.getHeight() - dbIndents.y);
					Window.this.setY(Window.this.getY() + winDif);
					setButtonIcon(getWidth(), getHeight(), screen.getStyle("Common").getString("arrowDown"));
					Window.this.setResizeN(false);
					Window.this.setResizeS(false);
				} else {
					isCollapsed = false;
					contentArea.show();
					Window.this.setHeight(getDragBarHeight() + contentArea.getHeight() + dbIndents.y + 2);
					dragBar.setY(Window.this.getHeight() - dragBar.getHeight() - dbIndents.y);
					Window.this.setY(Window.this.getY() - winDif);
					setButtonIcon(getWidth(), getHeight(), screen.getStyle("Common").getString("arrowUp"));
					Window.this.setResizeN(Window.this.getIsResizable());
					Window.this.setResizeS(Window.this.getIsResizable());
				}
			}
		};
		collapse.setButtonIcon(collapse.getWidth(), collapse.getHeight(), screen.getStyle("Common").getString("arrowUp"));
		collapse.setDocking(Docking.SE);

		contentArea = new Element(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, resizeBorders, defaultImg);
		contentArea.setLayoutManager(new MigLayout());
		contentArea.setIsResizable(true);
		contentArea.setEffectParent(true);

		addChild(contentArea);

		showSound = screen.getStyle("Window").getString("showSound");
		useShowSound = screen.getStyle("Window").getBoolean("useShowSound");
		showSoundVolume = screen.getStyle("Window").getFloat("showSoundVolume");
		hideSound = screen.getStyle("Window").getString("hideSound");
		useHideSound = screen.getStyle("Window").getBoolean("useHideSound");
		hideSoundVolume = screen.getStyle("Window").getFloat("hideSoundVolume");

		populateEffects("Window");
	}

	@Override
	public void onInitialized() {
		if (getPreferredDimensions() == null && LUtil.LAYOUT_SIZE.equals(getOrgDimensions()))
			sizeToContent();
	}

	/**
	 * Returns a pointer to the Element used as a window dragbar
	 * 
	 * @return Element
	 */
	public Element getDragBar() {
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
	public void setWindowTitle(String title) {
		dragBar.setText(title);
	}

	/**
	 * Shows the window using the default Show Effect
	 */
	public void showWindow() {
		Effect effect = getEffect(Effect.EffectEvent.Show);
		if (effect != null) {
			if (useShowSound && screen.getUseUIAudio()) {
				effect.setAudioFile(showSound);
				effect.setAudioVolume(showSoundVolume);
			}
			if (effect.getEffectType() == Effect.EffectType.FadeIn) {
				Effect clone = effect.clone();
				clone.setAudioFile(null);
				this.propagateEffect(clone, false);
			} else
				screen.getEffectManager().applyEffect(effect);
		} else
			this.show();
	}

	/**
	 * Hides the Window using the default Hide Effect
	 */
	public void hideWindow() {
		Effect effect = getEffect(Effect.EffectEvent.Hide);
		if (effect != null) {
			if (useHideSound && screen.getUseUIAudio()) {
				effect.setAudioFile(hideSound);
				effect.setAudioVolume(hideSoundVolume);
			}
			if (effect.getEffectType() == Effect.EffectType.FadeOut) {
				Effect clone = effect.clone();
				clone.setAudioFile(null);
				this.propagateEffect(clone, true);
			} else
				screen.getEffectManager().applyEffect(effect);
		} else
			this.hide();
	}

	/**
	 * Enables/disables the Window dragbar
	 * 
	 * @param isMovable
	 *            boolean
	 */
	public void setWindowIsMovable(boolean isMovable) {
		this.dragBar.setIsMovable(isMovable);
	}

	/**
	 * Returns if the Window dragbar is currently enabled/disabled
	 * 
	 * @return boolean
	 */
	public boolean getWindowIsMovable() {
		return this.dragBar.getIsMovable();
	}

	public void addWindowContent(Element el) {
		contentArea.addChild(el);
		contentArea.addClippingLayer(contentArea);
	}

	public void removeWindowContent(Element el) {
		contentArea.removeChild(el);
	}

	public Element getContentArea() {
		return contentArea;
	}

	public void setUseCloseButton(boolean use) {
		if (use) {
			this.useClose = true;
			dragBar.addChild(close);
		} else {
			this.useClose = false;
			dragBar.removeChild(close);
		}
	}

	public void setUseCollapseButton(boolean use) {
		if (use) {
			this.useCollapse = true;
			dragBar.addChild(collapse);
		} else {
			this.useCollapse = false;
			dragBar.removeChild(collapse);
		}
	}

}
