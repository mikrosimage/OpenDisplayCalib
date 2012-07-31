package fr.hd3d.colortribe.color.util;

import fr.hd3d.colortribe.color.type.Point2f;



public class Primaries {
    private static final String SECONDARY_PATH = "custom"; //$NON-NLS-1$
    public String name;
    public Point2f red;
    public Point2f green;
    public Point2f blue;

    public Primaries(String name, Point2f a, Point2f b, Point2f c) {
        this.name = name;
        red = a;
        green = b;
        blue = c;
    }

    public Primaries(String name, Primaries p) {
        this.name = name;
        red = p.red;
        green = p.green;
        blue = p.blue;
    }

    public Primaries(Primaries p) {
        this.name = SECONDARY_PATH;
        red = p.red;
        green = p.green;
        blue = p.blue;
    }

    public String toString() {
        return name;
    }

    public boolean sameAs(Primaries o) {
        if (o.red.equals(red) && o.green.equals(green) & o.blue.equals(blue))
            return true;
        else
            return false;
    }
}