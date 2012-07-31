package fr.hd3d.colortribe.color.type;

import fr.hd3d.colortribe.color.util.ColorMath;


public class Point3f implements IPoint
{
    public float _a = 0;
    public float _b = 0;
    public float _c = 0;

    public Point3f()
    {}

    public Point3f(float a, float b, float c)
    {
        _a = a;
        _b = b;
        _c = c;
    }

    public Point3f(Point3f p)
    {
        _a = p._a;
        _b = p._b;
        _c = p._c;
    }

    @Override
    public Point3f clone()
    {
        return new Point3f(_a, _b, _c);
    }

    /**
     * Linearly interpolates between this Point3D and Point3D p1 and places the result into this Point3D: this =
     * (1-alpha)*this + alpha*p1.
     * 
     * @param p1
     * @param alpha
     */
    public Point3f interpolate(Point3f p1, float alpha)
    {
        return interpolate(this, p1, alpha);
    }

    /**
     * Linearly interpolates between tuples p1 and p2 and places the result into this Point3D: this = (1-alpha)*p1 +
     * alpha*p2.
     * 
     * @param p1
     * @param p2
     * @param alpha
     */
    public Point3f interpolate(Point3f p1, Point3f p2, float alpha)
    {
        _a = ColorMath.interpolate(p1._a, p2._a, alpha);
        _b = ColorMath.interpolate(p1._b, p2._b, alpha);
        _c = ColorMath.interpolate(p1._c, p2._c, alpha);
        return this;
    }

    @Override
    public String toString()
    {
        return _a + " " + _b + " " + _c;
    }

    
    public String clampedToString()
    {
        return ((int) (_a * 100)) / 100f + " " + ((int) (_b * 100)) / 100f + " " + ((int) (_c * 100)) / 100f;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || obj instanceof Point3f == false)
            return false;
        final Point3f point = (Point3f) obj;
        return Float.compare(_a, point._a) == 0 && Float.compare(_b, point._b) == 0 && Float.compare(_c, point._c) == 0;
    }
}
