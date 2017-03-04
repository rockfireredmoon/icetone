package icetone.core;

import com.jme3.app.Application;
import com.jme3.asset.AssetLoader;

public class AndroidToolKit extends ToolKit {

	protected AndroidToolKit(Application application) {
		super(application);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends AssetLoader> getImageLoader() {
		try {
			return (Class<? extends AssetLoader>) getClass().getClassLoader()
					.loadClass("com.jme3.texture.plugins.AndroidImageLoader");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
