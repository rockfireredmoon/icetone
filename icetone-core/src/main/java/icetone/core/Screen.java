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
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;

import icetone.core.Layout.LayoutType;
import icetone.css.CssEvent;
import icetone.css.CssUtil;

/**
 * As {@link Element} adds CSS styling support to {@link BaseElement}, this
 * class adds CSS support at the {@link BaseScreen} level too. It is recommended
 * you use this over {@link BaseScreen} as it may contain both styled and
 * non-styled elements.
 */
public class Screen extends BaseScreen implements StyledNode<BaseScreen, UIEventTarget> {
	final static Logger LOG = Logger.getLogger(Screen.class.getName());

	final static List<String> STYLE_CLASS_NAMES = Arrays.asList("Screen");

	private String styleClass;
	private String styleId;
	private String css;
	private CssState cssState;

	private PseudoStyles ps;

	public Screen() {
		super();
	}

	public Screen(float width, float height) {
		super(width, height);
	}

	public Screen(Application app) {
		super(app);
	}

	public Screen setStyleClass(String styleClass) {
		if (!Objects.equals(styleClass, this.styleClass)) {
			this.styleClass = styleClass;
			dirtyLayout(true, LayoutType.reset);
			layoutChildren();
		}
		return this;
	}

	@Override
	public List<String> getStyleClassNames() {
		return STYLE_CLASS_NAMES;
	}

	@Override
	public String getStyleClass() {
		return styleClass;
	}

	@Override
	public PseudoStyles getPseudoStyles() {
		return ps;
	}

	@Override
	public String getStyleId() {
		return styleId;
	}

	public Screen setStyleId(String styleId) {
		this.styleId = styleId;
		dirtyLayout(true, LayoutType.reset);
		layoutChildren();
		return this;
	}

	public Screen setCss(String css) {
		this.css = css;
		dirtyLayout(false, LayoutType.reset);
		layoutChildren();
		return this;
	}

	@Override
	public BaseScreen setFontColor(ColorRGBA fontColor) {
		cssState.setFontColor(fontColor);
		return this;
	}

	@Override
	public BaseScreen setFontFamily(String fontName) {
		cssState.setFontFamily(fontName);
		return this;
	}

	@Override
	public BaseScreen setFontSize(float fontSize) {
		cssState.setFontSize(fontSize);
		return this;
	}

	@Override
	public String getCss() {
		return css;
	}

	@Override
	protected final void onAfterLayout() {
		if (cssState != null)
			cssState.completeCssProcessing();
		onAfterStyledLayout();
	}

	protected void onAfterStyledLayout() {
	}

	@Override
	public void applyCss(PropertyDeclaration decl) {

		String n = decl.getPropertyName();
		CSSPrimitiveValue v = decl.getValue();
		CSSName cssName = decl.getCSSName();

		if (cssName == CSSName.COLOR) {
			ColorRGBA col = CssUtil.toFontColor(decl, this);
			if (!Objects.equals(col, fontColor)) {
				fontColor = col;
				dirtyLayout(true, LayoutType.text);
			}
		} else if (cssName == CSSName.FONT_SIZE) {
			float fs;
			if (decl.getValue().getPrimitiveType() == CSSValue.CSS_INHERIT) {
				fs = -1;
			} else
				fs = v.getFloatValue(CSSPrimitiveValue.CSS_PT);
			if (fs != fontSize) {
				fontSize = fs;
				dirtyLayout(true, LayoutType.text);
			}
		} else if (cssName == CSSName.FONT_FAMILY) {
			String fn = null;
			if (decl.getValue().getPrimitiveType() != CSSValue.CSS_INHERIT) {
				fn = v.getStringValue();
			}
			if (!Objects.equals(fn, fontFamily)) {
				fontFamily = fn;
				dirtyLayout(true, LayoutType.text);
			}
		} else {
			// LOG.warning(String.format("Unknown style %s (%s) in %s", n,
			// v.getStringValue(), toString()));
		}
	}

	@Override
	public CssState getCssState() {
		return cssState;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public List<CssEvent> getActiveEvents() {
		return Collections.emptyList();
	}

	@Override
	protected final void preConfigureScreen() {
		cssState = new CssState(this);
		onPreConfigureScreen();
	}

	protected void onPreConfigureScreen() {
	}

	@Override
	protected final void configureScreen() {
		cssState = new CssState(this);
		ps = new PseudoStyles();
		onConfigureStyledScreen();
	}

	protected void onConfigureStyledScreen() {

	}
}
