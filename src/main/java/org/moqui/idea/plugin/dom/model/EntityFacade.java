package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface EntityFacade extends DomElement {

    public static final String TAG_NAME = "entity-facade";


    @NotNull GenericAttributeValue<String> getEntityEcaEnabled();
    @NotNull GenericAttributeValue<String> getDistributedCacheInvalidate();
    @NotNull GenericAttributeValue<String> getDciTopicFactory();
    @NotNull GenericAttributeValue<String> getSequencedIdPrefix();
    @NotNull GenericAttributeValue<String> getDefaultGroupName();
    @NotNull GenericAttributeValue<String> getDatabaseTimeZone();
    @NotNull GenericAttributeValue<String> getDatabaseLocale();
    @NotNull GenericAttributeValue<String> getCryptPass();
    @NotNull GenericAttributeValue<String> getCryptSalt();
    @NotNull GenericAttributeValue<String> getCryptIter();
    @NotNull GenericAttributeValue<String> getCryptAlgo();
    @NotNull GenericAttributeValue<String> getCryptQueryStats();

    @NotNull
    @SubTagList(DecryptAlt.TAG_NAME)
    List<DecryptAlt> getDecryptAltList();

    @NotNull
    @SubTag(ServerJndi.TAG_NAME)
    ServerJndi getServerJndi();

    @NotNull
    @SubTagList(Datasource.TAG_NAME)
    List<Datasource> getDatasourceList();
    @NotNull
    @SubTagList(LoadEntity.TAG_NAME)
    List<LoadEntity> getLoadEntityList();
    @NotNull
    @SubTagList(LoadData.TAG_NAME)
    List<LoadData> getLoadDataList();


}
