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
package icetone.xhtml;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.extend.OutputDevice;

import com.jme3.math.Vector2f;

import icetone.core.BaseElement;

public class XHTMLGlyphVector implements FSGlyphVector {
	private String string;
	private XHTMLFSFont font;

	public XHTMLGlyphVector(OutputDevice outputDevice, XHTMLFSFont font, String string) {
		this.string = string;
		this.font = font;
	}

	public String getString() {
		return string;
	}

	public Rectangle getGlyphPixelBounds(int index, XHTMLCanvas gc, float x, float y) {

		Rectangle2D rect = getGlyphVisualBounds(index, gc);
		int l = (int) Math.floor(rect.getX() + x);
		int t = (int) Math.floor(rect.getY() + y);
		int r = (int) Math.ceil(rect.getMaxX() + x);
		int b = (int) Math.ceil(rect.getMaxY() + y);
		return new Rectangle(l, t, r - l, b - t);
	}

	private Rectangle2D getGlyphVisualBounds(int index, XHTMLCanvas gc) {
		// TODO make this more efficient and create a single text element (than
		// can also be drawn)

		XHTMLFSFont wasfont = gc.getDrawFont();
		gc.setDrawFont(font);

		// Get the total size of the text block
		BaseElement text = gc.createText(string);
		Vector2f pref = text.calcPreferredSize();

		// Get the size of the text block from the index
		String tstr = string.substring(index);
		BaseElement ttext = gc.createText(tstr);
		Vector2f tpref = ttext.calcPreferredSize();

		// Get the size of the first character
		String fstr = string.substring(index, index + 1);
		BaseElement ftext = gc.createText(fstr);
		Vector2f fpref = ftext.calcPreferredSize();

		// The position of that character is the difference between the two
		float offsetx = pref.x - tpref.x;

		Rectangle r = new Rectangle((int) offsetx, 0, (int) fpref.x, (int) fpref.y);
		gc.setDrawFont(wasfont);
		return r;
	}

	public XHTMLFSFont getFont() {
		return font;
	}
}
