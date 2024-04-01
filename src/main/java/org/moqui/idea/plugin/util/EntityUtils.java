package org.moqui.idea.plugin.util;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.service.EntityPsiElementService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByConvertContext;
import static org.moqui.idea.plugin.util.MyStringUtils.isNotEmpty;


public final class EntityUtils {
    private EntityUtils() {
        throw new UnsupportedOperationException();
    }

    public static final class EntityDescriptor{
        EntityDescriptor(){}
        EntityDescriptor(@NotNull String fullName){
            int index = fullName.lastIndexOf('.');
            if (index<0) {
                entityName = fullName;
                entityPackage = MyStringUtils.EMPTY_STRING;
            }else {
                entityPackage = fullName.substring(0,index);
                entityName = fullName.substring(index+1);
            };
        }
        EntityDescriptor(String entityName,String entityPackage){
            this.entityName = entityName;
            this.entityPackage = entityPackage;
        }
        public String entityName;
        public String entityPackage;


        public String getFullName(){
            if(MyStringUtils.isEmpty(entityPackage)) {
                return entityName;
            }else {
                return entityPackage+"." +entityPackage;
            }

        }
    }

    public static boolean isEntitiesFile(@Nullable PsiFile file){
        return MyDomUtils.isSpecialXmlFile(file, Entities.TAG_NAME);
    }

    /**
     * 根据实体名和包名找到对应的实体
     * @param project 当前项目
     * @param entityName 实体名
     * @param entityPackage 包名
     * @return Collection<Entity>
     */
    public static Collection<Entity> findEntitiesByName(@NotNull Project project, @NotNull String entityName,
                                                        @NotNull String entityPackage){
        Collection<Entity> result = new ArrayList<Entity>();
        List<DomFileElement<Entities>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                if(entity.getEntityName().getValue().equals(entityName)
                && entity.getPackage().getValue().equals(entityPackage)) {
                    result.add(entity);
                }
            };
        }
        return result;
    }
    /**
     * 根据所有的实体
     * @param project 当前项目
     * @return Collection<Entity>
     */
    public static Collection<Entity> findAllEntity(@NotNull Project project){
        Collection<Entity> result = new ArrayList<>();
        List<DomFileElement<Entities>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                result.add(entity);
            };
        }
        return result;
    }
    public static Collection<AbstractEntity> findAllEntityAndViewEntity(@NotNull Project project){
        Collection<AbstractEntity> result = new ArrayList<>();
        List<DomFileElement<Entities>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                result.add(entity);
            };
            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
                result.add(viewEntity);
            };
        }
        return result;
    }

//    public static Map<String, DomElement> findAllEntityDomElement(@NotNull Project project) {
//        Map<String, DomElement> result = new HashMap<String, DomElement>();
//        Collection<DomElement> domElements = findAllEntity(project);
//
//        domElements.forEach(domElement -> {
//            result.put(getFullEntityNameFromDomElement(domElement), domElement);
//        }  );
//
//        return result;
//
//    }


    /**
     * 从Entity定义中拼接出完整名称
     */
    public static String getFullNameFromEntity(AbstractEntity entity) {
        return entity.getPackage().getXmlAttributeValue().getValue()
                + "." + entity.getEntityName().getXmlAttributeValue().getValue();
    }
    public static String getFullNameFromViewEntity(ViewEntity entity) {
        return entity.getPackage().getXmlAttributeValue().getValue()
                + "." + entity.getEntityName().getXmlAttributeValue().getValue();
    }
    public static String getFullNameFromExtendEntity(ExtendEntity entity) {
        return entity.getPackage().getXmlAttributeValue().getValue()
                + "." + entity.getEntityName().getXmlAttributeValue().getValue();
    }

    /**
     * 从Entity或View对应的DomElement中拼接出完整名称
     * 统一的属性名称是：entity-name和package
     */
    public static String getFullEntityNameFromDomElement(DomElement domElement) {
        String packageName = domElement.getXmlTag().getAttributeValue("package");
        String name = domElement.getXmlTag().getAttributeValue("entity-name");
        return packageName + "." + name;
    }


//    /**
//     * 根据实体名和包名找到对应的实体定义的XmlElement
//     * @param project
//     * @param entityName
//     * @param entityPackage
//     * @return
//     */
    public static Optional<XmlElement[]> findEntityElementsByNameAndPackage(@NotNull Project project, @NotNull String entityName,
                                                                            @NotNull String entityPackage){

        List<DomFileElement<Entities>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                if(entity.getEntityName().getValue().equals(entityName)
                        && entity.getPackage().getValue().equals(entityPackage)) {
                    XmlElement[] result = {entity.getXmlElement()};

                    return Optional.of(result);
                }
            };
            //如果在实体中没有找到，在视图中再查找
            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
                if(viewEntity.getEntityName().getValue().equals(entityName)
                        && viewEntity.getPackage().getValue().equals(entityPackage)) {
                    XmlElement[] viewElement = {viewEntity.getXmlElement()};

                    return Optional.of(viewElement);
                }
            };

        }
        return Optional.empty();
    }

    /**
     * 根据字符串，找到对应的Entity或ViewEntity对应的XmlElement
     * @param project
     * @param fullName
     * @return
     */
    public static Optional<XmlElement> findEntityAndViewEntityXmlElementByFullName(@NotNull Project project
            , @NotNull String fullName){
        EntityDescriptor entityDescriptor = new EntityDescriptor(fullName);
//        int index = fullName.lastIndexOf('.');
//        if (index<0) return Optional.empty();
//
//        final String packageName = fullName.substring(0,index);
//        final String entityName = fullName.substring(index+1);
        return findEntityAndViewEntityXmlElementByNameAndPackage(project,entityDescriptor.entityName,entityDescriptor.entityPackage);
    }


    /**
     * 找到Entity或ViewEntity对应的XmlElement
     * @param project
     * @param entityName
     * @param entityPackage
     * @return
     */
    public static Optional<XmlElement> findEntityAndViewEntityXmlElementByNameAndPackage(@NotNull Project project, @NotNull String entityName,
                                                                                         @NotNull String entityPackage){

        Optional<AbstractEntity> entity = findEntityAndViewEntityByNameAndPackage(project, entityName, entityPackage);
        if(entity.isPresent()) {
            return Optional.of(entity.get().getXmlElement());
        }
//        List<DomFileElement<Entities>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Entities.class);
//        for(DomFileElement<Entities> fileElement : fileElementList) {
//            for(Entity entity: fileElement.getRootElement().getEntities()) {
//                if(entity.getEntityName().getValue().equals(entityName)
//                        && entity.getPackage().getValue().equals(entityPackage)) {
//
//                    return Optional.of(entity.getXmlElement());
//                }
//            };
//            //如果在实体中没有找到，在视图中再查找
//            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
//                if(viewEntity.getEntityName().getValue().equals(entityName)
//                        && viewEntity.getPackage().getValue().equals(entityPackage)) {
//                    return Optional.of(viewEntity.getXmlElement());
//                }
//            };
//
//        }
        return Optional.empty();
    }

    /**
     * 根据字符串找到指定的Entity或ViewEntity
     * @param project
     * @param fullName
     * @return
     */
    public static Optional<AbstractEntity> findEntityAndViewEntityByFullName(@NotNull Project project
            , @NotNull String fullName){
//        EntityDescriptor descriptor = new EntityDescriptor(fullName);
//        return findEntityAndViewEntityByNameAndPackage(project,descriptor.entityName,descriptor.entityPackage);
        return findAllEntityAndViewEntity(project).stream().filter(item->{
                return isThisEntityName(item,fullName);
        }).findFirst();
    }

    /**
     * 找到指定的Entity或ViewEntity
     * @param project
     * @param entityName
     * @param entityPackage
     * @return
     */
    public static Optional<AbstractEntity> findEntityAndViewEntityByNameAndPackage(@NotNull Project project, @NotNull String entityName,
                                                              @NotNull String entityPackage){
        EntityDescriptor entityDescriptor = new EntityDescriptor(entityName,entityPackage);

        return findEntityAndViewEntityByFullName(project,entityDescriptor.getFullName());

//        return findAllEntityAndViewEntity(project).stream().filter(item->{
//            if(entityPackage.equals(MyStringUtils.EMPTY_STRING)) {
//                return entityName.equals(item.getEntityName().getValue());
//            }else {
//                return entityName.equals(item.getEntityName().getValue())
//                        && entityPackage.equals(item.getPackage().getValue());
//            }
//        }).findFirst();
    }

    /**
     * 根据全名找到指定的Entity
     * @param project
     * @param fullName
     * @return
     */
    public static Optional<Entity> findEntityByFullName(@NotNull Project project
            , @NotNull String fullName){
        EntityDescriptor descriptor = new EntityDescriptor(fullName);
        return findEntityByNameAndPackage(project,descriptor.entityName,descriptor.entityPackage);

    }

    /**
     * 找到指定的Entity
     * @param project
     * @param entityName
     * @param entityPackage
     * @return
     */
    public static Optional<Entity> findEntityByNameAndPackage(@NotNull Project project, @NotNull String entityName,
                                                                            @NotNull String entityPackage){

        List<DomFileElement<Entities>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                if(entityPackage.equals(MyStringUtils.EMPTY_STRING)) {
                    if (entity.getEntityName().getValue().equals(entityName)) {
                        return Optional.of(entity);
                    }

                }else {
                    if (entity.getEntityName().getValue().equals(entityName)
                            && entity.getPackage().getValue().equals(entityPackage)) {

                        return Optional.of(entity);
                    }
                }
            };
        }
        return Optional.empty();
    }

    /**
     * 根据EntityDescriptor找到对应的XmlElement
     * @param project
     * @param entityDescriptor
     * @return
     */
    public static Optional<XmlElement[]> findEntityByEntityDescriptor(@NotNull Project project, @NotNull EntityDescriptor entityDescriptor){
        if(entityDescriptor.entityName == null || entityDescriptor.entityPackage == null) {
            return Optional.empty();
        }

        return findEntityElementsByNameAndPackage(project, entityDescriptor.entityName,entityDescriptor.entityPackage);
    }

    /**
     * 将一个字符串转化成EntityDescriptor
     * @param fullName
     * @return
     */
    public static EntityDescriptor getEntityDescriptorFromFullName(@NotNull String fullName){
        int lastIndex = fullName.lastIndexOf(".");
        EntityDescriptor entityDescriptor;
        if(lastIndex<0) {
            entityDescriptor = new EntityDescriptor();
            entityDescriptor.entityName = fullName;
        }else {

            String entityName = fullName.substring(lastIndex + 1);
            String entityPackage = fullName.substring(0, lastIndex);
            entityDescriptor = new EntityDescriptor(entityName, entityPackage);
        }
        return entityDescriptor;

    }

    /**
     * 获取所有定义Entity的指定属性内容
     * @param project
     * @param attributeName
     * @param filterStr
     * @return
     */
    public static @NotNull Set<String> getEntityAttributes(@NotNull Project project,@NotNull String attributeName,@Nullable String filterStr){
        Set<String> attrNames = new HashSet<String>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体的属性名
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                var name = entity.getXmlTag().getAttribute(attributeName).getValue();
                if(name != null) {
                    if(isNotEmpty(filterStr)) {
                        if(name.contains(filterStr)) {attrNames.add(name);}
                    }else {
                        attrNames.add(name);
                    }
                }

            };
        }
        return attrNames;
    }

    /**
     * 获取指定Entity的所有Relationship的shortAlias
     * @param project
     * @param entityName
     * @return
     */
    public static @NotNull Set<String> getRelationshipShortAliases(@NotNull Project project,@NotNull String entityName){
        Set<String> attrNames = new HashSet<String>();
        List<Relationship> relationshipList = getEntityRelationshipList(project,entityName);

        return  relationshipList.stream().map(Relationship::getShortAlias)
                .map(GenericAttributeValue::getXmlAttributeValue)
                .map(XmlAttributeValue::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * 获取Entity的所有Relationship，包含ExtendEntity
     * @param project
     * @param entityName
     * @return
     */
    public static @NotNull List<Relationship> getEntityRelationshipList(@NotNull Project project,@NotNull String entityName){
        List<Relationship> relationshipList = new LinkedList<>();

        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体的Relationship
            for(Entity entity: fileElement.getRootElement().getEntities()) {

                if(isThisEntityName(entity,entityName)) {
                    relationshipList.addAll(
                            entity.getRelationshipList()
                    );
                }
            };
            //添加ExtendEntity的Relationship
            for(ExtendEntity extendEntity:fileElement.getRootElement().getExtendEntities()) {

                if(isThisExtendEntityName(extendEntity,entityName)) {
                    relationshipList.addAll(
                            extendEntity.getRelationshipList()
                    );
                }
            };
        }
        return relationshipList;
    }
    /**
     * 获取所有定义的ViewEntity某个属性的内容
     * @param project
     * @param attributeName
     * @param filterStr
     * @return
     */
    public static @NotNull Set<String> getViewEntityAttributes(@NotNull Project project,@NotNull String attributeName,@Nullable String filterStr){
        Set<String> attrNames = new HashSet<String>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {

            //添加视图实体的包名
            for(ViewEntity entity: fileElement.getRootElement().getViewEntities()) {
                var name = entity.getXmlTag().getAttribute(attributeName).getValue();
                if(name != null) {
                    if (isNotEmpty(filterStr)) {
                        if (name.contains(filterStr)) {
                            attrNames.add(name);
                        }
                    } else {
                        attrNames.add(name);
                    }
                }
            };
        }
        return attrNames;
    }

    /**
     * 获取所有定义的Entity或ViewEntity的某个属性的内容
     * @param project
     * @param attributeName
     * @param filterStr
     * @return
     */
    public static @NotNull Set<String> getEntityAndViewEntityAttributes(@NotNull Project project,@NotNull String attributeName,@Nullable String filterStr){
        Set<String> attrNames = new HashSet<String>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体的属性名
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                var name = entity.getXmlTag().getAttribute(attributeName).getValue();
                if(name != null) {
                    if(isNotEmpty(filterStr)) {
                        if(name.contains(filterStr)) {attrNames.add(name);}
                    }else {
                        attrNames.add(name);
                    }
                }

            };
            //添加视图实体的包名
            for(ViewEntity entity: fileElement.getRootElement().getViewEntities()) {
                var name = entity.getXmlTag().getAttribute(attributeName).getValue();
                if(name != null) {
                    if (isNotEmpty(filterStr)) {
                        if (name.contains(filterStr)) {
                            attrNames.add(name);
                        }
                    } else {
                        attrNames.add(name);
                    }
                }
            };
        }
        return attrNames;
    }
    public static @NotNull Set<String> getEntityFullNames(@NotNull Project project,@Nullable String filterStr){
        Set<String> entityNames = new HashSet<String>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                var entityName = getFullNameFromEntity(entity);
                if(isNotEmpty(filterStr)) {
                    if(entityName.contains(filterStr)) {entityNames.add(entityName);}
                }else {
                    entityNames.add(entityName);
                }

            };

        }
        return entityNames;
    }

    /**
     * 获取psiElement所在区域的Entity定义的字段列表
     * @param psiElement
     * @return
     */
    public static Set<String> getCurrentEntityFieldNames(PsiElement psiElement) {
        Set<String> fieldNames = new HashSet<String>();
        //获取Tag名
        Entity entity = DomUtil.findDomElement(psiElement,Entity.class);

        if (entity == null ) {
            return fieldNames;
        }
        return entity.getFieldList().stream()
                .map(Field::getName)
                .map(GenericAttributeValue::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * 获取Entity的字段列表，包括ExendEntity定义的字段
     * @param project
     * @param entityName
     * @return
     */
    public static @NotNull List<Field> getEntityFieldList(@NotNull Project project,@NotNull String entityName){
        List<Field> fieldList = new LinkedList<>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体
            fieldList.addAll(
                    fileElement.getRootElement().getEntities().stream()
                            .filter(e->isThisEntityName(e,entityName))
                            .flatMap(e-> {return e.getFieldList().stream();})
                            .toList()
            );

            //add ExtendEntity
            fieldList.addAll(
                    fileElement.getRootElement().getExtendEntities().stream()
                            .filter(e->isThisExtendEntityName(e,entityName))
                            .flatMap(e-> {return e.getFieldList().stream();})
                            .toList()
            );

        }
        return fieldList;
    }

    /**
     * 获取ViewEntity的字段列表
     * @param project
     * @param name
     * @return
     */
    public static @NotNull List<AbstractField> getViewEntityFieldList(@NotNull Project project, @NotNull String name){
        List<AbstractField> fieldList = new LinkedList<>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);
        ViewEntity viewEntity = null;
        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体
             viewEntity= fileElement.getRootElement().getViewEntities().stream()
                            .filter(e->isThisEntityName(e,name))
                            .findFirst().orElse(null);
             if(viewEntity != null) break;
        }
        if(viewEntity == null) return  fieldList;

        fieldList.addAll(getViewEntityFieldList(project,viewEntity));

//        //添加alias
//        fieldList.addAll(viewEntity.getAliasList());
//
//        //对AliasAll进行处理
//        List<AliasAll> aliasAllList = viewEntity.getAliasAllList();
//
//        for(AliasAll aliasAll : aliasAllList) {
//            String alias = aliasAll.getEntityAlias().getXmlAttributeValue().getValue();
//            List<AbstractField> aliasAllFieldList = new ArrayList<>();
//            //先查找MemberEntity，再操作MemberRelationship
//            MemberEntity memberEntity = getMemberEntityByAlias(viewEntity, alias);
//            if(memberEntity == null) {
//                MemberRelationship memberRelationship = getMemberRelationshipByAlias(viewEntity,alias);
//                if(memberRelationship != null){
//                    MemberEntity relationshipMemberEntity = getMemberEntityByAlias(viewEntity
//                            ,memberRelationship.getJoinFromAlias().getXmlAttributeValue().getValue());
//                   aliasAllFieldList.addAll(
//                           getEntityRelationshipFieldList(
//                            project
//                            ,relationshipMemberEntity.getEntityName().getXmlAttributeValue().getValue()
//                            ,memberRelationship.getRelationship().getXmlAttributeValue().getValue()
//                            )
//                   );
//
//                }
//            }else {
//                aliasAllFieldList.addAll(
//                        getEntityOrViewEntityFields(viewEntity.getXmlTag().getProject()
//                            ,memberEntity.getEntityName().getXmlAttributeValue().getValue())
//                );
//            }
//
//            fieldList.addAll(
//                    excludeFields(aliasAllFieldList,aliasAll.getExcludeList())
//            );
//
//        }

        return fieldList;

    }
    public static @NotNull List<AbstractField> getViewEntityFieldList(@NotNull Project project, @NotNull ViewEntity viewEntity){
        //添加alias
        List<AbstractField> fieldList = new LinkedList<>(viewEntity.getAliasList());

        //对AliasAll进行处理
        List<AliasAll> aliasAllList = viewEntity.getAliasAllList();

        for(AliasAll aliasAll : aliasAllList) {
            String alias = MyDomUtils.getXmlAttributeValueString(aliasAll.getEntityAlias()).orElse(MyStringUtils.EMPTY_STRING);
            if(MyStringUtils.isEmpty(alias)) continue;

            List<AbstractField> aliasAllFieldList = new ArrayList<>();
            //先查找MemberEntity，再查找MemberRelationship
            MemberEntity memberEntity = getMemberEntityByAlias(viewEntity, alias);
            if(memberEntity == null) {
                MemberRelationship memberRelationship = getMemberRelationshipByAlias(viewEntity,alias);
                if(memberRelationship != null){
                    final String joinFromAlias = MyDomUtils.getXmlAttributeValueString(memberRelationship.getJoinFromAlias())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    if(MyStringUtils.isEmpty(joinFromAlias)) continue;
                    //找到最终定义的那个MemberEntity，可以跳过中间有多个MemberRelationship
                    MemberEntity relationshipMemberEntity = getDefinedMemberEntityByAlias(viewEntity,joinFromAlias);

                    if(relationshipMemberEntity==null) continue;

                    final String entityName = MyDomUtils.getXmlAttributeValueString(relationshipMemberEntity.getEntityName())
                                    .orElse(MyStringUtils.EMPTY_STRING);
                    final String relationship = MyDomUtils.getXmlAttributeValueString(memberRelationship.getRelationship())
                                    .orElse(MyStringUtils.EMPTY_STRING);

                    if(MyStringUtils.isEmpty(entityName) || MyStringUtils.isEmpty(relationship)) continue;

                    aliasAllFieldList.addAll(
                            getEntityRelationshipFieldList(project,entityName,relationship)
                    );

                }
            }else {
                final String entityName = MyDomUtils.getXmlAttributeValueString(memberEntity.getEntityName())
                                .orElse(MyStringUtils.EMPTY_STRING);
                if(MyStringUtils.isEmpty(entityName)) continue;
                aliasAllFieldList.addAll(
                        getEntityOrViewEntityFields(project,entityName)
                );
            }

            fieldList.addAll(
                    excludeFields(aliasAllFieldList,aliasAll.getExcludeList())
            );

        }

        return fieldList;

    }

    /**
     * 根据别名找到MemberEntity，如果别名对应的是MemberRelationship，则根据JoinFromAlias找到最终的MemberEntity
     * todo:会存在死循环调用吗？
     * @param viewEntity
     * @param alias
     * @return
     */
    public static MemberEntity getDefinedMemberEntityByAlias(@NotNull ViewEntity viewEntity, @NotNull String alias){
        AbstractMemberEntity abstractMemberEntity = getViewEntityAbstractMemberEntityByAlias(viewEntity,alias).orElse(null);
        if(abstractMemberEntity == null) return null;
        if(abstractMemberEntity instanceof MemberEntity) return (MemberEntity)abstractMemberEntity;

        if(abstractMemberEntity instanceof MemberRelationship memberRelationship) {

            return getDefinedMemberEntityByAlias(viewEntity,
                    memberRelationship.getJoinFromAlias().getXmlAttributeValue().getValue());


        };
        return null;

    }
    /**
     * 根据别名找到MemberEntity，不对MemberRelationship进行处理
     * @param viewEntity
     * @param alias
     * @return
     */
    public static MemberEntity getMemberEntityByAlias(@NotNull ViewEntity viewEntity, @NotNull String alias){

        return viewEntity.getMemberEntityList().stream()
                .filter(item->{
                    final String itemAlias =MyDomUtils.getXmlAttributeValueString(item.getEntityAlias()).orElse(MyStringUtils.EMPTY_STRING);
                    return alias.equals(itemAlias);})
                .findFirst().orElse(null);

    }
    /**
     * 获取Entity的relationship的字段列表
     * @param project
     * @param entityFullName
     * @param relationshipName
     * @return
     */
    public static List<Field> getEntityRelationshipFieldList(@NotNull Project project, @NotNull String entityFullName, @NotNull String relationshipName){
        List<Field> result = new ArrayList<>();
        List<Relationship> relationshipList = getEntityRelationshipList(project,entityFullName);

        Relationship relationship = relationshipList.stream()
                .filter(item->{return isThisRelationshipRelatedName(item,relationshipName);})
                .findFirst().orElse(null);
        if(relationship != null) {
            result.addAll(getEntityFieldList(project, relationship.getRelated().getXmlAttributeValue().getValue()));
        }
        return result;
    }
    private static List<AbstractField> excludeFields(List<AbstractField> sourceFieldList,List<Exclude> excludeList){
        final Set<String> excludeFieldNames = excludeList.stream().map(Exclude::getField)
                .map(GenericAttributeValue::getXmlAttributeValue)
                .map(XmlAttributeValue::getValue)
                .collect(Collectors.toSet());

        return sourceFieldList.stream()
                .filter(item->{ return !excludeFieldNames.contains(item.getName().getXmlAttributeValue().getValue());})
                .toList();

    }

    /**
     * 根据别名找到对应的MemberRelationship
     * @param viewEntity
     * @param alias
     * @return
     */
    public static MemberRelationship getMemberRelationshipByAlias(@NotNull ViewEntity viewEntity, @NotNull String alias){
        return viewEntity.getMemberRelationshipList().stream()
                .filter(item->{
                    final String itemAlias = MyDomUtils.getXmlAttributeValueString(item.getEntityAlias()).orElse(MyStringUtils.EMPTY_STRING);
                    return alias.equals(itemAlias);
                    })
                .findFirst().orElse(null);
    }


    public static @NotNull Set<String> getEntityFieldNames(@NotNull Project project,@Nullable String entityName){
        List<Field> fieldList = getEntityFieldList(project,entityName);


        return  fieldList.stream()
                        .map(Field::getName)
                        .map(GenericAttributeValue::getValue)
                        .collect(Collectors.toSet())
        ;


    }

    /**
     * 获取所有视图名称，filterStr是视图名称的过滤条件
     * @param project
     * @param filterStr
     * @return
     */
    public static @NotNull Set<String> getViewEntityFullNames(@NotNull Project project,@Nullable String filterStr){
        Set<String> viewEntityNames = new HashSet<String>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加视图
            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
                var viewName = getFullNameFromViewEntity(viewEntity);
                if(isNotEmpty(filterStr)) {
                    if(viewEntityNames.contains(filterStr)) {viewEntityNames.add(viewName);}
                }else {
                    viewEntityNames.add(viewName);
                }
//                viewEntityNames.add(viewEntity.getPackage().getValue()+"."+viewEntity.getEntityName().getValue());
            };

        }
        return viewEntityNames;
    }
    public static @NotNull Set<String> getEntityAndViewEntityFullNames(@NotNull Project project,@Nullable String filterStr){
        Set<String> entityNames = new HashSet<String>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                var entityName = getFullNameFromEntity(entity);
                if(isNotEmpty(filterStr)) {
                    if(entityName.contains(filterStr)) {entityNames.add(entityName);}
                }else {
                    entityNames.add(entityName);
                }

            };
            //添加视图
            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
                var viewName = getFullNameFromViewEntity(viewEntity);
                if(isNotEmpty(filterStr)) {
                    if(entityNames.contains(filterStr)) {entityNames.add(viewName);}
                }else {
                    entityNames.add(viewName);
                }
//                viewEntityNames.add(viewEntity.getPackage().getValue()+"."+viewEntity.getEntityName().getValue());
            };

        }
        return entityNames;
    }
    public static @NotNull Set<String> getViewEntityAliases(@NotNull PsiElement psiElement){
        Set<String> fieldNames = new HashSet<String>();
        //获取Tag名
        ViewEntity entity = DomUtil.findDomElement(psiElement,ViewEntity.class);

        if (entity == null ) {
            return fieldNames;
        }
        fieldNames.addAll(
            entity.getMemberEntityList().stream()
                    .map(MemberEntity::getEntityAlias)
                    .map(GenericAttributeValue::getValue)
                    .collect(Collectors.toSet())
        );
        fieldNames.addAll(
                entity.getMemberRelationshipList().stream()
                        .map(MemberRelationship::getEntityAlias)
                        .map(GenericAttributeValue::getValue)
                        .collect(Collectors.toSet())
        );

        return fieldNames;
    }

    //    public static @NotNull Set<String> getEntityFullNames(@NotNull Project project, @NotNull String queryString){
//        Set<String> entityNames = getEntityFullNames(project);
//
//        return entityNames.stream().filter(item->item.indexOf(queryString)>=0)
//                .collect(Collectors.toSet());
//
//    }
//    public static @NotNull Set<String> getViewEntityFullNames(@NotNull Project project, @NotNull String queryString){
//        Set<String> viewEntityNames = getViewEntityFullNames(project);
//
//        return viewEntityNames.stream().filter(item->item.indexOf(queryString)>=0)
//                .collect(Collectors.toSet());
//
//    }
    public static String getFullName(@NotNull String name, @NotNull String packageName){
        return packageName + "." + name;
    }

    public static List<PsiElement> getRelatedEntity(@NotNull PsiElement psiElement, @NotNull String fullName) {
        List<PsiElement> resultList = new ArrayList<>();

        EntityPsiElementService entityPsiElementService =
                psiElement.getProject().getService(EntityPsiElementService.class);

        DomElement target = entityPsiElementService.getPsiElementByFullName(fullName);
        if (target == null) {
//            CustomNotifier.warn(psiElement.getProject(), "发现找不到的Entity，fullName：" + fullName +", 所在文件："
//                    +psiElement.getContainingFile().getVirtualFile().getPath());
        }else {
            resultList.add((PsiElement) target.getXmlElement());
        }

        return resultList;
    }

    public static Icon getNagavitorToEntityIcon() {
        return MyIcons.NAVIGATE_TO_ENTITY;
    }

    public static Icon getNagavitorToViewIcon() {
        return MyIcons.NAVIGATE_TO_VIEW;
    }
    public static String getNagavitorToEntityToolTips() {
        return "Navigating to Entity definition";
    }
    public static String getNagavitorToViewToolTips() {
        return "Navigating to View definition";
    }

    /**
     *  从对service-call的name中获取EntityName
     *  name中对CRUD的标准格式为create/update/delete#mantle.order.OrderItem
     * @param serviceCallName
     * @return
     */
    public static Optional<String> getEntityNameFromServiceCallName(@NotNull String serviceCallName) {
        String[] names = serviceCallName.split("#");

        if(names.length != 2) return Optional.empty();

        if((names[0].indexOf(".") < 0) && (names[1].indexOf(".") > 0)) {
            return  Optional.of(names[1]);
        }else {
            return Optional.empty();
        }

    }
    /**
     * 用在Inspection中
     * 根据指定的属性来进行Entity名称的验证
     *
     * @param attributeValue
     * @param holder
     * @param helper
     */
    public static void inspectEntityFromAttribute(@NotNull GenericAttributeValue attributeValue, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {
        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
        if (xmlAttributeValue == null) { return;}

        final String entityName = attributeValue.getXmlAttributeValue().getValue();
//        final int length = attributeValue.getXmlAttributeValue().getValueTextRange().getLength();
        final Project project =attributeValue.getXmlElement().getProject();

        Optional<XmlElement> optionalXmlElement = EntityUtils.findEntityAndViewEntityXmlElementByFullName(project, entityName);

        if (optionalXmlElement.isEmpty()) {
            holder.createProblem(attributeValue, HighlightSeverity.ERROR,"Entity is not found");
        }

    }

    /**
     * 根据TagName返回对应的icon，现在主要有Entity和ViewEntity
     * @param tagName
     * @return
     */
    public static Icon getIconByTagName(@NotNull String tagName){
        if (tagName.equals(Entity.TAG_NAME)) {
            return EntityUtils.getNagavitorToEntityIcon();
        }
        if(tagName.equals(ViewEntity.TAG_NAME)){
            return EntityUtils.getNagavitorToViewIcon();
        }
        return null;
    }

    /**
     * 从Relationship定义中获取RelatedName,主要用于Detail中的relationship属性值
     *  The relationship linking the master or parent detail to the detail. May be either short-alias or
     *  full relationship name (${title}#${related-entity-name} or just related-entity-name if no title).
     * @param relationship
     * @return
     */
    public static Optional<String> getRelatedNameFromRelationship(@NotNull Relationship relationship){
        if(relationship.getShortAlias().getXmlAttributeValue()!= null) {
            final String alias = relationship.getShortAlias().getXmlAttributeValue().getValue();
            if (MyStringUtils.isNotEmpty(alias)) return Optional.of(alias);
        }
        final String entityName = relationship.getRelated().getXmlAttribute().getValue();

        XmlAttributeValue titleAttr = relationship.getTitle().getXmlAttributeValue();
        if(titleAttr == null) {
            return Optional.of(entityName);
        }else {
            final String title = titleAttr.getValue();
            if(MyStringUtils.isEmpty(title)) {
                return Optional.of(entityName);
            }else {
                return Optional.of(title+"#" + entityName);
            }

        }




    }

    /**
     * 判断输入的字符串是否为relationship的RelatedName
     * @param thisRelationship
     * @param checkedStr
     * @return
     */
    public static Boolean isThisRelationshipRelatedName(@NotNull Relationship thisRelationship, @NotNull String checkedStr){
        if(thisRelationship.getShortAlias().getXmlAttributeValue() !=null) {
            final String alias = thisRelationship.getShortAlias().getXmlAttributeValue().getValue();
            if (MyStringUtils.isNotEmpty(alias))
                if (checkedStr.equals(alias)) return true;
        }

        final String entityName = thisRelationship.getRelated().getXmlAttribute().getValue();
        if(checkedStr.equals(entityName)) return true;

        if(thisRelationship.getTitle().getXmlAttributeValue() != null) {
            final String title = thisRelationship.getTitle().getXmlAttributeValue().getValue();
            if (MyStringUtils.isNotEmpty(title)) {
                if (checkedStr.equals(title + "#" + entityName)) return true;
            }
        }

        return false;

    }
    /**
     * 判断输入的字符串是否为entity的有效名称
     * 只含entityName或带有package的Name，都算有效
     * 如果是Entity，还需要进一步比较short-alias属性
     * @param thisEntity
     * @param checkedStr
     * @return
     */
    public static Boolean isThisEntityName(@NotNull AbstractEntity thisEntity, @NotNull String checkedStr){
        if(checkedStr.indexOf(".")<0) {
            if(thisEntity instanceof Entity entity) {
                Optional<String> optShortAlias = getEntityShortAlias(entity);
                if(optShortAlias.isPresent()) {
                    if(checkedStr.equals(optShortAlias.get())) return true;
                }

            }

            return thisEntity.getEntityName().getXmlAttributeValue().getValue().equals(checkedStr);
        }else {
            return getFullNameFromEntity(thisEntity).equals(checkedStr);
        }

    }
    /**
     * 获取Entity的ShortAlias属性

     * @return
     */
    public static Optional<String> getEntityShortAlias(@NotNull Entity entity){

        XmlAttributeValue value = entity.getShortAlias().getXmlAttributeValue();
        if(value == null) {
            return Optional.empty();

        }else {
            final String shortAlias = value.getValue();
            return Optional.ofNullable(shortAlias);
        }

    }
    /**
     * 判断输入的字符串是否为ExtendEntity的有效名称
     * 只含entityName或带有package的Name，都算有效
     * @param thisEntity
     * @param checkedStr
     * @return
     */
    public static Boolean isThisExtendEntityName(@NotNull ExtendEntity thisEntity, @NotNull String checkedStr){
        if(checkedStr.indexOf(".")<0) {
            return thisEntity.getEntityName().getXmlAttributeValue().getValue().equals(checkedStr);
        }else {
            return getFullNameFromExtendEntity(thisEntity).equals(checkedStr);
        }

    }
    /**
     * 根据ExtendEntity的EntityName属性的PsiReference，找到扩展的源Entity
     * @param context
     * @return
     */
    public static Optional<Entity> getExtendEntitySourceEntity(@NotNull ConvertContext context){
        ExtendEntity curExtendEntity = getLocalDomElementByConvertContext(context,ExtendEntity.class).orElse(null);
        if (curExtendEntity == null) return Optional.empty();
        PsiReference psiReference = curExtendEntity.getEntityName().getXmlAttributeValue().getReference();
        if (psiReference == null) return Optional.empty();

        PsiElement targetElement = psiReference.resolve();
        if (targetElement == null) return Optional.empty();

        Entity targetEntity = DomUtil.findDomElement(targetElement,Entity.class);
        if (targetEntity == null) {return Optional.empty();}
        else{ return Optional.of(targetEntity);}
    }
    /**
     * 获取当前ViewEntity所有定义的MemberEntity和MemberRelationship
     * @param context
     * @return
     */
    public static Collection<AbstractMemberEntity> getViewEntityAbstractMemberEntity(@NotNull ConvertContext context){

        ViewEntity viewEntity = getLocalDomElementByConvertContext(context,ViewEntity.class).orElse(null);
        if (viewEntity == null) return new ArrayList<>();

        return  getViewEntityAbstractMemberEntity(viewEntity);

    }
    public static Collection<AbstractMemberEntity> getViewEntityAbstractMemberEntity(@NotNull ViewEntity viewEntity){
        Collection<AbstractMemberEntity> result = new ArrayList<>();

        result.addAll(viewEntity.getMemberEntityList());
        result.addAll(viewEntity.getMemberRelationshipList());

        return result;
    }
    public static Optional<AbstractMemberEntity> getViewEntityAbstractMemberEntityByAlias(@NotNull ConvertContext context,@NotNull String alias) {
        return getViewEntityAbstractMemberEntity(context).stream()
                .filter(item ->{return alias.equals(item.getEntityAlias().getXmlAttributeValue().getValue());})
                .findFirst();
    }
    public static Optional<AbstractMemberEntity> getViewEntityAbstractMemberEntityByAlias(@NotNull ViewEntity viewEntity,@NotNull String alias) {

        return getViewEntityAbstractMemberEntity(viewEntity).stream()
                .filter(item ->{
                    final XmlAttributeValue value = item.getEntityAlias().getXmlAttributeValue();
                    if (value==null) return false;
                    final String itemAlias = value.getValue();
                    return alias.equals(itemAlias);
                })
                .findFirst();
    }

    /**
     * 获取MemberEntity不同属性对应的Entity或ViewEntity所对应的字段列表
     * @param member
     * @param attributeName
     * @return
     */
    public static List<AbstractField> getFieldListFromMemberEntity(@NotNull MemberEntity member,@NotNull String attributeName){
        List<AbstractField> result = new ArrayList<>();

        Project project = member.getXmlTag().getProject();
        String name = null;

        if(attributeName.equals(MemberEntity.ATTR_JOIN_FROM_ALIAS)) {
            ViewEntity viewEntity= DomUtil.getParentOfType(member,ViewEntity.class,true);
            final XmlAttributeValue attributeValue = member.getJoinFromAlias().getXmlAttributeValue();
            if(attributeValue == null) return result;

            MemberEntity alias = getDefinedMemberEntityByAlias(viewEntity
                    ,attributeValue.getValue());
            name = alias.getEntityName().getXmlAttributeValue().getValue();

        }
        if(attributeName.equals(MemberEntity.ATTR_ENTITY_NAME)) {
            name = member.getEntityName().getXmlAttributeValue().getValue();
        }

        result.addAll(getEntityOrViewEntityFields(project,name));
        return result;

    }
    /**
     * 获取MemberRelationship对应的字段列表
     * @param member
     * @return
     */
    public static List<AbstractField> getFieldListFromMemberRelationship(@NotNull MemberRelationship member){
        List<AbstractField> result = new ArrayList<>();

        Project project = member.getXmlTag().getProject();
        String name = null;

        ViewEntity viewEntity= DomUtil.getParentOfType(member,ViewEntity.class,true);
        AbstractMemberEntity alias = getViewEntityAbstractMemberEntityByAlias(viewEntity
                ,member.getJoinFromAlias().getXmlAttributeValue().getValue()).orElse(null);
        if(alias==null){ return result; }

        if(alias instanceof MemberEntity) {
            result.addAll(
                    getEntityRelationshipFieldList(project
                        ,((MemberEntity)alias).getEntityName().getXmlAttributeValue().getValue()
                        ,member.getRelationship().getXmlAttributeValue().getValue())
            );
        }
        //嵌套取字段
        if(alias instanceof MemberRelationship) {
            result.addAll(getFieldListFromMemberRelationship((MemberRelationship)alias));
        }
        return result;

    }
    public static List<AbstractField> getFieldListFromAbstractMemberEntity(@NotNull AbstractMemberEntity member){
        List<AbstractField> result = new ArrayList<>();
//        Project project = member.getXmlTag().getProject();

        if(member instanceof MemberEntity) {
            MemberEntity memberEntity=(MemberEntity)member;
            result.addAll(
                    getFieldListFromMemberEntity(memberEntity,MemberEntity.ATTR_ENTITY_NAME)
            );
        }

        if(member instanceof MemberRelationship) {
            result.addAll(getFieldListFromMemberRelationship((MemberRelationship)member));
        }

        return result;

    }

//
    /**
     * 获取Entity或ViewEntity的字段列表
     * @param project
     * @param entityName
     * @return
     */
    public static @NotNull Collection<AbstractField> getEntityOrViewEntityFields(@NotNull Project project, @NotNull String entityName){
        AbstractEntity entity = findEntityAndViewEntityByFullName(project,entityName).orElse(null);
        if(entity == null) return Collections.emptyList();
        Collection<AbstractField> result = new ArrayList<>();
        if(entity instanceof Entity) {
            result.addAll(getEntityFieldList(project,entityName));
        }
        if(entity instanceof ViewEntity) {
            result.addAll(getViewEntityFieldList(project,entityName));
        }
        return result;
    }
    public static Optional<Entity> getCurrentEntity(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Entity.class);

    }
    public static Optional<ExtendEntity> getCurrentExtendEntity(ConvertContext context){
        return getLocalDomElementByConvertContext(context,ExtendEntity.class);

    }

    public static Optional<Relationship> getCurrentRelationship(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Relationship.class);

    }
    public static Optional<MemberEntity> getCurrentMemberEntity(ConvertContext context){
        return getLocalDomElementByConvertContext(context,MemberEntity.class);
    }
    public static Optional<ECondition> getCurrentECondition(ConvertContext context){
        return getLocalDomElementByConvertContext(context,ECondition.class);
    }
    public static Optional<EntityCondition> getCurrentEntityCondition(ConvertContext context){
        return getLocalDomElementByConvertContext(context,EntityCondition.class);
    }

    /**
     * 根据ExtendEntity的EntityName属性的PsiReference，找到扩展的源Entity
     * @param context
     * @return
     */
    public static Optional<Entity> getCurrentExtendEntitySourceEntity(ConvertContext context){
        ExtendEntity curExtendEntity = getCurrentExtendEntity(context).orElse(null);
        if (curExtendEntity == null) return Optional.empty();
        PsiReference psiReference = curExtendEntity.getEntityName().getXmlAttributeValue().getReference();
        if (psiReference == null) return Optional.empty();

        PsiElement targetElement = psiReference.resolve();
        if (targetElement == null) return Optional.empty();

        Entity targetEntity = DomUtil.findDomElement(targetElement,Entity.class);
        if (targetEntity == null) {return Optional.empty();}
        else{ return Optional.of(targetEntity);}
    }

    /**
     * 找到当前所在位置的Entity对应的所有Relationship，不包括ExtendEntity的Relationship
     * @param context
     * @return
     */
    public static List<Relationship> getCurrentEntityRelationshipList(ConvertContext context){

        Entity entity = getLocalDomElementByConvertContext(context,Entity.class).orElse(null);
        if(entity == null) {
            return new ArrayList<Relationship>();
        }else {
            return entity.getRelationshipList();
        }

    }
    public static Optional<ViewEntity> getCurrentViewEntity(ConvertContext context){
        return getLocalDomElementByConvertContext(context,ViewEntity.class);

    }
    public static Optional<Alias> getCurrentAlias(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Alias.class);

    }
    public static Optional<AliasAll> getCurrentAliasAll(ConvertContext context){
        return getLocalDomElementByConvertContext(context,AliasAll.class);

    }
    public static Optional<ComplexAliasField> getCurrentComplexAliasField(ConvertContext context){
        return getLocalDomElementByConvertContext(context,ComplexAliasField.class);

    }
    public static Optional<MemberRelationship> getCurrentMemberRelationship(ConvertContext context){
        return getLocalDomElementByConvertContext(context,MemberRelationship.class);

    }
    public static Optional<AutoParameters> getCurrentAutoParameters(ConvertContext context){
        return getLocalDomElementByConvertContext(context,AutoParameters.class);

    }

    public static Optional<FieldMap> getCurrentFieldMap(ConvertContext context){
        return getLocalDomElementByConvertContext(context,FieldMap.class);

    }

    /**
     * 创建EntityName对应的psiReference
     * EntityName可能是下列3种形式：
     * 1、｛EntityName｝
     * 2、｛ShortAlias｝
     * 3、｛package｝.｛EntityName｝
     * 根据不同的内容，reference要分别对应到不同Entity属性上
     * @param project
     * @param element
     * @param entityName
     * @param startOffset 在element中的起始位置
     * @return
     */
    public static PsiReference @NotNull [] createEntityFullNameReferences(@NotNull Project project, @NotNull PsiElement element,@NotNull String  entityName,@NotNull int startOffset) {
        Optional<AbstractEntity> optEntity = EntityUtils.findEntityAndViewEntityByFullName(project,entityName);
        if (optEntity.isEmpty()) return PsiReference.EMPTY_ARRAY;

        List<PsiReference> psiReferences = new ArrayList<>();

        AbstractEntity abstractEntity = optEntity.get();
        if (entityName.indexOf(".")< 0) {
            //没有含包名
            //entityname reference or shortAlias reference
            XmlAttributeValue xmlAttributeValue;
            xmlAttributeValue = abstractEntity.getEntityName().getXmlAttributeValue();

            if(abstractEntity instanceof Entity entity) {
                Optional<String> optShortAlias = EntityUtils.getEntityShortAlias(entity);
                if(optShortAlias.isPresent()) {
                    if(optShortAlias.get().equals(entityName)) {
                        xmlAttributeValue = entity.getShortAlias().getXmlAttributeValue();
//                        psiReferences[1] = new PsiRef(element,
//                                new TextRange(startOffset,
//                                        entityName.length() + startOffset),
//                                entity.getShortAlias().getXmlAttributeValue());

                    }
                }
            }
            psiReferences.add(new PsiRef(element,
                    new TextRange(startOffset,
                            entityName.length() + startOffset),
                    xmlAttributeValue));

        }else {
            //package reference

            psiReferences.add(new PsiRef(element,
                    new TextRange(startOffset,
                            abstractEntity.getPackage().getStringValue().length()+startOffset),
                    abstractEntity.getPackage().getXmlAttributeValue()));


            //entityname reference
            psiReferences.add(new PsiRef(element,
                    new TextRange(startOffset + abstractEntity.getPackage().getStringValue().length() + 1,
                            startOffset + entityName.length()),
                    abstractEntity.getEntityName().getXmlAttributeValue()));

        }


        return psiReferences.toArray(new PsiReference[0]);
    }


}
