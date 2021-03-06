package icetone.xhtml;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.xhtml.FormListener;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.simple.xhtml.XhtmlNamespaceHandler;
import org.xhtmlrenderer.util.Configuration;
import org.xml.sax.InputSource;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.math.Vector2f;

import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.css.StyleManager;

/**
 * Add mouse handling, namespace handling and convience methods to the basic
 * {@link XHTMLRenderer} in general this is the element you should add to your
 * GUI.
 */
public class XHTMLDisplay extends XHTMLRenderer {

	private Box previousBox = null;
	private XHTMLSelectionHighlighter highlighter;

	public static class Link {

		private String uri;
		private String target;

		public Link() {
		}

		public Link(String uri) {
			this.uri = uri;
		}

		public String getUri() {
			return uri;
		}

		public String getTarget() {
			return target;
		}

	}

	public XHTMLDisplay() {
		this(BaseScreen.get());
	}

	public XHTMLDisplay(BaseScreen screen) {
		this(screen, new XHTMLUserAgent(screen));
	}

	public XHTMLDisplay(UserAgentCallback uac) {
		this(BaseScreen.get(), uac);
	}

	public XHTMLDisplay(BaseScreen screen, UserAgentCallback uac) {
		super(screen, uac);
		init();
	}

	/**
	 * Display an error page. Expects a template XHTML file on the classpath at
	 * <strong>/resources/conf/error-[code].xhtml</strong>. If this does not
	 * exist, the generic <strong>/resources/conf/error.xhtml</strong> will be
	 * looked for. If this does not exist, an exception will be thrown.
	 *
	 * @param exception
	 *            exception to display
	 * @see #errorPage(int, java.lang.String, java.lang.Throwable)
	 */
	public void errorPage(Throwable exception) {
		errorPage(0, exception.getMessage(), exception);
	}

	/**
	 * Display an error page. Expects a template XHTML file on the classpath at
	 * <strong>/resources/conf/error-[code].xhtml</strong>. If this does not
	 * exist, the generic <strong>/resources/conf/error.xhtml</strong> will be
	 * looked for. If this does not exist, an exception will be thrown.
	 *
	 * @param message
	 *            message (or <code>null</code> if no message)
	 * @see #errorPage(int, java.lang.String, java.lang.Throwable)
	 */
	public void errorPage(String message) {
		errorPage(0, message, null);
	}

	/**
	 * Display an error page. Expects a template XHTML file on the classpath at
	 * <strong>/resources/conf/error-[code].xhtml</strong>. If this does not
	 * exist, the generic <strong>/resources/conf/error.xhtml</strong> will be
	 * looked for. If this does not exist, an exception will be thrown.
	 *
	 * @param code
	 *            error code
	 * @param message
	 *            message (or <code>null</code> if no message)
	 * @param exception
	 *            exception to display
	 */
	public void errorPage(int code, String message, Throwable exception) {

		String errPage = Configuration.valueFor("xr.load.error-pages") + "/error-" + code + ".xhtml";

		AssetInfo info = ToolKit.get().getApplication().getAssetManager().locateAsset(new AssetKey<String>(errPage));
		if (info == null) {
			errPage = Configuration.valueFor("xr.load.error-pages") + "/error.xhtml";
			info = ToolKit.get().getApplication().getAssetManager().locateAsset(new AssetKey<String>(errPage));
			if (info == null) {
				throw new RuntimeException("No resource " + Configuration.valueFor("xr.load.error-pages")
						+ "/error.xhtml could " + "be found. This is needed a template to use for the error page."
						+ "Create one, and use ${errorMessage}, ${errorTrace} and ${errorCode} as "
						+ "place holders in the content. These will get replaced at runtime.");
			}
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(info.openStream()));
			StringBuilder bui = new StringBuilder();
			try {
				String line;
				while ((line = reader.readLine()) != null) {
					bui.append(line);
				}
			} finally {
				reader.close();
			}

			// Replace the content
			String content = bui.toString().replace("${errorCode}", code == 0 ? "" : String.valueOf(code));
			content = content.replace("${errorMessage}", message == null ? "" : message);
			if (exception != null) {
				StringWriter traceWriter = new StringWriter();
				exception.printStackTrace(new PrintWriter(traceWriter));
				content = content.replace("${errorTrace}", traceWriter.toString());
			}

			ByteArrayInputStream bain = new ByteArrayInputStream(content.getBytes("UTF-8"));
			setDocument(bain, errPage);
		} catch (IOException ioe) {
			throw new RuntimeException("Could not read error page.");
		}
	}

	public void setSelectable(boolean selectable) {
		boolean wasSelectable = highlighter != null;
		if (wasSelectable != selectable) {
			if (selectable) {
				highlighter = new XHTMLSelectionHighlighter();
				highlighter.install(this);
			} else {
				if (highlighter != null)
					highlighter.deinstall(this);
				highlighter = null;
			}
		}
	}

	@Override
	public void setDocument(String uri) {
		setDocument(loadDocument(uri), uri);
	}

	public void setDocument(Document doc) {
		setDocument(doc, "");
	}

	@Override
	public void setDocument(Document doc, String url) {
		super.setDocument(doc, url, createNamespaceHandler());
	}

	public void setDocumentFromString(String content, String url) {
		InputSource is = new InputSource(new BufferedReader(new StringReader(content)));
		Document dom = XMLResource.load(is).getDocument();
		setDocument(dom, url, createNamespaceHandler());
	}

	@Override
	public void setDocument(InputStream stream, String url) {
		super.setDocument(stream, url, createNamespaceHandler());
	}

	/**
	 * Get the form an element is part of.
	 *
	 * @param el
	 *            element
	 * @return form
	 */
	public XhtmlForm getForm(Element el) {
		ReplacedElementFactory ref = getSharedContext().getReplacedElementFactory();
		if (ref != null && ref instanceof XHTMLReplacedElementFactoryImpl) {
			return ((XHTMLReplacedElementFactoryImpl) ref).getForm(el);
		}
		return null;
	}

	protected void onInit() {
		// For sub-classes to override
	}

	protected void onHover(Element el) {
		// For sub-classes to override
	}

	protected void linkClicked(Link link) {
		setDocumentRelative(link.getUri());
	}

	protected Link findLink(Vector2f pos) {
		Box box = find((int) pos.x, (int) pos.y);
		if (box == null) {
			return null;
		}
		Element elem = box.getElement();
		if (elem == null) {
			return null;
		}

		return findLink(elem);
	}

	protected Link findLink(Element e) {
		Link link = null;

		for (Node node = e; node.getNodeType() == Node.ELEMENT_NODE; node = node.getParentNode()) {
			String uri = getSharedContext().getNamespaceHandler().getLinkUri((Element) node);
			if (uri != null) {
				link = new Link();
				link.uri = uri;
				link.target = getSharedContext().getNamespaceHandler().getAttributeValue((Element) node, "target");
				break;
			}
		}

		return link;
	}

	protected void onFormSubmit(XhtmlForm form) {
	}

	protected void onFormReset(XhtmlForm form) {
	}

	protected XhtmlNamespaceHandler createNamespaceHandler() {
		return new XhtmlNamespaceHandler() {
			@Override
			public XhtmlForm createForm(Element e) {
				final XhtmlForm createForm = super.createForm(e);
				createForm.addFormListener(new FormListener() {
					@Override
					public void resetted(XhtmlForm xf) {
						onFormReset(xf);
					}

					@Override
					public void submitted(XhtmlForm xf) {
						onFormSubmit(xf);
					}
				});
				return createForm;
			}
		};
	}

	private void init() {
		setIgnoreGlobalAlpha(true);
		innerBounds.setIgnoreMouse(false);
		scrollableArea.setIgnoreMouse(true);
		getSharedContext().setReplacedElementFactory(new XHTMLReplacedElementFactoryImpl(this));
		onInit();

		innerBounds.onMouseReleased(evt -> {
			Link uri = findLink(new Vector2f(evt.getRelx(), evt.getRely()));
			if (uri != null) {
				linkClicked(uri);
			}
		});

		innerBounds.onMouseMoved(evt -> {
			Vector2f p = new Vector2f(evt.getRelx(), evt.getRely() + getScrollableAreaVerticalPosition());
			mouseHover(p);
			mouseCursor(p);
		});
	}

	private Element getHoveredElement(StyleReference style, Box ib) {
		if (ib == null) {
			return null;
		}

		Element element = ib.getElement();

		while (element != null && !style.isHoverStyled(element)) {
			Node node = element.getParentNode();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				element = (Element) node;
			} else {
				element = null;
			}
		}

		return element;
	}

	private void mouseHover(Vector2f xy) {
		LayoutContext c = getLayoutContext();
		if (c == null) {
			return;
		}
		Box box = find((int) xy.x, (int) xy.y);
		Element previous = getHovered_element();
		Element current = getHoveredElement(c.getCss(), box);
		if (previous == current) {
			return;
		}
		setHovered_element(current);
		boolean needRepaint = false;
		boolean targetedRepaint = true;
		Rectangle repaintTarget = null;
		if (previous != null) {
			needRepaint = true;
			previousBox.restyle(c);

			PaintingInfo paintInfo = previousBox.getPaintingInfo();
			if (paintInfo == null) {
				targetedRepaint = false;
			} else {
				java.awt.Rectangle rect = paintInfo.getAggregateBounds();
				repaintTarget = new Rectangle(rect.x, rect.y, rect.width, rect.height);
			}

			previousBox = null;
		}
		if (current != null) {
			needRepaint = true;
			Box target = box.getRestyleTarget();
			target.restyle(c);

			if (targetedRepaint) {
				PaintingInfo paintInfo = target.getPaintingInfo();

				if (paintInfo == null) {
					targetedRepaint = false;
				} else {
					if (repaintTarget == null) {
						java.awt.Rectangle rect = paintInfo.getAggregateBounds();
						repaintTarget = new Rectangle(rect.x, rect.y, rect.width, rect.height);
					} else {
						java.awt.Rectangle rect = paintInfo.getAggregateBounds();
						repaintTarget.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
					}
				}
			}

			previousBox = box;
		}
		if (needRepaint) {
			if (targetedRepaint) {
				Vector2f origin = getOrigin();
				repaintTarget.x -= origin.x;
				repaintTarget.y -= origin.y;
				xinvalidate(repaintTarget);
			} else {
				xinvalidate();
			}
		}
		onHover(current);
	}

	private void mouseCursor(Vector2f pos) {
		Box box = find((int) pos.x, (int) pos.y);
		if (box == null) {
			return;
		}

		FSDerivedValue value = box.getStyle().valueByName(CSSName.CURSOR);
		StyleManager.CursorType cursor = StyleManager.CursorType.POINTER;

		if (value == IdentValue.AUTO || value == IdentValue.DEFAULT) {
			cursor = StyleManager.CursorType.POINTER;
		} else if (value == IdentValue.POINTER) {
			cursor = StyleManager.CursorType.HAND;
		} else if (value == IdentValue.MOVE) {
			cursor = StyleManager.CursorType.HAND;
		} else if (value == IdentValue.E_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_EW;
		} else if (value == IdentValue.NE_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_CNE;
		} else if (value == IdentValue.NW_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_CNW;
		} else if (value == IdentValue.N_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_NS;
		} else if (value == IdentValue.SE_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_CNW;
		} else if (value == IdentValue.SW_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_CNE;
		} else if (value == IdentValue.S_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_NS;
		} else if (value == IdentValue.W_RESIZE) {
			cursor = StyleManager.CursorType.RESIZE_EW;
		} else if (value == IdentValue.TEXT) {
			cursor = StyleManager.CursorType.TEXT;
			// Unsupported for now
		} else if (value == IdentValue.WAIT) {
		} else if (value == IdentValue.HELP) {
		} else if (value == IdentValue.PROGRESS) {
		} else if (value == IdentValue.CROSSHAIR) {
		}

		setCursor(cursor);
	}
}
