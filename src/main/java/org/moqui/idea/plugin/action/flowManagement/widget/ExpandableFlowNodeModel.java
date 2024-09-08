package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.psi.PsiElement;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ExpandableFlowNodeModel extends FlowNodeModel {


    protected boolean isExpanded = false;
    protected final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if(propertyChangeEvent.getPropertyName().equals(PROPERTY_NAME_LAYOUT_CHANGE)) {
                processLayout();
                firePropertyChange(propertyChangeEvent);
            }
        }
    };

    public ExpandableFlowNodeModel(FlowNodeType type, String name, PsiElement targetPsiElement) {
        this(type,name,targetPsiElement,"");
    }

    public ExpandableFlowNodeModel(FlowNodeType type, String name,PsiElement targetPsiElement, String toolTips ) {
        super(type, name,targetPsiElement, toolTips);
    }

    public void expandContent(){
        if(isExpanded) return;
        isExpanded= true;
        processLayout();
        firePropertyChange(PROPERTY_NAME_LAYOUT_CHANGE,false,true);
    }
    public void closeContent(){
        if(!isExpanded) return;

        isExpanded = false;

//        width = FlowNodeModel.DEFAULT_WIDTH + MARGIN_ALL * 2;
//        height = FlowNodeModel.DEFAULT_HEIGHT+ MARGIN_ALL * 2;
        inFlowPoint = new Point(width/2,0);
        outFlowPoint = new Point(width/2,height);
        firePropertyChange(PROPERTY_NAME_LAYOUT_CHANGE,true,false);
    }
    public void addPropertyListener(){
    }
    public void processLayout() {

    }
    public boolean isExpanded() {
        return isExpanded;
    }
}
