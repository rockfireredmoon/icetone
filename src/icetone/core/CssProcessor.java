package icetone.core;

import java.util.Arrays;
import java.util.List;

import org.xhtmlrenderer.css.extend.AttributeResolver;
import org.xhtmlrenderer.css.extend.TreeResolver;

public class CssProcessor implements TreeResolver, AttributeResolver {
	
	public enum PseudoStyle {
		hover, link, focus, active, visited
	}
	
	private Element element;
	private List<PseudoStyle> pseudoStyles;

	
	public PseudoStyle[] getPseudoStyles() {
		return pseudoStyles.toArray(new PseudoStyle[0]);
	}

	public void setPseudoStyles(PseudoStyle... pseudoStyles) {
		this.pseudoStyles = Arrays.asList(pseudoStyles);
	}

	@Override
	public boolean matchesElement(Object element, String namespaceURI, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLastChildElement(Object element) {
		Element el = (Element)element;
		Element par = el.getElementParent();
		if(par != null && par.getElementList().indexOf(el) == par.getElementList().size() - 1) {
			return true;
		}		
		return false;
	}

	@Override
	public boolean isFirstChildElement(Object element) {
		Element el = (Element)element;
		Element par = el.getElementParent();
		if(par != null && par.getElementList().indexOf(el) == 0) {
			return true;
		}		
		return false;
	}

	@Override
	public Object getPreviousSiblingElement(Object node) {
		Element el = (Element)element;
		Element par = el.getElementParent();
		if(par != null) {
			int idx = par.getElementList().indexOf(node);
			if(idx > 0) {
				return par.getElementList().get(idx - 1);
			}
		}	
		return null;
	}

	@Override
	public int getPositionOfElement(Object element) {
		Element el = (Element)element;
		Element par = el.getElementParent();
		if(par != null) {
			return par.getElementList().indexOf(element);
		}	
		return 0;
	}

	@Override
	public Object getParentElement(Object element) {
		return ((Element) element).getElementParent();
	}

	@Override
	public String getElementName(Object element) {
		return ((Element)element).getName();
	}
	
	@Override
	public boolean isVisited(Object e) {
		return pseudoStyles != null && pseudoStyles.contains(PseudoStyle.visited);
	}

	@Override
	public boolean isLink(Object e) {
		return pseudoStyles != null && pseudoStyles.contains(PseudoStyle.link);
	}

	@Override
	public boolean isHover(Object e) {
		return pseudoStyles != null && pseudoStyles.contains(PseudoStyle.hover);
	}

	@Override
	public boolean isFocus(Object e) {
		return pseudoStyles != null && pseudoStyles.contains(PseudoStyle.focus);
	}

	@Override
	public boolean isActive(Object e) {
		return pseudoStyles != null && pseudoStyles.contains(PseudoStyle.active);
	}

	@Override
	public String getNonCssStyling(Object e) {
		return null;
	}

	@Override
	public String getLang(Object e) {
		return null;
	}

	@Override
	public String getID(Object e) {
		return ((Element)e).getStyleId() == null ? ((Element)e).getUID() : ((Element)e).getStyleId();
	}

	@Override
	public String getElementStyling(Object e) {
		return ((Element) e).getCss();
	}

	@Override
	public String getClass(Object e) {
		Element el = (Element) e;
		if (el.getStyleClass() == null) {
			return el.getClass().getSimpleName();
		}
		return el.getStyleClass();
	}

	@Override
	public String getAttributeValue(Object e, String namespaceURI, String attrName) {
		return null;
	}

	@Override
	public String getAttributeValue(Object e, String attrName) {
		return null;
	}
}
