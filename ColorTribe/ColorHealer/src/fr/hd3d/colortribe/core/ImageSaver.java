package fr.hd3d.colortribe.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


/**
 * Ext can be jpg, bmp (j2se 1.5+), j2se 1.4+ png, j2se 1.6+ gif
 * 
 * @author mfe
 * 
 */

public class ImageSaver
{
    public static Image createImageFromJPanel(JPanel panel)
    {
        panel.validate();// orders the component of your panel even when not visible
        Image img = panel.createImage(panel.getWidth(), panel.getHeight());
        Graphics imgG = img.getGraphics();
        panel.paint(imgG);
        return img;
    }

    public static void save(BufferedImage image, String path, String ext)
    {
        File file = new File(path + "." + ext);
        try
        {
            ImageIO.write(image, ext, file); // ignore returned boolean
        }
        catch (IOException e)
        {
            System.out.println("Write error for " + file.getPath() + ": " + e.getMessage());
        }
    }

    public static BufferedImage toBufferedImage(Image src)
    {
        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int type = BufferedImage.TYPE_INT_RGB; // other options
        BufferedImage dest = new BufferedImage(w, h, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dest;
    }
}
