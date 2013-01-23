/*
 * Created on 10 mai 07
 * 
 * CurveTestProbe.java
 * 
 * Guillaume CHATELET Copyright MikrosImage (C)
 */
package fr.hd3d.colortribe.core.probes;

import java.awt.Color;

import fr.hd3d.colortribe.color.Formulas;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.color.util.InterpolatedCurve;
import fr.hd3d.colortribe.core.ColorHealerModel;


class FakeNoisyProbe extends AbstractProbe
{

    private boolean open;
    private final InterpolatedCurve noiseCurve = new InterpolatedCurve();
    private final InterpolatedCurve curveRed = new InterpolatedCurve();
    private final InterpolatedCurve curveGreen = new InterpolatedCurve();
    private final InterpolatedCurve curveBlue = new InterpolatedCurve();
    private final float white = 80;
    private final float black = 0.2f;

    public FakeNoisyProbe()
    {
        open = false;
        noiseCurve.put(0, 0.5f);
        noiseCurve.put(0.2f, 0.2f);
        noiseCurve.put(1, .5f);
        curveRed.put(0, 0);
        curveRed.put(1, 1);
        curveGreen.put(0, 0);
        curveGreen.put(1, 1);
        curveBlue.put(0, 0);
        curveBlue.put(1, 1);
    }

    public void close()
    {
        open = false;
    }

    public boolean isAvailable(String comPort) throws Exception
    {
        return true;
    }

    public boolean open(String comPort) throws Exception
    {
        open = true;
        return open;
    }

    public Point3f readXYZ() throws Exception
    {
        Thread.sleep(5);
        final Color forecastedColor =  ColorHealerModel._instance.getSocketServer().getCurrentPatchColor();
        final float rgb[] = forecastedColor.getRGBColorComponents(null);
        final float red = curveRed.getValue(rgb[0]);
        final float green = curveGreen.getValue(rgb[1]);
        final float blue = curveBlue.getValue(rgb[2]);
        final Point3f RGB = new Point3f(compensateValue(red), compensateValue(green), compensateValue(blue));
        final Point3f XYZ = Formulas.convertRGB_D65toXYZ(RGB);
        return Formulas.convertCIEXYZtoCIExyY(XYZ);
    }
    private static boolean pouet = false;
    private float compensateValue(float val)
    {
        float addOn;
        if(pouet)
            addOn = 0.1f;
        else 
            addOn = -0.1f;
        pouet = !pouet;
        final float gammaCoeff = (float) Math.random() * addOn + 2.2f;// gamma always > or < to target 2.2
//        System.out.println("fake gamma " + gammaCoeff);
        final float gamma = (float) Math.pow(val, gammaCoeff);
        final float toLum = (white - black) * gamma + black;
        final float noiseCoeff = 3.2f;//noiseCurve.getValue(val);// =0 -> no noise
        final float noised = toLum + (float) (noiseCoeff * Math.random());
        return noised;
    }

    

    public EProbeType getEProbeType()
    {
        return EProbeType.TEST_CURVE_PROBE;
    }

    public String getProbeDescription()
    {
        return "Test probe with defined curves and noise parameters";
    }

    static boolean isConnected()
    {
        return true;
    }

    public boolean isOpen()
    {
        return true;
    }

    public String getSerialInfo()
    {
        return "Serial killer";
    }

	public boolean isSpecificCalibrationRequired() {
		return false;
	}
}
