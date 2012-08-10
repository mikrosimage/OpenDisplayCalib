/********************************************************************
 * filename: EyeOneException.java
 * 
 * purpose: Exception thrown by the EyeOne class.
 * 
 * Copyright (c) 2003 by GretagMacbeth AG Switzerland.
 * 
 * ALL RIGHTS RESERVED.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *********************************************************************/

package com.datacolor.spyder4;

public class SpyderException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 3571593382335989248L;
    private static final int DC_STATUS_SUCCESS = 0x01;
    private static final int DC_STATUS_FAILURE = 0x00;
    private static final int DC_ERROR_API = 0x00;
    private static final int DC_ERROR_SYSTEM = 0x01;

    private static final int DC_ERROR_NOT_INITIALIZED = 0x00010001;
    private static final int DC_ERROR_INVALID_PARAMETER = 0x00010002;
    private static final int DC_ERROR_TRANSMISSION_ERROR = 0x00010004;
    private static final int DC_ERROR_TIMEOUT = 0x00010006;
    private static final int DC_ERROR_BAD_AUTHORIZATION = 0x00010007;
    private static final int DC_ERROR_NOT_AUTHORIZED = 0x00010008;
    private static final int DC_ERROR_NOT_VENDOR_DEVICE = 0x00010009;
    private static final int DC_LAST_FATAL_ERROR = 0x0001FFFF;

    private static final int DC_WARNING_ALREADY_INITIALIZED = 0x00020001;
    private static final int DC_WARNING_OVERALL_TIMEOUT = 0x00020003;
    private static final int DC_WARNING_NO_CRT_CALIBRATION = 0x00020005;
    private static final int DC_WARNING_NO_LCD_CALIBRATION = 0x00020006;
    private static final int DC_WARNING_NO_TOK_CALIBRATION = 0x00020007;
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
            case DC_STATUS_SUCCESS:
                return "Success";
            case DC_STATUS_FAILURE:
                return "Failure";
            default:
                return "unknown code : " + _code;

        }
    }

    public String getDetailledCodeString()
    {
        switch (_detailledCode)
        {
            case DC_ERROR_API:
                return "API error";
            case DC_ERROR_SYSTEM:
                return "System error";
            case DC_ERROR_NOT_INITIALIZED:
                return "Not initialized";
            case DC_ERROR_INVALID_PARAMETER:
                return "Invalid parameter";
            case DC_ERROR_TRANSMISSION_ERROR:
                return "Transmission error";
            case DC_ERROR_TIMEOUT:
                return "Timeout";
            case DC_ERROR_BAD_AUTHORIZATION:
                return "Bad autorization";
            case DC_ERROR_NOT_AUTHORIZED:
                return "Not authorized";
            case DC_ERROR_NOT_VENDOR_DEVICE:
                return "Not vendor device";
            case DC_LAST_FATAL_ERROR:
                return "Last fatal error";
            case DC_WARNING_ALREADY_INITIALIZED:
                return "Warning : already initialized";
            case DC_WARNING_OVERALL_TIMEOUT:
                return "Warning : overall timeout";
            case DC_WARNING_NO_CRT_CALIBRATION:
                return "Warning : no CRT calibration";
            case DC_WARNING_NO_LCD_CALIBRATION:
                return "Warning : no LCD calibration";
            case DC_WARNING_NO_TOK_CALIBRATION:
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
