/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.core.utils;

import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;

import icetone.core.Element;
import icetone.framework.core.AnimText;

/**
 *
 * @author t0neg0d
 */
public class BitmapTextUtil {
	
	/**
	 * Returns the width of the provided text
	 * @param ref Element The element the text will be added to (reference for font settings)
	 * @param text String the text to be evaluated
	 * @return float The width
	 */
	public static float getTextWidth(Element ref, String text) {
		BitmapText eval = new BitmapText(ref.getFont());
		eval.setSize(ref.getFontSize());
		eval.setLineWrapMode(LineWrapMode.NoWrap);
		eval.setBox(null);
		eval.setText(text);
		
		return eval.getLineWidth();
	}
	
	/**
	 * Returns the width of the provided text or the maxwidth, which ever is less
	 * @param ref Element The element the text will be added to (reference for font settings)
	 * @param text String the text to be evaluated
	 * @param maxWidth The maximum width considered a valid return value
	 * @return float The width
	 */
	public static float getTextWidth(Element ref, String text, float maxWidth) {
		BitmapText eval = new BitmapText(ref.getFont());
		eval.setSize(ref.getFontSize());
		eval.setText("Xg");
		eval.setText(text);
		
		return (eval.getLineWidth() < maxWidth) ? eval.getLineWidth() : maxWidth;
	}
	
	/**
	 * Returns the height value of a single line of text
	 * @param ref Element The element the text will be added to (reference for font settings)
	 * @param text String the text to be evaluated
	 * @return float
	 */
	public static float getTextLineHeight(Element ref, String text) {
		BitmapText eval = new BitmapText(ref.getFont());
		eval.setSize(ref.getFontSize());
		eval.setLineWrapMode(LineWrapMode.NoWrap);
		eval.setBox(null);
		eval.setText(text);
		
		return eval.getLineHeight();
	}
	
	/**
	 * Returns the total height of a wrapped text string
	 * @param ref Element The element the text will be added to (reference for font settings)
	 * @param text String the text to be evaluated
	 * @param maxWidth The maximum width considered a valid return value
	 * @return float
	 */
	public static float getTextTotalHeight(Element ref, String text, float maxWidth) {
		AnimText at = new AnimText(ref.getScreen().getApplication().getAssetManager(), ref.getFont());
		at.setFontSize(ref.getFontSize());
		at.setTextWrap(ref.getTextWrap());
		at.setBounds(maxWidth, Short.MAX_VALUE);
		at.setText(text);
		at.wrapTextToWord(maxWidth);

		return at.getTotalHeight();
		
//		BitmapText eval = new BitmapText(ref.getFont());
//		eval.setSize(ref.getFontSize());
//		eval.setText("Xg");
//		Rectangle rect = new Rectangle(0,0,maxWidth, eval.getLineHeight());
//		eval.setBox(rect);
//		eval.setText(text);
//		return eval.getLineHeight()*(at.getLineCount() + 1);

		
//		BitmapText eval = new BitmapText(ref.getFont());
//		eval.setSize(ref.getFontSize());
//		eval.setText("Xg");
//		Rectangle rect = new Rectangle(0,0,maxWidth, eval.getLineHeight());
//		eval.setBox(rect);
//		eval.setText(text);
//		return eval.getLineHeight()*eval.getLineCount();
	}
}
