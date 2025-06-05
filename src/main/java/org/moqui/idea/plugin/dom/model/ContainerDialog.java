package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.ContainerDialogPresentationProvider;

@Presentation(provider = ContainerDialogPresentationProvider.class)
public interface ContainerDialog extends WidgetElementsList {
    
    public static final String TAG_NAME = "container-dialog";

//    @NotNull
//    @SubTagList(EntityOptions.TAG_NAME)
//    List<Set> getSetList();

//    @NotNull GenericAttributeValue<String> getLocation();
    @NotNull GenericAttributeValue<String> getId();
    @NotNull GenericAttributeValue<String> getButtonText();
    @NotNull GenericAttributeValue<String> getButtonStyle();
    @NotNull GenericAttributeValue<String> getTitle();
    @NotNull GenericAttributeValue<String> getWidth();
    @NotNull GenericAttributeValue<String> getHeight();
    @NotNull GenericAttributeValue<String> getCondition();
    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getIcon();
}
