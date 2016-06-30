package icetone.xhtml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

import icetone.core.ElementManager;
import icetone.core.Screen;

/**
 * User agent appropriate for use with JME3. This loads things through the
 * {@link AssetManager}.
 */
public class TGGUserAgent implements UserAgentCallback {
    private static final Logger LOG = Logger.getLogger(TGGUserAgent.class.getName());

    private String baseUrl;
    private final ElementManager screen;

    public TGGUserAgent(ElementManager screen) {
        this.screen = screen;
    }

    protected InputStream getInputStream(String uri) {
        try {
            int l = uri.lastIndexOf('#');
            if(l != -1) {
                uri = uri.substring(0, l);
            }
            while(uri.startsWith("/")) {
        	uri = uri.substring(1);
            }
            AssetInfo info = screen.getApplication().getAssetManager().locateAsset(new AssetKey<String>(uri));
            return info.openStream();
        } catch (AssetNotFoundException anfe) {
            XRLog.exception(String.format("Item at URI %s not found", uri));
        } catch (Exception e) {
            XRLog.exception(String.format("Failed to load %s", uri), e);
        }
        return null;
    }

    public CSSResource getCSSResource(String uri) {
        return new CSSResource(getInputStream(uri));
    }

    public ImageResource getImageResource(String uri) {
        if (ImageUtil.isEmbeddedBase64Image(uri)) {
            return loadEmbeddedBase64ImageResource(uri);
        } else {
            uri = resolveURI(uri);
            try {
                Texture tex = screen.getApplication().getAssetManager().loadTexture(new TextureKey(uri));
                return new ImageResource(uri, new TGGFSImage(tex.getImage(), this, uri));
            } catch (AssetNotFoundException anfe) {
                XRLog.exception(String.format("Image at URI %s not found", uri));
            } catch (Exception e) {
                XRLog.exception(String.format("Failed to load %s", uri), e);
            }
            return null;
        }
    }

    protected ImageResource createImageResource(String uri, final InputStream is) {
        try {
            Class<? extends AssetLoader> loaderClazz;
            if (Screen.isAndroid()) {
                loaderClazz = (Class<? extends AssetLoader>) getClass().getClassLoader().loadClass("com.jme3.texture.plugins.AndroidImageLoader");
            } else {
                loaderClazz = (Class<? extends AssetLoader>) getClass().getClassLoader().loadClass("com.jme3.texture.plugins.AWTLoader");
            }
            Image img = (Image) loaderClazz.newInstance().load(new AssetInfo(screen.getApplication().getAssetManager(), new AssetKey(uri)) {
                @Override
                public InputStream openStream() {
                    return is;
                }
            });
            return new ImageResource(uri, new TGGFSImage(img, this, uri));
        } catch (Exception ex) {
            throw new AssetLoadException("Failed to load image.", ex);
        }
    }

    private ImageResource loadEmbeddedBase64ImageResource(final String uri) {
        byte[] image = ImageUtil.getEmbeddedBase64Image(uri);
        if (image != null) {
            return createImageResource(null, new ByteArrayInputStream(image));
        }
        return new ImageResource(null, null);
    }

    public XMLResource getXMLResource(String uri) {
        if (uri == null) {
            XRLog.exception("null uri requested");
            return null;
        }
        InputStream inputStream = getInputStream(uri);
        if (inputStream == null) {
            return null;
        }
        XMLResource xmlResource;
        try {
            xmlResource = XMLResource.load(inputStream);
        } catch (Exception e) {
            XRLog.exception(String.format("Unable to load xml resource %s", uri), e);
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        return xmlResource;
    }

    public boolean isVisited(String uri) {
        return false;
    }

    public void setBaseURL(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String resolveURI(String uri) {
        if (uri == null) {
            return null;
        }
        if (baseUrl == null || uri.startsWith("/")) {
            return uri;
        } else {
            if(uri.startsWith("http://") || uri.startsWith("https://")) {
                return uri;
            }
            else if (baseUrl.endsWith("/")) {
                return baseUrl + uri;
            } else {
                int idx = baseUrl.lastIndexOf('/');
                String b = baseUrl.substring(0, idx + 1);
                return b + uri;
            }
        }
    }

    public String getBaseURL() {
        return baseUrl;
    }

    /**
     * Dispose all images in cache and clean the cache.
     */
    public void disposeCache() {
        // TODO
        // Afaik can only entirely, not selectively clear asset manager cache
    }

    public byte[] getBinaryResource(String uri) {
        InputStream is = getInputStream(uri);
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buf = new byte[10240];
            int i;
            while ((i = is.read(buf)) != -1) {
                result.write(buf, 0, i);
            }
            is.close();
            is = null;

            return result.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
