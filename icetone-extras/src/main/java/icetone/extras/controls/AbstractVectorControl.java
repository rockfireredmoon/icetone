package icetone.extras.controls;

import com.jme3.math.Vector2f;

import icetone.core.ElementManager;
import icetone.core.Element;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeListener;

public abstract class AbstractVectorControl<V extends Cloneable> extends Element {

	public static final Vector2f SPINNER_SIZE = new Vector2f(90, 20);
	protected ChangeSupport<AbstractVectorControl<V>, V> changeSupport = new ChangeSupport<>();

	public AbstractVectorControl(ElementManager<?> screen, String styleId) {
		super(screen, styleId);
	}

	public abstract void setValue(V newValue);

	public abstract V getValue();

	public void onChange(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.bind(changeListener);
	}

	public void unbind(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.unbind(changeListener);
	}

	public void addChangeListener(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.addListener(changeListener);
	}

	public void removeChangeListener(UIChangeListener<AbstractVectorControl<V>, V> changeListener) {
		changeSupport.removeListener(changeListener);
	}
}
