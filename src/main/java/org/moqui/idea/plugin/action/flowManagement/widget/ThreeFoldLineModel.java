package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

public class ThreeFoldLineModel extends FlowLineModel{

    public static ThreeFoldLineModel ofBypassBlockRight(Point from, FlowNodeModel block, Point to){
        ThreeFoldLineModel lineModel = new ThreeFoldLineModel();
        lineModel.addLinePoint(from);

        int x = Math.max(from.x+ FlowLineModel.getLineDefaultWidth(),
                block.x + block.getWidth() + TopDownFlowLayout.getHorizontalSpace());

        lineModel.addLinePoint(new Point(x,from.y));
        lineModel.addLinePoint(new Point(x,to.y));
        lineModel.addLinePoint(to);
        return lineModel;
    }
    public static ThreeFoldLineModel ofBypassBlockLeft(Point from, FlowNodeModel block,Point to){
        ThreeFoldLineModel lineModel = new ThreeFoldLineModel();
        lineModel.addLinePoint(from);

        int x = Math.min(from.x - FlowLineModel.getLineDefaultWidth(),
                block.x - TopDownFlowLayout.getHorizontalSpace());

        lineModel.addLinePoint(new Point(x,from.y));
        lineModel.addLinePoint(new Point(x,to.y));
        lineModel.addLinePoint(to);
        return lineModel;
    }


//    public static ThreeFoldLineModel of(Point from, Point to){
//        return new ThreeFoldLineModel(from, to);
//    }
//
//    ThreeFoldLineModel(Point from, Point to){
//        super();
//        linePointArray.add(from);
//        //添加折线的中间的点
//        if(from.y <= to.y) {
//            int midY = (from.y - to.y) /2;
//            linePointArray.add(new Point(from.x,from.y+midY));
//            linePointArray.add(new Point(to.x,from.y+midY));
//        }else {
//            int midX = (from.x - to.x) / 2;
//            linePointArray.add(new Point(from.x+midX,from.y));
//            linePointArray.add(new Point(from.x+midX,to.y));
//
//        }
//        linePointArray.add(to);
//
//        //设置大小
//        width = Math.abs(from.x-to.x);
//        height = Math.abs(from.y-to.y);
//
//    }


}
