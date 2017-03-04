package icetone.core.layout.loader;

import icetone.controls.text.Label;
import icetone.core.ElementManager;

public class LabelLayoutPart extends AbstractElementLayoutPart<Label> {
	public LabelLayoutPart() {
	}

	public LabelLayoutPart(String data) {
	}

	@Override
	protected Label createElementObject(ElementManager screen) {
		return new Label(screen);
	}

}
