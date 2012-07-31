package fr.hd3d.colortribe.color;


import java.awt.Color;
import java.util.ListIterator;

import fr.hd3d.colortribe.color.recycle.ColorSet;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.predicate.IIndexable;


/**
 * A color mesure is a set of xyY samples corresponding to a RGB triplet.
 * 
 * @author mfe
 * 
 */
public class ColorMeasure implements Comparable<ColorMeasure>, IIndexable {
    private Color _patchColor;
    private ColorSet _samples;
    private String _label;

    public ColorMeasure(Color patchColor, String label) {
        _patchColor = patchColor;
        _samples = new ColorSet(EColorSpace.CIExyY);
        _label = label;
    }

    public String getLabel(){
        return _label;
    }

    public float getFloatXIndex() {
        if ((_patchColor.getRed() > 0) && (_patchColor.getBlue() == 0) && (_patchColor.getGreen() == 0)) {
            return _patchColor.getRed() / 255f;
        } else if ((_patchColor.getBlue() > 0) && (_patchColor.getRed() == 0) && (_patchColor.getGreen() == 0)) {
            return _patchColor.getBlue() / 255f;
        } else if ((_patchColor.getGreen() > 0) && (_patchColor.getBlue() == 0) && (_patchColor.getRed() == 0)) {
            return _patchColor.getGreen() / 255f;
        } else if (_patchColor.getGreen() == _patchColor.getBlue() && _patchColor.getRed() == _patchColor.getBlue()) {
            return _patchColor.getRed() / 255f;
        } else {
            throw new IllegalStateException("This method is available only for \"pure\" colors !");
        }
    }
   
    public void addSample(Point3f sample) {
        _samples.add(sample);
    }

    public ListIterator<Point3f> getSamples() {
        return _samples.listIterator();
    }

    /**
     * mean of samples
     * 
     * @return
     */
    public Point3f getValue() {
        Point3f mean = new Point3f(0,0,0);
        for (Point3f pt : _samples) {
            mean._a += pt._a;
            mean._b += pt._b;
            mean._c += pt._c;
        }
        int sampleCount = size();
        mean._a /= sampleCount;
        mean._b /= sampleCount;
        mean._c /= sampleCount;
        return mean;
    }

    public Point3f get(int index) {
        return _samples.get(index);
    }

    public int size() {
        return _samples.size();
    }

    public Color getPatchColor() {
        return _patchColor;
    };

    public String toString() {
        Point3f value = getValue();
        value._a = ((int)(value._a *1000))/1000f;
        value._b = ((int)(value._b *1000))/1000f;
        value._c = ((int)(value._c *1000))/1000f;
        return "["+_patchColor.getRed() + " " + _patchColor.getGreen() + " " + _patchColor.getBlue() + "]\t" + value._a + "\t" + value._b + "\t" + value._c ;
    }

    public int compareTo(ColorMeasure o) {
        //TODO rewrite this func
        if (getValue()._c == o.getValue()._c)
            return 0;
        else if (getValue()._c > o.getValue()._c)
            return 1;
        else
            return -1;
    }
    
   
 
}
