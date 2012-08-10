package fr.hd3d.colortribe.color.util;

import java.awt.Color;

import fr.hd3d.colortribe.color.type.Point2f;

public class ColorMath {

    public static final float interpolate(float a, float b, float alpha) {
        return (1 - alpha) * a + alpha * b;
    }
    
    static public Color floatColorToUCharColor(float r, float g, float b) {
        return new Color((int) (255 * r + .5f), (int) (255 * g + .5f), (int) (255 * b + .5f));
    }

    static public int[] floatColorToUShortColor(float r, float g, float b) {
        return new int[] { (int) (65535 * r + .5f), (int) (65535 * g + .5f), (int) (65535 * b + .5f) };
    }
    
    static public Point2f xyToupvp(Point2f xy){
        return xyToupvp(xy._a, xy._b);
    }
    static public Point2f xyToupvp(float x, float y){
        //u' = 4x/(-2x + 12y +3)
        float up = 4*x / (-2*x + 12*y + 3);
        //v' = 9y/(-2x + 12y +3)
        float vp = 9*y / (-2*x + 12*y + 3);
        return new Point2f(up, vp);
    }
}
