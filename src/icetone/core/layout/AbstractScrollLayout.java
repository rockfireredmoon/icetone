package icetone.core.layout;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.scrolling.ScrollBar;
import icetone.controls.scrolling.ScrollPanel;
import icetone.core.Element;

public abstract class AbstractScrollLayout extends AbstractLayout {

    public void layoutScroll(ScrollPanel childElement, LayoutManager scrollContentLayout) {
        Vector4f borders = childElement.borders;
        Element innerBounds = childElement.getScrollBounds();
        Element scrollableArea = childElement.getScrollableArea();

        // Record the current scroll size
        Vector2f ss = scrollableArea.getDimensions().clone();

        // Set the inner bounds (the view port)
        System.out.println("[REMOVEME] viewport: " + childElement.getDimensions());
        LUtil.setBounds(innerBounds, 0,0, childElement.getWidth() , childElement.getHeight());

        // First set the content to it's preferred size
        scrollableArea.setDimensions(innerBounds.getDimensions());
        Vector2f contentPref = scrollContentLayout.preferredSize(scrollableArea);
        if (childElement.getUseVerticalWrap()) {
            contentPref.x = innerBounds.getWidth();
        }

        // TODO needs to be more accurate?
        if (contentPref.y > innerBounds.getHeight()) {
            contentPref.x -= childElement.getScrollSize() + childElement.getGap();
        }

        System.out.println("[REMOVEME] scroll area pref: " + contentPref);
        LUtil.setDimensions(scrollableArea, contentPref.x, contentPref.y);

        // How much has it changed by?        
        Vector2f newSs = scrollableArea.getDimensions();
        Vector2f diff = newSs.subtract(ss);

        if (diff.y < 0) {
            childElement.scrollYBy(-diff.y);
        }

        // Layout the content
        scrollContentLayout.layout(scrollableArea);

        // Clipping
        childElement.updateClippingLayers();
    }

    public void fixScrollBars(ScrollPanel childElement) {
        ScrollBar v = childElement.getVerticalScrollBar();
        ScrollBar h = childElement.getHorizontalScrollBar();
        Element innerBounds = childElement.getScrollBounds();
        final float scrollSize = childElement.getScrollSize();
        final float gap = childElement.getGap();
        final boolean useVerticalWrap = childElement.getUseVerticalWrap();

        // Make various adjustments to the scrollbars. Some of these I think are
        // bugs in ScrollPanel, some are more personal adjustments
        if ((useVerticalWrap || v.getIsVisible()) && !h.getIsVisible()) {
            v.setHeight(innerBounds.getHeight());
            v.setY(0);
            v.setX(childElement.getWidth() - scrollSize);
            v.getButtonScrollDown().setDimensions(scrollSize, scrollSize);
            v.getButtonScrollUp().setDimensions(scrollSize, scrollSize);
            v.getButtonScrollUp().setY(innerBounds.getHeight() - scrollSize - 1);
            v.getScrollTrack().setDimensions(scrollSize, innerBounds.getHeight() - (scrollSize * 2));
//            childElement.setVThumbSize();
        } else if (!useVerticalWrap && h.getIsVisible() && !v.getIsVisible()) {
            h.setWidth(innerBounds.getWidth());
            h.getButtonScrollUp().setX(innerBounds.getWidth() - scrollSize);
            h.getScrollTrack().setDimensions(innerBounds.getWidth() - (scrollSize * 2), scrollSize);
//            childElement.setHThumbSize();
        } else if (!useVerticalWrap && h.getIsVisible() && v.getIsVisible()) {
            h.setWidth(innerBounds.getWidth() - scrollSize - gap);
            h.getButtonScrollUp().setX(innerBounds.getWidth() - (scrollSize));
            h.getScrollTrack().setDimensions(innerBounds.getWidth() - (scrollSize * 2), scrollSize);
            v.setX(childElement.getWidth() - scrollSize - 1);
            v.setDimensions(scrollSize, innerBounds.getHeight() - scrollSize - gap);
            v.setY(scrollSize + gap);
            v.getScrollTrack().setDimensions(scrollSize, innerBounds.getHeight() - (scrollSize * 2));
            v.getButtonScrollUp().setY(innerBounds.getHeight() - scrollSize);
//            childElement.setVThumbSize();
//            childElement.setHThumbSize();
        } else if (useVerticalWrap && h.getIsVisible()) {
            h.hide();
        }

            
            float maxDiff = -childElement.getScrollableArea().getHeight() + innerBounds.getHeight();
            if(maxDiff > childElement.getScrollableArea().getY()) {
                childElement.getScrollableArea().setY(maxDiff);
            }
    }
}
