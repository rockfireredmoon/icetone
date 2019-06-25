package icetone.core.layout.loader;

import icetone.controls.text.PasswordField;
import icetone.core.BaseScreen;

public class PasswordLayoutPart extends TextFieldLayoutPart {

	public PasswordLayoutPart() {
	}

	public PasswordLayoutPart(String data) {
	}

	@Override
	protected PasswordField createElementObject(BaseScreen screen) {
		return new PasswordField(screen);
	}

}
