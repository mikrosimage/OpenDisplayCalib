package fr.hd3d.colortribe.core.predicate;

/**
 * Use a number of iteration
 * 
 * @author mfe
 * 
 */
public class CountValuesPredicate extends APredicate {
    private int _maxCount = 1;

    public CountValuesPredicate(IIndexable indexable, int maxCount) {
        super(indexable);
        _maxCount = maxCount;
    }

    public boolean isDone() {
        return _indexy.size() == _maxCount;
    }
}
