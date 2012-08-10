package fr.hd3d.colortribe.color.type;


public class Point2f implements IPoint {
	public float _a = 0;
	public float _b = 0;

	public Point2f() {
	}

	public Point2f(float a, float b) {
		_a = a;
		_b = b;
	}

	public Point2f(Point2f p) {
		_a = p._a;
		_b = p._b;
	}

	@Override
	public Point2f clone() {
		return new Point2f(_a, _b);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj instanceof Point2f == false)
			return false;
		final Point2f point = (Point2f) obj;
		return Float.compare(_a, point._a) == 0
				&& Float.compare(_b, point._b) == 0;
	}

	@Override
	public String toString() {
		return _a + " " + _b;
	}
}
