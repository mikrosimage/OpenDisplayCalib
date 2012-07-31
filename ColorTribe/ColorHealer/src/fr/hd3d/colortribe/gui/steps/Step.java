package fr.hd3d.colortribe.gui.steps;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.core.ColorHealerModel;


abstract public class Step extends JPanel implements IStep
{
    /**
     * 
     */
    private static final long serialVersionUID = 6330606066326679079L;
    protected String _name;
    protected String _description;
    // protected JPanel _pane;
    protected int _width = 542;
    protected int _height = 555;
    protected boolean _isInit = false;
    protected List<String> _dependantSteps = null;
    

    public enum StepStatus
    {
        OK, DISABLE, NOT_COMPLETE, FAILED, UNKNOWN
    };

    protected StepStatus _status;

    public Step( String name, String description, StepStatus status, List<String> dependantSteps)
    {
        _name = name;
        _description = description;
        _status = status;
       _dependantSteps = dependantSteps;
        buildUI();
    }

    private void buildUI()
    {
        setPreferredSize(new Dimension(_width, _height));
    }

    public JPanel getContentPane()
    {
        return this;
    }

    public String getName()
    {
        return _name;
    }

    public String getDescription()
    {
        return _description;
    }

    public StepStatus getStatus()
    {
        return _status;
    }

    public boolean isEnabled()
    {
        return _status != StepStatus.DISABLE;
    }

    public String getHTMLDescription()
    {
        String htmlDesc = _description;
        htmlDesc = htmlDesc.replace("\n", "<br>");
        htmlDesc = "<html>" + htmlDesc + "</html>";
        return htmlDesc;
    }

    final public void unLockDependantStep()
    {
        if (canUnLockDependantStep())
            if (_dependantSteps != null)
            {
                for (String name : _dependantSteps)
                {
                    ColorHealerModel._instance.getProtocol().unLockStep(name);
                }
                ColorHealerGui.mainWindow.rePaintMenu();
                
            }
    }

    final public void lockDependantStep(String reason)
    {
        //if (canUnLockDependantStep())
            if (_dependantSteps != null)
            {
                for (String name : _dependantSteps)
                {
                    ColorHealerModel._instance.getProtocol().lockStep(name, reason);
                    ColorHealerGui.mainWindow.rePaintMenu();
                }
                
            }
    }
}
