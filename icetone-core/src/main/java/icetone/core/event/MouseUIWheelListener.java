/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package icetone.core.event;

import icetone.core.UIEventTarget;

/**
 *
 * @author t0neg0d
 */
public interface MouseUIWheelListener<E extends UIEventTarget> extends UIListener {
	void onMouseWheel(MouseUIWheelEvent<E> evt);
}
