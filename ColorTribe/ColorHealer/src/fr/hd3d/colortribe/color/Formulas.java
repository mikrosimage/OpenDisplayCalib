package fr.hd3d.colortribe.color;

import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.color.util.ColorMatrix;
import fr.hd3d.colortribe.color.util.Primaries;

public class Formulas {

	public static Point3f convertCIExyYtoCIEXYZ(Point3f p) {
		final float x = p._a;
		final float y = p._b;
		final float Y = p._c;
		p._a = (x / y) * Y;
		p._b = Y;
		p._c = ((1 - x - y) / y) * Y;
		return p;
	}

	public static Point3f convertCIExyYtoCIEXYZ(float x, float y, float Y) {
		Point3f p = new Point3f();
		p._a = (x / y) * Y;
		p._b = Y;
		p._c = ((1 - x - y) / y) * Y;
		return p;
	}

	public static Point3f convertCIEXYZtoCIExyY(Point3f p) {
		final float denominator = p._a + p._b + p._c;
		p._c = p._b;
		p._a = p._a / denominator;
		p._b = p._b / denominator;
		return p;
	}

	// from http://brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
	// 0.412424 0.212656 0.0193324
	// 0.357579 0.715158 0.119193
	// 0.180464 0.0721856 0.950444
	public static Point3f convertRGB_D65toXYZ(Point3f p) {
		Point3f tmp = new Point3f(p);
		p._a = tmp._a * 0.412424f + tmp._b * 0.357579f + tmp._c * 0.180464f;
		p._b = tmp._a * 0.212656f + tmp._b * 0.715158f + tmp._c * 0.0721856f;
		p._c = tmp._a * 0.0193324f + tmp._b * 0.119193f + tmp._c * 0.950444f;
		return p;
	}

	public static float[] calculatePrimariesYfromCIExyYandWhite(Primaries prim,
			Point2f white, float whiteLevel) {
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
		// WY = 1;

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
		// float y = 1;
		float z = Wz / Wy;
		float RY = mat.matrix00 * x + mat.matrix10 * x + mat.matrix20 * z;
		float GY = mat.matrix01 * x + mat.matrix11 * x + mat.matrix21 * z;
		float BY = mat.matrix02 * x + mat.matrix12 * x + mat.matrix22 * z;

		float sum = RY + GY + BY;

		return new float[] { RY * whiteLevel / sum, GY * whiteLevel / sum,
				BY * whiteLevel / sum };
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
