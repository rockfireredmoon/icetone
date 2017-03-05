package icetone.xhtml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.TextureKey;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ToolKit;

/**
 * User agent appropriate for use with JME3. This loads things through the
 * {@link AssetManager}.
 */
public class XHTMLUserAgent implements UserAgentCallback {

	private String baseUrl;
	private final BaseScreen screen;
	private Element missingImage;

	public XHTMLUserAgent() {
		this(null);
	}

	public XHTMLUserAgent(BaseScreen screen) {
		this.screen = screen;
	}

	protected Image getMissingImage() {
		if (missingImage == null) {
			// Fake element for storing the 'missing image' style
			missingImage = new Element(screen).setStyleClass("missing-image");
		}
		Texture elementTexture = missingImage.getElementTexture();
		if (elementTexture == null) {
			// TODO default
			return null;
		}
		return elementTexture.getImage();
	}

	protected InputStream getInputStream(String uri) {
		try {
			int l = uri.lastIndexOf('#');
			if (l != -1) {
				uri = uri.substring(0, l);
			}
			while (uri.startsWith("/")) {
				uri = uri.substring(1);
			}
			AssetInfo info = ToolKit.get().getApplication().getAssetManager().locateAsset(new AssetKey<String>(uri));
			if (info == null)
				throw new AssetNotFoundException(uri);
			return info.openStream();
		} catch (AssetNotFoundException anfe) {
			XRLog.exception(String.format("Item at URI %s not found", uri));
		} catch (Exception e) {
			XRLog.exception(String.format("Failed to load %s", uri), e);
		}
		return null;
	}

	@Override
	public CSSResource getCSSResource(String uri) {
		return new CSSResource(getInputStream(uri));
	}

	@Override
	public ImageResource getImageResource(String uri) {
		if (ImageUtil.isEmbeddedBase64Image(uri)) {
			return loadEmbeddedBase64ImageResource(uri);
		} else {
			uri = resolveURI(uri);
			try {
				Texture tex = ToolKit.get().getApplication().getAssetManager().loadTexture(new TextureKey(uri));
				return new ImageResource(uri, new XHTMLFSImage(tex.getImage(), this, uri));
			} catch (AssetNotFoundException anfe) {
				XRLog.exception(String.format("Image at URI %s not found", uri));
			} catch (Exception e) {
				XRLog.exception(String.format("Failed to load %s", uri), e);
			}
			return new ImageResource(uri, new XHTMLFSImage(getMissingImage(), this, uri));
		}
	}

	protected ImageResource createImageResource(String uri, final InputStream is) {
		try {
			Image img = (Image) ToolKit.get().getImageLoader().newInstance()
					.load(new AssetInfo(ToolKit.get().getApplication().getAssetManager(), new AssetKey<Image>(uri)) {
						@Override
						public InputStream openStream() {
							return is;
						}
					});
			return new ImageResource(uri, new XHTMLFSImage(img, this, uri));
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

	@Override
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

	@Override
	public boolean isVisited(String uri) {
		return false;
	}

	@Override
	public void setBaseURL(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String resolveURI(String uri) {
		if (uri == null) {
			return null;
		}
		if (baseUrl == null || uri.startsWith("/")) {
			return uri;
		} else {
			if (uri.startsWith("asset://"))
				return uri.substring(8);
			if (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("file:")) {
				return uri;
			} else if (baseUrl.endsWith("/")) {
				return baseUrl + uri;
			} else {
				int idx = baseUrl.lastIndexOf('/');
				String b = baseUrl.substring(0, idx + 1);
				return b + uri;
			}
		}
	}

	@Override
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

	@Override
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
