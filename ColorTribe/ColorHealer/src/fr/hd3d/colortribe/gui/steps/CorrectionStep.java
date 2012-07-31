package fr.hd3d.colortribe.gui.steps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.components.CorrectionCanvas;


public class CorrectionStep extends Step
{
    /**
     * 
     */
    public static String NAME = "Correction";
    private static final long serialVersionUID = 7306873765422762571L;

    private CustomTabbedPane _tabPane;
    private CorrectionCanvas _canvas;
    private StepStatus _oldStatus = StepStatus.OK;
    private JButton _toggleCorrBut ;
    
    boolean _toggleValue = true;

    private JList _list;
    private DefaultListModel _listModel;
    static List<String> _dependantSteps = new ArrayList<String>();
    static
    {}

    public CorrectionStep()
    {
        super(NAME, "Compute correction.", StepStatus.DISABLE, _dependantSteps);
        buildUI();
    }

    public void buildUI()
    {
        _tabPane = new CustomTabbedPane(this);
        Dimension buttonDim = new Dimension(120, 20);
        JPanel corrPan = new JPanel();
        corrPan.setLayout(new GridBagLayout());
        GridBagConstraints con = new GridBagConstraints();
        corrPan.setPreferredSize(new Dimension(_width, _height - 5));
        _canvas = new CorrectionCanvas();
        con.gridx = 0;
        con.gridy = 0;
        con.gridwidth = 3;
        con.fill = GridBagConstraints.BOTH;
        con.anchor = GridBagConstraints.PAGE_START;
        con.weightx = 1;
        con.weighty = 1;
        con.insets = new Insets(1, 1, 2, 1);
        corrPan.add(_canvas, con);

        _toggleCorrBut = new JButton("Unshow");
        _toggleCorrBut.setPreferredSize(buttonDim);
        _toggleCorrBut.setSize(buttonDim);
        _toggleCorrBut.setMinimumSize(buttonDim);

        _toggleCorrBut.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                int screenIndex = ColorHealerModel._instance.getDisplayDevice().getOsIndex();
                _toggleValue = !_toggleValue;
                if (!_toggleValue)
                    _toggleCorrBut.setText("Show");
                else
                    _toggleCorrBut.setText("Unshow");

                try
                {
                    ColorHealerModel._instance.getSocketServer().sendMessage(
                            "SHOULD_DISPLAY " + screenIndex + " " + _toggleValue + "\n");
                }
                catch (IllegalAccessException e1)
                {
                    e1.printStackTrace();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }

            }
        });
        _toggleCorrBut.setPreferredSize(new Dimension(100, 20));

        con.gridx = 1;
        con.gridy = 1;
        con.gridwidth = 1;
        con.weightx = 0;
        con.weighty = 0;
        con.anchor = GridBagConstraints.CENTER;
        con.fill = GridBagConstraints.NONE;
        corrPan.add(_toggleCorrBut, con);

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
                _canvas.repaint();
                model.getCorrection().sendLut();

            }
        });
        con.gridx = 0;
        con.gridy = 2;
        con.gridwidth = 2;
        con.gridheight = 2;
        con.anchor = GridBagConstraints.CENTER;
        corrPan.add(_list, con);
        con.gridx = 2;
        con.gridy = 2;
        con.gridwidth = 1;
     
        con.gridheight = 1;
        con.insets = new Insets(1, 2, 1, 2);
        con.anchor = GridBagConstraints.CENTER;
        corrPan.add(new JTextArea("You can compute deltas of\nthe selected correction."), con);
        con.gridy = 3;
        JButton computeDeltasButton = new JButton("compute deltas\n");
        computeDeltasButton.setPreferredSize(buttonDim);
        computeDeltasButton.setSize(buttonDim);
        computeDeltasButton.setMinimumSize(buttonDim);
        computeDeltasButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                ColorHealerModel._instance.getProtocol().setSelectedStep(ValidationStep.NAME);
                ColorHealerModel._instance.getProtocol().notifySetStepAsked();

            }
        });
        corrPan.add(computeDeltasButton, con);
        _tabPane.add("Correction", corrPan);
        getContentPane().add(_tabPane, BorderLayout.CENTER);
    }

    public boolean canUnLockDependantStep()
    {
        return _status == StepStatus.OK;
    }

    public void init()
    {
        ColorHealerModel model = ColorHealerModel._instance;

        try
        {
            model.getProtocol().getCorrection().sendLut();
            ColorHealerModel._instance.getSocketServer().sendMessage("SHOULD_DISPLAY " + model.getDisplayDevice().getOsIndex() + " " + true + "\n");
            _toggleValue = true;
            _toggleCorrBut.setText("unshow");
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }finally{
            
        }
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

    public void unLock()
    {
        _status = _oldStatus;
    }

    public void valid()
    {

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
