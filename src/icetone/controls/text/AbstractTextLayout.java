package icetone.controls.text;

import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.BitmapTextUtil;

public abstract class AbstractTextLayout extends AbstractLayout {

	public Vector2f minimumSize(Element parent) {
		return textSize(parent);
	}

	public Vector2f maximumSize(Element parent) {
		return null;
	}

	public Vector2f preferredSize(Element parent) {
//		Vector2f pref = textSize(parent);
//		float preferredWidth = parent.getFont().getLineWidth(parent.getText());
//		return new Vector2f(preferredWidth + parent.getTextPaddingVec().x + parent.getTextPaddingVec().y, pref.y);
		return LUtil.getPreferredTextSize(parent);
	}

	public void remove(Element child) {
	}

	public void constrain(Element child, Object constraints) {
	}

	protected Vector2f textSize(Element parent) {
//		return new Vector2f(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y,
//				BitmapTextUtil.getTextLineHeight(parent, "Xg") + parent.getTextPaddingVec().z + parent.getTextPaddingVec().w);
		return new Vector2f(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y,
				parent.getFont().getCharSet().getLineHeight() + parent.getTextPaddingVec().z + parent.getTextPaddingVec().w);
	}

	@Override
	public void layout(Element container) {
		container.setTextPosition(container.getTextPosition().x, 0);
//		TextElement textElement = container.getTextElement();
//		if(textElement != null)
//			LUtil.setBounds(textElement, container.getBounds());
	}
}