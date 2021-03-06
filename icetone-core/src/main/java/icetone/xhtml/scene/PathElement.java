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
package icetone.xhtml.scene;

import java.util.Collection;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Size;

/**
 * A rectangle wrapped in an {@link BaseElement}. Allows the rectangle to be added
 * as an ordinary Tonegod element.
 */
public class PathElement extends AbstractMeshElement {

	public PathElement(BaseScreen screen, Vector2f location, Collection<Vector2f> points, ColorRGBA color) {
		this(screen, location, points, color, 1);
	}

	public PathElement(BaseScreen screen, Vector2f location, Collection<Vector2f> points, ColorRGBA color,
			float lineWidth) {
		this(screen, location, points, color, lineWidth, false);
	}

	public PathElement(BaseScreen screen, Vector2f location, Collection<Vector2f> points, ColorRGBA color,
			float lineWidth, boolean close) {
		super(screen, location, getBounds(points));
		Path l1 = new Path(points, close);
		l1.setLineWidth(lineWidth);
		Geometry geom = new Geometry(getStyleId() + "-geom", l1);
		meshMaterial.setColor("Color", color);
		geom.setMaterial(meshMaterial);
		attachChild(geom);
	}

	public static Size getBounds(Collection<Vector2f> a) {
		Size b = new Size();
		for (Vector2f i : a) {
			b.x = Math.max(i.x, b.x);
			b.y = Math.max(i.y, b.y);
		}
		return b;
	}

}
