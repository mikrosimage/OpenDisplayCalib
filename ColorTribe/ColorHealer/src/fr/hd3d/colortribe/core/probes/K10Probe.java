package fr.hd3d.colortribe.core.probes;

import com.klein.k10.JK10;

import fr.hd3d.colortribe.color.type.Point3f;


public class K10Probe extends AbstractProbe
{
    private String _serialNumber = "";
    private static String s_port = "";
    
    /////////////
    //FIXME patch faire un truc propre
    public static String manualPort = null;
    public static boolean useManualPort = false;
    //////

    private boolean _isOpen = false;

    public void close()
    {
        JK10.release();
        _isOpen = false;
    }

    public EProbeType getEProbeType()
    {
        return EProbeType.K10;
    }

    public String getProbeDescription()
    {
        return "K-10 is ready";
    }

    public String getSerialInfo()
    {
        return _serialNumber;
    }

    public boolean isAvailable(String comPort) throws Exception
    {
        return isOpen();
    }

    static boolean isConnected()
    {
        String res = JK10.isConnected();
        System.out.println("JK10 " + res);
        
        
        if (res.compareTo("NOT_FOUND") == 0)
        {
            if(useManualPort){
                s_port = manualPort;
                return true;
            }
            s_port = "";
            return false;
        }
        else
        {
            s_port = res;
            return true;
        }
    }

    public boolean isOpen()
    {

        return _isOpen;
    }

    public boolean open(String comPort) throws Exception
    {
        ///////FIXME patch !!
       
        
        if (s_port.length() == 0)
        {
            return false;

        }
        else
        {
            _serialNumber = JK10.init(s_port);
            _isOpen = true;
            return true;
        }
    }

    public Point3f readXYZ() throws Exception
    {
        float[/* 3 [x,y,z] */] XYZ = JK10.getXYZ();
        float Y = XYZ[1];
        float x = XYZ[0] / (XYZ[0] + XYZ[1] + XYZ[2]);
        float y = XYZ[1] / (XYZ[0] + XYZ[1] + XYZ[2]);
        return new Point3f(x, y, Y);// fff //ciexyY
    }


}
