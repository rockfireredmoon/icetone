package icetone.xhtml;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.simple.extend.DefaultFormSubmissionListener;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class XHTMLReplacedElementFactory implements ReplacedElementFactory {

    private Map<Element, ReplacedElement> imageComponents = new HashMap<Element, ReplacedElement>();
    private FormSubmissionListener submissionListener;

    public XHTMLReplacedElementFactory() {
        submissionListener = new DefaultFormSubmissionListener();
    }

    public void clean() {
        reset();
    }

    @Override
	public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box,
            UserAgentCallback uac, int cssWidth, int cssHeight) {
        Element e = box.getElement();
        if (e == null) {
            return null;
        }

        if (c.getNamespaceHandler().isImageElement(e)) {
            return replaceImage(uac, c, e, cssWidth, cssHeight);
        }

        return null;
    }

    @Override
	public void remove(Element e) {
        imageComponents.remove(e);
    }

    @Override
	public void setFormSubmissionListener(FormSubmissionListener listener) {
        submissionListener = listener;
    }

    @Override
	public void reset() {
        imageComponents.clear();
    }

    protected ReplacedElement replaceImage(UserAgentCallback uac,
            LayoutContext context, Element elem, int cssWidth, int cssHeight) {
        ReplacedElement re = null;
        String imageSrc = context.getNamespaceHandler().getImageSourceURI(elem);

        if (imageSrc == null || imageSrc.length() == 0) {
            XRLog.layout(Level.WARNING, "No source provided for img element.");
            re = new XHTMLImageReplacedElement(new XHTMLFSImage(), cssWidth, cssHeight);
        } else if (ImageUtil.isEmbeddedBase64Image(imageSrc)) {
            XHTMLFSImage fsImage = (XHTMLFSImage) uac.getImageResource(imageSrc).getImage();
            if (fsImage != null) {
                re = new XHTMLImageReplacedElement(fsImage, cssWidth, cssHeight);
            }
        } else {
            re = lookupImageReplacedElement(elem);
            if (re == null) {
                ImageResource imageRes = uac.getImageResource(imageSrc);
				FSImage fsImage = imageRes == null ? null : imageRes.getImage();
                if (fsImage != null) {
                    re = new XHTMLImageReplacedElement(new XHTMLFSImage(
                            (XHTMLFSImage) fsImage), cssWidth, cssHeight);
                } else {
                    re = new XHTMLImageReplacedElement(new XHTMLFSImage(), cssWidth,
                            cssHeight);
                }
                storeImageReplacedElement(elem, re);
            }
        }
        return re;
    }

    protected void storeImageReplacedElement(Element e, ReplacedElement cc) {
        imageComponents.put(e, cc);
    }

    protected ReplacedElement lookupImageReplacedElement(Element e) {
        return imageComponents.get(e);
    }
}
