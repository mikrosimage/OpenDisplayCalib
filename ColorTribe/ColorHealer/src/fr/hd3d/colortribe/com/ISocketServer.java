package fr.hd3d.colortribe.com;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import fr.hd3d.colortribe.color.type.Point2f;


public interface ISocketServer
{
    public String acceptCom() throws IOException;

    public void closeCom() throws IOException;

    public void closeServer();

    public String sendMessageAndWait(String message) throws IllegalAccessException, IOException;

    public boolean displayColor(Color patch, boolean halo);

    public boolean displayFullRec(Color patch);

    public void sendMessage(String message) throws IllegalAccessException, IOException;

    public boolean sendLut(List<Point2f> red, List<Point2f> green, List<Point2f> blue, boolean showMire)
            throws IllegalAccessException, IOException;

    public void updateFile(String infos) throws IllegalAccessException, IOException;

    public String getInetAdressHostName();
    
    public Color getCurrentPatchColor();
}
