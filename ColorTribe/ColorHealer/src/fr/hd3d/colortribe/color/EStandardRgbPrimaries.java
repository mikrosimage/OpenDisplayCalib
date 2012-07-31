package fr.hd3d.colortribe.color;

import fr.hd3d.colortribe.color.type.Point2f;


/**
 * Defines a set of standard RGB Primaries. Each item is a IRgbPrimary so you can get useful informations about them.
 * see <code>IRgbPrimary</code> interface. As this set is an enum you can get a list of available items.
 * 
 */
public enum EStandardRgbPrimaries implements IRgbPrimary
{
    REC709(0.640f, 0.330f, 0.300f, 0.600f, 0.150f, 0.060f), //
    SMPTE_HD(REC709, "Same as Rec709 primaries"), // same as REC709
    EBU(0.640f, 0.330f, 0.290f, 0.600f, 0.150f, 0.060f, "1969"), // 1969
    SMPTE_C(0.635f, 0.340f, 0.305f, 0.595f, 0.155f, 0.070f, "NTSC 1979"), // NTSC 1979
    SMPTE_240M(0.630f, 0.340f, 0.310f, 0.595f, 0.155f, 0.070f, "old analog HD 1125/1250"), // old analog HD 1125/1250
    ORIGINAL_NTSC(0.670f, 0.330f, 0.210f, 0.710f, 0.140f, 0.080f, "old NTSC 1953"), // old NTSC
    COMMON_COMPUTER(0.628f, 0.346f, 0.268f, 0.588f, 0.150f, 0.070f), //
    YIQ(ORIGINAL_NTSC, "same as ORIGINAL_NTSC's"), // old NTSC same as ORIGINAL_NTSC 1953
    TRINITRON(0.625f, 0.340f, 0.280f, 0.595f, 0.155f, 0.070f), //
    APPLE_RGB(TRINITRON, "same as Trinitron's (expect gamma 1.8)"), // expecting gamma of 1.8
    sRGB(REC709, "same as REC709 primaries"), // same as REC709
    ADOBE_RGB(0.640f, 0.330f, 0.210f, 0.710f, 0.150f, 0.060f, "1998"), // 1998
    WIDE_GAMMUT_RGB(0.735f, 0.265f, 0.115f, 0.826f, 0.157f, 0.018f), //
    LCD(0.516f, 0.332f, 0.315f, 0.506f, 0.184f, 0.182f),//
    DCI(0.680f, 0.320f, 0.265f, 0.690f, 0.150f, 0.060f); //
    private final IRgbPrimary rgbPrimary;

    private EStandardRgbPrimaries(float xRed, float yRed, float xGreen, float yGreen, float xBlue, float yBlue,
            String comment)
    {
        final IIlluminant red = new Illuminant(0, toString() + "_red", new Point2f(xRed, yRed));
        final IIlluminant green = new Illuminant(0, toString() + "_green", new Point2f(xGreen, yGreen));
        final IIlluminant blue = new Illuminant(0, toString() + "_blue", new Point2f(xBlue, yBlue));
        rgbPrimary = new RgbPrimaries(toString(), red, green, blue, comment);
    }

    private EStandardRgbPrimaries(float xRed, float yRed, float xGreen, float yGreen, float xBlue, float yBlue)
    {
        this(xRed, yRed, xGreen, yGreen, xBlue, yBlue, "");
    }

    private EStandardRgbPrimaries(EStandardRgbPrimaries prim, String comment)
    {
        final IIlluminant red = prim.getRed();
        final IIlluminant green = prim.getGreen();
        final IIlluminant blue = prim.getBlue();
        rgbPrimary = new RgbPrimaries(toString(), red, green, blue, comment);
    }

    public IIlluminant getBlue()
    {
        return rgbPrimary.getBlue();
    }

    public String getComment()
    {
        return rgbPrimary.getComment();
    }

    public IIlluminant getGreen()
    {
        return rgbPrimary.getGreen();
    }

    public String getName()
    {
        return rgbPrimary.getName();
    }

    public IIlluminant getRed()
    {
        return rgbPrimary.getRed();
    }

    @Override
    public String toString()
    {
        return super.toString();
    }
}
