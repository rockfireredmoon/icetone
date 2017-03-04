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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.texture.Image;

import icetone.core.ToolKit;
import icetone.css.StyleManager.ThemeInstance;

public class AtlasMaker {
	final static Logger LOG = Logger.getLogger(AtlasMaker.class.getName());

	static class ImageDef implements Comparable<ImageDef> {
		String uri;
		Image tex;

		ImageDef(String uri, Image tex) {
			this.uri = uri;
			this.tex = tex;
		}

		@Override
		public int compareTo(ImageDef o) {
			int a = tex.getWidth() * tex.getHeight();
			int a2 = o.tex.getWidth() * o.tex.getHeight();
			return Integer.valueOf(a).compareTo(a2);
		}

		@Override
		public String toString() {
			return "ImageDef [uri=" + uri + ", tex=" + tex + "]";
		}
	}

	private ThemeInstance theme;
	private List<ImageDef> defs = new ArrayList<>();

	public AtlasMaker(ThemeInstance theme) {
		this.theme = theme;
	}

	@SuppressWarnings("unchecked")
	public void make() {

		for (Stylesheet s : theme.getStylesheets()) {
			for (Object o : s.getContents()) {
				if (o instanceof Ruleset) {
					Ruleset r = (Ruleset) o;
					for (PropertyDeclaration p : (Collection<PropertyDeclaration>) r.getPropertyDeclarations()) {
						if (p.getValue().getPrimitiveType() == CSSPrimitiveValue.CSS_URI
								&& p.getCSSName().equals(CSSName.BACKGROUND_IMAGE)) {
							String path = p.getValue().getStringValue();
							if (!path.startsWith("/")) {
								String dir = s.getURI();
								int idx = dir.lastIndexOf('/');
								if (idx != -1)
									dir = dir.substring(0, idx);
								path = dir + "/" + path;
							}
							try {
								ImageDef e = new ImageDef(path,
										ToolKit.get().getApplication().getAssetManager().loadTexture(path).getImage());
								defs.add(e);
							} catch (AssetNotFoundException anfe) {
								LOG.warning(String.format("CSS declaration contains missing image. %s. %s", path,
										p.toString()));
							}
						}
					}
				}
			}
		}
		Collections.sort(defs);
		Collections.reverse(defs);
		System.out.println("biggest " + defs.get(0));
	}
}
