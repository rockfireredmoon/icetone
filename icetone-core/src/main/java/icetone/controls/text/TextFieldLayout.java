package icetone.controls.text;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.BaseElement;
import icetone.text.TextElement;

public class TextFieldLayout extends AbstractTextInputLayout<TextField> {

	protected Vector4f calcTextOffset(TextField element, TextElement textElement, Vector4f textPadding) {
		return super.calcTextOffset(element, textElement, textPadding).add(new Vector4f(element.getIndent(), 0, 0, 0));
	}

	@Override
	protected void onLayout(TextField childElement) {
		super.onLayout(childElement);
		Vector4f m = childElement.getMargin();
		Vector4f tp = childElement.getAllPadding();
		Vector4f tcp = childElement.getTextClipPadding();
		Vector2f d = childElement.getDimensions();

		Vector2f cp = childElement.getCaret().calcPreferredSize();
		TextElement tel = childElement.getTextElement();
		float caretX = 0;
		Vector4f[] lp = null;
		float caretW = cp.x;
		if (tel != null) {
			lp = tel.getLetterPositions();
			if (lp.length > 0) {
				int caretIndex = childElement.caretIndex - childElement.head;
				if (caretIndex < 0)
					caretIndex = 0;
				if (caretIndex > lp.length)
					caretIndex = lp.length;
				if (caretIndex >= lp.length) {
					caretX = lp[lp.length - 1].x + lp[lp.length - 1].z + tel.getFont().getCharacterSpacing();
				} else
					caretX = lp[caretIndex].x;

				if (childElement.getMode() == Mode.OVERWRITE)
					caretW = lp[caretIndex >= lp.length ? lp.length - 1 : caretIndex].z;
			}
		} else {
			if (childElement.getMode() == Mode.OVERWRITE) {
				caretW = childElement.getThemeInstance().getFontInfo(BaseElement.calcFont(childElement))
						.getLineWidth("S");
			}
		}
		float x = childElement.getCaret().getIndent() + childElement.getIndent() + tp.x + caretX - (cp.x / 2f) - 1;
		if (x > d.x - tp.y) {
			x = d.x - tp.y;
		}
		childElement.getCaret().setBounds(x, m.z, caretW, d.y - m.z - m.w);
		childElement.getOverlay().setBounds(0, 0, childElement.getWidth(), childElement.getHeight());

		BaseElement range = childElement.getRange();
		if (tel == null || lp.length == 0 || childElement.rangeHead == -1 || childElement.rangeTail == -1
				|| childElement.rangeHead == childElement.rangeTail)
			range.setBounds(Vector4f.ZERO);
		else {
			int startIndex = childElement.rangeHead;
			int endIndex = childElement.rangeTail;

			if (startIndex >= childElement.tail)
				startIndex = childElement.tail;

			startIndex -= childElement.head;
			endIndex -= childElement.head;

			if (endIndex < startIndex) {
				int swap = endIndex;
				endIndex = startIndex;
				startIndex = swap;
			}
			if (startIndex < 0)
				startIndex = 0;
			else if (startIndex >= lp.length)
				startIndex = lp.length - 1;
			if (endIndex < 0)
				endIndex = 0;
			else if (endIndex > lp.length)
				endIndex = lp.length;

			caretX = lp[startIndex].x;
			float caretX2;

			if (endIndex >= lp.length) {
				caretX2 = lp[lp.length - 1].x + lp[lp.length - 1].z + tel.getFont().getCharacterSpacing();
			} else
				caretX2 = lp[endIndex].x;

			range.setBounds(Math.max(tcp.w, caretX + tp.x + childElement.getIndent() - range.getIndent()), m.z,
					Math.min(d.x - tcp.w - tcp.y, caretX2 - caretX + (range.getIndent() * 2)), d.y - m.z - m.w);
		}

	}
}