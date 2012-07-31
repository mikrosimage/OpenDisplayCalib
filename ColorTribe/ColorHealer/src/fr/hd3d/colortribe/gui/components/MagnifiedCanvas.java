package fr.hd3d.colortribe.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.core.ColorHealerModel;


@SuppressWarnings("serial")
public class MagnifiedCanvas extends GlLikeCanvas
{
    private Point2f _targetPoint;
    private Point2f _measuredPoint;
    private boolean _isOK = false;

    public MagnifiedCanvas()
    {
        setBackground(Color.black);
        setSize(300, 300);
        setPreferredSize(new Dimension(300, 300));
        _targetPoint = new Point2f();
        _measuredPoint = new Point2f();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e)
            {
                super.componentResized(e);
                computeCoeff();
            }
        });

    }

    public void setTargetPoint(float x, float y)
    {
        _targetPoint._a = x;
        _targetPoint._b = y;
        computeCoeff();
    }

    public void setMeasuredPoint(float x, float y)
    {
        _measuredPoint._a = x;
        _measuredPoint._b = y;
        computeCoeff();
    }

    private float _focusSize;
    private float _scale;

    private static int TARGET_SIZE = 10;

    private void computeCoeff()
    {
        float targetWidth = Math.abs(_measuredPoint._a - _targetPoint._a);
        float targetHeight = Math.abs(_measuredPoint._b - _targetPoint._b);
        _focusSize = targetWidth > targetHeight ? targetWidth : targetHeight;
        if (_focusSize < ColorHealerModel._instance.getTarget().getColorDelta() * 3)
            _focusSize = ColorHealerModel._instance.getTarget().getColorDelta() * 3;
        _focusSize *= 2;
        _scale = (getWidth() > getHeight() ? getWidth() : getHeight()) / _focusSize;

    }

    public void setOK(boolean isOk)
    {
        _isOK = isOk;
    }

    private Point2f toScreenCoord(Point2f point)
    {
        float tmpX = (point._a - _targetPoint._a) * _scale + getWidth() / 2;
        float tmpY = (point._b - _targetPoint._b) * _scale + getHeight() / 2;
        return new Point2f(tmpX, tmpY);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        g2.setColor(Color.gray);
        g2.drawLine(0, height / 2, width, height / 2);
        g2.drawLine(width / 2, 0, width / 2, height);

        Point2f pt = toScreenCoord(_targetPoint);
        float delta = ColorHealerModel._instance.getTarget().getColorDelta();
        int deltaZoneSize = (int) (delta * _scale) * 2;
        g2.drawRect((int) pt._a - deltaZoneSize / 2, (int) pt._b - deltaZoneSize / 2, deltaZoneSize, deltaZoneSize);
        pt = toScreenCoord(_measuredPoint);
        if (_isOK)
            g2.setColor(Color.green);
        else
            g2.setColor(Color.red);
        g2.fillOval((int) pt._a - TARGET_SIZE / 2, (int) pt._b - TARGET_SIZE / 2, TARGET_SIZE, TARGET_SIZE);
        g2.setColor(Color.gray);
        g2.drawOval((int) pt._a - TARGET_SIZE / 2, (int) pt._b - TARGET_SIZE / 2, TARGET_SIZE, TARGET_SIZE);

        // //
        int thickness = 5;
        int tmp;
        // Red helper
        g2.setColor(Color.red);
        g2.fillRect(width - thickness, 0, thickness, (int) (height * 0.93f));
        // Green helper
        g2.setColor(Color.green);
        g2.fillRect(0, height - thickness, (int) (width * 0.27f), thickness);
        tmp = (int) (0.46 * height);
        g2.fillRect(0, height - tmp, thickness, tmp);
        // Blue helper
        g2.setColor(Color.blue);
        g2.fillRect(0, 0, thickness, (int) (height * .34f));
        g2.fillRect(0, 0, (int) (width * 0.42f), thickness);
    }
}
