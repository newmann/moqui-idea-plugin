package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.openapi.editor.colors.EditorColorsManager;
import icons.MoquiIcons;
import org.moqui.idea.plugin.util.MyDomUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Optional;

public abstract class FlowNode extends JPanel implements MouseListener {
    protected int ACTION_PANEL_HEIGHT = 40;
    protected JPanel actionPanel;
//    protected ArrayList<JButton> actionButtonList = new ArrayList<>();
    protected final FlowNodeModel model;
    public FlowNode(FlowNodeModel model) {
        this.model = model;
        if(model.getToolTips() != null && !model.getToolTips().isBlank()) {
            setToolTipText(model.getToolTips());
        }

        initActionPanel();
        createActionButton();
        addMouseListener(this);
    }
    public void createActionButton(){
        if(model instanceof ExpandableFlowNodeModel expandableFlowNodeModel) {
            JButton extendButton = new JButton(MoquiIcons.ServiceTag);

            extendButton.addActionListener(actionEvent -> {
                if (expandableFlowNodeModel.isExpanded()) {
                    expandableFlowNodeModel.closeContent();
                } else {
                    expandableFlowNodeModel.expandContent();
                }
            });
            extendButton.setBounds(1, 1, 32, 32);
            actionPanel.add(extendButton);
        }

    }
    private void initActionPanel(){
        actionPanel = new JPanel();
        actionPanel.setLayout(null);
        actionPanel.setOpaque(false);
    }
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(model.getWidth(),model.getHeight());
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    public static Border ofBorderBlackLine(){
      return BorderFactory.createLineBorder(getDefaultLineColor());
    }
    public static Border ofBorderDashLine(){
      return BorderFactory.createDashedBorder(getDefaultLineColor(),8,2);
    }
    public static Stroke ofBorderDashLineStroke(){
        float[] dashArray = {8.0f, 2.0f};
        return new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10,dashArray,0);
    }
    public static Color getDefaultLineColor(){
        return EditorColorsManager.getInstance().getGlobalScheme().getDefaultForeground();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        //条件双击鼠标转跳到源文件的功能
        if(model.getTargetPsiElement() == null) {return;}
        if(mouseEvent.getClickCount() == 2) {
            MyDomUtils.openFileForPsiElement(model.getTargetPsiElement());
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
        if(getExistingContainerPanel().isEmpty()) {
            actionPanel.setBounds(0,0,model.getWidth(),ACTION_PANEL_HEIGHT);
            add(actionPanel);
            setComponentZOrder(actionPanel, 0);

            SwingUtilities.updateComponentTreeUI(this);
        }
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
        Dimension dimension = FlowNode.this.getSize();
        if(mouseEvent.getX()<0 || mouseEvent.getY() < 0
                || mouseEvent.getX() >= dimension.getWidth()
                || mouseEvent.getY() >= dimension.getHeight()) {
            Optional<Component> actionPanelOpt = getExistingContainerPanel();
            if(actionPanelOpt.isPresent()) {
                remove(actionPanelOpt.get());
                SwingUtilities.updateComponentTreeUI(this);
            }

        }

//        if(getMousePosition() == null) {
//            Optional<Component> actionPanelOpt = getExistingContainerPanel();
//            if(actionPanelOpt.isPresent()) {
//                remove(actionPanelOpt.get());
//                SwingUtilities.updateComponentTreeUI(this);
//            }
//        }
    }
    private Optional<Component> getExistingContainerPanel() {
        return  Arrays.stream(this.getComponents()).filter(item ->item.equals(actionPanel)).findFirst();
    }
}
