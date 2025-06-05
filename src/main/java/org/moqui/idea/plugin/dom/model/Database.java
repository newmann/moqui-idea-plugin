package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Database extends DomElement {

    public static final String TAG_NAME = "database";


    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getLbName();
    @NotNull GenericAttributeValue<String> getUseSchemas();
    @NotNull GenericAttributeValue<String> getUsePkConstraintNames();
    @NotNull GenericAttributeValue<String> getConstraintNameClipLength();
    @NotNull GenericAttributeValue<String> getResultFetchSize();
    @NotNull GenericAttributeValue<String> getUseForeignKeys();
    @NotNull GenericAttributeValue<String> getUseForeignKeyIndexes();
    @NotNull GenericAttributeValue<String> getFkStyle();
    @NotNull GenericAttributeValue<String> getUseFkInitiallyDeferred();
    @NotNull GenericAttributeValue<String> getUseIndexes();
    @NotNull GenericAttributeValue<String> getUseIndexesUnique();
    @NotNull GenericAttributeValue<String> getUseIndexesUniqueWhereNotNull();
    @NotNull GenericAttributeValue<String> getJoinStyle();
    @NotNull GenericAttributeValue<String> getOffsetStyle();
    @NotNull GenericAttributeValue<String> getFromLateralStyle();
    @NotNull GenericAttributeValue<String> getAddUniqueAs();
    @NotNull GenericAttributeValue<String> getAlwaysUseConstarntKeyword();
    @NotNull GenericAttributeValue<String> getUseSchemaForAll();
    @NotNull GenericAttributeValue<String> getNeverNulls();
    @NotNull GenericAttributeValue<String> getNeverTryInsert();
    @NotNull GenericAttributeValue<String> getUseBinaryTypeForBlob();
    @NotNull GenericAttributeValue<String> getTableEngine();
    @NotNull GenericAttributeValue<String> getCharacterSet();
    @NotNull GenericAttributeValue<String> getCollate();
    @NotNull GenericAttributeValue<String> getDefaultIsolationLevel();
    @NotNull GenericAttributeValue<String> getForUpdate();
    @NotNull GenericAttributeValue<String> getUseTmJoin();
    @NotNull GenericAttributeValue<String> getDefaultJdbcDriver();
    @NotNull GenericAttributeValue<String> getDefaultXaDsClass();
    @NotNull GenericAttributeValue<String> getDefaultTestQuery();
    @NotNull GenericAttributeValue<String> getDefaultStartupAddMissing();
    @NotNull GenericAttributeValue<String> getDefaultRuntimeAddMissing();
    @NotNull GenericAttributeValue<String> getDefaultRuntimeAddFks();
    @NotNull GenericAttributeValue<String> getDefaultStartServerArgs();





    @NotNull
    @SubTag(InlineJdbc.TAG_NAME)
    InlineJdbc getInlineJdbc();

    @NotNull
    @SubTagList(DatabaseType.TAG_NAME)
    List<DatabaseType> getDatabaseTypeList();
    @NotNull
    @SubTagList(NameReplace.TAG_NAME)
    List<NameReplace> getNameReplaceList();

}
