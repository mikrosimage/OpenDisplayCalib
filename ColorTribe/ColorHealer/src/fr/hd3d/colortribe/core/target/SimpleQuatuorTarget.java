package fr.hd3d.colortribe.core.target;

import fr.hd3d.colortribe.color.IIlluminant;
import fr.hd3d.colortribe.color.IRgbPrimary;


public class SimpleQuatuorTarget extends AbstractTarget
{

    public SimpleQuatuorTarget(float gamma, float maxLum, IIlluminant colorTemp, IRgbPrimary primaries)
    {
        _gamma = gamma;
        _maxLum = maxLum;
        _colorTemp = colorTemp;
        _primaries = primaries;
    }


    public ETargetType getType()
    {
        return ETargetType.SIMPLE_QUATUOR;
    }

    public float getGamma()
    {
        return _gamma;
    }

    public IIlluminant getColorTemp()
    {
        return _colorTemp;
    }

    public IRgbPrimary getPrimaries()
    {
        return _primaries;
    }

    public float getMaxLum()
    {
        return _maxLum;
    }

    public float getColorDelta()
    {
        return 0.005f;
    }

    public float getLumDelta()
    {
        return 5;
    }

    public float getTCDelta()
    {
        return 4;
    }

    public float getGrayGammaTarget()
    {
        return (float) (getMaxLum() * Math.pow(gammaGrayRatio, _gamma));

    }

}
