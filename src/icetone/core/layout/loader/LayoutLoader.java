package icetone.core.layout.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ServiceLoader;

import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

/**
 * 
 * @author rockfire
 */
public class LayoutLoader implements AssetLoader {

	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		if (!(assetInfo.getKey() instanceof LayoutAssetKey)) {
			throw new IOException("Loading Layout must use a " + LayoutAssetKey.class + " as it's key..");
		}
		Constructor constructor = new Constructor();
		InputStream file = assetInfo.openStream();
		Yaml yaml = new Yaml(constructor);
		registerTypes(constructor);
		try {
			return (LayoutPart<?>) yaml.load(file);
		} finally {
			file.close();
		}
	}

	private void registerTypes(Constructor constructor) {
		// Standard controls
		constructor.addTypeDescription(new TypeDescription(PanelLayoutPart.class, "!panel"));
		constructor.addTypeDescription(new TypeDescription(LabelLayoutPart.class, "!label"));
		constructor.addTypeDescription(new TypeDescription(ContainerLayoutPart.class, "!container"));
		constructor.addTypeDescription(new TypeDescription(TextFieldLayoutPart.class, "!textField"));
		constructor.addTypeDescription(new TypeDescription(PasswordLayoutPart.class, "!password"));
		constructor.addTypeDescription(new TypeDescription(XHTMLLabelLayoutPart.class, "!xhtmlLabel"));
		
		// Layouts
		constructor.addTypeDescription(new TypeDescription(BorderLayoutLayoutPart.class, "!borderLayout"));
		constructor.addTypeDescription(new TypeDescription(MigLayoutLayoutPart.class, "!migLayout"));

		// Custom
		ServiceLoader<LayoutPartRegisterable> parts = ServiceLoader.load(LayoutPartRegisterable.class);
		for (LayoutPartRegisterable part : parts) {
			part.register(constructor);
		}
	}
}
