package com.datacolor.spyder3;

public class SpyderException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 3571593382335989248L;
    static final int S3_STATUS_SUCCESS = 0x01;
    static final int S3_STATUS_FAILURE = 0x00;
    static final int S3_ERROR_API = 0x00;
    static final int S3_ERROR_SYSTEM = 0x01;

    static final int S3_ERROR_NOT_INITIALIZED = 0x00010001;
    static final int S3_ERROR_INVALID_PARAMETER = 0x00010002;
    static final int S3_ERROR_TRANSMISSION_ERROR = 0x00010004;
    static final int S3_ERROR_TIMEOUT = 0x00010006;
    static final int S3_ERROR_BAD_AUTHORIZATION = 0x00010007;
    static final int S3_ERROR_NOT_AUTHORIZED = 0x00010008;
    static final int S3_ERROR_NOT_VENDOR_DEVICE = 0x00010009;
    static final int S3_LAST_FATAL_ERROR = 0x0001FFFF;

    static final int S3_WARNING_ALREADY_INITIALIZED = 0x00020001;
    static final int S3_WARNING_OVERALL_TIMEOUT = 0x00020003;
    static final int S3_WARNING_NO_CRT_CALIBRATION = 0x00020005;
    static final int S3_WARNING_NO_LCD_CALIBRATION = 0x00020006;
    static final int S3_WARNING_NO_TOK_CALIBRATION = 0x00020007;
    private int _code;
    private int _detailledCode = -1;

    public int getDetailledCode()
    {
        return _detailledCode;
    }

    public void setDetailledCode(int detailledCode)
    {
        this._detailledCode = detailledCode;
    }

    public SpyderException()
    {
        super();
        System.out.println("Default Ctor called");
    }

    public SpyderException(String description)
    {
        super(description);
    }

    public SpyderException(int code, int detailledCode)
    {
        // super(description);
        setCode(code);
        setDetailledCode(detailledCode);

    }

    public int getCode()
    {
        return _code;
    }

    public void setCode(int code)
    {
        this._code = code;
    }

    public String getCodeString()
    {
        switch (_code)
        {
            case S3_STATUS_SUCCESS:
                return "Success";
            case S3_STATUS_FAILURE:
                return "Failure";
            default:
                return "unknown code : " + _code;

        }
    }

    public String getDetailledCodeString()
    {
        switch (_detailledCode)
        {
            case S3_ERROR_API:
                return "API error";
            case S3_ERROR_SYSTEM:
                return "System error";
            case S3_ERROR_NOT_INITIALIZED:
                return "Not initialized";
            case S3_ERROR_INVALID_PARAMETER:
                return "Invalid parameter";
            case S3_ERROR_TRANSMISSION_ERROR:
                return "Transmission error";
            case S3_ERROR_TIMEOUT:
                return "Timeout";
            case S3_ERROR_BAD_AUTHORIZATION:
                return "Bad autorization";
            case S3_ERROR_NOT_AUTHORIZED:
                return "Not authorized";
            case S3_ERROR_NOT_VENDOR_DEVICE:
                return "Not vendor device";
            case S3_LAST_FATAL_ERROR:
                return "Last fatal error";
            case S3_WARNING_ALREADY_INITIALIZED:
                return "Warning : already initialized";
            case S3_WARNING_OVERALL_TIMEOUT:
                return "Warning : overall timeout";
            case S3_WARNING_NO_CRT_CALIBRATION:
                return "Warning : no CRT calibration";
            case S3_WARNING_NO_LCD_CALIBRATION:
                return "Warning : no LCD calibration";
            case S3_WARNING_NO_TOK_CALIBRATION:
                return "Warning : no TOK calibration";
            default:
                return "unknown code : " + _code;
        }
    }

    public String getMessage()
    {
        return "error : "  + getCodeString() + ", " + getDetailledCodeString() + "\n" + getStackTrace();
    }

}
