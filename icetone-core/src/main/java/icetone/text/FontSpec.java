/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2017, Emerald Icemoon
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
package icetone.text;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jme3.math.ColorRGBA;

import icetone.core.BaseElement;
import icetone.css.CssUtil;

/**
 * Specification of the font to be used for text on {@link BaseElement}.
 * Instances of this class are immutable, so instead of setting font attributes
 * on existing instances, new instances must be created either via the
 * constructor, or based on other {@link FontSpec} using
 * {@link #deriveFromSize(float)} and other 'derive' methods.
 * <p>
 * Font specifications are inheritable. In general, if a {@link BaseElement}
 * does not have a font set via either CSS or programmatically using
 * {@link BaseElement#setFont(FontSpec)}, then the parent will be queried, and
 * so on until the one is found. An individual {@link FontSpec} may also omit
 * certain details, such as the <strong>Size</strong>, <strong>Family</strong>
 * or <strong>Styles</strong>. In this case, those details are also resolved
 * from the parent until a full resolved font specification is built.
 * <p>
 * The font specification must eventually be turned into {@link FontInfo} for
 * use by the {@link TextEngine} either supplied by this object or by default.
 * This is used to create {@link TextElement} instances that actual render text.
 * <p>
 * Font specifications may also carry optional <strong>Properties</strong> that
 * are specific to the engine. See the documentation for the text engine in
 * question for more details.
 * 
 * @see FontInfo
 * @see TextEngine
 */
public class FontSpec implements Cloneable {

	private final String engine;
	private final String family;
	private final String path;
	private final Set<TextStyle> styles;
	private final Set<TextStyle> inheritStyles;
	private final float size;
	private final Map<String, String> properties;
	private final float characterSpacing;

	FontSpec() {
		styles = new HashSet<>();
		engine = null;
		path = null;
		family = null;
		properties = null;
		size = -1f;
		characterSpacing = Float.MIN_VALUE;
		inheritStyles = new HashSet<>(Arrays.asList(TextStyle.values()));
	}

	/**
	 * Constructor.
	 * 
	 * @param fontPath font path (or null)
	 * @param family   family (or null)
	 * @param size     size (or -1)
	 * @param styles   optional styles
	 */
	public FontSpec(String fontPath, String family, float size, TextStyle... styles) {
		this(fontPath, family, size, Float.MIN_VALUE, styles);
	}

	/**
	 * Constructor.
	 * 
	 * @param fontPath         font path (or null)
	 * @param family           family (or null)
	 * @param size             size (or -1)
	 * @param characterSpacing character spacing
	 * @param styles           optional styles
	 */
	public FontSpec(String fontPath, String family, float size, float characterSpacing, TextStyle... styles) {
		this(fontPath, family, size, null, null, characterSpacing, styles);
	}

	/**
	 * Constructor.
	 * 
	 * @param styles optional styles
	 */
	public FontSpec(TextStyle... styles) {
		this(null, -1, styles);
	}

	/**
	 * Constructor.
	 * 
	 * @param family family (or null)
	 * @param styles optional styles
	 */
	public FontSpec(String family, TextStyle... styles) {
		this(family, -1, styles);
	}

	/**
	 * Constructor.
	 * 
	 * @param size   size (or -1)
	 * @param styles optional styles
	 */
	public FontSpec(float size, TextStyle... styles) {
		this(null, size, styles);
	}

	/**
	 * Constructor.
	 * 
	 * @param family family (or null)
	 * @param size   size (or -1)
	 * @param styles optional styles
	 */
	public FontSpec(String family, float size, TextStyle... styles) {
		this(null, family, size, styles);
	}

	/**
	 * Convenience constructor.
	 * 
	 * @param family family (or null)
	 * @param size   size (or -1)
	 * @param bold bold
	 * @param italic italic
	 * @param underline underline
	 */
	public FontSpec(String family, float size, boolean bold, boolean italic, boolean underline) {
		this(null, family, size, getStyles(bold, italic, underline));
	}

	/**
	 * Constructor.
	 * 
	 * @param path             font path (or null)
	 * @param family           family (or null)
	 * @param size             size (or -1)
	 * @param engine           engine (or null)
	 * @param properties       properties
	 * @param characterSpacing character spacing
	 * @param styles           optional styles
	 */
	public FontSpec(String path, String family, float size, String engine, Map<String, String> properties,
			float characterSpacing, TextStyle... styles) {
		this.engine = engine;
		this.characterSpacing = characterSpacing;
		this.path = path;
		this.properties = properties == null ? null : Collections.unmodifiableMap(properties);
		this.family = family;
		this.size = size;
		this.styles = new HashSet<>(Arrays.asList(styles));
		inheritStyles = new HashSet<>(Arrays.asList(TextStyle.values()));
		inheritStyles.removeAll(this.styles);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public FontSpec clone() {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	public float getCharacterSpacing() {
		return characterSpacing;
	}

	public String getFamily() {
		return family;
	}

	public String getPath() {
		return path;
	}

	public TextStyle[] getStyles() {
		return styles.toArray(new TextStyle[0]);
	}

	public String getEngine() {
		return engine;
	}

	public float getSize() {
		return size;
	}

	public boolean isItalic() {
		return styles.contains(TextStyle.ITALIC);
	}

	public boolean isInheritItalic() {
		return inheritStyles.contains(TextStyle.ITALIC);
	}

	public boolean isUnderline() {
		return styles.contains(TextStyle.UNDERLINE);
	}

	public boolean isInheritUnderline() {
		return inheritStyles.contains(TextStyle.UNDERLINE);
	}

	public boolean isBold() {
		return styles.contains(TextStyle.BOLD);
	}

	public boolean isInheritBold() {
		return inheritStyles.contains(TextStyle.BOLD);
	}

	@Override
	public String toString() {
		return "FontSpec [engine=" + engine + ", family=" + family + ", path=" + path + ", styles=" + styles
				+ ", inheritStyles=" + inheritStyles + ", size=" + size + ", properties=" + properties
				+ ", characterSpacing=" + characterSpacing + "]";
	}

	public FontSpec deriveFromStyle(boolean bold, boolean italic, boolean underline) {
		TextStyle[] arr = getStyles(bold, italic, underline);
		return new FontSpec(path, family, size, engine, properties, characterSpacing, arr);
	}

	public FontSpec deriveFromStyle(TextStyle... styles) {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles);
	}

	public FontSpec deriveFromFamily(String path, String family) {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	public FontSpec deriveFromTextEngine(String engine) {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	public FontSpec deriveFromSize(float size) {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	public FontSpec deriveFromCharacterSpacing(float characterSpacing) {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	public FontSpec derivePath(String path) {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	public FontSpec deriveProperty(String property, boolean value) {
		return deriveProperty(property, String.valueOf(value));
	}

	public FontSpec deriveProperty(String property, Number value) {
		return deriveProperty(property, value.toString());
	}

	public FontSpec deriveProperty(String property, ColorRGBA color) {
		return deriveProperty(property, CssUtil.toString(color));
	}

	public FontSpec deriveProperty(String property, String value) {
		Map<String, String> p = new HashMap<>();
		if (properties != null)
			p.putAll(properties);
		p.put(property, value);
		return deriveProperties(p);
	}

	public FontSpec deriveProperties(Map<String, String> properties) {
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(characterSpacing);
		result = prime * result + ((engine == null) ? 0 : engine.hashCode());
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + ((inheritStyles == null) ? 0 : inheritStyles.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + Float.floatToIntBits(size);
		result = prime * result + ((styles == null) ? 0 : styles.hashCode());
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
		FontSpec other = (FontSpec) obj;
		if (Float.floatToIntBits(characterSpacing) != Float.floatToIntBits(other.characterSpacing))
			return false;
		if (engine == null) {
			if (other.engine != null)
				return false;
		} else if (!engine.equals(other.engine))
			return false;
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
			return false;
		if (inheritStyles == null) {
			if (other.inheritStyles != null)
				return false;
		} else if (!inheritStyles.equals(other.inheritStyles))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (Float.floatToIntBits(size) != Float.floatToIntBits(other.size))
			return false;
		if (styles == null) {
			if (other.styles != null)
				return false;
		} else if (!styles.equals(other.styles))
			return false;
		return true;
	}

	public FontSpec inheritStyle(TextStyle style) {
		FontSpec fs = new FontSpec(path, family, size, engine, properties, characterSpacing,
				styles.toArray(new TextStyle[0]));
		fs.styles.remove(style);
		fs.inheritStyles.add(style);
		return fs;
	}

	public FontSpec removeStyle(TextStyle style) {
		FontSpec fs = new FontSpec(path, family, size, engine, properties, characterSpacing,
				styles.toArray(new TextStyle[0]));
		fs.styles.remove(style);
		fs.inheritStyles.remove(style);
		return fs;
	}

	public FontSpec addStyle(TextStyle style) {
		Set<TextStyle> styles = new HashSet<>();
		styles.addAll(this.styles);
		styles.add(style);
		return new FontSpec(path, family, size, engine, properties, characterSpacing, styles.toArray(new TextStyle[0]));
	}

	public boolean isValid() {
		return path != null || family != null;
	}

	protected static TextStyle[] getStyles(boolean bold, boolean italic, boolean underline) {
		Set<TextStyle> styles = new HashSet<>();
		if (bold)
			styles.add(TextStyle.BOLD);
		if (italic)
			styles.add(TextStyle.ITALIC);
		if (underline)
			styles.add(TextStyle.UNDERLINE);
		TextStyle[] arr = styles.toArray(new TextStyle[0]);
		return arr;
	}
}
