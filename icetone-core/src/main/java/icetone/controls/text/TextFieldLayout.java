package icetone.controls.text;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

public class TextFieldLayout extends AbstractTextInputLayout<TextField> {


	@Override
	protected void onLayout(TextField childElement) {
		TextInput textField = childElement;
		super.onLayout(childElement);
		Vector2f d = childElement.getDimensions();
		Vector4f padding = childElement.getAllPadding();
		childElement.getOverlay().setPosition(0,0);
		childElement.getOverlay().setDimensions(childElement.getDimensions());
		textField.getCaret().setBounds(padding.x, padding.z, d.x - (padding.x + padding.y),
				d.y - (padding.y + padding.z));
	}
}