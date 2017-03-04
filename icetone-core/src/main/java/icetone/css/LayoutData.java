package icetone.css;

import java.util.Collections;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

/* Non-standard for supplying string based layout data. */
public class LayoutData extends AbstractPropertyBuilder {

	@Override
	public List buildDeclarations(CSSName cssName, List values, int origin, boolean important,
			boolean inheritAllowed) {
		CSSPrimitiveValue value = (CSSPrimitiveValue) values.get(0);
		checkInheritAllowed(value, inheritAllowed);
		if (value.getCssValueType() != CSSValue.CSS_INHERIT) {
			return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));
		}
		return Collections.emptyList();
	}

}