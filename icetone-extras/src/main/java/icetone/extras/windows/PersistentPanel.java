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
package icetone.extras.windows;

import java.util.prefs.Preferences;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.Vector2f;

import icetone.controls.containers.Panel;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.extras.util.ExtrasUtil;

/**
 * Extension of {@link Panel} that saves it's current size and position (and
 * loads it when created). The bounds are stored in the Java Preferences API
 * (@{@link Preferences}).
 *
 */
public class PersistentPanel extends Panel {

	private SaveType saveType;
	private Preferences pref;

	public PersistentPanel(BaseScreen screen, String UID, Vector2f position, Size dimensions, SaveType saveType,
			Preferences pref) {
		super(screen, UID,
				ExtrasUtil.getWindowPosition(pref, screen, UID,
						ExtrasUtil.getWindowSize(pref, screen, UID, dimensions)),
				new Size(ExtrasUtil.getWindowSize(pref, screen, UID, dimensions)));
		init(saveType, pref);
	}

	public PersistentPanel(BaseScreen screen, String UID, int offset, VAlign vposition, Align hposition,
			Size dimensions, SaveType saveType, Preferences pref) {
		super(screen, UID,
				ExtrasUtil.getWindowPosition(pref, screen, UID, ExtrasUtil.getWindowSize(pref, screen, UID, dimensions),
						offset, hposition, vposition),
				new Size(ExtrasUtil.getWindowSize(pref, screen, UID, dimensions)));
		init(saveType, pref);
	}

	private void init(SaveType saveType, Preferences pref) {
		this.saveType = saveType;
		this.pref = pref;
		if (saveType != null) {
			switch (saveType) {
			case POSITION:
				setPosition(ExtrasUtil.getWindowPosition(pref, screen, getStyleId(), getDimensions()));
				break;
			case POSITION_AND_SIZE:
				setBounds(ExtrasUtil.getWindowPosition(pref, screen, getStyleId(), getDimensions()),
						ExtrasUtil.getWindowSize(pref, screen, getStyleId(), getDimensions()));
				break;
			case SIZE:
				setDimensions(ExtrasUtil.getWindowSize(pref, screen, getStyleId(), getDimensions()));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public final void controlResizeHook() {
		saveMetrics();
		onPersistentWindowReiszeHook();
	}

	@Override
	public final void controlMoveHook() {
		super.controlMoveHook();
		saveMetrics();
		onControlMoveHook();
	}

	protected void onControlMoveHook() {
	}

	protected void onPersistentWindowReiszeHook() {
	}

	private void saveMetrics() {
		if (saveType != null) {
			switch (saveType) {
			case POSITION:
				ExtrasUtil.saveWindowPosition(pref, this, getStyleId());
				break;
			case POSITION_AND_SIZE:
				ExtrasUtil.saveWindowPositionAndSize(pref, this, getStyleId());
				break;
			case SIZE:
				ExtrasUtil.saveWindowSize(pref, this, getStyleId());
				break;
			default:
				break;
			}
		}
	}
}
