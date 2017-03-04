package icetone.xhtml;

import java.awt.image.BufferedImage;

import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.UserAgentCallback;

import com.jme3.texture.Image;
import com.jme3.texture.plugins.AWTLoader;

import jme3tools.converters.ImageToAwt;

/**
 * {@link FSImage} implemtation that bridges JME images with flying saucer's. 
 */
public class XHTMLFSImage implements FSImage {

    private final String uri;
    private Image image;
    private final UserAgentCallback agent;

    public XHTMLFSImage() {
        this(null, null, null);
    }

    public XHTMLFSImage(Image image, UserAgentCallback agent, String uri) {
        this.agent = agent;
        this.uri = uri;
        this.image = image;
    }

    public XHTMLFSImage(XHTMLFSImage image) {
        agent = image.agent;
        uri = image.uri;
        this.image = image.image;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public int getHeight() {
        return image == null ? 10 : image.getHeight();
    }

    @Override
    public int getWidth() {
        return image == null ? 10 : image.getWidth();
    }

    @Override
	public void scale(int width, int height) {
        if (width < 0 && height < 0) {
            return;
        } else if (width < 0) {
            width = Math.round(width
                    * (height == 0 ? 1 : ((float) height / height)));
        } else if (height < 0) {
            height = Math.round(height
                    * (width == 0 ? 1 : ((float) width / width)));
        }
        BufferedImage bim = ImageToAwt.convert(image, false, true, 0);
        System.err.println("scaling image from " + image.getWidth() + " x " + image.getHeight() + " to " + width + " x " + height);
        BufferedImage tbim = new BufferedImage(width, height, bim.getType());
        tbim.getGraphics().drawImage(bim, 0, 0, width, height, null);
        image = new AWTLoader().load(tbim, false);
    }
}
