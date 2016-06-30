package icetone.controls.windows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Spatial;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.RadioButtonGroup;
import icetone.controls.lists.SlideTray;
import icetone.controls.lists.SlideTray.ZOrderSort;
import icetone.controls.text.LabelElement;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.LUtil;
import icetone.core.utils.BitmapTextUtil;
import icetone.core.utils.UIDUtil;
import icetone.framework.core.AnimText;

/**
 *
 * @author t0neg0d
 */
public class TabControl extends Element {
	protected Vector4f insets = Vector4f.ZERO;

	protected List<Button> tabs = new ArrayList<>();
	protected Map<Integer, ITabPanel> tabPanels = new HashMap<>();
	protected int tabButtonIndex = 0;
	protected RadioButtonGroup tabButtonGroup;
	protected Vector4f tabResizeBorders;
	protected SlideTray tabSlider;
	protected boolean isFixedTabSize = false;
	protected float fixedTabSize = 0;
	protected float tabTraySize, tabTrayOverlap = 3;
	protected float tabOverhang = 6;
	protected float labelPadding = 24;
	private Orientation orientation = Orientation.HORIZONTAL;

	private int selectedTabIndex = -1;

	public TabControl() {
		this(Screen.get(), Vector2f.ZERO);
	}

	public TabControl(ElementManager screen) {
		this(screen, Vector2f.ZERO);
	}

	public TabControl(Orientation orientation) {
		this(Screen.get(), Vector2f.ZERO, orientation);
	}

	public TabControl(ElementManager screen, Orientation orientation) {
		this(screen, Vector2f.ZERO, orientation);
	}

	public TabControl(ElementManager screen, Vector2f position) {
		this(screen, position, LUtil.LAYOUT_SIZE);
	}

	public TabControl(ElementManager screen, Vector2f position, Orientation orientation) {
		this(screen, position, LUtil.LAYOUT_SIZE, screen.getStyle("Tab").getVector4f("resizeBorders"),
				screen.getStyle("Tab").getString("defaultImg"), orientation);
	}

	public TabControl(ElementManager screen, Vector2f position, Vector2f dimensions) {
		this(screen, position, dimensions, screen.getStyle("Tab").getVector4f("resizeBorders"),
				screen.getStyle("Tab").getString("defaultImg"));
	}

	public TabControl(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
		this(screen, position, dimensions, resizeBorders, defaultImg, Orientation.HORIZONTAL);
	}

	public TabControl(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Orientation orientation) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, orientation);
	}

	public TabControl(ElementManager screen, String UID, Vector2f position) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, Vector4f.ZERO, screen.getStyle("Tab").getString("defaultImg"),
				Orientation.HORIZONTAL);
	}

	public TabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
		this(screen, UID, position, dimensions, Vector4f.ZERO, null, Orientation.HORIZONTAL);
	}

	public TabControl(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Orientation orientation) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg);

		this.orientation = orientation;

		this.setIsMovable(false);
		this.setIsResizable(false);
		this.setClipPadding(screen.getStyle("Tab").getFloat("clipPadding"));

		layoutManager = new TabLayoutManager(screen);
		setIgnoreMouse(true);
		configTabControl();

	}

	public SlideTray getSlider() {
		return tabSlider;
	}

	public void setSelectedTabIndex(int index) {
		this.selectedTabIndex = index;
		Button selectedTabButton = tabs.get(index);
		selectedTabButton.setIsToggled(true);
	}

	public String getTabTitle(int index) {
		return tabs.get(index).getText();
	}

	public ITabPanel getSelectedTab() {
		return tabPanels.get(selectedTabIndex);
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setTabTitle(int index, String name) {
		tabs.get(index).setText(name);
		layoutChildren();
	}

	public Orientation getOrientation() {
		return this.orientation;
	}

	/**
	 * Sets the resize borders for use with ElementQuadGrid per tab
	 * 
	 * @param tabResizeBorders
	 */
	public void setTabResizeBorders(Vector4f tabResizeBorders) {
		this.tabResizeBorders.set(tabResizeBorders);
	}

	/**
	 * Sets the width to always use for Tabs
	 * 
	 * @param fixedTabWidth
	 *            float Forced width of all Tabs
	 */
	public void setFixedTabSize(float fixedTabWidth) {
		if (fixedTabWidth > 0) {
			isFixedTabSize = true;
			this.fixedTabSize = fixedTabWidth;
		} else {
			isFixedTabSize = false;
			this.fixedTabSize = 0;
		}
	}

	/**
	 * Enables the SliderToEffect of the SlideTray containing the tabs
	 * 
	 * @param useSlideEffect
	 *            boolean
	 */
	public void setUseSlideEffect(boolean useSlideEffect) {
		tabSlider.setUseSlideEffect(useSlideEffect);
	}

	public void removeTab(int index) {
		/* Ugh .... It seems when removing elements that they don't get fully removed
		 * from Screen AND the parent "elements" list
		 */
		final Button removeButton = tabs.remove(index);
		screen.removeElement(removeButton);
		Element elTray = tabSlider.getChildElementById(tabSlider.getUID() + ":elTray");
		elTray.getElementsAsMap().remove(removeButton.getUID());
		final ITabPanel remove = tabPanels.remove(index);
		screen.removeElement((Element) remove);
		getElementsAsMap().remove(((Element) remove).getUID());
		if (index > 0) {
			setSelectedTabIndex(index - 1);
		}

		// *Sigh* .. need to remove from button group too
		tabButtonGroup.removeButton(removeButton);

		// *sigh* and slider tray
		List<Element> trayEls = tabSlider.getTrayElements();
		trayEls.remove(removeButton);

		// Need to fix the panel, button UIDs and keys
		Map<Integer, ITabPanel> m = new HashMap<Integer, ITabPanel>();
		int idx = 0;
		for (Integer i : tabPanels.keySet()) {
			// Fix button
			Button b = tabs.get(idx);
			b.setUID(getUID() + ":Tab" + idx);

			// Fix panel
			ITabPanel p = tabPanels.get(i);
			((Element) p).setUID(getUID() + ":TabPanel" + idx);

			// Fix key
			m.put(idx, p);
			idx++;
		}
		tabPanels.clear();
		tabPanels.putAll(m);
		tabButtonIndex = idx;

		layoutChildren();
	}

	public void addTab(String title, Element content) {
		addTabChild(addTab(title, getTabButton(), false), content);
	}

	public void addTab(String title) {
		ButtonAdapter tab = getTabButton();
		addTab(title, tab, false);
	}

	public void addTab(String title, ButtonAdapter tab) {
		addTab(title, tab, true);
	}

	public void addTabWithRMBSupport(String title) {
		ButtonAdapter tab = getTabButtonWithRMBSupport();
		addTab(title, tab, true);
	}

	/**
	 * Adds the provided Element to the panel associated with the tab index
	 * 
	 * @param index
	 *            int Tab index
	 * @param element
	 *            Element
	 */
	public void addTabChild(int index, Element element) {
		if (index > -1 && index < tabs.size()) {
			Element tabPanel = (Element) tabPanels.get(index);
			tabPanel.addChild(element);
			element.addClippingLayer(tabPanel);
		}
	}

	public void setSelectedTab(int index) {
		// if (tabButtonGroup.)
		tabButtonGroup.onSelect(index, tabs.get(index));
		tabSlider.toFront(tabs.get(index));
	}

	protected void onTabRightClick(int indexOf, MouseButtonEvent evt) {
	}

	protected int addTab(String title, ButtonAdapter tab, boolean isCustomButton) {
		LabelElement label = null;

		if (orientation == Orientation.VERTICAL) {
			label = getButtonLabel(title);
			AnimText txt = label.getAnimText();
			txt.setPosition(-((txt.getLineWidth() / 2f)), (txt.getLineHeight()) + (label.getHeight() / 2f));
			txt.setOrigin(txt.getLineWidth() / 2f, txt.getLineHeight() / 2f);
			txt.setRotation(90);
			label.setDimensions(label.getHeight(), label.getWidth());
			label.updateClippingLayers();
			txt.update(0);
		}

		if (!isCustomButton) {
			tab.clearAltImages();
			String hImg = (orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("tabHoverImg")
					: screen.getStyle("Tab").getString("tabHoverImgV");
			String pImg = (orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("tabPressedImg")
					: screen.getStyle("Tab").getString("tabPressedImgV");
			tab.setButtonHoverInfo(hImg, screen.getStyle("Tab").getColorRGBA("hoverColor"));
			tab.setButtonPressedInfo(pImg, screen.getStyle("Tab").getColorRGBA("pressedColor"));
		}

		tab.setElementUserData(tabButtonIndex);

		if (orientation == Orientation.VERTICAL) {
			tab.addChild(label);

			final Align hal = Align.valueOf(screen.getStyle("Tab").getString("textAlign"));
			final VAlign val = VAlign.valueOf(screen.getStyle("Tab").getString("textVAlign"));

			// Flip the alignments
			switch (hal) {
			case Center:
				label.setTextVAlign(VAlign.Center);
				break;
			case Left:
				label.setTextVAlign(VAlign.Top);
				break;
			case Right:
				label.setTextVAlign(VAlign.Bottom);
				break;

			}
			switch (val) {
			case Center:
				label.setTextAlign(Align.Center);
				break;
			case Top:
				label.setTextAlign(Align.Left);
				break;
			case Bottom:
				label.setTextAlign(Align.Right);
				break;

			}
			label.centerToParent();
		} else {

			tab.setText(title);
		}

		tabButtonGroup.addButton(tab);
		int tabIndex = tabs.size();
		tabs.add(tab);

		addTabPanel();

		tabSlider.addTrayElement(tab);
		if (tabButtonIndex == 0)
			tab.setIsToggled(true);

		tab.addClippingLayer(tab);

		if (orientation == Orientation.VERTICAL) {
			label.addClippingLayer(tab);
			label.removeClippingLayer(label);
			tab.resetTextElement();
		}

		tabButtonIndex++;
		layoutChildren();

		return tabIndex;
	}

	protected void onTabSelect(int index) {
	}

	protected String getTabStyleName() {
		return "Tab";
	}

	/**
	 * Adds a new Tab and TabPanel to the TabControl
	 * 
	 * @param title
	 *            String The Title to set for the Tab
	 */
	protected ButtonAdapter getTabButton() {
		return new TabButton(screen, getUID() + ":Tab" + tabButtonIndex, orientation) {

			@Override
			public float getAlphaFactor() {
				return super.getAlphaFactor() * (tabs.indexOf(this) == selectedTabIndex ? 1 : 0.5f);
			}

		};
	}

	protected LabelElement getButtonLabel(String title) {
		LabelElement label = new LabelElement(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		label.setText(title);
		label.setUseTextClipping(true);
		return label;
	}

	protected void configTabControl() {
		final String tabStyleName = getTabStyleName();
		if (orientation == Orientation.HORIZONTAL) {
			tabResizeBorders = screen.getStyle(tabStyleName).getVector4f("resizeBorders");
		} else {
			tabResizeBorders = screen.getStyle(tabStyleName).getVector4f("resizeBorders");
		}

		tabOverhang = screen.getStyle("Tab").getFloat("tabOverhang");
		labelPadding = screen.getStyle("Tab").getFloat("labelPadding");
		tabTrayOverlap = screen.getStyle("Tab").getFloat("tabTrayOverlap");

		tabButtonGroup = new RadioButtonGroup(screen, getUID() + ":TabButtonGroup") {
			@Override
			public void onSelect(int index, Button value) {
				Set<Integer> keys = tabPanels.keySet();
				Element selectedPanel = (Element) tabPanels.get(index);
				Button selectedTab = tabs.get(index);
				tabSlider.toFront(selectedTab);
				for (Integer key : keys) {
					if (key != index) {
						((Element) tabPanels.get(key)).hide();
					}
				}
				selectedPanel.show();
				selectedTab.removeFromParent();
				selectedTab.getElementParent().attachChild(selectedTab);
				selectedPanel.hide();
				// selectedPanel.dirtyLayout(true);
				// selectedPanel.layoutChildren();
				selectedPanel.show();

				selectedTabIndex = index;

				/* TODO seems excessive, but without this tabs get 'scrunched up' when you click between them.
				 * Find out why
				 */
				TabControl.this.dirtyLayout(true);
				TabControl.this.layoutChildren();

				onTabSelect(index);

				for (Button button : tabs) {
					button.updateGlobalAlpha();
				}
			}
		};

		if (orientation == Orientation.HORIZONTAL) {
			tabSlider = new SlideTray(screen, getUID() + ":tabSlider", Orientation.HORIZONTAL) {
				@Override
				public void controlResizeHook() {
					this.updateClippingLayers();
				}
			};
			tabSlider.setOverhang(-tabOverhang);
			tabSlider.setZOrderSorting(ZOrderSort.LAST_TO_FIRST);
			tabSlider.alignButtonsV(VAlign.Top);
		} else {
			tabSlider = new SlideTray(screen, getUID() + ":tabSlider", Orientation.VERTICAL) {
				@Override
				public void controlResizeHook() {
					this.updateClippingLayers();
				}
			};
			tabSlider.setOverhang(-tabOverhang);
			tabSlider.setZOrderSorting(ZOrderSort.FIRST_TO_LAST);
			tabSlider.alignButtonsH(Align.Left);
		}

		addChild(tabSlider);
	}

	private void addTabPanel() {
		TabPanel panel = createTabPanel();
		addChild(panel);
		tabPanels.put(tabButtonIndex, panel);

		if (tabButtonIndex != 0)
			panel.hide();
	}

	private ButtonAdapter getTabButtonWithRMBSupport() {
		ButtonAdapter tab = new ButtonAdapter(screen, getUID() + ":Tab" + tabButtonIndex, Vector2f.ZERO, LUtil.LAYOUT_SIZE,
				(getOrientation() == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getVector4f("tabResizeBorders")
						: screen.getStyle("Tab").getVector4f("tabResizeBordersV"),
				(getOrientation() == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("tabImg")
						: screen.getStyle("Tab").getString("tabImgV")) {
			@Override
			public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {
				super.onButtonMouseRightUp(evt, toggled);
				onTabRightClick((Integer) getElementUserData(), evt);

			}
		};
		String hImg = (getOrientation() == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("tabHoverImg")
				: screen.getStyle("Tab").getString("tabHoverImgV");
		String pImg = (getOrientation() == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("tabPressedImg")
				: screen.getStyle("Tab").getString("tabPressedImgV");
		tab.setButtonHoverInfo(hImg, screen.getStyle("Tab").getColorRGBA("hoverColor"));
		tab.setButtonPressedInfo(pImg, screen.getStyle("Tab").getColorRGBA("pressedColor"));
		return tab;
	}

	protected TabPanel createTabPanel() {
		TabPanel panel = new TabPanel(screen, getUID() + ":TabPanel" + tabButtonIndex, Vector2f.ZERO, LUtil.LAYOUT_SIZE);
		return panel;
	}

	public interface ITabPanel {
	}

	public class TabButton extends ButtonAdapter {
		public TabButton(ElementManager screen, String uid, Orientation orientation) {
			super(screen, uid, Vector2f.ZERO, LUtil.LAYOUT_SIZE,
					(orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getVector4f("tabResizeBorders")
							: screen.getStyle("Tab").getVector4f("tabResizeBordersV"),
					(orientation == Orientation.HORIZONTAL) ? screen.getStyle("Tab").getString("tabImg")
							: screen.getStyle("Tab").getString("tabImgV"));
		}

		@Override
		public Vector2f getPreferredDimensions() {
			return new Vector2f(getButtonWidth(), getButtonHeight());
		}

		protected float getButtonWidth() {
			Button selectedTab = selectedTabIndex == -1 ? null : tabs.get(selectedTabIndex);
			float tdec = this == selectedTab ? 0 : tabTrayOverlap;
			if (orientation == Orientation.HORIZONTAL) {
				final String txt = getText();
				if (txt == null || txt.equals("")) {
					return screen.getStyle(getTabStyleName()).getVector2f("tabDefaultSize").x;
				} else {
					float width = BitmapTextUtil.getTextWidth(this, txt);
					return width + (tabResizeBorders.z + tabResizeBorders.y + (labelPadding * 2));
				}
			} else {

				if (isFixedTabSize) {
					return fixedTabSize;
				} else {
					if (getElements().isEmpty()) {
						return screen.getStyle(getTabStyleName()).getVector2f("tabDefaultSize").y;
					} else {
						LabelElement l = (LabelElement) getElements().iterator().next();
						float height = l.getAnimText().getLineHeight();
						return height + (tabResizeBorders.w + tabResizeBorders.x) - tdec;
					}
				}
			}
		}

		protected float getButtonHeight() {
			Button selectedTab = selectedTabIndex == -1 ? null : tabs.get(selectedTabIndex);
			float tdec = this == selectedTab ? 0 : tabTrayOverlap;

			if (orientation == Orientation.HORIZONTAL) {
				if (isFixedTabSize) {
					return fixedTabSize - tdec;
				}
				if (getText().length() == 0) {
					return screen.getStyle(getTabStyleName()).getVector2f("tabDefaultSize").y;
				} else {
					return getTextElement().getLineHeight() + (tabResizeBorders.w + tabResizeBorders.x) - tdec + (labelPadding * 2);
				}
			} else {
				if (getElements().isEmpty()) {
					return screen.getStyle(getTabStyleName()).getVector2f("tabDefaultSize").x;
				} else {
					LabelElement l = (LabelElement) getElements().iterator().next();
					float height = l.getAnimText().getLineWidth();
					return height + (tabResizeBorders.y + tabResizeBorders.z + (labelPadding * 2)) - tdec;
				}
			}
		}
	}

	public static class TabPanel extends Element implements ITabPanel {
		/**
		 * Creates a new instance of the Panel control
		 * 
		 * @param screen
		 *            The screen control the Element is to be added to
		 * @param UID
		 *            A unique String identifier for the Element
		 * @param position
		 *            A Vector2f containing the x/y position of the Element
		 */
		public TabPanel(ElementManager screen, String UID, Vector2f position) {
			this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle("Tab").getVector4f("panelResizeBorders"),
					screen.getStyle("Tab").getString("panelImg"));
		}

		/**
		 * Creates a new instance of the Panel control
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
		 */
		public TabPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
			this(screen, UID, position, dimensions, screen.getStyle("Tab").getVector4f("panelResizeBorders"),
					screen.getStyle("Tab").getString("panelImg"));
		}

		/**
		 * Creates a new instance of the Panel control
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
		 *            A Vector4f containg the border information used when
		 *            resizing the default image (x = N, y = W, z = E, w = S)
		 * @param defaultImg
		 *            The default image to use for the Slider's track
		 */
		public TabPanel(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
				String defaultImg) {
			super(screen, UID, position, dimensions, resizeBorders, defaultImg);
			setTextPadding(screen.getStyle("Tab").getVector4f("panelTextPadding"));
			setLayoutManager(new BorderLayout(0, 0));
		}
	}

	public class TabLayoutManager extends AbstractLayout {

		private final ElementManager screen;

		public TabLayoutManager(ElementManager screen) {
			this.screen = screen;
		}

		public Vector2f maximumSize(Element parent) {

			Vector2f maxC = new Vector2f();
			for (ITabPanel p : tabPanels.values()) {
				for (Element e : ((Element) p).getElements()) {
					Vector2f cPref = LUtil.getContainerMaximumDimensions(e);
					maxC.x = Math.max(maxC.x, cPref.x);
					maxC.y = Math.max(maxC.y, cPref.y);
				}
			}

			Vector2f cPref = LUtil.getContainerMaximumDimensions(tabSlider).clone();
			if (getOrientation().equals(Orientation.HORIZONTAL)) {
				cPref.y += maxC.y;
				cPref.x = Math.max(cPref.x, maxC.x);
			} else {
				cPref.x += maxC.x;
				cPref.y = Math.max(cPref.y, maxC.y);
			}

			return cPref;
		}

		public Vector2f preferredSize(Element parent) {
			Vector2f maxC = new Vector2f();
			for (ITabPanel p : tabPanels.values()) {
				for (Element e : ((Element) p).getElements()) {
					Vector2f cPref = LUtil.getContainerPreferredDimensions(e);
					maxC.x = Math.max(maxC.x, cPref.x);
					maxC.y = Math.max(maxC.y, cPref.y);
				}
			}

			Vector2f cPref = LUtil.getContainerPreferredDimensions(tabSlider).clone();
			if (getOrientation().equals(Orientation.HORIZONTAL)) {
				cPref.y += maxC.y;
				cPref.x = Math.max(cPref.x, maxC.x);
			} else {
				cPref.x += maxC.x;
				cPref.y = Math.max(cPref.y, maxC.y);
			}

			return cPref;
		}

		public void layout(Element parent) {

			TabControl ltc = (TabControl) parent;
			Vector2f panelSize;
			Vector2f panelPos;

			Vector2f ps = LUtil.getContainerPreferredDimensions(tabSlider);

			try {
				int selectedTabIndex = getSelectedTabIndex();
				// TODO needed?
				if (selectedTabIndex == -1 && !tabPanels.isEmpty()) {
					selectedTabIndex = 0;
				}

				if (getOrientation().equals(Orientation.HORIZONTAL)) {
					LUtil.setBounds(tabSlider, insets.x, 0, ltc.getWidth() - insets.y - insets.z, ps.y);
					panelSize = new Vector2f(ltc.getDimensions().x - insets.z - insets.y,
							ltc.getDimensions().y - ps.y - insets.x - insets.w + tabTrayOverlap);
					panelPos = new Vector2f(insets.y, insets.x + ps.y - tabTrayOverlap);
				} else {
					LUtil.setBounds(tabSlider, insets.y, insets.x, ps.x, ltc.getHeight() - insets.x - insets.z);
					panelSize = new Vector2f(ltc.getDimensions().x - insets.z - insets.y - ps.x + tabTrayOverlap,
							ltc.getDimensions().y - insets.x - insets.w);
					panelPos = new Vector2f(insets.y + ps.x - tabTrayOverlap, insets.x);
				}
				tabSlider.layoutChildren();

				int tabIndex = 0;
				for (ITabPanel panel : tabPanels.values()) {
					LUtil.setBounds((Element) panel, panelPos.x, panelPos.y, panelSize.x, panelSize.y);
					if (tabIndex == selectedTabIndex) {
						if (!((Element) panel).getIsVisible()) {
							((Element) panel).show();
						}
						((Element) panel).setClipPadding(screen.getStyle(getTabStyleName()).getFloat("clipPadding"));
					} else {
						if (((Element) panel).getIsVisible()) {
							((Element) panel).hide();
						}
					}
					for (Spatial s : LUtil.getAllChildren((Element) panel)) {
						if (s instanceof Element) {
							Element el = (Element) s;
							LUtil.setBounds(el, 0, 0, panelSize.x, panelSize.y);
						}
					}
					tabIndex++;
				}

				updateClippingLayers();

			} catch (Exception e) {
				throw new RuntimeException("Could not get at tab panels.", e);
			}

		}

		public Vector2f minimumSize(Element parent) {

			Vector2f maxC = new Vector2f();
			for (ITabPanel p : tabPanels.values()) {
				for (Element e : ((Element) p).getElements()) {
					Vector2f cPref = LUtil.getContainerMinimumDimensions(e);
					maxC.x = Math.max(maxC.x, cPref.x);
					maxC.y = Math.max(maxC.y, cPref.y);
				}
			}

			Vector2f cPref = LUtil.getContainerMinimumDimensions(tabSlider).clone();
			if (getOrientation().equals(Orientation.HORIZONTAL)) {
				cPref.y += maxC.y;
				cPref.x = Math.max(cPref.x, maxC.x);
			} else {
				cPref.x += maxC.x;
				cPref.y = Math.max(cPref.y, maxC.y);
			}

			return cPref;
		}

		public void constrain(Element child, Object constraints) {
		}

		public void remove(Element child) {
		}
	}
}
