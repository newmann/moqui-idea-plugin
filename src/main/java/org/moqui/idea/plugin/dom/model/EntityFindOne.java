package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.EntityFindOnePresentationProvider;

import java.util.List;
@Presentation(provider = EntityFindOnePresentationProvider.class)
public interface EntityFindOne extends AbstractEntityName {

    String TAG_NAME = "entity-find-one";
//    String ATTR_ENTITY_NAME = "entity-name";

    String ATTR_VALUE_FIELD = "value-field";
//    @NotNull
//    @Attribute(ATTR_ENTITY_NAME)
//    @Convert(EntityFullNameConverter.class)
//    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute(ATTR_VALUE_FIELD)
    GenericAttributeValue<String> getValueField();

    @NotNull GenericAttributeValue<String> getAutoFieldMap();
    @NotNull GenericAttributeValue<String> getCache();
    @NotNull GenericAttributeValue<String> getForUpdate();
    @NotNull GenericAttributeValue<String> getUseClone();

    @NotNull
    @SubTagList(FieldMap.TAG_NAME)
    List<FieldMap> getFieldMapList();
    @NotNull
    @SubTagList(SelectField.TAG_NAME)
    List<SelectField> getSelectFieldList();


}
