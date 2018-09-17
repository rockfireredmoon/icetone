/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.core.utils;

import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;

import icetone.core.BaseElement;
import icetone.framework.core.AnimText;

/**
 *
 * @author t0neg0d
 */
public class BitmapTextUtil {

	/**
	 * Returns the width of the provided text
	 * 
	 * @param ref
	 *            Element The element the text will be added to (reference for
	 *            font settings)
	 * @param text
	 *            String the text to be evaluated
	 * @return float The width
	 */
	public static float getTextWidth(BaseElement ref, String text) {
		BitmapText eval = new BitmapText(BaseElement.calcFont(ref));
		eval.setSize(BaseElement.calcFontSize(ref));
		eval.setLineWrapMode(LineWrapMode.NoWrap);
		eval.setBox(null);
		eval.setText(text);

		return eval.getLineWidth();
	}

	/**
	 * Returns the width of the provided text or the maxwidth, which ever is
	 * less
	 * 
	 * @param ref
	 *            Element The element the text will be added to (reference for
	 *            font settings)
	 * @param text
	 *            String the text to be evaluated
	 * @param maxWidth
	 *            The maximum width considered a valid return value
	 * @return float The width
	 */
	public static float getTextWidth(BaseElement ref, String text, float maxWidth) {
		BitmapText eval = new BitmapText(BaseElement.calcFont(ref));
		eval.setSize(BaseElement.calcFontSize(ref));
		eval.setText("Xg");
		eval.setText(text);

		return (eval.getLineWidth() < maxWidth) ? eval.getLineWidth() : maxWidth;
	}

	/**
	 * Returns the height value of a single line of text
	 * 
	 * @param ref
	 *            Element The element the text will be added to (reference for
	 *            font settings)
	 * @param text
	 *            String the text to be evaluated
	 * @return float
	 */
	public static float getTextLineHeight(BaseElement ref, String text) {
		BitmapText eval = new BitmapText(BaseElement.calcFont(ref));
		eval.setSize(BaseElement.calcFontSize(ref));
		eval.setLineWrapMode(LineWrapMode.NoWrap);
		eval.setBox(null);
		eval.setText(text);

		return eval.getLineHeight();
	}

	/**
	 * Returns the total size of a wrapped text string
	 * 
	 * @param ref
	 *            Element The element the text will be added to (reference for
	 *            font settings)
	 * @param text
	 *            String the text to be evaluated
	 * @param maxWidth
	 *            The maximum width considered a valid return value
	 * @return float
	 */
	public static Vector2f getTextTotalSize(BaseElement ref, String text, float maxWidth) {

		AnimText at = createText(ref, text, maxWidth);
		return new Vector2f(at.getLineWidth(), at.getTotalHeight());
	}

	/**
	 * Returns the total height of a wrapped text string
	 * 
	 * @param ref
	 *            Element The element the text will be added to (reference for
	 *            font settings)
	 * @param text
	 *            String the text to be evaluated
	 * @param maxWidth
	 *            The maximum width considered a valid return value
	 * @return float
	 */
	public static float getTextTotalHeight(BaseElement ref, String text, float maxWidth) {
		AnimText at = createText(ref, text, maxWidth);
		return at.getTotalHeight();
	}

	protected static AnimText createText(BaseElement ref, String text, float maxWidth) {
		AnimText at = new AnimText(ref.getScreen(), BaseElement.calcFont(ref));
		at.setFontSize(BaseElement.calcFontSize(ref));
		at.setTextWrap(ref.getTextWrap());
		at.setLineHeight(ref.getFixedLineHeight() > 0 ? ref.getFixedLineHeight() : -1);
		at.setBounds(maxWidth, Short.MAX_VALUE);
		at.setText(text.length() < 1 ? "Xg" : text);
		switch (ref.getTextWrap()) {
		case Character:
			at.wrapTextToCharacter(maxWidth);
			break;
		case Word:
			at.wrapTextToWord(maxWidth);
			break;
		default:
			at.wrapTextNoWrap();
			break;
		}
		return at;
	}

	public static int getUnwrappedLineCount(String text) {
		int i = text.split("\n").length;
		if (text.endsWith("\n"))
			i++;
		return i;
	}
}
