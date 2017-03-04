package icetone.css;

import java.util.Collections;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

class FloatSeconds extends AbstractPropertyBuilder {
	@Override
	public List buildDeclarations(CSSName cssName, List values, int origin, boolean important,
			boolean inheritAllowed) {
		checkValueCount(cssName, 1, values.size());
		PropertyValue value = (PropertyValue) values.get(0);
		checkInheritAllowed(value, inheritAllowed);
		if (value.getCssValueType() != CSSValue.CSS_INHERIT) {
			checkDuration(cssName, value);
			// String strv = value.getStringValue();
			// if (strv != null strv.endsWith("s")) {
			// strv = strv.substring(0, strv.length() - 1);
			// }
			// try {
			// float val = Float.parseFloat(strv);
			// if (!isNegativeValuesAllowed() && value.getFloatValue() <
			// 0.0f) {
			// throw new CSSParseException(cssName + " may not be negative",
			// -1);
			// }
			// PropertyValue v = new PropertyValue(CSSPrimitiveValue.CSS_S,
			// val, String.format("%fs", val));
			// return Collections.singletonList(new
			// PropertyDeclaration(cssName, v, important, origin));
			// } catch (NumberFormatException nfe) {
			// throw new CSSParseException(cssName + " is not a duration in
			// seconds (float suffixed by s)", -1);
			// }
		}
		return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));

	}

	protected void checkDuration(CSSName cssName, CSSPrimitiveValue value) {
		int type = value.getPrimitiveType();
		if (type != CSSPrimitiveValue.CSS_NUMBER) {
			throw new CSSParseException("Value for " + cssName + " must be an float", -1);
		}
	}

	protected boolean isNegativeValuesAllowed() {
		return false;
	}
}