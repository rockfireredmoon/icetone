package icetone.core.layout.loader;

import java.util.ArrayList;
import java.util.List;

import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.asset.AssetKey;

import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Element;

public abstract class AbstractElementLayoutPart<E extends BaseElement> implements LayoutPart<E> {

	protected List<AbstractElementLayoutPart<?>> children = new ArrayList<>();
	protected LayoutLayoutPart layout;
	
	private String id;
	private String style;
	private String text;
	private String css;
	private String stylesheet;
	private List<String> stylesheets;
	private Object constraints;
	private String toolTipText;

	@Override
	public E createPart(ElementManager<?> screen, LayoutContext ctx) {
		E thisEl = createThisElement(screen, ctx);
		addChildren(screen, ctx, thisEl);
		return thisEl;
	}

	protected void addChildren(ElementManager<?> screen, LayoutContext ctx, E thisEl) {
		for (AbstractElementLayoutPart<?> part : children) {
			thisEl.addElement(part.createPart(screen, ctx));
		}
	}

	protected final E createThisElement(ElementManager<?> screen, LayoutContext ctx) {
		E el = createElementObject(screen);
		configureThisElement(el, ctx);
		return el;
	}

	protected abstract E createElementObject(ElementManager<?> screen);

	protected void configureThisElement(E el, LayoutContext ctx) {
		el.setConstraints(constraints);
		if (id != null)
			el.setStyleId(id);
		if (style != null && el instanceof Element)
			((Element)el).setStyleClass(style);
		if (text != null)
			el.setText(text);
		configureLayout(el, ctx);
		if (css != null && el instanceof Element)
			((Element)el).setCss(css);
		if (toolTipText != null)
			el.setToolTipText(toolTipText);
		if (stylesheet != null || (stylesheets != null && !stylesheets.isEmpty())) {
			if (stylesheet != null)
				if (el instanceof Element)
					((Element) el).addStylesheet(getStyleSheet(el, stylesheet));
			if (stylesheets != null)
				for (String sheet : stylesheets) {
					if (el instanceof Element)
						((Element) el).addStylesheet(getStyleSheet(el, sheet));
				}

		}
		ctx.configure(el);
	}

	protected void configureLayout(E el, LayoutContext ctx) {
		if (layout != null)
			el.setLayoutManager(layout.createPart(el.getScreen(), ctx));
	}

	private Stylesheet getStyleSheet(BaseElement el, String styleSheet2) {
		return el.getScreen().getApplication().getAssetManager().loadAsset(new AssetKey<Stylesheet>(styleSheet2));
	}

	public List<AbstractElementLayoutPart<?>> getChildren() {
		return children;
	}

	public void setChildren(List<AbstractElementLayoutPart<?>> children) {
		this.children = children;
	}

	public LayoutLayoutPart getLayout() {
		return layout;
	}

	public void setLayout(LayoutLayoutPart layout) {
		this.layout = layout;
	}

	public Object getConstraints() {
		return constraints;
	}

	public void setConstraints(Object constraints) {
		this.constraints = constraints;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public String getStylesheet() {
		return stylesheet;
	}

	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}

	public List<String> getStylesheets() {
		return stylesheets;
	}

	public void setStylesheets(List<String> stylesheets) {
		this.stylesheets = stylesheets;
	}

	public String getToolTipText() {
		return toolTipText;
	}

	public void setToolTipText(String tooltipText) {
		this.toolTipText = tooltipText;
	}

}
