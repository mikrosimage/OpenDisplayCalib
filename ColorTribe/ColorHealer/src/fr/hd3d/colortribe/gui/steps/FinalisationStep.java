package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.DisplayDevice;
import fr.hd3d.colortribe.core.DisplayDevice.EDisplayDeviceType;
import fr.hd3d.colortribe.core.target.ITarget;
import fr.hd3d.colortribe.gui.CustomTabbedPane;


public class FinalisationStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Apply calibration";
    private static final long serialVersionUID = 7306873765422762571L;

    private CustomTabbedPane _tabPane;
    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;
    private JTextArea _resume;
    private String _infos;
    private JTextArea _com;

    
    private JFileChooser fileChooser;

    private static List<String> _dependantSteps = new ArrayList<String>();
    static
    {}

    public FinalisationStep()
    {
        super(NAME, "Validate.", StepStatus.DISABLE, _dependantSteps);
        buildUI();
    }

    public void buildUI()
    {
        _tabPane = new CustomTabbedPane(this);

        JPanel corrPan = new JPanel();
        corrPan.setLayout(new BoxLayout(corrPan, BoxLayout.Y_AXIS));
        corrPan.setPreferredSize(new Dimension(_width, _height - 5));

        // /////////////

        JPanel resumPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        _resume = new JTextArea();
        _resume.setEditable(false);
        JScrollPane resumeScrollPane = new JScrollPane(_resume, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Dimension paneDim = new Dimension(500, 400);
        resumeScrollPane.setSize(paneDim);
        resumeScrollPane.setPreferredSize(paneDim);
        resumeScrollPane.setBorder(null);
        resumPane.add(resumeScrollPane);
        corrPan.add(resumPane);

        _com = new JTextArea(3, 20);
        _com.setEditable(true);
        _com.setBackground(Color.lightGray);
        _com.setForeground(Color.darkGray);
        JScrollPane scrollPane = new JScrollPane(_com);
        JLabel comText = new JLabel("Add a comment : ");
        JPanel comContainer = new JPanel(new FlowLayout());
        comContainer.add(comText);
        comContainer.add(scrollPane);

        corrPan.add(comContainer);

        final JButton applyCorrBut = new JButton("Apply");
        applyCorrBut.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                try
                {
                    ColorHealerModel._instance.getSocketServer().updateFile(_infos + "\n" + _com.getText());

                }
                catch (IllegalAccessException e1)
                {
                    e1.printStackTrace();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                fileChooser = new JFileChooser("Save profil backup");
                String saveDate = ColorHealerModel.getFormatDate();
                ColorHealerModel model = ColorHealerModel._instance;
                File dir = new File("summaries");
                fileChooser.setCurrentDirectory(dir);
                fileChooser.setSelectedFile(new File("summaries\\" + model.getDisplayDevice().getProfilName() + "."
                        + saveDate));
                int returnVal = fileChooser.showSaveDialog(applyCorrBut);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File file = fileChooser.getSelectedFile();
                    BufferedWriter sortie;
                    try
                    {
                        sortie = new BufferedWriter(new FileWriter(file));
                        ColorHealerModel._instance.getCorrection().saveLut(sortie);
                        sortie.append("\n" + _infos + "\n" + _com.getText());
                        sortie.append("First measures set : \n");
                        List<ColorMeasure> measures = ColorHealerModel._instance.getMeasuresSet(0).getMeasures();
                        for (ColorMeasure measure : measures)
                        {
                            Color color = measure.getPatchColor();
                            Point3f point = measure.getValue();
                            sortie.append("[r=" + color.getRed() + ", g=" + color.getGreen() + ", b=" + color.getBlue()
                                    + " ; " + " x=" + point._a + ", y=" + point._b + ", Y=" + point._c + "]\n");
                        }
                        sortie.close();
                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }
                applyCorrBut.setEnabled(false);
                ColorHealerModel._instance.setCalibUpdated(true);
                valid();
                unLockDependantStep();

            }
        });

        applyCorrBut.setPreferredSize(new Dimension(100, 20));

        JPanel buttPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttPan.add(applyCorrBut);
        corrPan.add(buttPan);

        _tabPane.add("Validation", corrPan);

        getContentPane().add(_tabPane, BorderLayout.CENTER);
    }



    public boolean canUnLockDependantStep()
    {
        return _status == StepStatus.OK;
    }

    public void init()
    {
        ColorHealerModel model = ColorHealerModel._instance;
        ITarget target = model.getTarget();
        Point2f targetWhite = target.getColorTemp().getxyCoordinates();
        Point3f whitePT = model.getBasicMeasuresSet().getMeasure(Color.white).getValue();
        DisplayDevice dispDev = model.getDisplayDevice();
        IRgbPrimary primaries = target.getPrimaries();
        float targLum = target.getMaxLum();
        Point2f red = primaries.getRed().getxyCoordinates();
        Point2f green = primaries.getGreen().getxyCoordinates();
        Point2f blue = primaries.getBlue().getxyCoordinates();
        String primariesString = "Target primaries : " + primaries.getName() + " : red (" + red._a + ", " + red._b
                + "), green (" + green._a + ", " + green._b + "), blue (" + blue._a + ", " + blue._b + ")\n";
        String primRed = model.getBasicMeasuresSet().getMeasure(Color.red).getValue().clampedToString();
        String primGreen = model.getBasicMeasuresSet().getMeasure(Color.green).getValue().clampedToString();
        String primBlue = model.getBasicMeasuresSet().getMeasure(Color.blue).getValue().clampedToString();
        primariesString += "Measured primaries : " + "red (" + primRed + "), \ngreen (" + primGreen + "), blue ("
                + primBlue + ")";
        float targGamma = target.getGamma();

        _infos = new String(":: Summary ::\n\n" + "Machine : " + model.getClientName() + " (" + model.getVenue() + ")"
                + "\n" + "Display device : " + dispDev.getManufacturer() + " " + dispDev.getModel() + " "
                + dispDev.getUid() + " (" + dispDev.getStringType() + ")" + "\n");
        if (model.getDisplayDevice().getType() == EDisplayDeviceType.PROJECTOR)
        {
            _infos += "Bulb hours count : " + model.getBulbHoursCount() + "\n";
            _infos += "Calibration format : " + model.getCalibrationFormat() + "\n";
        }

        _infos += "Probe : " + model.getProbe().getEProbeType() + "\n" + "\n" + "Target white temperature : "
                + target.getColorTemp() + " (" + targetWhite._a + ", " + targetWhite._b + "), " + targLum
                + " cda/m� \n" + "Final white temperature : " + whitePT._a + " " + whitePT._b + " " + whitePT._c + "\n"
                + primariesString + "\n";

        _infos += "Target Gamma : " + targGamma + "\n\n";

        _infos += model.getCorrection().getSummary();
        // ///////////
        _resume.setText(_infos);

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
