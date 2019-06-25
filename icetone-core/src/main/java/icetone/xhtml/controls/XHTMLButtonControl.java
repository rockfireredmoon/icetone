package icetone.xhtml.controls;

import org.w3c.dom.NodeList;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.controls.ButtonControl;

import com.jme3.texture.Image;

import icetone.controls.buttons.PushButton;
import icetone.core.BaseElement;
import icetone.xhtml.XHTMLFSImage;
import icetone.xhtml.XHTMLRenderer;

public class XHTMLButtonControl extends XHTMLControl {

	private Image _image = null;

	public XHTMLButtonControl(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac, BlockBox box) {
		super(control, parent, c, style, uac, box);
	}

	@Override
	protected BaseElement createElement(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac) {

		final ButtonControl bc = (ButtonControl) control;
		final PushButton button = new PushButton(parent.getScreen());
		button.onMouseReleased(evt -> {
			if (bc.getType().equals("submit")) {
				if (bc.press()) {
					bc.getForm().submit();
				}
			} else if (bc.getType().equals("reset")) {
				if (bc.press()) {
					bc.getForm().reset();
				}
			} else {
				bc.press();
			}
		});
		button.setText(bc.getLabel());
		if (bc.isExtended()) {
			// when defined with <button>, allow the first image to be used
			NodeList images = bc.getElement().getElementsByTagName("img");
			if (images.getLength() > 0) {
				org.w3c.dom.Element img = (org.w3c.dom.Element) images.item(0);
				String uri = c.getNamespaceHandler().getImageSourceURI(img);
				ImageResource res = uac.getImageResource(uri);
				XHTMLFSImage fsi = (XHTMLFSImage) res.getImage();
				// copy the image to prevent disposal, and apply a disabled
				// effect if needed
				button.getElementTexture().setImage(_image);
				// _image = new Image(button.getDisplay(), fsi.getImage(), (bc
				// .isEnabled() ? SWT.IMAGE_COPY : SWT.IMAGE_DISABLE));
			}
		}

		return button;
	}

}
