package icetone.xhtml.controls;

import org.xhtmlrenderer.simple.xhtml.FormControl;

import icetone.core.Element;

public interface TGGFormControl {

    FormControl getFormControl();

    Element getUIElement();

    int getIdealWidth();

    int getIdealHeight();
}
