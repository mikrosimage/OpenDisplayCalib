package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;


public class BeforeStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Before";
    private static final long serialVersionUID = 7306873765422762571L;

    public BeforeStep()
    {
        super(NAME, "Please read", StepStatus.OK, null);
        buildUI();
    }

    public void buildUI()
    {
        JTextArea instructionRecalls = new JTextArea();
        JTextArea instructionList = new JTextArea();

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setPreferredSize(new Dimension(_width, _height - 5));
        instructionRecalls.append("\n" + "> a calibration is valid for a given frequency and resolution.\n"
                + "> it is also set for specific OSD parameters (gain, bias, lum and contrast).\n\n"
                + "---> This means that if you change these values, the calibration won't be correct anymore.\n");
        instructionList.append("\n" + "> Check the geometry of your device.\n"
                + "> Check that your device displays correctly the gray ramp.\n"
                + "\n(you can use the different Migal test patterns to check these)");

        TitledBorder recallBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Recalls");
        recallBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        JPanel recallLabelPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recallLabelPan.setBorder(recallBorder);
        recallLabelPan.add(instructionRecalls);
        container.add(recallLabelPan);

        TitledBorder listBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Checks list ");
        listBorder.setTitleColor(JHealerColors.TEXT_COLOR);

        JPanel listLabelPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
        listLabelPan.setBorder(listBorder);
        listLabelPan.add(instructionList);
        container.add(listLabelPan);

        CustomTabbedPane tabPane = new CustomTabbedPane(this);

        tabPane.add(_name, container);
        tabPane.add("About", new JPanel());

        getContentPane().add(tabPane, BorderLayout.CENTER);
    }

    public boolean canUnLockDependantStep()
    {
        return false;
    }

    public void init()
    {
        Runnable runnable = new Runnable() {
            public void run()
            {
                //preload
                ColorHealerModel._instance.getProbesPool().getRefreshedProbesList();             
            }
        };
        Thread thread = new Thread(runnable, "probeListe2");
        thread.start();
    }

    public void unLock()
    {}

    public void valid()
    {
    // do nthg

    }

    public void lock(String reason)
    {}

}
