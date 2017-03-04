package icetone.core.layout.loader;

import icetone.controls.text.Password;
import icetone.core.ElementManager;

public class PasswordLayoutPart extends TextFieldLayoutPart {

	public PasswordLayoutPart() {
	}

	public PasswordLayoutPart(String data) {
	}

	@Override
	protected Password createElementObject(ElementManager screen) {
		return new Password(screen);
	}

}
