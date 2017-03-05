package icetone.core.layout.loader;

import icetone.controls.text.XHTMLLabel;
import icetone.core.BaseScreen;

public class XHTMLLabelLayoutPart extends AbstractElementLayoutPart<XHTMLLabel> {

	@Override
	protected XHTMLLabel createElementObject(BaseScreen screen) {
		return new XHTMLLabel(screen);
	}

}
