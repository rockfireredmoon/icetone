package icetone.xhtml.scene;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.UIDUtil;

/**
 * A {@link Mesh} wrapped in an {@link Element}. Allows any mesh to be added as an ordinary
 * Tonegod element.
 */
public class AbstractMeshElement extends Element {

    protected final Material meshMaterial;

    public AbstractMeshElement(ElementManager screen, Vector2f position, Vector2f dimension) {
        super(screen, UIDUtil.getUID(),
                position, dimension, Vector4f.ZERO, null);
        setScaleEW(false);
        setScaleNS(false);
        setDocking(null);
        setIgnoreMouse(true);
        
        meshMaterial = new Material(screen.getApplication().getAssetManager(), "icetone/shaders/Unshaded.j3md");
        meshMaterial.setVector2("OffsetAlphaTexCoord", new Vector2f(0, 0));
        meshMaterial.setFloat("GlobalAlpha", screen.getGlobalAlpha());
        meshMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        meshMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
    }

    @Override
    public void updateLocalClippingLayer() {
        super.updateLocalClippingLayer();
        if (!clippingLayers.isEmpty()) {
            meshMaterial.setVector4("Clipping", getClippingBounds());
            meshMaterial.setBoolean("UseClipping", true);
        } else {
            meshMaterial.setBoolean("UseClipping", false);
        }
    }
}
