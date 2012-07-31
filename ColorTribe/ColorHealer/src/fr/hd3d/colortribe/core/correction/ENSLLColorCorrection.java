package fr.hd3d.colortribe.core.correction;

import java.util.ArrayList;
import java.util.List;

import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;


public class ENSLLColorCorrection extends AbstractCorrection
{

    int _measureSetIndex;
    boolean _alreadyCompute = false;
    private Point3f _foundGamma;
    private Point3f _corrGamma;
    private Point3f _calibratedGamma = null;

    public ENSLLColorCorrection(int measureSetIndex)
    {
        super();
        _measureSetIndex = measureSetIndex;
        _foundGamma = new Point3f(-1, -1, -1);
        _corrGamma = new Point3f(-1, -1, -1);
    }

    public Point3f getComputeGamma()
    {
        return _foundGamma;
    }

    public Point3f getCorrectionGamma()
    {
        return _corrGamma;
    }

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

    @Override
    public void computeColorCorrection()
    {
        if (!_alreadyCompute || _measureSetIndex == -1)
        {
            MeasuresSet samplesSet;
            if (_measureSetIndex == -1)
                samplesSet = ColorHealerModel._instance.getBasicMeasuresSet();
            else
                samplesSet = ColorHealerModel._instance.getMeasuresSet(_measureSetIndex);
            _redCorrection = new ArrayList<Point2f>();
            _blueCorrection = new ArrayList<Point2f>();
            _greenCorrection = new ArrayList<Point2f>();
            float gamma = ColorHealerModel._instance.getTarget().getGamma();
            float firstValue, lastValue;
            // Pour chaque canal
            for (int i = 0; i < 3; i++)
            {
                //
                List<ColorMeasure> measures;
                List<Point2f> whereToAdd;
                if (i == 0)
                {
                    measures = samplesSet.getRedSortedMesures();
                    if(measures.size()==0)
                        continue;
                    // System.out.println("------------red
                    // correction-----------------");
                    whereToAdd = _redCorrection;
                    lastValue = getXValue(measures.get(measures.size() - 1).getValue());
                    firstValue = getXValue(measures.get(0).getValue());
                }
                else if (i == 1)
                {
                    measures = samplesSet.getGreenSortedMesures();
                    if(measures.size()==0)
                        continue;
                    // System.out.println("------------green
                    // correction-----------------");
                    whereToAdd = _greenCorrection;
                    lastValue = getYValue(measures.get(measures.size() - 1).getValue());
                    firstValue = getYValue(measures.get(0).getValue());
                }
                else
                {
                    measures = samplesSet.getBlueSortedMesures();
                    if(measures.size()==0)
                        continue;
                    // System.out.println("------------blue
                    // correction-----------------");
                    whereToAdd = _blueCorrection;
                    lastValue = getZValue(measures.get(measures.size() - 1).getValue());
                    firstValue = getZValue(measures.get(0).getValue());
                }

                float rangeValue = lastValue - firstValue;

                // init
                float minGamma = getMinGamma(measures, gamma, firstValue, rangeValue, i);
                float corrGamma = gamma / minGamma;

                for (int j = 0; j < 256; j++)
                {
                    float x = (j) / 255f;
                    float newX = (float) Math.pow(x, corrGamma);
                    whereToAdd.add(new Point2f(x, newX));
                }

                if (i == 0)
                {
                    _foundGamma._a = minGamma;
                    _corrGamma._a = corrGamma;
                }
                else if (i == 1)
                {
                    _foundGamma._b = minGamma;
                    _corrGamma._b = corrGamma;
                }
                else
                {
                    _foundGamma._c = minGamma;
                    _corrGamma._c = corrGamma;
                }

            }
            _alreadyCompute = true;
        }

    }

    @Override
    public String getSummary()
    {
        Point3f compGamma = getComputeGamma();
        Point3f corrGamma = getCorrectionGamma();
        Point3f calibGamma = getCalibratedGamma();
        String sum = "Correction (measure " + _measureSetIndex + ")\n";
        sum += "Computed RGB Gamma : " + compGamma.clampedToString() + "\n";
        sum += "Correction RGB Gamma : " + corrGamma.clampedToString() + "\n";
        if (calibGamma != null)
            sum += "Calibrated RGB Gamma : " + calibGamma.clampedToString();
        return sum;
    }

    @Override
    public Point3f getDelta()
    {
        float targetGamma = ColorHealerModel._instance.getTarget().getGamma();
        Point3f delta = new Point3f(targetGamma - _foundGamma._a, targetGamma - _foundGamma._b, targetGamma
                - _foundGamma._c);
        return delta;
    }

    @Override
    public void setMeasuredGamma(MeasuresSet set)
    {
        float firstValue, lastValue;
        float gamma = ColorHealerModel._instance.getTarget().getGamma();
        float redGamma = 0, greenGamma = 0, blueGamma = 0;
        for (int i = 0; i < 3; i++)
        {
            //
            List<ColorMeasure> measures;
            if (i == 0)
            {
                measures = set.getRedSortedMesures();
                lastValue = getXValue(measures.get(measures.size() - 1).getValue());
                firstValue = getXValue(measures.get(0).getValue());
            }
            else if (i == 1)
            {
                measures = set.getGreenSortedMesures();
                lastValue = getYValue(measures.get(measures.size() - 1).getValue());
                firstValue = getYValue(measures.get(0).getValue());
            }
            else
            {
                measures = set.getBlueSortedMesures();
                lastValue = getZValue(measures.get(measures.size() - 1).getValue());
                firstValue = getZValue(measures.get(0).getValue());
            }

            float rangeValue = lastValue - firstValue;

            // init
            float minGamma = getMinGamma(measures, gamma, firstValue, rangeValue, i);
            if (i == 0)
            {
                redGamma = minGamma;
            }
            else if (i == 1)
            {
                greenGamma = minGamma;
            }
            else
            {
                blueGamma = minGamma;
            }

        }
        _calibratedGamma = new Point3f(redGamma, greenGamma, blueGamma);
    }

    /**
     * Return validation Delta
     */
    @Override
    public Point3f getCalibratedDelta()
    {
        if (_calibratedGamma == null)
            return null;
        float targetGamma = ColorHealerModel._instance.getTarget().getGamma();
        Point3f delta = new Point3f(targetGamma - _calibratedGamma._a, targetGamma - _calibratedGamma._b, targetGamma
                - _calibratedGamma._c);
        return delta;
    }

    @Override
    public Point3f getCalibratedGamma()
    {
        return _calibratedGamma;
    }

}
