package icetone.extras.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import icetone.controls.buttons.PushButton;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.text.FontSpec;

public class ActionButton extends PushButton implements PropertyChangeListener {

	private AppAction action;
	private int iconSize = 16;

	public ActionButton() {
		super();
	}

	public ActionButton(BaseScreen screen) {
		super(screen);
	}

	public ActionButton(AppAction action) {
		super();
		setAction(action);
	}

	public ActionButton(BaseScreen screen, AppAction action) {
		super(screen);
		setAction(action);
	}

	public int getIconSize() {
		return iconSize;
	}

	public ActionButton setIconSize(int iconSize) {
		this.iconSize = iconSize;
		updateIcon();
		return this;
	}

	protected void updateIcon() {
		String iconText = action.getIconText();
		if (iconText != null && iconText.length() > 0) {
			Element buttonIcon = getButtonIcon();
			buttonIcon.setFont(new FontSpec("fontawesome-" + iconSize, iconSize));
			buttonIcon.addStyleClass("fa");
			buttonIcon.addStyleClass("fa-" + iconSize);
			buttonIcon.setText(iconText);
		} else {
			String iconTexture = action.getIconTexture();
			if (iconTexture != null && iconTexture.length() > 0) {
				setButtonIcon(iconTexture);
			} else {
				setButtonIcon(null);
			}
		}
	}

	public ActionButton setAction(AppAction action) {
		if (this.action != null) {
			this.action.removePropertyChangeListener(this);
		}
		setText(action.getName());
		this.action = action;
		this.action.addPropertyChangeListener(this);
		setToolTipText(action.getDescription());
		setEnabled(action.isEnabled());

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
		} else if (evt.getPropertyName().equals(AppAction.ICON_TEXT)
				|| evt.getPropertyName().equals(AppAction.ICON_TEXTURE)) {
			updateIcon();
		}
	}

}
