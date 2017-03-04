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

import java.util.Arrays;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.input.KeyInput;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import icetone.controls.text.AbstractTextLayout;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;
import icetone.core.Measurement.Unit;
import icetone.core.PseudoStyles;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.Element;
import icetone.core.event.MouseButtonHeldEvent;
import icetone.core.event.MouseButtonHeldListener;
import icetone.core.event.MouseButtonHeldSupport;
import icetone.core.event.MouseUIButtonEvent;
import icetone.css.CssExtensions;
import icetone.css.CssProcessor.PseudoStyle;
import icetone.css.CssUtil;
import icetone.framework.core.AnimText;

/**
 * @author t0neg0d
 * @author rockfire
 */
public class Button extends Element implements Control {
	protected static class ButtonLayout extends AbstractTextLayout<Button> {

		@Override
		public Vector2f preferredSize(Button parent) {
			Vector2f pref = super.preferredSize(parent);

			Vector2f ip = parent.getButtonIcon().calcPreferredSize();
			if (parent.getButtonIcon().getElementTexture() != null || !Vector2f.ZERO.equals(ip)) {
				Vector2f ps = new Vector2f();

				float bw = ip == null ? -1 : ip.x;
				float bh = ip == null ? -1 : ip.y;
				if (Vector2f.ZERO.equals(ip)) {
					float sc = Math.min(ps.x, ps.y);
					bw = sc / 2f;
					bh = sc / 2f;
				}

				Vector4f padding = parent.getAllPadding();
				if ((parent.getTextElement() == null || "".equals(parent.getText()))) {
					ps.x = bw + padding.x + padding.y;
					ps.y = Math.max(pref.y, bh + padding.z + padding.w);
				} else {
					if (parent.getButtonIconAlign() == Align.Center) {
						ps.x = Math.max(pref.x, bw + padding.x + padding.y);
						ps.y = pref.y + bh + parent.getIndent();
					} else {
						ps.x = pref.x + bw + parent.getIndent();
						ps.y = Math.max(pref.y, bh + padding.z + padding.w);
					}
				}
				pref = ps;
			}
			return pref;
		}

		// TODO renable

		@Override
		protected Vector4f calcTextOffset(Button element, AnimText textElement, Vector4f textPadding) {
			Vector4f off = super.calcTextOffset(element, textElement, textPadding).clone();
			Vector2f ip = element.getButtonIcon().calcPreferredSize();
			if (element.getButtonIcon().getElementTexture() != null || !Vector2f.ZERO.equals(ip)) {

				Vector2f ps = element.getDimensions().clone();
				Vector2f sz = ip.clone();
				if (Vector2f.ZERO.equals(ip)) {
					// Element is as big as we want it
					float sc = Math.min(ps.x, ps.y);
					sz.x = sc / 2f;
					sz.y = sc / 2f;
				}
				//
				if (element.getText() != null && !"".equals(element.getText())) {
					switch (element.getButtonIconAlign()) {
					case Left:
						off.x += sz.x + element.getIndent();
						;
						break;
					case Right:
						if (textElement.getTextAlign() != Align.Left)
							off.y += sz.x + element.getIndent();
						break;
					default:
						break;
					}

				}
			}

			return off;
		}

		@Override
		protected void onLayout(Button parent) {
			final String text1 = parent.getText();
			Vector4f textPadding = parent.getAllPadding();
			Vector2f dim = parent.getDimensions().subtract(textPadding.x  + textPadding.y, textPadding.z  + textPadding.w);
			Vector2f ip = parent.getButtonIcon().calcPreferredSize();
			if (parent.getButtonIcon().getElementTexture() != null || !Vector2f.ZERO.equals(ip)) {
				Vector2f ps = calcTextSize(parent, parent.getWidth() - parent.getTotalPadding().x);
				if (ps == null)
					ps = Vector2f.ZERO;
				Vector2f sz = ip.clone();
				if (Vector2f.ZERO.equals(ip)) {
					// Element is as big as we want it
					float sc = Math.min(ps.x, ps.y);
					sz.x = sc / 2f;
					sz.y = sc / 2f;
				}

				Vector2f pos = new Vector2f();

				float cx = ((parent.getWidth() - textPadding.x - textPadding.y) / 2f) - (sz.x / 2f) + textPadding.x;
				if (text1 == null || text1.equals("")) {
					pos.set(cx, (parent.getHeight() / 2f) - (sz.y / 2f));
				} else {
					switch (parent.getButtonIconAlign()) {
					case Left:
						pos.set(textPadding.x, (parent.getHeight() / 2f) - (sz.y / 2f));
						break;
					case Right:
						pos.set(parent.getWidth() - sz.x - textPadding.y, (parent.getHeight() / 2) - (sz.y / 2));
						break;
					default:
						switch (parent.getTextVAlign()) {
						case Top:
							pos.set(cx, parent.getHeight() - sz.y - textPadding.w);
							break;
						case Bottom:
							pos.set(cx, textPadding.z);
							break;
						default:
							pos.set(cx, (parent.getHeight() / 2f) - (sz.y / 2f));
							break;
						}
						break;
					}
				}
				parent.getButtonIcon().setBounds(pos.x, pos.y, sz.x, sz.y);
			}
			parent.getOverlay().setBounds(textPadding.x, textPadding.x, dim.x, dim.y);

		}
	}

	protected ButtonGroup<? extends Button> buttonGroup = null;
	protected Element icon;
	protected float initClickInterval = 0.25f, currentInitClickTrack = 0;
	protected boolean initClickPause = false;
	protected boolean isStillPressed = false;
	protected float trackInterval = (4 / 1000), currentTrack = 0;
	protected MouseButtonHeldSupport mouseButtonHeldSupport;

	private float intervalsPerSecond = 4;
	private boolean rightPressed;
	private boolean useInterval = false;
	private MouseUIButtonEvent<BaseElement> lastEvt;
	private Element overlay;
	// private boolean movedWhileLeftPressed;

	{
		setKeyboardFocusable(true);
	}

	public Button() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Button(ElementManager<?> screen) {
		super(screen);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Button(ElementManager<?> screen, float iconWidth, float iconHeight, String texturePath, String text) {
		this(screen);
		if (texturePath != null)
			setButtonIcon(iconWidth, iconHeight, texturePath);
		if (text != null)
			setText(text);
	}

	public Button(ElementManager<?> screen, String text) {
		this(screen);
		setText(text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Button(float iconWidth, float iconHeight, String texturePath) {
		this(iconWidth, iconHeight, texturePath, null);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Button(float iconWidth, float iconHeight, String texturePath, String text) {
		this(BaseScreen.get(), iconWidth, iconHeight, texturePath, text);
	}

	public Button(String text) {
		this(BaseScreen.get());
		setText(text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Button(String texturePath, String text) {
		this(-1, -1, texturePath, text);
	}

	public Button addIconStyleClass(String styleClass) {
		icon.addStyleClass(styleClass);
		return this;
	}

	/**
	 * Clears current hover and pressed images set by Style defines
	 */
	public Button clearAltImages() {
		setButtonHoverInfo(null, null);
		setButtonPressedInfo(null, null);
		return this;
	}

	@Override
	public Button clone() {
		cloning.set(cloning.get() + 1);
		try {
			Button el = new Button(screen);
			configureClone(el);
			return el;
		} finally {
			cloning.set(cloning.get() - 1);
		}
	}

	@Override
	protected void configureStyledElement() {

		styleClass = "standard";
		layoutManager = new ButtonLayout();

		setMouseFocusable(true);
		setIgnoreMouseButtons(false);
		setFocusRootOnly(false);

		icon = new Element(screen) {
			{
				styleClass = "icon";
				useParentPseudoStyles = true;
				setAffectParent(true);
			}
		};
		icon.setAsContainerOnly();
		addElement(icon);

		overlay = new Element(screen) {
			{
				styleClass = "overlay";
				useParentPseudoStyles = true;
			}
		};
		overlay.setAsContainerOnly();
		addElement(overlay);

		setAffectZOrder(false);

		// Left click handling
		onMousePressed(evt -> {
			isStillPressed = true;
			lastEvt = evt;
			if (isEnabled) {
				processMouseButtonPressed(evt);
			}
			evt.setConsumed();
		});
		onMouseReleased(evt -> {
			isStillPressed = false;
			if (isEnabled) {
				initClickPause = false;
				currentInitClickTrack = 0;
				processMouseButtonReleased(evt);
			}
			// movedWhileLeftPressed = false;
			evt.setConsumed();
		});
		onMouseMoved(evt -> {
			// if(isStillPressed && getIsMovable()) {
			// /** If we move while pressed and this is a movable element,
			// prevent
			// * further held events
			// */
			// movedWhileLeftPressed = true;
			// }
		});

		// Right click handling
		onMousePressed(evt -> {
			lastEvt = evt;
			rightPressed = true;
			if (isEnabled) {
				processRightMouseButtonPressed(evt);
			}
			evt.setConsumed();
		}, MouseUIButtonEvent.RIGHT);
		onMouseReleased(evt -> {
			rightPressed = false;
			if (isEnabled) {
				processRightMouseButtonReleased(evt);
			}
			evt.setConsumed();
		}, MouseUIButtonEvent.RIGHT);

		// Fake mouse events on SPACE
		onKeyboardPressed(evt -> {

			if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
				if (isEnabled && getParent() != null) {
					mouseButtonSupport.fireEvent(new MouseUIButtonEvent<BaseElement>(0, true, 0, 0, 0, 0, 1, 0, null));
					evt.setConsumed();
				}
			}
		});
		onKeyboardReleased(evt -> {

			if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
				if (isEnabled && getParent() != null) {
					mouseButtonSupport.fireEvent(new MouseUIButtonEvent<BaseElement>(0, false, 0, 0, 0, 0, 1, 0, null));
					evt.setConsumed();
				}
			}
		});
	}

	public BaseElement onMouseHeld(MouseButtonHeldListener<BaseElement> l) {
		if (mouseButtonHeldSupport == null)
			mouseButtonHeldSupport = new MouseButtonHeldSupport();
		if (isContainerOnly())
			makeNonContainer();
		mouseButtonHeldSupport.bind(l);
		return this;
	}

	public BaseElement onMouseHeld(MouseButtonHeldListener<BaseElement> l, int button) {
		if (mouseButtonHeldSupport == null)
			mouseButtonHeldSupport = new MouseButtonHeldSupport();
		if (isContainerOnly())
			makeNonContainer();
		mouseButtonHeldSupport.bind(l, button);
		return this;
	}

	public BaseElement addMouseButtonHeldListener(MouseButtonHeldListener<BaseElement> l) {
		if (mouseButtonHeldSupport == null)
			mouseButtonHeldSupport = new MouseButtonHeldSupport();
		if (isContainerOnly())
			makeNonContainer();
		mouseButtonHeldSupport.addListener(l);
		return this;
	}

	public BaseElement removeMouseButtonHeldListener(MouseButtonHeldListener<BaseElement> l) {
		if (mouseButtonHeldSupport != null)
			mouseButtonHeldSupport.removeListener(l);
		return this;
	}

	@Override
	protected void configureClone(BaseElement e) {
		super.configureClone(e);
		Button el = (Button) e;
		el.initClickInterval = initClickInterval;
		el.intervalsPerSecond = intervalsPerSecond;
		el.useInterval = useInterval;
		// el.icon = icon;

	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void controlIsEnabledHook(boolean isEnabled) {
	}

	public ButtonGroup<? extends Button> getButtonGroup() {
		return buttonGroup;
	}

	public Element getButtonIcon() {
		return this.icon;
	}

	public Element getOverlay() {
		return this.overlay;
	}

	public BitmapFont.Align getButtonIconAlign() {
		return icon.getLayoutData() == null ? Align.Left : Align.valueOf(icon.getLayoutData());
	}

	/**
	 * @see #setInterval(float)
	 * 
	 * @return interval
	 */

	public float getIntervalsPerSecond() {
		return intervalsPerSecond;
	}

	/**
	 * Returns if the button is still pressed
	 * 
	 * @return boolean
	 */
	public boolean getIsStillPressed() {
		return this.isStillPressed;
	}

	/**
	 * Abstract method for handling interval updates while the button is still
	 * pressed
	 * 
	 * NOTE: This is only called if the button's setInterval method has been
	 * previously called
	 */
	@Deprecated
	public void onButtonStillPressedInterval() {
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	}

	public Button setButtonGroup(ButtonGroup<? extends Button> buttonGroup) {
		if (buttonGroup.getButtons().contains(this)) {
			this.buttonGroup = buttonGroup;
		} else {
			buttonGroup.addButton(this);
		}
		return this;
	}

	/**
	 * Sets the texture image path and color to use when the button has mouse
	 * focus
	 * 
	 * @param pathHoverImg
	 *            String path to image for mouse focus event
	 * @param hoverFontColor
	 *            ColorRGBA to use for mouse focus event
	 */
	public final Button setButtonHoverInfo(String pathHoverImg, ColorRGBA hoverFontColor) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.BACKGROUND_IMAGE,
				pathHoverImg == null ? new PropertyValue(IdentValue.AUTO)
						: new PropertyValue(new FSFunction("url", Arrays.asList(pathHoverImg))),
				false, StylesheetInfo.USER);
		getCssState().addCssDeclaration(decl, PseudoStyle.hover);
		applyCss(decl);
		PropertyDeclaration declf = new PropertyDeclaration(CSSName.COLOR, hoverFontColor == null
				? new PropertyValue(IdentValue.AUTO) : new PropertyValue(CssUtil.rgbaColor(hoverFontColor)), false,
				StylesheetInfo.USER);
		getCssState().addCssDeclaration(declf, PseudoStyle.hover);
		applyCss(declf);
		layoutChildren();
		return this;
	}

	/**
	 * If called, an overlay icon is added to the button. This icon is centered
	 * by default
	 * 
	 * @param width
	 *            width to display icon
	 * @param height
	 *            to display icon
	 * @param texturePath
	 *            The path of the image to use as the icon overlay
	 */
	public Button setButtonIcon(float width, float height, String texturePath) {
		if (width != -1 && height != -1)
			icon.setPreferredDimensions(new Size(width, height));
		else
			icon.setPreferredDimensions(new Size(Unit.AUTO));
		// this.buttonWidth = width;
		// this.buttonHeight = height;
		// makeContainer(icon);
		icon.setTexture(texturePath);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * If called, an overlay icon is added to the button. This icon is centered
	 * by default
	 * 
	 * @param texturePath
	 *            The path of the image to use as the icon overlay
	 */
	public Button setButtonIcon(String texturePath) {
		setButtonIcon(-1, -1, texturePath);
		return this;
	}

	/**
	 * Set the alignment of the icon within the button. If there is text on the
	 * button, the icon will appear to the specified side of the text.
	 *
	 * @param buttonIconAlign
	 *            alignment
	 */
	public Button setButtonIconAlign(BitmapFont.Align buttonIconAlign) {
		if (icon != null)
			icon.setLayoutData(buttonIconAlign.name());
		layoutChildren();
		return this;
	}

	/**
	 * Sets the image and font color to use when the button is depressed
	 * 
	 * @param pathPressedImg
	 *            Path to the image for pressed state
	 * @param pressedFontColor
	 *            ColorRGBA to use for pressed state
	 */
	public final Button setButtonPressedInfo(String pathPressedImg, ColorRGBA pressedFontColor) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.BACKGROUND_IMAGE,
				new PropertyValue(new FSFunction("url", Arrays.asList(pathPressedImg))), false, StylesheetInfo.USER);
		getCssState().addCssDeclaration(decl, PseudoStyle.active);
		applyCss(decl);
		PropertyDeclaration declf = new PropertyDeclaration(CSSName.COLOR,
				new PropertyValue(CssUtil.rgbaColor(pressedFontColor)), false, StylesheetInfo.USER);
		getCssState().addCssDeclaration(declf, PseudoStyle.active);
		applyCss(declf);
		layoutChildren();
		return this;
	}

	public BaseElement setHoverSound(String hoverSound) {
		PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.PLAY_DURING_SHORTHAND,
				new PropertyValue(CSSPrimitiveValue.CSS_IDENT, hoverSound, hoverSound), false, StylesheetInfo.USER);
		getCssState().addCssDeclaration(decl, PseudoStyle.hover);
		applyCss(decl);
		return this;
	}

	public BaseElement setHoverVolume(float volume) {
		PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.VOLUME,
				new PropertyValue(CSSPrimitiveValue.CSS_PERCENTAGE, (int) (volume * 100f),
						String.valueOf((int) (volume * 100))),
				false, StylesheetInfo.USER);
		getCssState().addCssDeclaration(decl, PseudoStyle.hover);
		applyCss(decl);
		return this;
	}

	/**
	 * This method registers the button as a JME Control creating an interval
	 * event to be processed every time the interval limit has been reached.
	 * 
	 * See onButtonStillPressedInterval()
	 * 
	 * @param intervalsPerSecond
	 *            The number of calls to onButtonStillPressedInterval per
	 *            second.
	 */
	public Button setInterval(float intervalsPerSecond) {
		if (intervalsPerSecond > 0) {
			this.useInterval = true;
			this.intervalsPerSecond = intervalsPerSecond;
			this.trackInterval = 1 / intervalsPerSecond;
			this.addControl(this);
		} else {
			this.useInterval = false;
			this.intervalsPerSecond = intervalsPerSecond;
			this.removeControl(Button.class);
		}
		return this;
	}

	public BaseElement setPressedSound(String pressedSound) {
		PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.PLAY_DURING_SHORTHAND,
				new PropertyValue(CSSPrimitiveValue.CSS_IDENT, pressedSound, pressedSound), false, StylesheetInfo.USER);
		getCssState().addCssDeclaration(decl, PseudoStyle.active);
		applyCss(decl);
		return this;

	}

	public BaseElement setPressedVolume(float volume) {
		PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.VOLUME,
				new PropertyValue(CSSPrimitiveValue.CSS_PERCENTAGE, (int) (volume * 100f),
						String.valueOf((int) (volume * 100))),
				false, StylesheetInfo.USER);
		getCssState().addCssDeclaration(decl, PseudoStyle.active);
		applyCss(decl);
		return this;
	}

	@Override
	public void setSpatial(Spatial spatial) {
	}

	@Override
	public void update(float tpf) {
		if (isEnabled) {
			if (useInterval && isStillPressed) {
				if (initClickPause) {
					currentInitClickTrack += tpf;
					if (currentInitClickTrack >= initClickInterval) {
						initClickPause = false;
						currentInitClickTrack = 0;
					}
				} else {
					currentTrack += tpf;
					if (currentTrack >= trackInterval) {
						// if(!movedWhileLeftPressed)
						fireMouseButtonHeldEvent(
								new MouseButtonHeldEvent<BaseElement>(lastEvt, this, lastEvt.getModifiers()));
						onButtonStillPressedInterval();
						currentTrack = 0;
					}
				}
			}
		}
	}

	protected void arm() {
		isStillPressed = true;
		dirtyLayout(true, LayoutType.styling);
		layoutChildren();
	}

	protected void disarm() {
		isStillPressed = false;
		initClickPause = false;
		currentInitClickTrack = 0;
		dirtyLayout(true, LayoutType.styling);
		layoutChildren();
	}

	@Override
	public BaseElement setEnabled(boolean isEnabled) {
		if (isEnabled != this.isEnabled) {
			super.setEnabled(isEnabled);
			if (!isEnabled) {
				setHasFocus(false);
				disarm();
			} else {
				dirtyLayout(true, LayoutType.styling);
				layoutChildren();
			}
		}
		return this;
	}

	@Override
	public PseudoStyles getPseudoStyles() {
		PseudoStyles ps = super.getPseudoStyles();
		if (isStillPressed || rightPressed)
			ps = PseudoStyles.get(ps).addStyle(PseudoStyle.active);
		return ps;
	}

	@Override
	protected void onKeyboardOrMouseFocusChanged() {
		/*
		 * TODO only do this if any children are using parent pseudo styles.
		 * Button does the same thing
		 */
		dirtyLayout(true, LayoutType.styling);
	}

	protected void processMouseButtonPressed(MouseUIButtonEvent<BaseElement> evt) {
		dirtyLayout(true, LayoutType.styling);
		layoutChildren();
		initClickPause = true;
		currentInitClickTrack = 0;
	}

	protected void processMouseButtonReleased(MouseUIButtonEvent<BaseElement> evt) {
		if (buttonGroup != null)
			buttonGroup.setSelected(this);
		else {
			dirtyLayout(true, LayoutType.styling);
			layoutChildren();
		}
	}

	protected void processRightMouseButtonPressed(MouseUIButtonEvent<BaseElement> evt) {
	}

	protected void processRightMouseButtonReleased(MouseUIButtonEvent<BaseElement> evt) {
	}

	void fireMouseButtonHeldEvent(MouseButtonHeldEvent<BaseElement> evt) {
		if (mouseButtonHeldSupport != null)
			mouseButtonHeldSupport.fireEvent(evt);
	}
}
