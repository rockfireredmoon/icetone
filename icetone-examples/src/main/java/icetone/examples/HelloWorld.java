package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Frame;
import icetone.controls.containers.SplitPanel;
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
		Screen.init(this).showElement(new Frame() {
			{
				setResizable(true);
				setTitle("Horizontal Split");
				getContentArea().setLayoutManager(new FillLayout());
				getContentArea().addElement(new SplitPanel() {
					{
						setLeftOrTop(new Label("Left"));
						setRightOrBottom(new Label("Right"));
					}
				});
				setPosition(20, 270);
				setPreferredDimensions(new Size(310, 200));
			}
		});
	}

}