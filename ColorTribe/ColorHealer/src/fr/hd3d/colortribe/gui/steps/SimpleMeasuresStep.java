package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.EStandardRgbPrimaries;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.DisplayDevice;
import fr.hd3d.colortribe.core.MeasuresSet;
import fr.hd3d.colortribe.core.DisplayDevice.EDisplayDeviceType;
import fr.hd3d.colortribe.core.target.ITarget;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.components.JCIE31PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.JCIE76PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.MagnifiedCanvas;


public class SimpleMeasuresStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Simple measure";
    private static final long serialVersionUID = 7306873765422762571L;

    private JCIE31PrimariesCanvas _white31Canvas;
    private JCIE76PrimariesCanvas _white76Canvas;
    private MagnifiedCanvas _magnifiedCanvas;
    private JButton _measureBut;
    private boolean _isInit = false;
    private JComboBox primariesCombo;
    private JTextField rTxt;
    private JTextField gTxt;
    private JTextField bTxt;
    private JTextField labelTxt;
    private JTextArea _resume;
    private JFileChooser fileChooser;

    private CustomTabbedPane _tabPane;

    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;

    private static List<String> _dependantSteps = new ArrayList<String>();
    static
    {
        _dependantSteps.add(ContinuousMeasuresStep.NAME);
        _dependantSteps.add(BatchMeasuresStep.NAME);
        _dependantSteps.add(ProbeTargetStep.NAME);
    }

    public SimpleMeasuresStep()
    {
        super(NAME, "Place the probe on the patch\nAnd start measures.", StepStatus.DISABLE, _dependantSteps);
        buildUI();
    }

    public void buildUI()
    {
        _tabPane = new CustomTabbedPane(this);

        JPanel whitePan = new JPanel();
        Dimension panDim = new Dimension(_width, _height - 5);
        whitePan.setPreferredSize(panDim);
        whitePan.setSize(panDim);
        whitePan.setMinimumSize(panDim);

        // //////////white pane
        _white31Canvas = new JCIE31PrimariesCanvas(EStandardRgbPrimaries.REC709);
        _white76Canvas = new JCIE76PrimariesCanvas(EStandardRgbPrimaries.REC709);

        final JPanel canvasPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        canvasPan.add(_white76Canvas);

        _magnifiedCanvas = new MagnifiedCanvas();

        _measureBut = new JButton("measure");
        _measureBut.setPreferredSize(new Dimension(100, 20));
        _measureBut.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {

                _measureBut.setEnabled(false);
                _description = "";
                _tabPane.repaint();
                ColorHealerGui.mainWindow.rePaintMenu();
                measure();

            }
        });
        Dimension txtDim = new Dimension(60, 20);
        JPanel launchMeasurePan = new JPanel(new GridBagLayout());
        rTxt = new JTextField("255");
        rTxt.setPreferredSize(txtDim);
        rTxt.setHorizontalAlignment(JLabel.CENTER);
        gTxt = new JTextField("255");
        gTxt.setPreferredSize(txtDim);
        gTxt.setHorizontalAlignment(JLabel.CENTER);
        bTxt = new JTextField("255");
        bTxt.setPreferredSize(txtDim);
        bTxt.setHorizontalAlignment(JLabel.CENTER);
        JLabel label = new JLabel("Label : ");
        labelTxt = new JTextField("");
        labelTxt.setPreferredSize(new Dimension(100, 20));
        labelTxt.setHorizontalAlignment(JLabel.CENTER);
        // layout
        GridBagConstraints con2 = new GridBagConstraints();
        con2.gridx = 0;
        con2.gridy = 0;
        con2.insets = new Insets(2, 2, 2, 2);

        launchMeasurePan.add(rTxt, con2);
        con2.gridx++;
        launchMeasurePan.add(gTxt, con2);
        con2.gridx++;
        launchMeasurePan.add(bTxt, con2);
        con2.gridx++;
        launchMeasurePan.add(label, con2);
        con2.gridx++;
        launchMeasurePan.add(labelTxt, con2);
        con2.gridx++;
        launchMeasurePan.add(_measureBut, con2);

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
        // resumeScrollPane.setMaximumSize(dim2);
        // measurePan.setSize(dim2);
        // measurePan.setPreferredSize(dim2);

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
        primariesCombo = new JComboBox();
        primariesCombo.setPreferredSize(comboDim);
        primariesCombo.setSize(comboDim);

        primariesCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0)
            {
                _white31Canvas.setPrimariesToDisplay((IRgbPrimary) primariesCombo.getSelectedItem());
                _white31Canvas.repaint();
                _white76Canvas.setPrimariesToDisplay((IRgbPrimary) primariesCombo.getSelectedItem());
                _white76Canvas.repaint();

            }
        });
        final JComboBox chartsCombo = new JComboBox();
        chartsCombo.setPreferredSize(comboDim);
        chartsCombo.setSize(comboDim);
        chartsCombo.addItem("CIE 1976");
        chartsCombo.addItem("CIE 1931");
        chartsCombo.addItem("Magnified");
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
                else
                {
                    canvasPan.add(_magnifiedCanvas);
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
        displayPan.add(primariesCombo, con5);
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
                _magnifiedCanvas.repaint();
            }
        });
        final JButton exportMeasures = new JButton("export measures");
        exportMeasures.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e)
            {
                fileChooser = new JFileChooser("Choose a directory");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String saveDate = ColorHealerModel.getFormatDate();
                ColorHealerModel model = ColorHealerModel._instance;
                final MeasuresSet measures = model.getCurrentMeasuresSet();
                File dir = new File("summaries");
                fileChooser.setCurrentDirectory(dir);
                DisplayDevice dispDev = model.getDisplayDevice();
                int returnVal = fileChooser.showSaveDialog(exportMeasures);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = new File(fileChooser.getSelectedFile().getPath() + "\\measures_"
                            + dispDev.getProfilName() + "_" + saveDate + ".html");
                    File file2 = new File(fileChooser.getSelectedFile().getPath() + "\\measures_"
                            + dispDev.getProfilName() + "_" + saveDate + "diag.jpg");
                    BufferedImage diagram;
                    String diagramTitle;
                    if (chartsCombo.getSelectedIndex() == 0) // 76
                    {
                        diagram = _white76Canvas.getBufferedImage();

                        diagramTitle = "CIE 1976";
                    }
                    else if (chartsCombo.getSelectedIndex() == 1) // 31
                    {
                        diagram = _white31Canvas.getBufferedImage();
                        diagramTitle = "CIE 1931";
                    }
                    else
                    // magni
                    {
                        diagram = _magnifiedCanvas.getBufferedImage();
                        diagramTitle = "Magnified canvas";
                    }

                    String displayedPrimaries = _white31Canvas.getPrimariesToDisplay().getName();
                    String targetWhite = model.getTarget().getColorTemp().getName();
                    try
                    {
                        ImageIO.write(diagram, "JPG", file2);
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
                        sortie.append("<div >\n<b>Measures</b><br><br>\n" + measures.htmlToString() + "\n</div>\n");

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
        con.weighty = 0.65;

        whitePan.add(canvasPan, con);
        con.gridy++;
        con.weighty = 0.02;
        whitePan.add(launchMeasurePan, con);
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

        _tabPane.add("White point", whitePan);

        getContentPane().add(_tabPane, BorderLayout.CENTER);
    }

    private void launchMeasure()
    {
        ColorHealerModel model = ColorHealerModel._instance;
        final MeasuresSet measures = model.getCurrentMeasuresSet();
        Color color = new Color(Integer.valueOf(rTxt.getText()), Integer.valueOf(gTxt.getText()), Integer.valueOf(bTxt
                .getText()));
        lockDependantStep("PROBE_MEASURES");
        boolean res = measures.mesureThisColor(model.getCurrentMeasuresSet(), color, labelTxt.getText());

        if (!res)
            return;
        // set measured values
        ColorMeasure mes = measures.getMeasure(color);
        if (mes != null)
        {
            Point3f measuredPoint = mes.getValue();
            _magnifiedCanvas.setMeasuredPoint(measuredPoint._a, measuredPoint._b);
        }

        String info = "  R   G   B  \t" + "   x  " + "\t" + "   y  " + "\t" + "   Y   \tLabel\n";
        info += measures.toString();
        _resume.setText(info);
        _white31Canvas.repaint();
        _white76Canvas.repaint();
        _magnifiedCanvas.repaint();
        unLockDependantStep();
    }

    private void measure()
    {

        Thread thread;
        Runnable runnable = new Runnable() {
            public void run()

            {
               
                launchMeasure();
                valid();
                _measureBut.setEnabled(true);
                _measureBut.setText("start measures");

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
        ColorHealerModel model = ColorHealerModel._instance;
        if (!_isInit)
        {
            List<IRgbPrimary> custPrimaries = model.getCustomPrimaries();
            for (IRgbPrimary iRgbPrimary : custPrimaries)
            {
                primariesCombo.addItem(iRgbPrimary);

            }
            EStandardRgbPrimaries[] primaries = EStandardRgbPrimaries.values();
            for (IRgbPrimary rgbPrimary : primaries)
            {
                primariesCombo.addItem(rgbPrimary);
            }

            _isInit = true;
        }
        String info = "  R   G   B  \t" + "   x  " + "\t" + "   y  " + "\t" + "   Y   \tLabel\n";
        info += model.getCurrentMeasuresSet().toString();
        _resume.setText(info);
        model.getSocketServer().displayColor(Color.gray, true);
        ITarget target = model.getTarget();
        Point2f targetPoint = target.getColorTemp().getxyCoordinates();
        primariesCombo.setSelectedItem(target.getPrimaries());
        _white31Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
        _white31Canvas.repaint();
        _white76Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
        _white76Canvas.repaint();
        _magnifiedCanvas.setTargetPoint(targetPoint._a, targetPoint._b);
        _magnifiedCanvas.repaint();

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
