package fr.hd3d.colortribe.color.util;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.type.Point2f;


public class BinarySearchCurve {
    private final SortedMap<Float, Float> points = new TreeMap<Float, Float>();

    public BinarySearchCurve() {
    }

    public BinarySearchCurve(List<ColorMeasure> measures) {
        for (ColorMeasure mes : measures) {
            put(mes.getFloatXIndex(), mes.getValue()._c);
        }
    }

    public BinarySearchCurve(List<Point2f> points, boolean b) {
        for (Point2f point : points) {
            put(point._a, point._b);
        }
    }

    public void put(float x, float y) {
        points.put(x, y);
    }

    public float getValue(float x) {
        if (points.size() < 2)
            throw new IllegalArgumentException();
        final Float exactValue = points.get(x);
        if (exactValue != null)
            return exactValue;
        final Set<Entry<Float, Float>> entrySet = points.entrySet();
        Entry<Float, Float> previousEntry = null;
        for (Entry<Float, Float> entry : entrySet) {
            if (previousEntry != null && entry.getKey() > x) {
                final float alpha = (x - previousEntry.getKey()) / (entry.getKey() - previousEntry.getKey());
                return alpha * entry.getValue() + (1 - alpha) * previousEntry.getValue();
            }
            previousEntry = entry;
        }
        throw new IllegalArgumentException();
    }

    public float binarySearch(final float y, final float epsilon, float xMin, float xMax) {
        float yMin = getValue(xMin);
        float yMax = getValue(xMax);
        while (true) {
            float mid = (xMax + xMin) / 2;
            float midValue = getValue(mid);
            if (y > midValue) {
                xMin = mid;
                yMin = midValue;
            } else {
                xMax = mid;
                yMax = midValue;
            }
            if (yMin == y)
                return xMin;
            if (yMax == y)
                return xMax;
            if (yMax - yMin < epsilon)
                return (xMax + xMin) / 2;
        }
    }

    public void checkMonotonic() {
        Float previousValue = null;
        for (Float x : points.keySet()) {
            if (previousValue != null && x <= previousValue)
                throw new IllegalStateException("Function is not monotonic");
            previousValue = x;
        }
    }
    
 
 
}
