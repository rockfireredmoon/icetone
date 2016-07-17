package icetone.core.layout.loader;

import java.util.ArrayList;
import java.util.List;

import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.asset.AssetKey;

import icetone.core.Element;
import icetone.core.ElementManager;

public abstract class AbstractElementLayoutPart<E extends Element> implements LayoutPart<E> {
	
	private List<AbstractElementLayoutPart<?>> children = new ArrayList<>();
	private String id;
	private String style;
	private String text;
	private String css;
	private String stylesheet;
	private List<String> stylesheets;
	private LayoutLayoutPart layout;
	private Object constraints;
	private List<String> events;
	
	@Override
	public E createPart(ElementManager screen, LayoutContext ctx) {
		E thisEl = createThisElement(screen, ctx);
		for(AbstractElementLayoutPart<?> part : children) {
			thisEl.addChild(part.createPart(screen, ctx));
		}
		return thisEl;
	}
	
	protected final E createThisElement(ElementManager screen, LayoutContext ctx) {
		E el = createElementObject(screen);
		configureThisElement(el, ctx);
		return el;
	}

	protected abstract E createElementObject(ElementManager screen);

	protected void configureThisElement(E el, LayoutContext ctx) {
		el.setConstraints(constraints);
		if(id != null)
			el.setStyleId(id);
		if(style != null)
			el.setStyleClass(style);
		if(text != null)
			el.setText(text);
		if(layout != null)
			el.setLayoutManager(layout.createPart(el.getScreen(), ctx));
		if(css != null)
			el.setCss(css);
		if(stylesheet != null || (stylesheets != null && !stylesheets.isEmpty())) {
			if(stylesheet != null)
				el.addStylesheet(getStyleSheet(el, stylesheet));
			if(stylesheets != null)
				for(String sheet : stylesheets) {
					el.addStylesheet(getStyleSheet(el, sheet));
				}
			
		}
		if(events != null) {
			for(String evt : events) {
				ctx.bind(el, evt);
			}
		}
	}

	private Stylesheet getStyleSheet(Element el, String styleSheet2) {
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

	public List<String> getEvents() {
		return events;
	}

	public void setEvents(List<String> events) {
		this.events = events;
	}

}
