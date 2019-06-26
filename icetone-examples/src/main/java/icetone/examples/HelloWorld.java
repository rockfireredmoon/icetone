package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Frame;
import icetone.controls.containers.TabControl;
import icetone.controls.text.Label;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.FillLayout;

public class HelloWorld extends SimpleApplication {

	public static void main(String[] args) {
		new HelloWorld().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).showElement(new Frame(new FillLayout()) {
			{
				setTitle("Tabs");
				setResizable(true);
				getContentArea().addElement(new TabControl() {
					{
						addTab("Tab1", new Label("Tab Content 1"));
						addTab("Tab2", new Label("Tab Content 2"));
						addTab("Tab3", new Label("Tab Content 3"));
						addTab("Tab4", new Label("Tab Content 4"));
					}
				});

				setPosition(20, 390);
				setPreferredDimensions(new Size(440, 340));
			}
		});
	}

}