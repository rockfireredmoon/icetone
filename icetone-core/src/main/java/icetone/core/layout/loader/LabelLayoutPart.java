package icetone.core.layout.loader;

import icetone.controls.text.Label;
import icetone.core.BaseScreen;

public class LabelLayoutPart extends AbstractElementLayoutPart<Label> {
	public LabelLayoutPart() {
	}

	public LabelLayoutPart(String data) {
	}

	@Override
	protected Label createElementObject(BaseScreen screen) {
		return new Label(screen);
	}

}
