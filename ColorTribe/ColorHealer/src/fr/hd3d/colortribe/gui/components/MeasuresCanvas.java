package fr.hd3d.colortribe.gui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import fr.hd3d.colortribe.color.type.Point2f;
import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.MeasuresSet;
import fr.hd3d.colortribe.core.MeasuresSet.ETableType;


/**
 * Display color values during the color patchs measure
 * 
 * @author mfe
 * 
 */
public class MeasuresCanvas extends GlLikeCanvas {
    private static final long serialVersionUID = 7909048123494155203L;
    private boolean _displaySamplesRed = false;
    private boolean _displayPointsRed = true;
    private boolean _displayLineRed = true;
    private boolean _displaySamplesGreen = false;
    private boolean _displayPointsGreen = true;
    private boolean _displayLineGreen = true;
    private boolean _displaySamplesBlue = false;
    private boolean _displayPointsBlue = true;
    private boolean _displayLineBlue = true;
    private boolean _enableScale = true;
    private float _ymax = 0.0f;
    private final static int margin = 10;

    public MeasuresCanvas() {
        super();
        setBackground(Color.black);
        setSize(500, 300);
        setPreferredSize(new Dimension(500, 300));
    }

    public void setDisplaySample(boolean state) {
        _displaySamplesRed = state;
    }

    public void setDisplayPoints(boolean state) {
        _displayPointsRed = state;
    }

    public void setDisplayLine(boolean state) {
        _displayLineRed = state;
    }

    public void setDisplayGreenSample(boolean state) {
        _displaySamplesGreen = state;
    }

    public void setDisplayGreenPoints(boolean state) {
        _displayPointsGreen = state;
    }

    public void setDisplayGreenLine(boolean state) {
        _displayLineGreen = state;
    }

    public void setDisplayBlueSample(boolean state) {
        _displaySamplesBlue = state;
    }
                                                
    public void setDisplayBluePoints(boolean state) { 
        _displayPointsBlue = state;
    }

    public void setDisplayBlueLine(boolean state) {
        _displayLineBlue = state;
    }

    public void setScaleEnable(boolean state) {
        _enableScale = state;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        MeasuresSet set = ColorHealerModel._instance.getCurrentMeasuresSet();
        int scaleX = getWidth() - margin; 
        float diviseur;
        if (!_enableScale)
            diviseur = 1.5f;
        else if (_ymax < 1)
            diviseur = 1;
        else
            diviseur = _ymax;
        int scaleY = (int) ((getHeight() - margin) / diviseur); 
        drawAxis(g2);
        drawLine(g2, Color.lightGray, 1, 0, 1, 1, scaleX, getHeight());
        for (int i = 0; i < 3; i++) {
            List<Point2f> points;
            List<Point2f> samples = new ArrayList<Point2f>();
            Color color;
            boolean displaySamples, displayPoints, displayLine;
            if (i == 0) {
                points = set.computeSortedNormalizedDifferentialsPoints(ETableType.RED, samples);
                color = Color.red;
                displaySamples = _displaySamplesRed;
                displayPoints = _displayPointsRed;
                displayLine = _displayLineRed;
            } else if (i == 1) {
                points = set.computeSortedNormalizedDifferentialsPoints(ETableType.GREEN, samples);
                color = Color.green;
                displaySamples = _displaySamplesGreen;
                displayPoints = _displayPointsGreen;
                displayLine = _displayLineGreen;
            } else {
                points = set.computeSortedNormalizedDifferentialsPoints(ETableType.BLUE, samples);
                color = Color.blue;
                displaySamples = _displaySamplesBlue;
                displayPoints = _displayPointsBlue;
                displayLine = _displayLineBlue;
            }
            if (displaySamples)
                for (Point2f sample : samples) {
                    drawShape(g2, color.darker(), sample._a, sample._b, scaleX, scaleY, 1, EShapeType.FILLED_RECTANGLE);
                }
            // / x -> pixel value, y -> mesured lum / expected lum
            // // display curve
            if (displayLine) {
                Point2f previousPoint = null;
                for (Point2f point : points) {
                    if (previousPoint != null) {
                        drawLine(g2, color, point._a, point._b, previousPoint._a, previousPoint._b, scaleX, scaleY);
                    }
                    previousPoint = point;
                }
            }
            // //display points
            if (displayPoints)
                for (Point2f point : points) {
                    Color pointColor = new Color((int) (point._a * color.getRed()), (int) (point._a * color.getGreen()), (int) (point._a * color
                            .getBlue()));
                    drawShape(g2, pointColor, point._a, point._b, scaleX, scaleY, 4, EShapeType.BORDERED_FILLED_RECTANGLE);
                }
        }

    }

    

}
