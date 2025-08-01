package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.TransitionReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.DynamicDialogPresentationProvider;

import java.util.List;
@Presentation(provider = DynamicDialogPresentationProvider.class)
public interface DynamicDialog extends DomElement {
    
    String TAG_NAME = "dynamic-dialog";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getButtonText();
    @NotNull GenericAttributeValue<String> getButtonStyle();
    @NotNull GenericAttributeValue<String> getTitle();
    @NotNull GenericAttributeValue<String> getWidth();
    @NotNull GenericAttributeValue<String> getHeight();

    @NotNull
    @Referencing(TransitionReferenceConverter.class)
    GenericAttributeValue<String> getTransition();

    @NotNull GenericAttributeValue<String> getParameterMap();
    @NotNull GenericAttributeValue<String> getCondition();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getIcon();
}
