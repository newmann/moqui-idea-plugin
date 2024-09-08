package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

public class StraightLineModel extends FlowLineModel{

    public static StraightLineModel of(Point from, Point to){
        return new StraightLineModel(from, to);
    }

    public StraightLineModel(Point from, Point to){
        super();
        linePointArray.add(from);
        linePointArray.add(to);
        //设置大小
        width = Math.max(Math.abs(from.x-to.x),10);
        height = Math.max(Math.abs(from.y-to.y),10);
    }


}
