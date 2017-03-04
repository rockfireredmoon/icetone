package icetone.css;

import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders;
import org.xhtmlrenderer.css.parser.property.PropertyBuilder;

public class HandlePosition extends OneToFourPropertyBuilder {
	@Override
	protected CSSName[] getProperties() {
		return new CSSName[] { CssExtensions.HANDLE_POSITION_TOP, CssExtensions.HANDLE_POSITION_RIGHT, CssExtensions.HANDLE_POSITION_BOTTOM,
				CssExtensions.HANDLE_POSITION_LEFT };
	}

	@Override
	protected PropertyBuilder getPropertyBuilder() {
		return PrimitivePropertyBuilders.MARGIN;
	}
}