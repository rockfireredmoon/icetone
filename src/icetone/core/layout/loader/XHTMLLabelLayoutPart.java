package icetone.core.layout.loader;

import icetone.controls.text.XHTMLLabel;
import icetone.core.ElementManager;

public class XHTMLLabelLayoutPart extends AbstractElementLayoutPart<XHTMLLabel> {

	@Override
	protected XHTMLLabel createElementObject(ElementManager screen) {
		return new XHTMLLabel(screen);
	}

}
