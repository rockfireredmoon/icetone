package icetone.core;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.css.CSSPrimitiveValue;
import org.xhtmlrenderer.context.StylesheetFactoryImpl;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.newmatch.Matcher;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.app.Application;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import icetone.controls.extras.DragElement;
import icetone.controls.form.Form;
import icetone.core.CssProcessor.PseudoStyle;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.event.MouseUIMotionEvent;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;
import icetone.framework.core.AnimText;
import icetone.listeners.MouseButtonListener;
import icetone.listeners.MouseMovementListener;
import icetone.xhtml.TGGUserAgent;

/**
 * <p>
 * The Element class is the primitive in which all controls in the GUI portion
 * or the library are built upon. Unlink the overloaded common constructor(s)
 * you will find throughout the library, the library, there is a single verbose
 * constructor:<br/>
 * </br>
 * 
 * @see #Element(icetone.core.ElementManager, java.lang.String,
 *      com.jme3.math.Vector2f, com.jme3.math.Vector2f, com.jme3.math.Vector4f,
 *      java.lang.String)
 *      </p>
 *      <p>
 *      Element is backed by a 9-patch style Mesh and can be both movable and
 *      resizable by simply flagging them as such. There is no need to add
 *      Listeners to leverage this default behavior.<br/>
 *      <br/>
 *      See both:<br/>
 * @see #setIsMovable(boolean)
 * @see #setIsResizable(boolean)
 *      </p>
 * @author t0neg0d
 */
public class Element extends Node {

	static {

		LUtil.setInaccessibleField(true, "implemented", CSSName.CLIP, CSSName.class);
	}

	public static boolean CACHED_Z = true;
	public static boolean FORCE_LAYOUT = false;
	public static boolean NEW_YFLIPPING = true;
	public static boolean ENABLE_CLIPPING = true;

	public enum ZPriority {
		BACKGROUND, LAYERS, NORMAL, MENU, POPUP, DRAG, TOOLTIP, FOREGROUND
	}

	public static enum Borders {
		NW, N, NE, W, E, SW, S, SE;

		public Borders opposite() {
			switch (this) {
			case NW:
				return SE;
			case N:
				return S;
			case NE:
				return SW;
			case W:
				return E;
			case E:
				return W;
			case SW:
				return NE;
			case S:
				return N;
			case SE:
				return NW;
			}
			throw new IllegalArgumentException();
		}
	};

	/**
	 * Some controls provide different layout's based on the orientation of the
	 * control
	 */
	public static enum Orientation {
		/**
		 * Vertical layout
		 */
		VERTICAL,
		/**
		 * Horizontal layout
		 */
		HORIZONTAL
	}

	/**
	 * Defines how the element will dock to it's parent element during resize
	 * events
	 */
	public static enum Docking {
		/**
		 * Docks to the top left of parent
		 */
		NW,
		/**
		 * Docks to the top right of parent
		 */
		NE,
		/**
		 * Docks to the bottom left of parent
		 */
		SW,
		/**
		 * Docks to the bottom right of parent
		 */
		SE
	}

	// <editor-fold desc="Fields">
	protected Application app;
	protected ElementManager screen;
	private String UID;
	private Vector2f position = new Vector2f();
	public Vector2f orgPosition = new Vector2f();
	private Vector2f dimensions = LUtil.LAYOUT_SIZE.clone();
	public Vector2f orgDimensions;
	public Vector4f borders = new Vector4f(1, 1, 1, 1);
	public Vector4f borderHandles = new Vector4f(12, 12, 12, 12);
	private Vector2f minDimensions = null;

	private boolean ignoreMouse = false;
	private boolean ignoreMouseLeftButton = false;
	private boolean ignoreMouseRightButton = false;
	private boolean ignoreMouseWheelClick = true;
	private boolean ignoreMouseWheelMove = true;
	private boolean ignoreMouseFocus = false;
	private boolean ignoreTouch = false;
	private boolean ignoreTouchMove = false;
	private boolean ignoreFling = false;
	protected boolean isMovable = false;
	private boolean lockToParentBounds = false;
	private boolean isResizable = false;
	private boolean resizeN = true;
	private boolean resizeS = true;
	private boolean resizeW = true;
	private boolean resizeE = true;

	// TODO get rid of
	private boolean dockN = false;
	private boolean dockW = true;
	private boolean dockE = false;
	private boolean dockS = true;
	private boolean scaleNS = false;
	private boolean scaleEW = false;

	private boolean effectParent = false;
	private boolean effectAbsoluteParent = false;

	private Geometry geom;
	private ElementQuadGrid model;
	private boolean tileImage = false;
	private Material mat;
	private Texture defaultTex;
	private boolean useLocalAtlas = false;
	private boolean useLocalTexture = false;
	private String atlasCoords = "";
	private Texture alphaMap = null;

	protected BitmapText textElement;
//	 protected TextElement textElement;
//	 protected AnimText textElement;
	 
	protected Vector2f textPosition = new Vector2f(0, 0);
	protected LineWrapMode textWrap = LineWrapMode.Word;
	protected BitmapFont.Align textAlign = BitmapFont.Align.Left;
	protected BitmapFont.VAlign textVAlign = BitmapFont.VAlign.Center;
	protected String text = "";
	private String toolTipText = null;

	protected BitmapFont font;
	protected float fontSize = 20;
	protected Vector4f textPadding = new Vector4f(0, 0, 0, 0);
	protected ColorRGBA fontColor = ColorRGBA.White;
	private ColorRGBA defaultColor = new ColorRGBA(1, 1, 1, 0);

	private Element elementParent = null;
	protected Map<String, Element> elementChildren = new LinkedHashMap();
	protected List<Element> childList = new ArrayList<>();

	// Clipping
	protected boolean isClipped = false;
	protected boolean wasClipped = false;
	protected Element clippingLayer, secondaryClippingLayer;
	protected Vector4f clippingBounds = new Vector4f();
	private Vector4f clipPadding = new Vector4f(0, 0, 0, 0);
	private Vector4f textClipPadding = new Vector4f(0, 0, 0, 0);

	// New Clipping
	protected List<ClippingDefine> clippingLayers = new ArrayList();
	private List<ClippingDefine> remClippingLayers = new ArrayList();
	private Vector4f clipTest = new Vector4f();

	protected boolean isVisible = true;
	protected boolean wasVisible = true;
	protected boolean isVisibleAsModal = false;
	private boolean hasFocus = false;
	private boolean resetKeyboardFocus = true;

	private Form form;
	private int tabIndex = 0;

	float zOrder;
	private boolean effectZOrder = true;
	private Map<Effect.EffectEvent, Effect> effects = new HashMap();

	private OSRBridge bridge;

	private boolean ignoreGlobalAlpha = false;
	private boolean isModal = false;
	private boolean isGlobalModal = false;

	private Object elementUserData;

	private boolean initialized = false;
	protected boolean isEnabled = true;

	private boolean isDragElement = false, isDropElement = false;

	// TODO remove
	private Docking docking = null;

	protected float globalAlpha = 1f;
	protected boolean isAlwaysOnTop;

	protected boolean layoutDirty = true;
	protected LayoutManager layoutManager;
	protected Element associatedLabel;
	protected Vector2f maxDimensions;
	protected Vector2f prefDimensions;
	private Vector2f containerDimensions = new Vector2f(0, 0);
	protected Vector2f containerPosition = new Vector2f(0, 0);
	private boolean isKeepWithinScreenBounds;
	private List<Stylesheet> stylesheets;
	private String css;
	private String styleClass;
	private String styleId;
	private ToolTipProvider toolTipProvider;
	ZPriority priority = ZPriority.NORMAL;
	private boolean clippingEnabled = true;

	//
	private List<MouseMovementListener> mouseMotionListeners;
	private List<MouseButtonListener> mouseButtonListeners;
	private Object constraints;

	// </editor-fold>
	public Element() {
		this(Screen.get());
	}

	public Element(LayoutManager layoutManager) {
		this(Screen.get(), layoutManager);
	}

	public Element(ElementManager screen, LayoutManager layoutManager) {
		this(screen);
		setLayoutManager(layoutManager);
	}

	public Element(ElementManager screen) {
		this(screen, LUtil.LAYOUT_SIZE);
	}

	public Element(ElementManager screen, Vector2f dimensions) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, dimensions, Vector4f.ZERO, null);
	}

	public Element(ElementManager screen, String UID) {
		this(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
	}

	public Element(ElementManager screen, String UID, Vector2f dimensions) {
		this(screen, UID, Vector2f.ZERO, dimensions, Vector4f.ZERO, null);
	}

	public Element(ElementManager screen, Vector4f resizeBorders, String texturePath) {
		this(screen, UIDUtil.getUID(), resizeBorders, texturePath);
	}

	public Element(ElementManager screen, String UID, Vector4f resizeBorders, String texturePath) {
		this(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, resizeBorders, texturePath);
	}

	public Element(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
		this(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, texturePath);
	}

	/**
	 * The Element class is the single primitive for all controls in the gui
	 * library. Each element consists of an ElementQuadMesh for rendering
	 * resizable textures, as well as a BitmapText element if setText(String
	 * text) is called.
	 * 
	 * Behaviors, such as movement and resizing, are common to all elements and
	 * can be enabled/disabled to ensure the element reacts to user input as
	 * needed.
	 * 
	 * @param screen
	 *            The Screen control the element or it's absolute parent element
	 *            is being added to
	 * @param UID
	 *            A unique String identifier used when looking up elements by
	 *            screen.getElementByID()
	 * @param position
	 *            A Vector2f containing the x/y coordinates (relative to it's
	 *            parent elements x/y) for positioning
	 * @param dimensions
	 *            A Vector2f containing the dimensions of the element, x being
	 *            width, y being height
	 * @param resizeBorders
	 *            A Vector4f containing the size of each border used for scaling
	 *            images without distorting them (x = N, y = W, x = E, w = S)
	 * @param texturePath
	 *            A String path to the default image to be rendered on the
	 *            element's mesh
	 */
	public Element(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String texturePath) {

		this.app = screen.getApplication();
		this.screen = screen;

		if ("NEW".equals(UID)) {
			// New construction method!
			if (!screen.getStylesheets().isEmpty() || stylesheets != null)
				applyCss();
			return;
		}

		if (dimensions == null)
			dimensions = LUtil.LAYOUT_SIZE;
		if (UID == null) {
			this.UID = UIDUtil.getUID();
		} else {
			this.UID = UID;
		}
		this.position.set(position);

		if (!LUtil.LAYOUT_SIZE.equals(dimensions)) {
			this.prefDimensions = dimensions.clone();
			// this.minDimensions = this.prefDimensions;
			// this.maxDimensions = this.prefDimensions;
		}
		this.dimensions.set(dimensions);
		this.orgDimensions = dimensions.clone();
		this.borders.set(resizeBorders);

		BitmapFont tempFont = app.getAssetManager().loadFont(screen.getStyle("Font").getString("defaultFont"));

		fontSize = screen.getStyle("Font").getFloat("defaultFontSize");
		font = new BitmapFont();
		font.setCharSet(app.getAssetManager().loadFont(screen.getStyle("Font").getString("defaultFont")).getCharSet());
		Material[] pages = new Material[tempFont.getPageSize()];
		for (int i = 0; i < pages.length; i++) {
			pages[i] = tempFont.getPage(i).clone();
		}
		font.setPages(pages);

		float imgWidth = 100;
		float imgHeight = 100;
		float pixelWidth = 1f / imgWidth;
		float pixelHeight = 1f / imgHeight;
		float textureAtlasX = 0, textureAtlasY = 0, textureAtlasW = imgWidth, textureAtlasH = imgHeight;

		boolean useAtlas = screen.getUseTextureAtlas();

		if (texturePath != null) {
			if (useAtlas && texturePath.indexOf("|") != -1 && texturePath.indexOf("=") != -1) {
				float[] coords = screen.parseAtlasCoords(texturePath);
				textureAtlasX = coords[0];
				textureAtlasY = coords[1];
				textureAtlasW = coords[2];
				textureAtlasH = coords[3];

				this.atlasCoords = "x=" + coords[0] + "|y=" + coords[1] + "|w=" + coords[2] + "|h=" + coords[3];

				defaultTex = screen.getAtlasTexture();

				imgWidth = defaultTex.getImage().getWidth();
				imgHeight = defaultTex.getImage().getHeight();
				pixelWidth = 1f / imgWidth;
				pixelHeight = 1f / imgHeight;

				textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;
			} else {
				if (useAtlas)
					useLocalTexture = true;

				defaultTex = app.getAssetManager().loadTexture(texturePath);
				defaultTex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
				defaultTex.setMagFilter(Texture.MagFilter.Nearest);
				defaultTex.setWrap(Texture.WrapMode.Clamp);

				imgWidth = defaultTex.getImage().getWidth();
				imgHeight = defaultTex.getImage().getHeight();
				pixelWidth = 1f / imgWidth;
				pixelHeight = 1f / imgHeight;

				textureAtlasW = imgWidth;
				textureAtlasH = imgHeight;
			}
		}
		mat = new Material(app.getAssetManager(), "icetone/shaders/Unshaded.j3md");
		if (texturePath != null) {
			mat.setTexture("ColorMap", defaultTex);
			mat.setColor("Color", new ColorRGBA(1, 1, 1, 1));
		} else {
			mat.setColor("Color", defaultColor);
		}
		if (useAtlas)
			mat.setBoolean("UseEffectTexCoords", true);
		mat.setVector2("OffsetAlphaTexCoord", new Vector2f(0, 0));
		mat.setFloat("GlobalAlpha", screen.getGlobalAlpha() * getAlphaFactor());

		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);

		this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
				textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);

		this.setName(UID + ":Node");

		geom = new Geometry(UID + ":Geometry");
		geom.setMesh(model);
		geom.setCullHint(CullHint.Never);
		geom.setQueueBucket(Bucket.Gui);
		geom.setMaterial(mat);

		this.attachChild(geom);

		this.setQueueBucket(Bucket.Gui);

		if (this instanceof ToolTipProvider)
			setToolTipProvider((ToolTipProvider) this);

		setNodeLocation();
	}

	public Object getConstraints() {
		return constraints;
	}

	public void setConstraints(Object constraints) {
		this.constraints = constraints;
	}

	public void addMouseMotionListener(MouseMovementListener l) {
		if (mouseMotionListeners == null)
			mouseMotionListeners = new ArrayList<>();
		mouseMotionListeners.add(l);
	}

	public void removeMouseMotionListener(MouseMovementListener l) {
		mouseMotionListeners.remove(l);
		if (mouseMotionListeners.isEmpty())
			mouseMotionListeners = null;
	}

	public void bindPressed(MouseButtonListener l) {
		bindPressed(l, MouseUIButtonEvent.LEFT);
	}

	public void bindPressed(MouseButtonListener l, int button) {
		addMouseButtonListener(new MouseButtonListener() {
			@Override
			public void onMouseButton(MouseUIButtonEvent evt) {
				if (evt.isPressed() && (button == -1 || evt.getButtonIndex() == button))
					l.onMouseButton(evt);
			}
		});
	}

	public void bindReleased(MouseButtonListener l) {
		bindReleased(l, MouseUIButtonEvent.LEFT);
	}

	public void bindReleased(MouseButtonListener l, int button) {
		addMouseButtonListener(new MouseButtonListener() {
			@Override
			public void onMouseButton(MouseUIButtonEvent evt) {
				if (evt.isPressed() && (button == -1 || evt.getButtonIndex() == button))
					l.onMouseButton(evt);
			}
		});
	}

	public void addMouseButtonListener(MouseButtonListener l) {
		if (mouseButtonListeners == null)
			mouseButtonListeners = new ArrayList<>();
		mouseButtonListeners.add(l);
	}

	public void removeMouseButtonListener(MouseButtonListener l) {
		mouseButtonListeners.remove(l);
		if (mouseButtonListeners.isEmpty())
			mouseButtonListeners = null;
	}

	void fireMouseMotionEvent(MouseUIMotionEvent evt) {
		if (mouseMotionListeners != null)
			for (int i = mouseMotionListeners.size() - 1; i >= 0; i--) {
				mouseMotionListeners.get(i).onMouseMove(evt);
			}
	}

	void fireMouseButtonEvent(MouseUIButtonEvent evt) {
		if (mouseButtonListeners != null)
			for (int i = mouseButtonListeners.size() - 1; i >= 0; i--) {
				mouseButtonListeners.get(i).onMouseButton(evt);
			}
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyleId() {
		return styleId;
	}

	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
		applyCss();
		layoutChildren();
	}

	public void addStylesheet(Stylesheet sheet) {
		if (stylesheets == null)
			stylesheets = new ArrayList<>();
		stylesheets.add(sheet);
		applyCss();
		layoutChildren();
	}

	public final void layoutChildren() {
		// TODO temp
		if (FORCE_LAYOUT)
			dirtyLayout(false);

		// if (layoutDirty) {
		// if (layoutManager != null && initialized) {
		// onBeforeLayout();
		// layoutManager.layout(this);
		// layoutHeirarchy(this);
		// onAfterLayout();
		// } else {
		// layoutHeirarchy(this);
		// }
		// layoutDirty = false;
		// updateLocalClippingLayer();
		// }

		if (layoutDirty) {
			if (layoutManager != null) {
				if (isHeirarchyInitialized()) {
					onBeforeLayout();
					layoutManager.layout(this);
					layoutHeirarchy(this);
					onAfterLayout();
					layoutDirty = false;
				}
			} else {
				layoutHeirarchy(this);
				layoutDirty = false;
			}
			updateLocalClippingLayer();
		}
	}

	public int countParents() {
		Element e = elementParent;
		int i = 0;
		while (e != null) {
			i++;
			e = elementParent.getElementParent();
		}
		return i;
	}

	protected boolean isHeirarchyInitialized() {
		return initialized && (elementParent == null || elementParent.isHeirarchyInitialized());
	}

	protected void layoutHeirarchy(Node s) {
		applyZOrder();
		Collection<? extends Spatial> children;
		if (s instanceof Element) {
			children = ((Element) s).getElements();
		} else {
			children = s.getChildren();
		}
		for (Spatial childSpatial : children) {
			if (childSpatial instanceof Element) {
				((Element) childSpatial).layoutChildren();
			} else if (childSpatial instanceof Node) {
				layoutHeirarchy(((Node) childSpatial));
			}
		}
	}

	protected void onBeforeLayout() {
		// Instances can override to do stuff on layout
	}

	protected void onAfterLayout() {
		// Instances can override to do stuff on layout
	}

	protected final void layoutControl() {
		// TODO temporary to catch usages
		// Instances can override to do stuff on layout
	}

	public Element setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
		dirtyLayout(false);
		layoutChildren();
		return this;
	}

	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	public ColorRGBA getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(ColorRGBA defaultColor) {
		this.defaultColor = defaultColor;
		mat.setColor("Color", defaultColor);
	}

	public void setTexture(Texture texture) {

		this.defaultTex = texture;
		if (defaultTex == null) {
			if (this.geom != null) {
				this.geom.removeFromParent();
			}
			mat.setTexture("ColorMap", null);
			mat.setColor("Color", defaultColor);

			float imgWidth = 100;
			float imgHeight = 100;
			float pixelWidth = 1f / imgWidth;
			float pixelHeight = 1f / imgHeight;
			float textureAtlasX = 0, textureAtlasY = 0, textureAtlasW = imgWidth, textureAtlasH = imgHeight;

			this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
					textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);

			this.setName(UID + ":Node");

			geom = new Geometry(UID + ":Geometry");
			geom.setMesh(model);
			geom.setCullHint(CullHint.Never);
			geom.setQueueBucket(Bucket.Gui);
			geom.setMaterial(mat);

			this.attachChild(geom);

			setNodeLocation();

		} else {

			defaultTex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			defaultTex.setMagFilter(Texture.MagFilter.Nearest);
			defaultTex.setWrap(Texture.WrapMode.Clamp);

			float imgWidth = 100;
			float imgHeight = 100;
			float pixelWidth = 1f / imgWidth;
			float pixelHeight = 1f / imgHeight;
			float textureAtlasX = 0, textureAtlasY = 0, textureAtlasW = imgWidth, textureAtlasH = imgHeight;

			imgWidth = defaultTex.getImage().getWidth();
			imgHeight = defaultTex.getImage().getHeight();
			pixelWidth = 1f / imgWidth;
			pixelHeight = 1f / imgHeight;

			textureAtlasW = imgWidth;
			textureAtlasH = imgHeight;

			this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
					textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);

			this.setName(UID + ":Node");

			this.geom.setMesh(model);
			mat.setTexture("ColorMap", defaultTex);
			mat.setColor("Color", new ColorRGBA(1, 1, 1, 1));
			geom.setMaterial(mat);

			this.attachChild(geom);
		}

		if (elementParent != null) {
			elementParent.dirtyLayout(true);
			elementParent.layoutChildren();
		}
	}

	/**
	 * Converts the the inputed percentage (0.0f-1.0f) into pixels of the
	 * elements image
	 * 
	 * @param in
	 *            Vector2f containing the x and y percentage
	 * @return Vector2f containing the actual width/height in pixels
	 */
	public final Vector2f getV2fPercentToPixels(Vector2f in) {
		if (getElementParent() == null) {
			if (in.x < 1)
				in.setX(screen.getWidth() * in.x);
			if (in.y < 1)
				in.setY(screen.getHeight() * in.y);
		} else {
			if (in.x < 1)
				in.setX(getElementParent().getWidth() * in.x);
			if (in.y < 1)
				in.setY(getElementParent().getHeight() * in.y);
		}
		return in;
	}

	public void insertChild(Element el, Object constraints, int index) {
		addChild(el, constraints, false, true, -1);
	}

	public void insertChild(Element el, Object constraints, boolean hide, boolean layout, int index) {
		addChild(el, constraints, hide, layout, -1);
	}

	// <editor-fold desc="Parent/Child">
	/**
	 * Adds the specified Element as a child to this Element.
	 * 
	 * @param child
	 *            The Element to add as a child
	 */
	public void addChild(Element child) {
		addChild(child, false);
	}

	/**
	 * Adds the specified Element as a child to this Element.
	 * 
	 * @param child
	 *            The Element to add as a child
	 */
	public void addChild(Element child, boolean hide) {
		addChild(child, null, hide, true);
	}

	public void addChild(Element child, Object constraints) {
		addChild(child, constraints, false, true);
	}

	public void addChild(Element child, Object constraints, boolean hide, boolean layout) {
		addChild(child, constraints, hide, layout, -1);
	}

	protected void addChild(Element child, Object constraints, boolean hide, boolean layout, int index) {
		if (constraints == null)
			constraints = child.getConstraints();

		// child.setInitialized();
		child.elementParent = this;

		// for (ClippingDefine def : clippingLayers) {
		// if (def.getElement() != null) {
		// // if (child.getX() >= 0 && child.getX()+child.getWidth() <=
		// // def.getElement().getWidth() &&
		// // child.getY() >= 0 && child.getY()+child.getHeight() <=
		// // def.getElement().getHeight())
		// child.addClippingLayer(def.getElement());
		// }// else
		// // child.addClippingLayer(def.getElement(),def.getClipping());
		// }

		if (screen != null && !child.getInitialized()) {
			if (!Element.NEW_YFLIPPING)
				child.setY(this.getHeight() - child.getHeight() - child.getY());
			child.orgPosition = position.clone();
			// child.orgPosition.setY(child.getY());
			child.setInitialized(screen);
		}
		child.setQueueBucket(RenderQueue.Bucket.Gui);

		if (screen.getElementById(child.getUID()) != null) {
			Element other = screen.getElementById(child.getUID());
			throw new ConflictingIDException(String.format(
					"The child element '%s' (class: %s, hash: %s) conflicts with a previously added child element in parent element '%s' (class: %s, hash: %s).",
					child.getUID(), child.getClass(), child.hashCode(), getUID(), other.getClass(), other.hashCode()));
		} else {
			if (index == -1)
				childList.add(child);
			else
				childList.add(index, child);
			elementChildren.put(child.getUID(), child);
			this.attachChild(child);

			if (hide)
				child.hide();
		}
		if (constraints != null && layoutManager == null) {
			throw new IllegalStateException("Must have layout manager set to use constraints.");
		}
		dirtyLayout(false);
		if (layoutManager != null) {
			layoutManager.constrain(child, constraints);
		}
		if (layout)
			layoutChildren();
		else
			applyZOrder();
	}

	/**
	 * Removes the specified Element
	 * 
	 * @param child
	 *            Element to remove
	 */
	public void removeChild(Element child) {
		removeChild(child, true);
	}

	/**
	 * Removes the specified Element
	 * 
	 * @param child
	 *            Element to remove
	 */
	public void removeChild(Element child, boolean layout) {
		dirtyLayout(false);
		Element e = elementChildren.remove(child.getUID());
		if (e != null) {
			childList.remove(e);
			e.elementParent = null;
			e.removeFromParent();
			e.removeClippingLayer(this);
			for (ClippingDefine def : clippingLayers)
				e.removeClippingLayer(def.getElement());
			e.cleanup();

			if (screen.getUseToolTips()) {
				if (screen.getToolTipFocus() == this)
					screen.hideToolTip();
				else if (screen.getToolTipFocus() != null) {
					if (getChildElementById(screen.getToolTipFocus().getUID()) != null)
						screen.hideToolTip();
				}
			}
		}

		if (layoutManager != null) {
			layoutManager.remove(child);
		}
		if (layout)
			layoutChildren();
	}

	/**
	 * Remove all child Elements from this Element
	 */
	public void removeAllChildren() {
		for (Element e : elementChildren.values()) {
			e.removeFromParent();
			e.removeClippingLayer(this);
			for (ClippingDefine def : clippingLayers)
				e.removeClippingLayer(def.getElement());
		}
		childList.clear();
		if (layoutManager != null) {
			for (Element e : elementChildren.values()) {
				layoutManager.remove(e);
			}
		}
		elementChildren.clear();
		dirtyLayout(false);
		layoutChildren();
	}

	public void setLabel(Element label) {
		this.associatedLabel = label;
		setLabelVisibility();
	}

	/**
	 * Returns the child elements as a Map
	 * 
	 * @return
	 */
	public Map<String, Element> getElementsAsMap() {
		return this.elementChildren;
	}

	/**
	 * Returns the child elements as a List
	 * 
	 * @return
	 */
	public List<Element> getElementList() {
		return this.childList;
	}

	/**
	 * Returns the child elements as a Collection
	 * 
	 * @return
	 */
	public Collection<Element> getElements() {
		return this.elementChildren.values();
	}

	/**
	 * Returns the one and only Element's screen
	 * 
	 * @return
	 */
	public ElementManager getScreen() {
		return this.screen;
	}

	/**
	 * Returns a list of all children that are an instance of DragElement
	 * 
	 * @return List<Element>
	 */
	public List<Element> getDraggableChildren() {
		List<Element> ret = new ArrayList();
		for (Element el : elementChildren.values()) {
			if (el instanceof DragElement) {
				ret.add(el);
			}
		}
		return ret;
	}

	/**
	 * Recursively searches children elements for specified element containing
	 * the specified UID
	 * 
	 * @param UID
	 *            - Unique Indentifier of element to search for
	 * @return Element containing UID or null if not found
	 */
	public Element getChildElementById(String UID) {
		Element ret = null;
		if (this.UID.equals(UID)) {
			ret = this;
		} else {
			if (elementChildren.containsKey(UID)) {
				ret = elementChildren.get(UID);
			} else {
				for (Element el : elementChildren.values()) {
					ret = el.getChildElementById(UID);
					if (ret != null) {
						break;
					}
				}
			}
		}
		return ret;
	}

	public Element getChildElementByStyleId(String styleId) {
		for (Element el : elementChildren.values()) {
			if (styleId.equals(el.getStyleId()))
				return el;
			Element ret = el.getChildElementByStyleId(styleId);
			if (ret != null)
				return ret;
		}
		return null;
	}

	/**
	 * Returns the top-most parent in the tree of Elements. The topmost element
	 * will always have a parent of null
	 * 
	 * @return Element elementParent
	 */
	public Element getAbsoluteParent() {
		if (elementParent == null) {
			return this;
		} else {
			return elementParent.getAbsoluteParent();
		}
	}

	/**
	 * Returns the parent element of this node
	 * 
	 * @return Element elementParent
	 */
	public Element getElementParent() {
		return elementParent;
	}

	/**
	 * Sets the element's parent element
	 * 
	 * @param elementParent
	 *            Element
	 */
	public void setElementParent(Element elementParent) {
		this.elementParent = elementParent;
	}

	// </editor-fold>

	/**
	 * Allows for setting the Element UID if (and ONLY if) the Element Parent is
	 * null
	 * 
	 * @param UID
	 *            The new UID
	 * @return boolean If setting the UID was successful
	 */
	public boolean setUID(String UID) {
		if (this.elementParent == null) {
			this.UID = UID;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the element's unique string identifier
	 * 
	 * @return String UID
	 */
	public String getUID() {
		return UID;
	}

	protected void applyZOrder() {
		// float zi = 1f / (childList.size() + 1);
		float zi = zStep / (childList.size() + 1);
		float z = zi;
		// List<Element> sorted = new ArrayList<>(childList);
		// Collections.sort(sorted, new ZOrderComparator(childList));
		// for (Element e : sorted) {

		// TODO Need to sort elements or just top levels?
		for (Element e : childList) {
			e.setZStep(zi);
			e.setLocalTranslation(e.getLocalTranslation().setZ(z));
			// e.setLocalTranslation(e.getLocalTranslation().setZ(0));
			// System.out.println(String.format("%" + Math.max(1,
			// getParentCount()) + "s[%d] Z of %s is %f (%f - world %f)",
			// "",
			// getParentCount(), e, z, zi, e.getWorldTranslation().z));
			e.applyZOrder();
			// ((Element) e).updateZ();
			z += zi;
		}
	}

	public void bringToFront() {
		Element elementParent = getElementParent();
		if (elementParent != null) {
			if (elementParent.childList.remove(this)) {
				elementParent.childList.add(this);
				if (elementParent.children.remove(this)) {
					elementParent.children.add(this);
				}
			}
		}
		if (elementParent != null) {
			elementParent.bringToFront();
		} else {
			screen.updateZOrder(this);
		}
		movedToFrontHook();
	}

	public ZPriority getPriority() {
		return priority;
	}

	public void setPriority(ZPriority priority) {
		if (!Objects.equals(priority, this.priority)) {
			if (isVisibleAsModal && priority != ZPriority.NORMAL) {
				throw new IllegalArgumentException(
						String.format("Modal elements may only be of %s priority", ZPriority.NORMAL));
			}
			this.priority = priority;
			if (elementParent != null) {
				elementParent.applyZOrder();
			} else {
				screen.updateZOrder(this);
			}
		}
	}

	public void setEffectZOrder(boolean effectZOrder) {
		this.effectZOrder = effectZOrder;
	}

	public boolean getEffectZOrder() {
		return this.effectZOrder;
	}

	// </editor-fold>

	// <editor-fold desc="Scaling, Docking & Other Behaviors">
	/**
	 * The setAsContainer only method removes the Mesh component (rendered Mesh)
	 * from the Element, leaving only Element functionality. Call this method to
	 * set the Element for use as a parent container.
	 */
	public void setAsContainerOnly() {
		detachChildAt(0);
	}

	/**
	 * Informs the screen control that this Element should be ignored by mouse
	 * events.
	 * 
	 * @param ignoreMouse
	 *            boolean
	 */
	public void setIgnoreMouse(boolean ignoreMouse) {
		this.ignoreMouse = ignoreMouse;
		setIgnoreMouseButtons(ignoreMouse);
		setIgnoreMouseWheel(ignoreMouse);
		setIgnoreMouseFocus(ignoreMouse);
		setIgnoreTouchEvents(ignoreMouse);
	}

	/**
	 * Returns if the element is set to ingnore mouse events
	 * 
	 * @return boolean ignoreMouse
	 */
	public boolean getIgnoreMouse() {
		return this.ignoreMouse;
	}

	/**
	 * Element will ignore mouse left & right button events
	 * 
	 * @param ignoreMouseButtons
	 */
	public void setIgnoreMouseButtons(boolean ignoreMouseButtons) {
		setIgnoreMouseLeftButton(ignoreMouseButtons);
		setIgnoreMouseRightButton(ignoreMouseButtons);
	}

	/**
	 * Returns true if both left and right mouse buttons are being ignored
	 * 
	 * @return
	 */
	public boolean getIgnoreMouseButtons() {
		return (getIgnoreMouseLeftButton() && getIgnoreMouseRightButton());
	}

	/**
	 * Element will ignore mouse left button events
	 * 
	 * @param ignoreMouseLeftButton
	 */
	public void setIgnoreMouseLeftButton(boolean ignoreMouseLeftButton) {
		this.ignoreMouseLeftButton = ignoreMouseLeftButton;
	}

	/**
	 * Returns if the left mouse button is being ignored
	 * 
	 * @return
	 */
	public boolean getIgnoreMouseLeftButton() {
		return this.ignoreMouseLeftButton;
	}

	/**
	 * Element will ignore mouse right button events
	 * 
	 * @param ignoreMouseRightButton
	 */
	public void setIgnoreMouseRightButton(boolean ignoreMouseRightButton) {
		this.ignoreMouseRightButton = ignoreMouseRightButton;
	}

	/**
	 * Returns if the right mouse button is being ignored
	 * 
	 * @return
	 */
	public boolean getIgnoreMouseRightButton() {
		return this.ignoreMouseRightButton;
	}

	/**
	 * Element will ignore mouse focus
	 * 
	 * @param ignoreMouseFocus
	 */
	public void setIgnoreMouseFocus(boolean ignoreMouseFocus) {
		this.ignoreMouseFocus = ignoreMouseFocus;
	}

	/**
	 * Returns if the element ignores mouse focus
	 * 
	 * @return
	 */
	public boolean getIgnoreMouseFocus() {
		return this.ignoreMouseFocus;
	}

	/**
	 * Element will ignore mouse wheel click and move events
	 * 
	 * @param ignoreMouseWheel
	 */
	public void setIgnoreMouseWheel(boolean ignoreMouseWheel) {
		setIgnoreMouseWheelClick(ignoreMouseWheel);
		setIgnoreMouseWheelMove(ignoreMouseWheel);
	}

	/**
	 * Returns if the element is ignoring both mouse wheel click and move events
	 * 
	 * @return
	 */
	public boolean getIgnoreMouseWheel() {
		return (getIgnoreMouseWheelClick() && getIgnoreMouseWheelMove());
	}

	/**
	 * Element will ignore mouse wheel click events
	 * 
	 * @param ignoreMouseWheelClick
	 */
	public void setIgnoreMouseWheelClick(boolean ignoreMouseWheelClick) {
		this.ignoreMouseWheelClick = ignoreMouseWheelClick;
	}

	/**
	 * Returns if the element ignores mouse wheel clicks
	 * 
	 * @return
	 */
	public boolean getIgnoreMouseWheelClick() {
		return this.ignoreMouseWheelClick;
	}

	/**
	 * Element will ignore mouse wheel mouse events;
	 * 
	 * @param ignoreMouseWheelMove
	 */
	public void setIgnoreMouseWheelMove(boolean ignoreMouseWheelMove) {
		this.ignoreMouseWheelMove = ignoreMouseWheelMove;
	}

	/**
	 * Returns if the element is ignoring mouse wheel moves
	 * 
	 * @return
	 */
	public boolean getIgnoreMouseWheelMove() {
		return this.ignoreMouseWheelMove;
	}

	/**
	 * Element will ignore touch down up move and fling events
	 * 
	 * @param ignoreTouchEvents
	 */
	public void setIgnoreTouchEvents(boolean ignoreTouchEvents) {
		setIgnoreTouch(ignoreTouchEvents);
		setIgnoreTouchMove(ignoreTouchEvents);
		setIgnoreFling(ignoreTouchEvents);
	}

	/**
	 * Returns if the element ignores touch down, up, move & fling events
	 * 
	 * @return
	 */
	public boolean getIgnoreTouchEvents() {
		return (getIgnoreTouch() && getIgnoreTouchMove() && getIgnoreFling());
	}

	/**
	 * Element will ignore touch down and up events
	 * 
	 * @param ignoreTouch
	 */
	public void setIgnoreTouch(boolean ignoreTouch) {
		this.ignoreTouch = ignoreTouch;
	}

	/**
	 * Returns if the element ignores touch down and up events
	 * 
	 * @return
	 */
	public boolean getIgnoreTouch() {
		return ignoreTouch;
	}

	/**
	 * element will ignore touch move events
	 * 
	 * @param ignoreTouchMove
	 */
	public void setIgnoreTouchMove(boolean ignoreTouchMove) {
		this.ignoreTouchMove = ignoreTouchMove;
	}

	/**
	 * Returns if the element ignores touch move events
	 * 
	 * @return
	 */
	public boolean getIgnoreTouchMove() {
		return this.ignoreTouchMove;
	}

	/**
	 * Element will ignore touch fling events
	 * 
	 * @param ignoreFling
	 */
	public void setIgnoreFling(boolean ignoreFling) {
		this.ignoreFling = ignoreFling;
	}

	/**
	 * Returns if the element ignores fling events
	 * 
	 * @return
	 */
	public boolean getIgnoreFling() {
		return this.ignoreFling;
	}

	/**
	 * Enables draggable behavior for this element
	 * 
	 * @param isMovable
	 *            boolean
	 */
	public void setIsMovable(boolean isMovable) {
		this.isMovable = isMovable;
	}

	/**
	 * Returns if the element has draggable behavior set
	 * 
	 * @return boolean isMovable
	 */
	public boolean getIsMovable() {
		return this.isMovable;
	}

	/**
	 * Enables resize behavior for this element
	 * 
	 * @param isResizable
	 *            boolean
	 */
	public void setIsResizable(boolean isResizable) {
		this.isResizable = isResizable;
	}

	/**
	 * Returns if the element has resize behavior set
	 * 
	 * @return boolean isResizable
	 */
	public boolean getIsResizable() {
		return this.isResizable;
	}

	/**
	 * Enables/disables north border for resizing
	 * 
	 * @param resizeN
	 *            boolean
	 */
	public void setResizeN(boolean resizeN) {
		this.resizeS = resizeN;
	}

	/**
	 * Returns whether the elements north border has enabled/disabled resizing
	 * 
	 * @return boolean resizeN
	 */
	public boolean getResizeN() {
		return this.resizeS;
	}

	/**
	 * Enables/disables south border for resizing
	 * 
	 * @param resizeS
	 *            boolean
	 */
	public void setResizeS(boolean resizeS) {
		this.resizeN = resizeS;
	}

	/**
	 * Returns whether the elements south border has enabled/disabled resizing
	 * 
	 * @return boolean resizeS
	 */
	public boolean getResizeS() {
		return this.resizeN;
	}

	/**
	 * Enables/disables west border for resizing
	 * 
	 * @param resizeW
	 *            boolean
	 */
	public void setResizeW(boolean resizeW) {
		this.resizeW = resizeW;
	}

	/**
	 * Returns whether the elements west border has enabled/disabled resizing
	 * 
	 * @return boolean resizeW
	 */
	public boolean getResizeW() {
		return this.resizeW;
	}

	/**
	 * Enables/disables east border for resizing
	 * 
	 * @param resizeE
	 *            boolean
	 */
	public void setResizeE(boolean resizeE) {
		this.resizeE = resizeE;
	}

	/**
	 * Returns whether the elements east border has enabled/disabled resizing
	 * 
	 * @return boolean resizeE
	 */
	public boolean getResizeE() {
		return this.resizeE;
	}

	/**
	 * Sets how the element will docking to it's parent element during resize
	 * events. NW = Top Left of parent element NE = Top Right of parent element
	 * SW = Bottom Left of parent element SE = Bottom Right of parent element
	 * 
	 * @param docking
	 */
	public void setDocking(Docking docking) {
		this.docking = docking;
	}

	public Docking getDocking() {
		return this.docking;
	}

	/**
	 * Enables north docking of element (disables south docking of Element).
	 * This determines how the Element should retain positioning on parent
	 * resize events.
	 * 
	 * @param dockN
	 *            boolean
	 */
	@Deprecated
	public void setDockN(boolean dockN) {
		this.dockS = dockN;
		this.dockN = !dockN;
		Docking d = null;
		if (dockS) {
			if (dockE)
				d = Docking.NE;
			else
				d = Docking.NW;
		} else {
			if (dockE)
				d = Docking.SE;
			else
				d = Docking.SW;
		}
		setDocking(d);
	}

	/**
	 * Returns if the Element is docked to the north quadrant of it's parent
	 * element.
	 * 
	 * @return boolean dockN
	 */
	@Deprecated
	public boolean getDockN() {
		return this.dockS;
	}

	/**
	 * Enables west docking of Element (disables east docking of Element). This
	 * determines how the Element should retain positioning on parent resize
	 * events.
	 * 
	 * @param dockW
	 *            boolean
	 */
	@Deprecated
	public void setDockW(boolean dockW) {
		this.dockW = dockW;
		this.dockE = !dockW;
		Docking d = null;
		if (dockE) {
			if (dockS)
				d = Docking.NE;
			else
				d = Docking.SE;
		} else {
			if (dockS)
				d = Docking.NW;
			else
				d = Docking.SW;
		}
		setDocking(d);
	}

	/**
	 * Returns if the Element is docked to the west quadrant of it's parent
	 * element.
	 * 
	 * @return boolean dockW
	 */
	@Deprecated
	public boolean getDockW() {
		return this.dockW;
	}

	/**
	 * Enables east docking of Element (disables west docking of Element). This
	 * determines how the Element should retain positioning on parent resize
	 * events.
	 * 
	 * @param dockE
	 *            boolean
	 */
	@Deprecated
	public void setDockE(boolean dockE) {
		this.dockE = dockE;
		this.dockW = !dockE;
		Docking d = null;
		if (dockE) {
			if (dockS)
				d = Docking.NE;
			else
				d = Docking.SE;
		} else {
			if (dockS)
				d = Docking.NW;
			else
				d = Docking.SW;
		}
		setDocking(d);
	}

	/**
	 * Returns if the Element is docked to the east quadrant of it's parent
	 * element.
	 * 
	 * @return boolean dockE
	 */
	@Deprecated
	public boolean getDockE() {
		return this.dockE;
	}

	/**
	 * Enables south docking of Element (disables north docking of Element).
	 * This determines how the Element should retain positioning on parent
	 * resize events.
	 * 
	 * @param dockS
	 *            boolean
	 */
	@Deprecated
	public void setDockS(boolean dockS) {
		this.dockN = dockS;
		this.dockS = !dockS;
		Docking d = null;
		if (dockS) {
			if (dockE)
				d = Docking.NE;
			else
				d = Docking.NW;
		} else {
			if (dockE)
				d = Docking.SE;
			else
				d = Docking.SW;
		}
		setDocking(d);
	}

	/**
	 * Returns if the Element is docked to the south quadrant of it's parent
	 * element.
	 * 
	 * @return boolean dockS
	 */
	@Deprecated
	public boolean getDockS() {
		return this.dockN;
	}

	/**
	 * Determines if the element should scale with parent when resized
	 * vertically.
	 * 
	 * @param scaleNS
	 *            boolean
	 */
	public void setScaleNS(boolean scaleNS) {
		this.scaleNS = scaleNS;
	}

	/**
	 * Returns if the Element is set to scale vertically when it's parent
	 * Element is resized.
	 * 
	 * @return boolean scaleNS
	 */
	public boolean getScaleNS() {
		return this.scaleNS;
	}

	/**
	 * Determines if the element should scale with parent when resized
	 * horizontally.
	 * 
	 * @param scaleEW
	 *            boolean
	 */
	public void setScaleEW(boolean scaleEW) {
		this.scaleEW = scaleEW;
	}

	/**
	 * Returns if the Element is set to scale horizontally when it's parent
	 * Element is resized.
	 * 
	 * @return boolean scaleEW
	 */
	public boolean getScaleEW() {
		return this.scaleEW;
	}

	/**
	 * Sets the element to pass certain events (movement, resizing) to it direct
	 * parent instead of effecting itself.
	 * 
	 * @param effectParent
	 *            boolean
	 */
	public void setEffectParent(boolean effectParent) {
		this.effectParent = effectParent;
	}

	/**
	 * Returns if the Element is set to pass events to it's direct parent
	 * 
	 * @return boolean effectParent
	 */
	public boolean getEffectParent() {
		return this.effectParent;
	}

	/**
	 * Sets the element to pass certain events (movement, resizing) to it
	 * absolute parent instead of effecting itself.
	 * 
	 * The Elements absolute parent is the element farthest up in it's nesting
	 * order, or simply put, was added to the screen.
	 * 
	 * @param effectAbsoluteParent
	 *            boolean
	 */
	public void setEffectAbsoluteParent(boolean effectAbsoluteParent) {
		this.effectAbsoluteParent = effectAbsoluteParent;
	}

	/**
	 * Returns if the Element is set to pass events to it's absolute parent
	 * 
	 * @return boolean effectParent
	 */
	public boolean getEffectAbsoluteParent() {
		return this.effectAbsoluteParent;
	}

	/**
	 * Forces the object to stay within the constraints of it's parent Elements
	 * dimensions. NOTE: use setLockToParentBounds instead.
	 * 
	 * @param lockToParentBounds
	 *            boolean
	 */
	@Deprecated
	public void setlockToParentBounds(boolean lockToParentBounds) {
		this.lockToParentBounds = lockToParentBounds;
	}

	/**
	 * Forces the object to stay within the constraints of it's parent Elements
	 * dimensions.
	 * 
	 * @param lockToParentBounds
	 *            boolean
	 */
	public void setLockToParentBounds(boolean lockToParentBounds) {
		this.lockToParentBounds = lockToParentBounds;
	}

	/**
	 * Returns if the Element has been constrained to it's parent Element's
	 * dimensions.
	 * 
	 * @return boolean lockToParentBounds
	 */
	public boolean getLockToParentBounds() {
		return this.lockToParentBounds;
	}

	public void setGlobalUIScale(float widthPercent, float heightPercent) {
		for (Element el : elementChildren.values()) {
			el.setPosition(el.getPosition().x * widthPercent, el.getPosition().y * heightPercent);
			el.setDimensions(el.getDimensions().x * widthPercent, el.getDimensions().y * heightPercent);
			el.setFontSize(el.getFontSize() * heightPercent);
			el.setGlobalUIScale(widthPercent, heightPercent);
		}
	}

	/**
	 * Allows for dynamically enabling/disabling the element
	 * 
	 * @param isEnabled
	 *            boolean
	 */
	public void setIsEnabled(boolean isEnabled) {
		if (this.isEnabled != isEnabled) {
			this.isEnabled = isEnabled;
			updateGlobalAlpha();
			controlIsEnabledHook(isEnabled);
			for (Element el : elementChildren.values()) {
				el.setIsEnabled(isEnabled);
			}
		}
	}

	public void controlIsEnabledHook(boolean isEnabled) {
	}

	/**
	 * Returns if the element is currently enabled
	 * 
	 * @return boolean
	 */
	public boolean getIsEnabled() {
		return this.isEnabled;
	}

	// </editor-fold>

	// <editor-fold desc="Drag & Drop Support">
	/**
	 * Flags Element as Drag Element for Drag & Drop interaction
	 * 
	 * @param isDragElement
	 *            boolean
	 */
	public void setIsDragDropDragElement(boolean isDragElement) {
		this.isDragElement = isDragElement;
		if (isDragElement)
			this.isDropElement = false;
	}

	/**
	 * Returns if the Element is currently flagged as a Drag Element for Drag &
	 * Drop interaction
	 * 
	 * @return boolean
	 */
	public boolean getIsDragDropDragElement() {
		return this.isDragElement;
	}

	/**
	 * Flags Element as Drop Element for Drag & Drop interaction
	 * 
	 * @param isDropElement
	 *            boolean
	 */
	public void setIsDragDropDropElement(boolean isDropElement) {
		this.isDropElement = isDropElement;
		if (isDropElement)
			this.isDragElement = false;
	}

	/**
	 * Returns if the Element is currently flagged as a Drop Element for Drag &
	 * Drop interaction
	 * 
	 * @return boolean
	 */
	public boolean getIsDragDropDropElement() {
		return this.isDropElement;
	}

	// </editor-fold>

	// <editor-fold desc="Sizing & Positioning">
	/**
	 * Set the x,y coordinates of the Element. X and y are relative to the
	 * parent Element.
	 * 
	 * @param position
	 *            Vector2f screen poisition of Element
	 */
	public void setPosition(Vector2f position) {
		this.containerPosition.set(position);
		this.position.set(position);
		updateNodeLocation();
	}

	protected void setActualPosition(Vector2f position) {
		this.position.x = position.x;
		setActualY(position.y);
	}

	/**
	 * Set the x,y coordinates of the Element. X and y are relative to the
	 * parent Element.
	 * 
	 * @param x
	 *            The x coordinate screen poisition of Element
	 * @param y
	 *            The y coordinate screen poisition of Element
	 */
	public void setPosition(float x, float y) {
		this.containerPosition.set(x, y);
		this.position.set(x, y);
		updateNodeLocation();
	}

	protected void setActualPosition(float x, float y) {
		this.position.setX(x);
		setActualY(y);
	}

	/**
	 * Set the x coordinates of the Element. X is relative to the parent
	 * Element.
	 * 
	 * @param x
	 *            The x coordinate screen poisition of Element
	 */
	public void setX(float x) {
		this.containerPosition.x = x;
		this.position.x = x;
		updateNodeLocation();
	}

	protected void setActualX(float x) {
		this.position.x = x;
		updateNodeLocation();
	}

	/**
	 * Set the y coordinates of the Element. Y is relative to the parent
	 * Element.
	 * 
	 * @param y
	 *            The y coordinate screen poisition of Element
	 */
	public void setY(float y) {
		this.containerPosition.y = y;
		this.position.y = y;
		updateNodeLocation();
	}

	// TODO hack for checkbox
	protected void setActualY(float y) {

		if (elementParent != null) {
			if (!Element.NEW_YFLIPPING && initialized) {
				this.position.y = elementParent.getHeight() - y - getHeight();
			} else {
				this.position.y = y;
			}
		} else {
			if (!Element.NEW_YFLIPPING && initialized) {
				this.position.y = screen.getHeight() - y - getHeight();
			} else {
				this.position.y = y;
			}
		}

		this.position.setY(y);
		updateNodeLocation();
	}

	public void updateNodeLocation() {
		setNodeLocation();
		// updateClipping();
		updateClippingLayers();
	}

	protected void setNodeLocation() {
		if (NEW_YFLIPPING) {
			if (elementParent == null) {
				// System.out.println(String.format("UNL: %f,%f,%f", position.x,
				// screen.getHeight() - position.y - dimensions.y,
				// this.getLocalTranslation().getZ()));
				this.setLocalTranslation(position.x, screen.getHeight() - position.y - dimensions.y,
						this.getLocalTranslation().getZ());
			} else
				this.setLocalTranslation(position.x, elementParent.dimensions.y - position.y - dimensions.y,
						this.getLocalTranslation().getZ());
		} else {
			this.setLocalTranslation(position.x, position.y, this.getLocalTranslation().getZ());
		}
	}

	/**
	 * Returns the current screen location of the Element
	 * 
	 * @return Vector2f position
	 */
	public Vector2f getPosition() {
		return position;
	}

	/**
	 * Gets the relative x coordinate of the Element from it's parent Element's
	 * x
	 * 
	 * @return float
	 */
	public float getX() {
		return position.x;
	}

	/**
	 * Gets the relative y coordinate of the Element from it's parent Element's
	 * y
	 * 
	 * @return float
	 */
	public float getY() {
		return position.y;
	}

	public Vector2f getAbsolute() {
		return new Vector2f(getAbsoluteX(), getAbsoluteY());
	}

	/**
	 * Returns the x coord of an element from screen x 0, ignoring the nesting
	 * order.
	 * 
	 * @return float x
	 */
	public float getAbsoluteX() {
		float x = getX();
		Element el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			x += el.getX();
		}
		return x;
	}

	/**
	 * Returns the y coord of an element from screen y 0, ignoring the nesting
	 * order.
	 * 
	 * @return float
	 */
	public float getAbsoluteY() {
		float y = getY();
		Element el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			y += el.getY();
		}
		return y;
	}

	/**
	 * Sets the width and height of the element
	 * 
	 * @param w
	 *            float
	 * @param h
	 *            float
	 */
	public void setDimensions(float w, float h) {
		this.getContainerDimensions().x = w;
		this.getContainerDimensions().y = h;
		setActualDimensions(w, h);
	}

	protected final void setActualDimensions(float w, float h) {
		this.dimensions.setX(w);
		this.dimensions.setY(h);

		if (model != null) {
			model.updateDimensions(dimensions.x, dimensions.y);
			if (tileImage) {
				float tcW = dimensions.x / getModel().getImageWidth();
				float tcH = dimensions.y / getModel().getImageHeight();
				model.updateTiledTexCoords(0, -tcH, tcW, 0);
			}
			geom.updateModelBound();
		}

		if (textElement != null) {
			updateTextElement();
		}
		updateNodeLocation();
		// updateClipping();
		updateClippingLayers();
	}

	/**
	 * Sets the width and height of the element
	 * 
	 * @param dimensions
	 *            Vector2f
	 */
	public void setDimensions(Vector2f dimensions) {
		this.getContainerDimensions().set(dimensions);
		setActualDimensions(dimensions);
	}

	protected final void setActualDimensions(Vector2f dimensions) {
		this.dimensions.set(dimensions);
		getModel().updateDimensions(dimensions.x, dimensions.y);
		if (tileImage) {
			float tcW = dimensions.x / getModel().getImageWidth();
			float tcH = dimensions.y / getModel().getImageHeight();
			getModel().updateTiledTexCoords(0, -tcH, tcW, 0);
		}
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		// updateClipping();
		updateClippingLayers();
	}

	/**
	 * Stubbed for future use. This should limit resizing to the minimum
	 * dimensions defined
	 * 
	 * @param minDimensions
	 *            The absolute minimum dimensions for this Element.
	 */
	public Element setMinDimensions(Vector2f minDimensions) {
		if (this.minDimensions == null && minDimensions != null)
			this.minDimensions = new Vector2f();
		if (minDimensions != null) {
			this.minDimensions.set(minDimensions);
		} else
			this.minDimensions = null;
		dirtyLayout(false);
		return this;
	}

	public Vector2f getMinDimensions() {
		return this.minDimensions;
	}

	/**
	 * Sets the width of the element
	 * 
	 * @param width
	 *            float
	 */
	public void setWidth(float width) {
		this.getContainerDimensions().x = width;
		setActualWidth(width);
	}

	protected void setActualWidth(float width) {
		this.dimensions.setX(width);
		getModel().updateWidth(dimensions.x);
		if (tileImage) {
			float tcW = dimensions.x / getModel().getImageWidth();
			float tcH = dimensions.y / getModel().getImageHeight();
			getModel().updateTiledTexCoords(0, -tcH, tcW, 0);
		}
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		// updateClipping();
		updateClippingLayers();
	}

	/**
	 * Sets the height of the element
	 * 
	 * @param height
	 *            float
	 */
	public void setHeight(float height) {
		this.getContainerDimensions().y = height;
		setActualHeight(height);
	}

	protected void setActualHeight(float height) {
		this.dimensions.setY(height);
		getModel().updateHeight(dimensions.y);
		if (tileImage) {
			float tcW = dimensions.x / getModel().getImageWidth();
			float tcH = dimensions.y / getModel().getImageHeight();
			getModel().updateTiledTexCoords(0, -tcH, tcW, 0);
		}
		geom.updateModelBound();
		if (textElement != null) {
			updateTextElement();
		}
		updateNodeLocation();
		// updateClipping();
		updateClippingLayers();
	}

	/**
	 * Returns a Vector2f containing the actual width and height of an Element
	 * 
	 * @return float
	 */
	public Vector2f getDimensions() {
		return dimensions;
	}

	/**
	 * Returns the dimensions defined at the time of the Element's creation.
	 * 
	 * @return
	 */
	public Vector2f getOrgDimensions() {
		return this.orgDimensions;
	}

	/**
	 * Returns the actual width of an Element
	 * 
	 * @return float
	 */
	public float getWidth() {
		return dimensions.x;
	}

	/**
	 * Returns the actual height of an Element
	 * 
	 * @return float
	 */
	public float getHeight() {
		return dimensions.y;
	}

	/**
	 * Returns the width of an Element from screen x 0
	 * 
	 * @return float
	 */
	public float getAbsoluteWidth() {
		return getAbsoluteX() + getWidth();
	}

	/**
	 * Returns the height of an Element from screen y 0
	 * 
	 * @return float
	 */
	public float getAbsoluteHeight() {
		return getAbsoluteY() + getHeight();
	}

	// </editor-fold>

	/**
	 * Stubbed for future use.
	 */
	public void validateLayout() {
		if (getDimensions().x < 1 || getDimensions().y < 1) {
			Vector2f dim = getV2fPercentToPixels(getDimensions());
			resize(getAbsoluteX() + dim.x, getAbsoluteY() + dim.y, Element.Borders.SE);
		}
		if (getPosition().x < 1 || getPosition().y < 1) {
			Vector2f pos = getV2fPercentToPixels(getPosition());
			setPosition(pos.x, pos.y);
		}
		if (getElementParent() != null)
			setY(getElementParent().getHeight() - getHeight() - getY());
		else
			setY(screen.getHeight() - getHeight() - getY());

		for (Element el : elementChildren.values()) {
			el.validateLayout();
		}
	}

	/**
	 * Stubbed for future use.
	 * 
	 * @param screen
	 *            TODO
	 */
	public final void setInitialized(ElementManager screen) {
		if (initialized)
			throw new IllegalStateException("Already initialized.");

		if (this.screen == null && screen != null) {
			// New style init
			this.screen = screen;
		}

		this.initialized = true;
		// if (!screen.getStylesheets().isEmpty() || stylesheets != null)
		// applyCss();

		// Add the parents clipping layers
		if (elementParent != null) {
			for (ClippingDefine def : elementParent.clippingLayers) {
				if (def.getElement() != null) {
					addClippingLayer(def.getElement());
				}
			}
		}

		layoutChildren();

		for (Element e : childList) {
			if (!e.getInitialized()) {
				e.setInitialized(screen);
			}
		}
		onInitialized();
	}

	protected void onInitialized() {
		// For sub-classes to override
	}

	protected PseudoStyle[] getPseudoStyles() {
		return null;
	}

	protected void applyCss() {

		// Apply child CSS first
		for (Element e : childList) {
			e.applyCss();
		}

		List<Stylesheet> l = new ArrayList<Stylesheet>(
				screen.getStylesheets().size() + (stylesheets == null ? 0 : stylesheets.size()));
		l.addAll(screen.getStylesheets());
		if (stylesheets != null) {
			l.addAll(stylesheets);
		}

		// TODO share user agent with CSS loader
		// TODO share CSS factory with CSS loader
		CssProcessor processor = new CssProcessor();
		Matcher matcher = new Matcher(processor, processor, new StylesheetFactoryImpl(new TGGUserAgent(screen)), l,
				"screen");
		CascadedStyle style = matcher.getCascadedStyle(this, true);
		applyStyle(null, style);
		PseudoStyle[] ps = getPseudoStyles();
		if (ps != null) {
			for (PseudoStyle p : ps) {
				processor.setPseudoStyles(p);
				style = matcher.getCascadedStyle(this, true);
				applyStyle(p, style);
			}
		}
	}

	protected void applyStyle(PseudoStyle p, CascadedStyle style) {
		if (style != null) {
			for (Iterator<?> it = style.getCascadedPropertyDeclarations(); it.hasNext();) {
				PropertyDeclaration decl = (PropertyDeclaration) it.next();
				System.out.println("[REMOVEME] Got " + p + " style for " + toString() + "! " + style + " = " + decl
						+ " / " + decl.getClass().getName() + " : " + decl.getValue().getCssText());
				applyCss(p, decl);
			}
		}
	}

	protected void applyCss(PseudoStyle ps, PropertyDeclaration decl) {
		if (ps == null) {
			String n = decl.getPropertyName();
			CSSPrimitiveValue v = decl.getValue();
			if (n.equals("top")) {
				setX(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("left")) {
				setY(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("right")) {
				setWidth(getWidth() + v.getFloatValue(CSSPrimitiveValue.CSS_PX) - getX());
			} else if (n.equals("bottom")) {
				setHeight(getHeight() + v.getFloatValue(CSSPrimitiveValue.CSS_PX) - getY());
			} else if (n.equals("min-width")) {
				if (minDimensions == null)
					minDimensions = new Vector2f();
				minDimensions.setX(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("min-height")) {
				if (minDimensions == null)
					minDimensions = new Vector2f();
				minDimensions.setY(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("max-width")) {
				if (maxDimensions == null)
					maxDimensions = new Vector2f();
				maxDimensions.setX(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("max-height")) {
				if (maxDimensions == null)
					maxDimensions = new Vector2f();
				maxDimensions.setY(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("width")) {
				if (prefDimensions == null)
					prefDimensions = new Vector2f();
				prefDimensions.setX(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
				if (elementParent == null || elementParent.layoutManager == null)
					dimensions.setX(v.getFloatValue(CSSPrimitiveValue.CSS_PX));

			} else if (n.equals("height")) {
				if (prefDimensions == null)
					prefDimensions = new Vector2f();
				prefDimensions.setY(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
				if (elementParent == null || elementParent.layoutManager == null)
					dimensions.setY(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("padding-left")) {
				textPadding.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("padding-right")) {
				textPadding.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("padding-top")) {
				textPadding.z = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("padding-bottom")) {
				textPadding.w = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("border-left-width")) {
				borders.y = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("border-right-width")) {
				borders.z = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("border-top-width")) {
				borders.x = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("border-bottom-width")) {
				borders.w = v.getFloatValue(CSSPrimitiveValue.CSS_PX);
			} else if (n.equals("background-image")) {
				String stringValue = v.getStringValue();
				setTexture(stringValue);
			} else if (n.equals("background-color")) {
				getElementMaterial().setColor("Color", CssUtil.toColor(v.getCssText()));
			} else if (n.equals("background-repeat")) {
				setTileImage("repeat".equals(v.getStringValue()));
			} else if (n.equals("text-align")) {
				setTextAlign(CssUtil.toAlign(v.getStringValue()));
			} else if (n.equals("vertical-align")) {
				setTextVAlign(CssUtil.toVAlign(v.getStringValue()));
			} else if (n.equals("color")) {
				// TODO colours dont seem to work properly in FS... am a bit
				// confused
				setFontColor(CssUtil.toColor(v.getCssText()));
			} else if (n.equals("font-size")) {
				setFontSize(v.getFloatValue(CSSPrimitiveValue.CSS_PX));
			} else if (n.equals("font-family")) {
				setFont(screen.getStyle("Font").getString(v.getStringValue()));
			} else if (n.equals("white-space")) {
				if (v.getStringValue().equals("none")) {
					setTextWrap(LineWrapMode.NoWrap);
				} else if (v.getStringValue().equals("normal")) {
					setTextWrap(LineWrapMode.Word);
				} else if (v.getStringValue().equals("pre")) {
					setTextWrap(LineWrapMode.Character);
				} else if (v.getStringValue().equals("clip")) {
					setTextWrap(LineWrapMode.Clip);
				}
			}
		}
	}

	/**
	 * Stubbed for future use.
	 */
	public boolean getInitialized() {
		return this.initialized;
	}

	// <editor-fold desc="Resize & Move">
	/**
	 * The preferred method for resizing Elements if the resize must effect
	 * nested Elements as well.
	 * 
	 * @param x
	 *            the absolute x coordinate from screen x 0
	 * @param y
	 *            the absolute y coordinate from screen y 0
	 * @param dir
	 *            The Element.Borders used to determine the direction of the
	 *            resize event
	 */
	public void resize(float x, float y, Borders dir) {
		float prevWidth = getWidth();
		float prevHeight = getHeight();
		float oX = x, oY = y;
		if (getElementParent() != null) {
			x -= getAbsoluteX() - getX();
		}
		if (getElementParent() != null) {
			y -= getAbsoluteY() - getY();
		}
		float nextX, nextY;
		Vector2f minDimensions = LUtil.getContainerMinimumDimensions(this);
		Vector2f maxDimensions = LUtil.getContainerMaximumDimensions(this);
		System.err.println("minDimensions(" + minDimensions + ", " + maxDimensions + " dir: " + dir + " x:" + y + " y:"
				+ y + " pos: " + getPosition() + " dim: " + getDimensions());
		if (dir == Borders.NW) {
			if (getLockToParentBounds()) {
				if (x <= 0) {
					x = 0;
				}
			}
			if (minDimensions != null) {
				if (getX() + getWidth() - x <= minDimensions.x) {
					x = getX() + getWidth() - minDimensions.x;
				}
			}
			if (maxDimensions != null) {
				if (getX() + getWidth() - x >= maxDimensions.x) {
					x = getX() + getWidth() - maxDimensions.x;
				}
			}

			if (resizeW) {
				setWidth(getX() + getWidth() - x);
				setX(x);
			}
			if (getLockToParentBounds()) {
				if (y <= 0) {
					y = 0;
				}
			}
			if (minDimensions != null) {
				if (getY() + getHeight() - y <= minDimensions.y) {
					y = getY() + getHeight() - minDimensions.y;
				}
			}

			if (maxDimensions != null) {
				if (getY() + getHeight() - y >= maxDimensions.y) {
					y = getY() + getHeight() - maxDimensions.y;
				}
			}

			if (resizeN) {
				setHeight(getY() + getHeight() - y);
				setY(y);
			}
		} else if (dir == Borders.N) {
			if (getLockToParentBounds()) {
				if (y <= 0) {
					y = 0;
				}
			}
			if (minDimensions != null) {
				if (getY() + getHeight() - y <= minDimensions.y) {
					y = getY() + getHeight() - minDimensions.y;
				}
			}
			if (maxDimensions != null) {
				if (getY() + getHeight() - y >= maxDimensions.y) {
					y = getY() + getHeight() - maxDimensions.y;
				}
			}
			if (resizeN) {
				setHeight(getY() + getHeight() - y);
				setY(y);
			}
		} else if (dir == Borders.NE) {
			nextX = oX - getAbsoluteX();
			if (getLockToParentBounds()) {
				float checkWidth = (getElementParent() == null) ? screen.getWidth() : getElementParent().getWidth();
				if (nextX >= checkWidth - getX()) {
					nextX = checkWidth - getX();
				}
			}
			if (minDimensions != null) {
				if (nextX <= minDimensions.x) {
					nextX = minDimensions.x;
				}
			}
			if (maxDimensions != null) {
				if (nextX >= maxDimensions.x) {
					nextX = maxDimensions.x;
				}
			}
			if (resizeE) {
				setWidth(nextX);
			}
			if (getLockToParentBounds()) {
				if (y <= 0) {
					y = 0;
				}
			}
			if (minDimensions != null) {
				if (getY() + getHeight() - y <= minDimensions.y) {
					y = getY() + getHeight() - minDimensions.y;
				}
			}
			if (resizeN) {
				setHeight(getY() + getHeight() - y);
				setY(y);
			}
		} else if (dir == Borders.W) {
			if (getLockToParentBounds()) {
				if (x <= 0) {
					x = 0;
				}
			}
			if (minDimensions != null) {
				if (getX() + getWidth() - x <= minDimensions.x) {
					x = getX() + getWidth() - minDimensions.x;
				}
			}

			if (maxDimensions != null) {
				if (getX() + getWidth() - x >= maxDimensions.x) {
					x = getX() + getWidth() - maxDimensions.x;
				}
			}
			if (resizeW) {
				setWidth(getX() + getWidth() - x);
				setX(x);
			}
		} else if (dir == Borders.E) {
			nextX = oX - getAbsoluteX();
			if (getLockToParentBounds()) {
				float checkWidth = (getElementParent() == null) ? screen.getWidth() : getElementParent().getWidth();
				if (nextX >= checkWidth - getX()) {
					nextX = checkWidth - getX();
				}
			}
			if (minDimensions != null) {
				if (nextX <= minDimensions.x) {
					nextX = minDimensions.x;
				}
			}
			if (maxDimensions != null) {
				if (nextX >= maxDimensions.x) {
					nextX = maxDimensions.x;
				}
			}
			if (resizeE) {
				setWidth(nextX);
			}
		} else if (dir == Borders.SW) {
			if (getLockToParentBounds()) {
				if (x <= 0) {
					x = 0;
				}
			}
			if (minDimensions != null) {
				if (getX() + getWidth() - x <= minDimensions.x) {
					x = getX() + getWidth() - minDimensions.x;
				}
			}
			if (maxDimensions != null) {
				if (getX() + getWidth() - x >= maxDimensions.x) {
					x = getX() + getWidth() - maxDimensions.x;
				}
			}
			// if (resizeW) {
			setWidth(getX() + getWidth() - x);
			setX(x);
			// }
			nextY = oY - getAbsoluteY();
			if (getLockToParentBounds()) {
				float checkHeight = (getElementParent() == null) ? screen.getHeight() : getElementParent().getHeight();
				if (nextY >= checkHeight - getY()) {
					nextY = checkHeight - getY();
				}
			}
			if (minDimensions != null) {
				if (nextY <= minDimensions.y) {
					nextY = minDimensions.y;
				}
			}
			if (maxDimensions != null) {
				if (nextY >= maxDimensions.y) {
					nextY = maxDimensions.y;
				}
			}
			if (resizeS) {
				setHeight(nextY);
			}
		} else if (dir == Borders.S) {
			if (NEW_YFLIPPING) {

				nextY = oY - getAbsoluteY();
				if (getLockToParentBounds()) {
					float checkHeight = (getElementParent() == null) ? screen.getHeight()
							: getElementParent().getHeight();
					if (nextY >= checkHeight - getY()) {
						nextY = checkHeight - getY();
					}
				}
				if (minDimensions != null) {
					if (nextY <= minDimensions.y) {
						nextY = minDimensions.y;
					}
				}
				if (maxDimensions != null) {
					if (nextY >= maxDimensions.y) {
						nextY = maxDimensions.y;
					}
				}
				if (resizeS) {
					// setY(getY() - (prevHeight - nextY));
					setHeight(nextY);
				}
			} else {
				nextY = oY - getAbsoluteY();
				if (getLockToParentBounds()) {
					float checkHeight = (getElementParent() == null) ? screen.getHeight()
							: getElementParent().getHeight();
					if (nextY >= checkHeight - getY()) {
						nextY = checkHeight - getY();
					}
				}
				if (minDimensions != null) {
					if (nextY <= minDimensions.y) {
						nextY = minDimensions.y;
					}
				}
				if (maxDimensions != null) {
					if (nextY >= maxDimensions.y) {
						nextY = maxDimensions.y;
					}
				}
				if (resizeS) {
					setHeight(nextY);
				}
			}
		} else if (dir == Borders.SE) {
			nextX = oX - getAbsoluteX();
			if (getLockToParentBounds()) {
				float checkWidth = (getElementParent() == null) ? screen.getWidth() : getElementParent().getWidth();
				if (nextX >= checkWidth - getX()) {
					nextX = checkWidth - getX();
				}
			}
			if (minDimensions != null) {
				if (nextX <= minDimensions.x) {
					nextX = minDimensions.x;
				}
			}
			if (maxDimensions != null) {
				if (nextX >= maxDimensions.x) {
					nextX = maxDimensions.x;
				}
			}
			if (resizeE) {
				setWidth(nextX);
			}
			nextY = oY - getAbsoluteY();
			if (getLockToParentBounds()) {
				float checkHeight = (getElementParent() == null) ? screen.getHeight() : getElementParent().getHeight();
				if (nextY >= checkHeight - getY()) {
					nextY = checkHeight - getY();
				}
			}
			if (minDimensions != null) {
				if (nextY <= minDimensions.y) {
					nextY = minDimensions.y;
				}
			}
			if (maxDimensions != null) {
				if (nextY >= maxDimensions.y) {
					nextY = maxDimensions.y;
				}
			}
			if (resizeS) {
				setHeight(nextY);
			}
		}
		float diffX = prevWidth - getWidth();
		float diffY = prevHeight - getHeight();
		if (diffX != 0 || diffY != 0) {
			dirtyLayout(false);
			layoutChildren();
			controlResizeHook();
			for (Element el : elementChildren.values()) {
				el.childResize(diffX, diffY, dir);
				el.controlResizeHook();
			}
		}
	}

	public void dirtyLayout(boolean doChildren) {
		if (!layoutDirty) {
			layoutDirty = true;

			// Normally each child should dirty it's own layout when it actually
			// changes (e.g. by parent layout manager)
			if (doChildren) {
				// System.err.println("NOTE: A component requests dirtying of
				// children. This is inefficient. The component should be fixed
				// to use LUtil methods to change bounds so layout is
				// automatically changed, or other mechanisms.");
				for (Element e : elementChildren.values())
					e.dirtyLayout(doChildren);
			}
		}
	}

	// TODO: enforce minimum size
	private void childResize(float diffX, float diffY, Borders dir) {
		// boolean minSize = !(minDimensions == null);
		// if (dir == Borders.NW || dir == Borders.N || dir == Borders.NE) {
		// if (getScaleNS())
		// setHeight(getHeight() - diffY);
		// if ((getDocking() == Docking.NW || getDocking() == Docking.NE) &&
		// !getScaleNS())
		// setY(getY() - diffY);
		// } else if (dir == Borders.SW || dir == Borders.S || dir ==
		// Borders.SE) {
		// if (getScaleNS())
		// setHeight(getHeight() - diffY);
		// if ((getDocking() == Docking.NW || getDocking() == Docking.NE) &&
		// !getScaleNS())
		// setY(getY() - diffY);
		// }
		// if (dir == Borders.NW || dir == Borders.W || dir == Borders.SW) {
		// if (getScaleEW())
		// setWidth(getWidth() - diffX);
		// if ((getDocking() == Docking.NE || getDocking() == Docking.SE) &&
		// !getScaleEW())
		// setX(getX() - diffX);
		// } else if (dir == Borders.NE || dir == Borders.E || dir ==
		// Borders.SE) {
		// if (getScaleEW())
		// setWidth(getWidth() - diffX);
		// if ((getDocking() == Docking.NE || getDocking() == Docking.SE) &&
		// !getScaleEW())
		// setX(getX() - diffX);
		// }
		for (Element el : elementChildren.values()) {
			el.childResize(diffX, diffY, dir);
			el.controlResizeHook();
		}
	}

	/**
	 * Overridable method for extending the resize event
	 */
	public void controlResizeHook() {

	}

	public Element sizeToContent() {
		if (layoutManager != null) {
			final Vector2f newWindowSize = LUtil.getBoundPreferredSize(this);
			LUtil.setDimensions(this, newWindowSize);
			checkBounds();
			if (getLockToParentBounds()) {
				lockToParentBounds(getX(), getY());
			}
			dirtyLayout(false);
			layoutChildren();

			// TODO get rid of these
			controlResizeHook();

			// Work out the change
			// Vector2f current = getDimensions();
			// Vector2f dif = new Vector2f(current.x - newWindowSize.x,
			// current.y - newWindowSize.y);
			// if (initialized) {
			// if (dif.y > 0) {
			// setY(getY() - dif.y);
			// } else {
			// setY(getY() + dif.y);
			// }
			// } else {
			// if (dif.y > 0) {
			// setY(getY() - dif.y);
			// } else {
			// setY(getY() + dif.y);
			// }
			// }
			// setDimensions(newWindowSize);
			// controlResizeHook();
			// dirtyLayout();
			// layoutChildren();
			// checkBounds();
		} else {
			float innerX = 10000, innerY = 10000, innerW = -10000, innerH = -10000;
			float currentHeight = getHeight();
			Map<Element, Float> newY = new HashMap<>();
			for (Element child : elementChildren.values()) {
				float x = child.getX();
				float y = currentHeight - (child.getY() + child.getHeight());
				float w = child.getX() + child.getWidth();
				float h = currentHeight - child.getY();
				if (x < innerX)
					innerX = x;
				if (y < innerY)
					innerY = y;
				if (w > innerW)
					innerW = w;
				if (h > innerH)
					innerH = h;
				newY.put(child, h);
			}
			this.setDimensions(innerW + innerX, innerH + innerY);
			for (Element child : elementChildren.values()) {
				float diff = newY.get(child);
				child.setY(innerH - (diff - innerY));
			}
			lockToParentBounds(getX(), getY());
		}
		return this;
	}

	protected void checkBounds() {
		if (isKeepWithinScreenBounds) {
			if (getX() < 0) {
				setX(0);
			} else if (getX() + getWidth() > screen.getWidth()) {
				setX(screen.getWidth() - getWidth());
			}
			if (getY() < 0) {
				setY(0);
			} else if (getY() + getHeight() > screen.getHeight()) {
				setY(screen.getHeight() - getHeight());
			}
		}
	}

	public boolean isKeepWithinScreenBounds() {
		return isKeepWithinScreenBounds;
	}

	public void setKeepWithinScreenBounds(boolean isKeepWithinScreenBounds) {
		this.isKeepWithinScreenBounds = isKeepWithinScreenBounds;
		if (isKeepWithinScreenBounds)
			checkBounds();
	}

	/**
	 * Moves the Element to the specified coordinates
	 * 
	 * @param x
	 *            The new x screen coordinate of the Element
	 * @param y
	 *            The new y screen coordinate of the Element
	 */
	public void moveTo(float x, float y) {
		if (getLockToParentBounds()) {
			lockToParentBounds(x, y);
		} else {
			setX(x);
			setY(y);
		}
		controlMoveHook();
	}

	/**
	 * Overridable method for extending the move event
	 */
	public void controlMoveHook() {

	}

	public void lockToParentBounds(float x, float y) {
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		// TODO need to take the common text padding into account here now
		if (getElementParent() != null) {
			if (x > getElementParent().getWidth() - getWidth()) {
				x = getElementParent().getWidth() - getWidth();
			}
			if (y > getElementParent().getHeight() - getHeight()) {
				y = getElementParent().getHeight() - getHeight();
			}
			if (x < 0) {
				setWidth(getElementParent().getWidth());
			}
			if (y < 0) {
				setHeight(getElementParent().getHeight());
			}
		} else {
			if (x > screen.getWidth() - getWidth()) {
				x = screen.getWidth() - getWidth();
			}
			if (y > screen.getHeight() - getHeight()) {
				y = screen.getHeight() - getHeight();
			}
			if (x < 0) {
				setWidth(screen.getWidth());
			}
			if (y < 0) {
				setHeight(screen.getHeight());
			}
		}
		setX(x);
		setY(y);
	}

	// </editor-fold>

	// <editor-fold desc="Auto Centering">
	/**
	 * Centers the Element to it's parent Element. If the parent element is
	 * null, it will use the screen's width/height.
	 */
	public void centerToParent() {
		if (elementParent == null) {
			setPosition(screen.getWidth() / 2 - (getWidth() / 2), screen.getHeight() / 2 - (getHeight() / 2));
		} else {
			setPosition(elementParent.getWidth() / 2 - (getWidth() / 2),
					elementParent.getHeight() / 2 - (getHeight() / 2));
		}
	}

	public void centerToParentV() {
		if (elementParent == null) {
			setPosition(getX(), screen.getHeight() / 2 - (getHeight() / 2));
		} else {
			setPosition(getX(), elementParent.getHeight() / 2 - (getHeight() / 2));
		}
	}

	public void centerToParentH() {
		if (elementParent == null) {
			setPosition(screen.getWidth() / 2 - (getWidth() / 2), getY());
		} else {
			setPosition(elementParent.getWidth() / 2 - (getWidth() / 2), getY());
		}
	}

	// </editor-fold>

	// <editor-fold desc="Resze Borders">
	/**
	 * Set the north, west, east and south borders in number of pixels
	 * 
	 * @param borderSize
	 */
	public void setResizeBorders(float borderSize) {
		borders.set(borderSize, borderSize, borderSize, borderSize);
	}

	/**
	 * Set the north, west, east and south borders in number of pixels
	 * 
	 * @param nBorder
	 *            float
	 * @param wBorder
	 *            float
	 * @param eBorder
	 *            float
	 * @param sBorder
	 *            float
	 */
	public void setResizeBorders(float nBorder, float wBorder, float eBorder, float sBorder) {
		borders.setX(nBorder);
		borders.setY(wBorder);
		borders.setZ(eBorder);
		borders.setW(sBorder);
	}

	/**
	 * Sets the width of north border in number of pixels
	 * 
	 * @param nBorder
	 *            float
	 */
	public void setNorthResizeBorder(float nBorder) {
		borders.setX(nBorder);
	}

	/**
	 * Sets the width of west border in number of pixels
	 * 
	 * @param wBorder
	 *            float
	 */
	public void setWestResizeBorder(float wBorder) {
		borders.setY(wBorder);
	}

	/**
	 * Sets the width of east border in number of pixels
	 * 
	 * @param eBorder
	 *            float
	 */
	public void setEastResizeBorder(float eBorder) {
		borders.setZ(eBorder);
	}

	/**
	 * Sets the width of south border in number of pixels
	 * 
	 * @param sBorder
	 *            float
	 */
	public void setSouthResizeBorder(float sBorder) {
		borders.setW(sBorder);
	}

	/**
	 * Returns the height of the north resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderNorthSize() {
		return this.borderHandles.x;
	}

	/**
	 * Returns the width of the west resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderWestSize() {
		return this.borderHandles.y;
	}

	/**
	 * Returns the width of the east resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderEastSize() {
		return this.borderHandles.z;
	}

	/**
	 * Returns the height of the south resize border
	 * 
	 * @return float
	 */
	public float getResizeBorderSouthSize() {
		return this.borderHandles.w;
	}

	// </editor-fold>

	// <editor-fold desc="Mesh & Geometry">
	/**
	 * Returns a pointer to the custom mesh used to render the Element.
	 * 
	 * @return ElementGridQuad model
	 */
	public ElementQuadGrid getModel() {
		return this.model;
	}

	/**
	 * Returns the Element's Geometry.
	 * 
	 * @return
	 */
	public Geometry getGeometry() {
		return this.geom;
	}

	// </editor-fold>

	// <editor-fold desc="Fonts & Text">
	/**
	 * Sets the element's text layer font
	 * 
	 * @param fontPath
	 *            String The font asset path
	 */
	public void setFont(String fontPath) {

		// font = app.getAssetManager().loadFont(fontPath);

		// This was copied from constructor code that sets up font
		BitmapFont tempFont = app.getAssetManager().loadFont(fontPath);
		font = new BitmapFont();
		font.setCharSet(app.getAssetManager().loadFont(fontPath).getCharSet());
		Material[] pages = new Material[tempFont.getPageSize()];
		for (int i = 0; i < pages.length; i++) {
			pages[i] = tempFont.getPage(i).clone();
		}
		font.setPages(pages);
		// -- end of new bit

		if (textElement != null) {
			String text = this.getText();
			textElement.removeFromParent();
			textElement = null;
			setText(text);
		}
	}

	/**
	 * Returns the Bitmapfont used by the element's text layer
	 * 
	 * @return BitmapFont font
	 */
	public BitmapFont getFont() {
		return this.font;
	}

	/**
	 * Sets the element's text layer font size
	 * 
	 * @param fontSize
	 *            float The size to set the font to
	 */
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
		if (textElement != null) {
			textElement.setSize(fontSize);
//			textElement.setFontSize(fontSize);
		}
	}

	/**
	 * Returns the element's text layer font size
	 * 
	 * @return float fontSize
	 */
	public float getFontSize() {
		return this.fontSize;
	}

	/**
	 * Sets the element's text layer font color
	 * 
	 * @param fontColor
	 *            ColorRGBA The color to set the font to
	 */
	public void setFontColor(ColorRGBA fontColor) {
		this.fontColor = fontColor;
		if (textElement != null) {
			textElement.setColor(fontColor);
		}
	}

	/**
	 * Return the element's text layer font color
	 * 
	 * @return ColorRGBA fontColor
	 */
	public ColorRGBA getFontColor() {
		return this.fontColor;
	}

	/**
	 * Sets the element's text layer horizontal alignment
	 * 
	 * @param textAlign
	 */
	public Element setTextAlign(BitmapFont.Align textAlign) {
		this.textAlign = textAlign;
		if (textElement != null) {
			textElement.setAlignment(textAlign);
//			textElement.setTextAlign(textAlign);
		}
		return this;
	}

	/**
	 * Returns the element's text layer horizontal alignment
	 * 
	 * @return Align text Align
	 */
	public BitmapFont.Align getTextAlign() {
		return this.textAlign;
	}

	/**
	 * Sets the element's text layer vertical alignment
	 * 
	 * @param textVAlign
	 */
	public Element setTextVAlign(BitmapFont.VAlign textVAlign) {
		this.textVAlign = textVAlign;
		if (textElement != null) {
			textElement.setVerticalAlignment(textVAlign);
//			textElement.setTextVAlign(textVAlign);
		}
		return this;
	}

	/**
	 * Returns the element's text layer vertical alignment
	 * 
	 * @return VAlign textVAlign
	 */
	public BitmapFont.VAlign getTextVAlign() {
		return this.textVAlign;
	}

	/**
	 * Sets the element's text later wrap mode
	 * 
	 * @param textWrap
	 *            LineWrapMode textWrap
	 */
	public void setTextWrap(LineWrapMode textWrap) {
		this.textWrap = textWrap;
		if (textElement != null) {
			textElement.setLineWrapMode(textWrap);
//			textElement.setTextWrap(textWrap);
		}
	}

	/**
	 * Returns the element's text layer wrap mode
	 * 
	 * @return LineWrapMode textWrap
	 */
	public LineWrapMode getTextWrap() {
		return this.textWrap;
	}

	/**
	 * Sets the elements text layer position
	 * 
	 * @param x
	 *            Position's x coord
	 * @param y
	 *            Position's y coord
	 */
	public void setTextPosition(float x, float y) {
		this.textPosition = new Vector2f(x, y);
	}

	/**
	 * Returns the current x, y coords of the element's text layer
	 * 
	 * @return Vector2f textPosition
	 */
	public Vector2f getTextPosition() {
		return this.textPosition;
	}

	/**
	 * Sets the padding set for the element's text layer
	 * 
	 * @param textPadding
	 */
	public void setTextPadding(float textPadding) {
		setTextPadding(textPadding, textPadding, textPadding, textPadding);
	}

	public void setTextPadding(float left, float right, float top, float bottom) {
		this.textPadding.set(left, right, top, bottom);
	}

	/**
	 * Set text padding. The padding is ordered differently to resizer borders.
	 * x is the left padding, y is the right, z is top and w is the bottom.
	 * 
	 * @param textPadding
	 */
	public void setTextPadding(Vector4f textPadding) {
		this.textPadding.set(textPadding);
		dirtyLayout(false);
		layoutChildren();
	}

	/**
	 * Get text padding. The padding is ordered differently to resizer borders.
	 * x is the left padding, y is the right, z is top and w is the bottom.
	 * 
	 * @return float textPadding
	 */
	public float getTextPadding() {
		return this.textPadding.x;
	}

	public Vector4f getTextPaddingVec() {
		return this.textPadding;
	}

	/**
	 * Updates the element's textlayer position and boundary
	 */
	protected void updateTextElement() {
		if (textElement != null) {
			textElement.setLocalTranslation(textPosition.x + textPadding.x,
					getHeight() - (textPosition.y + textPadding.z), textElement.getLocalTranslation().z);
			textElement.setBox(new Rectangle(0, 0, dimensions.x - (textPadding.x + textPadding.y),
					dimensions.y - (textPadding.z + textPadding.w)));
//			textElement.setBounds(dimensions.x - (textPadding.x + textPadding.y),
//					dimensions.y - (textPadding.z + textPadding.w));
		}
	}

	public void resetTextElement() {
		if (textElement != null) {
			textElement.setLocalTranslation(textPosition.x + textPadding.x,
					getHeight() - (textPosition.y + textPadding.z), textElement.getLocalTranslation().z);
			textElement.setBox(new Rectangle(0, 0, dimensions.x - (textPadding.x + textPadding.y), 25));
//			textElement.setBounds(dimensions.x - (textPadding.x + textPadding.y), 25);
			dirtyLayout(false);
			layoutChildren();
		}
	}

	public void removeTextElement() {
		if (textElement != null) {
			textElement.removeFromParent();
			textElement = null;
			dirtyLayout(false);
			layoutChildren();
		}
	}

	/**
	 * Sets the text of the element.
	 * 
	 * @param text
	 *            String The text to display.
	 */
	public void setText(String text) {
		this.text = text;
		if (screen != null) {
			if (textElement == null) {
				textElement = new BitmapText(font, false);
				textElement.setBox(new Rectangle(0, 0, dimensions.x, dimensions.y));
				 //textElement = new LabelElement(screen, Vector2f.ZERO);
				 //textElement.setBox(new Rectangle(0, 0, dimensions.x, dimensions.y));
//				textElement = new AnimText(app.getAssetManager(), font);
			}
//			textElement.setBounds(dimensions.x, dimensions.y);
			textElement.setLineWrapMode(textWrap);
			textElement.setAlignment(textAlign);
			textElement.setVerticalAlignment(textVAlign);
			textElement.setSize(fontSize);
//			textElement.setTextWrap(textWrap);
//			textElement.setTextAlign(textAlign);
//			textElement.setTextVAlign(getTextVAlign());
//			textElement.setFontSize(fontSize);
			textElement.setColor(fontColor);
			textElement.setText(text);
			updateTextElement();
			updateGlobalAlpha();
			if (textElement.getParent() == null) {
				this.attachChild(textElement);
			}
			dirtyLayout(false);
		}
	}

	/**
	 * Retuns the current visible text of the element.
	 * 
	 * @return String text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Returns a pointer to the BitmapText element of this Element. Returns null
	 * if setText() has not been called.
	 * 
	 * @return BitmapText textElement
	 */
	public BitmapText getTextElement() {
		 //public TextElement getTextElement() {
//	public AnimText getTextElement() {
		return this.textElement;
	}

	/**
	 * Updates font materials with any changes to clipping layers
	 */
	private void setFontPages() {
		if (textElement != null && ENABLE_CLIPPING) {
			if (!isVisible) {
				for (int i = 0; i < font.getPageSize(); i++) {
					this.font.getPage(i).setVector4("Clipping", clippingBounds);
					this.font.getPage(i).setBoolean("UseClipping", true);
				}
			} else {
				if (isClipped) {
					for (int i = 0; i < font.getPageSize(); i++) {
						this.font.getPage(i).setVector4("Clipping", clippingBounds.add(textClipPadding.x,
								textClipPadding.y, -textClipPadding.z, -textClipPadding.w));
						this.font.getPage(i).setBoolean("UseClipping", true);
					}
				} else {
					for (int i = 0; i < font.getPageSize(); i++) {
						this.font.getPage(i).setBoolean("UseClipping", false);
					}
				}
			}
		}
	}

	// </editor-fold>

	// <editor-fold desc="Materials, Textures & Atlas">
	private void throwParserException() {
		try {
			throw new java.text.ParseException(
					"The provided texture information does not conform to the expected standard of x=(int)|y=(int)|w=(int)|h=(int)",
					0);
		} catch (ParseException ex) {
			Logger.getLogger(Element.class.getName()).log(Level.SEVERE,
					"The provided texture information does not conform to the expected standard of x=(int)|y=(int)|w=(int)|h=(int)",
					ex);
		}
	}

	/**
	 * Sets the texture to use as an atlas image as well as the atlas image
	 * coords.
	 * 
	 * @param tex
	 *            The texture to use as a local atlas image
	 * @param queryString
	 *            The position of the desire atlas image (e.g.
	 *            "x=0|y=0|w=50|h=50")
	 */
	public void setTextureAtlasImage(Texture tex, String queryString) {
		this.useLocalTexture = false;

		this.defaultTex = tex;
		mat.setTexture("ColorMap", tex);
		mat.setColor("Color", new ColorRGBA(1, 1, 1, 1));
		mat.setBoolean("UseEffectTexCoords", true);

		this.useLocalAtlas = true;
		this.atlasCoords = queryString;

		float[] coords = screen.parseAtlasCoords(queryString);
		float textureAtlasX = coords[0];
		float textureAtlasY = coords[1];
		float textureAtlasW = coords[2];
		float textureAtlasH = coords[3];

		float imgWidth = defaultTex.getImage().getWidth();
		float imgHeight = defaultTex.getImage().getHeight();
		float pixelWidth = 1f / imgWidth;
		float pixelHeight = 1f / imgHeight;

		textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;

		this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
				textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);
		geom.setMesh(model);
	}

	/**
	 * Returns the current unparsed string representing the Element's atlas
	 * image
	 * 
	 * @return
	 */
	public String getAtlasCoords() {
		return this.atlasCoords;
	}

	/**
	 * Sets the element image to the specified x/y/width/height
	 * 
	 * @param queryString
	 *            (e.g. "x=0|y=0|w=50|h=50")
	 */
	public void updateTextureAtlasImage(String queryString) {
		float[] coords = screen.parseAtlasCoords(queryString);
		float textureAtlasX = coords[0];
		float textureAtlasY = coords[1];
		float textureAtlasW = coords[2];
		float textureAtlasH = coords[3];

		float imgWidth = defaultTex.getImage().getWidth();
		float imgHeight = defaultTex.getImage().getHeight();
		float pixelWidth = 1f / imgWidth;
		float pixelHeight = 1f / imgHeight;

		textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;

		getModel().updateTexCoords(textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);
	}

	/**
	 * Returns if the element is using a local texture atlas of the screen
	 * defined texture atlas
	 * 
	 * @return
	 */
	public boolean getUseLocalAtlas() {
		return this.useLocalAtlas;
	}

	/**
	 * Returns the difference between the placement of the elements current
	 * image and the given texture coords.
	 * 
	 * @param coords
	 *            The x/y coords of the new image
	 * @return Vector2f containing The difference between the given coords and
	 *         the original image
	 */
	public Vector2f getAtlasTextureOffset(float[] coords) {
		Texture tex;
		if (defaultTex != null)
			tex = defaultTex;
		else
			tex = screen.getAtlasTexture();
		float imgWidth = tex.getImage().getWidth();
		float imgHeight = tex.getImage().getHeight();
		float pixelWidth = 1f / imgWidth;
		float pixelHeight = 1f / imgHeight;

		return new Vector2f(
				getModel().getEffectOffset(pixelWidth * coords[0], pixelHeight * (imgHeight - coords[1] - coords[3])));
	}

	public void setUseLocalTexture(boolean useLocalTexture) {
		this.useLocalTexture = useLocalTexture;
	}

	public boolean getUseLocalTexture() {
		return this.useLocalTexture;
	}

	/**
	 * Will set the textures WrapMode to repeat if enabled.<br/>
	 * <br/>
	 * NOTE: This only works when texture atlasing has not been enabled. For
	 * info on texture atlas usage, see both:<br/>
	 * 
	 * @see Screen#setUseTextureAtlas(boolean enable, String path)
	 * @see #setTextureAtlasImage(com.jme3.texture.Texture tex, java.lang.String
	 *      path)
	 * @param tileImage
	 */
	public void setTileImage(boolean tileImage) {
		this.useLocalTexture = true;

		this.tileImage = tileImage;
		if (tileImage)
			((Texture) mat.getParam("ColorMap").getValue()).setWrap(Texture.WrapMode.Repeat);
		else
			((Texture) mat.getParam("ColorMap").getValue()).setWrap(Texture.WrapMode.Clamp);
		setDimensions(dimensions);
	}

	public boolean getTileImage() {
		return this.tileImage;
	}

	public void setTileImageByKey(String style, String key) {
		boolean tile = false;
		try {
			tile = screen.getStyle(style).getBoolean(key);
		} catch (Exception ex) {
		}
		setTileImage(tile);
	}

	public void setClipPaddingByKey(String style, String key) {
		try {
			setClipPadding(screen.getStyle(style).getFloat(key));
		} catch (Exception ex) {
		}
		try {
			setClipPadding(screen.getStyle(style).getVector4f(key));
		} catch (Exception ex) {
		}
	}

	public void setFontByKey(String style, String key) {
		try {
			setFont(screen.getStyle("Font").getString(screen.getStyle(style).getString(key)));
		} catch (Exception ex) {
		}
	}

	public void setTextPaddingByKey(String style, String key) {
		try {
			setTextPadding(screen.getStyle(style).getFloat(key));
		} catch (Exception ex) {
		}
		try {
			setTextPadding(screen.getStyle(style).getVector4f(key));
		} catch (Exception ex) {
		}
	}

	public void setTextClipPaddingByKey(String style, String key) {
		try {
			setTextClipPadding(screen.getStyle(style).getFloat(key));
		} catch (Exception ex) {
		}
		try {
			setTextClipPadding(screen.getStyle(style).getVector4f(key));
		} catch (Exception ex) {
		}
	}

	/**
	 * Returns the default material for the element
	 * 
	 * @return Material mat
	 */
	public Material getMaterial() {
		return this.mat;
	}

	/**
	 * A way to override the default material of the element.
	 * 
	 * NOTE: It is important that the shader used with the new material is
	 * either: A: The provided Unshaded material contained with this library, or
	 * B: The custom shader contains the caret, text range, clipping and effect
	 * handling provided in the default shader.
	 * 
	 * @param mat
	 *            The Material to use for rendering this Element.
	 */
	public void setLocalMaterial(Material mat) {
		this.mat = mat;
		this.setMaterial(mat);
	}

	/**
	 * Returns the default material for the element
	 * 
	 * @param mat
	 */
	public void setElementMaterial(Material mat) {
		this.mat = mat;
	}

	/**
	 * Returns a pointer to the Material used for rendering this Element.
	 * 
	 * @return Material mat
	 */
	public Material getElementMaterial() {
		return this.mat;
	}

	/**
	 * Returns the default Texture for the Element
	 * 
	 * @return Texture defaultTexture
	 */
	public Texture getElementTexture() {
		return this.defaultTex;
	}

	/**
	 * Adds an alpha map to the Elements material
	 * 
	 * @param alphaMap
	 *            A String path to the alpha map
	 */
	public void setAlphaMap(String alphaMap) {
		Texture alpha = null;
		if (screen.getUseTextureAtlas() && !useLocalTexture) {
			if (this.getElementTexture() != null)
				alpha = getElementTexture();
			else
				alpha = screen.getAtlasTexture();
			Vector2f alphaOffset = getAtlasTextureOffset(screen.parseAtlasCoords(alphaMap));
			mat.setVector2("OffsetAlphaTexCoord", alphaOffset);
		} else {
			alpha = app.getAssetManager().loadTexture(alphaMap);
			alpha.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			alpha.setMagFilter(Texture.MagFilter.Nearest);
			alpha.setWrap(Texture.WrapMode.Clamp);
		}

		this.alphaMap = alpha;

		if (defaultTex == null) {
			if (!screen.getUseTextureAtlas() || useLocalTexture) {
				float imgWidth = alpha.getImage().getWidth();
				float imgHeight = alpha.getImage().getHeight();
				float pixelWidth = 1f / imgWidth;
				float pixelHeight = 1f / imgHeight;

				this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
						0, 0, imgWidth, imgHeight);

				geom.setMesh(model);
			} else {
				float[] coords = screen.parseAtlasCoords(alphaMap);
				float textureAtlasX = coords[0];
				float textureAtlasY = coords[1];
				float textureAtlasW = coords[2];
				float textureAtlasH = coords[3];

				float imgWidth = alpha.getImage().getWidth();
				float imgHeight = alpha.getImage().getHeight();
				float pixelWidth = 1f / imgWidth;
				float pixelHeight = 1f / imgHeight;

				textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;

				model = new ElementQuadGrid(this.getDimensions(), borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
						textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);

				geom.setMesh(model);
				mat.setVector2("OffsetAlphaTexCoord", new Vector2f(0, 0));
			}
		}
		mat.setTexture("AlphaMap", alpha);
	}

	public Texture getAlphaMap() {
		return this.alphaMap;
	}

	public Element setTexture(Image colorMap) {
		Texture color = null;
		if (screen.getUseTextureAtlas() && !useLocalTexture) {
			if (this.getElementTexture() != null)
				color = getElementTexture();
			else
				color = screen.getAtlasTexture();
		} else {
			color = new Texture2D(colorMap);
			color.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			color.setMagFilter(Texture.MagFilter.Nearest);
			color.setWrap(Texture.WrapMode.Clamp);
		}

		this.defaultTex = color;

		if (!screen.getUseTextureAtlas() || useLocalTexture) {
			float imgWidth = color.getImage().getWidth();
			float imgHeight = color.getImage().getHeight();
			float pixelWidth = 1f / imgWidth;
			float pixelHeight = 1f / imgHeight;

			this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight, 0,
					0, imgWidth, imgHeight);

			geom.setMesh(model);
		} else {
			throw new UnsupportedOperationException();
		}

		mat.setTexture("ColorMap", color);
		mat.setColor("Color", ColorRGBA.White);
		return this;
	}

	public final Element setTexture(String colorMap) {
		Texture color = null;
		if (screen.getUseTextureAtlas() && !useLocalTexture) {
			if (this.getElementTexture() != null)
				color = getElementTexture();
			else
				color = screen.getAtlasTexture();
		} else {
			color = app.getAssetManager().loadTexture(colorMap);
			color.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			color.setMagFilter(Texture.MagFilter.Nearest);
			color.setWrap(Texture.WrapMode.Clamp);
		}

		this.defaultTex = color;

		if (!screen.getUseTextureAtlas() || useLocalTexture) {
			float imgWidth = color.getImage().getWidth();
			float imgHeight = color.getImage().getHeight();
			float pixelWidth = 1f / imgWidth;
			float pixelHeight = 1f / imgHeight;

			this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight, 0,
					0, imgWidth, imgHeight);

			geom.setMesh(model);
		} else {
			float[] coords = screen.parseAtlasCoords(colorMap);
			float textureAtlasX = coords[0];
			float textureAtlasY = coords[1];
			float textureAtlasW = coords[2];
			float textureAtlasH = coords[3];

			float imgWidth = color.getImage().getWidth();
			float imgHeight = color.getImage().getHeight();
			float pixelWidth = 1f / imgWidth;
			float pixelHeight = 1f / imgHeight;

			textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;

			model = new ElementQuadGrid(this.getDimensions(), borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
					textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);

			geom.setMesh(model);
		}

		mat.setTexture("ColorMap", color);
		mat.setColor("Color", ColorRGBA.White);
		return this;
	}

	public void rebuildModel() {
		if (defaultTex != null) {
			if (!screen.getUseTextureAtlas() || useLocalTexture) {
				float imgWidth = defaultTex.getImage().getWidth();
				float imgHeight = defaultTex.getImage().getHeight();
				float pixelWidth = 1f / imgWidth;
				float pixelHeight = 1f / imgHeight;

				this.model = new ElementQuadGrid(this.dimensions, borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
						0, 0, imgWidth, imgHeight);
				geom.setMesh(model);
			} else {
				float[] coords = screen.parseAtlasCoords(this.atlasCoords);
				float textureAtlasX = coords[0];
				float textureAtlasY = coords[1];
				float textureAtlasW = coords[2];
				float textureAtlasH = coords[3];

				float imgWidth = defaultTex.getImage().getWidth();
				float imgHeight = defaultTex.getImage().getHeight();
				float pixelWidth = 1f / imgWidth;
				float pixelHeight = 1f / imgHeight;

				textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;

				model = new ElementQuadGrid(this.getDimensions(), borders, imgWidth, imgHeight, pixelWidth, pixelHeight,
						textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);
				geom.setMesh(model);
			}
		}
	}

	// </editor-fold>

	// <editor-fold desc="Visibility">
	/**
	 * This may be remove soon and probably should not be used as the method of
	 * handling hide show was updated making this unnecissary.
	 * 
	 * @param wasVisible
	 *            boolean
	 */
	public void setDefaultWasVisible(boolean wasVisible) {
		this.wasVisible = wasVisible;
	}

	/**
	 * Shows the current Element with the defined Show effect. If no Show effect
	 * is defined, the Element will show as normal.
	 */
	public void showWithEffect() {
		Effect effect = getEffect(Effect.EffectEvent.Show);
		if (effect != null) {
			if (effect.getEffectType() == Effect.EffectType.FadeIn) {
				Effect clone = effect.clone();
				clone.setAudioFile(null);
				this.propagateEffect(clone, false);
			} else {
				if (getTextElement() != null)
					getTextElement().setAlpha(1f * getAlphaFactor());
				screen.getEffectManager().applyEffect(effect);
			}
		} else
			this.show();
	}

	/**
	 * Sets this Element and any Element contained within it's nesting order to
	 * visible.
	 * 
	 * NOTE: Hide and Show relies on shader-based clipping
	 */
	public void show() {
		if (!isVisible) {
			// screen.updateZOrder(getAbsoluteParent());
			this.isVisible = true;
			this.isClipped = wasClipped;
			// updateClipping();
			updateClippingLayers();
			controlShowHook();

			if (getTextElement() != null)
				getTextElement().setAlpha(1f * getAlphaFactor());

			if (getParent() == null) {
				if (getElementParent() != null) {
					getElementParent().attachChild(this);
				} else {
					screen.getGUINode().attachChild(this);
				}
			}

			for (Element el : elementChildren.values()) {
				el.childShow();
			}
			setLabelVisibility();
		}
	}

	public void showAsModal(boolean showWithEffect) {
		if (!isVisibleAsModal) {
			if (priority == ZPriority.NORMAL) {
				isVisibleAsModal = true;
				screen.showAsModal(this, showWithEffect);
			} else {
				throw new IllegalStateException(
						String.format("May only show elements of priority %s as modal.", ZPriority.NORMAL));
			}
		}
	}

	/**
	 * Recursive call for properly showing children of the Element. I'm thinking
	 * this this needs to be a private method, however I need to verify this
	 * before I update it.
	 */
	public void childShow() {
		if (getTextElement() != null)
			getTextElement().setAlpha(1f * getAlphaFactor());

		this.isVisible = wasVisible;
		this.isClipped = wasClipped;
		// updateClipping();
		updateClippingLayers();
		controlShowHook();
		for (Element el : elementChildren.values()) {
			el.childShow();
		}
		layoutChildren();
	}

	public Vector2f getMaxDimensions() {
		return maxDimensions;
	}

	public Vector2f getPreferredDimensions() {
		return prefDimensions;
	}

	public Element setMaxDimensions(Vector2f maxDimensions) {
		this.maxDimensions = maxDimensions;
		dirtyLayout(false);
		return this;
	}

	public Element setPreferredDimensions(Vector2f prefDimensions) {
		this.prefDimensions = prefDimensions;
		dirtyLayout(false);
		return this;
	}

	public List<Element> getIgnore() {
		return null;
	}

	/**
	 * An overridable method for extending the show event.
	 */
	public void controlShowHook() {
	}

	/**
	 * Hides the element using the current defined Hide effect. If no Hide
	 * effect is defined, the Element will hide as usual.
	 */
	public void hideWithEffect() {
		Effect effect = getEffect(Effect.EffectEvent.Hide);
		if (effect != null) {
			if (effect.getIsDestroyOnHide()) {
				// Actually remove from the parents child list, but don't remove
				// from the scene yet. This lets
				// the event complete, but if another component with same UID
				// gets added again before the event
				// ends, it won't cause problems
				if (getElementParent() != null) {
					getChildren().remove(this);
				}
				if (screen instanceof Screen) {
					((Screen) screen).getElementsAsMap().remove(this.getUID());
				}
			}
			if (effect.getEffectType() == Effect.EffectType.FadeOut) {
				Effect clone = effect.clone();
				clone.setAudioFile(null);
				this.propagateEffect(clone, true);
			} else
				screen.getEffectManager().applyEffect(effect);
			if (isVisibleAsModal) {
				isVisibleAsModal = false;
				screen.releaseModal(this);
			}
		} else
			this.hide();
	}

	public boolean getIsVisibleAsModal() {
		return isVisibleAsModal;
	}

	/**
	 * Recursive call that sets this Element and any Element contained within
	 * it's nesting order to hidden.
	 * 
	 * NOTE: Hide and Show relies on shader-based clipping
	 */
	public void hide() {
		if (isVisible) {
			if (isVisibleAsModal) {
				isVisibleAsModal = false;
				screen.releaseModal(this);
			}
			this.wasVisible = isVisible;
			this.isVisible = false;
			this.isClipped = true;

			if (screen.getUseToolTips())
				if (screen.getToolTipFocus() == this)
					screen.hideToolTip();

			// updateClipping();
			setLabelVisibility();
			updateClippingLayers();
			controlHideHook();
			removeFromParent();
			for (Element el : elementChildren.values()) {
				el.childHide();
			}
		}
	}

	/**
	 * For internal use. This method should never be called directly.
	 */
	public void childHide() {
		if (isVisible) {
			this.wasVisible = isVisible;
			this.isVisible = false;
			this.isClipped = true;

			if (screen.getUseToolTips())
				if (screen.getToolTipFocus() == this)
					screen.hideToolTip();

			// updateClipping();
			updateClippingLayers();
			controlHideHook();
			for (Element el : elementChildren.values()) {
				el.childHide();
			}
		}
	}

	/**
	 * Hides or shows the element (true = show, false = hide)
	 * 
	 * @param visibleState
	 */
	public void setIsVisible(boolean visibleState) {
		if (visibleState) {
			show();
		} else {
			hide();
		}
	}

	/**
	 * Toggles the Element's visibility based on the current state.
	 */
	public void setIsVisible() {
		if (getIsVisible())
			hide();
		else
			show();
	}

	/**
	 * An overridable method for extending the hide event.
	 */
	public void controlHideHook() {
	}

	/**
	 * An overridable method for extending the bring to front event.
	 */
	public void movedToFrontHook() {
	}

	/**
	 * Return if the Element is visible
	 * 
	 * @return boolean isVisible
	 */
	public boolean getIsVisible() {
		return this.isVisible;
	}

	// </editor-fold>

	// <editor-fold desc="Cleanup">
	public void cleanup() {

		if (isVisibleAsModal) {
			isVisibleAsModal = false;
			screen.releaseModal(this);
		}

		controlCleanupHook();
		for (Element el : elementChildren.values()) {
			el.cleanup();
		}
	}

	/**
	 * An overridable method for handling control specific cleanup.
	 */
	public void controlCleanupHook() {
	}

	// </editor-fold>

	// <editor-fold desc="Clipping">
	/**
	 * Sets the elements clipping layer to the provided element.
	 * 
	 * @param clippingLayer
	 *            The element that provides the clipping boundaries.
	 */
	@Deprecated
	public void setClippingLayer(Element clippingLayer) {
		if (clippingLayer != null)
			addClippingLayer(clippingLayer);
		else
			removeClippingLayer(clippingLayer);
		/*
		 * if (clippingLayer != null) { this.isClipped = true; this.wasClipped =
		 * true; this.clippingLayer = clippingLayer;
		 * this.mat.setBoolean("UseClipping", true); } else { this.isClipped =
		 * false; this.wasClipped = false; this.clippingLayer = null;
		 * this.mat.setBoolean("UseClipping", false); }
		 */
	}

	/**
	 * Sets the elements clipping layer to the provided element.
	 * 
	 * @param clippingLayer
	 *            The element that provides the clipping boundaries.
	 */
	@Deprecated
	public void setSecondaryClippingLayer(Element secondaryClippingLayer) {
		if (secondaryClippingLayer != null)
			addClippingLayer(secondaryClippingLayer);
		else
			removeClippingLayer(secondaryClippingLayer);
		/*
		 * if (secondaryClippingLayer != null) { this.secondaryClippingLayer =
		 * secondaryClippingLayer; } else { this.secondaryClippingLayer = null;
		 * }
		 */
	}

	/**
	 * Recursive update of all child Elements clipping layer
	 * 
	 * @param clippingLayer
	 *            The clipping layer to apply
	 */
	@Deprecated
	public void setControlClippingLayer(Element clippingLayer) {
		setClippingLayer(clippingLayer);
		// for (Element el : elementChildren.values()) {
		// el.setControlClippingLayer(clippingLayer);
		// }
	}

	/**
	 * Recursive update of all child Elements clipping & secondary clipping
	 * layers
	 * 
	 * @param clippingLayer
	 *            The clipping layer to apply
	 * @param secondaryClippingLayer
	 *            The clipping layer's parent clipping layer to apply
	 */
	@Deprecated
	public void setControlClippingLayer(Element clippingLayer, Element secondaryClippingLayer) {
		setClippingLayer(clippingLayer);
		setSecondaryClippingLayer(secondaryClippingLayer);
		for (Element el : elementChildren.values()) {
			el.setControlClippingLayer(clippingLayer, secondaryClippingLayer);
		}
	}

	/**
	 * Returns if the Element's clipping layer has been set
	 * 
	 * @return boolean isClipped
	 */
	public boolean getIsClipped() {
		return isClipped;
	}

	/**
	 * Returns the elements clipping layer or null is element doesn't use
	 * clipping
	 * 
	 * @return Element clippingLayer
	 */
	public Element getClippingLayer() {
		return this.clippingLayer;
	}

	/**
	 * Returns a Vector4f containing the current boundaries of the element's
	 * clipping layer
	 * 
	 * @return Vector4f clippingBounds
	 */
	public Vector4f getClippingBounds() {
		return this.clippingBounds;
	}

	/**
	 * Adds a padding to the clippinglayer, in effect this contracts the size of
	 * the clipping bounds by the specified number of pixels
	 * 
	 * @param clipPadding
	 *            The number of pixels to pad the clipping area
	 */
	public void setClipPadding(float clipPadding) {
		this.clipPadding.set(clipPadding, clipPadding, clipPadding, clipPadding);
	}

	public void setClipPadding(float clipLeft, float clipRight, float clipTop, float clipBottom) {
		this.clipPadding.set(clipLeft, clipTop, clipRight, clipBottom);
	}

	public void setClipPadding(Vector4f clipPadding) {
		this.clipPadding.set(clipPadding);
	}

	/**
	 * Returns the current clipPadding
	 * 
	 * @return float clipPadding
	 */
	public float getClipPadding() {
		return clipPadding.x;
	}

	public Vector4f getClipPaddingVec() {
		return clipPadding;
	}

	/**
	 * Shrinks the clipping area by set number of pixels
	 * 
	 * @param textClipPadding
	 *            The number of pixels to pad the clipping area with on each
	 *            side
	 */
	public void setTextClipPadding(float textClipPadding) {
		this.textClipPadding.set(textClipPadding, textClipPadding, textClipPadding, textClipPadding);
	}

	public void setTextClipPadding(float clipLeft, float clipRight, float clipTop, float clipBottom) {
		this.textClipPadding.set(clipLeft, clipTop, clipRight, clipBottom);
	}

	public void setTextClipPadding(Vector4f textClipPadding) {
		this.textClipPadding.set(textClipPadding);
	}

	public float getTextClipPadding() {
		return textClipPadding.x;
	}

	public Vector4f getTextClipPaddingVec() {
		return textClipPadding;
	}

	/**
	 * Updates the clipping bounds for any element that has a clipping layer
	 * 
	 * See updateLocalClipping
	 */
	@Deprecated
	public void updateClipping() {
		// updateLocalClipping();
		updateLocalClippingLayer();
		for (Element el : elementChildren.values()) {
			el.updateClipping();
		}
	}

	// New Clipping
	public void addClippingLayer(Element el) {
		ClippingDefine def = new ClippingDefine(el);
		propigateClippingLayerAdd(def);
	}

	public void addClippingLayer(Element el, Vector4f relativeClippingBounds) {
		ClippingDefine def = new ClippingDefine(el, relativeClippingBounds);
		propigateClippingLayerAdd(def);
	}

	public ClippingDefine getClippingDefine(Element el) {
		ClippingDefine def = null;

		for (ClippingDefine d : clippingLayers) {
			if (d.getElement() == el) {
				def = d;
				break;
			}
		}
		return def;
	}

	public void updateClippingLayer(Element el, Vector4f clip) {
		ClippingDefine def = getClippingDefine(el);
		if (def != null) {
			if (def.clip == null)
				def.clip = new Vector4f();
			def.clip.set(clip);
		}
		validateClipSettings();
		for (Element c : elementChildren.values()) {
			c.updateClippingLayer(el, clip);
		}
	}

	public void propigateClippingLayerAdd(ClippingDefine def) {
		if (!clippingLayers.contains(def))
			clippingLayers.add(def);
		validateClipSettings();
		for (Element c : elementChildren.values()) {
			c.propigateClippingLayerAdd(def);
		}
	}

	public void removeClippingLayer(Element el) {
		for (ClippingDefine def : clippingLayers) {
			if (def.getElement() == el)
				remClippingLayers.add(def);
		}
		if (!remClippingLayers.isEmpty()) {
			clippingLayers.removeAll(remClippingLayers);
			remClippingLayers.clear();
		}
		for (Element c : elementChildren.values()) {
			c.removeClippingLayer(el);
		}
	}

	public boolean getHasClippingLayers() {
		return !clippingLayers.isEmpty();
	}

	public void updateClippingLayers() {
		updateLocalClippingLayer();
		validateClipSettings();
		for (Element c : elementChildren.values()) {
			c.updateClippingLayers();
		}
	}

	public void setIsClippingEnabled(boolean clippingEnabled) {
		this.clippingEnabled = clippingEnabled;
		updateClippingLayers();
	}

	public boolean getIsClippingEnabled() {
		return clippingEnabled;
	}

	protected boolean isClippingEnabledInHeirarchy() {
		return clippingEnabled && (elementParent == null || elementParent.getIsClippingEnabled());
	}

	public void updateLocalClippingLayer() {
		if (isVisible) {
			if (!clippingLayers.isEmpty()) {
				calcClipping();
				setFontPages();
			}
		}
	}

	private void calcClipping() {
		float cX = 0;
		float cY = 0;
		float cW = screen.getWidth();
		float cH = screen.getHeight();

		if (isClippingEnabledInHeirarchy()) {
			for (ClippingDefine def : clippingLayers) {
				// System.out.println("DEF: " + def.getClipping());
				Vector4f clippedArea = def.getClipping();
				// System.out.println("XXX: clipped " + clippedArea + " c: " +
				// def.getElement());
				clipTest.set(clippedArea);
				if (def.getElement() != this) {
					clipTest.addLocal(def.getElement().getClipPaddingVec().x, def.getElement().getClipPaddingVec().y,
							-def.getElement().getClipPaddingVec().z, -def.getElement().getClipPaddingVec().w);
				}
				if (clipTest.x > cX)
					cX = clipTest.x;
				if (clipTest.y > cY)
					cY = clipTest.y;
				if (clipTest.z < cW)
					cW = clipTest.z;
				if (clipTest.w < cH)
					cH = clipTest.w;
			}
			// System.out.println("XXX: " + cX + "," + cY + "," + cW + "," +
			// cH);
			// if(getElementParent() == null) {
			// cY = screen.getHeight() - cY;
			// }
			// else {
			// cY = getElementParent().getHeight() - cY;
			// }
		}
		clippingBounds.set(cX, cY, cW, cH);
	}

	protected void validateClipSettings() {
		if (ENABLE_CLIPPING && mat != null) {
			if (!clippingLayers.isEmpty() && isClippingEnabledInHeirarchy()) {
				this.isClipped = true;
				this.wasClipped = true;
				if (!(Boolean) mat.getParam("UseClipping").getValue())
					mat.setBoolean("UseClipping", true);
			} else {
				this.isClipped = false;
				this.wasClipped = false;
				if ((Boolean) mat.getParam("UseClipping").getValue())
					mat.setBoolean("UseClipping", false);
			}
			mat.setVector4("Clipping", clippingBounds);
		}
	}

	public class ClippingDefine {
		public Element owner;
		public Vector4f clip = null;
		private Vector4f tempV4 = new Vector4f();

		public ClippingDefine(Element owner) {
			this.owner = owner;
			if (owner == null) {
				throw new IllegalArgumentException("Owner may not be null.");
			}
		}

		public ClippingDefine(Element owner, Vector4f clip) {
			this.owner = owner;
			this.clip = new Vector4f(clip);
			if (owner == null) {
				throw new IllegalArgumentException("Owner may not be null.");
			}
		}

		public Element getElement() {
			return owner;
		}

		public Vector4f getClipping() {
			if (clip == null) {
				tempV4.setX(owner.getAbsoluteX());
				tempV4.setY(
						Element.NEW_YFLIPPING ? screen.getHeight() - owner.getAbsoluteHeight() : owner.getAbsoluteY());
				tempV4.setZ(tempV4.getX() + owner.getWidth());
				tempV4.setW(tempV4.getY() + owner.getHeight());
			} else {
				float x = owner.getAbsoluteX();
				float y = Element.NEW_YFLIPPING ? screen.getHeight() - owner.getAbsoluteHeight() : owner.getAbsoluteY();
				tempV4.set(x + clip.x, y + clip.y, x + clip.z, y + clip.w);
			}
			return tempV4;
		}
	}

	// </editor-fold>

	// <editor-fold desc="Effects">
	/**
	 * Associates an Effect with this Element. Effects are not automatically
	 * associated with the specified event, but instead, the event type is used
	 * to retrieve the Effect at a later point
	 * 
	 * @param effectEvent
	 *            The Effect.EffectEvent the Effect is to be registered with
	 * @param effect
	 *            The Effect to store
	 */
	public void addEffect(Effect.EffectEvent effectEvent, Effect effect) {
		addEffect(effect);
	}

	/**
	 * Associates an Effect with this Element. Effects are not automatically
	 * associated with the specified event, but instead, the event type is used
	 * to retrieve the Effect at a later point
	 * 
	 * @param effect
	 *            The Effect to store
	 */
	public void addEffect(Effect effect) {
		effects.remove(effect.getEffectEvent());
		if (!effects.containsKey(effect.getEffectEvent())) {
			effect.setElement(this);
			effects.put(effect.getEffectEvent(), effect);
		}
	}

	/**
	 * Removes the Effect associated with the Effect.EffectEvent specified
	 * 
	 * @param effectEvent
	 */
	public void removeEffect(Effect.EffectEvent effectEvent) {
		effects.remove(effectEvent);
	}

	/**
	 * Retrieves the Effect associated with the specified Effect.EffectEvent
	 * 
	 * @param effectEvent
	 * @return effect
	 */
	public Effect getEffect(Effect.EffectEvent effectEvent) {
		Effect effect = null;
		if (effects.get(effectEvent) != null)
			effect = effects.get(effectEvent).clone();
		return effect;
	}

	/**
	 * Called by controls during construction to prepopulate effects based on
	 * Styles.
	 * 
	 * @param styleName
	 *            The String identifier of the Style
	 */
	protected void populateEffects(String styleName) {
		int index = 0;
		Effect effect;
		effects.clear();
		while ((effect = screen.getStyle(styleName).getEffect("event" + index)) != null) {
			effect = effect.clone();
			effect.setElement(this);
			this.addEffect(effect);
			index++;
		}
	}

	/**
	 * For internal use only - DO NOT CALL THIS METHOD
	 * 
	 * @param effect
	 *            Effect
	 * @param callHide
	 *            boolean
	 */
	public void propagateEffect(Effect effect, boolean callHide) {
		Effect nEffect = effect.clone();
		nEffect.setCallHide(callHide);
		nEffect.setElement(this);
		screen.getEffectManager().applyEffect(nEffect);
		for (Element el : elementChildren.values()) {
			el.propagateEffect(effect, false);
		}
	}

	// </editor-fold>

	// <editor-fold desc="Alpha">
	/**
	 * Overrides the screen global alpha with the specified value.
	 * setIngoreGlobalAlpha must be enabled prior to calling this method.
	 * 
	 * @param globalAlpha
	 */
	public float getGlobalAlpha() {
		return globalAlpha;
	}

	public void setGlobalAlpha(float globalAlpha) {
		this.globalAlpha = globalAlpha;
		updateGlobalAlpha();
	}

	/**
	 * Will enable or disable the use of the screen defined global alpha
	 * setting.
	 * 
	 * @param ignoreGlobalAlpha
	 */
	public void setIgnoreGlobalAlpha(boolean ignoreGlobalAlpha) {
		this.ignoreGlobalAlpha = ignoreGlobalAlpha;
	}

	// </editor-fold>

	// <editor-fold desc="Focus">
	/**
	 * For use by the Form control (Do not call this method directly)
	 * 
	 * @param form
	 *            The form the Element has been added to
	 */
	public void setForm(Form form) {
		this.form = form;
	}

	/**
	 * Returns the form the Element is controlled by
	 * 
	 * @return Form form
	 */
	public Form getForm() {
		return this.form;
	}

	/**
	 * Sets the tab index (This is assigned by the Form control. Do not call
	 * this method directly)
	 * 
	 * @param tabIndex
	 *            The tab index assigned to the Element
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * Returns the tab index assigned by the Form control
	 * 
	 * @return tabIndex
	 */
	public int getTabIndex() {
		return tabIndex;
	}

	/**
	 * For internal use - DO NOT CALL THIS METHOD
	 * 
	 * @param hasFocus
	 *            boolean
	 */
	public void setHasFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}

	/**
	 * Returns if the Element currently has input focus
	 * 
	 * @return
	 */
	public boolean getHasFocus() {
		return this.hasFocus;
	}

	public void setResetKeyboardFocus(boolean resetKeyboardFocus) {
		this.resetKeyboardFocus = resetKeyboardFocus;
	}

	public boolean getResetKeyboardFocus() {
		return this.resetKeyboardFocus;
	}

	// </editor-fold>

	// Off Screen Rendering Bridge
	public void addOSRBridge(OSRBridge bridge) {
		this.bridge = bridge;
		addControl(bridge);
		getElementMaterial().setTexture("ColorMap", bridge.getTexture());
		getElementMaterial().setColor("Color", ColorRGBA.White);
	}

	// <editor-fold desc="Tool Tips">
	/**
	 * Sets the Element's ToolTip text
	 * 
	 * @param toolTip
	 *            String
	 */
	public void setToolTipText(String toolTip) {
		this.toolTipText = toolTip;
	}

	/**
	 * Returns the Element's current ToolTip text
	 * 
	 * @return String
	 */
	public String getToolTipText() {
		return toolTipText;
	}

	// </editor-fold>

	// <editor-fold desc="Modal">
	/**
	 * Enables standard modal mode for the Element.
	 * 
	 * @param isModal
	 */
	public void setIsModal(boolean isModal) {
		this.isModal = isModal;
	}

	/**
	 * Returns if the Element is currently modal
	 * 
	 * @return Ret
	 */
	public boolean getIsModal() {
		return this.isModal;
	}

	/**
	 * For internal use - DO NOT CALL THIS METHOD
	 * 
	 * @param hasFocus
	 *            boolean
	 */
	public void setIsGlobalModal(boolean isGlobalModal) {
		this.isGlobalModal = isGlobalModal;
	}

	/**
	 * For internal use - DO NOT CALL THIS METHOD
	 * 
	 * @param hasFocus
	 *            boolean
	 */
	public boolean getIsGlobalModal() {
		return this.isGlobalModal;
	}

	// </editor-fold>

	// <editor-fold desc="User Data">
	/**
	 * Stores provided data with the Element
	 * 
	 * @param elementUserData
	 *            Object Data to store
	 */
	public void setElementUserData(Object elementUserData) {
		this.elementUserData = elementUserData;
	}

	/**
	 * Returns the data stored with this Element
	 * 
	 * @return Object
	 */
	public Object getElementUserData() {
		return this.elementUserData;
	}

	// </editor-fold>

	Vector2f origin = new Vector2f(0, 0);
	private boolean bringToFrontOnClick;
	private float zStep;

	/**
	 * Stubbed for future use
	 * 
	 * @param originX
	 * @param originY
	 */
	public void setOrigin(float originX, float originY) {
		origin.set(originX, originY);
	}

	/**
	 * Stubbed for future use.
	 * 
	 * @return
	 */
	public Vector2f getOrigin() {
		return this.origin;
	}

	// </editor-fold>

	// New z-ordering code

	public boolean isDescendantOf(Element parent) {
		Element par = elementParent;
		while (par != null) {
			if (par.equals(parent)) {
				return true;
			}
			par = par.elementParent;
		}
		return false;
	}

	// New z-ordering code
	public Element getRootElement() {
		Element par = this;
		while (true) {
			if (par.elementParent == null) {
				return par;
			}
			par = par.elementParent;
		}
	}

	public boolean getIsBringToFrontOnClick() {
		return bringToFrontOnClick;
	}

	public void setIsBringToFrontOnClick(boolean bringToFrontOnClick) {
		this.bringToFrontOnClick = bringToFrontOnClick;
	}

	public float getAlphaFactor() {
		return isEnabled ? 1 : 0.3f;
	}

	public boolean isAlwaysOnTop() {
		return isAlwaysOnTop;
	}

	public void setAlwaysOnTop(boolean isAlwaysOnTop) {
		this.isAlwaysOnTop = isAlwaysOnTop;
	}

	public void updateGlobalAlpha() {
		if (!ignoreGlobalAlpha) {
			if (mat != null)
				mat.setFloat("GlobalAlpha", globalAlpha * getAlphaFactor());
			if (textElement != null) {
				textElement.setAlpha(globalAlpha * getAlphaFactor());
			}
			for (Element el : elementChildren.values()) {
				el.setGlobalAlpha(globalAlpha);
			}
		} else {
			if (mat != null)
				mat.setFloat("GlobalAlpha", 1 * getAlphaFactor());
			if (textElement != null) {
				textElement.setAlpha(1 * getAlphaFactor());
			}
		}
	}

	protected void setVisibleState(boolean visibleState) {
		isVisible = visibleState;
	}

	protected void setLabelVisibility() {
		if (this.associatedLabel != null) {
			this.associatedLabel.setIsVisible(getIsVisible());
		}
	}

	public Vector2f getContainerDimensions() {
		return containerDimensions;
	}

	public void setContainerDimensions(Vector2f containerDimensions) {
		this.containerDimensions = containerDimensions;
	}

	public void setToolTipProvider(ToolTipProvider toolTipProvider) {
		this.toolTipProvider = toolTipProvider;
	}

	public ToolTipProvider getToolTipProvider() {
		return toolTipProvider;
	}

	public void setZStep(float zStep) {
		this.zStep = zStep;
	}

	public Vector4f getBounds() {
		return new Vector4f(position.x, LUtil.getY(this), dimensions.x, dimensions.y);
	}

	public void bind(String string, Object object) {
		// TODO Auto-generated method stub

	}
}
