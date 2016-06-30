package icetone.xhtml;

import java.awt.Point;

import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;

public class TGGImageReplacedElement implements ReplacedElement {

    private final TGGFSImage image;
    private Point location = new Point(0, 0);

    public TGGImageReplacedElement(TGGFSImage image, int targetWidth,
            int targetHeight) {
        this.image = image;
        if (targetWidth != image.getWidth() || targetHeight != image.getHeight()) {
            image.scale(targetWidth, targetHeight);
        }
    }

    public TGGFSImage getImage() {
        return image;
    }

    public void detach(LayoutContext c) {
    }

    public int getIntrinsicHeight() {
        return image.getHeight();
    }

    public int getIntrinsicWidth() {
        return image.getWidth();
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(int x, int y) {
        location.setLocation(x, y);
    }

    public boolean isRequiresInteractivePaint() {
        return true;
    }

    public int getBaseline() {
        return 0;
    }

    public boolean hasBaseline() {
        return false;
    }
}
