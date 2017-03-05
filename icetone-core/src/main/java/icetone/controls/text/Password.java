/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.controls.text;

import com.jme3.font.BitmapText;

import icetone.core.BaseScreen;

/**
 * A specialisation of a {@link TextField} suitable for entry of passwords. Each
 * character is 'masked' as it types with a configured character.
 * 
 * @author t0neg0d
 */
public class Password extends TextField {
	private char mask = '*';
	private String maskedText = "";

	public Password() {
		this(BaseScreen.get());
	}

	public Password(BaseScreen screen) {
		super(screen);

	}

	/**
	 * Sets the mask character to use when hiding text input
	 * 
	 * @param mask
	 *            char
	 */
	public void setMask(char mask) {
		this.mask = mask;
	}

	/**
	 * Returns the current mask character used when hiding text input
	 * 
	 * @return
	 */
	public String getMask() {
		return String.valueOf(this.mask);
	}

	@Override
	protected String getVisibleText() {
		getTextFieldText();

		maskedText = "";
		for (int i = 0; i < finalText.length(); i++) {
			maskedText += String.valueOf(mask);
		}

		widthTest = new BitmapText(calcFont(), false);
		widthTest.setBox(null);
		widthTest.setSize(getFontSize());

		int index1 = 0, index2;
		widthTest.setText(maskedText.substring(index1));
		while (widthTest.getLineWidth() > getWidth()) {
			if (index1 == caretIndex)
				break;
			index1++;
			widthTest.setText(maskedText.substring(index1));
		}

		index2 = maskedText.length() - 1;
		if (index2 == caretIndex && caretIndex != textModel.size()) {
			index2 = caretIndex + 1;
			widthTest.setText(maskedText.substring(index1, index2));
			while (widthTest.getLineWidth() < getWidth()) {
				if (index2 == textModel.size())
					break;
				index2++;
				widthTest.setText(maskedText.substring(index1, index2));
			}
		}
		if (index2 != textModel.size())
			index2++;

		if (head != index1 || tail != index2) {
			head = index1;
			tail = index2;
		}
		if (head != tail && head != -1 && tail != -1) {
			visibleText = maskedText.substring(head, tail);
		} else {
			visibleText = "";
		}

		widthTest.setText(maskedText.substring(head, caretIndex));
		caretX = widthTest.getLineWidth();
		setCaretPosition(caretX);

		return visibleText;
	}

}
