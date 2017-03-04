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
package icetone.core.undo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UndoManager {

	private static final Logger LOG = Logger.getLogger(UndoManager.class.getName());

	public static class ListenerAdapter implements UndoListener {

		@Override
		public void undoing(UndoableCommand command) {
			starting();
			change();
		}

		@Override
		public void undone(UndoableCommand command) {
			complete();
			change();
		}

		@Override
		public void redoing(UndoableCommand command) {
			starting();
			change();
		}

		@Override
		public void redone(UndoableCommand command) {
			complete();
			change();
		}

		@Override
		public void doing(UndoableCommand command) {
			starting();
			change();
		}

		@Override
		public void done(UndoableCommand command) {
			complete();
			change();
		}

		protected void change() {
		}

		protected void starting() {
		}

		protected void complete() {
		}
	}

	private final HistoryStorage storage;
	private List<UndoListener> listeners = new ArrayList<>();

	public UndoManager() {
		this(new DefaultListStorage());
	}

	public UndoManager(HistoryStorage storage) {
		this.storage = storage;
	}

	public int redoSize() {
		return storage.redoSize();
	}

	public int undoSize() {
		return storage.undoSize();
	}

	public void addListener(UndoListener listener) {
		listeners.add(listener);
	}

	public void removeListener(UndoListener listener) {
		listeners.remove(listener);
	}

	public boolean isEmpty() {
		return storage.isEmpty();
	}

	public boolean isUndoAvailable() {
		return storage.isUndoAvailable();
	}

	public boolean isRedoAvailable() {
		return storage.isRedoAvailable();
	}

	public void undo() {
		if (!isUndoAvailable()) {
			throw new IllegalStateException("Cannot undo any further, no history.");
		}
		UndoableCommand last = storage.popUndo();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Undo  - history is now %d big, redo is %d", storage.undoSize(), storage.redoSize()));
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).undoing(last);
		}
		last.undoCommand();
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).undone(last);
		}
		storage.pushRedo(last);
	}

	public void redo() {
		if (!isRedoAvailable()) {
			throw new IllegalStateException("Cannot redo any further, no history.");
		}
		UndoableCommand last = storage.popRedo();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Redo  - history is now %d big, redo is %d", storage.undoSize(), storage.redoSize()));
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).redoing(last);
		}
		last.doCommand();
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).redone(last);
		}
		storage.pushUndo(last);
	}

	public void storeAndExecute(UndoableCommand command) {
		storage.pushUndo(command);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Do - history is now %d big, redo is %d", storage.undoSize(), storage.redoSize()));
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).doing(command);
		}
		command.doCommand();
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).done(command);
		}
		storage.clearRedo();
	}
}
