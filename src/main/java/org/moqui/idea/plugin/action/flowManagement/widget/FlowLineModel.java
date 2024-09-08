package org.moqui.idea.plugin.action.flowManagement.widget;


import java.awt.*;
import java.util.ArrayList;

/**
 * 绘制连接线
 */
public class FlowLineModel{
    /**
     * 绘制折线时，先往下绘制线条的高度
     * @return int
     */
    public static int getLineDefaultHeight(){
        return 20;
    }
    public static int getLineDefaultWidth(){
        return 10;
    }

    private static int ARRAY_WIDTH = 4;
    private static int ARRAY_LENGTH = 10;
    protected ArrayList<Point> linePointArray = new ArrayList<>();
    protected String label="";
//    public void FlowNodeModel(){
//        this.linePointArray = new ArrayList<>();
//        this.label = "";
//    }
//    public FlowLineModel(ArrayList<Point> linePointArray,String label){
//        this.linePointArray = linePointArray;
//        this.label = label;
//    };

    public int width;
    public int height;
    public ArrayList<Point> getLinePointArray() {
        return linePointArray;
    }

    public void addLinePoint(Point point){
        linePointArray.add(point);
    }
    public void setLinePointArray(ArrayList<Point> linePointArray){
        this.linePointArray.clear();
        this.linePointArray.addAll(linePointArray);
    }

    public String getLabel() {
        return label;
    }
//
//    public void addLinePoint(Point point){
//        linePointArray.add(point);
//    }

    public void drawLine(Graphics2D g2d) {
        if (linePointArray.isEmpty()) return;

        Point from = linePointArray.get(0);
        Point to = null;
        for(int i =1; i<linePointArray.size(); i++){
            to = linePointArray.get(i);
            g2d.drawLine(from.x,from.y,to.x,to.y);
            from = to;
        }

        //画箭头
        if(to == null){return;}
        from = linePointArray.get(linePointArray.size()-2);
        to = linePointArray.get(linePointArray.size()-1);
        if(from.x == to.x) {
            if(from.y < to.y) {
                //朝下
                g2d.drawLine(to.x-ARRAY_WIDTH,to.y-ARRAY_LENGTH,to.x,to.y);
                g2d.drawLine(to.x+ARRAY_WIDTH,to.y-ARRAY_LENGTH,to.x,to.y);
            }else {
                //朝上
                g2d.drawLine(to.x-ARRAY_WIDTH,to.y+ARRAY_LENGTH,to.x,to.y);
                g2d.drawLine(to.x+ARRAY_WIDTH,to.y+ARRAY_LENGTH,to.x,to.y);

            }

        }else if(from.y == to.y) {
            if(from.x < to.x) {
                //朝右
                g2d.drawLine(to.x-ARRAY_LENGTH,to.y-ARRAY_WIDTH,to.x,to.y);
                g2d.drawLine(to.x-ARRAY_LENGTH,to.y+ARRAY_WIDTH,to.x,to.y);

            }else {
                //朝左
                g2d.drawLine(to.x+ARRAY_LENGTH,to.y-ARRAY_WIDTH,to.x,to.y);
                g2d.drawLine(to.x+ARRAY_LENGTH,to.y+ARRAY_WIDTH,to.x,to.y);
            }
        }

//        if(label !=null && !label.isEmpty()){
//            g2d.drawString(label,);
//        }

    }
    public void drawLine(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        drawLine(g2d);
    }

    public static boolean isZeroPoint(Point point){
        return point.x == 0 && point.y == 0;
    }
    public static boolean isNotZeroPoint(Point point){
        return point.x != 0 || point.y != 0;
    }

}
