package fr.hd3d.colortribe.core.probes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.hd3d.colortribe.core.probes.IProbe.EProbeType;


public class ProbesPool
{

    private static Map<EProbeType, AbstractProbe> _probes = new HashMap<EProbeType, AbstractProbe>();

    public ProbesPool()
    {
    // sniffProbes();
    }

    private String getErrorMessage(EProbeType e, String message)
    {
        return e.toString() + " can't be loaded : " + message;
    }

    private boolean sniffProbes()
    {
//        boolean isSpyderConnected = false;
//        boolean /*isSpectroConnected*/ = false;

        if (FakeNoisyProbe.isConnected() == true)
        {
            _probes.put(EProbeType.TEST_CURVE_PROBE, new FakeNoisyProbe());
        }

        Spyder3Probe spyder3Probe = null;
        try
        {
            if (Spyder3Probe.isConnected() == true)
            {
                //isSpyderConnected = true;
                spyder3Probe = new Spyder3Probe();
                _probes.put(EProbeType.SPYDER_3, spyder3Probe);
            }
        }
        catch (Error error)
        {
            System.out.println(getErrorMessage(EProbeType.SPYDER_3, error.getMessage()));
        }
        
        try
        {
            if (Spyder4Probe.isConnected() == true)
            {
                _probes.put(EProbeType.SPYDER_4, new Spyder4Probe());
            }
        }
        catch (Error error)
        {
            System.out.println(getErrorMessage(EProbeType.SPYDER_4, error.getMessage()));
        }
        try
        {
            if (CS200Probe.isConnected() == true)
            {
                _probes.put(EProbeType.MK_CS200, new CS200Probe());
            }
        }
        catch (Error error)
        {
            System.out.println(getErrorMessage(EProbeType.MK_CS200, error.getMessage()));
        }
        try
        {
            if (K10Probe.isConnected() == true)
            {
                _probes.put(EProbeType.K10, new K10Probe());
            }
        }
        catch (Error error)
        {
            System.out.println(getErrorMessage(EProbeType.K10, error.getMessage()));
        }

//        // Probe couple
//        if ((ColorHealerModel._instance.getProtocol() instanceof CalibrationProtocol) && isSpyderConnected
//                && /*isASpectroConnectedS*/)
//        {
//            _probes.put(EProbeType.EYEONEPRO_SPYDER3, new GammaProbeAndColorProbe(spyder3Probe, /*spectroProbe*/,
//                    EProbeType.EYEONEPRO_SPYDER3));
//        }

        return false;
    }

    public void refreshProbesList()
    {
        _probes.clear();
        sniffProbes();
    }

    public Set<EProbeType> getRefreshedProbesList()
    {
        refreshProbesList();
        return _probes.keySet();
    }

    public Set<EProbeType> getProbesList()
    {
        return _probes.keySet();
    }

    public AbstractProbe getProbe(EProbeType probeType)
    {
        return _probes.get(probeType);
    }
}
