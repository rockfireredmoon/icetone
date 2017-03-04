package icetone.css;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

public class ThemeLoader implements AssetLoader {
	final static Logger LOG = Logger.getLogger(ThemeLoader.class.getName());

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		Constructor constructor = new Constructor();
		InputStream file = assetInfo.openStream();
		Yaml yaml = new Yaml(constructor);
		constructor.addTypeDescription(new TypeDescription(Theme.class, "!theme"));
		try {
			Theme theme = (Theme) yaml.load(file);
			String path = theme.getPath();
			if (path == null || path.equals("")) {
				theme.setPath(folderName(assetInfo.getKey().getName()) + "/" + theme.getName() + ".css");
			} else if (!path.startsWith("/")) {
				theme.setPath(folderName(assetInfo.getKey().getName()) + "/" + path);
			}
			return theme;
		} finally {
			file.close();
		}
	}

	String folderName(String path) {
		String fn = path;
		int idx = fn.lastIndexOf('/');
		if (idx != -1)
			fn = fn.substring(0, idx);
		return fn;
	}

}
