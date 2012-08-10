package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import com.java.ugui.SpringUtilities;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.DisplayDevice;
import fr.hd3d.colortribe.core.DisplayDevice.EDisplayDeviceType;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.components.JImageCanvas;


public class DisplayStep extends Step
{

    /**
     * 
     */
    public static String NAME = "Device informations";
    private static final long serialVersionUID = 7306873765422762571L;

    private boolean _isScreenTypeFilled = false;
    private EDisplayDeviceType _selectedType = null;
    private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;
    private CustomTabbedPane _tabPane;
    private JTextField _brandTex;
    private JTextField _modelTex;
    private JTextField _uidTex;
    private JTextField _hostTex;
    private JTextField _venueTex;
    private JTextField _bulbTextField;
    private JTextField _calibFormatTextField;
    private static List<String> _dependantSteps = new ArrayList<String>();
    static
    {
        _dependantSteps.add(ProbeTargetStep.NAME);
    }

    public DisplayStep()
    {
        super(NAME, "\n> Please select display type <\n\n", StepStatus.NOT_COMPLETE, _dependantSteps);
        buildUI();
    }

    private void buildUI()
    {

    }

    public void init()
    {
        if (!_isInit)

        {

            Runnable runnable = new Runnable() {
                public void run()
                {
                    // preload
                    ColorHealerModel._instance.getProbesPool().getRefreshedProbesList();
                }
            };
            Thread thread = new Thread(runnable, "probeListe2");
            thread.start();

            ColorHealerModel model = ColorHealerModel._instance;
            DisplayDevice disp = model.getDisplayDevice();
            JPanel container = new JPanel();

            container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
            container.setPreferredSize(new Dimension(_width, _height - 5));
            _tabPane = new CustomTabbedPane(this);

            // //host info
            JPanel hostPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
            TitledBorder tiledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                    "Host");
            tiledBorder.setTitleColor(JHealerColors.TEXT_COLOR);
            hostPan.setBorder(tiledBorder);
            Dimension txtFieldDim = new Dimension(120, 20);

            String tmpString = model.getClientName();
            JLabel hostLab = new JLabel("Machine : ");

            _hostTex = new JTextField(tmpString);
            if (tmpString != null)
                _hostTex.setEditable(false);
            _hostTex.setPreferredSize(txtFieldDim);
            _hostTex.setSize(txtFieldDim);
            JLabel venueLab = new JLabel("Place : ");
            _venueTex = new JTextField(" ");
            _venueTex.setPreferredSize(txtFieldDim);
            _venueTex.setSize(txtFieldDim);
            // layout
            hostPan.setLayout(new GridBagLayout());
            GridBagConstraints con = new GridBagConstraints();
            con.anchor = GridBagConstraints.FIRST_LINE_START;
            con.gridx = 0;
            con.gridy = 0;
            hostPan.add(hostLab, con);
            con.gridx++;
            hostPan.add(_hostTex, con);
            con.gridy++;
            con.gridx = 0;
            hostPan.add(venueLab, con);
            con.gridx++;
            hostPan.add(_venueTex, con);
            // //Device info
            JPanel devicePan = new JPanel(new GridLayout(2, 1));
            TitledBorder deviceBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
                    "Display Device");
            deviceBorder.setTitleColor(JHealerColors.TEXT_COLOR);
            devicePan.setBorder(deviceBorder);

            final JImageCanvas illus = new JImageCanvas("img/void_image.png", 100, 100);
            final JPanel illusPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            illusPan.add(illus);
            devicePan.add(illusPan);

            JLabel osidLab = new JLabel("OS id : ");

            JTextArea osidTex = new JTextArea(" " + disp.getOsIndex());

            JLabel brandLab = new JLabel("Brand : ");
            tmpString = disp.getManufacturer();
            _brandTex = new JTextField(" " + tmpString);
            if (tmpString != null)
                _brandTex.setEditable(false);

            tmpString = disp.getModel();
            JLabel modelLab = new JLabel("Model : ");
            _modelTex = new JTextField(" " + tmpString);

            if (tmpString != null)
                _modelTex.setEditable(false);

            tmpString = disp.getUid();
            JLabel uidLab = new JLabel("UID : ");
            _uidTex = new JTextField(" " + tmpString);
            if (tmpString != null)
                _uidTex.setEditable(false);

            JLabel typeLab = new JLabel("Type : ");
            JPanel textPan = new JPanel(new SpringLayout());

            textPan.add(osidLab);
            textPan.add(osidTex);
            textPan.add(brandLab);
            textPan.add(_brandTex);
            textPan.add(modelLab);
            textPan.add(_modelTex);
            textPan.add(uidLab);
            textPan.add(_uidTex);
            textPan.add(typeLab);

            final JLabel bulbLab = new JLabel("Bulb hours : ");
            final JLabel calibLab = new JLabel("Calibration format : ");
            _bulbTextField = new JTextField("");
            _calibFormatTextField = new JTextField("");
            bulbLab.setVisible(false);
            calibLab.setVisible(false);
            _bulbTextField.setVisible(false);
            _calibFormatTextField.setVisible(false);

            /*
             * if (disp.isTypeSet()) { JTextArea typeTex = new JTextArea(" " + disp.getType()); textPan.add(typeTex);
             * _isScreenTypeFilled = true; valid(); unLockDependantStep(); } else
             */
            {
                final JComboBox combox = new JComboBox();
                // combox.setBorder(new LineBorder(Color.black, 2));

                EDisplayDeviceType[] dispDevTypes = EDisplayDeviceType.values();
                for (EDisplayDeviceType e : dispDevTypes)
                {
                    combox.addItem(e);
                }

                if (disp.isTypeSet())
                {
                    combox.setSelectedItem(disp.getType());
                    _description = "Display : " + disp.getManufacturer() + " " + disp.getModel() + "\nModel ID : "
                            + disp.getModelId() + "\n" + "Type : " + disp.getStringType() + "\n" + "OS index : "
                            + disp.getOsIndex() + "\n";
                }
                else
                {
                    combox.setSelectedItem(null);
                }

                combox.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent arg0)
                    {
                        _isScreenTypeFilled = true;
                        _selectedType = (EDisplayDeviceType) combox.getSelectedItem();
                        illus.setImage(_selectedType.getImage());
                        illusPan.repaint();

                        if (_selectedType == EDisplayDeviceType.PROJECTOR)
                        {
                            bulbLab.setVisible(true);
                            calibLab.setVisible(true);
                            _bulbTextField.setVisible(true);
                            _calibFormatTextField.setVisible(true);
                        }
                        else
                        {
                            bulbLab.setVisible(false);
                            calibLab.setVisible(false);
                            _bulbTextField.setVisible(false);
                            _calibFormatTextField.setVisible(false);
                        }

                        // valid();
                    }
                });
                textPan.add(combox);
            }

            textPan.add(bulbLab);
            textPan.add(_bulbTextField);
            textPan.add(calibLab);
            textPan.add(_calibFormatTextField);
            // Lay out the panel.
            SpringUtilities.makeCompactGrid(textPan, 7, 2, // rows, cols
                    2, 2, // initialX, initialY
                    5, 5);// xPad, yPad

            JPanel texPanFlowPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
            texPanFlowPan.add(textPan);
            devicePan.add(texPanFlowPan);

            // //
            container.add(hostPan);
            container.add(devicePan);
            Dimension dim = new Dimension(120, 20);
            JButton setButton = new JButton("Apply");
            setButton.setPreferredSize(dim);
            setButton.setSize(dim);
            setButton.setMinimumSize(dim);
            setButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    super.mousePressed(e);
                    valid();
                }
            });

            container.add(setButton);
            _tabPane.add(_name, container);
            getContentPane().add(_tabPane, BorderLayout.CENTER);
            _isInit = true;

            try
            {
                ColorHealerModel._instance.getSocketServer().sendMessage(
                        "SET_LUT_OFF " + ColorHealerModel._instance.getDisplayDevice().getOsIndex() + "\n");
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public boolean canUnLockDependantStep()
    {
        return _isScreenTypeFilled;
    }

    public void unLock()
    {
        _status = _oldStatus;
    }

    public void valid()
    {
        if (_status != StepStatus.OK && _isScreenTypeFilled)
        {
            unLockDependantStep();
            _status = StepStatus.OK;
            _tabPane.repaint();
        }
        else
        {
            lockDependantStep("DEVICE_TYPE_CHANGED");
        }

        // modify model
        ColorHealerModel model = ColorHealerModel._instance;
        DisplayDevice disp = model.getDisplayDevice();
        disp.setType(_selectedType);
        if (_uidTex.isEditable())
            disp.setUid(_uidTex.getText());
        if (_brandTex.isEditable())
            disp.setManufacturer(_brandTex.getText());
        if (_modelTex.isEditable())
            disp.setModel(_modelTex.getText());

        if (_hostTex.isEditable())
            model.setClientName(_hostTex.getText());

        model.setVenue(_venueTex.getText());
        // change description
        _description = "Display : " + disp.getManufacturer() + " " + disp.getModel() + "\nModel ID : "
                + disp.getModelId() + "\n" + "Type : " + disp.getStringType() + "\n" + "OS index : "
                + disp.getOsIndex() + "\n";
        // change status
        // _status = StepStatus.OK;
        if (_selectedType == EDisplayDeviceType.PROJECTOR)
        {
            model.setBulbHoursCount(Integer.valueOf(_bulbTextField.getText()));
            model.setCalibrationFormat(_calibFormatTextField.getText());
        }
        else
        {
            model.setBulbHoursCount(Integer.valueOf(0));
            model.setCalibrationFormat("");
        }

        ColorHealerGui.mainWindow.rePaintMenu();
    }

    public void lock(String reasons)
    {
        _oldStatus = _status;
        _status = StepStatus.DISABLE;
    }

}
