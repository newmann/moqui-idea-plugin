package org.moqui.idea.plugin.action.flowManagement.widget;

import icons.MoquiIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IfFlowNode extends FlowNode {
    private final IfFlowNodeModel ifFlowNodeModel;
    public IfFlowNode(IfFlowNodeModel model) {
        super(model);
        this.ifFlowNodeModel = model;
        processLayout();
    }


    public void processLayout() {
        setLayout(null); //绝对布局
        JPanel conditionNode = FlowNodeBuilder.createNode(ifFlowNodeModel.getConditionNodeModel());
        conditionNode.setOpaque(false);
        FlowNodeBuilder.setBound(conditionNode, ifFlowNodeModel.getConditionNodeModel());
        add(conditionNode);

        if(ifFlowNodeModel.isExpanded()) {
            if (ifFlowNodeModel.getTrueSceneModel() != null) {
                JPanel trueNode = FlowNodeBuilder.createNode(ifFlowNodeModel.getTrueSceneModel());
                FlowNodeBuilder.setBound(trueNode, ifFlowNodeModel.getTrueSceneModel());
                add(trueNode);
            }
            if (ifFlowNodeModel.getFalseSceneModel() != null) {
                JPanel falseNode = FlowNodeBuilder.createNode(ifFlowNodeModel.getFalseSceneModel());
                FlowNodeBuilder.setBound(falseNode, ifFlowNodeModel.getFalseSceneModel());
                add(falseNode);

            }
            if(ifFlowNodeModel.getCollectionNodeModel() != null) {
                JPanel collectionNode = FlowNodeBuilder.createNode(ifFlowNodeModel.getCollectionNodeModel());
                FlowNodeBuilder.setBound(collectionNode, ifFlowNodeModel.getCollectionNodeModel());
                add(collectionNode);

            }
        }else {
            setBorder(ofBorderDashLine());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(ifFlowNodeModel.isExpanded()) {
            if (this.ifFlowNodeModel.getTrueSceneModel() != null &&
                    this.ifFlowNodeModel.getFalseSceneModel() != null) {
                processBothNoNull(g);
            } else {
                if (this.ifFlowNodeModel.getTrueSceneModel() == null &&
                        this.ifFlowNodeModel.getFalseSceneModel() == null) {
                    processBothIsNull(g);
                } else if (this.ifFlowNodeModel.getTrueSceneModel() == null) {
                    processTrueIsNull(g);
                } else {
                    processFalseIsNull(g);
                }
            }
        }
    }

    private void processBothNoNull(Graphics g){

        TwoFoldLineModel.of(this.ifFlowNodeModel.getConditionNodeModel().getAbsoluteLeftFlowPoint(),
                this.ifFlowNodeModel.getTrueSceneModel().getAbsoluteInFlowPoint(),TwoFlodLineType.FIRST_X).drawLine(g);

        TwoFoldLineModel.of(this.ifFlowNodeModel.getConditionNodeModel().getAbsoluteRightFlowPoint(),
                this.ifFlowNodeModel.getFalseSceneModel().getAbsoluteInFlowPoint(),TwoFlodLineType.FIRST_X).drawLine(g);

        if(!this.ifFlowNodeModel.getTrueSceneModel().getLastFlowNodeModel().isEndFlowNodeModel()) {
            TwoFoldLineModel.of(this.ifFlowNodeModel.getTrueSceneModel().getAbsoluteOutFlowPoint(),
                    this.ifFlowNodeModel.getCollectionNodeModel().getAbsoluteLeftFlowPoint(), TwoFlodLineType.FIRST_Y).drawLine(g);
        }

        if(!this.ifFlowNodeModel.getFalseSceneModel().getLastFlowNodeModel().isEndFlowNodeModel()) {
            TwoFoldLineModel.of(this.ifFlowNodeModel.getFalseSceneModel().getAbsoluteOutFlowPoint(),
                    this.ifFlowNodeModel.getCollectionNodeModel().getAbsoluteRightFlowPoint(), TwoFlodLineType.FIRST_Y).drawLine(g);
        }
    }

    private void processBothIsNull(Graphics g){
        StraightLineModel.of(this.ifFlowNodeModel.getConditionNodeModel().getAbsoluteOutFlowPoint(),
                this.ifFlowNodeModel.getCollectionNodeModel().getAbsoluteInFlowPoint()).drawLine(g);

    }

    private void processTrueIsNull(Graphics g){
        StraightLineModel.of(this.ifFlowNodeModel.getConditionNodeModel().getAbsoluteOutFlowPoint(),
                this.ifFlowNodeModel.getCollectionNodeModel().getAbsoluteInFlowPoint()).drawLine(g);

        TwoFoldLineModel.of(this.ifFlowNodeModel.getConditionNodeModel().getAbsoluteRightFlowPoint(),
                this.ifFlowNodeModel.getFalseSceneModel().getAbsoluteInFlowPoint(),TwoFlodLineType.FIRST_X).drawLine(g);

        if(!this.ifFlowNodeModel.getFalseSceneModel().getLastFlowNodeModel().isEndFlowNodeModel()) {
            TwoFoldLineModel.of(this.ifFlowNodeModel.getFalseSceneModel().getAbsoluteOutFlowPoint(),
                    this.ifFlowNodeModel.getCollectionNodeModel().getAbsoluteRightFlowPoint(), TwoFlodLineType.FIRST_Y).drawLine(g);
        }
    }

    private void processFalseIsNull(Graphics g){
        StraightLineModel.of(this.ifFlowNodeModel.getConditionNodeModel().getAbsoluteOutFlowPoint(),
                this.ifFlowNodeModel.getTrueSceneModel().getAbsoluteInFlowPoint()).drawLine(g);
        
        if(!this.ifFlowNodeModel.getTrueSceneModel().getLastFlowNodeModel().isEndFlowNodeModel()) {
            StraightLineModel.of(this.ifFlowNodeModel.getTrueSceneModel().getAbsoluteOutFlowPoint(),
                    this.ifFlowNodeModel.getCollectionNodeModel().getAbsoluteInFlowPoint()).drawLine(g);
        }

        ThreeFoldLineModel lineModel = ThreeFoldLineModel.ofBypassBlockRight(this.ifFlowNodeModel.getConditionNodeModel().getAbsoluteRightFlowPoint(),
                this.ifFlowNodeModel.getTrueSceneModel(),
                this.ifFlowNodeModel.getCollectionNodeModel().getAbsoluteRightFlowPoint()
                );
        lineModel.drawLine(g);

    }
}
