package fr.hd3d.colortribe.core.probes;

import java.awt.HeadlessException;
import java.util.ConcurrentModificationException;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.probes.IProbe.EProbeType;


public class SampleGrabber
{
    private final static Logger LOGGER = Logger.getLogger(SampleGrabber.class.getSimpleName());
    private IProbe _probe = null;
    private static SampleGrabber _instance;
    private Thread _thread;
    private final SamplesRunnable _runnable = new SamplesRunnable();
    private final Object stopLock = new Object();
    private final Object startLock = new Object();
    private Thread currentThreadOwner = null;

    private class SamplesRunnable implements Runnable
    {
        private boolean stop;
        private ISampleListener listener;

        public void run()
        {
            stop = false;
            try
            {
                _probe.readXYZ();
                while (!stop)
                {
                    if (listener != null)
                        checkProbe();
                    listener.measureDone(_probe.readXYZ());
                }
            }
            catch (Exception e)
            {
                LOGGER.warning(e.getMessage());
                JOptionPane.showMessageDialog(null, e.getMessage(), "Probe error", JOptionPane.ERROR_MESSAGE);
            }
            finally
            {
                listener = null;
            }
        }

        public void stop()
        {
            stop = true;
        }

        public void setListener(ISampleListener listener)
        {
            this.listener = listener;
        }
    }

    private SampleGrabber()
    {
        if (_probe == null)
        {
            EProbeType probeType = ColorHealerModel._instance.getProbe().getEProbeType();

            if (probeType != null)
            {
                setProbe(ColorHealerModel._instance.getProbesPool().getProbe(probeType));
            }

        }
    }

    synchronized static public SampleGrabber getInstance()
    {
        if (_instance == null)
            _instance = new SampleGrabber();
        return _instance;
    }

    public void checkProbe()
    {
        EProbeType probeType = ColorHealerModel._instance.getProbe().getEProbeType();
        if (probeType != _probe.getEProbeType())
        {
            setProbe(ColorHealerModel._instance.getProbesPool().getProbe(probeType));
        }
    }

    private void setProbe(IProbe probe)
    {
        _probe = probe;
    }

    synchronized public void startSamplesGrab(ISampleListener listener)
    {
        synchronized (startLock)
        {
            waitForThreadToFinish();
            _runnable.setListener(listener);
            launchThread();
            currentThreadOwner = Thread.currentThread();
        }
    }

    private void waitForThreadToFinish()
    {
        if (_thread != null)
        {
            try
            {
                _thread.join();
            }
            catch (InterruptedException e)
            {
                System.err.println("Thread Error : " + e.getMessage());
            }
            finally
            {
                _thread = null;
            }
        }
    }

    private void launchThread()
    {
        _thread = new Thread(_runnable, "continuous measure");
        _thread.start();
    }

    public void stopSamplesGrab()
    {
        if (currentThreadOwner == null)
            throw new ConcurrentModificationException();
        if (currentThreadOwner != Thread.currentThread() && _thread != Thread.currentThread())
            throw new ConcurrentModificationException();
        if (_thread != null)
            synchronized (stopLock)
            {
                _runnable.stop();
                if (_thread != Thread.currentThread())
                    try
                    {
                        _thread.join();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
            }
    }

    private boolean isSampleManagerAvailable() throws Exception
    {
        return _probe.isAvailable("");
    }

    public boolean calibrateSampleManager() throws HeadlessException, Exception
    {
        if (isSampleManagerAvailable())
        {
                _probe.open("");
            return true;
        }
        return false;
    }

    public String getProbeDescription()
    {
        return _probe.getProbeDescription();
    }

    public EProbeType getEProbeType()
    {
        return _probe.getEProbeType();
    }

    public String getProbeName()
    {
        return _probe.getEProbeType().getName();
    }

}
