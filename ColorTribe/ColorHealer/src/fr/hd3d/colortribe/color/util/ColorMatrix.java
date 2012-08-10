package fr.hd3d.colortribe.color.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import fr.hd3d.colortribe.color.Formulas;
import fr.hd3d.colortribe.color.type.Matrix3;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;

public class ColorMatrix extends Matrix3 {
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// This method is derived from interface java.io.Externalizable
		// to do: code goes here
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// This method is derived from interface java.io.Externalizable
		// to do: code goes here
	}

	public void debug() {
		System.out.println("Colour Matrix :  " + pad(matrix00) + " "
				+ pad(matrix10) + " " + pad(matrix20));
		System.out.println("                 " + pad(matrix01) + " "
				+ pad(matrix11) + " " + pad(matrix21));
		System.out.println("                 " + pad(matrix02) + " "
				+ pad(matrix12) + " " + pad(matrix22));
	}

	public String toString() {
		return "Colour Matrix :  " + matrix00 + " " + matrix10 + " " + matrix20
				+ "      " + matrix01 + " " + matrix11 + " " + matrix21
				+ "      " + matrix02 + " " + matrix12 + " " + matrix22;
	}

	private final static String pad(float f) {
		String s = f + "";
		while (s.length() < 12)
			s = " " + s;
		return s;
	}

	public ColorMatrix() {
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

	public void setup_CIEXYZtoRGB(Primaries p, Point2f whitePoint) {
		// this methods works, but is not recommended. Use measurements of xyY
		// for primaries instead. (more precise)
		setup_RGBtoCIEXYZ(p, whitePoint);
		invert();
	}

	private void setup_RGBtoCIEXYZ(Primaries p, Point2f whitePoint) {
		float whiteLevel = 1;
		float abc[] = Formulas.calculatePrimariesYfromCIExyYandWhite(p,
				whitePoint, 1);
		// System.out.println("abc="+abc[0]+", "+abc[1]+", "+abc[2]+" sum="+(abc[0]+abc[1]+abc[2]));
		Point3f red_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.red._a, p.red._b,
				abc[0]);
		Point3f green_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.green._a,
				p.green._b, abc[1]);
		Point3f blue_XYZ = Formulas.convertCIExyYtoCIEXYZ(p.blue._a, p.blue._b,
				abc[2]);
		Point3f white_XYZ = Formulas.convertCIExyYtoCIEXYZ(whitePoint._a,
				whitePoint._b, whiteLevel);
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
		// | red.X green.x blue.X |
		// | red.Y green.y blue.Y |
		// | red.Z green.z blue.Z |
		Point3f w = new Point3f(white_XYZ._a / white_XYZ._b, 1, white_XYZ._c
				/ white_XYZ._b);
		c.invert();
		Point3f a = c.transform(w); // |a|=|c|-1 x |w|
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