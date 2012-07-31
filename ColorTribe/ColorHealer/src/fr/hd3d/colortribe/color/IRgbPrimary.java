package fr.hd3d.colortribe.color;

/**
 * This interface represents an RGB Primary
 * ie a set of three illuminants associated with a name and/or a comment.
 * @author Guillaume CHATELET
 *
 */
public interface IRgbPrimary {
    public abstract String getName();

    public abstract String getComment();

    public abstract IIlluminant getBlue();

    public abstract IIlluminant getGreen();

    public abstract IIlluminant getRed();
}