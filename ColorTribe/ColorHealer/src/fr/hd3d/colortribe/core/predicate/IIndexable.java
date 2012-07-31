package fr.hd3d.colortribe.core.predicate;

import fr.hd3d.colortribe.color.type.Point3f;

public interface IIndexable {
    public abstract int size();
    public abstract Point3f get(int index);
}
