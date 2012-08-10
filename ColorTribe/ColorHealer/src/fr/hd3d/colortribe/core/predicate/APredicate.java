package fr.hd3d.colortribe.core.predicate;

abstract class APredicate implements IPredicate {
    protected final IIndexable _indexy;
    APredicate(IIndexable indexy) {
        _indexy = indexy;
    }
}
