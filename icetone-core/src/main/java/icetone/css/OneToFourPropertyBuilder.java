package icetone.css;

import java.util.ArrayList;
import java.util.List;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

abstract class OneToFourPropertyBuilder extends AbstractPropertyBuilder {
	protected abstract CSSName[] getProperties();

	protected abstract PropertyBuilder getPropertyBuilder();

	@Override
	public List buildDeclarations(CSSName cssName, List values, int origin, boolean important,
			boolean inheritAllowed) {
		List result = new ArrayList(4);
		checkValueCount(cssName, 1, 4, values.size());

		PropertyBuilder builder = getPropertyBuilder();

		CSSName[] props = getProperties();

		PropertyDeclaration decl1;
		PropertyDeclaration decl2;
		PropertyDeclaration decl3;
		PropertyDeclaration decl4;
		switch (values.size()) {
		case 1:
			decl1 = (PropertyDeclaration) builder.buildDeclarations(cssName, values, origin, important).get(0);

			result.add(copyOf(decl1, props[0]));
			result.add(copyOf(decl1, props[1]));
			result.add(copyOf(decl1, props[2]));
			result.add(copyOf(decl1, props[3]));
			break;

		case 2:
			decl1 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(0, 1), origin, important, false).get(0);
			decl2 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(1, 2), origin, important, false).get(0);

			result.add(copyOf(decl1, props[0]));
			result.add(copyOf(decl2, props[1]));
			result.add(copyOf(decl1, props[2]));
			result.add(copyOf(decl2, props[3]));
			break;

		case 3:
			decl1 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(0, 1), origin, important, false).get(0);
			decl2 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(1, 2), origin, important, false).get(0);
			decl3 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(2, 3), origin, important, false).get(0);

			result.add(copyOf(decl1, props[0]));
			result.add(copyOf(decl2, props[1]));
			result.add(copyOf(decl3, props[2]));
			result.add(copyOf(decl2, props[3]));
			break;

		case 4:
			decl1 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(0, 1), origin, important, false).get(0);
			decl2 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(1, 2), origin, important, false).get(0);
			decl3 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(2, 3), origin, important, false).get(0);
			decl4 = (PropertyDeclaration) builder
					.buildDeclarations(cssName, values.subList(3, 4), origin, important, false).get(0);

			result.add(copyOf(decl1, props[0]));
			result.add(copyOf(decl2, props[1]));
			result.add(copyOf(decl3, props[2]));
			result.add(copyOf(decl4, props[3]));
			break;
		}

		return result;
	}
}