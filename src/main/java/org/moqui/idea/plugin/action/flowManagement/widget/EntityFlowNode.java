package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.ui.components.JBLabel;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;

public class EntityFlowNode extends FlowNode {
    private final int offset;
    public EntityFlowNode(FlowNodeModel model) {
        super(model);

        //平行四边形，内角为60度
        offset = (int) Math.abs(model.getHeight()* Math.tan(15));

        setLayout(null);

        Pair<String,String> action = FlowNodeModelBuilder.decomposeEntityAction(model.getName());

//        JLabel labelType = FlowNodeBuilder.createTypeLabel(model.getType().getName());
//        labelType.setBounds(offset,0,model.getWidth()- 2* offset,model.getHeight()/2);
//        add(labelType);
        if(action.getKey()!= null) {
            JBLabel labelName =new JBLabel(action.getKey());
            labelName.setHorizontalAlignment(SwingConstants.CENTER);
            labelName.setVerticalAlignment(SwingConstants.BOTTOM);
            labelName.setBounds(offset,0,model.getWidth()- 2* offset,model.getHeight()/2);
            add(labelName);
        }
        if(action.getValue() != null) {

            JBLabel labelValue = new JBLabel(action.getValue());
            labelValue.setHorizontalAlignment(SwingConstants.CENTER);
            labelValue.setVerticalAlignment(SwingConstants.TOP);
            labelValue.setAllowAutoWrapping(true);
            labelValue.setToolTipText(action.getValue());

            labelValue.setBounds(offset, model.getHeight()/2, model.getWidth() - 2 * offset, model.getHeight()/2);
            add(labelValue);
        }
//        setBorder(BorderFactory.createLineBorder(Color.BLACK,2,true));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(1));

        g2d.drawLine(offset,1,model.getWidth(),1);
        g2d.drawLine(model.getWidth(),1,model.getWidth()-offset,model.getHeight()-1);
        g2d.drawLine(model.getWidth()-offset,model.getHeight()-1,0,model.getHeight()-1);
        g2d.drawLine(0,model.getHeight()-1,offset,1);
    }
}
