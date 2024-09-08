package org.moqui.idea.plugin.action.flowManagement.widget;

import icons.MoquiIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoopFlowNode extends FlowNode {
    LoopFlowNodeModel loopFlowNodeModel;

    public LoopFlowNode(LoopFlowNodeModel model) {
        super(model);
        this.loopFlowNodeModel = model;
        processLayout();
    }

    public void processLayout() {

        setLayout(null); //绝对布局
//        MouseAdapter mouseAdapter = new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if(e.getClickCount() == 2) {
//                    if(loopFlowNodeModel.isExpanded()) {
//                        loopFlowNodeModel.closeContent();
//                    }else {
//                        loopFlowNodeModel.expandContent();
//                    }
//                }
//            }
//        };
        JPanel conditionNode = FlowNodeBuilder.createNode(loopFlowNodeModel.getConditionNodeModel());
        FlowNodeBuilder.setBound(conditionNode, loopFlowNodeModel.getConditionNodeModel());
        add(conditionNode);
//        conditionNode.addMouseListener(mouseAdapter);
        conditionNode.setOpaque(false);

        if(loopFlowNodeModel.isExpanded()) {
            if(loopFlowNodeModel.getProcessRootNodeModel() != null) {
                JPanel processNode = FlowNodeBuilder.createNode(loopFlowNodeModel.getProcessRootNodeModel());
                FlowNodeBuilder.setBound(processNode, loopFlowNodeModel.getProcessRootNodeModel());
                add(processNode);
            }
            if(loopFlowNodeModel.getCollectFlowNodeModel() != null) {
                JPanel collectNode = FlowNodeBuilder.createNode(loopFlowNodeModel.getCollectFlowNodeModel());
                FlowNodeBuilder.setBound(collectNode, loopFlowNodeModel.getCollectFlowNodeModel());
                add(collectNode);
//                collectNode.addMouseListener(mouseAdapter);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (loopFlowNodeModel.isExpanded()) {
            FlowLineModel lineModel;
            switch (loopFlowNodeModel.getLoopFlowType()) {
                case WHEN -> {
                    lineModel = StraightLineModel.of(loopFlowNodeModel.getConditionNodeModel().getAbsoluteOutFlowPoint(),
                            loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteInFlowPoint());
                    lineModel.drawLine(g);

                    //画从ProcessNode返回的连接线
                    lineModel = new FlowLineModel();
                    lineModel.addLinePoint(loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteOutFlowPoint());
                    lineModel.addLinePoint(new Point(loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteOutFlowPoint().x,
                            loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteOutFlowPoint().y + FlowLineModel.getLineDefaultHeight()));
                    int leftX = Math.min(loopFlowNodeModel.getConditionNodeModel().getAbsoluteLeftFlowPoint().x - FlowLineModel.getLineDefaultWidth(),
                            loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteOutFlowPoint().x - loopFlowNodeModel.getProcessRootNodeModel().getWidth() / 2 - TopDownFlowLayout.getHorizontalSpace());

                    lineModel.addLinePoint(new Point(leftX,
                            loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteOutFlowPoint().y + FlowLineModel.getLineDefaultHeight()));
                    lineModel.addLinePoint(new Point(leftX,
                            loopFlowNodeModel.getConditionNodeModel().getAbsoluteLeftFlowPoint().y));
                    lineModel.addLinePoint(loopFlowNodeModel.getConditionNodeModel().getAbsoluteLeftFlowPoint());
                    lineModel.drawLine(g);


                    lineModel = ThreeFoldLineModel.ofBypassBlockRight(this.loopFlowNodeModel.getConditionNodeModel().getAbsoluteRightFlowPoint(),
                            this.loopFlowNodeModel.getProcessRootNodeModel(),
                            this.loopFlowNodeModel.getCollectFlowNodeModel().getAbsoluteRightFlowPoint()
                    );
                    lineModel.drawLine(g);
                }
                case UNTIL -> {
                    lineModel = StraightLineModel.of(loopFlowNodeModel.getCollectFlowNodeModel().getAbsoluteOutFlowPoint(),
                            loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteInFlowPoint());
                    lineModel.drawLine(g);

                    lineModel = ThreeFoldLineModel.ofBypassBlockRight(this.loopFlowNodeModel.getConditionNodeModel().getAbsoluteRightFlowPoint(),
                            this.loopFlowNodeModel.getProcessRootNodeModel(),
                            this.loopFlowNodeModel.getCollectFlowNodeModel().getAbsoluteRightFlowPoint()
                    );
                    lineModel.drawLine(g);

                    lineModel = StraightLineModel.of(loopFlowNodeModel.getProcessRootNodeModel().getAbsoluteOutFlowPoint(),
                            loopFlowNodeModel.getConditionNodeModel().getAbsoluteInFlowPoint());
                    lineModel.drawLine(g);

                }
            }
        }else{
            //画个椭圆形
            Graphics2D g2D = (Graphics2D) g;
            Stroke defaultStroke = g2D.getStroke();
            g2D.setStroke(ofBorderDashLineStroke());
            g2D.drawOval(0,0, loopFlowNodeModel.getWidth(),loopFlowNodeModel.getHeight()-1);
            g2D.setStroke(defaultStroke);
        }
    }
}
