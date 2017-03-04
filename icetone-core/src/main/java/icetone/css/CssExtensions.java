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
package icetone.css;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

/**
 * Extends the Flying Saucer CSS parser with Icetone custom styles. A horrible
 * hack because much of the CSS system is private. The intention is to request
 * the project make this easier, or fork it myself..
 * 
 * @author rockfire
 */
@SuppressWarnings("rawtypes")
public class CssExtensions {

	static {
		CSSName.getByID(0);
	}

	// public static class Atlas extends OneToFourPropertyBuilder {
	// protected CSSName[] getProperties() {
	// return new CSSName[] { ATLAS_X, ATLAS_Y, ATLAS_WIDTH, ATLAS_HEIGHT };
	// }
	//
	// protected PropertyBuilder getPropertyBuilder() {
	// return PrimitivePropertyBuilders.MARGIN;
	// }
	// }

	public final static IdentValue NS_RESIZE = addValue("ns-resize");
	public final static IdentValue EW_RESIZE = addValue("ew-resize");
	public final static IdentValue NWSE_RESIZE = addValue("nwse-resize");
	public final static IdentValue NESW_RESIZE = addValue("nesw-resize");

	private static final Integer PRIMITIVE = new Integer(0);
	private static final Integer SHORTHAND = new Integer(1);
	private static final Integer INHERITS = new Integer(2);
	private static final Integer NOT_INHERITED = new Integer(3);

	public final static IdentValue X_SOFT = addValue("x-soft");
	public final static IdentValue SOFT = addValue("soft");
	public final static IdentValue MEDIUM = addValue("medium");
	public final static IdentValue LOUD = addValue("loud");
	public final static IdentValue X_LOUD = addValue("x-loud");
	public final static IdentValue SILENT = addValue("silence");

	public final static IdentValue REVERSE = addValue("reverse");
	public final static IdentValue INFINITE = addValue("infinite");
	public final static IdentValue FXDIR_LEFT = addValue("-it-left");
	public final static IdentValue FXDIR_RIGHT = addValue("-it-right");
	public final static IdentValue FXDIR_TOP = addValue("-it-top");
	public final static IdentValue FXDIR_BOTTOM = addValue("-it-bottom");
	public final static IdentValue LINEAR = addValue("-it-linear");
	public final static IdentValue FADE = addValue("-it-fade");
	public final static IdentValue CIRCLE = addValue("-it-circle");
	public final static IdentValue CIRCLE_IN = addValue("-it-circleIn");
	public final static IdentValue CIRCLE_OUT = addValue("-it-circleOut");
	public final static IdentValue SINE = addValue("-it-sine");
	public final static IdentValue SINE_IN = addValue("-it-sineIn");
	public final static IdentValue SINE_OUT = addValue("-it-sineOut");
	public final static IdentValue EXP10 = addValue("-it-exp10");
	public final static IdentValue EXP10_IN = addValue("-it-exp10In");
	public final static IdentValue EXP10_OUT = addValue("-it-exp10Out");
	public final static IdentValue EXP5 = addValue("-it-exp5");
	public final static IdentValue EXP5_IN = addValue("-it-exp5In");
	public final static IdentValue EXP5_OUT = addValue("-it-exp5Out");
	public final static IdentValue ELASTIC = addValue("-it-elastic");
	public final static IdentValue ELASTIC_IN = addValue("-it-elasticIn");
	public final static IdentValue ELASTIC_OUT = addValue("-it-elasticOut");
	public final static IdentValue SWING = addValue("-it-swing");
	public final static IdentValue SWING_IN = addValue("-it-swingIn");
	public final static IdentValue SWING_OUT = addValue("-it-swingOut");
	public final static IdentValue BOUNCE = addValue("-it-bounce");
	public final static IdentValue BOUNCE_IN = addValue("-it-bounceIn");
	public final static IdentValue BOUNCE_OUT = addValue("-it-bounceOut");
	public final static IdentValue POW2 = addValue("-it-pow2");
	public final static IdentValue POW2_IN = addValue("-it-pow2In");
	public final static IdentValue POW2_OUT = addValue("-it-pow2Out");
	public final static IdentValue POW3 = addValue("-it-pow3");
	public final static IdentValue POW3_IN = addValue("-it-pow3In");
	public final static IdentValue POW3_OUT = addValue("-it-pow3Out");
	public final static IdentValue POW4 = addValue("-it-pow4");
	public final static IdentValue POW4_IN = addValue("-it-pow4In");
	public final static IdentValue POW4_OUT = addValue("-it-pow4Out");
	public final static IdentValue POW5 = addValue("-it-pow5");
	public final static IdentValue POW5_IN = addValue("-it-pow5In");
	public final static IdentValue POW5_OUT = addValue("-it-pow5Out");

	/**
	 * Unique CSSName instance for CSS2 property.
	 */
	public final static CSSName CURSOR = addProperty("cursor", PRIMITIVE, "auto", INHERITS, true, new Cursor());

	public final static CSSName PLAY_DURING_SOUND = addProperty("play-during-sound", PRIMITIVE, "none", NOT_INHERITED,
			true, new PlayDuringSound());

	public final static CSSName PLAY_DURING_SHORTHAND = addProperty("play-during", SHORTHAND, "none", NOT_INHERITED,
			true, new PlayDuringPropertyBuilder());

	public final static CSSName OVERFLOW = addProperty("overflow", PRIMITIVE, "visible", NOT_INHERITED, true,
			new Overflow());
	public final static CSSName OVERFLOW_X = addProperty("-it-overflow-x", PRIMITIVE, "0", NOT_INHERITED, true,
			new Overflow());
	public final static CSSName OVERFLOW_Y = addProperty("-it-overflow-y", PRIMITIVE, "0", NOT_INHERITED, true,
			new Overflow());

	public final static CSSName TEXT_ROTATION = addProperty("-it-text-rotation", PRIMITIVE, "auto", NOT_INHERITED, true,
			new TextRotation());
	public final static CSSName ANIMATION_NAME = addProperty("animation-name", PRIMITIVE, "auto", NOT_INHERITED, true,
			new AnimationName());
	public final static CSSName LAYOUT = addProperty("-it-layout", PRIMITIVE, "none", NOT_INHERITED, true,
			new LayoutData());
	public final static CSSName LAYOUT_DATA = addProperty("layout-data", PRIMITIVE, "none", NOT_INHERITED, true,
			new LayoutData());
	public final static CSSName ANIMATION_DURATION = addProperty("animation-duration", PRIMITIVE, "auto", NOT_INHERITED,
			true, new AnimationDuration());
	public final static CSSName ANIMATION_DELAY = addProperty("animation-delay", PRIMITIVE, "auto", NOT_INHERITED, true,
			new AnimationDelay());
	public final static CSSName ANIMATION_DIRECTION = addProperty("animation-direction", PRIMITIVE, "auto",
			NOT_INHERITED, true, new AnimationDirection());
	public final static CSSName ANIMATION_REVERSE = addProperty("-it-animation-reverse", PRIMITIVE, "auto",
			NOT_INHERITED, true, new AnimationReverse());
	public final static CSSName ANIMATION_ITERATION_COUNT = addProperty("animation-iteration-count", PRIMITIVE, "1",
			NOT_INHERITED, true, new AnimationIterationCount());
	public final static CSSName ANIMATION_TIMING_FUNCTION = addProperty("animation-timing-function", PRIMITIVE, "auto",
			NOT_INHERITED, true, new AnimationTimingFunction());

	public final static CSSName ANIMATION_DESTINATION_X = addProperty("-it-animation-destination-x", PRIMITIVE, "0",
			NOT_INHERITED, true, new AnimationDestinationX());

	public final static CSSName ANIMATION_DESTINATION_Y = addProperty("-it-animation-destination-y", PRIMITIVE, "0",
			NOT_INHERITED, true, new AnimationDestinationY());

	public final static CSSName ANIMATION_BLEND_COLOR = addProperty("-it-animation-blend-color", PRIMITIVE, "0",
			NOT_INHERITED, true, new GenericColor());

	public final static CSSName ANIMATION_IMAGE = addProperty("-it-animation-image", PRIMITIVE, "auto", NOT_INHERITED,
			true, new AnimationImage());

	public final static CSSName BGMAP_IMAGE = addProperty("-it-bgmap-image", PRIMITIVE, "auto", NOT_INHERITED, true,
			new BgMapImage());

	public final static CSSName BGMAP_COLOR = addProperty("-it-bgmap-color", PRIMITIVE, "0", NOT_INHERITED, true,
			new GenericColor());

	public final static CSSName OPACITY = addProperty("opacity", PRIMITIVE, "auto", NOT_INHERITED, true, new Opacity());

	public final static CSSName BACKGROUND_OPACITY = addProperty("-it-background-opacity", PRIMITIVE, "auto",
			NOT_INHERITED, true, new Opacity());

	public final static CSSName VOLUME = addProperty("volume", PRIMITIVE, "auto", NOT_INHERITED, true,
			new AbstractPropertyBuilder() {
				BitSet allowed = setFor(new IdentValue[] { X_SOFT, SOFT, MEDIUM, LOUD, X_LOUD, SILENT });

				@Override
				public List buildDeclarations(CSSName cssName, List values, int origin, boolean important,
						boolean inheritAllowed) {
					checkValueCount(cssName, 1, values.size());
					PropertyValue value = (PropertyValue) values.get(0);
					checkInheritAllowed(value, inheritAllowed);
					if (value.getCssValueType() != CSSValue.CSS_INHERIT) {
						checkIdentNumberOrPercentType(cssName, value);

						if (value.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
							IdentValue ident = checkIdent(cssName, value);
							checkValidity(cssName, allowed, ident);
						} else if (value.getFloatValue() < 0.0f) {
							throw new CSSParseException(cssName + " may not be negative", -1);
						}
					}

					return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));

				}

				protected void checkIdentNumberOrPercentType(CSSName cssName, CSSPrimitiveValue value) {
					int type = value.getPrimitiveType();
					if (type != CSSPrimitiveValue.CSS_IDENT && type != CSSPrimitiveValue.CSS_PERCENTAGE
							&& type != CSSPrimitiveValue.CSS_NUMBER) {
						throw new CSSParseException(
								"Value for " + cssName + " must be an identifier, length, or percentage", -1);
					}
				}
			});
	/**
	 * Unique CSSName instance for CSS2 property.
	 */

	public static final PropertyBuilder CLIP_PADDING = new NonNegativeLengthLike();

	public final static CSSName CLIP_PADDING_SHORTHAND = addProperty("-it-clip-padding", SHORTHAND, "0", NOT_INHERITED,
			true, new ClipPadding());

	public final static CSSName CLIP_PADDING_TOP = addProperty("-it-clip-padding-top", PRIMITIVE, "0", NOT_INHERITED,
			true, new PrimitivePropertyBuilders.PaddingTop());

	public final static CSSName CLIP_PADDING_BOTTOM = addProperty("-it-clip-padding-bottom", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.PaddingTop());

	public final static CSSName CLIP_PADDING_LEFT = addProperty("-it-clip-padding-left", PRIMITIVE, "0", NOT_INHERITED,
			true, new PrimitivePropertyBuilders.PaddingTop());

	public final static CSSName CLIP_PADDING_RIGHT = addProperty("-it-clip-padding-right", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.PaddingTop());

	public static final PropertyBuilder HANDLE_POSITION = new NonNegativeLengthLike();

	public final static CSSName HANDLE_POSITION_SHORTHAND = addProperty("-it-handle-position", SHORTHAND, "0",
			NOT_INHERITED, true, new HandlePosition());

	public final static CSSName HANDLE_POSITION_TOP = addProperty("-it-handle-position-top", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.PaddingTop());

	public final static CSSName HANDLE_POSITION_BOTTOM = addProperty("-it-handle-position-bottom", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.PaddingTop());

	public final static CSSName HANDLE_POSITION_LEFT = addProperty("-it-handle-position-left", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.PaddingTop());

	public final static CSSName HANDLE_POSITION_RIGHT = addProperty("-it-handle-position-right", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.PaddingTop());

	public static final PropertyBuilder BORDER_OFFSET = new LengthLike();

	public final static CSSName BORDER_OFFSET_SHORTHAND = addProperty("-it-border-offset", SHORTHAND, "0",
			NOT_INHERITED, true, new BorderOffset());

	public final static CSSName BORDER_OFFSET_TOP = addProperty("-it-border-offset-top", PRIMITIVE, "0", NOT_INHERITED,
			true, new PrimitivePropertyBuilders.MarginBottom());

	public final static CSSName BORDER_OFFSET_BOTTOM = addProperty("-it-border-offset-bottom", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.MarginBottom());

	public final static CSSName BORDER_OFFSET_LEFT = addProperty("-it-border-offset-left", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.MarginLeft());

	public final static CSSName BORDER_OFFSET_RIGHT = addProperty("-it-border-offset-right", PRIMITIVE, "0",
			NOT_INHERITED, true, new PrimitivePropertyBuilders.MarginBottom());

	public static final PropertyBuilder ATLAS = new NonNegativeLengthLike();

	public final static CSSName ATLAS_SHORTHAND = addProperty("-it-atlas", SHORTHAND, "0", NOT_INHERITED, true,
			new Atlas());

	public final static CSSName ATLAS_Y = addProperty("-it-atlas-y", PRIMITIVE, "0", NOT_INHERITED, true, new AtlasY());

	public final static CSSName ATLAS_HEIGHT = addProperty("-it-atlas-height", PRIMITIVE, "0", NOT_INHERITED, true,
			new AtlasHeight());

	public final static CSSName ATLAS_X = addProperty("-it-atlas-x", PRIMITIVE, "0", NOT_INHERITED, true, new AtlasX());

	public final static CSSName ATLAS_WIDTH = addProperty("-it-atlas-width", PRIMITIVE, "0", NOT_INHERITED, true,
			new AtlasWidth());

	public static CSSName addProperty(String propName, Object type, String initialValue, Object inherit,
			boolean implemented, PropertyBuilder builder) {
		try {
			Field field = CSSName.class.getDeclaredField("ALL_PROPERTY_NAMES");
			field.setAccessible(true);
			Map ALL_PROPERTY_NAMES = (Map) field.get(null);
			CSSName n = null;
			Field maxAssField = null;
			int maxWas = -1;
			if (ALL_PROPERTY_NAMES.containsKey(propName)) {
				n = (CSSName) ALL_PROPERTY_NAMES.get(propName);
				maxAssField = CSSName.class.getDeclaredField("maxAssigned");
				maxAssField.setAccessible(true);
				maxWas = maxAssField.getInt(null);
				maxAssField.set(null, n.FS_ID);
			}
			Method m = CSSName.class.getDeclaredMethod("addProperty", String.class, Object.class, String.class,
					Object.class, boolean.class, PropertyBuilder.class);
			m.setAccessible(true);

			CSSName nm = (CSSName) m.invoke(null, propName, type, initialValue, inherit, implemented, builder);
			if (n != null) {
				n = (CSSName) ALL_PROPERTY_NAMES.get(propName);
				maxAssField.set(null, maxWas);
			}
			return nm;
		} catch (Exception e) {
			throw new RuntimeException("Could not install custom CSS properties.", e);
		}
	}

	public static IdentValue addValue(String value) {
		try {
			Method m = IdentValue.class.getDeclaredMethod("addValue", String.class);
			m.setAccessible(true);
			return (IdentValue) m.invoke(null, value);
		} catch (Exception e) {
			throw new RuntimeException("Could not install custom CSS properties.", e);
		}
	}

	public static PropertyDeclaration createValues(CSSName cssName, int origin, boolean important,
			CSSPrimitiveValue... values) {
		return new PropertyDeclaration(cssName, new PropertyValue(Arrays.asList(values)), important, origin);
	}

	static BitSet setFor(IdentValue[] values) {
		BitSet result = new BitSet(IdentValue.getIdentCount());
		for (int i = 0; i < values.length; i++) {
			IdentValue ident = values[i];
			result.set(ident.FS_ID);
		}
		return result;
	}

	public static void setInaccessibleField(Object value, String fieldName, Object object, Class<?> elementName) {
		try {
			Field f = elementName.getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(object, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void init() {
		try {
			Field field = CSSName.class.getDeclaredField("ALL_PROPERTY_NAMES");
			field.setAccessible(true);
			Map ALL_PROPERTY_NAMES = (Map) field.get(null);
			Iterator iter = ALL_PROPERTY_NAMES.values().iterator();
			CSSName[] ALL_PROPERTIES = new CSSName[ALL_PROPERTY_NAMES.size()];
			while (iter.hasNext()) {
				CSSName name = (CSSName) iter.next();
				ALL_PROPERTIES[name.FS_ID] = name;
			}
			field = CSSName.class.getDeclaredField("ALL_PROPERTIES");
			field.setAccessible(true);
			field.set(null, ALL_PROPERTIES);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
