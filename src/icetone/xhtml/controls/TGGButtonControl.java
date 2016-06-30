/*
 * {{{ header & license
 * Copyright (c) 2007 Vianney le Clément
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package icetone.xhtml.controls;

import org.w3c.dom.NodeList;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.simple.xhtml.FormControl;
import org.xhtmlrenderer.simple.xhtml.controls.ButtonControl;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.texture.Image;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.xhtml.TGGFSImage;
import icetone.xhtml.TGGRenderer;

public class TGGButtonControl extends TGGControl {

    private Image _image = null;

    public TGGButtonControl(FormControl control, TGGRenderer parent,
            LayoutContext c, CalculatedStyle style, UserAgentCallback uac,
            BlockBox box) {
        super(control, parent, c, style, uac, box);
    }

    protected Element createElement(FormControl control,
            TGGRenderer parent, LayoutContext c, CalculatedStyle style,
            UserAgentCallback uac) {

        final ButtonControl bc = (ButtonControl) control;
        final ButtonAdapter button = new ButtonAdapter(parent.getScreen()) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                if (bc.getType().equals("submit")) {
                    if (bc.press()) {
                        bc.getForm().submit();
                    }
                } else if (bc.getType().equals("reset")) {
                    if (bc.press()) {
                        bc.getForm().reset();
                    }
                } else {
                    bc.press();
                }
            }
        };
        button.setMinDimensions(new Vector2f(1,1));
        button.setText(bc.getLabel());
        if (bc.isExtended()) {
            // when defined with <button>, allow the first image to be used
            NodeList images = bc.getElement().getElementsByTagName("img");
            if (images.getLength() > 0) {
                org.w3c.dom.Element img = (org.w3c.dom.Element) images.item(0);
                String uri = c.getNamespaceHandler().getImageSourceURI(img);
                ImageResource res = uac.getImageResource(uri);
                TGGFSImage fsi = (TGGFSImage) res.getImage();
                // copy the image to prevent disposal, and apply a disabled
                // effect if needed
                button.getElementTexture().setImage(_image);
//                _image = new Image(button.getDisplay(), fsi.getImage(), (bc
//                        .isEnabled() ? SWT.IMAGE_COPY : SWT.IMAGE_DISABLE));
            }
        }



        return button;
    }

    public int getIdealHeight() {
        final ButtonAdapter button = (ButtonAdapter) getUIElement();
        Element icon = button.getButtonIcon();
        Vector2f bSz = icon == null ? Vector2f.ZERO : new Vector2f(icon.getWidth(), icon.getHeight());
        float preferredHeight = renderer.getScreen().getStyle("Button").getVector2f("defaultSize").y;
        if (button.getTextElement() != null && button.getTextElement().getLineCount() > 1) {
            preferredHeight += getFont().getBitmapFont().getCharSet().getLineHeight() * button.getTextElement().getLineCount();
        }
        preferredHeight += bSz.y;
        return (int) preferredHeight;
    }

    public int getIdealWidth() {
        final ButtonAdapter button = (ButtonAdapter) getUIElement();
        Vector4f tp = button.getTextPaddingVec();
        Element icon = button.getButtonIcon();
        Vector2f bSz = icon == null ? Vector2f.ZERO : new Vector2f(icon.getWidth(), icon.getHeight());
        String text = button.getText();
        float preferredWidth = (text == null || text.equals("") ? 0 : button.getFont().getLineWidth(text)) + tp.y + tp.z;
        preferredWidth += bSz.x;
        return (int) preferredWidth;
    }

}
