package fr.hd3d.colortribe.core;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.JOptionPane;

import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;


public class MeasuresSet
{
    private Map<Color, ColorMeasure> _measures = new HashMap<Color, ColorMeasure>();

    public boolean mesureThisColor(MeasuresSet currentMeasuresSet, Color c, String label)
    {
        return mesureThisColor(currentMeasuresSet, c, label,true);
    }

    public boolean mesureThisColor(MeasuresSet currentMeasuresSet, Color c, String label, boolean isPatch)
    {
        try
        {
            // System.out.println("Measure color : " + c);
            ColorMeasureManager._instance.mesurePatch(currentMeasuresSet, c, label, isPatch);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage() + "\nRestart ColorHealer ! ", "Socket error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Probe error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    void addMeasure(ColorMeasure measure)
    {
        _measures.put(measure.getPatchColor(), measure);
        System.out.println("Measure : " + measure + "(" + _measures.size() + ")");
    }

    public ColorMeasure getMeasure(Color patch)
    {
        return _measures.get(patch);
    }

    public List<ColorMeasure> getMeasures()
    {
        return new ArrayList<ColorMeasure>(_measures.values());
    }

    public void clear()
    {
        _measures.clear();
    }

    public enum ETableType
    {
        RED, GREEN, BLUE, ALL
    };

    private boolean isGreen(ColorMeasure measure)
    {
        return (measure.getPatchColor().getBlue() == 0 && measure.getPatchColor().getRed() == 0);
    }

    private boolean isBlue(ColorMeasure measure)
    {
        return (measure.getPatchColor().getRed() == 0 && measure.getPatchColor().getGreen() == 0);
    }

    private boolean isRed(ColorMeasure measure)
    {
        return (measure.getPatchColor().getBlue() == 0 && measure.getPatchColor().getGreen() == 0);
    }

    // TODO check isRed
    public List<ColorMeasure> getRedSortedMesures()
    {
        List<ColorMeasure> red = new ArrayList<ColorMeasure>();
        for (ColorMeasure measure : _measures.values())
        {
            if (isRed(measure))
            {
                int i = 0;
                for (ColorMeasure redMes : red)
                {
                    if (measure.getPatchColor().getRed() < redMes.getPatchColor().getRed())
                    {
                        red.add(i, measure);
                        break;
                    }
                    i++;
                }
                if (i == red.size())
                    red.add(measure);
            }
        }
        return red;
    }

    public List<ColorMeasure> getGreenSortedMesures()
    {
        List<ColorMeasure> green = new ArrayList<ColorMeasure>();
        for (ColorMeasure measure : _measures.values())
        {
            if (isGreen(measure))
            {
                int i = 0;
                for (ColorMeasure greenMes : green)
                {
                    if (measure.getPatchColor().getGreen() < greenMes.getPatchColor().getGreen())
                    {
                        green.add(i, measure);
                        break;
                    }
                    i++;
                }
                if (i == green.size())
                    green.add(measure);
            }
        }
        return green;
    }

    public List<ColorMeasure> getBlueSortedMesures()
    {
        List<ColorMeasure> blue = new ArrayList<ColorMeasure>();
        for (ColorMeasure measure : _measures.values())
        {
            if (isBlue(measure))
            {
                int i = 0;
                for (ColorMeasure blueMes : blue)
                {
                    if (measure.getPatchColor().getBlue() < blueMes.getPatchColor().getBlue())
                    {
                        blue.add(i, measure);
                        break;
                    }
                    i++;
                }
                if (i == blue.size())
                    blue.add(measure);
            }
        }
        return blue;
    }

    public List<Point2f> computeSortedNormalizedDifferentialsPoints(ETableType type, List<Point2f> samples)
    {
        List<ColorMeasure> mesures;
        switch (type)
        {
            case RED:
                mesures = getRedSortedMesures();
                break;
            case BLUE:
                mesures = getBlueSortedMesures();
                break;
            case GREEN:
                mesures = getGreenSortedMesures();
                break;
            default:
                mesures = getMeasures();
        }
        List<Point2f> points = new ArrayList<Point2f>();
        float lum = ColorHealerModel._instance.getTarget().getMaxLum();
        float gamma = ColorHealerModel._instance.getTarget().getGamma();
        Point3f currentPoint3D;
        for (ColorMeasure mes : mesures)
        {
            currentPoint3D = mes.getValue();
            float colorRatio = mes.getFloatXIndex();
            float targetValue = (lum) * (float) Math.pow(colorRatio, gamma);
            float measuredValue = currentPoint3D._c;
            points.add(new Point2f(colorRatio, (measuredValue / targetValue)));
            if (samples != null)
            {
                ListIterator<Point3f> colorSamples = mes.getSamples();
                while (colorSamples.hasNext())
                {
                    Point3f sample = colorSamples.next();
                    samples.add(new Point2f(colorRatio, (sample._c / targetValue)));
                }
            }
        }
        return points;
    }

    public float getYMaxDifferentialsPoints()
    {
        List<ColorMeasure> mesures = getMeasures();
        float ymax = 0;
        float lum = ColorHealerModel._instance.getTarget().getMaxLum();
        float gamma = ColorHealerModel._instance.getTarget().getGamma();
        Point3f currentPoint3D;
        for (ColorMeasure mes : mesures)
        {
            if (mes.getPatchColor().getRGB() != Color.black.getRGB())
            {
                currentPoint3D = mes.getValue();
                float colorRatio = mes.getFloatXIndex();
                float targetValue = lum * (float) Math.pow(colorRatio, gamma);
                float measuredValue = currentPoint3D._c;
                if (ymax < (measuredValue / targetValue))
                    ymax = (measuredValue / targetValue);
            }
        }
        return ymax;
    }

    
    public String htmlToString()
    {
        String dump = "";
        List<ColorMeasure> mesures = getMeasures();
        for (ColorMeasure colorMeasure : mesures)
        {
            dump += colorMeasure.toString() + "     " + colorMeasure.getLabel() + "<br>\n";
        }
        return dump;
    }
    @Override
    public String toString()
    {
        String dump = "";
        List<ColorMeasure> mesures = getMeasures();
        for (ColorMeasure colorMeasure : mesures)
        {
            dump += colorMeasure.toString() + " \t" + colorMeasure.getLabel() + "\n";
        }
        return dump;
    }
}
