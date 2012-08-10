package fr.hd3d.colortribe.color.recycle;

import java.util.ArrayList;

import fr.hd3d.colortribe.color.EColorSpace;
import fr.hd3d.colortribe.color.type.Point3f;

/**
 * A ColorSet is a Collection of Point3D in a particular color space.
 * 
 * @author Guillaume CHATELET
 */
public class ColorSet extends ArrayList<Point3f> 
		{
	private static final long serialVersionUID = 3216833815824350405L;
	private EColorSpace colorSpace = EColorSpace.UNDEFINED;

	public ColorSet(EColorSpace colorSpace) {
		this.colorSpace = colorSpace;
	}

	private ColorSet(final ColorSet other) {
		this.addAll(other);
		colorSpace = other.colorSpace;
	}

	public EColorSpace getColorSpace() {
		return colorSpace;
	}

	

	@Override
	public String toString() {
		return "Colour set in " + colorSpace.toString() + " colorspace ("
				+ size() + " elements)";
	}

	@Override
	public ColorSet clone() {
		return new ColorSet(this);
	}

	// @Override
	// public int hashCode() {
	// int aSeed = HashCodeUtils.SEED;
	// HashCodeUtils.hash(aSeed, colorSpace);
	// HashCodeUtils.hash(aSeed, super.hashCode());
	// return aSeed;
	// }

	@Override
	public boolean equals(Object o) {
		if (o == null || o instanceof ColorSet == false)
			return false;
		final ColorSet other = (ColorSet) o;
		return colorSpace.equals(other.getColorSpace()) && super.equals(other);
	}

}
