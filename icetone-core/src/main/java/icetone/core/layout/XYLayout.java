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

import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;

/**
 * Layout manager that lays components at X,Y coordinates as supplied by either
 * styling or programmatically, and will either size to preferred size or by
 * either styling or programmatically depending on the
 * {@link XYLayoutConstraints} used. The preferred size of the container is the
 * minimum and maximum bounds of those positions.
 */
public class XYLayout extends AbstractGenericLayout<ElementContainer<?, ?>, Vector2f> {

	@Override
	protected Vector2f calcMinimumSize(ElementContainer<?, ?> parent) {
		return calcPreferredSize(parent);
	}

	@Override
	public boolean positionsElement(BaseElement element) {
		return false;
	}

	@Override
	protected Vector2f calcPreferredSize(ElementContainer<?, ?> target) {
		Vector2f prefSize = new Vector2f();
		for (BaseElement e : target.getElements()) {
			Vector2f bnds = e.calcPreferredSize();
			Vector2f pos = e.getPixelPosition();
			prefSize.x = Math.max(prefSize.x, Math.max(pos.x, 0) + bnds.x);
			prefSize.y = Math.max(prefSize.y, Math.max(pos.y, 0) + bnds.y);
		}
		return prefSize.addLocal(target.getTotalPadding());
	}

	@Override
	protected void onLayout(ElementContainer<?, ?> parent) {
		for (BaseElement e : parent.getElements()) {
			e.setDimensions(e.calcPreferredSize());
		}
	}

}
