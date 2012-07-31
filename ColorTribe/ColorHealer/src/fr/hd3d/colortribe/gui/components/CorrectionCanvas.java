package fr.hd3d.colortribe.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.correction.AbstractCorrection;


public class CorrectionCanvas extends GlLikeCanvas
{
    private static final long serialVersionUID = 7909048123494155203L;
    private boolean _displayRedLine = true;
    private boolean _displayGreenLine = true;
    private boolean _displayBlueLine = true;
    private float _userScale = 1;
    private float _userTranslateX = 0;
    private float _userTranslateY = 0;
    int _lastX;
    int _lastY;

    public void resetUserTransform()
    {
        _userScale = 1;
        _userTranslateX = 0;
        _userTranslateY = 0;
        repaint();
    }

    public void incScale(float howMuch)
    {
        _userScale += howMuch;
        repaint();
    }

    public void decScale(float howMuch)
    {
        _userScale -= howMuch;
        if (_userScale < 1)
            _userScale = 1;
        repaint();
    }

    public CorrectionCanvas()
    {
        super();
        setBackground(Color.black);
        Dimension dim = new Dimension(350, 350);
        setSize(350, 350);
        setPreferredSize(dim);
        setMaximumSize(dim);
        this.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                if (e.getWheelRotation() < 0)
                {
                    incScale(1f);
                }
                else
                {
                    decScale(1f);
                }
                repaint();
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e)
            {
                int addX = e.getX() - _lastX;
                int addY = e.getY() - _lastY;
                _lastX = e.getX();
                _lastY = e.getY();
                _userTranslateX += addX;
                _userTranslateY += addY;
                repaint();
            }

            public void mouseMoved(MouseEvent e)
            {}
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                _lastX = e.getX();
                _lastY = e.getY();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {}
        });
    }

    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(Color.black);
        g2.clearRect(0, 0, getWidth(), getHeight());
        g2.translate(_userTranslateX, _userTranslateY);
        super.paintComponent(g2);
        drawAxis(g2);
        int scaleX = (int) (320 * _userScale);
        int scaleY = (int) (320 * _userScale);
        drawLine(g2, Color.white, 0, 0, 1, 1, scaleX, scaleY);
        drawLine(g2, Color.darkGray, 0, 1, 1, 1, scaleX, scaleY);
        drawLine(g2, Color.darkGray, 1, 0, 1, 1, scaleX, scaleY);
        // drawLine(g2, Color.darkGray, 0, 0, 0, 1, scaleX, scaleY);
        AbstractCorrection colorCorrection = ColorHealerModel._instance.getCorrection();
        // display correction : x -> pixel value, y -> corrected pixel value
        if (colorCorrection != null)
        {
            List<Point2f> points;
            Color color;
            boolean display;
            for (int i = 0; i < 3; i++)
            {
                if (i == 0)
                {
                    points = colorCorrection.getRedCorrection();
                    color = Color.red;
                    display = _displayRedLine;
                }
                else if (i == 1)
                {
                    points = colorCorrection.getGreenCorrection();
                    color = Color.green;
                    display = _displayGreenLine;
                }
                else
                {
                    points = colorCorrection.getBlueCorrection();
                    color = Color.blue;
                    display = _displayBlueLine;
                }
                Point2f lastPoint = null;
                if (display)
                    for (Point2f point : points)
                    {
                        // drawShape(g2, color, point.x, point.y, scaleX, scaleY, smallPointSize,
                        // EShapeType.BORDERED_FILLED_RECTANGLE);
                        if (lastPoint != null)
                            drawLine(g2, color, point._a, point._b, lastPoint._a, lastPoint._b, scaleX, scaleY);
                        lastPoint = point;
                    }
            }
            // display mesured value : x -> pixel values, y -> lum
            // for (int i = 0; i < 3; i++) {
            // List<ColorMeasure> measures;
            // Color co;
            // if (i == 0) {
            // measures = HappyCalibModel._instance.getSamplesSet().getRedSortedMesures();
            // co = Color.red.brighter();
            // } else if (i == 1) {
            // measures = HappyCalibModel._instance.getSamplesSet().getGreenSortedMesures();
            // co = Color.green.brighter();
            // } else {
            // measures = HappyCalibModel._instance.getSamplesSet().getBlueSortedMesures();
            // co = Color.blue.brighter();
            // }
            // ColorMeasure mes2 = null;
            // for (ColorMeasure mes : measures) {
            // if (mes2 != null)
            // drawLine(g2, co, mes.getFloatXIndex(), mes.getValue().z, mes2.getFloatXIndex(), mes2.getValue().z,
            // scaleX, 1);
            // mes2 = mes;
            // }
            // }
        }
    }

    public void setDisplayBlueLine(boolean displayBlueLine)
    {
        _displayBlueLine = displayBlueLine;
    }

    public void setDisplayGreenLine(boolean displayGreenLine)
    {
        _displayGreenLine = displayGreenLine;
    }

    public void setDisplayRedLine(boolean displayRedLine)
    {
        _displayRedLine = displayRedLine;
    }
}
