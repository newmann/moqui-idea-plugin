package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

/**
 * 1、从上到下的布局算法
 * 2、将每个节点的in和out节点位置对其即可
 */
public class TopDownFlowLayout implements FlowLayout {
    //垂直间隔
    public static int getVerticalSpace() {
        return 50;
    }
    //水平间隔
    public static int getHorizontalSpace() {
        return 10;
    }

    private final FlowNodeModel rootNode;
    private int width,height;
//    private Point inFlowPoint,outFlowPoint;
    private FlowNodeModel lastNode;

//    private ArrayList<ArrayList<FlowNode>> rootNodeList =new ArrayList<>();
    public TopDownFlowLayout(FlowNodeModel rootNode){
        this.rootNode = rootNode;
    }

    @Override
    public void calcLayout() {
        width = 0;
        height = 0;
        if(rootNode == null) return;

        resetNodesTopLeft();
        FlowNodeModel parent = rootNode;
        height = parent.getHeight()+getVerticalSpace();
        width = parent.getWidth();


        FlowNodeModel child = parent.getChildNode();
        while(child != null) {

            resetParentsNodeX(child);
            child.y = height;
            parent = child;
            height += parent.getHeight() + getVerticalSpace();
            width = Math.max(width,parent.getWidth());
            child = parent.getChildNode();
        }
        //将height最后的间隔删除
        height -= getVerticalSpace();

        //设置最后的Node
        lastNode = parent;

    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Point getInFlowPoint() {
        if (rootNode == null) {
            return new Point(0, 0);
        }else {
            return rootNode.getAbsoluteInFlowPoint();
        }

    }

    @Override
    public Point getOutFlowPoint() {
        if(lastNode == null) {
            return new Point(0, 0);
        }else {
            return lastNode.getAbsoluteOutFlowPoint();
        }
    }

    /**
     * 将所有的node的topLeft重置为（0,0）
     * 在重新计算的时候有用
     */
    private void resetNodesTopLeft(){
        rootNode.travelChild(
                (item)->{
                            if(item != null) {
                                item.x = item.y = 0;
                            }
                        }
        );

    }

    private void resetParentsNodeX(FlowNodeModel child) {
        if(child == null) return;

        FlowNodeModel parent = child.getParentNode();

        while(parent != null) {
            int parentOutPointX = parent.getOutFlowPoint().x + parent.x;
            int childInPointX = child.getInFlowPoint().x + child.x;
            int xOffset= childInPointX - parentOutPointX;
            if(xOffset > 0) {
                parent.adjustX(xOffset);
//                parent.x += xOffset;

            }else if(xOffset < 0) {
                child.adjustX(-xOffset);
//                child.x -= xOffset;
            }
            child = parent;
            parent = child.getParentNode();
        }

    }

    @Override
    public FlowNodeModel getLastFlowNodeModel() {
        return lastNode;
    }
}
