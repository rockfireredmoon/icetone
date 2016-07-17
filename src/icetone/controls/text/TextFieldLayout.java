package icetone.controls.text;

import com.jme3.math.Vector2f;

import icetone.core.Element;

public class TextFieldLayout extends AbstractTextLayout {

	public Vector2f preferredSize(Element parent) {
		Vector2f pref = super.preferredSize(parent);
		TextInput textField = (TextInput) parent;
		return new Vector2f(
				textField.getMaxLength() > 0 ? (parent.getFont().getLineWidth("W") * textField.getMaxLength()) : pref.x, pref.y);
	}

	public void layout(Element childElement) {
		TextInput textField = (TextInput) childElement;
		super.layout(childElement);
		Vector2f d = childElement.getDimensions();
		textField.getCaret().setDimensions(new Vector2f(d.x - (childElement.getTextPaddingVec().x + childElement.getTextPaddingVec().y),
				d.y - (childElement.getTextPaddingVec().y + childElement.getTextPaddingVec().z)));
		textField.getCaret().setPosition(new Vector2f(childElement.getTextPaddingVec().x, childElement.getTextPaddingVec().z));
	}

	public void remove(Element child) {
	}

	public void constrain(Element child, Object constraints) {
	}
}