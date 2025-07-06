package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.moqui.idea.plugin.util.MyDomUtils.*;


public final class ScreenUtils{
    private static final Logger LOGGER = Logger.getInstance(ScreenUtils.class);

    private ScreenUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isScreenFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Screen.TAG_NAME,Screen.ATTR_NoNamespaceSchemaLocation,Screen.VALULE_NoNamespaceSchemaLocation);
    }
    public static Icon getNavigatorToScreenIcon() {
        return MyIcons.NavigateToScreen; //MyIcons.NAVIGATE_TO_SCREEN;
    }
    public static String getNavigatorToEntityToolTips() {
        return "Navigating to Screen definition";
    }


    public static Boolean isTabScreenBySubScreenItems(@NotNull SubScreensItem subScreensItem){
        if(subScreensItem.getParent() instanceof Screen screen) {
            List<SubScreensPanel> subScreensPanelList = screen.getWidgets().getSubScreensPanelList();
            if(subScreensPanelList.isEmpty()) return false;

            SubScreensPanel subScreensPanel = screen.getWidgets().getSubScreensPanelList().get(0);

            if(subScreensPanel == null) return false;
            return MyDomUtils.getValueOrEmptyString(subScreensPanel.getType()).equals("tab");
        }else {
            return false;
        }
    }
    /**
     * 根据当前位置找到所在screen的所有可用的Transition
     */
    public static List<AbstractTransition> getAbstractTransitionListFromConvertContext(ConvertContext context) {

        Screen screen = getCurrentScreen(context).orElse(null);
        return getAbstractTransitionListFromScreen(screen);
    }
    public static List<AbstractTransition> getAbstractTransitionListFromScreen(@Nullable Screen screen) {

        List<AbstractTransition> result = new ArrayList<>();

        if(screen != null){
            result.addAll(screen.getTransitionList());
            result.addAll(screen.getTransitionIncludeList());
        }
        return result;
    }

    /**
     * 根据当前位置找到所在screen的所有可用的AbstractTransition
     */
    public static List<AbstractTransition> getAbstractTransitionListFromPsiElement(PsiElement psiElement) {

        Screen screen = getCurrentScreen(psiElement).orElse(null);
        return getAbstractTransitionListFromScreen(screen);
    }

    /**
     * 根据当前位置找到所在screen的所有可用的SubScreensItem
     */
    public static List<SubScreensItem> getSubScreensItemList(ConvertContext context) {
        return getSubScreensItemList(getCurrentScreen(context).orElse(null));
    }
    /**
     * 根据当前位置找到所在screen的所有可用的SubScreensItem
     */
    public static List<SubScreensItem> getSubScreensItemList(PsiElement element) {

        return getSubScreensItemList(getCurrentScreen(element).orElse(null));
    }
    /**
     * 找到screen的所有可用的SubScreensItem
     */
    public static List<SubScreensItem> getSubScreensItemList(Screen screen) {

        List<SubScreensItem> result = new ArrayList<>();
        if(screen != null){
            result.addAll(screen.getSubScreensItemList());
            result.addAll(screen.getSubScreens().getSubScreensItemList());
        }
        return result;
    }
    public static List<String> getSubScreensItemNameList(ConvertContext context) {
        return getSubScreensItemList(context).stream()
                .map(SubScreensItem::getName)
                .map(MyDomUtils::getValueOrEmptyString)
                .toList();
    }

    public static Optional<SubScreensItem> getSubScreensItemByName(String itemName,ConvertContext context) {
        List<SubScreensItem> itemList = getSubScreensItemList(context);
        return itemList.stream().filter(
                item -> {
                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    return str.equals(itemName);
                }
        ).findFirst();
    }
    /**
     * 根据当前位置和名称对应的Transition
     */
    public static Optional<AbstractTransition> getAbstractTransitionFromConvertContextByName(String transitionName, ConvertContext context) {
        List<AbstractTransition> transitionList = getAbstractTransitionListFromConvertContext(context);
        return getAbstractTransitionFromListByName(transitionList,transitionName);
    }

    public static Optional<AbstractTransition> getAbstractTransitionFromListByName(@NotNull List<AbstractTransition> abstractTransitionList,@NotNull String transitionName){
        return abstractTransitionList.stream().filter(
                item->{
                    String str = MyDomUtils.getValueOrEmptyString(item.getName());
                    return str.equals(transitionName);
                }
        ).findFirst();

    }
    /**
     * 根据location，找到对应的文件
     * location格式为：component://xxxx
     */
    public static List<PsiElement> getRelatedScreenFile(@NotNull PsiElement psiElement, @NotNull String location) {
        List<PsiElement> resultList = new ArrayList<>();
        final Project project = psiElement.getProject();
        String relativePathName;

        if(location.indexOf(MyStringUtils.COMPONENT_LOCATION_PREFIX)==0) {
            relativePathName = location.substring(MyStringUtils.COMPONENT_LOCATION_PREFIX.length());
        } else {
            relativePathName = location;
        }

        List<DomFileElement<Screen>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Screen.class);
        for(DomFileElement<Screen> fileElement : fileElementList) {
            if (fileElement.getFile().getVirtualFile().getPath().contains(relativePathName)){
                resultList.add(fileElement.getRootTag());

            }
        }

        return resultList;
    }

    public static List<PsiElement> getRelatedTransitionInclude(@NotNull PsiElement psiElement,
                                                               @NotNull String name,
                                                               @NotNull String location) {
        List<PsiElement> resultList = new ArrayList<>();
        final Project project = psiElement.getProject();

        Optional<DomFileElement<Screen>> optionalScreenDomFileElement = getScreenFileByLocation(project,location);

        if(optionalScreenDomFileElement.isPresent()) {
            Optional<Transition> optionalTransition = getTransitionFromDomFileElementByName(optionalScreenDomFileElement.get(),name);
            optionalTransition.ifPresent(transition -> resultList.add(transition.getXmlElement()));

        }


        return resultList;
    }

    /**
     * 根據location，找到对应的DomFileElement
     */
    public static  Optional<DomFileElement<Screen>> getScreenFileByLocation(@NotNull Project project, @NotNull String location){


        String relativePathName;

        if(location.indexOf(MyStringUtils.COMPONENT_LOCATION_PREFIX)==0) {
            relativePathName = location.substring(MyStringUtils.COMPONENT_LOCATION_PREFIX.length());
        } else {
            relativePathName = location;
        }

        List<DomFileElement<Screen>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Screen.class);
        for(DomFileElement<Screen> fileElement : fileElementList) {
            if (fileElement.getFile().getVirtualFile().getPath().contains(relativePathName)){
                return Optional.of(fileElement);

            }
        }
        return Optional.empty();
    }

    public static Optional<Transition> getTransitionFromDomFileElementByName(@NotNull DomFileElement<Screen> fileElement, @NotNull String name){
        return fileElement.getRootElement().getTransitionList().stream()
                .filter(transition -> MyDomUtils.getValueOrEmptyString(transition.getName()).equals(name))
                .findFirst();
    }
    public static List<Transition> getTransitionListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        return fileElement.getRootElement().getTransitionList();
    }

    /**
     * 获取某个Screen中所有的FormSingle定义
     */
    public static List<FormSingle> getFormSingleListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        Widgets widgets = fileElement.getRootElement().getWidgets();
        return DomUtil.getChildrenOf(widgets,FormSingle.class);
    }

    /**
     * 包括section和section-iterate
     * @param fileElement screen的DomFileElement
     * @return List<Section></Section>
     */
    public static List<Section> getSectionListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        Widgets widgets = fileElement.getRootElement().getWidgets();
        List<Section> sectionList = new ArrayList<>();

        collectSections(widgets, sectionList);
//        widgets.accept(new DomElementVisitor() {
//            @Override
//            public void visitDomElement(DomElement domElement) {
//                domElement.acceptChildren(this); //循环检查下级的DomElement
//            }
//            public void visitSection(Section section){
//                sectionList.add(section);
//            }
//        });
        return sectionList;
    }
    private static void collectSections(Widgets widgets, List<Section> result) {
        result.addAll(widgets.getSectionList());
        result.addAll(widgets.getSectionIterateList());
        for(Section section: widgets.getSectionList()) {
            collectSections(section.getWidgets(),result);
        }
    }
    public static Optional<Section> getSectionFromScreenFileByName(@NotNull DomFileElement<Screen> fileElement,@NotNull String sectionName){

        return getSectionFromSectionList(getSectionListFromScreenFile(fileElement),sectionName);
    }
    public static Optional<Section> getSectionFromSectionList(@NotNull List<Section> sectionList,@NotNull String sectionName) {
        return sectionList.stream()
                .filter(section -> MyDomUtils.getValueOrEmptyString(section.getName()).equals(sectionName))
                .findFirst();
    }

    /**
     * 用于LocationConverter
     * @param context ConvertContext
     * @return List<String>
     */
    public static List<String> getFormSingleNameListByConvertContext(@NotNull ConvertContext context) {
        List<FormSingle> formSingleList = getFormSingleListFromScreenFile(context.getFile().getContainingFile());
        return formSingleList.stream().map(FormSingle::getName)
                .map(GenericValue::getValue)
                .toList();
    }
    public static List<String> getFormListNameListByConvertContext(@NotNull ConvertContext context) {
        List<FormList> formListList = getFormListListFromScreenFile(context.getFile().getContainingFile());
        return formListList.stream().map(FormList::getName)
                .map(GenericValue::getValue)
                .toList();
    }

    public static List<FormSingle> getFormSingleListFromScreenFile(PsiFile file){
        List<FormSingle> formSingleList = new ArrayList<>();
        List<XmlTag> formSingleTagList = new ArrayList<>();

        if(file instanceof XmlFile xmlFile) {
            XmlTag rootTag = xmlFile.getRootTag();
            if (rootTag!=null) {
                formSingleTagList = getSubTagList(rootTag,FormSingle.TAG_NAME);
            }
            for(XmlTag child : formSingleTagList) {
                DomElement domElement = DomUtil.getDomElement(child);
                if(domElement instanceof FormSingle formSingle) {
                    formSingleList.add(formSingle);
                }
            }
        }

        return formSingleList;

    }

    /**
     * 获取文件中所有Form定义，返回AbstractForm
     * @param file 所在文件
     * @return List<AbstractForm>
     */
    public static List<AbstractForm> getAbstractFormFromScreenFile(PsiFile file){
        List<AbstractForm> abstractFormList = new ArrayList<>();
        List<XmlTag> formTagList = new ArrayList<>();

        if(file instanceof XmlFile xmlFile) {
            XmlTag rootTag = xmlFile.getRootTag();
            if (rootTag!=null) {
                formTagList.addAll(getSubTagList(rootTag,FormSingle.TAG_NAME));
                formTagList.addAll(getSubTagList(rootTag,FormList.TAG_NAME));
            }

            for(XmlTag child : formTagList) {
                DomElement domElement = DomUtil.getDomElement(child);
                if(domElement instanceof AbstractForm form) {
                    abstractFormList.add(form);
                }
            }
        }

        return abstractFormList;

    }
    public static Optional<AbstractForm> getAbstractFormFromScreenFileByName(@NotNull PsiFile file,@NotNull String formName){
        List<AbstractForm> abstractFormList = getAbstractFormFromScreenFile(file);
        return abstractFormList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formName);
        }).findFirst();
    }


    public static Optional<FormSingle> getFormSingleFromScreenFileByName(@NotNull DomFileElement<Screen> fileElement,@NotNull String formSingleName){
        List<FormSingle> formSingleList = getFormSingleListFromScreenFile(fileElement);
        return formSingleList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formSingleName);
        }).findFirst();
    }
    public static Optional<FormSingle> getFormSingleFromScreenFileByName(@NotNull PsiFile file,@NotNull String formSingleName){
        List<FormSingle> formSingleList = getFormSingleListFromScreenFile(file);
        return formSingleList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formSingleName);
        }).findFirst();
    }

    public static List<FormList> getFormListListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        Widgets widgets = fileElement.getRootElement().getWidgets();
        return DomUtil.getChildrenOfType(widgets,FormList.class);
    }
    public static Optional<FormList> getFormListFromScreenFileByName(@NotNull DomFileElement<Screen> fileElement,@NotNull String formListName){
        List<FormList> formListList = getFormListListFromScreenFile(fileElement);
        return formListList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formListName);
        }).findFirst();
    }
    public static List<FormList> getFormListListFromScreenFile(PsiFile file){

        List<FormList> formListList = new ArrayList<>();
        List<XmlTag> formListTagList = new ArrayList<>();

        if(file instanceof XmlFile xmlFile) {
            XmlTag rootTag = xmlFile.getRootTag();
            if (rootTag!=null) {
                formListTagList = getSubTagList(rootTag,FormList.TAG_NAME);
            }
            for(XmlTag child : formListTagList) {
                DomElement domElement = DomUtil.getDomElement(child);
                if(domElement instanceof FormList formList) {
                    formListList.add(formList);
                }
            }
        }

        return formListList;

    }
    public static Optional<FormList> getFormListFromScreenFileByName(@NotNull PsiFile file,@NotNull String formListName){
        List<FormList> formSingleList = getFormListListFromScreenFile(file);
        return formSingleList.stream().filter(item->MyDomUtils.getValueOrEmptyString(item.getName()).equals(formListName))
                .findFirst();
    }
    /**
     * 用在Inspection中
     * 根据指定的属性来进行location位置的验证
     */
    public static void inspectTransitionInclude(@NotNull TransitionInclude transitionInclude, @NotNull DomElementAnnotationHolder holder) {
        XmlElement xmlElement = transitionInclude.getXmlElement();
        if(xmlElement == null) return;

        final Project project =xmlElement.getProject();

        final String name = MyDomUtils.getValueOrEmptyString(transitionInclude.getName());
        final String location = MyDomUtils.getValueOrEmptyString(transitionInclude.getLocation());

        if (MyStringUtils.isEmpty(location)) {
            holder.createProblem(transitionInclude, HighlightSeverity.ERROR,"Transition location is not defined");
            return;

        }

        final Optional<DomFileElement<Screen>> optionalScreenDomFileElement = getScreenFileByLocation(project,location);
        if(optionalScreenDomFileElement.isEmpty()) {
            holder.createProblem(transitionInclude.getLocation(), HighlightSeverity.ERROR,"Transition location is not found");
//                    TextRange.from(1,transitionInclude.getLocation().getXmlAttributeValue().getValueTextRange().getLength()));

        }else {
            final Optional<Transition> optionalTransition = getTransitionFromDomFileElementByName(optionalScreenDomFileElement.get(),name);

            if(optionalTransition.isEmpty()) {
                holder.createProblem(transitionInclude.getName(), HighlightSeverity.ERROR,"Transition name is not found");
//                        TextRange.from(1,transitionInclude.getName().getXmlAttributeValue().getValueTextRange().getLength()));

            }

        }

    }
    public static void inspectTransitionInclude(@NotNull TransitionInclude transitionInclude, @NotNull AnnotationHolder holder) {
        if(transitionInclude.getXmlElement() == null) return;

        final Project project =transitionInclude.getXmlElement().getProject();
        String name ;
        XmlAttributeValue xmlAttributeName = transitionInclude.getName().getXmlAttributeValue();
        if(xmlAttributeName == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Transition name is not defined")
                    .range(transitionInclude.getXmlElement().getTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
            return;
        }else {
            name = xmlAttributeName.getValue();
            if (MyStringUtils.isEmpty(name)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Transition name is not defined")
                        .range(xmlAttributeName.getTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }

        }
        final String location;
        XmlAttributeValue xmlAttributeLocation = transitionInclude.getLocation().getXmlAttributeValue();
        if(xmlAttributeLocation == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Transition location is not defined")
                    .range(transitionInclude.getXmlElement().getTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
        }else {
            location = xmlAttributeLocation.getValue();
            if (MyStringUtils.isEmpty(location)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Transition location is not defined")
                        .range(xmlAttributeLocation.getTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }else {
                final Optional<DomFileElement<Screen>> optionalScreenDomFileElement = getScreenFileByLocation(project,location);
                if(optionalScreenDomFileElement.isEmpty()) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Transition location is not found")
                            .range(xmlAttributeLocation.getTextRange())
                            .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                            .create();
//                    TextRange.from(1,transitionInclude.getLocation().getXmlAttributeValue().getValueTextRange().getLength()));

                }else if(!MyStringUtils.isEmpty(name)) {
                    final Optional<Transition> optionalTransition = getTransitionFromDomFileElementByName(optionalScreenDomFileElement.get(),name);

                    if(optionalTransition.isEmpty()) {
                        holder.newAnnotation(HighlightSeverity.ERROR, "Transition name is not found")
                                .range(xmlAttributeName.getTextRange())
                                .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                                .create();

//                        TextRange.from(1,transitionInclude.getName().getXmlAttributeValue().getValueTextRange().getLength()));

                    }

                }

            }

        }



    }

    /**
     * 获取FormSingle的Field定义，
     * todo DBForm如何支持？
     */
    public static List<AbstractField> getFieldListFromForm(@NotNull AbstractForm abstractForm){
        List<AbstractField> result = new ArrayList<>();
        final String extendsStr = MyDomUtils.getXmlAttributeValueString(abstractForm.getExtends()).orElse(MyStringUtils.EMPTY_STRING);
        if(MyStringUtils.isNotEmpty(extendsStr)){
            //根据extends，找到FormSingle的定义，如果没有#，就是在本文件中找
            final int poundIndex = extendsStr.indexOf("#");
            final PsiFile psiFile;
            final String extendFormName;
            XmlElement abstractFormXmlElement = abstractForm.getXmlElement();
            if(abstractFormXmlElement == null) return  result;

            if(poundIndex>=0) {
                final String location = extendsStr.substring(0, poundIndex);
                extendFormName = extendsStr.substring(poundIndex + 1);

                psiFile = LocationUtils.getFileFromLocation(abstractFormXmlElement.getProject(), location).orElse(null);
            }else {
                extendFormName = extendsStr;
                psiFile = abstractFormXmlElement.getContainingFile();
            }

            if(psiFile != null) {
                getAbstractFormFromScreenFileByName(psiFile, extendFormName)
                        .ifPresent(extendForm -> result.addAll(getFieldListFromForm(extendForm)));

            }


        }

        result.addAll(abstractForm.getFieldList());
        //添加AutoFieldFromService
        if(abstractForm instanceof FormSingle formSingle) {
            Project project = formSingle.getXmlElement().getProject();
            for(AutoFieldsService fieldsService: formSingle.getAutoFieldsServiceList()) {
                String serviceName = MyDomUtils.getValueOrEmptyString(fieldsService.getServiceName());
                if(MyStringUtils.isNotEmpty(serviceName)) {
                    ServiceUtils.getServiceInParamterList(project,serviceName).forEach(item->{
                        result.add(item.getAbstractField());
                    });
                }
            }
            for(AutoFieldsEntity fieldsEntity: formSingle.getAutoFieldsEntityList()) {
                String entityName = MyDomUtils.getValueOrEmptyString(fieldsEntity.getEntityName());
                if(MyStringUtils.isNotEmpty(entityName)) {
                    EntityUtils.getEntityOrViewEntityFields(project,entityName).forEach(item->{
                        result.add(item.getAbstractField());
                    });
                }
            }
        }
        return result;
    }

    public static Optional<Screen> getCurrentScreen(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Screen.class);

    }
    public static Optional<Screen> getCurrentScreen(PsiElement element){
        return getLocalDomElementByPsiElement(element,Screen.class);
    }
    public static Optional<FormSingle> getCurrentFormSingle(ConvertContext context){
        return getLocalDomElementByConvertContext(context,FormSingle.class);

    }
    public static Optional<AbstractForm> getCurrentAbstractForm(ConvertContext context){
        return getLocalDomElementByConvertContext(context,AbstractForm.class);
    }

    public static Optional<FormList> getCurrentFormList(ConvertContext context){
        return getLocalDomElementByConvertContext(context,FormList.class);

    }

    /**
     * 找到SubScreensItem的location对应Screen文件
     * @param subScreensItem 待处理的SubScreensItem
     * @return Optional<Screen>
     */
    public static Optional<Screen> getSubScreensItemLocationScreen(@NotNull Project project, @NotNull SubScreensItem subScreensItem) {

        PsiFile parentFile = LocationUtils.getFileFromLocation(project, MyDomUtils.getValueOrEmptyString(subScreensItem.getLocation())).orElse(null);
        if(parentFile == null){
            return Optional.empty();
        }else {
            return Optional.of(MyDomUtils.convertPsiFileToDomFile(parentFile,Screen.class).getRootElement());
        }
    }

}
