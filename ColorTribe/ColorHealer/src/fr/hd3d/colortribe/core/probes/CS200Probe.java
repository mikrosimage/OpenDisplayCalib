package fr.hd3d.colortribe.core.probes;

import com.konicaminolta.cs200.CS200;
import com.konicaminolta.cs200.CS200Exception;

import fr.hd3d.colortribe.color.type.Point3f;


public class CS200Probe extends AbstractProbe
{
    private CS200 _cs200;
    // private EDisplayDeviceType _deviceType;
    private boolean _isOpen;
    private String _serialNumber;
    private int _originalSync;
    private int _originalFrequency;
    private int _currentFrequency = -1;
    private boolean _wasFrequencySet = false;
    private int _originalSpeedMode;
    private int _originalSpeedDuration;
    private int _currentSpeedDuration = -1;
    private boolean _wasSpeedSet = false;

    public CS200Probe()
    {
        _isOpen = false;
        _cs200 = CS200.getInstance();
        _serialNumber = "unset"; // TODO
    }

    public void close()
    {
        _isOpen = false;
        try
        {
            if (_wasFrequencySet)
                setSyncAndFrequency(_originalSync, _originalFrequency);
            if (_wasSpeedSet)
                setSpeed(_originalSpeedMode, _originalSpeedDuration);
            _cs200.closeMeasurement();
        }
        catch (CS200Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public EProbeType getEProbeType()
    {
        return EProbeType.MK_CS200;
    }

    public String getProbeDescription()
    {
        return "CS200 is plugged, you can set up a different frequency :";
    }

    public boolean isAvailable(String comPort) throws Exception
    {
        return _cs200.isConnected();
    }

    public static boolean isConnected()
    {

        try
        {
            if (CS200.getInstance().isConnected())
            {
                return true;

            }
        }
        catch (CS200Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public void setSyncAndFrequency(int sync, int frequency) throws Exception
    {
        if ((sync == 0 || sync == 1) && (frequency >= 4000 && frequency <= 20000) && (frequency != _currentFrequency))
        {
            _cs200.setSyncAndFrequency(sync, frequency);
            _currentFrequency = frequency;
            _wasFrequencySet = true;
        }
    }

    public void setSpeed(int mode, int duration) throws Exception
    {
        if ((mode >= 0 && mode <= 5) && (duration >= 1 && duration <= 60) &&(duration != _currentSpeedDuration))
        {
            _cs200.setSpeed(mode, duration);
            _currentSpeedDuration = duration;
            _wasSpeedSet = true;
        }
    }

    public int getCurrentFrequency() throws Exception
    {
        if (_currentFrequency == -1)
        {
            if(!_isOpen)
                _cs200.init();
            String syncAndFreqRes = _cs200.getSyncAndFrequency();
            String[] splitString = syncAndFreqRes.split(",");
            if (splitString.length == 3 && splitString[0].contains("OK"))
            {
                _originalSync = Integer.valueOf(splitString[1]);
                String value = splitString[2].substring(0, splitString[2].length() - 2);
                value = value.replace(' ', '0');
                _currentFrequency = _originalFrequency = Integer.valueOf(value);// remove 2 delimiter chars (\n\r)
            }
        }
        return _currentFrequency;
    }

    public int getCurrentSpeed() throws Exception
    {
        if (_currentSpeedDuration == -1)
        {
            if(!_isOpen)
                _cs200.init();
            String speedRes = _cs200.getSpeed();
            String[] splitString = speedRes.split(",");
            if (splitString.length == 3 && splitString[0].contains("OK"))
            {
                _originalSpeedMode = Integer.valueOf(splitString[1]);
                String value = splitString[2].substring(0, splitString[2].length() - 2);
                value = value.replace(' ', '0');
                _currentSpeedDuration = _originalSpeedDuration = Integer.valueOf(value);
            }
        }
        return _currentSpeedDuration;
    }

    public boolean open(String comPort) throws Exception
    {
        if(!_isOpen)
            _cs200.init();
        _serialNumber = _cs200.getSerialID();
        _cs200.calibrate(CS200.OBSERVER_2degree, CS200.COLORSPACE_XYZ);

        // String syncAndFreqRes = _cs200.getSyncAndFrequency();
        // String[] splitString = syncAndFreqRes.split(",");
        // if (splitString.length == 3 && splitString[0].contains("OK"))
        // {
        // _originalSync = Integer.valueOf(splitString[1]);
        // String value = splitString[2].substring(0, splitString[2].length() - 2);
        // value = value.replace(' ', '0');
        // _currentFrequency = _originalFrequency = Integer.valueOf(value);// remove 2 delimiter chars (\n\r)
        // }
        getCurrentFrequency();
        getCurrentSpeed();

        _isOpen = true;
        return true;
    }

    public Point3f readXYZ() throws Exception
    {
        _cs200.startMeasurement();
        String mesureResult = _cs200.getTriStimulus();
        // format de la requ�te re�ue :
        // OK00,0,2,4,12,0, 0,0, 0, \t 85.215, 0.3118, 0.3381
        String[] splitString = mesureResult.split(",");
        if (splitString.length == 12 && splitString[0].contains("OK"))
        {
            float Y = Float.valueOf(splitString[9]);
            float x = Float.valueOf(splitString[10]);
            float y = Float.valueOf(splitString[11]);
            return new Point3f(x, y, Y);
        }
        else
            return null;

    }

    public boolean isOpen()
    {
        return _isOpen;
    }

    public String getSerialInfo()
    {
        return _serialNumber;
    }

}
