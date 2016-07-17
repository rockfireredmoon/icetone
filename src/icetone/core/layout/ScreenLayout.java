package icetone.core.layout;

import java.util.HashMap;
import java.util.Map;

import com.jme3.math.Vector2f;

import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Element.ZPriority;

public class ScreenLayout extends AbstractLayout {

	Map<Element, ScreenLayoutConstraints> contraints = new HashMap<>();

	@Override
	public Vector2f minimumSize(Element parent) {
		return new Vector2f(parent.getScreen().getWidth(), parent.getScreen().getHeight());
	}

	@Override
	public Vector2f maximumSize(Element parent) {
		return minimumSize(parent);
	}

	@Override
	public Vector2f preferredSize(Element parent) {
		return preferredSize(parent);
	}

	@Override
	public void layoutScreen(ElementManager screen) {
		for (Element el : screen.getElements()) {
			ScreenLayoutConstraints con = contraints.get(el);
			if (ScreenLayoutConstraints.fill == con) {
				LUtil.setBounds(el, 0, 0, screen.getWidth(), screen.getHeight());
			} else if (ScreenLayoutConstraints.none != con) {
				el.sizeToContent();
			}
			else
				el.lockToParentBounds(el.getX(), el.getY());
			// if (el.getX() + el.getWidth() >= screen.getWidth()) {
			// el.setX(screen.getWidth() - el.getWidth());
			// }
			// if (el.getY() + el.getHeight() >= screen.getHeight()) {
			// el.setY(screen.getHeight() - el.getHeight());
			// }
		}
	}

	@Override
	public void constrain(Element child, Object constraints) {
		contraints.put(child, (ScreenLayoutConstraints) constraints);
	}

	@Override
	public void remove(Element child) {
		contraints.remove(child);
	}

	@Override
	public void layout(Element container) {
	}

}