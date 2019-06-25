package icetone.extras.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import icetone.controls.buttons.CheckBox;
import icetone.controls.menuing.MenuItem;
import icetone.core.BaseScreen;
import icetone.extras.actions.AppAction.Style;

public class ActionMenuItem extends MenuItem<AppAction> implements PropertyChangeListener {

	public ActionMenuItem(BaseScreen screen, AppAction action) {
		super(screen, null, null, null);
		setAction(action);
	}

	public AppAction getAction() {
		return getValue();
	}

	public ActionMenuItem setAction(AppAction action) {
		setValue(action);
		return this;
	}

	public MenuItem<AppAction> setValue(AppAction action) {
		AppAction val = getValue();
		if (!Objects.equals(action, val)) {
			if (val != null) {
				val.removePropertyChangeListener(this);
			}
			if (action == null) {
				setToolTipText(null);
				setEnabled(true);
				setItemElement(null);
			} else {
				setToolTipText(action.getDescription());
				setEnabled(action.isEnabled());
				setText(action.getName());
				Style style = action.getStyle();
				switch (style) {
				case NORMAL:
					setItemElement(null);
					break;
				case TOGGLE:
					setItemElement(new CheckBox(getScreen()).setChecked(action.isActive()).onChange(evt -> {
						if (!evt.getSource().isAdjusting()) {
							action.setActive(evt.getNewValue());
							action.actionPerformed(new ActionEvent(action, evt));
							getMenu().close();
//							evt.setConsumed();
						}
					}));
					break;
				case ELEMENT:
					setItemElement(action.getElement());
					break;
				default:
					break;
				}
			}
			super.setValue(action);
			if (action != null)
				action.addPropertyChangeListener(this);
		}
		return this;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AppAction.NAME)) {
			setText((String) evt.getNewValue());
		} else if (evt.getPropertyName().equals(AppAction.DESCRIPTION)) {
			setToolTipText((String) evt.getNewValue());
		} else if (evt.getPropertyName().equals(AppAction.ENABLED)) {
			setEnabled((Boolean) evt.getNewValue());
		} else if (evt.getPropertyName().equals(AppAction.ACTIVE)) {
			if (getItemElement() instanceof CheckBox)
				getItemElement()
						.runAdjusting(() -> ((CheckBox) getItemElement()).setChecked((Boolean) evt.getNewValue()));
		}
	}

}
