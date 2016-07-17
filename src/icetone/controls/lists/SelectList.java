package icetone.controls.lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.listeners.KeyboardListener;

public class SelectList<O> extends AbstractList<O, SelectListItem<O>> implements KeyboardListener {

	public enum SelectionMode {
		NONE, SINGLE, MULTIPLE, TOGGLE
	}

	private List<Element> highlights = new ArrayList<>();
	protected ColorRGBA highlightColor;
	protected SelectionMode selectionMode = SelectionMode.SINGLE;
	protected List<Integer> selection = new ArrayList<>();
	private boolean ctrl;
	private boolean enableKeyboardNavigation = true;
	private boolean shift;

	/**
	 * Creates a new instance of the SelectList control
	 */
	public SelectList() {
		this(true);
	}

	/**
	 * Creates a new instance of the SelectList control
	 * 
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(boolean isScrollable) {
		this(Screen.get(), isScrollable);
	}

	/**
	 * Creates a new instance of the SelectList control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 */
	public SelectList(ElementManager screen) {
		this(screen, true);
	}

	/**
	 * Creates a new instance of the SelectList control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(ElementManager screen, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				screen.getStyle("SelectList").getVector4f("resizeBorders"),
				screen.getStyle("SelectList").getString("defaultImg"), isScrollable);
	}

	/**
	 * Creates a new instance of the SelectList control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(ElementManager screen, Vector2f position, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE,
				screen.getStyle("SelectList").getVector4f("resizeBorders"),
				screen.getStyle("SelectList").getString("defaultImg"), isScrollable);
	}

	/**
	 * Creates a new instance of the SelectList control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(ElementManager screen, Vector2f position, Vector2f dimensions, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle("SelectList").getVector4f("resizeBorders"),
				screen.getStyle("SelectList").getString("defaultImg"), isScrollable);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, boolean isScrollable) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, isScrollable);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(ElementManager screen, String UID, Vector2f position, boolean isScrollable) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("SelectList").getVector4f("resizeBorders"),
				screen.getStyle("SelectList").getString("defaultImg"), isScrollable);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, boolean isScrollable) {
		this(screen, UID, position, dimensions, screen.getStyle("SelectList").getVector4f("resizeBorders"),
				screen.getStyle("SelectList").getString("defaultImg"), isScrollable);
	}

	/**
	 * Creates a new instance of the Menu control
	 * 
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the Slider's track
	 * @param isScrollable
	 *            Boolean defining if the menu is a scrollable list
	 */
	public SelectList(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, boolean isScrollable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, isScrollable, "SelectList");

		setLayoutManager(new SelectListLayout());

		highlightColor = screen.getStyle("Menu").getColorRGBA("highlightColor");

		bindReleased(evt -> {
			for (int idx = listItems.size() - 1; idx >= 0; idx--) {
				SelectListItem<O> i = listItems.get(idx);
				float absY = LUtil.getAbsoluteY(i.getElement());
				if (evt.getY() >= absY && evt.getY() < absY + i.getElement().getHeight()) {
					select(idx);
					handleListItemClick(i, idx, i.getValue());
					break;
				}
			}
			evt.setConsumed();
		});
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		if (!Objects.equals(selectionMode, this.selectionMode)) {
			this.selectionMode = selectionMode;
			switch (selectionMode) {
			case NONE:
				selection.clear();
				removeAllHighlights();
				break;
			case SINGLE:
				while (selection.size() > 1) {
					selection.remove(selection.size() - 1);
					removeChild(highlights.get(0));
					highlights.remove(0);
				}
				break;
			default:
				break;
			}
			dirtyLayout(false);
			layoutChildren();
		}
	}

	@Override
	public void addListItem(String caption, O value, boolean isToggleItem, boolean isToggled, boolean pack) {
		SelectListItem<O> item = new SelectListItem<O>(this, caption, value, isToggleItem, isToggled);
		addListItem(item);
	}

	@Override
	public void insertListItem(int index, String caption, O value, boolean isToggleItem, boolean isToggled,
			boolean pack) {
		SelectListItem<O> item = new SelectListItem<O>(this, caption, value, isToggleItem, isToggled);
		insertListItem(index, item, pack);
	}

	/**
	 * Sets a single selected item
	 * 
	 * @param index
	 *            int
	 */
	public void setSelectedIndex(int index) {
		if (selectionMode != SelectionMode.NONE) {
			removeAllHighlights();
			selection.clear();
			selection.add(index);
			addChild(createHighlight(), null, false, false);
			dirtyLayout(false);
			layoutChildren();
		}
	}

	/**
	 * Add the selected index (or set if single selection mode)
	 * 
	 * @param index
	 *            int
	 */
	public void addSelectedIndex(int index) {
		if (selectionMode != SelectionMode.NONE) {
			if (selectionMode == SelectionMode.SINGLE) {
				selection.clear();
				removeAllHighlights();
			}
			selection.add(index);
			addChild(createHighlight(), null, false, false);
			dirtyLayout(false);
			layoutChildren();
		}
	}

	/**
	 * Remove the selected index
	 * 
	 * @param index
	 *            int
	 */
	public void removeSelectedIndex(int index) {
		selection.remove((Object) index);
		Element highlight = highlights.get(index);
		removeChild(highlight, false);
		highlights.remove(highlight);
		dirtyLayout(false);
		layoutChildren();
	}

	/**
	 * Clear all selection
	 * 
	 * @param index
	 *            int
	 */
	public void clearSelection() {
		removeAllHighlights();
		selection.clear();
		dirtyLayout(false);
		layoutChildren();
	}

	@Override
	public void removeListItem(int index) {
		if (selection.contains(index)) {
			selection.remove(index);
			Element el = highlights.get(0);
			removeChild(el);
			highlights.remove(el);
		}
		super.removeListItem(index);
	}

	@Override
	public void removeFirstListItem() {
		if (selection.contains(0)) {
			selection.remove((Object) 0);
			Element el = highlights.get(0);
			removeChild(el);
			highlights.remove(el);
		}
		super.removeFirstListItem();
	}

	@Override
	public void removeLastListItem() {
		int lastIdx = listItems.size() - 1;
		if (selection.contains(lastIdx)) {
			selection.remove((Object) lastIdx);
			Element el = highlights.get(0);
			removeChild(el);
			highlights.remove(el);
		}
		super.removeLastListItem();
	}

	@Override
	public void removeAllListItems() {
		removeAllHighlights();
		super.removeAllListItems();
	}

	protected void removeAllHighlights() {
		for (Element highlight : highlights) {
			removeChild(highlight, false);
		}
		highlights.clear();
	}

	public O getSelectedValue() {
		return selection.isEmpty() ? null : listItems.get(selection.get(0)).getValue();
	}

	public int getSelectedIndex() {
		return selection.isEmpty() ? -1 : selection.get(0);
	}

	protected void select(int index) {
		if (selectionMode == SelectionMode.NONE || selectionMode == SelectionMode.SINGLE
				|| (selectionMode == SelectionMode.MULTIPLE && !ctrl)) {
			selection.clear();
			removeAllHighlights();
			dirtyLayout(false);
		}
		if (selectionMode == SelectionMode.TOGGLE) {
			if (selection.contains(index)) {
				selection.remove((Object) index);
				Element highlight = highlights.get(0);
				highlights.remove(highlight);
				removeChild(highlight);
			} else {
				selection.add(index);
				addChild(createHighlight());
			}
			dirtyLayout(false);
		} else if (selectionMode != SelectionMode.NONE) {
			selection.add(index);
			addChild(createHighlight());
			dirtyLayout(false);
		}
		layoutChildren();
	}

	@Override
	public void onKeyPress(KeyInputEvent evt) {
		// Modifiers are used for mouse selection too
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = true;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = true;
		}

		if (enableKeyboardNavigation && getIsEnabled()) {
			if (selectionMode.equals(SelectionMode.NONE)) {
				return;
			}
			evt.setConsumed();
		}
	}

	@Override
	public void onKeyRelease(KeyInputEvent evt) {
		if (evt.getKeyCode() == KeyInput.KEY_LCONTROL || evt.getKeyCode() == KeyInput.KEY_RCONTROL) {
			ctrl = false;
		} else if (evt.getKeyCode() == KeyInput.KEY_LSHIFT || evt.getKeyCode() == KeyInput.KEY_RSHIFT) {
			shift = false;
		}

		if (enableKeyboardNavigation) {
			if (evt.getKeyCode() == KeyInput.KEY_UP) {
				setSelectedIndex(Math.max(getSelectedIndex() - 1, 0));
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
				setSelectedIndex(Math.min(getSelectedIndex() + 1, listItems.size() - 1));
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
				if (getSelectedIndex() >= 0 && getSelectedIndex() < listItems.size()) {
					SelectListItem<O> selectedItem = listItems.get(getSelectedIndex());
					handleListItemClick(selectedItem, getSelectedIndex(), selectedItem.getValue());
				}
				evt.setConsumed();
			}
		}
	}

	public boolean isEnableKeyboardNavigation() {
		return enableKeyboardNavigation;
	}

	public void setEnableKeyboardNavigation(boolean enableKeyboardNavigation) {
		this.enableKeyboardNavigation = enableKeyboardNavigation;
	}

	public void scrollToItem(int index) {
		scrollYTo(LUtil.getAbsoluteY(listItems.get(index).getElement()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onScrollContentHook(ScrollDirection direction) {
		((SelectListLayout) getLayoutManager()).repositionHighlight();
		super.onScrollContentHook(direction);
	}

	protected Element createHighlight() {
		Element highlight = new Element(screen);
		highlight.setIgnoreMouse(true);
		if (screen.getStyle("SelectList").getColorRGBA("highlightColor") != null) {
			highlight.getElementMaterial().setColor("Color", screen.getStyle("Menu").getColorRGBA("highlightColor"));
		} else {
			highlight.borders.set(screen.getStyle("SelectList").getVector4f("highlightResizeBorders"));
			highlight.setTexture(screen.getStyle("SelectList").getString("highlightImg"));
		}
		highlights.add(highlight);
		return highlight;
	}

	protected void onListItemClicked(int menuItemIndex, O value, boolean isToggled) {
		onChange();
	}

	protected void onChange() {
	}

	private void handleListItemClick(SelectListItem<O> menuItem, int menuItemIndex, O value) {
		if (menuItem.getIsToggleItem())
			menuItem.setIsToggled(!menuItem.getIsToggled());
		onListItemClicked(menuItemIndex, value, menuItem.getIsToggled());
	}

	class SelectListLayout extends AbstractListLayout {

		@Override
		public void layout(Element childElement) {
			super.layout(childElement);
			repositionHighlight();
		}

		protected void repositionHighlight() {
			Iterator<Integer> selIt = selection.iterator();
			for (Element highlight : highlights) {
				Integer idx = selIt.next();
				float sd = getVerticalScrollDistance() + getScrollableAreaVerticalPosition();
				SelectListItem<O> item = listItems.get(idx);
				LUtil.setBounds(highlight, textPadding.x, LUtil.getY(item.getElement()) - sd, getScrollBoundsWidth(),
						item.getElement().getHeight());
			}
		}

	}
}
