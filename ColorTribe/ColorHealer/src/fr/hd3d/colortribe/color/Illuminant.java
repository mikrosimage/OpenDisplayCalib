package fr.hd3d.colortribe.color;

import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.util.ColorMath;


/**
 * This class is intented to provide informations about illuminants. If you need predefined illuminants look at
 * <code>EStandardIlluminants</code>.
 * 
 * @author Guillaume CHATELET
 */
public class Illuminant implements IIlluminant
{
    private final String name;
    private final Point2f xyCoordinates;
    private final int value;
    private final String comment;

    public Illuminant(int value, String name, Point2f coordinates, String comment)
    {
        this.value = value;
        this.name = name;
        this.comment = comment;
        this.xyCoordinates = coordinates;
    }

    public Illuminant(int value, String name, Point2f coordinates)
    {
        this(value, name, coordinates, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.max.colour.IIlluminant#getComment()
     */
    public String getComment()
    {
        return comment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.max.colour.IIlluminant#getCoordinates()
     */
    public Point2f getxyCoordinates()
    {
        return (Point2f) xyCoordinates.clone();
    }
    
    public Point2f getuvCoordinates()
    {
       return ColorMath.xyToupvp(xyCoordinates);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.max.colour.IIlluminant#getName()
     */
    public String getName()
    {
        return name;
    }
    public int getValue()
    {
        return value;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
}
