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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;

import icetone.core.Layout.LayoutType;
import icetone.css.CssEventTrigger;
import icetone.css.CssExtensions;
import icetone.css.CssUtil;
import icetone.text.FontSpec;
import icetone.text.TextStyle;

/**
 * As {@link Element} adds CSS styling support to {@link BaseElement}, this
 * class adds CSS support at the {@link BaseScreen} level too. It is recommended
 * you use this over {@link BaseScreen} as it may contain both styled and
 * non-styled elements.
 */
public class Screen extends BaseScreen implements StyledNode<BaseScreen, UIEventTarget> {
	final static Logger LOG = Logger.getLogger(Screen.class.getName());

	final static List<String> STYLE_CLASS_NAMES = Arrays.asList("Screen");

	private String css;
	private CssState cssState;
	private PseudoStyles ps;
	private String styleClass;

	private String styleId;

	public Screen() {
		super();
	}

	public Screen(Application app) {
		super(app);
	}

	public Screen(float width, float height) {
		super(width, height);
	}

	@Override
	public void applyCss(PropertyDeclaration decl) {
		String n = decl.getPropertyName();
		if (decl.getCSSName() == CSSName.COLOR) {
			ColorRGBA col = CssUtil.toFontColor(decl, this);
			if (!Objects.equals(col, fontColor)) {
				fontColor = col;
				dirtyLayout(true, LayoutType.text);
			}
		} else if (n.startsWith("font")) {
			applyCssFont(decl);
		} else if (n.startsWith("text") || n.startsWith("-it-text")) {
			applyCssText(decl);
		}
	}

	@Override
	public List<CssEventTrigger<?>> getActiveEvents() {
		return Collections.emptyList();
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
		return ps;
	}

	@Override
	public String getStyleClass() {
		return styleClass;
	}

	@Override
	public List<String> getStyleClassNames() {
		return STYLE_CLASS_NAMES;
	}

	@Override
	public ElementContainer<?, ?> getStyledParentContainer() {
		return getParentContainer();
	}

	@Override
	public String getStyleId() {
		return styleId;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Screen setCss(String css) {
		if (!Objects.equals(css, this.css)) {
			this.css = css;
			dirtyLayout(false, LayoutType.reset);
			layoutChildren();
		}
		return this;
	}

	@Override
	public BaseScreen setElementAlpha(float elementAlpha) {
		if (this.elementAlpha != elementAlpha) {
			cssState.setElementAlpha(elementAlpha);
		}
		return this;
	}

	@Override
	public BaseScreen setFont(FontSpec fontName) {
		cssState.setFont(fontName);
		return this;
	}

	@Override
	public BaseScreen setFontColor(ColorRGBA fontColor) {
		if (!fontColor.equals(this.fontColor)) {
			cssState.setFontColor(fontColor);
		}
		return this;
	}

	public Screen setStyleClass(String styleClass) {
		if (!Objects.equals(styleClass, this.styleClass)) {
			this.styleClass = styleClass;
			dirtyLayout(true, LayoutType.reset);
			layoutChildren();
		}
		return this;
	}

	public Screen setStyleId(String styleId) {
		if (!Objects.equals(styleId, this.styleId)) {
			this.styleId = styleId;
			dirtyLayout(true, LayoutType.reset);
			layoutChildren();
		}
		return this;
	}

	protected void applyCssFont(PropertyDeclaration decl) {
		CSSName cssName = decl.getCSSName();
		if (cssName == CSSName.FONT_FAMILY) {
			String fn = null;
			if (decl.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				if (decl.asIdentValue() != IdentValue.INHERIT)
					throw new UnsupportedOperationException(
							String.format("Invalid font fammily %s", decl.getValue().toString()));
			} else
				fn = decl.getValue().getStringValue();
			if (font == null) {
				font = new FontSpec(fn);
				dirtyLayout(true, LayoutType.text);
			} else if (!Objects.equals(font.getFamily(), fn)) {
				font = font.deriveFromFamily(null, fn);
				dirtyLayout(true, LayoutType.text);
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
				fs = decl.getValue().getFloatValue(CSSPrimitiveValue.CSS_PT);
			if (font == null) {
				font = new FontSpec(fs);
				dirtyLayout(true, LayoutType.text);
			} else if (fs != font.getSize()) {
				font = font.deriveFromSize(fs);
				dirtyLayout(true, LayoutType.text);
			}
		} else if (cssName == CSSName.FONT_STYLE) {
			if (decl.asIdentValue() == IdentValue.INHERIT) {
				if (font != null && !font.isInheritItalic()) {
					font = font.inheritStyle(TextStyle.ITALIC);
					dirtyLayout(true, LayoutType.text);
				}
			} else if (decl.asIdentValue() == IdentValue.NORMAL) {
				if (font != null && (font.isItalic() || font.isInheritItalic())) {
					font = font.removeStyle(TextStyle.ITALIC);
					dirtyLayout(true, LayoutType.text);
				}
			} else if (decl.asIdentValue() == IdentValue.ITALIC || decl.asIdentValue() == IdentValue.OBLIQUE) {
				if (font == null) {
					font = new FontSpec(TextStyle.ITALIC);
					dirtyLayout(true, LayoutType.text);
				} else {
					font = font.addStyle(TextStyle.ITALIC);
					dirtyLayout(true, LayoutType.text);
				}
			}
		} else if (cssName == CSSName.FONT_WEIGHT) {
			if (decl.asIdentValue() == IdentValue.INHERIT) {
				if (font != null && !font.isInheritBold()) {
					font = font.inheritStyle(TextStyle.BOLD);
					dirtyLayout(true, LayoutType.text);
				}
			} else if (decl.asIdentValue() == IdentValue.NORMAL) {
				if (font != null && (font.isBold() || font.isInheritBold())) {
					font = font.removeStyle(TextStyle.BOLD);
					dirtyLayout(true, LayoutType.text);
				}
			} else if (decl.asIdentValue() == IdentValue.BOLD || decl.asIdentValue() == IdentValue.BOLDER) {
				if (font == null) {
					font = new FontSpec(TextStyle.BOLD);
					dirtyLayout(true, LayoutType.text);
				} else {
					font = font.addStyle(TextStyle.BOLD);
					dirtyLayout(true, LayoutType.text);
				}
			}
		}
	}

	protected void applyCssText(PropertyDeclaration decl) {
		CSSName cssName = decl.getCSSName();
		if (cssName == CSSName.TEXT_DECORATION) {
			if (decl.asIdentValue() == IdentValue.INHERIT) {
				if (font != null && !font.isInheritUnderline()) {
					font = font.inheritStyle(TextStyle.UNDERLINE);
					dirtyLayout(true, LayoutType.text);
				}
			} else if (decl.asIdentValue() == IdentValue.NORMAL) {
				if (font != null && (font.isUnderline() || font.isInheritUnderline())) {
					font = font.removeStyle(TextStyle.ITALIC);
					dirtyLayout(true, LayoutType.text);
				}
			} else if (decl.asIdentValue() == IdentValue.UNDERLINE) {
				if (font == null) {
					font = new FontSpec(TextStyle.UNDERLINE);
					dirtyLayout(true, LayoutType.text);
				} else {
					font = font.addStyle(TextStyle.UNDERLINE);
					dirtyLayout(true, LayoutType.text);
				}
			}
		} else if (cssName.equals(CssExtensions.TEXT_ENGINE)) {
			String textEngine = decl.getValue().getStringValue();
			if (font == null) {
				font = new FontSpec(null, null, -1, textEngine, null, 0);
				dirtyLayout(true, LayoutType.text);
			} else if (!Objects.equals(font.getEngine(), textEngine)) {
				font = font.deriveFromTextEngine(textEngine);
				dirtyLayout(true, LayoutType.text);
			}
		}
	}

	@Override
	protected final void configureScreen() {
		cssState = new CssState(this);
		ps = new PseudoStyles();
		onConfigureStyledScreen();
	}

	@Override
	protected final void onAfterLayout() {
		if (cssState != null)
			cssState.completeCssProcessing();
		onAfterStyledLayout();
	}

	protected void onAfterStyledLayout() {
	}

	protected void onConfigureStyledScreen() {

	}

	protected void onPreConfigureScreen() {
	}

	@Override
	protected final void preConfigureScreen() {
		cssState = new CssState(this);
		onPreConfigureScreen();
	}
}
