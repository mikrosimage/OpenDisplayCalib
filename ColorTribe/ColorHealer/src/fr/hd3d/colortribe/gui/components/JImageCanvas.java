package fr.hd3d.colortribe.gui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;


public class JImageCanvas extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 5983550779091115411L;
    Image _image;

    public JImageCanvas(String url, int width, int height)
    {
        this.setPreferredSize(new Dimension(width, height));

        this.setSize(width, height);
        this.setPreferredSize(new Dimension(width, height));
        _image = getToolkit().getImage(url);
        prepareImage(_image, this);
    }

    public void setImage(String url)
    {
        _image = getToolkit().getImage(url);
        prepareImage(_image, this);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(_image, 0, 0, this);
    }

}
