package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

public class TwoFoldLineModel extends FlowLineModel{
    private TwoFlodLineType type;
    /**
     * 折线的第一根线条的长度，用于定位入口节点的位置，
     * @return integer
     */


    public static TwoFoldLineModel of(Point from, Point to,TwoFlodLineType type){
        return new TwoFoldLineModel(from, to,type);

    }

    public TwoFoldLineModel(Point from, Point to,TwoFlodLineType type){
        super();
        this.type = type;
        linePointArray.add(from);

        switch (type){
            case FIRST_X -> {linePointArray.add(new Point(to.x, from.y));}
            case FIRST_Y -> {linePointArray.add(new Point(from.x, to.y));}
        }

        linePointArray.add(to);

        //设置大小
        width = Math.abs(from.x-to.x);
        height = Math.abs(from.y-to.y);

    }


}
