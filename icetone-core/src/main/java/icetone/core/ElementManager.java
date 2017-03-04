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
package icetone.core;

import java.util.List;

import com.jme3.app.Application;
import com.jme3.collision.CollisionResult;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import icetone.css.StyleManager.CursorType;
import icetone.css.StylesheetProvider;
import icetone.effects.EffectManager;
import icetone.framework.core.AnimLayer;
import icetone.framework.core.AnimManager;

/**
 *
 * @author t0neg0d
 * @author rockfire
 */
public interface ElementManager<ET extends UIEventTarget>
		extends ElementContainer<ElementManager<ET>, ET>, StylesheetProvider {

	AnimLayer addAnimLayer();

	Texture createNewTexture(String texturePath);

	AnimManager getAnimManager();

	Application getApplication();

	Texture getAtlasTexture();

	int getClickCount();

	BaseElement getDragElement();

	BaseElement getDropElement();

	Vector2f getDropElementOffset();

	EffectManager getEffectManager();

	EventCaster getEventCaster();

	float getGlobalAlpha();

	Node getGUINode();

	BaseElement getKeyboardFocus();

	CollisionResult getLastCollision();

	BaseElement getModalElement();

	List<BaseElement> getModalElements();

	BaseElement getMouseFocusElement();

	Vector2f getMouseXY();

	ToolTipManager getToolTipManager();

	Vector2f getTouchXY();

	boolean getUseTextureAtlas();

	boolean getUseUIAudio();

	void handleAndroidMenuState(BaseElement element);

	boolean isMouseButtonsEnabled();

	boolean isSnapToPixel();

	void onKeyEvent(KeyInputEvent evt);

	void releaseModal(BaseElement el);

	void removeAnimLayer(AnimLayer animLayer);

	void resetKeyboardFocus(BaseElement other);

	void setActiveCursor(CursorType cursorType);

	void setGlobalAlpha(float alpha);

	void setKeyboardFocus(BaseElement element);

	void setMouseButtonsEnabled(boolean b);

	void setUIAudioVolume(float volume);

	void setUseUIAudio(boolean use);

	@Override
	ElementManager<ET> showElement(BaseElement mapWindow);

	ElementManager<ET> showElement(BaseElement mapWindow, Object constraints);

	void updateZOrder(BaseElement element);

}