package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.controls.containers.SlideTray;
import icetone.core.Measurement.Unit;
import icetone.core.Screen;
import icetone.core.Size;
import icetone.core.layout.FillLayout;

public class HelloWorld extends SimpleApplication {

	public static void main(String[] args) {
		new HelloWorld().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).showElement(new Panel(new FillLayout()) {
			{
				addElement(new SlideTray() {
					{
						addTrayElement(new PushButton("Button1"));
						addTrayElement(new PushButton("Button2"));
						addTrayElement(new PushButton("Button3"));
						addTrayElement(new PushButton("Button4"));
						addTrayElement(new PushButton("Button5"));
						addTrayElement(new PushButton("Button6"));
						addTrayElement(new PushButton("Button7"));
						addTrayElement(new PushButton("Button8"));
						addTrayElement(new PushButton("Button9"));
						addTrayElement(new PushButton("Button10"));
						addTrayElement(new PushButton("Button11"));
					}
				});
				setPreferredDimensions(new Size(250, 0, Unit.PX, Unit.AUTO));
				setPosition(100, 100);
			}
		});
	}

}