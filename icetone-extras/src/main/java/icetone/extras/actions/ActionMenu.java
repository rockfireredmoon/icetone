package icetone.extras.actions;

public class ActionMenu implements Comparable<ActionMenu> {

	private String name;
	private int weight;

	public ActionMenu(String name) {
		this(name, 0);
	}

	public ActionMenu(String name, int weight) {
		super();
		this.name = name;
		this.weight = weight;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(ActionMenu o) {
		int cmp = weight - o.weight;
		return cmp == 0 ? name.compareTo(o.name) : cmp;
	}

}
