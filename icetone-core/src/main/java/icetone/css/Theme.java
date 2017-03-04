package icetone.css;

import java.util.Properties;

public class Theme {

	private String path;
	private String name;
	private String author;
	private String description;
	private String parent;
	
	public Theme() {
	}

	Theme(Properties properties, String path) {
		this.name = properties.getProperty("name");
		this.path = properties.getProperty("path");
		if (this.path == null) {
			int idx = path.lastIndexOf('/');
			String folder = path;
			if (idx != -1)
				folder = path.substring(0, idx);
			path = folder + "/" + name + ".css";
		}
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String toString() {
		return getName();
	}
}
