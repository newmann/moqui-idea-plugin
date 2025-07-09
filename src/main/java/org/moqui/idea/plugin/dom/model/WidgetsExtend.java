package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface WidgetsExtend extends WidgetElementsList {
    
    String TAG_NAME = "widgets-extend";


    String ATTR_NAME = "name";
    
    String ATTR_WHERE = "where";

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_WHERE)
    GenericAttributeValue<String> getWhere();

////for AllWidgets
//    @NotNull
//    @SubTagList(FormSingle.TAG_NAME)
//    List<FormSingle> getFormSingleList();
//
//    @NotNull
//    @SubTagList(FormList.TAG_NAME)
//    List<FormList> getFormListList();

//    @NotNull
//    @SubTagList(StandaloneFields.TAG_NAME)
//    List<StandaloneFields> getStandaloneFieldsList();

//    @NotNull
//    @SubTagList(Widgets.TAG_NAME)
//    List<Widgets> getWidgetsList();
//
//    @NotNull
//    @SubTagList(FailWidgets.TAG_NAME)
//    List<FailWidgets> getFailWidgetsList();
}
