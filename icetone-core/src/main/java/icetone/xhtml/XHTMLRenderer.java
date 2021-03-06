/*
 * {{{ header & license
 * Copyright (c) 2007 Vianney le Clément
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package icetone.xhtml;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.newmatch.CascadedStyle;
import org.xhtmlrenderer.css.sheet.PropertyDeclaration;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.event.DocumentListener;
import org.xhtmlrenderer.extend.FSCanvas;
import org.xhtmlrenderer.extend.NamespaceHandler;
import org.xhtmlrenderer.extend.TextRenderer;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.extend.UserInterface;
import org.xhtmlrenderer.layout.BoxBuilder;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.ViewportBox;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.NoNamespaceHandler;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.Uu;
import org.xhtmlrenderer.util.XRLog;
import org.xhtmlrenderer.util.XRLogger;
import org.xml.sax.InputSource;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

import icetone.controls.scrolling.ScrollPanel;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Measurement.Unit;
import icetone.core.ToolKit;
import icetone.core.event.ElementEvent.Type;
import icetone.css.CssUtil;

/**
 * Renders XML+CSS using TonegodGUI.
 */
public class XHTMLRenderer extends ScrollPanel implements UserInterface {

	class XHTMLRendererLayout extends AbstractGenericLayout<icetone.core.BaseElement, Object> {

		private Set<BaseElement> sized = new HashSet<>();

		@Override
		protected Vector2f calcPreferredSize(icetone.core.BaseElement parent) {
			return contentSize;
		}

		@Override
		protected void onLayout(icetone.core.BaseElement container) {
			for (BaseElement e : container.getElements()) {
				if (!sized.contains(e)) {
					sized.add(e);
					if (e.getDimensions().equals(Vector2f.ZERO)) {
						e.setDimensions(e.calcPreferredSize());
					}
				}
				e.updateNodeLocation();
			}
		}

		@Override
		protected void onRemove(BaseElement container) {
			sized.remove(container);
		}

	}

	private class RedrawNewOrigin extends SpecialRedraw {

		final Vector2f _previousOrigin;

		RedrawNewOrigin(Vector2f previousOrigin) {
			_previousOrigin = previousOrigin;
		}

		@Override
		boolean ignoreFurther() {
			return true;
		}

		@Override
		boolean isForFixedContent() {
			return false;
		}
	}

	private class RedrawNewSize extends SpecialRedraw {

		final Vector2f _previousSize;

		RedrawNewSize(Vector2f previousSize) {
			_previousSize = previousSize;
		}

		@Override
		boolean ignoreFurther() {
			return true;
		}

		@Override
		boolean isForFixedContent() {
			return false;
		}
	}

	private class RedrawTarget extends SpecialRedraw {

		final Rectangle _target;

		RedrawTarget(Rectangle target) {
			_target = target;
		}

		@Override
		boolean ignoreFurther() {
			return false;
		}

		@Override
		boolean isForFixedContent() {
			return true;
		}

		@Override
		void redraw() {
			// TGGRenderer.this.redraw(_target.x, _target.y, _target.width,
			// _target.height, true);
			XHTMLRenderer.this.redraw();
		}
	}

	/**
	 * Information about a special way of redrawing.
	 */
	private abstract class SpecialRedraw {

		/**
		 * @return <code>true</code> if special redraws of the same kind (but with other
		 *         parameters) should be ignored
		 */
		abstract boolean ignoreFurther();

		/**
		 * @return <code>true</code> if this special redraw method can also be applied
		 *         when there is fixed content
		 */
		abstract boolean isForFixedContent();

		/**
		 * Trigger redraw
		 */
		void redraw() {
			XHTMLRenderer.this.redraw();
		}
	}

	final static Logger LOG = Logger.getLogger(XHTMLRenderer.class.getName());
	private static final int PAGE_PAINTING_CLEARANCE = 10;
	static {
		XRLog.setLoggerImpl(new XRLogger() {
			@Override
			public void log(String where, Level level, String msg) {
				LOG.log(level, msg);
			}

			@Override
			public void log(String where, Level level, String msg, Throwable th) {
				LOG.log(level, msg, th);
			}

			@Override
			public void setLevel(String logger, Level level) {
				LOG.setLevel(level);
			}
		});
	}
	private Element activeElement = null;
	private ColorRGBA backgroundColor;
	private XHTMLCanvas canvas = null;
	private Vector2f contentSize = new Vector2f(0, 0);
	private Document document = null;
	private Set<DocumentListener> documentListeners = new HashSet<>();
	private boolean doLayout = false;
	private Element focusElement = null;
	private float fontScalingFactory = 1.2F;
	private ColorRGBA foregroundColor;
	private boolean hasFixedContent = false;
	private Element hoveredElement = null;
	private XHTMLCanvas layoutCanvas = null;
	private LayoutContext layoutContext;
	private float maxFontScale = 3.0F;
	private float minFontScale = 0.50F;
	private BlockBox rootBox = null;
	private ColorRGBA selectionBackgroundColor = ColorRGBA.Blue;
	private ColorRGBA selectionForegroundColor = ColorRGBA.White;
	private SharedContext sharedContext;
	private SpecialRedraw specialRedraw = null;

	/**
	 * Construct the renderer.
	 *
	 * @param screen screen
	 */
	public XHTMLRenderer(BaseScreen screen) {
		this(screen, new NaiveUserAgent(screen));
	}

	/**
	 * Construct the renderer.
	 *
	 * @param screen     screen
	 * @param styleId    style ID for styling
	 * @param position   position
	 * @param dimension  dimension
	 * @param borders    borders
	 * @param defaultImg default image
	 * @param uac        user agent
	 */
	public XHTMLRenderer(BaseScreen screen, UserAgentCallback uac) {
		super(screen);

		// Make this ScrollPane back into a rendered control
		// attachChildAt(getGeometry(), 0);

		sharedContext = new SharedContext(uac, new XHTMLFontResolver(this), new XHTMLReplacedElementFactory(),
				new XHTMLTextRenderer(this), Toolkit.getDefaultToolkit().getScreenResolution());
		sharedContext.setCanvas(new FSCanvas() {
			@Override
			public Rectangle getFixedRectangle() {
				return getClientArea();
			}

			@Override
			public int getX() {
				return (int) getScrollableAreaHorizontalPosition();
			}

			@Override
			public int getY() {
				return (int) getScrollableAreaVerticalPosition();
			}
		});

		// addDisposeListener(new DisposeListener() {
		// public void widgetDisposed(DisposeEvent e) {
		// // dispose used fonts
		// _sharedContext.flushFonts();
		// // clean ReplacedElementFactory
		// ReplacedElementFactory ref =
		// _sharedContext.getReplacedElementFactory();
		// if (ref instanceof TGGReplacedElementFactory) {
		// ((TGGReplacedElementFactory) ref).clean();
		// }
		// // dispose images when using NaiveUserAgent
		// UserAgentCallback uac = _sharedContext.getUac();
		// if (uac instanceof NaiveUserAgent) {
		// ((NaiveUserAgent) uac).disposeCache();
		// }
		// // dispose offscreen image
		// if (_offscreen != null) {
		// _offscreen.dispose();
		// }
		// // dispose temp Image/GC
		// if (_layout_image != null) {
		// _layout_gc.dispose();
		// _layout_image.dispose();
		// }
		// }
		// });
		// addKeyListener(new KeyAdapter() {
		// public void keyPressed(KeyEvent e) {
		// Point pt = new Point(_origin.x, _origin.y);
		// switch (e.keyCode) {
		// case SWT.ARROW_UP:
		// pt.y -= getVerticalBar().getIncrement();
		// break;
		// case SWT.ARROW_DOWN:
		// pt.y += getVerticalBar().getIncrement();
		// break;
		// case SWT.ARROW_LEFT:
		// pt.x -= getHorizontalBar().getIncrement();
		// break;
		// case SWT.ARROW_RIGHT:
		// pt.x += getHorizontalBar().getIncrement();
		// break;
		// case SWT.PAGE_UP:
		// pt.y -= getVerticalBar().getPageIncrement();
		// break;
		// case SWT.PAGE_DOWN:
		// pt.y += getVerticalBar().getPageIncrement();
		// break;
		// case SWT.HOME:
		// pt.x = 0;
		// pt.y = 0;
		// break;
		// case SWT.END:
		// pt.x = 0;
		// pt.y = _drawnSize.y; // will be fixed by setOrigin
		// break;
		// }
		// setOrigin(pt);
		// }
		// });

		// getScrollBounds().setClipPadding(new Vector4f(contentIndents.x,
		// contentIndents.w, contentIndents.y, contentIndents.z));
		canvas = new XHTMLCanvas(this);
		// setScrollContentLayout(new XYLayoutManager());

		setScrollContentLayout(new XHTMLRendererLayout());
		// setScrollContentLayout(new FixedLayoutManager());
		// updateScrollViewPort();

		// scrollableArea.setColorMap("Interface/bgw.jpg");
		// innerBounds.setColorMap("Interface/bgx.jpg");

		// This element isn't actually in the scene, it's just used to stored
		// selection styles
		icetone.core.Element el = new icetone.core.Element(screen);
		el.setStyleClass("xhtml-selection");
		el.setThemeInstance(ToolKit.get().getStyleManager().getDefaultInstance());
		CascadedStyle style = el.getCssState().getCascadedStyle(false);
		if (style != null) {
			PropertyDeclaration pd = style.propertyByName(CSSName.COLOR);
			selectionForegroundColor = CssUtil.toFontColor(pd, this);
			if (selectionForegroundColor == null)
				selectionForegroundColor = ColorRGBA.Blue;
			pd = style.propertyByName(CSSName.BACKGROUND_COLOR);
			selectionBackgroundColor = CssUtil.toFontColor(pd, this);
			if (selectionBackgroundColor == null)
				selectionBackgroundColor = ColorRGBA.Blue;
		}

		onElementEvent(evt -> {
			doLayout = true;
			checkDraw();
		}, Type.RESIZE);

		onElementEvent(evt -> {
			doLayout = true;
			checkDraw();
		}, Type.INITIALIZED);
		
	}

	public void addDocumentListener(DocumentListener listener) {
		documentListeners.add(listener);
	}

	/**
	 * Decrements all rendered fonts on the current document by the current scaling
	 * factor for the panel. Scaling applies culmulatively, which means that
	 * multiple calls to this method scale fonts smaller and smaller by applying the
	 * current scaling factor against itself. You can modify the scaling factor by
	 * {@link #setFontScalingFactor(float)}, and reset to the document's specified
	 * font size with {@link #resetFontSize()}.
	 */
	public void decrementFontSize() {
		scaleFont(1 / fontScalingFactory);
	}

	public Box find(int x, int y) {
		Layer l = getRootLayer();
		if (l != null) {
			Vector2f _origin = getOrigin();
			return l.find(layoutContext, x + (int) _origin.x, y + (int) _origin.y, false);
		}
		return null;
	}

	public Element getActive_element() {
		return activeElement;
	}

	public ColorRGBA getBackgroundColor() {
		return backgroundColor;
	}

	public Vector2f getContentSize() {
		return contentSize;
	}

	// private void paintPagedView(RenderingContext c, Layer root) {
	// if (root.getLastPage() == null) {
	// return;
	// }
	//
	// TGGOutputDevice out = (TGGOutputDevice) c.getOutputDevice();
	// TGGCanvas gc = out.getCanvas();
	// Shape working = out.getClip();
	//
	// List pages = root.getPages();
	// c.setPageCount(pages.size());
	// for (int i = 0; i < pages.size(); i++) {
	// PageBox page = (PageBox) pages.get(i);
	// c.setPage(i, page);
	//
	// java.awt.Rectangle overall = page.getScreenPaintingBounds(c,
	// PAGE_PAINTING_CLEARANCE);
	// overall.x -= 1;
	// overall.y -= 1;
	// overall.width += 1;
	// overall.height += 1;
	//
	// java.awt.Rectangle bounds = new java.awt.Rectangle(overall);
	// bounds.width += 1;
	// bounds.height += 1;
	// if (working.intersects(bounds)) {
	// page.paintBackground(c, PAGE_PAINTING_CLEARANCE,
	// Layer.PAGED_MODE_SCREEN);
	// page.paintMarginAreas(c, PAGE_PAINTING_CLEARANCE,
	// Layer.PAGED_MODE_SCREEN);
	// page.paintBorder(c, PAGE_PAINTING_CLEARANCE, Layer.PAGED_MODE_SCREEN);
	//
	// Color old = gc.getForeground();
	// gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
	// gc.drawRectangle(overall.x, overall.y, overall.width, overall.height);
	// gc.setForeground(old);
	//
	// java.awt.Rectangle content = page.getPagedViewClippingBounds(c,
	// PAGE_PAINTING_CLEARANCE);
	// out.clip(content);
	//
	// int left = PAGE_PAINTING_CLEARANCE
	// + page.getMarginBorderPadding(c, CalculatedStyle.LEFT);
	// int top = page.getPaintingTop()
	// + page.getMarginBorderPadding(c, CalculatedStyle.TOP) - page.getTop();
	//
	// out.translate(left, top);
	// root.paint(c);
	// out.translate(-left, -top);
	//
	// out.setClip(working);
	// }
	// }
	//
	// out.setClip(working);
	// }
	public Document getDocument() {
		return document;
	}

	public String getDocumentTitle() {
		if (document == null) {
			return null;
		}
		NamespaceHandler nsh = getSharedContext().getNamespaceHandler();
		if (nsh == null) {
			return null;
		}
		return nsh.getDocumentTitle(document);
	}

	public Element getFocus_element() {
		return focusElement;
	}

	public ColorRGBA getForegroundColor() {
		return foregroundColor;
	}

	public Element getHovered_element() {
		return hoveredElement;
	}

	public LayoutContext getLayoutContext() {
		return layoutContext;
	}

	/**
	 * Returns the maximum font scaling that may be applied, e.g. 3 times assigned
	 * font size.
	 */
	public float getMaxFontScale() {
		return maxFontScale;
	}

	/**
	 * Returns the minimum font scaling that may be applied, e.g. 0.5 times assigned
	 * font size.
	 */
	public float getMinFontScale() {
		return minFontScale;
	}

	// @Override
	// protected void onBeforeLayout() {
	// super.onBeforeLayout();
	// doLayout = true;
	// redraw();
	// }
	public Box getRootBox() {
		return rootBox;
	}

	public Layer getRootLayer() {
		return getRootBox() == null ? null : getRootBox().getLayer();
	}

	public ColorRGBA getSelectionBackgroundColor() {
		return selectionBackgroundColor;
	}

	public ColorRGBA getSelectionForegroundColor() {
		return selectionForegroundColor;
	}

	public SharedContext getSharedContext() {
		return sharedContext;
	}

	/**
	 * Increments all rendered fonts on the current document by the current scaling
	 * factor for the panel. Scaling applies culmulatively, which means that
	 * multiple calls to this method scale fonts larger and larger by applying the
	 * current scaling factor against itself. You can modify the scaling factor by
	 * {@link #setFontScalingFactor(float)}, and reset to the document's specified
	 * font size with {@link #resetFontSize()}.
	 */
	public void incrementFontSize() {
		scaleFont(fontScalingFactory);
	}

	@Override
	public boolean isActive(org.w3c.dom.Element e) {
		if (e == activeElement) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isFocus(org.w3c.dom.Element e) {
		if (e == focusElement) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isHover(org.w3c.dom.Element e) {
		if (e == hoveredElement) {
			return true;
		}
		return false;
	}

	public boolean isPrint() {
		return sharedContext.isPrint();
	}

	public void redraw() {
		draw();
		getScrollableArea().layoutChildren();
	}

	/**
	 * Reload the current document.
	 */
	public void reload() {
		if (document == null) {
			return;
		}
		rootBox = null;
		activeElement = null;
		hoveredElement = null;
		focusElement = null;
		if (Configuration.isTrue("xr.cache.stylesheets", true)) {
			sharedContext.getCss().flushStyleSheets();
		} else {
			sharedContext.getCss().flushAllStyleSheets();
		}

		// setCursor(null);
		sharedContext.reset();

		doLayout = true;
		checkDraw();

		// _origin = new Point(0, 0);
		// getHorizontalBar().setSelection(0);
		// getVerticalBar().setSelection(0);
	}

	public void removeDocumentListener(DocumentListener listener) {
		documentListeners.remove(listener);
	}

	/**
	 * Resets all rendered fonts on the current document to the font size specified
	 * in the document's styling instructions.
	 */
	public void resetFontSize() {
		getSharedContext().getTextRenderer().setFontScale(1f);
		reload();
	}

	public void setActive_element(Element active_element) {
		activeElement = active_element;
	}

	public void setBackgroundColor(ColorRGBA backgroundColor) {
		this.backgroundColor = backgroundColor;
		redraw();
	}

	@Override
	public void setBounds(Unit xUnit, float x, Unit yUnit, float y, float w, float h) {
		/*
		 * TODO. There must be a better solution than this. It is an attempt to avoid
		 * the chicken and situation where we can't get the preferred viewport size
		 * until the content has been laid out, but we can't lay the content out until
		 * we know how much space there is.
		 * 
		 * So we wait for the first setting of the bounds of the renderer which occurs
		 * during setup of the element hierarchy, and once this is some kind of size,
		 * layout the content. This lets us set an initial external preferred size for
		 * the content.
		 */
		Vector2f was = getDimensions().clone();
		super.setBounds(xUnit, x, yUnit, y, w, h);
		Vector2f now = getDimensions();
		if (isHeirarchyInitializing() && !was.equals(now) && now.x > 0 && now.y > 0) {
			xinvalidate();
		}
	}

	public void setDocument(Document doc, String url) {
		setDocument(doc, url, new NoNamespaceHandler());
	}

	public void setDocument(Document doc, String url, NamespaceHandler nsh) {
		rootBox = null;
		document = doc;

		activeElement = null;
		hoveredElement = null;
		focusElement = null;

		// have to do this first
		if (Configuration.isTrue("xr.cache.stylesheets", true)) {
			sharedContext.getCss().flushStyleSheets();
		} else {
			sharedContext.getCss().flushAllStyleSheets();
		}

		// setCursor(null);
		sharedContext.reset();

		// _origin = new Point(0, 0);
		// getHorizontalBar().setSelection(0);
		// getVerticalBar().setSelection(0);

		if (doc == null) {
			contentSize = new Vector2f(1, 1);
		} else {
			sharedContext.setBaseURL(url);
			sharedContext.setNamespaceHandler(nsh);
			sharedContext.getCss().setDocumentContext(sharedContext, sharedContext.getNamespaceHandler(), doc, this);
		}

		documentLoaded(url);
	}

	public void setDocument(InputStream stream, String url) {
		setDocument(stream, url, new NoNamespaceHandler());
	}

	public void setDocument(InputStream stream, String url, NamespaceHandler nsh) {
		Document dom = XMLResource.load(stream).getDocument();
		setDocument(dom, url, nsh);
	}

	public void setDocument(String url) {
		setDocument(loadDocument(url), url, new NoNamespaceHandler());
	}

	public void setDocument(String url, NamespaceHandler nsh) {
		setDocument(loadDocument(url), url, nsh);
	}

	public void setDocumentFromString(String content, String url, NamespaceHandler nsh) {
		InputSource is = new InputSource(new BufferedReader(new StringReader(content)));
		Document dom = XMLResource.load(is).getDocument();

		setDocument(dom, url, nsh);
	}

	/**
	 * Sets the new current document, where the new document is located relative,
	 * e.g using a relative URL.
	 *
	 * @param filename The new document to load
	 */
	public void setDocumentRelative(String filename) {
		String url = sharedContext.getUac().resolveURI(filename);
		if (isAnchorInCurrentDocument(filename)) {
			String id = getAnchorId(filename);
			Box box = sharedContext.getBoxById(id);
			if (box != null) {
				Vector2f pt;
				if (box.getStyle().isInline()) {
					pt = new Vector2f(0 /* box.getAbsX() */, box.getAbsY());
				} else {
					RectPropertySet margin = box.getMargin(layoutContext);
					pt = new Vector2f(0 /* box.getAbsX() + (int) margin.left() */, box.getAbsY() + (int) margin.top());
				}
				scrollYTo(pt.y);
				return;
			}
		}
		Document dom = loadDocument(url);
		setDocument(dom, url);
	}

	public void setFocus_element(Element focus_element) {
		focusElement = focus_element;
	}

	/**
	 * Sets the scaling factor used by {@link #incrementFontSize()} and
	 * {@link #decrementFontSize()}--both scale the font up or down by this scaling
	 * factor. The scaling roughly modifies the font size as a multiplier or
	 * divisor. A scaling factor of 1.2 applied against a font size of 10pt results
	 * in a scaled font of 12pt. The default scaling factor is 1.2F.
	 */
	public void setFontScalingFactor(float scaling) {
		fontScalingFactory = scaling;
	}

	public void setForegroundColor(ColorRGBA foregroundColor) {
		this.foregroundColor = foregroundColor;
		redraw();
	}

	public void setHovered_element(Element hovered_element) {
		hoveredElement = hovered_element;
	}

	/**
	 * Sets the maximum font scaling that may be applied, e.g. 3 times assigned font
	 * size. Calling incrementFontSize() after this scale has been reached doesn't
	 * have an effect.
	 */
	public void setMaxFontScale(float f) {
		maxFontScale = f;
	}

	/**
	 * Sets the minimum font scaling that may be applied, e.g. 3 times assigned font
	 * size. Calling decrementFontSize() after this scale has been reached doesn't
	 * have an effect.
	 */
	public void setMinFontScale(float f) {
		minFontScale = f;
	}

	public void setPrint(boolean print) {
		sharedContext.setPrint(print);
		sharedContext.setInteractive(!print);
		sharedContext.getReplacedElementFactory().reset();
		reload();
	}

	public void setSelectionBackgroundColor(ColorRGBA selectionBackgroundColor) {
		this.selectionBackgroundColor = selectionBackgroundColor;
	}

	public void setSelectionForegroundColor(ColorRGBA selectionForegroundColor) {
		this.selectionForegroundColor = selectionForegroundColor;
	}

	public void xinvalidate() {
		doLayout = true;
		checkDraw();
	}

	public void xinvalidate(java.awt.Rectangle rectangle) {
		java.awt.Rectangle r = getClientArea();
		redrawSpecial(new RedrawTarget(r.intersection(rectangle)));
	}

	protected void checkDraw() {
		boolean doDraw = doLayout;
		Layer root = maybeLayout();

		if (root == null) {
			XRLog.render(Level.FINE, "skipping the actual painting");
			canvas.reset();
			dirtyScrollContent();
			doDraw = false;
		}
		if (doDraw && root != null) {
			draw();
			dirtyScrollContent();
		}
	}

	protected void documentLoaded(String url) {
		doLayout = true;
		checkDraw();
		scrollToNewDocumentPosition(url);
	}

	protected void draw() {
		if (specialRedraw instanceof RedrawTarget) {
			// TODO partial redraws (and all the other types)
			canvas.reset();
			RenderingContext c = newRenderingContext(canvas);
			doRender(c);

		} else {
			canvas.reset();
			RenderingContext c = newRenderingContext(canvas);
			doRender(c);
		}
		// scrollableArea.setPreferredDimensions(contentSize);
		specialRedraw = null;
	}

	protected void fireDocumentLoaded() {
		Iterator<DocumentListener> it = documentListeners.iterator();
		while (it.hasNext()) {
			it.next().documentLoaded();
		}
	}

	protected void fireOnLayoutException(Throwable t) {
		Iterator<DocumentListener> it = documentListeners.iterator();
		while (it.hasNext()) {
			it.next().onLayoutException(t);
		}
	}

	protected void fireOnRenderException(Throwable t) {
		Iterator<DocumentListener> it = documentListeners.iterator();
		while (it.hasNext()) {
			it.next().onRenderException(t);
		}
	}

	protected Rectangle getClientArea() {
		Vector2f size = getClientSize();
		return new java.awt.Rectangle((int) size.x, (int) size.y);
	}

	protected final Vector2f getClientSize() {
		Vector2f size = getPreferredViewportSize();
		// size.subtractLocal(contentIndents.x + contentIndents.y,
		// contentIndents.z + contentIndents.w);
		if (size.x == 0 || size.y == 0) {
			size.x = 1;
			size.y = 1;
		}
		return size;
	}

	protected java.awt.Rectangle getInitialExtents(LayoutContext c) {
		if (!c.isPrint()) {
			return getClientArea();
		} else {
			PageBox first = Layer.createPageBox(c, "first");
			return new java.awt.Rectangle(0, 0, first.getContentWidth(c), first.getContentHeight(c));
		}
	}

	protected Document loadDocument(final String uri) {
		XMLResource xmlResource = sharedContext.getUac().getXMLResource(uri);
		if (xmlResource == null) {
			return null;
		}
		return xmlResource.getDocument();
	}

	protected LayoutContext newLayoutcontext() {
		LayoutContext result = sharedContext.newLayoutContextInstance();
		if (layoutCanvas == null) {
			layoutCanvas = new XHTMLCanvas(this);
		}
		result.setFontContext(new XHTMLFontContext(layoutCanvas));
		sharedContext.getTextRenderer().setup(result.getFontContext());

		return result;
	}

	protected RenderingContext newRenderingContext(XHTMLCanvas gc) {
		RenderingContext result = sharedContext.newRenderingContextInstance();
		result.setFontContext(new XHTMLFontContext(gc));
		result.setOutputDevice(new XHTMLOutputDevice(this, gc));
		sharedContext.getTextRenderer().setup(result.getFontContext());
		return result;
	}

	protected void onAfterScrollPanelLayout() {
		// LUtil.setY(scrollableArea, 0);

		// TODO dont think this is need, but if there are zorder probblems
		// applyZOrder();
	}

	protected Set<Box> removeContentInArea(Rectangle r) {
		List<icetone.core.BaseElement> toRemove = new ArrayList<icetone.core.BaseElement>();
		Set<Box> boxes = new LinkedHashSet<Box>();
		for (icetone.core.BaseElement e : getScrollableArea().getElements()) {
			Rectangle er = new Rectangle((Math.round(e.getX())), (Math.round(e.getY())), (Math.round(e.getWidth())),
					(Math.round(e.getHeight())));
			if (r.intersects(er)) {
				toRemove.add(e);
				Box box = find(er.x, er.y);
				if (box != null) {
					boxes.add(box);
				}
			}
		}
		invalidate();
		for (icetone.core.BaseElement e : toRemove) {
			removeScrollableContent(e);
		}
		validate();
		return boxes;
		// reshape();
	}

	protected void scrollToNewDocumentPosition(String url) {
		int anchorIdx = url == null ? -1 : url.lastIndexOf('#');
		if (anchorIdx != -1) {
			String anchor = url.substring(anchorIdx + 1);
			Box box = sharedContext.getBoxById(anchor);
			if (box != null) {
				Vector2f pt;
				if (box.getStyle().isInline()) {
					pt = new Vector2f(0, box.getAbsY());
				} else {
					RectPropertySet margin = box.getMargin(layoutContext);
					pt = new Vector2f(0, box.getAbsY() + (int) margin.top());
				}

				float y = pt.y;
				float sy = Math.abs(scrollableArea.getY());
				if (y + box.getHeight() > sy + getScrollBoundsHeight()) {
					scrollYTo(-y + getScrollBoundsHeight() - box.getHeight());
				} else {
					if (y < sy) {
						scrollYTo(-y);
					}
				}
				return;
			}
		}

		scrollToTop();
		scrollToLeft();
	}

	private void doLayout() {
		XRLog.render(Level.FINE, "laying out");
		if (document == null || !isInStyleHierarchy()) {
			return;
		}

		layoutContext = newLayoutcontext();

		try {
			long start = System.currentTimeMillis();

			if (rootBox != null && doLayout) {
				rootBox.reset(layoutContext);
			} else {
				rootBox = BoxBuilder.createRootBox(layoutContext, document);
			}
			final Rectangle initialExtents = getInitialExtents(layoutContext);

			rootBox.setContainingBlock(new ViewportBox(initialExtents));
			rootBox.layout(layoutContext);

			long end = System.currentTimeMillis();
			XRLog.layout(Level.INFO, "Layout took " + (end - start) + "ms");
		} catch (Throwable e) {
			e.printStackTrace();
			XRLog.exception(e.getMessage(), e);
		}

		Layer rootLayer = rootBox.getLayer();
		hasFixedContent = rootLayer.containsFixedContent();

		XRLog.layout(Level.FINEST, "after layout: " + rootBox);

		// update scrollbars
		Dimension intrinsic_size = rootLayer.getPaintingDimension(layoutContext);
		if (layoutContext.isPrint()) {
			rootLayer.trimEmptyPages(layoutContext, intrinsic_size.height);
			if (rootLayer.getLastPage() != null) {
				rootLayer.assignPagePaintingPositions(layoutContext, Layer.PAGED_MODE_SCREEN, PAGE_PAINTING_CLEARANCE);
				contentSize = new Vector2f(rootLayer.getMaxPageWidth(layoutContext, PAGE_PAINTING_CLEARANCE),
						rootLayer.getLastPage().getPaintingBottom() + PAGE_PAINTING_CLEARANCE);
			} else {
				contentSize = new Vector2f(0, 0);
			}
		} else {
			contentSize = new Vector2f(intrinsic_size.width, intrinsic_size.height);
		}

		doLayout = false;

		// TODO call only once? in display.asyncExec?
		fireDocumentLoaded();
	}

	private void doRender(RenderingContext c) {
		// doRender(c, rootBox.getLayer(), new Rectangle(0, 0, (int)
		// (contentSize.x - contentIndents.x - contentIndents.y),
		// (int) (Math.max(contentSize.y, getScrollBoundsHeight()) -
		// contentIndents.y - contentIndents.w)));
		doRender(c, rootBox.getLayer(),
				new Rectangle(0, 0, (int) (contentSize.x), (int) (Math.max(contentSize.y, getScrollBoundsHeight()))));
	}

	private void doRender(RenderingContext c, Layer layer, Rectangle bgBounds) {
		try {
			invalidate();
			// LUtil.setDimensions(scrollableArea, w, h);
			XRLog.log("FS", Level.FINE, String.format("Rendering layer %s in %s", toString(), bgBounds));
			// c.getOutputDevice().translate(contentIndents.x,
			// contentIndents.z);
			long start = System.currentTimeMillis();
			if (backgroundColor != null) {
				canvas.setBackground(backgroundColor);
				canvas.fillRectangle(bgBounds.x, bgBounds.y, bgBounds.width, bgBounds.height);
			}
			canvas.setForeground(foregroundColor);
			layer.paint(c);
			long after = System.currentTimeMillis();
			if (Configuration.isTrue("xr.incremental.repaint.print-timing", false)) {
				Uu.p("repaint took ms: " + (after - start));
			}
		} catch (Throwable e) {
			XRLog.exception(e.getMessage(), e);
		} finally {
			validate();
			// c.getOutputDevice().translate(-contentIndents.x,
			// -contentIndents.z);
		}
	}

	private String getAnchorId(String url) {
		return url.substring(1, url.length());
	}

	private boolean isAnchorInCurrentDocument(String str) {
		return str.charAt(0) == '#';
	}

	private Layer maybeLayout() {
		Layer root = getRootLayer();
		if (root == null || doLayout) {
			doLayout();
			root = getRootLayer();
		}
		return root;
	}

	private void redrawSpecial(SpecialRedraw type) {
		if (hasFixedContent && !type.isForFixedContent()) {
			xinvalidate();
		} else if (specialRedraw == null) {
			specialRedraw = type;
			specialRedraw.redraw();
		} else if (specialRedraw.getClass().equals(type.getClass()) && specialRedraw.ignoreFurther()) {
			specialRedraw.redraw();
		} else {
			xinvalidate();
		}
	}

	/**
	 * Applies a change in scale for fonts using the rendering context's text
	 * renderer.
	 */
	private void scaleFont(float scaleBy) {
		TextRenderer tr = getSharedContext().getTextRenderer();
		float fs = tr.getFontScale() * scaleBy;
		if (fs < minFontScale || fs > maxFontScale) {
			return;
		}
		tr.setFontScale(fs);
		reload();
	}
}
