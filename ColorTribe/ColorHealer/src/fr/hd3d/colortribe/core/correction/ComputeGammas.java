package fr.hd3d.colortribe.core.correction;

import java.util.List;

import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.type.Point3f;

public class ComputeGammas
{
    private float getXValue(Point3f value)
    {
        // X = x*Y/y
        // value.x -> x, value.y -> y, value.z -> Y
        return value._a * value._c / value._b;
    }

    private float getYValue(Point3f value)
    {
        // Y = Y
        // value.x -> x, value.y -> y, value.z -> Y
        return value._c;
    }

    private float getZValue(Point3f value)
    {
        // Z = (1-x-y)*Y / y
        // value.x -> x, value.y -> y, value.z -> Y
        return (1 - value._a - value._b) * value._c / value._b;
    }
    
    private float computeSAD(List<ColorMeasure> measures, float gamma, float firstValue, float rangeValue, int canal)
    {
        float sad = 0, x, target;
        for (ColorMeasure mes : measures)
        {
            x = mes.getFloatXIndex();
            // target = (float) Math.pow(x, gamma);
            target = firstValue + (float) Math.pow(x, gamma) * rangeValue;
            if (canal == 0)
            {// red
                sad += Math.abs(getXValue(mes.getValue()) - target);
            }
            else if (canal == 1)
            { // green
                sad += Math.abs(getYValue(mes.getValue()) - target);
            }
            else
            {// blue
                sad += Math.abs(getZValue(mes.getValue()) - target);
            }

        }
        return sad;
    }

    public float getMinGamma(List<ColorMeasure> measures, float gamma, float firstValue, float rangeValue, int canal)
    {
        float start = 2f;
        float step = 0.005f;
        float stop = 3.f;
        float minGamma = gamma;
        float minSad = computeSAD(measures, gamma, firstValue, rangeValue, canal);
        float tmpSad;

        float tmpGamma = start;
        while (tmpGamma < stop)
        {
            tmpSad = computeSAD(measures, tmpGamma, firstValue, rangeValue, canal);
            if (tmpSad < minSad)
            {
                minSad = tmpSad;
                minGamma = tmpGamma;
            }
            tmpGamma += step;
        }

        return minGamma;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        

    }

}
