package fr.hd3d.colortribe.core.probes;

import com.datacolor.spyder4.Spyder4;

import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.DisplayDevice;
import fr.hd3d.colortribe.core.DisplayDevice.EDisplayDeviceType;


public class Spyder4Probe extends AbstractProbe
{
    private Spyder4 _spydy;
    // private EDisplayDeviceType _deviceType;
    private boolean _isOpen;

    private String _serialNumber = "";
    private int _dllVersion = 0;
    private int _hardWareVersion = 0;

    public Spyder4Probe()
    {
        _isOpen = false;
        _spydy = Spyder4.getInstance();

    }

    public void close()
    {
        if (isOpen())
            try
            {
                _spydy.Shutdown();
            }
            catch (com.datacolor.spyder4.SpyderException e)
            {
                System.err.println(e.getMessage());
            }
        _isOpen = false;
    }

    public EProbeType getEProbeType()
    {
        return EProbeType.SPYDER_4;
    }

    public String getProbeDescription()
    {
        DisplayDevice disp = ColorHealerModel._instance.getDisplayDevice();
        if ((disp.getType() == EDisplayDeviceType.LCD) || (disp.getType() == EDisplayDeviceType.CRT))
            return "Your Spyder 4 is plugged.";
        else
            return "You can't calibrate a projector with this probe !";
    }

    public boolean isAvailable(String comPort) throws Exception
    {

        return _isOpen;
    }

    public static boolean isConnected()
    {
        try
        {
            Spyder4.getInstance().Startup();
            Spyder4.getInstance().Shutdown();
        }
        catch (com.datacolor.spyder4.SpyderException se)
        {
            return false;
        }

        return true;
    }

    public boolean open(String comPort) throws Exception
    {
        try
        {
            int[] VendorData = _spydy.Startup();
            _dllVersion = VendorData[0];
            _hardWareVersion = VendorData[1];
            for (int i = 0; i < 8; i++)
            {
                _serialNumber += VendorData[i + 2];
            }

            _spydy.Autorize(Spyder4.apiAutorizationKey, 1);
            _isOpen = true;
        }
        catch (com.datacolor.spyder4.SpyderException e)
        {
            System.err.println(e.getMessage());
        }

        return true;
    }

    public Point3f readXYZ() throws Exception
    {

        // TODO Measurement Time Recommendations
        // From our testing, we have found measurement (integration) times of 5 seconds
        // per sample to be optimal for measurements below 4 cd/m2, and measurement times of 2 seconds per sample to be
        // optimal for measurements at 4 cd/m2 and above. These measurement times are for typical LCD displays, and may
        // vary with other device types.

        int nFrame = 2 * 60;
        int[/* 3 [x,y,z] */] XYZ = _spydy.GetXYZ(nFrame);
        float Y = XYZ[1] / (float) 1000;
        float x = XYZ[0] / (float) (XYZ[0] + XYZ[1] + XYZ[2]);
        float y = XYZ[1] / (float) (XYZ[0] + XYZ[1] + XYZ[2]);
        return new Point3f(x, y, Y);// fff //ciexyY
    }

    public boolean isOpen()
    {
        return _isOpen;
    }

    public String getSerialInfo()
    {
        // TODO Auto-generated method stub
        return "SN " + _serialNumber + " Dll " + _dllVersion + " HW" + _hardWareVersion;
    }

}
