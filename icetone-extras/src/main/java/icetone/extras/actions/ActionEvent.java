package icetone.extras.actions;

import com.jme3.input.event.InputEvent;

import icetone.core.UIEventTarget;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIEvent;

public class ActionEvent extends InputEvent implements UIEvent {

	private AppAction sourceAction;
	private UIEventTarget sourceElement;
	private UIEvent sourceEvent;

	public ActionEvent(AppAction sourceAction, UIEventTarget sourceElement, UIEvent sourceEvent) {
		time = System.currentTimeMillis();
		this.sourceAction = sourceAction;
		this.sourceElement = sourceElement;
		this.sourceEvent = sourceEvent;
	}

	public ActionEvent(AppAction sourceAction, UIChangeEvent<? extends UIEventTarget,?> evt) {
		this(sourceAction, evt.getSource(), evt);
	}

	public AppAction getSourceAction() {
		return sourceAction;
	}

	public UIEventTarget getSourceElement() {
		return sourceElement;
	}

	public UIEvent getSourceEvent() {
		return sourceEvent;
	}


}
