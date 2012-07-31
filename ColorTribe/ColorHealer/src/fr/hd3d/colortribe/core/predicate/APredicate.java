package fr.hd3d.colortribe.core.predicate;

public abstract class APredicate implements IPredicate {
    protected final IIndexable _indexy;
    public APredicate(IIndexable indexy) {
        _indexy = indexy;
    }
}
