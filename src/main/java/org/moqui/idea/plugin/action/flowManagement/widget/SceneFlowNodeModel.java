package org.moqui.idea.plugin.action.flowManagement.widget;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 包含一个处理过程，同时对这个过程进行布局
 */
public class SceneFlowNodeModel extends FlowNodeModel {
    private final FlowNodeModel sceneNodeListRootNode;//通过开始节点，通过节点的parentNodeList和childNodeList就可以检索到整个流程
    private FlowNodeModel lastFlowNodeModel;
    public SceneFlowNodeModel(FlowNodeModel startNode){
        super(FlowNodeType.SCENE,null,null);//scene没有自己对应的PsiElement
        this.sceneNodeListRootNode = startNode;
        processLayout();
        addPropertyListener();
    }

    private void addPropertyListener(){

        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if(propertyChangeEvent.getPropertyName().equals(PROPERTY_NAME_LAYOUT_CHANGE)) {
                    processLayout();
                    firePropertyChange(propertyChangeEvent);
                }
            }
        };

        sceneNodeListRootNode.travelChild((child)->{
            child.addPropertyChangeListener(listener);
        });

    }
    public void processLayout(){
        FlowLayout layout = new TopDownFlowLayout(sceneNodeListRootNode);
        layout.calcLayout();

        //设置出入口点和自身的大小
        width = layout.getWidth();
        height = layout.getHeight();
        inFlowPoint = layout.getInFlowPoint();
        outFlowPoint = layout.getOutFlowPoint();
        lastFlowNodeModel = layout.getLastFlowNodeModel();

    };

    public FlowNodeModel getSceneNodeListRootNode() {
        return sceneNodeListRootNode;
    }

    public FlowNodeModel getLastFlowNodeModel(){return lastFlowNodeModel;}
}
