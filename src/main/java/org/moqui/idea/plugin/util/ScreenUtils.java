package org.moqui.idea.plugin.util;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
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
