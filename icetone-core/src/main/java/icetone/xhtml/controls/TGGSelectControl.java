package icetone.xhtml.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.FormControlAdapter;
import org.xhtmlrenderer.simple.xhtml.controls.SelectControl;

import icetone.controls.lists.ComboBox;
import icetone.controls.lists.SelectList;
import icetone.controls.lists.SelectListItem;
import icetone.core.BaseElement;
import icetone.xhtml.XHTMLRenderer;

public class TGGSelectControl extends TGGControl {

	private List<String> values, labels;

	public TGGSelectControl(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac, BlockBox box) {
		super(control, parent, c, style, uac, box);
	}

	@Override
	protected BaseElement createElement(FormControl control, XHTMLRenderer parent, LayoutContext c, CalculatedStyle style,
			UserAgentCallback uac) {
		final SelectControl sc = (SelectControl) control;
		@SuppressWarnings("unchecked")
		Map<String, String> options = sc.getOptions();
		values = new ArrayList<>(options.keySet());
		labels = new ArrayList<>(options.values());
		if (sc.getSize() > 1 || sc.isMultiple()) {
			return makeList(parent, sc);
		} else {
			return makeCombo(parent, sc);
		}
	}

	private SelectList<String> makeList(XHTMLRenderer parent, final SelectControl sc) {
		final SelectList<String> list = new SelectList<String>(parent.getScreen());
		list.onChanged(evt -> {
			if (!list.isAdjusting()) {
				list.runAdjusting(() -> {
					if (sc.isMultiple()) {
						Set<SelectListItem<String>> s = evt.getNewValue();
						String[] values = new String[s.size()];
						int i = 0;
						for (SelectListItem<String> item : s) {
							values[i] = item.getValue();
						}
						sc.setMultipleValues(values);
					} else {
						sc.setValue(evt.getNewValue().iterator().next().getValue());
					}
				});
			}
		});
		list.runAdjusting(() -> {
			for (String s : labels) {
				list.addListItem(s, s);
			}

			if (sc.isSuccessful()) {
				if (sc.isMultiple()) {
					String[] sel = sc.getMultipleValues();
					list.clearSelection();
					for (int i = 0; i < sel.length; i++) {
						list.addSelectedIndex(values.indexOf(sel[i]));
					}
				} else {
					list.setSelectedIndex(values.indexOf(sc.getValue()));
				}
			}
		});
		sc.addFormControlListener(new FormControlAdapter() {
			@Override
			public void changed(FormControl control) {
				if (!list.isAdjusting()) {
					list.runAdjusting(() -> {
						if (sc.isSuccessful()) {
							if (sc.isMultiple()) {
								String[] sel = sc.getMultipleValues();
								list.clearSelection();
								for (int i = 0; i < sel.length; i++) {
									list.addSelectedIndex(values.indexOf(sel[i]));
								}
							} else {
								list.setSelectedIndex(values.indexOf(sc.getValue()));
							}
						} else {
							// TODO is there no way to clear selection?
							list.setSelectedIndex(-1);
						}
					});
				}
			}

			@Override
			public void successful(FormControl control) {
				changed(control);
			}
		});
		return list;
	}

	private ComboBox<String> makeCombo(XHTMLRenderer parent, final SelectControl sc) {
		final ComboBox<String> comboEl = new ComboBox<String>(parent.getScreen());
		comboEl.onChange(evt -> {
			if (!comboEl.isAdjusting()) {
				if (evt.getNewValue() == null) {
					sc.setSuccessful(false);
				} else {
					sc.setValue(evt.getNewValue());
				}
			}
		});
		comboEl.runAdjusting(() -> {
			comboEl.invalidate();
			for (String s : labels.toArray(new String[labels.size()])) {
				comboEl.addListItem(s, s);
			}
			if (sc.isSuccessful()) {
				comboEl.setSelectedByValue(sc.getValue());
			}
			comboEl.validate();
		});
		sc.addFormControlListener(new FormControlAdapter() {
			@Override
			public void changed(FormControl control) {
				comboEl.runAdjusting(() -> {
					if (sc.isSuccessful()) {
						comboEl.setSelectedByValue(sc.getValue());
					} else {
						comboEl.setSelectedByValue(null);
					}
				});
			}

			@Override
			public void successful(FormControl control) {
				changed(control);
			}
		});
		return comboEl;
	}

}
