/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon (aka rockfire)
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

import java.util.Objects;

import org.xhtmlrenderer.extend.UserAgentCallback;

import com.jme3.font.BitmapFont.Align;
import com.jme3.math.ColorRGBA;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.xhtml.XHTMLDisplay;
import icetone.xhtml.XHTMLUserAgent;

public class XHTMLLabel extends XHTMLDisplay {

	public XHTMLLabel(BaseScreen screen, UserAgentCallback uac) {
		super(screen, uac);
		init();
	}
	public XHTMLLabel() {
		this("");
	}

	public XHTMLLabel(String text) {
		super(BaseScreen.get());
		init();
		setText(text);
	}

	public XHTMLLabel(UserAgentCallback uac) {
		super(uac);
		init();
	}

	public XHTMLLabel(BaseScreen screen) {
		super(screen, new XHTMLUserAgent(screen));
		init();
	}

	public XHTMLLabel(String text, BaseScreen screen) {
		super(screen, new XHTMLUserAgent(screen));
		setText(text);
		init();
	}

	@Override
	public BaseElement setText(String text) {
		if (!Objects.equals(text, this.text)) {
			setDocumentFromString(wrapTextInXHTML(text, calcFontColor(), getAlign()), "label://" + getStyleId());
			if (getElementParent() != null) {
				dirtyLayout(false, LayoutType.boundsChange());
				getElementParent().layoutChildren();
			}
		}
		return this;
	}

	public static String wrapTextInXHTML(String text, ColorRGBA col) {
		return wrapTextInXHTML(text, col, Align.Center);
	}

	public static String wrapTextInXHTML(String text, ColorRGBA col, Align align, String... cssResources) {
		final StringBuilder bui = new StringBuilder();
		bui.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bui.append("<!DOCTYPE html>\n");
		bui.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		bui.append("<head>");
		for (String cssResource : cssResources) {
			bui.append("<link rel=\"stylesheet\" href=\"" + cssResource + "\"/>");
		}
		bui.append("</head>");
		bui.append(String.format(
				"<body style=\"background-color: inherit; color: #%02x%02x%02x; text-align: "
						+ align.name().toLowerCase() + ";\">\n",
				(int) (col.getRed() * 255), (int) (col.getGreen() * 255), (int) (col.getBlue() * 255)));
		bui.append(text);
		bui.append("</body>\n");
		bui.append("</html>\n");
		return bui.toString();
	}

	private void init() {
		setVerticalScrollBarMode(ScrollBarMode.Never);
		setHorizontalScrollBarMode(ScrollBarMode.Never);
	}

}
