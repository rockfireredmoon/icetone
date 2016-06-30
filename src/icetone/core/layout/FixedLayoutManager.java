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

import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.ElementManager;

public class FixedLayoutManager extends AbstractLayout {

	public void layout(Element parent) {
		for(Element e : parent.getElementList()) {
			e.updateNodeLocation();
		}
	}

	@Override
	public void layoutScreen(ElementManager screen) {
	}

	public void constrain(Element child, Object constraints) {
		// No constraints supported
	}

	public Vector2f minimumSize(Element parent) {
		Vector2f min = new Vector2f(0,0);
		for(Element e : parent.getElementList()) {
			min.x = Math.max(min.x, e.getX() + e.getWidth());
			min.y = Math.max(min.y, e.getY() + e.getHeight());
		}
		min.addLocal(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y, parent.getTextPaddingVec().z + parent.getTextPaddingVec().w);
		return min;
	}

	public Vector2f preferredSize(Element target) {
		return minimumSize(target);
	}

	public Vector2f maximumSize(Element target) {
		return target.getMaxDimensions();
	}

	public void remove(Element child) {
	}
}
