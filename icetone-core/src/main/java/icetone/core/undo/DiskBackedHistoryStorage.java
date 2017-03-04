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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

public class DiskBackedHistoryStorage extends DefaultListStorage {

	private static final Logger LOG = Logger.getLogger(DiskBackedHistoryStorage.class.getName());
	private int pageSize;
	private int undoBlock;
	private final File undoDir;
	private final File redoDir;

	public DiskBackedHistoryStorage() throws IOException {
		this(250);
	}

	public DiskBackedHistoryStorage(int pageSize) throws IOException {
		this.pageSize = pageSize;

		final File storageDir = new File(new File(System.getProperty("java.io.tmpdir")),
				"umgr" + System.currentTimeMillis() + hashCode());
		if (!storageDir.mkdirs()) {
			throw new IOException(String.format("Could not create undo manager storage directory %s.", storageDir));
		}
		undoDir = new File(storageDir, "undo");
		if (!undoDir.mkdir()) {
			throw new IOException(String.format("Could not create undo manager storage directory %s.", undoDir));
		}
		redoDir = new File(storageDir, "redo");
		if (!redoDir.mkdir()) {
			throw new IOException(String.format("Could not create undo manager storage directory %s.", undoDir));
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				Path directory = Paths.get(storageDir.toURI());
				try {
					Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}
					});
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		});
	}

	@Override
	public void pushUndo(UndoableCommand command) {
		super.pushUndo(command);

		// If the undo list is at the maximum allowed in memory, store the
		// current contents
		// to disk
		if (undoSize() == pageSize) {
			//
			LOG.info(String.format("Writing block of %s", pageSize));
			try {
				FileOutputStream out = new FileOutputStream(new File(undoDir, (undoBlock++) + ".txt"));
				ObjectOutputStream oos = new ObjectOutputStream(out);
				try {
					while (!history.isEmpty()) {
						oos.writeObject(history.pop());
					}
					oos.flush();
				} finally {
					oos.close();
				}
			} catch (IOException ioe) {
				throw new RuntimeException("Failed to store undo operation to disk.", ioe);
			}
		}
	}
}
