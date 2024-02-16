package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ViewEntityAliasConverter;
import org.moqui.idea.plugin.dom.presentation.AliasAllPresentationProvider;

import java.util.List;
@Presentation(provider = AliasAllPresentationProvider.class)
public interface AliasAll extends DomElement {
    public static final String TAG_NAME = "alias-all";

    public static final String ATTR_ENTITY_ALIAS ="entity-alias";
    public static final String ATTR_PREFIX ="prefix";

    @NotNull
    @Attribute(ATTR_ENTITY_ALIAS)
    @Convert(ViewEntityAliasConverter.class)
    GenericAttributeValue<String> getEntityAlias();

    @NotNull
    @Attribute(ATTR_PREFIX)
    GenericAttributeValue<String> getPrefix();

    //for entity definition
    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();
    @NotNull
    @SubTagList(Exclude.TAG_NAME)
    List<Exclude> getExcludeList();


}
