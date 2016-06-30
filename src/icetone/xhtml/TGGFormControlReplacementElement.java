package icetone.xhtml;

import java.awt.Point;

import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;

import icetone.core.Element;
import icetone.core.layout.LUtil;
import icetone.xhtml.controls.TGGFormControl;

public class TGGFormControlReplacementElement implements ReplacedElement {

    private int width, height;
    private final TGGRenderer renderer;
    private final TGGFormControl control;
    private Point position = new Point(0, 0);

    public TGGFormControlReplacementElement(TGGFormControl control, TGGRenderer renderer) {
        this.control = control;
        this.renderer = renderer;
    }

    public TGGFormControl getControl() {
        return control;
    }

    public void detach(LayoutContext c) {
    }

    public int getIntrinsicHeight() {
        return height;
    }

    public int getIntrinsicWidth() {
        return width;
    }

    public void calculateSize(LayoutContext c, CalculatedStyle style,
            int cssWidth, int cssHeight) {
        width = cssWidth;
        height = cssHeight;
        if (width < 0) {
            width = control.getIdealWidth();
        }
        if (height < 0) {
            height = control.getIdealHeight();
        }
        final Element element = control.getUIElement();
        System.err.println("Setting size of " + element.getClass() + " to " + width + " x " + height);
        element.resize(element.getAbsoluteX() + width, element.getAbsoluteY() + height, Element.Borders.SE);
    }

    public Point getLocation() {
        return position;
    }

    public void setLocation(int x, int y) {
        position.setLocation(x, y);
        final Element uiElement = control.getUIElement();
        
        if (uiElement.getInitialized()) {
            LUtil.setPosition(uiElement, x,
                    renderer.getScrollableAreaHeight() - y - height);
        } else {
        	LUtil.setPosition(uiElement, x, y);
        }
        
//        if (uiElement.getInitialized()) {
//            LUtil.setPosition(uiElement, x + renderer.getContentIndents().x,
//                    renderer.getScrollableAreaHeight() - y - height - renderer.getContentIndents().z);
//        } else {
//        	LUtil.setPosition(uiElement, x + renderer.getContentIndents().x, y + renderer.getContentIndents().z);
//        }
    }

    public boolean isRequiresInteractivePaint() {
        return true;
    }

    public int getBaseline() {
        return 0;
    }

    public boolean hasBaseline() {
        return false;
    }
}
