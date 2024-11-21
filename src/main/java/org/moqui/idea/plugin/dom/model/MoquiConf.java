package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//@Stubbed
public interface MoquiConf extends DomElement {
    public static final String TAG_NAME = "moqui-conf";
    public static final String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    public static final String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/moqui-conf-3.xsd";

    @NotNull
//    @Stubbed
    @SubTagList(DefaultProperty.TAG_NAME)
    List<DefaultProperty> getEntities();

    @NotNull
    @SubTag(Tools.TAG_NAME)
    Tools getTools();
    @NotNull
    @SubTag(CacheList.TAG_NAME)
    CacheList getCacheList();
    @NotNull
    @SubTag(ServerStats.TAG_NAME)
    ServerStats getServerStats();
    @NotNull
    @SubTag(WebappList.TAG_NAME)
    WebappList getWebappList();
    @NotNull
    @SubTag(ArtifactExecutionFacade.TAG_NAME)
    ArtifactExecutionFacade getArtifactExecutionFacade();
    @NotNull
    @SubTag(UserFacade.TAG_NAME)
    UserFacade getUserFacade();
    @NotNull
    @SubTag(TransactionFacade.TAG_NAME)
    TransactionFacade getTransitionFacade();
    @NotNull
    @SubTag(ResourceFacade.TAG_NAME)
    ResourceFacade getResourceFacade();
    @NotNull
    @SubTag(ScreenFacade.TAG_NAME)
    ScreenFacade getScreenFacade();
    @NotNull
    @SubTag(ServiceFacade.TAG_NAME)
    ServiceFacade getServiceFacade();
    @NotNull
    @SubTag(ElasticFacade.TAG_NAME)
    ElasticFacade getElasticFacade();
    @NotNull
    @SubTag(EntityFacade.TAG_NAME)
    EntityFacade getEntityFacade();
    @NotNull
    @SubTag(DatabaseList.TAG_NAME)
    DatabaseList getDatabaseList();

    @NotNull
    @SubTag(RepositoryList.TAG_NAME)
    RepositoryList getRepositoryList();
    @NotNull
    @SubTag(ComponentList.TAG_NAME)
    ComponentList getComponentList();

}
