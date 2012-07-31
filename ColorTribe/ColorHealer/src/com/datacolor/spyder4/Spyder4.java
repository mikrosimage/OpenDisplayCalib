package com.datacolor.spyder4;

public final class Spyder4
{

    private static Spyder4 theInstance = new Spyder4();
    static final public String apiAutorizationKey = "no_auth_code"; // Contact Datacolor to get an API Autorization Key

    private Spyder4()
    {
        System.loadLibrary("dccmtr"); 
        System.loadLibrary("JSpyder4");
    }

    public static Spyder4 getInstance()
    {
        return theInstance;
    }

    // UINT8 DLLIMPORT DC_Authorize ( char* key, int includeDatacolorDevices );
    public native int Autorize(String key, int includeDatacolorDevices) throws SpyderException;

    // UINT8 DLLIMPORT DC_Startup (DC_VendorData_S *pVendorData, UINT16 USB_PID);
    // int DLLVersion,   int HardwareVersion,   int SerialNumber[/* DC_SERIAL_NUMBER_LEN = 8*/]
    public native int[/*10*/] Startup() throws SpyderException;

    // UINT8 DLLIMPORT DC_GetXYZ (UINT16 nFrames, SINT32 *pX, SINT32 *pY, SINT32 *pZ);
    public native int[/* 3 */] GetXYZ(int nFrame) throws SpyderException;

    // void DLLIMPORT DC_Shutdown (void);
    public native void Shutdown() throws SpyderException;

    // UINT32 DLLIMPORT DC_GetDetailedError (UINT8 *systemError);
    public native int GetDetailedError() throws SpyderException;

    // UINT8 DLLIMPORT DC_ReadAmbientLightLevel( UINT16 * pLux );
    public native int ReadAmbientLightLevel() throws SpyderException;

    // UINT8 DLLIMPORT DC_WriteAmbientLightControlRegVal( UINT8 val );
    public native void WriteAmbientLightControlRegVal(int val) throws SpyderException;

    // UINT8 DLLIMPORT DC_SetLED( UINT8 mode, UINT16 rate );
    public native void SetLED(int mode, int rate) throws SpyderException;
}
