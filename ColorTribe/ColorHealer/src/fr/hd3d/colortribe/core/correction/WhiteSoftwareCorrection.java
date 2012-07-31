package fr.hd3d.colortribe.core.correction;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.hd3d.colortribe.color.Formulas;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.color.util.ColorMatrix;
import fr.hd3d.colortribe.color.util.Primaries;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.target.ITarget;


public class WhiteSoftwareCorrection
{
    private int _nbBitsEntries = 8;
    private int _nbBitsValues = 16;
    // nombre de valeur de la LUT (ex : 256)
    private int nbEntry = (int) Math.pow(2, _nbBitsEntries);
    // valeur max des pixels values (ex : 65535)
    private int maxValue = (int) Math.pow(2, _nbBitsValues) - 1;

    private int _redMaxValue;
    private int _greenMaxValue;
    private int _blueMaxValue;

    public WhiteSoftwareCorrection(Point3f mesWhite, Point3f mesRed, Point3f mesGreen, Point3f mesBlue)
    {

        ITarget target = ColorHealerModel._instance.getTarget();
        // blanc de reférence
        Point2f targetColorTemp = target.getColorTemp().getxyCoordinates();
        IRgbPrimary targetPrimaries =  target.getPrimaries();
        // /
        Point2f targetWhite = new Point2f(targetColorTemp._a, targetColorTemp._b);
        // blanc mesuré
        Point2f screenWhite = new Point2f(mesWhite._a, mesWhite._b);

        // conversion du blanc mesuré en XYZ
        Point3f screen_xyY = new Point3f(screenWhite._a, screenWhite._b, maxValue * mesWhite._c / target.getMaxLum()); // pourrait
                                                                                                                                // être
                                                                                                                                // juste
                                                                                                                                // 65535
        Point3f screen_XYZ = Formulas.convertCIExyYtoCIEXYZ(screen_xyY);

        // conversion du blanc de ref en XYZ
        Point3f target_xyY = new Point3f(targetWhite._a, targetWhite._b, maxValue);
        Point3f target_XYZ = Formulas.convertCIExyYtoCIEXYZ(target_xyY);

        // primaires mesurées
        Point2f red = new Point2f(mesRed._a, mesRed._b);
        Point2f green = new Point2f(mesGreen._a, mesGreen._b);
        Point2f blue = new Point2f(mesBlue._a, mesBlue._b);
        Primaries screenPrim = new Primaries("primM", red, green, blue);

        // primaires de reference

        Point2f targetRed = targetPrimaries.getRed().getxyCoordinates();
        Point2f targetGreen = targetPrimaries.getGreen().getxyCoordinates();
        Point2f targetBlue = targetPrimaries.getBlue().getxyCoordinates();
        Primaries recPrim = new Primaries("primM", targetRed, targetGreen, targetBlue);

        // matrice de conversion de l'écran
        ColorMatrix screenMatrix = new ColorMatrix();
        screenMatrix.setup_CIEXYZtoRGB(screenPrim, screenWhite);

        // matrice de conversion de reférence
        ColorMatrix recMatrix = new ColorMatrix();
        recMatrix.setup_CIEXYZtoRGB(recPrim, screenWhite);


        Point3f resScreen = new Point3f(maxValue + 1, maxValue + 1, maxValue + 1);
        int currentY = maxValue + 1;
        while ((int) resScreen._a > maxValue || (int) resScreen._b > maxValue || (int) resScreen._c > maxValue)
        {
            --currentY;

            screen_xyY = new Point3f(screenWhite._a, screenWhite._b, currentY);
            screen_XYZ = Formulas.convertCIExyYtoCIEXYZ(screen_xyY);

            resScreen = screenMatrix.transform(screen_XYZ._a, screen_XYZ._b, screen_XYZ._c);
            // System.out.println(" screen " + res);
            //			

        }
        Point3f resRec = new Point3f(maxValue + 1, maxValue + 1, maxValue + 1);
        currentY = maxValue + 1;
        while ((int) resRec._a > maxValue || (int) resRec._b > maxValue || (int) resRec._c > maxValue)
        {
            --currentY;

            // System.out.println(" screen " + res);
            //			

            target_xyY = new Point3f(targetWhite._a, targetWhite._b, currentY);
            target_XYZ = Formulas.convertCIExyYtoCIEXYZ(target_xyY);

            resRec = recMatrix.transform(target_XYZ._a, target_XYZ._b, target_XYZ._c);

            // System.out.println(" rec " + resRec);
        }

        _redMaxValue = (int) ((resRec._a + resScreen._a) / 2f + 0.5f);
        _greenMaxValue = (int) ((resRec._b + resScreen._b) / 2f + 0.5f);
        _blueMaxValue = (int) ((resRec._c + resScreen._c) / 2f + 0.5f);

    }

    public WhiteSoftwareCorrection(int redMaxValue, int greenMaxValue, int blueMaxValue)
    {
        _redMaxValue = redMaxValue;
        _greenMaxValue = greenMaxValue;
        _blueMaxValue = blueMaxValue;
        
    }

    public int getRedMaxValue()
    {
        return _redMaxValue;
    }

    public int getGreenMaxValue()
    {
        return _greenMaxValue;
    }

    public int getBlueMaxValue()
    {
        return _blueMaxValue;
    }

    public float getValue(int chan, float x)
    {
        if (chan == 0)
        {
            return (x * _redMaxValue) / (float) maxValue;
        }
        else if (chan == 1)
        {
            return (x * _greenMaxValue) / (float) maxValue;
        }
        else
        {
            return (x * _blueMaxValue) / (float) maxValue;
        }
    }

    public void displayLUT()
    {
        for (int i = 0; i < nbEntry; i++)
        {
            int rValue = i * _redMaxValue / (nbEntry - 1);
            int gValue = i * _greenMaxValue / (nbEntry - 1);
            int bValue = i * _blueMaxValue / (nbEntry - 1);
            System.out.println(i + "\t" + rValue + "\t" + gValue + "\t" + bValue);
        }
    }

    public boolean sendPreviewLUT()
    {
        try
        {
            List<Point2f> red = new ArrayList<Point2f>();
            List<Point2f> green = new ArrayList<Point2f>();
            List<Point2f> blue = new ArrayList<Point2f>();
            for (int i = 0; i < 256; i++)
            {
                float x = i / 255f;
                red.add(new Point2f(x, getValue(0, x)));
                green.add(new Point2f(x, getValue(1, x)));
                blue.add(new Point2f(x, getValue(2, x)));
            }
            boolean ret =  ColorHealerModel._instance.getSocketServer().sendLut(red, green, blue, false);
            ColorHealerModel._instance.getSocketServer().displayColor(Color.white, false);
            return ret;
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

}
