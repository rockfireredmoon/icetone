package icetone.core.layout.loader;

import java.util.Collection;

import org.xhtmlrenderer.css.sheet.Stylesheet;

import icetone.core.BaseElement;

public interface LayoutContext {
	
	void init(LayoutAssetKey layoutAsset);
	
	Collection<Stylesheet> getStylesheets();

	void configure(BaseElement el);
	
	void end(BaseElement root);

}
