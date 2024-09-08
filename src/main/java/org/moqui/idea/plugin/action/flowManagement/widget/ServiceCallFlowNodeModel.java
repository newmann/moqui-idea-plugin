package org.moqui.idea.plugin.action.flowManagement.widget;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.awt.*;

/**
 * 包含一个处理过程，同时对这个过程进行布局
 */
public class ServiceCallFlowNodeModel extends ExpandableFlowNodeModel {



    private SceneFlowNodeModel sceneFlowNodeModel;
//    private boolean isExpanded = false;
    public ServiceCallFlowNodeModel(String name, PsiElement targetPsiElement, SceneFlowNodeModel sceneFlowNodeModel){
        super(FlowNodeType.SERVICE_CALL,name,targetPsiElement);
        this.sceneFlowNodeModel = sceneFlowNodeModel;
        isExpanded = false;
        addPropertyListener();
    }

    @Override
    public void addPropertyListener() {
        if(sceneFlowNodeModel != null) {
            sceneFlowNodeModel.addPropertyChangeListener(propertyChangeListener);
        }
    }


    @Override
    public void expandContent(){
        if(sceneFlowNodeModel == null) {
            if(getName().isEmpty()) return;
            sceneFlowNodeModel = loadService(getName());
            addPropertyListener();
        }
        if(sceneFlowNodeModel != null) {
            width = sceneFlowNodeModel.getWidth() + MARGIN_ALL * 2;
            height = sceneFlowNodeModel.getHeight() + TITLE_HEIGHT + MARGIN_ALL * 2;//name的展示位置
            inFlowPoint = new Point(width / 2, 0);
            outFlowPoint = new Point(width / 2, height);
            super.expandContent();
        }
    }

    private SceneFlowNodeModel loadService(String serviceName){
        Project project = targetPsiElement == null ? null : targetPsiElement.getProject();
        if(project== null ) return null;
        Service service = ServiceUtils.getServiceByFullName(project,serviceName).orElse(null);
        if(service==null) {
            return null;
        }else {
            return FlowNodeModelBuilder.ofServiceModel(service).orElse(null);
        }
    }
    @Override
    public void closeContent(){
        width = FlowNodeModel.DEFAULT_WIDTH;
        height = FlowNodeModel.DEFAULT_HEIGHT;
        super.closeContent();
    }
    public SceneFlowNodeModel getSceneFlowNodeModel() {
        return sceneFlowNodeModel;
    }

}
