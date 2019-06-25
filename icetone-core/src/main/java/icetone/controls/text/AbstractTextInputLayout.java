package icetone.controls.text;

import com.jme3.math.Vector2f;

import icetone.core.BaseElement;
import icetone.text.FontInfo;

public class AbstractTextInputLayout<C extends BaseElement> extends AbstractTextLayout<C> {

	@Override
	protected Vector2f calcTextSize(C parent, float inWidth) {
		Vector2f pref = super.calcTextSize(parent, inWidth);
		TextInput textField = (TextInput) parent;
		int len = textField.getCharacterWidth();
		if (len == 0)
			len = textField.getMaxLength();
		FontInfo font = parent.getThemeInstance().getFontInfo(BaseElement.calcFont(parent));
		float x = len > 0 && font != null ? (font.getLineWidth("W") * len) : pref == null ? 0 : pref.x;
		float y = pref == null ? (font.getTotalLineHeight()) : pref.y;
		return new Vector2f(x, y);
	}

}
