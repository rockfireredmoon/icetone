/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.core.event.mouse;

import icetone.core.UIEventTarget;
import icetone.core.event.UIListener;

/**
 *
 * @author t0neg0d
 */
public interface MouseUIWheelListener<E extends UIEventTarget> extends UIListener {
	void onMouseWheel(MouseUIWheelEvent<E> evt);
}
