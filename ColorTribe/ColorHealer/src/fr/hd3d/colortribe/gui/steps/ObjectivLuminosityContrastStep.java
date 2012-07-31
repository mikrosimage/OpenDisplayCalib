package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.components.JImageCanvas;


public class ObjectivLuminosityContrastStep extends LuminosityContrastStep
{

    /**
     * 
     */
    private static final long serialVersionUID = -8455699982319955862L;
    /**
     * 
     */

    protected JPanel _lumconPan;
    protected JPanel _subjectivePan;
    protected CustomTabbedPane _tabPane;

    LuminosityContrastStep _parent;
    private StepStatus _oldStatus = StepStatus.OK;

    public ObjectivLuminosityContrastStep(LuminosityContrastStep parent)
    {
        super();
        _parent = parent;
        buildUI();
    }

    public void buildSubjectivePan(JPanel subjectivePanel)
    {
        JPanel lumPan;
        JPanel contrastPan;
        lumPan = new JPanel();
        GridBagLayout lumLay = new GridBagLayout();
        lumPan.setLayout(lumLay);
        lumPan.setPreferredSize(new Dimension(_width, (_height - 5) / 2));
        contrastPan = new JPanel(new GridLayout(2, 1, 0, 0));
        contrastPan.setPreferredSize(new Dimension(_width, (_height - 5) / 2));
        _tabPane = new CustomTabbedPane(this);
        final JCheckBox mosaicBox2 = new JCheckBox();
        mosaicBox2.setVisible(true);
        mosaicBox2.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
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
                }
            }
        });

        JImageCanvas illus = new JImageCanvas("img/lum_pattern.png", 302, 202);
        JPanel illusPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        illusPan.add(illus);
        illusPan.setPreferredSize(new Dimension(300, 200));
        illusPan.setSize(new Dimension(300, 200));
        illusPan.setMinimumSize(new Dimension(300, 200));

        JTextArea lumIndic = new JTextArea("1. Increase luminosity until you can clearly discern the 2 squares.\n"
                + "2. Find the minimal luminosity for which the right square is still visible.\n\n"
                + "--> At the end of the procedure, the left patch must be clearly visible.");
        // JPanel textPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // textPan.add(lumIndic);

        JPanel lumIndicPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lumIndicPan.add(lumIndic);
        GridBagConstraints con0 = new GridBagConstraints();
        con0.gridx = 0;
        con0.gridy = 0;
        con0.fill = GridBagConstraints.HORIZONTAL;
        TitledBorder tiledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Luminosity");
        tiledBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        lumPan.setBorder(tiledBorder);
        lumPan.add(illusPan, con0);
        con0.gridy = 1;
        lumPan.add(lumIndicPan, con0);
        lumPan.validate();

        JImageCanvas illus2 = new JImageCanvas("img/cont_pattern.png", 401, 32);
        final JPanel illus2Pan = new JPanel(new FlowLayout(FlowLayout.CENTER));

        illus2Pan.add(illus2);
        contrastPan.add(illus2Pan);
        JTextArea contIndic = new JTextArea("1. Set contrast at maximum.\n"
                + "2. Decrease contrast until you discern white patches.\n\n");
        JPanel contIndicPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contIndicPan.add(contIndic);
        JPanel boxPan2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel boxLab2 = new JLabel("enable mosaic");
        boxPan2.add(mosaicBox2);
        boxPan2.add(boxLab2);
        TitledBorder tiledBorder2 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Contrast");
        tiledBorder2.setTitleColor(JHealerColors.TEXT_COLOR);
        contrastPan.setBorder(tiledBorder2);
        contrastPan.add(contIndicPan);

        // //
        subjectivePanel.setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.fill = GridBagConstraints.HORIZONTAL;
        con.anchor = GridBagConstraints.PAGE_START;
        subjectivePanel.add(lumPan, con);
        con.gridy = 1;
        subjectivePanel.add(contrastPan, con);
        con.gridy = 2;
        subjectivePanel.add(boxPan2, con);
        con.fill = GridBagConstraints.BOTH;
        con.weighty = 1.0;
        con.gridy = 3;
        subjectivePanel.add(new JPanel(), con);

    }

    public void buildObjectivePan(JPanel objectivePanel)
    {
        objectivePanel.setLayout(new GridBagLayout());
        // / luminosity panel
        JPanel luminosity = new JPanel(new GridBagLayout());
        TitledBorder tiledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Luminosity");
        tiledBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        luminosity.setBorder(tiledBorder);
        final JLabel blackLabel = new JLabel("Black : .... cda/m²");
        Dimension labelDim = new Dimension(150, 20);
        blackLabel.setMinimumSize(labelDim);
        blackLabel.setPreferredSize(labelDim);

        JButton blackButton = new JButton("measure...");
        blackButton.setPreferredSize(new Dimension(100, 20));
        blackButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                MeasuresSet basicSet =   ColorHealerModel._instance.getBasicMeasuresSet();
                basicSet.mesureThisColor(basicSet,Color.black,"black measure", false);
                float blackLum = ((int)(basicSet.getMeasure(Color.black).getValue()._c*1000))/1000f;
                blackLabel.setText("Black : "+blackLum + " cda/m²");
                try
                {
                    ColorHealerModel._instance.getSocketServer().sendMessage(
                            "DISPLAY_LUM_PATT " + ColorHealerModel._instance.getDisplayDevice().getOsIndex()
                                    + ColorHealerModel._instance.getTarget().getGamma() + "\n");
                }
                catch (IllegalAccessException e2)
                {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                catch (IOException e2)
                {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
        });
        GridBagConstraints lumCon = new GridBagConstraints();
        lumCon.anchor = GridBagConstraints.LINE_START ;
        lumCon.gridx = 0;
        lumCon.gridy = 0;
        luminosity.add(blackLabel, lumCon);
        lumCon.gridx = 1;
        luminosity.add(blackButton, lumCon);
        // / contrast panel
        JPanel contrast = new JPanel(new GridBagLayout());
        TitledBorder tiledBorder2 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Contrast");
        tiledBorder2.setTitleColor(JHealerColors.TEXT_COLOR);
        contrast.setBorder(tiledBorder2);
        final JLabel whiteLabel = new JLabel("White : .... cda/m²");
        whiteLabel.setMinimumSize(labelDim);
        whiteLabel.setPreferredSize(labelDim);

        final JLabel contrastLabel = new JLabel("Contrast : ....");
        contrastLabel.setMinimumSize(labelDim);
        contrastLabel.setPreferredSize(labelDim);
        JButton whiteButton = new JButton("measure...");
        whiteButton.setPreferredSize(new Dimension(100, 20));
        
        
        
        whiteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                MeasuresSet basicSet =   ColorHealerModel._instance.getBasicMeasuresSet();
                basicSet.mesureThisColor(basicSet,Color.white, "white measure", false);
                float whiteLum = ((int)(basicSet.getMeasure(Color.white).getValue()._c*1000))/1000f;                
                whiteLabel.setText("White : "+whiteLum + " cda/m²");
                if(basicSet.getMeasure(Color.black )!=null){
                    float blackLum =((int)(basicSet.getMeasure(Color.black).getValue()._c*1000))/1000f;
                    int contrastValue = (int)(whiteLum / blackLum);
                    contrastLabel.setText("Contrast : "+ contrastValue);
                }
                try
                {
                    ColorHealerModel._instance.getSocketServer().sendMessage(
                            "DISPLAY_LUM_PATT " + ColorHealerModel._instance.getDisplayDevice().getOsIndex()
                                    + ColorHealerModel._instance.getTarget().getGamma() + "\n");
                }
                catch (IllegalAccessException e2)
                {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                catch (IOException e2)
                {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }
        });
        
        lumCon.anchor = GridBagConstraints.LINE_START ;
        lumCon.gridx = 0;
        lumCon.gridy = 0;
        contrast.add(whiteLabel, lumCon);
        lumCon.gridx = 1;
        contrast.add(whiteButton, lumCon);
        lumCon.gridx =0;
        lumCon.gridy = 1;
        lumCon.gridwidth=2;
        contrast.add(contrastLabel, lumCon);
        // /
        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.weightx = 1.0;
    
        con.fill = GridBagConstraints.HORIZONTAL;
        con.anchor = GridBagConstraints.FIRST_LINE_START;;
        objectivePanel.add(luminosity, con);
        con.gridy = 1;
        objectivePanel.add(contrast,con);
        con.fill = GridBagConstraints.BOTH;
        con.weighty = 1.0;
        con.gridy = 2;
        objectivePanel.add(new JPanel(), con);
        objectivePanel.validate();
    }

    public void buildUI()
    {
        _lumconPan = new JPanel();
        _lumconPan.setPreferredSize(new Dimension(_width, _height - 5));
        buildObjectivePan(_lumconPan);
        _subjectivePan = new JPanel();
        _subjectivePan.setPreferredSize(new Dimension(_width, _height - 5));
        buildSubjectivePan(_subjectivePan);
        _tabPane = new CustomTabbedPane(this);
        _tabPane.add("Luminosity and Contrast", _lumconPan);
        _tabPane.add("Check patterns", _subjectivePan);

        getContentPane().add(_tabPane, BorderLayout.CENTER);
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
