package fr.hd3d.colortribe.color;

/**
 * Builds a representation of a set of RGB primary colors.
 * If you need predefined Primaries look at <code>EStandardRgbPrimaries</code>.
 * @author Guillaume CHATELET
 */
public class RgbPrimaries implements IRgbPrimary {
    private final String name;
    private final IIlluminant red, green, blue;
    private final String comment;

    public RgbPrimaries(String name, IIlluminant red, IIlluminant green, IIlluminant blue, String comment) {
        this.name = name;
        this.comment = comment;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RgbPrimaries(String name, IIlluminant red, IIlluminant green, IIlluminant blue) {
        this(name, red, green, blue, "");
    }

    /* (non-Javadoc)
     * @see com.max.colour.IPrimary#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.max.colour.IPrimary#getComment()
     */
    public String getComment() {
        return comment;
    }

    /* (non-Javadoc)
     * @see com.max.colour.IPrimary#getBlue()
     */
    public IIlluminant getBlue() {
        return blue;
    }

    /* (non-Javadoc)
     * @see com.max.colour.IPrimary#getGreen()
     */
    public IIlluminant getGreen() {
        return green;
    }

    /* (non-Javadoc)
     * @see com.max.colour.IPrimary#getRed()
     */
    public IIlluminant getRed() {
        return red;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
}
