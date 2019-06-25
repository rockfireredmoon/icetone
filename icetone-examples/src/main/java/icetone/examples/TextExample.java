package icetone.examples;

import java.util.ServiceLoader;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Slider;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.ElementContainer;
import icetone.core.Layout.LayoutType;
import icetone.core.Measurement.Unit;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.css.CssUtil;
import icetone.css.StyleManager.ThemeInstance;
import icetone.extras.chooser.ColorField;
import icetone.extras.controls.Vector2fControl;
import icetone.extras.controls.Vector4fControl;
import icetone.jmettf.JMETTFTextEngine;
import icetone.text.FontInfo;
import icetone.text.FontSpec;
import icetone.text.TextEngine;
import icetone.text.bitmap.RichBitmapText;

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
	private int noRows = 1;
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
	private Spinner<Float> rotation;
	private Spinner<Float> textRotation;
	private CheckBox clipped;
	private Vector2fControl origin;
	private ColorField color;
	private CheckBox styledElement;
	private FontSpec demoFont = new FontSpec();
	private ComboBox<TextEngine> fontEngine;
	private Slider<Float> alpha;
	private ColorField outline;
	private Label lineHeightInfo;
	private Label textLineHeightInfo;
	private Label textLineWidthInfo;
	private Label ascentInfo;
	private Label descentInfo;
	private Label preferredSizeInfo;

	@Override
	public void simpleInitApp() {
		/*
		 * We are only using a single screen, so just initialise it (and you don't need
		 * to provide the screen instance to each control).
		 * 
		 * It is passed to the buildExample method in this way to help ExampleRunner so
		 * this example can be run from there and as a standalone JME application
		 */
		buildExample(BaseScreen.init(this));

	}

	protected void buildExample(ElementContainer<?, ?> container) {

		demoFont = demoFont.deriveProperty(JMETTFTextEngine.OUTLINE, CssUtil.toString(ColorRGBA.Blue));

		scroller = new ScrollPanel();
		scroller.setHorizontalScrollBarMode(ScrollBarMode.Never);

		// ((WrappingLayout)
		((WrappingLayout) scroller.getScrollContentLayout()).setOrientation(Orientation.HORIZONTAL);
		((WrappingLayout) scroller.getScrollContentLayout()).setFill(true);

		StyledContainer opts = new StyledContainer();
		opts.setLayoutManager(new MigLayout("wrap 4, fill", "[][grow][][grow]", "[][][][][][]"));

		// Engine
		opts.addElement(new Label("Text Engine"));
		fontEngine = new ComboBox<>();
		fontEngine.setEditable(false);
		fontEngine.addComboItem("Default", null);
		for (TextEngine f : ServiceLoader.load(TextEngine.class)) {
			fontEngine.addComboItem(f.getClass().getSimpleName(), f);
		}
		fontEngine.onChange(evt -> {
			setFont(demoFont.deriveFromTextEngine(
					evt.getNewValue() == null ? null : evt.getNewValue().getClass().getSimpleName()));
			evt.getSource().setToolTipText(
					evt.getNewValue() == null ? "Default" : evt.getNewValue().getClass().getSimpleName());
		});
		opts.addElement(fontEngine);

		// Font
		opts.addElement(new Label("Font"));
		fontFamily = new ComboBox<String>();
		fontFamily.setEditable(false);
		for (String fntName : container.getScreen().getThemeInstance().getFontFamilies())
			fontFamily.addComboItem(fntName, fntName);
		if (fnt != null) {
			fontFamily.setSelectedByValue(fnt);
		}
		fontFamily.onChange(evt -> {
			setFont(demoFont.deriveFromFamily(null, evt.getNewValue()));
			evt.getSource().setToolTipText(evt.getNewValue());
		});
		opts.addElement(fontFamily);

		opts.addElement(new Label("Letter Spacing"), "growy, al 0");
		opts.addElement((new Spinner<Integer>(Orientation.HORIZONTAL, false))
				.setSpinnerModel(new IntegerRangeSpinnerModel(-256, 256, 1, 0))
				.onChange(evt -> setFont(demoFont.deriveFromCharacterSpacing(evt.getNewValue())))
				.setToolTipText("Additional gap between each character"));

		// Font size
		opts.addElement(new Label("Font Size"));
		fontSize = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		fontSize.onChange(evt -> setFont(demoFont.deriveFromSize(evt.getNewValue())));
		fontSize.setSpinnerModel(new IntegerRangeSpinnerModel(-1, 256, 1, -1));
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

		// Length of test text
		opts.addElement(new Label("Line Height"), "growy, al 0");
		opts.addElement((lineHeight = new Spinner<Integer>(Orientation.HORIZONTAL, false))
				.setSpinnerModel(new IntegerRangeSpinnerModel(-256, 256, 1, 0)).onChange(evt -> rebuildText())
				.setToolTipText("Fixed line height. Use zero for automatic"));

		opts.addElement(new Label("Rotation"), "growy, al 0");
		opts.addElement((rotation = new Spinner<Float>(Orientation.HORIZONTAL, false))
				.setSpinnerModel(new FloatRangeSpinnerModel(0, 359, 1, 0)).onChange(evt -> rebuildText())
				.setToolTipText("Rotation of element"));

		// Padding
		opts.addElement(new Label("Padding"), "spany 5");
		padding = new Vector4fControl(-100, 100, 1, new Vector4f(), false);
		padding.onChange(evt -> setPaddingOnScroller(evt.getNewValue()));
		opts.addElement(padding, "wrap, spany 5");

		opts.addElement(new Label("Text Rotation"), "growy, al 0");
		opts.addElement((textRotation = new Spinner<Float>(Orientation.HORIZONTAL, false))
				.setSpinnerModel(new FloatRangeSpinnerModel(0, 359, 1, 0)).onChange(evt -> rebuildText())
				.setToolTipText("Rotation of text"));

		opts.addElement(new Label("Origin"), "growy, al 0, spany 3");
		opts.addElement(
				(origin = new Vector2fControl(-100, 100, 1, Vector2f.ZERO, true)).onChange(evt -> rebuildText()),
				"spany 3");

		opts.addElement(new Label("Color"));
		color = new ColorField(ColorRGBA.White);
		color.setIncludeAlpha(true);
		color.onChange(evt -> rebuildText());
		opts.addElement(color, "wrap");

		opts.addElement(new Label("Alpha"));
		alpha = new Slider<Float>().setSliderModel(new FloatRangeSliderModel(0, 1, 1, 0.01f));
		alpha.onChanged(evt -> setAlphaOnScroller(evt.getNewValue()));
		opts.addElement(alpha, "wrap");

		opts.addElement((styledElement = new CheckBox("Styled Element")).onChange(evt -> rebuildText()),
				"span 2, gaptop 10, gapleft 30");
		opts.addElement((bold = new CheckBox("Bold")).onChange(
				evt -> setFont(demoFont.deriveFromStyle(bold.isChecked(), italic.isChecked(), underline.isChecked()))),
				"span 2, gaptop 10");
		opts.addElement(
				(clipped = new CheckBox("Clipped")).setChecked(true)
						.onChange(evt -> setFont(
								demoFont.deriveFromStyle(bold.isChecked(), italic.isChecked(), underline.isChecked()))),
				"span 2, gapleft 30");
		opts.addElement((italic = new CheckBox("Italic")).onChange(
				evt -> setFont(demoFont.deriveFromStyle(bold.isChecked(), italic.isChecked(), underline.isChecked()))),
				"span 2");
		opts.addElement((debugRows = new CheckBox("Debug background")).onChange(evt -> rebuildText()),
				"span 2, gapleft 30");
		opts.addElement((underline = new CheckBox("Underline")).onChange(
				evt -> setFont(demoFont.deriveFromStyle(bold.isChecked(), italic.isChecked(), underline.isChecked()))),
				"span 2");

		opts.addElement(new Label("RichBitmapText").addStyleClass("default-highlight"),
				"span 2, gaptop 10, gapbottom 10");
		opts.addElement(new Label("JMETTF").addStyleClass("default-highlight"), "span 2, gaptop 10, gapbottom 10");

		opts.addElement(new Label("Underline offset"));
		underlineOffset = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		underlineOffset.onChange(evt -> rebuildText());
		underlineOffset.setSpinnerModel(new IntegerRangeSpinnerModel(-999, 999, 1, -1));
		opts.addElement(underlineOffset);
		opts.addElement(
				(new CheckBox("Anti-alias", true)).onChange(
						evt -> setFont(demoFont.deriveProperty(JMETTFTextEngine.ANTI_ALIAS, evt.getNewValue()))),
				"span 2");

		opts.addElement(new Label("Underline width"));
		underlineSize = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		underlineSize.onChange(evt -> rebuildText());
		underlineSize.setSpinnerModel(new IntegerRangeSpinnerModel(1, 999, 1, 1));
		opts.addElement(underlineSize);

		opts.addElement(new Label("Outline"));
		outline = new ColorField(ColorRGBA.Blue);
		outline.setIncludeAlpha(true);
		outline.onChange(evt -> setFont(demoFont.deriveProperty(JMETTFTextEngine.OUTLINE, evt.getNewValue())));
		opts.addElement(outline, "wrap");

		opts.addElement(new Label("Italic skew"));
		italicSkew = new Spinner<Integer>(Orientation.HORIZONTAL, false);
		italicSkew.onChange(evt -> rebuildText());
		italicSkew.setSpinnerModel(new IntegerRangeSpinnerModel(-999, 999, 1, 3));
		opts.addElement(italicSkew);

		opts.addElement(new Label("Outline Size"));
		opts.addElement((new Spinner<Integer>(Orientation.HORIZONTAL, false))
				.setSpinnerModel(new IntegerRangeSpinnerModel(0, 20, 1, 0))
				.onChange(evt -> setFont(demoFont.deriveProperty(JMETTFTextEngine.OUTLINE_WIDTH, evt.getNewValue())))
				.setToolTipText("Size of font outline"));

		// Panel
		Panel xcw = new Panel();
		xcw.setLayoutManager(new MigLayout("wrap 1", "[fill, grow]", "[shrink 0][:300:,fill, grow]"));
		xcw.addElement(opts);
		xcw.addElement(scroller);
		xcw.sizeToContent();

		container.addElement(xcw);

		// Font information
		Panel fip = new Panel(new MigLayout("wrap 2"));
		fip.addElement(new Label("Line Height"));
		fip.addElement((lineHeightInfo = new Label()).setPreferredDimensions(new Size(100, 0, Unit.PX, Unit.AUTO)));
		fip.addElement(new Label("Line Height (text)"));
		fip.addElement((textLineHeightInfo = new Label()).setPreferredDimensions(new Size(100, 0, Unit.PX, Unit.AUTO)));
		fip.addElement(new Label("Line Width (text)"));
		fip.addElement((textLineWidthInfo = new Label()).setPreferredDimensions(new Size(100, 0, Unit.PX, Unit.AUTO)));
		fip.addElement(new Label("Preferred Size"));
		fip.addElement((preferredSizeInfo = new Label()).setPreferredDimensions(new Size(100, 0, Unit.PX, Unit.AUTO)));
		fip.addElement(new Label("Ascent"));
		fip.addElement((ascentInfo = new Label()).setPreferredDimensions(new Size(100, 0, Unit.PX, Unit.AUTO)));
		fip.addElement(new Label("Descent"));
		fip.addElement((descentInfo = new Label()).setPreferredDimensions(new Size(100, 0, Unit.PX, Unit.AUTO)));
		container.addElement(fip);

		rebuildText();
	}

	private void rebuildText() {
		scroller.getScrollableArea().removeAllChildren();
		for (int i = 0; i < noRows; i++) {
			createAndAddText(scroller);
		}
		updateInfo();
	}

	private void setFont(FontSpec demoFont) {
		this.demoFont = demoFont;
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setFont(demoFont);
		}
		layoutScroll();
		updateInfo();
	}

	protected void layoutScroll() {
		// TODO still need?
		scroller.getScrollableArea().dirtyLayout(false, LayoutType.text());
		scroller.layoutChildren();
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

	private void setAlphaOnScroller(float alpha) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setElementAlpha(alpha);
		}
	}

	private void setWrapOnScroller(LineWrapMode value) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setTextWrap(value);
		}
		layoutScroll();
		updateInfo();
	}

	private void setPaddingOnScroller(Vector4f value) {
		for (BaseElement el : scroller.getScrollableArea().getElements()) {
			el.setTextPadding(value);
		}
		layoutScroll();
		updateInfo();
	}

	private void createAndAddText(ScrollPanel ls) {
		BaseElement text = styledElement.isChecked() ? new Element() : new BaseElement();
		text.setInheritsStyles(false);

		String testString = getTestString();
		if (debugRows.isChecked()) {
			text.setTexture("/bgy.jpg");
			text.setBackgroundDimensions(Size.FILL);
		}
		text.setText(testString);
		if (minY > 0)
			text.setMinDimensions(new Size(0, minY, Unit.AUTO, Unit.PX));
		text.setFixedLineHeight(((Integer) lineHeight.getSpinnerModel().getCurrentValue()).floatValue());
		text.setTextWrap(wrap.getSelectedListItem().getValue());
		text.setFontColor(color.getValue());
		text.setClippingEnabled(clipped.isChecked());
		text.setTextVAlign(valign.getSelectedListItem().getValue());
		text.setTextAlign(align.getSelectedListItem().getValue());
		text.setTextRotation(textRotation.getSelectedValue());
		text.getTextElement().setOriginOffset(origin.getValue().x, origin.getValue().y);
		text.setRotation(rotation.getSelectedValue());
		text.setTextPadding(padding.getValue());
		text.setElementAlpha(alpha.getSelectedValue());
		text.setFont(demoFont);
		ls.addScrollableContent(text);

		if (text.getTextElement() instanceof RichBitmapText) {
			((RichBitmapText) text.getTextElement())
					.setSkewSize(((Integer) italicSkew.getSpinnerModel().getCurrentValue()).floatValue());
			((RichBitmapText) text.getTextElement())
					.setUnderlineOffset(((Integer) underlineOffset.getSpinnerModel().getCurrentValue()).floatValue());
			((RichBitmapText) text.getTextElement())
					.setUnderlineSize(((Integer) underlineSize.getSpinnerModel().getCurrentValue()).floatValue());
			text.getTextElement().updateTextState(false);
		}

	}

	protected String getTestString() {
		String testString = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod "
				+ "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, "
				+ "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
				+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu "
				+ "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in "
				+ "culpa qui officia deserunt mollit anim id est laborum";
		testString = testString.substring(0, (Integer) textLength.getSpinnerModel().getCurrentValue());
		return testString;
	}

	private void updateInfo() {
		ThemeInstance theme = scroller.getThemeInstance();
		FontInfo finfo = theme.getFontInfo(demoFont);
		System.out.println("INFO for " + demoFont + " IS " + finfo);
		if (lineHeight.getSelectedValue() == 0) {
			lineHeightInfo.setText(String.valueOf(finfo.getTotalLineHeight()));
		} else {
			lineHeightInfo.setText(String.valueOf(lineHeight.getSelectedValue()));
		}
		textLineHeightInfo.setText(String.valueOf(finfo.getTextLineHeight(getTestString())));
		textLineWidthInfo.setText(String.valueOf(finfo.getLineWidth(getTestString())));
		ascentInfo.setText(String.valueOf(finfo.getAscent()));
		descentInfo.setText(String.valueOf(finfo.getDescent()));
		preferredSizeInfo.setText(String.valueOf(finfo.getPreferredSize()));
	}

}
