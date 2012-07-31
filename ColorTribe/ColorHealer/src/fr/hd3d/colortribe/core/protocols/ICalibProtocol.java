package fr.hd3d.colortribe.core.protocols;

import java.util.LinkedHashMap;

import fr.hd3d.colortribe.core.correction.AbstractCorrection;
import fr.hd3d.colortribe.gui.steps.Step;
import fr.hd3d.colortribe.gui.steps.Step.StepStatus;


public interface ICalibProtocol
{
    public LinkedHashMap<String, Step> getSteps();
    public StepStatus getStepStatus(String name);
    public String getStepDescription(String name);
    public String getStepHTMLDescription(String name);
    public Step getSelectedStep();
    public void setSelectedStep(String name);
    public void unLockStep(String name);
    public void lockStep(String name, String reason);
    public void launchGammaMeasures();
    public void launchValidationMeasures();
    public void computeCorrection();
    public AbstractCorrection getCorrection();
    public void abortGammaMeasures();
    public boolean wasGammaMeasuresAborted();

}
