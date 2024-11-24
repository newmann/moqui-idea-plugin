package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FormListDefaultField extends SubFields,AllWidgetsList,StandaloneFieldsList {




//
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
//
//    //for AllWidgets
//    @NotNull
//    @SubTagList(FormSingle.TAG_NAME)
//    List<FormSingle> getFormSingle();
//    @NotNull
//    @SubTagList(FormList.TAG_NAME)
//    List<FormList> getFormList();


    @NotNull
    @SubTagList(Set.TAG_NAME)
    List<Set> getSetList();

    @NotNull
    @SubTag(VisibleWhen.TAG_NAME)
    VisibleWhen getVisibleWhen();


    @NotNull GenericAttributeValue<String> getTitle();
    @NotNull GenericAttributeValue<String> getTooltip();
    @NotNull GenericAttributeValue<String> getRedWhen();
    @NotNull GenericAttributeValue<String> getContainerStyler();
    @NotNull GenericAttributeValue<String> getValidateService();
    @NotNull GenericAttributeValue<String> getValidateParameter();
    @NotNull GenericAttributeValue<String> getValidateEntity();
    @NotNull GenericAttributeValue<String> getValidateField();

}
