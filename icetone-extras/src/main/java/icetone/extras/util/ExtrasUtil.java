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
package icetone.extras.util;

import java.util.prefs.Preferences;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Size;
import icetone.extras.windows.PersistentWindow;

public class ExtrasUtil {

	public static String getDirname(String value) {
		int idx = value.lastIndexOf('/');
		return idx == -1 ? null : value.substring(0, idx);
	}

	public static Vector2f getDefaultPosition(int offset, Align defaultHorizontal, VAlign defaultVertical,
			ElementManager<?> screen, Size windowSize) {
		return getDefaultPosition(offset, defaultHorizontal, defaultVertical, screen,
				windowSize == null ? null : windowSize.toVector2f());
	}

	public static Vector2f getDefaultPosition(int offset, Align defaultHorizontal, VAlign defaultVertical,
			ElementManager<?> screen, Vector2f windowSize) {
		return new Vector2f(getDefaultHorizontal(offset, defaultHorizontal, screen, windowSize),
				getDefaultVertical(offset, defaultVertical, screen, windowSize));
	}

	public static float getDefaultVertical(int offset, VAlign defaultVertical, ElementManager<?> screen,
			Vector2f windowSize) {
		float y = 0;
		switch (defaultVertical) {
		case Top:
			y = offset;
			break;
		case Bottom:
			y = screen.getHeight() - (windowSize == null ? (screen.getHeight() / 2f) : windowSize.y) - offset;
			break;
		case Center:
			y = (int) ((screen.getHeight() - (windowSize == null ? (screen.getHeight() / 2f) : windowSize.y)) / 2.0F);
			break;
		}
		return y;
	}

	public static float getDefaultHorizontal(int offset, Align defaultHorizontal, ElementManager<?> screen,
			Vector2f windowSize) {
		float x = 0;
		switch (defaultHorizontal) {
		case Left:
			x = offset;
			break;
		case Right:
			x = screen.getWidth() - (windowSize == null ? (screen.getWidth() / 2) : windowSize.x) - offset;
			break;
		case Center:
			x = (int) ((screen.getWidth() - (windowSize == null ? (screen.getHeight() / 2) : windowSize.y)) / 2.0F);
			break;
		}
		return x;
	}

	public static void saveWindowPosition(Preferences pref, BaseElement window, String id) {
		Vector2f pos = window.getPosition();
		pref.putInt(id + PersistentWindow.WINDOW_X, (int) pos.x);
		pref.putInt(id + PersistentWindow.WINDOW_Y, (int) pos.y);
	}

	public static boolean isWindowPositionSaved(Preferences pref, String id) {
		if (pref == null)
			return false;
		return pref.getFloat(id + PersistentWindow.WINDOW_X, Integer.MIN_VALUE) != Integer.MIN_VALUE
				&& pref.getFloat(id + PersistentWindow.WINDOW_Y, Integer.MIN_VALUE) != Integer.MIN_VALUE;
	}

	public static boolean isWindowSizeSaved(Preferences pref, String id) {
		if (pref == null)
			return false;
		return pref.getFloat(id + PersistentWindow.WINDOW_WIDTH, Integer.MIN_VALUE) != Integer.MIN_VALUE
				&& pref.getFloat(id + PersistentWindow.WINDOW_HEIGHT, Integer.MIN_VALUE) != Integer.MIN_VALUE;
	}

	public static Vector2f getWindowPosition(Preferences pref, ElementManager<?> screen, String id,
			Size defaultWindowSize) {
		return getWindowPosition(pref, screen, id, defaultWindowSize == null ? null : defaultWindowSize.toVector2f());
	}

	public static Vector2f getWindowPosition(Preferences pref, ElementManager<?> screen, String id,
			Vector2f defaultWindowSize) {
		return getWindowPosition(pref, screen, id, defaultWindowSize, 0, Align.Center, VAlign.Center);
	}

	public static Vector2f getWindowSize(Preferences pref, ElementManager<?> screen, String id,
			Size defaultWindowSize) {
		return getWindowSize(pref, screen, id, defaultWindowSize == null ? null : defaultWindowSize.toVector2f());
	}

	public static Vector2f getWindowSize(Preferences pref, ElementManager<?> screen, String id,
			Vector2f defaultWindowSize) {
		if (id == null) {
			return defaultWindowSize;
		}
		float fx = pref.getFloat(id + PersistentWindow.WINDOW_WIDTH,
				defaultWindowSize == null ? -1 : defaultWindowSize.x);
		float fy = pref.getFloat(id + PersistentWindow.WINDOW_HEIGHT,
				defaultWindowSize == null ? -1 : defaultWindowSize.y);
		return fx == -1 && fy == -1 ? null : new Vector2f(fx, fy);
	}

	public static Vector2f getWindowPosition(Preferences pref, ElementManager<?> screen, String id,
			Vector2f defaultWindowSize, int offset, Align defaultHorizontal, VAlign defaultVertical) {
		Vector2f windowSize = getWindowSize(pref, screen, id, defaultWindowSize);
		float x = Integer.MIN_VALUE;
		float y = Integer.MIN_VALUE;
		if (id != null) {
			x = pref.getFloat(id + PersistentWindow.WINDOW_X, Integer.MIN_VALUE);
			y = pref.getFloat(id + PersistentWindow.WINDOW_Y, Integer.MIN_VALUE);
		}
		if (x == Integer.MIN_VALUE || y == Integer.MIN_VALUE) {
			x = ExtrasUtil.getDefaultHorizontal(offset, defaultHorizontal, screen, windowSize);
			y = ExtrasUtil.getDefaultVertical(offset, defaultVertical, screen, windowSize);
		}
		if (x < 0) {
			x = 0;
		} else if (x + windowSize.x > screen.getWidth()) {
			x = screen.getWidth() - windowSize.x;
		}
		if (y < 0) {
			y = 0;
		} else if (y + windowSize.y > screen.getHeight()) {
			y = screen.getHeight() - windowSize.y;
		}
		return new Vector2f(x, y);
	}

	public static void saveWindowSize(Preferences pref, BaseElement window, String id) {
		pref.putInt(id + PersistentWindow.WINDOW_WIDTH, (int) window.getWidth());
		pref.putInt(id + PersistentWindow.WINDOW_HEIGHT, (int) window.getHeight());
	}

	public static void saveWindowPositionAndSize(Preferences pref, BaseElement window, String id) {
		saveWindowSize(pref, window, id);
		saveWindowPosition(pref, window, id);
	}

	public static String getFilename(String path) {
		int idx = path.lastIndexOf('/');
		return idx == -1 ? path : path.substring(idx + 1);
	}

	public static ColorRGBA fromColorString(String col) {
		return fromColorString(col, true);
	}

	public static ColorRGBA fromColorString(String col, boolean inclueAlpha) {
		if (col == null)
			throw new IllegalArgumentException("May not be null.");
		if (col.startsWith("#")) {
			col = col.substring(1);
		}

		try {
			return (ColorRGBA) ColorRGBA.class.getDeclaredField(col).get(null);
		} catch (Exception e) {
			if (col.length() == 3 || (col.length() == 4 && inclueAlpha)) {
				float rh = Long.decode("#" + col.substring(0, 1)).floatValue();
				float rg = Long.decode("#" + col.substring(1, 2)).floatValue();
				float rb = Long.decode("#" + col.substring(2, 3)).floatValue();
				float ra = col.length() == 4 ? Long.decode("#" + col.substring(3, 4)).floatValue() : 0xff;
				return new ColorRGBA(rh / 16, rg / 16, rb / 16, ra / 16);
			} else if (col.length() == 6 || (col.length() == 8 && inclueAlpha)) {
				float rh = Long.decode("#" + col.substring(0, 2)).floatValue();
				float rg = Long.decode("#" + col.substring(2, 4)).floatValue();
				float rb = Long.decode("#" + col.substring(4, 6)).floatValue();
				float ra = col.length() == 8 ? Long.decode("#" + col.substring(6, 8)).floatValue() : 0xff;
				return new ColorRGBA(rh / 255, rg / 255, rb / 255, ra / 255);
			}
			throw new IllegalArgumentException("Not a colour.");
		}
	}

	public static String toHexString(ColorRGBA color) {
		return toHexString(color, true);
	}

	public static String toHexString(ColorRGBA color, boolean includeAlpha) {
		if (color == null) {
			return "auto";
		}
		return "#" + toHexNumber(color, includeAlpha);
	}

	public static String toHexNumber(ColorRGBA color) {
		return toHexNumber(color, true);
	}

	public static String toHexNumber(ColorRGBA color, boolean includeAlpha) {
		return toHexDigits((int) (color.r * 255)) + toHexDigits((int) (color.g * 255))
				+ toHexDigits((int) (color.b * 255)) + (includeAlpha ? toHexDigits((int) (color.a * 255)) : "");
	}

	public static String toHexDigits(int value) {
		return String.format("%02x", value);
	}

	public static float[] toHSB(ColorRGBA rgba) {
		int r = (int) (rgba.r * 255f);
		int g = (int) (rgba.g * 255f);
		int b = (int) (rgba.b * 255f);
		float hue, saturation, brightness;
		float[] hsbvals = new float[3];
		int cmax = (r > g) ? r : g;
		if (b > cmax)
			cmax = b;
		int cmin = (r < g) ? r : g;
		if (b < cmin)
			cmin = b;
	
		brightness = ((float) cmax) / 255.0f;
		if (cmax != 0)
			saturation = ((float) (cmax - cmin)) / ((float) cmax);
		else
			saturation = 0;
		if (saturation == 0)
			hue = 0;
		else {
			float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
			float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
			float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
			if (r == cmax)
				hue = bluec - greenc;
			else if (g == cmax)
				hue = 2.0f + redc - bluec;
			else
				hue = 4.0f + greenc - redc;
			hue = hue / 6.0f;
			if (hue < 0)
				hue = hue + 1.0f;
		}
		hsbvals[0] = hue;
		hsbvals[1] = saturation;
		hsbvals[2] = brightness;
		return hsbvals;
	}

	public static ColorRGBA toRGBA(float[] hsb) {
		float hue = hsb[0];
		float saturation = hsb[1];
		float brightness = hsb[2];
		return toRGBA(hue, saturation, brightness);
	}

	public static ColorRGBA toRGBA(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
			case 0:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (t * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 1:
				r = (int) (q * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (p * 255.0f + 0.5f);
				break;
			case 2:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (brightness * 255.0f + 0.5f);
				b = (int) (t * 255.0f + 0.5f);
				break;
			case 3:
				r = (int) (p * 255.0f + 0.5f);
				g = (int) (q * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 4:
				r = (int) (t * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (brightness * 255.0f + 0.5f);
				break;
			case 5:
				r = (int) (brightness * 255.0f + 0.5f);
				g = (int) (p * 255.0f + 0.5f);
				b = (int) (q * 255.0f + 0.5f);
				break;
			}
		}
		return new ColorRGBA((float) r / 255f, (float) g / 255f, (float) b / 255f, 1f);
	}

	public static String toEnglish(Object object) {
		return toEnglish(object, true);
	}

	public static String toEnglish(Object o, boolean name) {
		if (o == null) {
			return "";
		}
		String str = String.valueOf(o);
		boolean newWord = true;
		StringBuffer newStr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			ch = Character.toLowerCase(ch);
			if (ch == '_') {
				ch = ' ';
			}
			if (ch == ' ') {
				newWord = true;
			}
			else if (newWord && name) {
				ch = Character.toUpperCase(ch);
				newWord = false;
			}
			newStr.append(ch);
		}
		return newStr.toString();
	}
}
