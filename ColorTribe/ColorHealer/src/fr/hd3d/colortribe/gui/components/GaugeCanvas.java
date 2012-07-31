package fr.hd3d.colortribe.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;


public class GaugeCanvas extends GlLikeCanvas
{
    private static final long serialVersionUID = -346963067992219048L;
    private float _maxValue;
    private float _minValue;
    private float _targetValue;
    private float _measuredValue;
    private int width = 100;
    private int height = 300;
    private int gradeZoneWidth = width / 2;
    private int gradeZoneHeight = height;
    private int gradeMargin = 5;
    private int gradeWidth = gradeZoneWidth - gradeMargin * 2;
    private int gradeHeight = gradeZoneHeight - gradeMargin * 2;

    public GaugeCanvas(float maxValue, float minValue, float targetValue)
    {
        super();
        _maxValue = maxValue;
        _minValue = minValue;
        _targetValue = targetValue;
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
    }

    private float normalizedValue(float value)
    {
        return value / (_maxValue - _minValue) - _minValue;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(1, -1);
        g2.translate(0, -this.getSize().height);
        // //draw axis
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gradeZoneWidth, gradeZoneHeight);
        g2.translate(gradeMargin, gradeMargin);
        g2.setColor(Color.white);
        g2.drawRect(0, 0, gradeWidth, gradeHeight);
        drawShape(g2, Color.yellow, gradeWidth / 2, (1 - normalizedValue(_targetValue)), 1, gradeHeight, gradeWidth,
                EShapeType.CURSOR);
        drawShape(g2, Color.gray, gradeWidth / 2, (1 - normalizedValue(_measuredValue)), 1, gradeHeight, gradeWidth,
                EShapeType.CURSOR);

        String number = NumberFormat.getInstance().format((int) (_targetValue * 100) / 100f);

        drawString(g2, Color.yellow, 2, (1 - normalizedValue(_targetValue)) * gradeHeight - 2, number);
        number = NumberFormat.getInstance().format((int) (_measuredValue * 100) / 100f);
        float yvalue = normalizedValue(_measuredValue);
        if (yvalue > 0.98)
            yvalue = 0.95f;

        drawString(g2, Color.lightGray, gradeWidth + 10, (1 - yvalue) * gradeHeight, Float.toString(_measuredValue));

        g2.scale(1, -1);
        g2.translate(0, -this.getSize().height);
    }

    public void setTarget(float target)
    {
        _targetValue = target;
    }

    public void setMeasure(float measure)
    {
        _measuredValue = measure;
    }

    public void setMax(float max)
    {
        _maxValue = max;
    }

    public void setMin(float min)
    {
        _minValue = min;
    }
}
