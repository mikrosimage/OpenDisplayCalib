package fr.hd3d.colortribe.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import fr.hd3d.colortribe.ColorHealerGui;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.DisplayDevice;
import fr.hd3d.colortribe.core.protocols.CalibrationProtocol;
import fr.hd3d.colortribe.core.protocols.MeasuresProtocol;
import fr.hd3d.colortribe.gui.components.JImageCanvas;


public class HealerWaitingWindow extends JFrame
{

    private JLabel _statutLab;
    private JButton _funcBut;
    private boolean _isConnected;
    /**
	 * 
	 */
    private static final long serialVersionUID = 8666214186131138078L;

    private int _dragX, _dragY;
    private int _oldPosX, _oldPosY;
    static public HealerWaitingWindow _instance = new HealerWaitingWindow();

    private HealerWaitingWindow()
    {

        super("ColorHealer");
        _isConnected = false;

        UIManager.put("Panel.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("Panel.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("Label.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("Label.foreground", JHealerColors.TEXT_COLOR);
        UIManager.put("Button.background", JHealerColors.BACKGROUNG_COLOR);
        UIManager.put("Button.foreground", JHealerColors.TEXT_COLOR);
        // UIManager.put("Button.shadow", SHADOW_COLOR);
        // UIManager.put("Button.disabledText", SHADOW_COLOR);
        UIManager.put("Button.border", new LineBorder(Color.black, 2));

        JImageCanvas bandeau = new JImageCanvas("img/bandeau_ww.png", 500, 76);

        bandeau.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                _dragX = e.getX() + getLocation().x;
                _dragY = e.getY() + getLocation().y;
                _oldPosX = getLocation().x;
                _oldPosY = getLocation().y;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        bandeau.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e)
            {
                int deltaX = (e.getX() + getLocation().x) - _dragX;
                int deltaY = (e.getY() + getLocation().y) - _dragY;
                setLocation(_oldPosX + deltaX, _oldPosY + deltaY);
            }

            public void mouseMoved(MouseEvent e)
            {}
        });

        setSize(500, 265);
        JPanel mainContainer = new JPanel(new BorderLayout());
        JPanel butContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        JPanel labContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        Dimension buttonDim = new Dimension(80, 20);
        _statutLab = new JLabel("Waiting for a connexion");
        _funcBut = new JButton("Close");
        _funcBut.setPreferredSize(buttonDim);
        _funcBut.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e)
            {
                if (!_isConnected)
                {
                    ColorHealerModel._instance.getSocketServer().closeServer();
                    System.exit(0);
                }
                else
                {

                    ColorHealerModel._instance.setProtocol(new CalibrationProtocol());
                    ColorHealerGui.mainWindow.open();
                    close();
                }
            }

        });
        JButton justMeasureButton = new JButton("Measure");
        justMeasureButton.setPreferredSize(buttonDim);
        justMeasureButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                super.mousePressed(e);
                if (!_isConnected)
                {
                    ColorHealerModel._instance.initDisplayDevice();
                    ColorHealerModel._instance.unplugSocketServer();
                }
                ColorHealerModel._instance.setProtocol(new MeasuresProtocol());
                ColorHealerGui.mainWindow.open();
                close();

            }
        });
        butContainer.add(_funcBut);
        butContainer.add(justMeasureButton);
        labContainer.add(_statutLab);
        mainContainer.add(labContainer, BorderLayout.CENTER);
        mainContainer.add(butContainer, BorderLayout.SOUTH);
        mainContainer.add(bandeau, BorderLayout.NORTH);
        add(mainContainer);
        setUndecorated(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = this.getSize();
        if (size.height > screenSize.height)
            size.height = screenSize.height;
        setLocation((int) getLocation().getX() + 5, (screenSize.height - size.height) / 2);

        initComThread();
    }

    private void initComThread()
    {
        Runnable runnable = new Runnable() {
            public void run()
            {
                try
                {
                    String message = ColorHealerModel._instance.getSocketServer().acceptCom();
                    DisplayDevice dispDev = ColorHealerModel._instance.setDisplayDevice(message);
                    ColorHealerModel._instance.setClientName(ColorHealerModel._instance.getSocketServer()
                            .getInetAdressHostName());
                  
                    _isConnected = true;
                    _statutLab.setText(ColorHealerModel._instance.getSocketServer().getInetAdressHostName()
                            + " ask a connection for screen " + dispDev.getOsIndex());
                    _funcBut.setText("Calibrate");

                }
                catch (IOException e)
                {
                    System.out.println("Can't connect");
                }

            }
        };
        Thread thread = new Thread(runnable, "Connection");
        thread.start();
    }

    public void open()
    {
        setVisible(true);

    }

    private void close()
    {
        setVisible(false);

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
