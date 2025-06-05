package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityNameReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.EntityPresentationProvider;

import java.util.List;
@Presentation(icon = "org.moqui.idea.plugin.MyIcons.EntityTag",provider = EntityPresentationProvider.class)
public interface Entity extends AbstractEntity {
    
    public static final String TAG_NAME = "entity";
//    public static final String ATTR_ENTITY_NAME = "entity-name";
//    public static final String ATTR_PACKAGE = "package";

    
    public static final String ATTR_TABLE_NAME = "table-name";

    
    public static final String ATTR_GROUP = "group";
    public static final String ATTR_USE = "use";

    
    public static final String ATTR_SEQUENCE_PRIMARY_USE_UUID = "sequence-primary-use-uuid";

    
    public static final String ATTR_SEQUENCE_BANK_SIZE = "sequence-bank-size";
    
    public static final String ATTR_SEQUENCE_PRIMARY_STAGGER = "sequence-primary-stagger";
    
    public static final String ATTR_SEQUENCE_PRIMARY_PREFIX = "sequence-primary-prefix";
    
    public static final String ATTR_SEQUENCE_SECONDARY_PADDED_LENGTH = "sequence-secondary-padded-length";
    
    public static final String ATTR_OPTIMISTIC_LOCK = "optimistic-lock";
    
    public static final String ATTR_NO_UPDATE_STAMP = "no-update-stamp";

    public static final String ATTR_CACHE = "cache";
    
    public static final String ATTR_AUTHORIZE_SKIP = "authorize-skip";
    
    public static final String ATTR_CREATE_ONLY = "create-only";
    
    public static final String ATTR_ENABLE_AUDIT_LOG = "enable-audit-log";
    
    public static final String ATTR_SHORT_ALIAS = "short-alias";

    //for rest-api-3.xsd

    public static final String ATTR_NAME = "name";

    @NotNull
    @Attribute(ATTR_NAME)
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityNameReferenceConverter.class)
    GenericAttributeValue<String> getName();
    public static final String ATTR_MasterName = "masterName";
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
