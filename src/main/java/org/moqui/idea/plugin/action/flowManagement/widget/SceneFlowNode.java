package org.moqui.idea.plugin.action.flowManagement.widget;


import javax.swing.*;
import java.awt.*;

/**
 * 包含一个处理过程，同时对这个过程进行布局
 */
public class SceneFlowNode extends FlowNode {
    private final SceneFlowNodeModel sceneModel;//通过开始节点，通过节点的parentNodeList和childNodeList就可以检索到整个流程
    public SceneFlowNode(SceneFlowNodeModel sceneModel){
        super(sceneModel);
//        super(FlowNodeType.SCENE,null);
        this.sceneModel = sceneModel;
        processLayout();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FlowNodeModel parent = sceneModel.getSceneNodeListRootNode();
        FlowNodeModel child;
        while(parent != null) {
            //连接线
            child = parent.getChildNode();
            if(child == null) break;
            Point from = parent.getAbsoluteOutFlowPoint();
            Point to = child.getAbsoluteInFlowPoint();
            FlowLineModel lineModel = StraightLineModel.of(from,to);
            lineModel.drawLine(g);

            parent = child;

        }

    }

    public void processLayout(){
        setLayout(null);
        FlowNodeModel parent = sceneModel.getSceneNodeListRootNode();
        FlowNodeModel child;
        while(parent != null) {
            JPanel parentPanel = FlowNodeBuilder.createNode(parent);
            FlowNodeBuilder.setBound(parentPanel,parent);
            add(parentPanel);
            child = parent.getChildNode();
            if(child == null) break;
            parent = child;

        }


    };

}
