package fr.hd3d.colortribe.com;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import javax.swing.JOptionPane;

import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.util.ColorMath;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.correction.AbstractCorrection;


public class CHSocketServer extends ServerSocket implements ISocketServer
{
    static public int DEFAULT_PORT = 7935;

    static private CHSocketServer _instance = null;
    private Socket _socket = null;
    private Color _currentPatchColor = Color.CYAN;
    private String _lastMessage="";
    

    private CHSocketServer() throws IOException
    {
        super(DEFAULT_PORT);
    }
    public Color getCurrentPatchColor(){
        return _currentPatchColor;
    }
    
    

    static public CHSocketServer getInstance()
    {
        if (_instance == null)
        {
            try
            {
                _instance = new CHSocketServer();
            }
            catch (IOException e)
            {
                System.out.println("Can't start the server. Program failed !");
                JOptionPane.showMessageDialog(null,
                        "Can't start the server ! \nA ColorHealer instance is probably already running !",
                        "Server error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        return _instance;
    }

    public String acceptCom() throws IOException
    {
        _socket = super.accept();
        try
        {
            String message = sendMessageAndWait("GET_SCREEN_INFO\n");
            System.out.println("Received : " + message);
            System.out.println("on " + getInetAdressHostName());           
            return message;
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public void closeCom() throws IOException
    {
        if (_socket != null)
            _socket.close();
        _socket = null;
        super.close();
    }

    public void closeServer()
    {
        if ((_instance != null) && (_instance._socket != null))
        {
            // TODO
            // _instance.sendMessageAndWait("SHOW_MIRE OFF\n");
            // _instance.sendMessageAndWait("BYE\n");

            try
            {
                _instance.closeCom();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            _instance = null;
        }
    }

    public String sendMessageAndWait(String message) throws IllegalAccessException, IOException
    {
        String save = message;
        if (_socket == null)
            throw new IllegalAccessException("socket wasn't init.");
        if ((_socket.isOutputShutdown()) || (_socket.isInputShutdown()))
        {
            return "error";
        }
        BufferedReader plec = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        PrintWriter pred = new PrintWriter(new OutputStreamWriter(_socket.getOutputStream()), true);
        try
        {
            pred.println(message); // envoi d'un message
            message = plec.readLine();
         
        }
        catch (SocketException e)
        {
            return "Socket error";
        }
        // lecture de l'echo
        if ((message == null) && (!save.contains("BYE")))
        {
            System.out.println("Connection lost (" + save + ") !\nRestart CK and CH !");
        }

        return message;
    }
    
    public boolean displayColor(Color patch, boolean halo){
        try
        {
          // if(_currentPatchColor.equals(patch)) return true;
            String message = "SET_PATCH_COLOR " + ColorHealerModel._instance.getDisplayDevice().getOsIndex()+ "-"+ patch.getRed()+"-"+patch.getGreen()+ "-"+patch.getBlue()+"-"+ halo +"-"  + "\n";
            if(message.compareTo(_lastMessage) !=0)
                sendMessageAndWait( message);
            _currentPatchColor = patch;
            _lastMessage = message;
            return true;
        }
        catch (IllegalAccessException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }
    }
    public boolean displayFullRec(Color patch){
        try
        {
            String message = "SET_REC_COLOR " + ColorHealerModel._instance.getDisplayDevice().getOsIndex()+ "-"+ patch.getRed()+"-"+patch.getGreen()+ "-"+patch.getBlue()+"-"+ "\n";
            if(message.compareTo(_lastMessage) !=0)
                sendMessageAndWait( message);
            _currentPatchColor = patch;
            _lastMessage = message;
            return true;
        }
        catch (IllegalAccessException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }
    }

    public void sendMessage(String message) throws IllegalAccessException, IOException
    {
        if (_socket == null)
            throw new IllegalAccessException("socket wasn't init.");
        if ((_socket.isOutputShutdown()) || (_socket.isInputShutdown()))
        {
            System.out.println("sendMessage error");
        }
      
        PrintWriter pred = new PrintWriter(new OutputStreamWriter(_socket.getOutputStream()), true);
        pred.println(message); // envoi d'un message
        _lastMessage = message;
    }
    
    public boolean sendLut(List<Point2f> red, List<Point2f> green, List<Point2f> blue, boolean showMire) throws IllegalAccessException, IOException {
        int size = (AbstractCorrection.LUT_MAX_VALUE + 1);
        sendMessageAndWait("SET_LUT_SIZE " + ColorHealerModel._instance.getDisplayDevice().getOsIndex()+ " -"+ size + "-\n");
        for (int i = 0; i < size; i++) {
            int[] color = ColorMath.floatColorToUShortColor(red.get(i)._b, green.get(i)._b, blue.get(i)._b);
            
            sendMessage("VALUE " +ColorHealerModel._instance.getDisplayDevice().getOsIndex()+" -" + color[0] + "-" +  color[1] + "-" +  color[2] + "-\n");
        }
        sendMessage("SET_LUT_DONE "+ ColorHealerModel._instance.getDisplayDevice().getOsIndex()+"\n");
//        String returnString = sendMessageAndWait("SET_CALIBRATION_LUT ON\n");
//        if (returnString.compareTo("SET_CALIBRATION_LUT FAILED") == 0) {
//            return false;
//        } else
//            if(showMire)
//            sendMessageAndWait("SHOW_MIRE ON\n");
        return true;
    }
    
    public void updateFile(String infos) throws IllegalAccessException, IOException {
        infos = infos.replace('\n','#');
        sendMessageAndWait("UPDATE_CALIB_FILE " +  ColorHealerModel._instance.getDisplayDevice().getOsIndex() +" "+ infos+ "\n");
    }

    public String getInetAdressHostName()
    {
        if (_socket != null)
        {
            return _socket.getInetAddress().getCanonicalHostName();
        }
        return null;
    }

}
