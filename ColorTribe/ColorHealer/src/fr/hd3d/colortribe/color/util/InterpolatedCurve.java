package fr.hd3d.colortribe.color.util;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class InterpolatedCurve {
    private final SortedMap<Float, Float> points = new TreeMap<Float, Float>();

    public InterpolatedCurve() {
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

    
 
 
}
