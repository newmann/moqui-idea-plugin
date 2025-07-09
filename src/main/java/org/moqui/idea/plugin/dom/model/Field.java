package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.FieldPresentationProvider;

@Presentation(provider = FieldPresentationProvider.class)
public interface Field extends AbstractField,FormFieldSingle,FormFieldList {

    String TAG_NAME = "field";
//    String ATTR_NAME = "name";

    String ATTR_COLUMN_NAME = "column-name";
//    String ATTR_TYPE = "type";

    String ATTR_IS_PK = "is-pk";

    String ATTR_NOT_NULL = "not-null";

    String ATTR_ENCRYPT = "encrypt";

    String ATTR_ENABLE_AUDIT_LOG = "enable-audit-log";

    String ATTR_ENABLE_LOCALIZATION = "enable-localization";
    String ATTR_DEFAULT = "default";

    String ATTR_CREATE_ONLY = "create-only";

//    @NotNull
//    @Attribute(ATTR_NAME)
//    GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_COLUMN_NAME)
    GenericAttributeValue<String> getColumnName();

//    @NotNull
//    @Attribute(ATTR_TYPE)
//    GenericAttributeValue<String> getType();
    @NotNull
    @Attribute(ATTR_IS_PK)
    GenericAttributeValue<Boolean> getIsPk();
    @NotNull
    @Attribute(ATTR_NOT_NULL)
    GenericAttributeValue<Boolean> getNotNull();
    @NotNull
    @Attribute(ATTR_ENCRYPT)
    GenericAttributeValue<Boolean> getEncrypt();
    @NotNull
    @Attribute(ATTR_ENABLE_AUDIT_LOG)
    GenericAttributeValue<String> getEnableAuditLog();
    @NotNull
    @Attribute(ATTR_ENABLE_LOCALIZATION)
    GenericAttributeValue<Boolean> getEnableLocalization();

    @NotNull
    @Attribute(ATTR_DEFAULT)
    GenericAttributeValue<String> getDefault();
    @NotNull
    @Attribute(ATTR_CREATE_ONLY)
    GenericAttributeValue<Boolean> getCreateOnly();


    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();

}
