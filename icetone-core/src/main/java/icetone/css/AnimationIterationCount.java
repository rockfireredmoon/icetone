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
package icetone.css;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.CSSParseException;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.parser.property.AbstractPropertyBuilder;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;

public final class AnimationIterationCount extends AbstractPropertyBuilder {
	BitSet allowed = CssExtensions.setFor(new IdentValue[] { CssExtensions.INFINITE });

	@Override
	public List buildDeclarations(CSSName cssName, List values, int origin, boolean important, boolean inheritAllowed) {
		checkValueCount(cssName, 1, values.size());
		PropertyValue value = (PropertyValue) values.get(0);
		checkInheritAllowed(value, inheritAllowed);
		if (value.getCssValueType() != CSSValue.CSS_INHERIT) {
			checkIdentNumberType(cssName, value);
			if (value.getPrimitiveType() == CSSPrimitiveValue.CSS_IDENT) {
				IdentValue ident = checkIdent(cssName, value);
				checkValidity(cssName, allowed, ident);
			} else if (value.getFloatValue() < 0.0f) {
				throw new CSSParseException(cssName + " may not be negative", -1);
			}
		}

		return Collections.singletonList(new PropertyDeclaration(cssName, value, important, origin));

	}

	protected void checkIdentNumberType(CSSName cssName, CSSPrimitiveValue value) {
		int type = value.getPrimitiveType();
		if (type != CSSPrimitiveValue.CSS_IDENT && type != CSSPrimitiveValue.CSS_NUMBER) {
			throw new CSSParseException("Value for " + cssName + " must be an identifier or number", -1);
		}
	}
}