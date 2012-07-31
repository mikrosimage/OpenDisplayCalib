package fr.hd3d.colortribe.core.correction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.color.util.ColorMath;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;


abstract public class AbstractCorrection
{

    public final static int LUT_MAX_VALUE = 255;
    protected List<Point2f> _redCorrection = null;
    protected List<Point2f> _greenCorrection = null;
    protected List<Point2f> _blueCorrection = null;
    public List<Point2f> _tmpPoints;

    public AbstractCorrection()
    {

    }

    abstract public void computeColorCorrection();

    abstract public Point3f getDelta();

    abstract public Point3f getCalibratedDelta();

    abstract public Point3f getComputeGamma();

    abstract public void setMeasuredGamma(MeasuresSet set);

    abstract public Point3f getCalibratedGamma();

    public boolean sendLut()
    {
        if (_redCorrection != null)
            try
            {
                return ColorHealerModel._instance.getSocketServer().sendLut(_redCorrection, _greenCorrection,
                        _blueCorrection, true);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        return false;
    }

    public void saveLut(String file)
    {
        BufferedWriter sortie;
        try
        {
            sortie = new BufferedWriter(new FileWriter(file));
            sortie.write("");
            int size = _redCorrection.size();
            for (int i = 0; i < size; i++)
            {
                int[] color = ColorMath.floatColorToUShortColor(_redCorrection.get(i)._b, _greenCorrection.get(i)._b,
                        _blueCorrection.get(i)._b);
                String s = i + "\t" + color[0] + "\t" + color[1] + "\t" + color[2] + "\n";
                sortie.append(s);
            }
            sortie.close();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    public void saveLut(BufferedWriter sortie)
    {
        try
        {
            int size = _redCorrection.size();
            for (int i = 0; i < size; i++)
            {
                int[] color = ColorMath.floatColorToUShortColor(_redCorrection.get(i)._b, _greenCorrection.get(i)._b,
                        _blueCorrection.get(i)._b);
                String s = i + "\t" + color[0] + "\t" + color[1] + "\t" + color[2] + "\n";
                sortie.append(s);
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    public List<Point2f> getRedCorrection()
    {
        return _redCorrection;
    }

    public List<Point2f> getBlueCorrection()
    {
        return _blueCorrection;
    }

    public List<Point2f> getGreenCorrection()
    {
        return _greenCorrection;
    }

    abstract public String getSummary();
}
