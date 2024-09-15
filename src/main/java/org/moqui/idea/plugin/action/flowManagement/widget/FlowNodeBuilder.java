package org.moqui.idea.plugin.action.flowManagement.widget;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FlowNodeBuilder {

    public static JPanel createNode(FlowNodeModel model){
        switch (model.getType()) {
            case IF -> {return new IfFlowNode((IfFlowNodeModel) model);}
            case LOOP ->  {return new LoopFlowNode((LoopFlowNodeModel) model);}
            case SCENE -> {return new SceneFlowNode((SceneFlowNodeModel) model);}
            case CONDITION -> {return new ConditionFlowNode(model);}
            case COLLECT -> {return new CollectFlowNode(model);}
            case SERVICE_CALL -> {return new ServiceCallFlowNode((ServiceCallFlowNodeModel) model);}
            case ENTITY_ACTION ->{return new EntityFlowNode(model);}
            case START,END -> {return new StartEndFlowNode(model);}
            default -> {return new GeneralFlowNode(model);}
        }
    }
    public static void setBound(JPanel node, FlowNodeModel model){
        node.setBounds(model.getTopLeftPoint().x,model.getTopLeftPoint().y,model.getWidth(),model.getHeight());
    }
    public static JLabel createTypeLabel(String type){
        JLabel label = new JLabel(type);
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK,1,true);
//        Border emptyBorder = BorderFactory.createEmptyBorder(0,0,5,0);
//        Border bottomBorder = new CompoundBorder(lineBorder,emptyBorder);
        label.setBorder(lineBorder);
        return label;
    };
    public static JLabel createNameLabel(String name){
        JLabel label = new JLabel(name);
//        label.setOpaque(true);
//        label.setBackground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
//        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK,1,true);
//        Border emptyBorder = BorderFactory.createEmptyBorder(0,0,5,0);
//        Border bottomBorder = new CompoundBorder(lineBorder,emptyBorder);
//        label.setBorder(lineBorder);

        return label;
    };
    public static JLabel createNameLabel(String name,String tooltips){
        JLabel label = new JLabel(name);
//        label.setOpaque(true);
//        label.setBackground(Color.GRAY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setToolTipText(tooltips);
//        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK,1,true);
//        Border emptyBorder = BorderFactory.createEmptyBorder(0,0,5,0);
//        Border bottomBorder = new CompoundBorder(lineBorder,emptyBorder);
//        label.setBorder(lineBorder);

        return label;
    };
    public static SceneFlowNode createSceneFlowNode(SceneFlowNodeModel model){
        SceneFlowNode sceneFlowNode = new SceneFlowNode(model);
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals(FlowNodeModel.PROPERTY_NAME_LAYOUT_CHANGE)) {
//                    if(propertyChangeEvent.getSource() instanceof FlowNodeModel flowNodeModel) {
//                        System.out.println("createSceneFlowNode->"+flowNodeModel.getName());
//                    }

                    sceneFlowNode.removeAll();
                    sceneFlowNode.processLayout();
                    SwingUtilities.updateComponentTreeUI(sceneFlowNode);
                }
            }
        });
        return sceneFlowNode;
    }
}
