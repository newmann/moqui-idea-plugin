package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//@Stubbed
public interface Screen extends ScreenBase,SectionElements,AbstractLocation {

    String TAG_NAME = "screen";




    String ATTR_DEFAULT_SUBSCREEN = "default-subscreen";
    //for moqui-conf-3.xsd ScreenFacade define
//    @NotNull
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getLocation();

    @NotNull
    @Attribute(ATTR_DEFAULT_SUBSCREEN)
    GenericAttributeValue<String> getDefaultSubscreen();

    @NotNull
    @SubTagList(SubScreensItem.TAG_NAME)
    List<SubScreensItem> getSubScreensItemList();
    //for moqui-conf-3.xsd end

    @NotNull
    @SubTagList(MacroTemplate.TAG_NAME)
    List<MacroTemplate> getMacroTemplateList();

    @NotNull
    @SubTag(WebSettings.TAG_NAME)
    WebSettings getWebSettings();

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull
    @SubTag(AlwaysActions.TAG_NAME)
    AlwaysActions getAlwaysActions();
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
    @SubTag(PreActions.TAG_NAME)
    PreActions getPreActions();

}
