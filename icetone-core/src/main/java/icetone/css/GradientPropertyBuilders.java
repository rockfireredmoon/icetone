/*
 * {{{ header & license
 * Copyright (c) 2007 Wisconsin Court System
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package icetone.css;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.Conversions;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public class GradientPropertyBuilders {
	public static final BitSet GRADIENT_DIRECTIONS = CssExtensions.setFor(
			new IdentValue[] { IdentValue.NONE, IdentValue.AUTO, CssExtensions.VERTICAL, CssExtensions.HORIZONTAL });

	public class GradientDirection extends SingleIdent {
		@Override
		protected BitSet getAllowed() {
			return GRADIENT_DIRECTIONS;
		}
	}

	public static class GradientBuilder extends AbstractPropertyBuilder {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public List buildDeclarations(CSSName cssName, List values, int origin, boolean important,
				boolean inheritAllowed) {

			List result = new ArrayList(3);

			checkValueCount(cssName, 1, 3, values.size());
			boolean haveGradientDirection = false;
			boolean haveStartColor = false;
			boolean haveEndColor = false;

			for (Iterator i = values.iterator(); i.hasNext();) {
				CSSPrimitiveValue value = (CSSPrimitiveValue) i.next();
				checkInheritAllowed(value, false);
				boolean matched = false;

				if (isGradientDirection(value)) {
					if (haveGradientDirection) {
						throw new CSSParseException("A gradient direction cannot be set twice", -1);
					}
					haveGradientDirection = true;
					matched = true;

					result.add(new PropertyDeclaration(CssExtensions.BACKGROUND_GRADIENT_DIRECTION, value, important,
							origin));
				}

				CSSPrimitiveValue gradientColor = convertToGradientColor(value);
				if (gradientColor != null) {
					if (haveStartColor && haveEndColor) {
						throw new CSSParseException("A border color cannot be set twice", -1);
					}
					matched = true;
					if (haveStartColor) {
						haveEndColor = true;
						result.add(new PropertyDeclaration(CssExtensions.BACKGROUND_GRADIENT_START, gradientColor,
								important, origin));
					} else {
						haveStartColor = true;
						result.add(new PropertyDeclaration(CssExtensions.BACKGROUND_GRADIENT_END, gradientColor,
								important, origin));
					}
				}

				if (!matched) {
					throw new CSSParseException(value.getCssText() + " is not a gradient direction or color", -1);
				}
			}

			if (!haveGradientDirection) {
				result.add(new PropertyDeclaration(CssExtensions.BACKGROUND_GRADIENT_DIRECTION,
						new PropertyValue(IdentValue.FS_INITIAL_VALUE), important, origin));
			}

			if (!haveStartColor) {
				result.add(new PropertyDeclaration(CssExtensions.BACKGROUND_GRADIENT_START,
						new PropertyValue(IdentValue.FS_INITIAL_VALUE), important, origin));
			}

			if (!haveEndColor) {
				result.add(new PropertyDeclaration(CssExtensions.BACKGROUND_GRADIENT_END,
						new PropertyValue(IdentValue.FS_INITIAL_VALUE), important, origin));
			}

			return result;
		}

		private boolean isGradientDirection(CSSPrimitiveValue value) {
			if (value.getPrimitiveType() != CSSPrimitiveValue.CSS_IDENT) {
				return false;
			}

			IdentValue ident = IdentValue.valueOf(value.getCssText());
			if (ident == null) {
				return false;
			}

			return GRADIENT_DIRECTIONS.get(ident.FS_ID);
		}

		private CSSPrimitiveValue convertToGradientColor(CSSPrimitiveValue value) {
			int type = value.getPrimitiveType();
			if (type != CSSPrimitiveValue.CSS_IDENT && type != CSSPrimitiveValue.CSS_RGBCOLOR) {
				return null;
			}

			if (type == CSSPrimitiveValue.CSS_RGBCOLOR) {
				return value;
			} else {
				FSRGBColor color = Conversions.getColor(value.getStringValue());
				if (color != null) {
					return new PropertyValue(color);
				}

				IdentValue ident = IdentValue.valueOf(value.getCssText());
				if (ident == null || ident != IdentValue.TRANSPARENT) {
					return null;
				}

				return value;
			}
		}
	}
}
