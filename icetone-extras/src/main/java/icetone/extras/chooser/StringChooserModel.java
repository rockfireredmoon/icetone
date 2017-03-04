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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of a {@link ChooserModel} that takes a list of strings, each
 * of which can be mapped to a hierarchy by using a separator character to
 * denote the parent. For example, take the following list of strings (and using
 * the '/' character as a separator) :-
 * 
 * <pre>
 * Vehicles/Cars/Volkswagon 
 * Vehicles/Cars/Ford 
 * Vehicles/Cars/Nissan
 * Vehicles/Bikes/Honda
 * Vehicles/Bikes/Ducati
 * Vehicles/Empty/
 * </pre>
 * 
 * This would be turned into tree arranged thus :-
 * 
 * <pre>
 * Vehicles
 *    |
 *    +------ Cars
 *    |         |
 *    |         +------- Volkswagon
 *    |         |
 *    |         +------- Ford
 *    |         |
 *    |         +------- Nissan
 *    |
 *    +-------Bikes
 *              |
 *              +-------- Honday
 *              |
 *              +-------- Ducati
 * </pre>
 *
 * To denote an empty, the string should end with the separator.
 */
public class StringChooserModel implements ChooserModel<String> {

	private String separator;
	private List<String> values;

	public StringChooserModel(String... values) {
		this("/", values);
	}

	public StringChooserModel(String separator, String... values) {
		this(separator, new HashSet<String>(Arrays.asList(values)));
	}

	public StringChooserModel(Collection<String> values) {
		this("/", values);
	}

	public StringChooserModel(String separator, Collection<String> values) {
		this.separator = separator;
		this.values = new ArrayList<>(values);
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public boolean isLeaf(String resource) {
		return resource.length() > 0 && !resource.endsWith(separator);
	}

	@Override
	public List<String> list(String root) {
		final Set<String> names = new HashSet<>();
		if (values != null) {
			for (String s : values) {
				if (root == null || s.startsWith(root)) {

					// Strip off the root
					String p = s;

					// Strip off trailing paths
					int idx = p.indexOf('/', root == null ? 0 : root.length());
					if (idx != -1) {
						names.remove(p.substring(0, idx));
						p = p.substring(0, idx + 1);
						names.add(p);
					} else {
						names.add(p);
					}
				}
			}
		}
		return new ArrayList<>(names);
	}

	@Override
	public String getParent(String p) {
		int idx = p.lastIndexOf(separator, p.endsWith(separator) ? p.length() - 2 : p.length() - 1);
		if (idx == -1)
			return null;
		else
			return p.substring(0, idx + 1);
	}

	@Override
	public String getLabel(String p) {
		int idx = p.lastIndexOf(separator, p.endsWith(separator) ? p.length() - 2 : p.length() - 1);
		return idx == -1 ? p : p.substring(idx + 1);
	}

	@Override
	public String parse(String value) {
		return value;
	}

}
