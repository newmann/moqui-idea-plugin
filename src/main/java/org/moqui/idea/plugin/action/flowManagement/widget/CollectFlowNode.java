package org.moqui.idea.plugin.action.flowManagement.widget;

import java.awt.*;

public class CollectFlowNode extends FlowNode {
    public CollectFlowNode(FlowNodeModel model) {
        super(model);
        setLayout(null);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //绘制图形
        int radius = Math.min(this.model.getWidth()/2, this.model.getHeight()/2);
        Point center= new Point(this.model.getWidth()/2, this.model.height/2);
        g2d.drawOval(0,0,this.model.getWidth()-1,this.model.getHeight()-1);//边框修正，否则绘制圆形不完整

        int offset = (int) (radius/ Math.sqrt(2));
        g2d.drawLine(center.x-offset,center.y-offset, center.x+offset,center.y+offset);
        g2d.drawLine(center.x+offset,center.y-offset, center.x-offset,center.y+offset);

    }

}
