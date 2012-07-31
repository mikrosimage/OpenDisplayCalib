package fr.hd3d.colortribe.color.type;

import fr.hd3d.colortribe.color.util.ColorMath;


public class Point2f implements IPoint
{
    public float _a = 0;
    public float _b = 0;

    public Point2f()
    {}

    public Point2f(float a, float b)
    {
        _a = a;
        _b = b;
    }

    public Point2f(Point2f p)
    {
        _a = p._a;
        _b = p._b;
    }

    @Override
    public Point2f clone()
    {
        return new Point2f(_a, _b);
    }

    /**
     * Linearly interpolates between this Point2D and Point2D p1 and places the result into this Point2D: this =
     * (1-alpha)*this + alpha*p1.
     * 
     * @param p1
     * @param alpha
     */
    public void interpolate(Point2f p1, float alpha)
    {
        interpolate(this, p1, alpha);
    }

    /**
     * Linearly interpolates between tuples p1 and p2 and places the result into this Point2D: this = (1-alpha)*p1 +
     * alpha*p2.
     * 
     * @param p1
     * @param p2
     * @param alpha
     */
    public void interpolate(Point2f p1, Point2f p2, float alpha)
    {
        _a = ColorMath.interpolate(p1._a, p2._a, alpha);
        _b = ColorMath.interpolate(p1._b, p2._b, alpha);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || obj instanceof Point2f == false)
            return false;
        final Point2f point = (Point2f) obj;
        return Float.compare(_a, point._a) == 0 && Float.compare(_b, point._b) == 0;
    }

    @Override
    public String toString()
    {
        return _a + " " + _b;
    }
}
