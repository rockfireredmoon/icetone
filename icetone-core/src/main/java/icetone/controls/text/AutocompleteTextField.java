package icetone.controls.text;

import java.util.Collection;
import java.util.List;

import com.jme3.input.KeyInput;

import icetone.controls.menuing.Menu;
import icetone.core.BaseScreen;

public class AutocompleteTextField<V extends Object> extends TextField {

	private AutocompleteSource<V> source;
	private Menu<V> popup;
	private boolean liveHighlight;

	public AutocompleteTextField(V... values) {
		this(BaseScreen.get(), values);
	}

	@SafeVarargs	
	public AutocompleteTextField(BaseScreen screen, V... values) {
		this(screen, new SimpleAutocompleteSource<>(values));
	}

	public AutocompleteTextField(BaseScreen screen, Collection<V> values) {
		this(screen, new SimpleAutocompleteSource<>(values));
	}

	public AutocompleteTextField(BaseScreen screen, AutocompleteSource<V> source) {
		super(screen);
		init(source);
	}

	public AutocompleteTextField(BaseScreen screen, String styleId, AutocompleteSource<V> source) {
		super(screen, styleId);
		init(source);
	}

	public boolean isLiveHighlight() {
		return liveHighlight;
	}

	public AutocompleteTextField<V> setLiveHighlight(boolean liveHighlight) {
		this.liveHighlight = liveHighlight;
		return this;
	}

	protected void onChange(String value) {
		// For sub-classes to be notified of selection (or RETURN press in text
		// field)
	}

	private void init(AutocompleteSource<V> source) {
		this.source = source;
		setLayoutManager(new AutocompleteLayout());
		onKeyboardPressed(evt -> {
			if (evt.getKeyChar() != 0 && evt.getKeyCode() != KeyInput.KEY_ESCAPE) {
				if (liveHighlight && (popup == null || !popup.isVisible())) {
					showCompletion();
				} else if (!liveHighlight) {
					if (popup != null && popup.isVisible()) {
						popup.close();
						evt.setConsumed();
						return;
					}
				}
			}
		});
		onKeyboardReleased(evt -> {
			if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
				evt.setConsumed();
				if (popup == null || !popup.isVisible()) {
					showCompletion();
				}
				return;
			} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
				evt.setConsumed();
				if (popup == null || !popup.isVisible()) {
					onChange(getText());
				}
				return;
			} else if (evt.isCtrl() && evt.getKeyCode() == KeyInput.KEY_SPACE) {
				evt.setConsumed();
				if (showCompletion()) {
					popup.focus();
				}
				return;
			}
		});
		onKeyboardFocusLost(evt -> {
			if(evt.getOther() != popup) {
				hidePopup();
			}
		});
	}

	public boolean showCompletion() {
		if (popup == null) {
			popup = new Menu<V>(screen);
			popup.onChanged((evt) -> {
				AutocompleteTextField.this.setText(String.valueOf(evt.getNewValue().getValue()));
			});
			popup.addStyleClass("autocomplete-popup");
			popup.onKeyboardFocusLost(evt -> {
				focus();
				evt.setConsumed();
			});
		}

		popup.removeAllMenuItems();
		final List<AutocompleteItem<V>> items = source.getItems(getText());
		if (!items.isEmpty()) {
			String find = getText().toLowerCase();
			for (AutocompleteItem<V> i : items) {
				popup.addMenuItem(i.getText(), null, i.getValue());
				if ((find.equals("") || i.getText().toLowerCase().startsWith(find)) && popup.getSelectedIndex() == -1)
					popup.setSelectedItem(popup.getMenuItemWithValue(i.getValue()));
			}
			popup.showMenu(this, null, null, 10);
			return true;
		} else {
			popup.close();
			return false;
		}
	}

	protected void hidePopup() {
		if (popup != null)
			popup.hide();
	}

	abstract class AutoCompletePopup extends icetone.controls.menuing.Menu<String> {

		public AutoCompletePopup(BaseScreen screen, String uid) {
			super(screen);
			onKeyboardReleased(evt -> {
				if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
					evt.setConsumed();
					if (getSelectedIndex() < getMenuItems().size() - 1) {
						setSelectedIndex(getSelectedIndex() + 1);
					}
				} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
					evt.setConsumed();
					if (getSelectedIndex() > 0) {
						setSelectedIndex(getSelectedIndex() - 1);
					}
				} else if (evt.getKeyCode() == KeyInput.KEY_RETURN && popup != null && popup.isVisible()) {
					AutocompleteTextField.this.setText(getMenuItem(getSelectedIndex()).getValue());
				}
			});
		}

	}

	public class AutocompleteLayout extends TextFieldLayout {

		@Override
		protected void onLayout(TextField childElement) {
			super.onLayout(childElement);
			if (popup != null) {
				popup.sizeToContent();
			}
		}

	}
}
