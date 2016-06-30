/*
 * Copyright (c) 2013-2014 Emerald Icemoon All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package icetone.core.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;

import icetone.core.Element;
import icetone.core.ElementManager;

/**
 * Simple layout manager that lays components out to their original sizes.
 */
public class XYLayoutManager extends AbstractLayout {

	private List<Element> done = new ArrayList<Element>();
	private Map<Element, Vector2f> positions = new HashMap<Element, Vector2f>();

	public void layout(Element parent) {
		for (Spatial s : LUtil.getAllChildren(parent)) {
			if (s instanceof Element) {
				Element el = (Element) s;
				Vector2f pos = positions.get(el);
				if (pos == null) {
					System.err.println("WARNING: no position known for " + el + " in " + parent);
				} else {
					Vector2f orgDimensions = LUtil.getContainerPreferredDimensions(el);
					LUtil.setBounds(el, pos.x, pos.y, orgDimensions.x, orgDimensions.y);
				}
			}
		}
	}

	@Override
	public void layoutScreen(ElementManager screen) {
	}

	public void remove(Element child) {
		positions.remove(child);
	}

	public void constrain(Element child, Object constraints) {
		// No constraints supported
		if (constraints == null || constraints.equals("")) {
			constraints = child.getPosition();
		}
		positions.put(child, ((Vector2f) constraints).clone());
	}

	public Vector2f minimumSize(Element parent) {
		return preferredSize(parent);
	}

	public Vector2f preferredSize(Element target) {
		Vector2f prefSize = new Vector2f();
		for (Element e : target.getElements()) {
			prefSize.x = Math.max(prefSize.x, e.getX() + e.getWidth());
			prefSize.y = Math.max(prefSize.y, e.getY() + e.getHeight());
		}
		prefSize.x += target.borders.z;
		prefSize.y += target.borders.w + target.borders.x;
		return prefSize;
	}

	public Vector2f maximumSize(Element target) {
		return LUtil.DEFAULT_MAX_SIZE;
	}
}
