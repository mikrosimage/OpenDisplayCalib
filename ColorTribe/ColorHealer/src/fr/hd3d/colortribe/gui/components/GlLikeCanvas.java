package fr.hd3d.colortribe.gui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

class GlLikeCanvas extends JPanel {

    private static final long serialVersionUID = -6097659150128153149L;
    enum EShapeType {
        RECTANGLE, CIRCLE, CURSOR, FILLED_RECTANGLE, FILLED_CIRCLE,BORDERED_FILLED_RECTANGLE
    };
    protected static int smallPointSize = 4;
    protected static int middlePointSize = 6;
    protected static int bigPointSize = 8;

    public GlLikeCanvas() {
      super();
            
    }
    
    void drawAxis(Graphics2D g2){
        //// place repere
        g2.translate(5, 5);
        ////draw axis
        g2.setColor(Color.gray);
        g2.drawLine(0, 0, 0, this.getSize().height*100);
        g2.setColor(Color.darkGray);
        g2.drawLine(0, 0, this.getSize().width*100, 0);
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
       
        //// place the repere
        g2.scale(1, -1);
        g2.translate(0, - this.getSize().height);
       
    }    


    public BufferedImage getBufferedImage()
    {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        printAll(g);
        return bufferedImage;
    }

    final void drawShape(Graphics2D g2, Color color, float x, float y, int scale, int pointSize, EShapeType shape) {
        drawShape(g2, color, x, y, scale, scale, pointSize, shape);
    }   
    
    
    final void drawShape(Graphics2D g2, Color color, float x, float y, int scaleX, int scaleY, int pointSize, EShapeType shape) {
        g2.setColor(color);
        switch (shape) {
        case RECTANGLE:
            g2.drawRect((int) (x * scaleX) - pointSize / 2, (int) (y * scaleY) - pointSize / 2, pointSize, pointSize);
            break;
        case FILLED_RECTANGLE:
            g2.fillRect((int) (x * scaleX) - pointSize / 2, (int) (y * scaleY) - pointSize / 2, pointSize, pointSize);
            break;
        case FILLED_CIRCLE:
            g2.fillOval((int) (x * scaleX) - pointSize / 2, (int) (y * scaleY) - pointSize / 2, pointSize, pointSize);
            break;
        case CIRCLE:
            g2.drawOval((int) (x * scaleX) - pointSize / 2, (int) (y * scaleY) - pointSize / 2, pointSize, pointSize);
            break;
        case CURSOR :
            g2.fillRect((int) (x * scaleX) - pointSize / 2, (int) (y * scaleY) - 1, pointSize, 2);
            break;
        case BORDERED_FILLED_RECTANGLE :
            drawShape(g2, color, x, y, scaleX, scaleY, pointSize, EShapeType.FILLED_RECTANGLE);
            drawShape(g2, Color.darkGray, x, y, scaleX, scaleY, pointSize, EShapeType.RECTANGLE);
            break;
        default:
            g2.drawOval((int) (x * scaleX) - pointSize / 2, (int) (y * scaleY) - pointSize / 2, pointSize, pointSize);
            break;
        }
    }
    
    void drawLine(Graphics2D g2, Color color, float x1, float y1, float x2, float y2, int scaleX, int scaleY){
        g2.setColor(color);
        g2.drawLine((int) (x1*scaleX), (int) (y1*scaleY), (int) (x2*scaleX), (int) (y2*scaleY));
    }
    
    void drawString(Graphics2D g2, Color color, float x, float y, String s){
        g2.setColor(color);       
        g2.drawString(s, x, y);       
        
    }
}
