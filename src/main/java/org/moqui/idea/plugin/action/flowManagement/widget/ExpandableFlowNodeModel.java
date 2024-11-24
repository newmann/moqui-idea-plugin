package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ExpandableFlowNodeModel extends FlowNodeModel {
    private static final Logger LOGGER = Logger.getInstance(ExpandableFlowNodeModel.class);

    protected boolean isExpanded = false;
    protected final PropertyChangeListener propertyChangeListener = propertyChangeEvent -> {
        if(propertyChangeEvent.getPropertyName().equals(PROPERTY_NAME_LAYOUT_CHANGE)) {
//                LOGGER.warn("ExpandableFlowNodeModel "+ getType() +":"+ getName() + " call processLayout ");
            processLayout();
            firePropertyChange(propertyChangeEvent);
        }
    };

    public ExpandableFlowNodeModel(FlowNodeType type, String name, PsiElement targetPsiElement) {
        this(type,name,targetPsiElement,"");
    }

    public ExpandableFlowNodeModel(FlowNodeType type, String name,PsiElement targetPsiElement, String toolTips ) {
        super(type, name,targetPsiElement, toolTips);
    }

    public void expandContent(){
        isExpanded= true;
        processLayout();
        firePropertyChange(PROPERTY_NAME_LAYOUT_CHANGE,false,true);
    }
    public void closeContent(){
        isExpanded = false;
        processLayout();
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
