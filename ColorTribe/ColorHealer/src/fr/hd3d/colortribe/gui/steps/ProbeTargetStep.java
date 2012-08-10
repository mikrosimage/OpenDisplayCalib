package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.java.ugui.SpringUtilities;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.color.EStandardIlluminants;
import fr.hd3d.colortribe.color.EStandardRgbPrimaries;
import fr.hd3d.colortribe.color.IIlluminant;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.probes.AbstractProbe;
import fr.hd3d.colortribe.core.probes.CS200Probe;
import fr.hd3d.colortribe.core.probes.IProbe.EProbeType;
import fr.hd3d.colortribe.core.target.ITarget;
import fr.hd3d.colortribe.core.target.SimpleQuatuorTarget;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.components.JImageCanvas;


public class ProbeTargetStep extends Step
{

    /**
     * 
     */
    public static String NAME = "Probe and Target";
    private static final long serialVersionUID = 7306873765422762571L;

    private JList _list;
    private DefaultListModel _listModel;

    // private JButton _refreshBut;
    private JLabel _refreshLab;
    private JScrollPane _listScrollPane;

    private JButton _calibBut;
    private JTextArea _calibLab;
    private CustomTabbedPane _tabPane;

    private EProbeType _selectedProbe = null;
    private IIlluminant _selectedIlluminant = null;
    private IRgbPrimary _selectedPrimaries = null;
    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;
    private JPanel _frequencyPanel;
    private JTextField _frequencyTextField;
    private JTextField _durationTextField;

    private static List<String> _dependantSteps = new ArrayList<String>();
    static
    {
        _dependantSteps.add(LuminosityContrastStep.NAME);
        _dependantSteps.add(WhiteStep.NAME);
        _dependantSteps.add(SimpleMeasuresStep.NAME);
        _dependantSteps.add(ContinuousMeasuresStep.NAME);
        _dependantSteps.add(BatchMeasuresStep.NAME);
    }

    public ProbeTargetStep(StepStatus startStatus)
    {
        super(NAME, "Choose your target and probe\nCalibrate your probe", startStatus, _dependantSteps);

    }

    public boolean canUnLockDependantStep()
    {
        return _status == StepStatus.OK;
    }

    private void sniffProbes()
    {
        Runnable runnable = new Runnable() {
            public void run()
            {
                _refreshLab.setText("in progress...");
                _listScrollPane.setEnabled(false);
                _listModel.clear();
                Set<EProbeType> probes = ColorHealerModel._instance.getProbesPool().getProbesList();
                for (EProbeType probeType : probes)
                {
                    _listModel.addElement(probeType);
                }
                // _refreshBut.setVisible(true);
                int probesCount = probes.size();
                if (probesCount < 2)
                    _refreshLab.setText(probesCount + " probe found.");
                else
                    _refreshLab.setText(probesCount + " probes found.");
                _listScrollPane.setEnabled(true);
            }
        };
        Thread thread = new Thread(runnable, "probeListe");
        thread.start();
    }

    public void init()
    {
        if (!_isInit)
        {
            JPanel container = new JPanel();

            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setPreferredSize(new Dimension(_width, _height - 5));

            _tabPane = new CustomTabbedPane(this);

            // Probe
            JPanel probePan = new JPanel(new SpringLayout());
            TitledBorder tiledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                    "Probe");
            tiledBorder.setTitleColor(JHealerColors.TEXT_COLOR);
            probePan.setBorder(tiledBorder);

            container.add(probePan);
            getContentPane().add(container, BorderLayout.CENTER);
            _isInit = true;
            final JImageCanvas illus = new JImageCanvas("img/void_image.png", 100, 100);
            final JPanel illusPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            illusPan.add(illus);
            probePan.add(illusPan);

            _listModel = new DefaultListModel();
            _list = new JList(_listModel);
            _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            _list.setSelectedIndex(-1);
            _list.setPreferredSize(new Dimension(200, 100));
            // list.addListSelectionListener(this);
            _list.setVisibleRowCount(5);

            _list.addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e)
                {

                    _selectedProbe = (EProbeType) _list.getSelectedValue();
                    if (_selectedProbe != null)
                    {
                        ColorHealerModel._instance.setProbe(_selectedProbe);
                        _calibBut.setEnabled(true);
                        if (_selectedProbe == EProbeType.MK_CS200)
                        {
                            _frequencyPanel.setVisible(true);
                            CS200Probe probe = (CS200Probe) ColorHealerModel._instance.getProbe();
                            try
                            {
                                _frequencyTextField.setText("" + (probe.getCurrentFrequency() / 100));
                                _durationTextField.setText("" + (probe.getCurrentSpeed()));
                            }
                            catch (Exception e2)
                            {}
                        }
                        else
                            _frequencyPanel.setVisible(false);
                        illus.setImage(_selectedProbe.getImage());
                        illusPan.repaint();

                        _calibLab.setText(ColorHealerModel._instance.getProbe().getProbeDescription());
                    }
                    lock("PROBE_CHANGE");

                }

            });
            _listScrollPane = new JScrollPane(_list);
            _listScrollPane.setPreferredSize(new Dimension(210, 110));

            _refreshLab = new JLabel("in progress...");

            JPanel refreshLabPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            refreshLabPan.add(_refreshLab);
            probePan.add(refreshLabPan);

            JPanel listPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            listPan.add(_listScrollPane);
            probePan.add(listPan);

            JPanel refreshButPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            probePan.add(refreshButPan);

            sniffProbes();

            // Lay out the panel.
            SpringUtilities.makeCompactGrid(probePan, 4, 1, // rows, cols
                    2, 2, // initialX, initialY
                    1, 1);// xPad, yPad

            // Target

            JPanel targetPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JPanel springPan = new JPanel(new GridBagLayout());

            TitledBorder tiledBorder2 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                    "Target");
            tiledBorder2.setTitleColor(JHealerColors.TEXT_COLOR);
            targetPan.setBorder(tiledBorder2);

            JLabel colTempLab = new JLabel("Color temperature : ");
            final JComboBox colorTemperatureCombo = new JComboBox();
            colorTemperatureCombo.setPreferredSize(new Dimension(120, 20));

            List<IIlluminant> custIlluminants = ColorHealerModel._instance.getCustomIlluminants();
            for (IIlluminant iIlluminant : custIlluminants)
            {
                colorTemperatureCombo.addItem(iIlluminant);
                System.out.println(iIlluminant);
            }
            EStandardIlluminants[] illuminants = EStandardIlluminants.values();
            for (EStandardIlluminants e : illuminants)
            {
                colorTemperatureCombo.addItem(e);
            }

            colorTemperatureCombo.setSelectedItem(EStandardIlluminants.D65);
            _selectedIlluminant = (EStandardIlluminants) colorTemperatureCombo.getSelectedItem();
            final JLabel illuminantComentLab = new JLabel(_selectedIlluminant.getComment());
            colorTemperatureCombo.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e)
                {
                    _selectedIlluminant = (IIlluminant) colorTemperatureCombo.getSelectedItem();
                    illuminantComentLab.setText(_selectedIlluminant.getComment());
                    if (_status == StepStatus.OK)
                    {
                        lock("COLOR_TEMP_CHANGED");
                    }
                }

            });
            // /////////
            JLabel primariesLab = new JLabel("Primaries : ");
            final JComboBox primariesCombo = new JComboBox();
            colorTemperatureCombo.setPreferredSize(new Dimension(120, 20));
            List<IRgbPrimary> custPrimaries = ColorHealerModel._instance.getCustomPrimaries();
            for (IRgbPrimary iRgbPrimary : custPrimaries)
            {
                primariesCombo.addItem(iRgbPrimary);
            }
            EStandardRgbPrimaries[] primaries = EStandardRgbPrimaries.values();
            for (EStandardRgbPrimaries e : primaries)
            {
                primariesCombo.addItem(e);
            }
            primariesCombo.setSelectedItem(EStandardRgbPrimaries.REC709);
            _selectedPrimaries = (EStandardRgbPrimaries) primariesCombo.getSelectedItem();
            final JLabel primariesComentLab = new JLabel(_selectedPrimaries.getComment());
            primariesCombo.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e)
                {
                    _selectedPrimaries = (IRgbPrimary) primariesCombo.getSelectedItem();
                    primariesComentLab.setText(_selectedPrimaries.getComment());
                    if (_status == StepStatus.OK)
                    {
                        lock("PRIMARIES_CHANGED");
                    }
                }

            });
            GridBagConstraints con = new GridBagConstraints();
            con.anchor = GridBagConstraints.FIRST_LINE_START;
            con.insets = new Insets(0, 4, 4, 0);
            con.gridx = 0;
            con.gridy = 0;
            springPan.add(colTempLab, con);
            con.gridx = 1;
            springPan.add(colorTemperatureCombo, con);
            con.gridx = 2;
            springPan.add(illuminantComentLab, con);
            con.gridx = 0;
            con.gridy = 1;
            springPan.add(primariesLab, con);
            con.gridx = 1;
            springPan.add(primariesCombo, con);
            con.gridx = 2;
            springPan.add(primariesComentLab, con);

            JLabel gammaLab = new JLabel("Gamma : ");
            final JTextField gammaTex = new JTextField("2.2");
            gammaTex.setPreferredSize(new Dimension(100, 20));
            gammaTex.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e)
                {
                    super.keyTyped(e);
                    lock("GAMMA_CHANGED");
                }
            });

            // JPanel gammaPan = new JPanel(new SpringLayout());
            con.gridx = 0;
            con.gridy = 2;
            springPan.add(gammaLab, con);
            con.gridx = 1;
            springPan.add(gammaTex, con);
            // targetPan.add(gammaPan);

            JLabel lumLab = new JLabel("Max luminosity : ");
            final JTextField lumTex = new JTextField("80");
            lumTex.setPreferredSize(new Dimension(100, 20));
            lumTex.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e)
                {
                    super.keyTyped(e);
                    lock("LUM_CHANGED");
                }
            });
            // JPanel lumPan = new JPanel(new SpringLayout());
            con.gridx = 0;
            con.gridy = 3;
            springPan.add(lumLab, con);
            con.gridx = 1;
            springPan.add(lumTex, con);
            // targetPan.add(lumPan);

            targetPan.add(springPan);

            // calibrate
            JPanel calibPan = new JPanel(new GridLayout(2, 1));
            TitledBorder tiledBorder3 = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                    "Set up probe");
            tiledBorder3.setTitleColor(JHealerColors.TEXT_COLOR);
            calibPan.setBorder(tiledBorder3);

            _calibLab = new JTextArea("\n");
            // JPanel calibLabPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            // calibLabPan.add(_calibLab);

            _calibBut = new JButton("set up");
            _calibBut.setPreferredSize(new Dimension(100, 20));
            _calibBut.setEnabled(false);
            _calibBut.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    if (_calibBut.isEnabled() == true)
                    {
                        super.mousePressed(e);

                        Runnable runnable = new Runnable() {
                            public void run()
                            {
                                _calibBut.setEnabled(false);
                                _calibBut.setText("in progess...");
                                try
                                {
                                    ColorHealerModel._instance.setTarget(new SimpleQuatuorTarget(Float.valueOf(gammaTex
                                            .getText()), Float.valueOf(lumTex.getText()), _selectedIlluminant,
                                            _selectedPrimaries));
                                    AbstractProbe probe = ColorHealerModel._instance.getProbe();
                                    probe.open(null);
                                    if (probe instanceof CS200Probe)
                                    {
                                        CS200Probe cs200Probe = (CS200Probe) probe;
                                        try
                                        {
                                            cs200Probe.setSyncAndFrequency(1, Integer.valueOf(_frequencyTextField
                                                    .getText()) * 100);
                                            cs200Probe.setSpeed(5, Integer.valueOf(_durationTextField
                                                    .getText()));
                                        }
                                        catch (Exception e2)
                                        {}
                                    }
                                    valid();
                                    unLockDependantStep();
                                    _calibLab.setText(_selectedProbe.getName() + " is calibrated.");

                                }
                                catch (Exception e1)
                                {
                                    JOptionPane.showMessageDialog(null, e1.getMessage() + e1.getLocalizedMessage(),
                                            "Probe error", JOptionPane.ERROR_MESSAGE);
                                    System.out.println(e1.getMessage() + e1.getLocalizedMessage());
                                }
                                finally
                                {
                                    _calibBut.setEnabled(true);
                                    _calibBut.setText("set up");
                                }
                            }
                        };
                        Thread thread = new Thread(runnable, "probeListe");
                        thread.start();

                    }
                }
            });
            calibPan.setPreferredSize(new Dimension(_width - 2, 100));
            GridBagConstraints con1 = new GridBagConstraints();
            calibPan.setSize(new Dimension(_width - 2, 100));
            _frequencyPanel = new JPanel(new GridBagLayout());
            JLabel freqLab = new JLabel("Frequency  : ");
            JLabel freqLegendLab = new JLabel(" Hz (40-200)");
            _frequencyTextField = new JTextField("");
            _durationTextField = new JTextField("");
            Dimension tfDim = new Dimension(150, 20);
            _frequencyTextField.setPreferredSize(tfDim);
            _frequencyTextField.setSize(tfDim);
            _frequencyTextField.setMinimumSize(tfDim);
            _durationTextField.setPreferredSize(tfDim);
            _durationTextField.setSize(tfDim);
            _durationTextField.setMinimumSize(tfDim);
            con1.gridx = 0;
            con1.gridy = 0;
            con1.insets = new Insets(1, 2, 1, 1);
            _frequencyPanel.add(freqLab);
            con1.gridx++;
            _frequencyPanel.add(_frequencyTextField, con1);
            con1.gridx++;
            _frequencyPanel.add(freqLegendLab, con1);
            _frequencyPanel.setVisible(false);
            con1.gridx = 0;
            con1.gridy++;
            _frequencyPanel.add(new JLabel("Mes duration : "), con1);
            con1.gridx++;
            _frequencyPanel.add(_durationTextField, con1);
            JPanel flowPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            flowPan.add(_calibBut);
            //
            calibPan.setLayout(new GridBagLayout());

            con1.gridx = 0;
            con1.gridy = 0;
            calibPan.add(_calibLab, con1);
            con1.gridy = 1;
            calibPan.add(_frequencyPanel, con1);
            con1.gridy = 2;
            calibPan.add(flowPan, con1);
            calibPan.validate();
            // ///
            container.add(probePan);
            container.add(targetPan);
            container.add(calibPan);
            _tabPane.add(_name, container);
            getContentPane().add(_tabPane, BorderLayout.CENTER);
            _isInit = true;

        }
    }

    public void unLock()
    {
        _status = _oldStatus;
    }

    public void valid()
    {
        _status = _oldStatus = StepStatus.OK;
        unLockDependantStep();
        ITarget target = ColorHealerModel._instance.getTarget();
        _description = _selectedProbe.getName() + "\n" + target.getColorTemp().getName() + ", " + target.getGamma()
                + ", " + target.getMaxLum() + " cda/m�.";
        _tabPane.repaint();
        ColorHealerGui.mainWindow.rePaintMenu();
    }

    public void lock(String reasons)
    {
        if (_status == StepStatus.OK)
        {
            if (reasons.compareTo("PROBE_MEASURES") == 0)
            {
                _oldStatus = _status;
                _status = StepStatus.DISABLE;
            }
            else
            {
                _status = StepStatus.NOT_COMPLETE;
                if (reasons.compareTo("COLOR_TEMP_CHANGED") == 0)
                {
                    _description = "Color temperature was changed.\nCalibrate your probe. ";
                }
                else if (reasons.compareTo("PRIMARIES_CHANGED") == 0)
                {
                    _description = "Primaries were changed.\nCalibrate your probe. ";
                }
                else if (reasons.compareTo("DEVICE_TYPE_CHANGED") == 0)
                {
                    _description = "Device type was changed.\nCalibrate your probe. ";
                }
                else if (reasons.compareTo("PROBE_CHANGE") == 0)
                {
                    _description = "Probe type was changed.\nCalibrate your probe. ";

                }
                else if (reasons.compareTo("GAMMA_CHANGED") == 0)
                {
                    _description = "Gamma was changed.\nCalibrate your probe. ";
                }
                else if (reasons.compareTo("LUM_CHANGED") == 0)
                {
                    _description = "Max lum was changed.\nCalibrate your probe. ";
                }
                _calibLab.setText(ColorHealerModel._instance.getProbe().getProbeDescription());
            }

            _tabPane.repaint();
            ColorHealerGui.mainWindow.rePaintMenu();
        }
    }

}
