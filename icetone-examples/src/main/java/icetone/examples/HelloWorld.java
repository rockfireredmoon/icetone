package icetone.examples;

import com.jme3.app.SimpleApplication;

import icetone.controls.containers.Panel;
import icetone.controls.text.Label;
import icetone.core.Screen;

public class HelloWorld extends SimpleApplication {

	public static void main(String[] args) {
		new HelloWorld().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).addElement(new Panel().addElement(new Label("Hello World!")));
	}

}
