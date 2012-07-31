package com.klein.k10;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;


public class JK10
{
    static public class XYZ extends Structure
    {
        public static class ByValue extends XYZ implements Structure.ByValue
        {
        }

        public float X;
        public float Y;
        public float Z;
    }

    public interface IK10 extends StdCallLibrary
    {
        public IK10 INSTANCE = (IK10) Native.loadLibrary("lib\\K10", IK10.class);

        public String isK10Connected();

        public String initK10(String port);

        public XYZ.ByValue getXYZ();

        void releaseK10();

    }

    static public String isConnected()
    {
        return IK10.INSTANCE.isK10Connected();
    }

    static public String init(String port)
    {
        return IK10.INSTANCE.initK10(port);
    }

    static public float[/* 3 */] getXYZ()
    {
        float values[] = new float[3];
        XYZ.ByValue res = IK10.INSTANCE.getXYZ();
        System.out.println(res.X + " " + res.Y + " " + res.Z);
        values[0] = res.X;
        values[1] = res.Y;
        values[2] = res.Z;
        return values;
    }
    static public void release(){
        IK10.INSTANCE.releaseK10();
    }

    //test
    // public static void main(String[] args)
    // {
    // String port = JK10.isConnected();
    // String serial = JK10.init(port);
    // System.out.println(port + " " + serial);
    // float[] res = JK10.getXYZ();
    // System.out.println(res[0] + " " + res[1] + " " + res[2]);
    // JK10.release();
    // }
}
