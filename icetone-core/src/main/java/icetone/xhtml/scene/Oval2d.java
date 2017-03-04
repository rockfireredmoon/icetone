/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.xhtml.scene;

import java.nio.FloatBuffer;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

/**
 * Circle.
 *
 * @author Martin Simons
 * @version $Id$
 */
public class Oval2d extends Mesh {
    /**
     * The center.
     */
    private Vector3f center;
    /**
     * The radius.
     */
    private Vector2f radius;
    /**
     * The samples.
     */
    private int samples;

    /**
     * Constructs a new instance of this class.
     *
     * @param radius
     */
    public Oval2d(Vector2f radius) {
        this(Vector3f.ZERO, radius, 16);
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param radius
     * @param samples
     */
    public Oval2d(Vector2f radius, int samples) {
        this(Vector3f.ZERO, radius, samples);
    }

    /**
     * Constructs a new instance of this class.
     *
     * @param center
     * @param radius
     * @param samples
     */
    public Oval2d(Vector3f center, Vector2f radius, int samples) {
        super();
        this.center = center;
        this.radius = radius;
        this.samples = samples;
        setMode(Mode.Lines);
        updateGeometry();
    }

    protected void updateGeometry() {
        FloatBuffer positions = BufferUtils.createFloatBuffer(samples * 3);
        FloatBuffer normals = BufferUtils.createFloatBuffer(samples * 3);
        short[] indices = new short[samples * 2];
        float rate = FastMath.TWO_PI / samples;
        float angle = 0;
        int idc = 0;
        for (int i = 0; i < samples; i++) {
            float x = FastMath.cos(angle) * radius.x + center.x;
            float z = FastMath.sin(angle) * radius.y + center.y;
            positions.put(x).put(z).put(0);
            normals.put(new float[]{0, 1, 0});
            indices[idc++] = (short) i;
            if (i < samples - 1) {
                indices[idc++] = (short) (i + 1);
            } else {
                indices[idc++] = 0;
            }
            angle += rate;
        }
        setBuffer(VertexBuffer.Type.Position, 3, positions);
        setBuffer(VertexBuffer.Type.Normal, 3, normals);
        setBuffer(VertexBuffer.Type.Index, 2, indices);
        setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{0, 0, 1, 1});
        updateBound();
    }
    
}
