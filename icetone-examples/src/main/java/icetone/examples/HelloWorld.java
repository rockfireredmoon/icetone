package icetone.examples;

import org.xhtmlrenderer.css.parser.property.PrimitivePropertyBuilders.FontStyle;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Panel;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.FillLayout;
import icetone.text.FontSpec;

public class HelloWorld extends SimpleApplication {

	public static void main(String[] args) {
		new HelloWorld().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).showElement(new Panel(new FillLayout()) {
			{
				setPosition(100, 100);
				setPreferredDimensions(new Size(200, 200));
				addElement(new ScrollPanel() {
					{
						for (int i = 0; i < 30; i++)
							addScrollableContent(new Label("ABCDEFGHIJKLMNOPQRSTUVWYZ").setFont(new FontSpec(30)));
					}
				});
			}
		});
	}

}