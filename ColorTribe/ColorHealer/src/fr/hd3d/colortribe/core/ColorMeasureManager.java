package fr.hd3d.colortribe.core;

import java.awt.Color;
import java.io.IOException;

import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.predicate.CountValuesPredicate;
import fr.hd3d.colortribe.core.predicate.IPredicate;
import fr.hd3d.colortribe.core.probes.ISampleListener;
import fr.hd3d.colortribe.core.probes.SampleGrabber;


public class ColorMeasureManager implements ISampleListener
{
    public static final ColorMeasureManager _instance = new ColorMeasureManager();
    private IPredicate _predicate;
    private ColorMeasure _currentMeasure;
    private MeasuresSet _currentMeasuresSet;
    private final Object lock = new Object();

    synchronized public void mesurePatch(MeasuresSet currentMeasuresSet, Color patchColor, String label, boolean isPatch) throws IllegalAccessException, IOException
    {
        this._currentMeasuresSet = currentMeasuresSet;
        if (isPatch)
            ColorHealerModel._instance.getSocketServer().displayColor(patchColor, false);
        else
            ColorHealerModel._instance.getSocketServer().displayFullRec(patchColor);
        _currentMeasure = new ColorMeasure(patchColor, label);
        _predicate = new CountValuesPredicate(_currentMeasure, 1);
        SampleGrabber.getInstance().startSamplesGrab(_instance);
        synchronized (lock)
        {
            try
            {
                lock.wait();
            }
            catch (InterruptedException e)
            {}
        }
    }

    public void measureDone(Point3f sample)
    {
        if ((sample._c > 0))
        {
            _currentMeasure.addSample(sample);
            // System.out.println(sample);
        }
        else
        {
            System.err.println("Negative values !!!");

        }
        if (_predicate.isDone())
        {
            SampleGrabber.getInstance().stopSamplesGrab();
            // if (allowAddSample) {
           _currentMeasuresSet.addMeasure(_currentMeasure);
            // }else{
            // _currentMeasure =null;
            // }
            synchronized (lock)
            {
                lock.notify();
            }
        }
    }
}
