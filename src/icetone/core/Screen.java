package icetone.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.font.BitmapFont;
import com.jme3.font.plugins.BitmapFontLoader;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.InputEvent;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import com.jme3.util.SafeArrayList;

import icetone.controls.extras.android.Keyboard;
import icetone.controls.form.Form;
import icetone.controls.lists.ComboBox;
import icetone.controls.menuing.AutoHide;
import icetone.controls.text.TextField;
import icetone.controls.util.ModalBackground;
import icetone.controls.util.ToolTip;
import icetone.core.Element.Borders;
import icetone.core.Element.ZPriority;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import icetone.core.utils.ScaleUtil;
import icetone.core.utils.UIDUtil;
import icetone.effects.EffectManager;
import icetone.effects.cursor.CursorEffects;
import icetone.fonts.BitmapFontLoaderX;
import icetone.framework.core.AnimElement;
import icetone.framework.core.AnimLayer;
import icetone.framework.core.AnimManager;
import icetone.framework.core.QuadData;
import icetone.listeners.FlingListener;
import icetone.listeners.KeyboardListener;
import icetone.listeners.MouseButtonListener;
import icetone.listeners.MouseFocusListener;
import icetone.listeners.MouseMovementListener;
import icetone.listeners.MouseWheelListener;
import icetone.listeners.TabFocusListener;
import icetone.listeners.TouchListener;
import icetone.style.LayoutParser;
import icetone.style.Style;
import icetone.style.StyleLoader;
import icetone.style.StyleManager;
import icetone.style.StyleManager.CursorType;

/**
 *
 * @author t0neg0d
 */
public class Screen implements ElementManager, Control, RawInputListener {
	private static Screen defaultInstance;
	private ToolTipProvider currentProvider;

	public static Screen init(Application app) {
		if (defaultInstance != null)
			throw new IllegalStateException("Already inited.");
		defaultInstance = new Screen(app);
		return defaultInstance;
	}

	public static Screen get() {
		if (defaultInstance == null)
			throw new IllegalStateException("Not inited.");
		return defaultInstance;
	}

	public final class ZComparator implements Comparator<Element> {
		@Override
		public int compare(Element o1, Element o2) {
			return Float.valueOf(o1.getLocalTranslation().z).compareTo(o2.getLocalTranslation().z);
		}
	}

	private enum EventCheckType {
		MouseLeft, MouseRight, MouseFocus, WheelClick, WheelMove, Touch, TouchMove, Fling, None
	}

	public interface BoundsChangeHandler {

		Element getElement();

		void leftBounds(boolean draggin);

		void enteredBounds(boolean draggin);
	}

	private Element lastBounds;
	private List<BoundsChangeHandler> boundsChangeHandlers = new ArrayList<BoundsChangeHandler>();
	private Application app;
	private LayoutManager layoutManager;
	protected Spatial spatial;
	private List<Element> childList = new CopyOnWriteArrayList<Element>();
	private Map<String, Element> elements = new LinkedHashMap<>();
	private Map<String, SubScreen> subscreens = new HashMap<>();
	private Ray elementZOrderRay = new Ray();
	private Vector3f guiRayOrigin = new Vector3f();
	// New z-ordering
	private CollisionResults results;

	private boolean useMultiTouch = false;
	private Vector2f tempElementOffset = new Vector2f();
	private Map<Integer, Vector2f> elementOffsets = new HashMap<>();
	private Map<Integer, Element> contactElements = new HashMap<>();
	private Map<Integer, Element> eventElements = new HashMap<>();
	private Map<Integer, Borders> eventElementResizeDirections = new HashMap<>();

	private Element eventElement = null;
	private Element targetElement = null;
	private Element keyboardElement = null;
	private Element tabFocusElement = null;
	private Form focusForm = null;
	private Vector2f eventElementOriginXY = new Vector2f();
	private float eventElementOffsetX = 0;
	private float eventElementOffsetY = 0;
	private float targetElementOffsetX = 0;
	private float targetElementOffsetY = 0;
	private Borders eventElementResizeDirection = null;
	private Element mouseFocusElement = null;
	private Element mouseWheelElement = null;
	private Element contactElement = null;
	private Element previousMouseFocusElement = null;
	private boolean focusElementIsMovable = false;
	private boolean mousePressed = false;
	private boolean mouseLeftPressed = false;
	private boolean mouseRightPressed = false;
	private boolean mouseWheelPressed = false;
	private CollisionResult lastCollision;
	private Element extendedToolTip;
	private Element currentMouseFocusElement;
	private String clipboardText = "";
	private CursorEffects cursorEffects;
	private StyleManager styleManager;
	private LayoutParser layoutParser;
	protected EffectManager effectManager;
	protected AnimManager animManager;

	protected Node t0neg0dGUI = new Node("t0neg0dGUI");

	private Vector2f touchXY = new Vector2f(0, 0);
	private Vector2f mouseXY = new Vector2f(0, 0);
	private boolean SHIFT = false;
	private boolean CTRL = false;
	private boolean ALT = false;

	private boolean useCustomCursors = false;
	private boolean forceCursor = false;
	private CursorType currentCursor = CursorType.POINTER;

	private boolean useToolTips = false;
	private ToolTip toolTip = null;
	private float toolTipMaxWidth = 250;
	private String forcedToolTipText = "";
	private boolean forcedToolTip = false;

	private float globalAlpha = 1.0f;

	private boolean useUIAudio = false;
	private float uiAudioVolume = 1;

	private boolean useCursorEffects = false;

	private Clipboard clipboard;
	private boolean clipboardActive = false;

	private boolean useTextureAtlas = false;
	private Texture atlasTexture;

	private ElementQuadGrid mesh;

	private BitmapFont defaultGUIFont;

	private ModalBackground modalBackground;
	private Keyboard virtualKeys;

	// AnimLayer & 2D framework support
	private Map<String, AnimLayer> layers = new LinkedHashMap();
	private AnimElement eventAnimElement = null;
	private QuadData eventQuad = null;
	private AnimElement targetAnimElement = null;
	private QuadData targetQuad = null;
	private AnimElement mouseFocusAnimElement = null;
	private AnimElement previousMouseFocusAnimElement = null;
	private AnimElement mouseFocusQuad = null;
	private AnimElement mouseWheelAnimElement = null;
	private float eventAnimOffsetX = 0;
	private float eventAnimOffsetY = 0;
	private float eventQuadOffsetX = 0;
	private float eventQuadOffsetY = 0;

	// SubScreen collisions
	private Vector2f click2d = new Vector2f(), tempV2 = new Vector2f();
	private Vector3f click3d = new Vector3f(), pickOrigin = new Vector3f(), pickDir = new Vector3f();
	private Ray pickRay = new Ray();
	private int[] indices = new int[3];
	private Vector3f cp = new Vector3f(), v1 = new Vector3f(), v2 = new Vector3f(), v3 = new Vector3f(), weights = new Vector3f();
	private Vector2f uv = new Vector2f(), uv1 = new Vector2f(), uv2 = new Vector2f(), uv3 = new Vector2f();

	private boolean initializedLoader;

	// 3D scene support
	private boolean use3DSceneSupport = false;
	private Node mouseFocusNode = null;
	private Node eventNode = null;
	private Node previousMouseFocusNode = null;
	private SafeArrayList<Node> scenes = new SafeArrayList(Node.class);
	private Map<Integer, Node> eventNodes = new HashMap();

	// Android input scaling
	float inputScale = 1;

	float orWidth, orHeight;
	boolean orDim = false;

	private ScaleUtil scaleManager;

	// CSS
	private List<Stylesheet> stylessheets = new ArrayList<>();
	private boolean layoutDirty;
	private boolean layingOut;
	private boolean mouseButtonsEnabled = true;

	/**
	 * Creates a new instance of the Screen control using the default style
	 * information
	 * provided with the library.
	 * 
	 * @param app
	 *            A JME Application
	 */
	public Screen(Application app) {
		this(app, "icetone/style/def/style_map.gui.xml");
	}

	/**
	 * Creates an instance of the Screen control.
	 * 
	 * @param app
	 *            A JME Application
	 * @param styleMap
	 *            A path to the style_map.xml file containing the custom theme
	 *            information
	 */
	public Screen(Application app, String styleMap) {
		if (defaultInstance == null) {
			defaultInstance = this;
		}

		if (!initializedLoader) {
			app.getAssetManager().registerLoader(StyleLoader.class, "gui.xml");
			initializedLoader = true;
		}

		this.app = app;
		this.elementZOrderRay.setDirection(Vector3f.UNIT_Z);
		// New z-ordering
		this.results = new CollisionResults();
		try {
			app.getAssetManager().unregisterLoader(BitmapFontLoader.class);
			app.getAssetManager().registerLoader(BitmapFontLoaderX.class, "fnt");
		} catch (Exception ex) {
		}

		styleManager = new StyleManager(this, styleMap);
		styleManager.parseStyles(styleMap);
		effectManager = new EffectManager(this);
		animManager = new AnimManager(this);
		app.getInputManager().addRawInputListener(this);
		layoutParser = new LayoutParser(this);

		defaultGUIFont = app.getAssetManager().loadFont(styleManager.getStyle("Font").getString("defaultFont"));

		scenes.add((Node) app.getViewPort().getScenes().get(0));

		scaleManager = new ScaleUtil(this);
		scaleManager.initialize();
	}

	public Screen(Application app, String styleMap, float width, float height) {
		if (!initializedLoader) {
			app.getAssetManager().registerLoader(StyleLoader.class, "gui.xml");
			initializedLoader = true;
		}
		this.orWidth = width;
		this.orHeight = height;
		this.orDim = true;
		this.inputScale = 1f / (app.getViewPort().getCamera().getWidth() / width);

		this.app = app;
		this.elementZOrderRay.setDirection(Vector3f.UNIT_Z);
		this.results = new CollisionResults();
		try {
			app.getAssetManager().unregisterLoader(BitmapFontLoader.class);
			app.getAssetManager().registerLoader(BitmapFontLoaderX.class, "fnt");
		} catch (Exception ex) {
		}

		styleManager = new StyleManager(this, styleMap);
		styleManager.parseStyles(styleMap);
		effectManager = new EffectManager(this);
		animManager = new AnimManager(this);
		app.getInputManager().addRawInputListener(this);
		layoutParser = new LayoutParser(this);

		scenes.add((Node) app.getViewPort().getScenes().get(0));

		scaleManager = new ScaleUtil(this);
		scaleManager.initialize();
	}

	/**
	 * Returns the JME application associated with the Screen
	 * 
	 * @return Application app
	 */
	@Override
	public Application getApplication() {
		return this.app;
	}

	public void addBoundsChangeHandler(BoundsChangeHandler boundsChangeHandler) {
		boundsChangeHandlers.add(boundsChangeHandler);
	}

	public void removeBoundsChangeHandler(BoundsChangeHandler boundsChangeHandler) {
		boundsChangeHandlers.add(boundsChangeHandler);
	}

	@Override
	public ScaleUtil getScaleManager() {
		return this.scaleManager;
	}

	public List<Stylesheet> getStylesheets() {
		return stylessheets;
	}

	@Override
	public float scaleFloat(float in) {
		return in * scaleManager.getGameScale();
	};

	@Override
	public Vector2f scaleVector2f(Vector2f in) {
		return in.mult(scaleManager.getGameScale());
	};

	@Override
	public Vector3f scaleVector3f(Vector3f in) {
		return in.mult(scaleManager.getGameScale());
	};

	@Override
	public Vector4f scaleVector4f(Vector4f in) {
		return in.mult(scaleManager.getGameScale());
	};

	@Override
	public float scaleFontSize(float in) {
		return in * scaleManager.getFontScale();
	};

	/**
	 * Return the width of the current Viewport
	 * 
	 * @return float width
	 */
	@Override
	public float getWidth() {
		return (orDim) ? orWidth : app.getViewPort().getCamera().getWidth();
	}

	/**
	 * Returns the height of the current Viewport
	 * 
	 * @return float height
	 */
	@Override
	public float getHeight() {
		return (orDim) ? orHeight : app.getViewPort().getCamera().getHeight();
	}

	/**
	 * Initializes the Screen control
	 */
	@Deprecated
	public void initialize() {
		// app.getInputManager().addRawInputListener(this);

		// if (getUseCustomCursors())
		// setCursor(CursorType.POINTER);
	}

	// <editor-fold desc="Basic Element Handling">
	public ElementQuadGrid getDefaultMesh() {
		return mesh;
	}

	/**
	 * Adds an Element to the Screen and scene graph
	 * 
	 * @param element
	 *            The Element to add
	 */
	@Override
	public void addElement(Element element) {
		addElement(element, null, false);
	}

	public final void layoutChildren() {
		if (layingOut) {
			return;
		}
		layingOut = true;
		try {
			if (Element.FORCE_LAYOUT)
				dirtyLayout();

			if (layoutDirty && layoutManager != null) {
				layoutManager.layoutScreen(this);
			}
			layoutHeirarchy(null);
			layoutDirty = false;
		} finally {
			layingOut = false;
		}
	}

	public void dirtyLayout() {
		if (!layoutDirty) {
			layoutDirty = true;
			for (Element e : childList)
				e.dirtyLayout(true);
		}
	}

	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	@SuppressWarnings("unchecked")
	public <T extends Element> T getElementByClass(Class<T> type) {
		for (Element el : getElements()) {
			if (el.getClass().equals(type)) {
				return (T) el;
			}
		}
		return null;
	}

	/**
	 * Adds an Element to the Screen and scene graph
	 * 
	 * @param element
	 *            The Element to add
	 */
	@Override
	public void addElement(Element element, Object constraints) {
		addElement(element, constraints, false);
	}

	/**
	 * Adds an Element to the Screen and scene graph
	 * 
	 * @param element
	 *            The Element to add
	 */
	@Override
	public void addElement(Element element, Object constraints, boolean hide) {
		if (element instanceof AutoHide)
			hide = true;

		if (getElementById(element.getUID()) != null) {
			try {
				throw new ConflictingIDException();
			} catch (ConflictingIDException ex) {
				Logger.getLogger(Element.class.getName()).log(Level.SEVERE, "The child element '" + element.getUID() + "' ("
						+ element.getClass() + ") conflicts with a previously added child element in parent Screen.", ex);
				System.exit(0);
			}
		} else {
			// New z-ordering
			synchronized (elements) {
				childList.add(element);
				elements.put(element.getUID(), element);
			}

			if (!element.getInitialized()) {
				initializeElement(element);
			}

			if (!hide) {
				t0neg0dGUI.attachChild(element);
			} else {
				element.setVisibleState(false);
			}
		}

		if (layoutManager != null) {
			layoutManager.constrain(element, constraints);
		}
		dirtyLayout();
		layoutChildren();

	}

	/**
	 * Removes an Element from the Screen and scene graph
	 * 
	 * @param element
	 *            The Element to remove
	 */
	@Override
	public void removeElement(Element element) {
		elements.remove(element.getUID());
		childList.remove(element);
		element.removeFromParent();
		element.cleanup();

		if (getUseToolTips()) {
			if (getToolTipFocus() == element)
				hideToolTip();
			else if (getToolTipFocus() != null) {
				if (element.getChildElementById(getToolTipFocus().getUID()) != null)
					hideToolTip();
			}
		}
		dirtyLayout();
		layoutChildren();
	}

	/**
	 * Returns the Element with the associated ID. If not found, returns null
	 * 
	 * @param UID
	 *            The String ID of Element to find
	 * @return Element element
	 */
	@Override
	public Element getElementById(String UID) {
		Element ret = null;
		synchronized (elements) {
			if (elements.containsKey(UID)) {
				ret = elements.get(UID);
			} else {
				for (Element el : new ArrayList<Element>(elements.values())) {
					ret = el.getChildElementById(UID);
					if (ret != null) {
						break;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Returns the screen level elements Map
	 * 
	 * @return
	 */
	public Map<String, Element> getElementsAsMap() {
		return this.elements;
	}

	/**
	 * Returns the screen level elements as a Collection
	 * 
	 * @return
	 */
	public Collection<Element> getElements() {
		return this.elements.values();
	}

	/**
	 * Returns the guiNode used by the Screen
	 * 
	 * @return Node
	 */
	@Override
	public Node getGUINode() {
		return t0neg0dGUI;
	}
	// </editor-fold>

	// <editor-fold desc="Texture Atlas Support">
	public void setUseTextureAtlas(boolean useTextureAtlas, String texturePath) {
		this.useTextureAtlas = useTextureAtlas;

		if (texturePath != null) {
			atlasTexture = app.getAssetManager().loadTexture(texturePath);
			atlasTexture.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			atlasTexture.setMagFilter(Texture.MagFilter.Bilinear);
			atlasTexture.setWrap(Texture.WrapMode.Clamp);
		} else {
			atlasTexture = null;
		}
	}

	@Override
	public boolean getUseTextureAtlas() {
		return this.useTextureAtlas;
	}

	@Override
	public Texture getAtlasTexture() {
		return atlasTexture;
	}

	@Override
	public Texture createNewTexture(String texturePath) {
		Texture newTex = app.getAssetManager().loadTexture(texturePath);
		newTex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
		newTex.setMagFilter(Texture.MagFilter.Bilinear);
		newTex.setWrap(Texture.WrapMode.Clamp);
		return newTex;
	}

	@Override
	public float[] parseAtlasCoords(String texturePath) {
		float[] coords = new float[4];

		if (texturePath != null) {
			StringTokenizer st = new StringTokenizer(texturePath, "|");
			if (st.countTokens() == 4) {
				try {
					String token = st.nextToken();
					coords[0] = Float.parseFloat(token.substring(token.indexOf('=') + 1));
					token = st.nextToken();
					coords[1] = Float.parseFloat(token.substring(token.indexOf('=') + 1));
					token = st.nextToken();
					coords[2] = Float.parseFloat(token.substring(token.indexOf('=') + 1));
					token = st.nextToken();
					coords[3] = Float.parseFloat(token.substring(token.indexOf('=') + 1));
				} catch (Exception ex) {
					throwParserException();
				}
			} // else throwParserException(texturePath);
		}
		return coords;
	}

	// </editor-fold>

	/**
	 * Get the topmost current global modal element.
	 */
	public Element getModalElement() {
		List<Element> l = getModalElements();
		return l.isEmpty() ? null : l.get(0);
	}

	public List<Element> getModalElements() {
		List<Element> l = new ArrayList<Element>();
		synchronized (elements) {
			for (Element e : elements.values()) {
				if (e.getIsModal() || e.getIsVisibleAsModal()) {
					l.add(e);
				}
			}
		}
		Collections.sort(l, new ZComparator());
		return l;
	}

	public boolean isTop(Element element) {
		int indexOf = childList.indexOf(element);
		return indexOf != -1 && indexOf == childList.size() - 1;
	}

	/**
	 * Brings the element specified to the front of the zOrder list shifting
	 * other below to keep all
	 * Elements within the current z-order range.
	 * 
	 * @param topMost
	 *            The Element to bring to the front
	 */
	@Override
	public void updateZOrder(Element topMost) {
		// zOrderCurrent = zOrderInit;

		synchronized (elements) {
			if (!childList.contains(topMost)) {
				// Not a child of the screen
				// System.out.println("Updating non-childing to TOP " +
				// topMost);
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
		topMost.movedToFrontHook();

	}

	// </editor-fold>

	// <editor-fold desc="Input Handlers">
	public void forceFocusElementRefresh() {
		mouseFocusElement = getEventElement(mouseXY.x, mouseXY.y, EventCheckType.None);
		eventElement = mouseFocusElement;
		if (getUseToolTips())
			updateToolTipLocation();
	}

	public ToolTipProvider createDefaultToolTipProvider(Element element) {
		return null;
	}

	/**
	 * Returns a Vector2f containing the last stored mouse X/Y coords
	 * 
	 * @return Vector2f mouseXY
	 */
	@Override
	public Vector2f getMouseXY() {
		return this.mouseXY;
	}

	@Override
	public Vector2f getTouchXY() {
		return this.touchXY;
	}

	// Raw Input handlers
	@Override
	public void beginInput() {
		// throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void endInput() {
		// throw new UnsupportedOperationException("Not supported yet.");
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
	public void onMouseMotionEvent(MouseMotionEvent evt) {
		if (Element.NEW_YFLIPPING) {
			setMouseXY(evt.getX(), getHeight() - evt.getY());
		} else {
			setMouseXY(evt.getX(), evt.getY());
		}

		final MouseMotionEvent origEvt = evt;
		evt = new MouseMotionEvent(evt.getX(), (int) (getHeight() - evt.getY()), evt.getDX(), evt.getDY(), evt.getWheel(),
				evt.getDeltaWheel()) {
			@Override
			public void setConsumed() {
				super.setConsumed();
				origEvt.setConsumed();
			}

		};
		evt.setTime(evt.getTime());
		// }

		if (this.useCursorEffects) {
			if (app.getInputManager().isCursorVisible())
				cursorEffects.updatePosition(mouseXY);
		}
		if (useToolTips)
			updateToolTipLocation();
		if (!mousePressed) {
			mouseFocusElement = getEventElement(mouseXY.x, mouseXY.y, EventCheckType.MouseFocus);
			if (mouseFocusElement != null) {
				// System.out.println("EL> " + mouseFocusElement + " lz: " +
				// mouseFocusElement.getLocalTranslation().z + " / " +
				// mouseFocusElement.getWorldTranslation().z);
				if (useCustomCursors) {
					if (mouseFocusElement.getIsResizable()) {
						float offsetX = mouseXY.x;
						float offsetY = Element.NEW_YFLIPPING ? mouseXY.y : getHeight() - mouseXY.y;
						Element el = mouseFocusElement;

						if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.getResizeBorderWestSize()
								&& offsetY > LUtil.getAbsoluteY(el)
								&& offsetY < LUtil.getAbsoluteY(el) + el.getResizeBorderNorthSize()) {
							if (el.getResizeW() && el.getResizeN())
								this.setCursor(CursorType.RESIZE_CNW);
							else if (el.getResizeW())
								this.setCursor(CursorType.RESIZE_EW);
							else if (el.getResizeN())
								this.setCursor(CursorType.RESIZE_NS);
						} else if (offsetX > (el.getAbsoluteWidth() - el.getResizeBorderEastSize())
								&& offsetX < el.getAbsoluteWidth() && offsetY > LUtil.getAbsoluteY(el)
								&& offsetY < LUtil.getAbsoluteY(el) + el.getResizeBorderNorthSize()) {
							if (el.getResizeE() && el.getResizeN())
								this.setCursor(CursorType.RESIZE_CNE);
							else if (el.getResizeE())
								this.setCursor(CursorType.RESIZE_EW);
							else if (el.getResizeN())
								this.setCursor(CursorType.RESIZE_NS);
						} else if (offsetY > LUtil.getAbsoluteY(el)
								&& offsetY < LUtil.getAbsoluteY(el) + el.getResizeBorderNorthSize()) {
							if (el.getResizeN())
								this.setCursor(CursorType.RESIZE_NS);
						} else if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.getResizeBorderWestSize()
								&& offsetY > (LUtil.getAbsoluteHeight(el) - el.getResizeBorderSouthSize())
								&& offsetY < LUtil.getAbsoluteHeight(el)) {
							if (el.getResizeW() && el.getResizeS())
								this.setCursor(CursorType.RESIZE_CNE);
							else if (el.getResizeW())
								this.setCursor(CursorType.RESIZE_EW);
							else if (el.getResizeS())
								this.setCursor(CursorType.RESIZE_NS);
						} else if (offsetX > (el.getAbsoluteWidth() - el.getResizeBorderEastSize())
								&& offsetX < el.getAbsoluteWidth()
								&& offsetY > (LUtil.getAbsoluteHeight(el) - el.getResizeBorderSouthSize())
								&& offsetY < LUtil.getAbsoluteHeight(el)) {
							if (el.getResizeE() && el.getResizeS())
								this.setCursor(CursorType.RESIZE_CNW);
							else if (el.getResizeE())
								this.setCursor(CursorType.RESIZE_EW);
							else if (el.getResizeS())
								this.setCursor(CursorType.RESIZE_NS);
						} else if (offsetY > (LUtil.getAbsoluteHeight(el) - el.getResizeBorderSouthSize())
								&& offsetY < LUtil.getAbsoluteHeight(el)) {
							if (el.getResizeS())
								this.setCursor(CursorType.RESIZE_NS);
						} else if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.getResizeBorderWestSize()) {
							if (el.getResizeW())
								this.setCursor(CursorType.RESIZE_EW);
						} else if (offsetX > (el.getAbsoluteWidth() - el.getResizeBorderEastSize())
								&& offsetX < el.getAbsoluteWidth()) {
							if (el.getResizeE())
								this.setCursor(CursorType.RESIZE_EW);
						} else {
							this.setCursor(CursorType.POINTER);
						}
					} else {
						if (currentCursor != CursorType.HAND && currentCursor != CursorType.TEXT)
							this.setCursor(CursorType.POINTER);
					}
				} else {
					this.setCursor(CursorType.POINTER);
				}
			}
			if (mouseFocusElement != previousMouseFocusElement) {
				if (previousMouseFocusElement instanceof MouseFocusListener) {
					((MouseFocusListener) previousMouseFocusElement).onLoseFocus(evt);
				}
				if (mouseFocusElement instanceof MouseFocusListener) {
					((MouseFocusListener) mouseFocusElement).onGetFocus(evt);
				}
				previousMouseFocusElement = mouseFocusElement;
			}
			if (mouseFocusElement != null) {
				focusElementIsMovable = mouseFocusElement.getIsMovable();

			}
			if (mouseFocusElement instanceof MouseMovementListener) {
				((MouseMovementListener) mouseFocusElement).onMouseMove(evt);
			}
			mouseWheelElement = getEventElement(mouseXY.x, mouseXY.y, EventCheckType.WheelMove);
			if (mouseWheelElement instanceof MouseWheelListener) {
				if (evt.getDeltaWheel() > 0) {
					((MouseWheelListener) mouseWheelElement).onMouseWheelDown(evt);
				} else if (evt.getDeltaWheel() < 0) {
					((MouseWheelListener) mouseWheelElement).onMouseWheelUp(evt);
				}
			}
		} else {
			if (eventElement != null) {
				if (mouseLeftPressed) {
					focusElementIsMovable = contactElement.getIsMovable();
					if (eventElementResizeDirection != null) {
						eventElement.resize(mouseXY.x, mouseXY.y, eventElementResizeDirection);
					} else if (focusElementIsMovable) {
						eventElement.moveTo(mouseXY.x - eventElementOffsetX, mouseXY.y - eventElementOffsetY);
					}
				}

				if (eventElement instanceof MouseMovementListener) {
					((MouseMovementListener) eventElement).onMouseMove(evt);
				}
			}
		}
		if (!subscreens.isEmpty() && !evt.isConsumed()) {
			setLastCollision();
			if (lastCollision != null) {
				// TODO: Add ray casting to subscreen
				for (SubScreen s : subscreens.values()) {
					if (s.getGeometry() == lastCollision.getGeometry()) {
						s.onMouseMotionEvent(evt, (MouseMotionEvent) getORSTCEvent(s, evt, 0));
					}
				}
			}
		}
		if (!mousePressed && mouseFocusElement == null) {
			if (currentCursor != CursorType.POINTER)
				this.setCursor(CursorType.POINTER);
		}

		// 2D Framework
		if (mouseFocusElement == null) {
			if (!mousePressed) {
				if (previousMouseFocusAnimElement != null) {
					if (previousMouseFocusAnimElement instanceof MouseFocusListener) {
						((MouseFocusListener) previousMouseFocusAnimElement).onLoseFocus(evt);
						previousMouseFocusAnimElement = null;
					}
				}
				// getAnimEventTargets(evt.getX(), evt.getY());
				if (eventAnimElement != null) {
					mouseFocusAnimElement = eventAnimElement;
					if (eventAnimElement instanceof MouseFocusListener) {
						((MouseFocusListener) mouseFocusAnimElement).onGetFocus(evt);
					}
					previousMouseFocusAnimElement = mouseFocusAnimElement;
				}
			} else {
				if (eventAnimElement != null) {
					if (eventAnimElement.getIsMovable()) {

					} else if (eventQuad.getIsMovable()) {
						eventQuad.setPosition(mouseXY.x - eventQuadOffsetX, mouseXY.y - eventQuadOffsetY);
					}
				}
			}
		}

		if (use3DSceneSupport) {
			s3dOnMouseMotionEvent(evt, mouseFocusElement != null || mouseFocusAnimElement != null);
		}

		// See if in bounds of any element the handlers are interested in
		Element newBounds = null;
		for (BoundsChangeHandler h : boundsChangeHandlers) {
			Element el = h.getElement();
			if (evt.getX() >= el.getAbsoluteX() && evt.getX() <= el.getAbsoluteWidth() && evt.getY() >= el.getAbsoluteY()
					&& evt.getY() <= el.getAbsoluteHeight()) {
				newBounds = el;
			}
		}

		// Now inform each handler if the element has changed
		if ((newBounds == null && lastBounds != null) || (newBounds != null && lastBounds == null)
				|| (newBounds != null && !newBounds.equals(lastBounds))) {

			for (BoundsChangeHandler h : boundsChangeHandlers) {
				Element el = h.getElement();
				if (el.equals(lastBounds)) {
					h.leftBounds(mousePressed);
				} else if (el.equals(newBounds)) {
					h.enteredBounds(mousePressed);
				}

				lastBounds = newBounds;

			}

		}
	}

	@Override
	public void onMouseButtonEvent(MouseButtonEvent evt) {
		if (!useMultiTouch && mouseButtonsEnabled) {

			if (Element.NEW_YFLIPPING) {
				setMouseXY(evt.getX(), getHeight() - evt.getY());
			} else {
				setMouseXY(evt.getX(), evt.getY());
			}

			// if (Element.NEW_YFLIPPING) {
			final MouseButtonEvent origEvt = evt;
			evt = new MouseButtonEvent(evt.getButtonIndex(), evt.isPressed(), evt.getX(), (int) (getHeight() - evt.getY())) {
				@Override
				public void setConsumed() {
					super.setConsumed();
					origEvt.setConsumed();
				}

			};
			evt.setTime(evt.getTime());
			// }

			EventCheckType check = null;
			if (evt.getButtonIndex() == 0)
				check = EventCheckType.MouseLeft;
			else if (evt.getButtonIndex() == 1)
				check = EventCheckType.MouseRight;
			else
				check = EventCheckType.WheelClick;

			if (evt.isPressed()) {
				mousePressed = true;
				eventElement = getEventElement(mouseXY.x, mouseXY.y, check);
				// if (eventElement != null) {
				// if (eventElement.getResetKeyboardFocus())
				// resetTabFocusElement();
				// } else
				// resetTabFocusElement();

				if (this.useCursorEffects) {
					cursorEffects.handleClick(evt.getButtonIndex());
				}
				switch (evt.getButtonIndex()) {
				case 0:
					mouseLeftPressed = true;
					// eventElement = getEventElement(evt.getX(), evt.getY());
					if (eventElement != null) {
						if (eventElement.getAbsoluteParent().getEffectZOrder())
							updateZOrder(eventElement.getAbsoluteParent());
						if (eventElement.getIsBringToFrontOnClick())
							eventElement.bringToFront();
						if (eventElement.getResetKeyboardFocus())
							this.setTabFocusElement(eventElement);

						if (eventElement.getIsDragDropDragElement())
							targetElement = null;
						if (eventElement.getIsResizable()) {
							float offsetX = mouseXY.x;
							float offsetY = mouseXY.y;
							Element el = eventElement;
							if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.getResizeBorderWestSize()) {
								// West

								if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.getResizeBorderNorthSize()) {
									eventElementResizeDirection = Borders.NW;
								} else if (offsetY > (LUtil.getAbsoluteHeight(el) - el.getResizeBorderSouthSize())
										&& offsetY < LUtil.getAbsoluteHeight(el)) {
									eventElementResizeDirection = Borders.SW;
								} else {
									eventElementResizeDirection = Borders.W;
								}
							} else if (offsetX > (el.getAbsoluteWidth() - el.getResizeBorderEastSize())
									&& offsetX < el.getAbsoluteWidth()) {
								// East

								if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.getResizeBorderNorthSize()) {
									eventElementResizeDirection = Borders.NE;
								} else if (offsetY > (el.getAbsoluteHeight() - el.getResizeBorderSouthSize())
										&& offsetY < el.getAbsoluteHeight()) {
									eventElementResizeDirection = Borders.SE;
								} else {
									eventElementResizeDirection = Borders.E;
								}
							} else {
								if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.getResizeBorderNorthSize()) {
									eventElementResizeDirection = Borders.N;
								} else if (offsetY > (el.getAbsoluteHeight() - el.getResizeBorderSouthSize())
										&& offsetY < el.getAbsoluteHeight()) {
									eventElementResizeDirection = Borders.S;
								}
							}
							if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
								if (keyboardElement instanceof TextField)
									((TextField) keyboardElement).resetTabFocus();
							}
							if (eventElement.getResetKeyboardFocus())
								keyboardElement = null;
						} else if (eventElement.getIsMovable() && eventElementResizeDirection == null) {
							eventElementResizeDirection = null;
							if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
								if (keyboardElement instanceof TextField)
									((TextField) keyboardElement).resetTabFocus();
							}
							if (eventElement.getResetKeyboardFocus())
								keyboardElement = null;
							eventElementOriginXY.set(eventElement.getPosition());
						} else if (eventElement instanceof KeyboardListener) {
							if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
								if (keyboardElement instanceof TextField)
									((TextField) keyboardElement).resetTabFocus();
							}
							if (eventElement.getResetKeyboardFocus())
								keyboardElement = eventElement;
							if (keyboardElement instanceof TextField) {
								((TextField) keyboardElement).setTabFocus();
								if (Screen.isAndroid())
									showVirtualKeyboard();
								// ((TextField)keyboardElement).setCaretPositionByX(evt.getX());
							}
							// TODO: Update target element's font shader
						} else {
							eventElementResizeDirection = null;
							if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
								if (keyboardElement instanceof TextField)
									((TextField) keyboardElement).resetTabFocus();
							}
							if (eventElement.getResetKeyboardFocus())
								keyboardElement = null;
						}
						if (eventElement instanceof MouseButtonListener) {
							((MouseButtonListener) eventElement).onMouseLeftPressed(evt);
						}
						if (keyboardElement == null)
							if (Screen.isAndroid())
								hideVirtualKeyboard();
						evt.setConsumed();
					} else {
						defaultClick();
					}

					// 2D Framework
					if (eventElement == null) {
						if (eventAnimElement != null) {
							setAnimElementZOrder();
							if (eventAnimElement instanceof MouseButtonListener) {
								((MouseButtonListener) eventAnimElement).onMouseLeftPressed(evt);
							}
							evt.setConsumed();
						}
					}
					break;
				case 1:
					mouseRightPressed = true;
					// eventElement = getEventElement(evt.getX(), evt.getY());
					if (eventElement != null) {
						if (eventElement.getAbsoluteParent().getEffectZOrder())
							updateZOrder(eventElement.getAbsoluteParent());
						if (eventElement instanceof MouseButtonListener) {
							((MouseButtonListener) eventElement).onMouseRightPressed(evt);
						}
						evt.setConsumed();
					} else {
						// 2D Framework
						if (eventAnimElement != null) {
							setAnimElementZOrder();
							if (eventAnimElement instanceof MouseButtonListener) {
								((MouseButtonListener) eventAnimElement).onMouseRightPressed(evt);
							}
							evt.setConsumed();
						}
					}
					break;
				case 2:
					mouseWheelPressed = true;
					// eventElement = getEventElement(evt.getX(), evt.getY());
					if (eventElement != null) {
						if (eventElement instanceof MouseWheelListener) {
							((MouseWheelListener) eventElement).onMouseWheelPressed(evt);
						}
						evt.setConsumed();
					} else {
						// 2D Framework
						if (eventAnimElement != null) {
							setAnimElementZOrder();
							if (eventAnimElement instanceof MouseWheelListener) {
								((MouseWheelListener) eventAnimElement).onMouseWheelPressed(evt);
							}
							evt.setConsumed();
						}
					}
					break;
				}
			} else if (evt.isReleased()) {
				handleMenuState();
				switch (evt.getButtonIndex()) {
				case 0:
					mouseLeftPressed = false;
					eventElementResizeDirection = null;
					// if (eventElement.getIsDragDropDragElement())
					targetElement = getTargetDropElement(mouseXY.x, mouseXY.y);
					if (eventElement instanceof MouseButtonListener) {
						((MouseButtonListener) eventElement).onMouseLeftReleased(evt);
					}
					if (eventElement != null)
						evt.setConsumed();
					else {
						if (eventAnimElement != null) {
							if (eventAnimElement instanceof MouseButtonListener) {
								((MouseButtonListener) eventAnimElement).onMouseLeftReleased(evt);
							}
							evt.setConsumed();
						}
					}
					break;
				case 1:
					mouseRightPressed = false;
					if (eventElement instanceof MouseButtonListener) {
						((MouseButtonListener) eventElement).onMouseRightReleased(evt);
					}
					if (eventElement != null)
						evt.setConsumed();
					else {
						if (eventAnimElement != null) {
							if (eventAnimElement instanceof MouseButtonListener) {
								((MouseButtonListener) eventAnimElement).onMouseRightReleased(evt);
							}
							evt.setConsumed();
						}
					}
					break;
				case 2:
					mouseWheelPressed = false;
					if (eventElement instanceof MouseWheelListener) {
						((MouseWheelListener) eventElement).onMouseWheelReleased(evt);
					}
					if (eventElement != null)
						evt.setConsumed();
					else {
						if (eventAnimElement != null) {
							if (eventAnimElement instanceof MouseWheelListener) {
								((MouseWheelListener) eventAnimElement).onMouseWheelReleased(evt);
							}
							evt.setConsumed();
						}
					}
					break;
				}
				mousePressed = false;
				eventElement = null;
			}
			if (!subscreens.isEmpty()) {
				setLastCollision();
				if (lastCollision != null) {
					// TODO: Add ray casting to subscreen
					for (SubScreen s : subscreens.values()) {
						if (s.getGeometry() == lastCollision.getGeometry()) {
							s.onMouseButtonEvent(evt, (MouseButtonEvent) getORSTCEvent(s, evt, 1));
						}
					}
				}
			}

			if (use3DSceneSupport && !evt.isConsumed()) {
				s3dOnMouseButtonEvent(evt);
			}
		}
	}

	@Override
	public void onKeyEvent(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			if (evt.isPressed())
				SHIFT = true;
			else
				SHIFT = false;
		}
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			if (evt.isPressed())
				CTRL = true;
			else
				CTRL = false;
		}
		if (evt.getKeyCode() == KeyInput.KEY_LMENU || evt.getKeyCode() == KeyInput.KEY_RMENU) {
			if (evt.isPressed())
				ALT = true;
			else
				ALT = false;
		}
		if (evt.getKeyCode() == KeyInput.KEY_TAB && evt.isPressed()) {
			if (focusForm != null) {
				if (!SHIFT)
					focusForm.tabNext();
				else
					focusForm.tabPrev();
			}
		} else {
			if (keyboardElement != null) {
				if (keyboardElement.getParent() != null && keyboardElement.getIsVisible()) {
					if (evt.isPressed()) {
						((KeyboardListener) keyboardElement).onKeyPress(evt);
					} else if (evt.isReleased()) {
						((KeyboardListener) keyboardElement).onKeyRelease(evt);
					}
				}
			}
		}
	}
	// </editor-fold>

	// <editor-fold desc="Android Event Support">
	public void setUseMultiTouch(boolean useMultiTouch) {
		this.useMultiTouch = useMultiTouch;
		app.getInputManager().setSimulateMouse(!useMultiTouch);
		app.getInputManager().setSimulateKeyboard(!useMultiTouch);
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
				break;
			}
		}
	}

	// <editor-fold desc="SubScreen Support (OSR Collision)">
	/**
	 * Adds an Element to the Screen and scene graph
	 * 
	 * @param element
	 *            The Element to add
	 */
	public void addSubScreen(SubScreen subscreen) {
		if (getSubScreenById(subscreen.getUID()) != null) {
			try {
				throw new ConflictingIDException();
			} catch (ConflictingIDException ex) {
				Logger.getLogger(Element.class.getName()).log(Level.SEVERE, "The SubScreen '" + subscreen.getUID() + "' ("
						+ subscreen.getClass() + ") conflicts with a previously added subscreen in parent Screen.", ex);
				System.exit(0);
			}
		} else {
			subscreens.put(subscreen.getUID(), subscreen);
		}
	}

	/**
	 * Removes an Element from the Screen and scene graph
	 * 
	 * @param element
	 *            The Element to remove
	 */
	public void removeSubScreen(SubScreen subscreen) {
		subscreens.remove(subscreen.getUID());
	}

	/**
	 * Returns the Element with the associated ID. If not found, returns null
	 * 
	 * @param UID
	 *            The String ID of Element to find
	 * @return Element element
	 */
	public SubScreen getSubScreenById(String UID) {
		SubScreen ret = null;
		if (subscreens.containsKey(UID)) {
			ret = subscreens.get(UID);
		}
		return ret;
	}

	@Override
	public CollisionResult getLastCollision() {
		return lastCollision;
	}
	// </editor-fold>

	public void forceEventElement(Element element) {
		float x = element.getAbsoluteX() + 1;
		float y = element.getAbsoluteY() + 1;
		eventElement = getEventElement(x, y, EventCheckType.None);
		if (eventElement != null) {
			if (eventElement.getAbsoluteParent().getEffectZOrder())
				updateZOrder(eventElement.getAbsoluteParent());
			this.setTabFocusElement(eventElement);
			if (eventElement.getIsDragDropDragElement())
				targetElement = null;
			if (eventElement.getIsResizable()) {
				float offsetX = x;
				float offsetY = y;
				Element el = eventElement;

				if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (eventElement.getResetKeyboardFocus())
					keyboardElement = null;
			} else if (eventElement.getIsMovable() && eventElementResizeDirection == null) {
				eventElementResizeDirection = null;
				if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (eventElement.getResetKeyboardFocus())
					keyboardElement = null;
				eventElementOriginXY.set(eventElement.getPosition());
			} else if (eventElement instanceof KeyboardListener) {
				if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (eventElement.getResetKeyboardFocus())
					keyboardElement = eventElement;
				if (keyboardElement instanceof TextField) {
					((TextField) keyboardElement).setTabFocus();
				}
				// TODO: Update target element's font shader
			} else {
				eventElementResizeDirection = null;
				if (keyboardElement != null && eventElement.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (eventElement.getResetKeyboardFocus())
					keyboardElement = null;
			}
		}
	}

	/**
	 * Returns the current mouse focus Element
	 * 
	 * @return Element
	 */
	public Element getMouseFocusElement() {
		return this.mouseFocusElement;
	}

	/**
	 * Returns the current Drag enabled Element
	 * 
	 * @return Element
	 */
	public Element getDragElement() {
		return this.eventElement;
	}

	/**
	 * Returns the current Drop enabled Element
	 * 
	 * @return Element
	 */
	@Override
	public Element getDropElement() {
		return this.targetElement;
	}
	/*
	public void getAnimEventTargets(float x, float y) {
		guiRayOrigin.set(x, y, 0f);
		
		elementZOrderRay.setOrigin(guiRayOrigin);
		results.clear();
		
		t0neg0dGUI.collideWith(elementZOrderRay, results);
		
		lastCollision = results.getClosestCollision();
		
		eventAnimElement = null;
		eventQuad = null;
		for (CollisionResult result : results) {
			boolean discard = false;
			if (!discard) {
				if (result.getGeometry().getParent() instanceof AnimElement) {
					eventAnimElement = (AnimElement)result.getGeometry().getParent();
					if (!eventAnimElement.getIgnoreMouse()) {
						eventAnimOffsetX = x-eventAnimElement.getPositionX();
						eventAnimOffsetY = y-eventAnimElement.getPositionY();
						eventQuad = eventAnimElement.getQuad((int)FastMath.floor(result.getTriangleIndex()/2));
						eventQuadOffsetX = x-eventQuad.getPositionX();
						eventQuadOffsetY = y-eventQuad.getPositionY();
						break;
					} else {
						eventAnimElement = null;
						eventQuad = null;
					}
				}
			}
		}
	}
	*/

	public QuadData getEventQuad() {
		return this.eventQuad;
	}

	public float getEventQuadOffsetX() {
		return this.eventQuadOffsetX;
	}

	public float getEventQuadOffsetY() {
		return this.eventQuadOffsetY;
	}

	public AnimElement getEventAnimElement() {
		return this.eventAnimElement;
	}

	public float getEventAnimOffsetX() {
		return this.eventAnimOffsetX;
	}

	public float getEventAnimOffsetY() {
		return this.eventAnimOffsetY;
	}
	// </editor-fold>

	// <editor-fold desc="Clipboard Support">
	/**
	 * Sets the current stored text to the internal clipboard. This is probably
	 * going
	 * to vanish quickly.
	 * 
	 * @param text
	 *            The text to store
	 */
	@Override
	public void setClipboardText(String text) {
		try {
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(text);
			clipboard.setContents(stringSelection, new ClipboardOwner() {
				@Override
				public void lostOwnership(Clipboard clipboard, Transferable contents) {
				}
			});
		} catch (Exception ex) {
			this.clipboardText = text;
		}
	}

	/**
	 * Returns the internal clipboard's current stored text
	 * 
	 * @return String text
	 */
	@Override
	public String getClipboardText() {
		try {
			String ret = "";
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable text = clipboard.getContents(null);
			boolean isText = (text != null && text.isDataFlavorSupported(DataFlavor.stringFlavor));
			if (isText) {
				try {
					ret = (String) text.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception ex) {
					ret = this.clipboardText;
					if (ret == null)
						ret = "";
				}
			}
			return ret;
		} catch (Exception ex) {
			return "";
		}
	}

	// @Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// System.out.println("Clipboard failed, switching to internal
		// clipboard.");
		// this.clipboardActive = false;
	}
	// </editor-fold>

	// <editor-fold desc="2D Framework Support">
	/**
	 * Returns a pointer to the AnimManager. The AnimManager is a time based
	 * queuing
	 * for TemporalActions used with @Transformable ( See @AnimElement @QuadData
	 * )
	 * 
	 * @return AnimManager animManager
	 */
	@Override
	public AnimManager getAnimManager() {
		return this.animManager;
	}

	public AnimLayer addAnimLayer() {
		return addAnimLayer(UIDUtil.getUID());
	}

	@Override
	public AnimLayer addAnimLayer(String UID) {
		if (getAnimLayerById(UID) != null) {
			try {
				throw new ConflictingIDException();
			} catch (ConflictingIDException ex) {
				Logger.getLogger(Element.class.getName()).log(Level.SEVERE,
						"The child layer '" + UID + "' (Element) conflicts with a previously added child layer in parent Screen.",
						ex);
				System.exit(0);
			}
			return null;
		} else {
			AnimLayer layer = new AnimLayer(this, UID);
			layers.put(UID, layer);
			if (!layer.getInitialized()) {
				layer.orgPosition = layer.getPosition().clone();
				layer.setInitialized(this);
			}
			t0neg0dGUI.attachChild(layer);
			t0neg0dGUI.addControl(layer);

			return layer;
		}
	}

	@Override
	public void addAnimLayer(String UID, AnimLayer layer) {
		if (getAnimLayerById(UID) != null) {
			try {
				throw new ConflictingIDException();
			} catch (ConflictingIDException ex) {
				Logger.getLogger(Element.class.getName()).log(Level.SEVERE,
						"The child layer '" + UID + "' (Element) conflicts with a previously added child layer in parent Screen.",
						ex);
				System.exit(0);
			}
		} else {

			layers.put(UID, layer);
			if (!layer.getInitialized()) {
				layer.orgPosition = layer.getPosition().clone();
				layer.setInitialized(this);
			}
			t0neg0dGUI.attachChild(layer);
			t0neg0dGUI.addControl(layer);
		}
	}

	@Override
	public AnimLayer removeAnimLayer(String UID) {
		AnimLayer animLayer = layers.get(UID);
		if (animLayer != null) {
			removeAnimLayer(animLayer);
			return animLayer;
		} else
			return null;
	}

	@Override
	public void removeAnimLayer(AnimLayer animLayer) {
		if (layers.containsValue(animLayer)) {
			t0neg0dGUI.removeControl(animLayer);
			layers.remove(animLayer.getUID());
			applyZOrder();
			animLayer.removeFromParent();
			animLayer.cleanup();
		}
	}

	public AnimLayer getAnimLayerById(String UID) {
		AnimLayer ret = null;
		if (layers.containsKey(UID)) {
			ret = layers.get(UID);
		} else {
			for (AnimLayer el : layers.values()) {
				ret = (AnimLayer) el.getChildElementById(UID);
				if (ret != null) {
					break;
				}
			}
		}
		return ret;
	}

	// <editor-fold desc="3D Scene Support">
	public void setUse3DSceneSupport(boolean enable) {
		this.use3DSceneSupport = enable;
	}

	// <editor-fold desc="JME Control Methods">
	@Override
	public void update(float tpf) {
	}

	@Override
	public void render(RenderManager rm, ViewPort vp) {
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		Screen screen = new Screen(this.app, styleManager.getStyleMap());
		synchronized (screen.elements) {
			screen.elements.putAll(this.elements);
		}
		// New z-ordering
		screen.childList.addAll(this.childList);
		return screen;
	}

	@Override
	public void setSpatial(Spatial spatial) {
		this.spatial = spatial;
		if (spatial != null) {
			((Node) spatial).attachChild(t0neg0dGUI);
			t0neg0dGUI.addControl(effectManager);
			t0neg0dGUI.addControl(animManager);
			initModalBackground();
			addElement(modalBackground, null, true);
			// t0neg0dGUI.attachChild(modalBackground);
			if (isAndroid())
				initVirtualKeys();
		}
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
	}

	@Override
	public void read(JmeImporter im) throws IOException {
	}
	// </editor-fold>

	/**
	 * Returns a pointer to the EffectManager
	 * 
	 * @return EffectManager effectManager
	 */
	@Override
	public EffectManager getEffectManager() {
		return this.effectManager;
	}

	/**
	 * Returns the Style object associated to the provided key
	 * 
	 * @param key
	 *            The String key of the Style
	 * @return Style style
	 */
	@Override
	public Style getStyle(String key) {
		return styleManager.getStyle(key);
	}

	// <editor-fold desc="Custom Cursor Support">
	/**
	 * Enables the use of Style defined custom cursors. Initially set prior to
	 * initializing screen
	 * 
	 * @param useCustomCursors
	 *            boolean
	 */
	@Override
	public void setUseCustomCursors(boolean useCustomCursors) {
		this.useCustomCursors = useCustomCursors;
		if (!useCustomCursors) {
			getApplication().getInputManager().setMouseCursor(null);
		} else {
			setCursor(StyleManager.CursorType.POINTER);
		}
	}

	/**
	 * Returns true if custom cursors are currently enabled
	 * 
	 * @return boolean
	 */
	@Override
	public boolean getUseCustomCursors() {
		return this.useCustomCursors;
	}

	/**
	 * For internal use - Use setForcedCursor instead
	 * 
	 * @param cur
	 */
	@Override
	public void setCursor(CursorType cur) {
		if (getUseCustomCursors()) {
			if (!forceCursor) {
				JmeCursor jmeCur = styleManager.getCursor(cur);
				// if (cur == CursorType.TEXT) {
				// jmeCur.setxHotSpot(jmeCur.getWidth()/2);
				// jmeCur.setyHotSpot(jmeCur.getHeight()/2);
				// }
				if (jmeCur != null) {
					getApplication().getInputManager().setMouseCursor(jmeCur);
					currentCursor = cur;
				}
			}
		}
	}

	/**
	 * Sets the cursor and locks the cursor until releaseForcedCursor is called.
	 * 
	 * @param cur
	 *            CursorType
	 */
	public void setForcedCursor(CursorType cur) {
		if (getUseCustomCursors()) {
			JmeCursor jmeCur = styleManager.getCursor(cur);
			if (jmeCur != null) {
				getApplication().getInputManager().setMouseCursor(jmeCur);
				forceCursor = true;
			}
		}
	}

	/**
	 * Release cursor control back to the Element level
	 */
	public void releaseForcedCursor() {
		if (getUseCustomCursors()) {
			JmeCursor jmeCur = styleManager.getCursor(CursorType.POINTER);
			if (jmeCur != null) {
				getApplication().getInputManager().setMouseCursor(jmeCur);
			}
			forceCursor = false;
		}
	}
	// </editor-fold>

	// <editor-fold desc="Global Alpha & UI Scaling">
	public void setGlobalUIScale(float widthPercent, float heightPercent) {
		synchronized (elements) {
			for (Element el : elements.values()) {
				el.setPosition(el.getPosition().x * widthPercent, el.getPosition().y * heightPercent);
				el.setDimensions(el.getDimensions().x * widthPercent, el.getDimensions().y * heightPercent);
				el.setFontSize(el.getFontSize() * heightPercent);
				el.setGlobalUIScale(widthPercent, heightPercent);
			}
		}
	}

	/**
	 * Sets the overall opacity of all elements that have not been flagged as
	 * ignoreGlobalAlpha(true)
	 * 
	 * @param globalAlpha
	 *            float
	 */
	@Override
	public void setGlobalAlpha(float globalAlpha) {
		this.globalAlpha = globalAlpha;
		synchronized (elements) {
			for (Element el : elements.values()) {
				el.setGlobalAlpha(globalAlpha);
			}
		}
	}

	/**
	 * Returns the current value of global alpha
	 * 
	 * @return float
	 */
	@Override
	public float getGlobalAlpha() {
		return this.globalAlpha;
	}

	@Override
	public BitmapFont getDefaultGUIFont() {
		return this.defaultGUIFont;
	}
	// </editor-fold>

	@Override
	public void handleAndroidMenuState(Element target) {
		synchronized (elements) {
			if (target == null) {
				for (Element el : elements.values()) {
					if (el instanceof AutoHide) {
						el.hide();
					}
				}
			} else {
				if (!(target.getAbsoluteParent() instanceof AutoHide) && !(target.getParent() instanceof ComboBox)) {
					for (Element el : elements.values()) {
						if (el instanceof AutoHide) {
							el.hide();
						}
					}
				} else if (target.getAbsoluteParent() instanceof AutoHide) {
					for (Element el : elements.values()) {
						if (el instanceof AutoHide && el != target.getAbsoluteParent()) {
							el.hide();
						}
					}
				} else if (target.getParent() instanceof ComboBox) {
					for (Element el : elements.values()) {
						if (el instanceof AutoHide && el != ((ComboBox<?>) target.getParent()).getMenu()) {
							el.hide();
						}
					}
				}
			}
		}
	}
	// </editor-fold>

	// <editor-fold desc="ToolTips">
	/**
	 * Enables/disables the use of ToolTips
	 * 
	 * @param useToolTips
	 *            boolean
	 */
	@Override
	public void setUseToolTips(boolean useToolTips) {
		if (this.useToolTips != useToolTips) {
			this.useToolTips = useToolTips;
			updateToolTipLocation();
		}
	}

	/**
	 * Returns if ToolTips are enabled/disabled
	 * 
	 * @return boolean
	 */
	@Override
	public boolean getUseToolTips() {
		return useToolTips;
	}

	/**
	 * For internal use only - DO NOT CALL THIS METHOD
	 */
	@Override
	public void updateToolTipLocation() {
		if (toolTip == null && useToolTips) {
			/*
			 * Initialize the global tool tip on first invocation.
			 */
			toolTip = new ToolTip(this, "GlobalToolTip");
			toolTip.setIgnoreGlobalAlpha(true);
			toolTip.setIsGlobalModal(true);
			// toolTip.setText("");
			// toolTip.setTextPadding(2);
			// toolTip.setTextPosition(0, 0);
			// toolTip.hide();
			addElement(toolTip, null, true);
			toolTip.bringToFront();
			// toolTip.move(0, 0, 20);
		} else if (toolTip != null && !useToolTips) {
			removeElement(toolTip);
			toolTip = null;
		}

		if (extendedToolTip != null && !useToolTips) {
			removeElement(extendedToolTip);
			extendedToolTip = null;
		}

		if (useToolTips) {

			if (mouseFocusElement != null && app.getInputManager().isCursorVisible()) {
				ToolTipProvider toolTipProvider = mouseFocusElement.getToolTipProvider();
				if (toolTipProvider == null) {
					toolTipProvider = createDefaultToolTipProvider(mouseFocusElement);
				}

				if (toolTipProvider != null) {
					if (currentProvider == null || !toolTipProvider.equals(currentProvider)
							|| !currentMouseFocusElement.equals(mouseFocusElement)) {
						if (extendedToolTip != null) {
							removeElement(extendedToolTip);
						}
						currentProvider = toolTipProvider;
						extendedToolTip = currentProvider.createToolTip();
						if (extendedToolTip != null) {
							addElement(extendedToolTip);
							extendedToolTip.bringToFront();
						}
						currentMouseFocusElement = mouseFocusElement;
					}
					if (extendedToolTip != null) {
						float nextX = mouseXY.x - (extendedToolTip.getWidth() / 2);
						if (nextX < 0) {
							nextX = 0;
						} else if (nextX + extendedToolTip.getWidth() > getWidth()) {
							nextX = getWidth() - extendedToolTip.getWidth();
						}
						float nextY = mouseXY.y - extendedToolTip.getHeight() - 40;
						if (nextY < 0) {
							nextY = mouseXY.y + 5;
						}
						extendedToolTip.moveTo(nextX, nextY);
						if (!extendedToolTip.getIsVisible()) {
							extendedToolTip.show();
							extendedToolTip.bringToFront();
						}
					}
					return;
				}
			}

			// A standard tooltip, remove any extended tooltips first
			if (extendedToolTip != null) {
				currentProvider = null;
				removeElement(extendedToolTip);
				extendedToolTip = null;
			}

			/*
			 * Determine what text (if any) the tool tip should display.
			 */
			String newText = null;
			if (useToolTips && getApplication().getInputManager().isCursorVisible()) {
				if (mouseFocusElement != null) {
					newText = mouseFocusElement.getToolTipText();
				} else {
					newText = forcedToolTipText;
				}
			}

			if (newText == null || newText.isEmpty()) {
				/*
				 * Clear and hide the old tool tip.
				 */
				toolTip.setText("");
				toolTip.hide();
				return;
			}

			String oldText = toolTip.getText();
			if (!oldText.equals(newText)) {

				toolTip.setText(newText);
				toolTip.sizeToContent();

				toolTip.bringToFront();
			}

			setToolTipLocation();
			if (!toolTip.getIsVisible()) {
				toolTip.show();
				toolTip.bringToFront();
			}
		}
	}

	public void setForcedToolTip(String toolTipText) {
		forcedToolTipText = toolTipText;
		updateToolTipLocation();
	}

	public void releaseForcedToolTip() {
		setForcedToolTip(null);
	}

	public void forceToolTipLocationUpdate() {
		setToolTipLocation();
	}

	@Override
	public Element getToolTipFocus() {
		return this.mouseFocusElement;
	}

	@Override
	public void hideToolTip() {
		toolTip.setText(" ");
		toolTip.hide();
	}
	// </editor-fold>

	// <editor-fold desc="Audio Support">
	/**
	 * Enables/disables UI Audio
	 * 
	 * @param useUIAudio
	 *            boolean
	 */
	@Override
	public void setUseUIAudio(boolean useUIAudio) {
		this.useUIAudio = useUIAudio;
	}

	/**
	 * Returns if the UI Audio option is enabled/disabled
	 * 
	 * @return boolean
	 */
	@Override
	public boolean getUseUIAudio() {
		return this.useUIAudio;
	}

	/**
	 * Sets the global UI Audio volume
	 * 
	 * @param uiAudioVolume
	 *            float
	 */
	@Override
	public void setUIAudioVolume(float uiAudioVolume) {
		this.uiAudioVolume = uiAudioVolume;
	}

	/**
	 * Gets the current global UI Audio volume
	 * 
	 * @return float
	 */
	public float getUIAudioVolume() {
		return this.uiAudioVolume;
	}

	/**
	 * Plays an instance of an audio node
	 * 
	 * @param key
	 *            String The key associated with the audio node
	 * @param volume
	 *            float the volume to play the instance at (effected by global
	 *            volume)
	 */
	public void playAudioNode(String key, float volume) {
		AudioNode audioNode = styleManager.getAudioNode(key);
		if (audioNode != null) {
			audioNode.setVolume(volume * getUIAudioVolume());
			audioNode.playInstance();
		}
	}
	// </editor-fold>

	// <editor-fold desc="Cursor Effects">
	/**
	 * Enables/disables the use of Cursor effects
	 * 
	 * @param useCursorEffects
	 *            boolean
	 */
	@Override
	public void setUseCursorEffects(boolean useCursorEffects) {
		if (cursorEffects == null) {
			cursorEffects = new CursorEffects(this);
			// cursorEffects.setTheme(CursorEffects.EmitterTheme.FLAMES);
		}
		this.useCursorEffects = useCursorEffects;

		if (useCursorEffects) {
			if (!cursorEffects.getIsActive()) {
				cursorEffects.getEmitter().removeAllParticles();
				cursorEffects.updatePosition(mouseXY);
				cursorEffects.start();
			}
		} else if (cursorEffects.getIsActive())
			cursorEffects.stop();
	}

	/**
	 * Returns the cursor effects manager
	 * 
	 * @return
	 */
	@Override
	public CursorEffects getCursorEffects() {
		return this.cursorEffects;
	}
	// </editor-fold>

	// <editor-fold desc="Forms & Tab Focus">
	/**
	 * Method for setting the tab focus element
	 * 
	 * @param element
	 *            The Element to set tab focus to
	 */
	@Override
	public void setTabFocusElement(Element element) {
		resetFocusElement();
		Element el = element;
		focusForm = null;
		while (focusForm == null && el != null) {
			focusForm = el.getForm();
			if (focusForm == null)
				el = el.getElementParent();
		}
		if (focusForm != null) {
			element = el;
		}
		if (element.getResetKeyboardFocus()) {
			tabFocusElement = element;
			if (focusForm != null) {
				focusForm.setSelectedTabIndex(element);
			}
			if (tabFocusElement instanceof TabFocusListener) {
				((TabFocusListener) element).setTabFocus();
			}
		}
	}

	/**
	 * Resets the tab focus element to null after calling the TabFocusListener's
	 * resetTabFocus method.
	 */
	@Override
	public void resetTabFocusElement() {
		resetFocusElement();
		this.tabFocusElement = null;
		this.focusForm = null;
	}

	/**
	 * Returns the current tab focus element
	 */
	public Element getTabFocusElement() {
		return this.tabFocusElement;
	}

	public boolean isMouseButtonsEnabled() {
		return mouseButtonsEnabled;
	}

	public void setMouseButtonsEnabled(boolean mouseButtonsEnabled) {
		this.mouseButtonsEnabled = mouseButtonsEnabled;
	}

	/**
	 * Sets the current Keyboard focus Element
	 * 
	 * @param element
	 *            The Element to set keyboard focus to
	 */
	@Override
	public Element getKeyboardElement() {
		return keyboardElement;
	}

	/**
	 * Sets the current Keyboard focus Element
	 * 
	 * @param element
	 *            The Element to set keyboard focus to
	 */
	@Override
	public void setKeyboardElement(Element element) {

		if (element != null) {

			if (element.getResetKeyboardFocus())
				keyboardElement = element;
		} else {
			Element el = keyboardElement;
			keyboardElement = null;
			if (el != null) {
				if (el instanceof TextField)
					((TextField) el).resetTabFocus();
			}
		}
	}
	// </editor-fold>

	// Layout Parser
	public void parseLayout(String path, AbstractAppState state) {
		layoutParser.parseLayout(path, state);
	}

	// <editor-fold desc="Modal Background">
	@Override
	public ModalBackground getModalBackground() {
		return this.modalBackground;
	}

	@Override
	public void showAsModal(Element el, boolean showWithEffect) {
		modalBackground.fillScreen();
		modalBackground.show();
		//
		updateZOrder(el);
		if (showWithEffect)
			el.showWithEffect();
		else
			el.show();
		// updateZOrder(el.getAbsoluteParent());
	}

	@Override
	public void hideModalBackground() {
		modalBackground.hide();
	}

	@Override
	public void releaseModal(Element el) {
		List<Element> els = getModalElements();
		els.remove(el);
		if (els.isEmpty())
			hideModalBackground();
		else {
			updateZOrder(els.get(0));
		}
	}

	// </editor-fold>

	// <editor-fold desc="Android Keyboard Control">
	public void setUseKeyboardIcons(boolean useIcons) {
		if (Screen.isAndroid())
			virtualKeys.setUseIcons(useIcons);
	}

	public Keyboard getVirtualKeyboard() {
		if (isAndroid())
			return virtualKeys;
		else
			return null;
	}

	@Override
	public void showVirtualKeyboard() {
		if (isAndroid())
			virtualKeys.show();
	}

	@Override
	public void hideVirtualKeyboard() {
		if (isAndroid())
			virtualKeys.hide();
	}
	// </editor-fold>

	// <editor-fold desc="Keyboard Key States">
	public boolean getCtrl() {
		return this.CTRL;
	}

	public boolean getShift() {
		return this.SHIFT;
	}

	public boolean getAlt() {
		return this.ALT;
	}
	// </editor-fold>

	// <editor-fold desc="OS Helpers">
	public static boolean isWindows() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public static boolean isSolaris() {
		String OS = System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("sunos") >= 0);
	}

	public static boolean isAndroid() {
		String OS = System.getProperty("java.vendor").toLowerCase();
		return (OS.indexOf("android") >= 0);
	}
	// </editor-fold>

	// <editor-fold desc="Node Event Methods">
	/**
	 * Determines and returns the current mouse focus Node
	 * 
	 * @param x
	 *            The current mouse X coord
	 * @param y
	 *            The current mouse Y coord
	 * @return Element eventElement
	 */
	public void addScene(Node scene) {
		scenes.add(scene);
	}

	public void removeScene(Node scene) {
		if (scenes.contains(scene))
			scenes.remove(scene);
	}

	// </editor-fold>

	public List<Element> getElementList() {
		// New z-ordering
		return Collections.unmodifiableList(childList);
	}

	// </editor-fold>

	// </editor-fold>

	protected void applyZOrder() {
		float zi = (float) Integer.MAX_VALUE / (childList.size() + 1);
		float z = zi;

		List<Element> sorted = new ArrayList<>(childList);

		/* If modal background is visible, it sits just below the topmost NORMAL */
		if (modalBackground != null && modalBackground.getIsVisible() && sorted.size() > 1) {
			int topMost = 0;
			for (int i = 0; i < sorted.size(); i++) {
				if (ZPriority.NORMAL == sorted.get(i).getPriority()) {
					topMost = i;
				}
			}
			sorted.remove(modalBackground);
			sorted.add(topMost - 1, modalBackground);
			// sorted.add(1, modalBackground);
		}

		Collections.sort(sorted, new ZOrderComparator(childList));

		for (Element c : sorted) {
			c.setZStep(zi);
			c.setLocalTranslation(c.getLocalTranslation().setZ(z));
			c.applyZOrder();
			z += zi;
		}
	}

	protected void defaultClick() {
		resetTabFocusElement();
		if (keyboardElement == null) {
			if (Screen.isAndroid())
				hideVirtualKeyboard();
		} else {
			setKeyboardElement(null);
		}
	}

	protected void layoutHeirarchy(Node s) {
		for (Element c : childList) {
			c.layoutChildren();
		}
		applyZOrder();
	}

	protected void initializeElement(Element element) {
		// element.setY(getHeight()-element.getHeight()-element.getY());
		element.orgPosition = element.getPosition().clone();
		element.orgPosition.setY(element.getY());
		element.setInitialized(this);

		if (!Element.NEW_YFLIPPING)
			element.setY(getHeight() - element.getDimensions().y - element.orgPosition.y);
		else
			element.updateNodeLocation();

		if (element.getLockToParentBounds()) {
			element.lockToParentBounds(element.getX(), element.getY());
		}
		// LUtil.setDimensions(element, element.getDimensions());
		element.layoutChildren();
	}

	/**
	 * Stored the current mouse position as a Vector2f
	 * 
	 * @param x
	 *            The mouse's current X coord
	 * @param y
	 *            The mouse's current Y coord
	 */
	private void setMouseXY(float x, float y) {
		mouseXY.set(x, y).multLocal(this.inputScale);
	}

	private void setTouchXY(float x, float y) {
		touchXY.set(x, y).multLocal(this.inputScale);
	}

	private void throwParserException() {
		try {
			throw new java.text.ParseException(
					"The provided texture information does not conform to the expected standard of x=(int)|y=(int)|w=(int)|h=(int)",
					0);
		} catch (ParseException ex) {
			Logger.getLogger(Screen.class.getName()).log(Level.SEVERE,
					"The provided texture information does not conform to the expected standard of x=(int)|y=(int)|w=(int)|h=(int)",
					ex);
		}
	}

	private void setLastCollision() {
		if (Screen.isAndroid())
			click2d.set(touchXY);
		else
			click2d.set(mouseXY);
		tempV2.set(click2d);
		click3d.set(app.getCamera().getWorldCoordinates(tempV2, 0f));
		pickDir.set(app.getCamera().getWorldCoordinates(tempV2, 1f).subtractLocal(click3d).normalizeLocal());
		pickRay.setOrigin(click3d);
		pickRay.setDirection(pickDir);
		CollisionResults results = new CollisionResults();
		app.getViewPort().getScenes().get(0).collideWith(pickRay, results);
		lastCollision = results.getClosestCollision();
	}

	private Vector3f getBarycentricCoords(Vector3f origin, Vector3f direction, Vector3f v0, Vector3f v1, Vector3f v2,
			Vector3f store) {
		Vector3f diff = origin.subtract(v0);
		Vector3f edge1 = v1.subtract(v0);
		Vector3f edge2 = v2.subtract(v0);
		Vector3f norm = edge1.cross(edge2);

		float dirDotNorm = direction.dot(norm);
		float sign;
		if (dirDotNorm > FastMath.FLT_EPSILON) {
			sign = 1;
		} else if (dirDotNorm < -FastMath.FLT_EPSILON) {
			sign = -1f;
			dirDotNorm = -dirDotNorm;
		} else {
			return store;
		}

		float dirDotEdge1xDiff = sign * direction.dot(edge1.crossLocal(diff));
		float dirDotDiffxEdge2 = sign * direction.dot(diff.cross(edge2, edge2));
		float inv = 1f / dirDotNorm;
		float w1 = dirDotDiffxEdge2 * inv;
		float w2 = dirDotEdge1xDiff * inv;
		float w0 = 1.0f - w1 - w2;
		return store.set(w0, w1, w2);
	}

	private InputEvent getORSTCEvent(SubScreen s, InputEvent oldEvt, int type) {
		Geometry geom = lastCollision.getGeometry();
		int triIndex = lastCollision.getTriangleIndex();
		Triangle tri = lastCollision.getTriangle(null);
		geom.getMesh().getTriangle(triIndex, indices);
		cp.set(lastCollision.getContactPoint());

		FloatBuffer tc = geom.getMesh().getFloatBuffer(VertexBuffer.Type.TexCoord);
		uv1.set(tc.get(indices[0] * 2), tc.get(indices[0] * 2 + 1));
		uv2.set(tc.get(indices[1] * 2), tc.get(indices[1] * 2 + 1));
		uv3.set(tc.get(indices[2] * 2), tc.get(indices[2] * 2 + 1));

		geom.getWorldTransform().transformVector(tri.get1(), v1);
		geom.getWorldTransform().transformVector(tri.get2(), v2);
		geom.getWorldTransform().transformVector(tri.get3(), v3);
		weights.set(getBarycentricCoords(pickRay.getOrigin(), pickRay.getDirection(), v1, v2, v3, weights));

		uv.set(uv1.x * weights.x + uv2.x * weights.y + uv3.x * weights.z,
				uv1.y * weights.x + uv2.y * weights.y + uv3.y * weights.z);
		uv.x *= s.getWidth();
		uv.y *= s.getHeight();

		InputEvent evt = null;
		switch (type) {
		case 0:
			evt = new MouseMotionEvent((int) (uv.x), (int) (uv.y), ((MouseMotionEvent) oldEvt).getDX(),
					((MouseMotionEvent) oldEvt).getDY(), ((MouseMotionEvent) oldEvt).getWheel(),
					((MouseMotionEvent) oldEvt).getDeltaWheel());
			break;
		case 1:
			evt = new MouseButtonEvent(((MouseButtonEvent) oldEvt).getButtonIndex(), ((MouseButtonEvent) oldEvt).isPressed(),
					(int) (uv.x), (int) (uv.y));
			break;
		}

		return evt;
	}

	/**
	 * This scales the current event values of deltaX and deltaY to a value
	 * between 0.0f and 1.0f
	 * 
	 * @param evt
	 */
	private void androidFlingEvent(TouchEvent evt) {
		setTouchXY(evt.getX(), evt.getY());
		float flingX = 1f / 8000f * evt.getDeltaX();
		float flingY = 1f / 8000f * evt.getDeltaY();
		evt.set(evt.getType(), touchXY.x, touchXY.y, flingX, flingY);

		Element contact = getContactElement(touchXY.x, touchXY.y, EventCheckType.Fling);
		Vector2f offset = tempElementOffset.clone();
		Element target = getEventElement(touchXY.x, touchXY.y, EventCheckType.Fling);

		if (target != null) {
			if (target instanceof FlingListener) {
				((FlingListener) target).onFling(evt);
			}
		}
	}

	private void androidTouchDownEvent(TouchEvent evt) {
		// setTouchXY(evt.getX(),evt.getY());
		mousePressed = true;
		Element contact = getContactElement(touchXY.x, touchXY.y, EventCheckType.Touch);
		Vector2f offset = tempElementOffset.clone();
		Element target = getEventElement(touchXY.x, touchXY.y, EventCheckType.Touch);

		Borders dir = null;
		if (target != null) {
			if (target.getResetKeyboardFocus())
				resetTabFocusElement();

			if (target.getAbsoluteParent().getEffectZOrder())
				updateZOrder(target.getAbsoluteParent());
			if (target.getResetKeyboardFocus())
				this.setTabFocusElement(target);
			if (target.getIsDragDropDragElement())
				targetElement = null;
			if (target.getIsResizable()) {
				float offsetX = touchXY.x;
				float offsetY = touchXY.y;
				Element el = target;

				if (offsetX > el.getAbsoluteX() && offsetX < el.getAbsoluteX() + el.getResizeBorderWestSize()) {
					if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.getResizeBorderNorthSize()) {
						dir = Borders.NW;
					} else if (offsetY > (el.getAbsoluteHeight() - el.getResizeBorderSouthSize())
							&& offsetY < el.getAbsoluteHeight()) {
						dir = Borders.SW;
					} else {
						dir = Borders.W;
					}
				} else if (offsetX > (el.getAbsoluteWidth() - el.getResizeBorderEastSize()) && offsetX < el.getAbsoluteWidth()) {
					if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.getResizeBorderNorthSize()) {
						dir = Borders.NE;
					} else if (offsetY > (el.getAbsoluteHeight() - el.getResizeBorderSouthSize())
							&& offsetY < el.getAbsoluteHeight()) {
						dir = Borders.SE;
					} else {
						dir = Borders.E;
					}
				} else {
					if (offsetY > el.getAbsoluteY() && offsetY < el.getAbsoluteY() + el.getResizeBorderNorthSize()) {
						dir = Borders.N;
					} else if (offsetY > (el.getAbsoluteHeight() - el.getResizeBorderSouthSize())
							&& offsetY < el.getAbsoluteHeight()) {
						dir = Borders.S;
					}
				}
				if (keyboardElement != null && target.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (target.getResetKeyboardFocus())
					keyboardElement = null;
			} else if (target.getIsMovable() && dir == null) {
				dir = null;
				if (keyboardElement != null && target.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (target.getResetKeyboardFocus())
					keyboardElement = null;
				eventElementOriginXY.set(target.getPosition());
			} else if (target instanceof KeyboardListener) {
				if (keyboardElement != null && target.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (target.getResetKeyboardFocus())
					keyboardElement = target;
				if (keyboardElement instanceof TextField) {
					((TextField) keyboardElement).setTabFocus();
					showVirtualKeyboard();
				}
			} else {
				dir = null;
				if (keyboardElement != null && target.getResetKeyboardFocus()) {
					if (keyboardElement instanceof TextField)
						((TextField) keyboardElement).resetTabFocus();
				}
				if (target.getResetKeyboardFocus())
					keyboardElement = null;
			}
			if (target instanceof MouseButtonListener) {
				MouseButtonEvent mbEvt = new MouseButtonEvent(0, true, (int) touchXY.x, (int) touchXY.y);
				((MouseButtonListener) target).onMouseLeftPressed(mbEvt);
			}
			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchDown(evt);
			}
			if (keyboardElement == null)
				hideVirtualKeyboard();
			evt.setConsumed();
			contactElements.put(evt.getPointerId(), contact);
			elementOffsets.put(evt.getPointerId(), offset);
			eventElements.put(evt.getPointerId(), target);
			eventElementResizeDirections.put(evt.getPointerId(), dir);
		} else {
			// 2D Framework
			if (eventElement == null) {
				if (eventAnimElement != null) {
					setAnimElementZOrder();
					if (eventAnimElement instanceof MouseButtonListener) {
						MouseButtonEvent mbEvt = new MouseButtonEvent(0, true, (int) touchXY.x, (int) touchXY.y);
						((MouseButtonListener) eventAnimElement).onMouseLeftPressed(mbEvt);
					}
					evt.setConsumed();
				}
			}
			if (keyboardElement == null)
				hideVirtualKeyboard();
			resetTabFocusElement();
		}

		if (use3DSceneSupport && !evt.isConsumed()) {
			s3dOnTouchDownEvent(evt);
		}
	}

	private void androidTouchMoveEvent(TouchEvent evt) {
		// setTouchXY(evt.getX(),evt.getY());
		for (Integer key : eventElements.keySet()) {
			if (key == evt.getPointerId()) {
				Element target = eventElements.get(key);
				if (target != null) {
					Element contact = contactElements.get(key);
					Vector2f offset = elementOffsets.get(key);
					Borders dir = eventElementResizeDirections.get(key);

					boolean movable = contact.getIsMovable();
					if (dir != null) {
						target.resize(touchXY.x, touchXY.y, dir);
					} else if (movable) {
						target.moveTo(touchXY.x - offset.x, touchXY.y - offset.y);
					}

					if (target instanceof MouseMovementListener) {
						MouseMotionEvent mbEvt = new MouseMotionEvent((int) touchXY.x, (int) touchXY.y, (int) evt.getDeltaX(),
								(int) evt.getDeltaY(), 0, 0);
						((MouseMovementListener) target).onMouseMove(mbEvt);
					}
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

	private void androidTouchUpEvent(TouchEvent evt) {
		// setTouchXY(evt.getX(),evt.getY());
		Element target = eventElements.get(evt.getPointerId());
		handleAndroidMenuState(target);
		if (target != null) {
			// if (!(target.getAbsoluteParent() instanceof Menu)) {
			// handleAndroidMenuState(target);
			// }
			if (target instanceof MouseButtonListener) {
				MouseButtonEvent mbEvt = new MouseButtonEvent(0, true, (int) touchXY.x, (int) touchXY.y);
				((MouseButtonListener) target).onMouseLeftReleased(mbEvt);
			}
			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchUp(evt);
			}
			evt.setConsumed();
			eventElements.remove(evt.getPointerId());
			contactElements.remove(evt.getPointerId());
			elementOffsets.remove(evt.getPointerId());
			eventElementResizeDirections.remove(evt.getPointerId());
		} else
		// handleAndroidMenuState(target);
		if (eventAnimElement != null) {
			if (eventAnimElement instanceof MouseButtonListener) {
				MouseButtonEvent mbEvt = new MouseButtonEvent(0, true, (int) touchXY.x, (int) touchXY.y);
				((MouseButtonListener) eventAnimElement).onMouseLeftReleased(mbEvt);
			}
			evt.setConsumed();
		}

		if (use3DSceneSupport && !evt.isConsumed()) {
			s3dOnTouchUpEvent(evt);
		}

		mousePressed = false;
	}

	// <editor-fold desc="Element Event Methods">
	private boolean getIgnoreEvent(Element el, EventCheckType check) {
		switch (check) {
		case MouseLeft:
			return el.getIgnoreMouseLeftButton();
		case MouseRight:
			return el.getIgnoreMouseRightButton();
		case WheelClick:
			return el.getIgnoreMouseWheelClick();
		case WheelMove:
			return el.getIgnoreMouseWheelMove();
		case MouseFocus:
			return el.getIgnoreMouseFocus() && el.getToolTipProvider() == null
					&& (el.getToolTipText() == null || el.getToolTipText().equals(""));
		case Touch:
			return el.getIgnoreTouch();
		case TouchMove:
			return el.getIgnoreTouchMove();
		case Fling:
			return el.getIgnoreFling();
		default:
			return false;
		}
	}

	/**
	 * Determines and returns the current mouse focus Element
	 * 
	 * @param x
	 *            The current mouse X coord
	 * @param y
	 *            The current mouse Y coord
	 * @return Element eventElement
	 */
	private Element getEventElement(float x, float y, EventCheckType check) {
		if (Element.NEW_YFLIPPING) {
			guiRayOrigin.set(x, getHeight() - y, 0);
		} else {
			guiRayOrigin.set(x, y, 0);
		}

		elementZOrderRay.setOrigin(guiRayOrigin);
		results.clear();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		lastCollision = results.getClosestCollision();

		Element testEl = null, el = null;

		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof Element) {
				testEl = ((Element) (result.getGeometry().getParent()));
				if (getIgnoreEvent(testEl, check)) {
					discard = true;
				} else if (testEl.getIsClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX()
							|| result.getContactPoint().getX() > testEl.getClippingBounds().getZ()
							|| result.getContactPoint().getY() < testEl.getClippingBounds().getY()
							|| result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}

			if (!discard) {
				if (result.getGeometry().getParent() instanceof Element) {
					el = testEl;
				}
			}
		}

		if (el != null) {
			contactElement = el;
			Element parent = null;
			if (el.getEffectParent() && mousePressed) {
				parent = el.getElementParent();
			} else if (el.getEffectAbsoluteParent() && mousePressed) {
				parent = el.getAbsoluteParent();
			}
			if (parent != null) {
				el = parent;
			}
			eventElementOffsetX = x - el.getX();
			eventElementOffsetY = y - el.getY();
			return el;
		} else {
			// 2D Framework
			eventAnimElement = null;
			eventQuad = null;
			for (CollisionResult result : results) {
				boolean discard = false;
				if (result.getGeometry().getParent() instanceof AnimElement) {
					AnimElement testAnimEl = (AnimElement) result.getGeometry().getParent();
					if (result.getContactPoint().getX() < testAnimEl.getClippingPosition().getX()
							|| result.getContactPoint().getX() > testAnimEl.getClippingPosition().getZ()
							|| result.getContactPoint().getY() < testAnimEl.getClippingPosition().getY()
							|| result.getContactPoint().getY() > testAnimEl.getClippingPosition().getW()) {
						discard = true;
					}
					if (!discard) {
						eventAnimElement = (AnimElement) result.getGeometry().getParent();
						if (!eventAnimElement.getIgnoreMouse()) {
							eventAnimOffsetX = x - eventAnimElement.getPositionX();
							eventAnimOffsetY = y - eventAnimElement.getPositionY();
							eventQuad = eventAnimElement.getQuad((int) FastMath.floor(result.getTriangleIndex() / 2));
							eventQuadOffsetX = x - eventQuad.getPositionX();
							eventQuadOffsetY = y - eventQuad.getPositionY();
							break;
						} else {
							eventAnimElement = null;
							eventQuad = null;
						}
					}
				}
			}
			return null;
		}
	}

	private Element getContactElement(float x, float y, EventCheckType check) {
		if (Element.NEW_YFLIPPING) {
			guiRayOrigin.set(x, getHeight() - y, 0);
		} else {
			guiRayOrigin.set(x, y, 0);
		}

		elementZOrderRay.setOrigin(guiRayOrigin);
		results.clear();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		float z = 0;
		Element testEl = null, el = null;
		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof Element) {
				testEl = ((Element) (result.getGeometry().getParent()));
				if (getIgnoreEvent(testEl, check)) {
					discard = true;
				} else if (testEl.getIsClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX()
							|| result.getContactPoint().getX() > testEl.getClippingBounds().getZ()
							|| result.getContactPoint().getY() < testEl.getClippingBounds().getY()
							|| result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}
			if (!discard) {
				if (result.getGeometry().getParent() instanceof Element) {
					el = testEl;
				}
			}
		}
		if (el != null) {
			Element parent = null;
			if (el.getEffectParent() && mousePressed) {
				parent = el.getElementParent();
			} else if (el.getEffectAbsoluteParent() && mousePressed) {
				parent = el.getAbsoluteParent();
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

	/**
	 * Determines and returns the drop Element
	 * 
	 * @param x
	 *            The current mouse X coord
	 * @param y
	 *            The current mouse Y coord
	 * @return Element eventElement
	 */
	private Element getTargetDropElement(float x, float y) {

		if (Element.NEW_YFLIPPING) {
			guiRayOrigin.set(x, getHeight() - y, 0);
		} else {
			guiRayOrigin.set(x, y, 0);
		}

		elementZOrderRay.setOrigin(guiRayOrigin);
		results.clear();

		t0neg0dGUI.collideWith(elementZOrderRay, results);

		float z = 0;
		Element testEl = null, el = null;
		for (CollisionResult result : results) {
			boolean discard = false;
			if (result.getGeometry().getParent() instanceof Element) {
				testEl = ((Element) (result.getGeometry().getParent()));
				if (testEl.getIgnoreMouse() || testEl.getIsDragDropDragElement()
						|| (!testEl.getIsDragDropDragElement() && !testEl.getIsDragDropDropElement())) {
					discard = true;
				} else if (testEl.getIsClipped()) {
					if (result.getContactPoint().getX() < testEl.getClippingBounds().getX()
							|| result.getContactPoint().getX() > testEl.getClippingBounds().getZ()
							|| result.getContactPoint().getY() < testEl.getClippingBounds().getY()
							|| result.getContactPoint().getY() > testEl.getClippingBounds().getW()) {
						discard = true;
					}
				}
			}
			if (!discard) {

				if (result.getContactPoint().getZ() > z) {
					z = result.getContactPoint().getZ();
					if (result.getGeometry().getParent() instanceof Element) {
						el = testEl;// ((Element)(result.getGeometry().getParent()));
					}
				}
			}
		}
		if (el != null) {
			Element parent = null;
			if (el.getEffectParent() && mousePressed) {
				parent = el.getElementParent();
			} else if (el.getEffectAbsoluteParent() && mousePressed) {
				parent = el.getAbsoluteParent();
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

	private void setAnimElementZOrder() {
		if (eventAnimElement != null) {
			if (eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Self
					|| eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Both)
				if (eventAnimElement.getParentLayer() != null)
					eventAnimElement.getParentLayer().bringAnimElementToFront(eventAnimElement);
			if (eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Child
					|| eventAnimElement.getZOrderEffect() == AnimElement.ZOrderEffect.Both)
				eventAnimElement.bringQuadToFront(eventQuad);
		}
	}

	// <editor-fold desc="Menu Handling">
	private void handleMenuState() {
		if (!Screen.isAndroid()) {
			synchronized (elements) {
				if (eventElement == null) {
					for (Element el : elements.values()) {
						if (el instanceof AutoHide) {
							el.hide();
						}
					}
				} else {
					if (!(eventElement.getAbsoluteParent() instanceof AutoHide)
							&& !(eventElement.getParent() instanceof ComboBox)) {
						for (Element el : elements.values()) {
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
						for (Element el : elements.values()) {
							if (el instanceof AutoHide && el != ((ComboBox) eventElement.getParent()).getMenu()) {
								el.hide();
							}
						}
					}
				}
			}
		}
	}

	private void s3dOnMouseMotionEvent(MouseMotionEvent evt, boolean guiFocus) {
		if (!mousePressed) {
			float x = Screen.isAndroid() ? touchXY.x : mouseXY.x;
			float y = Screen.isAndroid() ? touchXY.y : mouseXY.y;
			mouseFocusNode = getEventNode(x, y);
			if (!guiFocus) {
				if (mouseFocusNode != previousMouseFocusNode) {
					if (previousMouseFocusNode instanceof MouseFocusListener) {
						((MouseFocusListener) previousMouseFocusNode).onLoseFocus(evt);
					}
					if (mouseFocusNode instanceof MouseFocusListener) {
						((MouseFocusListener) mouseFocusNode).onGetFocus(evt);
					}
					previousMouseFocusNode = mouseFocusNode;
				}
				if (mouseFocusNode != null) {
					if (mouseFocusNode instanceof MouseWheelListener) {
						if (evt.getDeltaWheel() > 0) {
							((MouseWheelListener) mouseFocusNode).onMouseWheelDown(evt);
						} else if (evt.getDeltaWheel() < 0) {
							((MouseWheelListener) mouseFocusNode).onMouseWheelUp(evt);
						}
					}
				}
				if (mouseFocusNode instanceof MouseMovementListener) {
					((MouseMovementListener) mouseFocusNode).onMouseMove(evt);
				}
			} else {
				if (previousMouseFocusNode instanceof MouseFocusListener) {
					((MouseFocusListener) previousMouseFocusNode).onLoseFocus(evt);
					previousMouseFocusNode = null;
				}
			}
		}
	}

	private void s3dOnMouseButtonEvent(MouseButtonEvent evt) {
		float x = Screen.isAndroid() ? touchXY.x : mouseXY.x;
		float y = Screen.isAndroid() ? touchXY.y : mouseXY.y;
		eventNode = getEventNode(x, y);
		if (eventNode != null) {
			if (evt.isPressed()) {
				switch (evt.getButtonIndex()) {
				case 0:
					if (eventNode instanceof MouseButtonListener) {
						((MouseButtonListener) eventNode).onMouseLeftPressed(evt);
					}
					break;
				case 1:
					if (eventNode instanceof MouseButtonListener) {
						((MouseButtonListener) eventNode).onMouseRightPressed(evt);
					}
					break;
				case 2:
					if (eventNode instanceof MouseWheelListener) {
						((MouseWheelListener) eventNode).onMouseWheelPressed(evt);
					}
					break;
				}
			} else if (evt.isReleased()) {
				switch (evt.getButtonIndex()) {
				case 0:
					if (eventNode instanceof MouseButtonListener) {
						((MouseButtonListener) eventNode).onMouseLeftReleased(evt);
					}
					break;
				case 1:
					if (eventNode instanceof MouseButtonListener) {
						((MouseButtonListener) eventNode).onMouseRightReleased(evt);
					}
					break;
				case 2:
					if (eventNode instanceof MouseWheelListener) {
						((MouseWheelListener) eventNode).onMouseWheelReleased(evt);
					}
					break;
				}
				eventNode = null;
			}
		}
	}

	private void s3dOnTouchDownEvent(TouchEvent evt) {
		float x = Screen.isAndroid() ? touchXY.x : mouseXY.x;
		float y = Screen.isAndroid() ? touchXY.y : mouseXY.y;
		Node target = getEventNode(x, y);
		if (target != null) {
			if (target instanceof MouseButtonListener) {
				MouseButtonEvent mbEvt = new MouseButtonEvent(0, true, (int) x, (int) y);
				((MouseButtonListener) target).onMouseLeftPressed(mbEvt);
			}
			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchDown(evt);
			}
			eventNodes.put(evt.getPointerId(), target);
		}
	}

	private void s3dOnTouchMoveEvent(TouchEvent evt) {
		float x = Screen.isAndroid() ? touchXY.x : mouseXY.x;
		float y = Screen.isAndroid() ? touchXY.y : mouseXY.y;
		for (Integer key : eventNodes.keySet()) {
			if (key == evt.getPointerId()) {
				Node target = eventNodes.get(key);
				if (target != null) {
					if (target instanceof MouseMovementListener) {
						MouseMotionEvent mbEvt = new MouseMotionEvent((int) x, (int) y, (int) evt.getDeltaX(),
								(int) evt.getDeltaY(), 0, 0);
						((MouseMovementListener) target).onMouseMove(mbEvt);
					}
					if (target instanceof TouchListener) {
						((TouchListener) target).onTouchMove(evt);
					}
				}
			}
		}
	}

	private void s3dOnTouchUpEvent(TouchEvent evt) {
		float x = Screen.isAndroid() ? touchXY.x : mouseXY.x;
		float y = Screen.isAndroid() ? touchXY.y : mouseXY.y;
		Node target = eventNodes.get(evt.getPointerId());
		if (target != null) {
			if (target instanceof MouseButtonListener) {
				MouseButtonEvent mbEvt = new MouseButtonEvent(0, true, (int) x, (int) y);
				((MouseButtonListener) target).onMouseLeftReleased(mbEvt);
			}
			if (target instanceof TouchListener) {
				((TouchListener) target).onTouchUp(evt);
			}
			eventNodes.remove(evt.getPointerId());
		}
	}

	private void setToolTipLocation() {
		float nextX = (Screen.isAndroid()) ? touchXY.x - (toolTip.getWidth() / 2) : mouseXY.x - (toolTip.getWidth() / 2);
		if (nextX < 0)
			nextX = 0;
		else if (nextX + toolTip.getWidth() > getWidth())
			nextX = getWidth() - toolTip.getWidth();
		float nextY = Element.NEW_YFLIPPING ? ((Screen.isAndroid()) ? touchXY.y + 40 : mouseXY.y + 40)
				: ((Screen.isAndroid()) ? touchXY.y - toolTip.getHeight() - 40 : mouseXY.y - toolTip.getHeight() - 40);
		if (nextY < 0)
			nextY = (Screen.isAndroid()) ? touchXY.y + 5 : mouseXY.y + 5;
		// TODO no idea if this makes any difference
		toolTip.moveTo((int) nextX, (int) nextY);
	}

	/**
	 * Send reset to the current Tab Focus Element
	 */
	private void resetFocusElement() {
		if (tabFocusElement != null) {
			if (tabFocusElement.getResetKeyboardFocus()) {
				if (tabFocusElement instanceof TabFocusListener) {
					((TabFocusListener) tabFocusElement).resetTabFocus();
				}
			}
		}
	}

	private void initModalBackground() {
		modalBackground = new ModalBackground(this);
		modalBackground.hide();
	}

	private void initVirtualKeys() {
		virtualKeys = new Keyboard(this);
		virtualKeys.setIsModal(true);
		virtualKeys.setIsGlobalModal(true);
		addElement(virtualKeys);
		virtualKeys.hide();
	}

	private Node getEventNode(float x, float y) {
		Node testEl = null, el = null;

		for (ViewPort vp : app.getRenderManager().getMainViews()) {
			Node root = (Node) vp.getScenes().get(0);

			if (!Screen.isAndroid())
				click2d.set(app.getInputManager().getCursorPosition());
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
					if (parent instanceof MouseFocusListener || parent instanceof MouseButtonListener
							|| parent instanceof MouseMovementListener || parent instanceof MouseWheelListener
							|| parent instanceof TouchListener) {
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
	// </editor-fold>
}
