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
import fr.hd3d.colortribe.color.util.ColorMath;
import fr.hd3d.colortribe.color.util.ColorSpectrumTables;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;


public class JCIE76PrimariesCanvas extends GlLikeCanvas
{
    private static final long serialVersionUID = -6097659150128153149L;
    private IRgbPrimary _primariesToDisplay;
    private Point2f _targetPoint;
    private float _userScale = 1;
    
    public JCIE76PrimariesCanvas(EStandardRgbPrimaries primariesToDisplay){
        this(primariesToDisplay,  300,300);
    }

    public JCIE76PrimariesCanvas(EStandardRgbPrimaries primariesToDisplay, int width, int height)
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

    public void setPrimariesToDisplay(IRgbPrimary primariesToDisplay)
    {
        _primariesToDisplay = primariesToDisplay;
    }

    public void setTargetPoint(float x, float y)
    {
        Point2f uv = ColorMath.xyToupvp(x, y);
        _targetPoint._a = uv._a;
        _targetPoint._b = uv._b;
    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawAxis(g2);
        Point2f currentPoint;
        int scale = (int)(getWidth()*1.55f);
        MeasuresSet samplesSet = ColorHealerModel._instance.getBasicMeasuresSet();

        if (_userScale == 1)
        {
            // spectrum locus
            GeneralPath gp = new GeneralPath();

            int i = 0;
            while (i < ColorSpectrumTables.spectrum_x.length)
            {
                Point2f uv = ColorMath.xyToupvp(ColorSpectrumTables.spectrum_x[i], ColorSpectrumTables.spectrum_y[i]);
                if (i == 0)
                    gp.moveTo(uv._a * scale, uv._b * scale);
                gp.lineTo(uv._a * scale, uv._b * scale);
                ++i;
            }
            gp.closePath();
            g2.setColor(Color.darkGray);
            g2.draw(gp);

          
            // //////////////// RVB measured
            GeneralPath triangle = new GeneralPath();
            Point3f currentPoint3D;
            boolean drawTriangle = true;
            if (samplesSet.getMeasure(Color.red) != null)
            {
                currentPoint3D = samplesSet.getMeasure(Color.red).getValue();
                Point2f uv = ColorMath.xyToupvp(currentPoint3D._a, currentPoint3D._b);
                triangle.moveTo(uv._a * scale, uv._b * scale);
            }
            else
                drawTriangle = false;

            if (samplesSet.getMeasure(Color.green) != null)
            {
                currentPoint3D = samplesSet.getMeasure(Color.green).getValue();
                Point2f uv = ColorMath.xyToupvp(currentPoint3D._a, currentPoint3D._b);
                if (drawTriangle)
                    triangle.lineTo(uv._a * scale, uv._b * scale);
            }
            else
                drawTriangle = false;
            if (samplesSet.getMeasure(Color.blue) != null)
            {
                currentPoint3D = samplesSet.getMeasure(Color.blue).getValue();
                Point2f uv = ColorMath.xyToupvp(currentPoint3D._a, currentPoint3D._b);
                if (drawTriangle)
                    triangle.lineTo(uv._a * scale, uv._b * scale);
            }
            else
                drawTriangle = false;
            // // draw triangle
            if (drawTriangle)
            {
                g2.setColor(Color.darkGray);
                g2.fill(triangle);
            }

            // /// red
            currentPoint = _primariesToDisplay.getRed().getuvCoordinates();
            drawShape(g2, Color.white, currentPoint._a, currentPoint._b, scale, middlePointSize, EShapeType.CIRCLE);
            // /// green
            currentPoint = _primariesToDisplay.getGreen().getuvCoordinates();
            drawShape(g2, Color.white, currentPoint._a, currentPoint._b, scale, middlePointSize, EShapeType.CIRCLE);
            // /// blue
            currentPoint = _primariesToDisplay.getBlue().getuvCoordinates();
            drawShape(g2, Color.white, currentPoint._a, currentPoint._b, scale, middlePointSize, EShapeType.CIRCLE);
            drawShape(g2, Color.yellow, _targetPoint._a, _targetPoint._b, scale, bigPointSize, EShapeType.CIRCLE);
            List<ColorMeasure> measures = samplesSet.getMeasures();
            for (ColorMeasure mes : measures)
            {
                Point3f point = mes.getValue();
                Point2f uv = ColorMath.xyToupvp(point._a, point._b);
                drawShape(g2, mes.getPatchColor(), uv._a, uv._b, scale, smallPointSize, EShapeType.RECTANGLE);
            }
        }
    }
}
