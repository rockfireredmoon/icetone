package icetone.xhtml.controls;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;
import org.xhtmlrenderer.simple.xhtml.controls.SelectControl;
import org.xhtmlrenderer.util.XRLog;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.lists.ComboBox;
import icetone.controls.menuing.Menu;
import icetone.controls.menuing.MenuItem;
import icetone.controls.scrolling.ScrollArea;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.utils.BitmapTextUtil;
import icetone.xhtml.TGGFSFont;
import icetone.xhtml.TGGRenderer;
import icetone.xhtml.hacks.ElementHack;
import icetone.xhtml.hacks.SelectList;

public class TGGSelectControl extends TGGControl {

    private boolean combo;
    private List values, labels;
    private boolean adjusting;

    public TGGSelectControl(FormControl control, TGGRenderer parent,
            LayoutContext c, CalculatedStyle style, UserAgentCallback uac,
            BlockBox box) {
        super(control, parent, c, style, uac, box);
    }

    public int getIdealHeight() {
        float listItemHeight = BitmapTextUtil.getTextLineHeight(tgElement, "Xg");
        if (combo) {
            return (int) (listItemHeight + tgElement.borders.x + tgElement.borders.w);
        } else {
            final int cSz = ((SelectControl) control).getSize();
            final HackedSelectList listEl = (HackedSelectList) getUIElement();
            int size = cSz > 1 ? cSz : (Math.min(6, listEl.getListItems().size()));
            int h = (int) (listEl.getListPadding().x + listEl.getListPadding().w + (listItemHeight * size));
            return h;
        }
    }

    public int getIdealWidth() {

        // A minimum size. Not really needed, but what the hell
        float listItemHeight = BitmapTextUtil.getTextLineHeight(tgElement, "Xg");
        float w = listItemHeight * 3 + tgElement.borders.y + tgElement.borders.z;

        // hm
        String leftSpacer = "  ";
        String rightSpacer = "  ";

        if (combo) {
            for (MenuItem<String> m : ((ComboBox<String>) tgElement).getListItems()) {
                w = Math.max(w, (listItemHeight * 2) + BitmapTextUtil.getTextWidth(tgElement, leftSpacer + m.getCaption() + rightSpacer));
            }
        } else {
            for (SelectList.ListItem mi : ((SelectList) tgElement).getListItems()) {
                w = Math.max(w, (listItemHeight * 2) + BitmapTextUtil.getTextWidth(tgElement, leftSpacer + mi.getCaption() + rightSpacer));
            }
        }
        return (int) w;
    }

    @Override
    protected void configureCommonAttributes(CalculatedStyle style, FormControl control) {
        super.configureCommonAttributes(style, control);
        final TGGFSFont font = getFont();
        if (combo) {
            HackedComboBox comboEl = (HackedComboBox) tgElement;
            comboEl.setFont(font);
//            comboEl.pack();
            comboEl.setMenuItemHeight(BitmapTextUtil.getTextLineHeight(tgElement, "g"));
        } else {
            HackedSelectList listEl = (HackedSelectList) tgElement;
            listEl.getScrollArea().setFont(font.getFontPath());
            listEl.getScrollArea().setFontSize(font.getSize2D());
            listEl.setListItemHeight(font.getLineHeight("Xg"));
        }
    }

    protected Element createElement(FormControl control,
            TGGRenderer parent, LayoutContext c, CalculatedStyle style,
            UserAgentCallback uac) {
        final SelectControl sc = (SelectControl) control;
        Map options = sc.getOptions();
        values = new ArrayList(options.keySet());
        labels = new ArrayList(options.values());
        if (sc.getSize() > 1 || sc.isMultiple()) {
            return makeList(parent, sc);
        } else {
            return makeCombo(parent, sc);
        }
    }

    private SelectList makeList(TGGRenderer parent, final SelectControl sc) {
        combo = false;
        final HackedSelectList list = new HackedSelectList(parent.getScreen()) {
            @Override
            public void onChange() {
                if (!adjusting) {
                    adjusting = true;
                    try {
                        if (sc.isMultiple()) {
                            List<SelectList.ListItem> s = getSelectedListItems();
                            String[] values = new String[s.size()];
                            for (int i = 0; i < values.length; i++) {
                                values[i] = (String) s.get(i).getValue();
                            }
                            sc.setMultipleValues(values);
                        } else {
                            sc.setValue((String) getListItem(getSelectedIndex()).getValue());
                        }
                    } finally {
                        adjusting = false;
                    }
                }
            }
        };
        try {

            adjusting = true;
            for (String s : (String[]) labels
                    .toArray(new String[labels.size()])) {
                list.addListItem(s, s);
            }

            if (sc.isSuccessful()) {
                if (sc.isMultiple()) {
                    String[] sel = sc.getMultipleValues();
                    Integer[] indices = new Integer[sel.length];
                    for (int i = 0; i < sel.length; i++) {
                        indices[i] = values.indexOf(sel[i]);
                    }
                    list.setSelectedIndexes(indices);
                } else {
                    list.setSelectedIndex(values.indexOf(sc.getValue()));
                }
            }
        } finally {
            adjusting = false;
        }
        sc.addFormControlListener(new FormControlAdapter() {
            @Override
            public void changed(FormControl control) {
                if (!adjusting) {
                    try {
                        adjusting = true;

                        if (sc.isSuccessful()) {
                            if (sc.isMultiple()) {
                                String[] sel = sc.getMultipleValues();
                                Integer[] indices = new Integer[sel.length];
                                for (int i = 0; i < sel.length; i++) {
                                    indices[i] = values.indexOf(sel[i]);
                                }
                                list.setSelectedIndexes(indices);
                            } else {
                                list.setSelectedIndex(values.indexOf(sc.getValue()));
                            }
                        } else {
                            // TODO is there no way to clear selection?
                            list.setSelectedIndex(-1);
                        }
                    } finally {
                        adjusting = false;
                    }
                }
            }

            @Override
            public void successful(FormControl control) {
                changed(control);
            }
        });
        return list;
    }

    private ComboBox makeCombo(TGGRenderer parent, final SelectControl sc) {
        combo = true;
        final HackedComboBox comboEl = new HackedComboBox(parent.getScreen()) {
            @Override
            public void onChange(int selectedIndex, Object value) {
                if (!adjusting) {
                    if (selectedIndex < 0) {
                        sc.setSuccessful(false);
                    } else {
                        sc.setValue((String) value);
                    }
                }
            }
        };
        try {

            adjusting = true;
            for (String s : (String[]) labels
                    .toArray(new String[labels.size()])) {
                comboEl.addListItem(s, s, false);
            }
            if (sc.isSuccessful()) {
                comboEl.setSelectedByValue(sc.getValue(), false);
            }
        } finally {
            adjusting = false;
        }
        sc.addFormControlListener(new FormControlAdapter() {
            @Override
            public void changed(FormControl control) {
                try {
                    adjusting = true;
                    if (sc.isSuccessful()) {
                        comboEl.setSelectedByValue(sc.getValue(), false);
                    } else {
                        comboEl.setSelectedByValue(null, false);
                    }
                } finally {
                    adjusting = false;
                }
            }

            @Override
            public void successful(FormControl control) {
                changed(control);
            }
        });
        return comboEl;
    }

    /**
     * A hack so we can get at some otherwise inaccessable attributes.
     */
    abstract class HackedComboBox extends ComboBox {

        public HackedComboBox(ElementManager screen) {
            super(screen);
            Element button = getChildElementById(getUID() + ":ArrowDown");

            // Stops 'jumping' when resizing
            button.setDocking(null);
        }

        @Override
        public void controlResizeHook() {
            super.controlResizeHook();

            // The button gets positioned incorrectly, *shrugs*
            Element button = getChildElementById(getUID() + ":ArrowDown");
            button.setX(getWidth());
            resizeMenu();
        }

        public void setFont(final TGGFSFont font) {
            Menu menu = getMenu();
            if (menu != null) {
                // TODO - need an easy way to get at the font of the menus 
                try {
                    Field f = ScrollArea.class.getDeclaredField("scrollableArea");
                    f.setAccessible(true);
                    Element el = (Element) f.get(menu);
                    ElementHack.setFont(font.getFontPath(), el);
                    el.setFontSize(font.getSize2D());
                } catch (Exception e) {
                    XRLog.render(Level.WARNING, "Failed to set font size of SelectList.");
                }
            }
        }

        public float getMenuItemHeight() {
            Menu menu = getMenu();
            if (menu != null) {
                // TODO - need an easy way to get at the menu item height
                try {
                    Field f = Menu.class.getDeclaredField("menuItemHeight");
                    f.setAccessible(true);
                    return (Float) f.get(menu);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to get menu item height.", e);
                }
            }
            return getHeight();
        }

        public void setMenuItemHeight(float height) {
            Menu menu = getMenu();
            if (menu != null) {
                // TODO - need an easy way to get at the menu item height
                try {
                    Field f = Menu.class.getDeclaredField("menuItemHeight");
                    f.setAccessible(true);
                    f.set(menu, height);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to set menu item height.", e);
                }

                // TODO and the highlight ..
                try {
                    Field f = Menu.class.getDeclaredField("highlight");
                    f.setAccessible(true);
                    Element highlight = (Element) f.get(menu);
                    highlight.setHeight(height);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to set menu item height.", e);
                }

                resizeMenu();
            }
        }

        private void resizeMenu() {
            // Make the menu as big as the field
            Menu menu = getMenu();
            if (menu != null) {
                final int cSz = ((SelectControl) control).getSize();
                int size = cSz > 1 ? cSz : getListItems().size();
                final float h = size * getMenuItemHeight();
                menu.resize(menu.getAbsoluteX() + getWidth(), getAbsoluteY() + h, Borders.SE);
            }
        }
    }

    /**
     * A hack so we can get at some otherwise inaccessable attributes.
     */
    abstract class HackedSelectList extends SelectList {

        public HackedSelectList(ElementManager screen) {
            super(screen);

            // Resizing utterly fails, so lets handle it ourself
            scrollableArea.setScaleEW(false);
            scrollableArea.setScaleNS(false);
            scrollableArea.setDocking(null);


            setMinDimensions(new Vector2f(1, 1));
        }

        public void setListItemHeight(float listItemHeight) {
            try {
                Field f = SelectList.class.getDeclaredField("listItemHeight");
                f.setAccessible(true);
                f.set(this, listItemHeight);
                redisplayHighlights();
            } catch (Exception e) {
                throw new RuntimeException("Failed to set list item height.", e);
            }
        }

        @Override
        public void controlResizeHook() {
            super.controlResizeHook();
            scrollableArea.setHeight(getListItemHeight() * getListItems().size());
            scrollToTop();
            redisplayHighlights();
        }

        public Element getScrollArea() {
            return scrollableArea;
        }

        public Vector4f getListPadding() {
            // TODO - need an easy way to get at the list padding
            try {
                Field f = SelectList.class.getDeclaredField("listPadding");
                f.setAccessible(true);
                return (Vector4f) f.get(this);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get menu item height.", e);
            }
        }

        private void redisplayHighlights() {
            try {
                Method m = SelectList.class.getDeclaredMethod("displayHighlights");
                m.setAccessible(true);
                m.invoke(this);
            } catch (Exception e) {
                throw new RuntimeException("Failed to redisplay highlights.", e);
            }
        }
    }
}
