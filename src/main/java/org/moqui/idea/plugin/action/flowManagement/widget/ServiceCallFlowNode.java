package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.ui.components.JBPanel;
import icons.MoquiIcons;
import org.moqui.idea.plugin.util.ServiceCallDescriptor;
import org.moqui.idea.plugin.util.ServiceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Optional;

public class ServiceCallFlowNode extends FlowNode  {
    private final ServiceCallFlowNodeModel serviceCallFlowNodeModel;
    public static final int LINE_SPACE = 10;
    private JLabel nameLabel = null;
    private JPanel contentPanel = null;
    public ServiceCallFlowNode(ServiceCallFlowNodeModel model) {
        super(model);
        this.serviceCallFlowNodeModel = model;
        setLayout(null);
        if(serviceCallFlowNodeModel.isExpanded()) {
            nameLabel = FlowNodeBuilder.createNameLabel(model.getName());
            nameLabel.setBounds(0, 0,
                    serviceCallFlowNodeModel.getWidth(), ServiceCallFlowNodeModel.MARGIN_ALL + ServiceCallFlowNodeModel.TITLE_HEIGHT);

            add(nameLabel);
            JPanel contentPanel = FlowNodeBuilder.createNode(serviceCallFlowNodeModel.getSceneFlowNodeModel());
            contentPanel.setBounds(ServiceCallFlowNodeModel.MARGIN_ALL,
                    ServiceCallFlowNodeModel.TITLE_HEIGHT + ServiceCallFlowNodeModel.MARGIN_ALL + ServiceCallFlowNodeModel.MARGIN_ALL/2,
                    serviceCallFlowNodeModel.getSceneFlowNodeModel().getWidth(),
                    serviceCallFlowNodeModel.getSceneFlowNodeModel().getHeight());

            add(contentPanel);
            setBorder(ofBorderDashLine());

        }else{
            ServiceCallDescriptor serviceDescriptor = ServiceCallDescriptor.of(model.getName());

            nameLabel = FlowNodeBuilder.createNameLabel(serviceDescriptor.getClassName());
            nameLabel.setVerticalAlignment(SwingConstants.BOTTOM);
            nameLabel.setBounds(LINE_SPACE, 0, model.getWidth() - 2 * LINE_SPACE, model.getHeight()/2);
            add(nameLabel);
            JLabel actionLabel = FlowNodeBuilder.createNameLabel(serviceDescriptor.getActionString());
            actionLabel.setVerticalAlignment(SwingConstants.TOP);
            actionLabel.setBounds(LINE_SPACE, model.getHeight()/2, model.getWidth() - 2 * LINE_SPACE, model.getHeight()/2);
            add(actionLabel);
            setBorder(ofBorderBlackLine());
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        if(serviceCallFlowNodeModel.isExpanded()) {
            //画Title的虚线
            Stroke defaultStroke = g2D.getStroke();
            g2D.setStroke(ofBorderDashLineStroke());
            g2D.drawLine(0,
                    ServiceCallFlowNodeModel.MARGIN_ALL + ServiceCallFlowNodeModel.TITLE_HEIGHT,
                    model.getWidth(),
                    ServiceCallFlowNodeModel.MARGIN_ALL + ServiceCallFlowNodeModel.TITLE_HEIGHT);
            g2D.setStroke(defaultStroke);

        }else{

            //左右两根竖线
            g2D.drawLine(LINE_SPACE, 0, LINE_SPACE, model.getHeight());
            g2D.drawLine(model.getWidth() - LINE_SPACE, 0, model.getWidth() - LINE_SPACE, model.getHeight());
        }

    }

}
