package fr.hd3d.colortribe.gui.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import fr.hd3d.colortribe.gui.HealerMainWindow;
import fr.hd3d.colortribe.gui.JHealerColors;


public class JHealerMenu extends JPanel
{

    /**
     * 
     * 
     */
    private static final long serialVersionUID = -378482601802610586L;

    private int _dragX, _dragY;
    private int _oldPosX, _oldPosY;
    private HealerMainWindow _parent;
    private JHealerTree _tree;

    public JHealerMenu(HealerMainWindow parent)
    {
        _parent = parent;
        _tree = new JHealerTree(this);

        setSize(219, 566);
        setPreferredSize(new Dimension(219, 566));
        setLayout(new BorderLayout());
        setBackground(JHealerColors.BACKGROUNG_COLOR);
        JImageCanvas logo = new JImageCanvas("img/CTCH_logo.png", 219, 76);

        logo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                _dragX = e.getX() + _parent.getLocation().x;
                _dragY = e.getY() + _parent.getLocation().y;
                _oldPosX = _parent.getLocation().x;
                _oldPosY = _parent.getLocation().y;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        logo.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e)
            {
                int deltaX = (e.getX() + _parent.getLocation().x) - _dragX;
                int deltaY = (e.getY() + _parent.getLocation().y) - _dragY;
                _parent.setLocation(_oldPosX + deltaX, _oldPosY + deltaY);
            }

            public void mouseMoved(MouseEvent e)
            {}
        });

        add(logo, BorderLayout.NORTH);
        GridLayout gridLayout = new GridLayout(1, 1);

        JPanel treePanel = new JPanel(gridLayout);
        treePanel.setPreferredSize(new Dimension(190, 560));
        treePanel.add(_tree);
        _tree.setAlignmentX(Component.LEFT_ALIGNMENT);
        _tree.setAlignmentY(TOP_ALIGNMENT);

        add(treePanel, BorderLayout.CENTER);

    }

    public void rePaintStep()
    {
        _parent.rePaintStep();
    }

}
