package icetone.controls.text;

import com.jme3.math.Vector2f;

import icetone.core.Element;

public class TextFieldLayout extends AbstractTextLayout {

	public Vector2f preferredSize(Element parent) {
		Vector2f pref = super.preferredSize(parent);
		TextField textField = (TextField) parent;
		return new Vector2f(
				textField.getMaxLength() > 0 ? (textField.getFont().getLineWidth("W") * textField.getMaxLength()) : pref.x, pref.y);
	}

	public void layout(Element childElement) {
		TextField textField = (TextField) childElement;
		super.layout(childElement);
		Vector2f d = childElement.getDimensions();
		textField.getCaret().setDimensions(new Vector2f(d.x - (textField.getTextPaddingVec().x + textField.getTextPaddingVec().y),
				d.y - (textField.getTextPaddingVec().y + textField.getTextPaddingVec().z)));
		textField.getCaret().setPosition(new Vector2f(textField.getTextPaddingVec().x, textField.getTextPaddingVec().z));
	}

	public void remove(Element child) {
	}

	public void constrain(Element child, Object constraints) {
	}
}