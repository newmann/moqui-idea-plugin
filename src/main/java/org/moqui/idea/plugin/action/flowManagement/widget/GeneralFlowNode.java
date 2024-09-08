package org.moqui.idea.plugin.action.flowManagement.widget;

import com.github.weisj.jsvg.S;
import com.intellij.ui.components.JBLabel;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GeneralFlowNode extends FlowNode {

    public GeneralFlowNode(FlowNodeModel model) {
        super(model);
        Pair<String,String> action = FlowNodeModelBuilder.decomposeEntityAction(model.getName());

        setLayout(null);
//        setBackground(Color.red);

//        JLabel typeLabel = FlowNodeBuilder.createTypeLabel(model.getType().getName());
        JBLabel actionLabel = new JBLabel(action.getKey() == null ? "" : action.getKey());
        actionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        actionLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        actionLabel.setBounds(0,0,model.getWidth(),model.getHeight()/2);
        add(actionLabel);
        JBLabel nameLabel = new JBLabel(action.getValue() == null ? "" : action.getValue());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setVerticalAlignment(SwingConstants.TOP);
        nameLabel.setBounds(0,model.getHeight()/2,model.getWidth(),model.getHeight()/2);
        add(nameLabel);
//        add(typeLabel,BorderLayout.NORTH);

        setBorder(BorderFactory.createLineBorder(getDefaultLineColor(),1,true));
    }
}
