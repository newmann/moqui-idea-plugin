package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityNameReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.RelationshipPresentationProvider;

import java.util.List;
@Presentation( icon = "MoquiIcons.RelationshipTag",provider = RelationshipPresentationProvider.class)
public interface Relationship extends DomElement {
    public static final String TAG_NAME = "relationship";

    public static final String ATTR_TYPE = "type";
    public static final String ATTR_TITLE = "title";
    public static final String ATTR_RELATED = "related";
    public static final String ATTR_FK_NAME = "fk-name";

    public static final String ATTR_SHORT_ALIAS = "short-alias";
    public static final String ATTR_MUTABLE = "mutable";

    @NotNull
    @Attribute(ATTR_TYPE)
    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute(ATTR_RELATED)
//    @Convert(EntityNameConverter.class)
    @Referencing(EntityNameReferenceConverter.class)
    GenericAttributeValue<String> getRelated();
    @NotNull
    @Attribute(ATTR_FK_NAME)
    GenericAttributeValue<String> getFkName();


    @NotNull
    @Attribute(ATTR_TITLE)
    GenericAttributeValue<String> getTitle();

    @NotNull
    @Attribute(ATTR_SHORT_ALIAS)
    GenericAttributeValue<String> getShortAlias();

    @NotNull
    @Attribute(ATTR_MUTABLE)
    GenericAttributeValue<Boolean> getMutable();

    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();

    @NotNull
    @SubTagList(KeyMap.TAG_NAME)
    List<KeyMap> getKeyMapList();
    @NotNull
    @SubTagList(KeyValue.TAG_NAME)
    List<KeyValue> getKeyValueList();
}
