package icetone.controls.lists;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpinnerModel<V> implements SpinnerModel<V>{

	private List<SpinnerModelListener> listeners = new ArrayList<>();

	@Override
	public void addListener(SpinnerModelListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(SpinnerModelListener listener) {
		listeners.remove(listener);
	}

	protected void fireChanged() {
		for(int i = listeners.size() - 1; i >= 0 ; i--) 
			listeners.get(i).modelChanged();
	}
	
}
