package fr.hd3d.colortribe.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class CHDatagramServer
{

    static public int DEFAULT_PORT = 7935;
    private DatagramSocket _socket;
    static private CHDatagramServer _instance = null;

    public CHDatagramServer()
    {

    }

    static public CHDatagramServer getInstance()
    {
        if (_instance == null)
        {
            _instance = new CHDatagramServer();
        }
        return _instance;
    }

    public void acceptCom() throws IOException
    {
        _socket = new DatagramSocket(DEFAULT_PORT);
        int taille = 1024;
        byte buffer[] = new byte[taille];

        DatagramPacket data = new DatagramPacket(buffer, buffer.length);
        _socket.receive(data);
        System.out.println(data.getAddress());
        _socket.send(data);

        System.out.println("cool ! ");
    }


}
