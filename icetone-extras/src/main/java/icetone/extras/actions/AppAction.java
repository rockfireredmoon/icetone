package icetone.extras.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import icetone.core.BaseElement;
import icetone.extras.util.AbstractPropertyChangeSupport;

public class AppAction extends AbstractPropertyChangeSupport implements ActionListener {

	public enum Style {
		NORMAL, TOGGLE, ELEMENT
	}

	private Map<String, Object> properties = new HashMap<>();

	private ActionSupport actionSupport;

	public final static String NAME = "Name";
	public final static String DESCRIPTION = "Description";
	public final static String ICON_TEXT = "IconText";
	public final static String ICON_TEXTURE = "IconTexture";
	public final static String MENU = "Menu";
	public final static String MENU_GROUP = "MenuGroup";
	public final static String COMMAND = "Command";
	public final static String ENABLED = "Enabled";
	public final static String INTERVAL = "Interval";
	public final static String STYLE = "Style";
	public final static String ACTIVE = "Action";
	public final static String ELEMENT = "Element";
	public final static String SUBMENU = "Submenu";
	
	public AppAction(ActionMenu submenu) {
		this(submenu.getName(), null);
		setSubmenu(submenu);
	}

	public AppAction(String name) {
		this(name, null);
	}

	public AppAction(String name, ActionListener listener) {
		setName(name);
		setMenuGroup(0);
		setEnabled(true);
		setStyle(Style.NORMAL);
		setInterval(0);
		setActive(false);
		if (listener != null)
			onAction(listener);
	}

	public AppAction onAction(ActionListener listener) {
		if (actionSupport == null)
			actionSupport = new ActionSupport();
		actionSupport.bind(listener);
		return this;
	}

	public AppAction addActionListener(ActionListener listener) {
		if (actionSupport != null)
			actionSupport = new ActionSupport();
		actionSupport.addListener(listener);
		return this;
	}

	public AppAction removeActionListener(ActionListener listener) {
		if (actionSupport != null)
			actionSupport.removeListener(listener);
		return this;
	}

	public void setProperty(String name, Object value) {
		Object old = properties.put(name, value);
		if (!Objects.equals(old, value)) {
			firePropertyChange(name, old, value);
		}
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public String getName() {
		return (String) getProperty(NAME);
	}

	public String getDescription() {
		return (String) getProperty(DESCRIPTION);
	}

	public String getIconText() {
		return (String) getProperty(ICON_TEXT);
	}

	public String getIconTexture() {
		return (String) getProperty(ICON_TEXTURE);
	}

	public String getMenu() {
		return (String) getProperty(MENU);
	}

	public ActionMenu getSubmenu() {
		return (ActionMenu) getProperty(SUBMENU);
	}

	public Style getStyle() {
		return (Style) getProperty(STYLE);
	}

	public String getCommand() {
		return (String) getProperty(COMMAND);
	}

	public int getMenuGroup() {
		return (Integer) getProperty(MENU_GROUP);
	}

	public float getInterval() {
		return (Float) getProperty(INTERVAL);
	}

	public boolean isEnabled() {
		return (Boolean) getProperty(ENABLED);
	}

	public boolean isActive() {
		return (Boolean) getProperty(ACTIVE);
	}

	public BaseElement getElement() {
		return (BaseElement) getProperty(ELEMENT);
	}

	public AppAction setElement(BaseElement element) {
		setProperty(ELEMENT, element);
		setStyle(Style.ELEMENT);
		return this;
	}

	public AppAction setCommand(String command) {
		setProperty(COMMAND, command);
		return this;
	}

	public AppAction setName(String name) {
		setProperty(NAME, name);
		return this;
	}

	public AppAction setStyle(Style style) {
		setProperty(STYLE, style);
		return this;
	}

	public AppAction setMenu(String menu) {
		setProperty(MENU, menu);
		return this;
	}

	public AppAction setMenuGroup(int menuGroup) {
		setProperty(MENU_GROUP, menuGroup);
		return this;
	}

	public AppAction setInterval(float interval) {
		setProperty(INTERVAL, interval);
		return this;
	}

	public AppAction setEnabled(boolean enabled) {
		setProperty(ENABLED, enabled);
		return this;
	}

	public AppAction setActive(boolean active) {
		setProperty(ACTIVE, active);
		return this;
	}

	public AppAction setSubmenu(ActionMenu menu) {
		setProperty(SUBMENU, menu);
		return this;
	}

	public void actionPerformed(ActionEvent evt) {
		if (actionSupport != null)
			actionSupport.fireEvent(evt);
	}

}
