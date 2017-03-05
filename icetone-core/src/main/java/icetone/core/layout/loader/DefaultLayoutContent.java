package icetone.core.layout.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xhtmlrenderer.css.sheet.Stylesheet;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetNotFoundException;

import icetone.core.BaseElement;
import icetone.core.BaseScreen;

public class DefaultLayoutContent implements LayoutContext {

	protected LayoutAssetKey loader;
	protected BaseScreen screen;

	public DefaultLayoutContent(BaseScreen screen) {
		this.screen = screen;
	}

	@Override
	public void configure(BaseElement el) {
	}

	@Override
	public Collection<Stylesheet> getStylesheets() {
		// Try to locate a stylesheet for this layout
		String cn = loader.getName();
		String ext = loader.getExtension();
		if (ext == null || ext.equals(""))
			throw new IllegalArgumentException("Layout path must have an extension.");
		cn = cn.substring(0, cn.length() - ext.length()) + "css";
		AssetKey<Stylesheet> ss = new AssetKey<>(cn);
		/// TODO probably wrong now?
		List<Stylesheet> all = new ArrayList<Stylesheet>(screen.getStylesheets());
		try {
			Stylesheet sheet = screen.getApplication().getAssetManager().loadAsset(ss);
			if (sheet != null)
				all.add(sheet);
		} catch (AssetNotFoundException anfe) {
			// Don't care?
		}
		return all;
	}

	@Override
	public void init(LayoutAssetKey layoutAsset) {
		this.loader = layoutAsset;

	}

	@Override
	public void end(BaseElement root) {
	}
}