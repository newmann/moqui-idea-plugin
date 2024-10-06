package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import icons.MoquiIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.AbstractEntityOrViewNameReference;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.service.AbstractIndexEntity;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.service.IndexViewEntity;
import org.moqui.idea.plugin.service.MoquiIndexService;
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
    public static final String  ENTITY_NAME_DOT = ".";
    public static final String  ENTITY_FIELD_COMMA = ",";
    private EntityUtils() {
        throw new UnsupportedOperationException();
    }

//    public static final class EntityDescriptor{
//        EntityDescriptor(){}
//        EntityDescriptor(@NotNull String fullName){
//            int index = fullName.lastIndexOf('.');
//            if (index<0) {
//                entityName = fullName;
//                entityPackage = MyStringUtils.EMPTY_STRING;
//            }else {
//                entityPackage = fullName.substring(0,index);
//                entityName = fullName.substring(index+1);
//            };
//        }
//        EntityDescriptor(String entityName,String entityPackage){
//            this.entityName = entityName;
//            this.entityPackage = entityPackage;
//        }
//        public String entityName;
//        public String entityPackage;
//
//
//        public String getFullName(){
//            if(MyStringUtils.isEmpty(entityPackage)) {
//                return entityName;
//            }else {
//                return entityPackage+"." +entityPackage;
//            }
//
//        }
//    }

    public static boolean isEntitiesFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Entities.TAG_NAME,Entities.ATTR_NoNamespaceSchemaLocation,Entities.VALUE_NoNamespaceSchemaLocation);
    }
    /**
     * 根据所有的实体（Entity）,不含视图（ViewEntity）
     * @param project 当前项目
     * @return Collection<Entity>
     */
    public static Collection<String> getAllEntityFullNameCollection(@NotNull Project project){
        return getAllEntityCollection(project).stream().map(EntityUtils::getFullNameFromEntity).collect(Collectors.toSet());
    }
    /**
     * 根据所有的视图（ViewEntity），不包含实体（Entity）
     * @param project 当前项目
     * @return Collection<String>
     */
    public static Collection<String> getAllViewEntityFullNameCollection(@NotNull Project project){
        return getAllViewEntityCollection(project).stream().map(EntityUtils::getFullNameFromEntity).collect(Collectors.toSet());
    }
    /**
     * 根据所有的实体
     * @param project 当前项目
     * @return Collection<Entity>
     */
    public static Collection<Entity> getAllEntityCollection(@NotNull Project project){
//        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
//        return moquiIndexService.getAllEntity().orElse(new ArrayList<>());
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);
//        Collection<Entity> result = new ArrayList<>();
        return  fileElementList.stream().map(DomFileElement::getRootElement)
                .map(Entities::getEntities)
                .flatMap(List::stream)
                .collect(Collectors.toList());
//        for(DomFileElement<Entities> fileElement : fileElementList) {
//            result.addAll(fileElement.getRootElement().getEntities());
//        }
//        return result;
    }
    public static Map<String,DomElement> getAllEntityAndViewEntityDomElementMap(@NotNull Project project){
        return getAllEntityAndViewEntityCollection(project).stream().collect(
                Collectors.toMap(EntityUtils::getFullNameFromEntity,AbstractEntity->AbstractEntity)
        );

//        Map<String,DomElement> result = new HashMap<String,DomElement>();
//        getAllEntityAndViewEntityCollection(project)
//                .forEach((item)->{result.put(getFullNameFromEntity(item),item);});
//        return result;

    }
    public static Collection<ViewEntity> getAllViewEntityCollection(@NotNull Project project){
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);

        return  fileElementList.stream().map(DomFileElement::getRootElement)
                .map(Entities::getViewEntities)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
    public static Collection<AbstractEntity> getAllEntityAndViewEntityCollection(@NotNull Project project){
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);
        Collection<AbstractEntity> result = new ArrayList<>();
        for(DomFileElement<Entities> fileElement : fileElementList) {
            result.addAll(fileElement.getRootElement().getEntities());
            result.addAll(fileElement.getRootElement().getViewEntities());
        }
        return result;
    }



    /**
     * 从Entity定义中拼接出完整名称
     */
    public static String getFullNameFromEntity(AbstractEntity entity) {
        return MyDomUtils.getValueOrEmptyString(entity.getPackage())
                + ENTITY_NAME_DOT + MyDomUtils.getValueOrEmptyString(entity.getEntityName());
    }

    public static String getFullNameFromExtendEntity(ExtendEntity entity) {
        return MyDomUtils.getValueOrEmptyString(entity.getPackage())
                + ENTITY_NAME_DOT + MyDomUtils.getValueOrEmptyString(entity.getEntityName());
    }


    /**
     * 根据实体名和包名找到对应的实体定义的XmlElement
     * @param project
     * @param entityName
     * @return
     */
    public static Optional<XmlElement[]> getEntityElementsByName(@NotNull Project project, @NotNull String entityName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        Optional<AbstractEntity> entity = moquiIndexService.getEntityOrViewEntity(entityName);
        if(entity.isPresent()) {
            XmlElement[] result = {entity.get().getXmlElement()};
            return Optional.of(result);
        }else {
            return Optional.empty();
        }

    }

    /**
     * 根据字符串，找到对应的Entity或ViewEntity对应的XmlElement
     * @param project
     * @param name
     * @return
     */
    public static Optional<XmlElement> getEntityOrViewEntityXmlElementByName(@NotNull Project project
            , @NotNull String name){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);

        Optional<AbstractEntity> entity = moquiIndexService.getEntityOrViewEntity(name);
        return entity.map(AbstractEntity::getXmlElement);
    }


    /**
     * 根据字符串找到指定的Entity或ViewEntity
     * @param project
     * @param name fullName或者shortAlias，EntityName也可以
     * @return
     */
    public static Optional<AbstractEntity> getEntityOrViewEntityByName(@NotNull Project project
            , @NotNull String name){
        MoquiIndexService moqiIndexService = project.getService(MoquiIndexService.class);
        return moqiIndexService.getEntityOrViewEntity(name);

    }


    /**
     * 根据全名找到指定的Entity
     * @param project 当前的Project
     * @param name 待查找的名称，可以带packageName，也可以不带
     * @return 对应的Entity
     */
    public static Optional<Entity> getEntityByName(@NotNull Project project
            , @NotNull String name){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getEntityByName(name);


    }

    public static Optional<ViewEntity> getViewEntityByNameFromFile(@NotNull Project project
            , @NotNull String name){
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
                if(isThisEntityName(viewEntity,name)){return Optional.of(viewEntity);}
            }

        }
        return Optional.empty();
    }
    public static Optional<Entity> getEntityByNameFromFile(@NotNull Project project
            , @NotNull String name){
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                if(isThisEntityName(entity,name)){return Optional.of(entity);}
            }

        }
        return Optional.empty();
    }
    public static Optional<List<ExtendEntity>> getExtendEntityListByNameFromFile(@NotNull Project project
            , @NotNull String name){
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);
        List<ExtendEntity> result = new ArrayList<>();

        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(ExtendEntity entity: fileElement.getRootElement().getExtendEntities()) {
                if(isThisExtendEntityName(entity,name)){result.add(entity);}
            }

        }
        return Optional.of(result);
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

//    /**
//     * 获取指定Entity的所有Relationship的shortAlias
//     * @param project
//     * @param entityName
//     * @return
//     */
//    public static @NotNull Set<String> getRelationshipShortAliases(@NotNull Project project,@NotNull String entityName){
////        Set<String> attrNames = new HashSet<String>();
//        List<Relationship> relationshipList = getEntityRelationshipList(project,entityName);
//
//        return  relationshipList.stream().map(Relationship::getShortAlias)
//                .map(GenericAttributeValue::getXmlAttributeValue)
//                .map(XmlAttributeValue::getValue)
//                .collect(Collectors.toSet());
//    }

    /**
     * 获取Entity的所有Relationship，包含ExtendEntity
     * @param project
     * @param entityName
     * @return
     */
    public static @NotNull List<Relationship> getEntityRelationshipList(@NotNull Project project,@NotNull String entityName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getRelationshipListByEntityName(entityName).orElse(new ArrayList<Relationship>());
    }
//    /**
//     * 获取所有定义的ViewEntity某个属性的内容
//     * @param project
//     * @param attributeName
//     * @param filterStr
//     * @return
//     */
//    public static @NotNull Set<String> getViewEntityAttributes(@NotNull Project project,@NotNull String attributeName,@Nullable String filterStr){
//        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
//        List<ViewEntity> viewEntityList = moquiIndexService.getAllViewEntity().orElse(new ArrayList<>());
//                Set<String> attrNames = new HashSet<String>();
//        for(ViewEntity entity: viewEntityList) {
//            MyDomUtils.getXmlTagAttributeValueByAttributeName(entity.getXmlTag(), attributeName, filterStr)
//                .ifPresent(attrNames::add);
//        };
//        return attrNames;
//
//    }

    /**
     * 获取所有定义的Entity或ViewEntity的某个属性的内容
     * @param project
     * @param attributeName
     * @param filterStr
     * @return
     */
    public static @NotNull Set<String> getEntityAndViewEntityAttributes(@NotNull Project project,@NotNull String attributeName,@Nullable String filterStr){
        Set<String> attrNames = new HashSet<String>();

        Collection<Entity> entityList = getAllEntityCollection(project);
        for(Entity entity:entityList) {
            MyDomUtils.getXmlTagAttributeValueByAttributeName(entity.getXmlTag(), attributeName, filterStr)
                    .ifPresent(attrNames::add);
        }
        Collection<ViewEntity> viewEntityList = getAllViewEntityCollection(project);
        for(ViewEntity entity:viewEntityList) {
            MyDomUtils.getXmlTagAttributeValueByAttributeName(entity.getXmlTag(), attributeName, filterStr)
                    .ifPresent(attrNames::add);
        }
        return attrNames;
    }
    public static @NotNull Set<String> getEntityFullNameSet(@NotNull Project project, @Nullable String filterStr){
        return getAllEntityFullNameCollection(project).stream()
                .filter(item->{
                    if(MyStringUtils.isEmpty(filterStr)) {
                        return true;
                    }else{
                        return item.contains(filterStr);
                    }
                }).collect(Collectors.toSet());
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
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        Optional<IndexEntity> indexEntity = moquiIndexService.getIndexEntityByName(entityName);
        return indexEntity.map(entity -> entity.getFieldList().orElse(new ArrayList<>())).orElseGet(ArrayList::new);

    }

//    /**
//     *
//     * @param project
//     * @param entityName
//     * @return
//     */
//    public static @NotNull List<Field> getEntityFieldList(@NotNull Project project,@NotNull String entityName){
//        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
//        Optional<IndexEntity> indexEntity = moquiIndexService.getIndexEntityByName(entityName);
//        return indexEntity.map(entity -> entity.getFieldList().orElse(new ArrayList<>())).orElseGet(ArrayList::new);
//
//    }
    /**
     * 获取ViewEntity的字段列表
     * @param project
     * @param name
     * @return
     */
    public static @NotNull Optional<List<AbstractField>> getViewEntityFieldList(@NotNull Project project, @NotNull String name){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        Optional<IndexViewEntity> indexViewEntity = moquiIndexService.getIndexViewEntityByName(name);
        return indexViewEntity.map(entity->entity.getAbstractFieldList().orElse(new ArrayList<>()));

    }
    public static @NotNull Optional<List<AbstractField>> getViewEntityFieldList(@NotNull ViewEntity viewEntity) {
        final Project project;
        if(viewEntity.getXmlElement() ==null) {
            return Optional.empty();
        }else {
            project = viewEntity.getXmlElement().getProject();
        }

        String entityName = MyDomUtils.getValueOrEmptyString(viewEntity.getEntityName());
        return getViewEntityFieldList(project, entityName);
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
                    final String itemAlias =MyDomUtils.getValueOrEmptyString(item.getEntityAlias());
                    return alias.equals(itemAlias);})
                .findFirst().orElse(null);

    }


    public static List<AbstractField> excludeFields(List<AbstractField> sourceFieldList,List<Exclude> excludeList){
        final Set<String> excludeFieldNames = excludeList.stream().map(Exclude::getField)
                .map(GenericAttributeValue::getXmlAttributeValue).filter(Objects::nonNull)
                .map(XmlAttributeValue::getValue)
                .collect(Collectors.toSet());

        return sourceFieldList.stream()
                .filter(item->{ return !excludeFieldNames.contains(MyDomUtils.getValueOrEmptyString(item.getName()));})
                .toList();

    }

    /**
     * 获取所有视图名称，filterStr是视图名称的过滤条件
     * @param project
     * @param filterStr
     * @return
     */
    public static @NotNull Set<String> getViewEntityFullNameSet(@NotNull Project project, @Nullable String filterStr){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        Map<String,IndexViewEntity> indexViewEntityMap = moquiIndexService.getIndexViewEntityMap();
        Set<String> viewEntityNames = new HashSet<String>();
        for(String key: indexViewEntityMap.keySet()) {
            viewEntityNames.add(indexViewEntityMap.get(key).getFullName());
        }

        return viewEntityNames;
    }

    public static String getFullName(@NotNull String name, @NotNull String packageName){
        return packageName + ENTITY_NAME_DOT + name;
    }

    public static List<PsiElement> getRelatedEntity(@NotNull PsiElement psiElement, @NotNull String fullName) {
        List<PsiElement> resultList = new ArrayList<>();

        MoquiIndexService moquiIndexService =
                psiElement.getProject().getService(MoquiIndexService.class);

        Optional<Entity> target = moquiIndexService.getEntityByName(fullName);
        target.ifPresent(entity -> resultList.add(entity.getXmlElement()));

        return resultList;
    }

    public static Icon getNagavitorToEntityIcon() {
        return MoquiIcons.NavigateToEntity; //MyIcons.NAVIGATE_TO_ENTITY;
    }

    public static Icon getNagavitorToViewIcon() {
        return MoquiIcons.NavigateToView; //MyIcons.NAVIGATE_TO_VIEW;
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
     */
    public static void inspectEntityFromAttribute(@NotNull GenericAttributeValue<String> attributeValue, @NotNull DomElementAnnotationHolder holder) {
        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
        if (xmlAttributeValue == null) { return;}

        final String entityName = attributeValue.getXmlAttributeValue().getValue();
//        final int length = attributeValue.getXmlAttributeValue().getValueTextRange().getLength();
        final Project project = xmlAttributeValue.getProject();

        Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(project, entityName);

        if (optionalXmlElement.isEmpty()) {
            holder.createProblem(attributeValue, HighlightSeverity.ERROR,"Entity is not found");
        }

    }
    public static void inspectEntityFromAttribute(@NotNull GenericAttributeValue<String> attributeValue, @NotNull AnnotationHolder holder) {
        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
        if (xmlAttributeValue == null) { return;}

        final String entityName = attributeValue.getXmlAttributeValue().getValue();
//        final int length = attributeValue.getXmlAttributeValue().getValueTextRange().getLength();
        final Project project = xmlAttributeValue.getProject();

        Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(project, entityName);

        if (optionalXmlElement.isEmpty()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Entity is not found")
                    .range(xmlAttributeValue.getTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
        }

    }
    public static void inspectExtendEntity(@NotNull ExtendEntity extendEntity, @NotNull DomElementAnnotationHolder holder) {
        String entityName = MyDomUtils.getValueOrEmptyString(extendEntity.getEntityName());
        XmlTag xmlTag = extendEntity.getXmlTag();
        if(xmlTag == null) {return;}

        Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(
                xmlTag.getProject(), entityName);

        if (optionalXmlElement.isEmpty()) {
            int start = xmlTag.getTextOffset();
            int length = xmlTag.getLocalName().length();

            holder.createProblem(extendEntity, ProblemHighlightType.ERROR,"Entity is not found",
                    TextRange.from(1, length));
        }
    }
    public static void inspectExtendEntity(@NotNull ExtendEntity extendEntity, @NotNull AnnotationHolder holder) {
        String entityName = MyDomUtils.getValueOrEmptyString(extendEntity.getEntityName());
        XmlTag xmlTag = extendEntity.getXmlTag();
        if(xmlTag == null) {return;}

        Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(
                xmlTag.getProject(), entityName);

        if (optionalXmlElement.isEmpty()) {
            int start = xmlTag.getTextOffset();
            int length = xmlTag.getLocalName().length();
            holder.newAnnotation(HighlightSeverity.ERROR, "Entity is not found")
                    .range(TextRange.from(1, length))
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
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
     * @param thisRelationship 当前Relationship
     * @param checkedStr 判断的字符串
     * @return
     */
    public static Boolean isThisRelationshipRelatedName(@NotNull Relationship thisRelationship, @NotNull String checkedStr){
        final String shortAlias = MyDomUtils.getValueOrEmptyString(thisRelationship.getShortAlias());
        if (checkedStr.equals(shortAlias)) return true;

        final String entityName = MyDomUtils.getValueOrEmptyString(thisRelationship.getRelated());
        if(checkedStr.equals(entityName)) return true;

        final String title = MyDomUtils.getValueOrEmptyString(thisRelationship.getTitle());
        return checkedStr.equals(title + "#" + entityName);

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
        if(!checkedStr.contains(".")) {
            if(thisEntity instanceof Entity entity) {
                Optional<String> optShortAlias = getEntityShortAlias(entity);
                if(optShortAlias.isPresent()) {
                    if(checkedStr.equals(optShortAlias.get())) return true;
                }

            }

            return MyDomUtils.getValueOrEmptyString(thisEntity.getEntityName()).equals(checkedStr);
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
        if(checkedStr.contains(".")) {
            return getFullNameFromExtendEntity(thisEntity).equals(checkedStr);
        }else {
            return MyDomUtils.getValueOrEmptyString(thisEntity.getEntityName()).equals(checkedStr);
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
     * 根据ViewEntity找到IndexViewEntity
     * @param viewEntity
     * @return
     */
    public static Optional<IndexViewEntity> getIndexViewEntityByViewEntity(@NotNull ViewEntity viewEntity){
        final XmlElement xmlElement = viewEntity.getXmlElement();
        if(xmlElement == null) return Optional.empty();
        final Project project = xmlElement.getProject();
        final MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getIndexViewEntityByName(getFullNameFromEntity(viewEntity));
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
                .filter(item -> alias.equals(item.getEntityAlias().getStringValue()))
                .findFirst();
    }
    public static Optional<AbstractMemberEntity> getViewEntityAbstractMemberEntityByAlias(@NotNull ViewEntity viewEntity,@NotNull String alias) {

        return getViewEntityAbstractMemberEntity(viewEntity).stream()
                .filter(item -> alias.equals(item.getEntityAlias().getStringValue()))
                .findFirst();
    }

    /**
     * 根据EntityAlias找到对应的AbstractIndexEntity，获取FieldList等
     * @param viewEntity
     * @param alias
     * @return
     */
    public static Optional<AbstractIndexEntity> getViewEntityAbstractIndexEntityByAlias(@NotNull ViewEntity viewEntity, @NotNull String alias) {
        final Project project;
        if(viewEntity.getXmlElement() ==null) {
            return Optional.empty();
        }else {
            project = viewEntity.getXmlElement().getProject();
        }

        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        String viewEntityFullName = getFullNameFromEntity(viewEntity);

        Optional<IndexViewEntity> optionalIndexViewEntity = moquiIndexService.getIndexViewEntityByName(viewEntityFullName);

        if(optionalIndexViewEntity.isPresent()) {
            return optionalIndexViewEntity.get().getAbstractIndexEntityByAlias(alias);
        }else {
            return Optional.empty();
        }
    }
    /**
     * 根据EntityAlias找到对应的AbstractIndexEntity，直接返回AbstractFieldList等
     * @param viewEntity
     * @param alias
     * @return
     */
    public static Optional<List<AbstractField>> getAbstractFieldListFromViewEntityByAlias(@NotNull ViewEntity viewEntity,@NotNull String alias) {

        AbstractIndexEntity abstractIndexEntity = getViewEntityAbstractIndexEntityByAlias(
                viewEntity,
                alias
        ).orElse(null);
        if(abstractIndexEntity == null) return Optional.empty();

        return abstractIndexEntity.getAbstractFieldList();
    }

//
    /**
     * 获取Entity或ViewEntity的字段列表
     * @param project
     * @param entityName
     * @return
     */
    public static @NotNull Collection<AbstractField> getEntityOrViewEntityFields(@NotNull Project project, @NotNull String entityName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        Optional<List<AbstractField>> result = moquiIndexService.getEntityOrViewEntityFieldList(entityName);
        return result.orElse(Collections.emptyList());
    }
    public static Optional<Entity> getCurrentEntity(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Entity.class);

    }
    public static Optional<IndexField> getCurrentIndexField(ConvertContext context){
        return getLocalDomElementByConvertContext(context,IndexField.class);

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
    public static Optional<KeyMap> getCurrentKeyMap(ConvertContext context){
        return getLocalDomElementByConvertContext(context,KeyMap.class);
    }
    public static Optional<KeyValue> getCurrentKeyValue(ConvertContext context){
        return getLocalDomElementByConvertContext(context,KeyValue.class);
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
    public static Optional<ComplexAlias> getCurrentComplexAlias(ConvertContext context){
        return getLocalDomElementByConvertContext(context,ComplexAlias.class);
    }
    public static Optional<AliasAll> getCurrentAliasAll(ConvertContext context){
        return getLocalDomElementByConvertContext(context,AliasAll.class);

    }
    public static Optional<Exclude> getCurrentExclude(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Exclude.class);

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

    @Deprecated
    public static @NotNull PsiReference[] createEntityNameReferences(@NotNull Project project, @NotNull PsiElement element, @NotNull String  entityName, @NotNull int startOffset) {
        Optional<AbstractEntity> optEntity = EntityUtils.getEntityOrViewEntityByName(project,entityName);
        if (optEntity.isEmpty()) return PsiReference.EMPTY_ARRAY;

        List<PsiReference> psiReferences = new ArrayList<>();

        AbstractEntity abstractEntity = optEntity.get();
        if (!entityName.contains(".")) {
            //没有含包名
            //entityname reference or shortAlias reference
            XmlAttributeValue xmlAttributeValue;
            xmlAttributeValue = abstractEntity.getEntityName().getXmlAttributeValue();

            if(abstractEntity instanceof Entity entity) {
                String shortAlias = MyDomUtils.getValueOrEmptyString(entity.getShortAlias());
                if(shortAlias.equals(entityName)) {
                    xmlAttributeValue = entity.getShortAlias().getXmlAttributeValue();

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
    /**
     * 创建EntityName对应的psiReference
     * EntityName可能是下列3种形式：
     * 1、｛EntityName｝
     * 2、｛ShortAlias｝
     * 3、｛package｝.｛EntityName｝
     * 根据不同的内容，reference要分别对应到不同Entity属性上
     * @param project
     * @param element
     * @return
     */
    public static @NotNull PsiReference[] createEntityOrViewNameReferences(@NotNull Project project, @NotNull PsiElement element,@NotNull EntityScope entityScope) {
        BeginAndEndCharPattern stringPattern = BeginAndEndCharPattern.of(element);
        List<PsiReference> psiReferences = new ArrayList<>();
        int tmpStartOffset,tmpEndOffset;



        String entityName = stringPattern.getContent();
        if(entityName.isBlank()){
            tmpStartOffset = stringPattern.getBeginChar().length();
            tmpEndOffset = tmpStartOffset;
            psiReferences.add(createEntityOrViewNameReference(element,
                    new TextRange(tmpStartOffset,tmpEndOffset),null,entityScope));//提供 code completion

        }else {
            Optional<AbstractEntity> entityOptional = EntityUtils.getEntityOrViewEntityByName(project, entityName);
            int lastDotIndex = entityName.lastIndexOf('.');
            if(entityOptional.isPresent()) {
                if(lastDotIndex >0) {
                    tmpStartOffset = stringPattern.getBeginChar().length();
                    tmpEndOffset = stringPattern.getBeginChar().length() + lastDotIndex;
                    //package reference
                    psiReferences.add(createEntityOrViewNameReference(element,
                            new TextRange(tmpStartOffset,tmpEndOffset),
                            MyDomUtils.getPsiElementFromAttributeValue(entityOptional.get().getPackage().getXmlAttributeValue()).orElse(null),
                            entityScope)
                    );
                    tmpStartOffset = stringPattern.getBeginChar().length()+ lastDotIndex +1;
                    tmpEndOffset = stringPattern.getBeginChar().length() + entityName.length();

                    //entityname reference
                    psiReferences.add(createEntityOrViewNameReference(element,
                            new TextRange(tmpStartOffset,tmpEndOffset),
                            MyDomUtils.getPsiElementFromAttributeValue(entityOptional.get().getEntityName().getXmlAttributeValue()).orElse(null),
                            entityScope)
                    );
                }else {
                    tmpStartOffset = stringPattern.getBeginChar().length();
                    tmpEndOffset = stringPattern.getBeginChar().length() + entityName.length();
                    XmlAttributeValue attributeValue;
                    //entityname reference，需要考虑shortAlias
                    attributeValue = entityOptional.get().getEntityName().getXmlAttributeValue();
                    if(entityOptional.get() instanceof Entity entity) {
                        if(MyDomUtils.getValueOrEmptyString(entity.getShortAlias()).equals(entityName)) {
                            attributeValue = entity.getShortAlias().getXmlAttributeValue();
                        }
                    }
                    psiReferences.add(createEntityOrViewNameReference(element,
                            new TextRange(tmpStartOffset, tmpEndOffset),
                            MyDomUtils.getPsiElementFromAttributeValue(attributeValue).orElse(null),
                            entityScope)
                    );
                }

            }else {
                if (lastDotIndex <= 0) {
                    //没有含包名
                    tmpStartOffset = stringPattern.getBeginChar().length();
                    tmpEndOffset = tmpStartOffset + entityName.length();
                } else {
                    tmpStartOffset = stringPattern.getBeginChar().length() + lastDotIndex + 1;
                    tmpEndOffset = stringPattern.getBeginChar().length() + entityName.length();

                }
                psiReferences.add(createEntityOrViewNameReference(element,
                        new TextRange(tmpStartOffset,tmpEndOffset),
                        null,entityScope));

            }

        }
        return psiReferences.toArray(new PsiReference[0]);
    }
    public static PsiReference createEntityOrViewNameReference(@NotNull PsiElement element, @NotNull TextRange textRange, @Nullable PsiElement resolve,@NotNull EntityScope entityScope){
        return switch (entityScope) {
            case ENTITY_ONLY -> AbstractEntityOrViewNameReference.ofEntityNameReference(element, textRange,resolve);
            case VIEW_ONLY -> AbstractEntityOrViewNameReference.ofViewEntityNameReference(element, textRange,resolve);
            case ENTITY_AND_VIEW -> AbstractEntityOrViewNameReference.ofEntityAndViewEntityNameReference(element, textRange,resolve);
        };
    }
    /**
     * 根据Entity的Name和Package查找所有的ExtendEntity
     * @param project 当前项目
     * @param entityName
     * @return Collection<ExtendEntity>
     */
    public static Optional<List<ExtendEntity>> getExtendEntityListByName(@NotNull Project project, @NotNull String entityName){

        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getExtendEntityListByEntityName(entityName);

    }

    /**
     * 判断输入的字符串是否为全称格式，即是否包含“.”
     * @param name 待判断的字符串
     * @return boolean
     *
     */
    public static boolean isFullName(@NotNull String name) {
        return name.indexOf(ENTITY_NAME_DOT) > 0;
    }

    /**
     * 判断一个字段的名字是否为groovy变量，如果内部包含${,则判断为groovy变量
     * @param fieldName 字段名
     * @return boolean
     */
    public static boolean fieldIsGroovyVariable(@NotNull String fieldName){
        return  fieldName.contains("${");
    }

    /**
     * 字段名的第一个字符是否为排序控制字符
     * @param fieldName 字段名
     * @return boolean
     */
    public static boolean fieldHasOrderCommand(@NotNull String fieldName){
        if(fieldName.isBlank()) return false;
        return ServiceUtils.ORDER_BY_COMMANDER.contains(fieldName.substring(0,1));
    }

    /**
     * 将多个字段组成的字符串拆分
     *
     * 字段实例：
     * 1、 orderId,orderPartSeqId,shipmentMethodEnumId,quantityTotal
     * 2、resQuantity${onlyPartlyShipped != 'true' ? '' : ',issuedQuantity'}${excludeNotPickLoc == 'false' ? '' : ',itemBomQuantityTotal'}
     * @param fieldString
     * @return
     */
    public static Optional<List<FieldStringSplitUnit>> splitFieldString(@NotNull String fieldString) {
        List<FieldStringSplitUnit> result = new ArrayList<>();
        StringBuilder curField= new StringBuilder(MyStringUtils.EMPTY_STRING);
        boolean inVariable = false;
        int beginIndex = 0;

        int fieldLength = fieldString.length();
        for(int i =0; i< fieldLength;i++){
            if(fieldString.substring(i,i+1).equals(EntityUtils.ENTITY_FIELD_COMMA)){
                if(!inVariable) {
                    FieldStringSplitUnit fieldStringSplitUnit = new FieldStringSplitUnit(curField.toString(),beginIndex,i);

                    result.add(fieldStringSplitUnit);
                    curField = new StringBuilder(MyStringUtils.EMPTY_STRING);
                    beginIndex = i+1;
                }
            }else {

                if((i<=fieldLength-2) && fieldString.substring(i,i+2).equals("${")) {
                    inVariable = true;

                }
                if(fieldString.charAt(i) == '}') {
                    inVariable = false;
                }

                curField.append(fieldString, i, i+1);
            }
        }
        if(!curField.toString().equals(MyStringUtils.EMPTY_STRING)) {
            FieldStringSplitUnit fieldStringSplitUnit = new FieldStringSplitUnit(curField.toString(),beginIndex,fieldLength);
            result.add(fieldStringSplitUnit);
        }

        return Optional.of(result);
    }

}
