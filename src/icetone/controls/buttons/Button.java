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

import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;

import icetone.controls.text.AbstractTextLayout;
import icetone.controls.text.TextElement;
import icetone.core.CssProcessor.PseudoStyle;
import icetone.core.CssUtil;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.LUtil;
import icetone.core.utils.BitmapTextUtil;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;
import icetone.listeners.KeyboardListener;
import icetone.listeners.MouseButtonListener;
import icetone.listeners.MouseFocusListener;
import icetone.listeners.TabFocusListener;
import icetone.style.Style;
import icetone.style.StyleManager.CursorType;

/**
 *
 * @author t0neg0d
 */
public abstract class Button extends Element
		implements Control, MouseButtonListener, MouseFocusListener, KeyboardListener, TabFocusListener {
	protected String hoverSound, pressedSound;
	protected boolean useHoverSound, usePressedSound;
	protected float hoverSoundVolume, pressedSoundVolume;
	protected Element icon;
	protected Texture hoverImg = null, pressedImg = null;
	protected ColorRGBA hoverFontColor = null, pressedFontColor = null;
	protected boolean isToggleButton = false;
	protected boolean isToggled = false;
	protected boolean isStillPressed = false;
	private boolean useInterval = false;
	private float intervalsPerSecond = 4;
	protected float trackInterval = (4 / 1000), currentTrack = 0;
	protected boolean initClickPause = false;
	protected float initClickInterval = 0.25f, currentInitClickTrack = 0;
	protected RadioButtonGroup radioButtonGroup = null;
	protected boolean isRadioButton = false;
	protected ColorRGBA originalFontColor;
	protected Vector2f hoverImgOffset, pressedImgOffset;
	// Optional LabelElement
	protected boolean useOptionalLabel = false;
	protected TextElement buttonLabel;
	protected BitmapFont.Align buttonIconAlign = BitmapFont.Align.Left;
	protected float buttonWidth;
	protected float buttonHeight;
	protected boolean usePreferredWidth = false;
	protected String styleName;

	public Button(String text) {
		this(Screen.get());
		setText(text);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public Button(ElementManager screen) {
		this(screen, LUtil.LAYOUT_SIZE);
	}

	public Button(ElementManager screen, Vector2f dimensions, String defaultImg) {
		this(screen, dimensions, screen.getStyle("Button").getVector4f("resizeBorders"), defaultImg);
	}

	public Button(ElementManager screen, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, UIDUtil.getUID(), dimensions, resizeBorders, defaultImg);
	}

	public Button(ElementManager screen, String UID) {
		this(screen, UID, LUtil.LAYOUT_SIZE);
	}

	public Button(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param dimensions
	 *            A Vector2f containing the size of the Element
	 */
	public Button(ElementManager screen, Vector2f dimensions) {
		this(screen, dimensions, screen.getStyle("Button").getVector4f("resizeBorders"),
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
	public Button(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("Button").getVector4f("resizeBorders"),
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
	 * @param resizeBorders
	 *            A Vector4f containing the border information used when
	 *            resizing the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Button
	 */
	public Button(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg);
	}

	/**
	 * Creates a new instance of the Button control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param dimensions
	 *            A Vector2f containing the size of the Element
	 */
	public Button(ElementManager screen, String UID, Vector2f dimensions) {
		this(screen, UID, dimensions, screen.getStyle("Button").getVector4f("resizeBorders"),
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
	public Button(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
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
	public Button(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		// Want to be able to shrink in width
		layoutManager = new ButtonLayout();
		setEffectZOrder(false);

		this.setFontSize(screen.getStyle("Button").getFloat("fontSize"));
		this.setFontColor(screen.getStyle("Button").getColorRGBA("fontColor"));
		this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Button").getString("textVAlign")));
		this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Button").getString("textAlign")));
		this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Button").getString("textWrap")));
		setTextPaddingByKey("Button", "textPadding");

		if (getUseLocalTexture()) {
			// setColorMap(screen.getStyle("Button").getString("defaultImg"));
			boolean tile = false;
			try {
				tile = screen.getStyle("Button").getBoolean("tileImages");
			} catch (Exception ex) {
			}
			this.setTileImage(tile);
		}

		if (screen.getStyle("Button").getString("hoverImg") != null) {
			setButtonHoverInfo(screen.getStyle("Button").getString("hoverImg"),
					screen.getStyle("Button").getColorRGBA("hoverColor"));
		}
		if (screen.getStyle("Button").getString("pressedImg") != null) {
			setButtonPressedInfo(screen.getStyle("Button").getString("pressedImg"),
					screen.getStyle("Button").getColorRGBA("pressedColor"));
		}

		originalFontColor = fontColor.clone();

		hoverSound = screen.getStyle("Button").getString("hoverSound");
		useHoverSound = screen.getStyle("Button").getBoolean("useHoverSound");
		hoverSoundVolume = screen.getStyle("Button").getFloat("hoverSoundVolume");
		pressedSound = screen.getStyle("Button").getString("pressedSound");
		usePressedSound = screen.getStyle("Button").getBoolean("usePressedSound");
		pressedSoundVolume = screen.getStyle("Button").getFloat("pressedSoundVolume");

		populateEffects("Button");
		if (Screen.isAndroid()) {
			removeEffect(Effect.EffectEvent.Hover);
			removeEffect(Effect.EffectEvent.TabFocus);
			removeEffect(Effect.EffectEvent.LoseTabFocus);
		}

		buttonLabel = new TextElement(screen, Vector2f.ZERO, getDimensions(), screen.getDefaultGUIFont()) {
			@Override
			public void onUpdate(float tpf) {
			}

			@Override
			public void onEffectStart() {
			}

			@Override
			public void onEffectStop() {
			}
		};
		buttonLabel.setIgnoreMouse(true);
		buttonLabel.setIsResizable(false);
		buttonLabel.setIsMovable(false);
		buttonLabel.setFontSize(fontSize);
		buttonLabel.setFontColor(fontColor);
		buttonLabel.setUseTextClipping(true);
		buttonLabel.setTextAlign(textAlign);
		buttonLabel.setTextVAlign(textVAlign);
		buttonLabel.addClippingLayer(this);

		/*
		 * String defaultIcon =
		 * screen.getStyle("Button").getString("defaultIcon"); if (defaultIcon
		 * != null) { Vector2f size = getDimensions(); try {
		 * size.set(screen.getStyle("Button").getVector2f("defaultIconSize")); }
		 * catch (Exception ex) { } setButtonIcon(size.x, size.y,defaultIcon); }
		 */
	}

	@Override
	protected PseudoStyle[] getPseudoStyles() {
		return new PseudoStyle[] { PseudoStyle.active, PseudoStyle.hover };
	}

	public boolean isUsePreferredWidth() {
		return usePreferredWidth;
	}

	@Override
	public void setSpatial(Spatial spatial) {
	}

	public void controlIsEnabledHook(boolean isEnabled) {
		if (!isEnabled) {
			if (isToggled)
				runPressedEffect(false);
			else
				runResetEffect();
		} else {
			runLoseFocusEffect();
		}
	}

	/**
	 * Clears current hover and pressed images set by Style defines
	 */
	public void clearAltImages() {
		setButtonHoverInfo(null, null);
		setButtonPressedInfo(null, null);
	}

	/**
	 * Sets if the button is to interact as a Toggle Button Click once to
	 * activate / Click once to deactivate
	 * 
	 * @param isToggleButton
	 *            boolean
	 */
	public void setIsToggleButton(boolean isToggleButton) {
		this.isToggleButton = isToggleButton;
	}

	/**
	 * Returns if the Button is flagged as a Toggle Button
	 * 
	 * @return boolean isToggleButton
	 */
	public boolean getIsToggleButton() {
		return this.isToggleButton;
	}

	/**
	 * Sets if the button is to interact as a Radio Button Click once to
	 * activate - stays active
	 * 
	 * @param isRadioButton
	 *            boolean
	 */
	public void setIsRadioButton(boolean isRadioButton) {
		this.isRadioButton = isRadioButton;
	}

	/**
	 * Returns if the Button is flagged as a Toggle Button
	 * 
	 * @return boolean isRadioButton
	 */
	public boolean getIsRadioButton() {
		return this.isRadioButton;
	}

	/**
	 * Set a toggle button state to toggled/untoggled
	 * 
	 * @param isToggled
	 *            boolean
	 */
	public void setIsToggledNoCallback(boolean isToggled) {
		if (this.isToggled == isToggled)
			return;

		this.isToggled = isToggled;

		if (pressedImg != null && isToggled) {
			runPressedEffect(false);
		} else {
			runResetEffect();
		}

		if (radioButtonGroup != null) {
			if (isToggled)
				radioButtonGroup.setSelected(this);
		}
	}

	/**
	 * Set a toggle button state to toggled/untoggled and calls the user left
	 * mouse button event methods
	 * 
	 * @param isToggled
	 *            boolean
	 */
	public void setIsToggled(boolean isToggled) {
		if (isToggled == this.isToggled)
			return;
		this.isToggled = isToggled;

		if (pressedImg != null && isToggled) {
			runPressedEffect(false);
		} else {
			runResetEffect();
		}

		MouseButtonEvent evtd = new MouseButtonEvent(0, true, 0, 0);
		MouseButtonEvent evtu = new MouseButtonEvent(0, false, 0, 0);
		onButtonMouseLeftDown(evtd, isToggled);
		onButtonMouseLeftUp(evtu, isToggled);
		if (radioButtonGroup != null) {
			if (isToggled)
				radioButtonGroup.setSelected(this);
		}
		evtu.setConsumed();
		evtd.setConsumed();
	}

	/**
	 * Returns the current toggle state of the Button if the Button has been
	 * flagged as isToggle
	 * 
	 * @return boolean isToggle
	 */
	public boolean getIsToggled() {
		return this.isToggled;
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
	public final void setButtonHoverInfo(String pathHoverImg, ColorRGBA hoverFontColor) {
		if (pathHoverImg != null) {
			if ((!screen.getUseTextureAtlas() && !getUseLocalAtlas()) || getUseLocalTexture()) {
				try {
					this.hoverImg = app.getAssetManager().loadTexture(pathHoverImg);
					this.hoverImg.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
					this.hoverImg.setMagFilter(Texture.MagFilter.Nearest);
					this.hoverImg.setWrap(Texture.WrapMode.Repeat);
				} catch (Exception ex) {
				}
			} else {
				this.hoverImg = this.getElementTexture();
				hoverImgOffset = getAtlasTextureOffset(screen.parseAtlasCoords(pathHoverImg));
			}
		} else {
			this.hoverImg = null;
		}
		if (hoverFontColor != null) {
			this.hoverFontColor = hoverFontColor;
		} else {
			this.hoverFontColor = null;
		}
	}

	/**
	 * Returns the Texture used when button has mouse focus
	 * 
	 * @return Texture
	 */
	public Texture getButtonHoverImg() {
		return this.hoverImg;
	}

	protected void runHoverEffect(boolean audio) {
		if (hoverImg != null) {
			Effect effect = getEffect(Effect.EffectEvent.Hover);
			if (effect != null) {
				if (useHoverSound && screen.getUseUIAudio() && audio) {
					effect.setAudioFile(hoverSound);
					effect.setAudioVolume(hoverSoundVolume);
				}
				effect.setBlendImage(hoverImg);
				if ((screen.getUseTextureAtlas() || getUseLocalAtlas()) && !getUseLocalTexture())
					effect.setBlendImageOffset(hoverImgOffset);
				screen.getEffectManager().applyEffect(effect);
			}
		}
		if (hoverFontColor != null) {
			if (!useOptionalLabel)
				setFontColor(hoverFontColor);
			else
				buttonLabel.setFontColor(hoverFontColor);
		}
	}

	/**
	 * Sets the image and font color to use when the button is depressed
	 * 
	 * @param pathPressedImg
	 *            Path to the image for pressed state
	 * @param pressedFontColor
	 *            ColorRGBA to use for pressed state
	 */
	public final void setButtonPressedInfo(String pathPressedImg, ColorRGBA pressedFontColor) {
		if (pathPressedImg != null) {
			if ((!screen.getUseTextureAtlas() && !getUseLocalAtlas()) || getUseLocalTexture()) {
				try {
					this.pressedImg = app.getAssetManager().loadTexture(pathPressedImg);
					this.pressedImg.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
					this.pressedImg.setMagFilter(Texture.MagFilter.Nearest);
					this.pressedImg.setWrap(Texture.WrapMode.Repeat);
				} catch (Exception ex) {
				}
			} else {
				this.pressedImg = this.getElementTexture();
				pressedImgOffset = getAtlasTextureOffset(screen.parseAtlasCoords(pathPressedImg));
			}
		} else {
			this.pressedImg = null;
		}
		if (pressedFontColor != null) {
			this.pressedFontColor = pressedFontColor;
		} else {
			this.pressedFontColor = null;
		}
	}

	/**
	 * Returns the texture to be used when the button is depressed
	 * 
	 * @return Texture
	 */
	public Texture getButtonPressedImg() {
		return this.pressedImg;
	}

	protected void runPressedEffect(boolean audio) {
		if (pressedImg != null) {
			Effect effect = getEffect(Effect.EffectEvent.Press);
			if (effect != null) {
				if (usePressedSound && screen.getUseUIAudio() && audio) {
					effect.setAudioFile(pressedSound);
					effect.setAudioVolume(pressedSoundVolume);
				}
				effect.setBlendImage(pressedImg);
				if ((screen.getUseTextureAtlas() || getUseLocalAtlas()) && !getUseLocalTexture())
					effect.setBlendImageOffset(pressedImgOffset);
				screen.getEffectManager().applyEffect(effect);
			}
		}
		if (pressedFontColor != null) {
			if (!useOptionalLabel)
				setFontColor(pressedFontColor);
			else
				buttonLabel.setFontColor(pressedFontColor);
		}
	}

	protected void runLoseFocusEffect() {
		Effect effect = getEffect(Effect.EffectEvent.LoseFocus);
		if (effect != null) {
			effect.setBlendImage(getElementTexture());
			if ((screen.getUseTextureAtlas() || getUseLocalAtlas()) && !getUseLocalTexture())
				effect.setBlendImageOffset(new Vector2f(0, 0));
			screen.getEffectManager().applyEffect(effect);
		}
		if (originalFontColor != null) {
			if (!useOptionalLabel)
				setFontColor(originalFontColor);
			else
				buttonLabel.setFontColor(originalFontColor);
		}
	}

	protected void runResetEffect() {
		Effect effect = getEffect(Effect.EffectEvent.Press);
		if (effect != null) {
			effect.setBlendImage(getElementTexture());
			if ((screen.getUseTextureAtlas() || getUseLocalAtlas()) && !getUseLocalTexture())
				effect.setBlendImageOffset(new Vector2f(0, 0));
			screen.getEffectManager().applyEffect(effect);
		}
		if (originalFontColor != null) {
			if (!useOptionalLabel)
				setFontColor(originalFontColor);
			else
				buttonLabel.setFontColor(originalFontColor);
		}
	}

	/**
	 * If called, an overlay icon is added to the button. This icon is centered
	 * by default
	 * 
	 * @param texturePath
	 *            The path of the image to use as the icon overlay
	 */
	public void setButtonIcon(String texturePath) {
		setButtonIcon(-1, -1, texturePath);
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
	public void setButtonIcon(float width, float height, String texturePath) {
		this.buttonWidth = width;
		this.buttonHeight = height;
		// if (icon != null) {
		// if (icon.getParent() != null) {
		// removeChild(icon);
		// // elementChildren.remove(icon.getUID());
		// // icon.removeFromParent();
		// }
		// icon = null;
		// }
		if (icon == null) {
			icon = new Element(screen, this.getUID() + ":btnIcon", new Vector4f(0, 0, 0, 0), texturePath);
			icon.setIgnoreMouse(true);
			this.addChild(icon);
		} else {
			icon.setTexture(texturePath);
		}
		if (screen.getUseTextureAtlas() || this.getUseLocalAtlas())
			icon.setTextureAtlasImage(icon.getElementTexture(), texturePath);

		dirtyLayout(false);
		layoutChildren();
	}

	public Element getButtonIcon() {
		return this.icon;
	}

	@Override
	public void onMouseButton(MouseUIButtonEvent evt) {
		if (evt.isLeft()) {
			if (evt.isPressed()) {
				if (isEnabled) {
					if (isToggleButton) {
						if (isToggled) {
							if (!isRadioButton)
								isToggled = false;
						} else {
							isToggled = true;
						}
					}
					runPressedEffect(true);
					isStillPressed = true;
					initClickPause = true;
					currentInitClickTrack = 0;
					onButtonMouseLeftDown(evt, isToggled);
				}
				evt.setConsumed();
			} else {
				if (isEnabled) {
					if (!isToggleButton) {
						if (getHasFocus()) {
							runLoseFocusEffect();
							runHoverEffect(false);
						} else {
							runLoseFocusEffect();
						}
					} else {
						if (!isToggled) {
							runLoseFocusEffect();
							runHoverEffect(false);
						}
					}
					isStillPressed = false;
					initClickPause = false;
					currentInitClickTrack = 0;
					onButtonMouseLeftUp(evt, isToggled);
					if (radioButtonGroup != null)
						radioButtonGroup.setSelected(this);
				}
				evt.setConsumed();
			}
		} else if (evt.isRight()) {
			if (evt.isPressed()) {

				if (isEnabled) {
					onButtonMouseRightDown(evt, isToggled);
					if (screen.getUseToolTips()) {

					}
				}
				evt.setConsumed();
			} else {
				if (isEnabled) {
					onButtonMouseRightUp(evt, isToggled);
				}
				evt.setConsumed();
			}
		}
	}

	public void setRadioButtonGroup(RadioButtonGroup radioButtonGroup) {
		this.radioButtonGroup = radioButtonGroup;
		this.isToggleButton = true;
		this.isRadioButton = true;
	}

	@Override
	public void onGetFocus(MouseMotionEvent evt) {
		if (isEnabled) {
			if (!getHasFocus()) {
				if (!isToggled) {
					runHoverEffect(true);
				}
				screen.setCursor(CursorType.HAND);
				onButtonFocus(evt);
				if (screen.getUseToolTips()) {

				}
			}
			setHasFocus(true);
		}
	}

	@Override
	public void onLoseFocus(MouseMotionEvent evt) {
		if (isEnabled) {
			if (getHasFocus()) {
				if (!isToggled) {
					runLoseFocusEffect();
				}
				screen.setCursor(CursorType.POINTER);
				onButtonLostFocus(evt);
			}
			setHasFocus(false);
		}
	}

	/**
	 * Enables/disbale hover effect sound
	 * 
	 * @param useHoverSound
	 */
	public void setUseButtonHoverSound(boolean useHoverSound) {
		this.useHoverSound = useHoverSound;
	}

	/**
	 * Enable/disable pressed effect sound
	 * 
	 * @param usePressedSound
	 */
	public void setUseButtonPressedSound(boolean usePressedSound) {
		this.usePressedSound = usePressedSound;
	}

	public abstract void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled);

	public abstract void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled);

	public abstract void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled);

	public abstract void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled);

	public abstract void onButtonFocus(MouseMotionEvent evt);

	public abstract void onButtonLostFocus(MouseMotionEvent evt);

	/**
	 * Abstract method for handling interval updates while the button is still
	 * pressed
	 * 
	 * NOTE: This is only called if the button's setInterval method has been
	 * previously called
	 */
	public void onButtonStillPressedInterval() {
	}

	/**
	 * Returns if the button is still pressed
	 * 
	 * @return boolean
	 */
	public boolean getIsStillPressed() {
		return this.isStillPressed;
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		return this;
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {

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
						onButtonStillPressedInterval();
						currentTrack = 0;
					}
				}
			}
		}
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
	 * This method registers the button as a JME Control creating an interval
	 * event to be processed every time the interval limit has been reached.
	 * 
	 * See onButtonStillPressedInterval()
	 * 
	 * @param intervalsPerSecond
	 *            The number of calls to onButtonStillPressedInterval per
	 *            second.
	 */
	public void setInterval(float intervalsPerSecond) {
		if (intervalsPerSecond > 0) {
			this.useInterval = true;
			this.intervalsPerSecond = intervalsPerSecond;
			this.trackInterval = (float) (1 / intervalsPerSecond);
			this.addControl(this);
		} else {
			this.useInterval = false;
			this.intervalsPerSecond = intervalsPerSecond;
			this.removeControl(Button.class);
		}
	}

	// Tab focus
	@Override
	public void setTabFocus() {
		screen.setKeyboardElement(this);
		if (isEnabled) {
			Effect effect = getEffect(Effect.EffectEvent.TabFocus);
			if (effect != null) {
				effect.setColor(ColorRGBA.DarkGray);
				screen.getEffectManager().applyEffect(effect);
			}
		}
	}

	@Override
	public void resetTabFocus() {
		screen.setKeyboardElement(null);
		if (isEnabled) {
			if (!getIsToggled()) {
				Effect effect = getEffect(Effect.EffectEvent.LoseTabFocus);
				if (effect != null) {
					effect.setColor(ColorRGBA.White);
					screen.getEffectManager().applyEffect(effect);
				}
			}
		}
	}

	// Default keyboard interaction
	@Override
	public void onKeyPress(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
			if (isEnabled && getParent() != null)
				onMouseButton(new MouseUIButtonEvent(0, true, 0, 0, 0, 0, 1, 0, null));
		}
	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_SPACE) {
			if (isEnabled && getParent() != null)
				onMouseButton(new MouseUIButtonEvent(0, false, 0, 0, 0, 0, 1, 0, null));
		}
	}

	@Override
	public void setText(String text) {
		this.text = text;
		if (textElement == null) {
			textElement = new BitmapText(font, false);
			 //textElement = new LabelElement(screen, Vector2f.ZERO);
//			textElement = new AnimText(screen.getApplication().getAssetManager(), font);
			textElement.setBox(new Rectangle(0, 0, getDimensions().x, getDimensions().y));
//			textElement.setBounds(getDimensions().x, getDimensions().y);
		}
//		textElement.setTextWrap(textWrap);
//		textElement.setTextAlign(textAlign);
//		textElement.setTextVAlign(textVAlign);
//		textElement.setFontSize(fontSize);
		textElement.setLineWrapMode(textWrap);
		textElement.setAlignment(textAlign);
		textElement.setVerticalAlignment(textVAlign);
		textElement.setSize(fontSize);
		textElement.setColor(fontColor);

		// if (textVAlign == BitmapFont.VAlign.Center) {
		// textElement.setVerticalAlignment(BitmapFont.VAlign.Top);
		// centerTextVertically(text);
		// }

		textElement.setText(text);
		updateTextElement();
		if (textElement.getParent() == null) {
			this.attachChild(textElement);
		}
		dirtyLayout(false);
		layoutChildren();
	}

	public void setLabelText(String text) {
		this.useOptionalLabel = true;
		// buttonLabel.setSizeToText(true);
		buttonLabel.setDimensions(getDimensions());
		buttonLabel.getAnimText().setBounds(getDimensions());
		buttonLabel.setText(text);
		if (buttonLabel.getParent() == null) {
			addChild(buttonLabel);
		}
	}

	public void setFontColor(ColorRGBA fontColor, boolean makeDefault) {
		if (!useOptionalLabel)
			super.setFontColor(fontColor);
		else
			buttonLabel.setFontColor(fontColor);
		if (makeDefault)
			originalFontColor = fontColor.clone();
	}

	public void setStyles(String styleName) {
		LUtil.removeEffects(this);

		Style style = screen.getStyle(styleName);
		if (style == null) {
			throw new IllegalArgumentException("No such style " + styleName);
		}

		// images and state colours
		String img = style.getString("defaultImg");

		if (img != null)
			setTexture(img);
		if (style.getString("hoverImg") != null) {
			setButtonHoverInfo(style.getString("hoverImg"), style.getColorRGBA("hoverColor"));
		}
		if (style.getString("pressedImg") != null) {
			setButtonPressedInfo(style.getString("pressedImg"), style.getColorRGBA("pressedColor"));
		}

		String fn = style.getString("fontName");
		if (fn != null)
			setFont(screen.getStyle("Font").getString(fn));

		// fonts and text
		setFontSize(style.getFloat("fontSize"));
		// setFont((Screen)screen.getDefaultGUIFont());
		setFontColor(style.getColorRGBA("fontColor"));
		setTextVAlign(BitmapFont.VAlign.valueOf(style.getString("textVAlign")));
		setTextAlign(BitmapFont.Align.valueOf(style.getString("textAlign")));
		setTextWrap(LineWrapMode.valueOf(style.getString("textWrap")));
		setTextPaddingByKey(styleName, "textPadding");
		// buttonTextInsets = style.getFloat("buttonTextInsets");

		// borders
		borders.set(style.getVector4f("resizeBorders"));

		// audio
		hoverSound = style.getString("hoverSound");
		useHoverSound = style.getBoolean("useHoverSound");
		hoverSoundVolume = style.getFloat("hoverSoundVolume");
		pressedSound = style.getString("pressedSound");
		usePressedSound = style.getBoolean("usePressedSound");
		pressedSoundVolume = style.getFloat("pressedSoundVolume");

		// Fx
		populateEffects(styleName);
		if (Screen.isAndroid()) {
			removeEffect(Effect.EffectEvent.Hover);
			removeEffect(Effect.EffectEvent.TabFocus);
			removeEffect(Effect.EffectEvent.LoseTabFocus);
		}

		// defaultSize = style.getVector2f(“defaultSize”);
		setDimensions(getOrgDimensions());
		getModel().updateDimensions(getWidth(), getHeight());

		// TODO
		rebuildModel();

		originalFontColor = fontColor.clone();
	}

	public BitmapFont.Align getButtonIconAlign() {
		return buttonIconAlign;
	}

	/**
	 * Set the alignment of the icon within the button. If there is text on the
	 * button, the icon will appear to the specified side of the text.
	 *
	 * @param buttonIconAlign
	 *            alignment
	 */
	public void setButtonIconAlign(BitmapFont.Align buttonIconAlign) {
		this.buttonIconAlign = buttonIconAlign;
		layoutChildren();
	}

	protected void applyCss(PseudoStyle ps, PropertyDeclaration decl) {
		if (PseudoStyle.hover == ps) {
			if (decl.getPropertyName().equals("background-image")) {
				setButtonHoverInfo(decl.getValue().getStringValue(), null);
			} else if (decl.getPropertyName().equals("background-color")) {
				setButtonHoverInfo(null, CssUtil.toColor(decl.getValue().getCssText()));
			}
		} else if (PseudoStyle.active == ps) {
			if (decl.getPropertyName().equals("background-image")) {
				setButtonPressedInfo(decl.getValue().getStringValue(), null);
			} else if (decl.getPropertyName().equals("background-color")) {
				setButtonPressedInfo(null, CssUtil.toColor(decl.getValue().getCssText()));
			}
		} else {
			super.applyCss(ps, decl);
		}
	}

	/**
	 * Fix for BitmapFont.VAlign
	 * 
	 * @param text
	 */
	private void centerTextVertically(String text) {
		float height = BitmapTextUtil.getTextLineHeight(this, text);
		setTextPosition(getTextPosition().x, (int) (getHeight() / 2 - ((height - (height * .1f)) / 2)));
	}

	protected class ButtonLayout extends AbstractTextLayout {

		private float tx;
		private float ty;

		public Vector2f minimumSize(Element parent) {
			// Vector2f ps = super.minimumSize(parent);
			// if (icon != null) {
			// float bh = buttonHeight;
			// float bw = buttonWidth;
			// if (bh == -1) {
			// bh = ps.y / 2f;
			// }
			// if (bw == -1) {
			// bw = ps.y / 2f;
			// }
			// ps.x += bw;
			// ps.y = Math.max(ps.y, bh + parent.getTextPaddingVec().z +
			// parent.getTextPaddingVec().w);
			// }
			return preferredSize(parent);
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f ps = LUtil.getPreferredTextSize(parent);

			float bh = buttonHeight;
			float bw = buttonWidth;
			if (bh == -1) {
				bh = ps.y / 2f;
			}
			if (bw == -1) {
				bw = ps.y / 2f;
			}

			if (icon != null) {
				if ((getTextElement() == null || text.equals(""))) {
					ps.x = bw + textPadding.x + textPadding.y;
					ps.y = Math.max(ps.y, bh + textPadding.z + textPadding.w);
				} else {
					if (buttonIconAlign == Align.Center) {
						ps.x = Math.max(ps.x, bw + textPadding.z + textPadding.y);
						ps.y += bh;
					} else {
						ps.x += bw;
						ps.y = Math.max(ps.y, bh + textPadding.z + textPadding.w);
					}
				}
			} else if (usePreferredWidth) {
				ps.x = getPreferredWidth();
			}
			return ps;
		}

		public void layout(Element childElement) {
			final String text1 = getText();
			if (icon != null) {
				// Determine icon size
				Vector2f sz = new Vector2f(buttonWidth, buttonHeight);
				if (buttonWidth == -1) {
					sz.x = Math.min(childElement.getHeight(), childElement.getWidth()) / 2f;
				}
				if (buttonHeight == -1) {
					sz.y = Math.min(childElement.getHeight(), childElement.getWidth()) / 2f;
				}

				Vector2f pos = new Vector2f();

				if (text1 == null || text1.equals("")) {
					pos.set((getWidth() / 2) - (sz.x / 2), (childElement.getHeight() / 2f) - (sz.y / 2f));
				} else {
					switch (buttonIconAlign) {
					case Left:
						tx = sz.x;
						pos.set(textPadding.x, (childElement.getHeight() / 2f) - (sz.y / 2f));
						break;
					case Right:
						tx = 0;
						pos.set(childElement.getWidth() - sz.x - textPadding.y,
								(childElement.getHeight() / 2) - (sz.y / 2));
						break;
					default:
						tx = 0;
						ty = 0;
						switch (textVAlign) {
						case Top:
							pos.set((childElement.getWidth() / 2f) - (sz.x / 2f),
									childElement.getHeight() - sz.y - childElement.getTextPaddingVec().w);
							break;
						case Bottom:
							pos.set((getWidth() / 2f) - (sz.x / 2f), childElement.getTextPaddingVec().z);
							break;
						default:
							pos.set((childElement.getWidth() / 2f) - (sz.x / 2f),
									(childElement.getHeight() / 2f) - (sz.y / 2f));
							break;
						}
						break;
					}
				}

				LUtil.setBounds(icon, pos.x, pos.y, sz.x, sz.y);
			}

			if (textElement != null) {
//				if (textVAlign == BitmapFont.VAlign.Center) {
//					// This is a work around for the bad vertical centering you
//					// get
//					// from BitmapText (standard Button does a similar thing).
//					// The text
//					// is instead aligned to top, and it's position adjusted by
//					// the
//					// amount BitmapText would have offset it.
//					textElement.setVerticalAlignment(BitmapFont.VAlign.Top);
//					float height = BitmapTextUtil.getTextLineHeight(childElement, text);
//					setTextPosition(tx,
//							ty + (int) (((getHeight() - textPadding.w - textPadding.z) / 2f) - (height / 2f)));
//				} else {
//					setTextPosition(tx, ty);
//				}
				setTextPosition(tx, ty);
			}

			updateTextElement();
			layoutControl();
		}

		private float getPreferredWidth() {
			Vector2f bSz = icon == null ? Vector2f.ZERO : new Vector2f(buttonWidth, buttonHeight);
			float preferredWidth = font.getLineWidth(getText()) + (textPadding.x + textPadding.y);
			if (!buttonIconAlign.equals(BitmapFont.Align.Center)) {
				preferredWidth += bSz.x;
			}
			return preferredWidth;
		}
	}
}
