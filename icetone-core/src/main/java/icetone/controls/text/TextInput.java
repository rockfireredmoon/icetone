package icetone.controls.text;

import icetone.core.BaseElement;

public interface TextInput {

	BaseElement getCaret();

	Object getFont();

	int getMaxLength();

	int getLength();

	int getCharacterWidth();

}
