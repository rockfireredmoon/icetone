/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2017, Emerald Icemoon
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.lwjgl.opengl.Display;
import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.app.Application;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;

import icetone.controls.lists.ComboBox;
import icetone.controls.menuing.AutoHide;
import icetone.controls.util.ModalBackground;
import icetone.core.Layout.LayoutType;
import icetone.core.event.FlingListener;
import icetone.core.event.ScreenEvent;
import icetone.core.event.ScreenEvent.Type;
import icetone.core.event.ScreenEventListener;
import icetone.core.event.ScreenEventSupport;
import icetone.core.event.TouchListener;
import icetone.core.event.keyboard.KeyboardFocusEvent;
import icetone.core.event.keyboard.KeyboardFocusEvent.KeyboardFocusEventType;
import icetone.core.event.keyboard.KeyboardFocusListener;
import icetone.core.event.keyboard.KeyboardFocusSupport;
import icetone.core.event.keyboard.KeyboardUIEvent;
import icetone.core.event.mouse.HoverEvent;
import icetone.core.event.mouse.HoverEvent.HoverEventType;
import icetone.core.event.mouse.HoverListener;
import icetone.core.event.mouse.MouseButtonListener;
import icetone.core.event.mouse.MouseButtonSupport;
import icetone.core.event.mouse.MouseMovementListener;
import icetone.core.event.mouse.MouseMovementSupport;
import icetone.core.event.mouse.MouseUIButtonEvent;
import icetone.core.event.mouse.MouseUIMotionEvent;
import icetone.core.event.mouse.MouseUIWheelEvent;
import icetone.core.event.mouse.MouseUIWheelListener;
import icetone.core.event.mouse.MouseWheelSupport;
import icetone.core.layout.ScreenLayout;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.loader.DefaultLayoutContent;
import icetone.core.layout.loader.LayoutAssetKey;
import icetone.core.layout.loader.LayoutContext;
import icetone.core.layout.loader.LayoutPart;
import icetone.core.scene.DefaultSceneElement;
import icetone.core.utils.Alarm.AlarmTask;
import icetone.css.StyleManager.CursorType;
import icetone.css.StyleManager.ThemeInstance;
import icetone.effects.EffectManager;
import icetone.text.FontSpec;
import icetone.text.TextStyle;

/**
 *
 * @author t0neg0d
 */
public class BaseScreen implements Control, ElementContainer<BaseScreen, UIEventTarget>, RawInputListener {
	final static Logger LOG = Logger.getLogger(BaseScreen.class.getName());

	public final class ZComparator implements Comparator<BaseElement> {

		@Override
		public int compare(BaseElement o1, BaseElement o2) {
			return Float.valueOf(o1.getLocalTranslation().z).compareTo(o2.getLocalTranslation().z);
		}
	}

	public enum EventCheckType {
		Fling, MouseFocus, MouseLeft, MouseMovement, MouseRight, None, Touch, TouchMove, WheelClick, WheelMove, Drag
	}

	private static BaseScreen defaultInstance;

	private static boolean singleScreensInUse;

	public static BaseScreen get() {
		if (defaultInstance == null)
			throw new IllegalStateException("Not inited.");
		singleScreensInUse = true;
		return defaultInstance;
	}

	public static BaseScreen init(Application app) {
		if (defaultInstance != null)
			throw new IllegalStateException("Already inited.");
		defaultInstance = new Screen(app);
		return defaultInstance;
	}

	protected EffectManager effectManager;
	protected MouseButtonSupport<UIEventTarget> mouseButtonSupport;
	protected MouseMovementSupport<UIEventTarget> mouseMovementSupport;
	protected MouseWheelSupport<UIEventTarget> mouseWheelSupport;
	protected Spatial spatial;
	protected CollisionResults results;
	protected BaseElement eventElement = null;
	protected boolean mousePressed = false;
	protected int keyboardModifiers;
	protected EventCaster eventCaster;
	protected float elementAlpha;

	protected Node t0neg0dGUI = new Node("t0neg0dGUI");
	// Android input scaling
	float inputScale = 1;
	boolean orDim = false;
	float orWidth, orHeight;

	private Texture atlasTexture;
	private List<BaseElement> childList = new CopyOnWriteArrayList<BaseElement>();
	// SubScreen collisions
	private Vector2f click2d = new Vector2f(), tempV2 = new Vector2f();
	private Vector3f click3d = new Vector3f(), pickDir = new Vector3f();
	private int clickCount;
	private Map<Integer, BaseElement> contactElements = new HashMap<>();
	private CursorType currentCursor;
	private DefaultFocusCycle defaultFocusCycle;
	private Set<LayoutType> dirty = new HashSet<>();
	private Map<Integer, Vector2f> elementOffsets = new HashMap<>();
	private Ray elementZOrderRay = new Ray();
	// AnimLayer & 2D framework support
	// private Map<String, AnimLayer> layers = new LinkedHashMap();
	private float eventElementOffsetX = 0;
	private float eventElementOffsetY = 0;
	private Vector2f eventElementOriginXY = new Vector2f();
	private Borders eventElementResizeDirection = null;
	private Map<Integer, Borders> eventElementResizeDirections = new HashMap<>();
	private Map<Integer, BaseElement> eventElements = new HashMap<>();
	private Node eventNode = null;
	private Map<Integer, Node> eventNodes = new HashMap<>();
	private boolean focusElementIsMovable = false;
	private FocusCycle focusForm = null;
	private boolean forceCursor = false;
	private float globalAlpha = 1.0f;
	private Vector3f guiRayOrigin = new Vector3f();
	private BaseElement keyboardFocus = null;
	private CollisionResult lastCollision;
	private boolean layingOut;
	private int layoutCounter;
	private Layout<?, ?> layoutManager = new ScreenLayout();
	private ElementQuadGrid mesh;
	private List<BaseElement> modal = new ArrayList<>();
	private ModalBackground modalBackground;
	private boolean mouseButtonsEnabled = true;
	private BaseElement mouseFocusElement = null;
	private Node mouseFocusNode = null;
	private boolean mouseLeftPressed = false;
	private BaseElement mouseWheelElement = null;
	private Vector2f mouseXY = new Vector2f(0, 0);
	private Ray pickRay = new Ray();
	private BaseElement previousMouseFocusElement = null;
	private Node previousMouseFocusNode = null;
	// New z-ordering
	private Set<TextStyle> textStyles;

	private boolean snapToPixel = true;
	// CSS
	private List<Stylesheet> stylesheets = new ArrayList<>();
	private List<Stylesheet> allStylesheets = new ArrayList<>();
	private BaseElement targetElement = null;
	private float targetElementOffsetX = 0;
	private float targetElementOffsetY = 0;
	private Vector2f tempElementOffset = new Vector2f();
	private ToolTipManager toolTipManager;
	private Vector2f touchXY = new Vector2f(0, 0);
	private float uiAudioVolume = 1;
	// 3D scene support
	private boolean use3DSceneSupport = false;
	private boolean useMultiTouch = false;
	private boolean useTextureAtlas = true;
	private boolean useUIAudio = false;
	private KeyboardFocusSupport keyboardFocusSupport;
	protected ScreenEventSupport screenEventSupport;
	private CursorType defaultCursor;
	private ThemeInstance themeInstance;
	protected FontSpec font;
	protected ColorRGBA fontColor = ColorRGBA.White;
	private boolean inheritsStyles;
	private AlarmTask clickResetTask;
	private int lastClickX = -1;
	private int lastClickY = -1;

	protected BaseScreen() {
		this(ToolKit.get().getApplication());
	}

	/**
	 * Creates a new instance of the Screen control using the default style
	 * information provided with the library.
	 * 
	 * @param app A JME Application
	 */
	@Deprecated
	BaseScreen(Application app) {
		this(app, -1, -1);
	}

	protected BaseScreen(float width, float height) {
		this(ToolKit.get().getApplication(), width, height);
	}

	@Deprecated
	BaseScreen(Application app, float width, float height) {

		if (singleScreensInUse) {
			throw new IllegalStateException(String.format(
					"Cannot construct a 2nd %s instance once the first has been used for an Element's screenless constructor.",
					BaseScreen.class));
		}

		if (defaultInstance == null) {
			if (!ToolKit.isInited())
				ToolKit.init(app);
			defaultInstance = this;
		}

		if (width > -1 && height > -1) {
			this.orWidth = width;
			this.orHeight = height;
			this.orDim = true;
			this.inputScale = 1f / (app.getViewPort().getCamera().getWidth() / width);
		}

		this.elementZOrderRay.setDirection(Vector3f.UNIT_Z);
		this.results = new CollisionResults();
		try {
		} catch (Exception ex) {
		}

		preConfigureScreen();

		eventCaster = new EventCaster();
		effectManager = new EffectManager(this);
		defaultFocusCycle = new DefaultFocusCycle(this);
		app.getInputManager().addRawInputListener(this);

		if (app instanceof SimpleApplication) {
			((SimpleApplication) app).getGuiNode().addControl(this);

			/*
			 * Without doing this, we lose the initial mouse pointer, as the FlyCamAppState
			 * will hide the pointer after the screen has been initialised. If an
			 * application doesn't want this, just set dragToRotate back to the desired
			 * value later in initialisation.
			 */
			FlyCamAppState fas = app.getStateManager().getState(FlyCamAppState.class);
			if (fas != null) {
				fas.getCamera().setDragToRotate(true);
			}
		}

		configureScreen();
		setToolTipManager(new ToolTipManager());
		setThemeInstance(ToolKit.get().getStyleManager().getDefaultInstance());
	}

	public BaseScreen addTextStyles(TextStyle... textStyles) {
		if (this.textStyles == null)
			this.textStyles = new LinkedHashSet<>();
		for (TextStyle t : textStyles) {
			this.textStyles.add(t);
		}
		dirtyLayout(false, LayoutType.text);
		layoutChildren();
		return this;
	}

	public Set<TextStyle> getTextStyles() {
		return textStyles;
	}

	/**
	 * Adds an Element to the Screen and scene graph. The element will be added
	 * <b>immediately</b>, and no show events will be fired. If you want events
	 * (usually you do), then use {@link #showElement()} instead.
	 * 
	 * @param element The Element to add
	 */

	@Override
	public BaseScreen addElement(BaseElement element) {
		addElement(element, null, false);
		return this;
	}

	/**
	 * Adds an Element to the Screen and scene graph
	 * 
	 * @param element The Element to add
	 */

	@Override
	public BaseScreen attachElement(BaseElement element) {
		addElement(element, null, true);
		return this;
	}

	/**
	 * Adds an Element to the Screen and scene graph
	 * 
	 * @param element The Element to add
	 */

	@Override
	public BaseScreen addElement(BaseElement element, Object constraints) {
		return addElement(element, constraints, false);
	}

	public BaseScreen removeAllChildren() {
		if (!childList.isEmpty()) {
			for (BaseElement e : childList) {
				e.elementParent = null;
				e.detachFromParent();
				e.cleanup();
			}
			for (BaseElement e : childList) {
				layoutManager.remove(e);
			}
			childList.clear();
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	protected BaseScreen addElement(BaseElement element, Object constraints, boolean hide) {

		if (constraints == null)
			constraints = element.getConstraints();

		if (element instanceof AutoHide)
			hide = true;

		if (childList.contains(element)) {
			throw new IllegalArgumentException(String.format(
					"The child element '%s' (class: %s, str: %s, hash: %s) is already added to the screen.",
					element.getStyleId(), element.getClass(), element.toString(), element.hashCode(), this));

		} else {
			if (element.isModal()) {
				if (modal.contains(element))
					throw new IllegalStateException(String.format("%s is already modal.", element));
				if (element.getPriority() != ZPriority.NORMAL) {
					throw new IllegalStateException(
							String.format("May only show elements of priority %s as modal.", ZPriority.NORMAL));
				}
				if (modal.isEmpty()) {
					initModalBackground();
				}
				modal.add(element);
				// updateZOrder(element);
			}

			// New z-ordering
			synchronized (childList) {
				childList.add(element);
			}

			if (!element.isInitialized()) {
				initializeElement(element);
			}

			if (!hide) {
				t0neg0dGUI.attachChild(element);
			} else {
				element.setVisibleState(false);
			}
		}

		if (layoutManager != null) {
			((Layout<BaseScreen, Object>) layoutManager).constrain(element, constraints);
		}
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;

	}

	@Override
	public BaseScreen addMouseButtonListener(MouseButtonListener<UIEventTarget> l) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport<UIEventTarget>();
		mouseButtonSupport.addListener(l);
		return this;
	}

	@Override
	public BaseScreen addMouseMovementListener(MouseMovementListener<UIEventTarget> l) {
		if (mouseMovementSupport == null)
			mouseMovementSupport = new MouseMovementSupport<UIEventTarget>();
		mouseMovementSupport.addListener(l);
		return this;
	}

	@Override
	public BaseScreen addMouseWheelListener(MouseUIWheelListener<UIEventTarget> l) {
		if (mouseWheelSupport == null)
			mouseWheelSupport = new MouseWheelSupport<UIEventTarget>();
		mouseWheelSupport.addListener(l);
		return this;
	}

	public BaseScreen addKeyboardFocusListener(KeyboardFocusListener l) {
		if (keyboardFocusSupport == null)
			keyboardFocusSupport = new KeyboardFocusSupport();
		keyboardFocusSupport.addListener(l);
		return this;
	}

	public void addStylesheet(Stylesheet sheet) {
		stylesheets.add(sheet);
		dirtyLayout(true, LayoutType.reset);
		layoutChildren();
	};

	@Override
	public void applyZOrder() {
		float zi = (float) Integer.MAX_VALUE / (childList.size() + 1);
		float z = zi;

		List<BaseElement> sorted = new ArrayList<>(childList);

		/*
		 * If modal background is visible, it sits just below the topmost NORMAL
		 */
		if (modalBackground != null && modalBackground.isVisible() && modalBackground.getParent() != null
				&& sorted.size() > 1) {
			int topMost = 0;
			for (int i = 0; i < sorted.size(); i++) {
				if (ZPriority.NORMAL == sorted.get(i).getPriority()) {
					topMost = i;
				}
			}
			sorted.remove(modalBackground);
			sorted.add(topMost - 1, modalBackground);
		}

		Collections.sort(sorted, new ZOrderComparator(childList));

		for (BaseElement c : sorted) {
			c.setZStep(zi);
			c.setLocalTranslation(c.getLocalTranslation().setZ(z));
			c.applyZOrder();
			z += zi;
		}
	}

	@Override
	public void beginInput() {
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		BaseScreen screen = new BaseScreen();
		synchronized (screen.childList) {
			screen.childList.addAll(this.childList);
		}
		return screen;
	}

	public Texture createNewTexture(String texturePath) {
		Texture newTex = getApplication().getAssetManager().loadTexture(texturePath);
		newTex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		newTex.setMagFilter(Texture.MagFilter.Bilinear);
		newTex.setWrap(Texture.WrapMode.Clamp);
		return newTex;
	}

	@Override
	public void dirtyLayout(boolean doChildren, LayoutType... layoutType) {
		// doChildren = true;
		// layoutType = new LayoutType[] { LayoutType.all };
		if (layoutType.length == 0)
			dirty.add(LayoutType.all);
		else
			for (LayoutType t : layoutType)
				dirty.add(t);

		// Normally each child should dirty it's own layout when it actually
		// changes (e.g. by parent layout manager)
		if (doChildren) {
			// System.err.println("NOTE: A component requests dirtying of
			// children. This is inefficient. The component should be fixed
			// to use LUtil methods to change bounds so layout is
			// automatically changed, or other mechanisms.");
			for (BaseElement e : childList)
				e.dirtyLayout(doChildren, layoutType);
		}
	}

	@Override
	public void endInput() {
	}

	public void forceEventElement(BaseElement element) {
		float x = element.getAbsoluteX() + 1;
		float y = element.getAbsoluteY() + 1;
		eventElement = getEventElement(x, y, EventCheckType.None);
		if (eventElement != null) {
			if (eventElement.getAbsoluteParent().isAffectZOrder())
				updateZOrder(eventElement.getAbsoluteParent());
			this.setKeyboardFocus(eventElement);
			if (eventElement.isDraggable())
				targetElement = null;
			if (eventElement.isResizable()) {
				resetKeyboardFocus(null);
			} else if (eventElement.isDraggable() && eventElementResizeDirection == null) {
				eventElementResizeDirection = null;
				resetKeyboardFocus(null);
				eventElementOriginXY.set(eventElement.getPixelPosition());
			} else if (eventElement.isKeyboardFocusable()) {
				setKeyboardFocus(eventElement);
			} else {
				eventElementResizeDirection = null;
				resetKeyboardFocus(null);
			}
		}
	}

	@Override
	public Vector2f getAbsolute() {
		return Vector2f.ZERO;
	}

	@Override
	public Vector4f getAllPadding() {
		return Vector4f.ZERO;
	}

	public Texture getAtlasTexture() {
		return atlasTexture;
	}

	public int getClickCount() {
		return clickCount;
	}

	public ElementQuadGrid getDefaultMesh() {
		return mesh;
	}

	@Override
	public Vector2f getDimensions() {
		return new Vector2f(getWidth(), getHeight());
	}

	/**
	 * Returns the current Drag enabled Element
	 * 
	 * @return Element
	 */

	public BaseElement getDragElement() {
		return this.eventElement;
	}

	/**
	 * Returns the current Drop enabled Element
	 * 
	 * @return Element
	 */

	public BaseElement getDropElement() {
		return this.targetElement;
	}

	/**
	 * Returns the current Drop Element offset
	 * 
	 * @return Element
	 */

	public Vector2f getDropElementOffset() {
		return new Vector2f(targetElementOffsetX, targetElementOffsetY);
	}
	/*
	 * public void getAnimEventTargets(float x, float y) { guiRayOrigin.set(x, y,
	 * 0f);
	 * 
	 * elementZOrderRay.setOrigin(guiRayOrigin); results.clear();
	 * 
	 * t0neg0dGUI.collideWith(elementZOrderRay, results);
	 * 
	 * lastCollision = results.getClosestCollision();
	 * 
	 * eventAnimElement = null; eventQuad = null; for (CollisionResult result :
	 * results) { boolean discard = false; if (!discard) { if
	 * (result.getGeometry().getParent() instanceof AnimElement) { eventAnimElement
	 * = (AnimElement)result.getGeometry().getParent(); if
	 * (!eventAnimElement.getIgnoreMouse()) { eventAnimOffsetX =
	 * x-eventAnimElement.getPositionX(); eventAnimOffsetY =
	 * y-eventAnimElement.getPositionY(); eventQuad =
	 * eventAnimElement.getQuad((int)FastMath.floor(result.getTriangleIndex()/2) );
	 * eventQuadOffsetX = x-eventQuad.getPositionX(); eventQuadOffsetY =
	 * y-eventQuad.getPositionY(); break; } else { eventAnimElement = null;
	 * eventQuad = null; } } } } }
	 */

	// </editor-fold>

	/**
	 * Returns a pointer to the EffectManager
	 * 
	 * @return EffectManager effectManager
	 */

	public EffectManager getEffectManager() {
		return this.effectManager;
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseElement> T getElementByClass(Class<T> type) {
		for (BaseElement el : getElements()) {
			if (el.getClass().equals(type)) {
				return (T) el;
			}
		}
		return null;
	}

	public BaseElement getElementByStyleId(String styleId) {
		BaseElement ret = null;
		synchronized (childList) {
			for (BaseElement el : childList) {
				if (styleId.equals(el.getStyleId())) {
					ret = el;
					break;
				} else {
					ret = el.getElementByStyleId(styleId);
					if (ret != null) {
						break;
					}
				}
			}
		}
		return ret;
	}

	public List<BaseElement> getElementList() {
		// New z-ordering
		return Collections.unmodifiableList(childList);
	}

	// </editor-fold>

	/**
	 * Returns the screen level elements as a Collection
	 * 
	 * @return
	 */

	@Override
	public List<BaseElement> getElements() {
		return childList;
	}

	public EventCaster getEventCaster() {
		return eventCaster;
	}

	@Override
	public FocusCycle getFocusCycle() {
		return defaultFocusCycle;
	}

	/**
	 * Returns the current value of global alpha
	 * 
	 * @return float
	 */

	public float getGlobalAlpha() {
		return this.globalAlpha;
	}

	/**
	 * Returns the guiNode used by the Screen
	 * 
	 * @return Node
	 */

	public Node getGUINode() {
		return t0neg0dGUI;
	}
	// </editor-fold>

	/**
	 * Returns the height of the current Viewport
	 * 
	 * @return float height
	 */

	@Override
	public float getHeight() {
		return (orDim) ? orHeight : getApplication().getViewPort().getCamera().getHeight();
	}

	@Override
	public float getIndent() {
		return 0;
	}

	/**
	 * Returns the current tab focus element
	 */

	public BaseElement getKeyboardFocus() {
		return this.keyboardFocus;
	}

	public CollisionResult getLastCollision() {
		return lastCollision;
	}
	// </editor-fold>

	@Override
	public long getLayoutCounter() {
		return layoutCounter;
	}

	@Override
	public Layout<?, ?> getLayoutManager() {
		return layoutManager;
	}

	@Override
	public Vector4f getMargin() {
		return Vector4f.ZERO;
	}

	/**
	 * Get the topmost current global modal element.
	 */

	public BaseElement getModalElement() {
		List<BaseElement> l = getModalElements();
		return l.isEmpty() ? null : l.get(0);
	}

	public List<BaseElement> getModalElements() {
		List<BaseElement> l = new ArrayList<BaseElement>(modal);
		Collections.sort(l, new ZComparator());
		return l;
	}

	/**
	 * Returns the current mouse focus Element
	 * 
	 * @return Element
	 */

	public BaseElement getMouseFocusElement() {
		return this.mouseFocusElement;
	}

	/**
	 * Returns a Vector2f containing the last stored mouse X/Y coords
	 * 
	 * @return Vector2f mouseXY
	 */

	public Vector2f getMouseXY() {
		return this.mouseXY;
	}

	@Override
	public ElementContainer<?, ?> getParentContainer() {
		return null;
	}

	@Override
	public Vector2f getPixelPosition() {
		return Vector2f.ZERO;
	}

	@Override
	public Position getPosition() {
		return Position.TOP_LEFT;
	}

	public Collection<Stylesheet> getStylesheets() {
		return Collections.unmodifiableCollection(allStylesheets);
	}

	@Override
	public Vector4f getTextPadding() {
		return Vector4f.ZERO;
	}

	public ToolTipManager getToolTipManager() {
		return toolTipManager;
	}

	@Override
	public Vector2f getTotalPadding() {
		return Vector2f.ZERO;
	}

	@Override
	public Vector2f getTotalPaddingOffset() {
		return new Vector2f(0, 0);
	}

	public Vector2f getTouchXY() {
		return this.touchXY;
	}

	/**
	 * Gets the current global UI Audio volume
	 * 
	 * @return float
	 */
	public float getUIAudioVolume() {
		return this.uiAudioVolume;
	}

	public boolean getUseTextureAtlas() {
		return this.useTextureAtlas;
	}

	/**
	 * Returns if the UI Audio option is enabled/disabled
	 * 
	 * @return boolean
	 */

	public boolean getUseUIAudio() {
		return this.useUIAudio;
	}

	/**
	 * Return the width of the current Viewport
	 * 
	 * @return float width
	 */

	@Override
	public float getWidth() {
		return (orDim) ? orWidth : getApplication().getViewPort().getCamera().getWidth();
	}

	public void handleAndroidMenuState(BaseElement target) {
		synchronized (childList) {
			if (target == null) {
				for (BaseElement el : childList) {
					if (el instanceof AutoHide) {
						el.hide();
					}
				}
			} else {
				if (!(target.getAbsoluteParent() instanceof AutoHide) && !(target.getParent() instanceof ComboBox)) {
					for (BaseElement el : childList) {
						if (el instanceof AutoHide) {
							el.hide();
						}
					}
				} else if (target.getAbsoluteParent() instanceof AutoHide) {
					for (BaseElement el : childList) {
						if (el instanceof AutoHide && el != target.getAbsoluteParent()) {
							el.hide();
						}
					}
				} else if (target.getParent() instanceof ComboBox) {
					for (BaseElement el : childList) {
						if (el instanceof AutoHide && el != ((ComboBox<?>) target.getParent()).getMenu()) {
							el.hide();
						}
					}
				}
			}
		}
	}

	void hideModalBackground() {
		if (modalBackground != null) {
			modalBackground.setDestroyOnHide(true);
			modalBackground.hide();
			modalBackground = null;
		}
	}

	@Override
	public boolean isKeyboardFocusRoot() {
		return true;
	}

	@Override
	public BaseScreen getScreen() {
		return this;
	}

	public boolean isMouseButtonsEnabled() {
		return mouseButtonsEnabled;
	}

	public boolean isSnapToPixel() {
		return snapToPixel;
	}

	@Override
	public final void layoutChildren() {
		if (layingOut) {
			return;
		}

		layingOut = true;
		layoutCounter++;
		onBeforeLayout();
		try {
			layoutThis();
			layoutHeirarchy(null);
		} finally {
			onAfterLayout();
			layingOut = false;
		}

	}

	protected void onAfterLayout() {
	}

	protected void onBeforeLayout() {
	}

	@SuppressWarnings("unchecked")
	protected void layoutThis() {
		while (layoutManager != null && !dirty.isEmpty()) {
			List<LayoutType> d = new ArrayList<>(dirty);
			Collections.sort(d);
			dirty.clear();
			if (d.contains(LayoutType.all)) {
				((Layout<ElementContainer<?, ?>, ?>) layoutManager).layout(this, LayoutType.all);

				/*
				 * Only styling should cause dirtying of most types, and this is done first,
				 * followed by the individual layout types, so nothing should actually be dirty
				 * at this point
				 */
				dirty.clear();
			} else {
				if (d.contains(LayoutType.styling) && d.contains(LayoutType.reset))
					d.remove(LayoutType.styling);

				/*
				 * Special case for screens. If doing a reset, then rebuild the list of all
				 * stylesheets
				 */
				if (d.contains(LayoutType.reset)) {
					allStylesheets.clear();
					if (themeInstance != null)
						allStylesheets.addAll(themeInstance.getStylesheets());
					allStylesheets.addAll(stylesheets);
				}

				while (d.size() > 0) {
					LayoutType type = d.remove(0);
					((Layout<ElementContainer<?, ?>, ?>) layoutManager).layout(this, type);

					/*
					 * If anything was dirtied that we are going to process anyway, remove it from
					 * the list now so it doesn't get done twice needlessly
					 */
					d.removeAll(dirty);

				}
			}
		}
	}

	public BaseElement loadFromLayout(String layoutPath) {
		LayoutAssetKey loader = new LayoutAssetKey(this, layoutPath);
		LayoutPart<?> loadAsset = getApplication().getAssetManager().loadAsset(loader);
		return (BaseElement) loadAsset.createPart(this, new DefaultLayoutContent(this));
	}

	public BaseElement loadFromLayout(String layoutPath, LayoutContext ctx) {
		LayoutAssetKey loader = new LayoutAssetKey(this, layoutPath);
		ctx.init(loader);
		LayoutPart<?> ast = getApplication().getAssetManager().loadAsset(loader);
		BaseElement part = (BaseElement) ast.createPart(this, ctx);
		Collection<Stylesheet> sheets = ctx.getStylesheets();
		if (sheets != null)
			for (Stylesheet ss : sheets)
				if (part instanceof Element)
					((Element) part).addStylesheet(ss);
		ctx.end(part);
		return part;
	}

	@Override
	public void onJoyAxisEvent(JoyAxisEvent evt) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onJoyButtonEvent(JoyButtonEvent evt) {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onKeyEvent(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			if (evt.isPressed())
				keyboardModifiers |= KeyboardUIEvent.SHIFT_MASK;
			else
				keyboardModifiers &= ~KeyboardUIEvent.SHIFT_MASK;
		}
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			if (evt.isPressed())
				keyboardModifiers |= KeyboardUIEvent.CTRL_MASK;
			else
				keyboardModifiers &= ~KeyboardUIEvent.CTRL_MASK;
		}
		if (evt.getKeyCode() == KeyInput.KEY_LMENU || evt.getKeyCode() == KeyInput.KEY_RMENU) {
			if (evt.isPressed())
				keyboardModifiers |= KeyboardUIEvent.ALT_MASK;
			else
				keyboardModifiers &= ~KeyboardUIEvent.ALT_MASK;
		}

		if (keyboardFocus != null) {
			if (keyboardFocus.getParent() != null && keyboardFocus.isVisible()) {
				KeyboardUIEvent kevt = new KeyboardUIEvent(evt, keyboardModifiers, keyboardFocus);
				keyboardFocus.keyEvent(kevt);
				if (kevt.isConsumed()) {
					evt.setConsumed();
					return;
				}
			}

			if (evt.isPressed() && evt.getKeyCode() == KeyInput.KEY_ESCAPE) {
				resetKeyboardFocus(null);
				evt.setConsumed();
			}
		}

		if (evt.getKeyCode() == KeyInput.KEY_TAB && evt.isPressed()) {
			if (focusForm != null) {
				if ((keyboardModifiers & KeyboardUIEvent.SHIFT_MASK) == 0)
					focusForm.tabNext();
				else
					focusForm.tabPrev();
			}
		}
	}

	@Override
	public void onMouseButtonEvent(MouseButtonEvent evt) {
		if (!useMultiTouch && mouseButtonsEnabled) {

			setMouseXY(evt.getX(), getHeight() - evt.getY());

			// Create a new event with a flipped Y
			final MouseButtonEvent origEvt = evt;
			evt = new MouseButtonEvent(evt.getButtonIndex(), evt.isPressed(), evt.getX(),
					(int) (getHeight() - evt.getY())) {

				@Override
				public void setConsumed() {
					super.setConsumed();
					origEvt.setConsumed();
				}

			};
			evt.setTime(evt.getTime());

			EventCheckType check = null;
			if (evt.getButtonIndex() == MouseUIButtonEvent.LEFT)
				check = EventCheckType.MouseLeft;
			else if (evt.getButtonIndex() == MouseUIButtonEvent.RIGHT)
				check = EventCheckType.MouseRight;
			else
				check = EventCheckType.WheelClick;

			if (evt.isPressed()) {
				mousePressed = true;

				/*
				 * Track click count using a timer task. Also, if the next click is more than a
				 * certain distance from the first click, the count will be reset
				 */
				float dh = ToolKit.get().getConfiguration().getDoubleClickDistanceThreshold();
				if (lastClickX != -1 && (evt.getX() < lastClickX - dh || evt.getX() > lastClickX + dh
						|| evt.getY() < lastClickY - dh || evt.getY() > lastClickY + dh)) {
					clickCount = 0;
				}

				lastClickX = evt.getX();
				lastClickY = evt.getY();
				clickCount++;
				if (clickResetTask != null)
					clickResetTask.cancel();
				clickResetTask = ToolKit.get().getAlarm().timed(() -> {
					clickCount = 0;
				}, ToolKit.get().getConfiguration().getDoubleClickTime());

				eventElement = getEventElement(mouseXY.x, mouseXY.y, check);
				// if (eventElement != null) {
				// if (eventElement.getResetKeyboardFocus())
				// resetTabFocusElement();
				// } else
				// resetTabFocusElement();

				switch (evt.getButtonIndex()) {
				case MouseUIButtonEvent.LEFT:
					onMouseLeftPressed(evt, check);
					break;
				case MouseUIButtonEvent.RIGHT:
					onMouseRightPressed(evt);
					break;
				case MouseUIButtonEvent.WHEEL:
					onMouseWheelPressed(evt);
					break;
				}
			} else if (evt.isReleased()) {
				handleMenuState();
				switch (evt.getButtonIndex()) {
				case MouseUIButtonEvent.LEFT:
					onMouseLeftReleased(evt);
					break;
				case MouseUIButtonEvent.RIGHT:
					onMouseRightReleased(evt);
					break;
				case MouseUIButtonEvent.WHEEL:
					onMouseWheelReleased(evt);
					break;
				}
				mousePressed = false;
				eventElement = null;
			}

			if (use3DSceneSupport && !evt.isConsumed()) {
				s3dOnMouseButtonEvent(evt);
			}
		}
	}

	protected void onMouseWheelReleased(MouseButtonEvent evt) {
		if (eventElement != null && eventElement.isEnabled()) {
			MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventElement,
					keyboardModifiers);

			if (mouseButtonSupport != null)
				mouseButtonSupport.fireEvent(mevt);

			if (!mevt.isConsumed()) {
				eventElement = (BaseElement) eventCaster.fireMouseButtonEvent(eventElement, mevt).getTargetElement();
				evt.setConsumed();
			}
		}
	}

	protected void onMouseRightReleased(MouseButtonEvent evt) {
		if (eventElement != null && eventElement.isEnabled()) {
			MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventElement,
					keyboardModifiers);

			if (mouseButtonSupport != null)
				mouseButtonSupport.fireEvent(mevt);

			if (!mevt.isConsumed()) {
				eventElement = (BaseElement) eventCaster.fireMouseButtonEvent(eventElement, mevt).getTargetElement();
				evt.setConsumed();
			}
		}
	}

	protected void onMouseLeftReleased(MouseButtonEvent evt) {
		mouseLeftPressed = false;
		eventElementResizeDirection = null;
		targetElement = getTargetDropElement(mouseXY.x, mouseXY.y);
		if (eventElement != null && eventElement.isEnabled()) {
			MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventElement,
					keyboardModifiers);

			if (mouseButtonSupport != null)
				mouseButtonSupport.fireEvent(mevt);

			if (!mevt.isConsumed()) {
				eventElement = (BaseElement) eventCaster.fireMouseButtonEvent(eventElement, mevt).getTargetElement();
				mevt.setConsumed();
			}
		}
	}

	protected void onMouseWheelPressed(MouseButtonEvent evt) {
		if (eventElement != null && eventElement.isEnabled()) {
			MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventElement,
					keyboardModifiers);

			if (mouseButtonSupport != null)
				mouseButtonSupport.fireEvent(mevt);

			if (!mevt.isConsumed()) {
				eventElement = (BaseElement) eventCaster.fireMouseButtonEvent(eventElement, mevt).getTargetElement();
				evt.setConsumed();
			}
		}
	}

	protected void onMouseRightPressed(MouseButtonEvent evt) {
		if (eventElement != null && eventElement.isEnabled()) {
			MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventElement,
					keyboardModifiers);

			if (mouseButtonSupport != null)
				mouseButtonSupport.fireEvent(mevt);

			if (!mevt.isConsumed()) {
				if (eventElement.getAbsoluteParent().isAffectZOrder())
					updateZOrder(eventElement.getAbsoluteParent());
				eventElement = (BaseElement) eventCaster.fireMouseButtonEvent(eventElement, mevt).getTargetElement();
				evt.setConsumed();
			}
		}
	}

	protected void onMouseLeftPressed(MouseButtonEvent evt, EventCheckType check) {
		mouseLeftPressed = true;
		// eventElement = getEventElement(evt.getX(), evt.getY());
		if (eventElement != null && eventElement.isEnabled()) {
			MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventElement,
					keyboardModifiers);

			if (mouseButtonSupport != null)
				mouseButtonSupport.fireEvent(mevt);

			if (!mevt.isConsumed()) {

				if (eventElement.getAbsoluteParent().isAffectZOrder())
					updateZOrder(eventElement.getAbsoluteParent());

				BaseElement frontElement = eventElement.calcBringToFront();
				if (frontElement != null)
					frontElement.bringToFront();

				if (eventElement.isDraggable())
					targetElement = null;
				if (eventElement.isResizable()) {
					float offsetX = mouseXY.x;
					float offsetY = mouseXY.y;
					BaseElement el = eventElement;
					if (eventElement.isResizeW() && offsetX > el.getAbsoluteX()
							&& offsetX < el.getAbsoluteX() + el.handlePosition.y) {
						// West
						if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.handlePosition.x) {
							eventElementResizeDirection = Borders.NW;
						} else if (offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
								&& offsetY < el.getAbsoluteHeight()) {
							eventElementResizeDirection = Borders.SW;
						} else {
							eventElementResizeDirection = Borders.W;
						}
					} else if (eventElement.isResizeE() && offsetX > (el.getAbsoluteWidth() - el.handlePosition.z)
							&& offsetX < el.getAbsoluteWidth()) {
						// East

						if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.handlePosition.x) {
							eventElementResizeDirection = Borders.NE;
						} else if (offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
								&& offsetY < el.getAbsoluteHeight()) {
							eventElementResizeDirection = Borders.SE;
						} else {
							eventElementResizeDirection = Borders.E;
						}
					} else {
						if (eventElement.isResizeN() && offsetY > el.getAbsoluteY()
								&& offsetY < el.getAbsoluteY() + el.handlePosition.x) {
							eventElementResizeDirection = Borders.N;
						} else if (eventElement.isResizeS() && offsetY > (el.getAbsoluteHeight() - el.handlePosition.z)
								&& offsetY < el.getAbsoluteHeight()) {
							eventElementResizeDirection = Borders.S;
						}
					}
					// resetKeyboardFocus(null);
				} else if (eventElement.isDraggable() && eventElementResizeDirection == null) {
					eventElementResizeDirection = null;
					resetKeyboardFocus(eventElement);
					eventElementOriginXY.set(eventElement.getPixelPosition());
				} else if (eventElement.isKeyboardFocusableInHierarchy()) {
					focusElementIsMovable = eventElement.isDraggable();
					setKeyboardFocus(eventElement);
				} else {
					focusElementIsMovable = false;
					eventElementResizeDirection = null;
					resetKeyboardFocus(eventElement);
				}
				eventElement = (BaseElement) eventCaster.fireMouseButtonEvent(eventElement, mevt).getTargetElement();

				/*
				 * Do this again in case the element was moved during any hooks (such as
				 * onMouseLeftPress). This happens for example with drag and drop, where the
				 * element is taken out of its nested element and placed in the screen for the
				 * duration of the drag. This means its relative locations change
				 */
				eventElement = calculateEventElementOffsets(mouseXY.x, mouseXY.y, check, eventElement);

				evt.setConsumed();
			}
		} else {
			if (mouseButtonSupport != null) {
				MouseUIButtonEvent<UIEventTarget> mevt = new MouseUIButtonEvent<UIEventTarget>(evt, eventElement,
						keyboardModifiers);
				mouseButtonSupport.fireEvent(mevt);
				if (!mevt.isConsumed())
					defaultClick();
			} else
				defaultClick();
		}
	}

	@Override
	public void onMouseMotionEvent(MouseMotionEvent evt) {
		setMouseXY(evt.getX(), getHeight() - evt.getY());

		final MouseMotionEvent origEvt = evt;
		evt = new MouseMotionEvent(evt.getX(), (int) (getHeight() - evt.getY()), evt.getDX(), evt.getDY(),
				evt.getWheel(), evt.getDeltaWheel()) {

			@Override
			public void setConsumed() {
				super.setConsumed();
				origEvt.setConsumed();
			}

		};
		evt.setTime(evt.getTime());

		if (mouseMovementSupport != null) {
			mouseMovementSupport.fireEvent(new MouseUIMotionEvent<>(evt, keyboardModifiers));
			if (evt.isConsumed()) {
				return;
			}
		}

		if (!mousePressed) {
			mouseFocusElement = getEventElement(mouseXY.x, mouseXY.y, EventCheckType.MouseMovement);
			if (mouseFocusElement != null) {
				if (getThemeInstance().hasCursors()) {

					/*
					 * For resize checks, first go up the tree to see which parent we are affecting
					 * and use that for some tests such as resizing at all test (but not which
					 * border)
					 */
					BaseElement affectedParent = mouseFocusElement;
					while (affectedParent != null && affectedParent.isAffectParent())
						affectedParent = affectedParent.getElementParent();

					if (affectedParent.isResizable()) {
						float offsetX = mouseXY.x;
						float offsetY = mouseXY.y;
						BaseElement el = mouseFocusElement;

						if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.handlePosition.y
								&& offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.handlePosition.x) {
							if (el.isResizeW() && el.isResizeN())
								this.setActiveCursor(CursorType.RESIZE_CNW);
							else if (el.isResizeW())
								this.setActiveCursor(CursorType.RESIZE_EW);
							else if (el.isResizeN())
								this.setActiveCursor(CursorType.RESIZE_NS);
							else
								this.setActiveCursor(calcCursor(el));
						} else if (offsetX > (el.getAbsoluteWidth() - el.handlePosition.z)
								&& offsetX < el.getAbsoluteWidth() && offsetY > el.getAbsoluteY()
								&& offsetY < el.getAbsoluteY() + el.handlePosition.x) {
							if (el.isResizeE() && el.isResizeN())
								this.setActiveCursor(CursorType.RESIZE_CNE);
							else if (el.isResizeE())
								this.setActiveCursor(CursorType.RESIZE_EW);
							else if (el.isResizeN())
								this.setActiveCursor(CursorType.RESIZE_NS);
							else
								this.setActiveCursor(calcCursor(el));
						} else if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.handlePosition.x) {
							if (el.isResizeN())
								this.setActiveCursor(CursorType.RESIZE_NS);
							else
								this.setActiveCursor(calcCursor(el));
						} else if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.handlePosition.y
								&& offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
								&& offsetY < el.getAbsoluteHeight()) {
							if (el.isResizeW() && el.isResizeS())
								this.setActiveCursor(CursorType.RESIZE_CNE);
							else if (el.isResizeW())
								this.setActiveCursor(CursorType.RESIZE_EW);
							else if (el.isResizeS())
								this.setActiveCursor(CursorType.RESIZE_NS);
							else
								this.setActiveCursor(calcCursor(el));
						} else if (offsetX > (el.getAbsoluteWidth() - el.handlePosition.z)
								&& offsetX < el.getAbsoluteWidth()
								&& offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
								&& offsetY < el.getAbsoluteHeight()) {
							if (el.isResizeE() && el.isResizeS())
								this.setActiveCursor(CursorType.RESIZE_CNW);
							else if (el.isResizeE())
								this.setActiveCursor(CursorType.RESIZE_EW);
							else if (el.isResizeS())
								this.setActiveCursor(CursorType.RESIZE_NS);
							else
								this.setActiveCursor(calcCursor(el));
						} else if (offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
								&& offsetY < el.getAbsoluteHeight()) {
							if (el.isResizeS())
								this.setActiveCursor(CursorType.RESIZE_NS);
							else
								this.setActiveCursor(calcCursor(el));
						} else if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.handlePosition.y) {
							if (el.isResizeW())
								this.setActiveCursor(CursorType.RESIZE_EW);
							else
								this.setActiveCursor(calcCursor(el));
						} else if (offsetX > (el.getAbsoluteWidth() - el.handlePosition.z)
								&& offsetX < el.getAbsoluteWidth()) {
							if (el.isResizeE())
								this.setActiveCursor(CursorType.RESIZE_EW);
							else
								this.setActiveCursor(calcCursor(el));
						} else {
							this.setActiveCursor(calcCursor(el));
						}
					} else {
						this.setActiveCursor(calcCursor(mouseFocusElement));
					}
				} else {
					this.setActiveCursor(calcCursor(mouseFocusElement));
				}
			}
			if (mouseFocusElement != previousMouseFocusElement) {
				if (previousMouseFocusElement == null || previousMouseFocusElement.getElementParent() != null)
					eventCaster.fireMouseFocusEvent(previousMouseFocusElement, new HoverEvent<BaseElement>(evt,
							previousMouseFocusElement, mouseFocusElement, keyboardModifiers, HoverEventType.leave));
				if (mouseFocusElement != null) {
					eventCaster.fireMouseFocusEvent(mouseFocusElement, new HoverEvent<BaseElement>(evt,
							mouseFocusElement, previousMouseFocusElement, keyboardModifiers, HoverEventType.enter));
				}
				previousMouseFocusElement = mouseFocusElement;
			}
			if (mouseFocusElement != null) {
				focusElementIsMovable = mouseFocusElement.isDraggable();
				eventCaster.fireMouseMotionEvent(mouseFocusElement,
						new MouseUIMotionEvent<BaseElement>(evt, mouseFocusElement, keyboardModifiers));
			}
			mouseWheelElement = getEventElement(mouseXY.x, mouseXY.y, EventCheckType.WheelMove);
			if (mouseWheelElement != null && evt.getDeltaWheel() != 0 && mouseWheelElement.mouseWheelSupport != null) {
				mouseWheelElement.mouseWheelSupport
						.fireEvent(new MouseUIWheelEvent<BaseElement>(evt, mouseWheelElement, keyboardModifiers));
			}
		} else {
			if (eventElement != null) {
				if (mouseLeftPressed) {
					if (eventElementResizeDirection != null) {
						resizeElement(eventElement, mouseXY.x, mouseXY.y, eventElementResizeDirection);
					} else if (focusElementIsMovable) {
						float moveX = mouseXY.x - eventElementOffsetX;
						float moveY = mouseXY.y - eventElementOffsetY;
						moveElement(eventElement, moveX, moveY);
					}
				}

				eventCaster.fireMouseMotionEvent(eventElement,
						new MouseUIMotionEvent<BaseElement>(evt, eventElement, keyboardModifiers));
			}
		}
		if (!mousePressed && mouseFocusElement == null) {
			this.setActiveCursor(defaultCursor);
		}

		if (use3DSceneSupport) {
			s3dOnMouseMotionEvent(evt, mouseFocusElement != null);
		}
	}

	private CursorType calcCursor(BaseElement element) {
		ElementContainer<?, ?> p = element;
		CursorType c = element.isEnabled() ? element.getCursor() : null;
		while (c == null && p != null) {
			p = p.getParentContainer();
			if (p != null) {
				if (!(p instanceof BaseElement) || ((BaseElement) p).isEnabled()) {
					c = p.getCursor();
				}
			}
		}
		return c;
	}

	public BaseScreen removeScreenListener(ScreenEventListener l) {
		if (screenEventSupport != null)
			screenEventSupport.removeListener(l);
		return this;
	}

	public BaseScreen addScreenListener(ScreenEventListener l) {
		if (screenEventSupport == null)
			screenEventSupport = new ScreenEventSupport();
		screenEventSupport.addListener(l);
		return this;
	}

	public BaseScreen onScreenEvent(ScreenEventListener l, Type type) {
		if (screenEventSupport == null)
			screenEventSupport = new ScreenEventSupport();
		screenEventSupport.bind(l, type);
		return this;
	}

	@Override
	public BaseScreen onMouseMoved(MouseMovementListener<UIEventTarget> l) {
		if (mouseMovementSupport == null)
			mouseMovementSupport = new MouseMovementSupport<UIEventTarget>();
		mouseMovementSupport.bind(l);
		return this;
	}

	@Override
	public BaseScreen onMousePressed(MouseButtonListener<UIEventTarget> l) {
		onMousePressed(l, MouseUIButtonEvent.LEFT);
		return this;
	}

	@Override
	public BaseScreen onMousePressed(MouseButtonListener<UIEventTarget> l, int button) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport<UIEventTarget>();
		mouseButtonSupport.bindPressed(l, button);
		return this;
	}

	@Override
	public BaseScreen onMouseReleased(MouseButtonListener<UIEventTarget> l) {
		onMouseReleased(l, MouseUIButtonEvent.LEFT);
		return this;
	}

	@Override
	public BaseScreen onMouseReleased(MouseButtonListener<UIEventTarget> l, int button) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport<UIEventTarget>();
		mouseButtonSupport.bindReleased(l, button);
		return this;
	}

	@Override
	public BaseScreen onMouseWheel(MouseUIWheelListener<UIEventTarget> l) {
		if (mouseWheelSupport == null)
			mouseWheelSupport = new MouseWheelSupport<UIEventTarget>();
		mouseWheelSupport.bind(l);
		return this;
	}

	public BaseScreen onKeyboardFocus(KeyboardFocusListener l) {
		if (keyboardFocusSupport == null) {
			keyboardFocusSupport = new KeyboardFocusSupport();
		}
		keyboardFocusSupport.bind(l);
		return this;
	}

	@Override
	public void onTouchEvent(TouchEvent evt) {
		setTouchXY(evt.getX(), evt.getY());

		// evt.set(evt.getType(),touchXY.x,touchXY.y,evt.getDeltaX()*inputScale,evt.getDeltaY()*inputScale);
		if (useMultiTouch) {
			switch (evt.getType()) {
			case DOWN:
				androidTouchDownEvent(evt);
				break;
			case MOVE:
				androidTouchMoveEvent(evt);
				break;
			case UP:
				androidTouchUpEvent(evt);
				break;
			case FLING:
				androidFlingEvent(evt);
			default:
				break;
			}
		}
	}

	/**
	 * Plays an instance of an audio node
	 * 
	 * @param key    String The key associated with the audio node
	 * @param volume float the volume to play the instance at (effected by global
	 *               volume)
	 */
	public void playAudioNode(String key, float volume) {
		AudioNode audioNode = new AudioNode(getApplication().getAssetManager(), key, false);
		audioNode.setPositional(false);
		audioNode.setReverbEnabled(false);
		audioNode.setVolume(volume * getUIAudioVolume());
		getGUINode().attachChild(audioNode);
		audioNode.playInstance();
		audioNode.addControl(new AbstractControl() {

			@Override
			protected void controlRender(RenderManager rm, ViewPort vp) {
			}

			@Override
			protected void controlUpdate(float tpf) {
				if (audioNode.getStatus() != Status.Playing) {
					audioNode.removeFromParent();
				}
			}
		});
	}

	@Override
	public void read(JmeImporter im) throws IOException {
	}

	/**
	 * Release cursor control back to the Element level
	 */
	public void releaseForcedCursor() {
		if (getThemeInstance().hasCursors()) {
			JmeCursor jmeCur = getThemeInstance().getCursor(currentCursor == null ? CursorType.POINTER : currentCursor);
			if (jmeCur != null) {
				getApplication().getInputManager().setMouseCursor(jmeCur);
			}
			forceCursor = false;
		}
	}

	public void releaseModal(BaseElement el) {
		if (modal.remove(el)) {
			if (modal.isEmpty())
				hideModalBackground();
			else {
				updateZOrder(modal.get(0));
			}
		}
	}

	/**
	 * Removes an Element from the Screen and scene graph
	 * 
	 * @param element The Element to remove
	 */

	@Override
	public BaseScreen removeElement(BaseElement element) {
		if (childList.remove(element)) {
			element.detachFromParent();
			element.cleanup();

			/* Remove the tooltip if it was associated with this element */
			if (toolTipManager != null)
				toolTipManager.removeToolTipFor(element);

			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
		return this;
	}

	@Override
	public BaseScreen removeMouseButtonListener(MouseButtonListener<UIEventTarget> l) {
		if (mouseButtonSupport != null)
			mouseButtonSupport.removeListener(l);
		return this;
	}

	@Override
	public BaseScreen removeMouseMovementListener(MouseMovementListener<UIEventTarget> l) {
		if (mouseMovementSupport != null)
			mouseMovementSupport.removeListener(l);
		return this;
	}

	@Override
	public BaseScreen removeMouseWheelListener(MouseUIWheelListener<UIEventTarget> l) {
		if (mouseWheelSupport != null)
			mouseWheelSupport.removeListener(l);
		return this;
	}

	public BaseScreen removeKeyboardFocusListener(KeyboardFocusListener l) {
		if (keyboardFocusSupport != null)
			keyboardFocusSupport.removeListener(l);
		return this;
	}

	public void removeStylesheet(Stylesheet sheet) {
		stylesheets.remove(sheet);
		dirtyLayout(true, LayoutType.reset);
		layoutChildren();
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	}

	public void resetClickCount() {
		clickCount = 0;
	}

	/**
	 * Resets the tab focus element to null after calling the TabFocusListener's
	 * resetTabFocus method.
	 */

	public void resetKeyboardFocus(BaseElement other) {
		this.focusForm = null;
		if (keyboardFocus != null) {
			BaseElement el = keyboardFocus;
			keyboardFocus = null;
			KeyboardFocusEvent evt = new KeyboardFocusEvent(null, el, other, KeyboardFocusEventType.lost);
			if (keyboardFocusSupport != null)
				keyboardFocusSupport.fireEvent(evt);
			el.keyboardFocusChanged(evt);
		}
	}

	@Override
	public BaseScreen setCursor(CursorType cursorType) {
		this.defaultCursor = cursorType;
		if (currentCursor == null) {
			setActiveCursor(cursorType);
		}
		return this;
	}

	@Override
	public CursorType getCursor() {
		return defaultCursor;
	}

	/**
	 * For internal use - Use setForcedCursor instead
	 * 
	 * @param cur
	 */

	public void setActiveCursor(CursorType cur) {
		if (!Objects.equals(cur, currentCursor)) {
			InputManager inputManager = getApplication().getInputManager();
			if (themeInstance != null && themeInstance.hasCursors()) {
				if (!forceCursor) {
					if (cur == null || cur.equals(CursorType.HIDDEN)) {
						inputManager.setCursorVisible(false);
					} else {
						inputManager.setCursorVisible(true);
						JmeCursor jmeCur = themeInstance.getCursor(cur);
						if (jmeCur != null) {
							inputManager.setMouseCursor(jmeCur);
						}
					}
				}
			}
			currentCursor = cur;
		}
	}

	@Override
	public float getElementAlpha() {
		return elementAlpha;
	}

	public BaseScreen setElementAlpha(float elementAlpha) {
		if (this.elementAlpha != elementAlpha) {
			this.elementAlpha = elementAlpha;
			dirtyLayout(true, LayoutType.alpha);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Sets the cursor and locks the cursor until releaseForcedCursor is called.
	 * 
	 * @param cur CursorType
	 */
	public void setForcedCursor(CursorType cur) {
		if (getThemeInstance().hasCursors()) {
			JmeCursor jmeCur = getThemeInstance().getCursor(cur);
			if (jmeCur != null) {
				getApplication().getInputManager().setMouseCursor(jmeCur);
				forceCursor = true;
			}
		}
	}

	/**
	 * Sets the overall opacity of all elements that have not been flagged as
	 * ignoreGlobalAlpha(true)
	 * 
	 * @param globalAlpha float
	 */

	public void setGlobalAlpha(float globalAlpha) {
		this.globalAlpha = globalAlpha;
		synchronized (childList) {
			for (BaseElement el : childList) {
				el.setGlobalAlpha(globalAlpha);
			}
		}
	}

	// <editor-fold desc="Forms & Tab Focus">
	/**
	 * Method for setting the tab focus element
	 * 
	 * @param element The Element to set tab focus to
	 */

	public void setKeyboardFocus(BaseElement element) {
		if (element != null)
			element = element.getKeyboardFocusParent();

		if (!Objects.equals(keyboardFocus, element)) {

			if (element != null && (!element.isEnabled() || !element.isKeyboardFocusable()))
				return;

			resetKeyboardFocus(element);

			ElementContainer<?, ?> el = element;
			focusForm = null;
			while (focusForm == null && el != null) {
				focusForm = el.getFocusCycle();
				if (focusForm == null)
					el = el.getParentContainer();
			}

			// Nott right? form might be > 1 level away
			// if (focusForm != null) {
			// element = el;
			// }

			if (element != null) {
				keyboardFocus = element;
				if (focusForm != null) {
					focusForm.setFocusCycleElement(element);
				}
				KeyboardFocusEvent evt = new KeyboardFocusEvent(null, element, keyboardFocus,
						KeyboardFocusEventType.gained);
				if (keyboardFocusSupport != null)
					keyboardFocusSupport.fireEvent(evt);
				keyboardFocus.keyboardFocusChanged(evt);

			}
		}
	}

	@Override
	public BaseScreen setLayoutManager(Layout<?, ?> layoutManager) {
		this.layoutManager = layoutManager;
		return this;
	}

	public void setMouseButtonsEnabled(boolean mouseButtonsEnabled) {
		this.mouseButtonsEnabled = mouseButtonsEnabled;
	}

	public void setSnapToPixel(boolean snapToPixel) {
		this.snapToPixel = snapToPixel;
		dirtyLayout(true, LayoutType.contentChange());
		layoutChildren();
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
		if (spatial != null) {
			((Node) spatial).attachChild(t0neg0dGUI);
			t0neg0dGUI.addControl(effectManager);
		}
	}

	public void setToolTipManager(ToolTipManager toolTipManager) {
		if (!Objects.equals(toolTipManager, this.toolTipManager)) {
			if (this.toolTipManager != null)
				this.toolTipManager.cleanup();
			this.toolTipManager = toolTipManager;
			if (this.toolTipManager != null) {
				toolTipManager.init(this);
			}
		}
	}

	/**
	 * Sets the global UI Audio volume
	 * 
	 * @param uiAudioVolume float
	 */

	public void setUIAudioVolume(float uiAudioVolume) {
		this.uiAudioVolume = uiAudioVolume;
	}

	// <editor-fold desc="3D Scene Support">
	public void setUse3DSceneSupport(boolean enable) {
		this.use3DSceneSupport = enable;
	}

	public void setUseMultiTouch(boolean useMultiTouch) {
		this.useMultiTouch = useMultiTouch;
		getApplication().getInputManager().setSimulateMouse(!useMultiTouch);
		getApplication().getInputManager().setSimulateKeyboard(!useMultiTouch);
	}

	public void setUseTextureAtlas(boolean useTextureAtlas, String texturePath) {
		this.useTextureAtlas = useTextureAtlas;

		if (texturePath != null) {
			atlasTexture = getApplication().getAssetManager().loadTexture(texturePath);
			atlasTexture.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			atlasTexture.setMagFilter(Texture.MagFilter.Bilinear);
			atlasTexture.setWrap(Texture.WrapMode.Clamp);
		} else {
			atlasTexture = null;
		}
	}

	/**
	 * Enables/disables UI Audio
	 * 
	 * @param useUIAudio boolean
	 */

	public void setUseUIAudio(boolean useUIAudio) {
		this.useUIAudio = useUIAudio;
	}

	/**
	 * Adds an Element to the Screen and scene graph and show it. The element will
	 * be added <b>hidden</b>, followed immediate by a {@link BaseElement#show()}.
	 * This will cause CSS events to be fired and is the preferred way to add new
	 * elements to the screen.
	 * 
	 * @param child The Element to add
	 */

	@Override
	public BaseScreen showElement(BaseElement child) {
		return showElement(child, null);
	}

	/**
	 * Adds an Element to the Screen and scene graph and show it. The element will
	 * be added <b>hidden</b>, followed immediate by a {@link BaseElement#show()}.
	 * This will cause CSS events to be fired and is the preferred way to add new
	 * elements to the screen.
	 * 
	 * @param child The Element to add
	 */

	@Override
	public BaseScreen showElement(BaseElement child, Object constraints) {
		boolean wasDestroyOnHide = child.isDestroyOnHide();
		child.setDestroyOnHide(false);
		try {
			addElement(child, constraints, true);
			child.show();
			return this;
		} finally {
			child.setDestroyOnHide(wasDestroyOnHide);
		}
	}

	@Override
	public void update(float tpf) {
		if (Display.wasResized()) {
			/*
			 * Workaround. I am not sure why Application#reshape() is not getting called (on
			 * Linux at least). This is an attempt to make sure cameras get reconfigure when
			 * the screen is resized
			 */
			int w = getApplication().getViewPort().getCamera().getWidth();
			int h = getApplication().getViewPort().getCamera().getHeight();
			if (w != Display.getWidth() || h != Display.getHeight()) {
				getApplication().getRenderManager().notifyReshape(Display.getWidth(), Display.getHeight());
			}

			if (orDim) {
				inputScale = 1f / (getApplication().getViewPort().getCamera().getWidth() / orWidth);
			} else {
				dirtyLayout(true, LayoutType.boundsChange());
				layoutChildren();
			}

			if (screenEventSupport != null)
				screenEventSupport.fireEvent(new ScreenEvent(this, Type.RESIZE));
		}

		/*
		 * if (Vector4f.ZERO.x != 0 || Vector4f.ZERO.y != 0 || Vector4f.ZERO.z != 0 ||
		 * Vector4f.ZERO.z != 0) { System.err.println(
		 * "Something messed up Vector4f.ZERO!"); System.exit(0); } if (Vector2f.ZERO.x
		 * != 0 || Vector2f.ZERO.y != 0) { System.err.println(
		 * "Something messed up Vector2f.ZERO!"); System.exit(0); } if (Vector3f.ZERO.x
		 * != 0 || Vector3f.ZERO.y != 0) { System.err.println(
		 * "Something messed up Vector3f.ZERO!"); System.exit(0); } if (null.x != 0 ||
		 * null.y != 1) { System.err.println("Something messed up LAYOUT_SIZE!");
		 * System.exit(0);
		 * 
		 * }
		 */
	}

	/**
	 * Brings the element specified to the front of the zOrder list shifting other
	 * below to keep all Elements within the current z-order range.
	 * 
	 * @param topMost The Element to bring to the front
	 */

	public void updateZOrder(BaseElement topMost) {
		// zOrderCurrent = zOrderInit;

		synchronized (childList) {
			if (!childList.contains(topMost)) {
				// Not a child of the screen
				return;
			}
			if (childList.remove(topMost)) {
				childList.add(topMost);
				if (t0neg0dGUI.getChildren().remove(topMost)) {
					t0neg0dGUI.getChildren().add(topMost);
				}
			}
		}

		applyZOrder();
		// dirtyLayout(false, LayoutType.zorder);
		// layoutChildren();

		topMost.movedToFrontHook();

	}

	@Override
	public void write(JmeExporter ex) throws IOException {
	}

	public void resetStyling() {
		font = null;
	}

	protected void defaultClick() {
		resetKeyboardFocus(null);
	}

	protected void initializeElement(BaseElement element) {
		element.setInitialized(this);

		// TODO is this still needed?
//		if (element.getDimensions().equals(Vector2f.ZERO))
//			element.sizeToContent();

		// if (element.isLockToParentBounds()) {
		// element.lockToParentBounds(element.getX(), element.getY());
		// }

		// shoulld have already been done
		// element.layoutChildren();
	}

	protected void layoutHeirarchy(Node s) {
		applyZOrder();
		for (BaseElement el : childList) {
			el.layoutChildren();
		}
	}

	protected void moveElement(BaseElement el, float x, float y) {
		while (el != null && el.getElementParent() != null && el.isAffectParent()) {
			el = el.getElementParent();
		}
		el.moveTo(x, y);
	}

	protected void resizeElement(BaseElement el, float x, float y, Borders resizeDir) {
		while (el != null && el.getElementParent() != null && el.isAffectParent()) {
			el = el.getElementParent();
		}
		el.resize(mouseXY.x, mouseXY.y, resizeDir);
	}

	/**
	 * This scales the current event values of deltaX and deltaY to a value between
	 * 0.0f and 1.0f
	 * 
	 * @param evt
	 */
	private void androidFlingEvent(TouchEvent evt) {
		setTouchXY(evt.getX(), evt.getY());
		float flingX = 1f / 8000f * evt.getDeltaX();
		float flingY = 1f / 8000f * evt.getDeltaY();
		evt.set(evt.getType(), touchXY.x, touchXY.y, flingX, flingY);

		BaseElement target = getEventElement(touchXY.x, touchXY.y, EventCheckType.Fling);

		if (target != null) {
			if (target instanceof FlingListener) {
				((FlingListener) target).onFling(evt);
			}
		}
	}

	protected void androidTouchDownEvent(TouchEvent evt) {
		// setTouchXY(evt.getX(),evt.getY());
		mousePressed = true;
		Vector2f offset = tempElementOffset.clone();
		BaseElement target = getEventElement(touchXY.x, touchXY.y, EventCheckType.Touch);

		Borders dir = null;
		if (target != null) {
			if (target.isKeyboardFocusable())
				resetKeyboardFocus(null);

			if (target.getAbsoluteParent().isAffectZOrder())
				updateZOrder(target.getAbsoluteParent());
			if (target.isKeyboardFocusable())
				this.setKeyboardFocus(target);
			if (target.isDragDropDragElement())
				targetElement = null;
			if (target.isResizable()) {
				float offsetX = touchXY.x;
				float offsetY = touchXY.y;
				BaseElement el = target;

				if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.handlePosition.y) {
					if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.handlePosition.x) {
						dir = Borders.NW;
					} else if (offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
							&& offsetY < el.getAbsoluteHeight()) {
						dir = Borders.SW;
					} else {
						dir = Borders.W;
					}
				} else if (offsetX > (el.getAbsoluteWidth() - el.handlePosition.z) && offsetX < el.getAbsoluteWidth()) {
					if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.handlePosition.w) {
						dir = Borders.NE;
					} else if (offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
							&& offsetY < el.getAbsoluteHeight()) {
						dir = Borders.SE;
					} else {
						dir = Borders.E;
					}
				} else {
					if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.handlePosition.x) {
						dir = Borders.N;
					} else if (offsetY > (el.getAbsoluteHeight() - el.handlePosition.w)
							&& offsetY < el.getAbsoluteHeight()) {
						dir = Borders.S;
					}
				}
				resetKeyboardFocus(null);
			} else if (target.isDraggable() && dir == null) {
				dir = null;
				resetKeyboardFocus(null);
				eventElementOriginXY.set(target.getPixelPosition());
			} else if (target.isKeyboardFocusable()) {
				setKeyboardFocus(target);
			} else {
				dir = null;
				resetKeyboardFocus(null);
			}

			eventElement = eventCaster.fireMouseButtonEvent(eventElement,
					new MouseUIButtonEvent<BaseElement>(new MouseButtonEvent(0, true, (int) touchXY.x, (int) touchXY.y),
							getClickCount(), keyboardModifiers))
					.getTargetElement();

			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchDown(evt);
			}
			evt.setConsumed();
			contactElements.put(evt.getPointerId(), getContactElement(touchXY.x, touchXY.y, EventCheckType.Touch));
			elementOffsets.put(evt.getPointerId(), offset);
			eventElements.put(evt.getPointerId(), target);
			eventElementResizeDirections.put(evt.getPointerId(), dir);
		} else {
			resetKeyboardFocus(null);
		}

		if (use3DSceneSupport && !evt.isConsumed()) {
			s3dOnTouchDownEvent(evt);
		}
	}

	@SuppressWarnings("unchecked")
	private void androidTouchMoveEvent(TouchEvent evt) {
		// setTouchXY(evt.getX(),evt.getY());
		for (Integer key : eventElements.keySet()) {
			if (key == evt.getPointerId()) {
				BaseElement target = eventElements.get(key);
				if (target != null) {
					BaseElement contact = contactElements.get(key);
					Vector2f offset = elementOffsets.get(key);
					Borders dir = eventElementResizeDirections.get(key);

					boolean movable = contact.isDraggable();
					if (dir != null) {
						resizeElement(target, touchXY.x, touchXY.y, dir);
					} else if (movable) {
						moveElement(target, touchXY.x - offset.x, touchXY.y - offset.y);
					}

					MouseUIMotionEvent<BaseElement> mbEvt = new MouseUIMotionEvent<BaseElement>(
							new MouseMotionEvent((int) touchXY.x, (int) touchXY.y, (int) evt.getDeltaX(),
									(int) evt.getDeltaY(), 0, 0),
							target, keyboardModifiers);
					if (target instanceof MouseMovementListener) {
						((MouseMovementListener<UIEventTarget>) target)
								.onMouseMove(new MouseUIMotionEvent<UIEventTarget>(mbEvt, target, keyboardModifiers));
					}
					eventCaster.fireMouseMotionEvent(target, mbEvt);
					if (target instanceof TouchListener) {
						((TouchListener) target).onTouchMove(evt);
					}
				}
			}
		}

		if (use3DSceneSupport) {
			s3dOnTouchMoveEvent(evt);
		}
	}

	protected void androidTouchUpEvent(TouchEvent evt) {
		// setTouchXY(evt.getX(),evt.getY());
		BaseElement target = eventElements.get(evt.getPointerId());
		handleAndroidMenuState(target);
		if (target != null) {
			eventElement = eventCaster.fireMouseButtonEvent(eventElement,
					new MouseUIButtonEvent<BaseElement>(new MouseButtonEvent(0, true, (int) touchXY.x, (int) touchXY.y),
							getClickCount(), keyboardModifiers))
					.getTargetElement();

			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchUp(evt);
			}
			evt.setConsumed();
			eventElements.remove(evt.getPointerId());
			contactElements.remove(evt.getPointerId());
			elementOffsets.remove(evt.getPointerId());
			eventElementResizeDirections.remove(evt.getPointerId());
		}

		if (use3DSceneSupport && !evt.isConsumed()) {
			s3dOnTouchUpEvent(evt);
		}

		mousePressed = false;
	}

	private BaseElement getContactElement(float x, float y, EventCheckType check) {
		guiRayOrigin.set(x, getHeight() - y, 0);

		elementZOrderRay.setOrigin(guiRayOrigin);
		results.clear();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		BaseElement testEl = null, el = null;
		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof BaseElement) {
				testEl = ((BaseElement) (result.getGeometry().getParent()));
				if (getIgnoreEvent(testEl, check)) {
					discard = true;
				} else if (testEl.isClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX()
							|| result.getContactPoint().getX() > testEl.getClippingBounds().getZ()
							|| result.getContactPoint().getY() < testEl.getClippingBounds().getY()
							|| result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}
			if (!discard) {
				if (result.getGeometry().getParent() instanceof BaseElement) {
					el = testEl;
				}
			}
		}
		if (el != null) {
			BaseElement parent = null;
			if (el.isAffectParent() && mousePressed) {
				parent = el.getElementParent();
			}
			if (parent != null)
				tempElementOffset.set(x - parent.getX(), y - parent.getY());
			else
				tempElementOffset.set(x - el.getX(), y - el.getY());
			return el;
		} else {
			// 2D Framework

			return null;
		}
	}

	public List<BaseElement> getElementsAt(float x, float y) {
		List<BaseElement> els = new ArrayList<>();
		guiRayOrigin.set(x, getHeight() - y, 0);

		elementZOrderRay.setOrigin(guiRayOrigin);

		results.clear();
		t0neg0dGUI.collideWith(elementZOrderRay, results);

		BaseElement testEl = null;

		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof BaseElement) {
				testEl = ((BaseElement) (result.getGeometry().getParent()));
				if (getIgnoreEvent(testEl, null)) {
					discard = true;
				} else if (testEl.isClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX()
							|| result.getContactPoint().getX() > testEl.getClippingBounds().getZ()
							|| result.getContactPoint().getY() < testEl.getClippingBounds().getY()
							|| result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}

			if (!discard) {
				if (result.getGeometry().getParent() instanceof BaseElement) {
					els.add(testEl);
				}
			}
		}

		return els;

	}

	protected BaseElement getEventElement(float x, float y, EventCheckType check) {
		guiRayOrigin.set(x, getHeight() - y, 0);

		elementZOrderRay.setOrigin(guiRayOrigin);
		results.clear();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		lastCollision = results.getClosestCollision();

		BaseElement testEl = null, el = null;

		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof BaseElement) {
				testEl = ((BaseElement) (result.getGeometry().getParent()));
				if (getIgnoreEvent(testEl, check)) {
					discard = true;
				} else if (testEl.isClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX()
							|| result.getContactPoint().getX() > testEl.getClippingBounds().getZ()
							|| result.getContactPoint().getY() < testEl.getClippingBounds().getY()
							|| result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}

			if (!discard) {
				if (result.getGeometry().getParent() instanceof BaseElement) {
					el = testEl;
				}
			}
		}

		if (el != null) {
			el = calculateEventElementOffsets(x, y, check, el);
			return el;
		} else {
			return null;
		}
	}

	protected BaseElement calculateEventElementOffsets(float x, float y, EventCheckType check, BaseElement el) {
		BaseElement parent = null;

		/* Are we within a resizable parents handle area? */
		parent = el.getElementParent();
		while (parent != null) {
			if (parent.isResizable()) {
				if (!(x >= parent.getAbsoluteX() + parent.handlePosition.w
						&& x < parent.getAbsoluteWidth() - parent.handlePosition.y
						&& y >= parent.getAbsoluteY() + parent.handlePosition.x
						&& y < parent.getAbsoluteHeight() - parent.handlePosition.z)) {
					el = parent;
				}
				break;
			}

			parent = parent.getElementParent();
		}

		/* Effect parent */
		// TODO Check if this is right. It seems to work OK
		// with the new code below, but more testing is
		// required
//			parent = null;
//			if (el.isAffectParent() && mousePressed) {
//				parent = el.getElementParent();
//			}
//			if (parent != null) {
//				el = parent;
//			}
		parent = el;
		if (el.isAffectParent() && mousePressed) {
			parent = el.getElementParent();
		}

		switch (check) {
		case MouseLeft:
		case MouseRight:
		case WheelClick:
			eventElementOffsetX = x - parent.getAlignedX();
			eventElementOffsetY = y - parent.getAlignedY();
			break;
		default:
			break;
		}
		return el;
	}

	private Node getEventNode(float x, float y) {
		Node testEl = null, el = null;

		for (ViewPort vp : getApplication().getRenderManager().getMainViews()) {
			Node root = (Node) vp.getScenes().get(0);

			if (!ToolKit.isAndroid())
				click2d.set(getApplication().getInputManager().getCursorPosition());
			else
				click2d.set(touchXY);
			tempV2.set(click2d);
			click3d.set(vp.getCamera().getWorldCoordinates(tempV2, 0f));
			pickDir.set(vp.getCamera().getWorldCoordinates(tempV2, 1f).subtractLocal(click3d).normalizeLocal());
			pickRay.setOrigin(click3d);
			pickRay.setDirection(pickDir);
			results.clear();
			root.collideWith(pickRay, results);

			boolean listener = false;

			for (CollisionResult result : results) {
				Node parent = result.getGeometry().getParent();
				while (parent != root && listener == false) {
					if (parent instanceof HoverListener || parent instanceof MouseButtonListener
							|| parent instanceof MouseMovementListener || parent instanceof TouchListener) {
						testEl = parent;
						listener = true;
						break;
					}
					parent = parent.getParent();
				}
				if (listener)
					break;
			}

			if (testEl != null)
				el = testEl;
		}

		if (el != null)
			return el;
		else
			return null;
	}

	// <editor-fold desc="Element Event Methods">
	private boolean getIgnoreEvent(BaseElement el, EventCheckType check) {
		if (check == null)
			return false;

		if (el.isIgnore(check))
			return true;

		switch (check) {
		case Drag:
			return (el.isIgnoreMouseMovement() && el.getToolTipProvider() == null
					&& (el.getToolTipText() == null || el.getToolTipText().equals("")))
					|| (!el.isDraggable() && !el.isResizable());
		case MouseMovement:
			return el.isIgnoreMouseMovement() && el.getToolTipProvider() == null
					&& (el.getToolTipText() == null || el.getToolTipText().equals(""));
		case MouseLeft:
			return el.isIgnoreMouseLeftButton();
		case MouseRight:
			return el.isIgnoreMouseRightButton();
		case WheelClick:
			return el.isIgnoreMouseWheelClick();
		case WheelMove:
			return el.isIgnoreMouseWheelMove();
		case MouseFocus:
			return !el.isHoverable() && el.getToolTipProvider() == null
					&& (el.getToolTipText() == null || el.getToolTipText().equals(""));
		case Touch:
			return el.isIgnoreTouch();
		case TouchMove:
			return el.isIgnoreTouchMove();
		case Fling:
			return el.isIgnoreFling();
		default:
			return false;
		}
	}

	/**
	 * Determines and returns the drop Element
	 * 
	 * @param x The current mouse X coord
	 * @param y The current mouse Y coord
	 * @return Element eventElement
	 */
	private BaseElement getTargetDropElement(float x, float y) {
		guiRayOrigin.set(x, getHeight() - y, 0);

		elementZOrderRay.setOrigin(guiRayOrigin);
		results.clear();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		float z = 0;
		BaseElement testEl = null, el = null;
		Node par = null;
		for (CollisionResult result : results) {
			par = result.getGeometry().getParent();

			if (par instanceof DefaultSceneElement) {
				par = par.getParent();
			}

			if (!(par instanceof BaseElement))
				continue;

			testEl = (BaseElement) par;

			if (testEl.equals(getDragElement()))
				continue;
			if (testEl.isDragDropDragElement())
				continue;
			if (!testEl.isDragDropDropElement())
				continue;

			if (testEl.isClipped()) {
				if (result.getContactPoint().getX() < testEl.getClippingBounds().getX()
						|| result.getContactPoint().getX() > testEl.getClippingBounds().getZ()
						|| result.getContactPoint().getY() < testEl.getClippingBounds().getY()
						|| result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
					continue;
				}
			}

			if (result.getContactPoint().getZ() > z) {
				z = result.getContactPoint().getZ();
				el = testEl;
			}
		}
		if (el != null) {
			BaseElement parent = null;
			if (el.isAffectParent() && mousePressed) {
				parent = el.getElementParent();
			}
			if (parent != null) {
				el = parent;
			}
			targetElementOffsetX = x - el.getX();
			targetElementOffsetY = y - el.getY();
			return el;
		} else {
			// 2D Framework

			return null;
		}
	}

	// <editor-fold desc="Menu Handling">
	private void handleMenuState() {
		if (!ToolKit.isAndroid()) {
			synchronized (childList) {
				if (eventElement == null) {
					for (BaseElement el : childList) {
						if (el instanceof AutoHide) {
							el.hide();
						}
					}
				} else {
					/*
					 * Autohide if required (the check for combobox is to stop the menu getting
					 * hidden when the mouse is released)
					 */
					if (!(eventElement.getAbsoluteParent() instanceof AutoHide)
							&& !(eventElement instanceof ComboBox || eventElement.getParent() instanceof ComboBox)) {
						for (BaseElement el : childList) {
							if (el instanceof AutoHide) {
								el.hide();
							}
						}
					} else if (eventElement.getAbsoluteParent() instanceof AutoHide) {
						// for (Element el : elements.values()) {
						// if (el instanceof AutoHide && el !=
						// eventElement.getAbsoluteParent()) {
						// System.err.println("[REMOVEME] HIding " +
						// el.toString() + " because it is autohide and not " +
						// eventElement.getAbsoluteParent() + " which is abs
						// parent of " + eventElement);
						// el.hide();
						// }
						// }
					} else if (eventElement.getParent() instanceof ComboBox) {
						for (BaseElement el : childList) {
							if (el instanceof AutoHide && el != ((ComboBox<?>) eventElement.getParent()).getMenu()) {
								el.hide();
							}
						}
					}
				}
			}
		}
	}

	private void initModalBackground() {
		if (modalBackground == null) {
			modalBackground = new ModalBackground(this);
			showElement(modalBackground, ScreenLayoutConstraints.fill);
		}
	}

	@SuppressWarnings("unchecked")
	private void s3dOnMouseButtonEvent(MouseButtonEvent evt) {
		float x = ToolKit.isAndroid() ? touchXY.x : mouseXY.x;
		float y = ToolKit.isAndroid() ? touchXY.y : mouseXY.y;
		eventNode = getEventNode(x, y);
		if (eventNode != null) {
			if (eventNode instanceof MouseButtonListener) {
				NodeEventTarget target = new NodeEventTarget(this, eventNode);
				MouseUIButtonEvent<NodeEventTarget> mevt = new MouseUIButtonEvent<NodeEventTarget>(evt, target,
						keyboardModifiers);
				((MouseButtonListener<NodeEventTarget>) eventNode).onMouseButton(mevt);
			}
			eventNode = null;
		}
	}

	@SuppressWarnings("unchecked")
	protected void s3dOnMouseMotionEvent(MouseMotionEvent evt, boolean guiFocus) {
		if (!mousePressed) {
			float x = ToolKit.isAndroid() ? touchXY.x : mouseXY.x;
			float y = ToolKit.isAndroid() ? touchXY.y : mouseXY.y;
			mouseFocusNode = getEventNode(x, y);
			if (!guiFocus) {
				if (mouseFocusNode != previousMouseFocusNode) {

					// TODO move all this stuff to event caster

					HoverEvent<BaseElement> fevt = new HoverEvent<>(evt,
							previousMouseFocusNode instanceof BaseElement ? ((BaseElement) previousMouseFocusNode)
									: null,
							mouseFocusElement instanceof BaseElement ? ((BaseElement) mouseFocusElement) : null,
							keyboardModifiers, HoverEventType.leave);

					if (previousMouseFocusNode instanceof HoverListener) {
						((HoverListener<BaseElement>) previousMouseFocusNode).onFocusChange(fevt);
					}
					if (previousMouseFocusNode instanceof BaseElement) {
						eventCaster.fireMouseFocusEvent((BaseElement) previousMouseFocusNode, fevt);
					}
					HoverEvent<BaseElement> fgevt = new HoverEvent<>(evt,
							mouseFocusNode instanceof BaseElement ? (BaseElement) mouseFocusNode : null,
							previousMouseFocusNode instanceof BaseElement ? (BaseElement) previousMouseFocusNode : null,
							keyboardModifiers, HoverEventType.enter);
					if (mouseFocusNode instanceof HoverListener) {
						((HoverListener<BaseElement>) mouseFocusNode).onFocusChange(fgevt);
					}
					if (mouseFocusNode instanceof BaseElement) {
						eventCaster.fireMouseFocusEvent((BaseElement) mouseFocusNode, fgevt);
					}
					previousMouseFocusNode = mouseFocusNode;
				}
				if (mouseFocusNode != null) {
					if (mouseFocusNode instanceof MouseUIWheelListener) {
						((MouseUIWheelListener<NodeEventTarget>) mouseFocusNode)
								.onMouseWheel(new MouseUIWheelEvent<NodeEventTarget>(evt,
										new NodeEventTarget(this, mouseFocusNode), keyboardModifiers));
					}
				}
				if (mouseFocusNode instanceof MouseMovementListener) {
					((MouseMovementListener<NodeEventTarget>) mouseFocusNode)
							.onMouseMove(new MouseUIMotionEvent<NodeEventTarget>(evt,
									new NodeEventTarget(this, mouseFocusNode), keyboardModifiers));
				}
			} else {
				if (previousMouseFocusNode instanceof HoverListener) {
					((HoverListener<BaseElement>) previousMouseFocusNode).onFocusChange(new HoverEvent<BaseElement>(evt,
							previousMouseFocusNode instanceof BaseElement ? (BaseElement) previousMouseFocusNode : null,
							null, keyboardModifiers, HoverEventType.leave));
					previousMouseFocusNode = null;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void s3dOnTouchDownEvent(TouchEvent evt) {
		float x = ToolKit.isAndroid() ? touchXY.x : mouseXY.x;
		float y = ToolKit.isAndroid() ? touchXY.y : mouseXY.y;
		Node target = getEventNode(x, y);
		if (target != null) {
			MouseUIButtonEvent<NodeEventTarget> mevt = new MouseUIButtonEvent<NodeEventTarget>(
					new MouseButtonEvent(0, true, (int) x, (int) y), new NodeEventTarget(this, target),
					keyboardModifiers);
			if (target instanceof MouseButtonListener) {
				((MouseButtonListener<NodeEventTarget>) target).onMouseButton(mevt);
			}
			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchDown(evt);
			}
			eventNodes.put(evt.getPointerId(), target);
		}
	}

	@SuppressWarnings("unchecked")
	private void s3dOnTouchMoveEvent(TouchEvent evt) {
		float x = ToolKit.isAndroid() ? touchXY.x : mouseXY.x;
		float y = ToolKit.isAndroid() ? touchXY.y : mouseXY.y;
		for (Integer key : eventNodes.keySet()) {
			if (key == evt.getPointerId()) {
				Node target = eventNodes.get(key);
				if (target != null) {
					if (target instanceof MouseMovementListener) {
						((MouseMovementListener<NodeEventTarget>) target)
								.onMouseMove(new MouseUIMotionEvent<NodeEventTarget>(
										new MouseMotionEvent((int) x, (int) y, (int) evt.getDeltaX(),
												(int) evt.getDeltaY(), 0, 0),
										new NodeEventTarget(this, target), keyboardModifiers));
					}
					if (target instanceof TouchListener) {
						((TouchListener) target).onTouchMove(evt);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void s3dOnTouchUpEvent(TouchEvent evt) {
		float x = ToolKit.isAndroid() ? touchXY.x : mouseXY.x;
		float y = ToolKit.isAndroid() ? touchXY.y : mouseXY.y;
		Node target = eventNodes.get(evt.getPointerId());
		if (target != null) {
			MouseUIButtonEvent<NodeEventTarget> mevt = new MouseUIButtonEvent<NodeEventTarget>(0, true, (int) x,
					(int) y, 0, 0, getClickCount(), keyboardModifiers, new NodeEventTarget(this, target));
			if (target instanceof MouseButtonListener) {
				((MouseButtonListener<NodeEventTarget>) target).onMouseButton(mevt);
			}
			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchUp(evt);
			}
			eventNodes.remove(evt.getPointerId());
		}
	}

	/**
	 * Stored the current mouse position as a Vector2f
	 * 
	 * @param x The mouse's current X coord
	 * @param y The mouse's current Y coord
	 */
	private void setMouseXY(float x, float y) {
		mouseXY.set(x, y).multLocal(this.inputScale);
	}

	private void setTouchXY(float x, float y) {
		touchXY.set(x, y).multLocal(this.inputScale);
	}

	@Override
	public ThemeInstance getThemeInstance() {
		return themeInstance;
	}

	@Override
	public BaseScreen setThemeInstance(ThemeInstance themeInstance) {
		if (!Objects.equals(themeInstance, this.themeInstance)) {
			if (this.themeInstance != null) {
				this.themeInstance.getScreens().remove(this);
			}
			this.themeInstance = themeInstance;
			if (this.themeInstance != null) {
				this.themeInstance.getScreens().add(this);
				this.themeInstance.install(this);
			}
		}
		return this;
	}

	public Application getApplication() {
		return ToolKit.get().getApplication();
	}

	public BaseScreen setFontColor(ColorRGBA fontColor) {
		if (!Objects.equals(fontColor, this.fontColor)) {
			this.fontColor = fontColor;
			dirtyLayout(false, LayoutType.text);
			layoutChildren();
		}
		return this;
	}

	@Override
	public FontSpec getFont() {
		return font;
	}

	public BaseScreen setFont(FontSpec font) {
		if (!Objects.equals(font, this.font)) {
			this.font = font;
			dirtyLayout(true, LayoutType.text());
			layoutChildren();
		}
		return this;
	}

	@Override
	public ColorRGBA getFontColor() {
		return fontColor;
	}

	@Override
	public boolean isVisibilityAllowed() {
		return true;
	}

	protected void configureScreen() {
	}

	protected void preConfigureScreen() {
	}

	@Override
	public boolean isInheritsStyles() {
		return inheritsStyles;
	}

	public void setInheritsStyles(boolean inheritsStyles) {
		this.inheritsStyles = inheritsStyles;
	}

	@Override
	public boolean isHeirarchyInitializing() {
		return false;
	}
}
