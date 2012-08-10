package fr.hd3d.colortribe.color.util;

import fr.hd3d.colortribe.color.type.Point2f;

class WhitePoint
{
    String name;
    private Point2f _point;

    Point2f asDoubleFloat()
    {
        return new Point2f(_point);
    }

    WhitePoint(String name, float a, float b)
    {
        _point = new Point2f(a,b);       
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    
}
