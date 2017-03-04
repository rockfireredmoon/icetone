package icetone.xhtml.controls;

import org.xhtmlrenderer.simple.xhtml.FormControl;

import icetone.core.BaseElement;

public interface TGGFormControl {

    FormControl getFormControl();

    BaseElement getUIElement();

    int getIdealWidth();

    int getIdealHeight();
}
