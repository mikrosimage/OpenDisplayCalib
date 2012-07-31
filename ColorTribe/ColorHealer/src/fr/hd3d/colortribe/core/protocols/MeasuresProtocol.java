package fr.hd3d.colortribe.core.protocols;

import java.util.LinkedHashMap;

import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.correction.AbstractCorrection;
import fr.hd3d.colortribe.gui.steps.BatchMeasuresStep;
import fr.hd3d.colortribe.gui.steps.ContinuousMeasuresStep;
import fr.hd3d.colortribe.gui.steps.DisplayStep;
import fr.hd3d.colortribe.gui.steps.ProbeTargetStep;
import fr.hd3d.colortribe.gui.steps.SimpleMeasuresStep;
import fr.hd3d.colortribe.gui.steps.Step;
import fr.hd3d.colortribe.gui.steps.Step.StepStatus;


public class MeasuresProtocol extends AbstractProtocol
{
    private LinkedHashMap<String, Step> _steps;
    private String _selectedStep;

    public MeasuresProtocol()
    {
        _steps = new LinkedHashMap<String, Step>();
        _steps.put(DisplayStep.NAME, new DisplayStep());
        _steps.put(ProbeTargetStep.NAME, new ProbeTargetStep(StepStatus.DISABLE));
        _steps.put(SimpleMeasuresStep.NAME, new SimpleMeasuresStep());
        _steps.put(ContinuousMeasuresStep.NAME, new ContinuousMeasuresStep());
        _steps.put(BatchMeasuresStep.NAME, new BatchMeasuresStep());
        _selectedStep = DisplayStep.NAME;
        _steps.get(_selectedStep).init();
    }

    public LinkedHashMap<String, Step> getSteps()
    {
        return _steps;
    }

    public StepStatus getStepStatus(String name)
    {
        Step step = _steps.get(name);
        if (step != null)
            return step.getStatus();
        else
            return StepStatus.UNKNOWN;
    }

    public String getStepDescription(String name)
    {
        Step step = _steps.get(name);
        if (step != null)
            return step.getDescription();
        else
            return "UNKNOWN";
    }

    public String getStepHTMLDescription(String name)
    {
        Step step = _steps.get(name);
        if (step != null)
            return step.getHTMLDescription();
        else
            return "UNKNOWN";
    }

    public Step getSelectedStep()
    {
        return _steps.get(_selectedStep);
    }

    public void setSelectedStep(String name)
    {
        _selectedStep = name;
    }

    public void unLockStep(String name)
    {
        Step step = _steps.get(name);
        if (step != null)
        {
            step.unLock();
        }

    }

    public void lockStep(String name, String reason)
    {
        Step step = _steps.get(name);
        if (step != null)
        {
            step.lock(reason);
        }

    }

  

    public void launchGammaMeasures()
    {
        throw new UnsupportedOperationException();        

    }

    public void abortGammaMeasures()
    {
        throw new UnsupportedOperationException();        
    }

    public boolean wasGammaMeasuresAborted()
    {
        throw new UnsupportedOperationException();        
    }

    public void computeCorrection()
    {
        ColorHealerModel._instance.getCorrection().computeColorCorrection();
    }

    public AbstractCorrection getCorrection()
    {
        return ColorHealerModel._instance.getCorrection();
    }

    public void launchValidationMeasures()
    {
        throw new UnsupportedOperationException();        
    }

}
