package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

public class DiamondLineModel extends FlowLineModel{

    public static DiamondLineModel of(int width,int height,String label){
        return new DiamondLineModel(width,height,label);
    }

    public DiamondLineModel(int width,int height,String label){
        super();
        this.width = width;
        this.height = height;
        this.label = label;
        //添加4个顶点
        int midWidth = width/2;
        int midHeight = height/2;
        this.linePointArray.add(new Point(midWidth,0));
        this.linePointArray.add(new Point(width,midHeight));
        this.linePointArray.add(new Point(midWidth,height-1));
        this.linePointArray.add(new Point(0,midHeight));
        this.linePointArray.add(new Point(midWidth,0));

    }

//    public void addLinePoint(Point point){
//        linePointArray.add(point);
//    }


}
