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
package icetone.controls.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonGroup;
import icetone.controls.buttons.StatefulButton;
import icetone.controls.lists.SlideTray;
import icetone.controls.lists.SlideTray.ZOrderSort;
import icetone.controls.text.Label;
import icetone.core.AbstractGenericLayout;
import icetone.core.BaseElement;
import icetone.core.ElementManager;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.layout.Border;
import icetone.core.layout.FillLayout;
import icetone.core.utils.ClassUtil;
import icetone.core.utils.MathUtil;

/**
 * A container type control that can hold multiple child elements, with only one
 * visible at a time. Each child has an associated button, that upon clicking
 * with replace the currently visible child with it's own.
 * <p>
 * The buttons may be placed along any of the four edges using the
 * {@link Border} passed in to the constructor (or later set).
 *
 * @author t0neg0d
 * @author rockfire
 */
public class TabControl extends Element {

	public static class TabButton extends StatefulButton<Boolean> {
		{
			setStyleClass("tab-button");
		}

		public TabButton() {
			super();
		}

		public TabButton(ElementManager<?> screen) {
			super(screen);
		}

		public TabButton(ElementManager<?> screen, String text) {
			super(screen, text);
		}

		public TabButton(String text) {
			super(text);
		}

		public TabButton(String texturePath, String text) {
			super(texturePath, text);
		}

	}

	public class TabLayoutManager extends AbstractGenericLayout<TabControl, Object> {

		@Override
		protected Vector2f calcMinimumSize(TabControl parent) {
			Vector2f maxC = new Vector2f();
			for (TabPanel p : tabPanels) {
				maxC = MathUtil.max(maxC, p.calcMinimumSize());
			}
			Vector2f cPref = tabSlider.calcMinimumSize();
			if (getOrientation().equals(Orientation.HORIZONTAL)) {
				cPref.y += maxC.y;
				cPref.x = Math.max(cPref.x, maxC.x);
			} else {
				cPref.x += maxC.x;
				cPref.y = Math.max(cPref.y, maxC.y);
			}
			cPref.addLocal(parent.getTotalPadding());
			return cPref;
		}

		@Override
		protected Vector2f calcPreferredSize(TabControl parent) {
			Vector2f maxC = new Vector2f();
			for (TabPanel p : tabPanels) {
				Vector2f prf = p.calcPreferredSize();
				maxC = MathUtil.max(maxC, prf);
			}
			Vector2f cPref = tabSlider.calcPreferredSize();
			if (getOrientation().equals(Orientation.HORIZONTAL)) {
				cPref.y += maxC.y;
				cPref.x = Math.max(cPref.x, maxC.x);
			} else {
				cPref.x += maxC.x;
				cPref.y = Math.max(cPref.y, maxC.y);
			}
			cPref.addLocal(parent.getTotalPadding());
			return cPref;
		}

		@Override
		protected void onLayout(TabControl ltc) {

			Vector2f panelSize;
			Vector2f panelPos;

			Vector2f ps = tabSlider.calcPreferredSize();
			Vector4f insets = ltc.getAllPadding();

			try {
				int selectedTabIndex = getSelectedTabIndex();
				// TODO needed?
				if (selectedTabIndex == -1 && !tabPanels.isEmpty()) {
					selectedTabIndex = 0;
				}

				if (getTabPlacement().equals(Border.SOUTH)) {
					tabSlider.setBounds(insets.x, ltc.getHeight() - insets.w - ps.y,
							ltc.getWidth() - insets.x - insets.y, ps.y);
					panelSize = new Vector2f(ltc.getDimensions().x - insets.x - insets.y,
							ltc.getDimensions().y - ps.y - insets.z - insets.w + getIndent());
					panelPos = new Vector2f(insets.x, insets.y);
				} else if (getTabPlacement().equals(Border.EAST)) {
					tabSlider.setBounds(ltc.getWidth() - insets.y - ps.x, insets.y, ps.x, ltc.getHeight() - insets.w - insets.z);
					panelSize = new Vector2f(ltc.getDimensions().x - insets.x - insets.y - ps.x + getIndent(),
							ltc.getDimensions().y - insets.z - insets.w);
					panelPos = new Vector2f(insets.x, insets.y);
				} else if (getTabPlacement().equals(Border.WEST)) {
					tabSlider.setBounds(insets.x, insets.y, ps.x, ltc.getHeight() - insets.w - insets.z);
					panelSize = new Vector2f(ltc.getDimensions().x - insets.x - insets.y - ps.x + getIndent(),
							ltc.getDimensions().y - insets.z - insets.w);
					panelPos = new Vector2f(insets.x + ps.x - getIndent(), insets.y);
				} else {
					tabSlider.setBounds(insets.x, 0, ltc.getWidth() - insets.x - insets.y, ps.y);
					panelSize = new Vector2f(ltc.getDimensions().x - insets.x - insets.y,
							ltc.getDimensions().y - ps.y - insets.z - insets.w + getIndent());
					panelPos = new Vector2f(insets.x, insets.y + ps.y - getIndent());
				}

				for (TabPanel panel : tabPanels) {
					panel.setBounds(panelPos.x, panelPos.y, panelSize.x, panelSize.y);
				}

			} catch (Exception e) {
				throw new RuntimeException("Could not get at tab panels.", e);
			}

		}
	}

	public static class TabPanel extends Element {

		public TabPanel(ElementManager<?> screen) {
			super(screen);
			setLayoutManager(new FillLayout());
		}
	}

	protected ButtonGroup<StatefulButton<Boolean>> tabButtonGroup;
	protected List<TabPanel> tabPanels = new ArrayList<>();

	protected List<StatefulButton<Boolean>> tabs = new ArrayList<>();
	protected SlideTray tabSlider;

	private int selectedTabIndex = -1;
	private TabPanel showingTab;
	private Border tabPlacement;

	public TabControl() {
		this(BaseScreen.get());
	}

	public TabControl(Border tabPlacement) {
		this(BaseScreen.get(), tabPlacement);
	}

	public TabControl(ElementManager<?> screen) {
		this(screen, Orientation.HORIZONTAL);
	}

	public TabControl(ElementManager<?> screen, Border tabPlacement) {
		super(screen);
		setLayoutManager(new TabLayoutManager());
		setTabPlacement(tabPlacement);
		setMovable(false);
		setResizable(false);

	}

	public TabControl(ElementManager<?> screen, Orientation orientation) {
		this(screen, orientation == Orientation.HORIZONTAL ? Border.NORTH : Border.WEST);
	}

	public TabControl(Orientation orientation) {
		this(BaseScreen.get(), orientation);
	}

	public TabControl addTab(BaseElement content) {
		addTab((String) null, content);
		return this;
	}

	public TabControl addTab(String title) {
		TabButton tabButton = getTabButton();
		addTab(title, tabButton, false);
		return this;
	}

	public TabControl addTab(String title, BaseElement content) {
		TabButton tabButton = getTabButton();
		addTabChild(addTab(title, tabButton, false), content);
		return this;
	}

	public TabControl addTab(TabButton tab, BaseElement content) {
		addTabChild(addTab(tab.getText(), tab, true), content);
		return this;
	}

	/**
	 * Adds the provided Element to the panel associated with the tab index
	 * 
	 * @param index
	 *            int Tab index
	 * @param element
	 *            Element
	 */
	public TabControl addTabChild(int index, BaseElement element) {
		if (index > -1 && index < tabs.size()) {
			BaseElement tabPanel = tabPanels.get(index);
			tabPanel.addElement(element);
			element.addClippingLayer(tabPanel);
		}
		return this;
	}

	public Orientation getOrientation() {
		return tabPlacement == Border.NORTH || tabPlacement == Border.SOUTH ? Orientation.HORIZONTAL
				: Orientation.VERTICAL;
	}

	public TabPanel getSelectedTab() {
		return tabPanels.get(selectedTabIndex);
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public SlideTray getSlider() {
		return tabSlider;
	}

	@Override
	public List<String> getStyleClassNames() {
		List<String> l = new ArrayList<>(super.getStyleClassNames());
		if (tabPlacement != null) {
			switch (tabPlacement) {
			case NORTH:
				l.add("North" + ClassUtil.getMainClassName(getClass()));
				break;
			case SOUTH:
				l.add("South" + ClassUtil.getMainClassName(getClass()));
				break;
			case WEST:
				l.add("West" + ClassUtil.getMainClassName(getClass()));
				break;
			case EAST:
				l.add("East" + ClassUtil.getMainClassName(getClass()));
				break;
			default:
				break;
			}
		}
		return l;
	}

	public TabButton getTabButton(BaseElement content) {
		int idx = tabPanels.indexOf(content);
		if (idx == -1)
			return null;
		return (TabButton) tabs.get(idx);
	}

	public TabButton getTabButton(int index) {
		return (TabButton) tabs.get(index);
	}

	public TabButton getTabButton(String title) {
		for (StatefulButton<?> b : tabs)
			if (title.equals(b.getText()))
				return (TabButton) b;
		return null;
	}

	public Border getTabPlacement() {
		return this.tabPlacement;
	}

	public String getTabTitle(int index) {
		return tabs.get(index).getText();
	}

	public TabControl removeAllTabs() {
		tabs.forEach(t -> tabButtonGroup.removeButton(t));
		tabs.clear();
		tabSlider.removeAllTrayElements();
		tabPanels.forEach(t -> t.removeFromParent());
		return this;
	}

	public TabControl removeTab(int index) {
		StatefulButton<Boolean> removeButton = tabs.remove(index);
		removeButton.removeFromParent();
		tabSlider.removeTrayElement(removeButton);
		TabPanel tabPanel = tabPanels.remove(index);
		tabPanel.removeFromParent();
		if (index > 0) {
			setSelectedTabIndex(index - 1);
		}
		if (showingTab == tabPanel) {
			showTab(tabPanels.isEmpty() ? null : tabPanels.get(0));
		}
		tabButtonGroup.removeButton(removeButton);
		layoutChildren();
		return this;
	}

	public TabControl setSelectedTab(int index) {
		// if (tabButtonGroup.)
		tabButtonGroup.setSelected(index);
		tabSlider.toFront(tabs.get(index));
		return this;
	}

	public void setSelectedTabIndex(int index) {
		this.selectedTabIndex = index;
		StatefulButton<Boolean> selectedTabButton = tabs.get(index);
		selectedTabButton.setState(true);
	}

	public TabControl setTabPlacement(Border tabPlacement) {
		if (!Objects.equals(tabPlacement, this.tabPlacement)) {

			if (tabPlacement == Border.CENTER) {
				throw new IllegalArgumentException("Center is not a valid tab placement.");
			}

			this.tabPlacement = tabPlacement;

			if (tabPlacement == Border.NORTH || tabPlacement == Border.SOUTH) {
				tabSlider.setOrientation(Orientation.HORIZONTAL);
				tabSlider.setZOrderSorting(ZOrderSort.LAST_TO_FIRST);
			} else {
				tabSlider.setOrientation(Orientation.VERTICAL);
				tabSlider.setZOrderSorting(ZOrderSort.FIRST_TO_LAST);
			}

			dirtyLayout(true, LayoutType.all);
			layoutChildren();
		}
		return this;
	}

	public TabControl setTabTitle(int index, String name) {
		tabs.get(index).setText(name);
		layoutChildren();
		return this;
	}

	/**
	 * Enables the SliderToEffect of the SlideTray containing the tabs
	 * 
	 * @param useSlideEffect
	 *            boolean
	 */
	public TabControl setUseSlideEffect(boolean useSlideEffect) {
		tabSlider.setUseSlideEffect(useSlideEffect);
		return this;
	}

	protected int addTab(String title, StatefulButton<Boolean> tabButton, boolean isCustomButton) {

		// label = getButtonLabel(title);
		// AnimText txt = label.getTextElement();
		// txt.setPosition(-((txt.getLineWidth() / 2f)), (txt.getLineHeight()) +
		// (label.getHeight() / 2f));
		// txt.setOrigin(txt.getLineWidth() / 2f, txt.getLineHeight() / 2f);
		// txt.setRotation(90);
		// label.setDimensions(label.getHeight(), label.getWidth());
		// tabButton.addElement(label);

		tabButton.setText(title);

		TabPanel panel = createTabPanel();
		tabs.add(tabButton);
		tabPanels.add(panel);

		if (tabPanels.size() == 1)
			showTab(panel);
		else {
			dirtyLayout(false, LayoutType.boundsChange());
			layoutChildren();
		}

		// if (tabPanels.size() > 1)
		// panel.hide();

		tabSlider.addTrayElement(tabButton);
		tabButtonGroup.addButton(tabButton);

		// tab.addClippingLayer(tab);

		// if (orientation == Orientation.VERTICAL) {
		// label.addClippingLayer(tab);
		// label.removeClippingLayer(label);
		// // tab.layoutTextElement();
		// }

		return tabs.size() - 1;
	}

	@Override
	protected void configureStyledElement() {

		tabButtonGroup = new ButtonGroup<StatefulButton<Boolean>>();
		tabButtonGroup.onChange(evt -> {
			Button selectedTab = evt.getNewValue();
			int index = evt.getSource().getButtons().indexOf(selectedTab);
			TabPanel selectedPanel = tabPanels.get(index);

			tabSlider.toFront(selectedTab);
			showTab(selectedPanel);

			selectedTabIndex = index;

			/*
			 * TODO seems excessive, but without this tabs get 'scrunched up'
			 * when you click between them. Find out why
			 */
			// TabControl.this.dirtyLayout(true, LayoutType.all);
			// TabControl.this.layoutChildren();

			onTabSelect(index);
		});

		if (tabPlacement == Border.NORTH || tabPlacement == Border.SOUTH) {
			tabSlider = new SlideTray(screen, Orientation.HORIZONTAL);
			tabSlider.setZOrderSorting(ZOrderSort.LAST_TO_FIRST);
		} else {
			tabSlider = new SlideTray(screen, Orientation.VERTICAL);
			tabSlider.setZOrderSorting(ZOrderSort.FIRST_TO_LAST);
		}

		addElement(tabSlider);
	}

	protected TabPanel createTabPanel() {
		return new TabPanel(screen);
	}

	protected Label getButtonLabel(String title) {
		Label label = new Label(screen);
		label.setStyleClass("tab-label");
		label.setText(title);
		return label;
	}

	/**
	 * Adds a new Tab and TabPanel to the TabControl
	 * 
	 * @param title
	 *            String The Title to set for the Tab
	 */
	protected TabButton getTabButton() {
		return new TabButton(screen);
	}

	@Override
	protected Collection<BaseElement> getZSortedChildren() {
		List<BaseElement> sorted = new ArrayList<>(super.getZSortedChildren());
		Collections.reverse(sorted);
		return sorted;
	}

	protected void onTabSelect(int index) {
	}

	protected void showTab(TabPanel panel) {
		if (!Objects.equals(panel, showingTab)) {
			if (showingTab != null) {
				removeElement(showingTab);
				showingTab = null;
			}
			if (panel != null) {
				showingTab = panel;
				addElement(panel);
			}
			layoutChildren();
		}

	}
}
