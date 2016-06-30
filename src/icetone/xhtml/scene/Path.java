package icetone.xhtml.scene;

import java.util.Collection;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

public class Path extends Mesh {
    private Collection<Vector2f> path;
    private final boolean close;

    public Path(Collection<Vector2f> path, boolean close) {
        this.path = path;
        this.close = close;
        setMode(Mode.Lines);
        updateGeometry();
    }

    protected final void updateGeometry() {
        Vector3f[] vertices = new Vector3f[path.size()];
        int idx = 0;
        float boundY = 0;
        for (Vector2f p : path) {
            boundY = Math.max(boundY, p.y);
        }
        for (Vector2f p : path) {
            vertices[idx++] = new Vector3f(p.x, boundY - p.y, 0);
        }
        int numIndexes = 2 * vertices.length;
        int numLines = numIndexes / 2;
        int padding = close ? 0 : 1;
        int[] indexes = new int[numIndexes];
        for (int i = 0; i < numLines - padding; i++) {
            indexes[2 * i] = i;
            indexes[2 * i + 1] = (i + 1) % numLines;
        }
        setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(VertexBuffer.Type.Index, 2, BufferUtils.createIntBuffer(indexes));
        updateBound();
    }
    
}
