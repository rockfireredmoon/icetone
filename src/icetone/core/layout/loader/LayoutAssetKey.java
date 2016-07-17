package icetone.core.layout.loader;

import com.jme3.asset.AssetKey;

import icetone.core.ElementManager;

public class LayoutAssetKey extends AssetKey<LayoutPart<?>> {

	private ElementManager screen;

	public LayoutAssetKey(ElementManager screen) {
		super();
		this.screen = screen;
	}

	public LayoutAssetKey(ElementManager screen, String name) {
		super(name);
		this.screen = screen;
	}

	public ElementManager getScreen() {
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
