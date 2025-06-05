package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface HeaderField extends SubFields,AllWidgetsList,StandaloneFieldsList {
    
    public static final String TAG_NAME = "header-field";

//    //for AllWidgets
//    @NotNull
//    @SubTagList(FormSingle.TAG_NAME)
//    List<FormSingle> getFormSingle();
//    @NotNull
//    @SubTagList(FormList.TAG_NAME)
//    List<FormList> getFormList();

//    //for StandaloneFields
//    @NotNull
//    @SubTagList(Editable.TAG_NAME)
//    List<Editable> getEditableList();
//
//    @NotNull
//    @SubTagList(Label.TAG_NAME)
//    List<Label> getLabelList();
//
//    @NotNull
//    @SubTagList(Image.TAG_NAME)
//    List<Image> getImageList();
//    @NotNull
//    @SubTagList(Link.TAG_NAME)
//    List<Link> getLinkList();
    @NotNull
    @SubTagList(Set.TAG_NAME)
    List<Set> getSetList();


    @NotNull GenericAttributeValue<String> getTitle();
    @NotNull GenericAttributeValue<String> getShowOrderBy();
    @NotNull GenericAttributeValue<String> getContainerStyler();
}
