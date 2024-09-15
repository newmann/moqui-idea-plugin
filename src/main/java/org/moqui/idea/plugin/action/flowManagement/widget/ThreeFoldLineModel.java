package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

public class ThreeFoldLineModel extends FlowLineModel{

    public static ThreeFoldLineModel ofBypassBlockRight(Point from, FlowNodeModel block, Point to){
        ThreeFoldLineModel lineModel = new ThreeFoldLineModel();
        lineModel.addLinePoint(from);

        int x = Math.max(from.x,
                block.x + block.getWidth()) + FlowLineModel.getLineDefaultWidth();

        lineModel.addLinePoint(new Point(x,from.y));
        lineModel.addLinePoint(new Point(x,to.y));
        lineModel.addLinePoint(to);
        return lineModel;
    }
    public static ThreeFoldLineModel ofBypassBlockLeft(Point from, FlowNodeModel block,Point to){
        ThreeFoldLineModel lineModel = new ThreeFoldLineModel();
        lineModel.addLinePoint(from);

        int x = Math.min(from.x - FlowLineModel.getLineDefaultWidth(),
                block.x - FlowLineModel.getLineDefaultWidth());

        lineModel.addLinePoint(new Point(x,from.y));
        lineModel.addLinePoint(new Point(x,to.y));
        lineModel.addLinePoint(to);
        return lineModel;
    }

    public static ThreeFoldLineModel ofBypassBlockRight(Point from, int foldPointX, Point to){
        ThreeFoldLineModel lineModel = new ThreeFoldLineModel();
        lineModel.addLinePoint(from);
        lineModel.addLinePoint(new Point(foldPointX,from.y));
        lineModel.addLinePoint(new Point(foldPointX,to.y));
        lineModel.addLinePoint(to);
        return lineModel;
    }


}
