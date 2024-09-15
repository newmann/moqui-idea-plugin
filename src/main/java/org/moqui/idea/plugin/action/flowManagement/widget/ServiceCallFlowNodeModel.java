package org.moqui.idea.plugin.action.flowManagement.widget;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.awt.*;

/**
 * 包含一个处理过程，同时对这个过程进行布局
 */
public class ServiceCallFlowNodeModel extends ExpandableFlowNodeModel {
    private static final Logger LOGGER = Logger.getInstance(ServiceCallFlowNodeModel.class);


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
    public void processLayout() {
            //如果是expand，需要对size进行计算
        if(isExpanded) {
            if(sceneFlowNodeModel != null) {
                sceneFlowNodeModel.processLayout();

                width = sceneFlowNodeModel.getWidth() + MARGIN_ALL * 2;
                height = sceneFlowNodeModel.getHeight() + TITLE_HEIGHT + MARGIN_ALL * 2;//name的展示位置
                inFlowPoint = new Point(width/2,0);
                outFlowPoint = new Point(width/2,height);

            }
        }else {
            width = FlowNodeModel.DEFAULT_WIDTH;
            height = FlowNodeModel.DEFAULT_HEIGHT;
            inFlowPoint = new Point(width/2,0);
            outFlowPoint = new Point(width/2,height);
        }
    }

    @Override
    public void expandContent(){
        if(isExpanded) return;
        if(sceneFlowNodeModel == null) {
            if(getName().isEmpty()) return;
            sceneFlowNodeModel = loadService(getName());
            addPropertyListener();
        }
        if(sceneFlowNodeModel != null) {
//            inFlowPoint = new Point(width / 2, 0);
//            outFlowPoint = new Point(width / 2, height);
//            LOGGER.warn("in ServiceCallFlowNodeModel expandContent");
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
        if(!isExpanded) return;
//        LOGGER.warn("in ServiceCallFlowNodeModel closeContent");
        super.closeContent();
    }
    public SceneFlowNodeModel getSceneFlowNodeModel() {
        return sceneFlowNodeModel;
    }

}
