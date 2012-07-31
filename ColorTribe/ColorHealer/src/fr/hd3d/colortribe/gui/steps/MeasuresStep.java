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
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.probes.AbstractProbe;
import fr.hd3d.colortribe.core.probes.GammaProbeAndColorProbe;
import fr.hd3d.colortribe.core.protocols.AbstractProtocol.ProtocolEvent;
import fr.hd3d.colortribe.core.protocols.AbstractProtocol.ProtocolListener;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.components.MeasuresCanvas;


public class MeasuresStep extends Step implements ProtocolListener
{
    /**
     * 
     */
    public static String NAME = "Measure Gamma";
    private static final long serialVersionUID = 7306873765422762571L;

    private CustomTabbedPane _tabPane;
    private MeasuresCanvas _canvas;
    private JButton _measureButton;
    private JTextArea _measureExplanation;
    private JButton _abortButton;
    private boolean _canUnlock = false;
    private boolean _alreadyLocked = false;
    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;
    static List<String> _dependantSteps = new ArrayList<String>();
    boolean firstTime = true;
    private JList _list;
    private DefaultListModel _listModel;
    static
    {
        _dependantSteps.add(WhiteStep.NAME);
        _dependantSteps.add(ProbeTargetStep.NAME);
        _dependantSteps.add(DisplayStep.NAME);
        _dependantSteps.add(LuminosityContrastStep.NAME);
        _dependantSteps.add(CorrectionStep.NAME);
        _dependantSteps.add(ValidationStep.NAME);
        _dependantSteps.add(FinalisationStep.NAME);
    }
 
    public MeasuresStep()
    {
        super(NAME, "Measures", StepStatus.DISABLE, _dependantSteps);
        buildUI();
    }

    public void buildUI()
    {
        _tabPane = new CustomTabbedPane(this);

        JPanel measuresPan = new JPanel();
        measuresPan.setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        // measuresPan.setPreferredSize(new Dimension(_width, _height - 5));

        _canvas = new MeasuresCanvas();

        con.gridx = 0;
        con.gridy = 0;
        con.gridwidth = 2;
        con.anchor = GridBagConstraints.CENTER;
        con.insets = new Insets(1, 1, 1, 1);
        measuresPan.add(_canvas, con);

        _measureButton = new JButton("measure");
        _measureButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                Runnable runnable = new Runnable() {
                    public void run()
                    {
                        ColorHealerModel model = ColorHealerModel._instance;
                        _measureButton.setEnabled(false);
                        _abortButton.setVisible(true);
                        if (!_alreadyLocked)
                            lockDependantStep("PROBE_MEASURES");

                        ColorHealerGui.mainWindow.rePaintMenu();
                        model.addMeasuresSet();
                        model.getProtocol().launchGammaMeasures();
                        if (model.getProtocol().wasGammaMeasuresAborted())
                        {
                            model.removeLastMeasuresSet();
                            _canvas.repaint();
                            if (_status == StepStatus.OK){
                                unLockDependantStep();
                                _alreadyLocked = false;
                                
                            }
                            else{
                                _alreadyLocked = true;
                            }

                        }
                        else
                        {
                            int currentMeasureIndex = model.getCurrentMeasuresSetIndex();
                            model.getProtocol().computeCorrection();
                            _listModel.addElement("Measure " + currentMeasureIndex + " ("+ model.getCorrection().getComputeGamma().clampedToString() +")");
                            _list.setSelectedIndex(_listModel.getSize() - 1);
                            _canUnlock = true;
                            valid();
                            unLockDependantStep();
                            _alreadyLocked = false;
                            _measureButton.setText("re-measure");
                            _measureExplanation
                                    .setText("If you're unhappy with the last set of measures, re-measure.\nThis will create a new measure and \na corresponding correction.");
                        }
                        ColorHealerModel._instance.getSocketServer().displayColor(Color.gray, true);
                        _measureButton.setEnabled(true);
                        _abortButton.setVisible(false);
                        _list.repaint();
                        // _tabPane.repaint();
                    }
                };

                Thread thread = new Thread(runnable, "measures patches");
                thread.start();
            }
        });
        _measureButton.setPreferredSize(new Dimension(100, 20));
        _abortButton = new JButton("abort");
        _abortButton.setPreferredSize(new Dimension(100, 20));
        _abortButton.setVisible(false);
        _abortButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                ColorHealerModel model = ColorHealerModel._instance;
                model.getProtocol().abortGammaMeasures();

            }
        });
        _measureExplanation = new JTextArea(
                "Now we need to evaluate gamma values \nfor each canal (red, green, blue).\nThis will take several minutes.");
        _measureExplanation.setPreferredSize(new Dimension(250, 100));
        _measureExplanation.setEditable(false);
        con.gridx = 0;
        con.gridy = 1;
        con.gridwidth = 1;
        con.gridheight = 3;
        con.anchor = GridBagConstraints.CENTER;
        con.insets = new Insets(5, 5, 1, 1);
        measuresPan.add(_measureExplanation, con);
        con.gridx = 1;
        con.gridy = 1;
        con.gridwidth = 1;
        con.gridheight = 1;
        con.insets = new Insets(5, 1, 1, 1);
        con.anchor = GridBagConstraints.CENTER;
        measuresPan.add(_measureButton, con);
        con.gridy = 2;
        measuresPan.add(_abortButton, con);
        con.insets = new Insets(1, 1, 1, 1);
        _listModel = new DefaultListModel();
        _list = new JList(_listModel);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _list.setSelectedIndex(-1);
        _list.setBorder(BorderFactory.createLineBorder(Color.black));
        _list.setPreferredSize(new Dimension(200, 100));
        _list.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e)
            {
                ColorHealerModel._instance.setCurrentMeasuresSet(_list.getSelectedIndex());
                _canvas.repaint();
            
            }
        });
        _list.setVisibleRowCount(5);
        con.gridx = 0;
        con.gridy = 4;
        con.gridwidth = 1;
        con.insets = new Insets(1, 5, 5, 1);
        con.anchor = GridBagConstraints.FIRST_LINE_START;
        measuresPan.add(new JLabel("List of measures : "), con);
        con.insets = new Insets(1, 1, 1, 1);
        con.anchor = GridBagConstraints.CENTER;
        con.gridy = 5;
        con.gridheight = 2;
        measuresPan.add(_list, con);
        
        _tabPane.add("Measure Gamma", measuresPan);
        getContentPane().add(_tabPane, BorderLayout.CENTER);
    }

    public boolean canUnLockDependantStep()
    {
        return _canUnlock;
    }

    public void init()
    {
        ColorHealerModel model = ColorHealerModel._instance;
        if (firstTime)
        {
            model.getProtocol().addProtocolListener(this);
            firstTime = false;
        }
        _list.setSelectedIndex(model.getCurrentMeasuresSetIndex());
        AbstractProbe probe = ColorHealerModel._instance.getProbe();
        if(probe instanceof GammaProbeAndColorProbe){
            GammaProbeAndColorProbe gammaColorProbe = (GammaProbeAndColorProbe) probe;
            gammaColorProbe.selectGammaProbe();
            JOptionPane.showConfirmDialog(null,
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

    public void newMeasure(ProtocolEvent event)
    {
        _canvas.repaint();

    }

    public void measuresAbortion(ProtocolEvent event)
    {
    // TODO Auto-generated method stub

    }

    public void setStepAsked(ProtocolEvent event)
    {
    // TODO Auto-generated method stub

    }
}
