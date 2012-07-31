package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.components.JImageCanvas;


public class SubjectivLuminosityContrastStep extends LuminosityContrastStep
{

    /**
     * 
     */
    private static final long serialVersionUID = -8455699982319955862L;
    /**
     * 
     */

    protected JPanel _lumPan;
    protected JPanel _contrastPan;
    protected CustomTabbedPane _tabPane;

    LuminosityContrastStep _parent;
    private StepStatus _oldStatus = StepStatus.OK;

    public SubjectivLuminosityContrastStep(LuminosityContrastStep parent)
    {
        super();
        _parent = parent;
        buildUI();
    }

    public void buildUI()
    {
        _lumPan = new JPanel();
        GridLayout lumLay = new GridLayout(3, 1, 5, 5);
        _lumPan.setLayout(lumLay);
        _lumPan.setPreferredSize(new Dimension(_width, _height - 5));
        _contrastPan = new JPanel(new GridLayout(3, 1, 5, 5));
        _contrastPan.setPreferredSize(new Dimension(_width, _height - 5));
        _tabPane = new CustomTabbedPane(this);
        final JCheckBox mosaicBox = new JCheckBox();
        final JCheckBox mosaicBox2 = new JCheckBox();
        mosaicBox.setVisible(true);
        mosaicBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (mosaicBox.isSelected())
                {
                    try
                    {
                        ColorHealerModel._instance.getSocketServer().sendMessage(
                                "SET_MOSAIC " + ColorHealerModel._instance.getDisplayDevice().getOsIndex() + "\n");
                    }
                    catch (IllegalAccessException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    mosaicBox2.setSelected(true);
                }
                else
                {
                    try
                    {
                        ColorHealerModel._instance.getSocketServer().sendMessage(
                                "UNSET_MOSAIC " + ColorHealerModel._instance.getDisplayDevice().getOsIndex() + "\n");
                    }
                    catch (IllegalAccessException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    mosaicBox2.setSelected(false);
                }
            }
        });
        mosaicBox2.setVisible(true);
        mosaicBox2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (mosaicBox2.isSelected())
                {
                    try
                    {
                        ColorHealerModel._instance.getSocketServer().sendMessage(
                                "SET_MOSAIC " + ColorHealerModel._instance.getDisplayDevice().getOsIndex() + "\n");
                    }
                    catch (IllegalAccessException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    mosaicBox.setSelected(true);
                }
                else
                {
                    try
                    {
                        ColorHealerModel._instance.getSocketServer().sendMessage(
                                "UNSET_MOSAIC " + ColorHealerModel._instance.getDisplayDevice().getOsIndex() + "\n");
                    }
                    catch (IllegalAccessException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    mosaicBox.setSelected(false);
                }
            }
        });

        JPanel boxPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel boxLab = new JLabel("enable mosaic");
        boxPan.add(mosaicBox);
        boxPan.add(boxLab);

        JImageCanvas illus = new JImageCanvas("img/lum_pattern.png", 302, 202);
        JPanel illusPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        illusPan.add(illus);
        illusPan.setPreferredSize(new Dimension(300,300));

        JTextArea lumIndic = new JTextArea("1. Increase luminosity until you can clearly discern the 2 squares.\n"
                + "2. Find the minimal luminosity for which the right square is still visible.\n\n"
                + "--> At the end of the procedure, the left patch must be clearly visible.");
        // JPanel textPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // textPan.add(lumIndic);

        JPanel lumIndicPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lumIndicPan.add(lumIndic);

        _lumPan.add(illusPan);
        _lumPan.add(lumIndicPan);
        _lumPan.add(boxPan);

        _lumPan.validate();

        JImageCanvas illus2 = new JImageCanvas("img/cont_pattern.png", 401, 32);
        final JPanel illus2Pan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        illus2Pan.add(illus2);
        _contrastPan.add(illus2Pan);
        JTextArea contIndic = new JTextArea("1. Set contrast at maximum.\n"
                + "2. Decrease contrast until you discern white patches.\n\n");
        JPanel contIndicPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contIndicPan.add(contIndic);
        JPanel boxPan2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel boxLab2 = new JLabel("enable mosaic");
        boxPan2.add(mosaicBox2);
        boxPan2.add(boxLab2);

        _contrastPan.add(contIndicPan);
        _contrastPan.add(boxPan2);
        // /
        _tabPane.add("Luminosity", _lumPan);
        _tabPane.add("Contrast", _contrastPan);
        super.getContentPane().add(_tabPane, BorderLayout.CENTER);
    }

    @Override
    public boolean canUnLockDependantStep()
    {
        return false;
    }

    @Override
    public void init()
    {

        if (!_isInit)
        {
            _isInit = true;
        }
        try
        {
            ColorHealerModel._instance.getSocketServer().sendMessage(
                    "DISPLAY_LUM_PATT " + ColorHealerModel._instance.getDisplayDevice().getOsIndex()
                            + ColorHealerModel._instance.getTarget().getGamma() + "\n");
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void unLock()
    {
        _parent._status = _oldStatus;
        _status = _oldStatus;
    }

    @Override
    public void valid()
    {
        _parent._status = StepStatus.OK;
        _status = StepStatus.OK;
        _tabPane.repaint();
        ColorHealerGui.mainWindow.rePaintMenu();
    }

    @Override
    public void lock(String reason)
    {
        _oldStatus = _status;
        _parent._status = StepStatus.DISABLE;
        _status = StepStatus.DISABLE;
    }

}
