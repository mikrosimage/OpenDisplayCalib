package fr.hd3d.colortribe.core.target;

import java.awt.Color;

import fr.hd3d.colortribe.color.IIlluminant;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.util.ColorMath;

public interface ITarget {
	public static final float gammaGrayRatio = 0.2f;
	public static final Color GAMMA_GRAY = ColorMath.floatColorToUCharColor(
			gammaGrayRatio, gammaGrayRatio, gammaGrayRatio);

	static enum ETargetType {
		SIMPLE_QUATUOR(
				"Color Temperature, Primaries, Gamma, Maximum Luminosity (cda/m�)");

		private String _description = null;

		private ETargetType(String name) {
			_description = name;
		}

		public String getName() {
			return _description;
		}

	}

	public ETargetType getType();

	public float getGamma();

	public IIlluminant getColorTemp();

	public IRgbPrimary getPrimaries();

	public float getMaxLum();

	public float getColorDelta();

	public float getLumDelta();

	public float getGrayGammaTarget();

	public float getTCDelta();

}
