package org.moqui.idea.plugin.util;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.icon.MyIcons;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByConvertContext;
import static org.moqui.idea.plugin.util.MyDomUtils.getSubTagList;


public final class ScreenUtils {
    private ScreenUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isScreenFile(@Nullable PsiFile file){
        return MyDomUtils.isSpecialXmlFile(file, Screen.TAG_NAME);
    }
    public static Icon getNagavitorToScreenIcon() {
        return MyIcons.NAVIGATE_TO_SCREEN;
    }
    public static String getNagavitorToEntityToolTips() {
        return "Navigating to Screen definition";
    }

    /**
     * 判断url字符串是否为Transition，
     * @param url
     * @return
     */
    public static boolean isValidTransitionFormat(@NotNull String url){
        if(url.equals(".")) return false;

        if(url.contains("..")) return false;

        if(url.contains("/")) return false;

        if(url.contains("$")) return false;

        return true;
    }
    /**
     * 根据当前位置找到所在screen的所有可用的Transition
     *
     * @param context
     * @return
     */
    public static List<AbstractTransition> getTransitionList(ConvertContext context) {

        List<AbstractTransition> result = new ArrayList<AbstractTransition>();


        Screen screen = ScreenUtils.getCurrentScreen(context).orElse(null);
        if(screen != null){
            result.addAll(screen.getTransitionList());
            result.addAll(screen.getTransitionIncludeList());
        }
        return result;
    }
    /**
     * 根据当前位置和名称对应的Transition
     * @param transitionName
     * @param context
     * @return
     */
    public static Optional<AbstractTransition> getTransitionByName(String transitionName, ConvertContext context) {
        List<AbstractTransition> transitionList = getTransitionList(context);
        return transitionList.stream().filter(
                item->{
                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    return str.equals(transitionName);
                }
        ).findFirst();

    }
    /**
     * 根据location，找到对应的文件
     * location格式为：component://xxxx
     *
     * @param psiElement
     * @param location
     * @return
     */
    public static List<PsiElement> getRelatedScreenFile(@NotNull PsiElement psiElement, @NotNull String location) {
        List<PsiElement> resultList = new ArrayList<>();
        final Project project = psiElement.getProject();
        final String componentPreStr = "component://";
        String relativePathName;

        if(location.indexOf(componentPreStr)==0) {
            relativePathName = location.substring(componentPreStr.length());
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

        Optional<DomFileElement<Screen>> optionalScreenDomFileElement = findScreenFileByLocation(project,location);

        if(optionalScreenDomFileElement.isPresent()) {
            Optional<Transition> optionalTransition = findTransitionElementByName(optionalScreenDomFileElement.get(),name);
            if(optionalTransition.isPresent()) {
                resultList.add(optionalTransition.get().getXmlElement());
            }

//            for(Transition transition: optionalScreenDomFileElement.get().getRootElement().getTransitionList()) {
//                if(transition.getName().getValue().equals(name)) {
//                    resultList.add(transition.getXmlElement());
//                }
//            }
        }


//        final String componentPreStr = "component://";
//        String relativePathName;
//
//        if(location.indexOf(componentPreStr)==0) {
//            relativePathName = location.substring(componentPreStr.length());
//        } else {
//            relativePathName = location;
//        }
//
//        List<DomFileElement<Screen>> fileElementList  = DomUtils.findDomFileElementsByRootClass(project, Screen.class);
//        for(DomFileElement<Screen> fileElement : fileElementList) {
//            if (fileElement.getFile().getVirtualFile().getPath().contains(relativePathName)){
//
//                for(Transition transition: fileElement.getRootElement().getTransitionList()) {
//                    if(transition.getName().getValue().equals(name)) {
//                        resultList.add(transition.getXmlElement());
//                    }
//                }
//
//
//            }
//        }

        return resultList;
    }

    /**
     * 根據location，找到对应的DomFileElement
     * @param location
     * @return
     */
    public static  Optional<DomFileElement<Screen>> findScreenFileByLocation(@NotNull Project project,@NotNull String location){

        final String componentPreStr = "component://";
        String relativePathName;

        if(location.indexOf(componentPreStr)==0) {
            relativePathName = location.substring(componentPreStr.length());
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

    public static Optional<Transition> findTransitionElementByName(@NotNull DomFileElement<Screen> fileElement, @NotNull String name){
        for(Transition transition: fileElement.getRootElement().getTransitionList()) {
            if(transition.getName().getXmlAttributeValue().getValue().equals(name)) {
                return Optional.of(transition);
            }
        }
        return Optional.empty();
    }
    public static List<Transition> getTransitionListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        return fileElement.getRootElement().getTransitionList();
    }

    /**
     * 获取某个Screen中所有的FormSingle定义
     * @param fileElement
     * @return
     */
    public static List<FormSingle> getFormSingleListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        Widgets widgets = fileElement.getRootElement().getWidgets();
        return DomUtil.getChildrenOf(widgets,FormSingle.class);
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
//            DomFileElement<Screen> screen = DomManager.getDomManager(file.getProject())
//                    .getFileElement(xmlFile, Screen.class);
//            if(screen != null) {
//                return getFormSingleListFromScreenFile(screen);
//            }
        }

        return formSingleList;

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
        return formSingleList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formListName);
        }).findFirst();
    }
    /**
     * 用在Inspection中
     * 根据指定的属性来进行location位置的验证
     *
     * @param transitionInclude
     * @param holder
     * @param helper
     */
    public static void inspectTransitionInclude(@NotNull TransitionInclude transitionInclude, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
//        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
//        if (xmlAttributeValue == null) { return;}
        final Project project =transitionInclude.getXmlElement().getProject();

        final String name = transitionInclude.getName().getValue();
        final String location = transitionInclude.getLocation().getValue();

        if (MyStringUtils.isEmpty(location)) {
            holder.createProblem(transitionInclude, HighlightSeverity.ERROR,"Transition location is not defined");
            return;

        }
        if (MyStringUtils.isEmpty(location)) {
            holder.createProblem(transitionInclude, HighlightSeverity.ERROR,"transition name is not defined");
            return;
        }

        final Optional<DomFileElement<Screen>> optionalScreenDomFileElement = findScreenFileByLocation(project,location);
        if(optionalScreenDomFileElement.isEmpty()) {
            holder.createProblem(transitionInclude.getLocation(), HighlightSeverity.ERROR,"Transition location is not found");
//                    TextRange.from(1,transitionInclude.getLocation().getXmlAttributeValue().getValueTextRange().getLength()));

        }else {
            final Optional<Transition> optionalTransition = findTransitionElementByName(optionalScreenDomFileElement.get(),name);

            if(optionalTransition.isEmpty()) {
                holder.createProblem(transitionInclude.getName(), HighlightSeverity.ERROR,"Transition name is not found");
//                        TextRange.from(1,transitionInclude.getName().getXmlAttributeValue().getValueTextRange().getLength()));

            }

        }

    }

    /**
     * 获取FormSingle的Field定义，
     * @param abstractForm
     * @return
     */
    public static List<Field> getFieldListFromForm(@NotNull AbstractForm abstractForm){
        List<Field> result = new ArrayList<>();
        final String extendsStr = MyDomUtils.getXmlAttributeValueString(abstractForm.getExtends()).orElse(MyStringUtils.EMPTY_STRING);
        if(MyStringUtils.isNotEmpty(extendsStr)){
            //根据extends，找到FormSingle的定义
            final int poundIndex = extendsStr.indexOf("#");
            if(poundIndex>=0) {
                final String location = extendsStr.substring(0, poundIndex);
                final String extendFormName = extendsStr.substring(poundIndex+1);

                PsiFile psiFile = MyDomUtils.getFileFromLocation(abstractForm.getXmlElement().getProject(),location).orElse(null);
                if(psiFile != null) {
                    AbstractForm extendForm;
                    if(abstractForm instanceof FormSingle) {
                        extendForm = getFormSingleFromScreenFileByName(psiFile, extendFormName).orElse(null);
                    }else {
                        extendForm = getFormListFromScreenFileByName(psiFile, extendFormName).orElse(null);
                    }

                    if(extendForm != null) {
                        //嵌套调用
                        result.addAll(getFieldListFromForm(extendForm));
                    }

                }

            }

        }

        result.addAll(abstractForm.getFieldList());
        return result;
    }

    public static Optional<Screen> getCurrentScreen(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Screen.class);

    }
    public static Optional<FormSingle> getCurrentFormSingle(ConvertContext context){
        return getLocalDomElementByConvertContext(context,FormSingle.class);

    }
    public static Optional<FormList> getCurrentFormList(ConvertContext context){
        return getLocalDomElementByConvertContext(context,FormList.class);

    }
}
