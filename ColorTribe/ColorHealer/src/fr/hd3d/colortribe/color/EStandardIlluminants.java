package fr.hd3d.colortribe.color;

import fr.hd3d.colortribe.color.type.Point2f;


/**
 * Defines a set of standard illuminants. You can change the observer angle for the black body temperature coordinates
 * with the <code>
 * daylightIlluminantIs10Degree</code> boolean static variable. As this set is an enum you can get a list of
 * available items.
 * 
 * 
 */
public enum EStandardIlluminants implements IIlluminant
{
    /**
     * Standard daylight illmuniants are computed from the black body illumination under the associated temperature
     */
    // SRC http://en.wikipedia.org/wiki/Standard_illuminant
    D50(5003, getStandardCoordinates("D50"), "Horizon Light"), //
    D55(5503, getStandardCoordinates("D55"), "Mid-morning / Mid-afternoon Daylight"), //
    D65(6504, getStandardCoordinates("D65"), "Noon Daylight"), //
    D75(7504, getStandardCoordinates("D75"), "North sky Daylight"), //
    D93(9300, getBlackBodyCoordinates(9300), "Daylight at 9300°K"), //
    A(2856, getStandardCoordinates("A"), "Incandescent / Tungsten"), //
    B(4874, getStandardCoordinates("B"), " Direct sunlight at noon"), //
    C(6774, getStandardCoordinates("C"), "Average / North sky Daylight"), //
    E(5454, getStandardCoordinates("E"), "Equal energy"), //
    F1(6430, getStandardCoordinates("F1"), "Daylight Fluorescent"), //
    F2(4230, getStandardCoordinates("F2"),//
            "Cool White Fluorescent"), //
    F3(3450, getStandardCoordinates("F3"), "White Fluorescent"), //
    F4(2940, getStandardCoordinates("F4"), "Warm White Fluorescent"), //
    F5(6350, getStandardCoordinates("F5"), "Daylight Fluorescent"), //
    F6(4150, getStandardCoordinates("F6"), "Lite White Fluorescent"), //
    F7(6500, getStandardCoordinates("F7"), "D65 simulator"), //
    F8(5000, getStandardCoordinates("F8"), "D50 simulator"), //
    F9(4150, getStandardCoordinates("F9"), "Cool White Deluxe Fluorescent"), //
    F10(5000, getStandardCoordinates("F10"), "Philips TL85, Ultralume 50"), //
    F11(4000, getStandardCoordinates("F11"), "Philips TL84, Ultralume 40"), //
    F12(3000, getStandardCoordinates("F12"), "Philips TL84, Ultralume 30"),
    DCI((int)getApproximateColorTemperature(new Point2f(0.314f, 0.351f)),new Point2f(0.314f, 0.351f), "DCI");
    /**
     * change this to true if you want to use the black body color under a 10 degrees observation angle instead of 2
     * degrees.
     */
    private static final boolean daylightIlluminantIs10Degree = false;// mis à

    public static final Point2f getStandardCoordinates(String value)
    {
        if (value.compareTo("D50") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.34567f, 0.35850f);
            else
                return new Point2f(0.34773f, 0.35952f);
        }
        else if (value.compareTo("D55") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.33242f, 0.34743f);
            else
                return new Point2f(0.33411f, 0.34877f);
        }
        else if (value.compareTo("D65") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.31271f, 0.32902f);
            else
                return new Point2f(0.31382f, 0.33100f);
        }
        else if (value.compareTo("D75") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.29902f, 0.31485f);
            else
                return new Point2f(0.29968f, 0.31740f);
        }
        else if (value.compareTo("A") == 0)
        {

            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.44757f, 0.40745f);
            else
                return new Point2f(0.45117f, 0.40594f);
        }
        else if (value.compareTo("B") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.34842f, 0.35161f);
            else
                return new Point2f(0.3498f, 0.3527f);
        }
        else if (value.compareTo("C") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.31006f, 0.31616f);
            else
                return new Point2f(0.31039f, 0.31905f);
        }
        else if (value.compareTo("E") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.33333f, 0.33333f);
            else
                return new Point2f(0.33333f, 0.33333f);
        }
        else if (value.compareTo("F1") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.31310f, 0.33727f);
            else
                return new Point2f(0.31811f, 0.33559f);
        }
        else if (value.compareTo("F2") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.37208f, 0.37529f);
            else
                return new Point2f(0.37925f, 0.36733f);
        }
        else if (value.compareTo("F3") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.40910f, 0.39430f);
            else
                return new Point2f(0.41761f, 0.38324f);
        }
        else if (value.compareTo("F4") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.44018f, 0.40329f);
            else
                return new Point2f(0.44920f, 0.39074f);
        }
        else if (value.compareTo("F5") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.31379f, 0.34531f);
            else
                return new Point2f(0.31975f, 0.34246f);
        }
        else if (value.compareTo("F6") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.37790f, 0.38835f);
            else
                return new Point2f(0.38660f, 0.37847f);
        }
        else if (value.compareTo("F7") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.31292f, 0.32933f);
            else
                return new Point2f(0.31569f, 0.32960f);
        }
        else if (value.compareTo("F8") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.34588f, 0.35875f);
            else
                return new Point2f(0.34902f, 0.35939f);
        }
        else if (value.compareTo("F9") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.37417f, 0.37281f);
            else
                return new Point2f(0.37829f, 0.37045f);
        }
        else if (value.compareTo("F10") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.34609f, 0.35986f);
            else
                return new Point2f(0.35090f, 0.35444f);
        }
        else if (value.compareTo("F11") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.38052f, 0.37713f);
            else
                return new Point2f(0.38541f, 0.37123f);
        }
        else if (value.compareTo("F12") == 0)
        {
            if (!daylightIlluminantIs10Degree)
                return new Point2f(0.43695f, 0.40441f);
            else
                return new Point2f(0.44256f, 0.39717f);
        }
        return null;
    }

    public static final Point2f getBlackBodyCoordinates(float temperature)
    {
        if (daylightIlluminantIs10Degree)
            return PlanckianLocus.getBlackBodyCoordinatesAt10Degrees(temperature);
        else
            return PlanckianLocus.getBlackBodyCoordinatesAt2Degrees(temperature);
    }

    public static final float getApproximateColorTemperature(Point2f blackBody)
    {
        float lowerBound = 1000;
        float upperBound = 10000;
        float meanValue;
        Point2f currentBlackBody;
        float distance;
        int count = 0;
        do
        {
            meanValue = (lowerBound + upperBound) / 2;
            currentBlackBody = getBlackBodyCoordinates(meanValue);
            if (currentBlackBody._a > blackBody._a)
            {
                lowerBound = meanValue;
            }
            else
            {
                upperBound = meanValue;
            }

            distance = (float) Math.sqrt((currentBlackBody._a - blackBody._a) * (currentBlackBody._a - blackBody._a));
            count++;
        }
        while (distance > 0.000001 && count < 20);

        return ((int) (meanValue / 10)) * 10;
    }

    private final IIlluminant illuminant;

    private EStandardIlluminants(int value, Point2f coordinates, String comment)
    {
        illuminant = new Illuminant(value, toString(), coordinates, comment);
    }

    private EStandardIlluminants(int value, Point2f coordinates)
    {
        illuminant = new Illuminant(value, toString(), coordinates);
    }

    public String getComment()
    {
        return illuminant.getComment();
    }

    public Point2f getxyCoordinates()
    {
        return illuminant.getxyCoordinates();
    }

    public Point2f getuvCoordinates()
    {
        return illuminant.getuvCoordinates();
    }

    public String getName()
    {
        return illuminant.getName();
    }

    public int getValue()
    {
        return illuminant.getValue();
    }

    @Override
    public String toString()
    {

        return super.toString();
    }

}
