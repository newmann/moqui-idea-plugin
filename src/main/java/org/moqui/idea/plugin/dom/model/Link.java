package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityAndViewNameReferenceConverter;
import org.moqui.idea.plugin.dom.converter.TextTemplateConverter;
import org.moqui.idea.plugin.dom.converter.UrlConverter;
import org.moqui.idea.plugin.dom.presentation.LinkPresentationProvider;

import java.util.List;
@Presentation(provider = LinkPresentationProvider.class)
public interface Link extends DomElement {
    public static final String TAG_NAME = "link";

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull
    @SubTag(Image.TAG_NAME)
    Image getImage();


    @NotNull GenericAttributeValue<String> getId();

    @NotNull GenericAttributeValue<String> getLinkType();

    @NotNull
    @Convert(UrlConverter.class)
    GenericAttributeValue<String> getUrl();

    @NotNull GenericAttributeValue<String> getUrlType();
    @NotNull GenericAttributeValue<String> getUrlNoparam();
    @NotNull
    @Convert(TextTemplateConverter.class)
    GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getTextMap();
    @NotNull GenericAttributeValue<String> getEncode();
    @NotNull GenericAttributeValue<String> getIcon();
    @NotNull GenericAttributeValue<String> getBadge();
    @NotNull GenericAttributeValue<String> getTooltip();
    @NotNull GenericAttributeValue<String> getTargetWindow();
    @NotNull GenericAttributeValue<String> getConfirmation();
    @NotNull GenericAttributeValue<String> getParameterMap();
    @NotNull GenericAttributeValue<Boolean> getPassThroughParameters();
    @NotNull GenericAttributeValue<String> getExpandTransitionUrl();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<String> getBtnStyle();
    @NotNull GenericAttributeValue<String> getDynamicLoadId();
    @NotNull GenericAttributeValue<String> getCondition();
    @NotNull
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityAndViewNameReferenceConverter.class)
    GenericAttributeValue<String> getEntityName();

    @NotNull GenericAttributeValue<String> getEntityKeyName();
    @NotNull GenericAttributeValue<Boolean> getEntityUseCache();

}
