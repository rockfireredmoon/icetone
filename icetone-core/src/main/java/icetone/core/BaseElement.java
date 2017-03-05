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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
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
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture2D;

import icetone.controls.extras.DragElement;
import icetone.core.Layout.LayoutType;
import icetone.core.event.ElementEvent;
import icetone.core.event.ElementEvent.Type;
import icetone.core.event.ElementEventListener;
import icetone.core.event.ElementEventSupport;
import icetone.core.event.KeyboardFocusEvent;
import icetone.core.event.KeyboardFocusEvent.KeyboardFocusEventType;
import icetone.core.event.KeyboardFocusListener;
import icetone.core.event.KeyboardFocusSupport;
import icetone.core.event.KeyboardSupport;
import icetone.core.event.KeyboardUIEvent;
import icetone.core.event.MouseButtonListener;
import icetone.core.event.MouseButtonSupport;
import icetone.core.event.MouseFocusSupport;
import icetone.core.event.MouseMovementListener;
import icetone.core.event.MouseMovementSupport;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.event.MouseUIFocusEvent;
import icetone.core.event.MouseUIFocusEvent.FocusEventType;
import icetone.core.event.MouseUIFocusListener;
import icetone.core.event.MouseUIWheelListener;
import icetone.core.event.MouseWheelSupport;
import icetone.core.event.UIKeyboardListener;
import icetone.core.layout.DefaultLayout;
import icetone.core.layout.loader.MouseButtonTarget;
import icetone.core.utils.MathUtil;
import icetone.css.CssEvent;
import icetone.css.StyleManager.CursorType;
import icetone.css.StyleManager.ThemeInstance;
import icetone.effects.Effect;
import icetone.effects.EffectChannel;
import icetone.effects.EffectFactory;
import icetone.framework.core.AnimText;
import icetone.framework.core.AnimText.TextStyle;

/**
 * <p>
 * The BaseElement class is the primitive in which all controls in the GUI
 * portion or the library are built upon.<br/>
 * <p>
 * Element is backed by a 9-patch style Mesh and can be both movable and
 * resizable by simply flagging them as such. There is no need to add Listeners
 * to leverage this default behavior.
 * 
 * @author t0neg0d
 */
public class BaseElement extends Node implements ElementContainer<BaseElement, BaseElement>, UIEventTarget {

	public enum TileMode {
		NONE, REPEAT, REPEAT_X, REPEAT_Y
	}

	public static boolean DISABLE_SIZE_CACHING = false;
	public static boolean ENABLE_CLIPPING = true;

	public final static Logger LOG = Logger.getLogger("Icetone");

	protected final static ThreadLocal<Integer> cloning = new ThreadLocal<>();

	private final static ThreadLocal<Boolean> reverseLayout = new ThreadLocal<>();
	private static int seq = 0;
	private final static Object seqLock = new Object();

	static {
		if (DISABLE_SIZE_CACHING) {
			System.err.println("************************************************************");
			System.err.println("* Warning: Size caching is disabled. This is only intended *");
			System.err.println("*          to be used as a debugging aid as it can harm    *");
			System.err.println("*          peformance.                                     *");
			System.err.println("************************************************************");
		}
		if (!ENABLE_CLIPPING) {
			System.err.println("*********************************************************");
			System.err.println("* Warning: Clipping is disabled. This is only intended  *");
			System.err.println("*          to be used as a debugging aid                *");
			System.err.println("*********************************************************");
		}
	}
	protected List<CssEvent> activeEvent = new LinkedList<>();
	protected float alpha = 1f;
	// TODO remove
	protected BaseElement associatedLabel;
	protected Vector4f atlas;
	protected Position backgroundPosition;
	protected Size backgroundSize = Size.FILL;
	protected Vector4f borderOffset = new Vector4f(0, 0, 0, 0);
	protected Vector4f borders = new Vector4f(0, 0, 0, 0);
	protected List<BaseElement> childList = new LinkedList<>();
	protected Vector4f clipPadding = new Vector4f(0, 0, 0, 0);
	protected Vector4f clippingBounds = new Vector4f();
	// New Clipping
	protected List<ClippingDefine> clippingLayers = new ArrayList<>();
	protected boolean configured;
	protected boolean containerOnly;
	protected CursorType cursor;
	protected ColorRGBA defaultColor = new ColorRGBA(1, 1, 1, 0);
	protected Texture defaultTex;
	protected boolean destroyOnHide;
	protected Set<LayoutType> dirty = new HashSet<>();
	protected boolean doInitialLayout = true;

	//
	// Private
	//

	protected Map<EffectChannel, EffectFactory> effects;
	protected float elementAlpha = 1f;

	protected ElementEventSupport<BaseElement> elementEventSupport;
	protected BaseElement elementParent = null;
	protected boolean focusRootOnly = true;

	protected BitmapFont font;
	protected ColorRGBA fontColor = null;
	protected String fontFamily;
	protected float fontSize = -1;
	protected float globalAlpha = 1f;
	protected Vector4f handlePosition = new Vector4f(10, 10, 10, 10);
	protected float indent;
	protected float intervalsPerSecond = 4;
	protected boolean isAlwaysOnTop;
	protected boolean isEnabled = true;
	protected boolean isMovable = false;
	protected boolean isVisible = true;
	protected boolean keyboardFocusable = false;
	protected boolean keyboardFocusRoot;
	protected KeyboardFocusSupport keyboardFocusSupport;
	protected KeyboardSupport keyboardSupport;
	protected String layoutData;
	protected Layout<?, ?> layoutManager = DefaultLayout.SHARED_INSTANCE;
	protected Vector4f margin = new Vector4f(0, 0, 0, 0);
	protected Size maxDimensions;
	protected Size minDimensions = null;
	protected MouseButtonSupport<BaseElement> mouseButtonSupport;
	protected boolean mouseFocusable = false;
	protected MouseFocusSupport mouseFocusSupport;
	protected MouseMovementSupport<BaseElement> mouseMovementSupport;
	protected MouseWheelSupport<BaseElement> mouseWheelSupport;
	protected Size prefDimensions;
	protected ZPriority priority = ZPriority.NORMAL;
	protected Vector2f scale = new Vector2f(1, 1);
	protected boolean scaled = false;
	protected BaseScreen screen;
	protected String styleId;
	protected String text;
	protected BitmapFont.Align textAlign = BitmapFont.Align.Left;
	protected AnimText textElement;
	protected Vector4f textPadding = new Vector4f(0, 0, 0, 0);
	protected Vector2f textPosition = new Vector2f(0, 0);
	protected float textRotation;
	protected Set<TextStyle> textStyles;
	protected BitmapFont.VAlign textVAlign = BitmapFont.VAlign.Center;

	protected LineWrapMode textWrap = LineWrapMode.Clip;
	protected TileMode tileMode = TileMode.NONE;
	protected boolean validated = true;
	protected boolean visibilityAllowed = true;
	protected MinFilter minFilter = Texture.MinFilter.BilinearNoMipMaps;
	protected MagFilter magFilter = Texture.MagFilter.Nearest;

	protected boolean wasVisible = true;
	Vector2f origin = new Vector2f(0, 0);
	float zOrder;
	private boolean adjusting;
	private boolean affectParent = false;
	private boolean affectZOrder = true;
	private Align align = Align.Left;
	private Texture alphaMap = null;
	private Texture bgMap = null;
	private ColorRGBA bgMapColor = ColorRGBA.Black;
	private boolean bringToFrontOnClick;
	private Vector2f cachedMax;
	private Vector2f cachedMin;
	private Vector2f cachedPref;
	private boolean clippingEnabled = true;
	private Object constraints;
	private Vector2f dimensions = new Vector2f();
	private Object elementUserData;
	private Form form;
	private Geometry geom;
	private boolean hasFocus = false;
	private boolean hasFocussedChild;
	//
	private boolean ignoreFling = true;
	private boolean ignoreGlobalAlpha = false;
	private boolean ignoreMouseLeftButton = true;
	private boolean ignoreMouseMovement = true;
	private boolean ignoreMouseRightButton = true;
	private boolean ignoreMouseWheelClick = true;
	private boolean ignoreMouseWheelMove = true;
	private boolean ignoreTouch = true;
	private boolean ignoreTouchMove = true;
	private boolean initialized = false;
	private boolean isDragElement = false, isDropElement = false;
	private boolean isModal = false;
	private boolean isResizable = false;
	private long layoutCounter;
	private float lineHeight;
	private boolean lockToParentBounds = false;
	private Material mat;
	private ElementQuadGrid model;
	private boolean parseTextTags = false;
	private Vector2f position = new Vector2f();
	private boolean resizeE = true;
	private boolean resizeN = true;
	private boolean resizeS = true;

	private boolean resizeW = true;
	private boolean styleIdIsGenerated;
	private int tabIndex = 0;
	private Vector4f textClipPadding = new Vector4f(0, 0, 0, 0);
	private boolean textOnTop;
	private String texturePath;
	protected ThemeInstance themeInstance;
	private ToolTipProvider toolTipProvider;
	private String toolTipText = null;

	private boolean useLocalAtlas = false;

	private VAlign valign = VAlign.Top;
	private float zStep;

	{
		if (cloning.get() == null)
			cloning.set(0);
	}

	public BaseElement() {
		this(BaseScreen.get());
	}

	public BaseElement(BaseScreen screen) {
		this(screen, null, null, null, null, null);
	}

	public BaseElement(BaseScreen screen, Layout<?, ?> layoutManager) {
		this(screen);
		setLayoutManager(layoutManager);
	}

	public BaseElement(BaseScreen screen, Size dimensions) {
		this(screen, null, Vector2f.ZERO, dimensions, null, null);
	}

	public BaseElement(BaseScreen screen, String texture) {
		this(screen);
		setTexture(texture);
	}

	public BaseElement(BaseScreen screen, String styleId, Size dimensions) {
		this(screen, styleId, Vector2f.ZERO, dimensions, null, null);
	}

	public BaseElement(BaseScreen screen, String styleId, Size dimensions, Vector4f resizeBorders, String texturePath) {
		this(screen, styleId, Vector2f.ZERO, dimensions, resizeBorders, texturePath);
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
	 * @param styleId
	 *            ID for CSS and element matching
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
	public BaseElement(BaseScreen screen, String styleId, Vector2f position, Size dimensions, Vector4f resizeBorders,
			String texturePath) {

		this.screen = screen;

		if (this.styleId == null) {
			if (styleId == null) {
				styleIdIsGenerated = true;
				synchronized (seqLock) {
					this.styleId = String.valueOf("-it-element-" + (++seq));
				}
			} else {
				this.styleId = styleId;
			}
		}

		setName(styleId);
		preConfigureElement();

		if (position != null)
			this.position.set(position);

		if (dimensions != null) {
			setPreferredDimensions(dimensions);
			this.dimensions.setX(dimensions.x).setY(dimensions.y);
		}

		if (resizeBorders != null)
			setResizeBorders(resizeBorders);

		checkMaterial(screen);

		if (texturePath != null) {
			setTexture(texturePath);
		} else
			createDefaultGeometry();

		this.setQueueBucket(Bucket.Gui);

		if (this instanceof ToolTipProvider)
			setToolTipProvider((ToolTipProvider) this);

		configureElement();
		configured = true;
		if (doInitialLayout)
			layoutChildren();
	}

	public BaseElement(BaseScreen screen, String styleId, Vector4f resizeBorders, String texturePath) {
		this(screen, styleId, Vector2f.ZERO, null, resizeBorders, texturePath);
	}

	public BaseElement(BaseScreen screen, Vector2f position, Size dimensions, Vector4f resizeBorders,
			String texturePath) {
		this(screen, null, position, dimensions, resizeBorders, texturePath);
	}

	public BaseElement(BaseScreen screen, Vector4f resizeBorders, String texturePath) {
		this(screen, null, resizeBorders, texturePath);
	}

	public BaseElement(Layout<?, ?> layoutManager) {
		this(BaseScreen.get(), layoutManager);
	}

	public BaseElement(String texture) {
		this(BaseScreen.get(), texture);
	}

	// New Clipping
	public BaseElement addClippingLayer(BaseElement el) {
		addClippingLayer(el, null);
		return this;
	}

	public BaseElement addClippingLayer(BaseElement el, Vector4f relativeClippingBounds) {
		ClippingDefine def = new ClippingDefine(el, relativeClippingBounds);
		propagateClippingLayerAdd(def);
		dirtyLayout(false, LayoutType.clipping);
		layoutChildren();
		return this;
	}

	/**
	 * Adds the specified Element as a child to this Element.
	 * 
	 * @param child
	 *            The Element to add as a child
	 */
	@Override
	public BaseElement addElement(BaseElement child) {
		addElement(child, null, false, -1);
		return this;
	}

	/**
	 * Adds the specified Element as a child to this Element.
	 * 
	 * @param child
	 *            The Element to add as a child
	 */
	@Override
	public BaseElement attachElement(BaseElement child) {
		addElement(child, null, true, -1);
		return this;
	}

	@Override
	public BaseElement addElement(BaseElement child, Object constraints) {
		addElement(child, constraints, false, -1);
		return this;
	}

	public BaseElement addElementListener(ElementEventListener<BaseElement> l) {
		if (elementEventSupport == null)
			elementEventSupport = new ElementEventSupport<BaseElement>();
		elementEventSupport.addListener(l);
		return this;
	}

	public BaseElement addFocusListener(MouseUIFocusListener<BaseElement> l) {
		if (mouseFocusSupport == null)
			mouseFocusSupport = new MouseFocusSupport();
		mouseFocusSupport.addListener(l);
		return this;
	}

	public BaseElement addKeyboardFocusListener(KeyboardFocusListener l) {
		if (keyboardFocusSupport == null)
			keyboardFocusSupport = new KeyboardFocusSupport();
		keyboardFocusSupport.addListener(l);
		return this;
	}

	public BaseElement addKeyboardListener(UIKeyboardListener l) {
		if (keyboardSupport == null)
			keyboardSupport = new KeyboardSupport();
		keyboardSupport.addListener(l);
		return this;
	}

	@Override
	public BaseElement addMouseButtonListener(MouseButtonListener<BaseElement> l) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport<BaseElement>();
		if (isContainerOnly())
			makeNonContainer();
		setIgnoreMouseButtons(false);
		mouseButtonSupport.addListener(l);
		return this;
	}

	@Override
	public BaseElement addMouseMovementListener(MouseMovementListener<BaseElement> l) {
		if (mouseMovementSupport == null)
			mouseMovementSupport = new MouseMovementSupport<BaseElement>();
		if (isContainerOnly())
			makeNonContainer();
		setIgnoreMouseMovement(false);
		mouseMovementSupport.addListener(l);
		return this;
	}

	@Override
	public BaseElement addMouseWheelListener(MouseUIWheelListener<BaseElement> l) {
		if (mouseWheelSupport == null)
			mouseWheelSupport = new MouseWheelSupport<BaseElement>();
		if (isContainerOnly())
			makeNonContainer();
		setIgnoreMouseWheelMove(false);
		mouseWheelSupport.addListener(l);
		return this;
	}

	// Off Screen Rendering Bridge
	public BaseElement addOSRBridge(OSRBridge bridge) {
		addControl(bridge);
		getElementMaterial().setTexture("ColorMap", bridge.getTexture());
		setElementMaterialColor(ColorRGBA.White);
		return this;
	}

	public BaseElement addTextStyles(TextStyle... textStyles) {
		if (this.textStyles == null)
			this.textStyles = new LinkedHashSet<>();
		for (TextStyle t : textStyles) {
			this.textStyles.add(t);
		}
		if (textElement != null) {
			textElement.setTextStyles(this.textStyles);
			dirtyLayout(false, LayoutType.text);
			layoutChildren();
		}
		return this;
	}

	@Override
	public void applyZOrder() {
		float zi = zStep / (childList.size() + 1 + (textElement != null ? 1 : 0));
		float z = zi;

		if (textElement != null && !textOnTop) {
			textElement.getLocalTranslation().z = z;
			z += zi;
		}

		// TODO Need to sort elements or just top levels?
		for (BaseElement e : getZSortedChildren()) {
			e.setZStep(zi);
			e.setLocalTranslation(e.getLocalTranslation().setZ(z));

			// TODO Dont think thiis is needed as layoutHeirarchy gets called at
			// every level
			e.applyZOrder();

			z += zi;
		}

		if (textElement != null && textOnTop)
			textElement.getLocalTranslation().z = z;
	}

	public void bind(Object target) {

		for (Method method : target.getClass().getMethods()) {
			final MouseButtonTarget annotation = method.getAnnotation(MouseButtonTarget.class);
			if (annotation != null) {
				String defaultId = method.getName();
				String id = annotation.id();
				if (id.equals(""))
					id = defaultId;

				BaseElement el = getElementByStyleId(id);
				if (el == null)
					throw new IllegalArgumentException(String.format("No element with style ID of %s", id));

				el.addMouseButtonListener(l -> {
					if ((annotation.button() == MouseUIButtonEvent.ALL || annotation.button() == l.getButtonIndex())
							&& ((annotation.pressed() && l.isPressed()) || (!annotation.pressed() && l.isReleased()))) {
						try {
							if (method.getParameterTypes().length == 0) {
								method.invoke(target);
								l.setConsumed();
							} else if (method.getParameterTypes().length == 1
									&& method.getParameterTypes()[0].equals(MouseUIButtonEvent.class)) {
								method.invoke(target, l);
							} else {
								throw new IllegalArgumentException(String.format(
										"Method %s must have either no arguments, or a single argument of type %s",
										method, MouseUIButtonEvent.class));
							}
						} catch (Exception e) {
							throw new RuntimeException("Failed to handle mouse button event.", e);
						}
					}
				});
			}
		}

	}

	public BaseElement bringToFront() {
		BaseElement elementParent = getElementParent();
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
		return this;
	}

	/**
	 * Calculate the maximum size of this element based on it's background
	 * image, text and child elements and text padding. The returned object is a
	 * new instance and is safe to manipulate. As an optimisation the size is
	 * also cached. This cache is invalidated by any change that would affects
	 * the bounds of the element (see {@link LayoutType#affectsBounds()}. You
	 * can force this by called {@link #dirtyLayout(boolean, LayoutType...)}
	 * with any of the types in {@link LayoutType#affectsBounds()}..
	 * 
	 * @return maximum size
	 */
	@SuppressWarnings("unchecked")
	public Vector2f calcMaximumSize() {

		if (cachedMax == null || DISABLE_SIZE_CACHING) {
			ElementContainer<?, ?> container = getParentContainer();
			cachedMax = new Vector2f(Short.MAX_VALUE, Short.MAX_VALUE);
			if (container != null) {
				Vector2f csz = container.getDimensions().clone().subtractLocal(container.getTotalPadding());
				Vector2f layoutMax = null;

				if (maxDimensions != null) {

					switch (maxDimensions.xUnit) {
					case AUTO:
						layoutMax = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).maximumSize(this);
						cachedMax.x = layoutMax.x;
						break;
					case PX:
						cachedMax.x = maxDimensions.x;
						break;
					case PERCENT:
						cachedMax.x = csz.x * (maxDimensions.x / 100f);
						break;
					default:
						throw new UnsupportedOperationException(
								String.format("Unit X type of %s is not supported.", maxDimensions.xUnit));
					}

					switch (maxDimensions.yUnit) {
					case AUTO:
						if (layoutMax == null)
							layoutMax = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).maximumSize(this);
						if (layoutMax != null)
							cachedMax.y = layoutMax.y;
						break;
					case PX:
						cachedMax.y = maxDimensions.y;
						break;
					case PERCENT:
						cachedMax.y = csz.y * (maxDimensions.y / 100f);
						break;
					default:
						throw new UnsupportedOperationException(
								String.format("Unit Y type of %s is not supported.", maxDimensions.yUnit));
					}
				} else {
					Vector2f lmax = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).maximumSize(this);
					if (lmax != null)
						cachedMax = lmax;
				}
			}
		}
		return cachedMax.clone();
	}

	/**
	 * Calculate the minimum size of this element based on it's background
	 * image, text and child elements and text padding. The returned object is a
	 * new instance and is safe to manipulate. As an optimisation the size is
	 * also cached. This cache is invalidated by any change that would affects
	 * the bounds of the element (see {@link LayoutType#affectsBounds()}. You
	 * can force this by called {@link #dirtyLayout(boolean, LayoutType...)}
	 * with any of the types in {@link LayoutType#affectsBounds()}..
	 * 
	 * @return minimum size
	 */
	@SuppressWarnings("unchecked")
	public Vector2f calcMinimumSize() {

		if (cachedMin == null || DISABLE_SIZE_CACHING) {
			cachedMin = new Vector2f();
			ElementContainer<?, ?> container = getParentContainer();
			if (container != null) {
				Vector2f csz = container.getDimensions().clone().subtractLocal(container.getTotalPadding());
				Vector2f layoutMin = null;

				if (minDimensions != null) {

					switch (minDimensions.xUnit) {
					case AUTO:
						layoutMin = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).minimumSize(this);
						if (layoutMin != null)
							cachedMin.x = layoutMin.x;
						break;
					case PX:
						cachedMin.x = minDimensions.x;
						break;
					case PERCENT:
						cachedMin.x = csz.x * (minDimensions.x / 100f);
						break;
					default:
						throw new UnsupportedOperationException(
								String.format("Unit X type of %s is not supported.", minDimensions.xUnit));
					}

					switch (minDimensions.yUnit) {
					case AUTO:
						if (layoutMin == null)
							layoutMin = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).minimumSize(this);
						if (layoutMin != null)
							cachedMin.y = layoutMin.y;
						break;
					case PX:
						cachedMin.y = minDimensions.y;
						break;
					case PERCENT:
						cachedMin.y = csz.y * (minDimensions.y / 100f);
						break;
					default:
						throw new UnsupportedOperationException(
								String.format("Unit Y type of %s is not supported.", minDimensions.yUnit));
					}
				} else {
					layoutMin = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).minimumSize(this);
					if (layoutMin != null)
						cachedMin = layoutMin;
				}
			}
		}
		// if(!cachedMin.equals(Vector2f.ZERO) && this instanceof StyledElement
		// && "will-charge-meter".equals(((StyledElement)this).getStyleClass()))
		return cachedMin.clone();

	}

	/**
	 * Calculate the preferred size of this element based on it's background
	 * image, text and child elements and text padding. The returned object is a
	 * new instance and is safe to manipulate. As an optimisation the size is
	 * also cached. This cache is invalidated by any change that would affects
	 * the bounds of the element (see {@link LayoutType#affectsBounds()}. You
	 * can force this by called {@link #dirtyLayout(boolean, LayoutType...)}
	 * with any of the types in {@link LayoutType#affectsBounds()}..
	 * 
	 * @return preferred size
	 */
	public Vector2f calcPreferredSize() {
		if (cachedPref == null || DISABLE_SIZE_CACHING) {
			Vector2f min = calcMinimumSize();
			Vector2f max = calcMaximumSize();
			Vector2f pref = calcUnboundedPreferredSize();
			cachedPref = MathUtil.clampSize(pref, min, max);
		}
		return cachedPref.clone();
	}

	@SuppressWarnings("unchecked")
	public Vector2f calcUnboundedPreferredSize() {
		Vector2f pref = null;
		ElementContainer<?, ?> container = getParentContainer();
		if (container != null) {
			Vector2f csz = container.getDimensions().clone().subtractLocal(container.getTotalPadding());
			Vector2f layoutMax = null;

			if (prefDimensions != null) {
				pref = new Vector2f();

				switch (prefDimensions.xUnit) {
				case AUTO:
					layoutMax = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).preferredSize(this);
					if (layoutMax != null)
						pref.x = layoutMax.x;
					break;
				case PX:
					pref.x = prefDimensions.x;
					break;
				case PERCENT:
					pref.x = csz.x * (prefDimensions.x / 100f);
					break;
				default:
					throw new UnsupportedOperationException(
							String.format("Unit X type of %s is not supported.", prefDimensions.xUnit));
				}

				switch (prefDimensions.yUnit) {
				case AUTO:
					if (layoutMax == null)
						layoutMax = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).preferredSize(this);
					if (layoutMax != null)
						pref.y = layoutMax.y;
					break;
				case PX:
					pref.y = prefDimensions.y;
					break;
				case PERCENT:
					pref.y = csz.y * (prefDimensions.y / 100f);
					break;
				default:
					throw new UnsupportedOperationException(
							String.format("Unit Y type of %s is not supported.", prefDimensions.yUnit));
				}
			} else {
				pref = ((Layout<ElementContainer<?, ?>, ?>) layoutManager).preferredSize(this);
			}
		}
		if (pref == null) {
			pref = Vector2f.ZERO;
		}
		return pref.clone();
	}

	/**
	 * Centers the Element to it's parent Element. If the parent element is
	 * null, it will use the screen's width/height.
	 */
	public BaseElement centerToParent() {
		if (elementParent == null) {
			setPosition(screen.getWidth() / 2 - (getWidth() / 2), screen.getHeight() / 2 - (getHeight() / 2));
		} else {
			setPosition(elementParent.getWidth() / 2 - (getWidth() / 2),
					elementParent.getHeight() / 2 - (getHeight() / 2));
		}
		return this;
	}

	public BaseElement centerToParentH() {
		if (elementParent == null) {
			setPosition(screen.getWidth() / 2 - (getWidth() / 2), getY());
		} else {
			setPosition(elementParent.getWidth() / 2 - (getWidth() / 2), getY());
		}
		return this;
	}

	public BaseElement centerToParentV() {
		if (elementParent == null) {
			setPosition(getX(), screen.getHeight() / 2 - (getHeight() / 2));
		} else {
			setPosition(getX(), elementParent.getHeight() / 2 - (getHeight() / 2));
		}
		return this;
	}

	/**
	 * For internal use. This method should never be called directly.
	 */
	public void childHide() {
		if (isVisible) {
			this.wasVisible = isVisible;
			this.isVisible = false;
			if (screen != null && screen.getToolTipManager() != null)
				screen.getToolTipManager().removeToolTipFor(this);

			// updateClipping();
			controlHideHook();
			for (BaseElement el : childList) {
				el.childHide();
			}
		}
	}

	// <editor-fold desc="Cleanup">
	public void cleanup() {
		screen.releaseModal(this);
		controlCleanupHook();
		for (BaseElement el : childList) {
			el.cleanup();
		}
	}

	@Override
	public BaseElement clone() {
		BaseElement el = new BaseElement(screen);
		configureClone(el);
		return el;
	}

	/**
	 * An overridable method for handling control specific cleanup.
	 */
	public void controlCleanupHook() {
	}

	/**
	 * An overridable method for extending the hide event.
	 */
	public void controlHideHook() {
	}

	public void controlIsEnabledHook(boolean isEnabled) {
	}

	/**
	 * Overridable method for extending the move event
	 */
	@Deprecated
	public void controlMoveHook() {

	}

	/**
	 * Overridable method for extending the resize event
	 */
	@Deprecated
	public void controlResizeHook() {

	}

	/**
	 * An overridable method for extending the show event.
	 */
	public void controlShowHook() {
	}

	public int countParents() {
		BaseElement e = elementParent;
		int i = 0;
		while (e != null) {
			i++;
			e = elementParent.getElementParent();
		}
		return i;
	}

	public void defocus() {
		if (isKeyboardFocussed())
			screen.resetKeyboardFocus(null);
	}

	public void destroy() {
		if (getParent() != null && isVisible())
			hide();
		if (getParentContainer() != null && getParentContainer().getElements().contains(this))
			getParentContainer().removeElement(this);
	}

	public boolean detachFromParent() {
		if (parent != null) {
			parent.detachChild(this);
			if (destroyOnHide) {
				destroy();
			}

			return true;
		}
		return false;
	}

	@Override
	public void dirtyLayout(boolean doChildren, LayoutType... layoutType) {

		boolean clearCache = false;
		if (layoutType.length == 0) {
			dirty.add(LayoutType.all);
			clearCache = true;
		} else {
			for (LayoutType t : layoutType) {
				if (t.affectsBounds()) {
					clearCache = true;
				}
				dirty.add(t);
			}
		}
		if (clearCache) {
			cachedMax = null;
			cachedPref = null;
			cachedMin = null;
		}

		// Normally each child should dirty it's own layout when it actually
		// changes (e.g. by parent layout manager)
		if (doChildren) {
			for (BaseElement e : childList)
				e.dirtyLayout(doChildren, layoutType);
		}
	}

	public void dirtyParent(boolean doChildren, LayoutType... layoutType) {
		ElementContainer<?, ?> par = getParentContainer();
		if (par != null) {
			par.dirtyLayout(doChildren, layoutType);
		}
	}

	public void focus() {
		if (!isKeyboardFocussed() && isKeyboardFocusable()) {
			screen.setKeyboardFocus(this);
		}
	}

	public String formatText(String text) {
		return text;
	}

	@Override
	public Vector2f getAbsolute() {
		return new Vector2f(getAbsoluteX(), getAbsoluteY());
	}

	/**
	 * Returns the height of an Element from screen y 0
	 * 
	 * @return float
	 */
	public float getAbsoluteHeight() {
		return getAbsoluteY() + getHeight();
	}

	/**
	 * Returns the top-most parent in the tree of Elements. The topmost element
	 * will always have a parent of null
	 * 
	 * @return Element elementParent
	 */
	public BaseElement getAbsoluteParent() {
		if (elementParent == null) {
			return this;
		} else {
			return elementParent.getAbsoluteParent();
		}
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
	 * Returns the x coord of an element from screen x 0, ignoring the nesting
	 * order.
	 * 
	 * @return float x
	 */
	@Override
	public float getAbsoluteX() {
		float x = getAlignedX();
		BaseElement el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			x += el.getAlignedX();
		}
		return x;
	}

	/**
	 * Returns the y coord of an element from screen y 0, ignoring the nesting
	 * order.
	 * 
	 * @return float
	 */
	@Override
	public float getAbsoluteY() {
		float y = getAlignedY();
		BaseElement el = this;
		while (el.getElementParent() != null) {
			el = el.getElementParent();
			y += el.getAlignedY();
		}
		return y;
	}

	public List<CssEvent> getActiveEvents() {
		return activeEvent;
	}

	public Align getAlign() {
		return align;
	}

	@Override
	public Vector4f getAllPadding() {
		return textPadding.add(margin);
	}

	public Texture getAlphaMap() {
		return this.alphaMap;
	}

	public float getAlphaValue() {
		return alpha * globalAlpha * elementAlpha;
	}

	/**
	 * Get the coords of the texture atlas used for this elements background
	 * image.
	 * 
	 * 
	 * @return
	 */
	public Vector4f getAtlasCoords() {
		return atlas;
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

	public Size getBackgroundDimensions() {
		return backgroundSize;
	}

	public Position getBackgroundPosition() {
		return backgroundPosition;
	}

	public Texture getBgMap() {
		return this.bgMap;
	}

	public ColorRGBA getBgMapColor() {
		return bgMapColor;
	}

	public Vector4f getBorderOffset() {
		return this.borderOffset;
	}

	public Vector4f getBounds() {
		return new Vector4f(position.x, position.y, dimensions.x, dimensions.y);
	}

	/**
	 * Recursively searches children elements for specified element containing
	 * the specified UID
	 * 
	 * @param UID
	 *            - Unique Indentifier of element to search for
	 * @return Element containing UID or null if not found
	 */
	@Deprecated
	public BaseElement getChildElementById(String UID) {
		return getElementByStyleId(UID);
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

	public ClippingDefine getClippingLayer(BaseElement el) {
		ClippingDefine def = null;

		for (ClippingDefine d : clippingLayers) {
			if (d.getElement() == el) {
				def = d;
				break;
			}
		}
		return def;
	}

	public List<ClippingDefine> getClippingLayers() {
		return Collections.unmodifiableList(clippingLayers);
	}

	public Object getConstraints() {
		return constraints;
	}

	@Override
	public CursorType getCursor() {
		return cursor;
	}

	public ColorRGBA getDefaultColor() {
		return defaultColor;
	}

	/**
	 * Returns a Vector2f containing the actual width and height of an Element
	 * 
	 * @return float
	 */
	@Override
	public Vector2f getDimensions() {
		return dimensions;
	}

	/**
	 * Returns a list of all children that are an instance of DragElement
	 * 
	 * @return List<Element>
	 */
	public List<BaseElement> getDraggableChildren() {
		List<BaseElement> ret = new ArrayList<>();
		for (BaseElement el : childList) {
			if (el instanceof DragElement) {
				ret.add(el);
			}
		}
		return ret;
	}

	public float getElementAlpha() {
		return elementAlpha;
	}

	public BaseElement getElementByStyleId(String styleId) {
		for (BaseElement el : childList) {
			if (styleId.equals(el.getStyleId()))
				return el;
			BaseElement ret = el.getElementByStyleId(styleId);
			if (ret != null)
				return ret;
		}
		return null;
	}

	/**
	 * Returns the child elements as a List
	 * 
	 * @return
	 */
	@Deprecated
	public List<BaseElement> getElementList() {
		return getElements();
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
	 * Returns the parent element of this node
	 * 
	 * @return Element elementParent
	 */
	public BaseElement getElementParent() {
		return elementParent;
	}

	/**
	 * Returns the child elements as a Collection
	 * 
	 * @return
	 */
	@Override
	public List<BaseElement> getElements() {
		return childList;
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
	 * Returns the data stored with this Element
	 * 
	 * @return Object
	 */
	public Object getElementUserData() {
		return this.elementUserData;
	}

	@Override
	public UIEventTarget getEventTargetParent() {
		return getElementParent();
	}

	public float getFixedLineHeight() {
		return lineHeight;
	}

	/**
	 * Returns the form the Element is controlled by
	 * 
	 * @return Form form
	 */
	@Override
	public FocusCycle getFocusCycle() {
		return getForm();
	}

	/**
	 * Returns the Bitmapfont used by the element's text layer
	 * 
	 * @return BitmapFont font
	 */
	@Override
	public BitmapFont getFont() {
		return this.font;
	}

	/**
	 * Return the element's text layer font color
	 * 
	 * @return ColorRGBA fontColor
	 */
	@Override
	public ColorRGBA getFontColor() {
		return fontColor;
	}

	@Override
	public String getFontFamily() {
		return fontFamily;
	}

	/**
	 * Returns the element's text layer font size
	 * 
	 * @return float fontSize
	 */
	@Override
	public float getFontSize() {
		return fontSize;
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
	 * Returns the Element's Geometry.
	 * 
	 * @return
	 */
	public Geometry getGeometry() {
		return this.geom;
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

	public Vector4f getHandlePosition() {
		return handlePosition;
	}

	/**
	 * Returns if the Element currently has mouse focus (i.e hover)
	 * 
	 * @return
	 */
	public boolean isFocussed() {
		return this.hasFocus;
	}

	/**
	 * Returns if the Element currently has keyboard focus
	 * 
	 * @return
	 */
	public boolean isKeyboardFocussed() {
		return screen != null && screen.getKeyboardFocus() == this;
	}

	public boolean isKeyboardFocusableChild() {
		for (BaseElement e : childList) {
			if (e.isKeyboardFocusable() || e.isKeyboardFocusableChild())
				return true;
		}
		return false;
	}

	public boolean isKeyboardFocussedChild() {
		return !focusRootOnly && hasFocussedChild;
	}

	public boolean isKeyboardFocussedParent() {
		return !focusRootOnly && (isKeyboardFocussed()
				|| (elementParent != null && !isKeyboardFocusRoot() && elementParent.isKeyboardFocussedParent()));
	}

	// </editor-fold>

	/**
	 * Returns the actual height of an Element
	 * 
	 * @return float
	 */
	@Override
	public float getHeight() {
		return dimensions.y;
	}

	/**
	 * Returns if the element ignores fling events
	 * 
	 * @return
	 */
	public boolean isIgnoreFling() {
		return this.ignoreFling;
	}

	/**
	 * Returns true if all mouse events are being ignored
	 * 
	 * @return
	 */
	public boolean isIgnoreMouse() {
		return isIgnoreMouseMovement() && isIgnoreMouseButtons() && isIgnoreMouseWheel() && isIgnoreTouchEvents();
	}

	/**
	 * Returns true if both left and right mouse buttons are being ignored
	 * 
	 * @return
	 */
	public boolean isIgnoreMouseButtons() {
		return (isIgnoreMouseLeftButton() && isIgnoreMouseRightButton());
	}

	/**
	 * Returns if the left mouse button is being ignored
	 * 
	 * @return
	 */
	@Override
	public boolean isIgnoreMouseLeftButton() {
		return this.ignoreMouseLeftButton;
	}

	/**
	 * Returns if the element is set to ignore mouse events
	 * 
	 * @return boolean ignoreMouse
	 */
	@Override
	public boolean isIgnoreMouseMovement() {
		return this.ignoreMouseMovement;
	}

	/**
	 * Returns if the right mouse button is being ignored
	 * 
	 * @return
	 */
	@Override
	public boolean isIgnoreMouseRightButton() {
		return this.ignoreMouseRightButton;
	}

	/**
	 * Returns if the element is ignoring both mouse wheel click and move events
	 * 
	 * @return
	 */
	public boolean isIgnoreMouseWheel() {
		return (isIgnoreMouseWheelClick() && isIgnoreMouseWheelMove());
	}

	/**
	 * Returns if the element ignores mouse wheel clicks
	 * 
	 * @return
	 */
	public boolean isIgnoreMouseWheelClick() {
		return this.ignoreMouseWheelClick;
	}

	/**
	 * Returns if the element is ignoring mouse wheel moves
	 * 
	 * @return
	 */
	public boolean isIgnoreMouseWheelMove() {
		return this.ignoreMouseWheelMove;
	}

	/**
	 * Returns if the element ignores touch down and up events
	 * 
	 * @return
	 */
	public boolean isIgnoreTouch() {
		return ignoreTouch;
	}

	/**
	 * Returns if the element ignores touch down, up, move & fling events
	 * 
	 * @return
	 */
	public boolean isIgnoreTouchEvents() {
		return (isIgnoreTouch() && isIgnoreTouchMove() && isIgnoreFling());
	}

	/**
	 * Returns if the element ignores touch move events
	 * 
	 * @return
	 */
	public boolean isIgnoreTouchMove() {
		return this.ignoreTouchMove;
	}

	/**
	 * Get the generic indent value. If and how this is used depends on the
	 * component. A basic {@link BaseElement} does not use it..
	 * 
	 * @return indent
	 */
	@Override
	public float getIndent() {
		return indent;
	}

	/**
	 * Stubbed for future use.
	 */
	public boolean isInitialized() {
		return this.initialized;
	}

	public boolean isBringToFrontOnClick() {
		return bringToFrontOnClick;
	}

	/**
	 * Returns if the Element's clipping layer has been set
	 * 
	 * @return boolean isClipped
	 */
	public boolean isClipped() {
		return isVisible() && isClippingEnabled() && !clippingLayers.isEmpty();
	}

	public boolean isClippingEnabled() {
		return clippingEnabled;
	}

	public boolean isContainerOnly() {
		return containerOnly;
	}

	/**
	 * Returns if the Element is currently flagged as a Drag Element for Drag &
	 * Drop interaction
	 * 
	 * @return boolean
	 */
	public boolean isDragDropDragElement() {
		return this.isDragElement;
	}

	/**
	 * Returns if the Element is currently flagged as a Drop Element for Drag &
	 * Drop interaction
	 * 
	 * @return boolean
	 */
	public boolean isDragDropDropElement() {
		return this.isDropElement;
	}

	/**
	 * Returns if the element is currently enabled
	 * 
	 * @return boolean
	 */
	public boolean isEnabled() {
		return this.isEnabled;
	}

	/**
	 * Returns if the Element is currently modal
	 * 
	 * @return modal
	 */
	public boolean isModal() {
		return this.isModal;
	}

	/**
	 * Returns if the element has draggable behavior set
	 * 
	 * @return boolean isMovable
	 */
	public boolean isMovable() {
		return this.isMovable;
	}

	/**
	 * Returns if the element has resize behavior set
	 * 
	 * @return boolean isResizable
	 */
	public boolean isResizable() {
		return this.isResizable;
	}

	/**
	 * Return if the Element is visible. Note, visible does not necessarily mean
	 * the control is actual displayed on the screen. By default, elements are
	 * visible, but will not appear on the screen until they are added to the
	 * parent. To determine if the element is actually displayed, use
	 * {@link #isShowing()}.
	 * 
	 * @return boolean isVisible
	 */
	public boolean isVisible() {
		return this.isVisible;
	}

	public BaseElement getKeyboardFocusParent() {
		return isKeyboardFocusable() ? this : (elementParent != null ? elementParent.getKeyboardFocusParent() : null);
	}

	@Override
	public long getLayoutCounter() {
		return layoutCounter;
	}

	/**
	 * Gets the element's text layer horizontal alignment. Some layout managers
	 * may look in here for CSS provided last constraints.
	 * 
	 * @return data
	 */
	public String getLayoutData() {
		return layoutData;
	}

	@Override
	public Layout<?, ?> getLayoutManager() {
		return layoutManager;
	}

	public float getLocalAlpha() {
		return alpha;
	}

	/**
	 * Returns if the Element has been constrained to it's parent Element's
	 * dimensions.
	 * 
	 * @return boolean lockToParentBounds
	 */
	public boolean isLockToParentBounds() {
		return this.lockToParentBounds;
	}

	@Override
	/**
	 * Get margin. The margin is ordered differently to resize borders. x is the
	 * left margin, y is the right, z is top and w is the bottom.
	 * 
	 * @return text padding
	 */
	public Vector4f getMargin() {
		return this.margin;
	}

	/**
	 * Returns the default material for the element
	 * 
	 * @return Material mat
	 */
	public Material getMaterial() {
		return this.mat;
	}

	public Size getMaxDimensions() {
		return maxDimensions;
	}

	public Size getMinDimensions() {
		return minDimensions;
	}

	// <editor-fold desc="Mesh & Geometry">
	/**
	 * Returns a pointer to the custom mesh used to render the Element.
	 * 
	 * @return ElementGridQuad model
	 */
	public ElementQuadGrid getModel() {
		return this.model;
	}

	@Override
	public MouseButtonSupport<BaseElement> getMouseButtonSupport() {
		return mouseButtonSupport;
	}

	@Override
	public MouseMovementSupport<BaseElement> getMouseMovementSupport() {
		return mouseMovementSupport;
	}

	public Vector2f getOrigin() {
		return origin;
	}

	/**
	 * Get a parent of a given class.
	 * 
	 * @param clazz
	 *            class
	 * @return parent of null if not found
	 */
	public BaseElement getParent(Class<? extends BaseElement> clazz) {
		if (clazz.isAssignableFrom(getClass())) {
			return this;
		} else if (elementParent != null)
			return elementParent.getParent(clazz);
		else
			return null;
	}

	@Override
	public ElementContainer<?, ?> getParentContainer() {
		return elementParent == null ? screen : elementParent;
	}

	/**
	 * Returns the current screen location of the Element
	 * 
	 * @return Vector2f position
	 */
	@Override
	public Vector2f getPosition() {
		return position;
	}

	public Size getPreferredDimensions() {
		return prefDimensions;
	}

	public ZPriority getPriority() {
		return priority;
	}

	/**
	 * Get the resize borders. The x,y,z,w attributes are arranged as left,
	 * right, top, bottom borders.
	 * 
	 * @return borders
	 */
	public Vector4f getResizeBorders() {
		return borders;
	}

	/**
	 * Returns whether the elements east border has enabled/disabled resizing
	 * 
	 * @return boolean resizeE
	 */
	public boolean isResizeE() {
		return this.resizeE;
	}

	/**
	 * Returns whether the elements north border has enabled/disabled resizing
	 * 
	 * @return boolean resizeN
	 */
	public boolean isResizeN() {
		return this.resizeS;
	}

	/**
	 * Returns whether the elements south border has enabled/disabled resizing
	 * 
	 * @return boolean resizeS
	 */
	public boolean isResizeS() {
		return this.resizeN;
	}

	/**
	 * Returns whether the elements west border has enabled/disabled resizing
	 * 
	 * @return boolean resizeW
	 */
	public boolean isResizeW() {
		return this.resizeW;
	}

	// New z-ordering code
	public BaseElement getRootElement() {
		BaseElement par = this;
		while (true) {
			if (par.elementParent == null) {
				return par;
			}
			par = par.elementParent;
		}
	}

	public Vector2f getScale() {
		return scale;
	}

	/**
	 * Returns the one and only Element's screen
	 * 
	 * @return
	 */
	@Override
	public BaseScreen getScreen() {
		return this.screen;
	}

	public String getStyleId() {
		return styleId;
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
	 * Retuns the current visible text of the element.
	 * 
	 * @return String text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Returns the element's text layer horizontal alignment
	 * 
	 * @return Align text Align
	 */
	public BitmapFont.Align getTextAlign() {
		return textAlign;
	}

	public Vector4f getTextClipPaddingVec() {
		return textClipPadding;
	}

	/**
	 * Returns a pointer to the BitmapText element of this Element. Returns null
	 * if setText() has not been called.
	 * 
	 * @return BitmapText textElement
	 */
	public AnimText getTextElement() {
		return this.textElement;
	}

	/**
	 * Get text padding. The padding is ordered differently to resize borders. x
	 * is the left padding, y is the right, z is top and w is the bottom.
	 * 
	 * @return text padding
	 */
	@Override
	public Vector4f getTextPadding() {
		return this.textPadding;
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
	 * Sets the element's text rotation. This will affect the preferred bounds
	 * of the element. For example, rotating 90 degrees would flip the preferred
	 * width and height.
	 * <p>
	 * Note, currently 90, 180, 270 and 0 degrees will produce the correct
	 * dimensions. Artbitrary rotations are not supported.
	 * 
	 * 
	 * @param textAlign
	 *            rotation in radians..
	 */
	public float getTextRotation() {
		return textRotation;
	}

	/**
	 * Returns the element's text layer vertical alignment
	 * 
	 * @return VAlign textVAlign
	 */
	public BitmapFont.VAlign getTextVAlign() {
		return textVAlign;
	}

	/**
	 * Returns the element's text layer wrap mode
	 * 
	 * @return LineWrapMode textWrap
	 */
	public LineWrapMode getTextWrap() {
		return textWrap;
	}

	// </editor-fold>

	@Override
	public ThemeInstance getThemeInstance() {
		if (this.themeInstance != null)
			return themeInstance;
		ElementContainer<?, ?> p = getParentContainer();
		if (p == null)
			return ToolKit.get().getStyleManager().getDefaultInstance();
		else
			return p.getThemeInstance();
	}

	public TileMode getTileMode() {
		return tileMode;
	}

	public ToolTipProvider getToolTipProvider() {
		return toolTipProvider;
	}

	/**
	 * Returns the Element's current ToolTip text
	 * 
	 * @return String
	 */
	public String getToolTipText() {
		return toolTipText;
	}

	@Override
	public Vector2f getTotalPadding() {
		Vector4f totPad = getAllPadding();
		return new Vector2f(totPad.x + totPad.y, totPad.z + totPad.w);
	}

	@Override
	public Vector2f getTotalPaddingOffset() {
		return new Vector2f(textPadding.x + margin.x, textPadding.y + margin.y);
	}

	/**
	 * Returns the element's unique string identifier
	 * 
	 * @return String UID
	 */
	@Deprecated
	public String getUID() {
		return styleId;
	}

	/**
	 * Returns if the element is using a local texture atlas of the screen
	 * defined texture atlas
	 * 
	 * @return
	 */
	public boolean isUseLocalAtlas() {
		return this.useLocalAtlas;
	}

	public VAlign getValign() {
		return valign;
	}

	/**
	 * Returns the actual width of an Element
	 * 
	 * @return float
	 */
	@Override
	public float getWidth() {
		return dimensions.x;
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

	public float getZOrder() {
		return zOrder;
	}

	public boolean hasClippingLayer(BaseElement el) {
		for (ClippingDefine d : clippingLayers) {
			if (d.getElement() == el) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Recursive call that sets this Element and any Element contained within
	 * it's nesting order to hidden.
	 * 
	 * NOTE: Hide and Show relies on shader-based clipping
	 */
	public BaseElement hide() {
		hide(CssEvent.HIDE, LayoutType.clipping, LayoutType.styling);
		return this;
	}

	public BaseElement insertChild(BaseElement el, Object constraints, boolean hide, int index) {
		addElement(el, constraints, hide, -1);
		return this;
	}

	public BaseElement insertChild(BaseElement el, Object constraints, int index) {
		addElement(el, constraints, false, -1);
		return this;
	}

	public void invalidate() {
		if (!validated)
			throw new IllegalStateException("Not validated.");
		validated = false;
	}

	public boolean isAdjusting() {
		return adjusting;
	}

	/**
	 * Returns if the Element is set to pass events to it's direct parent
	 * 
	 * @return boolean effectParent
	 */
	public boolean isAffectParent() {
		return this.affectParent;
	}

	public boolean isAffectZOrder() {
		return this.affectZOrder;
	}

	// </editor-fold>

	public boolean isAlwaysOnTop() {
		return isAlwaysOnTop;
	}

	public boolean isAtlasTextureInUse() {
		return ((screen != null && screen.getUseTextureAtlas()) || isUseLocalAtlas()) && atlas != null
				&& !atlas.equals(Vector4f.ZERO);
	}

	public boolean isDescendantOf(BaseElement parent) {
		BaseElement par = elementParent;
		while (par != null) {
			if (par.equals(parent)) {
				return true;
			}
			par = par.elementParent;
		}
		return false;
	}

	public boolean isDestroyOnHide() {
		return destroyOnHide;
	}

	@Deprecated
	public boolean isFlipY() {
		return false;
	}

	public boolean isFocusRootOnly() {
		return focusRootOnly;
	}

	public boolean isKeyboardFocusable() {
		return keyboardFocusable;
	}

	public boolean isKeyboardFocusableInHierarchy() {
		return isKeyboardFocusable() || (elementParent != null && elementParent.isKeyboardFocusableInHierarchy());
	}

	/**
	 * Gets whether this element is the 'Keyboard Focus Root'.
	 * 
	 * @return keyboard focus root
	 * @see #setKeyboardFocusRoot(boolean)
	 */
	@Override
	public boolean isKeyboardFocusRoot() {
		return keyboardFocusRoot;
	}

	public boolean isMouseFocusable() {
		return mouseFocusable;
	}

	public boolean isParseTextTags() {
		return parseTextTags;
	}

	public boolean isScaled() {
		return scaled;
	}

	/**
	 * Get if the element is actually visible on the screen.
	 * 
	 * @return showing
	 */
	public boolean isShowing() {
		return getParent() != null;
	}

	public boolean isTextOnTop() {
		return textOnTop;
	}

	public boolean isUseColorMapForSizeCalculations() {
		return Size.AUTO.equals(backgroundSize);
	}

	@Override
	public boolean isVisibilityAllowed() {
		return visibilityAllowed;
	}

	@Override
	public final void layoutChildren() {
		if (configured && isValidated()) {
			doLayoutChildren();
		} else
			applyZOrder();
	}

	public void lockToParentBounds(float x, float y) {
		Vector4f v = new Vector4f(x, y, dimensions.x, dimensions.y);
		ElementContainer<?, ?> container = getParentContainer();
		if (container != null) {
			Vector4f parentPadding = container.getAllPadding();

			// If the element is outside of the parent to the top or to the
			// left,
			// move both to the bound.
			if (v.x < parentPadding.x) {
				v.x = parentPadding.x;
			}

			// If this element now drops off the right of the container, move it
			// left till it fits (or overlaps the left)
			if (v.x + v.z > container.getWidth() - parentPadding.x) {
				v.x = container.getWidth() - v.z - parentPadding.x;
			}

			// The element is again off the left, now it needs to be shrunk
			if (v.x < parentPadding.x) {
				v.x = parentPadding.x;
				v.z = container.getWidth() - parentPadding.x - parentPadding.y;
			}

			// If the element is outside of the top of parent, move to the
			// parents
			// content edge.
			if (v.y < parentPadding.z) {
				v.y = parentPadding.z;
			}

			// If this element now drops off the bottom of the container, move
			// it up
			// till it fits (or overlaps the top)
			if (v.y + v.w > container.getHeight() - parentPadding.z) {
				v.y = container.getHeight() - v.w - parentPadding.z;
			}

			// The element is again off the top, now it needs to be shrunk
			if (v.y < parentPadding.z) {
				v.y = parentPadding.z;
				v.w = container.getHeight() - parentPadding.z - parentPadding.w;
			}
		}

		setBounds(v);
	}

	/**
	 * An overridable method for extending the bring to front event.
	 */
	public void movedToFrontHook() {
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
		if (isLockToParentBounds()) {
			lockToParentBounds(x, y);
		} else {
			setPosition(x, y);
		}
	}

	public BaseElement onElementEvent(ElementEventListener<BaseElement> l, Type type) {
		if (elementEventSupport == null)
			elementEventSupport = new ElementEventSupport<BaseElement>();
		elementEventSupport.bind(l, type);
		return this;
	}

	public BaseElement onFocus(MouseUIFocusListener<BaseElement> l) {
		if (mouseFocusSupport == null) {
			mouseFocusSupport = new MouseFocusSupport();
		}
		setMouseFocusable(true);
		mouseFocusSupport.bind(l);
		return this;
	}

	public BaseElement onFocusGained(MouseUIFocusListener<BaseElement> l) {
		if (mouseFocusSupport == null) {
			mouseFocusSupport = new MouseFocusSupport();
		}
		setMouseFocusable(true);
		mouseFocusSupport.bind(l, FocusEventType.gained);
		return this;
	}

	public BaseElement onFocusLost(MouseUIFocusListener<BaseElement> l) {
		if (mouseFocusSupport == null) {
			mouseFocusSupport = new MouseFocusSupport();
		}
		setMouseFocusable(true);
		mouseFocusSupport.bind(l, FocusEventType.lost);
		return this;
	}

	public BaseElement onKeyboard(UIKeyboardListener l) {
		if (keyboardSupport == null) {
			keyboardSupport = new KeyboardSupport();
		}
		setKeyboardFocusable(true);
		keyboardSupport.bind(l);
		return this;
	}

	public BaseElement onKeyboardFocus(KeyboardFocusListener l) {
		if (keyboardFocusSupport == null) {
			keyboardFocusSupport = new KeyboardFocusSupport();
		}
		setMouseFocusable(true);
		keyboardFocusSupport.bind(l);
		return this;
	}

	public BaseElement onKeyboardFocusGained(KeyboardFocusListener l) {
		if (keyboardFocusSupport == null) {
			keyboardFocusSupport = new KeyboardFocusSupport();
		}
		setKeyboardFocusable(true);
		keyboardFocusSupport.bind(l, KeyboardFocusEventType.gained);
		return this;
	}

	public BaseElement onKeyboardFocusLost(KeyboardFocusListener l) {
		if (keyboardFocusSupport == null) {
			keyboardFocusSupport = new KeyboardFocusSupport();
		}
		setKeyboardFocusable(true);
		keyboardFocusSupport.bind(l, KeyboardFocusEventType.lost);
		return this;
	}

	public BaseElement onKeyboardPressed(UIKeyboardListener l) {
		if (keyboardSupport == null) {
			keyboardSupport = new KeyboardSupport();
		}
		setKeyboardFocusable(true);
		keyboardSupport.bindPressed(l);
		return this;
	}

	public BaseElement onKeyboardReleased(UIKeyboardListener l) {
		if (keyboardSupport == null) {
			keyboardSupport = new KeyboardSupport();
		}
		setKeyboardFocusable(true);
		keyboardSupport.bindReleased(l);
		return this;
	}

	@Override
	public BaseElement onMouseMoved(MouseMovementListener<BaseElement> l) {
		if (mouseMovementSupport == null)
			mouseMovementSupport = new MouseMovementSupport<BaseElement>();
		if (isContainerOnly())
			makeNonContainer();
		setIgnoreMouseMovement(false);
		mouseMovementSupport.bind(l);
		return this;
	}

	@Override
	public BaseElement onMousePressed(MouseButtonListener<BaseElement> l) {
		onMousePressed(l, MouseUIButtonEvent.LEFT);
		return this;
	}

	@Override
	public BaseElement onMousePressed(MouseButtonListener<BaseElement> l, int button) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport<BaseElement>();
		if (isContainerOnly())
			makeNonContainer();
		setIgnoreMouseButtons(false);
		mouseButtonSupport.bindPressed(l, button);
		return this;
	}

	@Override
	public BaseElement onMouseReleased(MouseButtonListener<BaseElement> l) {
		onMouseReleased(l, MouseUIButtonEvent.LEFT);
		return this;
	}

	@Override
	public BaseElement onMouseReleased(MouseButtonListener<BaseElement> l, int button) {
		if (mouseButtonSupport == null)
			mouseButtonSupport = new MouseButtonSupport<BaseElement>();
		setIgnoreMouseButtons(false);
		if (isContainerOnly())
			makeNonContainer();
		mouseButtonSupport.bindReleased(l, button);
		return this;
	}

	@Override
	public BaseElement onMouseWheel(MouseUIWheelListener<BaseElement> l) {
		if (mouseWheelSupport == null)
			mouseWheelSupport = new MouseWheelSupport<BaseElement>();
		if (isContainerOnly())
			makeNonContainer();
		setIgnoreMouseWheelMove(false);
		mouseWheelSupport.bind(l);
		return this;
	}

	/**
	 * Remove all child Elements from this Element
	 */
	public BaseElement removeAllChildren() {
		if (!childList.isEmpty()) {
			for (BaseElement e : childList) {
				e.detachFromParent();
				e.removeClipForElement(this);
				for (ClippingDefine def : clippingLayers)
					e.removeClipForElement(def.getElement());
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

	public BaseElement removeClippingLayer(BaseElement el) {
		removeClippingLayer(el, true);
		return this;
	}

	public BaseElement removeClippingLayer(BaseElement el, boolean layout) {
		removeClipForElement(el);
		dirtyLayout(false, LayoutType.clipping);
		if (layout)
			layoutChildren();
		return this;
	}

	/**
	 * Removes the specified Element
	 * 
	 * @param child
	 *            Element to remove
	 */
	@Override
	public BaseElement removeElement(BaseElement child) {
		dirtyLayout(false, LayoutType.boundsChange());
		if (childList.remove(child)) {
			child.elementParent = null;
			child.detachFromParent();
			child.removeClipForElement(this);
			for (ClippingDefine def : clippingLayers)
				child.removeClipForElement(def.getElement());
			child.cleanup();
			if (screen.getToolTipManager() != null)
				screen.getToolTipManager().removeToolTipFor(this);
		}

		layoutManager.remove(child);
		layoutChildren();
		return this;
	}

	public BaseElement removeElementEventListener(ElementEventListener<BaseElement> l) {
		if (elementEventSupport != null)
			elementEventSupport.removeListener(l);
		return this;
	}

	public BaseElement removeFocusListener(MouseUIFocusListener<BaseElement> l) {
		if (mouseFocusSupport != null)
			mouseFocusSupport.removeListener(l);
		return this;
	}

	@Override
	public boolean removeFromParent() {
		BaseElement el = getElementParent();
		if (el != null) {
			el.removeElement(this);
			return true;
		} else if (screen.getElements().contains(this)) {
			screen.removeElement(this);
			return true;
		} else
			return super.removeFromParent();
	}

	public BaseElement removeKeyboardFocusListener(KeyboardFocusListener l) {
		if (keyboardFocusSupport != null)
			keyboardFocusSupport.removeListener(l);
		return this;
	}

	public BaseElement removeKeyboardListener(UIKeyboardListener l) {
		if (keyboardSupport != null)
			keyboardSupport.removeListener(l);
		return this;
	}

	@Override
	public BaseElement removeMouseButtonListener(MouseButtonListener<BaseElement> l) {
		if (mouseButtonSupport != null)
			mouseButtonSupport.removeListener(l);
		return this;
	}

	@Override
	public BaseElement removeMouseMovementListener(MouseMovementListener<BaseElement> l) {
		if (mouseMovementSupport != null)
			mouseMovementSupport.removeListener(l);
		return this;
	}

	@Override
	public BaseElement removeMouseWheelListener(MouseUIWheelListener<BaseElement> l) {
		if (mouseWheelSupport != null)
			mouseWheelSupport.removeListener(l);
		return this;
	}

	public void removeTextElement() {
		if (textElement != null) {
			textElement.removeFromParent();
			textElement = null;
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
	}

	// <editor-fold desc="Resize & Move">

	public void runAdjusting(Runnable r) {
		if (adjusting) {
			throw new IllegalStateException("Already adjusting.");
		}
		adjusting = true;
		try {
			r.run();
		} finally {
			adjusting = false;
		}
	}

	public MinFilter getMinFilter() {
		return minFilter;
	}

	public BaseElement setMinFilter(MinFilter minFilter) {
		if (!Objects.equals(minFilter, this.minFilter)) {
			this.minFilter = minFilter;
			if(defaultTex != null) {
				defaultTex.setMinFilter(minFilter);
			}			
		}
		return this;
	}

	public MagFilter getMagFilter() {
		return magFilter;
	}

	public BaseElement setMagFilter(MagFilter magFilter) {
		if (!Objects.equals(magFilter, this.magFilter)) {
			this.magFilter = magFilter;
			if(defaultTex != null) {
				defaultTex.setMagFilter(magFilter);
			}			
		}
		return this;
	}

	/**
	 * Sets the element to pass certain events (movement, resizing) to it direct
	 * parent instead of effecting itself.
	 * 
	 * @param affectParent
	 *            boolean
	 */
	public BaseElement setAffectParent(boolean affectParent) {
		this.affectParent = affectParent;
		return this;
	}

	public BaseElement setAffectZOrder(boolean affectZOrder) {
		this.affectZOrder = affectZOrder;
		return this;
	}

	public BaseElement setAlign(Align align) {
		if (!Objects.equals(align, this.align)) {
			this.align = align;
			updateNodeLocation();
		}
		return this;
	}

	/**
	 * Adds an alpha map to the Elements material
	 * 
	 * @param alphaMap
	 *            A String path to the alpha map
	 */
	public BaseElement setAlphaMap(String alphaMap) {
		Texture alpha = null;
		if (isAtlasTextureInUse()) {
			if (this.getElementTexture() != null)
				alpha = getElementTexture();
			else
				alpha = screen.getAtlasTexture();

			// TODO
			// Vector2f alphaOffset =
			// getAtlasTextureOffset(screen.parseAtlasCoords(alphaMap));
			// mat.setVector2("OffsetAlphaTexCoord", alphaOffset);
			throw new UnsupportedOperationException();
		} else {
			alpha = ToolKit.get().getApplication().getAssetManager().loadTexture(alphaMap);
			alpha.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			alpha.setMagFilter(Texture.MagFilter.Nearest);
			alpha.setWrap(Texture.WrapMode.Clamp);
		}

		this.alphaMap = alpha;

		if (defaultTex == null) {
			if (!isAtlasTextureInUse()) {
				float imgWidth = alpha.getImage().getWidth();
				float imgHeight = alpha.getImage().getHeight();
				float pixelWidth = 1f / imgWidth;
				float pixelHeight = 1f / imgHeight;

				this.model = new ElementQuadGrid(dimensions.clone(), calcBorders(), imgWidth, imgHeight, pixelWidth,
						pixelHeight, 0, 0, imgWidth, imgHeight);

				geom.setMesh(model);
			} else {
				// TODO
				// float[] coords = screen.parseAtlasCoords(alphaMap);
				// float textureAtlasX = coords[0];
				// float textureAtlasY = coords[1];
				// float textureAtlasW = coords[2];
				// float textureAtlasH = coords[3];
				//
				// float imgWidth = alpha.getImage().getWidth();
				// float imgHeight = alpha.getImage().getHeight();
				// float pixelWidth = 1f / imgWidth;
				// float pixelHeight = 1f / imgHeight;
				//
				// textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;
				//
				// model = new ElementQuadGrid(dimensions.clone(),
				// borders.clone(), imgWidth, imgHeight, pixelWidth,
				// pixelHeight, textureAtlasX, textureAtlasY, textureAtlasW,
				// textureAtlasH);
				//
				// geom.setMesh(model);
				// mat.setVector2("OffsetAlphaTexCoord", new Vector2f(0, 0));
				throw new UnsupportedOperationException();
			}
		}
		mat.setTexture("AlphaMap", alpha);
		return this;
	}

	public BaseElement setAlwaysOnTop(boolean isAlwaysOnTop) {
		this.isAlwaysOnTop = isAlwaysOnTop;
		return this;
	}

	/**
	 * The setAsContainer only method removes the Mesh component (rendered Mesh)
	 * from the Element, leaving only Element functionality. Call this method to
	 * set the Element for use as a parent container.
	 */
	public BaseElement setAsContainerOnly() {
		if (!containerOnly) {
			setText(null);
			setTexture((Texture) null);
			containerOnly = true;
			for (int i = getChildren().size() - 1; i >= 0; i--) {
				Spatial n = getChild(i);
				if (!(n instanceof BaseElement)) {
					n.removeFromParent();
				}
			}
			geom = null;
			textElement = null;
		}
		return this;
	}

	public BaseElement setAtlas(Vector4f atlas) {
		this.atlas.set(atlas);
		dirtyLayout(false, LayoutType.background);
		layoutChildren();
		return this;
	}

	public BaseElement setBackgroundDimensions(Size backgroundSize) {
		this.backgroundSize = backgroundSize;
		dirtyLayout(false, LayoutType.background);
		layoutChildren();
		return this;
	}

	public BaseElement setBackgroundPosition(Position backgroundPosition) {
		this.backgroundPosition = backgroundPosition;
		dirtyLayout(false, LayoutType.background);
		layoutChildren();
		return this;
	}

	/**
	 * Adds an background map to the Elements material
	 * 
	 * @param bgMap
	 *            A String path to the background map
	 */
	public BaseElement setBgMap(String bgMap) {
		Texture bg = null;
		if (isAtlasTextureInUse()) {
			// TODO test
			throw new UnsupportedOperationException();
			// if (this.getElementTexture() != null)
			// alpha = getElementTexture();
			// else
			// alpha = screen.getAtlasTexture();
			// Vector2f alphaOffset =
			// getAtlasTextureOffset(screen.parseAtlasCoords(alphaMap));
			// mat.setVector2("OffsetAlphaTexCoord", alphaOffset);
		} else {
			bg = ToolKit.get().getApplication().getAssetManager().loadTexture(bgMap);
			bg.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
			bg.setMagFilter(Texture.MagFilter.Nearest);
			bg.setWrap(Texture.WrapMode.Repeat);
		}

		this.bgMap = bg;
		mat.setTexture("BgMap", bg);
		mat.setColor("BgMapColor", bgMapColor);
		return this;
	}

	public BaseElement setBgMapColor(ColorRGBA bgMapColor) {
		this.bgMapColor = bgMapColor;
		mat.setColor("BgMapColor", bgMapColor);
		return this;
	}

	public BaseElement setBorderOffset(Vector4f borderOffset) {
		this.borderOffset.set(borderOffset);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * Set the bounds of the element. This is the actual bounds that will be
	 * used, no limits will be set using the minimum or maximum sizes.
	 * 
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param w
	 *            w
	 * @param h
	 *            h
	 */
	public void setBounds(float x, float y, float w, float h) {

		if (w < 0)
			w = 0;
		if (h < 0)
			h = 0;

		if (screen == null || screen.isSnapToPixel()) {
			x = Math.round(x);
			y = Math.round(y);
			w = Math.round(w);
			h = Math.round(h);
		}

		if (LOG.isLoggable(Level.FINE))
			LOG.fine(String.format("Setting bounds of %s to %f x %f @ %f x %f", this, w, h, x, y));

		boolean szChanged = false;
		boolean posChanged = false;
		if (dimensions.x != w || dimensions.y != h) {
			this.dimensions.set(w, h);
			szChanged = true;
		}

		if (x != position.x || y != position.y) {
			this.position.set(x, y);
			posChanged = true;
		}
		if (posChanged && !szChanged) {
			dirtyLayout(false, LayoutType.clipping);
		} else if (szChanged) {
			dirtyLayout(false, LayoutType.boundsChange());
		}
		layoutChildren();
		updateNodeLocation();
		if (posChanged) {
			if (elementEventSupport != null) {
				ElementEvent<BaseElement> evt = new ElementEvent<BaseElement>(this, Type.MOVED);
				elementEventSupport.fireEvent(evt);
				if (!evt.isConsumed())
					controlMoveHook();
			} else
				controlMoveHook();

		}
		if (szChanged) {
			if (elementEventSupport != null) {
				ElementEvent<BaseElement> evt = new ElementEvent<BaseElement>(this, Type.RESIZE);
				elementEventSupport.fireEvent(evt);
				if (!evt.isConsumed())
					controlResizeHook();
			} else
				controlResizeHook();
		}
	}

	public void setBounds(Vector2f position, Vector2f dimension) {
		setBounds(position.x, position.y, dimension.x, dimension.y);
	}

	public void setBounds(Vector4f position) {
		setBounds(position.x, position.y, position.z, position.w);
	}

	/**
	 * Adds a padding to the clippinglayer, in effect this contracts the size of
	 * the clipping bounds by the specified number of pixels
	 * 
	 * @param clipPadding
	 *            The number of pixels to pad the clipping area
	 */
	public BaseElement setClipPadding(float clipPadding) {
		setClipPadding(clipPadding, clipPadding, clipPadding, clipPadding);
		return this;
	}

	public BaseElement setClipPadding(float left, float top, float right, float bottom) {
		this.clipPadding.set(left, top, right, bottom);
		dirtyLayout(false, LayoutType.clipping);
		layoutChildren();
		return this;
	}

	public BaseElement setClipPadding(Vector4f clipPadding) {
		setClipPadding(clipPadding.x, clipPadding.y, clipPadding.z, clipPadding.w);
		return this;
	}

	public BaseElement setConstraints(Object constraints) {
		this.constraints = constraints;
		return this;
	}

	@Override
	public BaseElement setCursor(CursorType cursor) {
		this.cursor = cursor;
		return this;
	}

	public BaseElement setDefaultColor(ColorRGBA defaultColor) {
		this.defaultColor = defaultColor;
		dirtyLayout(false, LayoutType.background);
		layoutChildren();
		return this;
	}

	public BaseElement setDestroyOnHide(boolean destroyOnHide) {
		this.destroyOnHide = destroyOnHide;
		return this;
	}

	public BaseElement setDimensions(float w, float h) {
		setBounds(position.x, position.y, w, h);
		return this;
	}

	/**
	 * Sets the width and height of the element
	 * 
	 * @param dimensions
	 *            Vector2f
	 */
	public BaseElement setDimensions(Vector2f dimensions) {
		setDimensions(dimensions.x, dimensions.y);
		return this;
	}

	public BaseElement setElementAlpha(float elementAlpha) {
		if (this.elementAlpha != elementAlpha) {
			this.elementAlpha = elementAlpha;
			updateAlpha();
			for (BaseElement el : childList) {
				el.setElementAlpha(elementAlpha);
			}
		}
		return this;
	}

	/**
	 * Returns the default material for the element
	 * 
	 * @param mat
	 */
	public BaseElement setElementMaterial(Material mat) {
		this.mat = mat;
		return this;
	}

	/**
	 * Sets the element's parent element
	 * 
	 * @param elementParent
	 *            Element
	 */
	public void setElementParent(BaseElement elementParent) {
		this.elementParent = elementParent;
	}

	// <editor-fold desc="User Data">
	/**
	 * Stores provided data with the Element
	 * 
	 * @param elementUserData
	 *            Object Data to store
	 */
	public BaseElement setElementUserData(Object elementUserData) {
		this.elementUserData = elementUserData;
		return this;
	}

	public BaseElement setFixedLineHeight(float lineHeight) {
		this.lineHeight = lineHeight;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public void setFocusRootOnly(boolean focusRootOnly) {
		this.focusRootOnly = focusRootOnly;
	}

	// </editor-fold>

	/**
	 * Sets the element's text layer font
	 * 
	 * @param fontPath
	 *            String The font asset path
	 */
	public BaseElement setFont(BitmapFont tempFont) {
		if (!Objects.equals(font, tempFont)) {
			this.font = tempFont;
			// doSetFont(tempFont);
			if (textElement != null) {
				textElement.setFont(tempFont);
				// removeTextElement();
				// if (text != null)
				// createTextElement();
			}
		}
		return this;
	}

	/**
	 * Sets the element's text layer font
	 * 
	 * @param fontPath
	 *            String The font asset path
	 */
	public BaseElement setFont(String fontPath) {
		setFont(ToolKit.get().getApplication().getAssetManager().loadFont(fontPath));
		return this;
	}

	/**
	 * Sets the element's text layer font color
	 * 
	 * @param fontColor
	 *            ColorRGBA The color to set the font to
	 */
	public BaseElement setFontColor(ColorRGBA fontColor) {
		if (!Objects.equals(fontColor, this.fontColor)) {
			this.fontColor = fontColor;
			if (textElement != null) {
				textElement.setFontColor(fontColor);
			}
			dirtyLayout(true, LayoutType.text);
			layoutChildren();
		}
		return this;
	}

	public BaseElement setFontFamily(String fontName) {
		if (!Objects.equals(fontFamily, fontName)) {
			String fnt = getThemeInstance().getFontPath(fontName);
			if (fnt == null)
				LOG.warning(String.format("No logical font named %s", fontName));
			else
				setFont(fnt);
			fontFamily = fontName;
		}
		return this;
	}

	/**
	 * Sets the element's text layer font size
	 * 
	 * @param fontSize
	 *            float The size to set the font to
	 */
	public BaseElement setFontSize(float fontSize) {
		if (this.fontSize != fontSize) {
			this.fontSize = fontSize;
			if (textElement != null) {
				textElement.setFontSize(calcFontSize());
				dirtyLayout(false, LayoutType.text);
				layoutChildren();
			}
		}
		return this;
	}

	// <editor-fold desc="Focus">
	/**
	 * For use by the Form control (Do not call this method directly)
	 * 
	 * @param form
	 *            The form the Element has been added to
	 */
	public BaseElement setForm(Form form) {
		if (!form.hasFormElement(this))
			form.addFormElement(this);
		else
			this.form = form;
		return this;
	}

	public BaseElement setGlobalAlpha(float globalAlpha) {
		if (globalAlpha != this.globalAlpha) {
			this.globalAlpha = globalAlpha;
			updateAlpha();
		}
		if (!ignoreGlobalAlpha) {
			for (BaseElement el : childList) {
				el.setGlobalAlpha(globalAlpha);
			}
		}
		return this;
	}

	public BaseElement setGlobalUIScale(float widthPercent, float heightPercent) {
		// TODO this is probably very broken because of layout managers.
		// Delegate this to layout
		setPosition(getPosition().x * widthPercent, getPosition().y * heightPercent);
		setDimensions(getDimensions().x * widthPercent, getDimensions().y * heightPercent);
		setFontSize(getFontSize() * heightPercent);
		for (BaseElement el : childList) {
			el.setGlobalUIScale(widthPercent, heightPercent);
		}
		return this;
	}

	public BaseElement setHandlePosition(Vector4f handlePosition) {
		this.handlePosition.set(handlePosition);
		return this;
	}

	/**
	 * Sets the height of the element
	 * 
	 * @param height
	 *            float
	 */
	public BaseElement setHeight(float height) {
		setDimensions(dimensions.x, height);
		return this;
	}

	/**
	 * Element will ignore touch fling events
	 * 
	 * @param ignoreFling
	 */
	public BaseElement setIgnoreFling(boolean ignoreFling) {
		this.ignoreFling = ignoreFling;
		return this;
	}

	/**
	 * Will enable or disable the use of the screen defined global alpha
	 * setting.
	 * 
	 * @param ignoreGlobalAlpha
	 */
	public BaseElement setIgnoreGlobalAlpha(boolean ignoreGlobalAlpha) {
		this.ignoreGlobalAlpha = ignoreGlobalAlpha;
		updateAlpha();
		return this;
	}

	/**
	 * Informs the screen control that this Element should be ignored by all
	 * mouse events.
	 * 
	 * @param ignoreMouse
	 *            boolean
	 */
	public BaseElement setIgnoreMouse(boolean ignoreMouse) {
		setIgnoreMouseMovement(ignoreMouse);
		setIgnoreMouseButtons(ignoreMouse);
		setIgnoreMouseWheel(ignoreMouse);
		setIgnoreTouchEvents(ignoreMouse);
		return this;
	}

	/**
	 * Element will ignore mouse left & right button events
	 * 
	 * @param ignoreMouseButtons
	 */
	public BaseElement setIgnoreMouseButtons(boolean ignoreMouseButtons) {
		setIgnoreMouseLeftButton(ignoreMouseButtons);
		setIgnoreMouseRightButton(ignoreMouseButtons);
		return this;
	}

	/**
	 * Element will ignore mouse left button events
	 * 
	 * @param ignoreMouseLeftButton
	 */
	public BaseElement setIgnoreMouseLeftButton(boolean ignoreMouseLeftButton) {
		this.ignoreMouseLeftButton = ignoreMouseLeftButton;
		return this;
	}

	/**
	 * Informs the screen control that this Element should be ignored by mouse
	 * movement events.
	 * 
	 * @param ignoreMouse
	 *            boolean
	 */
	public BaseElement setIgnoreMouseMovement(boolean ignoreMouseMovement) {
		this.ignoreMouseMovement = ignoreMouseMovement;
		return this;
	}

	/**
	 * Element will ignore mouse right button events
	 * 
	 * @param ignoreMouseRightButton
	 */
	public BaseElement setIgnoreMouseRightButton(boolean ignoreMouseRightButton) {
		this.ignoreMouseRightButton = ignoreMouseRightButton;
		return this;
	}

	/**
	 * Element will ignore mouse wheel click and move events
	 * 
	 * @param ignoreMouseWheel
	 */
	public BaseElement setIgnoreMouseWheel(boolean ignoreMouseWheel) {
		setIgnoreMouseWheelClick(ignoreMouseWheel);
		setIgnoreMouseWheelMove(ignoreMouseWheel);
		return this;
	}

	/**
	 * Element will ignore mouse wheel click events
	 * 
	 * @param ignoreMouseWheelClick
	 */
	public BaseElement setIgnoreMouseWheelClick(boolean ignoreMouseWheelClick) {
		this.ignoreMouseWheelClick = ignoreMouseWheelClick;
		return this;
	}

	/**
	 * Element will ignore mouse wheel mouse events;
	 * 
	 * @param ignoreMouseWheelMove
	 */
	public BaseElement setIgnoreMouseWheelMove(boolean ignoreMouseWheelMove) {
		this.ignoreMouseWheelMove = ignoreMouseWheelMove;
		return this;
	}

	/**
	 * Element will ignore touch down and up events
	 * 
	 * @param ignoreTouch
	 */
	public BaseElement setIgnoreTouch(boolean ignoreTouch) {
		this.ignoreTouch = ignoreTouch;
		return this;
	}

	/**
	 * Element will ignore touch down up move and fling events
	 * 
	 * @param ignoreTouchEvents
	 */
	public BaseElement setIgnoreTouchEvents(boolean ignoreTouchEvents) {
		setIgnoreTouch(ignoreTouchEvents);
		setIgnoreTouchMove(ignoreTouchEvents);
		setIgnoreFling(ignoreTouchEvents);
		return this;
	}

	/**
	 * element will ignore touch move events
	 * 
	 * @param ignoreTouchMove
	 */
	public BaseElement setIgnoreTouchMove(boolean ignoreTouchMove) {
		this.ignoreTouchMove = ignoreTouchMove;
		return this;
	}

	// </editor-fold>

	/**
	 * Sets the generic indent. How or if this is used depends on the component.
	 * A basic {@link BaseElement} does not use it.
	 * 
	 * @param ident
	 */
	public BaseElement setIndent(float indent) {
		this.indent = indent;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public BaseElement setBringToFrontOnClick(boolean bringToFrontOnClick) {
		this.bringToFrontOnClick = bringToFrontOnClick;
		return this;
	}

	// </editor-fold>

	public BaseElement setClippingEnabled(boolean clippingEnabled) {
		this.clippingEnabled = clippingEnabled;
		dirtyLayout(false, LayoutType.clipping);
		layoutChildren();
		return this;
	}

	/**
	 * Flags Element as Drag Element for Drag & Drop interaction
	 * 
	 * @param isDragElement
	 *            boolean
	 */
	public BaseElement setDragDropDragElement(boolean isDragElement) {
		this.isDragElement = isDragElement;
		if (isDragElement) {
			this.isDropElement = false;
			setIgnoreMouseLeftButton(false);
			setMouseFocusable(true);
			setIgnoreTouch(false);
		}
		return this;
	}

	/**
	 * Flags Element as Drop Element for Drag & Drop interaction
	 * 
	 * @param isDropElement
	 *            boolean
	 */
	public BaseElement setDragDropDropElement(boolean isDropElement) {
		this.isDropElement = isDropElement;
		if (isDropElement) {
			this.isDragElement = false;
			setIgnoreMouseLeftButton(false);
			setIgnoreMouseMovement(false);
			setIgnoreTouch(false);
		}
		return this;
	}

	/**
	 * Allows for dynamically enabling/disabling the element
	 * 
	 * @param isEnabled
	 *            boolean
	 */
	public BaseElement setEnabled(boolean isEnabled) {
		if (this.isEnabled != isEnabled) {
			this.isEnabled = isEnabled;
			if (!isEnabled && hasFocus)
				setHasFocus(false);
			else {
				onKeyboardOrMouseFocusChanged();
				layoutChildren();
			}
			controlIsEnabledHook(isEnabled);
			for (BaseElement el : childList) {
				el.setEnabled(isEnabled);
			}
		}
		return this;
	}

	/**
	 * Enables standard modal mode for the Element.
	 * 
	 * @param isModal
	 */
	public BaseElement setModal(boolean isModal) {
		this.isModal = isModal;
		return this;
	}

	/**
	 * Enables draggable behavior for this element
	 * 
	 * @param isMovable
	 *            boolean
	 */
	public BaseElement setMovable(boolean isMovable) {
		this.isMovable = isMovable;
		if (isMovable) {
			setIgnoreMouseLeftButton(false);
			setMouseFocusable(true);
		}
		return this;
	}

	/**
	 * Enables resize behavior for this element
	 * 
	 * @param isResizable
	 *            boolean
	 */
	public BaseElement setResizable(boolean isResizable) {
		this.isResizable = isResizable;
		if (isResizable) {
			setIgnoreMouseLeftButton(false);
			setIgnoreMouseMovement(false);
			setIgnoreTouch(false);
		}
		return this;
	}

	/**
	 * Toggles the Element's visibility based on the current state.
	 */
	public BaseElement toggleVisible() {
		if (isVisible())
			hide();
		else
			show();
		return this;
	}

	/**
	 * Hides or shows the element (true = show, false = hide)
	 * 
	 * @param visibleState
	 */
	public BaseElement setVisible(boolean visibleState) {
		if (visibleState) {
			show();
		} else {
			hide();
		}
		return this;
	}

	public void setKeyboardFocusable(boolean keyboardFocusable) {
		if (!keyboardFocusable && isKeyboardFocussed()) {
			screen.resetKeyboardFocus(null);
		}
		this.keyboardFocusable = keyboardFocusable;
		if (keyboardFocusable && mouseFocusable) {
			setIgnoreMouseLeftButton(false);
			setIgnoreMouseMovement(false);
			setIgnoreTouch(false);
		}
	}

	/**
	 * Sets whether this element is the 'Keyboard Focus Root'. This is an
	 * optimisation hint to signal that focus changing events (that trigger
	 * re-styling) should not occur beyond this point in the hierarchy.
	 * Typically this is meant for Panel, Window etc, where having keyboard
	 * focus might affect the styling of decoration, but nothing external to it.
	 * It is also used as a limit on where Tab focusing will traverse to with
	 * the {@link DefaultFocusCycle}.
	 * 
	 * @param focusRoot
	 *            is keyboard focus root
	 * @see #isKeyboardFocusRoot()
	 */
	public void setKeyboardFocusRoot(boolean focusRoot) {
		this.keyboardFocusRoot = focusRoot;
	}

	public BaseElement setLabel(BaseElement label) {
		this.associatedLabel = label;
		setLabelVisibility();
		return this;
	}

	/**
	 * Sets the element's text layer horizontal alignment. Some layout managers
	 * may look in here for CSS provided last constraints.
	 * 
	 * @param layoutData
	 *            data
	 */
	public BaseElement setLayoutData(String layoutData) {
		this.layoutData = layoutData;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BaseElement setLayoutManager(Layout<?, ?> layoutManager) {
		Map<BaseElement, Object> con = new HashMap<>();
		if (this.layoutManager != null) {
			for (BaseElement el : childList) {
				Object elcon = this.layoutManager.remove(el);
				if (elcon != null)
					con.put(el, elcon);
			}
		}

		this.layoutManager = layoutManager;
		for (BaseElement el : childList) {
			((Layout<BaseElement, Object>) layoutManager).constrain(el, con.get(el));
		}
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public BaseElement setLocalAlpha(float alpha) {
		if (this.alpha != alpha) {
			this.alpha = alpha;
			updateAlpha();
			for (BaseElement el : childList) {
				el.setLocalAlpha(alpha);
			}
		}
		return this;
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
	public BaseElement setLocalMaterial(Material mat) {
		this.mat = mat;
		this.setMaterial(mat);
		return this;
	}

	/**
	 * Forces the object to stay within the constraints of it's parent Elements
	 * dimensions.
	 * 
	 * @param lockToParentBounds
	 *            boolean
	 */
	public BaseElement setLockToParentBounds(boolean lockToParentBounds) {
		this.lockToParentBounds = lockToParentBounds;
		return this;
	}

	public BaseElement setMargin(float left, float right, float top, float bottom) {
		this.margin.set(left, right, top, bottom);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public BaseElement setMargin(Vector4f margin) {
		setMargin(margin.x, margin.y, margin.z, margin.w);
		return this;
	}

	public BaseElement setMaxDimensions(Size maxDimensions) {
		this.maxDimensions = maxDimensions;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * Stubbed for future use. This should limit resizing to the minimum
	 * dimensions defined
	 * 
	 * @param minDimensions
	 *            The absolute minimum dimensions for this Element.
	 */
	public BaseElement setMinDimensions(Size minDimensions) {
		this.minDimensions = minDimensions;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public void setMouseFocusable(boolean mouseFocusable) {
		if (!mouseFocusable && hasFocus) {
			setHasFocus(false);
		}
		if (mouseFocusable) {
			if (isContainerOnly())
				makeNonContainer();
			if (isIgnoreMouseMovement())
				setIgnoreMouseMovement(false);
			if (keyboardFocusable) {
				setIgnoreMouseLeftButton(false);
			}
		}
		this.mouseFocusable = mouseFocusable;
	}

	public BaseElement setOrigin(float originX, float originY) {
		origin.set(originX, originY);
		updateNodeLocation();
		dirtyLayout(true, LayoutType.clipping);
		layoutChildren();
		return this;
	}

	public BaseElement setOrigin(Vector2f origin) {
		setOrigin(origin.x, origin.y);
		return this;
	}

	public BaseElement setParseTextTags(boolean parseTextTags) {
		this.parseTextTags = parseTextTags;
		if (textElement != null)
			textElement.setParseTags(parseTextTags);
		return this;
	}

	public void setPosition(BaseElement c, Vector2f p) {
		setPosition(p.x, p.y);
	}

	public BaseElement setPosition(float x, float y) {
		setBounds(x, y, dimensions.x, dimensions.y);
		return this;
	}

	// <editor-fold desc="Sizing & Positioning">
	/**
	 * Set the x,y coordinates of the Element. X and y are relative to the
	 * parent Element.
	 * 
	 * @param position
	 *            Vector2f screen poisition of Element
	 */
	public BaseElement setPosition(Vector2f position) {
		setBounds(position.x, position.y, dimensions.x, dimensions.y);
		return this;
	}

	public BaseElement setPreferredDimensions(Size prefDimensions) {
		this.prefDimensions = prefDimensions;
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public BaseElement setPriority(ZPriority priority) {
		if (!Objects.equals(priority, this.priority)) {
			if (screen != null && screen.getModalElements().contains(this) && priority != ZPriority.NORMAL) {
				throw new IllegalArgumentException(
						String.format("Modal elements may only be of %s priority", ZPriority.NORMAL));
			}
			this.priority = priority;
			if (elementParent != null) {
				// elementParent.dirtyLayout(false, LayoutType.zorder);
				// elementParent.layoutChildren();
				elementParent.applyZOrder();
			} else if (screen != null) {
				screen.updateZOrder(this);
			}
		}
		return this;
	}

	/**
	 * Set the north, west, east and south borders in number of pixels
	 * 
	 * @param borderSize
	 */
	public BaseElement setResizeBorders(float borderSize) {
		setResizeBorders(borderSize, borderSize, borderSize, borderSize);
		return this;
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
	public BaseElement setResizeBorders(float nBorder, float wBorder, float eBorder, float sBorder) {
		this.borders.set(nBorder, wBorder, eBorder, sBorder);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	public BaseElement setResizeBorders(Vector4f resizeBorders) {
		if (resizeBorders == null)
			setResizeBorders(0);
		else
			setResizeBorders(resizeBorders.x, resizeBorders.y, resizeBorders.z, resizeBorders.w);
		return this;
	}

	/**
	 * Enables/disables east border for resizing
	 * 
	 * @param resizeE
	 *            boolean
	 */
	public BaseElement setResizeE(boolean resizeE) {
		this.resizeE = resizeE;
		return this;
	}

	/**
	 * Enables/disables north border for resizing
	 * 
	 * @param resizeN
	 *            boolean
	 */
	public BaseElement setResizeN(boolean resizeN) {
		this.resizeS = resizeN;
		return this;
	}

	/**
	 * Enables/disables south border for resizing
	 * 
	 * @param resizeS
	 *            boolean
	 */
	public BaseElement setResizeS(boolean resizeS) {
		this.resizeN = resizeS;
		return this;
	}

	/**
	 * Enables/disables west border for resizing
	 * 
	 * @param resizeW
	 *            boolean
	 */
	public BaseElement setResizeW(boolean resizeW) {
		this.resizeW = resizeW;
		return this;
	}

	public void setScale(float scale) {
		setScale(scale, scale);
	}

	public void setScale(float x, float y) {
		this.scale.set(x, y);
		updateNodeLocation();
	}

	public void setScale(Vector2f scale) {
		setScale(scale.x, scale.y);
	}

	public void setScaled(boolean scaled) {
		this.scaled = scaled;
		// TODO update
	}

	public BaseElement setStyleId(String styleId) {
		this.styleId = styleId;
		setName(styleId + ":Node");
		if (geom != null)
			geom.setName(styleId + ":Geometry");
		dirtyLayout(true, LayoutType.reset);
		layoutChildren();
		return this;
	}

	/**
	 * Sets the tab index (This is assigned by the Form control. Do not call
	 * this method directly)
	 * 
	 * @param tabIndex
	 *            The tab index assigned to the Element
	 */
	public BaseElement setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
		return this;
	}

	/**
	 * Sets the text of the element.
	 * 
	 * @param text
	 *            String The text to display.
	 */
	public BaseElement setText(String text) {
		if (!Objects.equals(text, this.text)) {
			this.text = text;
			text = formatText(text);
			if (textElement != null && text == null) {
				removeTextElement();
			} else if (textElement != null && text != null) {
				textElement.setText(text);
				dirtyLayout(false, LayoutType.boundsChange());
				layoutChildren();
			} else if (text != null) {
				if (isContainerOnly())
					makeNonContainer();
				createTextElement();
			}
		}

		return this;
	}

	/**
	 * Sets the element's text layer horizontal alignment
	 * 
	 * @param textAlign
	 */
	public BaseElement setTextAlign(BitmapFont.Align textAlign) {
		this.textAlign = textAlign;
		if (textElement != null) {
			textElement.setTextAlign(textAlign);

			/*
			 * Text and children because some components use text align for
			 * layout of children
			 */
			dirtyLayout(false, LayoutType.text, LayoutType.children);
		}
		layoutChildren();
		return this;
	}

	/**
	 * Shrinks the clipping area by set number of pixels
	 * 
	 * @param textClipPadding
	 *            The number of pixels to pad the clipping area with on each
	 *            side
	 */
	public BaseElement setTextClipPadding(float textClipPadding) {
		this.textClipPadding.set(textClipPadding, textClipPadding, textClipPadding, textClipPadding);
		return this;
	}

	public BaseElement setTextClipPadding(float clipLeft, float clipRight, float clipTop, float clipBottom) {
		this.textClipPadding.set(clipLeft, clipTop, clipRight, clipBottom);
		return this;
	}

	public BaseElement setTextClipPadding(Vector4f textClipPadding) {
		this.textClipPadding.set(textClipPadding);
		return this;
	}

	public BaseElement setTextOnTop(boolean textOnTop) {
		if (textOnTop != this.textOnTop) {
			this.textOnTop = textOnTop;
			applyZOrder();
		}
		return this;
	}

	/**
	 * Sets the padding set for the element's text layer
	 * 
	 * @param textPadding
	 */
	public BaseElement setTextPadding(float textPadding) {
		setTextPadding(textPadding, textPadding, textPadding, textPadding);
		return this;
	}

	public BaseElement setTextPadding(float left, float right, float top, float bottom) {
		this.textPadding.set(left, right, top, bottom);
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
		return this;
	}

	/**
	 * Set text padding. The padding is ordered differently to resizer borders.
	 * x is the left padding, y is the right, z is top and w is the bottom.
	 * 
	 * @param textPadding
	 */
	public BaseElement setTextPadding(Vector4f textPadding) {
		setTextPadding(textPadding.x, textPadding.y, textPadding.z, textPadding.w);
		return this;
	}

	public BaseElement setTextPaddingByKey(String style, String key) {
		// try {
		// setTextPadding(screen.getStyle(style).getFloat(key));
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// try {
		// setTextPadding(screen.getStyle(style).getVector4f(key));
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		return this;
	}

	/**
	 * Sets the elements text layer position
	 * 
	 * @param x
	 *            Position's x coord
	 * @param y
	 *            Position's y coord
	 */
	public BaseElement setTextPosition(float x, float y) {
		this.textPosition = new Vector2f(x, y);
		return this;
	}

	/**
	 * Sets the element's text rotation. This will affect the preferred bounds
	 * of the element. For example, rotating 90 degrees would flip the preferred
	 * width and height.
	 * <p>
	 * Note, currently 90, 180, 270 and 0 degrees will produce the correct
	 * dimensions. Artbitrary rotations are not supported.
	 * 
	 * 
	 * @param textAlign
	 *            rotation in degrees..
	 */
	public BaseElement setTextRotation(float textRotation) {
		this.textRotation = textRotation;
		if (textElement != null) {
			// textElement.setRotation(textRotation);

			/*
			 * Text and children because some components use text align for
			 * layout of children
			 */
			dirtyLayout(false, LayoutType.boundsChange());
		}
		layoutChildren();
		return this;
	}

	public void setTextStyles(TextStyle... textStyles) {
		this.textStyles = new LinkedHashSet<>(Arrays.asList(textStyles));
		if (textElement != null) {
			textElement.setTextStyles(this.textStyles);
			dirtyLayout(false, LayoutType.text);
			layoutChildren();
		}
	}

	// </editor-fold>

	public BaseElement setTexture(Image colorMap) {

		Texture color = null;
		if (isAtlasTextureInUse()) {
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

		setTexture(color);
		return this;
	}

	public BaseElement setTexture(String colorMap) {
		makeNonContainer();
		loadTexture(colorMap);
		layoutChildren();
		return this;
	}

	public BaseElement setTexture(Texture texture) {
		if (!Objects.equals(texture, this.defaultTex)) {
			applyTexture(texture);
			texturePath = null;
			layoutChildren();
		}
		return this;
	}

	/**
	 * Sets the element's text layer vertical alignment
	 * 
	 * @param textVAlign
	 */
	public BaseElement setTextVAlign(BitmapFont.VAlign textVAlign) {
		this.textVAlign = textVAlign;
		if (textElement != null) {
			textElement.setTextVAlign(textVAlign);

			/*
			 * Text and children because some components use text align for
			 * layout of children
			 */
			dirtyLayout(false, LayoutType.text, LayoutType.children);
		}
		layoutChildren();
		return this;
	}

	/**
	 * Sets the element's text later wrap mode
	 * 
	 * @param textWrap
	 *            LineWrapMode textWrap
	 */
	public BaseElement setTextWrap(LineWrapMode textWrap) {
		this.textWrap = textWrap;
		if (textElement != null) {
			textElement.setTextWrap(textWrap);
			dirtyLayout(false, LayoutType.text);
		}
		layoutChildren();
		return this;
	}

	@Override
	public BaseElement setThemeInstance(ThemeInstance themeInstance) {
		if (!Objects.equals(this.themeInstance, themeInstance)) {
			this.themeInstance = themeInstance;
			dirtyLayout(true, LayoutType.reset);
			doLayoutChildren();
		}
		return this;
	}

	/**
	 * Will set the textures WrapMode to repeat if enabled.<br/>
	 * <br/>
	 * NOTE: This only works when texture atlasing has not been enabled. For
	 * info on texture atlas usage, see both:<br/>
	 * 
	 * @see BaseScreen#setUseTextureAtlas(boolean enable, String path)
	 * @see #setTextureAtlasImage(com.jme3.texture.Texture tex, java.lang.String
	 *      path)
	 * @param tileImage
	 */
	public BaseElement setTileMode(TileMode tileMode) {
		if (!Objects.equals(tileMode, this.tileMode)) {
			this.tileMode = tileMode;
			dirtyLayout(false, LayoutType.background);
			layoutChildren();
		}
		return this;
	}

	public BaseElement setToolTipProvider(ToolTipProvider toolTipProvider) {
		this.toolTipProvider = toolTipProvider;
		return this;
	}

	// </editor-fold>

	/**
	 * Sets the Element's ToolTip text
	 * 
	 * @param toolTip
	 *            String
	 */
	public BaseElement setToolTipText(String toolTip) {
		this.toolTipText = toolTip;
		if (screen != null && screen.getToolTipManager() != null
				&& this == screen.getToolTipManager().getToolTipFocus())
			ToolKit.get().execute(() -> screen.getToolTipManager().updateToolTipLocation());
		return this;
	}

	public BaseElement setValign(VAlign valign) {
		if (!Objects.equals(valign, this.valign)) {
			this.valign = valign;
			updateNodeLocation();
		}
		return this;
	}

	public void setVisibilityAllowed(boolean visibilityAllowed) {
		if (!Objects.equals(this.visibilityAllowed, visibilityAllowed)) {
			this.visibilityAllowed = visibilityAllowed;
			checkVisibleState();
		}
	}

	/**
	 * Sets the width of the element
	 * 
	 * @param width
	 *            float
	 */
	public BaseElement setWidth(float width) {
		setDimensions(width, dimensions.y);
		return this;
	}

	/**
	 * Set the x coordinates of the Element. X is relative to the parent
	 * Element.
	 * 
	 * @param x
	 *            The x coordinate screen poisition of Element
	 */
	public BaseElement setX(float x) {
		setBounds(x, position.y, dimensions.x, dimensions.y);
		return this;
	}

	// </editor-fold>

	public BaseElement setY(float y) {
		setBounds(position.x, y, dimensions.x, dimensions.y);
		return this;
	}

	public void setZOrder(float zOrder) {
		this.zOrder = zOrder;
	}

	public BaseElement setZStep(float zStep) {
		this.zStep = zStep;
		return this;
	}

	/**
	 * Sets this Element and any Element contained within it's nesting order to
	 * visible.
	 * 
	 * NOTE: Hide and Show relies on shader-based clipping
	 */
	public BaseElement show() {
		show(CssEvent.SHOW, LayoutType.clipping, LayoutType.styling, LayoutType.effects);
		return this;
	}

	// </editor-fold>

	// New z-ordering code

	@Override
	public BaseElement showElement(BaseElement child) {
		return showElement(child, null);
	}

	@Override
	public BaseElement showElement(BaseElement child, Object constraints) {
		boolean wasDestroyOnHide = child.isDestroyOnHide();
		child.setDestroyOnHide(false);
		try {
			addElement(child, constraints, true, -1);
			child.show();
			return this;
		} finally {
			child.setDestroyOnHide(wasDestroyOnHide);
		}
	}

	public BaseElement sizeToContent() {
		Vector2f was = dimensions.clone();

		/*
		 * Reset size to zero to preferred text widths calculate properly. If
		 * this is not done, tooltips won't size correctly
		 */
		dimensions.set(0, 0);

		/*
		 * This is done for a maximum of twice for the benefit of elements that
		 * only know their true preferred size once their children are laid out
		 * (such as scroll panel and wrapping text).
		 */
		for (int i = 0; i < 2; i++) {
			Vector2f newWindowSize = calcPreferredSize();
			if (newWindowSize.equals(was)) {
				dimensions.set(was);
				break;
			}
			setDimensions(newWindowSize);
			if (isLockToParentBounds()) {
				lockToParentBounds(getX(), getY());
			}

			// Shoud be done in setDimensiions
			// if (!was.equals(dimensions)) {
			// dirtyLayout(false, LayoutType.boundsChange());
			// layoutChildren();
			// }
			was = newWindowSize;
		}

		return this;
	}

	@Override
	public String toString() {
		return getClass() + " [styleId=" + styleId + "]";
	}

	public void triggerCssEvent(CssEvent event) {
		activeEvent.add(event);
		try {
			dirtyLayout(true, LayoutType.clipping, LayoutType.styling, LayoutType.effects);
			layoutChildren();
		} finally {
			activeEvent.remove(event);
		}
	}

	public void updateNodeLocation() {
		float x = 0, y = 0;
		ElementContainer<?, ?> elementParent = getParentContainer();

		Vector2f dimensions = this.dimensions;
		if (scaled) {
			Vector2f ps = calcPreferredSize();
			scale.x = dimensions.x / ps.x;
			scale.y = dimensions.y / ps.y;
			dimensions = ps;
		}

		if (elementParent != null) {

			switch (valign) {
			case Top:
				y = elementParent.getHeight() - position.y - dimensions.y - origin.y;
				break;
			case Bottom:
				y = position.y + origin.y;
				break;
			case Center:
				x = (elementParent.getHeight() - dimensions.y) / 2f;
				break;
			default:
				break;
			}
			switch (align) {
			case Left:
				x = position.x + origin.x;
				break;
			case Right:
				x = elementParent.getWidth() - dimensions.x - position.x - origin.x;
				break;
			case Center:
				x = (elementParent.getWidth() - dimensions.x) / 2f;
				break;
			default:
				break;
			}
		}
		if (scale.x != 1 || scale.y != 1) {
			setLocalScale(scale.x, scale.y, 1);
			setLocalTranslation(x, y, getLocalTranslation().getZ());
		} else {
			setLocalScale(1, 1, 1);
			setLocalTranslation(x, y, getLocalTranslation().getZ());
		}
	}

	public void validate() {
		if (validated)
			throw new IllegalStateException("Validated.");
		validated = true;

		reverseLayout.set(true);
		try {
			layoutChildren();
		} finally {
			reverseLayout.remove();
		}
	}

	@SuppressWarnings("unchecked")
	protected void addElement(BaseElement child, Object constraints, boolean hide, int index) {
		if (child.equals(this)) {
			throw new IllegalArgumentException("Cannot add a child to itself.");
		}

		if (constraints == null)
			constraints = child.getConstraints();

		child.elementParent = this;

		if (screen != null && !child.isInitialized() && isInitialized()) {
			child.setInitialized(screen);
		}
		child.setQueueBucket(RenderQueue.Bucket.Gui);

		if (childList.contains(child)) {
			throw new ConflictingIDException(
					String.format("The child element '%s' is already added to the element %s.", child, this));
		} else {
			if (index == -1)
				childList.add(child);
			else
				childList.add(index, child);

			if (child.isVisible())
				this.attachChild(child);

			if (hide)
				child.hide(null, LayoutType.clipping);
		}
		if (constraints != null && layoutManager == null) {
			throw new IllegalStateException("Must have layout manager set to use constraints.");
		}
		((Layout<BaseElement, Object>) layoutManager).constrain(child, constraints);
		dirtyLayout(false, LayoutType.boundsChange());

		// Now parented, the child CSS might have styles with parent in selector
		child.dirtyLayout(true, LayoutType.reset);

		layoutChildren();
	}

	protected void applyTexture(Texture texture) {
		if (containerOnly)
			throw new IllegalStateException("Element is a container.");
		if (texture == null) {
			if (geom != null) {
				geom.removeFromParent();
				geom = null;
			}
			createDefaultGeometry();
			defaultTex = null;
		} else {
			if (defaultTex == null && geom != null) {
				geom.removeFromParent();
				geom = null;
			}
			defaultTex = texture;
			if (geom == null) {
				geom = new Geometry(styleId + ":Geometry");
				geom.setCullHint(CullHint.Never);
				geom.setQueueBucket(Bucket.Gui);
				geom.setMaterial(mat);
			}

			defaultTex.setMinFilter(minFilter);
			defaultTex.setMagFilter(magFilter);

			switch (tileMode) {
			case NONE:
				defaultTex.setWrap(Texture.WrapMode.Clamp);
				break;
			case REPEAT:
			case REPEAT_X:
				defaultTex.setWrap(Texture.WrapMode.Repeat);
				break;
			case REPEAT_Y:
				defaultTex.setWrap(WrapAxis.T, Texture.WrapMode.Repeat);
				defaultTex.setWrap(WrapAxis.S, Texture.WrapMode.Clamp);
				break;
			}

			recreateElementQuadGrid();
			mat.setTexture("ColorMap", defaultTex);
			setElementMaterialColor(defaultColor);
			geom.setMaterial(mat);

			if (geom.getParent() == null)
				attachChild(geom);
		}

		dirtyLayout(false, LayoutType.boundsChange());
		updateNodeLocation();
	}

	protected void checkMaterial(BaseScreen screen) {
		if (mat == null) {
			mat = new Material(ToolKit.get().getApplication().getAssetManager(), "icetone/shaders/Unshaded.j3md");
			mat.setVector2("OffsetAlphaTexCoord", new Vector2f(0, 0));
			mat.setFloat("GlobalAlpha", getAlphaValue());
			mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
			if (isAtlasTextureInUse()) {
				mat.setBoolean("UseEffectTexCoords", true);
			} else {
				mat.setBoolean("UseEffectTexCoords", false);
			}
			mat.setColor("Color", defaultColor);
			mat.setTexture("ColorMap", null);
		}
	}

	protected boolean checkVisibleState() {
		if (isVisibilityAllowed() && isVisible && getParent() == null) {
			if (getElementParent() != null) {
				getElementParent().attachChild(this);
				return true;
			} else if (screen.getElements().contains(this)) {
				screen.getGUINode().attachChild(this);
				return true;
			}
		} else if ((!isVisibilityAllowed() || !isVisible) && parent != null) {
			parent.detachChild(this);
		}
		return false;
	}

	protected void configureClone(BaseElement el) {
		el.alpha = alpha;
		el.associatedLabel = associatedLabel;
		el.effects = effects == null ? null : new HashMap<>(effects);
		el.atlas = atlas == null ? null : atlas.clone();
		el.backgroundPosition = backgroundPosition == null ? null : backgroundPosition.clone();
		el.backgroundSize = backgroundSize.clone();
		el.handlePosition = handlePosition.clone();
		el.borders = calcBorders();
		el.clipPadding = clipPadding.clone();
		el.clippingBounds = clippingBounds.clone();
		el.containerOnly = containerOnly;
		el.cursor = cursor;
		el.defaultColor = defaultColor.clone();
		el.defaultTex = defaultTex == null ? null : defaultTex.clone();
		el.destroyOnHide = destroyOnHide;
		// ? need to clone
		el.font = font;
		el.scale = scale.clone();
		el.fontColor = fontColor == null ? null : fontColor.clone();
		el.fontSize = fontSize;
		el.globalAlpha = globalAlpha;
		el.indent = indent;
		el.isAlwaysOnTop = isAlwaysOnTop;
		el.isEnabled = isEnabled;
		el.isMovable = isMovable;
		el.isVisible = isVisible;
		el.visibilityAllowed = visibilityAllowed;
		el.keyboardFocusable = keyboardFocusable;
		el.keyboardFocusSupport = keyboardFocusSupport;
		el.keyboardSupport = keyboardSupport;
		el.layoutData = layoutData;
		el.layoutManager = layoutManager;
		el.margin = margin.clone();
		el.borderOffset = borderOffset.clone();
		el.maxDimensions = maxDimensions == null ? null : maxDimensions.clone();
		el.minDimensions = minDimensions == null ? null : minDimensions.clone();
		el.mouseButtonSupport = mouseButtonSupport;
		el.mouseFocusable = mouseFocusable;
		el.prefDimensions = prefDimensions == null ? null : prefDimensions.clone();
		el.screen = screen;
		if (!styleIdIsGenerated)
			el.styleId = styleId;
		el.text = text;
		if (el.text != null)
			el.createTextElement();
		el.textAlign = textAlign;
		el.textPadding = textPadding.clone();
		el.textPosition = textPosition.clone();
		el.textRotation = textRotation;
		if (textStyles != null && !textStyles.isEmpty()) {
			el.textStyles = new LinkedHashSet<TextStyle>();
			el.textStyles.addAll(textStyles);
		}
		el.textVAlign = textVAlign;
		el.textWrap = textWrap;
		el.tileMode = tileMode;
		el.wasVisible = wasVisible;
		el.origin = origin.clone();
		el.priority = priority;
		el.zOrder = zOrder;
		el.adjusting = adjusting;
		el.alphaMap = alphaMap == null ? null : alphaMap.clone();
		el.bgMap = bgMap == null ? null : bgMap.clone();
		el.bgMapColor = bgMapColor.clone();
		el.bringToFrontOnClick = bringToFrontOnClick;
		el.clippingEnabled = clippingEnabled;
		el.constraints = constraints;
		el.dimensions = dimensions.clone();
		el.affectParent = affectParent;
		el.affectZOrder = affectZOrder;
		el.elementUserData = elementUserData;
		el.focusRootOnly = focusRootOnly;
		el.hasFocus = hasFocus;
		el.hasFocussedChild = hasFocussedChild;
		el.ignoreFling = ignoreFling;
		el.ignoreGlobalAlpha = ignoreGlobalAlpha;
		el.ignoreMouseLeftButton = ignoreMouseLeftButton;
		el.ignoreMouseMovement = ignoreMouseMovement;
		el.ignoreMouseRightButton = ignoreMouseRightButton;
		el.ignoreMouseWheelClick = ignoreMouseWheelClick;
		el.ignoreMouseWheelMove = ignoreMouseWheelMove;
		el.ignoreTouch = ignoreTouch;
		el.ignoreTouchMove = ignoreTouchMove;
		el.isDragElement = isDragElement;
		el.isDragElement = isDropElement;
		el.isModal = isModal;
		el.isResizable = isResizable;
		el.lineHeight = lineHeight;
		el.lockToParentBounds = lockToParentBounds;
		el.mat = mat == null ? null : mat.clone();
		el.parseTextTags = parseTextTags;
		el.position = position.clone();
		el.resizeE = resizeE;
		el.resizeW = resizeW;
		el.resizeS = resizeS;
		el.resizeN = resizeN;
		el.tabIndex = tabIndex;
		el.textClipPadding = textClipPadding.clone();
		el.texturePath = texturePath;
		el.toolTipProvider = toolTipProvider;
		el.toolTipText = toolTipText;
		el.useLocalAtlas = useLocalAtlas;
		el.zStep = zStep;
		for (ClippingDefine def : clippingLayers) {
			el.clippingLayers.add(def);
		}

		if (el.defaultTex == null) {
			el.createDefaultGeometry();
			el.defaultTex = null;
		} else {
			if (el.geom == null) {
				el.geom = new Geometry(el.styleId + ":Geometry");
				el.geom.setCullHint(CullHint.Never);
				el.geom.setQueueBucket(Bucket.Gui);
				el.geom.setMaterial(el.mat);
			}
			el.recreateElementQuadGrid();
			el.mat.setTexture("ColorMap", el.defaultTex);
			el.setElementMaterialColor(el.defaultColor);
			el.geom.setMaterial(el.mat);

			if (el.geom.getParent() == null)
				el.attachChild(el.geom);
		}

	}

	protected void preConfigureElement() {
	}

	protected void configureElement() {
	}

	protected void createDefaultGeometry() {
		defaultTex = null;
		mat.setTexture("ColorMap", null);
		setElementMaterialColor(defaultColor);

		geom = new Geometry(styleId + ":Geometry");
		geom.setCullHint(CullHint.Never);
		geom.setQueueBucket(Bucket.Gui);
		geom.setMaterial(mat);

		recreateElementQuadGrid();

		this.attachChild(geom);
	}

	protected void createTextElement() {
		if (containerOnly)
			throw new IllegalStateException("Element is a container.");

		textElement = new AnimText(screen, calcFont());
		textElement.setBounds(MathUtil.max(dimensions, new Vector2f(1, 1)));
		textElement.setScale(1, 1);
		textElement.setOrigin(0, 0);
		textElement.setParseTags(parseTextTags);
		textElement.setRotation(textRotation);
		textElement.setTextWrap(textWrap);
		textElement.setTextAlign(textAlign);
		textElement.setTextVAlign(textVAlign);
		textElement.setFontSize(calcFontSize());
		textElement.setFontColor(calcFontColor());
		if (textStyles != null)
			textElement.setTextStyles(textStyles);

		textElement.setText(text);
		textElement.setPositionY(getHeight() - textElement.getLineHeight());
		updateAlpha();
		if (textElement.getParent() == null) {
			this.attachChild(textElement);
		}
		dirtyLayout(false, LayoutType.boundsChange());
		layoutChildren();
	}

	protected void doSetFont(BitmapFont tempFont) {
		// fontFamily = null;
		// font = new BitmapFont();
		// font.setCharSet(tempFont.getCharSet());
		// Material[] pages = new Material[tempFont.getPageSize()];
		// for (int i = 0; i < pages.length; i++) {
		// pages[i] = tempFont.getPage(i).clone();
		// }
		// font.setPages(pages);
		font = tempFont;
	}

	protected float getAlignedX() {
		ElementContainer<?, ?> elementParent = getParentContainer();
		switch (align) {
		case Right:
			return elementParent == null ? getX() : elementParent.getWidth() - position.x - dimensions.x;
		case Center:
			return elementParent == null ? getX() : (elementParent.getWidth() - dimensions.x) / 2f;
		default:
			return getX();
		}
	}

	protected float getAlignedY() {
		ElementContainer<?, ?> elementParent = getParentContainer();
		switch (valign) {
		case Bottom:
			return elementParent == null ? getY() : elementParent.getHeight() - position.y - dimensions.y;
		case Center:
			return elementParent == null ? getY() : (elementParent.getHeight() - dimensions.y) / 2f;
		default:
			return getY();
		}
	}

	/**
	 * Returns a Vector4f containing the current boundaries of the element's
	 * clipping layer (left, top, right, bottom)
	 * 
	 * @return Vector4f clippingBounds
	 */
	protected Vector4f getClippingBounds() {
		return this.clippingBounds;
	}

	protected Map<EffectChannel, EffectFactory> getEffects() {
		if (effects == null)
			effects = new HashMap<>();
		dirtyLayout(false, LayoutType.effects);
		return effects;
	}

	protected BaseElement getKeyboardFocusRoot() {
		if (isKeyboardFocusRoot())
			return this;
		else if (elementParent == null)
			return null;
		else
			return elementParent.getKeyboardFocusRoot();
	}

	protected Collection<BaseElement> getZSortedChildren() {
		return ZOrderComparator.sortChildren(this);
		// return childList;
	}

	protected void hide(CssEvent event, LayoutType... layout) {
		if (isVisible) {
			if (elementEventSupport != null) {
				ElementEvent<BaseElement> evt = new ElementEvent<BaseElement>(this, Type.ABOUT_TO_HIDE);
				elementEventSupport.fireEvent(evt);
				if (evt.isConsumed())
					return;
			}

			if (screen != null)
				screen.releaseModal(this);

			this.wasVisible = true;
			this.isVisible = false;

			if (screen != null && screen.getToolTipManager() != null)
				screen.getToolTipManager().removeToolTipFor(this);

			setLabelVisibility();

			activeEvent.add(event);
			try {
				if (isKeyboardFocussed())
					defocus();
				else {
					// TODO true really needed?
					dirtyLayout(false, layout);
					layoutChildren();
				}
			} finally {
				activeEvent.remove(event);
			}

			controlHideHook();
			if (shouldDetachFromParentOnHide()) {
				detachFromParent();
			}
			for (BaseElement el : childList) {
				el.childHide();
			}

			if (elementEventSupport != null) {
				ElementEvent<BaseElement> evt = new ElementEvent<BaseElement>(this, Type.HIDDEN);
				elementEventSupport.fireEvent(evt);
			}
		}
	}

	protected boolean isValidated() {
		return screen != null && validated
				&& (elementParent == null || (elementParent != null && elementParent.isValidated()));
	}

	protected void layoutHeirarchy(Node s) {
		applyZOrder();
		Collection<? extends Spatial> children;
		if (s instanceof BaseElement) {
			children = ((BaseElement) s).getElements();
		} else {
			children = s.getChildren();
		}
		for (Spatial childSpatial : children) {
			if (childSpatial instanceof BaseElement) {
				((BaseElement) childSpatial).layoutChildren();
			} else if (childSpatial instanceof Node) {
				layoutHeirarchy(((Node) childSpatial));
			}
		}
	}

	protected void layoutThis() {
		if (dirty.isEmpty())
			return;

		// boolean isReady = getWidth() > 0 && getHeight() > 0 &&
		// isInStyleHierarchy();
		boolean isReady = isInStyleHierarchy();
		// if (!isReady)
		// return;

		if (LOG.isLoggable(Level.FINE))
			LOG.fine(String.format("Laying out %s, %s", toString(), dirty));

		Set<LayoutType> redo = new LinkedHashSet<>();

		layoutCounter++;
		while (!dirty.isEmpty()) {
			List<LayoutType> d = new ArrayList<>(dirty);
			Collections.sort(d);
			dirty.clear();
			if (d.contains(LayoutType.all)) {

				if (isReady) {
					layoutThis(this, LayoutType.all);
					/*
					 * Only styling should cause dirtying of most types, and
					 * this is done first, followed by the individual layout
					 * types, so nothing should actually be dirty at this point
					 */
					dirty.clear();
					continue;
				}

				/* Do all that are possible while not ready */
				d.clear();
				d.addAll(Arrays.asList(LayoutType.allExceptAll()));

			}

			/* Reset implies styling */
			if (d.contains(LayoutType.styling) && d.contains(LayoutType.reset))
				d.remove(LayoutType.styling);

			while (d.size() > 0) {
				LayoutType type = d.remove(0);
				if (!type.requiresReady() || isReady) {
					layoutThis(this, type);
				} else
					redo.add(type);

				/*
				 * If anything was dirtied that we are going to process anyway,
				 * remove it from the list now so it doesn't get done twice
				 * needlessly
				 */
				d.removeAll(dirty);

			}
		}

		if (!redo.isEmpty()) {
			if (LOG.isLoggable(Level.FINE))
				LOG.fine(String.format("Deferring %s for %s as not ready.", redo, this));
			dirtyLayout(false, redo.toArray(new LayoutType[0]));
		}

		// if (!isReady)
		// dirtyLayout(false, LayoutType.boundsChange());
	}

	@SuppressWarnings("unchecked")
	protected void layoutThis(ElementContainer<?, ?> container, LayoutType type) {
		((Layout<ElementContainer<?, ?>, ?>) layoutManager).layout(container, type);
	}

	protected void loadTexture(String colorMap) {
		if (!Objects.equals(this.texturePath, colorMap)) {
			Texture color = null;
			this.texturePath = colorMap;
			if (this.texturePath != null) {
				// if (isAtlasTextureInUse()) {
				// if (this.getElementTexture() != null)
				// color = getElementTexture();
				// else
				// color = screen.getAtlasTexture();
				// } else {
				color = ToolKit.get().getApplication().getAssetManager().loadTexture(colorMap);
				color.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
				color.setMagFilter(Texture.MagFilter.Nearest);
				// color.setWrap(Texture.WrapMode.Clamp);
				// }
			}
			applyTexture(color);
		}
	}

	/**
	 * Reverses the effects of {@link #setAsContainerOnly()} allowing textures,
	 * text and background colours to be set.
	 * 
	 */
	protected void makeNonContainer() {
		if (containerOnly) {
			containerOnly = false;
			checkMaterial(screen);

			geom = new Geometry(styleId + ":Geometry");
			geom.setCullHint(CullHint.Never);
			geom.setQueueBucket(Bucket.Gui);
			geom.setMaterial(mat);

			recreateElementQuadGrid();
			mat.setTexture("ColorMap", defaultTex);
			setElementMaterialColor(defaultColor);
			geom.setMaterial(mat);

			if (geom.getParent() == null)
				attachChild(geom);

			dirtyLayout(false, LayoutType.boundsChange());
			updateNodeLocation();
		}
	}

	protected void onAfterLayout() {
		// Instances can override to do stuff on layout
	}

	protected void onBeforeLayout() {
		// Instances can override to do stuff on layout
	}

	protected void onInitialized() {
		// For sub-classes to override
	}

	protected void onKeyboardOrMouseFocusChanged() {
		/*
		 * Hrm.... excessive, but required currently or CSS styles are not
		 * returned properly to default to state. A symptom would be for
		 * exxample setTextWrap on a TextArea, then hover over it, and wrap mode
		 * is lost
		 */
		dirtyLayout(false, LayoutType.styling);
	}

	protected void propagateEffect(Effect effect, boolean callHide) {
		Effect nEffect = effect.clone();
		nEffect.setCallHide(callHide);
		nEffect.setElement(this);
		screen.getEffectManager().applyEffect(nEffect);
		for (BaseElement el : childList) {
			el.propagateEffect(effect, false);
		}
	}

	protected void recreateElementQuadGrid() {
		Vector4f calcBorders = calcBorders();
		if (defaultTex == null) {
			float imgWidth = 100;
			float imgHeight = 100;
			float pixelWidth = 1f / imgWidth;
			float pixelHeight = 1f / imgHeight;
			float textureAtlasX = 0, textureAtlasY = 0, textureAtlasW = imgWidth, textureAtlasH = imgHeight;
			model = new ElementQuadGrid(dimensions.clone(), calcBorders, imgWidth, imgHeight, pixelWidth,
					pixelHeight, textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);
		} else {
			if (!isAtlasTextureInUse()) {
				float imgWidth = defaultTex.getImage().getWidth();
				float imgHeight = defaultTex.getImage().getHeight();
				float pixelWidth = 1f / imgWidth;
				float pixelHeight = 1f / imgHeight;

				model = new ElementQuadGrid(dimensions.clone(), calcBorders, imgWidth, imgHeight, pixelWidth,
						pixelHeight, 0, 0, imgWidth, imgHeight);

				mat.setBoolean("UseEffectTexCoords", false);
			} else {
				mat.setBoolean("UseEffectTexCoords", true);

				float textureAtlasX = atlas.x;
				float textureAtlasY = atlas.y;
				float textureAtlasW = atlas.z;
				float textureAtlasH = atlas.w;

				float imgWidth = defaultTex.getImage().getWidth();
				float imgHeight = defaultTex.getImage().getHeight();
				float pixelWidth = 1f / imgWidth;
				float pixelHeight = 1f / imgHeight;

				textureAtlasY = imgHeight - textureAtlasY - textureAtlasH;

				model = new ElementQuadGrid(this.getDimensions().clone(), calcBorders, imgWidth, imgHeight,
						pixelWidth, pixelHeight, textureAtlasX, textureAtlasY, textureAtlasW, textureAtlasH);

			}
		}
		geom.setMesh(model);
	}

	protected Vector4f calcBorders() {
		Vector4f b = borders.clone();
		float tw = b.x + b.y;
		if(tw > getWidth()) {
			float fac = getWidth() / tw;
			b.x *= fac;
			b.y *= fac;
		}
		float th = b.z + b.w;
		if(th > getHeight()) {
			float fac = getHeight() / th;
			b.z *= fac;
			b.w *= fac;
		}
		return b;
	}

	protected void removeClipForElement(BaseElement el) {
		for (Iterator<ClippingDefine> it = clippingLayers.iterator(); it.hasNext();) {
			if (el == it.next().getElement())
				it.remove();
		}
		for (BaseElement c : childList) {
			c.removeClipForElement(el);
		}
	}

	/**
	 * For resizing based on absolute screen positions, should only be needed
	 * the {@link BaseScreen} and subclasses that want to override window
	 * resizing behaviour (when acting as a resize proxy to another element for
	 * example).
	 * 
	 * @param x
	 *            the absolute x coordinate from screen x 0
	 * @param y
	 *            the absolute y coordinate from screen y 0
	 * @param dir
	 *            The Element.Borders used to determine the direction of the
	 *            resize event
	 */
	protected void resize(float x, float y, Borders dir) {
		float oX = x, oY = y;
		if (getElementParent() != null) {
			x -= getAbsoluteX() - getX();
		}
		if (getElementParent() != null) {
			y -= getAbsoluteY() - getY();
		}
		float nextX, nextY;
		Vector2f minDimensions = calcMinimumSize();
		Vector2f maxDimensions = calcMaximumSize();
		if (dir == Borders.NW) {
			if (isLockToParentBounds()) {
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
				setBounds(x, position.y, getX() + getWidth() - x, dimensions.y);
			}
			if (isLockToParentBounds()) {
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
				setBounds(position.x, y, dimensions.x, getY() + getHeight() - y);
			}
		} else if (dir == Borders.N) {
			if (isLockToParentBounds()) {
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
				setBounds(position.x, y, dimensions.x, getY() + getHeight() - y);
			}
		} else if (dir == Borders.NE) {
			nextX = oX - getAbsoluteX();
			if (isLockToParentBounds()) {
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
			if (isLockToParentBounds()) {
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
				setBounds(position.x, y, dimensions.x, getY() + getHeight() - y);
			}
		} else if (dir == Borders.W) {
			if (isLockToParentBounds()) {
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
				setBounds(x, position.y, getX() + getWidth() - x, dimensions.y);
			}
		} else if (dir == Borders.E) {
			nextX = oX - getAbsoluteX();
			if (isLockToParentBounds()) {
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
			if (isLockToParentBounds()) {
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
			setBounds(x, position.y, getX() + getWidth() - x, dimensions.y);
			// }
			nextY = oY - getAbsoluteY();
			if (isLockToParentBounds()) {
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

			nextY = oY - getAbsoluteY();
			if (isLockToParentBounds()) {
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
				// setY(getY() - (prevHeight - nextY));
				setHeight(nextY);
			}
		} else if (dir == Borders.SE) {
			nextX = oX - getAbsoluteX();
			if (isLockToParentBounds()) {
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
			if (isLockToParentBounds()) {
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
		// float diffX = prevWidth - getWidth();
		// float diffY = prevHeight - getHeight();
		// if (diffX != 0 || diffY != 0) {
		// // TODO children needed?
		// dirtyLayout(false, LayoutType.boundsChange());
		// layoutChildren();
		// controlResizeHook();
		// }
	}

	protected void setElementMaterialColor(ColorRGBA col) {
		if (containerOnly)
			throw new IllegalStateException("Element is a container.");

		if (defaultTex == null)
			getElementMaterial().setColor("Color", col);
		else {
			ColorRGBA c = col.clone();
			c.a = 1.0f;
			getElementMaterial().setColor("Color", c);
		}
	}

	/**
	 * For internal use - DO NOT CALL THIS METHOD
	 * 
	 * @param hasFocus
	 *            boolean
	 */
	protected void setHasFocus(boolean hasFocus) {
		if (this.hasFocus != hasFocus && mouseFocusable && (!hasFocus || isEnabled)) {
			this.hasFocus = hasFocus;
			onKeyboardOrMouseFocusChanged();
			layoutChildren();
		}
	}

	protected void setInitialized(BaseScreen screen) {
		if (initialized)
			throw new IllegalStateException("Already initialized.");

		if (LOG.isLoggable(Level.FINE))
			LOG.fine(String.format("Initializing %s", toString()));

		if (this.screen == null && screen != null) {
			// New style init
			this.screen = screen;
		}

		this.initialized = true;

		if (isLockToParentBounds()) {
			lockToParentBounds(getX(), getY());
		}

		// Add the parents clipping layers
		if (elementParent != null) {
			for (ClippingDefine def : elementParent.clippingLayers) {
				if (def.getElement() != null && !hasClippingLayer(def.getElement())) {
					addClippingDefine(def);
					dirtyLayout(false, LayoutType.clipping);
				}
			}
		}

		for (BaseElement e : childList) {
			if (!e.isInitialized()) {
				e.setInitialized(screen);
			}
		}

		// Now we are possibly part of the scene, layout so styles are
		// calculated
		layoutChildren();
		onInitialized();

		if (getDimensions().equals(Vector2f.ZERO))
			sizeToContent();
	}

	protected void setLabelVisibility() {
		if (this.associatedLabel != null) {
			this.associatedLabel.setVisible(isVisible());
		}
	}

	protected void setTextElementText(String text) {

	}

	protected void setVisibleState(boolean visibleState) {
		isVisible = visibleState;
	}

	protected boolean shouldDetachFromParentOnHide() {
		return screen == null || !screen.getEffectManager().hasEffectFor(this, EffectChannel.fx);
	}

	protected void show(CssEvent event, LayoutType... layout) {

		if (!isVisible) {
			if (elementEventSupport != null) {
				ElementEvent<BaseElement> evt = new ElementEvent<BaseElement>(this, Type.ABOUT_TO_SHOW);
				elementEventSupport.fireEvent(evt);
				if (evt.isConsumed())
					return;
			}

			// screen.updateZOrder(getAbsoluteParent());
			wasVisible = false;
			isVisible = true;

			setLabelVisibility();
			activeEvent.add(event);
			try {
				dirtyLayout(true, layout);
				controlShowHook();
				if (getTextElement() != null)
					getTextElement().setAlpha(getAlphaValue());

				checkVisibleState();
				for (BaseElement el : childList) {
					el.childShow();
				}
				layoutChildren();
			} finally {
				activeEvent.remove(event);
			}
			setLabelVisibility();

			if (elementEventSupport != null) {
				ElementEvent<BaseElement> evt = new ElementEvent<BaseElement>(this, Type.SHOWN);
				elementEventSupport.fireEvent(evt);
			}
		}
	}

	protected void updateAlpha() {
		if (!ignoreGlobalAlpha) {
			if (mat != null)
				mat.setFloat("GlobalAlpha", getAlphaValue());
			if (textElement != null) {
				textElement.setAlpha(getAlphaValue());
			}
		} else {
			if (mat != null)
				mat.setFloat("GlobalAlpha", getAlphaValue());
			if (textElement != null) {
				textElement.setAlpha(getAlphaValue());
				textElement.update(0);
			}
		}
	}

	public BitmapFont calcFont() {
		return calcFont(this);
	}

	public float calcFontSize() {
		return calcFontSize(this);
	}

	public static float calcFontSize(ElementContainer<?, ?> container) {
		float f = container.getFontSize();
		if (f == -1) {
			ElementContainer<?, ?> p = container.getParentContainer();
			while (p != null && f == -1) {
				f = p.getFontSize();
				p = p.getParentContainer();
			}
			if (f == -1)
				f = 8;
		}
		return f;

	}

	public String calcFontFamily() {
		return calcFontFamily(this);
	}

	public static String calcFontFamily(ElementContainer<?, ?> container) {
		String f = container.getFontFamily();
		if (f == null) {
			ElementContainer<?, ?> p = container.getParentContainer();
			while (p != null && f == null) {
				f = p.getFontFamily();
				p = p.getParentContainer();
			}
			if (f == null)
				f = "default";
		}
		return f;

	}

	public static BitmapFont calcFont(ElementContainer<?, ?> container) {
		BitmapFont fnt = container.getFont();
		if (fnt == null) {
			ElementContainer<?, ?> p = container.getParentContainer();
			while (p != null && fnt == null) {
				fnt = p.getFont();
				p = p.getParentContainer();
			}
			if (fnt == null) {
				fnt = ToolKit.get().getStyleManager().getDefaultInstance().getDefaultGUIFont();
			}
		}
		return fnt;

	}

	public ColorRGBA calcFontColor() {
		return calcFontColor(this);

	}

	public static ColorRGBA calcFontColor(ElementContainer<?, ?> container) {
		ColorRGBA f = container.getFontColor();
		if (f == null) {
			ElementContainer<?, ?> p = container.getParentContainer();
			while (p != null && f == null) {
				f = p.getFontColor();
				p = p.getParentContainer();
			}
			if (f == null)
				f = ColorRGBA.White;
		}
		return f;

	}

	void focusChanged(MouseUIFocusEvent<BaseElement> event) {
		boolean hasFocus = event.getEventType() == FocusEventType.gained;
		if (this.hasFocus != hasFocus && mouseFocusable && (!hasFocus || isEnabled)) {
			setHasFocus(hasFocus);
			ElementContainer<?, ?> p = this;
			CursorType c = getCursor();
			while (c == null && p != null) {
				p = p.getParentContainer();
				if (p != null)
					c = p.getCursor();
			}
			screen.setActiveCursor(c);
		}
	}

	boolean isClippingEnabledInHeirarchy() {
		return clippingEnabled && (elementParent == null || elementParent.isClippingEnabled());
	}

	boolean isHeirarchyInitialized() {
		return initialized && (elementParent == null || elementParent.isHeirarchyInitialized());
	}

	/**
	 * Get whether this element is in the style hierarchy. The full hierarchy is
	 * only known once the root Element is added to the Screen.
	 * 
	 * @return is in style hierarchy.
	 */
	boolean isInStyleHierarchy() {
		return (elementParent != null && elementParent.isInStyleHierarchy())
				|| (getParentContainer() instanceof BaseScreen
						&& ((BaseScreen) getParentContainer()).getElements().contains(this));
	}

	void keyboardFocusChanged(KeyboardFocusEvent keyboardFocusEvent) {
		// if(hasFocussedChild && keyboardFocusEvent.getEventType() ==
		// KeyboardFocusEventType.gained) {
		// // If we already have a focus child, then don't go any further
		// return;
		// }

		hasFocussedChild = keyboardFocusEvent.getEventType() == KeyboardFocusEventType.gained;

		if (keyboardFocusSupport != null)
			keyboardFocusSupport.fireEvent(keyboardFocusEvent);

		if (this == keyboardFocusEvent.getElement()) {
			onKeyboardOrMouseFocusChanged();
		} else {
			/*
			 * OPTIMISATION: Only restyle if this elements is actually keyboard
			 * focusable
			 */
			if (isKeyboardFocusableChild())
				dirtyLayout(false, LayoutType.styling);

			if (elementParent == null || isKeyboardFocusRoot()) {
				/*
				 * OPTIMISATION: Layout when we get to the top of the tree, but
				 * skip this if we are losing focus and both the elements are in
				 * the same hierarchy (as layout will happen when focus
				 * is-regained)
				 */
				if (keyboardFocusEvent.getEventType() == KeyboardFocusEventType.lost
						&& keyboardFocusEvent.getOther() != null && Objects.equals(getKeyboardFocusRoot(),
								keyboardFocusEvent.getOther().getKeyboardFocusRoot())) {
					if (LOG.isLoggable(Level.FINE))
						LOG.fine("Skipping focus lost layout because in same root as opposite.");
				} else
					layoutChildren();
			}
		}

		if (elementParent != null && !keyboardFocusEvent.isConsumed()) {
			// Pass the event to the parent
			elementParent.keyboardFocusChanged(keyboardFocusEvent);
		}
	}

	void keyEvent(KeyboardUIEvent keyboardUIEvent) {

		if (!keyboardUIEvent.isConsumed()) {
			if (keyboardSupport != null)
				keyboardSupport.fireEvent(keyboardUIEvent);
		}

		if (!keyboardUIEvent.isConsumed()) {
			// Still not consumed, fire to children
			for (BaseElement e : childList) {
				e.keyEvent(keyboardUIEvent);
				if (keyboardUIEvent.isConsumed())
					break;
			}
		}

		if (!keyboardUIEvent.isConsumed()) {
			// Still not consumed, fire to parents
			BaseElement el = elementParent;
			while (!keyboardUIEvent.isConsumed() && el != null) {
				if (el.keyboardSupport != null) {
					el.keyboardSupport.fireEvent(keyboardUIEvent);
				}
				el = el.elementParent;
			}
		}
	}

	private void addClippingDefine(ClippingDefine def) {
		if (ENABLE_CLIPPING) {
			if (!clippingLayers.contains(def))
				clippingLayers.add(def);
			else
				System.err.println("WARNING: " + def + " is already added to " + toString());
		}
	}

	/**
	 * Recursive call for properly showing children of the Element. I'm thinking
	 * this this needs to be a private method, however I need to verify this
	 * before I update it.
	 */
	private void childShow() {
		if (getTextElement() != null)
			getTextElement().setAlpha(getAlphaValue());

		if (!isVisible) {
			this.isVisible = true;
			controlShowHook();
			for (BaseElement el : childList) {
				el.childShow();
			}
			dirtyLayout(false, LayoutType.styling, LayoutType.clipping);
		}
	}

	private void doLayoutChildren() {
		onBeforeLayout();
		if (Boolean.TRUE.equals(reverseLayout.get())) {
			layoutHeirarchy(this);
			layoutThis();
		} else {
			layoutThis();
			layoutHeirarchy(this);
		}

		onAfterLayout();
	}

	private void propagateClippingLayerAdd(ClippingDefine def) {
		addClippingDefine(def);
		for (BaseElement c : childList) {
			c.propagateClippingLayerAdd(def);
		}
	}
}
