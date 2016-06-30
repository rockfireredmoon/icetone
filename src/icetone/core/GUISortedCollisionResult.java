package icetone.core;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;

public class GUISortedCollisionResult extends CollisionResults {

	private ViewPort vp;

	public GUISortedCollisionResult(ViewPort vp) {
		this.vp = vp;
	}

	@Override
	public void addCollision(CollisionResult result) {
		super.addCollision(new CollisionResultWrapper(result));
	}

	class CollisionResultWrapper extends CollisionResult {

		private CollisionResult wrapped;

		CollisionResultWrapper(CollisionResult wrapped) {
			this.wrapped = wrapped;
		}

		public int compareTo(CollisionResult other) {
			return vp.getQueue().getGeometryComparator(Bucket.Gui).compare(wrapped.getGeometry(), other.getGeometry());
		}

		public void setGeometry(Geometry geom) {
			wrapped.setGeometry(geom);
		}

		public void setContactNormal(Vector3f norm) {
			wrapped.setContactNormal(norm);
		}

		public void setContactPoint(Vector3f point) {
			wrapped.setContactPoint(point);
		}

		public void setDistance(float dist) {
			wrapped.setDistance(dist);
		}

		public void setTriangleIndex(int index) {
			wrapped.setTriangleIndex(index);
		}

		public Triangle getTriangle(Triangle store) {
			return wrapped.getTriangle(store);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CollisionResult) {
				return ((CollisionResult) obj).compareTo(this) == 0;
			}
			return super.equals(obj);
		}

		public Vector3f getContactPoint() {
			return wrapped.getContactPoint();
		}

		public Vector3f getContactNormal() {
			return wrapped.getContactNormal();
		}

		public float getDistance() {
			return wrapped.getDistance();
		}

		public Geometry getGeometry() {
			return wrapped.getGeometry();
		}

		public int getTriangleIndex() {
			return wrapped.getTriangleIndex();
		}
	}

}
