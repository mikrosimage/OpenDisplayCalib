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
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.EStandardIlluminants;
import fr.hd3d.colortribe.color.EStandardRgbPrimaries;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.DisplayDevice;
import fr.hd3d.colortribe.core.MeasuresSet;
import fr.hd3d.colortribe.core.DisplayDevice.EDisplayDeviceType;
import fr.hd3d.colortribe.core.probes.IProbe.EProbeType;
import fr.hd3d.colortribe.core.target.ITarget;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.components.JCIE31PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.JCIE76PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.MagnifiedCanvas;


public class ContinuousMeasuresStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Continuous measure";
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
    private boolean _isMeasuring = false;
    private JLabel _dxLab;
    private JLabel _dyLab;
    private JLabel _dYLab;
    private JLabel _xLab;
    private JLabel _yLab;
    private JLabel _YLab;
    private JLabel _colorTemp;
    private JLabel _dcolorTemp;
    private JLabel _goodBad;
    private JFileChooser fileChooser;

    private CustomTabbedPane _tabPane;

    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;

    private static List<String> _dependantSteps = new ArrayList<String>();
    static
    {
        _dependantSteps.add(SimpleMeasuresStep.NAME);
        _dependantSteps.add(ProbeTargetStep.NAME);
        _dependantSteps.add(BatchMeasuresStep.NAME);
    }

    public ContinuousMeasuresStep()
    {
        super(NAME, "Place the probe on the patch\nAnd start measures.", StepStatus.DISABLE, _dependantSteps);
        buildUI();
    }

    public void buildUI()
    {
        _tabPane = new CustomTabbedPane(this);

        JPanel whitePan = new JPanel();
        whitePan.setLayout(new GridBagLayout());
        whitePan.setPreferredSize(new Dimension(_width, _height - 5));

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
                if (!_isMeasuring)
                {
                    _description = "";
                    _tabPane.repaint();
                    ColorHealerGui.mainWindow.rePaintMenu();
                    measure();
                }
                else
                {
                    _isMeasuring = false;
                    valid();
                }

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
        GridBagConstraints con1 = new GridBagConstraints();

        Dimension labSize = new Dimension(50, 20);
        _dxLab = new JLabel("-");
        _dxLab.setPreferredSize(labSize);
        _dyLab = new JLabel("-");
        _dyLab.setPreferredSize(labSize);
        _dYLab = new JLabel("-");
        _dYLab.setPreferredSize(labSize);
        _xLab = new JLabel("-");
        _xLab.setPreferredSize(labSize);
        _yLab = new JLabel("-");
        _yLab.setPreferredSize(labSize);
        _YLab = new JLabel("-");
        _YLab.setPreferredSize(labSize);
        _colorTemp = new JLabel("-");
        _colorTemp.setPreferredSize(labSize);
        _dcolorTemp = new JLabel("-");
        _dcolorTemp.setPreferredSize(labSize);
        _goodBad = new JLabel("-");
        _goodBad.setPreferredSize(labSize);
        _goodBad.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        _goodBad.setHorizontalTextPosition(JLabel.CENTER);
        _goodBad.setHorizontalAlignment(JLabel.CENTER);

        con1.gridx = 0;
        con1.gridy = 0;
        con1.gridwidth = 1;
        con1.anchor = GridBagConstraints.FIRST_LINE_START;
        con1.fill = GridBagConstraints.BOTH;
        con1.insets = new Insets(1, 1, 1, 1);
        measurePan.add(new JLabel("x : "), con1);
        con1.gridx = 1;
        measurePan.add(_xLab, con1);
        con1.gridx = 2;
        measurePan.add(_dxLab, con1);
        con1.gridx = 3;
        measurePan.add(new JLabel("y : "), con1);
        con1.gridx = 4;
        measurePan.add(_yLab, con1);
        con1.gridx = 5;
        measurePan.add(_dyLab, con1);
        con1.gridx = 6;
        measurePan.add(new JLabel("Y : "), con1);
        con1.gridx = 7;
        measurePan.add(_YLab, con1);
        con1.gridx = 8;
        measurePan.add(_dYLab, con1);
        con1.gridy = 1;
        con1.gridx = 0;
        measurePan.add(new JLabel("TC : "), con1);
        con1.gridx = 1;
        measurePan.add(_colorTemp, con1);
        con1.gridx = 2;
        measurePan.add(_dcolorTemp, con1);
        con1.gridx = 3;
        con1.gridwidth = 2;
        measurePan.add(_goodBad, con1);
        GridBagConstraints con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.fill = GridBagConstraints.BOTH;
        con.weightx = 1;
        con.weighty = 1;

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
        con = new GridBagConstraints();
        con.gridx = 0;
        con.gridy = 0;
        con.gridwidth = 2;
        con.anchor = GridBagConstraints.PAGE_START;
        con.fill = GridBagConstraints.BOTH;
        con.insets = new Insets(1, 1, 1, 1);

        whitePan.add(canvasPan, con);
        con.gridx = 0;
        con.gridy = 1;
        con.gridwidth = 2;
        whitePan.add(launchMeasurePan, con);

        con.gridx = 0;
        con.gridy = 3;
        con.gridwidth = 2;
        con.weighty = 1;
        whitePan.add(measurePan, con);
        con.gridx = 0;
        con.gridy = 4;
        con.gridwidth = 2;
        con.weighty = 0;
        whitePan.add(displayPan, con);
        final JButton exportMeasures = new JButton("export measures");
        exportMeasures.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e)
            {
                fileChooser = new JFileChooser("Choose a directory");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                String saveDate = ColorHealerModel.getFormatDate();
                ColorHealerModel model = ColorHealerModel._instance;
                File dir = new File("summaries");
                fileChooser.setCurrentDirectory(dir);
                DisplayDevice dispDev = model.getDisplayDevice();
                int returnVal = fileChooser.showSaveDialog(exportMeasures);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = new File(fileChooser.getSelectedFile().getPath() + "\\set_value_"
                            + dispDev.getProfilName() + "_" + saveDate + ".html");
                    File file2 = new File(fileChooser.getSelectedFile().getPath() + "\\set_value_"
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
                    String title = "Set up " + saveDate + " " + dispDev.getManufacturer() + " " + dispDev.getModel()
                            + " " + dispDev.getUid();

                    String resume = " ";
                    final Color color = new Color(Integer.valueOf(rTxt.getText()), Integer.valueOf(gTxt.getText()),
                            Integer.valueOf(bTxt.getText()));
                    final boolean isColorMonochrome = color.getRed() == color.getBlue()
                            && color.getBlue() == color.getGreen();
                    if (isColorMonochrome)
                    {
                        resume += "x : " + _xLab.getText() + " (" + _dxLab.getText() + ")" + " y : " + _yLab.getText()
                                + " (" + _dyLab.getText() + ")" + " Y : " + _YLab.getText() + " (" + _dYLab.getText()
                                + ")" + "<br>\n";

                        int approxColorTemp = (int) EStandardIlluminants.getApproximateColorTemperature(new Point2f(
                                Float.valueOf(_xLab.getText()),  Float.valueOf(_yLab.getText())));

                       

                        // calcul de la diff en Mired
                        // delta = (approxColorTemp - targetTemp)
                        // miredDelta = (1000000/approxColorTemp - 1000000/targetTemp)
                        int targetTemp = model.getTarget().getColorTemp().getValue();
                        int miredDelta = 1000000 / approxColorTemp - 1000000 / targetTemp;
                        resume += "Color temperature : " + approxColorTemp + " mired delta : " + miredDelta + " md";

                    }
                    else
                    {
                        resume += "x : " + _xLab.getText() + " y : " + _yLab.getText() + " Y : " + _YLab.getText()
                                + "<br>\n";

                    }

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
                        sortie.append("<div >\n<b>Result</b><br><br>\n" + resume+ "\n</div>\n");

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
        con.gridy = 5;
        con.gridx = 1;
        con.gridwidth = 1;
        con.anchor = GridBagConstraints.CENTER;
        con.fill = GridBagConstraints.NONE;
        whitePan.add(exportMeasures, con);
        con.gridx = 0;
        JButton clearMeasure = new JButton("clear measures");
        clearMeasure.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                MeasuresSet set = ColorHealerModel._instance.getCurrentMeasuresSet();
                set.clear();
                _white31Canvas.repaint();
                _white76Canvas.repaint();
                _magnifiedCanvas.repaint();
            }
        });
        whitePan.add(clearMeasure, con);

        con.gridy = 6;
        con.fill = GridBagConstraints.BOTH;
        con.weighty = 1.0;
        whitePan.add(new JPanel(), con);

        _tabPane.add("White point", whitePan);

        getContentPane().add(_tabPane, BorderLayout.CENTER);
    }

    private void launchMeasure()
    {
        final ColorHealerModel model = ColorHealerModel._instance;
        final MeasuresSet measures = model.getCurrentMeasuresSet();
        final Color color = new Color(Integer.valueOf(rTxt.getText()), Integer.valueOf(gTxt.getText()), Integer
                .valueOf(bTxt.getText()));
        final ITarget target = model.getTarget();
        final Point2f targetPoint = target.getColorTemp().getxyCoordinates();
        final float colorDelta = target.getColorDelta();
        final float lumDelta = target.getLumDelta();
        final float targetLum = target.getMaxLum();
        final int targetTemp = target.getColorTemp().getValue();
        final boolean isColorMonochrome = color.getRed() == color.getBlue() && color.getBlue() == color.getGreen();
        if (!isColorMonochrome)
        {
            _dxLab.setText("");
            _dyLab.setText("");
            _dYLab.setText("");
            _colorTemp.setText("");
            _dcolorTemp.setText("");
            _goodBad.setText("");
        }
        Thread thread;
        Runnable runnable = new Runnable() {
            public void run()

            {
                lockDependantStep("PROBE_MEASURES");
                boolean isMKCS200 = ColorHealerModel._instance.getProbe().getEProbeType() == EProbeType.MK_CS200;
                do
                {
                    boolean res = measures.mesureThisColor(model.getCurrentMeasuresSet(), color, labelTxt.getText());

                    if (!res)
                        return;
                    // set measured values
                    ColorMeasure mes = measures.getMeasure(color);
                    if (mes != null)
                    {
                        Point3f measuredPoint = mes.getValue();
                        _magnifiedCanvas.setMeasuredPoint(measuredPoint._a, measuredPoint._b);
                        _xLab.setText(String.valueOf(((int) (measuredPoint._a * 1000) / 1000f)));
                        _yLab.setText(String.valueOf(((int) (measuredPoint._b * 1000) / 1000f)));
                        _YLab.setText(String.valueOf(((int) (measuredPoint._c * 1000) / 1000f)));
                        if (isColorMonochrome)
                        {
                            _magnifiedCanvas.setMeasuredPoint(measuredPoint._a, measuredPoint._b);
                            float dx = measuredPoint._a - targetPoint._a;
                            float dy = measuredPoint._b - targetPoint._b;
                            float dY = measuredPoint._c - targetLum;
                            _dxLab.setText("" + ((int) (dx * 1000)) / 1000f);
                            _dyLab.setText("" + ((int) (dy * 1000)) / 1000f);
                            _dYLab.setText("" + ((int) (dY * 1000)) / 1000f);
                            dx = Math.abs(dx);
                            dy = Math.abs(dy);
                            dY = Math.abs(dY);
                            if (dx < colorDelta)
                                _dxLab.setForeground(Color.green);
                            else
                                _dxLab.setForeground(Color.red);

                            if (dy < colorDelta)
                                _dyLab.setForeground(Color.green);
                            else
                                _dyLab.setForeground(Color.red);

                            if (dY < lumDelta)
                                _dYLab.setForeground(Color.green);
                            else
                                _dYLab.setForeground(Color.red);

                            int approxColorTemp = (int) EStandardIlluminants
                                    .getApproximateColorTemperature(new Point2f(measuredPoint._a, measuredPoint._b));

                            _colorTemp.setText("" + approxColorTemp);
                            if ((dx < colorDelta) && (dy < colorDelta))
                                _colorTemp.setForeground(Color.green);
                            else
                                _colorTemp.setForeground(Color.red);

                            // calcul de la diff en Mired
                            // delta = (approxColorTemp - targetTemp)
                            // miredDelta = (1000000/approxColorTemp - 1000000/targetTemp)
                            int miredDelta = 1000000 / approxColorTemp - 1000000 / targetTemp;
                            _dcolorTemp.setText("" + miredDelta + " Md");
                            miredDelta = Math.abs(miredDelta);
                            if (miredDelta > target.getTCDelta())
                                _dcolorTemp.setForeground(Color.red);
                            else
                                _dcolorTemp.setForeground(Color.green);

                            if ((dx > colorDelta) || (dy > colorDelta) || (dY > lumDelta)
                                    || (miredDelta > target.getTCDelta()))
                            {
                                _goodBad.setText("BAD");
                                _goodBad.setForeground(Color.red);
                                _magnifiedCanvas.setOK(false);
                            }
                            else
                            {
                                _goodBad.setText("GOOD");
                                _goodBad.setForeground(Color.green);
                                _magnifiedCanvas.setOK(true);

                            }

                        }

                    }

                    _white31Canvas.repaint();
                    _white76Canvas.repaint();
                    _magnifiedCanvas.repaint();
                }
                while (_isMeasuring && !isMKCS200);
                if (isMKCS200)
                {
                    _isMeasuring = false;
                }
                unLockDependantStep();
                _measureBut.setText("start measures");

            }
        };
        // start thread
        thread = new Thread(runnable, "mesure gain");
        thread.start();
    }

    private void measure()
    {

        _measureBut.setText("stop measures");
        _isMeasuring = true;
        _description = "Try to reduce deltas.\nIf you can't force validation.";
        _tabPane.repaint();
        ColorHealerGui.mainWindow.rePaintMenu();

        launchMeasure();
        valid();

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
                primariesCombo.addItem(iRgbPrimary);

            }
            EStandardRgbPrimaries[] primaries = EStandardRgbPrimaries.values();
            for (IRgbPrimary rgbPrimary : primaries)
            {
                primariesCombo.addItem(rgbPrimary);
            }

            _isInit = true;
        }
        ColorHealerModel._instance.getSocketServer().displayColor(Color.gray, true);
        ITarget target = ColorHealerModel._instance.getTarget();
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
