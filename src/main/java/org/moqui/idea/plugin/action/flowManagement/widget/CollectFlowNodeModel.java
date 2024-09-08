package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

public class CollectFlowNodeModel extends FlowNodeModel {
    public static final int COLLECT_WIDTH = 30;
    public static final int COLLECT_HEIGHT = 30;
    protected Point leftFlowPoint ;//左出入点
    protected Point rightFlowPoint ;//右出入点
    public CollectFlowNodeModel() {
        super(FlowNodeType.COLLECT, null,null);
        this.width = COLLECT_WIDTH;
        this.height = COLLECT_HEIGHT;
        this.inFlowPoint = new Point(this.width/2, 0);
        this.outFlowPoint = new Point(this.width/2, this.height);
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
