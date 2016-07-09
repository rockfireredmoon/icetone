package icetone.core.layout.mig;
/*
 * License (BSD):
 * ==============
 *
 * TonegodGUI version (for JME3 engine). 
 * http://hub.jmonkeyengine.org/forum/topic/tonegodgui-documentation/
 * Based on SWT version.
 * 
 * Copyright (c) 2013, Emerald Icemoon. All rights reserved.
 * 
 * =======================================================================================
 * Copyright (c) 2004, Mikael Grev, MiG InfoCom AB. (miglayout (at) miginfocom (dot) com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * Neither the name of the MiG InfoCom AB nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @version 1.0
 * @author Mikael Grev, MiG InfoCom AB
 *         Date: 2006-sep-08
 */

import java.util.ArrayList;
import java.util.List;

import com.jme3.scene.Spatial;

import icetone.core.Element;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

/**
 */
public final class TonegodGUIContainerWrapper extends TonegodGUIComponentWrapper implements ContainerWrapper {

    public TonegodGUIContainerWrapper(Element c) {
        super(c);
    }

    public ComponentWrapper[] getComponents() {
        Element c = (Element) getComponent();
        List<Spatial> cons = c.getChildren();
        List<ComponentWrapper> elementCons = new ArrayList<ComponentWrapper>();
        for (Spatial s : c.getElements()) {
            if (s instanceof Element) {
                int t = MigLayout.checkType(s);
                if (t == TYPE_CONTAINER) {
                    elementCons.add(new TonegodGUIContainerWrapper((Element) s));
                } else {
                    elementCons.add(new TonegodGUIComponentWrapper((Element) s));
                }
            }
        }
        return elementCons.toArray(new ComponentWrapper[elementCons.size()]);
    }

    public int getComponentCount() {
        return ((Element) getComponent()).getElements().size();
    }

    public Object getLayout() {
        return ((Element) getComponent()).getElementUserData();
    }

    public final boolean isLeftToRight() {
        return true;
    }

    @Override
    public int getComponetType(boolean disregardScrollPane) {
        return TYPE_CONTAINER;
    }

    @Override
    public int getLayoutHashCode() {
        int h = super.getLayoutHashCode();

        if (isLeftToRight()) {
            h |= (1 << 26);
        }

        return h;
    }
}