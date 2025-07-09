package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public interface Datasource extends DomElement {

    String TAG_NAME = "datasource";


    @NotNull GenericAttributeValue<String> getGroupName();
    @NotNull GenericAttributeValue<String> getDatabaseConfName();
    @NotNull GenericAttributeValue<String> getSchemaName();
    @NotNull GenericAttributeValue<String> getStartupAddMissing();
    @NotNull GenericAttributeValue<String> getRuntimeAddMissing();
    @NotNull GenericAttributeValue<String> getRuntimeAddFks();
    @NotNull GenericAttributeValue<String> getObjectFactory();
    @NotNull GenericAttributeValue<String> getSequencePrimaryUseUuid();
    @NotNull GenericAttributeValue<String> getStartServerArgs();
    @NotNull GenericAttributeValue<String> getDisabled();



    @NotNull
    @SubTag(JndiJdbc.TAG_NAME)
    JndiJdbc getJndiJdbc();
    @NotNull
    @SubTag(InlineJdbc.TAG_NAME)
    InlineJdbc getInlineJdbc();
    @NotNull
    @SubTag(InlineOther.TAG_NAME)
    InlineOther getInlineOther();


}
