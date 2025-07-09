package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ScreenExtend extends ScreenBase {
    
    String TAG_NAME = "screen-extend";


//    @NotNull
//    @Attribute("xmlns:xsi")
//    GenericAttributeValue<String> getXmlnsXsi();
//
//    @NotNull
//    @Attribute("xsi:noNamespaceSchemaLocation")
//    GenericAttributeValue<String> getXsiNoNamespaceSchemaLocation();

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull
    @SubTagList(Transition.TAG_NAME)
    List<Transition> getTransitionList();

    @NotNull
    @SubTagList(TransitionInclude.TAG_NAME)
    List<TransitionInclude> getTransitionIncludeList();

    @NotNull
    @SubTag(SubScreens.TAG_NAME)
    SubScreens getSubScreens();
    @NotNull
    @SubTagList(ActionsExtend.TAG_NAME)
    List<ActionsExtend> getActionsExtendList();

    @NotNull
    @SubTagList(WidgetsExtend.TAG_NAME)
    List<WidgetsExtend> getWidgetsExtendList();

    @NotNull
    @SubTagList(FormSingle.TAG_NAME)
    List<FormSingle> getFormSingleList();

    @NotNull
    @SubTagList(FormList.TAG_NAME)
    List<FormList> getFormListList();
    @NotNull
    @SubTagList(Section.TAG_NAME)
    List<Section> getsSectionList();

    @NotNull
    @SubTagList(SectionIterate.TAG_NAME)
    List<SectionIterate> getSectionIterateList();


}
