package icetone.core.layout.mig;
/*
 * License (BSD):
 * ==============
 *
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

import static net.miginfocom.layout.ComponentWrapper.TYPE_BUTTON;
import static net.miginfocom.layout.ComponentWrapper.TYPE_CHECK_BOX;
import static net.miginfocom.layout.ComponentWrapper.TYPE_COMBO_BOX;
import static net.miginfocom.layout.ComponentWrapper.TYPE_CONTAINER;
import static net.miginfocom.layout.ComponentWrapper.TYPE_LABEL;
import static net.miginfocom.layout.ComponentWrapper.TYPE_LIST;
import static net.miginfocom.layout.ComponentWrapper.TYPE_PANEL;
import static net.miginfocom.layout.ComponentWrapper.TYPE_SCROLL_PANE;
import static net.miginfocom.layout.ComponentWrapper.TYPE_SLIDER;
import static net.miginfocom.layout.ComponentWrapper.TYPE_SPINNER;
import static net.miginfocom.layout.ComponentWrapper.TYPE_TEXT_FIELD;
import static net.miginfocom.layout.ComponentWrapper.TYPE_UNKNOWN;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Spatial;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.SelectList;
import icetone.controls.lists.Slider;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollArea;
import icetone.controls.text.Label;
import icetone.controls.text.Password;
import icetone.controls.text.TextElement;
import icetone.controls.text.TextField;
import icetone.controls.windows.Panel;
import icetone.controls.windows.Window;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.ContainerWrapper;
import net.miginfocom.layout.Grid;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.LayoutCallback;
import net.miginfocom.layout.LayoutUtil;
import net.miginfocom.layout.PlatformDefaults;

/**
 * A very flexible layout manager.
 * <p>
 * Read the documentation that came with this layout manager for information on
 * usage.
 */
public class MigLayout implements Externalizable, LayoutManager {

	static {
		// Mac Swing defaults to 96 DPI from Java6 though SWT is still on 72.
		if (PlatformDefaults.getPlatform() == PlatformDefaults.MAC_OSX) {
			PlatformDefaults.setDefaultDPI(72);
		}
	}

	// ******** Instance part ********
	/**
	 * The component to string constraints mappings.
	 */
	private final Map<Element, Object> scrConstrMap = new IdentityHashMap<Element, Object>(8);
	private final Map<Element, Object> constraints = new HashMap<Element, Object>();
	/**
	 * Hold the serializable text representation of the constraints.
	 */
	private Object layoutConstraints = "", colConstraints = "", rowConstraints = "";    // Should
																					    // never
																					    // be
																					    // null!
	// ******** Transient part ********
	private transient ContainerWrapper cacheParentW = null;
	private transient final Map<ComponentWrapper, CC> ccMap = new HashMap<ComponentWrapper, CC>(8);
	private transient LC lc = null;
	private transient AC colSpecs = null, rowSpecs = null;
	private transient Grid grid = null;
	private transient java.util.Timer debugTimer = null;
	private transient long curDelay = -1;
	private transient int lastModCount = PlatformDefaults.getModCount();
	private transient int lastHash = -1;
	private transient ArrayList<LayoutCallback> callbackList = null;
	private transient final ElementManager screen;

	/**
	 * Constructor with no constraints and default screen
	 */
	public MigLayout() {
		this(Screen.get());
	}

	/**
	 * Constructor with no constraints.
	 */
	public MigLayout(ElementManager screen) {
		this(screen, "", "", "");
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as "".
	 */
	public MigLayout(String layoutConstraints) {
		this(Screen.get(), layoutConstraints, "", "");
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as "".
	 */
	public MigLayout(ElementManager screen, String layoutConstraints) {
		this(screen, layoutConstraints, "", "");
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as "".
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as "".
	 */
	public MigLayout(String layoutConstraints, String colConstraints) {
		this(Screen.get(), layoutConstraints, colConstraints, "");
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as "".
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as "".
	 */
	public MigLayout(ElementManager screen, String layoutConstraints, String colConstraints) {
		this(screen, layoutConstraints, colConstraints, "");
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as "".
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as "".
	 * @param rowConstraints
	 *            The constraints for the rows in the grid. <code>null</code>
	 *            will be treated as "".
	 */
	public MigLayout(String layoutConstraints, String colConstraints, String rowConstraints) {
		this(Screen.get(), layoutConstraints, colConstraints, rowConstraints);
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as "".
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as "".
	 * @param rowConstraints
	 *            The constraints for the rows in the grid. <code>null</code>
	 *            will be treated as "".
	 */
	public MigLayout(ElementManager screen, String layoutConstraints, String colConstraints, String rowConstraints) {
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);

		this.screen = screen;
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as an empty
	 *            cosntraint.
	 */
	public MigLayout(LC layoutConstraints) {
		this(Screen.get(), layoutConstraints);
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as an empty
	 *            cosntraint.
	 */
	public MigLayout(Screen screen, LC layoutConstraints) {
		this(screen, layoutConstraints, null, null);
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as an empty
	 *            cosntraint.
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as an empty
	 *            constraint.
	 */
	public MigLayout(LC layoutConstraints, AC colConstraints) {
		this(Screen.get(), layoutConstraints, colConstraints);
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as an empty
	 *            cosntraint.
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as an empty
	 *            constraint.
	 */
	public MigLayout(Screen screen, LC layoutConstraints, AC colConstraints) {
		this(screen, layoutConstraints, colConstraints, null);
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as an empty
	 *            cosntraint.
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as an empty
	 *            constraint.
	 * @param rowConstraints
	 *            The constraints for the rows in the grid. <code>null</code>
	 *            will be treated as an empty constraint.
	 */
	public MigLayout(LC layoutConstraints, AC colConstraints, AC rowConstraints) {
		this(Screen.get(), layoutConstraints, colConstraints, rowConstraints);
	}

	/**
	 * Constructor.
	 *
	 * @param layoutConstraints
	 *            The constraints that concern the whole * * * * * * *
	 *            layout. <code>null</code> will be treated as an empty
	 *            cosntraint.
	 * @param colConstraints
	 *            The constraints for the columns in the * * * * * * * * *
	 *            grid. <code>null</code> will be treated as an empty
	 *            constraint.
	 * @param rowConstraints
	 *            The constraints for the rows in the grid. <code>null</code>
	 *            will be treated as an empty constraint.
	 */
	public MigLayout(Screen screen, LC layoutConstraints, AC colConstraints, AC rowConstraints) {
		setLayoutConstraints(layoutConstraints);
		setColumnConstraints(colConstraints);
		setRowConstraints(rowConstraints);

		this.screen = screen;
	}

	/**
	 * Helper to set an element's constraints.
	 *
	 * @param constraints
	 *            contraints
	 */
	public void constrain(Element element, Object constraints) {
		this.constraints.put(element, constraints);
	}

	/**
	 * Helper to remove an element's constraints.
	 *
	 * @param constraints
	 *            contraints
	 */
	public void remove(Element element) {
		constraints.remove(element);
		Object o = scrConstrMap.remove(element);
		if (o instanceof ComponentWrapper) {
			ccMap.remove((ComponentWrapper) o);
		}
	}

	/**
	 * Returns layout constraints either as a
	 * <code>String</code> or {@link net.miginfocom.layout.LC} depending what
	 * was sent in
	 * to the constructor or set with {@link #setLayoutConstraints(Object)}.
	 *
	 * @return The layout constraints eighter as a <code>String</code> or
	 *         {@link net.miginfocom.layout.LC} depending what was sent in to
	 *         the constructor or
	 *         set with {@link #setLayoutConstraints(Object)}. Never
	 *         <code>null</code>.
	 */
	public Object getLayoutConstraints() {
		return layoutConstraints;
	}

	/**
	 * Sets the layout constraints for the layout manager instance as a String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 *
	 * @param s
	 *            The layout constraints as a String representation.
	 *            <code>null</code> is
	 *            converted to <code>""</code> for storage.
	 * @throws RuntimeException
	 *             if the constraint was not valid.
	 */
	public void setLayoutConstraints(Object s) {
		if (s == null || s instanceof String) {
			s = ConstraintParser.prepare((String) s);
			lc = ConstraintParser.parseLayoutConstraint((String) s);
		} else if (s instanceof LC) {
			lc = (LC) s;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + s.getClass().toString());
		}
		layoutConstraints = s;
		grid = null;
	}

	/**
	 * Returns the column layout constraints either as a
	 * <code>String</code> or {@link net.miginfocom.layout.AC}.
	 *
	 * @return The column constraints eighter as a <code>String</code> or
	 *         {@link net.miginfocom.layout.LC} depending what was sent in to
	 *         the constructor or
	 *         set with {@link #setLayoutConstraints(Object)}. Never
	 *         <code>null</code>.
	 */
	public Object getColumnConstraints() {
		return colConstraints;
	}

	/**
	 * Sets the column layout constraints for the layout manager instance as a
	 * String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 *
	 * @param constr
	 *            The column layout constraints as a String * * * * * * *
	 *            representation. <code>null</code> is converted to
	 *            <code>""</code> for storage.
	 * @throws RuntimeException
	 *             if the constraint was not valid.
	 */
	public void setColumnConstraints(Object constr) {
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			colSpecs = ConstraintParser.parseColumnConstraints((String) constr);
		} else if (constr instanceof AC) {
			colSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		colConstraints = constr;
		grid = null;
	}

	/**
	 * Returns the row layout constraints as a String representation. This
	 * string is the
	 * exact string as set with {@link #setRowConstraints(Object)} or sent into
	 * the
	 * constructor.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 *
	 * @return The row layout constraints as a String representation. * * * * *
	 *         * * *
	 *         Never <code>null</code>.
	 */
	public Object getRowConstraints() {
		return rowConstraints;
	}

	/**
	 * Sets the row layout constraints for the layout manager instance as a
	 * String.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 *
	 * @param constr
	 *            The row layout constraints as a String * * * * * * * *
	 *            representation. <code>null</code> is converted to
	 *            <code>""</code> for storage.
	 * @throws RuntimeException
	 *             if the constaint was not valid.
	 */
	public void setRowConstraints(Object constr) {
		if (constr == null || constr instanceof String) {
			constr = ConstraintParser.prepare((String) constr);
			rowSpecs = ConstraintParser.parseRowConstraints((String) constr);
		} else if (constr instanceof AC) {
			rowSpecs = (AC) constr;
		} else {
			throw new IllegalArgumentException("Illegal constraint type: " + constr.getClass().toString());
		}
		rowConstraints = constr;
		grid = null;
	}

	/**
	 * Returns a shallow copy of the constraints map.
	 *
	 * @return A shallow copy of the constraints map. Never <code>null</code>.
	 */
	public Map<Element, Object> getConstraintMap() {
		return new IdentityHashMap<Element, Object>(scrConstrMap);
	}

	/**
	 * Sets the constraints map.
	 *
	 * @param map
	 *            The map. Will be copied.
	 */
	public void setConstraintMap(Map<Element, Object> map) {
		scrConstrMap.clear();
		ccMap.clear();
		for (Map.Entry<Element, Object> e : map.entrySet()) {
			setComponentConstraintsImpl(e.getKey(), e.getValue(), true);
		}
	}

	/**
	 * Sets the component constraint for the component that already must be
	 * handleded by
	 * this layout manager.
	 * <p>
	 * See the class JavaDocs for information on how this string is formatted.
	 *
	 * @param constr
	 *            The component constraints as a String or
	 *            {@link net.miginfocom.layout.CC}. <code>null</code> is ok.
	 * @param comp
	 *            The component to set the constraints for.
	 * @param noCheck
	 *            Doesn't check if control already is managed.
	 * @throws RuntimeException
	 *             if the constaint was not valid.
	 * @throws IllegalArgumentException
	 *             If the component is not handling the component.
	 */
	private void setComponentConstraintsImpl(Element comp, Object constr, boolean noCheck) {
		if (noCheck == false && scrConstrMap.containsKey(comp) == false) {
			throw new IllegalArgumentException("Component must already be added to parent!");
		}

		ComponentWrapper cw = new TonegodGUIComponentWrapper(comp);

		if (constr == null || constr instanceof String) {
			String cStr = ConstraintParser.prepare((String) constr);

			scrConstrMap.put(comp, constr);
			ccMap.put(cw, ConstraintParser.parseComponentConstraint(cStr));

		} else if (constr instanceof CC) {

			scrConstrMap.put(comp, constr);
			ccMap.put(cw, (CC) constr);

		} else {
			throw new IllegalArgumentException("Constraint must be String or ComponentConstraint: " + constr.getClass().toString());
		}
		grid = null;
	}

	/**
	 * Returns if this layout manager is currently managing this component.
	 *
	 * @param c
	 *            The component to check. If <code>null</code> then
	 *            <code>false</code> will
	 *            be returned.
	 * @return If this layout manager is currently managing this component.
	 */
	public boolean isManagingComponent(Element c) {
		return scrConstrMap.containsKey(c);
	}

	/**
	 * Adds the callback function that will be called at different stages of the
	 * layout
	 * cycle.
	 *
	 * @param callback
	 *            The callback. Not <code>null</code>.
	 */
	public void addLayoutCallback(LayoutCallback callback) {
		if (callback == null) {
			throw new NullPointerException();
		}

		if (callbackList == null) {
			callbackList = new ArrayList<LayoutCallback>(1);
		}

		callbackList.add(callback);
	}

	/**
	 * Removes the callback if it exists.
	 *
	 * @param callback
	 *            The callback. May be <code>null</code>.
	 */
	public void removeLayoutCallback(LayoutCallback callback) {
		if (callbackList != null) {
			callbackList.remove(callback);
		}
	}

	/**
	 * Sets the debugging state for this layout manager instance. If debug is
	 * turned on a
	 * timer will repaint the last laid out parent with debug information on
	 * top.
	 * <p>
	 * Red fill and dashed dark red outline is used to indicate occupied cells
	 * in the
	 * grid. Blue dashed outline indicate component bounds set.
	 * <p>
	 * Note that debug can also be set on the layout constraints. There it will
	 * be
	 * persisted. The value set here will not. See the class JavaDocs for
	 * information.
	 *
	 * @param parentW
	 *            The parent. Never <code>null</code>.
	 * @param b
	 *            <code>true</code> means debug is turned on.
	 */
	private synchronized void setDebug(final ComponentWrapper parentW, boolean b) {
		if (b && (debugTimer == null || curDelay != getDebugMillis())) {
			if (debugTimer != null) {
				debugTimer.cancel();
			}

			debugTimer = new Timer(true);
			curDelay = getDebugMillis();
			debugTimer.schedule(new MyDebugRepaintTask(screen, this), curDelay, curDelay);

			ContainerWrapper pCW = parentW.getParent();
			Element parent = pCW != null ? (Element) pCW.getComponent() : null;
			if (parent != null) {
				layout(parent);
			}

		} else if (!b && debugTimer != null) {
			debugTimer.cancel();
			debugTimer = null;
		}
	}

	/**
	 * Returns the current debugging state.
	 *
	 * @return The current debugging state.
	 */
	private boolean getDebug() {
		return debugTimer != null;
	}

	/**
	 * Returns the debug millis. Combines the value from
	 * {@link net.miginfocom.layout.LC#getDebugMillis()} and
	 * {@link net.miginfocom.layout.LUtil#getGlobalDebugMillis()}
	 *
	 * @return The combined value.
	 */
	private int getDebugMillis() {
		int globalDebugMillis = LayoutUtil.getGlobalDebugMillis();
		return globalDebugMillis > 0 ? globalDebugMillis : lc.getDebugMillis();
	}

	/**
	 * Check if something has changed and if so recreate it to the cached
	 * objects.
	 *
	 * @param parent
	 *            The parent that is the target for this layout manager.
	 */
	private void checkCache(Object parent) {
		if (parent == null) {
			return;
		}

		checkConstrMap(parent);

		ContainerWrapper par = checkParent(parent);

		// Check if the grid is valid
		int mc = PlatformDefaults.getModCount();
		if (lastModCount != mc) {
			grid = null;
			lastModCount = mc;
		}

		int hash = parent instanceof Element ? ((Element) parent).getDimensions().hashCode()
				: new Vector2f(((Screen) parent).getWidth(), ((Screen) parent).getHeight()).hashCode();
		for (ComponentWrapper cw : ccMap.keySet()) {
			hash ^= cw.getLayoutHashCode();
			hash += 285134905;
		}

		if (hash != lastHash) {
			grid = null;
			lastHash = hash;
		}

		setDebug(par, getDebugMillis() > 0);

		if (grid == null) {
			grid = new Grid(par, lc, rowSpecs, colSpecs, ccMap, callbackList);
		}
	}

	private boolean checkConstrMap(Object parent) {
		Collection<? extends Spatial> comps = parent instanceof Element ? ((Element) parent).getChildren()
				: ((Screen) parent).getElements();
		boolean changed = comps.size() != scrConstrMap.size();

		if (changed == false) {
			for (Spatial c : comps) {
				if (c instanceof Element) {
					Element el = (Element) c;
					Object layoutData = constraints.get(el);
					if (layoutData != null && layoutData != scrConstrMap.get(el)) {
						changed = true;
						break;
					}
				}
			}
		}

		if (changed) {
			scrConstrMap.clear();
			for (Spatial c : comps) {
				if (c instanceof Element) {
					Element el = (Element) c;
					Object layoutData = constraints.get(el);
					if (layoutData != null) {
						setComponentConstraintsImpl(el, layoutData, true);
					}
				}
			}
		}
		return changed;
	}

	private ContainerWrapper checkParent(Object parent) {
		if (parent == null) {
			return null;
		}

		if (cacheParentW == null || cacheParentW.getComponent() != parent) {
			if (parent instanceof Element) {
				cacheParentW = new TonegodGUIContainerWrapper((Element) parent);
			} else {
				cacheParentW = new TonegodGUIScreenWrapper((Screen) parent);
			}
		}

		return cacheParentW;
	}

	public Vector2f minimumSize(Element parent) {
		return getSizeImpl(parent, LayoutUtil.MIN).add(new Vector2f(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y, parent.getTextPaddingVec().w + parent.getTextPaddingVec().z));
	}

	public Vector2f preferredSize(Element parent) {
		return getSizeImpl(parent, LayoutUtil.PREF).add(new Vector2f(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y, parent.getTextPaddingVec().w + parent.getTextPaddingVec().z));
	}

	public Vector2f maximumSize(Element parent) {
		return getSizeImpl(parent, LayoutUtil.MAX).add(new Vector2f(parent.getTextPaddingVec().x + parent.getTextPaddingVec().y, parent.getTextPaddingVec().w + parent.getTextPaddingVec().z));
	}

	public float getLayoutAlignmentX(Element parent) {
		return lc != null && lc.getAlignX() != null ? lc.getAlignX().getPixels(1, checkParent(parent), null) : 0;
	}

	public float getLayoutAlignmentY(Element parent) {
		return lc != null && lc.getAlignY() != null ? lc.getAlignY().getPixels(1, checkParent(parent), null) : 0;
	}

	public Vector2f computeSize(Element parent, int wHint, int hHint, boolean flushCache) {
		checkCache(parent);

		int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, LayoutUtil.PREF);
		int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, LayoutUtil.PREF);

		return new Vector2f(w, h);
	}

	public void layoutScreen(ElementManager screen) {
		// Do this so the size is good
		checkCache(screen);
		Vector2f sz = new Vector2f(screen.getWidth(), screen.getHeight());
		int[] b = new int[] { 0, 0, (int) sz.x, (int) sz.y };
		final boolean layoutAgain = grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug(), true);
		if (layoutAgain) {
			grid = null;
			checkCache(screen);
			grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug(), false);
		}
		LUtil.layoutChildren((Screen) screen);
	}

	protected void layout(Element parent, boolean flushCache) {
		// Do this so the size is good
		if (flushCache) {
			grid = null;
		}

		checkCache(parent);
		Vector2f sz = parent.getDimensions();
		Vector4f padding = parent.getTextPaddingVec();
		int[] b = new int[] { (int) padding.x, (int) padding.z, (int) sz.x - (int) padding.x - (int) padding.y,
				(int) sz.y - (int) padding.z - (int) padding.w };
		final boolean layoutAgain = grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug(), true);
		if (layoutAgain) {
			grid = null;
			checkCache(parent);
			grid.layout(b, lc.getAlignX(), lc.getAlignY(), getDebug(), false);
		}
		LUtil.layoutChildren(parent);
	}

	protected boolean flushCache(Element control) {
		grid = null;
		return true;
	}

	// ************************************************
	// Persistence Delegate and Serializable combined.
	// ************************************************
	private Object readResolve() throws ObjectStreamException {
		return LayoutUtil.getSerializedObject(this);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(in));
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if (getClass() == MigLayout.class) {
			LayoutUtil.writeAsXML(out, this);
		}
	}

	private Vector2f getSizeImpl(Element parent, int sizeType) {
		checkCache(parent);
		Vector4f padding = parent.getTextClipPaddingVec();
		int w = LayoutUtil.getSizeSafe(grid != null ? grid.getWidth() : null, sizeType) + (int) padding.x + (int) padding.y;
		int h = LayoutUtil.getSizeSafe(grid != null ? grid.getHeight() : null, sizeType) + (int) padding.z + (int) padding.w;
		return new Vector2f(w, h);
	}

	private static class MyDebugRepaintTask extends TimerTask {

		private final WeakReference<MigLayout> layoutRef;
		private final ElementManager screen;

		private MyDebugRepaintTask(ElementManager screen, MigLayout layout) {
			this.layoutRef = new WeakReference<MigLayout>(layout);
			this.screen = screen;
		}

		public void run() {
			final MigLayout layout = layoutRef.get();
			if (layout != null && layout.grid != null) {
				screen.getApplication().enqueue(new Callable<Void>() {
					public Void call() throws Exception {
						if (layout.grid != null) {
							layout.grid.paintDebug();
						}
						return null;
					}
				});
			}
		}
	}

	/**
	 * Layout the element.
	 *
	 * @param element
	 *            element to layout
	 */
	public void layout(Element element) {
		layout(element, true);
	}

	static int checkType(Object wrappedComponent) {
		if (wrappedComponent instanceof TextField || wrappedComponent instanceof Password) {
			// TODO not sure about TextArea
			return TYPE_TEXT_FIELD;
		} else if (wrappedComponent instanceof Label || wrappedComponent instanceof TextElement) {
			return TYPE_LABEL;
		} else if (wrappedComponent instanceof CheckBox) {
			return TYPE_CHECK_BOX;
		} else if (wrappedComponent instanceof Button) {
			return TYPE_BUTTON;
		} else if (wrappedComponent instanceof ComboBox) {
			return TYPE_COMBO_BOX;
		} else if (wrappedComponent instanceof ScrollArea) {
			return TYPE_SCROLL_PANE;
		} else if (wrappedComponent instanceof Panel) {
			return TYPE_PANEL;
		} else if (wrappedComponent instanceof SelectList) {
			return TYPE_LIST;
		} else if (wrappedComponent instanceof Spinner) {
			return TYPE_SPINNER;
		} // else if (c instanceof ProgressBar) {
			 // return TYPE_PROGRESS_BAR;
			 // }
		else if (wrappedComponent instanceof Slider || wrappedComponent instanceof Slider) {
			return TYPE_SLIDER;
		} else if (wrappedComponent instanceof Panel || wrappedComponent instanceof Window || wrappedComponent instanceof Element) {    // only
																																	    // AWT
																																	    // components
																																	    // is
																																	    // not
																																	    // containers.
			return TYPE_CONTAINER;
		}
		return TYPE_UNKNOWN;
	}
}