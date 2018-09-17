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

import icetone.controls.containers.Frame;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.extras.util.ExtrasUtil;

/**
 * Extension of {@link Frame} that saves it's current size and position (and
 * loads it when created). The bounds are stored in the Java Preferences API
 * (@{@link Preferences}).
 *
 */
public class PersistentWindow extends PositionableFrame {
	public final static String WINDOW = "Window";
	public final static String WINDOW_X = WINDOW + "X";
	public final static String WINDOW_Y = WINDOW + "Y";
	public final static String WINDOW_HEIGHT = WINDOW + "Height";
	public final static String WINDOW_WIDTH = WINDOW + "Width";

	private SaveType saveType;
	private boolean loadedGeometry;

	protected Preferences pref;

	public PersistentWindow(BaseScreen screen, String styleId, Vector2f position, Size dimensions,
			boolean closeable, SaveType saveType, Preferences pref) {
		super(screen, styleId, position, dimensions, closeable);
		init(saveType, pref);
	}

	public PersistentWindow(BaseScreen screen, String styleId, VAlign vposition, Align hposition,
			Size dimensions, boolean closeable, SaveType saveType, Preferences pref) {
		this(screen, styleId, -1, vposition, hposition, dimensions, closeable, saveType, pref);
	}

	public PersistentWindow(BaseScreen screen, String styleId, int offset, VAlign vposition, Align hposition,
			Size dimensions, boolean closeable, SaveType saveType, Preferences pref) {
		super(screen, styleId, offset, vposition, hposition, dimensions, closeable);
		init(saveType, pref);
	}

	private void init(SaveType saveType, Preferences pref) {
		this.saveType = saveType;
		this.pref = pref;
	}

	@Override
	public void onInitialized() {
		super.onInitialized();
		if (saveType != null) {
			switch (saveType) {
			case POSITION:
				if (ExtrasUtil.isWindowPositionSaved(pref, getStyleId())) {
					final Vector2f windowPosition = ExtrasUtil.getWindowPosition(pref, screen, getStyleId(),
							getDimensions());
					setPosition(windowPosition);
				}
				sizeToContent();
				setMovable(true);
				break;
			case POSITION_AND_SIZE:
				if (ExtrasUtil.isWindowPositionSaved(pref, getStyleId())) {
					final Vector2f windowPosition = ExtrasUtil.getWindowPosition(pref, screen, getStyleId(),
							getDimensions());
					setPosition(windowPosition);
				}
				if (ExtrasUtil.isWindowSizeSaved(pref, getStyleId())) {
					setDimensions(ExtrasUtil.getWindowSize(pref, screen, getStyleId(), getDimensions()));
				} else
					sizeToContent();
				setMovable(true);
				setResizable(true);
				break;
			case SIZE:
				setResizable(true);
				if (ExtrasUtil.isWindowSizeSaved(pref, getStyleId())) {
					setDimensions(ExtrasUtil.getWindowSize(pref, screen, getStyleId(), getDimensions()));
				} else
					sizeToContent();
				break;
			default:
				break;
			}
			loadedGeometry = true;
		}
	}

	@Override
	protected final void onControlResizeHook() {
		super.onControlResizeHook();
		saveMetrics();
		onPersistentWindowResizeHook();
	}

	@Override
	public final void controlMoveHook() {
		super.controlMoveHook();
		saveMetrics();
		onControlMoveHook();
	}

	@Deprecated
	protected void onControlMoveHook() {
	}

	protected void onPersistentWindowResizeHook() {
	}

	private void saveMetrics() {
		if (loadedGeometry && pref != null) {
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

	public void destroy() {
		setDestroyOnHide(true);
		hide();
	}
}
