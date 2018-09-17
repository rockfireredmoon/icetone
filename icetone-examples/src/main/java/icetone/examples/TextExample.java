package icetone.examples;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.ElementContainer;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.controls.Vector4fControl;
import icetone.framework.core.AnimText.TextStyle;

/**
 * This example shows some examples of usage of various different types of
 * {@link PushButton}
 */
public class TextExample extends SimpleApplication {

	public static void main(String[] args) {
		TextExample app = new TextExample();
		app.start();
	}

	private String fnt;
	private Spinner<Integer> fontSize;
	private ComboBox<String> fontFamily;
	private ComboBox<VAlign> valign;
	private ComboBox<Align> align;
	private ScrollPanel scroller;
	private ComboBox<LineWrapMode> wrap;
	private Spinner<Integer> rows;
	private int noRows = 5;
	private CheckBox debugRows;
	private Spinner<Integer> textLength;
	private Spinner<Integer> lineHeight;
	private Vector4fControl padding;
	private int minY;
	private CheckBox bold;
	private CheckBox italic;
	private CheckBox underline;
	private Spinner<Integer> underlineOffset;
	private Spinner<Integer> italicSkew;
	private Spinner<Integer> underlineSize;
	private CheckBox clipped;

	@Override
	public void simpleInitApp() {
		/*
		 * We are only using a single screen, so just initialise it (and you
		 * don't need to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help
		 * ExampleRunner so this example can be run from there and as a
		 * standalone JME application
		 */
		buildExample(BaseScreen.init(this));

	}

	protected void buildExample(ElementContainer<?, ?> container) {

		scroller = new ScrollPanel();
		scroller.setHorizontalScrollBarMode(ScrollBarMode.Never);

		// ((WrappingLayout)
		((WrappingLayout) scroller.getScrollContentLayout()).setOrientation(Orientation.HORIZONTAL);
		((WrappingLayout) scroller.getScrollContentLayout()).setFill(true);

		StyledContainer opts = new StyledContainer();
		opts.setLayoutManager(new MigLayout("wrap 4, fill", "[][grow][][grow]", "[][][][][][]"));

		// Font
		opts.addElement(new Label("Font"));
		fontFamily = new ComboBox<String>();
		fontFamily.setEditable(false);
		for (String fntName : container.getScreen().getThemeInstance().getFontFamilies())
			fontFamily.addListItem(fntName, fntName);
		if (fnt != null) {
			fontFamily.setSelectedByValue(fnt);
		}
		fontFamily.onChange(evt -> {
			setFontOnScroller(evt.getNewValue());
			evt.getSource().setToolTipText(evt.getNewValue());
		});
		opts.addElement(fontFamily);

		// Font size
		opts.addElement(new Label("Font Size"));
		fontSize = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		fontSize.onChange(evt -> setFontSizeOnScroller(evt.getNewValue()));
		fontSize.setSpinnerModel(new IntegerRangeSpinnerModel(1, 64, 1, (int) fontSize.getFontSize()));
		opts.addElement(fontSize, "");

		// Wrap
		opts.addElement(new Label("Wrap"));
		wrap = new ComboBox<LineWrapMode>(LineWrapMode.values());
		wrap.onChange(evt -> setWrapOnScroller(evt.getNewValue()));
		opts.addElement(wrap, "");

		// VAlign
		opts.addElement(new Label("VAlign"));
		valign = new ComboBox<VAlign>(VAlign.values());
		valign.setSelectedByValue(VAlign.Center);
		valign.onChange(evt -> setVAlignOnScroller(evt.getNewValue()));
		opts.addElement(valign, "");

		// Align
		opts.addElement(new Label("Align"));
		align = new ComboBox<Align>(Align.values());
		align.setSelectedByValue(Align.Left);
		align.onChange(evt -> setAlignOnScroller(evt.getNewValue()));
		opts.addElement(align, "");

		// Number of rows
		opts.addElement(new Label("Rows"));
		rows = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		rows.onChange(evt -> {
			noRows = evt.getNewValue();
			rebuildText();
		});
		rows.setSpinnerModel(new IntegerRangeSpinnerModel(1, 64, 1, noRows));
		opts.addElement(rows, "");

		// Number of rows
		opts.addElement(new Label("Min Height"));
		Spinner<Integer> minHeight = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		minHeight.onChange(evt -> {
			minY = evt.getNewValue();
			rebuildText();
		});
		minHeight.setSpinnerModel(new IntegerRangeSpinnerModel(0, 256, 1, 0));
		opts.addElement(minHeight, "");

		// Length of test text
		opts.addElement(new Label("Text Length"));
		textLength = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		textLength.onChange(evt -> rebuildText());
		textLength.setSpinnerModel(new IntegerRangeSpinnerModel(0, 256, 10, 32));
		opts.addElement(textLength);

		opts.addElement(new Label("Underline offset"));
		underlineOffset = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		underlineOffset.onChange(evt -> rebuildText());
		underlineOffset.setSpinnerModel(new IntegerRangeSpinnerModel(-999, 999, 1, -1));
		opts.addElement(underlineOffset);

		opts.addElement(new Label("Underline width"));
		underlineSize = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		underlineSize.onChange(evt -> rebuildText());
		underlineSize.setSpinnerModel(new IntegerRangeSpinnerModel(1, 999, 1, 1));
		opts.addElement(underlineSize);

		opts.addElement(new Label("Italic skew"));
		italicSkew = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		italicSkew.onChange(evt -> rebuildText());
		italicSkew.setSpinnerModel(new IntegerRangeSpinnerModel(-999, 999, 1, 3));
		opts.addElement(italicSkew);

		// Length of test text
		opts.addElement(new Label("Line Height"), "growy, al 0");
		opts.addElement((lineHeight = new Spinner<Integer>(Orientation.HORIZONTAL, false))
				.setSpinnerModel(new IntegerRangeSpinnerModel(0, 256, 1, 0)).onChange(evt -> rebuildText())
				.setToolTipText("Fixed line height. Use zero for automatic"));

		// Padding
		opts.addElement(new Label("Padding"));
		padding = new Vector4fControl(-100, 100, 1, new Vector4f(), false);
		padding.onChange(evt -> setPaddingOnScroller(evt.getNewValue()));
		opts.addElement(padding, "wrap");

		opts.addElement((bold = new CheckBox("Bold")).onChange(evt -> rebuildText()), "span 4");
		opts.addElement((italic = new CheckBox("Italic")).onChange(evt -> rebuildText()), "span 4");
		opts.addElement((underline = new CheckBox("Underline")).onChange(evt -> rebuildText()), "span 4");
		opts.addElement((clipped = new CheckBox("Clipped")).setChecked(true).onChange(evt -> rebuildText()),
				"span 4");

		// Debug bg
		opts.addElement((debugRows = new CheckBox("Debug background")).onChange(evt -> rebuildText()), "span 4");

		// Panel
		Panel xcw = new Panel();
		xcw.setLayoutManager(new MigLayout("wrap 1", "[fill, grow]", "[shrink 0][:300:,fill, grow]"));
		xcw.addElement(opts);
		xcw.addElement(scroller);
		xcw.sizeToContent();

		container.addElement(xcw);

		rebuildText();
	}

	private void setFontOnScroller(String path) {
		rebuildText();
		//
		//
		// for (Element el : ls.getScrollableArea().getElements()) {
		// ((LTextElement)el).setFont(path);
		// }
		// ls.getLayoutManager().layout(ls);
	}

	private void rebuildText() {
		scroller.getScrollableArea().removeAllChildren();
		for (int i = 0; i < noRows; i++) {
			createAndAddText(scroller);
		}
	}

	private void setFontSizeOnScroller(int value) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setFontSize(value);
		}
		layoutScroll();
	}

	protected void layoutScroll() {
		scroller.getScrollableArea().dirtyLayout(false, LayoutType.text());
		scroller.getScrollableArea().layoutChildren();
	}

	private void setVAlignOnScroller(VAlign value) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setTextVAlign(value);
		}
		layoutScroll();
	}

	private void setAlignOnScroller(Align value) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setTextAlign(value);
		}
		layoutScroll();
	}

	private void setWrapOnScroller(LineWrapMode value) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setTextWrap(value);
		}
		layoutScroll();
	}

	private void setPaddingOnScroller(Vector4f value) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setTextPadding(value);
		}
		layoutScroll();
	}

	private void createAndAddText(ScrollPanel ls) {
		BaseElement text = new BaseElement();
		String selectedValue = fontFamily.getSelectedValue();
		if (selectedValue != null && selectedValue.length() > 0)
			text.setFontFamily(selectedValue);
		text.setFontSize(((Integer) fontSize.getSpinnerModel().getCurrentValue()).floatValue());

		// try {
		// text.setText(IOUtils.toString(getClass().getResource("/META-INF/help.txt")).replace("\n",
		// "<br/>"));
		// } catch (Exception ex) {
		// text.setText("Failed to load help. " + ex.getMessage());
		// }
		String testString = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod "
				+ "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, "
				+ "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
				+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu "
				+ "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in "
				+ "culpa qui officia deserunt mollit anim id est laborum";
		testString = testString.substring(0, (Integer) textLength.getSpinnerModel().getCurrentValue());
		if (debugRows.isChecked()) {
			text.setTexture("/bgy.jpg");
		}
		text.setText(testString);
		if (minY > 0)
			text.setMinDimensions(new Size(0, minY));
		if (bold.getIsToggled())
			text.addTextStyles(TextStyle.bold);
		if (italic.getIsToggled())
			text.addTextStyles(TextStyle.italic);
		if (underline.getIsToggled())
			text.addTextStyles(TextStyle.underline);
		text.setFixedLineHeight(((Integer) lineHeight.getSpinnerModel().getCurrentValue()).floatValue());
		text.setTextWrap(wrap.getSelectedListItem().getValue());
		text.setClippingEnabled(clipped.isChecked());
		text.setTextVAlign(valign.getSelectedListItem().getValue());
		text.setTextAlign(align.getSelectedListItem().getValue());
		text.setTextPadding(padding.getValue());
		text.getTextElement().setSkewSize(((Integer) italicSkew.getSpinnerModel().getCurrentValue()).floatValue());
		text.getTextElement()
				.setUnderlineOffset(((Integer) underlineOffset.getSpinnerModel().getCurrentValue()).floatValue());
		text.getTextElement()
				.setUnderlineSize(((Integer) underlineSize.getSpinnerModel().getCurrentValue()).floatValue());
		ls.addScrollableContent(text);
	}

}
