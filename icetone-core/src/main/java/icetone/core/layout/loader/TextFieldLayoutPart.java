package icetone.core.layout.loader;

import icetone.controls.text.TextField;
import icetone.core.BaseScreen;

public class TextFieldLayoutPart extends AbstractElementLayoutPart<TextField> {

	private boolean allowCopy = true;
	private boolean allowPaste = true;
	private int characterLength;
	private int maxLength;
	private boolean editable = true;

	public TextFieldLayoutPart() {
	}

	public TextFieldLayoutPart(String data) {
	}

	@Override
	protected void configureThisElement(TextField el, LayoutContext ctx) {
		super.configureThisElement(el, ctx);
		el.setAllowCopy(allowCopy);
		el.setAllowPaste(allowPaste);
		if (characterLength > 0)
			el.setCharacterLength(characterLength);
		if (maxLength > 0)
			el.setMaxLength(maxLength);
	}

	@Override
	protected TextField createElementObject(BaseScreen screen) {
		return new TextField(screen);
	}

	public boolean isAllowCopy() {
		return allowCopy;
	}

	public void setAllowCopy(boolean allowCopy) {
		this.allowCopy = allowCopy;
	}

	public boolean isAllowPaste() {
		return allowPaste;
	}

	public void setAllowPaste(boolean allowPaste) {
		this.allowPaste = allowPaste;
	}

	public int getCharacterLength() {
		return characterLength;
	}

	public void setCharacterLength(int characterLength) {
		this.characterLength = characterLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
