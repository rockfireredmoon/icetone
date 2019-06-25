package icetone.extras.controls;

import com.jme3.math.Vector2f;

import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeListener;

public abstract class AbstractVectorControl<V extends Cloneable> extends Element {

	public static final Vector2f SPINNER_SIZE = new Vector2f(90, 20);
	protected ChangeSupport<AbstractVectorControl<V>, V> changeSupport = new ChangeSupport<>();

	public AbstractVectorControl(BaseScreen screen) {
		super(screen);
	}

	public abstract void setValue(V newValue);

	public abstract V getValue();

	public AbstractVectorControl<V> onChange(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.bind(changeListener);
		return this;
	}

	public AbstractVectorControl<V> unbind(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.unbind(changeListener);
		return this;
	}

	public AbstractVectorControl<V> addChangeListener(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.addListener(changeListener);
		return this;
	}

	public AbstractVectorControl<V> removeChangeListener(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.removeListener(changeListener);
		return this;
	}
}
