package icetone.css;

import java.util.Collection;

import org.xhtmlrenderer.css.sheet.Stylesheet;

import icetone.core.BaseElement;

public interface StylesheetProvider {

	Collection<Stylesheet> getStylesheets();

	BaseElement getElementByStyleId(String styleId);
}
