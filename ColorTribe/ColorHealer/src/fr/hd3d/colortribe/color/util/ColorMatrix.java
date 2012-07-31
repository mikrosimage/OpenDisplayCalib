package fr.hd3d.colortribe.color.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import fr.hd3d.colortribe.color.Formulas;
import fr.hd3d.colortribe.color.type.Matrix3;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;



public class ColorMatrix extends Matrix3 {
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // This method is derived from interface java.io.Externalizable
        // to do: code goes here
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // This method is derived from interface java.io.Externalizable
        // to do: code goes here
    }

    public void debug() {
        System.out.println("Colour Matrix :  " + pad(matrix00) + " " + pad(matrix10) + " " + pad(matrix20));
        System.out.println("                 " + pad(matrix01) + " " + pad(matrix11) + " " + pad(matrix21));
        System.out.println("                 " + pad(matrix02) + " " + pad(matrix12) + " " + pad(matrix22));
    }

    public float[] toArray() {
        float f[] = new float[3 * 3];
        f[0] = matrix00;
        f[1] = matrix10;
        f[2] = matrix20;
        f[3] = matrix01;
        f[4] = matrix11;
        f[5] = matrix21;
        f[6] = matrix02;
        f[7] = matrix12;
        f[8] = matrix22;
        return f;
    }

    public String toString() {
        return "Colour Matrix :  " + matrix00 + " " + matrix10 + " " + matrix20 + "      " + matrix01 + " " + matrix11 + " " + matrix21 + "      "
                + matrix02 + " " + matrix12 + " " + matrix22;
    }

    public String toFormulastring() {
        return "A=r*" + matrix00 + "+g*" + matrix10 + "+b*" + matrix20 + "\n" + "B=r*" + matrix01 + "+g*" + matrix11 + "+b*" + matrix21 + "\n"
                + "C=r*" + matrix02 + "+g*" + matrix12 + "+b*" + matrix22 + "\n";
    }

    private final static String pad(float f) {
        String s = f + "";
        while (s.length() < 12)
            s = " " + s;
        return s;
    }

    public final void transform(int pixels[]) {
        int data;
        float r, g, b;
        int i = 0;
        while (i < pixels.length) {
            data = pixels[i];
            r = (data >> 16 & 255) / 255f;
            g = (data >> 8 & 255) / 255f;
            b = (data & 255) / 255f;
            data = (int) ((matrix00 * r + matrix01 * g + matrix02 * b) * 255f) << 16;
            data += (int) ((matrix10 * r + matrix11 * g + matrix12 * b) * 255f) << 8;
            data += (int) ((matrix20 * r + matrix11 * g + matrix22 * b) * 255f);
            pixels[i++] = data;
        }
    }

    public ColorMatrix() {
    }

    public ColorMatrix(ColorMatrix o) {
        matrix00 = o.matrix00;
        matrix10 = o.matrix10;
        matrix20 = o.matrix20;
        matrix01 = o.matrix01;
        matrix11 = o.matrix11;
        matrix21 = o.matrix21;
        matrix02 = o.matrix02;
        matrix12 = o.matrix12;
        matrix22 = o.matrix22;
    }

    public void makeIdentity() {
        matrix00 = 1;
        matrix10 = 0;
        matrix20 = 0;
        matrix02 = 0;
        matrix11 = 1;
        matrix21 = 0;
        matrix02 = 0;
        matrix12 = 0;
        matrix22 = 1;
    }

    public void setup_CIEXYZtoRGB() {
        matrix00 = 3.240479f;
        matrix01 = -0.969256f;
        matrix02 = 0.055648f;
        matrix10 = -1.537150f;
        matrix11 = 1.875992f;
        matrix12 = -0.204043f;
        matrix20 = -0.498535f;
        matrix21 = 0.041556f;
        matrix22 = 1.057311f;
    }

    public void setup_RGBtoCIEXYZ() {
        // this methods works, but is not recommended. Use measurements of xyY for primaries instead. (more precise)
        setup_CIEXYZtoRGB();
        invert();
    }

    public void setup_CIEXYZtoRGB(Primaries p, WhitePoint w) {
        setup_CIEXYZtoRGB(p, new Point2f(w._point._a, w._point._b));
    }

    public void setup_CIEXYZtoRGB(Primaries p, Point2f whitePoint) {
        // this methods works, but is not recommended. Use measurements of xyY for primaries instead. (more precise)
        setup_RGBtoCIEXYZ(p, whitePoint);
        invert();
    }

    public void setup_YUVtoRGB() {
        setup_RGBtoYUV();
        invert();
    }

    public void setup_RGBtoYUV() {
        matrix00 = 65.481f;
        matrix01 = -37.797f;
        matrix02 = 112f;
        matrix10 = 128.553f;
        matrix11 = -74.203f;
        matrix12 = -93.786f;
        matrix20 = 24.966f;
        matrix21 = 112f;
        matrix22 = -18.214f;
    }

    public void setup_CIEXYZtoRGB(Point3f redPrimary, Point3f greenPrimary, Point3f bluePrimary) {
        //  This maybe wrong...
        // convert xyY (where Y=1) to XYZ
        Point3f red = Formulas.convertCIExyYtoCIEXYZ(redPrimary);
        Point3f green = Formulas.convertCIExyYtoCIEXYZ(greenPrimary);
        Point3f blue = Formulas.convertCIExyYtoCIEXYZ(bluePrimary);
        matrix00 = red._a;
        matrix10 = green._a;
        matrix20 = blue._a;
        matrix01 = red._b;
        matrix11 = green._b;
        matrix21 = blue._b;
        matrix02 = red._c;
        matrix12 = green._c;
        matrix22 = blue._c;
    }

    public void setup_RGBtoCIEXYZ(Point3f redPrimary, Point3f greenPrimary, Point3f bluePrimary) {
        //  This maybe wrong...
        // convert xyY (where Y=1) to XYZ
        Point3f red = Formulas.convertCIExyYtoCIEXYZ(redPrimary);
        Point3f green = Formulas.convertCIExyYtoCIEXYZ(greenPrimary);
        Point3f blue = Formulas.convertCIExyYtoCIEXYZ(bluePrimary);
        matrix00 = red._a;
        matrix10 = green._a;
        matrix20 = blue._a;
        matrix01 = red._b;
        matrix11 = green._b;
        matrix21 = blue._b;
        matrix02 = red._c;
        matrix12 = green._c;
        matrix22 = blue._c;
        invert();
    }

    public void setup_RGBtoCIEXYZ(Primaries p, WhitePoint w) {
        setup_RGBtoCIEXYZ(p, new Point2f(w._point._a, w._point._b));
    }

    public void setup_sRGBtoCIEXYZ_DCI(Primaries sourcePrimaries, WhitePoint whitePoint) {
        Primaries p = sourcePrimaries;
        float whiteLevel = 1;
        float abc[] = Formulas.calculatePrimariesYfromCIExyYandWhite(p, whitePoint.asDoubleFloat(), whiteLevel);
        // System.out.println("abc="+abc[0]+", "+abc[1]+", "+abc[2]+" sum="+(abc[0]+abc[1]+abc[2]));        
        Point3f red_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.red._a, p.red._b, abc[0]);
        Point3f green_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.green._a, p.green._b, abc[1]);
        Point3f blue_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.blue._a, p.blue._b, abc[2]);
        Point3f white_XYZ = Formulas.convertCIExyYtoCIEXYZ(whitePoint._point._a, whitePoint._point._b, whiteLevel);
        ColorMatrix c = new ColorMatrix();
        c.matrix00 = red_XYZ._a;
        c.matrix10 = green_XYZ._a;
        c.matrix20 = blue_XYZ._a;
        c.matrix01 = red_XYZ._b;
        c.matrix11 = green_XYZ._b;
        c.matrix21 = blue_XYZ._b;
        c.matrix02 = red_XYZ._c;
        c.matrix12 = green_XYZ._c;
        c.matrix22 = blue_XYZ._c;
        // = matrix :
        //     | red.X green.x blue.X |
        //     | red.Y green.y blue.Y | 
        //     | red.Z green.z blue.Z |
        Point3f w = new Point3f(white_XYZ._a / white_XYZ._b, 1, white_XYZ._c / white_XYZ._b);
        c.invert();
        Point3f a = c.transform(w); // |a|=|c|-1  x |w| 
        whiteLevel = 48f / 52.37f;
        matrix00 = red_XYZ._a * a._a * whiteLevel;
        matrix10 = green_XYZ._a * a._b * whiteLevel;
        matrix20 = blue_XYZ._a * a._c * whiteLevel;
        matrix01 = red_XYZ._b * a._a * whiteLevel;
        matrix11 = green_XYZ._b * a._b * whiteLevel;
        matrix21 = blue_XYZ._b * a._c * whiteLevel;
        matrix02 = red_XYZ._c * a._a * whiteLevel;
        matrix12 = green_XYZ._c * a._b * whiteLevel;
        matrix22 = blue_XYZ._c * a._c * whiteLevel;
    }

    public void setup_RGBtoCIEXYZ(Primaries p, Point2f whitePoint) {
        float whiteLevel = 1;
        float abc[] = Formulas.calculatePrimariesYfromCIExyYandWhite(p, whitePoint, 1);
        //System.out.println("abc="+abc[0]+", "+abc[1]+", "+abc[2]+" sum="+(abc[0]+abc[1]+abc[2]));        
        Point3f red_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.red._a, p.red._b, abc[0]);
        Point3f green_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.green._a, p.green._b, abc[1]);
        Point3f blue_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.blue._a, p.blue._b, abc[2]);
        Point3f white_XYZ = Formulas.convertCIExyYtoCIEXYZ(whitePoint._a, whitePoint._b, whiteLevel);
        ColorMatrix c = new ColorMatrix();
        c.matrix00 = red_XYZ._a;
        c.matrix10 = green_XYZ._a;
        c.matrix20 = blue_XYZ._a;
        c.matrix01 = red_XYZ._b;
        c.matrix11 = green_XYZ._b;
        c.matrix21 = blue_XYZ._b;
        c.matrix02 = red_XYZ._c;
        c.matrix12 = green_XYZ._c;
        c.matrix22 = blue_XYZ._c;
        // = matrix :
        //     | red.X green.x blue.X |
        //     | red.Y green.y blue.Y | 
        //     | red.Z green.z blue.Z |
        Point3f w = new Point3f(white_XYZ._a / white_XYZ._b, 1, white_XYZ._c / white_XYZ._b);
        c.invert();
        Point3f a = c.transform(w); // |a|=|c|-1  x |w| 
        whiteLevel = 1;
        matrix00 = red_XYZ._a * a._a * whiteLevel;
        matrix10 = green_XYZ._a * a._b * whiteLevel;
        matrix20 = blue_XYZ._a * a._c * whiteLevel;
        matrix01 = red_XYZ._b * a._a * whiteLevel;
        matrix11 = green_XYZ._b * a._b * whiteLevel;
        matrix21 = blue_XYZ._b * a._c * whiteLevel;
        matrix02 = red_XYZ._c * a._a * whiteLevel;
        matrix12 = green_XYZ._c * a._b * whiteLevel;
        matrix22 = blue_XYZ._c * a._c * whiteLevel;
    }
}