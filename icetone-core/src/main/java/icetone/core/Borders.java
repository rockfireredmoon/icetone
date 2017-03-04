package icetone.core;

public enum Borders {
	E, N, NE, NW, S, SE, SW, W;

	public Borders opposite() {
		switch (this) {
		case NW:
			return SE;
		case N:
			return S;
		case NE:
			return SW;
		case W:
			return E;
		case E:
			return W;
		case SW:
			return NE;
		case S:
			return N;
		case SE:
			return NW;
		}
		throw new IllegalArgumentException();
	}
}