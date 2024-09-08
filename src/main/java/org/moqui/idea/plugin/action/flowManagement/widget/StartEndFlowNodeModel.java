package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.psi.PsiElement;

import java.awt.*;

public class StartEndFlowNodeModel extends FlowNodeModel {
    private static final int START_END_WIDTH = 150;
    private static final int START_END_HEIGHT = 50;
    StartEndFlowNodeModel(FlowNodeType type, String name,PsiElement targetPsiElement) {
        this(type,name,targetPsiElement,"");
    }

    public StartEndFlowNodeModel(FlowNodeType type, String name, PsiElement targetPsiElement, String toolTips) {
        super(type, name,targetPsiElement, toolTips);
        this.width = START_END_WIDTH;
        this.height = START_END_HEIGHT;
        this.inFlowPoint = new Point(this.width/2, 0);
        this.outFlowPoint = new Point(this.width/2, this.height);

    }
    public static StartEndFlowNodeModel ofBeginFlowNodeModel(String name, PsiElement targetPsiElement,String toolTips){
        return new StartEndFlowNodeModel(FlowNodeType.START,name,targetPsiElement, toolTips);
    }
    public static StartEndFlowNodeModel ofBeginFlowNodeModel(String name,PsiElement targetPsiElement){
        return ofBeginFlowNodeModel(name,targetPsiElement, "");
    }
    public static StartEndFlowNodeModel ofBeginFlowNodeModel(PsiElement targetPsiElement){
        return ofBeginFlowNodeModel("Start", targetPsiElement,"");
    }

    public static StartEndFlowNodeModel ofEndFlowNodeModel(String name,PsiElement targetPsiElement, String toolTips){
        return new StartEndFlowNodeModel(FlowNodeType.END,name, targetPsiElement,toolTips);
    }
    public static StartEndFlowNodeModel ofEndFlowNodeModel(String name,PsiElement targetPsiElement){
        return ofEndFlowNodeModel(name, targetPsiElement,"");
    }
    public static StartEndFlowNodeModel ofEndFlowNodeModel(PsiElement targetPsiElement){
        return ofEndFlowNodeModel("End", targetPsiElement,"");
    }

}
