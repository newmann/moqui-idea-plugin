package org.moqui.idea.plugin.action.flowManagement.widget;


import com.intellij.psi.PsiElement;

import java.awt.*;

public class LoopFlowNodeModel extends ExpandableFlowNodeModel{
    public enum LoopFlowType{
        WHEN, UNTIL;
    }

    private final String condition;
    private final LoopFlowType loopFlowType;
    private final SceneFlowNodeModel processRootNodeModel;

    private final ConditionFlowNodeModel conditionNodeModel;
    private final CollectFlowNodeModel collectFlowNodeModel;
//    private boolean isExpanded = true;
    public LoopFlowNodeModel(LoopFlowType loopFlowType, String condition, PsiElement targetPsiElement, SceneFlowNodeModel processRootNodeModel) {
        super(FlowNodeType.LOOP, null,targetPsiElement);
        this.loopFlowType = loopFlowType;
        this.condition = condition;
        this.processRootNodeModel = processRootNodeModel;
        conditionNodeModel = new ConditionFlowNodeModel(condition,targetPsiElement);
        collectFlowNodeModel = new CollectFlowNodeModel();
        isExpanded = true;
        processLayout();
        addPropertyListener();
    }

    @Override
    public void addPropertyListener(){

        if(processRootNodeModel != null) {
            processRootNodeModel.addPropertyChangeListener(propertyChangeListener);
        }
    }
    @Override
    public void expandContent(){
        if(isExpanded) return;
        super.expandContent();

    }
    @Override
    public void closeContent(){
        if(!isExpanded) return;

        super.closeContent();

    }
    @Override
    public void processLayout() {
        if(isExpanded) {
            processExpand();
        }else {
            conditionNodeModel.x = MARGIN_ALL;
            conditionNodeModel.y = MARGIN_ALL;
            width = FlowNodeModel.DEFAULT_WIDTH + MARGIN_ALL * 2;
            height = FlowNodeModel.DEFAULT_HEIGHT+ MARGIN_ALL * 2;
            inFlowPoint = new Point(width/2,0);
            outFlowPoint = new Point(width/2,height);
        }

    }
    private void processExpand(){
        //复位Top
        conditionNodeModel.resetTop();
        if(processRootNodeModel!= null) processRootNodeModel.resetTop();
        collectFlowNodeModel.resetTop();

        FlowNodeModel tempRootModel;
        tempRootModel = conditionNodeModel;
        if(processRootNodeModel != null) {
            switch (loopFlowType) {
                case WHEN -> {

                    FlowNodeModel.setRelation(conditionNodeModel, processRootNodeModel);
                    FlowNodeModel.setRelation(processRootNodeModel, collectFlowNodeModel);
                }
                case UNTIL -> {
                    FlowNodeModel.setRelation(collectFlowNodeModel, processRootNodeModel);
                    FlowNodeModel.setRelation(processRootNodeModel, conditionNodeModel);
                }
                default -> {
                    return;
                }
            }
        }else {
            FlowNodeModel.setRelation(conditionNodeModel, conditionNodeModel);
        }

        FlowLayout layout = new TopDownFlowLayout(tempRootModel);
        layout.calcLayout();

        //设置自身的大小，左右的宽度需要增加画线的空间，所以还需要调整内部Node的x，
        int gap = FlowLineModel.getLineDefaultWidth() + 1;


        tempRootModel.travelChild((child)->{child.adjustX(gap);});


        width = layout.getWidth()+ 2* gap;//todo 修正最右边画线时不可见问题。
        height = layout.getHeight();
        //设置出入口点
        inFlowPoint = layout.getInFlowPoint();
        outFlowPoint = layout.getOutFlowPoint();

    }

    public SceneFlowNodeModel getProcessRootNodeModel() {
        return isExpanded ? processRootNodeModel: null;
    }


    public ConditionFlowNodeModel getConditionNodeModel() {
        return conditionNodeModel;
    }

    public CollectFlowNodeModel getCollectFlowNodeModel(){return isExpanded ? collectFlowNodeModel : null;}

    public LoopFlowType getLoopFlowType() {return loopFlowType;}

    public String getCondition() {
        return condition;
    }
}
