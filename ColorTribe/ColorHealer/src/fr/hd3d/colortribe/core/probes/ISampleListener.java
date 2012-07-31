package fr.hd3d.colortribe.core.probes;

import fr.hd3d.colortribe.color.type.Point3f;

public interface ISampleListener {
    public void measureDone(Point3f sample);
}
