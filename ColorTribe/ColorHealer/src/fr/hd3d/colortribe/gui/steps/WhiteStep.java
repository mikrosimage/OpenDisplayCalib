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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.EStandardIlluminants;
import fr.hd3d.colortribe.color.EStandardRgbPrimaries;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;
import fr.hd3d.colortribe.core.correction.WhiteSoftwareCorrection;
import fr.hd3d.colortribe.core.probes.AbstractProbe;
import fr.hd3d.colortribe.core.probes.GammaProbeAndColorProbe;
import fr.hd3d.colortribe.core.probes.IProbe.EProbeType;
import fr.hd3d.colortribe.core.target.ITarget;
import fr.hd3d.colortribe.gui.CustomTabbedPane;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.components.GaugeCanvas;
import fr.hd3d.colortribe.gui.components.JCIE31PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.JCIE76PrimariesCanvas;
import fr.hd3d.colortribe.gui.components.MagnifiedCanvas;


public class WhiteStep extends Step
{
    /**
     * 
     */
	public static String NAME = "White and gray points";
	private static final long serialVersionUID = 7306873765422762571L;

	private JCIE31PrimariesCanvas _white31Canvas;
	private JCIE76PrimariesCanvas _white76Canvas;
	private MagnifiedCanvas _magnifiedCanvas;
	private GaugeCanvas _whiteGaugeCanvas;
	private GaugeCanvas _grayGaugeCanvas;
	private JCIE31PrimariesCanvas _gray31Canvas;
	private JCIE76PrimariesCanvas _gray76Canvas;
	private MagnifiedCanvas _magnifiedCanvas2;
	private JButton _whiteStartBut;
	private JButton _grayStartBut;
	private boolean _isMeasuring = false;
	private boolean _isInit = false;
	private JComboBox primariesCombo;
	private JComboBox primariesCombo2;

	private JLabel _dxLab;
	private JLabel _dyLab;
	private JLabel _dYLab;
	private JLabel _xLab;
	private JLabel _yLab;
	private JLabel _YLab;
	private JLabel _colorTemp;
	private JLabel _dcolorTemp;
	private JLabel _goodBad;

	private JLabel _dxLab2;
	private JLabel _dyLab2;
	private JLabel _dYLab2;
	private JLabel _xLab2;
	private JLabel _yLab2;
	private JLabel _YLab2;
	private JLabel _colorTemp2;
	private JLabel _dcolorTemp2;
	private JLabel _goodBad2;

	private CustomTabbedPane _tabPane;
	private JCheckBox _measurePrimChec;
	private JCheckBox _measurePrimChec2;
	private JCheckBox _forceValidation;

	private JSpinner rTxt;
	private JSpinner gTxt;
	private JSpinner bTxt;
	private SpinnerNumberModel rNumberModel;
	private SpinnerNumberModel gNumberModel;
	private SpinnerNumberModel bNumberModel;
	private JButton checkSoftCorrButt;

	private int whitePaneIndex = 0;
	private int grayPaneIndex = 1;

	private StepStatus _oldStatus = StepStatus.NOT_COMPLETE;

	private static List<String> _dependantSteps = new ArrayList<String>();
	static {
		_dependantSteps.add(MeasuresStep.NAME);
	}

	public WhiteStep() {
		super(NAME, "Place the probe on the patch\nAnd start measures.",
				StepStatus.DISABLE, _dependantSteps);
		buildUI();
	}

	public void buildUI() {
		_tabPane = new CustomTabbedPane(this);

		JPanel whitePan = new JPanel();
		whitePan.setLayout(new GridBagLayout());
		whitePan.setPreferredSize(new Dimension(_width, _height - 5));
		JPanel grayPan = new JPanel();
		grayPan.setLayout(new GridBagLayout());
		grayPan.setPreferredSize(new Dimension(_width, _height - 5));

		// //////////white pane
		_white31Canvas = new JCIE31PrimariesCanvas(EStandardRgbPrimaries.REC709);
		_white76Canvas = new JCIE76PrimariesCanvas(EStandardRgbPrimaries.REC709);

		final JPanel canvasPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
		canvasPan.add(_white76Canvas);

		_magnifiedCanvas = new MagnifiedCanvas();

		_whiteGaugeCanvas = new GaugeCanvas(150, 0, 0);

		_whiteStartBut = new JButton("start measures");
		_whiteStartBut.setPreferredSize(new Dimension(100, 20));
		_whiteStartBut.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ColorHealerModel model = ColorHealerModel._instance;
				if (!_isMeasuring) {
					if (_measurePrimChec.isSelected()) {
						ColorHealerModel._instance.getCurrentMeasuresSet()
								.mesureThisColor(model.getCurrentMeasuresSet(),
										Color.red, "primary measure");
						_white31Canvas.repaint();
						_magnifiedCanvas.repaint();
						_white76Canvas.repaint();
						ColorHealerModel._instance.getCurrentMeasuresSet()
								.mesureThisColor(model.getCurrentMeasuresSet(),
										Color.green, "primary measure");
						_white31Canvas.repaint();
						_magnifiedCanvas.repaint();
						_white76Canvas.repaint();
						ColorHealerModel._instance.getCurrentMeasuresSet()
								.mesureThisColor(model.getCurrentMeasuresSet(),
										Color.blue, "primary measure");
						_white31Canvas.repaint();
						_magnifiedCanvas.repaint();
						_white76Canvas.repaint();
					}
					_whiteStartBut.setText("stop measures");
					_isMeasuring = true;
					_tabPane.setEnabledAt(grayPaneIndex, false);
					_description = "Try to reduce deltas.\nIf you can't force validation.";
					_tabPane.repaint();
					ColorHealerGui.mainWindow.rePaintMenu();
					white();
				} else {
					_whiteStartBut.setEnabled(false);
					_tabPane.setEnabledAt(grayPaneIndex, true);
					_isMeasuring = false;
					valid();
				}

			}
		});
		JPanel buttPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttPan.add(_whiteStartBut);

		_measurePrimChec = new JCheckBox();
		_measurePrimChec.setSelected(true);
		JLabel mesPrimLab = new JLabel("Measure primaries");
		buttPan.add(_measurePrimChec);
		buttPan.add(mesPrimLab);
		// ///////////
		JPanel measurePan = new JPanel(new GridBagLayout());
		TitledBorder measureBorder = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Measures");
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

		_forceValidation = new JCheckBox();
		_forceValidation.setSelected(false);
		_forceValidation.setEnabled(false);
		_forceValidation.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				AbstractButton abstractButton = (AbstractButton) e.getSource();
				ButtonModel buttonModel = abstractButton.getModel();
				boolean pressed = buttonModel.isPressed();
				if (pressed) {
					_isMeasuring = false;
					_whiteStartBut.setEnabled(true);
					_tabPane.setEnabledAt(grayPaneIndex, true);
					_tabPane.setEnabledAt(whitePaneIndex, true);
					valid();
				}
			}
		});
		JLabel forceValLab = new JLabel("Force validation");
		JPanel forceValPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
		forceValPan.add(_forceValidation);
		forceValPan.add(forceValLab);

		con1.gridx = 5;
		con1.gridwidth = GridBagConstraints.REMAINDER;
		measurePan.add(forceValPan, con1);

		JPanel displayPan = new JPanel(new GridBagLayout());
		GridBagConstraints con5 = new GridBagConstraints();
		TitledBorder displayBorder = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Display");
		displayBorder.setTitleColor(JHealerColors.TEXT_COLOR);
		displayPan.setBorder(displayBorder);
		Dimension comboDim = new Dimension(150, 20);
		primariesCombo = new JComboBox();
		primariesCombo.setPreferredSize(comboDim);
		primariesCombo.setSize(comboDim);

		primariesCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				_white31Canvas
						.setPrimariesToDisplay((IRgbPrimary) primariesCombo
								.getSelectedItem());
				_white31Canvas.repaint();
				_white76Canvas
						.setPrimariesToDisplay((IRgbPrimary) primariesCombo
								.getSelectedItem());
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

			public void itemStateChanged(ItemEvent e) {
				canvasPan.removeAll();
				if (chartsCombo.getSelectedIndex() == 0) {
					canvasPan.add(_white76Canvas);
				} else if (chartsCombo.getSelectedIndex() == 1) {
					canvasPan.add(_white31Canvas);
				} else {
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
		JPanel softCorrPan = new JPanel(new GridBagLayout());
		GridBagConstraints con7 = new GridBagConstraints();
		TitledBorder softCorrBorder = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black),
				"White soft correction");
		softCorrBorder.setTitleColor(JHealerColors.TEXT_COLOR);
		softCorrPan.setBorder(softCorrBorder);
		Dimension txtDim = new Dimension(65, 20);

		rNumberModel = new SpinnerNumberModel(4095, 0, 4095, 50);
		gNumberModel = new SpinnerNumberModel(4095, 0, 4095, 50);
		bNumberModel = new SpinnerNumberModel(4095, 0, 4095, 50);
		rTxt = new JSpinner(rNumberModel);
		rTxt.setPreferredSize(txtDim);
		gTxt = new JSpinner(gNumberModel);
		gTxt.setPreferredSize(txtDim);
		bTxt = new JSpinner(bNumberModel);
		bTxt.setPreferredSize(txtDim);

		final JCheckBox useSoftCorr = new JCheckBox();
		useSoftCorr.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ColorHealerModel model = ColorHealerModel._instance;
				if (!useSoftCorr.isSelected()) {

					WhiteSoftwareCorrection corr = new WhiteSoftwareCorrection(
							rNumberModel.getNumber().intValue() * 65535 / 4095,
							gNumberModel.getNumber().intValue() * 65535 / 4095,
							bNumberModel.getNumber().intValue() * 65535 / 4095);
					model.setSoftWhiteCorrection(corr);
					model.setSoftWhiteCorrectionEnable(true);
					valid();
				} else {
					model.setSoftWhiteCorrectionEnable(false);
					unValid();

				}
			}
		});
		checkSoftCorrButt = new JButton("Check");
		checkSoftCorrButt.setPreferredSize(new Dimension(100, 20));
		checkSoftCorrButt.setEnabled(false);
		checkSoftCorrButt.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				checkSoftCorrButt.setEnabled(false);
				Thread thread;
				Runnable runnable = new Runnable() {
					public void run() {
						WhiteSoftwareCorrection corr = new WhiteSoftwareCorrection(
								rNumberModel.getNumber().intValue() * 65535 / 4095,
								gNumberModel.getNumber().intValue() * 65535 / 4095,
								bNumberModel.getNumber().intValue() * 65535 / 4095);
						int screenIndex = ColorHealerModel._instance
								.getDisplayDevice().getOsIndex();
						corr.sendPreviewLUT();
						try {
							ColorHealerModel._instance.getSocketServer()
									.sendMessage(
											"SHOULD_DISPLAY " + screenIndex
													+ " " + true + "\n");
							oneWhite();
							ColorHealerModel._instance.getSocketServer()
									.sendMessage(
											"SHOULD_DISPLAY " + screenIndex
													+ " " + false + "\n");
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						checkSoftCorrButt.setEnabled(true);
						if (useSoftCorr.isSelected())
							valid();

					}
				};
				// start thread
				thread = new Thread(runnable, "mesure gain");
				thread.start();

			}
		});

		con7.fill = GridBagConstraints.HORIZONTAL;
		con7.insets = new Insets(0, 2, 2, 2);
		con7.gridx = 0;
		con7.gridy = 0;
		softCorrPan.add(rTxt, con7);
		con7.gridx = 1;
		softCorrPan.add(gTxt, con7);
		con7.gridx = 2;
		softCorrPan.add(bTxt, con7);
		con7.gridx = 3;
		softCorrPan.add(checkSoftCorrButt, con7);
		con7.gridy = 0;
		con7.gridx = 4;
		JLabel useSoftCorrLab = new JLabel("Use it");
		JPanel useSoftCorrPan = new JPanel(new FlowLayout(FlowLayout.CENTER));
		useSoftCorrPan.add(useSoftCorr);
		useSoftCorrPan.add(useSoftCorrLab);
		softCorrPan.add(useSoftCorrPan, con7);

		GridBagConstraints con = new GridBagConstraints();
		con.gridx = 1;
		con.gridy = 0;
		con.gridwidth = 1;
		con.anchor = GridBagConstraints.PAGE_START;
		con.fill = GridBagConstraints.HORIZONTAL;
		con.insets = new Insets(1, 1, 1, 1);

		whitePan.add(canvasPan, con);
		con.gridx = 0;
		whitePan.add(_whiteGaugeCanvas, con);
		con.gridy = 1;
		con.gridwidth = 2;
		whitePan.add(buttPan, con);

		con.gridx = 0;
		con.gridy = 3;
		con.gridwidth = 2;
		whitePan.add(measurePan, con);
		con.gridx = 0;
		con.gridy = 4;
		con.gridwidth = 2;
		whitePan.add(displayPan, con);
		con.gridy = 5;
		whitePan.add(softCorrPan, con);
		con.fill = GridBagConstraints.BOTH;
		con.weighty = 1.0;
		con.gridy = 5;
		whitePan.add(new JPanel(), con);

		// ////////gray pan

		_gray31Canvas = new JCIE31PrimariesCanvas(EStandardRgbPrimaries.REC709);
		_gray76Canvas = new JCIE76PrimariesCanvas(EStandardRgbPrimaries.REC709);

		final JPanel grayCanvasPan = new JPanel(new FlowLayout(
				FlowLayout.CENTER));
		grayCanvasPan.add(_gray76Canvas);
		_magnifiedCanvas2 = new MagnifiedCanvas();
		_grayGaugeCanvas = new GaugeCanvas(10, 0, 0);

		_grayStartBut = new JButton("start measures");
		_grayStartBut.setPreferredSize(new Dimension(100, 20));
		_grayStartBut.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ColorHealerModel model = ColorHealerModel._instance;
				if (!_isMeasuring) {
					if (_measurePrimChec2.isSelected()) {

						ColorHealerModel._instance.getCurrentMeasuresSet()
								.mesureThisColor(model.getCurrentMeasuresSet(),
										Color.red, "primary measure");
						ColorHealerModel._instance.getCurrentMeasuresSet()
								.mesureThisColor(model.getCurrentMeasuresSet(),
										Color.green, "primary measure");
						ColorHealerModel._instance.getCurrentMeasuresSet()
								.mesureThisColor(model.getCurrentMeasuresSet(),
										Color.blue, "primary measure");
						_grayStartBut.repaint();
					}
					_grayStartBut.setText("stop measures");
					_isMeasuring = true;
					_tabPane.setEnabledAt(whitePaneIndex, false);
					_description = "Try to reduce deltas.\nIf you can't force validation.";
					_tabPane.repaint();
					ColorHealerGui.mainWindow.rePaintMenu();
					gray();

				} else {

					_isMeasuring = false;
					valid();
				}

			}
		});
		JPanel buttPan2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttPan2.add(_grayStartBut);
		_measurePrimChec2 = new JCheckBox();
		_measurePrimChec2.setSelected(true);
		JLabel mesPrimLab2 = new JLabel("Measure primaries");
		buttPan2.add(_measurePrimChec2);
		buttPan2.add(mesPrimLab2);
		// ///////////////

		JPanel measurePan2 = new JPanel(new GridBagLayout());
		TitledBorder measureBorder2 = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Measures");
		measureBorder2.setTitleColor(JHealerColors.TEXT_COLOR);
		measurePan2.setBorder(measureBorder2);
		GridBagConstraints con3 = new GridBagConstraints();

		_dxLab2 = new JLabel("-");
		_dxLab2.setPreferredSize(labSize);
		_dyLab2 = new JLabel("-");
		_dyLab2.setPreferredSize(labSize);
		_dYLab2 = new JLabel("-");
		_dYLab2.setPreferredSize(labSize);
		_xLab2 = new JLabel("-");
		_xLab2.setPreferredSize(labSize);
		_yLab2 = new JLabel("-");
		_yLab2.setPreferredSize(labSize);
		_YLab2 = new JLabel("-");
		_YLab2.setPreferredSize(labSize);
		_colorTemp2 = new JLabel("-");
		_colorTemp2.setPreferredSize(labSize);
		_dcolorTemp2 = new JLabel("-");
		_dcolorTemp2.setPreferredSize(labSize);
		_goodBad2 = new JLabel("-");
		_goodBad2.setPreferredSize(labSize);
		_goodBad2.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		_goodBad2.setHorizontalAlignment(JLabel.CENTER);

		con3.gridx = 0;
		con3.gridy = 0;
		con3.gridwidth = 1;
		con3.anchor = GridBagConstraints.FIRST_LINE_START;
		con3.fill = GridBagConstraints.BOTH;
		con3.insets = new Insets(1, 1, 1, 1);
		measurePan2.add(new JLabel("x : "), con3);
		con3.gridx = 1;
		measurePan2.add(_xLab2, con3);
		con3.gridx = 2;
		measurePan2.add(_dxLab2, con3);
		con3.gridx = 3;
		measurePan2.add(new JLabel("y : "), con3);
		con3.gridx = 4;
		measurePan2.add(_yLab2, con3);
		con3.gridx = 5;
		measurePan2.add(_dyLab2, con3);
		con3.gridx = 6;
		measurePan2.add(new JLabel("Y : "), con3);
		con3.gridx = 7;
		measurePan2.add(_YLab2, con3);
		con3.gridx = 8;
		measurePan2.add(_dYLab2, con3);
		con3.gridy = 1;
		con3.gridx = 0;
		measurePan2.add(new JLabel("T� : "), con3);
		con3.gridx = 1;
		measurePan2.add(_colorTemp2, con3);
		con3.gridx = 2;
		measurePan2.add(_dcolorTemp2, con3);
		con3.gridx = 3;
		con3.gridwidth = 2;
		measurePan2.add(_goodBad2, con3);

		// /
		JPanel displayPan2 = new JPanel(new GridBagLayout());
		GridBagConstraints con6 = new GridBagConstraints();
		TitledBorder displayBorder2 = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.black), "Display");
		displayBorder2.setTitleColor(JHealerColors.TEXT_COLOR);
		displayPan2.setBorder(displayBorder2);
		primariesCombo2 = new JComboBox();
		primariesCombo2.setPreferredSize(comboDim);
		primariesCombo2.setSize(comboDim);

		primariesCombo2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				_gray31Canvas
						.setPrimariesToDisplay((IRgbPrimary) primariesCombo2
								.getSelectedItem());
				_gray31Canvas.repaint();
				_gray76Canvas
						.setPrimariesToDisplay((IRgbPrimary) primariesCombo2
								.getSelectedItem());
				_gray76Canvas.repaint();
			}
		});
		final JComboBox chartsCombo2 = new JComboBox();
		chartsCombo2.setPreferredSize(comboDim);
		chartsCombo2.setSize(comboDim);
		chartsCombo2.addItem("CIE 1976");
		chartsCombo2.addItem("CIE 1931");
		chartsCombo2.addItem("Magnified");
		chartsCombo2.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				grayCanvasPan.removeAll();
				if (chartsCombo2.getSelectedIndex() == 0) {
					grayCanvasPan.add(_gray76Canvas);
				} else if (chartsCombo2.getSelectedIndex() == 1) {
					grayCanvasPan.add(_gray31Canvas);

				} else {
					grayCanvasPan.add(_magnifiedCanvas2);

				}
				grayCanvasPan.validate();
				grayCanvasPan.repaint();

			}
		});
		con6.gridx = 0;
		con6.gridy = 0;
		con6.gridwidth = 1;
		con6.anchor = GridBagConstraints.FIRST_LINE_START;
		con6.fill = GridBagConstraints.BOTH;
		con6.insets = new Insets(0, 3, 0, 3);
		displayPan2.add(new JLabel("Primaries :"), con6);
		con6.gridx = 1;
		displayPan2.add(primariesCombo2, con6);
		con6.gridx = 2;
		displayPan2.add(new JLabel("Chart :"), con6);
		con6.gridx = 3;
		displayPan2.add(chartsCombo2, con6);

		GridBagConstraints con4 = new GridBagConstraints();
		con4.gridx = 1;
		con4.gridy = 0;
		con4.gridwidth = 1;
		con4.anchor = GridBagConstraints.PAGE_START;
		;
		con4.fill = GridBagConstraints.HORIZONTAL;
		con4.insets = new Insets(1, 1, 1, 1);

		grayPan.add(grayCanvasPan, con4);
		con4.gridx = 0;
		grayPan.add(_grayGaugeCanvas, con4);

		con4.gridx = 0;
		con4.gridy = 1;
		con4.gridwidth = 2;
		grayPan.add(buttPan2, con4);

		con4.gridx = 0;
		con4.gridy = 3;
		con4.gridwidth = 2;
		grayPan.add(measurePan2, con4);

		con4.gridx = 0;
		con4.gridy = 4;
		con4.gridwidth = 2;
		grayPan.add(displayPan2, con4);
		con4.fill = GridBagConstraints.BOTH;
		con4.weighty = 1.0;
		con4.gridy = 5;
		grayPan.add(new JPanel(), con4);

		// /
		_tabPane.add("White point", whitePan);
		_tabPane.add("Gray point", grayPan);

		getContentPane().add(_tabPane, BorderLayout.CENTER);
	}

	private void oneWhite() {
		ColorHealerModel model = ColorHealerModel._instance;
		ITarget target = model.getTarget();
		final MeasuresSet measures = model.getCurrentMeasuresSet();
		boolean res = measures.mesureThisColor(model.getCurrentMeasuresSet(),
				Color.white, "white measure");
		Point2f targetPoint = target.getColorTemp().getxyCoordinates();
		float colorDelta = target.getColorDelta();
		float lumDelta = target.getLumDelta();
		float targetLum = target.getMaxLum();
		int targetTemp = target.getColorTemp().getValue();
		if (!res)
			return;
		// set measured values
		if (measures.getMeasure(Color.white) != null) {
			Point3f measuredPoint = measures.getMeasure(Color.white).getValue();
			_magnifiedCanvas.setMeasuredPoint(measuredPoint._a,
					measuredPoint._b);
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
			_xLab.setText(String
					.valueOf(((int) (measuredPoint._a * 1000) / 1000f)));
			_yLab.setText(String
					.valueOf(((int) (measuredPoint._b * 1000) / 1000f)));
			_YLab.setText(String
					.valueOf(((int) (measuredPoint._c * 1000) / 1000f)));

			int approxColorTemp = (int) EStandardIlluminants
					.getApproximateColorTemperature(new Point2f(
							measuredPoint._a, measuredPoint._b));

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
					|| (miredDelta > target.getTCDelta())) {
				_goodBad.setText("BAD");
				_goodBad.setForeground(Color.red);
				_magnifiedCanvas.setOK(false);
			} else {
				_goodBad.setText("GOOD");
				_goodBad.setForeground(Color.green);
				_magnifiedCanvas.setOK(true);

			}

			_whiteGaugeCanvas.setMeasure(measuredPoint._c);

		}

		_white31Canvas.repaint();
		_white76Canvas.repaint();
		_magnifiedCanvas.repaint();
		_whiteGaugeCanvas.repaint();
	}

	private void white() {
		final MeasuresSet measures = ColorHealerModel._instance
				.getCurrentMeasuresSet();

		Thread thread;
		Runnable runnable = new Runnable() {
			public void run()

			{
				lockDependantStep("Start measure");
				boolean isMKCS200 = ColorHealerModel._instance.getProbe()
						.getEProbeType() == EProbeType.MK_CS200;
				do {
					oneWhite();
				} while (_isMeasuring && !isMKCS200);
				if (isMKCS200) {
					_measurePrimChec.setSelected(false);
					_isMeasuring = false;
				}
				valid();
				_forceValidation.setEnabled(true);
				_whiteStartBut.setEnabled(true);
				_whiteStartBut.setText("start measures");
				_tabPane.setEnabledAt(grayPaneIndex, true);
				ColorMeasure white = measures.getMeasure(Color.white);
				ColorMeasure red = measures.getMeasure(Color.red);
				ColorMeasure green = measures.getMeasure(Color.green);
				ColorMeasure blue = measures.getMeasure(Color.blue);
				if ((white != null) && (red != null) && (green != null)
						&& (blue != null)) {
					WhiteSoftwareCorrection tmpSoftCorrection = new WhiteSoftwareCorrection(
							white.getValue(), red.getValue(), green.getValue(),
							blue.getValue());
					rNumberModel
							.setValue(tmpSoftCorrection.getRedMaxValue() * 4095 / 65535);
					gNumberModel
							.setValue(tmpSoftCorrection.getGreenMaxValue() * 4095 / 65535);
					bNumberModel
							.setValue(tmpSoftCorrection.getBlueMaxValue() * 4095 / 65535);
					checkSoftCorrButt.setEnabled(true);

				}

			}
		};
		// start thread
		thread = new Thread(runnable, "mesure gain");
		thread.start();
	}

	private void gray() {
		final MeasuresSet measures = ColorHealerModel._instance
				.getCurrentMeasuresSet();
		Thread thread;
		Runnable runnable = new Runnable() {
			public void run() {
				lockDependantStep("Start measure");
				ITarget target = ColorHealerModel._instance.getTarget();
				Point2f targetPoint = target.getColorTemp().getxyCoordinates();
				float colorDelta = target.getColorDelta();
				float lumDelta = target.getLumDelta();
				float targetLum = target.getGrayGammaTarget();
				int targetTemp = target.getColorTemp().getValue();
				boolean isMKCS200 = ColorHealerModel._instance.getProbe()
						.getEProbeType() == EProbeType.MK_CS200;
				ColorHealerModel model = ColorHealerModel._instance;
				do {
					boolean res = measures.mesureThisColor(
							model.getCurrentMeasuresSet(), ITarget.GAMMA_GRAY,
							"bias measure");

					if (!res)
						return;
					// set measured values
					if (measures.getMeasure(ITarget.GAMMA_GRAY) != null) {
						Point3f measuredPoint = measures.getMeasure(
								ITarget.GAMMA_GRAY).getValue();
						_magnifiedCanvas2.setMeasuredPoint(measuredPoint._a,
								measuredPoint._b);
						float dx2 = ((int) ((measuredPoint._a - targetPoint._a) * 1000)) / 1000f;
						float dy2 = ((int) ((measuredPoint._b - targetPoint._b) * 1000)) / 1000f;
						float dY2 = ((int) ((measuredPoint._c - targetLum) * 1000)) / 1000f;
						_dxLab2.setText("" + dx2);
						_dyLab2.setText("" + dy2);
						_dYLab2.setText("" + dY2);
						dx2 = Math.abs(dx2);
						dy2 = Math.abs(dy2);
						dY2 = Math.abs(dY2);

						if (dx2 < colorDelta)
							_dxLab2.setForeground(Color.green);
						else
							_dxLab2.setForeground(Color.red);

						if (dy2 < colorDelta)
							_dyLab2.setForeground(Color.green);
						else
							_dyLab2.setForeground(Color.red);

						if (dY2 < lumDelta)
							_dYLab2.setForeground(Color.green);
						else
							_dYLab2.setForeground(Color.red);
						_xLab2.setText(String
								.valueOf(((int) (measuredPoint._a * 1000) / 1000f)));
						_yLab2.setText(String
								.valueOf(((int) (measuredPoint._b * 1000) / 1000f)));
						_YLab2.setText(String
								.valueOf(((int) (measuredPoint._c * 1000) / 1000f)));
						_grayGaugeCanvas.setMeasure(measuredPoint._c);

						int approxColorTemp = (int) EStandardIlluminants
								.getApproximateColorTemperature(new Point2f(
										measuredPoint._a, measuredPoint._b));

						_colorTemp2.setText("" + approxColorTemp);
						if ((dx2 < colorDelta) && (dy2 < colorDelta))
							_dcolorTemp2.setForeground(Color.green);
						else
							_dcolorTemp2.setForeground(Color.red);

						_dcolorTemp2.setText(""
								+ (approxColorTemp - targetTemp));

						if ((dx2 > colorDelta) || (dy2 > colorDelta)
								|| (dY2 > lumDelta)) {
							_goodBad2.setText("BAD");
							_goodBad2.setForeground(Color.red);
							_magnifiedCanvas2.setOK(false);
						} else {
							_goodBad2.setText("GOOD");
							_goodBad2.setForeground(Color.green);
							_magnifiedCanvas2.setOK(true);

						}
					}
					_gray31Canvas.repaint();
					_gray76Canvas.repaint();
					_magnifiedCanvas2.repaint();
					_grayGaugeCanvas.repaint();
				} while (_isMeasuring && !isMKCS200);
				if (isMKCS200) {
					_measurePrimChec2.setSelected(false);
					_isMeasuring = false;
				}
				_grayStartBut.setEnabled(true);
				_grayStartBut.setText("start measures");
				_tabPane.setEnabledAt(whitePaneIndex, true);
				valid();

			}
		};
		// start thread
		thread = new Thread(runnable, "mesure bias");
		thread.start();
	}

	public boolean canUnLockDependantStep() {
		return _status == StepStatus.OK;
	}

	public void init()

	{
		if (!_isInit) {
			List<IRgbPrimary> custPrimaries = ColorHealerModel._instance
					.getCustomPrimaries();
			for (IRgbPrimary iRgbPrimary : custPrimaries) {
				primariesCombo.addItem(iRgbPrimary);
				primariesCombo2.addItem(iRgbPrimary);

			}
			EStandardRgbPrimaries[] primaries = EStandardRgbPrimaries.values();
			for (IRgbPrimary rgbPrimary : primaries) {
				primariesCombo.addItem(rgbPrimary);
				primariesCombo2.addItem(rgbPrimary);
			}

			_isInit = true;
		}
		ColorHealerModel._instance.getSocketServer().displayColor(Color.gray,
				true);
		ITarget target = ColorHealerModel._instance.getTarget();
		Point2f targetPoint = target.getColorTemp().getxyCoordinates();
		primariesCombo.setSelectedItem(target.getPrimaries());
		_white31Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
		_white31Canvas.repaint();
		_white76Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
		_white76Canvas.repaint();
		_magnifiedCanvas.setTargetPoint(targetPoint._a, targetPoint._b);
		_magnifiedCanvas.repaint();
		_magnifiedCanvas2.setTargetPoint(targetPoint._a, targetPoint._b);
		_magnifiedCanvas2.repaint();
		_gray31Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
		_gray31Canvas.repaint();
		_gray76Canvas.setTargetPoint(targetPoint._a, targetPoint._b);
		_gray76Canvas.repaint();
		_whiteGaugeCanvas.setMax(150);
		_whiteGaugeCanvas.setTarget(target.getMaxLum());
		_whiteGaugeCanvas.repaint();
		_grayGaugeCanvas.setMax(10);
		_grayGaugeCanvas.setTarget(target.getGrayGammaTarget());
		_grayGaugeCanvas.repaint();

		//
		AbstractProbe probe = ColorHealerModel._instance.getProbe();
		if (probe instanceof GammaProbeAndColorProbe) {
			GammaProbeAndColorProbe gammaColorProbe = (GammaProbeAndColorProbe) probe;
			try {
				gammaColorProbe.selectColorProbe();
			} catch (Exception e) {
				e.printStackTrace();
			}
			JOptionPane
					.showMessageDialog(null,
							"Use " + gammaColorProbe.getSelectedProbeType()
									+ " here !", "Select right probe",
							JOptionPane.OK_OPTION);
		}

	}

	public void unLock() {
		_status = _oldStatus;
	}

	private void unValid() {
		boolean mustRepain = true;
		if (_status == StepStatus.NOT_COMPLETE)
			mustRepain = false;
		else {
			_status = StepStatus.NOT_COMPLETE;
			_description = "White point checked";
		}
		lockDependantStep("White point isn't validated !");
		if (mustRepain) {
			ColorHealerGui.mainWindow.rePaintMenu();
			_tabPane.repaint();
		}
	}

	public void valid() {

		if (_forceValidation.isSelected()) {
			if (_status != StepStatus.OK) {
				_status = StepStatus.OK;
				unLockDependantStep();
				_description = "Validation forced";
				ColorHealerGui.mainWindow.rePaintMenu();
				_tabPane.repaint();
			}
			return;
		}
		ITarget target = ColorHealerModel._instance.getTarget();
		Point2f targetPoint = target.getColorTemp().getxyCoordinates();
		float colorDelta = target.getColorDelta();
		float lumDelta = target.getLumDelta();
		float targetLum = target.getMaxLum();
		final MeasuresSet measures = ColorHealerModel._instance
				.getCurrentMeasuresSet();
		ColorMeasure white = measures.getMeasure(Color.white);
		if (white == null)
			return;
		Point3f measuredPoint = white.getValue();
		float dx = Math.abs(measuredPoint._a - targetPoint._a);
		float dy = Math.abs(measuredPoint._b - targetPoint._b);
		float dY = Math.abs(measuredPoint._c - targetLum);

		boolean mustRepain = true;

		int approxColorTemp = (int) EStandardIlluminants
				.getApproximateColorTemperature(new Point2f(measuredPoint._a,
						measuredPoint._b));
		int targetTemp = target.getColorTemp().getValue();
		int miredDelta = 1000000 / approxColorTemp - 1000000 / targetTemp;
		miredDelta = Math.abs(miredDelta);

		if (!((dx > colorDelta) || (dy > colorDelta) || (dY > lumDelta) || (miredDelta > target
				.getTCDelta()))) {
			if (_status == StepStatus.OK)
				mustRepain = false;
			else {
				_status = StepStatus.OK;
				_description = "White point checked";
			}
			unLockDependantStep();
		} else {
			if (_status == StepStatus.NOT_COMPLETE)
				mustRepain = false;
			else {
				_status = StepStatus.NOT_COMPLETE;
				_description = "White point checked";
			}
			lockDependantStep("White point isn't validated !");
		}
		if (mustRepain) {
			ColorHealerGui.mainWindow.rePaintMenu();
			_tabPane.repaint();
		}

	}

	public void lock(String reason) {
		_oldStatus = _status;
		_status = StepStatus.DISABLE;
	}

}
