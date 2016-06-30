package icetone.core;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.layout.LayoutManager;

public class Container extends Element {

	public Container() {
		super();
		init();
	}
	public Container(ElementManager screen, LayoutManager layoutManager) {
		this(screen);
		setLayoutManager(layoutManager);
	}

	public Container(LayoutManager layoutManager) {
		super();
		init();
		setLayoutManager(layoutManager);
	}

	public Container(ElementManager screen, String UID, Vector4f resizeBorders,String texturePath) {
		super(screen, UID, resizeBorders, texturePath);
		init();
	}

	public Container(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String texturePath) {
		super(screen, UID, position, dimensions, resizeBorders, texturePath);
		init();
	}

	public Container(ElementManager screen, String UID, Vector2f dimensions, Vector4f resizeBorders, String texturePath) {
		super(screen, UID, dimensions, resizeBorders, texturePath);
		init();
	}

	public Container(ElementManager screen, String UID, Vector2f dimensions) {
		super(screen, UID, dimensions);
		init();
	}

	public Container(ElementManager screen, String UID) {
		super(screen, UID);
		init();
	}

	public Container(ElementManager screen, Vector2f dimensions) {
		super(screen, dimensions);
		init();
	}

	public Container(ElementManager screen) {
		super(screen);
		init();
	}

	public Container(Vector2f dimensions) {
		this(Screen.get(), dimensions);
	}

	private void init() {
		 setAsContainerOnly();
		setIgnoreMouse(true);
	}
}
