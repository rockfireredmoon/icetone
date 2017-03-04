package icetone.css;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.Conversions;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

class GenericColor extends AbstractPropertyBuilder {
	private static final BitSet ALLOWED = CssExtensions.setFor(new IdentValue[] { IdentValue.TRANSPARENT });

	@Override
	public List buildDeclarations(CSSName cssName, List values, int origin, boolean important,
			boolean inheritAllowed) {
		checkValueCount(cssName, 1, values.size());
		CSSPrimitiveValue value = (CSSPrimitiveValue) values.get(0);
		checkInheritAllowed(value, inheritAllowed);
		if (value.getCssValueType() != CSSValue.CSS_INHERIT) {
			checkIdentOrColorType(cssName, value);

			if (value.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				FSRGBColor color = Conversions.getColor(value.getStringValue());
				if (color != null) {
					return Collections.singletonList(
							new PropertyDeclaration(cssName, new PropertyValue(color), important, origin));
				}

				IdentValue ident = checkIdent(cssName, value);
				checkValidity(cssName, ALLOWED, ident);
			}
		}

		return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
	}
}