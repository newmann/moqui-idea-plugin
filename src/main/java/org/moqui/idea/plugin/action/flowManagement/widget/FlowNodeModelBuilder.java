package org.moqui.idea.plugin.action.flowManagement.widget;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Optional;

public class FlowNodeModelBuilder {
    public static final Logger LOG = Logger.getInstance(FlowNodeModelBuilder.class);
    public static Optional<FlowNodeModel> ofSetModel(@NotNull Set set){
        String name = "Set#" + MyDomUtils.getValueOrEmptyString(set.getField());
        return Optional.of(new FlowNodeModel(FlowNodeType.ASSIGNMENT,name,set.getXmlElement()));
    }
    public static Optional<FlowNodeModel> ofSetModel(@NotNull XmlTag setXmlTag){
//        PsiElement firstAttribute = setXmlTag.getAttributes()[0];
        return MyDomUtils.getLocalDomElementByPsiElement(setXmlTag, Set.class,false)
                .flatMap(FlowNodeModelBuilder::ofSetModel);
    }
    public static Optional<IfFlowNodeModel> ofIfModel(@NotNull If ifDomElement){
        String condition = MyDomUtils.getValueOrEmptyString(ifDomElement.getConditionAttr());
        SceneFlowNodeModel trueSceneFlowNodeModel = ifDomElement.getThen().getXmlTag() == null ? null : ofSceneFlowNodeModel(ifDomElement.getThen().getXmlTag()).orElse(null);
        SceneFlowNodeModel falseSceneFlowNodeModel = ifDomElement.getElse().getXmlTag() == null ? null : ofSceneFlowNodeModel(ifDomElement.getElse().getXmlTag()).orElse(null);
        if(trueSceneFlowNodeModel == null) {
            trueSceneFlowNodeModel = ifDomElement.getXmlTag() == null ? null : ofSceneFlowNodeModel(ifDomElement.getXmlTag()).orElse(null);
        }

        return Optional.of(new IfFlowNodeModel(condition,ifDomElement.getXmlTag(),trueSceneFlowNodeModel,falseSceneFlowNodeModel));
    }

    public static Optional<IfFlowNodeModel> ofIfModel(@NotNull XmlTag ifXmlTag){
        return MyDomUtils.getLocalDomElementByPsiElement(ifXmlTag,If.class,false)
                .flatMap(FlowNodeModelBuilder::ofIfModel);
    }
    public static Optional<SceneFlowNodeModel> ofServiceModel(@NotNull Service service){
        FlowNodeModel beginModel = StartEndFlowNodeModel.ofBeginFlowNodeModel(service.getInParameters().getXmlTag());
        FlowNodeModel endModel = StartEndFlowNodeModel.ofEndFlowNodeModel(service.getOutParameters().getXmlTag());
        XmlTag actionsXmlTag = service.getActions().getXmlTag();
        if(actionsXmlTag == null || actionsXmlTag.getChildren().length == 0) {
            FlowNodeModel.setRelation(beginModel,endModel);
        }else {
            Optional<SceneFlowNodeModel> sceneFlowNode = FlowNodeModelBuilder.ofSceneFlowNodeModel(actionsXmlTag);
            if(sceneFlowNode.isPresent()) {
                FlowNodeModel.setRelation(beginModel,sceneFlowNode.get());
                FlowNodeModel.setRelation(sceneFlowNode.get(),endModel);
            }else {
                FlowNodeModel.setRelation(beginModel,endModel);
            }
        }
        return Optional.of(new SceneFlowNodeModel(beginModel));

    }

    public static Optional<FlowNodeModel> ofServiceCallModel(@NotNull ServiceCall serviceCall){
        String serviceName = MyDomUtils.getValueOrEmptyString(serviceCall.getName());
        String[] contentArray = serviceName.split("#");
        if(ServiceUtils.STANDARD_CRUD_COMMANDER.contains(contentArray[0])) {
            return ofEntityActionModel(serviceName,serviceCall.getXmlTag());
        }else {
            return Optional.of(new ServiceCallFlowNodeModel(serviceName, serviceCall.getXmlTag(),null));
        }

    }

    public static Optional<FlowNodeModel> ofServiceCallModel(@NotNull XmlTag serviceCallXmlTag){
        return MyDomUtils.getLocalDomElementByPsiElement(serviceCallXmlTag,ServiceCall.class,false)
                .flatMap(FlowNodeModelBuilder::ofServiceCallModel);
    }
    public static Optional<LoopFlowNodeModel> ofLoopModel(@NotNull Iterate iterate){
        String list = MyDomUtils.getValueOrEmptyString(iterate.getList());
        String entry = MyDomUtils.getValueOrEmptyString(iterate.getEntry());
        String condition = Iterate.ATTR_LIST+":"+list+","+ Iterate.ATTR_ENTRY +":" + entry;
        SceneFlowNodeModel sceneFlowNodeModel = iterate.getXmlTag() == null ? null : FlowNodeModelBuilder.ofSceneFlowNodeModel(iterate.getXmlTag()).orElse(null);

        return Optional.of(new LoopFlowNodeModel(LoopFlowNodeModel.LoopFlowType.WHEN,condition,iterate.getXmlTag(), sceneFlowNodeModel));
    }
    public static Optional<LoopFlowNodeModel> ofLoopModel(@NotNull XmlTag iterateXmlTag){
        return MyDomUtils.getLocalDomElementByPsiElement(iterateXmlTag,Iterate.class,false)
                .flatMap(FlowNodeModelBuilder::ofLoopModel);
    }
    public static Optional<FlowNodeModel> ofEntityActionModel(@NotNull String entityAction,PsiElement targetElement){
        return Optional.of(new FlowNodeModel(FlowNodeType.ENTITY_ACTION, entityAction,targetElement));
    }
    public static Optional<FlowNodeModel> ofEntityActionModel(@NotNull XmlTag entityActionXmlTag){
        String actionName = entityActionXmlTag.getName();
        String entityName = entityActionXmlTag.getAttributeValue(AbstractEntityName.ATTR_ENTITY_NAME);
        return ofEntityActionModel(composeEntityAction(actionName,entityName),entityActionXmlTag);
    }
    public static Optional<FlowNodeModel> ofEntitySetTag(@NotNull XmlTag entitySetXmlTag){
        String actionName = entitySetXmlTag.getName();
        String entityName = entitySetXmlTag.getAttributeValue(EntitySet.ATTR_VALUE_Field);
        return ofEntityActionModel(composeEntityAction(actionName,entityName),entitySetXmlTag);
    }
    public static Optional<FlowNodeModel> ofEntityUpdateTag(@NotNull XmlTag entityUpdateXmlTag){
        String actionName = entityUpdateXmlTag.getName();
        String entityName = entityUpdateXmlTag.getAttributeValue(EntityUpdate.ATTR_VALUE_FIELD);
        return ofEntityActionModel(composeEntityAction(actionName,entityName),entityUpdateXmlTag);
    }
    public static String composeEntityAction(String actionName, String entityName){
        return (actionName == null ? "": actionName) +"#"+ (entityName == null ? "" : entityName);
    }
    public static Pair<String,String> decomposeEntityAction(String action){
        String [] actionArray = action.split("#");
        switch (actionArray.length){
            case 1->{return MutablePair.of(actionArray[0],null);}
            case 2->{return MutablePair.of(actionArray[0],actionArray[1]);}
            default ->{return MutablePair.of(null,null);}
        }
    }
    public static Optional<FlowNodeModel> ofReturnModel(@NotNull Return returnDomElement){
        String returnMessage = MyDomUtils.getValueOrEmptyString(returnDomElement.getMessage());
        return Optional.of(StartEndFlowNodeModel.ofEndFlowNodeModel(returnMessage,returnDomElement.getXmlTag()));
    }
    public static Optional<FlowNodeModel> ofReturnModel(@NotNull XmlTag returnXmlTag){
        return MyDomUtils.getLocalDomElementByPsiElement(returnXmlTag,Return.class,false)
                .flatMap(FlowNodeModelBuilder::ofReturnModel);
    }
    public static Optional<FlowNodeModel> ofScriptModel(@NotNull String entityAction){
        return Optional.empty();
    }
    public static Optional<FlowNodeModel> ofScriptModel(@NotNull XmlTag entityActionXmlTag){
        return Optional.empty();
    }
    public static Optional<SceneFlowNodeModel> ofSceneFlowNodeModel(@NotNull XmlTag parentXmlTag){
        if(parentXmlTag.getChildren().length == 0) {
            return Optional.empty();
        }else {
            FlowNodeModel model = null;
            FlowNodeModel parent = null;
            FlowNodeModel currentModel = null;
            for (PsiElement child : parentXmlTag.getChildren()) {
                if (child instanceof XmlTag xmlChild) {
                    switch (xmlChild.getName()) {
                        case Set.TAG_NAME ->currentModel = FlowNodeModelBuilder.ofSetModel(xmlChild).orElse(null);
                        case If.TAG_NAME ->currentModel = FlowNodeModelBuilder.ofIfModel(xmlChild).orElse(null);
                        case ServiceCall.TAG_NAME ->currentModel = FlowNodeModelBuilder.ofServiceCallModel(xmlChild).orElse(null);
                        case Iterate.TAG_NAME ->currentModel = FlowNodeModelBuilder.ofLoopModel(xmlChild).orElse(null);
                        case EntityFind.TAG_NAME,EntityFindCount.TAG_NAME,
                                EntityFindOne.TAG_NAME,
                                EntityDelete.TAG_NAME,EntityDeleteByCondition.TAG_NAME,
                                EntityMakeValue.TAG_NAME->currentModel = FlowNodeModelBuilder.ofEntityActionModel(xmlChild).orElse(null);
                        case EntitySet.TAG_NAME -> {
                            currentModel = ofEntitySetTag(xmlChild).orElse(null);
                        }
                        case EntityUpdate.TAG_NAME -> {
                            currentModel = ofEntityUpdateTag(xmlChild).orElse(null);
                        }

                        case Script.TAG_NAME->currentModel = FlowNodeModelBuilder.ofScriptModel(xmlChild).orElse(null);
                        case Return.TAG_NAME -> currentModel = FlowNodeModelBuilder.ofReturnModel(xmlChild).orElse(null);
                        default->{
                            LOG.warn("发现未处理的Tag类型：" + xmlChild.getName());
                            continue;
                        }
                    }
                    if (currentModel != null){
                        if(model == null) {
                            model = currentModel;
                            parent = currentModel;
                        }else{
                            //两个都是Set，跳过
                            if(!(currentModel.getType().equals(FlowNodeType.ASSIGNMENT)
                                && parent.getType().equals(FlowNodeType.ASSIGNMENT)
                            )){
                                FlowNodeModel.setRelation(parent, currentModel);
                                parent = currentModel;
                            }
                        }
                    }
                }
            }
            if(model == null) {
                return Optional.empty();
            }else {
                return Optional.of(new SceneFlowNodeModel(model));
            }
        }
    }
}
