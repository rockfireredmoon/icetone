/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.controls.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.jme3.input.KeyInput;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout;
import icetone.core.Layout.LayoutType;
import icetone.core.PseudoStyles;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.event.mouse.HoverEvent.HoverEventType;
import icetone.css.CssProcessor.PseudoStyle;

public class SelectList<O> extends AbstractList<O, SelectListItem<O>> {

	public enum SelectionMode {
		MULTIPLE, NONE, SINGLE, TOGGLE
	}

	protected List<Integer> selection = new ArrayList<>();
	protected SelectionMode selectionMode = SelectionMode.SINGLE;
	private ChangeSupport<SelectList<O>, Set<SelectListItem<O>>> changeSupport;
	private SelectListItem<O> highlight = null;

	{
		onMouseReleased(evt -> {
			for (int idx = listItems.size() - 1; idx >= 0; idx--) {
				SelectListItem<O> i = listItems.get(idx);
				float absY = i.getElement().getAbsoluteY();
				if (evt.getY() >= absY && evt.getY() < absY + i.getElement().getHeight()) {
					Set<SelectListItem<O>> was = getSelectedListItems();
					select(idx, evt.isCtrl() || selectionMode == SelectionMode.TOGGLE);
					handleListItemClick(i);
					changed(was);
					break;
				}
			}
			evt.setConsumed();
		});

		onHover(l -> {
			if (l.getEventType() == HoverEventType.leave) {
				setHighlighted(null);
				l.setConsumed();
			}
		});

		onMouseMoved(evt -> {
			for (int idx = listItems.size() - 1; idx >= 0; idx--) {
				SelectListItem<O> i = listItems.get(idx);
				float absY = i.getElement().getAbsoluteY();
				if (evt.getY() >= absY && evt.getY() < absY + i.getElement().getHeight()) {
					setHighlighted(i);
					return;
				}
			}

			if (highlight != null) {
				setHighlighted(null);
			}
		});

		onNavigationKey(evt -> {
			if ((selectionMode == SelectionMode.MULTIPLE || selectionMode == SelectionMode.TOGGLE)
					&& evt.getKeyCode() == KeyInput.KEY_A && evt.isCtrl()) {
				if (evt.isPressed()) {
					if (selection.size() == listItems.size())
						clearSelection();
					else
						selectAll();
				}
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_UP) {
				if (evt.isPressed()) {
					int idx = Math.max(getSelectedIndex() - 1, 0);
					Set<SelectListItem<O>> was = getSelectedListItems();
					select(idx, false);
					scrollToItem(idx);
					changed(was);
				}
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_DOWN) {
				if (evt.isPressed()) {
					int idx = Math.min(getSelectedIndex() + 1, listItems.size() - 1);
					Set<SelectListItem<O>> was = getSelectedListItems();
					select(idx, false);
					scrollToItem(idx);
					changed(was);
				}
				evt.setConsumed();
			} else if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
				if (evt.isPressed()) {
					if (getSelectedIndex() >= 0 && getSelectedIndex() < listItems.size()) {
						Set<SelectListItem<O>> was = getSelectedListItems();
						SelectListItem<O> selectedItem = listItems.get(getSelectedIndex());
						handleListItemClick(selectedItem);
						changed(was);
					}
				}
				evt.setConsumed();
			}
		});
	}

	private int preferredRows = -1;

	/**
	 * Creates a new instance of the SelectList control
	 */
	public SelectList() {
		this(BaseScreen.get());
	}

	/**
	 * Creates a new instance of the SelectList control
	 * 
	 * @param screen The screen control the Element is to be added to
	 */
	public SelectList(BaseScreen screen) {
		this(screen, null);
	}

	/**
	 * Creates a new instance of the SelectList control
	 * 
	 * @param screen The screen control the Element is to be added to
	 * @param list   of values
	 */
	public SelectList(BaseScreen screen, Collection<O> values) {
		super(screen, true);
		if (values != null) {
			for (O o : values) {
				addListItem(new SelectListItem<O>(this, String.valueOf(o), o, false, false));
			}
		}
	}

	/**
	 * Get the preferred number of rows to show visually. This overrides the height
	 * element of all of other preferred size calculations. A value of -1 indicates
	 * the usual preferred size calculations should be used.
	 * 
	 * @return preferred number of rows to show
	 * @return this for chaining
	 */
	public int getPreferredRows() {
		return preferredRows;
	}

	/**
	 * Set the preferred number of rows to show visually. This overrides the height
	 * element of all of other preferred size calculations. A value of -1 indicates
	 * the usual preferred size calculations should be used.
	 * 
	 * @param preferredRows preferred number of rows to show
	 * @return this for chaining
	 */
	public SelectList<O> setPreferredRows(int preferredRows) {
		if (this.preferredRows != preferredRows) {
			boundsSet = true;
			this.preferredRows = preferredRows;
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}
		return this;
	}

	public SelectList<O> onChanged(UIChangeListener<SelectList<O>, Set<SelectListItem<O>>> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public SelectList<O> addChangeListener(UIChangeListener<SelectList<O>, Set<SelectListItem<O>>> listener) {
		changeSupport.addListener(listener);
		return this;
	}

	public SelectList<O> removeChangeListener(UIChangeListener<SelectList<O>, Set<SelectListItem<O>>> listener) {
		changeSupport.removeListener(listener);
		return this;
	}

	public SelectList<O> unbindChanged(UIChangeListener<SelectList<O>, Set<SelectListItem<O>>> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	@Override
	public AbstractList<O, SelectListItem<O>> addListItem(String caption, O value, boolean isToggleItem,
			boolean isToggled) {
		SelectListItem<O> item = new SelectListItem<O>(this, caption, value, isToggleItem, isToggled);
		addListItem(item);
		return this;
	}

	/**
	 * Add the selected index (or set if single selection mode)
	 * 
	 * @param index int
	 */
	public void addSelectedIndex(int index) {
		if (selectionMode != SelectionMode.NONE) {
			Set<SelectListItem<O>> was = getSelectedListItems();
			if (selectionMode == SelectionMode.SINGLE) {
				selection.clear();
			}
			selection.add(index);
			SelectListItem<O> item = getListItem(index);
			item.getElement().dirtyLayout(true, LayoutType.styling);
			item.getElement().layoutChildren();
			changed(was);
		}
	}

	/**
	 * Clear all selection
	 */
	public void clearSelection() {
		if (!selection.isEmpty()) {
			Set<SelectListItem<O>> was = getSelectedListItems();
			selection.clear();
			dirtyLayout(true, LayoutType.styling);
			layoutChildren();
			changed(was);
		}
	}

	public void selectAll() {
		if (selection.size() != listItems.size() && !listItems.isEmpty()) {
			Set<SelectListItem<O>> was = getSelectedListItems();
			selection.clear();
			for (int i = 0; i < listItems.size(); i++)
				selection.add(i);
			dirtyLayout(true, LayoutType.styling);
			layoutChildren();
			changed(was);
		}
	}

	public int getSelectedIndex() {
		return selection.isEmpty() ? -1 : selection.get(0);
	}

	public O getSelectedValue() {
		return selection.isEmpty() ? null : listItems.get(selection.get(0)).getValue();
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	@Override
	public void insertListItem(int index, String caption, O value, boolean isToggleItem, boolean isToggled) {
		SelectListItem<O> item = new SelectListItem<O>(this, caption, value, isToggleItem, isToggled);
		insertListItem(index, item);
	}

	@Override
	public void removeFirstListItem() {
		if (selection.contains(0)) {
			selection.remove((Object) 0);
		}
		super.removeFirstListItem();
	}

	@Override
	public void removeLastListItem() {
		int lastIdx = listItems.size() - 1;
		if (selection.contains(lastIdx)) {
			selection.remove((Object) lastIdx);
		}
		super.removeLastListItem();
	}

	@Override
	public void removeListItem(int index) {
		if (selection.contains(index)) {
			selection.remove(index);
		}
		super.removeListItem(index);
	}

	/**
	 * Remove the selected index
	 * 
	 * @param index int
	 */
	public void removeSelectedIndex(int index) {
		Set<SelectListItem<O>> was = getSelectedListItems();
		selection.remove((Object) index);
		BaseElement element = listItems.get(index).getElement();
		element.dirtyLayout(true, LayoutType.styling);
		element.layoutChildren();
		changed(was);
	}

	public void scrollToItem(int index) {
		BaseElement element = listItems.get(index).getElement();
		float yoff = element.getY();
		float amt = 0;
		if (yoff + element.getHeight() > getScrollBoundsHeight() - scrollableArea.getY()) {
			amt = yoff + element.getHeight() - getScrollBoundsHeight() - scrollableArea.getY();
		} else if (yoff < scrollableArea.getY() * -1) {
			amt = scrollableArea.getY() + yoff;
		}
		if (amt != 0)
			scrollYBy(amt);
	}

	/**
	 * Sets a single selected item
	 * 
	 * @param index int
	 */
	public void setSelectedIndex(int index) {
		Set<SelectListItem<O>> was = getSelectedListItems();
		select(index, false);
		changed(was);
	}

	public void setSelectedIndexes(int... indices) {
		Set<SelectListItem<O>> was = getSelectedListItems();
		for (int index : indices) {
			select(index, false);
		}
		changed(was);
	}

	/**
	 * Sets a single selected item without firing any events
	 * 
	 * @param index int
	 */
	public void setSelectedIndexNoCallback(int index) {
		select(index, false);
	}

	public SelectList<O> setSelectionMode(SelectionMode selectionMode) {
		if (!Objects.equals(selectionMode, this.selectionMode)) {
			this.selectionMode = selectionMode;
			switch (selectionMode) {
			case NONE:
				selection.clear();
				break;
			case SINGLE:
				while (selection.size() > 1) {
					selection.remove(selection.size() - 1);
				}
				break;
			default:
				break;
			}
			dirtyLayout(true, LayoutType.styling);
			layoutChildren();
		}
		return this;
	}

	/**
	 * Returns a set containing all ListItems corresponding to the list of
	 * selectedIndexes
	 * 
	 * @return List<ListItem>
	 */
	public Set<SelectListItem<O>> getSelectedListItems() {
		Set<SelectListItem<O>> ret = new LinkedHashSet<>();
		for (Integer i : selection) {
			ret.add(getListItem(i));
		}
		return ret;
	}

	@Override
	protected PseudoStyles getItemPseudoStyles(SelectListItem<O> mi, PseudoStyles styles) {
		PseudoStyles ps = super.getItemPseudoStyles(mi, styles);
		int indexOf = listItems.indexOf(mi);
		if (mi == highlight)
			ps = PseudoStyles.get(ps).addStyle(PseudoStyle.hover);
		if (selection.contains(indexOf)) {
			if (ps != null) {
				ps.remove(PseudoStyle.active);
				ps.remove(PseudoStyle.hover);
			}
			ps = PseudoStyles.get(ps).addStyle(PseudoStyle.link);
		}
		return ps;
	}

	protected void select(int index, boolean multiple) {

		List<Integer> wasSelection = this.selection;
		List<Integer> newSelection = new ArrayList<>(Math.max(wasSelection.size(), 1));

		if (selectionMode == SelectionMode.NONE || selectionMode == SelectionMode.SINGLE
				|| ((selectionMode == SelectionMode.MULTIPLE || selectionMode == SelectionMode.TOGGLE) && !multiple)) {
			if (wasSelection.size() == 1 && wasSelection.get(0) == index)
				return;
		} else
			newSelection.addAll(wasSelection);

		if (selectionMode == SelectionMode.TOGGLE) {
			if (newSelection.contains(index)) {
				newSelection.remove((Object) index);
			} else {
				newSelection.add(index);
			}
		} else if (selectionMode != SelectionMode.NONE) {
			newSelection.add(index);
		}
		if (!newSelection.equals(wasSelection)) {
			SelectListItem<O> newItem = getListItem(index);
			for (Integer row : wasSelection) {
				SelectListItem<O> item = getListItem(row);
				if (!newItem.equals(item)) {
					BaseElement element = item.getElement();
					element.dirtyLayout(true, LayoutType.styling);
				}
			}
			this.selection = newSelection;
			BaseElement newElement = newItem.getElement();
			newElement.dirtyLayout(true, LayoutType.styling);
			layoutChildren();
		}
	}

	protected Layout<?, ?> createScrollPanelLayout() {
		return new ScrollPanelLayout<ScrollPanel>() {
			@Override
			protected Vector2f calcPreferredSize(ScrollPanel parent) {
				Vector2f cps = super.calcPreferredSize(parent);
				if (preferredRows != -1) {
					Vector4f allPadding = parent.getAllPadding();
					Label l = new Label(getScreen(), "W") {
						{
							styleClass = "item";
						}
					};
					l.setStyledParentContainer(SelectList.this);
					cps = new Vector2f(cps.x, (preferredRows * l.calcPreferredSize().y) + allPadding.y + allPadding.z);
				}
				return cps;
			}

		};
	}

	protected void setHighlighted(SelectListItem<O> item) {
		if (!Objects.equals(item, highlight)) {
			if (highlight != null) {
				SelectListItem<O> h = highlight;
				highlight = null;
				BaseElement el = h.getElement();
				el.dirtyLayout(true, LayoutType.styling);
				el.layoutChildren();
			}
			if (item != null) {
				highlight = item;
				BaseElement el = highlight.getElement();
				el.dirtyLayout(true, LayoutType.styling);
				el.layoutChildren();
			}
		}
	}

	protected void handleListItemClick(SelectListItem<O> menuItem) {
		if (menuItem.getIsToggleItem())
			menuItem.setIsToggled(!menuItem.getIsToggled());
	}

	protected void changed(Set<SelectListItem<O>> was) {
		if (changeSupport != null) {
			Set<SelectListItem<O>> is = getSelectedListItems();
			if (!Objects.equals(is, was))
				changeSupport.fireEvent(new UIChangeEvent<SelectList<O>, Set<SelectListItem<O>>>(this, was, is));
		}
	}

}
