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

import icetone.core.BaseElement;

/**
 * Interface implemented by all components that accept text input.
 */
public interface TextInput {

	/**
	 * Get the element used as the caret.
	 * 
	 * @return caret element
	 */
	BaseElement getCaret();

	/**
	 * Get the maximum number of characters in total that may be contained in the
	 * text component.
	 * 
	 * @return maximum length
	 */
	int getMaxLength();

	/**
	 * Current the current length of the text input.
	 * 
	 * @return text input
	 */
	int getLength();

	/**
	 * Get the number of characters to use as the visual width of the text
	 * component. For example, in a {@link TextArea}, this is the number of columns.
	 * For {@link TextField} it is the length of the prototype type string used to
	 * determine the preferred width.
	 * 
	 * @return width in characters of visual component
	 */
	int getCharacterWidth();

	/**
	 * Set the number of characters to use as the visual width of the text
	 * component. For example, in a {@link TextArea}, this is the number of columns.
	 * For {@link TextField} it is the length of the prototype type string used to
	 * determine the preferred width.
	 * 
	 * @param size width in characters of visual component
	 * @return instance for chaining
	 */
	BaseElement setCharacterLength(int size);

	/**
	 * Set the maximum number of characters in total that may be contained in the
	 * text component.
	 * 
	 * @param maxLength maximum length
	 * @return instance for chaining
	 */
	BaseElement setMaxLength(int maxLength);

}
