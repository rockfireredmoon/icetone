/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package icetone.extras.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractPropertyChangeSupport {
	protected transient PropertyChangeSupport changeSupport;

	public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		checkChangeSupport();
		changeSupport.addPropertyChangeListener(propertyName, listener);
		onAddPropertyChangeListener(propertyName, listener);
	}

	public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		checkChangeSupport();
		changeSupport.removePropertyChangeListener(propertyName, listener);
		onRemovePropertyChangeListener(propertyName, listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		checkChangeSupport();
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}
		changeSupport.firePropertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
	}

	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		checkChangeSupport();
		changeSupport.addPropertyChangeListener(listener);
		onAddPropertyChangeListener(null, listener);
	}

	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		checkChangeSupport();
		changeSupport.removePropertyChangeListener(listener);
		onRemovePropertyChangeListener(null, listener);
	}

	protected void onAddPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	}

	protected void onRemovePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	}

	private void checkChangeSupport() {
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
	}

}
