package icetone.core.layout.loader;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetProcessor;

public class LayoutAssetProcessor implements AssetProcessor {

	@Override
	public Object postProcess(AssetKey key, Object obj) {
		return obj;
	}

	@Override
	public Object createClone(Object obj) {
		// Caching not in use so this should be ok
		return obj;
	}

}
