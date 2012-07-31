package fr.hd3d.colortribe.gui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import fr.hd3d.colortribe.core.ColorHealerModel;
import fr.hd3d.colortribe.core.protocols.AbstractProtocol.ProtocolEvent;
import fr.hd3d.colortribe.core.protocols.AbstractProtocol.ProtocolListener;
import fr.hd3d.colortribe.gui.JHealerColors;
import fr.hd3d.colortribe.gui.steps.Step;
import fr.hd3d.colortribe.gui.steps.Step.StepStatus;


public class JHealerTree extends JPanel implements ProtocolListener
{

    /**
     * 
     */

    private int _w = 185;
    private int _h = 566;
    private int _xOffset;
    private int _offset;
    private int _gap = 3;
    JTree _tree;
    JScrollPane _pane;
    JHealerMenu _parent;
    MultiLinesRenderer _cellRenderer;
    Map<String, Integer> _stepToRow;

    private static final long serialVersionUID = -561368673415100610L;

    static private Icon okIcon = new ImageIcon("img/node_ok.png");
    static private Icon failedIcon = new ImageIcon("img/node_failed.png");
    static private Icon disableIcon = new ImageIcon("img/node_disable.png");
    static private Icon enableIcon = new ImageIcon("img/node_enable.png");

    public JHealerTree(JHealerMenu parent)
    {
        _parent = parent;
        LinkedHashMap<String, Step> steps = ColorHealerModel._instance.getProtocol().getSteps();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        DisabledNode child;

        DisabledNode grandChild;

        Collection<Step> coll = steps.values();
        _stepToRow = new HashMap<String, Integer>();
        int i = 0;
        for (Step step : coll)
        {
            child = new DisabledNode(step.getName(), null);
            root.add(child);
            grandChild = new DisabledNode(step.getHTMLDescription(), child);
            _stepToRow.put(step.getName(), i);
            grandChild.setAllowsChildren(false);
            grandChild.setEnabled(false);
            if (step.isEnabled())
            {
                child.setEnabled(true);
            }
            else
                child.setEnabled(false);
            child.add(grandChild);
            i++;

        }

        UIManager.put("Tree.line", JHealerColors.STEP_BACKGROUNG_COLOR);
        _tree = new JTree(root);
        _tree.setRowHeight(-1);
        _tree.setRootVisible(false);
        _tree.setEditable(false);
        _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        _tree.putClientProperty("JTree.lineStyle", "Horizontal");
        _tree.setSelectionRow(0);

        BasicTreeUI basicTreeUI = (BasicTreeUI) _tree.getUI();
        basicTreeUI.setRightChildIndent(8);
        basicTreeUI.setLeftChildIndent(5);

        _cellRenderer = new MultiLinesRenderer();

        _tree.setCellRenderer(_cellRenderer);

        Rectangle rectangle = _tree.getPathBounds(_tree.getPathForRow(2));
        _xOffset = 2 * rectangle.x;

        setVisible(true);
        _pane = new JScrollPane(_tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        _pane.setPreferredSize(new Dimension(_w, _h));
        _pane.setBorder(null);

        add(_pane);

        setSize(_w, _h);
        // setVisible(true);
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e)
            {
                Dimension d = getSize();
                _w = d.width;
                _h = d.height;
                _offset = _xOffset + _pane.getVerticalScrollBar().getSize().width + _gap;
                boolean rootVisible = _tree.isRootVisible();
                _tree.setRootVisible(!rootVisible);
                _tree.setRootVisible(rootVisible);
            }
        });

        _tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getLastSelectedPathComponent();

                if (node == null)
                    return;
                if (node.isLeaf())
                    return;
                if (!(node instanceof DisabledNode))
                    return;

                DisabledNode dNode = (DisabledNode) node;
                if (dNode.isEnabled())
                {
                    Object nodeInfo = node.getUserObject();

                    ColorHealerModel._instance.getProtocol().setSelectedStep((String) nodeInfo.toString());
                    _cellRenderer.setSelectedRow(_tree.getRowForPath(_tree.getSelectionPath()));
                    _parent.rePaintStep();

                }
                else
                {
                    _tree.setSelectionRow(_cellRenderer.getSelectedRow());
                }
            }

        });
        ColorHealerModel model = ColorHealerModel._instance;
        model.getProtocol().addProtocolListener(this);
    }

    private class DisabledNode extends DefaultMutableTreeNode
    {
        /**
         * 
         */
        private static final long serialVersionUID = -5315773670676234113L;
        protected boolean enabled;

        private DisabledNode _parent = null;

        public DisabledNode(Object userObject, DisabledNode parent)
        {
            this(userObject, true, true, parent);
        }

        public DisabledNode(Object userObject, boolean allowsChildren, boolean enabled, DisabledNode parent)
        {
            super(userObject, allowsChildren);
            this.enabled = enabled;
            _parent = parent;
        }

        public int getChildCount()
        {
            if (enabled)
            {
                return super.getChildCount();
            }
            else
            {
                return 0;
            }
        }

        public boolean isLeaf()
        {
            return (super.getChildCount() == 0);
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public String getParentName()
        {
            if (_parent == null)
                return "root";
            else
                return _parent.toString();
        }

        public DisabledNode getDisableNodeParent()
        {
            return _parent;
        }

    }

    private class MultiLinesRenderer extends DefaultTreeCellRenderer
    {
        /**
         * 
         */
        private static final long serialVersionUID = 4120536714270270671L;
        private JEditorPane _pane = new JEditorPane();
        private int _row;
        private int _selectedRow;
        private boolean _isLeaf;
        private boolean _isExtended;
        private boolean _isEnable;

        MultiLinesRenderer()
        {
            _pane.setContentType("text/html");
            setLeafIcon(null);
            setOpenIcon(disableIcon);
            setClosedIcon(disableIcon);
            setDisabledIcon(disableIcon);
            setTextNonSelectionColor(JHealerColors.TEXT_COLOR);
            setTextSelectionColor(JHealerColors.TEXT_COLOR);
            setBackgroundNonSelectionColor(JHealerColors.BACKGROUNG_COLOR);
            setBackgroundSelectionColor(JHealerColors.BACKGROUNG_COLOR);
            setBorderSelectionColor(JHealerColors.BACKGROUNG_COLOR);

        }

        public void setStatusIcon(StepStatus status)
        {
            switch (status)
            {
                case UNKNOWN:
                case DISABLE:
                    setOpenIcon(disableIcon);
                    setClosedIcon(disableIcon);
                    setDisabledIcon(disableIcon);
                    setLeafIcon(null);
                    break;
                case FAILED:
                    setOpenIcon(failedIcon);
                    setClosedIcon(failedIcon);
                    setDisabledIcon(failedIcon);
                    setLeafIcon(null);
                    break;
                case OK:
                    setOpenIcon(okIcon);
                    setClosedIcon(okIcon);
                    setDisabledIcon(okIcon);
                    setLeafIcon(null);
                    break;
                case NOT_COMPLETE:
                    setOpenIcon(enableIcon);
                    setClosedIcon(enableIcon);
                    setDisabledIcon(enableIcon);
                    setLeafIcon(null);
                    break;
                default:
                    break;
            }
        }

        public void enableNode(StepStatus status, DisabledNode node)
        {
            switch (status)
            {
                case DISABLE:
                    node.setEnabled(false);
                    break;
                case FAILED:
                case OK:
                case NOT_COMPLETE:
                    node.setEnabled(true);
                    break;
                case UNKNOWN:
                default:
                    break;
            }
        }

        public Component getTreeCellRendererComponent(javax.swing.JTree tree, Object value, boolean sel,
                boolean isExtended, boolean isLeaf, int row, boolean hasFocus)
        {
            setText((String) value.toString());
            _row = row;
            _isLeaf = isLeaf;
            _isExtended = isExtended;
            StepStatus status = ColorHealerModel._instance.getProtocol().getStepStatus(getText());
            setStatusIcon(status);

            _pane.setEditable(false);
            _isEnable = true;

            if (isLeaf)
            {

                setFont(new Font("norm", Font.PLAIN, 11));
            }
            else
                setFont(new Font("norm", Font.BOLD, 12));
            if (_row != _selectedRow)
            {
                setTextNonSelectionColor(JHealerColors.TEXT_COLOR.darker());
                setTextSelectionColor(JHealerColors.TEXT_COLOR.darker());
            }
            else
            {
                setTextNonSelectionColor(JHealerColors.SELECTION_TEXT_COLOR);
                setTextSelectionColor(JHealerColors.SELECTION_TEXT_COLOR);
            }

            if (value instanceof DisabledNode)
            {
                DisabledNode currentNode = (DisabledNode) value;
                enableNode(status, currentNode);
                boolean treeIsEnabled = tree.isEnabled();
                boolean nodeIsEnabled = currentNode.isEnabled();
                boolean isEnabled = (treeIsEnabled && nodeIsEnabled);
                setEnabled(isEnabled);
                if (!isEnabled)
                {
                    selected = false;
                    sel = selected;
                    _isEnable = false;
                }

                if (isLeaf && currentNode.getDisableNodeParent().isEnabled())
                {
                    currentNode.setUserObject(ColorHealerModel._instance.getProtocol().getStepHTMLDescription(
                            currentNode.getParentName()));
                }
            }

            return super.getTreeCellRendererComponent(tree, value, sel, isExtended, isLeaf, row, hasFocus);
        }

        public Dimension getPreferredSize()
        {
            _pane.setText(getText());
            _pane.setSize(_w - _offset, Integer.MAX_VALUE);
            Dimension d = _pane.getPreferredScrollableViewportSize();
            return new Dimension(_w - _offset, d.height);
        }

        protected void paintComponent(Graphics g)
        {

            super.paintComponent(g);
            if (!_isLeaf)
            {
                g.setColor(JHealerColors.TEXT_COLOR);
                if (_isEnable)
                {
                    if (!_isExtended)
                    {
                        g.setFont(new Font("norm", Font.PLAIN, 12));
                        g.drawString("+", getWidth() - 40, getHeight() / 2 + 5);
                    }
                    else
                    {
                        g.setFont(new Font("norm", Font.PLAIN, 14));
                        g.drawString("-", getWidth() - 38, getHeight() / 2 + 5);
                    }
                }
            }
        }

        public void setSelectedRow(int row)
        {
            _selectedRow = row;
        }

        public int getSelectedRow()
        {
            return _selectedRow;
        }

    }

    public void measuresAbortion(ProtocolEvent event)
    {
    // TODO Auto-generated method stub

    }

    public void newMeasure(ProtocolEvent event)
    {
    // TODO Auto-generated method stub

    }

    public void setStepAsked(ProtocolEvent event)
    {
        Step currStep = ColorHealerModel._instance.getProtocol().getSelectedStep();
        int row = _stepToRow.get(currStep.getName());
        _cellRenderer.setSelectedRow(row);
        _tree.setSelectionRow(row);
        _parent.rePaintStep();

    }

}
