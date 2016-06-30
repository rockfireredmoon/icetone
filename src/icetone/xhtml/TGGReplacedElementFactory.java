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
import org.xhtmlrenderer.simple.extend.DefaultFormSubmissionListener;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

public class TGGReplacedElementFactory implements ReplacedElementFactory {

    private Map<Element, ReplacedElement> imageComponents = new HashMap<Element, ReplacedElement>();
    private FormSubmissionListener submissionListener;

    public TGGReplacedElementFactory() {
        submissionListener = new DefaultFormSubmissionListener();
    }

    public void clean() {
        reset();
    }

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

    public void remove(Element e) {
        imageComponents.remove(e);
    }

    public void setFormSubmissionListener(FormSubmissionListener listener) {
        submissionListener = listener;
    }

    public void reset() {
        imageComponents.clear();
    }

    protected ReplacedElement replaceImage(UserAgentCallback uac,
            LayoutContext context, Element elem, int cssWidth, int cssHeight) {
        ReplacedElement re = null;
        String imageSrc = context.getNamespaceHandler().getImageSourceURI(elem);

        if (imageSrc == null || imageSrc.length() == 0) {
            XRLog.layout(Level.WARNING, "No source provided for img element.");
            re = new TGGImageReplacedElement(new TGGFSImage(), cssWidth, cssHeight);
        } else if (ImageUtil.isEmbeddedBase64Image(imageSrc)) {
            TGGFSImage fsImage = (TGGFSImage) uac.getImageResource(imageSrc).getImage();
            if (fsImage != null) {
                re = new TGGImageReplacedElement(fsImage, cssWidth, cssHeight);
            }
        } else {
            re = lookupImageReplacedElement(elem);
            if (re == null) {
                FSImage fsImage = uac.getImageResource(imageSrc).getImage();
                if (fsImage != null) {
                    re = new TGGImageReplacedElement(new TGGFSImage(
                            (TGGFSImage) fsImage), cssWidth, cssHeight);
                } else {
                    re = new TGGImageReplacedElement(new TGGFSImage(), cssWidth,
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
