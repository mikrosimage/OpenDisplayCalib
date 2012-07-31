package com.datacolor.spyder3;

public final class Spyder3
{
    private static Spyder3 theInstance = new Spyder3();
    static final public String apiAutorizationKey = "no_auth_code"; // Contact Datacolor to get an API Autorization Key
    
    private Spyder3()
    {
        System.loadLibrary("Spyder3"); 
        System.loadLibrary("JSpyder3");
    }

    public static Spyder3 getInstance()
    {
        return theInstance;
    }

    // UINT8 DLLIMPORT S3_Authorize ( char* key, int includeDatacolorDevices );
    public native int Autorize(String key, int includeDatacolorDevices) throws SpyderException;

    // UINT8 DLLIMPORT S3_Startup (S3_VendorData_S *pVendorData);
    // int DLLVersion,   int HardwareVersion,   int SerialNumber[/* S3_SERIAL_NUMBER_LEN = 8*/]
    public native int[/*10*/] Startup() throws SpyderException;

    // UINT8 DLLIMPORT S3_GetXYZ (UINT16 nFrames, SINT32 *pX, SINT32 *pY, SINT32 *pZ);
    public native int[/* 3 */] GetXYZ(int nFrame) throws SpyderException;

    // void DLLIMPORT S3_Shutdown (void);
    public native void Shutdown() throws SpyderException;

    // UINT32 DLLIMPORT S3_GetDetailedError (UINT8 *systemError);
    public native int GetDetailedError() throws SpyderException;

    // UINT8 DLLIMPORT S3_ReadAmbientLightLevel( UINT16 * pLux );
    public native int ReadAmbientLightLevel() throws SpyderException;

    // UINT8 DLLIMPORT S3_WriteAmbientLightControlRegVal( UINT8 val );
    public native void WriteAmbientLightControlRegVal(int val) throws SpyderException;

    // UINT8 DLLIMPORT S3_SetLED( UINT8 mode, UINT16 rate );
    public native void SetLED(int mode, int rate) throws SpyderException;
}
