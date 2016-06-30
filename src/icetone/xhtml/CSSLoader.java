package icetone.xhtml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.xhtmlrenderer.context.StylesheetFactoryImpl;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

import icetone.core.Screen;

public class CSSLoader implements AssetLoader {
	final static Logger LOG = Logger.getLogger(CSSLoader.class.getName());


	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		StylesheetFactory fact = new StylesheetFactoryImpl(new TGGUserAgent(Screen.get()));
		InputStream openStream = assetInfo.openStream();
		try {
			StylesheetInfo ssInfo = new StylesheetInfo();
			ssInfo.setUri(assetInfo.getKey().getName());
//			ssInfo.setMedia("screen");
			return fact.parse(new InputStreamReader(openStream), ssInfo);
		} finally {
			openStream.close();
		}
	}
}
