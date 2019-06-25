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
package icetone.core.event.keyboard;

import icetone.core.ToolKit;
import icetone.core.event.AbstractEventSupport;
import icetone.core.utils.Alarm.AlarmTask;
import icetone.effects.EffectChannel;

/**
 * @author rockfire
 */
public class KeyboardSupport extends AbstractEventSupport<UIKeyboardListener, KeyboardUIEvent> {

	private KeyboardUIEvent last;
	private AlarmTask repeatTask;
	private boolean defeatRepeat;
	private boolean firingRepeat;

	public void cancelRepeats() {
		if (firingRepeat)
			defeatRepeat = true;
		if (repeatTask != null) {
			repeatTask.cancel();
			repeatTask = null;
			last = null;
		}
	}

	public boolean fireLimitedEvent(KeyboardUIEvent evt) {
		if (evt.isPressed()) {
			/* Pressed events are fired constantly when repeating */
			if (repeatTask != null) {
				if (last != null && evt.isSameKey(last)) {
					/*
					 * Same key, ignore this event, but update the key event we will eventually sent
					 */
					last = evt;

					/* Handled because */
					return true;
				} else {
					/*
					 * Different key, cancel repeat of previous key and let new one fire
					 * immediately, then repeat
					 */
					cancelRepeats();
				}
			}

			/* Fire once immediately */
			try {
				defeatRepeat = false;
				firingRepeat = true;
				fireEvent(evt);
			} finally {
				firingRepeat = false;
			}

			if (!defeatRepeat && evt.isConsumed()) {
				/* If consumed, it can be repeated */
				last = evt;
				repeatKey(false, ToolKit.get().getConfiguration().getRepeatDelay());

				/* Handled if was consumed */
				return true;
			}

			/* Not handled as repeating key */
			return false;
		} else {
			/* If there is a repeat task, cancel it */
			cancelRepeats();

			/* Always fire release immediately */
			try {
				firingRepeat = true;
				fireEvent(evt);
			} finally {
				firingRepeat = false;
			}

			/* Handled if was consumed */
			return evt.isConsumed();
		}
	}

	protected void repeatKey(boolean repeat, float time) {
		repeatTask = ToolKit.get().getAlarm().timed(() -> {
			if (last != null) {
				if(repeat)
					last.setRepeating();
				float interval = ToolKit.get().getConfiguration().getRepeatInterval();
				try {
					firingRepeat = true;
					if(last.getElement().getScreen().getEffectManager().hasEffectFor(last.getElement(), EffectChannel.fx)) {
						interval = 0.001f;
					}
					else
						fireEvent(last);
				} finally {
					firingRepeat = false;
				}
				if (!defeatRepeat)
					repeatKey(true, interval);
			}
		}, time);
	}

	public void bindPressed(UIKeyboardListener l) {
		bind(l, new UIKeyboardListener() {
			@Override
			public void onKey(KeyboardUIEvent evt) {
				if (evt.isPressed())
					l.onKey(evt);
			}
		});
	}

	public void bindReleased(UIKeyboardListener l) {
		bind(l, new UIKeyboardListener() {
			@Override
			public void onKey(KeyboardUIEvent evt) {
				if (evt.isReleased())
					l.onKey(evt);
			}
		});
	}

	@Override
	protected void handleEvent(UIKeyboardListener listener, KeyboardUIEvent evt) {
		listener.onKey(evt);
	}

}
