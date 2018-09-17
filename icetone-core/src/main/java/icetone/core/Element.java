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
package icetone.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;

import icetone.core.Layout.LayoutType;
import icetone.core.Measurement.Unit;
import icetone.core.layout.DefaultLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.GUIConstants;
import icetone.css.AudioEffect;
import icetone.css.ControlEffect;
import icetone.css.CssEffect;
import icetone.css.CssExtensions;
import icetone.css.CssProcessor.PseudoStyle;
import icetone.css.CssUtil;
import icetone.css.StyleManager.CursorType;
import icetone.effects.Effect.EffectDirection;
import icetone.effects.EffectChannel;
import icetone.effects.EffectFactory;
import icetone.fonts.FontSpec;
import icetone.framework.animation.Interpolation;

/**
 * Extension of {@link BaseElement} that supports styling using standard CSS.
 * Nearly all visual aspects of an element may be styled this way using common
 * CSS dialect with a few extensions. Extended styles begin with '-it-' (see
 * example style packs for examples).
 * <p>
 * CSS styling incurs some performance penalty. If you do not need it, instead
 * use the basic {@link BaseElement}. Note, once an {@link BaseElement} is
 * encountered during styling, that element will not be traversed for further
 * styled elements.
 * <p>
 * Most of the setters (e.g. {@link #setAtlas(Vector4f)},
 * {@link #setTexture(String)}, and so on) are replaced when using a styled
 * element. Instead of acting directly on the Element attributes, CSS styles are
 * injected to a local element specific stylesheet and the appropriate style
 * rules applied. This allows an element to default to the CSS provided style,
 * but allow specific attributes to be changed programmatically while still
 * maintaining cascading styles. In some cases, setting to <code>null</code> or
 * another default primitive value value may return the CSS provided style.
 * 
 * @author rockfire
 */
public class Element extends BaseElement implements StyledNode<BaseElement, BaseElement> {

	private static Map<Class<? extends BaseElement>, List<String>> classNames = new HashMap<>();
	protected String styleClass;
	protected boolean useParentPseudoStyles;
	private String css;
	private CssState cssState;

	private List<Stylesheet> stylesheets;

	public Element() {
		super();
	}

	public Element(BaseScreen screen) {
		super(screen);
	}

	public Element(BaseScreen screen, Layout<?, ?> layoutManager) {
		super(screen, layoutManager);
	}

	public Element(BaseScreen screen, Size dimensions) {
		super(screen, dimensions);
	}

	public Element(BaseScreen screen, String styleId) {
		super(screen, styleId, null, null);
	}

	public Element(BaseScreen screen, String styleId, Vector2f position, Size dimensions) {
		super(screen, styleId, position, dimensions, null, null);
	}

	public Element(BaseScreen screen, Vector2f position, Size dimensions) {
		super(screen, null, position, dimensions, null, null);
	}

	public Element(Layout<?, ?> layoutManager) {
		super(layoutManager);
	}

	public Element(String texture) {
		super(texture);
	}

	/**
	 * Get a string representation of the current styles applied to this
	 * element. Useful for debugging.
	 * 
	 * @return current styles
	 */
	public String getCurrentStyles() {
		return cssState.getCurrentStyles();
	}

	/**
	 * Add a new <i>Style Class</i> to this element. The style will be added to
	 * end of the list of classes leaving existing classes in place. If the
	 * provided class name already exists, it will NOT be added.
	 * 
	 * @param styleClass
	 *            style class to add to list
	 * @return this
	 */
	public Element addStyleClass(String styleClass) {
		styleClass = styleClass.trim();
		List<String> arr = this.styleClass == null || this.styleClass.length() == 0 ? new ArrayList<>()
				: new ArrayList<String>(Arrays.asList((this.styleClass == null ? "" : this.styleClass).split("\\s+")));
		for (String c : styleClass.split("\\s+")) {
			if (!arr.contains(c)) {
				arr.add(c);
			}
		}
		this.styleClass = String.join(" ", arr);
		dirtyLayout(true, LayoutType.reset);
		layoutChildren();
		return this;
	}

	/**
	 * Add a new style that applies to this element and all of it's children.
	 * 
	 * @param sheet
	 *            stylesheet
	 * @return this
	 */
	public BaseElement addStylesheet(Stylesheet sheet) {
		if (stylesheets == null)
			stylesheets = new ArrayList<>();
		stylesheets.add(sheet);
		dirtyLayout(true, LayoutType.reset);
		layoutChildren();
		return this;
	}

	@Override
	public void applyCss(PropertyDeclaration decl) {
		// if (ps == null) {
		String n = decl.getPropertyName();
		CSSPrimitiveValue v = decl.getValue();
		CSSName cssName = decl.getCSSName();
		if (cssName.equals(CssExtensions.TEXT) || cssName.equals(CSSName.CONTENT)) {
			if (cssName.equals(CSSName.CONTENT)) {
				LOG.warning(String.format(
						"Usage of 'content' CSS attribute. This is deprecated as it does not work correctly (it is intended for use as a 'pseudo element'). Use '-it-text' instead. Element is %s.",
						toString()));
			}
			String text = v.getStringValue();
			if (!Objects.equals(text, this.text)) {
				this.text = text;
				text = formatText(text);
				if (textElement != null && text == null) {
					removeTextElement();
				} else if (textElement != null && text != null) {
					textElement.setText(text);
					dirtyLayout(false, LayoutType.boundsChange());
				} else if (text != null) {
					if (isContainerOnly())
						makeNonContainer();
					createTextElement();
				}
			}
		} else if (cssName == CSSName.TOP) {
			if (elementParent == null || elementParent.layoutManager == null
					|| !elementParent.layoutManager.positionsElement(this)) {
				setValign(VAlign.Top);
				Position was = position.clone();
				if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
						&& decl.asIdentValue() == IdentValue.AUTO) {
					position.y = 0;
					position.yUnit = Unit.AUTO;
				} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
						&& decl.getValue().getCssText().endsWith("%")) {
					position.y = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
					position.yUnit = Unit.PERCENT;
				} else {
					position.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
					position.yUnit = Unit.PX;
				}
				if (!Objects.equals(position, was))
					dirtyLayout(false, LayoutType.boundsChange());
			}
		} else if (cssName == CSSName.LEFT) {
			if (elementParent == null || elementParent.layoutManager == null
					|| !elementParent.layoutManager.positionsElement(this)) {
				setAlign(Align.Left);
				Position was = position.clone();
				if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
						&& decl.asIdentValue() == IdentValue.AUTO) {
					position.x = 0;
					position.xUnit = Unit.AUTO;
				} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
						&& decl.getValue().getCssText().endsWith("%")) {
					position.x = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
					position.xUnit = Unit.PERCENT;
				} else {
					position.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
					position.xUnit = Unit.PX;
				}
				if (!Objects.equals(position, was))
					dirtyLayout(false, LayoutType.boundsChange());
			}
		} else if (cssName == CSSName.RIGHT) {
			if (elementParent != null
					&& (elementParent.layoutManager == null || !elementParent.layoutManager.positionsElement(this))) {
				setAlign(Align.Right);
				Position was = position.clone();
				if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
						&& decl.asIdentValue() == IdentValue.AUTO) {
					position.x = 0;
					position.xUnit = Unit.AUTO;
				} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
						&& decl.getValue().getCssText().endsWith("%")) {
					position.x = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
					position.xUnit = Unit.PERCENT;
				} else {
					position.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
					position.xUnit = Unit.PX;
				}
				if (!Objects.equals(position, was))
					dirtyLayout(false, LayoutType.boundsChange());
			}
		} else if (cssName == CSSName.BOTTOM) {
			if (elementParent != null
					&& (elementParent.layoutManager == null || !elementParent.layoutManager.positionsElement(this))) {
				setValign(VAlign.Bottom);
				Position was = position.clone();
				if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
						&& decl.asIdentValue() == IdentValue.AUTO) {
					position.y = 0;
					position.yUnit = Unit.AUTO;
				} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
						&& decl.getValue().getCssText().endsWith("%")) {
					position.y = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
					position.yUnit = Unit.PERCENT;
				} else {
					position.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
					position.yUnit = Unit.PX;
				}
				if (!Objects.equals(position, was))
					dirtyLayout(false, LayoutType.boundsChange());
			}
		} else if (cssName == CSSName.TEXT_INDENT) {
			indent = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.MIN_WIDTH) {
			if (minDimensions == null)
				minDimensions = new Size(Unit.AUTO);
			Size was = minDimensions.clone();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				minDimensions.x = 0;
				minDimensions.xUnit = Unit.AUTO;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				minDimensions.x = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				minDimensions.xUnit = Unit.PERCENT;
			} else {
				minDimensions.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				minDimensions.xUnit = Unit.PX;
			}
			if (!Objects.equals(minDimensions, was))
				dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.MIN_HEIGHT) {
			if (minDimensions == null)
				minDimensions = new Size(Unit.AUTO);
			Size was = minDimensions.clone();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				minDimensions.y = 0;
				minDimensions.yUnit = Unit.AUTO;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				minDimensions.y = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				minDimensions.yUnit = Unit.PERCENT;
			} else {
				minDimensions.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				minDimensions.yUnit = Unit.PX;
			}
			if (!Objects.equals(minDimensions, was))
				dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.MAX_WIDTH) {
			if (maxDimensions == null)
				maxDimensions = new Size(Unit.AUTO);
			Size was = maxDimensions.clone();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				maxDimensions.x = 0;
				maxDimensions.xUnit = Unit.AUTO;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				maxDimensions.x = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				maxDimensions.xUnit = Unit.PERCENT;
			} else {
				maxDimensions.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				maxDimensions.xUnit = Unit.PX;
			}
			if (!Objects.equals(maxDimensions, was))
				dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.MAX_HEIGHT) {
			if (maxDimensions == null)
				maxDimensions = new Size(Unit.AUTO);
			Size was = maxDimensions.clone();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				maxDimensions.y = 0;
				maxDimensions.yUnit = Unit.AUTO;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				maxDimensions.y = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				maxDimensions.yUnit = Unit.PERCENT;
			} else {
				maxDimensions.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				maxDimensions.yUnit = Unit.PX;
			}
			if (!Objects.equals(maxDimensions, was))
				dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.WIDTH) {
			if (prefDimensions == null)
				prefDimensions = new Size(Unit.AUTO);
			Size was = prefDimensions.clone();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				prefDimensions.x = 0;
				prefDimensions.xUnit = Unit.AUTO;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				prefDimensions.x = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				prefDimensions.xUnit = Unit.PERCENT;
			} else {
				prefDimensions.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				prefDimensions.xUnit = Unit.PX;
			}
			if (!Objects.equals(prefDimensions, was))
				dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.HEIGHT) {
			if (prefDimensions == null)
				prefDimensions = new Size(Unit.AUTO);
			Size was = prefDimensions.clone();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				prefDimensions.y = 0;
				prefDimensions.yUnit = Unit.AUTO;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				prefDimensions.y = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				prefDimensions.yUnit = Unit.PERCENT;
			} else {
				prefDimensions.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				prefDimensions.yUnit = Unit.PX;
			}
			if (!Objects.equals(prefDimensions, was))
				dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CssExtensions.TEXT_ROTATION) {
			textRotation = decl.getValue().getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
			// if (textElement != null)
			// textElement.setRotation(textRotation);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.DISPLAY) {
			if (decl.asIdentValue() == IdentValue.NONE) {
				if (!isContainerOnly())
					setAsContainerOnly();
			}
		} else if (cssName == CSSName.TEXT_ALIGN) {
			textAlign = CssUtil.identToAlign(decl.asIdentValue());
			if (textElement != null) {
				textElement.setTextAlign(textAlign);
			}

			/*
			 * Text and children because some components use text align for
			 * layout of children
			 */
			dirtyLayout(false, LayoutType.text, LayoutType.children);
		} else if (cssName == CSSName.VERTICAL_ALIGN) {
			textVAlign = CssUtil.identToVAlign(decl.asIdentValue());
			if (textElement != null) {
				textElement.setTextVAlign(textVAlign);
			}
			/*
			 * Text and children because some components use text align for
			 * layout of children
			 */
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.COLOR) {
			// TODO colours dont seem to work properly in FS... am a bit
			// confused
			ColorRGBA col = CssUtil.toFontColor(decl, this);
			if (!Objects.equals(col, fontColor)) {
				fontColor = col;
				dirtyLayout(true, LayoutType.text());
			}
		} else if (cssName == CSSName.FONT_SIZE) {
			float fs;
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.INHERIT) {
					fs = -1;
				} else
					throw new UnsupportedOperationException(
							String.format("Invalid font size %s", decl.getValue().toString()));
			} else
				fs = v.getFloatValue(CSSPrimitiveValue.CSS_PT);
			if (fs != font.getSize()) {
				font = new FontSpec(font.getPath(), font.getFamily(), fs);
				if(font.getPath() != null)
					bitmapFont = font.load(ToolKit.get().getApplication().getAssetManager());
				dirtyLayout(true, LayoutType.text());
			}
		} else if (cssName == CSSName.VISIBILITY) {

			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				if (ident == IdentValue.HIDDEN) {
					setVisibilityAllowed(false);
				} else if (ident == IdentValue.VISIBLE) {
					setVisibilityAllowed(true);
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
				String tex = v.getStringValue();
				createAudioEffect().setUri(tex);
			} else
				throw new UnsupportedOperationException(
						String.format("Invalid visibility type %d", decl.getValue().getPrimitiveType()));
		} else if (cssName == CssExtensions.OPACITY) {
			this.elementAlpha = CssUtil.getAlpha(decl.getValue());
			updateAlpha();
		} else if (cssName == CSSName.Z_INDEX) {
			setZOrder(v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER));
			this.elementAlpha = CssUtil.getAlpha(decl.getValue());
			updateAlpha();
		} else if (cssName == CSSName.FONT_FAMILY) {
			String fn = null;
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() != IdentValue.INHERIT)
					throw new UnsupportedOperationException(
							String.format("Invalid font fammily %s", decl.getValue().toString()));
			} else
				fn = v.getStringValue();
			if (!Objects.equals(fn, font.getFamily())) {
				String fnt = getThemeInstance().getFontPath(fn);
				if (fnt == null)
					LOG.warning(String.format("No logical font named %s", fn));
				else {
					font = new FontSpec(fnt, fn, font.getSize());
					bitmapFont = font.load(ToolKit.get().getApplication().getAssetManager());
					dirtyLayout(true, LayoutType.text());
				}
			}
		} else if (cssName == CSSName.WHITE_SPACE) {
			LineWrapMode lwm = CssUtil.identToLineWrapMode(decl.asIdentValue());
			if (!Objects.equals(lwm, textWrap)) {
				textWrap = lwm;
				if (textElement != null) {
					textElement.setTextWrap(textWrap);
					dirtyLayout(false, LayoutType.boundsChange());
				}
			}
		} else if (cssName == CssExtensions.VOLUME) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				// TODO various ident volumes
				throw new UnsupportedOperationException(String.format("Invalid volume %s.", ident));
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
				createAudioEffect().setVolume(decl.getValue().getFloatValue(CSSPrimitiveValue.CSS_PERCENTAGE) / 100f);
			} else
				throw new UnsupportedOperationException(
						String.format("Invalid volume type %d", decl.getValue().getPrimitiveType()));
		} else if (cssName == CssExtensions.PLAY_DURING_SOUND) {

			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				if (ident == IdentValue.NONE || ident == IdentValue.AUTO) {
					getEffects().put(EffectChannel.control,
							new ControlEffect(ControlEffect.Type.stop, EffectChannel.audio));
				} else if (ident == IdentValue.NONE) {
					throw new UnsupportedOperationException(String.format("Invalid audio type %s.", ident));
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
				String tex = v.getStringValue();
				createAudioEffect().setUri(tex);
			} else
				throw new UnsupportedOperationException(
						String.format("Invalid animation image type %d", decl.getValue().getPrimitiveType()));
		} else if (cssName == CssExtensions.LAYOUT_DATA) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.NONE)
					layoutData = null;
			} else {
				layoutData = v.getStringValue();
				dirtyLayout(false, LayoutType.boundsChange());
			}
			parseLayoutData(layoutData);
		} else if (cssName == CssExtensions.LAYOUT) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.NONE)
					setLayoutManager(DefaultLayout.SHARED_INSTANCE);
			} else {
				try {
					setLayoutManager((Layout<?, ?>) Class.forName(v.getStringValue()).newInstance());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		} else if (cssName == CssExtensions.MIN_FILTER) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				super.setMinFilter(CssUtil.identToMinFilter(decl.asIdentValue()));
			} else {
				throw new UnsupportedOperationException(
						String.format("Invalid min filter type %d", decl.getValue().getPrimitiveType()));
			}
		} else if (cssName == CssExtensions.MAG_FILTER) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				super.setMagFilter(CssUtil.identToMagFilter(decl.asIdentValue()));
			} else {
				throw new UnsupportedOperationException(
						String.format("Invalid mag filter type %d", decl.getValue().getPrimitiveType()));
			}
		} else if (n.equals("cursor")) {
			applyCssCursor(decl);
		} else if (n.startsWith("animation-") || n.startsWith("-it-animation-")) {
			applyCssAnimation(decl);
		} else if (n.startsWith("-it-bgmap-")) {
			applyCssBgMap(decl);
		} else if (n.startsWith("background") || n.startsWith("-it-background-")) {
			applyCssBackground(decl);
		} else if (n.startsWith("overflow") || n.startsWith("-it-overflow-")) {
			applyCssOverflow(decl);
		} else if (n.startsWith("padding")) {
			applyCssPadding(decl);
		} else if (n.startsWith("margin")) {
			applyCssMargin(decl);
		} else if (n.startsWith("-it-clip-padding")) {
			applyCssClipPadding(decl);
		} else if (n.startsWith("-it-handle-position")) {
			applyCssBorderHandlePosition(decl);
		} else if (n.startsWith("-it-border-offset")) {
			applyCssBorderOffsetPosition(decl);
		} else if (n.startsWith("-it-atlas")) {
			applyCssAtlas(decl);
		} else if (n.startsWith("border-")) {
			applyCssBorder(decl);
		} else {
			LOG.warning(String.format("Unknown style %s (%s) in %s", n, v.getStringValue(), toString()));
		}
		// }
	}

	/**
	 * Returns the amount of <i>Indent</i> for this Element. The meaning of
	 * indent will depend on the concrete use of the Element, but it is often
	 * used to determine inter-element spacing for a layout manager. For
	 * example, {@link FlowLayout} uses it to determine its 'gap' between each
	 * laid out element.
	 * <p>
	 * The calculated indent is derived from the CSS property 'text-indent' and
	 * is a pixel value.
	 * 
	 * @return calculated indent
	 */
	public Float calcIndent() {
		CascadedStyle style = cssState.getCascadedStyle(false);
		if (style != null) {
			PropertyDeclaration pd = style.propertyByName(CSSName.TEXT_INDENT);
			if (pd != null)
				return pd.getValue().getFloatValue(CSSPrimitiveValue.CSS_PX);
		}
		return null;
	}

	/**
	 * Removes all user styles. User styles are those applied programmatically
	 * with calls to methods such as {@link #setTexture(String)},
	 * {@link #setMargin(Vector4f)}, basically any of the overridden methods
	 * used to alter the appearance outside of CSS.
	 */
	public void clearUserStyles() {
		cssState.clear();
		dirtyLayout(true, LayoutType.styling);
		layoutChildren();
	}

	@Override
	public Element clone() {
		cloning.set(cloning.get() + 1);
		try {
			Element el = new Element(screen);
			configureClone(el);
			return el;
		} finally {
			cloning.set(cloning.get() - 1);
		}
	}

	@Override
	public String getCss() {
		return css;
	}

	@Override
	public CssState getCssState() {
		return cssState;
	}

	@Override
	public PseudoStyles getPseudoStyles() {
		PseudoStyles pseudoStyles = null;
		if (isEnabled) {
			if (isHovering()) {
				pseudoStyles = PseudoStyles.get(pseudoStyles).addStyle(PseudoStyle.hover);
			}
			if (isKeyboardFocussed() || isKeyboardFocussedChild() || isKeyboardFocussedParent()) {
				pseudoStyles = PseudoStyles.get(pseudoStyles).addStyle(PseudoStyle.focus);
			}
		}
		if (useParentPseudoStyles && elementParent != null && elementParent instanceof StyledNode<?, ?>) {
			pseudoStyles = PseudoStyles.get(pseudoStyles);
			PseudoStyles pps = ((StyledNode<?, ?>) elementParent).getPseudoStyles();
			if (pps != null)
				pseudoStyles.addAll(pps);
		}
		return pseudoStyles;
	}

	@Override
	public String getStyleClass() {
		return styleClass;
	}

	@Override
	public List<String> getStyleClassNames() {
		return classNames.get(getClass());
	}

	public boolean isUseParentPseudoStyles() {
		return useParentPseudoStyles;
	}

	public Element removeStyleClass(String styleClass) {
		if (this.styleClass != null) {
			List<String> cols = new LinkedList<>(Arrays.asList(this.styleClass.split("\\s+")));
			for (String c : styleClass.split("\\s+")) {
				cols.remove(c);
			}
			this.styleClass = String.join(" ", cols);
			dirtyLayout(true, LayoutType.reset);
			layoutChildren();
		}
		return this;
	}

	public BaseElement removeStylesheet(Stylesheet sheet) {
		if (stylesheets != null)
			stylesheets.remove(sheet);
		dirtyLayout(true, LayoutType.reset);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setAtlas(Vector4f atlas) {
		PropertyDeclaration declX = new PropertyDeclaration(CssExtensions.ATLAS_X,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, atlas.x, String.format("%fpx", atlas.x)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declY = new PropertyDeclaration(CssExtensions.ATLAS_Y,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, atlas.y, String.format("%fpx", atlas.y)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declW = new PropertyDeclaration(CssExtensions.ATLAS_WIDTH,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, atlas.z, String.format("%fpx", atlas.z)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declH = new PropertyDeclaration(CssExtensions.ATLAS_HEIGHT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, atlas.w, String.format("%fpx", atlas.w)), false,
				StylesheetInfo.USER);
		cssState.addAllCssDeclaration(declX);
		applyCss(declX);
		cssState.addAllCssDeclaration(declY);
		applyCss(declY);
		cssState.addAllCssDeclaration(declH);
		applyCss(declH);
		cssState.addAllCssDeclaration(declW);
		applyCss(declW);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setBackgroundDimensions(Size backgroundSize) {
		setCssSize(CSSName.BACKGROUND_SIZE, backgroundSize);
		return this;
	}

	@Override
	public BaseElement setBackgroundPosition(Position backgroundPosition) {
		setCssPosition(CSSName.BACKGROUND_POSITION, backgroundPosition);
		return this;
	}

	@Override
	public BaseElement setClipPadding(float left, float right, float top, float bottom) {
		PropertyDeclaration declLeft = new PropertyDeclaration(CssExtensions.CLIP_PADDING_LEFT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, left, String.format("%fpx", left)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declRight = new PropertyDeclaration(CssExtensions.CLIP_PADDING_RIGHT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, right, String.format("%fpx", right)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declBottom = new PropertyDeclaration(CssExtensions.CLIP_PADDING_BOTTOM,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, bottom, String.format("%fpx", bottom)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declTop = new PropertyDeclaration(CssExtensions.CLIP_PADDING_TOP,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, top, String.format("%fpx", top)), false,
				StylesheetInfo.USER);
		cssState.addAllCssDeclaration(declLeft);
		cssState.addAllCssDeclaration(declRight);
		cssState.addAllCssDeclaration(declTop);
		cssState.addAllCssDeclaration(declBottom);
		applyCss(declLeft);
		applyCss(declRight);
		applyCss(declTop);
		applyCss(declBottom);
		layoutChildren();
		return this;
	}

	public BaseElement setCss(String css) {
		this.css = css;
		dirtyLayout(false, LayoutType.reset);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setCursor(CursorType cursor) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.CURSOR, CssUtil.cursorToPropertyValue(cursor), false,
				StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setDefaultColor(ColorRGBA defaultColor) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.BACKGROUND_COLOR,
				new PropertyValue(CssUtil.rgbaColor(defaultColor)), false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCssBackground(decl);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setElementAlpha(float elementAlpha) {
		if (this.elementAlpha != elementAlpha) {
			PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.OPACITY,
					new PropertyValue(CSSPrimitiveValue.CSS_NUMBER, elementAlpha, String.valueOf(elementAlpha)), false,
					StylesheetInfo.USER);
			cssState.addAllCssDeclaration(decl);
			applyCss(decl);
			layoutChildren();
			for (BaseElement el : childList) {
				el.setElementAlpha(elementAlpha);
			}
		}
		return this;
	}

	/**
	 * Sets the element's text layer font color
	 * 
	 * @param fontColor
	 *            ColorRGBA The color to set the font to
	 */
	@Override
	public BaseElement setFontColor(ColorRGBA fontColor) {
		cssState.setFontColor(fontColor);
		return this;
	}

	@Override
	public BaseElement setFontFamily(String fontName) {
		cssState.setFontFamily(fontName);
		return this;
	}

	/**
	 * Sets the element's text layer font size
	 * 
	 * @param fontSize
	 *            float The size to set the font to
	 */
	@Override
	public BaseElement setFontSize(float fontSize) {
		cssState.setFontSize(fontSize);
		return this;
	}

	@Override
	public BaseElement setHandlePosition(Vector4f borderHandles) {
		PropertyDeclaration declLeft = new PropertyDeclaration(CssExtensions.HANDLE_POSITION_LEFT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, borderHandles.w, String.format("%fpx", borderHandles.w)),
				false, StylesheetInfo.USER);
		PropertyDeclaration declRight = new PropertyDeclaration(CssExtensions.HANDLE_POSITION_RIGHT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, borderHandles.y, String.format("%fpx", borderHandles.y)),
				false, StylesheetInfo.USER);
		PropertyDeclaration declBottom = new PropertyDeclaration(CssExtensions.HANDLE_POSITION_BOTTOM,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, borderHandles.z, String.format("%fpx", borderHandles.z)),
				false, StylesheetInfo.USER);
		PropertyDeclaration declTop = new PropertyDeclaration(CssExtensions.HANDLE_POSITION_TOP,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, borderHandles.x, String.format("%fpx", borderHandles.x)),
				false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(declLeft);
		applyCss(declLeft);
		cssState.addAllCssDeclaration(declRight);
		applyCss(declRight);
		cssState.addAllCssDeclaration(declTop);
		applyCss(declTop);
		cssState.addAllCssDeclaration(declBottom);
		applyCss(declBottom);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setIndent(float indent) {
		if (indent != this.indent) {
			this.indent = indent;

			PropertyDeclaration decl = new PropertyDeclaration(CSSName.TEXT_INDENT,
					new PropertyValue(CSSPrimitiveValue.CSS_PX, indent, String.format("%fpx", indent)), false,
					StylesheetInfo.USER);
			cssState.addAllCssDeclaration(decl);
			applyCss(decl);
			layoutChildren();
		}
		return this;
	}

	@Override
	public BaseElement setMinFilter(MinFilter minFilter) {
		if (minFilter != this.minFilter) {
			this.minFilter = minFilter;
			PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.MIN_FILTER,
					new PropertyValue(CssUtil.minFilterToIdent(minFilter)), false, StylesheetInfo.USER);
			cssState.addAllCssDeclaration(decl);
			applyCss(decl);
			layoutChildren();
		}
		return this;
	}

	@Override
	public BaseElement setMagFilter(MagFilter magFilter) {
		if (magFilter != this.magFilter) {
			this.magFilter = magFilter;
			PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.MAG_FILTER,
					new PropertyValue(CssUtil.magFilterToIdent(magFilter)), false, StylesheetInfo.USER);
			cssState.addAllCssDeclaration(decl);
			applyCss(decl);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Sets the element's text layer horizontal alignment. Some layout managers
	 * may look in here for CSS provided last constraints.
	 * 
	 * @param layoutData
	 *            data
	 */
	@Override
	public BaseElement setLayoutData(String layoutData) {
		PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.LAYOUT_DATA,
				layoutData == null ? new PropertyValue(IdentValue.NONE)
						: new PropertyValue(CSSPrimitiveValue.CSS_STRING, layoutData, layoutData),
				false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		return this;
	}

	@Override
	public BaseElement setMargin(float left, float right, float top, float bottom) {
		PropertyDeclaration declLeft = new PropertyDeclaration(CSSName.MARGIN_LEFT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, left, String.format("%fpx", left)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declRight = new PropertyDeclaration(CSSName.MARGIN_RIGHT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, right, String.format("%fpx", right)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declBottom = new PropertyDeclaration(CSSName.MARGIN_BOTTOM,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, bottom, String.format("%fpx", bottom)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declTop = new PropertyDeclaration(CSSName.MARGIN_TOP,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, top, String.format("%fpx", top)), false,
				StylesheetInfo.USER);
		cssState.addAllCssDeclaration(declLeft);
		applyCssMargin(declLeft);
		cssState.addAllCssDeclaration(declRight);
		applyCssMargin(declRight);
		cssState.addAllCssDeclaration(declTop);
		applyCssMargin(declTop);
		cssState.addAllCssDeclaration(declBottom);
		applyCssMargin(declBottom);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setMaxDimensions(Size maxDimensions) {
		setCssDimensions(CSSName.MAX_WIDTH, CSSName.MAX_HEIGHT, maxDimensions);
		return this;
	}

	/**
	 * Stubbed for future use. This should limit resizing to the minimum
	 * dimensions defined
	 * 
	 * @param minDimensions
	 *            The absolute minimum dimensions for this Element.
	 */
	@Override
	public BaseElement setMinDimensions(Size minDimensions) {
		setCssDimensions(CSSName.MIN_WIDTH, CSSName.MIN_HEIGHT, minDimensions);
		return this;
	}

	@Override
	public BaseElement setPreferredDimensions(Size prefDimensions) {
		setCssDimensions(CSSName.WIDTH, CSSName.HEIGHT, prefDimensions);
		return this;
	}

	/**
	 * Set the north, west, east and south borders in number of pixels
	 * 
	 * @param nBorder
	 *            float
	 * @param wBorder
	 *            float
	 * @param eBorder
	 *            float
	 * @param sBorder
	 *            float
	 */
	@Override
	public BaseElement setResizeBorders(float nBorder, float wBorder, float eBorder, float sBorder) {
		PropertyDeclaration declW = new PropertyDeclaration(CSSName.BORDER_LEFT_WIDTH,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, eBorder, String.format("%fpx", eBorder)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declE = new PropertyDeclaration(CSSName.BORDER_RIGHT_WIDTH,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, wBorder, String.format("%fpx", wBorder)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declN = new PropertyDeclaration(CSSName.BORDER_TOP_WIDTH,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, nBorder, String.format("%fpx", nBorder)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declS = new PropertyDeclaration(CSSName.BORDER_BOTTOM_WIDTH,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, sBorder, String.format("%fpx", sBorder)), false,
				StylesheetInfo.USER);
		cssState.addAllCssDeclaration(declW);
		cssState.addAllCssDeclaration(declE);
		cssState.addAllCssDeclaration(declN);
		cssState.addAllCssDeclaration(declS);
		applyCss(declW);
		applyCss(declE);
		applyCss(declN);
		applyCss(declS);
		layoutChildren();
		return this;
	}

	public Element setStyleClass(String styleClass) {
		if (!Objects.equals(styleClass, this.styleClass)) {
			this.styleClass = styleClass;
			dirtyLayout(true, LayoutType.reset);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Sets the text of the element.
	 * 
	 * @param text
	 *            String The text to display.
	 */
	@Override
	public BaseElement setText(String text) {
		if (!Objects.equals(text, getText())) {
			if (text == null) {
				cssState.removeAllCssDeclaration(CssExtensions.TEXT.toString());
			} else {
				PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.TEXT,
						new PropertyValue(CSSPrimitiveValue.CSS_STRING, text, text), false, StylesheetInfo.USER);
				cssState.addAllCssDeclaration(decl);
				try {
					applyCss(decl);
				} catch (IndexOutOfBoundsException iiobe) {
					System.err.println("**BUG** Text for " + toString() + " is '" + text + "'");
					iiobe.printStackTrace();
				}
			}
			layoutChildren();
		}
		return this;
	}

	/**
	 * Sets the element's text layer horizontal alignment
	 * 
	 * @param textAlign
	 */
	@Override
	public BaseElement setTextAlign(BitmapFont.Align textAlign) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.TEXT_ALIGN,
				new PropertyValue(CssUtil.alignToIdent(textAlign)), false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setTextPadding(float left, float right, float top, float bottom) {
		PropertyDeclaration declLeft = new PropertyDeclaration(CSSName.PADDING_LEFT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, left, String.format("%fpx", left)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declRight = new PropertyDeclaration(CSSName.PADDING_RIGHT,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, right, String.format("%fpx", right)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declBottom = new PropertyDeclaration(CSSName.PADDING_BOTTOM,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, bottom, String.format("%fpx", bottom)), false,
				StylesheetInfo.USER);
		PropertyDeclaration declTop = new PropertyDeclaration(CSSName.PADDING_TOP,
				new PropertyValue(CSSPrimitiveValue.CSS_PX, top, String.format("%fpx", top)), false,
				StylesheetInfo.USER);
		cssState.addAllCssDeclaration(declLeft);
		applyCssPadding(declLeft);
		cssState.addAllCssDeclaration(declRight);
		applyCssPadding(declRight);
		cssState.addAllCssDeclaration(declTop);
		applyCssPadding(declTop);
		cssState.addAllCssDeclaration(declBottom);
		applyCssPadding(declBottom);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setTextRotation(float textRotation) {
		PropertyDeclaration decl = new PropertyDeclaration(CssExtensions.TEXT_ROTATION,
				new PropertyValue(CSSPrimitiveValue.CSS_NUMBER, textRotation, String.format("%f", textRotation)), false,
				StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setTexture(String colorMap) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.BACKGROUND_IMAGE,
				colorMap == null ? new PropertyValue(IdentValue.AUTO)
						: new PropertyValue(CSSPrimitiveValue.CSS_URI, colorMap, String.format("uri(%s)", colorMap)),
				false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
		return this;
	}

	/**
	 * Sets the element's text layer vertical alignment
	 * 
	 * @param textVAlign
	 */
	@Override
	public BaseElement setTextVAlign(BitmapFont.VAlign textVAlign) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.VERTICAL_ALIGN,
				new PropertyValue(CssUtil.vAlignToIdent(textVAlign)), false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
		return this;
	}

	/**
	 * Sets the element's text later wrap mode
	 * 
	 * @param textWrap
	 *            LineWrapMode textWrap
	 */
	@Override
	public BaseElement setTextWrap(LineWrapMode textWrap) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.WHITE_SPACE,
				new PropertyValue(CssUtil.lineWrapModeToIdent(textWrap)), true, StylesheetInfo.USER);

		// experiment
		cssState.resetCssProcessor();
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
		return this;
	}

	/**
	 * Will set the textures repeat mode<br/>
	 * <br/>
	 * NOTE: This only works when texture atlasing has not been enabled. For
	 * info on texture atlas usage, see both:<br/>
	 * 
	 * @see BaseScreen#setUseTextureAtlas(boolean enable, String path)
	 * @see #setTextureAtlasImage(com.jme3.texture.Texture tex, java.lang.String
	 *      path)
	 * @param tileMode
	 *            mode
	 */
	@Override
	public BaseElement setTileMode(TileMode tileMode) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.BACKGROUND_REPEAT,
				new PropertyValue(CssUtil.tileModeToIdent(tileMode)), false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();

		return this;
	}

	public void setUseParentPseudoStyles(boolean useParentPseudoStyles) {
		this.useParentPseudoStyles = useParentPseudoStyles;
		dirtyLayout(false, LayoutType.styling);
		layoutChildren();
	}

	@Override
	public String toString() {
		return getClass() + " [styleClass=" + styleClass + ", styleId=" + styleId + ", " + getStyleClassNames() + ","
				+ getDimensions() + "," + getPosition() + "]";
	}

	protected void applyCssAnimation(PropertyDeclaration decl) {
		CSSPrimitiveValue v = decl.getValue();
		CSSName cssName = decl.getCSSName();
		if (cssName == CssExtensions.ANIMATION_DURATION) {
			createCssEffect().setDuration(decl.getValue().getFloatValue(CSSValue.CSS_PRIMITIVE_VALUE));
		} else if (cssName == CssExtensions.ANIMATION_DELAY) {
			createCssEffect().setDelay(decl.getValue().getFloatValue(CSSValue.CSS_PRIMITIVE_VALUE));
		} else if (cssName == CssExtensions.ANIMATION_ITERATION_COUNT) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == CssExtensions.INFINITE)
					createCssEffect().setIterations(Integer.MAX_VALUE);
				else
					throw new UnsupportedOperationException("Unsupported background size " + v);
			} else {
				createCssEffect().setIterations((int) decl.getValue().getFloatValue(CSSValue.CSS_PRIMITIVE_VALUE));
			}
		} else if (cssName == CssExtensions.ANIMATION_DIRECTION) {
			IdentValue iv = decl.asIdentValue();
			if (iv == CssExtensions.FXDIR_LEFT) {
				createCssEffect().setDirection(EffectDirection.Left);
			} else if (iv == CssExtensions.FXDIR_RIGHT) {
				createCssEffect().setDirection(EffectDirection.Right);
			} else if (iv == CssExtensions.FXDIR_TOP) {
				createCssEffect().setDirection(EffectDirection.Top);
			} else if (iv == CssExtensions.FXDIR_BOTTOM) {
				createCssEffect().setDirection(EffectDirection.Bottom);
			}
		} else if (cssName == CssExtensions.ANIMATION_REVERSE) {
			IdentValue iv = decl.asIdentValue();
			if (iv == CssExtensions.REVERSE) {
				createCssEffect().setReverse(true);
			} else if (iv == IdentValue.AUTO) {
				createCssEffect().setReverse(false);
			}
		} else if (cssName == CssExtensions.ANIMATION_TIMING_FUNCTION) {
			IdentValue iv = decl.asIdentValue();
			if (iv == CssExtensions.LINEAR) {
				createCssEffect().setInterpolation(Interpolation.linear);
			} else if (iv == CssExtensions.BOUNCE) {
				createCssEffect().setInterpolation(Interpolation.bounce);
			} else if (iv == CssExtensions.FADE) {
				createCssEffect().setInterpolation(Interpolation.fade);
			} else if (iv == CssExtensions.FXDIR_BOTTOM) {
				throw new UnsupportedOperationException("TODO: the rest of the effects");
			}
		} else if (cssName == CssExtensions.ANIMATION_BLEND_COLOR) {
			createCssEffect().setBlendColor(CssUtil.toColor(v.getCssText()));
		} else if (cssName == CssExtensions.ANIMATION_DESTINATION_X) {
			CssEffect effect = createCssEffect();
			Vector2f dest = effect.getDestination();
			if (dest == null)
				dest = new Vector2f();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				dest.x = 0;
				effect.getDestinationUnits().x = CSSPrimitiveValue.CSS_PX;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				dest.x = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				effect.getDestinationUnits().x = CSSPrimitiveValue.CSS_PERCENTAGE;
			} else {
				dest.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				effect.getDestinationUnits().x = CSSPrimitiveValue.CSS_PX;
			}
			if (dest.equals(Vector2f.ZERO))
				dest = null;
			effect.setDestination(dest);
			;
		} else if (cssName == CssExtensions.ANIMATION_DESTINATION_Y) {
			CssEffect effect = createCssEffect();
			Vector2f dest = effect.getDestination();
			if (dest == null)
				dest = new Vector2f();
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
					&& decl.asIdentValue() == IdentValue.AUTO) {
				dest.y = 0;
				effect.getDestinationUnits().y = CSSPrimitiveValue.CSS_PX;
			} else if (decl.getValue().getCssValueType() == CSSPrimitiveValue.CSS_NUMBER
					&& decl.getValue().getCssText().endsWith("%")) {
				dest.y = v.getFloatValue(CSSPrimitiveValue.CSS_NUMBER);
				effect.getDestinationUnits().y = CSSPrimitiveValue.CSS_PERCENTAGE;
			} else {
				dest.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				effect.getDestinationUnits().y = CSSPrimitiveValue.CSS_PX;
			}
			if (dest.equals(Vector2f.ZERO))
				dest = null;
			effect.setDestination(dest);
			;
		} else if (cssName == CssExtensions.ANIMATION_IMAGE) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				if (ident == IdentValue.NONE || ident == IdentValue.AUTO) {
					createCssEffect().setImageUri(null);
				} else
					throw new UnsupportedOperationException(String.format("Invalid animation image type %s.", ident));
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
				createCssEffect().setImageUri(v.getStringValue());
				// Texture color = app.getAssetManager().loadTexture(tex);
				// color.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
				// color.setMagFilter(Texture.MagFilter.Nearest);
				// cssEffect.setImageUri(color);
			} else
				throw new UnsupportedOperationException(
						String.format("Invalid animation image type %d", decl.getValue().getPrimitiveType()));
		} else if (cssName == CssExtensions.ANIMATION_NAME) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				if (ident == IdentValue.AUTO || ident == IdentValue.NONE) {
					if (screen != null && screen.getEffectManager().hasEffectFor(this, EffectChannel.fx)) {
						getEffects().put(EffectChannel.control,
								new ControlEffect(ControlEffect.Type.stop, EffectChannel.fx));
					}
				} else
					throw new UnsupportedOperationException(String.format("Invalid animation name type %s.", ident));
			} else {
				createCssEffect().getEffects().addAll(Arrays.asList(v.getStringValue().split(",")));
			}
		}

	}

	protected void applyCssAtlas(PropertyDeclaration decl) {
		CSSPrimitiveValue v = decl.getValue();
		CSSName cssName = decl.getCSSName();
		if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
				&& (cssName == CssExtensions.ATLAS_X || cssName == CssExtensions.ATLAS_X
						|| cssName == CssExtensions.ATLAS_WIDTH || cssName == CssExtensions.ATLAS_HEIGHT)) {
			if (decl.asIdentValue() == IdentValue.AUTO) {
				if (atlas != null && !atlas.equals(Vector4f.ZERO)) {
					atlas = null;
					if (defaultTex != null)
						applyTexture(defaultTex);
				}
			} else
				throw new UnsupportedOperationException(
						String.format("Invalid ident %s", decl.asIdentValue().asString()));

		} else if (cssName == CssExtensions.ATLAS_X) {
			if (atlas == null)
				atlas = new Vector4f();
			atlas.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			if (defaultTex != null)
				applyTexture(defaultTex);
		} else if (cssName == CssExtensions.ATLAS_Y) {
			if (atlas == null)
				atlas = new Vector4f();
			atlas.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);

			if (defaultTex != null)
				applyTexture(defaultTex);
		} else if (cssName == CssExtensions.ATLAS_WIDTH) {
			if (atlas == null)
				atlas = new Vector4f();
			atlas.z = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			if (defaultTex != null)
				applyTexture(defaultTex);
		} else if (cssName == CssExtensions.ATLAS_HEIGHT) {
			if (atlas == null)
				atlas = new Vector4f();
			atlas.w = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			if (defaultTex != null)
				applyTexture(defaultTex);
		}

	}

	protected void applyCssBackground(PropertyDeclaration decl) {
		CSSPrimitiveValue v = decl.getValue();
		CSSName cssName = decl.getCSSName();
		if (cssName == CSSName.BACKGROUND_IMAGE) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				if (ident == IdentValue.NONE) {
					makeNonContainer();
					loadTexture(null);
				} else if (ident == IdentValue.NONE) {
					throw new UnsupportedOperationException(String.format("Invalid background image type %s.", ident));
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
				String tex = v.getStringValue();
				if (tex != null) {
					makeNonContainer();
					loadTexture(tex);
				}
			} else
				throw new UnsupportedOperationException(
						String.format("Invalid background image type %d", decl.getValue().getPrimitiveType()));
		} else if (cssName == CSSName.BACKGROUND_SIZE) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.AUTO) {
					backgroundSize = Size.AUTO;
				} else if (decl.asIdentValue() == IdentValue.COVER) {
					backgroundSize = Size.ZOOM;
				} else if (decl.asIdentValue() == IdentValue.CONTAIN) {
					backgroundSize = Size.FIT;
				} else {
					throw new UnsupportedOperationException("Unsupported background size " + v);
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_UNKNOWN) {
				if (decl.getValue() instanceof PropertyValue) {
					PropertyValue fscss = (PropertyValue) decl.getValue();
					if (fscss.getPropertyValueType() == PropertyValue.VALUE_TYPE_LIST) {
						@SuppressWarnings("unchecked")
						List<PropertyValue> pv = fscss.getValues();
						float bx = Short.MIN_VALUE;
						float by = Short.MIN_VALUE;
						Unit xu = Unit.PX;
						Unit yu = Unit.PX;
						Iterator<PropertyValue> pvit = pv.iterator();
						if (pvit.hasNext()) {
							PropertyValue p = pvit.next();
							if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
								if (p.getIdentValue() != IdentValue.AUTO) {
									throw new UnsupportedOperationException("Unsupported background size " + v);
								}
								xu = Unit.AUTO;
							} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
								bx = p.getFloatValue();
							} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								bx = p.getFloatValue();
								xu = Unit.PERCENT;
							}
							if (pvit.hasNext()) {
								p = pvit.next();
								if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
									if (p.getIdentValue() != IdentValue.AUTO) {
										throw new UnsupportedOperationException("Unsupported background size " + v);
									}
									yu = Unit.AUTO;
								} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
									by = p.getFloatValue();
									yu = Unit.PX;
								} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
									by = p.getFloatValue();
									yu = Unit.PERCENT;
								}
							}
						}
						if (bx == Short.MIN_VALUE && by == Short.MIN_VALUE)
							backgroundSize = Size.AUTO;
						else
							backgroundSize = new Size(bx, by, xu, yu);
					}
				}
			}
			dirtyLayout(false, LayoutType.background);
		} else if (cssName == CSSName.BACKGROUND_POSITION) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.AUTO) {
					backgroundPosition = null;
				} else {
					throw new UnsupportedOperationException("Unsupported background position " + v);
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_UNKNOWN) {
				if (decl.getValue() instanceof PropertyValue) {
					PropertyValue fscss = (PropertyValue) decl.getValue();
					if (fscss.getPropertyValueType() == PropertyValue.VALUE_TYPE_LIST) {
						@SuppressWarnings("unchecked")
						List<PropertyValue> pv = fscss.getValues();
						Iterator<PropertyValue> pvit = pv.iterator();
						Position pos = Position.TOP_LEFT.clone();
						if (pvit.hasNext()) {
							PropertyValue p = pvit.next();
							pos.x = 50;
							pos.xUnit = Unit.PERCENT;
							pos.y = 50;
							pos.yUnit = Unit.PERCENT;
							if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
								if (p.getIdentValue() == IdentValue.AUTO || p.getIdentValue() == IdentValue.LEFT) {
									pos.x = 0;
								} else if (p.getIdentValue() == IdentValue.CENTER) {
									//
								} else if (p.getIdentValue() == IdentValue.RIGHT) {
									pos.x = 100;
								} else if (p.getIdentValue() == IdentValue.BOTTOM) {
									pos.y = 100;
								} else {
									throw new UnsupportedOperationException("Unsupported background position " + v);
								}
							} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
								pos.x = p.getFloatValue();
							} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
								pos.xUnit = Unit.PX;
								pos.x = p.getFloatValue();
							}
							if (pvit.hasNext()) {
								p = pvit.next();
								if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
									if (p.getIdentValue() == IdentValue.AUTO || p.getIdentValue() == IdentValue.TOP) {
										pos.y = 0;
									} else if (p.getIdentValue() == IdentValue.CENTER) {
										//
									} else if (p.getIdentValue() == IdentValue.BOTTOM) {
										pos.y = 100;
									} else {
										throw new UnsupportedOperationException("Unsupported background size " + v);
									}
								} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PERCENTAGE) {
									pos.y = p.getFloatValue();
								} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
									pos.yUnit = Unit.PX;
									pos.y = p.getFloatValue();
								}
							}
						}
						backgroundPosition = pos.equals(Position.TOP_LEFT) ? null : pos;
					}
				}
			}
			dirtyLayout(false, LayoutType.background);
		} else if (cssName == CSSName.BACKGROUND_COLOR) {
			defaultColor = CssUtil.toColor(v.getCssText());
			if (isContainerOnly()) {
				if (!GUIConstants.DEFAULT_ELEMENT_COLOR.equals(defaultColor))
					makeNonContainer();
			} else {
				setElementMaterialColor(defaultColor);
			}
		} else if (cssName == CssExtensions.BACKGROUND_OPACITY) {
			defaultColor = defaultColor.clone();
			defaultColor.a = CssUtil.getAlpha(v);
			if (!containerOnly) {
				setElementMaterialColor(defaultColor);
				dirtyLayout(false, LayoutType.background);
			}
		} else if (cssName == CSSName.BACKGROUND_REPEAT) {
			TileMode tile = CssUtil.identToTileMode(decl.asIdentValue());
			if (tile != this.tileMode) {
				this.tileMode = tile;
				if (defaultTex != null) {
					// setAsContainerOnly();
					// makeNonContainer();
					applyTexture(defaultTex);
					dirtyLayout(false, LayoutType.background);
				}
			}
		}

	}

	protected void applyCssBgMap(PropertyDeclaration decl) {
		CSSPrimitiveValue v = decl.getValue();
		CSSName cssName = decl.getCSSName();
		if (cssName == CssExtensions.BGMAP_COLOR) {
			super.setBgMapColor(CssUtil.toColor(v.getCssText()));
		} else if (cssName == CssExtensions.BGMAP_IMAGE) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				if (ident == IdentValue.NONE || ident == IdentValue.AUTO) {
					super.setBgMap(null);
				} else
					throw new UnsupportedOperationException(String.format("Invalid animation image type %s.", ident));
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_URI) {
				super.setBgMap(v.getStringValue());
			} else
				throw new UnsupportedOperationException(
						String.format("Invalid animation image type %d", decl.getValue().getPrimitiveType()));
		}
	}

	protected void applyCssBorder(PropertyDeclaration decl) {
		CSSName cssName = decl.getCSSName();
		CSSPrimitiveValue v = decl.getValue();
		if (cssName == CSSName.BORDER_LEFT_WIDTH) {
			borders.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.BORDER_RIGHT_WIDTH) {
			borders.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.BORDER_TOP_WIDTH) {
			borders.z = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.BORDER_BOTTOM_WIDTH) {
			borders.w = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		}

	}

	protected void applyCssBorderHandlePosition(PropertyDeclaration decl) {
		handlePosition = applyCssPaddingType(CssExtensions.HANDLE_POSITION_LEFT, CssExtensions.HANDLE_POSITION_RIGHT,
				CssExtensions.HANDLE_POSITION_TOP, CssExtensions.HANDLE_POSITION_BOTTOM,
				CssExtensions.HANDLE_POSITION_SHORTHAND, decl, handlePosition, false, Vector4f.ZERO,
				LayoutType.clipping);
	}

	protected void applyCssBorderOffsetPosition(PropertyDeclaration decl) {
		borderOffset = applyCssPaddingType(CssExtensions.BORDER_OFFSET_LEFT, CssExtensions.BORDER_OFFSET_RIGHT,
				CssExtensions.BORDER_OFFSET_TOP, CssExtensions.BORDER_OFFSET_BOTTOM,
				CssExtensions.BORDER_OFFSET_SHORTHAND, decl, borderOffset, false, Vector4f.ZERO,
				LayoutType.boundsChange());
	}

	protected void applyCssClipPadding(PropertyDeclaration decl) {
		clipPadding = applyCssPaddingType(CssExtensions.CLIP_PADDING_LEFT, CssExtensions.CLIP_PADDING_RIGHT,
				CssExtensions.CLIP_PADDING_TOP, CssExtensions.CLIP_PADDING_BOTTOM, CssExtensions.CLIP_PADDING_SHORTHAND,
				decl, clipPadding, false, Vector4f.ZERO, LayoutType.clipping);
	}

	protected void applyCssCursor(PropertyDeclaration decl) {
		if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT)
			cursor = CssUtil.identValueToCursor(decl.asIdentValue());
	}

	protected void applyCssMargin(PropertyDeclaration decl) {
		CSSName cssName = decl.getCSSName();
		CSSPrimitiveValue v = decl.getValue();
		float fv;
		if (cssName == CSSName.MARGIN_RIGHT) {
			fv = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			if (margin.y != fv) {
				margin.y = fv;
				dirtyLayout(false, LayoutType.background);
			}
		} else if (cssName == CSSName.MARGIN_TOP) {
			fv = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			if (margin.z != fv) {
				margin.z = fv;
				dirtyLayout(false, LayoutType.background);
			}
		} else if (cssName == CSSName.MARGIN_BOTTOM) {
			fv = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			if (margin.w != fv) {
				margin.w = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
				dirtyLayout(false, LayoutType.background);
			}
		} else if (cssName == CSSName.MARGIN_LEFT) {
			fv = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			if (margin.x != fv) {
				margin.x = fv;
				dirtyLayout(false, LayoutType.background);
			}
		} else if (cssName == CSSName.MARGIN_SHORTHAND) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.AUTO) {
					margin = Vector4f.ZERO;
				} else {
					throw new UnsupportedOperationException("Unsupported margin size " + v);
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_UNKNOWN) {
				if (decl.getValue() instanceof PropertyValue) {
					PropertyValue fscss = (PropertyValue) decl.getValue();
					if (fscss.getPropertyValueType() == PropertyValue.VALUE_TYPE_LIST) {
						@SuppressWarnings("unchecked")
						List<PropertyValue> pv = fscss.getValues();
						float bx = 0;
						float by = 0;
						float bz = 0;
						float bw = 0;
						Iterator<PropertyValue> pvit = pv.iterator();
						if (pvit.hasNext()) {
							PropertyValue p = pvit.next();
							if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
								if (decl.asIdentValue() != IdentValue.AUTO) {
									throw new UnsupportedOperationException("Unsupported marginmargin size " + v);
								}
							} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
								bx = p.getFloatValue();
							}
							if (pvit.hasNext()) {
								p = pvit.next();
								if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
									if (decl.asIdentValue() != IdentValue.AUTO) {
										throw new UnsupportedOperationException("Unsupported margin size " + v);
									}
								} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
									by = p.getFloatValue();
								}
								if (pvit.hasNext()) {
									p = pvit.next();
									if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
										if (decl.asIdentValue() != IdentValue.AUTO) {
											throw new UnsupportedOperationException("Unsupported margin size " + v);
										}
									} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
										bz = p.getFloatValue();
									}
									if (pvit.hasNext()) {
										p = pvit.next();
										if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
											if (decl.asIdentValue() != IdentValue.AUTO) {
												throw new UnsupportedOperationException("Unsupported margin size " + v);
											}
										} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
											bz = p.getFloatValue();
										}
									}
									if (pvit.hasNext()) {
										p = pvit.next();
										if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
											if (decl.asIdentValue() != IdentValue.AUTO) {
												throw new UnsupportedOperationException("Unsupported margin size " + v);
											}
										} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
											bw = p.getFloatValue();
										}
									}
								}
							}
						}
						if (bx != margin.x || by != margin.y || bz != margin.z || bw != margin.w) {
							margin.set(bx, by, bz, bw);
							dirtyLayout(false, LayoutType.background);
						}
					}
				}
			}
		}
	}

	protected void applyCssOverflow(PropertyDeclaration decl) {
		CSSName cssName = decl.getCSSName();
		if (cssName == CSSName.OVERFLOW || cssName == CssExtensions.OVERFLOW_X || cssName == CssExtensions.OVERFLOW_Y
				|| cssName == CssExtensions.OVERFLOW) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = decl.asIdentValue();
				if (ident == IdentValue.VISIBLE) {
					if (hasClippingLayer(this)) {
						removeClippingLayer(this, false);
						dirtyLayout(false, LayoutType.clipping);
					}
				} else {
					// All other modes currently completely clip
					if (!hasClippingLayer(this)) {
						addClippingLayer(this, null);
						dirtyLayout(false, LayoutType.clipping);
					}
				}
			} else
				throw new UnsupportedOperationException(String.format("Invalid overflow %s", decl.getValue()));
		}
	}

	protected void applyCssPadding(PropertyDeclaration decl) {
		CSSName cssName = decl.getCSSName();
		CSSPrimitiveValue v = decl.getValue();
		if (cssName == CSSName.PADDING_RIGHT) {
			textPadding.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.PADDING_TOP) {
			textPadding.z = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.PADDING_BOTTOM) {
			textPadding.w = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.PADDING_LEFT) {
			textPadding.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(false, LayoutType.boundsChange());
		} else if (cssName == CSSName.PADDING_SHORTHAND) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.AUTO) {
					textPadding = Vector4f.ZERO;
				} else {
					throw new UnsupportedOperationException("Unsupported background size " + v);
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_UNKNOWN) {
				if (decl.getValue() instanceof PropertyValue) {
					PropertyValue fscss = (PropertyValue) decl.getValue();
					if (fscss.getPropertyValueType() == PropertyValue.VALUE_TYPE_LIST) {
						@SuppressWarnings("unchecked")
						List<PropertyValue> pv = fscss.getValues();
						float bx = 0;
						float by = 0;
						float bz = 0;
						float bw = 0;
						Iterator<PropertyValue> pvit = pv.iterator();
						if (pvit.hasNext()) {
							PropertyValue p = pvit.next();
							if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
								if (decl.asIdentValue() != IdentValue.AUTO) {
									throw new UnsupportedOperationException("Unsupported background size " + v);
								}
							} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
								bx = p.getFloatValue();
							}
							if (pvit.hasNext()) {
								p = pvit.next();
								if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
									if (decl.asIdentValue() != IdentValue.AUTO) {
										throw new UnsupportedOperationException("Unsupported background size " + v);
									}
								} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
									by = p.getFloatValue();
								}
								if (pvit.hasNext()) {
									p = pvit.next();
									if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
										if (decl.asIdentValue() != IdentValue.AUTO) {
											throw new UnsupportedOperationException("Unsupported background size " + v);
										}
									} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
										bz = p.getFloatValue();
									}
									if (pvit.hasNext()) {
										p = pvit.next();
										if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
											if (decl.asIdentValue() != IdentValue.AUTO) {
												throw new UnsupportedOperationException(
														"Unsupported background size " + v);
											}
										} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
											bz = p.getFloatValue();
										}
									}
									if (pvit.hasNext()) {
										p = pvit.next();
										if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
											if (decl.asIdentValue() != IdentValue.AUTO) {
												throw new UnsupportedOperationException(
														"Unsupported background size " + v);
											}
										} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
											bw = p.getFloatValue();
										}
									}
								}
							}
						}
						textPadding.set(bx, by, bz, bw);
						;
					}
				}
			}
			dirtyLayout(false, LayoutType.boundsChange());
		}
	}

	protected Vector4f applyCssPaddingType(CSSName left, CSSName right, CSSName top, CSSName bottom, CSSName shorthand,
			PropertyDeclaration decl, Vector4f vec, boolean layoutChildren, Vector4f defVal,
			LayoutType... layoutTypes) {
		CSSName cssName = decl.getCSSName();
		CSSPrimitiveValue v = decl.getValue();
		if (cssName == right) {
			vec.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(layoutChildren, layoutTypes);
		} else if (cssName == top) {
			vec.z = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(layoutChildren, layoutTypes);
		} else if (cssName == bottom) {
			vec.w = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(layoutChildren, layoutTypes);
		} else if (cssName == left) {
			vec.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			dirtyLayout(layoutChildren, layoutTypes);
		} else if (cssName == shorthand) {
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() == IdentValue.AUTO) {
					vec = defVal;
				} else if (decl.asIdentValue() == IdentValue.NONE) {
					vec = new Vector4f();
				} else {
					throw new UnsupportedOperationException("Unsupported background size " + v);
				}
			} else if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_UNKNOWN) {
				if (decl.getValue() instanceof PropertyValue) {
					PropertyValue fscss = (PropertyValue) decl.getValue();
					if (fscss.getPropertyValueType() == PropertyValue.VALUE_TYPE_LIST) {
						@SuppressWarnings("unchecked")
						List<PropertyValue> pv = fscss.getValues();
						float bx = 0;
						float by = 0;
						float bz = 0;
						float bw = 0;
						Iterator<PropertyValue> pvit = pv.iterator();
						if (pvit.hasNext()) {
							PropertyValue p = pvit.next();
							if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
								if (decl.asIdentValue() != IdentValue.AUTO) {
									throw new UnsupportedOperationException("Unsupported background size " + v);
								}
							} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
								bx = p.getFloatValue();
							}
							if (pvit.hasNext()) {
								p = pvit.next();
								if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
									if (decl.asIdentValue() != IdentValue.AUTO) {
										throw new UnsupportedOperationException("Unsupported background size " + v);
									}
								} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
									by = p.getFloatValue();
								}
								if (pvit.hasNext()) {
									p = pvit.next();
									if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
										if (decl.asIdentValue() != IdentValue.AUTO) {
											throw new UnsupportedOperationException("Unsupported background size " + v);
										}
									} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
										bz = p.getFloatValue();
									}
									if (pvit.hasNext()) {
										p = pvit.next();
										if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
											if (decl.asIdentValue() != IdentValue.AUTO) {
												throw new UnsupportedOperationException(
														"Unsupported background size " + v);
											}
										} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
											bz = p.getFloatValue();
										}
									}
									if (pvit.hasNext()) {
										p = pvit.next();
										if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
											if (decl.asIdentValue() != IdentValue.AUTO) {
												throw new UnsupportedOperationException(
														"Unsupported background size " + v);
											}
										} else if (p.getPrimitiveType() == CSSPrimitiveValue.CSS_PX) {
											bw = p.getFloatValue();
										}
									}
								}
							}
						}
						vec.set(bx, by, bz, bw);
					}
				}
			}
			dirtyLayout(layoutChildren, layoutTypes);
		}
		return vec;
	}

	protected Align calcAlign() {
		CascadedStyle style = cssState.getCascadedStyle(false);
		if (style != null) {
			PropertyDeclaration pv = style.propertyByName(CSSName.TEXT_ALIGN);
			if (pv != null)
				return CssUtil.identToAlign(pv.asIdentValue());
		}
		return Align.Center;
	}

	protected LineWrapMode calcLineWrapMode() {
		CascadedStyle style = cssState.getCascadedStyle(false);
		if (style != null) {
			PropertyDeclaration pv = style.propertyByName(CSSName.WHITE_SPACE);
			if (pv != null)
				return CssUtil.identToLineWrapMode(pv.asIdentValue());
		}
		return LineWrapMode.Clip;
	}

	protected VAlign calcVAlign() {
		CascadedStyle style = cssState.getCascadedStyle(false);
		if (style != null) {
			PropertyDeclaration pv = style.propertyByName(CSSName.VERTICAL_ALIGN);
			if (pv != null)
				return CssUtil.identToVAlign(pv.asIdentValue());
		}
		return VAlign.Center;
	}

	@Override
	protected void configureClone(BaseElement e) {
		super.configureClone(e);
		Element el = (Element) e;
		el.cssState.configureFrom(cssState);
		el.stylesheets = stylesheets;
		el.useParentPseudoStyles = useParentPseudoStyles;
		el.css = css;
	}

	@Override
	protected final void preConfigureElement() {
		cssState = new CssState(this);
		preConfigureStyledElement();
	}

	@Override
	protected final void configureElement() {

		/*
		 * As concrete types of Elements are constructed, we construct the list
		 * of class names they will be know as. This greatly speeds up CSS
		 * processing as it won't have to traverse the classes hierarchy every
		 * time styles are applied. We stored this in static map rather than in
		 * the instance of the element to further save time and memory
		 */
		Class<?> clazz = getClass();
		if (!classNames.containsKey(clazz)) {
			List<String> n = new ArrayList<>();
			while (clazz != null && !clazz.equals(Node.class)) {
				String simpleName = clazz.getSimpleName();
				n.add(simpleName);
				clazz = clazz.getSuperclass();
			}
			classNames.put(getClass(), n);
		}

		configureStyledElement();
		if (cloning.get() == 0) {
			if (screen != null && !screen.getStylesheets().isEmpty() || stylesheets != null) {
				dirtyLayout(false, LayoutType.styling);
			}
			updateNodeLocation();
		}
		super.configureElement();
	}

	protected void configureStyledElement() {

	}

	protected void preConfigureStyledElement() {

	}

	protected AudioEffect createAudioEffect() {
		Map<EffectChannel, EffectFactory> m = getEffects();
		AudioEffect fx = (AudioEffect) m.get(EffectChannel.audio);
		if (fx == null) {
			fx = new AudioEffect();
			m.put(EffectChannel.audio, fx);
		}
		return fx;
	}

	protected CssEffect createCssEffect() {
		Map<EffectChannel, EffectFactory> m = getEffects();
		CssEffect fx = (CssEffect) m.get(EffectChannel.fx);
		if (fx == null) {
			fx = new CssEffect();
			m.put(EffectChannel.fx, fx);
		}
		return fx;
	}

	protected float getCssBound(CascadedStyle style, CSSName name) {
		PropertyDeclaration decl = style.propertyByName(name);
		return decl == null || (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT
				&& decl.asIdentValue() == IdentValue.NONE) ? 0
						: decl.getValue().getFloatValue(CSSPrimitiveValue.CSS_PX);
	}

	@Override
	protected final void onAfterLayout() {
		cssState.completeCssProcessing();
		onAfterStyledLayout();
	}

	protected void onAfterStyledLayout() {
	}

	protected void setCssBounds(CSSName size, Vector4f dim) {
		PropertyDeclaration decl = dim == null
				? CssExtensions.createValues(size, StylesheetInfo.USER, false, new PropertyValue(IdentValue.AUTO),
						new PropertyValue(IdentValue.AUTO))
				: CssExtensions.createValues(size, StylesheetInfo.USER, false,
						new PropertyValue(CSSPrimitiveValue.CSS_PX, dim.x, String.format("%fpx", dim.x)),
						new PropertyValue(CSSPrimitiveValue.CSS_PX, dim.y, String.format("%fpx", dim.y)),
						new PropertyValue(CSSPrimitiveValue.CSS_PX, dim.z, String.format("%fpx", dim.z)),
						new PropertyValue(CSSPrimitiveValue.CSS_PX, dim.w, String.format("%fpx", dim.w)));
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
	}

	protected void setCssDimensions(CSSName width, CSSName height, Size dim) {
		PropertyDeclaration declX = dim == null
				? new PropertyDeclaration(width, new PropertyValue(IdentValue.AUTO), false, StylesheetInfo.USER)
				: new PropertyDeclaration(width, dim.xUnit == Unit.AUTO ? new PropertyValue(IdentValue.AUTO)
						: new PropertyValue(
								dim.xUnit == Unit.PX ? CSSPrimitiveValue.CSS_PX : CSSPrimitiveValue.CSS_PERCENTAGE,
								dim.x, String.format("%f%s", dim.x, dim.xUnit == Unit.PX ? "px" : "%")),
						false, StylesheetInfo.USER);
		PropertyDeclaration declY = dim == null
				? new PropertyDeclaration(height, new PropertyValue(IdentValue.AUTO), false, StylesheetInfo.USER)
				: new PropertyDeclaration(height, dim.yUnit == Unit.AUTO ? new PropertyValue(IdentValue.AUTO)
						: new PropertyValue(
								dim.yUnit == Unit.PX ? CSSPrimitiveValue.CSS_PX : CSSPrimitiveValue.CSS_PERCENTAGE,
								dim.y, String.format("%f%s", dim.y, dim.yUnit == Unit.PX ? "px" : "%")),
						false, StylesheetInfo.USER);
		cssState.addAllCssDeclaration(declX);
		cssState.addAllCssDeclaration(declY);
		applyCss(declX);
		applyCss(declY);
		layoutChildren();
	}

	protected void setCssPosition(CSSName size, Measurement dim) {
		PropertyDeclaration decl = dim == null
				? CssExtensions.createValues(size, StylesheetInfo.USER, false, new PropertyValue(IdentValue.AUTO),
						new PropertyValue(IdentValue.AUTO))
				: CssExtensions.createValues(size, StylesheetInfo.USER, false,
						dim.xUnit == Unit.AUTO ? new PropertyValue(IdentValue.AUTO)
								: new PropertyValue(
										dim.yUnit == Unit.PX ? CSSPrimitiveValue.CSS_PX
												: CSSPrimitiveValue.CSS_PERCENTAGE,
										dim.x, String.format("%fpx", dim.x)),
						dim.yUnit == Unit.AUTO ? new PropertyValue(IdentValue.AUTO)
								: new PropertyValue(
										dim.yUnit == Unit.PX ? CSSPrimitiveValue.CSS_PX
												: CSSPrimitiveValue.CSS_PERCENTAGE,
										dim.y, String.format("%fpx", dim.y)));
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
	}

	protected void setCssSize(CSSName size, Size dim) {
		PropertyDeclaration decl = dim == null
				? CssExtensions.createValues(size, StylesheetInfo.USER, false, new PropertyValue(IdentValue.AUTO),
						new PropertyValue(IdentValue.AUTO))
				: CssExtensions.createValues(size, StylesheetInfo.USER, false,
						dim.xUnit == Unit.AUTO ? new PropertyValue(IdentValue.AUTO)
								: new PropertyValue(
										dim.xUnit == Unit.PX ? CSSPrimitiveValue.CSS_PX
												: CSSPrimitiveValue.CSS_PERCENTAGE,
										dim.x, String.format("%f%s", dim.x, dim.xUnit == Unit.PX ? "px" : "%")),
						dim.yUnit == Unit.AUTO ? new PropertyValue(IdentValue.AUTO)
								: new PropertyValue(
										dim.yUnit == Unit.PX ? CSSPrimitiveValue.CSS_PX
												: CSSPrimitiveValue.CSS_PERCENTAGE,
										dim.y, String.format("%f%s", dim.y, dim.yUnit == Unit.PX ? "px" : "%")));
		cssState.addAllCssDeclaration(decl);
		applyCss(decl);
		layoutChildren();
	}

	protected void parseLayoutData(String layoutData) {
	}
}
