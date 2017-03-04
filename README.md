# icetone
A GUI Library for JME3 based on a heavily modified version of  Tonegod's 'Tonegodgui'.

It started life as an extension to TonegodGUI adding Swing-like layout managers, in particular 'Mig Layout'. However,
TonegodGUI seems no longer maintained, and maintaining 100% compatibility with it seemed pointless. 
For the most part, porting an existing TonegodGUII application to icetone would mostly be a case a fixing imports. 
Although there is already some behaviour difference and will likely diverge more.

Now Mavenized, also added are a number of additional components such as Split Pane, Table, and an XHTML renderer
based on Flying Saucer.

Differences Between Icetone and TonegodGUI

* Different namespace (tonegod.gui vs icetone).
* TonegodGUI was Java 6 and above, Icetone is Java 8 and above only.
* Icetone makes use of generics for many of the components.
* File based layout and styling in TonegodGUI was a restricted XML format. Icetone uses CSS for styling of all
  all element attributes and YAML based component and screen layouts.
* TonegodGUI used sub-classing for event handling, Icetone uses listeners (configured using Lamda syntax)
* TonegodGUI's 'Docking and Borders' (and the new Layout interface) layout system has been completely removed
  and replaced entirely with LayoutManager implementations. Lots of ready made layouts are provided. 
* Icetone contains a more refined control suite with additional controls, bug fixes and features.
