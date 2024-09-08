package org.moqui.idea.plugin.action.flowManagement.widget;

import javax.swing.*;
import java.awt.*;

public class ConditionFlowNode extends FlowNode {
    private final FlowLineModel lineModel;
    public ConditionFlowNode(FlowNodeModel model) {
        super(model);
        setLayout(null);
        this.lineModel = DiamondLineModel.of(model.getWidth(),model.getHeight(),model.getName());
        if(model.getName()!=null) {
            JLabel label = new JLabel(model.getName());
            label.setBounds(model.getWidth()/8,model.getHeight()/4,model.getWidth()*3/4,model.getHeight()/2);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setVerticalAlignment(JLabel.CENTER);

            add(label);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.lineModel.drawLine(g);
    }

}
