package icetone.core.layout.loader;

import com.jme3.asset.AssetKey;

import icetone.core.BaseScreen;

public class LayoutAssetKey extends AssetKey<LayoutPart<?>> {

	private BaseScreen screen;

	public LayoutAssetKey(BaseScreen screen) {
		super();
		this.screen = screen;
	}

	public LayoutAssetKey(BaseScreen screen, String name) {
		super(name);
		this.screen = screen;
	}

	public BaseScreen getScreen() {
		return screen;
	}

//	public Class<? extends AssetCache> getCacheType() {
//		return null;
//	}
//
//	public Class<? extends AssetProcessor> getProcessorType() {
//		return LayoutAssetProcessor.class;
//	}

}
