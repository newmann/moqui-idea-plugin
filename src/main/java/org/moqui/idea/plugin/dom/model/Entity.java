package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityNameReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.EntityPresentationProvider;

import java.util.List;
@Presentation(icon = "org.moqui.idea.plugin.MyIcons.EntityTag",provider = EntityPresentationProvider.class)
public interface Entity extends AbstractEntity {
    
    String TAG_NAME = "entity";
//    String ATTR_ENTITY_NAME = "entity-name";
//    String ATTR_PACKAGE = "package";

    
    String ATTR_TABLE_NAME = "table-name";

    
    String ATTR_GROUP = "group";
    String ATTR_USE = "use";

    
    String ATTR_SEQUENCE_PRIMARY_USE_UUID = "sequence-primary-use-uuid";

    
    String ATTR_SEQUENCE_BANK_SIZE = "sequence-bank-size";
    
    String ATTR_SEQUENCE_PRIMARY_STAGGER = "sequence-primary-stagger";
    
    String ATTR_SEQUENCE_PRIMARY_PREFIX = "sequence-primary-prefix";
    
    String ATTR_SEQUENCE_SECONDARY_PADDED_LENGTH = "sequence-secondary-padded-length";
    
    String ATTR_OPTIMISTIC_LOCK = "optimistic-lock";
    
    String ATTR_NO_UPDATE_STAMP = "no-update-stamp";

    String ATTR_CACHE = "cache";
    
    String ATTR_AUTHORIZE_SKIP = "authorize-skip";
    
    String ATTR_CREATE_ONLY = "create-only";
    
    String ATTR_ENABLE_AUDIT_LOG = "enable-audit-log";
    
    String ATTR_SHORT_ALIAS = "short-alias";

    //for rest-api-3.xsd

    String ATTR_NAME = "name";

    @NotNull
    @Attribute(ATTR_NAME)
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityNameReferenceConverter.class)
    GenericAttributeValue<String> getName();
    String ATTR_MasterName = "masterName";
    @NotNull
    @Attribute(ATTR_MasterName)
    GenericAttributeValue<String> getMasterName();

    @NotNull GenericAttributeValue<String> getOperation();
    //for rest-api-3.xsd end


//    @NotNull
//    @Attribute(ATTR_ENTITY_NAME)
//    GenericAttributeValue<String> getEntityName();
//
//    @NotNull
//    @Attribute(ATTR_PACKAGE)
//    GenericAttributeValue<String> getPackage();
    @NotNull
    @Attribute(ATTR_TABLE_NAME)
    GenericAttributeValue<String> getTableName();
    @NotNull
    @Attribute(ATTR_GROUP)
    GenericAttributeValue<String> getGroup();

    @NotNull
    @Attribute(ATTR_CACHE)
    GenericAttributeValue<String> getCache();
    @NotNull
    @Attribute(ATTR_AUTHORIZE_SKIP)
    GenericAttributeValue<String> getAuthorizeSkip();

    @NotNull
    @Attribute(ATTR_SEQUENCE_BANK_SIZE)
    GenericAttributeValue<String> getSequenceBankSize();

    @NotNull
    @Attribute(ATTR_SEQUENCE_PRIMARY_USE_UUID)
    GenericAttributeValue<Boolean> getSequencePrimaryUseUuid();

    @NotNull
    @Attribute(ATTR_SEQUENCE_PRIMARY_STAGGER)
    GenericAttributeValue<String> getSequencePrimaryStagger();

    @NotNull
    @Attribute(ATTR_SEQUENCE_PRIMARY_PREFIX)
    GenericAttributeValue<String> getSequencePrimaryPrefix();
    @NotNull
    @Attribute(ATTR_SEQUENCE_SECONDARY_PADDED_LENGTH)
    GenericAttributeValue<String> getSequenceSecondaryPaddedLength();
    @NotNull
    @Attribute(ATTR_OPTIMISTIC_LOCK)
    GenericAttributeValue<Boolean> getOptimisticLock();
    @NotNull
    @Attribute(ATTR_NO_UPDATE_STAMP)
    GenericAttributeValue<Boolean> getNoUpdateStamp();
    @NotNull
    @Attribute(ATTR_CREATE_ONLY)
    GenericAttributeValue<Boolean> getCreateOnly();

    @NotNull
    @Attribute(ATTR_USE)
    GenericAttributeValue<String> getUse();
    @NotNull
    @Attribute(ATTR_ENABLE_AUDIT_LOG)
    GenericAttributeValue<String> getEnableAuditLog();

    @NotNull
    @Attribute(ATTR_SHORT_ALIAS)
    GenericAttributeValue<String> getShortAlias();

    @NotNull
    @SubTagList(Field.TAG_NAME)
    List<Field> getFieldList();



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
