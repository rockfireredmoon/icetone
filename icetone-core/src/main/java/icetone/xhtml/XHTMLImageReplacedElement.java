package icetone.xhtml;

import java.awt.Point;

import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;

public class XHTMLImageReplacedElement implements ReplacedElement {

    private final XHTMLFSImage image;
    private Point location = new Point(0, 0);

    public XHTMLImageReplacedElement(XHTMLFSImage image, int targetWidth,
            int targetHeight) {
        this.image = image;
        if (targetWidth != image.getWidth() || targetHeight != image.getHeight()) {
            image.scale(targetWidth, targetHeight);
        }
    }

    public XHTMLFSImage getImage() {
        return image;
    }

    @Override
	public void detach(LayoutContext c) {
    }

    @Override
	public int getIntrinsicHeight() {
        return image.getHeight();
    }

    @Override
	public int getIntrinsicWidth() {
        return image.getWidth();
    }

    @Override
	public Point getLocation() {
        return location;
    }

    @Override
	public void setLocation(int x, int y) {
        location.setLocation(x, y);
    }

    @Override
	public boolean isRequiresInteractivePaint() {
        return true;
    }

    @Override
	public int getBaseline() {
        return 0;
    }

    @Override
	public boolean hasBaseline() {
        return false;
    }
}
