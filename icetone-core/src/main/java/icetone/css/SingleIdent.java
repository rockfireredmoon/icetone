package icetone.css;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

abstract class SingleIdent extends AbstractPropertyBuilder {
	@Override
	public List buildDeclarations(CSSName cssName, List values, int origin, boolean important,
			boolean inheritAllowed) {
		checkValueCount(cssName, 1, values.size());
		CSSPrimitiveValue value = (CSSPrimitiveValue) values.get(0);
		checkInheritAllowed(value, inheritAllowed);
		if (value.getCssValueType() != CSSValue.CSS_INHERIT) {
			checkIdentType(cssName, value);
			IdentValue ident = checkIdent(cssName, value);

			checkValidity(cssName, getAllowed(), ident);
		}

		return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));

	}

	protected abstract BitSet getAllowed();
}