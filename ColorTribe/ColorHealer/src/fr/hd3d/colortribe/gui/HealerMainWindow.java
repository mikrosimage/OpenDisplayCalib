package fr.hd3d.colortribe.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.probes.IProbe;
import fr.hd3d.colortribe.gui.components.JHealerMenu;
import fr.hd3d.colortribe.gui.steps.Step;


public class HealerMainWindow extends JFrame
{
    /**
     * 
     */
    private static final long serialVersionUID = 8666214186131138078L;
    private JPanel _step;
    private JHealerMenu _menu;
    private JButton _closeButt;
    private JPanel _mainContainer = new JPanel(new BorderLayout());
    static public HealerMainWindow _instance = new HealerMainWindow();

    private HealerMainWindow()
    {
        super("ColorHealer");
        setSize(790, 610);
        _mainContainer = new JPanel(new BorderLayout());

        // register the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run()
            {
                ColorHealerModel._instance.getSocketServer().closeServer();
                IProbe probe = ColorHealerModel._instance.getProbe();
                if (probe != null)
                    probe.close();
            }
        });
        ;

        /*
         * try { UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"); } catch (ClassNotFoundException e)
         * {} catch (InstantiationException e) {} catch (IllegalAccessException e) {} catch
         * (UnsupportedLookAndFeelException e) {}
         */
        UIManager.put("Panel.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("Panel.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("Label.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("Label.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("TextArea.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("TextArea.foreground", JHealerColors.TEXT_COLOR);
        // ///button
        UIManager.put("Button.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("Button.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("Button.shadow", JHealerColors.DISABLE_TEXT_COLOR);
        UIManager.put("Button.disabledText", JHealerColors.DISABLE_TEXT_COLOR);
        UIManager.put("Button.border", new LineBorder(Color.black, 2));
        UIManager.put("ToggleButton.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("ToggleButton.foreground", JHealerColors.TEXT_COLOR);
        // UIManager.put("ToggleButton.shadow", SHADOW_COLOR);
        // UIManager.put("ToggleButton.disabledText", SHADOW_COLOR);
        // UIManager.put("ToggleButton.border", new LineBorder(BORDER_COLOR, 1));
        UIManager.put("ToggleButton.select", Color.black);
        // /
        UIManager.put("ComboBox.background", JHealerColors.BACKGROUNG_COLOR.brighter());
        UIManager.put("ComboBox.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("ComboBox.selectionBackground", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("ComboBox.selectionForeground", JHealerColors.TEXT_COLOR);

        UIManager.put("List.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("List.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("List.selectionBackground", JHealerColors.BACKGROUNG_COLOR.darker());
        UIManager.put("List.selectionForeground", JHealerColors.TEXT_COLOR);
        // ComboBox.background
        // ComboBox.buttonBackground
        // ComboBox.buttonDarkShadow
        // ComboBox.buttonHighlight
        // ComboBox.buttonShadow
        // ComboBox.disabledBackground
        // ComboBox.disabledForeground
        // ComboBox.font
        // ComboBox.foreground
        // ComboBox.selectionBackground
        // ComboBox.selectionForeground
        // ComboBox.timeFactor

        // //////error dialogs
        UIManager.put("OptionPane.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("OptionPane.border.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("OptionPane.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("OptionPane.messageForeground", JHealerColors.TEXT_COLOR);
        // font //
        UIManager.put("TextField.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("TextField.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("TextField.caretForeground", JHealerColors.TEXT_COLOR);

        // //
        // UIManager.put("TextField.font", FONT);
        // UIManager.put("TextArea.font", FONT);
        // UIManager.put("Label.font", FONT_BOLD);
        // UIManager.put("Button.font", FONT_BOLD);

        UIManager.put("ToolTip.foreground", JHealerColors.TEXT_COLOR.brighter());
        UIManager.put("Slider.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("Slider.messageForeground", JHealerColors.TEXT_COLOR);
        UIManager.put("RadioButton.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("CheckBox.background", JHealerColors.BACKGROUNG_COLOR);
        // tree
        UIManager.put("Tree.background", JHealerColors.BACKGROUNG_COLOR);

        add(_mainContainer);
        setUndecorated(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = this.getSize();
        if (size.height > screenSize.height)
            size.height = screenSize.height;
        setLocation((int) getLocation().getX() + 5, (screenSize.height - size.height) / 2);

        JPanel downPan = new JPanel();
        _closeButt = new JButton("Close");
        _closeButt.setPreferredSize(new Dimension(80, 20));
        _closeButt.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e)
            {

                int reponse = 66;
                if (!ColorHealerModel._instance.isCalibUpdated())
                    reponse = JOptionPane.showConfirmDialog(null,
                            "Screen calibration is not over.\nDo you really want to quit ?", "Quit ?",
                            JOptionPane.YES_NO_OPTION);

                if (ColorHealerModel._instance.isCalibUpdated() || reponse == JOptionPane.YES_OPTION)
                {
                    try
                    {
                        ColorHealerModel._instance.getSocketServer().sendMessage(
                                "BUH_BYE " + ColorHealerModel._instance.getDisplayDevice().getOsIndex() + "\n");
                    }
                    catch (IllegalAccessException e1)
                    {
                        e1.printStackTrace();
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }

                    System.exit(0);
                }
            }

        });
        downPan.add(_closeButt);
        _mainContainer.add(downPan, BorderLayout.SOUTH);
        add(_mainContainer);
    }

    private boolean isInit = false;

    private void init()
    {
        if (!isInit)
        {
            _step = ColorHealerModel._instance.getProtocol().getSelectedStep().getContentPane();
            _mainContainer.add(_step, BorderLayout.CENTER);
            _menu = new JHealerMenu(this);
            _mainContainer.add(_menu, BorderLayout.WEST);
            isInit = true;
        }

    }

    void open()
    {
        init();
        setVisible(true);

    }

    

    public void rePaintStep()
    {

        _mainContainer.remove(_step);
        Step currStep = ColorHealerModel._instance.getProtocol().getSelectedStep();
        currStep.init();
        _step = currStep.getContentPane();
        _mainContainer.add(_step);
        _mainContainer.validate();
        _mainContainer.repaint();
        // validate();
        // repaint();
    }

    public void rePaintMenu()
    {

        _menu.validate();
        _menu.repaint();
        // validate();
        // repaint();
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        int height = this.getHeight();
        int width = this.getWidth();
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 2, height - 2);
    }

}
