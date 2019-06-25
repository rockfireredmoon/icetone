package icetone.jmettf;

import com.atr.jme.font.shape.TrueTypeMeshContainer;
import com.jme3.scene.Node;

import icetone.css.StyleManager.ThemeInstance;
import icetone.text.FontSpec;

public class JMETTFTrueTypeTextElement extends JMETTFTextElement {

	public JMETTFTrueTypeTextElement(ThemeInstance theme, JMETTFFontInfo fontInfo, FontSpec font, Node parent) {
		super(theme, fontInfo, font, parent);
	}

	@Override
	void recreateSpatial() {
		super.recreateSpatial();
		((TrueTypeMeshContainer) spatial).setAA(font.getProperties() != null && !"false".equals(font.getProperties().get(JMETTFTextEngine.ANTI_ALIAS)));
	}

}
