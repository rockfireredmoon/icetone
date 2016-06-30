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

import icetone.controls.windows.Window;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.ControlUtil;

/**
 * Interface to be implemented by layout managers. These should be set on container like
 * controls that have layout support (e.g {@link Window}.
 */
public interface LayoutManager {

    public final static Object EXCLUDE_FROM_LAYOUT = new Object();
    public final static String DEFAULT_LAYOUT = "";

    /**
     * Get the minimum size used for this layout. Will be called it either a control is
     * NOT {@link LayoutConstrained} or {@link LayoutConstrained#getMinDimensions() }, and
     * it DOES implement the {@link LayoutAware} interface. Return
     * <code>null</code> here to use the default minimum size of 0,0.
     * <p>
     * Do not call this in your own code. Instead use {@link ControlUtil#getMaximumSize(icetone.core.Element) }.
     * </p>
     *
     * @param parent layout owner
     * @return minimum size
     */
    Vector2f minimumSize(Element parent);

    /**
     * Get the maximum size used for this layout. Will be called it either a control is
     * NOT {@link LayoutConstrained} or {@link LayoutConstrained#getMaxDimensions() }, and
     * it DOES implement the {@link LayoutAware} interface. Return
     * <code>null</code> here to use the default minimum size of 32768, 32768
     * <p>
     * Do not call this in your own code. Instead use {@link ControlUtil#getMaximumSize(icetone.core.Element) }.
     * </p>
     *
     * @param parent layout owner
     * @return maxium size
     */
    Vector2f maximumSize(Element parent);

    /**
     * Get the preferred size used for this layout. Will be called it either a control is
     * NOT {@link LayoutConstrained} or {@link LayoutConstrained#getMaxDimensions() }, and
     * it DOES implement the {@link LayoutAware} interface. Return
     * <code>null</code> here to use the controls {@link Element#getOrgDimensions()}.
     * <p>
     * Do not call this in your own code. Instead use {@link ControlUtil#getMaximumSize(icetone.core.Element) }.
     * </p>
     *
     * @param parent layout owner
     * @return preferred size
     */
    Vector2f preferredSize(Element parent);

    /**
     * Layout the provided container.
     *
     * @param container container
     */
    void layout(Element container);

    /**
     * Layout the provided screen.
     *
     * @param screen screen
     */
    void layoutScreen(ElementManager screen);

    /**
     * Configure a child's constrain. The type will be depend on the layout manager.
     *
     * @param child child
     * @param constraints constraints
     */
    void constrain(Element child, Object constraints);

    /**
     * Called when a child is removed from it's contain. If the layout maintains any kind
     * of caching or other state for the child, this is the signal it can be removed.
     *
     * @param child child removed
     */
    void remove(Element child);
}
