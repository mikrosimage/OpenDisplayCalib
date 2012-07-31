package fr.hd3d.colortribe.color.recycle;



import java.util.ArrayList;
import java.util.Iterator;

import fr.hd3d.colortribe.color.EColorSpace;
import fr.hd3d.colortribe.color.type.Point3f;


/**
 * A ColorSet is a Collection of Point3D in a particular color space.
 * @author Guillaume CHATELET
 */
public class ColorSet extends ArrayList<Point3f> implements IColorConvertibleSetProvider {
    private static final long serialVersionUID = 3216833815824350405L;
    private EColorSpace colorSpace = EColorSpace.UNDEFINED;

    public ColorSet(EColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }

    public ColorSet(final ColorSet other) {
        this.addAll(other);
        colorSpace = other.colorSpace;
    }

    public EColorSpace getColorSpace() {
        return colorSpace;
    }

    void setColorSpace(EColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }

    /**
     * Return a linearly interpolated value
     * This function will return a new Point3D
     * contrary to get(int) which returns the real point
     * stored in the list.
     * @param index
     * @return
     */
    public Point3f get(float index) {
        final int floor = (int) index;
        final Point3f floorPoint = super.get(floor);
        if (index == floor)
            return floorPoint;
        final float alpha = index - floor;
        return new Point3f(floorPoint).interpolate(super.get(floor + 1), alpha);
    }

    @Override
    public String toString() {
        return "Colour set in " + colorSpace.toString() + " colorspace (" + size() + " elements)";
    }

    @Override
    public ColorSet clone() {
        return new ColorSet(this);
    }

//    @Override
//    public int hashCode() {
//        int aSeed = HashCodeUtils.SEED;
//        HashCodeUtils.hash(aSeed, colorSpace);
//        HashCodeUtils.hash(aSeed, super.hashCode());
//        return aSeed;
//    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o instanceof ColorSet == false)
            return false;
        final ColorSet other = (ColorSet) o;
        return colorSpace.equals(other.getColorSpace()) && super.equals(other);
    }

    public IColorConvertibleSet getColorConvertibleSet() {
        return new IColorConvertibleSet() {
            public EColorSpace getColorSpace() {
                return ColorSet.this.getColorSpace();
            }

            public void setColorSpace(EColorSpace colorSpace) {
                ColorSet.this.setColorSpace(colorSpace);
            }

            public Iterator<Point3f> iterator() {
                return ColorSet.this.iterator();
            }
        };
    }
}
