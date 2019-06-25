# icetone

![Alt text](src/main/readme/theme-default-256.png?raw=true "Default Theme")
![Alt text](src/main/readme/theme-gold-256.png?raw=true "Gold Theme")
![Alt text](src/main/readme/theme-paranoid-256.png?raw=true "Paranoid Theme")
![Alt text](src/main/readme/theme-slicknessruby-256.png?raw=true "Slickness Ruby Theme")
![Alt text](src/main/readme/theme-steampunk-256.png?raw=true "Steampunk Theme")

A GUI Library for JME3 based on a heavily modified version of  Tonegod's 'Tonegodgui'.

It started life as an extension to TonegodGUI adding Swing-like layout managers, in particular 'Mig Layout'. However,
TonegodGUI seems no longer maintained, and maintaining 100% compatibility with it seemed pointless. 
For the most part, porting an existing TonegodGUII application to icetone would mostly be a case a fixing imports. 
Although there is already some behaviour difference and will likely diverge more.

Now Mavenized, also added are a number of additional components such as Split Pane, Table, and an XHTML renderer
based on Flying Saucer.

## HelloWorld

For the impatient, here is the ubiquitous *HelloWorld* example for Icetone. It simply displays a a text label on an immovable panel at the top left of the screen.  

```java

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

```

## Differences Between Icetone and TonegodGUI

* Different namespace (tonegod.gui vs icetone).
* TonegodGUI was Java 6 and above, Icetone is Java 8 and above only.
* Icetone makes use of generics for many of the components.
* File based layout and styling in TonegodGUI was a restricted XML format. Icetone uses CSS for styling of all
  all element attributes and YAML based component and screen layouts.
* TonegodGUI used sub-classing for event handling, Icetone uses listeners (configured using Lamda syntax)
* TonegodGUI's 'Docking and Borders' (and the new Layout interface) layout system has been completely removed
  and replaced entirely with LayoutManager implementations. Lots of ready made layouts are provided. 
* Icetone contains a more refined control suite with additional controls, bug fixes and features.
* TonegodGUI had a two ways of rendering text, Icetone has a pluggable system that provides 4 different methods
  depending on yours, including TTF support provided by JMETTF.
  
## CSS Support and Themes

Icetone has incredibly powerful CSS support, allowing GUI elements to be styled, sized and positioned entirely using CSS. 

### CSS

As with CSS as it is used in HTML, in Icetone, you generally select elements based on :-

  * Their element name (which maps to the base Java class name of the control). For example, `icetone.controls.text.Label` would be expressed as `Label` in CSS.
  * Their element ID. When not supplied in code, this is auto-generated. To set in code, use
  `yourElement.setStyleId("myId")`. This would then be referenced in CSS as `#myId`.
  * Their element class(es). An element may have multiple classes which may be set using
  `yourElement.addStyleClass("aClass")` and `yourElement.setStyleClass("aClass bClass")`.

As with HTML CSS, you can specify a path to your element(s) to style. Taking the *HelloWorld* above as an example, you could style label and make it red with following :-

```css
Frame Panel Label {
	color: red;
}
```

Many other selector and pseudo expressions are supported including **:focus**, **:hover**, **:nth-child(n)** and more.  

Many styles such as font styling support inheritance, and relative sizes are supported too.

#### Applying CSS to controls

There are a number of different ways CSS may actually be applied to elements.

 * Themes. The CSS contained within a theme is automatically applied to all elements in a component hierarchy. See below for more on Themes.
 
 * You can set arbitrary CSS on any control or container. If set on a container, and the style is inherited by children (e.g. font), then all children will receive that style too.
  `yourElement.setCss("color: red;");`
 
 * Add a an entire stylesheet to a control or container. 
   `yourElement.addStylesheet(Screen.get().getApplication().getAssetManager().loadAsset(new AssetKey<Stylesheet>("mystyles.css")));`

## Themes

CSS Stylesheets may be grouped together in *Themes*, and along with some meta-data may be deployed as self contained Jar files that can be automatically discovered by Icetone at runtime.

You can either set a global theme, or even different themes on certain parts of your GUI. Theme changes can be requested 'live', with the new style being applied immediately without restarting the application.

If you want to create a reusable custom component and share it with others, you may wish to supply some default CSS along with this component. A special theme type, a *Pseudo Theme*, fills this role. Such themes are always loaded along with the main theme (or it can be configured to only work with certain themes using a regular expression pattern).

### Adding Themes

Themes can be installed either by just adding them to the applications CLASSPATH, or you can add them using JMonkey's asset system.

```java
ToolKit.get().getStyleManager().addTheme("/path/to/your.theme");
// or
ToolKit.get().getStyleManager().addTheme(new AssetKey<Theme>("/path/to/your.theme")));
```

### Selecting A Theme

This will set the theme globally.

```java
StyleManager mgr = ToolKit.get().getStyleManager(); 
mgr.setTheme(mgr.getTheme("MyThemeName"));
```

## Controls

Icetone comes with a suite of common controls that should cover most if not all of your needs.
All controls are fully styleable with CSS using methods described above.

### Buttons

Button control types provide a way for the user to interact with clicks to perform an action. Some maintain state, and / or may be grouped (e.g. `RadioButton`). 

Buttons support an icon (any image) and / or text.

#### PushButton

The most basic button type, provides a control for the user to click. A `MouseUIButtonEvent` event is fired upon click which may be listened for and acted upon.

![Alt text](src/main/readme/controls-pushbutton.png?raw=true "PushButton") 

```java
import com.jme3.app.SimpleApplication;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.Screen;

public class PushButtonExample extends SimpleApplication {

	public static void main(String[] args) {
		new PushButtonExample().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).addElement(new Panel()
				.addElement(new PushButton("Press Me!").onMouseReleased((evt) -> System.out.println("Button pressed!")))
				.centerToParent());
	}

}

```

#### CheckBox

A simply dual state button that may be *true* or *false*. A `UIChangeEvent` is fired when this state changes.

![Alt text](src/main/readme/controls-checkbox.png?raw=true "CheckBox") 

```java
import com.jme3.app.SimpleApplication;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.Screen;

public class CheckBoxExample extends SimpleApplication {

	public static void main(String[] args) {
		new CheckBoxExample().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).addElement(new Panel()
				.addElement(new CheckBox("Toggle Me!").onChange((evt) -> System.out.println("I've been toggled!")))
				.centerToParent());
	}

}

```

#### RadioButton

Radio Buttons allow toggling between more than two states, allowing buttons to be grouped. When arranged this way, clicking on one button to select it will deselect the current button.

![Alt text](src/main/readme/controls-radiobutton.png?raw=true "Radio Button") 

```java
import com.jme3.app.SimpleApplication;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.Screen;

public class RadioButtonExample extends SimpleApplication {

	public static void main(String[] args) {
		new RadioButtonExample().start();
	}

	@Override
	public void simpleInitApp() {
		BaseScreen screen = Screen.init(this);
		ButtonGroup<RadioButton<Integer>> grp = new ButtonGroup<>();

		RadioButton<Integer> one = new RadioButton<>("One");
		RadioButton<Integer> two = new RadioButton<>("Two");
		RadioButton<Integer> three = new RadioButton<>("Three");

		grp.addButton(one).addButton(two).addButton(three);
		grp.onChange((evt) -> System.out.print("Value: " + grp.getSelected().getValue()));

		Panel panel = new Panel(new MigLayout("wrap 1"));
		panel.addElement(one);
		panel.addElement(two);
		panel.addElement(three);

		screen.addElement(panel.centerToParent());
	}

}

```

#### Dial

A Dial is a circular control, allowing choice of a value within a range of values. Values may be chosen by either clicking on the appropriate position along the circumference of the dial or dragging in a circular motion. It basically mimics a *Volume Dial*. 

![Alt text](src/main/readme/controls-dial.png?raw=true "Dial") 

```java
import com.jme3.app.SimpleApplication;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.Screen;

public class DialExample extends SimpleApplication {

	public static void main(String[] args) {
		new DialExample().start();
	}

	@Override
	public void simpleInitApp() {
		Screen.init(this).addElement(
				new Panel().addElement(new Dial<Integer>().addStepValue(1).addStepValue(2).addStepValue(3)).centerToParent());
	}

}

```

#### Slider

TODO

### Lists

TODO

### Text

TODO

### Containers

TODO

### Others

TODO

## Layout

TODO

### In Code

TODO

### In YAML

TODO

## Events

TODO

## XHTML

TODO

## Building Your Own Controls

TODO