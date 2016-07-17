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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.scrolling.ScrollArea;
import icetone.controls.scrolling.VScrollBar;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.BitmapTextUtil;
import icetone.effects.Effect;
import icetone.framework.core.AnimText;
import icetone.listeners.MouseWheelListener;

/**
 * Some utilities used in layout management.
 */
public class LUtil {

	@SuppressWarnings("unchecked")
	public static <T> T getInaccessibleField(Class<T> clazz, String fieldName, Object object, Class<?> elementName) {
		try {
			Field f = elementName.getDeclaredField(fieldName);
			f.setAccessible(true);
			return (T) f.get(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void setInaccessibleField(Object value, String fieldName, Object object, Class<?> elementName) {
		try {
			Field f = elementName.getDeclaredField(fieldName);
			f.setAccessible(true);
			f.set(object, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void updateTextElement(Element el) {
		try {
			Method m = Element.class.getDeclaredMethod("updateTextElement");
			m.setAccessible(true);
			m.invoke(el);
		} catch (Exception e) {
			// TODO Ugh
			throw new RuntimeException(e);
		}
	}

	public static void positionScrollbars(ScrollArea area, Vector2f containerDimensions) {

		VScrollBar scrollBar = area.getVScrollBar();
		try {
			float scrollSize = LUtil.getInaccessibleField(Float.class, "scrollSize", area, ScrollArea.class);
			if (scrollBar != null) {
				Field field = ScrollArea.class.getDeclaredField("scrollHidden");
				field.setAccessible(true);
				if (field.getBoolean(area)) {
					area.setDimensions(containerDimensions.x, containerDimensions.y);
				} else {
					scrollBar.setPosition(containerDimensions.x - scrollSize, 0);
					scrollBar.setDimensions(scrollSize, containerDimensions.y);

					field = VScrollBar.class.getDeclaredField("btnScrollTrack");
					field.setAccessible(true);
					ButtonAdapter btnScrollTrack = (ButtonAdapter) field.get(scrollBar);
					btnScrollTrack.setDimensions(scrollBar.getWidth(),
							scrollBar.getHeight() - (scrollBar.getWidth() * 2));

					field = VScrollBar.class.getDeclaredField("btnScrollUp");
					field.setAccessible(true);
					ButtonAdapter btnScrollUp = (ButtonAdapter) field.get(scrollBar);
					btnScrollUp.setPosition(0, scrollBar.getHeight() - scrollBar.getWidth());

					area.setDimensions(containerDimensions.x - scrollSize, containerDimensions.y);
					scrollBar.setThumbScale();
				}
				scrollBar.setByThumbPosition();
			} else {
				area.setDimensions(containerDimensions.x, containerDimensions.y);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public static void layoutChildren(ElementManager screen) {
		// Collection<? extends Element> children = screen.getElements();
		//
		// // Layout any child MigLayouts
		// for (Element childElement : children) {
		// layout(childElement);
		// }
	}

	@Deprecated
	public static void layout(Spatial childSpatial) {
		// // New way
		// if (childSpatial instanceof Element) {
		// Element childElement = (Element) childSpatial;
		// childElement.layoutChildren();
		// } else {
		// // Search for children
		// if (childSpatial instanceof Node) {
		// layoutChildren((Node) childSpatial);
		// }
		// }
	}

	@Deprecated
	public static void layoutChildren(Node parent) {
		return;
		// Collection<? extends Spatial> children = parent.getChildren();
		// if (parent instanceof Element) {
		// children = ((Element) parent).getElements();
		// }
		//
		// // Layout any child MigLayouts
		// for (Spatial childSpatial : children) {
		// layout(childSpatial);
		// }
	}

	/**
	 * Size to use to give a component a maximum size. The component must be
	 * managed by MigLayot, which will collapse it to a size that fits the area
	 * available. Use with growx/growy etc
	 */
	public static final Vector2f LAYOUT_SIZE = new Vector2f(0, 1);
	public static final Vector2f DEFAULT_MAX_SIZE = new Vector2f(Short.MAX_VALUE, Short.MAX_VALUE);

	public static int getMinimumHeight(Element c) {
		return (int) getMinimumSize(c).y;
	}

	public static int getMinimumWidth(Element c) {
		return (int) getMinimumSize(c).x;
	}

	public static void setX(Element c, float x) {
		c.setX(x);

		// This is new ... not sure about it yet
		// c.controlMoveHook();
	}

	public static float getAbsoluteHeight(Element c) {
		return getAbsoluteY(c) + c.getHeight();
	}

	public static float getAbsoluteY(Element c) {
		float y = getY(c);
		Element el = c;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			y += LUtil.getY(el);
		}
		return y;
	}

	public static Vector2f getPosition(Element c) {
		return new Vector2f(c.getX(), getY(c));
	}

	public static float getY(Element c) {
		Element par = c.getElementParent();
		if (par != null) {
			if (isFlipY(c)) {
				return par.getHeight() - c.getY() - c.getHeight();
			} else {
				return c.getY();
			}
		} else {
			if (isFlipY(c)) {
				return c.getScreen().getHeight() - c.getY() - c.getHeight();
			} else {
				return c.getY();
			}
		}
	}

	public static void setY(Element c, float y) {
		Element par = c.getElementParent();
		float oy = 0;
		if (par != null) {
			if (isFlipY(c)) {
				oy = par.getHeight() - y - c.getHeight();
			} else {
				oy = y;
			}
		} else {
			if (isFlipY(c)) {
				oy = c.getScreen().getHeight() - y - c.getHeight();
			} else {
				oy = y;
			}
		}
		if (oy != c.getY()) {
			c.setY(oy);
			c.dirtyLayout(false);
		}
		if (Element.NEW_YFLIPPING)
			c.updateNodeLocation();

		// This is new ... not sure about it yet
		// c.controlMoveHook();
	}

	public static void setPosition(Element c, Vector2f p) {
		setPosition(c, p.x, p.y);
	}

	public static void setPosition(Element c, float x, float y) {
		Element par = c.getElementParent();
		float ox = c.getX(), oy = c.getY();
		if (par != null) {
			if (isFlipY(c)) {
				ox = x;
				oy = par.getHeight() - y - c.getHeight();
			} else {
				ox = x;
				oy = y;
			}
		} else {
			if (isFlipY(c)) {
				ox = x;
				oy = c.getScreen().getHeight() - y - c.getHeight();
			} else {
				ox = x;
				oy = y;
			}
		}
		if (ox != c.getX() || oy != c.getY()) {
			c.setPosition(ox, oy);
			c.dirtyLayout(false);

			// This is new ... not sure about it yet
			c.controlMoveHook();
		} else if (Element.NEW_YFLIPPING)
			c.updateNodeLocation();
	}

	protected static boolean isFlipY(Element c) {
		return !Element.NEW_YFLIPPING && c.getInitialized();
	}

	public static void setBounds(Element c, Vector4f bounds) {
		setBounds(c, bounds.x, bounds.y, bounds.z, bounds.w);
	}

	public static void setBounds(Element c, float x, float y, float w, float h) {
		Element par = c.getElementParent();
		if (c.getMinDimensions() != null) {
			w = Math.max(c.getMinDimensions().x, w);
			h = Math.max(c.getMinDimensions().y, h);
		}
		if (c.getMaxDimensions() != null) {
			w = Math.min(c.getMaxDimensions().x, w);
			h = Math.min(c.getMaxDimensions().y, h);
		}
		boolean changed = false;
		if (c.getWidth() != w || c.getHeight() != h) {
			c.setDimensions(w, h);
			changed = true;
		}

		float ox = 0, oy = 0;
		if (par != null) {
			if (isFlipY(c)) {
				ox = x;
				oy = par.getHeight() - c.getHeight() - y;
			} else {
				ox = x;
				oy = y;
			}
		} else {
			if (isFlipY(c)) {
				ox = x;
				oy = c.getScreen().getHeight() - y - c.getHeight();
			} else {
				ox = x;
				oy = y;
			}
		}
		if (ox != c.getX() || oy != c.getY()) {
			c.setPosition(ox, oy);
			changed = true;
		}
		if (Element.NEW_YFLIPPING)
			c.updateNodeLocation();
		if (changed) {
			c.dirtyLayout(false);
		}
	}

	public static void setDimensions(Element c, Vector2f dimension) {
		setDimensions(c, dimension.x, dimension.y);
	}

	public static void setDimensions(Element c, float w, float h) {
		setBounds(c, c.getX(), getY(c), w, h);
	}

	public static Vector2f getMaximumSize(Element c) {
		Vector2f max = c.getMaxDimensions();
		if (max == null && c.getLayoutManager() != null) {
			max = c.getLayoutManager().maximumSize(c);
		}
		if (max == null) {
			max = new Vector2f(Short.MAX_VALUE, Short.MAX_VALUE);
		}
		return max;
	}

	public static Vector2f getBoundPreferredSize(Element c) {
		Vector2f min = getMinimumSize(c);
		Vector2f max = getMaximumSize(c);
		Vector2f pref = getPreferredSize(c);
		Vector2f boundPref = clampSize(pref, min, max);
		return boundPref;
	}

	public static Vector2f getPreferredSizeFromTexture(Element c) {
		if (c != null && c.getElementTexture() != null) {
			Texture tex = c.getElementTexture();
			if (tex.getImage() != null)
				return new Vector2f(tex.getImage().getWidth(), tex.getImage().getHeight());
		}
		return null;
	}

	public static Vector2f addPaddingToSize(Vector2f sz, Vector4f padding) {
		return new Vector2f(sz.x, sz.y).addLocal(padding.x, padding.z).addLocal(padding.y, padding.w);
	}

	public static Vector2f getPreferredSize(Element c) {
		Vector2f pref = c.getPreferredDimensions();
		if (pref == null && !LAYOUT_SIZE.equals(c.getOrgDimensions())) {
			pref = c.getOrgDimensions();
		}
		if (pref == null && c.getLayoutManager() != null) {
			pref = c.getLayoutManager().preferredSize(c);
		}
		if (pref == null && c.getElementTexture() != null) {
			Texture tex = c.getElementTexture();
			if (tex.getImage() != null)
				pref = new Vector2f(tex.getImage().getWidth(), tex.getImage().getHeight());
		}
		if (pref == null) {
			if (!c.getText().equals("")) {
				pref = getPreferredTextSize(c);
			}
		}
		if (pref == null) {
			pref = Vector2f.ZERO;
		}
		pref = pref.clone();

		/*
		 * if (c instanceof LayoutConstrained) { pref = ((LayoutConstrained)
		 * c).getPreferredDimensions(); // System.err.println(
		 * "cu: constrined: pref of " + c + "[" + c.getClass() + "] is " +
		 * pref); } if (pref == null && c instanceof LayoutAware &&
		 * ((LayoutAware) c).getLayoutManager() != null) { pref = ((LayoutAware)
		 * c).getLayoutManager().preferredSize(c); // System.err.println(
		 * "cu: aware: pref of " + c + "[" + c.getClass() + "] is " + pref); }
		 * if (pref == null) { pref = new Vector2f(c.getOrgDimensions().x,
		 * c.getOrgDimensions().y); // System.err.println("cu: org: pref of " +
		 * c + "[" + c.getClass() + "] is " + pref); } else { pref =
		 * pref.clone(); }
		 */
		// Vector2f min = getMinimumSize(c);
		// if (min != null) {
		// pref.x = Math.max(min.x, pref.x);
		// pref.y = Math.max(min.y, pref.y);
		// }
		// // System.err.println("min is " + min);
		// Vector2f max = getMaximumSize(c);
		// // System.err.println("max is " + max);
		// if (max != null) {
		// pref.x = Math.min(max.x, pref.x);
		// pref.y = Math.min(max.y, pref.y);
		// }
		// pref.x = (int) pref.x;
		// pref.y = (int) pref.y;

		// System.err.println("cu: fine: pref of " + c + "[" + c.getClass() + "]
		// is " + pref);
		return pref;
	}

	public static int getMaximumWidth(Element c) {
		return (int) getMaximumSize(c).x;
	}

	public static int getMaximumHeight(Element c) {
		return (int) getMaximumSize(c).y;
	}

	public static int getPreferredWidth(Element c) {
		return (int) getPreferredSize(c).x;
	}

	public static int getPreferredHeight(Element c) {
		return (int) getPreferredSize(c).y;
	}

	public static Vector2f getMinimumSize(Element c) {
		Vector2f min = c.getMinDimensions();
		if (min == null && c.getLayoutManager() != null) {
			min = c.getLayoutManager().minimumSize(c);
		}
		if (min == null) {
			min = new Vector2f(1, 1);
		}
		return min;
	}

	public static Vector2f getPreferredTextSize(Element el) {
		/*
		 * If the element has a fixed max dimension, keep the preferred size
		 * within that to give us a chance of properly laying out text elements
		 * of an unknown size
		 */
		Vector2f max = el.getMaxDimensions();

		float scale = el.getFont() == null || el.getText() == null ? 0
				: (float) el.getFontSize() / (float) el.getFont().getCharSet().getRenderedSize();

		float lh = 0;
		if (max != null && max.x != Short.MAX_VALUE) {
			lh = BitmapTextUtil.getTextTotalHeight(el, el.getText(), max.x);
		} else {
			lh = el.getFont().getCharSet().getLineHeight();
		}

		float preferredHeight = scale == 0 ? 0 : (lh * scale);
		float preferredWidth = scale == 0 ? 0 : (el.getFont().getLineWidth(el.getText()) * scale);

		// TODO returns weird line count when opening things like combo boxes
		// int lines = textElement.getLineCount();
		// if (textElement != null && lines > 1) {
		// preferredHeight = preferredHeight * lines;
		// }
		return addPaddingToSize(new Vector2f((int) preferredWidth, (int) preferredHeight), el.getTextPaddingVec());
	}

	public static Collection<? extends Spatial> getAllChildren(Spatial panel) {
		if (panel instanceof Element) {
			return ((Element) panel).getElements();
		} else if (panel instanceof Node) {
			return ((Node) panel).getChildren();
		} else {
			return Collections.emptyList();
		}
	}

	public static Vector2f getContainerMinimumDimensions(Element element) {
		Vector2f minDimensions = element.getMinDimensions();
		if (minDimensions == null && element.getLayoutManager() != null) {
			minDimensions = element.getLayoutManager().minimumSize(element);
		}
		return minDimensions == null ? new Vector2f(1, 1) : minDimensions.clone();
	}

	@Deprecated
	public static Vector2f getContainerMaximumDimensions(Element element) {
		return getMaximumSize(element);
	}

	@Deprecated
	public static Vector2f getContainerPreferredDimensions(Element element) {
		// Vector2f prefDimensions = element.getPreferredDimensions();
		// if (prefDimensions == null) {
		// if (element.getElementTexture() != null) {
		// Texture tex = element.getElementTexture();
		// if (tex.getImage() != null)
		// prefDimensions = new Vector2f(tex.getImage().getWidth(),
		// tex.getImage().getHeight());
		// }
		// }
		// if (prefDimensions == null && element.getLayoutManager() != null
		// && (((element.getOrgDimensions().equals(LAYOUT_SIZE)
		// || LAYOUT_SIZE.equals(element.getPreferredDimensions()))))) {
		// prefDimensions = element.getLayoutManager().preferredSize(element);
		// }
		// return (prefDimensions == null ? element.getOrgDimensions() :
		// prefDimensions).clone();
		return getPreferredSize(element);

	}

	public static Element getRootElement(Element aThis) {
		while (aThis != null) {
			if (aThis.getElementParent() == null) {
				return aThis;
			}
			aThis = aThis.getElementParent();
		}
		return null;
	}

	public static Vector2f getScreenSize(ElementManager screen) {
		return new Vector2f(screen.getWidth(), screen.getHeight());
	}

	public static void removeEffects(Element el) {
		for (Effect.EffectEvent evt : Effect.EffectEvent.values()) {
			el.removeEffect(evt);
		}
	}

	public static void fireMouseWheelReleasedToParent(Element el, MouseButtonEvent evt) {
		while ((el = el.getElementParent()) != null) {
			if (el instanceof MouseWheelListener) {
				((MouseWheelListener) el).onMouseWheelPressed(evt);
			}
		}
	}

	public static void fireMouseWheelUpToParent(Element el, MouseMotionEvent evt) {
		while ((el = el.getElementParent()) != null) {
			if (el instanceof MouseWheelListener) {
				((MouseWheelListener) el).onMouseWheelUp(evt);
			}
		}
	}

	public static void fireMouseWheelDownToParent(Element el, MouseMotionEvent evt) {
		while ((el = el.getElementParent()) != null) {
			if (el instanceof MouseWheelListener) {
				((MouseWheelListener) el).onMouseWheelDown(evt);
			}
		}
	}

	public static Vector2f max(Vector2f v1, Vector2f v2) {
		return new Vector2f(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y));
	}

	public static Vector2f min(Vector2f v1, Vector2f v2) {
		return new Vector2f(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y));
	}

	public static void restrictToScreen(Element el, float x, float y) {
		ElementManager screen = el.getScreen();
		float cx = x;
		if (cx + el.getWidth() > screen.getWidth()) {
			cx = screen.getWidth() - el.getWidth();
		}
		float cy = screen.getHeight() - y - (el.getHeight() / 2);
		if (cy < 0) {
			cy = 0;
		}
		el.setPosition(cx, cy);
	}

	public static Vector2f union(Vector2f clone, Vector2f containerPreferredDimensions) {
		return new Vector2f(Math.max(clone.x, containerPreferredDimensions.x),
				Math.max(clone.y, containerPreferredDimensions.y));
	}

	static class ShowData implements Savable {

		Element el;
		Element tel;

		public void write(JmeExporter ex) throws IOException {
		}

		public void read(JmeImporter im) throws IOException {
		}
	}

	public static Vector2f clampSize(Vector2f sz, Vector2f minDimensions, Vector2f maxDimensions) {
		if (minDimensions != null)
			sz = max(minDimensions, sz);
		if (maxDimensions != null)
			sz = min(maxDimensions, sz);
		return sz;
	}

}
