package fr.hd3d.colortribe.core.probes;

import fr.hd3d.colortribe.color.type.Point3f;


public class GammaProbeAndColorProbe extends AbstractProbe
{
    private AbstractProbe gammaProbe;
    private AbstractProbe colorProbe;
    // private EDisplayDeviceType _deviceType;
    private boolean isOpen = false;

    AbstractProbe selectedProbe = null;
    private final EProbeType probeType;

    public void selectGammaProbe()
    {
        selectedProbe = gammaProbe;
    }

    public void selectColorProbe()
    {
        selectedProbe = colorProbe;
    }

    public EProbeType getSelectedProbeType()
    {
        return selectedProbe.getEProbeType();
    }

    public GammaProbeAndColorProbe(AbstractProbe gammaProbe, AbstractProbe colorProbe, EProbeType probeType)
    {
        this.probeType = probeType;
        isOpen = true;
        this.gammaProbe = gammaProbe;
        this.colorProbe = colorProbe;
        selectedProbe = colorProbe;
    }

    public void close()
    {
        gammaProbe.close();
        colorProbe.close();
        isOpen = false;
    }

    public EProbeType getEProbeType()
    {
        return probeType;
    }

    public String getProbeDescription()
    {

        return colorProbe.getProbeDescription() + "\n" + gammaProbe.getProbeDescription();

    }

    public boolean isAvailable(String comPort) throws Exception
    {

        return isOpen;
    }

    public static boolean isConnected()
    {
        // DO nothing
        return true;
    }

    public boolean open(String comPort) throws Exception
    {
        colorProbe.open("");
        gammaProbe.open("");
        return true;
    }

    public Point3f readXYZ() throws Exception
    {
        return selectedProbe.readXYZ();
    }

    public boolean isOpen()
    {
        return isOpen;
    }

    public String getSerialInfo()
    {
        return gammaProbe.getEProbeType() + " : " + gammaProbe.getSerialInfo() + "\n" + colorProbe.getEProbeType()
                + " : " + colorProbe.getSerialInfo();
    }

}
