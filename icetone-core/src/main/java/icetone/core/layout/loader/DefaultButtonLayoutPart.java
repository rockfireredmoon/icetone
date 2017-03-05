package icetone.core.layout.loader;

import icetone.controls.buttons.PushButton;
import icetone.core.BaseScreen;

public class DefaultButtonLayoutPart extends AbstractButtonLayout<PushButton> {

	public DefaultButtonLayoutPart() {
	}

	public DefaultButtonLayoutPart(String data) {
	}

	@Override
	protected PushButton createElementObject(BaseScreen screen) {
		return new PushButton(screen);
	}

}
