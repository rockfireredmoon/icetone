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

import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

/**
 */
public class TonegodGUIComponentWrapper extends AbstractWrapper<BaseElement> implements ComponentWrapper {

	/**
	 * User data key for layout (stored in {@link BaseElement}
	 */
	public final static String USER_DATA_LAYOUT = "Layout";

	private static boolean vp = true;
	private final BaseElement c;

	public TonegodGUIComponentWrapper(BaseElement c) {
		super(c, c.getScreen());
		this.c = c;
	}

	@Override
	public final int getX() {
		return (int) c.getX();
	}

	@Override
	public final int getY() {
		return (int) c.getY();
	}

	@Override
	public final int getWidth() {
		int width = (int) c.getWidth();
		return width;
	}

	@Override
	public final int getHeight() {
		return (int) c.getHeight();
	}

	@Override
	public final int getScreenLocationX() {
		return (int) c.getAbsoluteX();
	}

	@Override
	public final int getScreenLocationY() {
		return (int) c.getAbsoluteY();
	}

	@Override
	public final int getMinimumHeight(int sz) {
		return (int) c.calcMinimumSize().y;
	}

	@Override
	public final int getMinimumWidth(int sz) {
		return (int) c.calcMinimumSize().x;
	}

	@Override
	public final int getPreferredHeight(int sz) {
		return (int) c.calcPreferredSize().y;
	}

	@Override
	public final int getPreferredWidth(int sz) {
		return (int) c.calcPreferredSize().x;
	}

	@Override
	public final int getMaximumHeight(int sz) {
		return (int) c.calcMaximumSize().y;
	}

	@Override
	public final int getMaximumWidth(int sz) {
		return (int) c.calcMaximumSize().x;
	}

	@Override
	public final ContainerWrapper getParent() {
		return c.getElementParent() == null ? new TonegodGUIScreenWrapper(screen)
				: new TonegodGUIContainerWrapper(c.getElementParent());
	}

	@Override
	public final void setBounds(int x, int y, int width, int height) {
		c.setBounds(x, y, width, height);
	}

	@Override
	public boolean isVisible() {
		return c.isVisible();
	}

	public static boolean isUseVisualPadding() {
		return vp;
	}

	public static void setUseVisualPadding(boolean b) {
		vp = b;
	}

	@Override
	BitmapFont getFont() {
		return BaseElement.calcFont(c);
	}

	@Override
	public int[] getVisualPadding() {
		return null;
	}
}
