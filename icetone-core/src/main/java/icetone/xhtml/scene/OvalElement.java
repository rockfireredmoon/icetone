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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Size;

/**
 * A circle wrapped in an {@link BaseElement}. Allows the circle to be added as an
 * ordinary Tonegod element.
 *
 * <a href=
 * "http://hub.jmonkeyengine.org/forum/topic/drawing-a-simple-circle-in-jme3/page/2/">See
 * JME forums.</a>
 */
public class OvalElement extends AbstractMeshElement {

	public OvalElement(ElementManager<?> screen, Vector2f p1, Vector2f radius, ColorRGBA color) {
		this(screen, p1, radius, color, 1);
	}

	public OvalElement(ElementManager<?> screen, Vector2f p1, Vector2f radius, ColorRGBA color, float lineWidth) {
		super(screen, p1, new Size(radius.x * 2f, radius.y * 2f));
		Oval2d l1 = new Oval2d(new Vector3f(radius.x, radius.y, 0f), radius, 16);
		l1.setLineWidth(lineWidth);
		Geometry geom = new Geometry(getStyleId() + "-geom", l1);
		meshMaterial.setColor("Color", color);
		geom.setMaterial(meshMaterial);
		attachChild(geom);
	}
}
