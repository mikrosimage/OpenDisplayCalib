package fr.hd3d.colortribe.core.target;

import fr.hd3d.colortribe.color.EStandardIlluminants;
import fr.hd3d.colortribe.color.EStandardRgbPrimaries;
import fr.hd3d.colortribe.color.IIlluminant;
import fr.hd3d.colortribe.color.IRgbPrimary;


abstract public class AbstractTarget implements ITarget
{
    protected float _gamma = 2.2f;
    protected float _maxLum = 80f;
    protected IIlluminant _colorTemp = EStandardIlluminants.D65;
    protected IRgbPrimary _primaries = EStandardRgbPrimaries.REC709;
}
