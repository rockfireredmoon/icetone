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
package icetone.extras.chooser;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of a {@link ChooserModel} to provide a <i>File Chooser</i>
 * type control.
 */
public class FileChooserModel implements ChooserModel<File> {

	private File root;
	private FilenameFilter filenameFilter;
	private FileFilter fileFilter;
	private boolean hiddenFiles;
	private boolean folders = true;
	private boolean files = true;
	private boolean sortByName = true;
	private boolean sortReverse;

	public FileChooserModel() {
		this(new File(System.getProperty("user.dir")));
	}

	public FileChooserModel(File root) {
		this.root = root;
	}

	public boolean isSortByName() {
		return sortByName;
	}

	public void setSortByName(boolean sortByName) {
		this.sortByName = sortByName;
	}

	public boolean isSortReverse() {
		return sortReverse;
	}

	public void setSortReverse(boolean sortReverse) {
		this.sortReverse = sortReverse;
	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public FilenameFilter getFilenameFilter() {
		return filenameFilter;
	}

	public void setFilenameFilter(FilenameFilter filenameFilter) {
		this.filenameFilter = filenameFilter;
	}

	@Override
	public List<File> list(File root) {
		if (root == null)
			root = this.root;
		if (!root.isDirectory())
			return null;

		return sort(Arrays.asList(filenameFilter == null && fileFilter == null ? root.listFiles(createDefaultFilter())
				: (fileFilter == null ? root.listFiles(filenameFilter) : root.listFiles(fileFilter))));
	}

	private List<File> sort(List<File> asList) {
		if(sortByName) {
			asList = new ArrayList<File>(asList);
			Collections.sort(asList);
			if(sortReverse)
				Collections.reverse(asList);
		}
		return asList;
	}

	@Override
	public boolean isLeaf(File resource) {
		return !resource.isDirectory();
	}

	@Override
	public File getParent(File p) {
		File parentFile = p.getParentFile();
		return parentFile.equals(root) ? null : parentFile;
	}

	@Override
	public String getLabel(File p) {
		return p.getName();
	}

	@Override
	public File parse(String value) {
		return new File(root, value);
	}

	public boolean isHiddenFiles() {
		return hiddenFiles;
	}

	public void setHiddenFiles(boolean hiddenFiles) {
		this.hiddenFiles = hiddenFiles;
	}

	public boolean isFolders() {
		return folders;
	}

	public void setFolders(boolean folders) {
		this.folders = folders;
	}

	public boolean isFiles() {
		return files;
	}

	public void setFiles(boolean files) {
		this.files = files;
	}

	protected FileFilter createDefaultFilter() {
		return new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory() && !folders)
					return false;
				if (pathname.isFile() && !files)
					return false;
				try {
					if (Files.isHidden(pathname.toPath()) && !hiddenFiles)
						return false;
				} catch (IOException e) {
				}
				return true;
			}

		};
	}
}
