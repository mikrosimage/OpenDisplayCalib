package fr.hd3d.colortribe.color;

import fr.hd3d.colortribe.color.type.Point2f;

public interface IIlluminant {
    public abstract String getComment();

    public abstract Point2f getxyCoordinates();
    public abstract Point2f getuvCoordinates();

    public abstract String getName();
    public abstract int  getValue();
}