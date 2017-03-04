package icetone.core.layout.loader;

import icetone.controls.buttons.PushButton;
import icetone.core.ElementManager;

public class DefaultButtonLayoutPart extends AbstractButtonLayout<PushButton> {

	public DefaultButtonLayoutPart() {
	}

	public DefaultButtonLayoutPart(String data) {
	}

	@Override
	protected PushButton createElementObject(ElementManager screen) {
		return new PushButton(screen);
	}

}
