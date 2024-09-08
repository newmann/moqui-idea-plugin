package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.psi.PsiElement;

import java.awt.*;

public class ConditionFlowNodeModel extends FlowNodeModel {

    protected Point leftFlowPoint ;//左出入点
    protected Point rightFlowPoint ;//右出入点
    public ConditionFlowNodeModel(String condition, PsiElement targetElement) {

        super(FlowNodeType.CONDITION, condition,targetElement);

        this.leftFlowPoint = new Point(0, this.height/2);
        this.rightFlowPoint = new Point(this.width, this.height/2);
    }

    public Point getLeftFlowPoint() {
        return leftFlowPoint;
    }
    public Point getRightFlowPoint(){
        return rightFlowPoint;
    }

    public Point getAbsoluteLeftFlowPoint(){
        return new Point(x+leftFlowPoint.x, y+leftFlowPoint.y);
    }
    public Point getAbsoluteRightFlowPoint(){
        return new Point(x+rightFlowPoint.x, y+rightFlowPoint.y);
    }

}
