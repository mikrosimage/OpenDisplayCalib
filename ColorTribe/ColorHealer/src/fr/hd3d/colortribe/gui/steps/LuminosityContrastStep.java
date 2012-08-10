package fr.hd3d.colortribe.gui.steps;

import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.probes.IProbe.EProbeType;


public class LuminosityContrastStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Luminosity and Contrast";
    private static final long serialVersionUID = 7306873765422762571L;

    private LuminosityContrastStep _selectedStep = null;
    private SubjectivLuminosityContrastStep _subj = null;
    private ObjectivLuminosityContrastStep _obj = null;

    public LuminosityContrastStep()
    {
        super(NAME, "Check lum and Constrast", StepStatus.DISABLE, null);

    }

    public void buildUI()
    {
        if (_selectedStep != null)
        {
            _selectedStep.buildUI();
            getContentPane().removeAll();
            getContentPane().add(_selectedStep.getContentPane());
            validate();
            repaint();
        }
    }

    public boolean canUnLockDependantStep()
    {
        return _selectedStep._status == StepStatus.OK;
    }

    public void init()
    {
        if (!_isInit) // TODO
        {

            if (ColorHealerModel._instance.getProbe().getEProbeType() == EProbeType.MK_CS200)
            {
                if (_obj == null)
                {
                    _obj = new ObjectivLuminosityContrastStep(this);
                }
                _selectedStep = _obj;
            }
            else
            {
                if (_subj == null)
                {
                   _subj = new SubjectivLuminosityContrastStep(this);
                }
                _selectedStep = _subj;
            }
            buildUI();
            _isInit = true;

            

        }
        _selectedStep.init();
    }

    public void unLock()
    {
        if (_selectedStep == null)
        {
            init();
        }
        _selectedStep.unLock();
    }

    public void valid()
    {
        _selectedStep.valid();
    }

    public void lock(String reason)
    {
        if (_selectedStep == null)
        {
            init();
        }
        _selectedStep.lock(reason);
    }

}
