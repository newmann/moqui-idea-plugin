package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.psi.PsiElement;

import java.awt.*;

public class IfFlowNodeModel extends ExpandableFlowNodeModel{
    private final String condition;
    private final SceneFlowNodeModel trueSceneModel;
    private final SceneFlowNodeModel falseSceneModel;

    private final ConditionFlowNodeModel conditionNodeModel;
    private final CollectFlowNodeModel collectFlowNodeModel;
//    private boolean isExpanded = true;

    public IfFlowNodeModel(String condition, PsiElement targetPsiElement, SceneFlowNodeModel trueSceneModel, SceneFlowNodeModel falseSceneModel) {
        this(condition,targetPsiElement, trueSceneModel, falseSceneModel,"");
    }
    public IfFlowNodeModel(String condition, PsiElement targetPsiElement, SceneFlowNodeModel trueSceneModel, SceneFlowNodeModel falseSceneModel,String toolTips) {
        super(FlowNodeType.IF, null,targetPsiElement,toolTips);
        this.condition = condition;
        this.trueSceneModel = trueSceneModel;
        this.falseSceneModel = falseSceneModel;
        collectFlowNodeModel = new CollectFlowNodeModel();
        conditionNodeModel = new ConditionFlowNodeModel(condition,targetPsiElement);
        isExpanded = true;
        processLayout();
        addPropertyListener();
    }
    @Override
    public void addPropertyListener(){
        if(trueSceneModel != null) {
            trueSceneModel.addPropertyChangeListener(propertyChangeListener);
        }
        if(falseSceneModel != null) {
            falseSceneModel.addPropertyChangeListener(propertyChangeListener);
        }
    }
    @Override
    public void expandContent(){
//        if(isExpanded) return;
//        isExpanded= true;
        //复位conditionNode
//        conditionNodeModel.x = 0;
//        conditionNodeModel.y = 0;
        super.expandContent();
//        processLayout();
//        firePropertyChange(PROPERTY_NAME_LAYOUT_CHANGE,false,true);
    }
    @Override
    public void closeContent(){
//        if(!isExpanded) return;
//
//        isExpanded = false;
        //复位conditionNode
        conditionNodeModel.x = MARGIN_ALL;
        conditionNodeModel.y = MARGIN_ALL;
        width = FlowNodeModel.DEFAULT_WIDTH + MARGIN_ALL * 2;
        height = FlowNodeModel.DEFAULT_HEIGHT+ MARGIN_ALL * 2;

        super.closeContent();
//        inFlowPoint = new Point(width/2,0);
//        outFlowPoint = new Point(width/2,height);
//        firePropertyChange(PROPERTY_NAME_LAYOUT_CHANGE,true,false);

    }

    @Override
    public void processLayout() {
        //复位各个组件
        conditionNodeModel.resetTop();
        collectFlowNodeModel.resetTop();

        if(trueSceneModel !=null && falseSceneModel !=null) {
            processBothNoNull();
        }else {
            if(trueSceneModel == null && falseSceneModel ==null) {
                processBothIsNull();
            }else if(trueSceneModel == null) {
                processTrueIsNull();
            }else {
                processFalseIsNull();
            }
        }

        //取IF组件的出入口
        inFlowPoint = conditionNodeModel.getAbsoluteInFlowPoint();

        outFlowPoint = collectFlowNodeModel.getAbsoluteOutFlowPoint();

    }
    private void processBothIsNull(){
        int totalHeight = conditionNodeModel.getHeight() + TopDownFlowLayout.getVerticalSpace();
        int conditionWidth = conditionNodeModel.getWidth();
        //定位Collect
        collectFlowNodeModel.x = (conditionWidth - collectFlowNodeModel.getWidth())/2;
        collectFlowNodeModel.y = totalHeight;

        totalHeight += collectFlowNodeModel.getHeight();

        //设置IF组件的大小
        height = totalHeight;

    }
    private void processFalseIsNull(){
        trueSceneModel.resetTop();
        //将conditionNode的出口和trueSceneNode的入口对齐
        //将collectNode的入口和trueSceneNode的出口对齐
        int totalHeight = conditionNodeModel.getHeight() + TopDownFlowLayout.getVerticalSpace();
        Point inPoint = trueSceneModel.getInFlowPoint();
        Point outPoint = conditionNodeModel.getOutFlowPoint();
        int offset = outPoint.x - inPoint.x;
        if(offset > 0 ) {
            trueSceneModel.x = offset;
        }else if(offset < 0) {
            conditionNodeModel.x = -offset;
        }
        trueSceneModel.y = totalHeight;

        totalHeight +=trueSceneModel.getHeight();
        totalHeight += TopDownFlowLayout.getVerticalSpace();
        //定位Collect
        inPoint = collectFlowNodeModel.getInFlowPoint();

        outPoint = trueSceneModel.getAbsoluteOutFlowPoint();
        offset = outPoint.x - inPoint.x;

        if(offset > 0 ) {
            collectFlowNodeModel.x = offset;
        }else if(offset < 0) {
            //需要同时调整前面的两个Node，一般都不会出现这种情况，因为collectNode的宽度小于conditionNode
            trueSceneModel.x += -offset;
            conditionNodeModel.x += -offset;
        }
        collectFlowNodeModel.y = totalHeight;
        totalHeight += collectFlowNodeModel.getHeight();

        //设置IF组件的大小，为了画连接线，所以多5个点
        width = Math.max(conditionNodeModel.x+ conditionNodeModel.getWidth()+FlowLineModel.getLineDefaultWidth(),
                    trueSceneModel.getTopLeftPoint().x+trueSceneModel.getWidth()+ FlowLineModel.getLineDefaultWidth()) +5;
        height = totalHeight;
    }
    private void processTrueIsNull(){
        falseSceneModel.resetTop();
        //直线直接连接到collectNode的入口，falseSceneNode的左侧和Collect的右侧对齐即可
        //如果falseSceneNode的宽度比conditionNode的宽度小，需要微调


        int totalHeight = conditionNodeModel.getHeight() + TopDownFlowLayout.getVerticalSpace();


        int falseDefaultX = conditionNodeModel.outFlowPoint.x + collectFlowNodeModel.width/2;
        Point inPoint = falseSceneModel.getInFlowPoint();
        Point outPoint = conditionNodeModel.getRightFlowPoint();
        int offset = outPoint.x+FlowLineModel.getLineDefaultWidth() - inPoint.x;
        if(offset>0) {
            falseSceneModel.x = falseDefaultX+offset;
        }else {
            falseSceneModel.x = falseDefaultX;
        }
        falseSceneModel.y = totalHeight;

        totalHeight +=falseSceneModel.getHeight();
        totalHeight += TopDownFlowLayout.getVerticalSpace();

        //定位Collect
        collectFlowNodeModel.x = conditionNodeModel.getOutFlowPoint().x - collectFlowNodeModel.getInFlowPoint().x;
        collectFlowNodeModel.y = totalHeight;

        totalHeight += collectFlowNodeModel.getHeight();

        //设置IF组件的大小
        width = Math.max(conditionNodeModel.getWidth(),falseSceneModel.getTopLeftPoint().x+falseSceneModel.getWidth());
        height = totalHeight;

    }

    private void processBothNoNull(){
        trueSceneModel.resetTop();
        falseSceneModel.resetTop();

        int totalHeight = conditionNodeModel.getHeight() + TopDownFlowLayout.getVerticalSpace();
        int conditionWidth = conditionNodeModel.getWidth();

        trueSceneModel.y = totalHeight;
        falseSceneModel.y = totalHeight;
        //定位condition
        //先按缺省方式摆好trueSceneNode和falseSceneNode
        falseSceneModel.x = trueSceneModel.getWidth() + TopDownFlowLayout.getHorizontalSpace();
        //先计算标准间隔是否能放下conditionNode
        int gap = falseSceneModel.getTopLeftPoint().x+falseSceneModel.getInFlowPoint().x - trueSceneModel.getInFlowPoint().x;
        int offset = gap - conditionNodeModel.width - 2 * FlowLineModel.getLineDefaultWidth();
        if(offset<0) {
            //放不下，移动falseSceneNode
            falseSceneModel.adjustX(-offset);
//            falseSceneModel.x -= offset;
            gap -= offset;
        }
        //将conditionNode移动到trueSceneNode和falseSceneNode之间
        conditionNodeModel.x =trueSceneModel.getInFlowPoint().x + (gap-conditionNodeModel.getWidth()) / 2;

        totalHeight += Math.max(trueSceneModel.getHeight(), falseSceneModel.getHeight());

        int sceneWidth = falseSceneModel.x + falseSceneModel.getWidth();
        totalHeight += TopDownFlowLayout.getVerticalSpace();

        //定位Collect
        gap = falseSceneModel.getTopLeftPoint().x+falseSceneModel.getOutFlowPoint().x - trueSceneModel.getOutFlowPoint().x;
        offset = gap - collectFlowNodeModel.getWidth() - 2 * FlowLineModel.getLineDefaultWidth();
        if(offset<0) {
            //放不下，移动falseSceneNode 和conditionNode
            falseSceneModel.adjustX(-offset);
//            falseSceneModel.x -= offset;
            conditionNodeModel.adjustX(-offset);
//            conditionNodeModel.x -= offset;
            gap -= offset;
        }
        collectFlowNodeModel.x = trueSceneModel.getOutFlowPoint().x + (gap- collectFlowNodeModel.getWidth()) / 2;
        collectFlowNodeModel.y = totalHeight;

        totalHeight += collectFlowNodeModel.getHeight();

        //设置IF组件的大小
        width = Math.max(conditionWidth,sceneWidth);
        height = totalHeight;

    }

//    public boolean isExpanded() {
//        return isExpanded;
//    }

    public SceneFlowNodeModel getTrueSceneModel() {
        return isExpanded ? trueSceneModel : null;
    }

    public SceneFlowNodeModel getFalseSceneModel() {
        return isExpanded ? falseSceneModel: null;
    }


    public ConditionFlowNodeModel getConditionNodeModel() {
        return conditionNodeModel;
    }

    public CollectFlowNodeModel getCollectionNodeModel(){
        return isExpanded ? collectFlowNodeModel: null;
    }
}
