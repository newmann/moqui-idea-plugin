package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WidgetTemplate extends AllWidgets,SubFields,StandaloneFieldsList {
    public static final String TAG_NAME = "widget-template";

    public static final String ATTR_NAME="name";

    @NotNull
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();


//    @NotNull
//    @SubTag(SubFields.TAG_NAME)
//    SubFields getSubFields();
//
//    @NotNull
//    @SubTag(AllWidgets.TAG_NAME)
//    AllWidgets getAllWidgets();

    //for AllWidgets

//    @NotNull
//    @SubTag(FormSingle.TAG_NAME)
//    FormSingle getFormSingle();
//    @NotNull
//    @SubTag(FormList.TAG_NAME)
//    FormList getFormList();

//    @NotNull
//    @SubTagList(StandaloneFields.TAG_NAME)
//    List<StandaloneFields> getStandaloneFieldsList();


}
