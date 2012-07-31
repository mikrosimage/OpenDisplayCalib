package fr.hd3d.colortribe.com;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import fr.hd3d.colortribe.color.type.Point2f;


public class UnpluggedSocketServer implements ISocketServer
{
    static public int DEFAULT_PORT = 7935;

    static private UnpluggedSocketServer _instance = null;
    private Color _currentPatchColor = Color.CYAN;

    private UnpluggedSocketServer()
    {}

    public Color getCurrentPatchColor()
    {
        return _currentPatchColor;
    }

    static public UnpluggedSocketServer getInstance()
    {
        if (_instance == null)
        {
            _instance = new UnpluggedSocketServer();
        }
        return _instance;
    }

    public String acceptCom()
    {
        return "";
    }

    public void closeCom() throws IOException
    {}

    public void closeServer()
    {

    }

    public String sendMessageAndWait(String message)
    {

        return message;
    }

    public boolean displayColor(Color patch, boolean halo)
    {
        _currentPatchColor = patch;
        return true;
    }

    public boolean displayFullRec(Color patch)
    {
        _currentPatchColor = patch;
        return true;
    }

    public void sendMessage(String message) throws IllegalAccessException, IOException
    {
    }

    public boolean sendLut(List<Point2f> red, List<Point2f> green, List<Point2f> blue, boolean showMire)
            throws IllegalAccessException, IOException
    {
       
        return true;
    }

    public void updateFile(String infos) throws IllegalAccessException, IOException
    {
       
    }

    public String getInetAdressHostName()
    {
        return "unknown";
    }

}
