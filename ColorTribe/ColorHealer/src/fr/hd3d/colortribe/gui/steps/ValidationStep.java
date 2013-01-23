package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.correction.AbstractCorrection;
import fr.hd3d.colortribe.core.probes.AbstractProbe;
import fr.hd3d.colortribe.core.probes.GammaProbeAndColorProbe;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;


public class ValidationStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Validation";
    private static final long serialVersionUID = 7306873765422762571L;
    private JList _list;
    private DefaultListModel _listModel;
    private CustomTabbedPane _tabPane;
    private boolean _canUnlock = false;
    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;
    private static List<String> _dependantSteps = new ArrayList<String>();
    
    private JLabel targetLab;
    private JLabel calculatedGammaLab;
    private JLabel deltacalculatedGammaLab;
    private JLabel calibratedGammaLab;
    private JLabel deltaGammaLab;
    static
    {}

    public ValidationStep()
    {
        super(NAME, "Measure delta for selected correction", StepStatus.DISABLE, _dependantSteps);

        buildUI();
    }

    public void buildUI()
    {
        JLabel selectLab = new JLabel("Select correction :");
        final TitledBorder tiledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Deltas");
        JPanel validationPan = new JPanel();
        _listModel = new DefaultListModel();
        _list = new JList(_listModel);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _list.setSelectedIndex(-1);
        _list.setBorder(BorderFactory.createLineBorder(Color.black));
        Dimension listDim = new Dimension(350, 100);
        _list.setPreferredSize(listDim);
        _list.setSize(listDim);
        _list.setMinimumSize(listDim);
        _list.setVisibleRowCount(5);
        _list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e)
            {
                ColorHealerModel model = ColorHealerModel._instance;
                int index = _list.getSelectedIndex();
                if (index == -1)
                    index = _list.getComponentCount() - 1;
                model.setCurrentCorrection(index);
                model.getCorrection().computeColorCorrection();
                model.getCorrection().sendLut();
                ColorHealerModel._instance.getSocketServer().displayColor(Color.gray, true);
                tiledBorder.setTitle("Deltas for correction " + model.getCurrentCorrectionIndex());
                updateLabels();
            }
        });
        // /
        final JButton computeDeltaButton = new JButton("compute");
        computeDeltaButton.setPreferredSize(new Dimension(120, 20));
        computeDeltaButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);

                Runnable runnable = new Runnable() {
                    public void run()
                    {
                        computeDeltaButton.setEnabled(false);
                        ColorHealerModel._instance.getProtocol().launchValidationMeasures();
                        computeDeltaButton.setEnabled(true);
                        updateList();
                        updateLabels();
                    }
                };

                Thread thread = new Thread(runnable, "measures patches");
                thread.start();

            }
        });

        targetLab = new JLabel("Target gamma : ");
        calculatedGammaLab = new JLabel("Calculated gamma : ");
        deltacalculatedGammaLab = new JLabel("Deltas : ");
        calibratedGammaLab = new JLabel("Calibrated gamma : ");
        deltaGammaLab = new JLabel("Deltas : ");

        JPanel deltasPanel = new JPanel();
        tiledBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        deltasPanel.setBorder(tiledBorder);
        deltasPanel.setLayout(new GridBagLayout());
        GridBagConstraints deltaCon = new GridBagConstraints();
        deltaCon.gridx = 0;
        deltaCon.gridy = 0;
        deltaCon.insets = new Insets(2, 2, 2, 2);
        deltaCon.fill = GridBagConstraints.HORIZONTAL;
        deltaCon.anchor = GridBagConstraints.FIRST_LINE_START;
        deltasPanel.add(targetLab, deltaCon);
        deltaCon.gridy++;
        deltasPanel.add(calculatedGammaLab, deltaCon);
        deltaCon.gridy++;
        deltasPanel.add(deltacalculatedGammaLab, deltaCon);
        deltaCon.gridy++;
        deltasPanel.add(calibratedGammaLab, deltaCon);
        deltaCon.gridy++;
        deltasPanel.add(deltaGammaLab, deltaCon);
        deltaCon.gridy++;
        deltasPanel.add(computeDeltaButton, deltaCon);
        // /
        // ////////layout
        _tabPane = new CustomTabbedPane(this);
        validationPan.setLayout(new GridBagLayout());

        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.insets = new Insets(2, 2, 2, 2);
        con.anchor = GridBagConstraints.LINE_START;
        con.fill = GridBagConstraints.HORIZONTAL;

        validationPan.add(selectLab, con);
        con.gridy++;
        validationPan.add(_list, con);
        con.gridy++;

        con.weightx = 1;
        validationPan.add(deltasPanel, con);
        _tabPane.add("Validation", validationPan);
        validationPan.setPreferredSize(new Dimension(_width, _height - 5));
        getContentPane().add(_tabPane, BorderLayout.CENTER);

    }

    public boolean canUnLockDependantStep()
    {
        return _canUnlock;
    }

    private void updateList()
    {
        ColorHealerModel model = ColorHealerModel._instance;
        _listModel.clear();
        for (int i = 0; i < model.getNumberOfCorrection(); i++)
        {
            Point3f delta = model.getCorrectionDelta(i);
            String deltaString = "";
            if (delta == null)
                deltaString = "(No delta available for correction)";
            else
                deltaString = "(Delta : " + delta.clampedToString() + ")";
            _listModel.addElement("Correction " + i + " --- " + deltaString);
        }
        _list.setSelectedIndex(model.getCurrentMeasuresSetIndex());
        if (_list.getSelectedIndex() == -1)
            _list.setSelectedIndex(0);
        _list.updateUI();
    }

    private void updateLabels()
    {
        ColorHealerModel model = ColorHealerModel._instance;
        AbstractCorrection correction = model.getCorrection();
        targetLab.setText("Target gamma : " + model.getTarget().getGamma());
        Point3f computeGamma = correction.getComputeGamma();
        Point3f deltaComputeGamma = correction.getDelta();
        calculatedGammaLab.setText("Calculate gamma : " + computeGamma.clampedToString());
        deltacalculatedGammaLab.setText("Deltas : " + deltaComputeGamma.clampedToString());
        Point3f calibratedGamma = correction.getCalibratedGamma();
        if (calibratedGamma != null)
        {
            calibratedGammaLab.setText("Calibrated gamma : " + calibratedGamma.clampedToString());
            deltaGammaLab.setText("Deltas : " + correction.getCalibratedDelta().clampedToString());
        }
        else
        {
            calibratedGammaLab.setText("Calibrated gamma : ");
            deltaGammaLab.setText("Deltas : ");
        }
    }

    public void init()
    {
//        CHSocketServer.getInstance().displayColor(Color.gray, true);
        updateList();
        updateLabels();
        AbstractProbe probe = ColorHealerModel._instance.getProbe();
        if(probe instanceof GammaProbeAndColorProbe){
            GammaProbeAndColorProbe gammaColorProbe = (GammaProbeAndColorProbe) probe;
            try {
				gammaColorProbe.selectGammaProbe();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            JOptionPane.showMessageDialog(null,
                    "Use " +  gammaColorProbe.getSelectedProbeType() + " here !", "Select right probe",
                    JOptionPane.OK_OPTION);
        }

    }

    public void unLock()
    {
        _status = _oldStatus;
    }

    public void valid()
    {
        _status = StepStatus.OK;
    }

    public void lock(String reason)
    {
        if (_status != StepStatus.DISABLE)
        {
            _oldStatus = _status;
            _status = StepStatus.DISABLE;
        }
    }

}
