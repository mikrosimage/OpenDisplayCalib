package fr.hd3d.colortribe.color.util;

import fr.hd3d.colortribe.color.type.Point2f;



public class Primaries {
    String name;
    public Point2f red;
    public Point2f green;
    public Point2f blue;

    public Primaries(String name, Point2f a, Point2f b, Point2f c) {
        this.name = name;
        red = a;
        green = b;
        blue = c;
    }

    Primaries(String name, Primaries p) {
        this.name = name;
        red = p.red;
        green = p.green;
        blue = p.blue;
    }


    public String toString() {
        return name;
    }

    
}