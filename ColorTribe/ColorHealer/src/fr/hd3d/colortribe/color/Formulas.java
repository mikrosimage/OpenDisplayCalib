package fr.hd3d.colortribe.color;

import static java.lang.Math.atan2;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.color.util.Primaries;
import fr.hd3d.colortribe.color.util.ColorMatrix;


public class Formulas
{
    private static final double XYZ_ratioLimit = 0.008856;
    private static final float third = 1f / 3f;
    private static final float sixth = 1f / 6f;
    private static final float twelveth = 1f / 12f;
    private static final float quarter = 1f / 4f;

    public static Point3f convertCIELABtoCIELCH(Point3f p)
    {
        final float C = (float) sqrt(p._b * p._b + p._c * p._c);
        final float H = (float) atan2(p._b, p._c);
        p._b = C;
        p._c = H;
        return p;
    }

    public static Point3f convertCIEuvwtoCIExyz(Point3f p)
    {
        final float denominator = 6 * p._a - 16 * p._b + 12;
        p._c = (-3 * p._a + 20 * p._b + 12) / denominator;
        p._a = 9 * p._a / denominator;
        p._b = 4 * p._b / denominator;
        return p;
    }

    public static Point3f convertCIExyYtoCIExyz(Point3f p)
    {
        p._c = 1 - p._a - p._b;
        return p;
    }

    public static Point3f convertCIExyYtoCIEXYZ(Point3f p)
    {
        final float x = p._a;
        final float y = p._b;
        final float Y = p._c;
        p._a = (x / y) * Y;
        p._b = Y;
        p._c = ((1 - x - y) / y) * Y;
        return p;
    }

    public static Point3f convertCIExyYtoCIEXYZ(float x, float y, float Y)
    {
        Point3f p = new Point3f();
        p._a = (x / y) * Y;
        p._b = Y;
        p._c = ((1 - x - y) / y) * Y;
        return p;
    }

    // CIE 1976 L*a*b* system
    // http://fr.wikipedia.org/wiki/CIE_Lab
    public static Point3f convertCIEXYZtoCIELab(Point3f p, final Point3f w)
    {
        final float L = convertCIEYtoL(p._b, w._b);
        final float fX = LabPerceptualCorrection(p._a / w._a);
        final float fY = LabPerceptualCorrection(p._b / w._b);
        final float fZ = LabPerceptualCorrection(p._c / w._c);
        final float aStar = 500 * (fX - fY);
        final float bStar = 200 * (fY - fZ);
        p._a = L;
        p._b = aStar;
        p._c = bStar;
        return p;
    }

    // CIE 1978/79/80 LABNHU system
    public static Point3f convertCIEXYZtoCIELABNHU(Point3f p, final Point3f w)
    {
        final Point3f txyz = convertCIExyYtoCIExyz(convertCIEXYZtoCIExyY(new Point3f(p)));
        final Point3f nxyz = convertCIExyYtoCIExyz(convertCIEXYZtoCIExyY(new Point3f(w)));
        final float Ap = (float) (quarter * pow((txyz._a / txyz._b) + sixth, third));
        final float Bp = (float) (-twelveth * pow((txyz._c / txyz._b) + sixth, third));
        final float Apn = (float) (quarter * pow((nxyz._a / nxyz._b) + sixth, third));
        final float Bpn = (float) (-twelveth * pow((nxyz._c / nxyz._b) + sixth, third));
        final float L = convertCIEYtoL(p._b, w._b);
        final float A = (float) (500 * (Ap - Apn) * pow(p._b, third));
        final float B = (float) (500 * (Bp - Bpn) * pow(p._b, third));
        p._a = L;
        p._b = A;
        p._c = B;
        return p;
    }

    // CIE 1976 L*u*v* system
    // http://fr.wikipedia.org/wiki/CIE_Lab
    public static Point3f convertCIEXYZtoCIELuv(Point3f t, final Point3f w)
    {
        final float tDenominator = t._a + 15 * t._b + 3 * t._c;
        final float up = 4 * t._a / tDenominator;
        final float vp = 9 * t._b / tDenominator;
        final float wDenominator = w._a + 15 * w._b + 3 * w._c;
        final float uwp = 4 * w._a / wDenominator;
        final float vwp = 9 * w._b / wDenominator;
        final float LStar = convertCIEYtoL(t._b, w._b);
        final float uStar = 13 * LStar * (up - uwp);
        final float vStar = 13 * LStar * (vp - vwp);
        t._a = LStar;
        t._b = uStar;
        t._c = vStar;
        return t;
    }

    public static Point3f convertCIExyztoCIEuvw(Point3f p)
    {
        final float denominator = -2 * p._a + 12 * p._b + 3;
        p._c = (-6 * p._a + 3 * p._b + 3) / denominator;
        p._a = 4 * p._a / denominator;
        p._b = 9 * p._b / denominator;
        return p;
    }

    public static Point3f convertCIEXYZtoCIEuvw(Point3f p)
    {
        final float denominator = p._a + 15 * p._b + 3 * p._c;
        p._c = (-3 * p._a + 6 * p._b + 3 * p._c) / denominator;
        p._a = 4 * p._a / denominator;
        p._b = 9 * p._b / denominator;
        return p;
    }

    public static Point3f convertCIEXYZtoCIEUVW(Point3f p, Point3f w)
    {
        final Point3f uvw = convertCIEXYZtoCIEuvw(new Point3f(p));
        final Point3f uvw0 = convertCIEXYZtoCIEuvw(new Point3f(w));
        final float W = (float) (25 * pow(p._b, third) - 17);
        p._c = W;
        p._a = 13 * W * (uvw._a - uvw0._a);
        p._b = 13 * W * (uvw._b - uvw0._b);
        return p;
    }

    public static Point3f convertCIEXYZtoCIExyY(Point3f p)
    {
        final float denominator = p._a + p._b + p._c;
        p._c = p._b;
        p._a = p._a / denominator;
        p._b = p._b / denominator;
        return p;
    }

    // Non linear adaptation of the XYZ space to the Lab space
    public static float LabPerceptualCorrection(final float t)
    {
        if (t > XYZ_ratioLimit)
            return (float) pow(t, third);
        else
            return 7.787f * t + 16f / 116f;
    }

    // Y is sample's light intensity and Yn is white point light intensity - returns L*
    public static float convertCIEYtoL(float Y, float Yn)
    {
        final float f = Y / Yn;
        if (f <= XYZ_ratioLimit)
            return (903.3f * f);
        else
            return (float) ((116 * pow(f, third)) - 16);
    }

    // Y is sample's light intensity and Yn is white point light intensity
    public static float convertLtoCIEY(float L, float Yn)
    {
        final float r;
        if (L <= (XYZ_ratioLimit * 903.3f))
            r = (L / 903.3f);
        else
            r = (float) (pow(((L + 16) / 116f), 3));
        return r * Yn;
    }

    // from http://brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
    // 0.412424 0.212656 0.0193324
    // 0.357579 0.715158 0.119193
    // 0.180464 0.0721856 0.950444
    public static Point3f convertRGB_D65toXYZ(Point3f p)
    {
        Point3f tmp = new Point3f(p);
        p._a = tmp._a * 0.412424f + tmp._b * 0.357579f + tmp._c * 0.180464f;
        p._b = tmp._a * 0.212656f + tmp._b * 0.715158f + tmp._c * 0.0721856f;
        p._c = tmp._a * 0.0193324f + tmp._b * 0.119193f + tmp._c * 0.950444f;
        return p;
    }

    // from http://brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
    // 3.24071 -0.969258 0.0556352
    // -1.53726 1.87599 -0.203996
    // -0.498571 0.0415557 1.05707
    public static Point3f convertXYZtoRGB_D65(Point3f p)
    {
        Point3f tmp = new Point3f(p);
        p._a = tmp._a * 3.24071f + tmp._b * -1.53726f + tmp._c * -0.498571f;
        p._b = tmp._a * -0.969258f + tmp._b * 1.87599f + tmp._c * 0.0415557f;
        p._c = tmp._a * 0.0556352f + tmp._b * -0.203996f + tmp._c * 1.05707f;
        return p;
    }

    public static float[] calculatePrimariesYfromCIExyYandWhite(Primaries prim, Point2f white, float whiteLevel)
    {
        float Rx, Ry, Rz, Bx, By, Bz, Wx, Wy, Wz, Gx, Gy, Gz;

        Rx = prim.red._a;
        Ry = prim.red._b;
        Rz = 1 - Rx - Ry;
        Gx = prim.green._a;
        Gy = prim.green._b;
        Gz = 1 - Gx - Gy;
        Bx = prim.blue._a;
        By = prim.blue._b;
        Bz = 1 - Bx - By;
        Wx = white._a;
        Wy = white._b;
        Wz = 1 - Wx - Wy;
        //WY = 1;

        ColorMatrix mat = new ColorMatrix();
        mat.matrix00 = Rx / Ry;
        mat.matrix10 = Gx / Gy;
        mat.matrix20 = Bx / By;
        mat.matrix01 = 1;
        mat.matrix11 = 1;
        mat.matrix21 = 1;
        mat.matrix02 = Rz / Ry;
        mat.matrix12 = Gz / Gy;
        mat.matrix22 = Bz / By;

        mat.invert();

        float x = Wx / Wy;
       //float y = 1;
        float z = Wz / Wy;
        float RY = mat.matrix00 * x + mat.matrix10 * x + mat.matrix20 * z;
        float GY = mat.matrix01 * x + mat.matrix11 * x + mat.matrix21 * z;
        float BY = mat.matrix02 * x + mat.matrix12 * x + mat.matrix22 * z;

        float sum = RY + GY + BY;

        return new float[] { RY * whiteLevel / sum, GY * whiteLevel / sum, BY * whiteLevel / sum };
        // float Rx,Ry,a,b,c,Bx,By,Wx,Wy,WY,Gx,Gy;
        //
        // Rx=prim.red.a;
        // Ry=prim.red.b;
        // Gx=prim.green.a;
        // Gy=prim.green.b;
        // Bx=prim.blue.a;
        // By=prim.blue.b;
        // Wx=white.x;
        // Wy=white.y;
        // WY=1;
        //        
        // float div=(Gy*Bx-Ry*Bx-Gx*By-Gy*Rx+By*Rx+Gx*Ry);
        // a=-(-Gy*Bx+Wy*Bx+Gx*By+Gy*Wx-By*Wx-Gx*Wy)/div;
        // b=-(-By*Rx+Wy*Rx+Bx*Ry+By*Wx-Ry*Wx-Bx*Wy)/div;
        // c=-(Gy*Rx-Wy*Rx-Gx*Ry-Gy*Wx+Ry*Wx+Gx*Wy)/div;
        //        
        // return new float[]{a*whiteLevel,b*whiteLevel,c*whiteLevel};

    }
}
