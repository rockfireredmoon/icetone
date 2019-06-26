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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.prefs.Preferences;

import icetone.controls.buttons.Button;
import icetone.controls.containers.SlideTray;
import icetone.controls.containers.SplitPanel;
import icetone.controls.table.Table;
import icetone.controls.table.TableCell;
import icetone.controls.table.TableRow;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.event.ChangeSupport;
import icetone.core.event.UIChangeEvent;
import icetone.core.event.UIChangeListener;
import icetone.core.event.ElementEvent.Type;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.controls.BusySpinner;

/**
 * The {@link ChooserDialog} provides a window for selecting one or more entries
 * from a hierarchical list of strings. The hierarchy is determined by the '/'
 * in the paths supplied.
 * <p>
 * The window is split into two parts, the left hand side lists the available
 * 'folders', i.e. all of the parents derived from the list of provided resource
 * path strings. The right hand side is used to list the resources in the
 * folder. The presentation for this is provided by an implementor of
 * {@link ChooserView}.
 */
public class ChooserPanel<I> extends StyledContainer {

	public enum SelectionMode {
		SINGLE, MULTIPLE, NONE;
	}

	private final ChooserView<I> view;
	private final SplitPanel split;
	private final BusySpinner busySpinner;
	private SelectionMode selectionMode;
	private boolean busy;
	private ExecutorService loader = Executors.newFixedThreadPool(1, new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread("ChooserPanel-" + ChooserPanel.this.hashCode());
			t.setDaemon(true);
			return t;
		}
	});

	/**
	 * Implement to provide the UI component used for the right hand side of the
	 * chooser dialog.
	 */
	public interface ChooserView<I> {

		/**
		 * Set whether the view is enabled or not. May change during life of view
		 * 
		 * @param enabled enabled
		 */
		void setIsEnabled(boolean enabled);

		/**
		 * Create the UI component for this view. It will only be called once.
		 *
		 * @param chooser chooser
		 * @return view element
		 */
		BaseElement createView(ChooserPanel<I> chooser);

		/**
		 * Rebuild the displayed items. Will be called multiple times as the user
		 * navigates the folder heirarchy.
		 *
		 * @param cwd       the current working directory, i.e. the path of parent.
		 * @param resources the list of resources in the current working directory
		 */
		void rebuild(I cwd, Collection<I> resources);

		/**
		 * Present the file as selected in this view.
		 *
		 * @param file file
		 */
		void select(I file);
	}

	private final Table folders;
	private ChooserModel<I> resources;
	private I cwd;
	private final SlideTray breadCrumbs;
	private I selected;
	private ChangeSupport<ChooserPanel<I>, I> changeSupport;

	@SuppressWarnings("unchecked")
	public ChooserPanel(final BaseScreen screen, ChooserModel<I> resources, Preferences pref, ChooserView<I> view) {
		super(screen);

		this.view = view;
		layoutManager = new MigLayout(screen, "ins 0, wrap 2, fill", "[grow][shrink 0]", "[shrink 0][grow]");

		// Bread crumbs
		breadCrumbs = new SlideTray(screen) {
			{
				styleClass = "breadcrumbs";
			}
		};

		// Busy
		busySpinner = new BusySpinner(screen);

		// Folders
		folders = new Table(screen);
		folders.onMouseReleased(evt -> {
			if (evt.getClicks() == 2) {
				final List<TableRow> selectedListItems = folders.getSelectedRows();
				if (!selectedListItems.isEmpty()) {
					TableRow selRow = selectedListItems.iterator().next();
					TableCell selCell = (TableCell) selRow.getElements().iterator().next();
					runAdjusting(() -> setFolder((I) selCell.getValue()));
				}
			}
		});

		folders.setMinDimensions(Size.ZERO);
		folders.addColumn("Folder");
		folders.setHeadersVisible(false);
		folders.setColumnResizeMode(Table.ColumnResizeMode.AUTO_ALL);

		// Split
		split = new SplitPanel(screen, Orientation.HORIZONTAL);
		split.setLeftOrTop(folders);
		split.setRightOrBottom(view.createView(this));
		split.setDefaultDividerLocationRatio(0.25f);

		// This
		addElement(breadCrumbs, "growx");
		addElement(busySpinner);
		addElement(split, "growx, growy, span 2");

		//
		this.resources = resources;

		setAvailable();
		// setFolder(null);

		onElementEvent(evt -> {
			loader.shutdown();
		}, Type.CLEANUP);
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public ChooserPanel<I> addChangeListener(UIChangeListener<ChooserPanel<I>, I> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.addListener(listener);
		return this;
	}

	public ChooserPanel<I> onChange(UIChangeListener<ChooserPanel<I>, I> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.bind(listener);
		return this;
	}

	public ChooserPanel<I> removeChangeListener(UIChangeListener<ChooserPanel<I>, I> listener) {
		if (changeSupport == null)
			changeSupport = new ChangeSupport<>();
		changeSupport.removeListener(listener);
		return this;
	}

	public ChooserPanel<I> unbindChanged(UIChangeListener<ChooserPanel<I>, I> listener) {
		if (changeSupport != null)
			changeSupport.unbind(listener);
		return this;
	}

	public BusySpinner getBusy() {
		return busySpinner;
	}

	public ChooserView<I> getView() {
		return view;
	}

	public I getSelected() {
		return selected;
	}

	public final void setSelectedFile(I file) {
		I was = selected;
		I dirname = resources.getParent(file);
		if (!Objects.equals(dirname, cwd)) {
			// Will cause a rebuild so no need to select invidually
			selected = file;
			setFolder(dirname);
		} else {
			selected = file;
			view.select(file);
		}
		change(was, selected, true);
	}

	public void choose(I path) {
		change(getSelected(), path, false);
	}

	public final void setFolder(I folder) {
		this.cwd = folder;
		rebuildList();
		rebuildBreadCrumbs();
	}

	protected void change(I oldValue, I newValue, boolean temporary) {
		if (changeSupport != null)
			changeSupport
					.fireEvent(new UIChangeEvent<ChooserPanel<I>, I>(this, oldValue, newValue).setTemporary(temporary));
	}

	private void rebuildBreadCrumbs() {
		breadCrumbs.invalidate();
		breadCrumbs.removeAllTrayElements();
		Button root = new Button(screen);
		root.onMouseReleased(evt -> setFolder(null));
		root.setText("/");
		breadCrumbs.addTrayElement(root);
		if (cwd != null) {

			/* Split up the current working path into individual nodes */
			List<I> l = new ArrayList<>();
			I c = cwd;
			while (c != null) {
				l.add(0, c);
				c = resources.getParent(c);
			}

			for (I p : l) {
				Button dir = new Button(screen);
				dir.onMouseReleased(evt -> setFolder(p));
				dir.setText(resources.getLabel(p));
				breadCrumbs.addTrayElement(dir);
			}
		}
		breadCrumbs.validate();
	}

	private void rebuildList() {

		folders.invalidate();
		folders.removeAllRows();

		// Find all the unique folder names in the current path
		Set<I> foldersNames = new LinkedHashSet<I>();
		final Set<I> filesNames = new LinkedHashSet<I>();
		if (resources != null) {
			for (I s : resources.list(cwd)) {
				if (resources.isLeaf(s)) {
					filesNames.add(s);
				} else {
					foldersNames.add(s);
				}
			}
		}

		// Folders
		if (cwd != null) {
			TableRow row = new TableRow(screen, folders);
			row.addCell("..", resources.getParent(cwd));
			folders.addRow(row);
		}
		for (I s : foldersNames) {
			TableRow row = new TableRow(screen, folders);
			row.addCell(resources.getLabel(s), s);
			folders.addRow(row);
		}
		folders.validate();

		// Now the images
		boolean foundSelection = false;
		for (I s : filesNames) {
			if (s.equals(selected)) {
				foundSelection = true;
			}
		}

		loader.execute(() -> view.rebuild(cwd, filesNames));

		// Clear selection if it is not in current direction
		if (!foundSelection) {
			selected = null;
		}
	}

	public ChooserModel<I> getResources() {
		return resources;
	}

	public void setResources(ChooserModel<I> resources) {
		this.resources = resources;
		setFolder(cwd);
	}

	public void busy() {
		if (busy)
			throw new IllegalStateException("Already busy");
		this.busy = true;
		busySpinner.setSpeed(BusySpinner.DEFAULT_SPINNER_SPEED);
		setAvailable();
	}

	public void idle() {
		if (!busy)
			throw new IllegalStateException("Not busy");
		this.busy = false;
		busySpinner.setSpeed(0);
		setAvailable();
	}

	private void setAvailable() {
		folders.setEnabled(!busy);
		breadCrumbs.setEnabled(!busy);
	}
}
