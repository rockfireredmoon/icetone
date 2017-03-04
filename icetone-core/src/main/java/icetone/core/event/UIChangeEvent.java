package icetone.core.event;

import com.jme3.input.event.InputEvent;

public class UIChangeEvent<S, V extends Object> extends InputEvent implements UIEvent {

	private S source;
	private V oldValue;
	private V newValue;
	private boolean temporary;

	public UIChangeEvent(S source, V oldValue, V newValue) {
		time = System.currentTimeMillis();
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.source = source;
	}

	public boolean isTemporary() {
		return temporary;
	}

	public UIChangeEvent<S, V> setTemporary(boolean temporary) {
		this.temporary = temporary;
		return this;
	}

	public S getSource() {
		return source;
	}

	public V getOldValue() {
		return oldValue;
	}

	public V getNewValue() {
		return newValue;
	}

	@Override
	public String toString() {
		return "UIChangeEvent [source=" + source + ", oldValue=" + oldValue + ", newValue=" + newValue + ", temporary=" + temporary + "]";
	}

}
