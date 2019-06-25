package icetone.extras.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import icetone.controls.buttons.CheckBox;
import icetone.controls.menuing.Menu;
import icetone.controls.menuing.MenuBar;
import icetone.core.ElementContainer;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.extras.actions.AppAction.Style;

public class ActionMenuBar extends MenuBar {

	public final static Comparator<AppAction> ACTION_MENU_GROUP_COMPARATOR = new Comparator<AppAction>() {
		@Override
		public int compare(AppAction o1, AppAction o2) {
			return Integer.valueOf(o1.getMenuGroup()).compareTo(o2.getMenuGroup());
		}
	};

	private List<AppAction> actions = new ArrayList<>();
	private List<ActionMenu> menus = new ArrayList<>();

	public ActionMenuBar() {
		super();
	}

	public ActionMenuBar(BaseScreen screen) {
		super(screen);
	}

	public ActionMenuBar addActionMenu(ActionMenu menu) {
		menus.add(menu);
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
		return this;
	}

	public ActionMenuBar removeActionMenu(ActionMenu menu) {
		menus.remove(menu);
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
		return this;
	}

	public ActionMenuBar addAction(AppAction action) {
		actions.add(action);
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
		return this;
	}

	public ActionMenuBar removeAction(AppAction action) {
		actions.remove(action);
		dirtyLayout(false, LayoutType.items);
		layoutChildren();
		return this;
	}

	@Override
	protected void layoutThis(ElementContainer<?, ?> container, LayoutType type) {
		if (type == LayoutType.items) {

			/* Get unique menus */
			Map<String, ActionMenu> m = new HashMap<>();
			for (ActionMenu menu : menus) {
				m.put(menu.getName(), menu);
			}
			List<ActionMenu> a = new ArrayList<>(m.values());
			Collections.sort(a);

			/* Categorise actions */
			Map<String, List<AppAction>> map = new HashMap<>();
			for (AppAction action : actions) {
				List<AppAction> l = map.get(action.getMenu());
				if (l == null) {
					l = new ArrayList<>();
					map.put(action.getMenu(), l);
				}
				l.add(action);
			}

			/* Build menus */
			removeAllChildren();
			for (ActionMenu menu : a) {
				addMenu(buildMenu(map, menu));
			}

			sizeToContent();
		} else {
			super.layoutThis(container, type);
		}
	}

	protected Menu<AppAction> buildMenu(Map<String, List<AppAction>> map, ActionMenu menu) {
		Menu<AppAction> x = new Menu<>(screen, menu.getName());
		x.onChanged(evt -> {
			if (!evt.getSource().isAdjusting()) {
				AppAction action = evt.getNewValue().getValue();
				if (action != null) {
					if (action.getStyle() == Style.TOGGLE) {
						action.setActive(!((CheckBox) evt.getNewValue().getItemElement()).isChecked());
						evt.setConsumed();
					}
					action.actionPerformed(new ActionEvent(action, evt));
				}
			}
		});

		List<AppAction> l = map.get(menu.getName());

		if (l != null) {
			/* Sort into groups within the menu */
			Collections.sort(l, ACTION_MENU_GROUP_COMPARATOR);

			int group = -1;
			for (AppAction action : l) {
				if (group != -1 && group != action.getMenuGroup())
					x.addSeparator();

				group = action.getMenuGroup();

				ActionMenu subMenu = action.getSubmenu();
				if (subMenu != null) {
					x.addMenuItem(subMenu.getName(), buildMenu(map, subMenu), action);
				} else
					x.addMenuItem(new ActionMenuItem(screen, action));
			}
		}
		return x;
	}

}
