package fr.hd3d.colortribe.color.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

import fr.hd3d.colortribe.color.type.Point2f;

public class ColorSpectrumTables {
    public static WhitePoint WHITEPOINT_ILLUMINANT_A;
    public static WhitePoint WHITEPOINT_ILLUMINANT_B;
    public static WhitePoint WHITEPOINT_ILLUMINANT_C;
    public static WhitePoint WHITEPOINT_D50;
    public static WhitePoint WHITEPOINT_D55;
    public static WhitePoint WHITEPOINT_D65;
    public static WhitePoint WHITEPOINT_D75;
    public static WhitePoint WHITEPOINT_D93;
    public static WhitePoint WHITEPOINT_ILLUMINANT_E;
    public static WhitePoint WHITEPOINT_LCD;
    public static Primaries PRIMARIES_SMPTE_HD; // same as PRIMARIES_REC709
    public static Primaries PRIMARIES_EBU; // 1969
    public static Primaries PRIMARIES_SMPTE_C; // NTSC 1979
    public static Primaries PRIMARIES_SMPTE_240M; // old analog HD 1125/1250
    public static Primaries PRIMARIES_ORIGINAL_NTSC; // old NTSC
    public static Primaries PRIMARIES_YIQ; // old NTSC same as PRIMARIES_ORIGINAL_NTSC 1953
    public static Primaries PRIMARIES_COMMON_COMPUTER;
    public static Primaries PRIMARIES_REC709;
    public static Primaries PRIMARIES_TRINITRON;
    public static Primaries PRIMARIES_APPLE_RGB; // expecting gamma of  1.8
    public static Primaries PRIMARIES_sRGB; // same as PRIMARIES_REC709
    public static Primaries PRIMARIES_ADOBE_RGB; // 1998
    public static Primaries PRIMARIES_WIDE_GAMMUT_RGB;
    public static Primaries PRIMARIES_LCD;
    public static float colorTemp_K[] = new float[54];
    public static float colorTemp_x[] = new float[54];
    public static float colorTemp_y[] = new float[54];
    public static float spectrum_x[] = new float[81];
    public static float spectrum_y[] = new float[81];
    public static Vector<WhitePoint> listWhitePoints;
    public static Vector<Primaries> listPrimaries;
    static String prefsLocation = System.getProperty("java.home") + System.getProperty("file.separator") + "ColourMath.formulas.config";
   // private static Vector listComboBoxes;
    static {
        WHITEPOINT_ILLUMINANT_A = new WhitePoint("ILLUMINANT_A", 0.4476f, 0.4074f);
        WHITEPOINT_ILLUMINANT_B = new WhitePoint("ILLUMINANT_B", 0.3484f, 0.3516f);
        WHITEPOINT_ILLUMINANT_C = new WhitePoint("ILLUMINANT_C", 0.3101f, 0.3162f);
        WHITEPOINT_D50 = new WhitePoint("D50", 0.3457f, 0.3585f);
        WHITEPOINT_D55 = new WhitePoint("D55", 0.3320f, 0.3480f);
        WHITEPOINT_D65 = new WhitePoint("D65", 0.3127f, 0.3290f);
        WHITEPOINT_D75 = new WhitePoint("D75", 0.2990f, 0.3150f);
        WHITEPOINT_D93 = new WhitePoint("D93", 0.2848f, 0.2932f);
        WHITEPOINT_ILLUMINANT_E = new WhitePoint("ILLUMINANT_E", 0.3333f, 0.33333f);
        WHITEPOINT_LCD = new WhitePoint("LCD", 0.327f, 0.354f);
        PRIMARIES_REC709 = new Primaries("REC709", new Point2f(0.640f, 0.330f), new Point2f(0.300f, 0.600f), new Point2f(0.150f,
                0.060f));
        PRIMARIES_SMPTE_HD = new Primaries("SMPTE_HD", PRIMARIES_REC709);
        PRIMARIES_EBU = new Primaries("EBU", new Point2f(0.640f, 0.330f), new Point2f(0.290f, 0.600f), new Point2f(0.150f, 0.060f));
        PRIMARIES_SMPTE_C = new Primaries("SMPTE_C", new Point2f(0.635f, 0.340f), new Point2f(0.305f, 0.595f), new Point2f(0.155f,
                0.070f));
        PRIMARIES_SMPTE_240M = /**/new Primaries("SMPTE_240M", new Point2f(0.630f, 0.340f), new Point2f(0.310f, 0.595f),
                new Point2f(0.155f, 0.070f));
        PRIMARIES_ORIGINAL_NTSC = new Primaries("ORIGINAL_NTSC", new Point2f(0.670f, 0.330f), new Point2f(0.210f, 0.710f),
                new Point2f(0.140f, 0.080f));
        PRIMARIES_COMMON_COMPUTER = new Primaries("COMMON_COMPUTER", new Point2f(0.628f, 0.346f), new Point2f(0.268f, 0.588f),
                new Point2f(0.150f, 0.070f));
        PRIMARIES_YIQ = new Primaries("YIQ", PRIMARIES_ORIGINAL_NTSC);
        PRIMARIES_TRINITRON = new Primaries("TRINITRON", new Point2f(0.625f, 0.340f), new Point2f(0.280f, 0.595f), new Point2f(
                0.155f, 0.070f));
        PRIMARIES_APPLE_RGB = new Primaries("APPLE_RGB", PRIMARIES_TRINITRON);
        PRIMARIES_sRGB = new Primaries("sRGB", PRIMARIES_REC709);
        PRIMARIES_ADOBE_RGB = new Primaries("ADOBE_RGB", new Point2f(0.640f, 0.330f), new Point2f(0.210f, 0.710f), new Point2f(
                0.150f, 0.060f));
        PRIMARIES_WIDE_GAMMUT_RGB = new Primaries("WIDE_GAMMUT_RGB", new Point2f(0.735f, 0.265f), new Point2f(0.115f, 0.826f),
                new Point2f(0.157f, 0.018f));
        PRIMARIES_LCD = new Primaries("LCD", new Point2f(0.516f, 0.332f), new Point2f(0.315f, 0.506f), new Point2f(0.184f, 0.182f));
        colorTemp_K = new float[54];
        colorTemp_x = new float[54];
        colorTemp_y = new float[54];
        int i = 0;
        colorTemp_K[i] = 1000f;
        colorTemp_x[i] = 0.653f;
        colorTemp_y[i] = 0.344f;
        i++;
        colorTemp_K[i] = 1200f;
        colorTemp_x[i] = 0.625f;
        colorTemp_y[i] = 0.367f;
        i++;
        colorTemp_K[i] = 1400f;
        colorTemp_x[i] = 0.599f;
        colorTemp_y[i] = 0.386f;
        i++;
        colorTemp_K[i] = 1500f;
        colorTemp_x[i] = 0.588f;
        colorTemp_y[i] = 0.393f;
        i++;
        colorTemp_K[i] = 1600f;
        colorTemp_x[i] = 0.573f;
        colorTemp_y[i] = 0.399f;
        i++;
        colorTemp_K[i] = 1700f;
        colorTemp_x[i] = 0.561f;
        colorTemp_y[i] = 0.404f;
        i++;
        colorTemp_K[i] = 1800f;
        colorTemp_x[i] = 0.549f;
        colorTemp_y[i] = 0.408f;
        i++;
        colorTemp_K[i] = 1900f;
        colorTemp_x[i] = 0.538f;
        colorTemp_y[i] = 0.411f;
        i++;
        colorTemp_K[i] = 2000f;
        colorTemp_x[i] = 0.527f;
        colorTemp_y[i] = 0.413f;
        i++;
        colorTemp_K[i] = 2100f;
        colorTemp_x[i] = 0.516f;
        colorTemp_y[i] = 0.415f;
        i++;
        colorTemp_K[i] = 2200f;
        colorTemp_x[i] = 0.506f;
        colorTemp_y[i] = 0.415f;
        i++;
        colorTemp_K[i] = 2300f;
        colorTemp_x[i] = 0.496f;
        colorTemp_y[i] = 0.415f;
        i++;
        colorTemp_K[i] = 2400f;
        colorTemp_x[i] = 0.486f;
        colorTemp_y[i] = 0.415f;
        i++;
        colorTemp_K[i] = 2500f;
        colorTemp_x[i] = 0.477f;
        colorTemp_y[i] = 0.414f;
        i++;
        colorTemp_K[i] = 2600f;
        colorTemp_x[i] = 0.468f;
        colorTemp_y[i] = 0.412f;
        i++;
        colorTemp_K[i] = 2700f;
        colorTemp_x[i] = 0.460f;
        colorTemp_y[i] = 0.411f;
        i++;
        colorTemp_K[i] = 2800f;
        colorTemp_x[i] = 0.452f;
        colorTemp_y[i] = 0.409f;
        i++;
        colorTemp_K[i] = 2900f;
        colorTemp_x[i] = 0.444f;
        colorTemp_y[i] = 0.407f;
        i++;
        colorTemp_K[i] = 3000f;
        colorTemp_x[i] = 0.437f;
        colorTemp_y[i] = 0.404f;
        i++;
        colorTemp_K[i] = 3100f;
        colorTemp_x[i] = 0.430f;
        colorTemp_y[i] = 0.402f;
        i++;
        colorTemp_K[i] = 3200f;
        colorTemp_x[i] = 0.423f;
        colorTemp_y[i] = 0.399f;
        i++;
        colorTemp_K[i] = 3250f;
        colorTemp_x[i] = 0.420f;
        colorTemp_y[i] = 0.398f;
        i++;
        colorTemp_K[i] = 3300f;
        colorTemp_x[i] = 0.417f;
        colorTemp_y[i] = 0.396f;
        i++;
        colorTemp_K[i] = 3400f;
        colorTemp_x[i] = 0.411f;
        colorTemp_y[i] = 0.394f;
        i++;
        colorTemp_K[i] = 3500f;
        colorTemp_x[i] = 0.405f;
        colorTemp_y[i] = 0.391f;
        i++;
        colorTemp_K[i] = 3600f;
        colorTemp_x[i] = 0.409f;
        colorTemp_y[i] = 0.388f;
        i++;
        colorTemp_K[i] = 3700f;
        colorTemp_x[i] = 0.395f;
        colorTemp_y[i] = 0.385f;
        i++;
        colorTemp_K[i] = 3800f;
        colorTemp_x[i] = 0.390f;
        colorTemp_y[i] = 0.382f;
        i++;
        colorTemp_K[i] = 3900f;
        colorTemp_x[i] = 0.385f;
        colorTemp_y[i] = 0.380f;
        i++;
        colorTemp_K[i] = 4000f;
        colorTemp_x[i] = 0.381f;
        colorTemp_y[i] = 0.377f;
        i++;
        colorTemp_K[i] = 4100f;
        colorTemp_x[i] = 0.376f;
        colorTemp_y[i] = 0.374f;
        i++;
        colorTemp_K[i] = 4200f;
        colorTemp_x[i] = 0.372f;
        colorTemp_y[i] = 0.371f;
        i++;
        colorTemp_K[i] = 4300f;
        colorTemp_x[i] = 0.368f;
        colorTemp_y[i] = 0.369f;
        i++;
        colorTemp_K[i] = 4400f;
        colorTemp_x[i] = 0.364f;
        colorTemp_y[i] = 0.366f;
        i++;
        colorTemp_K[i] = 4500f;
        colorTemp_x[i] = 0.361f;
        colorTemp_y[i] = 0.364f;
        i++;
        colorTemp_K[i] = 4600f;
        colorTemp_x[i] = 0.357f;
        colorTemp_y[i] = 0.361f;
        i++;
        colorTemp_K[i] = 4700f;
        colorTemp_x[i] = 0.354f;
        colorTemp_y[i] = 0.359f;
        i++;
        colorTemp_K[i] = 4800f;
        colorTemp_x[i] = 0.351f;
        colorTemp_y[i] = 0.356f;
        i++;
        colorTemp_K[i] = 4900f;
        colorTemp_x[i] = 0.348f;
        colorTemp_y[i] = 0.354f;
        i++;
        colorTemp_K[i] = 5000f;
        colorTemp_x[i] = 0.345f;
        colorTemp_y[i] = 0.352f;
        i++;
        colorTemp_K[i] = 5200f;
        colorTemp_x[i] = 0.340f;
        colorTemp_y[i] = 0.347f;
        i++;
        colorTemp_K[i] = 5400f;
        colorTemp_x[i] = 0.335f;
        colorTemp_y[i] = 0.343f;
        i++;
        colorTemp_K[i] = 5600f;
        colorTemp_x[i] = 0.330f;
        colorTemp_y[i] = 0.339f;
        i++;
        colorTemp_K[i] = 5800f;
        colorTemp_x[i] = 0.326f;
        colorTemp_y[i] = 0.335f;
        i++;
        colorTemp_K[i] = 6000f;
        colorTemp_x[i] = 0.322f;
        colorTemp_y[i] = 0.332f;
        i++;
        colorTemp_K[i] = 6500f;
        colorTemp_x[i] = 0.314f;
        colorTemp_y[i] = 0.354f;
        i++;
        colorTemp_K[i] = 7000f;
        colorTemp_x[i] = 0.306f;
        colorTemp_y[i] = 0.317f;
        i++;
        colorTemp_K[i] = 7500f;
        colorTemp_x[i] = 0.300f;
        colorTemp_y[i] = 0.310f;
        i++;
        colorTemp_K[i] = 8000f;
        colorTemp_x[i] = 0.295f;
        colorTemp_y[i] = 0.305f;
        i++;
        colorTemp_K[i] = 8500f;
        colorTemp_x[i] = 0.291f;
        colorTemp_y[i] = 0.300f;
        i++;
        colorTemp_K[i] = 9000f;
        colorTemp_x[i] = 0.287f;
        colorTemp_y[i] = 0.296f;
        i++;
        colorTemp_K[i] = 9300f;
        colorTemp_x[i] = 0.285f;
        colorTemp_y[i] = 0.293f;
        i++;
        colorTemp_K[i] = 10000f;
        colorTemp_x[i] = 0.281f;
        colorTemp_y[i] = 0.288f;
        i++;
        colorTemp_K[i] = 15000f;
        colorTemp_x[i] = 0.264f;
        colorTemp_y[i] = 0.267f;
        i++;
        i = 0;
        spectrum_x[i] = 0.1741f;
        spectrum_y[i] = 0.0050f;
        i++;
        spectrum_x[i] = 0.1740f;
        spectrum_y[i] = 0.0050f;
        i++;
        spectrum_x[i] = 0.1738f;
        spectrum_y[i] = 0.0049f;
        i++;
        spectrum_x[i] = 0.1736f;
        spectrum_y[i] = 0.0049f;
        i++;
        spectrum_x[i] = 0.1733f;
        spectrum_y[i] = 0.0048f;
        i++;
        spectrum_x[i] = 0.1730f;
        spectrum_y[i] = 0.0048f;
        i++;
        spectrum_x[i] = 0.1726f;
        spectrum_y[i] = 0.0048f;
        i++;
        spectrum_x[i] = 0.1721f;
        spectrum_y[i] = 0.0048f;
        i++;
        spectrum_x[i] = 0.1714f;
        spectrum_y[i] = 0.0051f;
        i++;
        spectrum_x[i] = 0.1703f;
        spectrum_y[i] = 0.0058f;
        i++;
        spectrum_x[i] = 0.1689f;
        spectrum_y[i] = 0.0069f;
        i++;
        spectrum_x[i] = 0.1669f;
        spectrum_y[i] = 0.0086f;
        i++;
        spectrum_x[i] = 0.1644f;
        spectrum_y[i] = 0.0109f;
        i++;
        spectrum_x[i] = 0.1611f;
        spectrum_y[i] = 0.0138f;
        i++;
        spectrum_x[i] = 0.1566f;
        spectrum_y[i] = 0.0177f;
        i++;
        spectrum_x[i] = 0.1510f;
        spectrum_y[i] = 0.0227f;
        i++;
        spectrum_x[i] = 0.1440f;
        spectrum_y[i] = 0.0297f;
        i++;
        spectrum_x[i] = 0.1355f;
        spectrum_y[i] = 0.0399f;
        i++;
        spectrum_x[i] = 0.1241f;
        spectrum_y[i] = 0.0578f;
        i++;
        spectrum_x[i] = 0.1096f;
        spectrum_y[i] = 0.0868f;
        i++;
        spectrum_x[i] = 0.0913f;
        spectrum_y[i] = 0.1327f;
        i++;
        spectrum_x[i] = 0.0687f;
        spectrum_y[i] = 0.2007f;
        i++;
        spectrum_x[i] = 0.0454f;
        spectrum_y[i] = 0.2950f;
        i++;
        spectrum_x[i] = 0.0235f;
        spectrum_y[i] = 0.4127f;
        i++;
        spectrum_x[i] = 0.0082f;
        spectrum_y[i] = 0.5384f;
        i++;
        spectrum_x[i] = 0.0039f;
        spectrum_y[i] = 0.6548f;
        i++;
        spectrum_x[i] = 0.0139f;
        spectrum_y[i] = 0.7502f;
        i++;
        spectrum_x[i] = 0.0389f;
        spectrum_y[i] = 0.8120f;
        i++;
        spectrum_x[i] = 0.0743f;
        spectrum_y[i] = 0.8338f;
        i++;
        spectrum_x[i] = 0.1142f;
        spectrum_y[i] = 0.8262f;
        i++;
        spectrum_x[i] = 0.1547f;
        spectrum_y[i] = 0.8059f;
        i++;
        spectrum_x[i] = 0.1929f;
        spectrum_y[i] = 0.7816f;
        i++;
        spectrum_x[i] = 0.2296f;
        spectrum_y[i] = 0.7543f;
        i++;
        spectrum_x[i] = 0.2658f;
        spectrum_y[i] = 0.7243f;
        i++;
        spectrum_x[i] = 0.3016f;
        spectrum_y[i] = 0.6923f;
        i++;
        spectrum_x[i] = 0.3373f;
        spectrum_y[i] = 0.6589f;
        i++;
        spectrum_x[i] = 0.3731f;
        spectrum_y[i] = 0.6245f;
        i++;
        spectrum_x[i] = 0.4087f;
        spectrum_y[i] = 0.5896f;
        i++;
        spectrum_x[i] = 0.4441f;
        spectrum_y[i] = 0.5547f;
        i++;
        spectrum_x[i] = 0.4788f;
        spectrum_y[i] = 0.5202f;
        i++;
        spectrum_x[i] = 0.5125f;
        spectrum_y[i] = 0.4866f;
        i++;
        spectrum_x[i] = 0.5448f;
        spectrum_y[i] = 0.4544f;
        i++;
        spectrum_x[i] = 0.5752f;
        spectrum_y[i] = 0.4242f;
        i++;
        spectrum_x[i] = 0.6029f;
        spectrum_y[i] = 0.3965f;
        i++;
        spectrum_x[i] = 0.6270f;
        spectrum_y[i] = 0.3725f;
        i++;
        spectrum_x[i] = 0.6482f;
        spectrum_y[i] = 0.3514f;
        i++;
        spectrum_x[i] = 0.6658f;
        spectrum_y[i] = 0.3340f;
        i++;
        spectrum_x[i] = 0.6801f;
        spectrum_y[i] = 0.3197f;
        i++;
        spectrum_x[i] = 0.6915f;
        spectrum_y[i] = 0.3083f;
        i++;
        spectrum_x[i] = 0.7006f;
        spectrum_y[i] = 0.2993f;
        i++;
        spectrum_x[i] = 0.7079f;
        spectrum_y[i] = 0.2920f;
        i++;
        spectrum_x[i] = 0.7140f;
        spectrum_y[i] = 0.2859f;
        i++;
        spectrum_x[i] = 0.7190f;
        spectrum_y[i] = 0.2809f;
        i++;
        spectrum_x[i] = 0.7230f;
        spectrum_y[i] = 0.2770f;
        i++;
        spectrum_x[i] = 0.7260f;
        spectrum_y[i] = 0.2740f;
        i++;
        spectrum_x[i] = 0.7283f;
        spectrum_y[i] = 0.2717f;
        i++;
        spectrum_x[i] = 0.7300f;
        spectrum_y[i] = 0.2700f;
        i++;
        spectrum_x[i] = 0.7311f;
        spectrum_y[i] = 0.2689f;
        i++;
        spectrum_x[i] = 0.7320f;
        spectrum_y[i] = 0.2680f;
        i++;
        spectrum_x[i] = 0.7327f;
        spectrum_y[i] = 0.2673f;
        i++;
        spectrum_x[i] = 0.7334f;
        spectrum_y[i] = 0.2666f;
        i++;
        spectrum_x[i] = 0.7340f;
        spectrum_y[i] = 0.2660f;
        i++;
        spectrum_x[i] = 0.7344f;
        spectrum_y[i] = 0.2656f;
        i++;
        spectrum_x[i] = 0.7346f;
        spectrum_y[i] = 0.2654f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        spectrum_x[i] = 0.7347f;
        spectrum_y[i] = 0.2653f;
        i++;
        loadConfig(); // load user defined whitepoints and primaries
    }

    public static void loadConfig() {
        //System.out.println(propLocation);
        initWhitePoints();
        initPrimaries();
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(prefsLocation);
            br = new BufferedReader(fr);
            String s = "";
            while (s != null) {
                s = br.readLine();
                if (s != null) {
                    s = s.trim();
                    if (s.startsWith("WHITEPOINT=")) {
                        try {
                            String n = s.substring(11);
                            float x, y;
                            s = br.readLine().trim();
                            int xp = s.indexOf(' ');
                            x = Float.valueOf(s.substring(0, xp)).floatValue();
                            y = Float.valueOf(s.substring(xp)).floatValue();
                            addWhitePoint(new WhitePoint(n, x, y));
                        } catch (Exception e) {
                        }
                    }
                    if (s.startsWith("PRIMARIES=")) {
                        try {
                            String n = s.substring(10);
                            float x, y;
                            s = br.readLine().trim();
                            int xp = s.indexOf(' ');
                            x = Float.valueOf(s.substring(0, xp)).floatValue();
                            y = Float.valueOf(s.substring(xp)).floatValue();
                            Point2f red = new Point2f(x, y);
                            s = br.readLine().trim();
                            xp = s.indexOf(' ');
                            x = Float.valueOf(s.substring(0, xp)).floatValue();
                            y = Float.valueOf(s.substring(xp)).floatValue();
                            Point2f green = new Point2f(x, y);
                            s = br.readLine().trim();
                            xp = s.indexOf(' ');
                            x = Float.valueOf(s.substring(0, xp)).floatValue();
                            y = Float.valueOf(s.substring(xp)).floatValue();
                            Point2f blue = new Point2f(x, y);
                            addPrimaries(new Primaries(n, red, green, blue));
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            if (br != null)
                br.close();
        } catch (Exception e) {
        }
        try {
            if (fr != null)
                fr.close();
        } catch (Exception e) {
        }
        // updateComboBoxes();
    }

    public static void addWhitePoint(WhitePoint wp) {
        boolean doesntexists = true;
        int i = 0;
        while (i < listWhitePoints.size()) {
            if (wp.name.trim().toUpperCase().compareTo(((WhitePoint) listWhitePoints.elementAt(i)).name.trim().toUpperCase()) == 0) {
                doesntexists = false;
                i = listWhitePoints.size();
            }
            i++;
        }
        if (doesntexists)
            listWhitePoints.addElement(wp);
    }

    public static void addPrimaries(Primaries pf) {
        boolean doesntexists = true;
        int i = 0;
        while (i < listPrimaries.size()) {
            if (pf.name.trim().toUpperCase().compareTo(((Primaries) listPrimaries.elementAt(i)).name.trim().toUpperCase()) == 0) {
                doesntexists = false;
                i = listPrimaries.size();
            }
            i++;
        }
        if (doesntexists)
            listPrimaries.addElement(pf);
    }

    public static void saveConfig() {
        FileWriter fw = null;
        PrintWriter bw = null;
        try {
            fw = new FileWriter(prefsLocation);
            bw = new PrintWriter(fw);
            bw.println("# List of whitepoints and primaries used by ACE.Cube.ColourMath.formulas object.");
            bw.println("# This file is loaded on first use, and is designed to keep trace of user defined parameters.");
            bw.println("# Factory values are also shown but you cannot change them.");
            bw.println("# ");
            int i = 0;
            WhitePoint wp;
            while (i < listWhitePoints.size()) {
                wp = (WhitePoint) listWhitePoints.elementAt(i++);
                bw.println("WHITEPOINT=" + wp.name);
                bw.println(wp._point._a + " " + wp._point._b);
            }
            i = 0;
            Primaries pf;
            while (i < listPrimaries.size()) {
                pf = (Primaries) listPrimaries.elementAt(i++);
                bw.println("PRIMARIES=" + pf.name);
                bw.println(pf.red._a + " " + pf.red._b);
                bw.println(pf.green._a + " " + pf.green._b);
                bw.println(pf.blue._a + " " + pf.blue._b);
            }
        } catch (Exception e) {
        }
        try {
            bw.close();
        } catch (Exception e) {
        }
        try {
            fw.close();
        } catch (Exception e) {
        }
    }

//    public static void addComboBox(ComboBoxWrapper c) {
//        if (!listComboBoxes.contains(c)) {
//            listComboBoxes.addElement(c);
//        }
//    }
//
//    public static void removeComboBox(ComboBoxWrapper c) {
//        listComboBoxes.remove(c);
//    }
//
//    public static void updateComboBoxes() {
//        int i = 0;
//        while (i < listComboBoxes.size())
//            ((ComboBoxWrapper) (listComboBoxes.elementAt(i++))).updateItems();
//    }

    public static void initWhitePoints() {
        listWhitePoints = new Vector<WhitePoint>();
        listWhitePoints.addElement(WHITEPOINT_D65);
        listWhitePoints.addElement(WHITEPOINT_D50);
        listWhitePoints.addElement(WHITEPOINT_D55);
        listWhitePoints.addElement(WHITEPOINT_D75);
        listWhitePoints.addElement(WHITEPOINT_D93);
        listWhitePoints.addElement(WHITEPOINT_ILLUMINANT_A);
        listWhitePoints.addElement(WHITEPOINT_ILLUMINANT_B);
        listWhitePoints.addElement(WHITEPOINT_ILLUMINANT_C);
        listWhitePoints.addElement(WHITEPOINT_ILLUMINANT_E);
        listWhitePoints.addElement(WHITEPOINT_LCD);
    }

    public static void initPrimaries() {
        listPrimaries = new Vector<Primaries>();
        listPrimaries.addElement(PRIMARIES_REC709);
        listPrimaries.addElement(PRIMARIES_SMPTE_HD);
        listPrimaries.addElement(PRIMARIES_EBU);
        listPrimaries.addElement(PRIMARIES_SMPTE_C);
        listPrimaries.addElement(PRIMARIES_SMPTE_240M);
        listPrimaries.addElement(PRIMARIES_ORIGINAL_NTSC);
        listPrimaries.addElement(PRIMARIES_COMMON_COMPUTER);
        listPrimaries.addElement(PRIMARIES_YIQ);
        listPrimaries.addElement(PRIMARIES_TRINITRON);
        listPrimaries.addElement(PRIMARIES_APPLE_RGB);
        listPrimaries.addElement(PRIMARIES_sRGB);
        listPrimaries.addElement(PRIMARIES_ADOBE_RGB);
        listPrimaries.addElement(PRIMARIES_WIDE_GAMMUT_RGB);
        listPrimaries.addElement(PRIMARIES_LCD);
    }

    public static WhitePoint getWhitePointFromName(String s) {
        s = s.trim().toUpperCase();
        WhitePoint p;
        int i = 0;
        while (i < listWhitePoints.size()) {
            p = (WhitePoint) listWhitePoints.elementAt(i);
            if (p.name.trim().toUpperCase().equals(s))
                return p;
            i++;
        }
        return null;
    }

    public static Primaries getPrimariesFromName(String s) {
        s = s.trim().toUpperCase();
        Primaries p;
        int i = 0;
        while (i < listPrimaries.size()) {
            p = (Primaries) listPrimaries.elementAt(i);
            if (p.name.trim().toUpperCase().equals(s))
                return p;
            i++;
        }
        return null;
    }
}