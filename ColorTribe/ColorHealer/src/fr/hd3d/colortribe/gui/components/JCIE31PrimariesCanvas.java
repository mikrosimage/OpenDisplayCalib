package fr.hd3d.colortribe.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.geom.GeneralPath;
import java.util.List;

import fr.hd3d.colortribe.color.ColorMeasure;
import fr.hd3d.colortribe.color.EStandardRgbPrimaries;
import fr.hd3d.colortribe.color.IRgbPrimary;
import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.color.type.Point3f;
import fr.hd3d.colortribe.color.util.ColorSpectrumTables;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;


public class JCIE31PrimariesCanvas extends GlLikeCanvas
{
    private static final long serialVersionUID = -6097659150128153149L;
    private IRgbPrimary _primariesToDisplay;
   

    private Point2f _targetPoint;
    private float _userScale = 1;

    public JCIE31PrimariesCanvas(EStandardRgbPrimaries primariesToDisplay){
        this(primariesToDisplay, 300,300);
    }
    public JCIE31PrimariesCanvas(EStandardRgbPrimaries primariesToDisplay,  int width, int height)
    {
        super();
        setBackground(Color.black);
        setSize(width, height);
        setPreferredSize(new Dimension(width, height));
        _primariesToDisplay = primariesToDisplay;
        _targetPoint = new Point2f();
        // this.addMouseWheelListener(new MouseWheelListener() {
        // public void mouseWheelMoved(MouseWheelEvent e) {
        // if (e.getWheelRotation() < 0) {
        // _userScale = 4;
        // _userTranslateX = +45;
        // _userTranslateY = -300;
        // } else {
        // _userScale = 1;
        // _userTranslateX = 0;
        // _userTranslateY = 0;
        // }
        // repaint();
        // }
        // });
        this.addMouseListener(new MouseAdapter() {
            // private boolean _zoom = false;

            // public void mouseClicked(MouseEvent e)
            // {
            // _zoom = !_zoom;
            // if (_zoom)
            // {
            // _userScale = 4;
            // _userTranslateX = +45;
            // _userTranslateY = -300;
            // }
            // else
            // {
            // _userScale = 1;
            // _userTranslateX = 0;
            // _userTranslateY = 0;
            // }
            // repaint();
            // }
        });
    }
    public IRgbPrimary getPrimariesToDisplay()
    {
        return _primariesToDisplay;
    }
    public void setPrimariesToDisplay(IRgbPrimary primariesToDisplay)
    {
        _primariesToDisplay = primariesToDisplay;
    }

    public void setTargetPoint(float x, float y)
    {
        _targetPoint._a = x;
        _targetPoint._b = y;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawAxis(g2);
        Point2f currentPoint;
        int scale =(int)( getWidth()*1.17);
        MeasuresSet samplesSet = ColorHealerModel._instance.getBasicMeasuresSet();

        if (_userScale == 1)
        {
            // spectrum locus
            GeneralPath gp = new GeneralPath();

            int i = 0;
            while (i < ColorSpectrumTables.spectrum_x.length)
            {
                if (i == 0)
                    gp.moveTo(ColorSpectrumTables.spectrum_x[i] * scale, ColorSpectrumTables.spectrum_y[i] * scale);
                gp.lineTo(ColorSpectrumTables.spectrum_x[i] * scale, ColorSpectrumTables.spectrum_y[i] * scale);
                ++i;
            }
            gp.closePath();
            g2.setColor(Color.darkGray);
            g2.draw(gp);
            // //////////////// RVB measured
            boolean drawTriangle = true;
            GeneralPath triangle = new GeneralPath();
            Point3f currentPoint3D;
            if (samplesSet.getMeasure(Color.red) != null)
            {
                currentPoint3D = samplesSet.getMeasure(Color.red).getValue();
                triangle.moveTo(currentPoint3D._a * scale, currentPoint3D._b * scale);
            }
            else
                drawTriangle = false;

            if (samplesSet.getMeasure(Color.green) != null)
            {
                currentPoint3D = samplesSet.getMeasure(Color.green).getValue();
                if (drawTriangle)
                    triangle.lineTo(currentPoint3D._a * scale, currentPoint3D._b * scale);
            }
            else
                drawTriangle = false;
            if (samplesSet.getMeasure(Color.blue) != null)
            {
                currentPoint3D = samplesSet.getMeasure(Color.blue).getValue();
                if (drawTriangle)
                    triangle.lineTo(currentPoint3D._a * scale, currentPoint3D._b * scale);
            }
            else
                drawTriangle = false;
            // // draw triangle
            if (drawTriangle)
            {
                g2.setColor(Color.darkGray);
                g2.fill(triangle);
            }
            //
            drawShape(g2, Color.yellow, _targetPoint._a, _targetPoint._b, scale, bigPointSize, EShapeType.CIRCLE);
            List<ColorMeasure> measures = samplesSet.getMeasures();
            for (ColorMeasure mes : measures)
            {
                Point3f point = mes.getValue();
                drawShape(g2, mes.getPatchColor(), point._a, point._b, scale, smallPointSize, EShapeType.RECTANGLE);
            }

            // /// red
            currentPoint = _primariesToDisplay.getRed().getxyCoordinates();
            drawShape(g2, Color.white, currentPoint._a, currentPoint._b, scale, middlePointSize, EShapeType.CIRCLE);
            // /// green
            currentPoint = _primariesToDisplay.getGreen().getxyCoordinates();
            drawShape(g2, Color.white, currentPoint._a, currentPoint._b, scale, middlePointSize, EShapeType.CIRCLE);
            // /// blue
            currentPoint = _primariesToDisplay.getBlue().getxyCoordinates();
            drawShape(g2, Color.white, currentPoint._a, currentPoint._b, scale, middlePointSize, EShapeType.CIRCLE);

            // targetPoint
            // drawShape(g2, Color.white, _measuredPoint._a, _measuredPoint._b, scale, smallPointSize,
            // EShapeType.RECTANGLE);
        }
    }
}
