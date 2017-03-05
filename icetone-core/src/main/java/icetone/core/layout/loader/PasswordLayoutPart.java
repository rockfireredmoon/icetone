package icetone.core.layout.loader;

import icetone.controls.text.Password;
import icetone.core.BaseScreen;

public class PasswordLayoutPart extends TextFieldLayoutPart {

	public PasswordLayoutPart() {
	}

	public PasswordLayoutPart(String data) {
	}

	@Override
	protected Password createElementObject(BaseScreen screen) {
		return new Password(screen);
	}

}
