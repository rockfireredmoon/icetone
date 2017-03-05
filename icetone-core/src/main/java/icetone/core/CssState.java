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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.newmatch.Matcher;
import org.xhtmlrenderer.css.newmatch.Selector;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

import com.jme3.math.ColorRGBA;

import icetone.css.CssProcessor;
import icetone.css.CssUtil;
import icetone.css.StylesheetProvider;
import icetone.css.CssProcessor.PseudoStyle;

/**
 * Abstracts much of the CSS handling required per {@link Element} so it may be
 * reused for {@link Screen} too.
 * <p>
 * This class looks after tracking when the element should be re-styled, gathers
 * all the required stylesheet's for the element and applies each individual
 * style.
 */
public class CssState implements Cloneable {
	final static Logger LOG = Logger.getLogger(Screen.class.getName());

	public static class StyleOverrideKey {
		private String name;
		private List<PseudoStyle> ps;

		public StyleOverrideKey(String decl, PseudoStyle... ps) {
			this.name = decl;
			this.ps = Arrays.asList(ps);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((ps == null) ? 0 : ps.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StyleOverrideKey other = (StyleOverrideKey) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (ps == null) {
				if (other.ps != null)
					return false;
			} else if (!ps.equals(other.ps))
				return false;
			return true;
		}

	}

	private StyledNode<?, ?> container;

	protected Matcher cssMatcher;
	private boolean restyle;
	private PseudoStyle[] lastPseudoStyles;
	private List<Stylesheet> lastStylesheets;
	protected Map<StyleOverrideKey, Ruleset> cssDeclarations;
	private Stylesheet elementStylesheet;

	public CssState(StyledNode<?, ?> container) {
		this.container = container;
	}

	public void configureFrom(CssState other) {

		// Use the same matcher and processor until it changes
		cssMatcher = other.cssMatcher;

		/*
		 * TODO no longer works - sort of because text content is stored in CSS
		 * declaration for styled elements but there is more to it (maybe pseduo
		 * styles)
		 */
		// el.cssDeclarations = cssDeclarations == null ? null : new
		// HashMap<>(cssDeclarations);
		// el.elementStylesheet = elementStylesheet == null ? null :
		// elementStylesheet;
		cssDeclarations = null;
		elementStylesheet = null;

		// ? hmm
		lastPseudoStyles = other.lastPseudoStyles == null ? null : other.lastPseudoStyles;
		lastStylesheets = other.lastStylesheets == null ? null : new LinkedList<>(other.lastStylesheets);
		restyle = other.restyle;
	}

	@Override
	public CssState clone() {
		CssState s = new CssState(container);
		s.configureFrom(this);
		return s;
	}

	public Matcher getCssMatcher() {
		recreateCssProcessor();
		return cssMatcher;
	}

	@SuppressWarnings("unchecked")
	public String getCurrentStyles() {
		recreateCssProcessor();
		StringBuilder bui = new StringBuilder();
		CascadedStyle style = cssMatcher.getCascadedStyle(container, restyle);
		for (Iterator<PropertyDeclaration> declIn = style.getCascadedPropertyDeclarations(); declIn.hasNext();) {
			PropertyDeclaration d = declIn.next();
			if (bui.length() > 0)
				bui.append("\n");
			bui.append(d.toString());
			;
		}
		return bui.toString();
	}

	public void applyCss() {
		recreateCssProcessor();
		if (LOG.isLoggable(Level.FINE))
			LOG.fine(String.format("Applying CSS to %s, restyle: %s, ps: %s", container, restyle,
					container.getPseudoStyles()));
		CascadedStyle style = cssMatcher.getCascadedStyle(container, restyle);
		applyStyle(null, style);
		restyle = false;
	}

	public void removeAllCssDeclaration(String name) {
		removeCssDeclaration(name);
		for (PseudoStyle p : PseudoStyle.values())
			removeCssDeclaration(name, p);
	}

	public void addAllCssDeclaration(PropertyDeclaration decl) {
		addCssDeclaration(decl);
		for (PseudoStyle p : PseudoStyle.values())
			addCssDeclaration(decl, p);
		resetCssProcessor();
	}

	public void removeCssDeclaration(String decl, PseudoStyle... ps) {
		if (cssDeclarations != null) {
			Ruleset prev = cssDeclarations.remove(new StyleOverrideKey(decl, ps));
			if (prev != null) {
				elementStylesheet.getContents().remove(prev);
				restyleCssProcessor();
			}
		}
	}

	public void addCssDeclaration(PropertyDeclaration decl, PseudoStyle... ps) {
		if (elementStylesheet == null)
			elementStylesheet = new Stylesheet("element://", StylesheetInfo.USER);
		if (cssDeclarations == null)
			cssDeclarations = new HashMap<>();
		String key = decl.getPropertyName();
		List<String> n = new ArrayList<>(ps.length);
		for (PseudoStyle p : ps)
			n.add(p.name());
		removeCssDeclaration(key, ps);
		Ruleset rules = new Ruleset(StylesheetInfo.USER);
		elementStylesheet.addContent(rules);
		cssDeclarations.put(new StyleOverrideKey(key, ps), rules);
		Selector selector = new Selector();
		selector.setParent(rules);
		selector.setName(null);
		for (PseudoStyle p : ps)
			p.select(selector);
		rules.addFSSelector(selector);
		rules.addProperty(decl);
		restyleCssProcessor();
	}

	public void recreateCssProcessor() {
		PseudoStyles ps = container.getPseudoStyles();
		PseudoStyle[] pseudoStyles = ps == null ? null : ps.asArray();
		if (cssMatcher == null || !Arrays.equals(pseudoStyles, lastPseudoStyles)) {
			lastPseudoStyles = pseudoStyles;
			if (cssMatcher == null) {
				lastStylesheets = getAllStyleSheets();
				cssMatcher = new Matcher(CssProcessor.DEFAULT, CssProcessor.DEFAULT,
						container.getThemeInstance().getStylesheetFactory(), lastStylesheets, "screen");
				restyle = false;
			} else {
				cssMatcher.removeStyle(this);
			}
		}
	}

	public void resetCssProcessor() {
		cssMatcher = null;
	}

	public void restyleCssProcessor() {
		restyle = true;
	}

	public void completeCssProcessing() {
		restyle = false;
	}

	public List<Stylesheet> getAllStyleSheets() {

		// TODO this should be a set
		List<Stylesheet> l = new ArrayList<Stylesheet>();
		if (elementStylesheet != null)
			l.add(elementStylesheet);
		if (container.getThemeInstance() != null) {
			l.addAll(container.getThemeInstance().getStylesheets());
		}
		ElementContainer<?, ?> container = this.container;
		while (container != null) {
			if (container instanceof StylesheetProvider) {
				Collection<Stylesheet> ss = ((StylesheetProvider) container).getStylesheets();
				if (ss != null) {
					for (Stylesheet sheet : ss) {
						if (!l.contains(sheet))
							l.add(0, sheet);
					}
				}
			}
			container = container.getParentContainer();
		}
		Collections.reverse(l);
		return l;
	}

	/**
	 * Sets the element's text layer font color
	 * 
	 * @param fontColor
	 *            ColorRGBA The color to set the font to
	 */
	public CssState setFontColor(ColorRGBA fontColor) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.COLOR, fontColor == null
				? new PropertyValue(IdentValue.INHERIT) : new PropertyValue(CssUtil.rgbaColor(fontColor)), false,
				StylesheetInfo.USER);
		addAllCssDeclaration(decl);
		container.applyCss(decl);
		container.layoutChildren();
		return this;
	}

	public CssState setFontFamily(String fontName) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.FONT_FAMILY,
				fontName == null ? new PropertyValue(IdentValue.INHERIT)
						: new PropertyValue(CSSPrimitiveValue.CSS_STRING, fontName, fontName),
				false, StylesheetInfo.USER);
		addAllCssDeclaration(decl);
		container.applyCss(decl);
		container.layoutChildren();
		return this;
	}

	public CssState setFontSize(float fontSize) {
		PropertyDeclaration decl = new PropertyDeclaration(CSSName.FONT_SIZE,
				fontSize == -1 ? new PropertyValue(IdentValue.INHERIT)
						: new PropertyValue(CSSPrimitiveValue.CSS_PT, fontSize, String.format("%dpt", (int) fontSize)),
				false, StylesheetInfo.USER);
		addAllCssDeclaration(decl);
		container.applyCss(decl);
		container.layoutChildren();
		return this;
	}

	protected void applyStyle(PseudoStyle p, CascadedStyle style) {
		if (style != null && container.isVisibilityAllowed()) {
			/*
			 * Style some names last, so widths/heights might be set correctly
			 * for bottom/right positioning.
			 */
			List<PropertyDeclaration> deferred = new LinkedList<>();
			for (Iterator<?> it = style.getCascadedPropertyDeclarations(); it.hasNext();) {
				PropertyDeclaration decl = (PropertyDeclaration) it.next();
				if (decl.getCSSName().equals(CSSName.RIGHT) || decl.getCSSName().equals(CSSName.BOTTOM))
					deferred.add(decl);
				else
					container.applyCss(decl);
			}
			for (PropertyDeclaration decl : deferred) {
				container.applyCss(decl);
			}
		}
	}

	public CascadedStyle getCascadedStyle(boolean restyle) {
		recreateCssProcessor();
		return cssMatcher.getCascadedStyle(container, restyle);
	}

	public void clear() {
		if (cssDeclarations != null)
			cssDeclarations.clear();

	}

}
