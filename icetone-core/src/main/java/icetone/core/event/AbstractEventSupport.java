/**
 * ICETONE - A GUI Library for JME3 based on a heavily modified version of 
 * Tonegod's 'Tonegodgui'.  
 * 
 * Copyright (c) 2013, t0neg0d
 * Copyright (c) 2016, Emerald Icemoon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */
package icetone.core.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author rockfire
 */
public abstract class AbstractEventSupport<L extends UIListener, E extends UIEvent> {

	protected List<L> listeners = new CopyOnWriteArrayList<>();
	protected Map<L, L> bindMap = new HashMap<>();

	public final void bind(L listener) {
		bind(listener, null);
	}

	public final void unbind(L listener) {
		if (bindMap.containsKey(listener)) {
			removeListener(bindMap.remove(bindMap.get(listener)));
		} else
			removeListener(listener);
	}

	public final void bind(L listener, L filteredListener) {
		if (filteredListener != null && listener != filteredListener) {
			bindMap.put(listener, filteredListener);
			addListener(filteredListener);
		} else
			addListener(listener);
	}

	public void addListener(L listener) {
		if(listener == null)
			throw new NullPointerException();
		listeners.add(listener);
	}

	public void removeListener(L listener) {
		listeners.remove(listener);
	}

	public void fireEvent(E evt) {
		for (int i = listeners.size() - 1 ; i >= 0; i--) {
			if (evt.isConsumed())
				break;
			handleEvent(listeners.get(i), evt);
		}
	}
	protected abstract void handleEvent(L listener, E evt);
}
