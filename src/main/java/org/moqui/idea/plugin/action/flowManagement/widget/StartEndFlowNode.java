package org.moqui.idea.plugin.action.flowManagement.widget;

import javax.swing.*;
import java.awt.*;

public class StartEndFlowNode extends FlowNode {

    public StartEndFlowNode(FlowNodeModel model) {
        super(model);


        setLayout(null);

        JLabel labelName = FlowNodeBuilder.createNameLabel(model.getName());
        labelName.setBounds(model.getHeight()/2,0,model.getWidth()- model.getHeight(),model.getHeight());
        add(labelName);

//        setBorder(BorderFactory.createLineBorder(Color.BLACK,2,true));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        g2d.setStroke(new BasicStroke(1));
        //边框需要留出1个点的宽度，否则画不出线
        g2d.drawLine(model.getHeight()/2,1,model.getWidth()-model.getHeight()/2,1);
        g2d.drawLine(model.getHeight()/2,model.getHeight()-1,model.getWidth()-model.getHeight()/2,model.getHeight()-1);
        g2d.drawArc(0,1,model.getHeight()-2,model.getHeight()-2,90,180);
        g2d.drawArc(model.getWidth()-model.getHeight()-2,1,model.getHeight()-2,model.getHeight()-2,270,180);
    }
}
