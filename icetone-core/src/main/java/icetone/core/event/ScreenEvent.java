package icetone.core.event;

import com.jme3.input.event.InputEvent;

import icetone.core.BaseScreen;

public class ScreenEvent extends InputEvent implements UIEvent {
	
	public enum Type {
		RESIZE
	}

	private BaseScreen source;
	private Type type;

	public ScreenEvent(BaseScreen source, Type type) {
		time = System.currentTimeMillis();
		this.type = type;
		this.source = source;
	}

	public BaseScreen getSource() {
		return source;
	}

	public Type getType() {
		return type;
	}

}
