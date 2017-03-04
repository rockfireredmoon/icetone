package icetone.css;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public final class Opacity extends AbstractPropertyBuilder {
	BitSet allowed = CssExtensions.setFor(new IdentValue[] { IdentValue.AUTO });

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
			throw new CSSParseException("Value for " + cssName + " must be an identifier, length, or percentage",
					-1);
		}
	}
}