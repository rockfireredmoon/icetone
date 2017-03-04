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
package icetone.css;

import java.util.List;

import org.xhtmlrenderer.css.extend.AttributeResolver;
import org.xhtmlrenderer.css.extend.TreeResolver;
import org.xhtmlrenderer.css.newmatch.Selector;

import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.PseudoStyles;
import icetone.core.StyledNode;
import icetone.core.utils.ClassUtil;

//
// TODO why cant this be shared across all elements now?
//
public class CssProcessor implements TreeResolver, AttributeResolver {

	public final static CssProcessor DEFAULT = new CssProcessor();

	public enum PseudoStyle {
		hover, link, focus, active, visited;

		public void select(Selector selector) {
			switch (this) {
			case hover:
				selector.setPseudoClass(Selector.HOVER_PSEUDOCLASS);
				break;
			case focus:
				selector.setPseudoClass(Selector.FOCUS_PSEUDOCLASS);
				break;
			case active:
				selector.setPseudoClass(Selector.ACTIVE_PSEUDOCLASS);
				break;
			case visited:
				selector.setPseudoClass(Selector.VISITED_PSEUDOCLASS);
				break;
			case link:
				selector.addLinkCondition();
				break;
			}
		}
	}

	@Override
	public boolean matchesElement(Object element, String namespaceURI, String name) {
		return element instanceof StyledNode && ((StyledNode<?, ?>) element).getStyleClassNames().contains(name);
	}

	@Override
	public boolean isLastChildElement(Object element) {
		StyledNode<?, ?> el = (StyledNode<?, ?>) element;
		ElementContainer<?, ?> par = el.getParentContainer();
		if (par != null && par.getElements().indexOf(el) == par.getElements().size() - 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isFirstChildElement(Object element) {
		StyledNode<?, ?> el = (StyledNode<?, ?>) element;
		ElementContainer<?, ?> par = el.getParentContainer();
		if (par != null && par.getElements().indexOf(el) == 0) {
			return true;
		}
		return false;
	}

	@Override
	public Object getPreviousSiblingElement(Object element) {
		StyledNode<?, ?> el = (StyledNode<?, ?>) element;
		ElementContainer<?, ?> par = el.getParentContainer();
		if (par != null) {
			int idx = par.getElements().indexOf(element);
			if (idx > 0) {
				return par.getElements().get(idx - 1);
			}
		}
		return null;
	}

	@Override
	public int getPositionOfElement(Object element) {
		StyledNode<?, ?> el = (StyledNode<?, ?>) element;
		ElementContainer<?, ?> par = el.getParentContainer();
		if (par != null) {
			return par.getElements().indexOf(element);
		}
		return 0;
	}

	@Override
	public Object getParentElement(Object element) {
		StyledNode<?, ?> el = (StyledNode<?, ?>) element;
		ElementContainer<?, ?> par = el.getParentContainer();
		return par instanceof StyledNode<?, ?> ? par : null;
	}

	@Override
	public String getElementName(Object element) {
		return ClassUtil.getMainClassName(element.getClass());
	}

	@Override
	public boolean isVisited(Object e) {
		PseudoStyles ps = e instanceof StyledNode ? ((StyledNode<?, ?>) e).getPseudoStyles() : null;
		return ps != null && ps.contains(PseudoStyle.visited);
	}

	@Override
	public boolean isLink(Object e) {
		PseudoStyles ps = e instanceof StyledNode ? ((StyledNode<?, ?>) e).getPseudoStyles() : null;
		return ps != null && ps.contains(PseudoStyle.link);
	}

	@Override
	public boolean isHover(Object e) {
		PseudoStyles ps = e instanceof StyledNode ? ((StyledNode<?, ?>) e).getPseudoStyles() : null;
		return ps != null && ps.contains(PseudoStyle.hover);
	}

	@Override
	public boolean isFocus(Object e) {
		PseudoStyles ps = e instanceof StyledNode ? ((StyledNode<?, ?>) e).getPseudoStyles() : null;
		return ps != null && ps.contains(PseudoStyle.focus);
	}

	@Override
	public boolean isActive(Object e) {
		PseudoStyles ps = e instanceof StyledNode ? ((StyledNode<?, ?>) e).getPseudoStyles() : null;
		return ps != null && ps.contains(PseudoStyle.active);
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
		return ((StyledNode<?, ?>) e).getStyleId();
	}

	@Override
	public String getElementStyling(Object e) {
		return e instanceof StyledNode ? ((StyledNode<?, ?>) e).getCss() : null;
	}

	@Override
	public String getClass(Object e) {
		StyledNode<?, ?> el = (StyledNode<?, ?>) e;
		if (!(el instanceof StyledNode) || ((StyledNode) el).getStyleClass() == null) {
			/// return ClassUtil.getMainClassName(el.getClass());
			return null;
		}
		return el.getStyleClass();
	}

	@Override
	public String getAttributeValue(Object e, String namespaceURI, String attrName) {
		return getAttributeValue(e, attrName);
	}

	@Override
	public String getAttributeValue(Object e, String attrName) {
		StyledNode<?, ?> el = (StyledNode<?, ?>) e;
		if (attrName.equals("disabled")) {
			return String.valueOf(!el.isEnabled());
		} else if (attrName.equals("-it-css-event")) {
			List<CssEvent> activeEvents = el.getActiveEvents();
			if (activeEvents.isEmpty())
				return null;
			else {
				return activeEvents.get(0).toString();
			}
		}
		return null;
	}
}
