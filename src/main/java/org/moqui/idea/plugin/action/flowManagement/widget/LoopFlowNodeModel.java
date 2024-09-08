package org.moqui.idea.plugin.action.flowManagement.widget;


import com.intellij.psi.PsiElement;

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

        super.expandContent();

    }
    @Override
    public void closeContent(){

        conditionNodeModel.x = MARGIN_ALL;
        conditionNodeModel.y = MARGIN_ALL;
        width = FlowNodeModel.DEFAULT_WIDTH + MARGIN_ALL * 2;
        height = FlowNodeModel.DEFAULT_HEIGHT+ MARGIN_ALL * 2;

        super.closeContent();

    }
    @Override
    public void processLayout() {
        //复位Top
        conditionNodeModel.resetTop();
        processRootNodeModel.resetTop();
        collectFlowNodeModel.resetTop();

        FlowNodeModel tempRootModel;

        switch (loopFlowType) {
            case WHEN -> {
                tempRootModel = conditionNodeModel;
                FlowNodeModel.setRelation(conditionNodeModel, processRootNodeModel);
                FlowNodeModel.setRelation(processRootNodeModel,collectFlowNodeModel);
            }
            case UNTIL -> {
                tempRootModel = collectFlowNodeModel;
                FlowNodeModel.setRelation(collectFlowNodeModel, processRootNodeModel);
                FlowNodeModel.setRelation(processRootNodeModel,conditionNodeModel);
            }
            default -> {return;}
        }

        FlowLayout layout = new TopDownFlowLayout(tempRootModel);
        layout.calcLayout();

        //设置自身的大小，左右的宽度需要增加画线的空间，所以还需要调整内部Node的x，
        int gap = FlowLineModel.getLineDefaultWidth() + 10;


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