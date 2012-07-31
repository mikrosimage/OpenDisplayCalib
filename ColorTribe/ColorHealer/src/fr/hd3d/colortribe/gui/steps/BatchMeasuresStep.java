package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import fr.hd3d.colortribe.color.EStandardRgbPrimaries;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.com.UnpluggedSocketServer;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.DisplayDevice;
import fr.hd3d.colortribe.core.MeasuresSet;
import fr.hd3d.colortribe.core.DisplayDevice.EDisplayDeviceType;
import fr.hd3d.colortribe.core.correction.AbstractCorrection;
import fr.hd3d.colortribe.core.probes.AbstractProbe;
import fr.hd3d.colortribe.core.probes.CS200Probe;
import fr.hd3d.colortribe.core.target.ITarget;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.components.JCIE31PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.JCIE76PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.MeasuresCanvas;


public class BatchMeasuresStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Batch measures";
    private static final long serialVersionUID = 7306873765422762571L;

    private JCIE31PrimariesCanvas _white31Canvas;
    private JCIE76PrimariesCanvas _white76Canvas;
    private boolean _isInit = false;
    private JComboBox _primariesCombo;
    JTextArea _resume;
    private JFileChooser _fileChooser;
    ArrayList<Color> _colorPatches = new ArrayList<Color>();;
    private JButton _launchBatchButton;
    private CustomTabbedPane _tabPane;
    private JLabel _displayPatchLabel;
    private JButton _readyButton;
    private boolean _wasReadyClicked = false;
    private MeasuresCanvas _mesuresCanvas;
    private JLabel _gammaValuesLab;

    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;

    static List<String> _dependantSteps = new ArrayList<String>();
    static
    {
        _dependantSteps.add(ProbeTargetStep.NAME);
        _dependantSteps.add(SimpleMeasuresStep.NAME);
        _dependantSteps.add(ContinuousMeasuresStep.NAME);
    }

    public BatchMeasuresStep()
    {
        super(NAME, "Load a batch, place\n the probe on the patch\nAnd start measures.", StepStatus.DISABLE,
                _dependantSteps);
        buildUI();
    }

    public void buildUI()
    {
        _tabPane = new CustomTabbedPane(this);
        _mesuresCanvas = new MeasuresCanvas();
        JPanel whitePan = new JPanel();
        Dimension panDim = new Dimension(_width, _height - 5);
        whitePan.setPreferredSize(panDim);
        whitePan.setSize(panDim);
        whitePan.setMinimumSize(panDim);
        final JPanel videoOptionsPan = new JPanel(new GridBagLayout());
        videoOptionsPan.setBackground(getBackground().brighter());
        final JPanel manualOptionsPan = new JPanel(new GridBagLayout());
        manualOptionsPan.setBackground(getBackground().brighter());

        // //////////white pane
        _white31Canvas = new JCIE31PrimariesCanvas(EStandardRgbPrimaries.REC709, 160, 160);
        _white76Canvas = new JCIE76PrimariesCanvas(EStandardRgbPrimaries.REC709, 160, 160);

        final JPanel canvasPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        canvasPan.add(_white76Canvas);

        final JPanel batchPan = new JPanel(new GridBagLayout());
        TitledBorder batchBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Batch");
        batchBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        batchPan.setBorder(batchBorder);
        // components
        Dimension tfDim = new Dimension(120, 20);
        JLabel batchFileLab = new JLabel("Batch file : ");
        batchFileLab.setPreferredSize(new Dimension(140, 20));
        JButton batchFileButton = new JButton("Load...");
        batchFileButton.setPreferredSize(new Dimension(60, 20));
        final JLabel batchDescLabel = new JLabel("0 patch loaded.");
        batchFileButton.addMouseListener(new MouseAdapter() {
            private JFileChooser _fileChooser;

            @Override
            public void mouseClicked(MouseEvent e)
            {
                _fileChooser = new JFileChooser(".");

                int returnVal = _fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = _fileChooser.getSelectedFile();
                    try
                    {
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line = reader.readLine();
                        int patchCount = Integer.parseInt(line);

                        int r = 0, g = 0, b = 0;

                        int beg;
                        _colorPatches.clear();
                        for (int i = 0; i < patchCount; i++)
                        {
                            line = reader.readLine();
                            beg = line.indexOf(" ");
                            r = Integer.parseInt(line.substring(0, beg));
                            line = line.substring(beg + 1);
                            beg = line.indexOf(" ");
                            g = Integer.parseInt(line.substring(0, beg));
                            line = line.substring(beg + 1);
                            b = Integer.parseInt(line);
                            _colorPatches.add(new Color(r, g, b));
                        }
                        batchDescLabel.setText(_colorPatches.size() + " patch(es) loaded.");
                        reader.close();
                        valid();

                    }
                    catch (FileNotFoundException e1)
                    {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);

                    }
                    catch (IOException e2)
                    {
                        JOptionPane.showMessageDialog(null, e2.getMessage(), "File reader error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });

        JLabel patcherLab = new JLabel("Patches synchro : ");
        final JComboBox patcherList = new JComboBox();
        patcherList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                String selection = (String) patcherList.getSelectedItem();

                if (selection.compareTo("Video") == 0)
                {
                    videoOptionsPan.setVisible(true);
                    manualOptionsPan.setVisible(false);
                }
                else if (selection.compareTo("Manual") == 0)
                {

                    manualOptionsPan.setVisible(true);
                    videoOptionsPan.setVisible(false);
                }
                else
                {

                    manualOptionsPan.setVisible(false);
                    videoOptionsPan.setVisible(false);
                }
                batchPan.validate();
            }
        });
        if (!(ColorHealerModel._instance.getSocketServer() instanceof UnpluggedSocketServer))
            patcherList.addItem("ColorKeeper");

        patcherList.addItem("Manual");
        manualOptionsPan.setVisible(false);
        patcherList.addItem("Video");
        videoOptionsPan.setVisible(false);
        patcherList.setSelectedIndex(0);
        patcherList.setPreferredSize(new Dimension(120, 20));
        _launchBatchButton = new JButton("Launch batch");
        _launchBatchButton.setPreferredSize(tfDim);
        // //manual mode
        JLabel manualLabel = new JLabel("Manual mode allows you to measure patches step by step.");
        _displayPatchLabel = new JLabel("Launch batch first.");
        _readyButton = new JButton("Go");
        _readyButton.setPreferredSize(new Dimension(60, 20));
        _readyButton.setEnabled(false);
        _readyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (_readyButton.isEnabled())
                {
                    _wasReadyClicked = true;
                }
            }

        });
        GridBagConstraints conManual = new GridBagConstraints();
        conManual.gridx = 0;
        conManual.gridy = 0;
        conManual.fill = GridBagConstraints.BOTH;
        conManual.anchor = GridBagConstraints.LINE_START;
        conManual.insets = new Insets(2, 4, 2, 4);
        conManual.gridwidth = 2;

        manualOptionsPan.add(manualLabel, conManual);
        conManual.gridy++;
        conManual.gridwidth = 1;
        conManual.weightx = 0.6f;
        conManual.fill = GridBagConstraints.VERTICAL;
        manualOptionsPan.add(_displayPatchLabel, conManual);
        conManual.weightx = 0.4;
        conManual.gridx++;
        manualOptionsPan.add(_readyButton, conManual);

        // //video mode

        JLabel durationLabel = new JLabel("Probe measure duration : ");
        JLabel delayLabel = new JLabel("Video patch duration : ");
        Dimension smallTfDim = new Dimension(60, 20);
        final JTextField durationTextField = new JTextField("3");
        durationTextField.setPreferredSize(smallTfDim);
        final JTextField delayTextField = new JTextField("5");
        delayTextField.setPreferredSize(smallTfDim);
        GridBagConstraints conVideo = new GridBagConstraints();
        JLabel videoLabel = new JLabel("Video mode allows you to synchronize measures with a patches video.");
        JLabel timeToLaunchLabel = new JLabel("How many time for video launching :");
        JLabel secLabel = new JLabel("sec.");
        final JLabel realProbeDurationLab = new JLabel("Real measure duration : ---- s. (must be < to patch duration) ");
        final JButton checkButton = new JButton("evaluate");
        checkButton.setPreferredSize(new Dimension(60, 20));
        checkButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (checkButton.isEnabled())
                {
                    checkButton.setEnabled(false);
                    Thread thread;
                    Runnable runnable = new Runnable() {
                        public void run()
                        {
                            AbstractProbe probe = ColorHealerModel._instance.getProbe();
                            if (probe instanceof CS200Probe)
                            {
                                CS200Probe cs200Probe = (CS200Probe) probe;
                                int duration = Integer.valueOf(durationTextField.getText());
                                try
                                {
                                    if (duration != cs200Probe.getCurrentSpeed())
                                        cs200Probe.setSpeed(5, duration);
                                }
                                catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }

                            }
                            long startMes = System.currentTimeMillis();
                            MeasuresSet tmp = new MeasuresSet();
                            tmp.mesureThisColor(tmp, new Color(128, 128, 128), "check");
                            float measureDuration = (float) ((System.currentTimeMillis() - startMes) / 1000f);
                            realProbeDurationLab.setText("Real measure duration : " + measureDuration
                                    + " s. (must be < to patch duration) ");
                            checkButton.setEnabled(true);

                        }
                    };
                    // start thread
                    thread = new Thread(runnable, "mesure");
                    thread.start();

                }
            }
        });

        final JTextField timeToLaunchTF = new JTextField("3");
        timeToLaunchTF.setPreferredSize(smallTfDim);
        conVideo.gridx = 0;
        conVideo.gridy = 0;
        conVideo.fill = GridBagConstraints.VERTICAL;
        conVideo.anchor = GridBagConstraints.LINE_START;
        conVideo.insets = new Insets(2, 4, 2, 4);
        conVideo.gridwidth = 4;
        videoOptionsPan.add(videoLabel, conVideo);
        conVideo.gridy++;
        conVideo.gridwidth = 2;
        videoOptionsPan.add(timeToLaunchLabel, conVideo);
        conVideo.gridx += conVideo.gridwidth;
        conVideo.gridwidth = 1;
        videoOptionsPan.add(timeToLaunchTF, conVideo);
        conVideo.gridx++;
        videoOptionsPan.add(secLabel, conVideo);
        conVideo.gridx = 0;
        conVideo.gridy++;
        videoOptionsPan.add(durationLabel, conVideo);
        conVideo.gridx++;
        videoOptionsPan.add(durationTextField, conVideo);
        conVideo.gridx++;
        videoOptionsPan.add(delayLabel, conVideo);
        conVideo.gridx++;
        videoOptionsPan.add(delayTextField, conVideo);
        conVideo.gridwidth = 3;
        conVideo.gridx = 0;
        conVideo.gridy++;
        videoOptionsPan.add(realProbeDurationLab, conVideo);
        conVideo.gridx += conVideo.gridwidth;
        conVideo.gridwidth = 1;
        videoOptionsPan.add(checkButton, conVideo);

        _launchBatchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {

                if (_launchBatchButton.isEnabled() && _colorPatches.size() > 0)
                {
                    AbstractProbe probe = ColorHealerModel._instance.getProbe();
                    boolean isVideoMode = false;
                    boolean isManualMode = false;
                    String selection = (String) patcherList.getSelectedItem();
                    if (selection.compareTo("Video") == 0)
                        isVideoMode = true;
                    else if (selection.compareTo("Manual") == 0)
                        isManualMode = true;
                    if (probe instanceof CS200Probe && isVideoMode)
                    {
                        CS200Probe cs200Probe = (CS200Probe) probe;
                        int duration = Integer.valueOf(durationTextField.getText());
                        try
                        {
                            if (duration != cs200Probe.getCurrentSpeed())
                                cs200Probe.setSpeed(5, duration);
                            System.out.println("current speed " + cs200Probe.getCurrentSpeed());
                        }
                        catch (Exception e1)
                        {
                            e1.printStackTrace();
                        }

                    }
                    if (isVideoMode)
                    {
                        int patchDuration = Integer.valueOf(delayTextField.getText());
                        int waitTime = Integer.valueOf(timeToLaunchTF.getText());
                        automaticBatchedMeasures(patchDuration * 1000, waitTime * 1000);
                    }
                    else if (isManualMode)
                    {
                        manualBatchedMeasures();
                    }
                    else
                        automaticBatchedMeasures(1, 1);
                }
            }
        });

        // layout
        GridBagConstraints conBatch = new GridBagConstraints();
        conBatch.gridx = 0;
        conBatch.gridy = 0;
        conBatch.fill = GridBagConstraints.VERTICAL;
        conBatch.anchor = GridBagConstraints.LINE_START;
        conBatch.insets = new Insets(2, 4, 2, 4);
        batchPan.add(batchFileLab, conBatch);
        conBatch.gridx++;
        conBatch.fill = GridBagConstraints.BOTH;
        conBatch.weightx = 1;
        batchPan.add(batchDescLabel, conBatch);
        conBatch.gridx++;
        batchPan.add(batchFileButton, conBatch);
        conBatch.gridx++;
        batchPan.add(patcherLab, conBatch);
        conBatch.gridx++;
        batchPan.add(patcherList, conBatch);
        conBatch.gridy++;
        conBatch.gridx = 0;
        conBatch.gridwidth = 5;
        batchPan.add(videoOptionsPan, conBatch);
        conBatch.gridy++;
        batchPan.add(manualOptionsPan, conBatch);
        conBatch.gridwidth = 1;
        conBatch.gridy++;
        conBatch.gridx += 2;
        batchPan.add(_launchBatchButton, conBatch);

        // ///////////
        JPanel measurePan = new JPanel(new GridBagLayout());
        TitledBorder measureBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Measures");
        measureBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        measurePan.setBorder(measureBorder);

        _resume = new JTextArea();

        _resume.setEditable(false);
        JScrollPane resumeScrollPane = new JScrollPane(_resume, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        resumeScrollPane.setBorder(null);
        resumeScrollPane.setAutoscrolls(true);
        Dimension dim2 = new Dimension(_width - 2, 200);
        resumeScrollPane.setSize(dim2);
        resumeScrollPane.setPreferredSize(dim2);

        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1;
        con.weighty = 1;
        measurePan.add(resumeScrollPane, con);

        JPanel displayPan = new JPanel(new GridBagLayout());
        GridBagConstraints con5 = new GridBagConstraints();
        TitledBorder displayBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Display");
        displayBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        displayPan.setBorder(displayBorder);
        Dimension comboDim = new Dimension(150, 20);
        _primariesCombo = new JComboBox();
        _primariesCombo.setPreferredSize(comboDim);
        _primariesCombo.setSize(comboDim);

        _primariesCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0)
            {
                _white31Canvas.setPrimariesToDisplay((IRgbPrimary) _primariesCombo.getSelectedItem());
                _white31Canvas.repaint();
                _white76Canvas.setPrimariesToDisplay((IRgbPrimary) _primariesCombo.getSelectedItem());
                _white76Canvas.repaint();

            }
        });
        final JComboBox chartsCombo = new JComboBox();
        chartsCombo.setPreferredSize(comboDim);
        chartsCombo.setSize(comboDim);
        chartsCombo.addItem("CIE 1976");
        chartsCombo.addItem("CIE 1931");
        chartsCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e)
            {
                canvasPan.removeAll();
                if (chartsCombo.getSelectedIndex() == 0)
                {
                    canvasPan.add(_white76Canvas);
                }
                else if (chartsCombo.getSelectedIndex() == 1)
                {
                    canvasPan.add(_white31Canvas);
                }
                canvasPan.validate();
                canvasPan.repaint();

            }
        });
        con5.gridx = 0;
        con5.gridy = 0;
        con5.gridwidth = 1;

        con5.anchor = GridBagConstraints.FIRST_LINE_START;
        con5.fill = GridBagConstraints.BOTH;
        con5.insets = new Insets(0, 3, 0, 3);
        displayPan.add(new JLabel("Primaries :"), con5);
        con5.gridx = 1;
        displayPan.add(_primariesCombo, con5);
        con5.gridx = 2;
        displayPan.add(new JLabel("Chart :"), con5);
        con5.gridx = 3;
        displayPan.add(chartsCombo, con5);
        // /

        JButton clearMeasure = new JButton("clear measures");
        clearMeasure.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                MeasuresSet set = ColorHealerModel._instance.getCurrentMeasuresSet();
                set.clear();
                String info = "  R   G   B  \t" + "   x  " + "\t" + "   y  " + "\t" + "   Y   \tLabel\n";
                info += set.toString();
                _resume.setText(info);
                _white31Canvas.repaint();
                _white76Canvas.repaint();
            }
        });
        final JButton exportMeasures = new JButton("export measures");
        exportMeasures.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e)
            {
                _fileChooser = new JFileChooser("Choose a directory");
                _fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String saveDate = ColorHealerModel.getFormatDate();
                ColorHealerModel model = ColorHealerModel._instance;
                final MeasuresSet measures = model.getCurrentMeasuresSet();
                File dir = new File("summaries");
                _fileChooser.setCurrentDirectory(dir);
                DisplayDevice dispDev = model.getDisplayDevice();
                int returnVal = _fileChooser.showSaveDialog(exportMeasures);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = new File(_fileChooser.getSelectedFile().getPath() + "\\batchColors_"
                            + dispDev.getProfilName() + "_" + saveDate + ".html");
                    File file2 = new File(_fileChooser.getSelectedFile().getPath() + "\\batchColors_"
                            + dispDev.getProfilName() + "_" + saveDate + "diag.jpg");
                    File file3 = new File(_fileChooser.getSelectedFile().getPath() + "\\batchColors_"
                            + dispDev.getProfilName() + "_" + saveDate + "diag2.jpg");
                    BufferedImage diagram;
                    String diagramTitle;
                    if (chartsCombo.getSelectedIndex() == 0) // 76
                    {
                        diagram = _white76Canvas.getBufferedImage();

                        diagramTitle = "CIE 1976";
                    }
                    else
                    // 31
                    {
                        diagram = _white31Canvas.getBufferedImage();
                        diagramTitle = "CIE 1931";
                    }

                    BufferedImage diagram2 = _mesuresCanvas.getBufferedImage();
                    
                    String displayedPrimaries = _white31Canvas.getPrimariesToDisplay().getName();
                    String targetWhite = model.getTarget().getColorTemp().getName();
                    try
                    {
                        ImageIO.write(diagram, "JPG", file2);
                        ImageIO.write(diagram2, "JPG", file3);
                    }
                    catch (IOException e2)
                    {
                        JOptionPane.showMessageDialog(null, "Failed to write : " + file2.getPath(), null,
                                JOptionPane.ERROR_MESSAGE);
                    }

                    String infos;
                    infos = new String("Machine : " + model.getClientName() + " (" + model.getVenue() + ")" + "<br>\n"
                            + "Display device : " + dispDev.getManufacturer() + " " + dispDev.getModel() + " "
                            + dispDev.getUid() + " (" + dispDev.getStringType() + ")" + "<br>\n");
                    if (model.getDisplayDevice().getType() == EDisplayDeviceType.PROJECTOR)
                    {
                        infos += "Bulb hours count : " + model.getBulbHoursCount() + "<br>\n";
                        infos += "Calibration format : " + model.getCalibrationFormat() + "<br>\n";
                    }

                    infos += "Probe : " + model.getProbe().getEProbeType() + "<br><br>\n";
                    String title = "Measures " + saveDate + " " + dispDev.getManufacturer() + " " + dispDev.getModel()
                            + " " + dispDev.getUid();
                    BufferedWriter sortie;
                    try
                    {
                        sortie = new BufferedWriter(new FileWriter(file));
                        sortie.append("<html>\n<head>\n <title>\n" + title
                                + "\n</title>\n</head>\n<body style=\"font-family: Verdana; font-size: 11px;\">\n");
                        sortie.append("<div style=\"text-align: center;\">\n <b>" + title + "</b><br><br><img title=\""
                                + diagramTitle + "\" src=\"" + file2.getName() + "\">\n<br>Diagram : " + diagramTitle
                                + ", " + displayedPrimaries + ", " + targetWhite + "</div>\n");
                        sortie.append("<div >\n<b>Infos</b><br><br>\n" + infos + "\n</div>\n");
                        sortie.append("<div >\n<b>Measures</b><br><br>\n" + measures.htmlToString() + "\n</div><br>\n");
                        sortie.append("<div >\n<b>Gamma</b><br><br><img title=\""
                                + diagramTitle + "\" src=\"" + file3.getName() + "\">\n<br>\n" + _gammaValuesLab.getText()+ "\n</div>\n");

                        sortie.append("</body>\n</html>");
                        sortie.close();
                    }
                    catch (IOException e1)
                    {
                        JOptionPane.showMessageDialog(null, " Failed to write : " + file.getPath(), null,
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            }

        });
        // Layout
        whitePan.setLayout(new GridBagLayout());
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.gridwidth = 2;
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1;
        con.weighty = 0.3;

        whitePan.add(canvasPan, con);
        con.gridy++;
        con.weighty = 0.1;
        whitePan.add(batchPan, con);
        con.weighty = 0.1;
        con.gridy++;

        whitePan.add(measurePan, con);
        con.gridy++;
        con.weighty = 0.03;
        whitePan.add(displayPan, con);

        con.gridy++;
        con.gridwidth = 1;
        con.weighty = 0.01;
        con.fill = GridBagConstraints.VERTICAL;
        con.anchor = GridBagConstraints.CENTER;
        whitePan.add(clearMeasure, con);
        con.gridx = 1;
        whitePan.add(exportMeasures, con);

        _tabPane.add("Measures", whitePan);
        // /

        JPanel gammaPanel = new JPanel(new GridBagLayout());
        final JPanel gammaValuePanel = new JPanel(new GridBagLayout());
        TitledBorder gammaBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                "Gamma approximation");
        gammaBorder.setTitleColor(JHealerColors.TEXT_COLOR);
        gammaValuePanel.setBorder(gammaBorder);
        _gammaValuesLab = new JLabel("Red gamma : ---  Green gamma : --- Blue Gamma : --- ");
        GridBagConstraints conGamma = new GridBagConstraints();
        conGamma.gridx = 0;
        conGamma.gridy = 0;
        conGamma.gridwidth = 1;
        conGamma.fill = GridBagConstraints.BOTH;
        conGamma.weightx = 1;
        gammaValuePanel.add(_gammaValuesLab);

        // layout
        conGamma = new GridBagConstraints();
        conGamma.gridx = 0;
        conGamma.gridy = 0;
        conGamma.gridwidth = 1;
        conGamma.fill = GridBagConstraints.BOTH;
        conGamma.anchor = GridBagConstraints.PAGE_START;

        gammaPanel.add(_mesuresCanvas, conGamma);
        conGamma.gridy++;
        gammaPanel.add(gammaValuePanel, conGamma);
        conGamma.gridy++;
        conGamma.weighty = 1;
        gammaPanel.add(new JPanel(), conGamma);

        _tabPane.add("Gamma view", gammaPanel);

        getContentPane().add(_tabPane, BorderLayout.CENTER);
    }

    public class BatchAction
    {
        Timer t;

        public BatchAction(int actionDuration)
        {
            t = new Timer();
            _launchBatchButton.setText("in progress");
            _launchBatchButton.setEnabled(false);
            lockDependantStep("PROBE_MEASURES");
            t.schedule(new OneMeasureAction(), 0, actionDuration);
        }

        class OneMeasureAction extends TimerTask
        {
            int nbPatchMeasured = 0;

            public void run()
            {

                if (nbPatchMeasured < _colorPatches.size())
                {
                    Color color = _colorPatches.get(nbPatchMeasured);
                    ColorHealerModel model = ColorHealerModel._instance;
                    final MeasuresSet measures = model.getCurrentMeasuresSet();
                    boolean res = measures.mesureThisColor(model.getCurrentMeasuresSet(), color, "");
                    if (!res)
                        return;

                    String info = "  R   G   B  \t" + "   x  " + "\t" + "   y  " + "\t" + "   Y   \tLabel\n";
                    info += measures.toString();
                    _resume.setText(info);
                    _white31Canvas.repaint();
                    _white76Canvas.repaint();
                    _mesuresCanvas.repaint();
                    nbPatchMeasured++;
                }
                else
                {
                    t.cancel();

                    _launchBatchButton.setEnabled(true);
                    _launchBatchButton.setText("launch batch...");
                    unLockDependantStep();
                    AbstractCorrection correction = ColorHealerModel._instance.getCorrection();
                    correction.computeColorCorrection();
                    Point3f gamma = correction.getComputeGamma();
                    _gammaValuesLab.setText("Red gamma : " + ((int) (gamma._a * 1000)) / 1000f + "  Green gamma : "
                            + ((int) (gamma._b * 1000)) / 1000f + " Blue Gamma : " + ((int) (gamma._c * 1000)) / 1000f);
                }
            }
        }
    }

    public void manualBatchedMeasures()
    {

        Thread thread;
        Runnable runnable = new Runnable() {
            public void run()

            {

                lockDependantStep("PROBE_MEASURES");
                _launchBatchButton.setText("in progress");
                _launchBatchButton.setEnabled(false);
                _readyButton.setEnabled(true);
                ColorHealerModel model = ColorHealerModel._instance;
                final MeasuresSet measures = model.getCurrentMeasuresSet();

                for (int i = 0; i < _colorPatches.size(); i++)
                {
                    Color currentColor = _colorPatches.get(i);
                    _displayPatchLabel.setText("Display patch " + currentColor.getRed() + " " + currentColor.getGreen()
                            + " " + currentColor.getBlue() + " and clic on Go.");
                    while (!_wasReadyClicked)
                    {

                    }
                    _wasReadyClicked = false;
                    _readyButton.setEnabled(false);
                    boolean res = measures.mesureThisColor(model.getCurrentMeasuresSet(), currentColor, "");
                    if (!res)
                        return;
                    _readyButton.setEnabled(true);
                    String info = "  R   G   B  \t" + "   x  " + "\t" + "   y  " + "\t" + "   Y   \tLabel\n";
                    info += measures.toString();
                    _resume.setText(info);
                    _white31Canvas.repaint();
                    _white76Canvas.repaint();
                    _mesuresCanvas.repaint();
                }
                _readyButton.setEnabled(false);
                _launchBatchButton.setEnabled(true);
                _displayPatchLabel.setText("Launch batch first.");
                _launchBatchButton.setText("launch batch...");
                unLockDependantStep();
                AbstractCorrection correction = ColorHealerModel._instance.getCorrection();
                correction.computeColorCorrection();
                Point3f gamma = correction.getComputeGamma();
                _gammaValuesLab.setText("Red gamma : " + ((int) (gamma._a * 1000)) / 1000f + "    Green gamma : "
                        + ((int) (gamma._b * 1000)) / 1000f + "    Blue Gamma : " + ((int) (gamma._c * 1000)) / 1000f);

            }
        };
        // start thread
        thread = new Thread(runnable, "mesure");
        thread.start();

    }

    public void automaticBatchedMeasures(final int patchDuration, final int waitingTime)
    {

        Thread thread;
        Runnable runnable = new Runnable() {
            public void run()

            {
                int interval = waitingTime / 100;
                try
                {
                    for (int i = 0; i < 100; i++)
                    {
                        Thread.sleep(interval); // do nothing for 1000 miliseconds (1 second)
                        _launchBatchButton.setText("waiting " + (waitingTime - i * interval) / 1000);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                new BatchAction(patchDuration);

            }
        };
        // start thread
        thread = new Thread(runnable, "mesure");
        thread.start();

    }

    public boolean canUnLockDependantStep()
    {
        return true;
    }

    public void init()

    {
        if (!_isInit)
        {
            List<IRgbPrimary> custPrimaries = ColorHealerModel._instance.getCustomPrimaries();
            for (IRgbPrimary iRgbPrimary : custPrimaries)
            {
                _primariesCombo.addItem(iRgbPrimary);

            }
            EStandardRgbPrimaries[] primaries = EStandardRgbPrimaries.values();
            for (IRgbPrimary rgbPrimary : primaries)
            {
                _primariesCombo.addItem(rgbPrimary);
            }

            _isInit = true;
        }
        ColorHealerModel._instance.getSocketServer().displayColor(Color.gray, true);
        ITarget target = ColorHealerModel._instance.getTarget();
        Point2f targetPoint = target.getColorTemp().getxyCoordinates();
        _primariesCombo.setSelectedItem(target.getPrimaries());
        _white31Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
        _white31Canvas.repaint();
        _white76Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
        _white76Canvas.repaint();

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
        _oldStatus = _status;
        _status = StepStatus.DISABLE;
    }
}
