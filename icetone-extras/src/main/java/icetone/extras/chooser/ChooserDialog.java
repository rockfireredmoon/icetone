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

import java.util.prefs.Preferences;

import icetone.core.BaseScreen;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserPanel.ChooserView;

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
public class ChooserDialog<I> extends AbstractChooserDialog<I> {

	public ChooserDialog(String title, ChooserModel<I> resources) {
		this(BaseScreen.get(), title, resources, new DefaultButtonView<I>());
	}

	public ChooserDialog(String title, ChooserModel<I> resources, ChooserView<I> view) {
		this(BaseScreen.get(), title, resources, view);
	}

	public ChooserDialog(final BaseScreen screen, String title, ChooserModel<I> resources, ChooserView<I> view) {
		this(screen, null, title, resources, null, view);
	}

	public ChooserDialog(final BaseScreen screen, String styleId, String title, ChooserModel<I> resources,
			Preferences pref, ChooserView<I> view) {
		super(screen, styleId, title, resources, pref, view);
		content.setLayoutManager(new MigLayout(screen, "gap 0, ins 0, wrap 1", "[fill, grow]", "[fill, grow]"));
		content.addElement(panel);
		setFolder(null);
	}

	@Override
	protected ChooserPanel<I> createPanel() {
		return new ChooserPanel<I>(screen, resources, pref, view);
	}

}
