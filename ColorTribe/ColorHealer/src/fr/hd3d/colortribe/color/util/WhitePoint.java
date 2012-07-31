package fr.hd3d.colortribe.color.util;

import fr.hd3d.colortribe.color.type.Point2f;

public class WhitePoint
{
    public String name;
    Point2f _point;

    public Point2f asDoubleFloat()
    {
        return new Point2f(_point);
    }

    public WhitePoint(String name, float a, float b)
    {
        _point = new Point2f(a,b);       
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public boolean sameAs(WhitePoint o)
    {
        if (o.name.equals(name) && o._point._a == _point._a && o._point._b == _point._b)
            return true;
        else
            return false;
    }
}
