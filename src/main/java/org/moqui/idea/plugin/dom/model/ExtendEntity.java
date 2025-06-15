package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ExtendEntityNameConverter;
import org.moqui.idea.plugin.dom.converter.ExtendEntityPackageConverter;

import java.util.List;

public interface ExtendEntity extends DomElement {
    
    public static final String TAG_NAME = "extend-entity";
    
    public static final String ATTR_ENTITY_NAME = "entity-name";
    
    public static final String ATTR_PACKAGE = "package";
    
    public static final String ATTR_TABLE_NAME = "table-name";

    
    public static final String ATTR_GROUP = "group";
    
    public static final String ATTR_SEQUENCE_BANK_SIZE = "sequence-bank-size";
    
    public static final String ATTR_SEQUENCE_PRIMARY_PREFIX = "sequence-primary-prefix";
    
    public static final String ATTR_OPTIMISTIC_LOCK = "optimistic-lock";
    
    public static final String ATTR_NO_UPDATE_STAMP = "no-update-stamp";

    public static final String ATTR_CACHE = "cache";
    
    public static final String ATTR_AUTHORIZE_SKIP = "authorize-skip";
    
    public static final String ATTR_ENABLE_AUDIT_LOG = "enable-audit-log";


    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
    @Convert(ExtendEntityNameConverter.class)
    GenericAttributeValue<String> getEntityName();

//
    @NotNull
    @Attribute(ATTR_PACKAGE)
    @Convert(ExtendEntityPackageConverter.class)
    GenericAttributeValue<String> getPackage();

    @NotNull
    @Attribute(ATTR_TABLE_NAME)
    GenericAttributeValue<String> getTableName();
    @NotNull
    @Attribute(ATTR_GROUP)
    GenericAttributeValue<String> getGroup();
    @NotNull
    @Attribute(ATTR_SEQUENCE_BANK_SIZE)
    GenericAttributeValue<String> getSequenceBankSize();
    @NotNull
    @Attribute(ATTR_SEQUENCE_PRIMARY_PREFIX)
    GenericAttributeValue<String> getSequencePrimaryPrefix();
    @NotNull
    @Attribute(ATTR_OPTIMISTIC_LOCK)
    GenericAttributeValue<Boolean> getOptimisticLock();
    @NotNull
    @Attribute(ATTR_NO_UPDATE_STAMP)
    GenericAttributeValue<Boolean> getNoUpdateStamp();
    @NotNull
    @Attribute(ATTR_ENABLE_AUDIT_LOG)
    GenericAttributeValue<String> getEnableAuditLog();
    @NotNull
    @Attribute(ATTR_CACHE)
    GenericAttributeValue<String> getCache();
    @NotNull
    @Attribute(ATTR_AUTHORIZE_SKIP)
    GenericAttributeValue<String> getAuthorizeSkip();

    @NotNull
    @SubTagList(Field.TAG_NAME)
    List<Field> getFieldList();

    @NotNull
    @SubTagList(Relationship.TAG_NAME)
    List<Relationship> getRelationshipList();

    @NotNull
    @SubTagList(Index.TAG_NAME)
    List<Index> getIndexList();

    @NotNull
    @SubTag(SeedData.TAG_NAME)
    SeedData getSeedData();

    @NotNull
    @SubTagList(Master.TAG_NAME)
    List<Master> getMasterList();
}
