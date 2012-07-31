package fr.hd3d.colortribe.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import fr.hd3d.colortribe.gui.steps.Step;


public class CustomTabbedPane extends JTabbedPane
{

    /**
     * 
     */
    private static final long serialVersionUID = -1945949580102530962L;

    public CustomTabbedPane(Step parent)
    {
        super();
        setUI(new CustomTabbedPaneUI(parent));
    }

     private class CustomTabbedPaneUI extends BasicTabbedPaneUI
    {
        private final Insets NO_INSETS = new Insets(0, 0, 0, 0);

        private int _buttonHeight = 12;
        private Color _tabColor = null;
        private Color _background = null;
        private Color _backgroundDarker1 = null;
        private Color _backgroundDarker2 = null;
        private int _leftInset = 2;
        private Step _parent = null;

        public CustomTabbedPaneUI(Step parent)
        {
            _parent = parent;
        }

        // public static ComponentUI createUI(JComponent c)
        // {
        // return new CustomTabbedPaneUI();
        // }

        protected void installComponents()
        {
            super.installComponents();

            _background = JHealerColors.BACKGROUNG_COLOR;
            _backgroundDarker1 = _background.darker();
            _backgroundDarker2 = _backgroundDarker1.darker();
            _tabColor = _backgroundDarker1;
        }

        protected void installDefaults()
        {
            super.installDefaults();
            tabAreaInsets.left = _leftInset;
            selectedTabPadInsets = new Insets(0, 0, 0, 0);
        }

        public int getTabRunCount(JTabbedPane pane)
        {
            return 1;
        }

        protected Insets getContentBorderInsets(int tabPlacement)
        {
            return NO_INSETS;
        }

        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight)
        {
            if (tabPlacement == tabIndex)
            {
                return _buttonHeight;
            }
            else
            {
                return _buttonHeight + (_buttonHeight / 2) + 6;
            }
        }

        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics)
        {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + _buttonHeight;
        }

        protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex)
        {
            int tw = tabPane.getBounds().width;

            g.setColor(_tabColor);
            g.fillRect(0, 0, tw, _buttonHeight);
            g.draw3DRect(0, 0, _leftInset - 1, _buttonHeight, true);

            int x = rects[rects.length - 1].x + rects[rects.length - 1].width;
            g.draw3DRect(x, 0, tw - x - 1, _buttonHeight, true);

            g.setColor(Color.black);
            g.drawLine(0, _buttonHeight + 1, tw - 1, _buttonHeight + 1);

            super.paintTabArea(g, tabPlacement, selectedIndex);
        }

        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int tx, int ty, int tw, int th,
                boolean isSelected)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.translate(tx, 0);

            if (isSelected)
            {
                int[] x = new int[3];
                int[] y = new int[3];

                g.setColor(_tabColor);

                g.fillRect(0, 0, tw, _buttonHeight);
                g.draw3DRect(0, 0, tw - 1, _buttonHeight, true);
                g.fillRect(_buttonHeight / 2, _buttonHeight, tw - _buttonHeight, _buttonHeight / 2 + 1);

                // Left Polygon
                x[0] = 0;
                y[0] = _buttonHeight;
                x[1] = _buttonHeight / 2;
                y[1] = _buttonHeight + (_buttonHeight / 2);
                x[2] = _buttonHeight / 2;
                y[2] = _buttonHeight;
                g.fillPolygon(x, y, 3);

                // Right Polygon
                x[0] = tw;
                y[0] = _buttonHeight;
                x[1] = tw - _buttonHeight / 2;
                y[1] = _buttonHeight + (_buttonHeight / 2);
                x[2] = tw - _buttonHeight / 2;
                y[2] = _buttonHeight;
                g.fillPolygon(x, y, 3);

                g.setColor(_backgroundDarker1);
                g.drawLine(0, _buttonHeight, _buttonHeight / 2, _buttonHeight + (_buttonHeight / 2));

                g.setColor(_backgroundDarker2);
                g.drawLine(0, _buttonHeight + 1, _buttonHeight / 2, _buttonHeight + (_buttonHeight / 2) + 1);
                g.drawLine(_buttonHeight / 2, _buttonHeight + (_buttonHeight / 2) + 1, tw - _buttonHeight / 2,
                        _buttonHeight + (_buttonHeight / 2) + 1);
                g.drawLine(tw - _buttonHeight / 2, _buttonHeight + (_buttonHeight / 2), tw, _buttonHeight);

                g.setColor(Color.black);
                g.drawLine(_buttonHeight / 2 + 1, _buttonHeight + (_buttonHeight / 2) + 2, tw - _buttonHeight / 2 - 1,
                        _buttonHeight + (_buttonHeight / 2) + 2);
                g.drawLine(tw - _buttonHeight / 2 - 1, _buttonHeight + (_buttonHeight / 2) + 2, tw, _buttonHeight + 1);

            }
            else
            {
                g.setColor(_tabColor);

                g.fillRect(0, 0, tw, _buttonHeight);
                g.draw3DRect(0, 0, tw - 1, _buttonHeight, true);

                g.setColor(Color.black);
                g.drawLine(0, _buttonHeight + 1, tw - 1, _buttonHeight + 1);

            }

            g2d.translate(-1 * tx, 0);
        }

        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex,
                String title, Rectangle textRect, boolean isSelected)
        {
            Rectangle r = rects[tabIndex];

            Graphics2D g2d = (Graphics2D) g;
            g2d.translate(r.x, 0);

            if (isSelected)
            {
                FontMetrics fm = getFontMetrics();
                switch (_parent.getStatus())
                {
                    case OK:
                        g.setColor(Color.GREEN);
                        break;
                    case FAILED:
                        g.setColor(Color.RED);
                        break;
                    case NOT_COMPLETE:
                        g.setColor(Color.ORANGE);
                        break;
                    case DISABLE:
                        g.setColor(JHealerColors.SELECTION_TEXT_COLOR);
                        break;

                    default:
                        break;
                }
                
                g.drawString(title, (r.width / 2 - fm.stringWidth(title) / 2) + 1, _buttonHeight / 2
                        + fm.getMaxDescent() + _buttonHeight / 2 + 3);

            }
            else
            {
                FontMetrics fm = getFontMetrics();
                
                switch (_parent.getStatus())
                {
                    case OK:
                        g.setColor(Color.GREEN.darker().darker());
                        break;
                    case FAILED:
                        g.setColor(Color.RED.darker().darker());
                        break;
                    case NOT_COMPLETE:
                        g.setColor(Color.ORANGE.darker().darker());
                        break;
                    case DISABLE:
                        g.setColor(JHealerColors.TEXT_COLOR);
                        break;

                    default:
                        break;
                }
                
                g.drawString(title, (r.width / 2 - fm.stringWidth(title) / 2) + 1, _buttonHeight / 2
                        + fm.getMaxDescent() + 2);
            }

            g2d.translate(-1 * r.x, 0);
        }

        // ------------------------------------------------------------------------------------------------------------------
        // Methods that we want to suppress the behaviour of the superclass
        // ------------------------------------------------------------------------------------------------------------------

        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
                boolean isSelected)
        {
        // Do nothing
        }

        protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
                Rectangle iconRect, Rectangle textRect, boolean isSelected)
        {
        // Do nothing
        }

        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex)
        {
        // Do nothing
        }
    }
}
