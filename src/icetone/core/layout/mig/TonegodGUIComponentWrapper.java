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
 * 
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

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

/**
 */
public class TonegodGUIComponentWrapper extends AbstractWrapper implements ComponentWrapper {

    /**
     * User data key for layout (stored in {@link Element}
     */
    public final static String USER_DATA_LAYOUT = "Layout";
    /**
     * Debug color for component bounds outline.
     */
    private static ColorRGBA DB_COMP_OUTLINE = ColorRGBA.Red;
    private static boolean vp = false;
    private final Element c;
    private Vector2f prefSize;

    public TonegodGUIComponentWrapper(Element c) {
        super(c, c.getScreen());
        this.c = c;
    }

    public final int getX() {
        return (int) c.getX();
    }

    public final int getY() {
        return (int) c.getY();
    }

    public final int getWidth() {
        return (int) c.getWidth();
    }

    public final int getHeight() {
        return (int) c.getHeight();
    }

    public final int getScreenLocationX() {
        return (int) c.getAbsoluteX();
    }

    public final int getScreenLocationY() {
        return (int) c.getAbsoluteY();
    }

    public final int getMinimumHeight(int sz) {
        return LUtil.getMinimumHeight(c);
    }

    public final int getMinimumWidth(int sz) {
        return LUtil.getMinimumWidth(c);
    }

    public final int getPreferredHeight(int sz) {
        return LUtil.getPreferredHeight(c);
    }

    public final int getPreferredWidth(int sz) {
        return LUtil.getPreferredWidth(c);
    }

    public final int getMaximumHeight(int sz) {
        return LUtil.getMaximumHeight(c);
    }

    public final int getMaximumWidth(int sz) {
        return LUtil.getMaximumWidth(c);
    }

    public final ContainerWrapper getParent() {
        return c.getElementParent() == null ? new TonegodGUIScreenWrapper((Screen) screen) : 
                new TonegodGUIContainerWrapper(c.getElementParent());
    }

    public final void setBounds(int x, int y, int width, int height) {
        LUtil.setBounds(c, x, y, width, height);
    }

    public boolean isVisible() {
        return c.getIsVisible();
    }

    public final int[] getVisualPadding() {
        return null;
    }

    public static boolean isUseVisualPadding() {
        return vp;
    }

    public static void setUseVisualPadding(boolean b) {
        vp = b;
    }

    public int getLayoutHashCode() {
        if (c.getParent() == null) {
            return -1;
        }

        Vector2f sz = c.getDimensions();
        Vector2f p = c.getOrgDimensions();
        int h = (int) p.x + ((int) p.y << 12) + ((int) sz.x << 22) + ((int) sz.y << 16);

        if (c.getIsVisible()) {
            h += 1324511;
        }
        String id = getLinkId();
        if (id != null) {
            h += id.hashCode();
        }
        return h;
    }

    @Override
    BitmapFont getFont() {
        return c.getFont();
    }
}
