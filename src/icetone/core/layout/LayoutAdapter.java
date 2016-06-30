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

/**
 * Layout manager that delegeates to another layout manager. Useful for doing stuff before
 * or after layout or providing different container sizes.
 */
public class LayoutAdapter implements LayoutManager {

    private final LayoutManager delegate;

    public LayoutAdapter(LayoutManager parent) {
        this.delegate = parent;
    }

    public Vector2f minimumSize(Element parent) {
        return delegate.minimumSize(parent);
    }

    public Vector2f maximumSize(Element parent) {
        return delegate.maximumSize(parent);
    }

    public Vector2f preferredSize(Element parent) {
        return delegate.preferredSize(parent);
    }

    public void layout(Element childElement) {
        delegate.layout(childElement);
    }

    public void remove(Element child) {
        delegate.remove(child);
    }

    public void constrain(Element child, Object constraints) {
        delegate.constrain(child, constraints);
    }

    public void layoutScreen(ElementManager screen) {
        delegate.layoutScreen(screen);
    }
}
