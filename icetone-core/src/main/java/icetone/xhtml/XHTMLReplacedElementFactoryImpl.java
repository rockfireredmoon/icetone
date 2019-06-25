package icetone.xhtml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.XhtmlForm;
import org.xhtmlrenderer.simple.xhtml.XhtmlNamespaceHandler;
import org.xhtmlrenderer.simple.xhtml.controls.ButtonControl;
import org.xhtmlrenderer.simple.xhtml.controls.CheckControl;
import org.xhtmlrenderer.simple.xhtml.controls.SelectControl;
import org.xhtmlrenderer.simple.xhtml.controls.TextControl;

import icetone.xhtml.controls.XHTMLButtonControl;
import icetone.xhtml.controls.XHTMLCheckControl;
import icetone.xhtml.controls.XHTMLFormControl;
import icetone.xhtml.controls.XHTMLSelectControl;
import icetone.xhtml.controls.XHTMLTextControl;

public class XHTMLReplacedElementFactoryImpl extends XHTMLReplacedElementFactory {

	private final XHTMLRenderer renderer;
	private Map<Element, XhtmlForm> forms = new HashMap<>();
	private Map<Element, ReplacedElement> controls = new HashMap<>();

	public XHTMLReplacedElementFactoryImpl(XHTMLRenderer renderer) {
		this.renderer = renderer;
	}

	public XhtmlForm getForm(Element e) {
		return forms.get(e);
	}

	@Override
	public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth,
			int cssHeight) {
		ReplacedElement replacedElement = super.createReplacedElement(c, box, uac, cssWidth, cssHeight);
		if (replacedElement == null && c.getNamespaceHandler() instanceof XhtmlNamespaceHandler && !c.isPrint()) {
			XhtmlNamespaceHandler nsh = (XhtmlNamespaceHandler) c.getNamespaceHandler();
			Element e = box.getElement();
			if (e == null) {
				return null;
			}

			if (controls != null) {
				replacedElement = controls.get(e);
			}
			if (replacedElement != null) {
				if (replacedElement instanceof XHTMLFormControlReplacementElement) {
					((XHTMLFormControlReplacementElement) replacedElement).calculateSize(c, box.getStyle(), cssWidth,
							cssHeight);
				}
				return replacedElement;
			}

			Element parentForm = getParentForm(e, c);
			XhtmlForm form = forms.get(parentForm);
			if (form == null) {
				form = nsh.createForm(parentForm);
				forms.put(parentForm, form);
			}

			FormControl control = form.createControl(e);
			if (control == null) {
				return null;
			}

			XHTMLFormControl formControl;
			if (control instanceof TextControl) {
				formControl = new XHTMLTextControl(control, renderer, c, box.getStyle(), uac, box);
			} else if (control instanceof ButtonControl) {
				formControl = new XHTMLButtonControl(control, renderer, c, box.getStyle(), uac, box);
			} else if (control instanceof CheckControl) {
				formControl = new XHTMLCheckControl(control, renderer, c, box.getStyle(), uac, box);
			} else if (control instanceof SelectControl) {
				formControl = new XHTMLSelectControl(control, renderer, c, box.getStyle(), uac, box);
			} else {
				return null;
			}
			XHTMLFormControlReplacementElement fcre = new XHTMLFormControlReplacementElement(formControl, renderer);
			fcre.calculateSize(c, box.getStyle(), cssWidth, cssHeight);
//			System.err.println("calcing size for " + fcre.getControl().getClass() + " - " + cssWidth + ", " + cssHeight
//					+ " = " + fcre.getControl().getUIElement().getDimensions());
			replacedElement = fcre;
			controls.put(e, replacedElement);
		}
		return replacedElement;
	}

	@Override
	public void remove(Element e) {
		super.remove(e);
		controls.remove(e);
		forms.remove(e);
	}

	@Override
	public void reset() {
		super.reset();
		forms.clear();
		controls.clear();
	}

	protected Element getParentForm(Element e, LayoutContext context) {
		Node node = e;
		XhtmlNamespaceHandler nsh = (XhtmlNamespaceHandler) context.getNamespaceHandler();

		do {
			node = node.getParentNode();
		} while (node.getNodeType() == Node.ELEMENT_NODE && !nsh.isFormElement((Element) node));

		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		}

		return (Element) node;
	}
}
