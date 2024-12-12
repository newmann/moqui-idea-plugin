package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.AbstractEntityOrViewNameReference;
import org.moqui.idea.plugin.reference.EntityFieldNameReference;
import org.moqui.idea.plugin.service.*;

import javax.swing.*;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

import static org.moqui.idea.plugin.util.MyDomUtils.*;
import static org.moqui.idea.plugin.util.MyStringUtils.isNotEmpty;


public final class EntityUtils {
    public static final String  ENTITY_NAME_DOT = ".";
    public static final String  ENTITY_FIELD_COMMA = ",";
    private EntityUtils() {
        throw new UnsupportedOperationException();
    }

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

        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);
        return  fileElementList.stream().map(DomFileElement::getRootElement)
                .map(Entities::getEntities)
                .flatMap(List::stream)
                .collect(Collectors.toList());

    }

    public static Collection<ViewEntity> getAllViewEntityCollection(@NotNull Project project){
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);

        return  fileElementList.stream().map(DomFileElement::getRootElement)
                .map(Entities::getViewEntities)
                .flatMap(List::stream)
                .collect(Collectors.toList());
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
     * 根据字符串，找到对应的Entity或ViewEntity对应的XmlElement
     * @param project 当前的Project
     * @param name 字符串
     * @return Optional<XmlElement>
     */
    public static Optional<XmlElement> getEntityOrViewEntityXmlElementByName(@NotNull Project project
            , @NotNull String name){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);

        Optional<AbstractEntity> entity = moquiIndexService.getEntityOrViewEntity(name);
        return entity.map(AbstractEntity::getXmlElement);
    }


    /**
     * 根据字符串找到指定的Entity或ViewEntity
     * @param project 当前的Project
     * @param name fullName或者shortAlias，EntityName也可以
     * @return Optional<AbstractEntity>
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
    public static Optional<IndexEntity> getIndexEntityByName(@NotNull Project project
            , @NotNull String name){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getIndexEntityByName(name);
    }
    public static Optional<AbstractIndexEntity> getAbstractIndexEntityByName(@NotNull Project project
            , @NotNull String name){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getIndexEntityOrIndexViewEntity(name);
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
    public static List<ExtendEntity> getExtendEntityListByNameFromFile(@NotNull Project project
            , @NotNull String name){
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class);
        List<ExtendEntity> result = new ArrayList<>();

        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(ExtendEntity entity: fileElement.getRootElement().getExtendEntities()) {
                if(isThisExtendEntityName(entity,name)){result.add(entity);}
            }

        }
        return result;
    }


    /**
     * 获取所有定义Entity的指定属性内容
     * @param project 当前Project
     * @param attributeName 待查找的属性名称
     * @param filterStr 过滤字符串
     * @return Set<String>
     */
    public static @NotNull Set<String> getEntityAttributes(@NotNull Project project,@NotNull String attributeName,@Nullable String filterStr){
        Set<String> attrNames = new HashSet<>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体的属性名
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                XmlTag xmlTag = entity.getXmlTag();
                if(xmlTag == null) continue;
                XmlAttribute xmlAttribute = xmlTag.getAttribute(attributeName);
                if(xmlAttribute == null) continue;
                var name = xmlAttribute.getValue();
                if(name != null) {
                    if(isNotEmpty(filterStr)) {
                        if(name.contains(filterStr)) {attrNames.add(name);}
                    }else {
                        attrNames.add(name);
                    }
                }

            }
        }
        return attrNames;
    }

    /**
     * 获取Entity的所有Relationship，包含ExtendEntity
     * @param project 当前Project
     * @param entityName Entity Name
     * @return  List<Relationship>
     */
    public static @NotNull List<Relationship> getEntityRelationshipList(@NotNull Project project,@NotNull String entityName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getRelationshipListByEntityName(entityName);
    }


    /**
     * 获取所有定义的Entity或ViewEntity的某个属性的内容
     * @param project 当前Project
     * @param attributeName 待查找的属性名称
     * @param filterStr 过滤字符串
     * @return  Set<String>
     */
    public static @NotNull Set<String> getEntityAndViewEntityAttributes(@NotNull Project project,@NotNull String attributeName,@Nullable String filterStr){
        Set<String> attrNames = new HashSet<>();

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
     * 获取ViewEntity的字段列表
     * @param project 当前Project
     * @param entityName  View Entity Name
     * @return List<IndexAbstractField>
     */
    public static @NotNull List<IndexAbstractField> getViewEntityIndexAbstractFieldList(@NotNull Project project, @NotNull String entityName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        Optional<IndexViewEntity> indexViewEntity = moquiIndexService.getIndexViewEntityByName(entityName);
        return indexViewEntity.map(AbstractIndexEntity::getIndexAbstractFieldList).orElse(new ArrayList<>());

    }

    public static @NotNull List<IndexAbstractField> getViewEntityIndexAbstractFieldList(@NotNull ViewEntity viewEntity) {
        final Project project;
        if(viewEntity.getXmlElement() ==null) {
            return new ArrayList<>();
        }else {
            project = viewEntity.getXmlElement().getProject();
        }

        String entityName = MyDomUtils.getValueOrEmptyString(viewEntity.getEntityName());
        return getViewEntityIndexAbstractFieldList(project, entityName);
    }

    /**
     * 根据别名找到MemberEntity，不对MemberRelationship进行处理
     * @param viewEntity View Entity
     * @param alias Alias Name
     * @return Optional<MemberEntity>
     */
    public static Optional<MemberEntity> getMemberEntityByAlias(@NotNull ViewEntity viewEntity, @NotNull String alias){

        return viewEntity.getMemberEntityList().stream()
                .filter(item->{
                    final String itemAlias =MyDomUtils.getValueOrEmptyString(item.getEntityAlias());
                    return alias.equals(itemAlias);})
                .findFirst();

    }


    public static List<IndexAbstractField> excludeFields(List<IndexAbstractField> sourceFieldList, List<Exclude> excludeList){
        final Set<String> excludeFieldNames = excludeList.stream().map(Exclude::getField)
                .map(GenericAttributeValue::getXmlAttributeValue).filter(Objects::nonNull)
                .map(XmlAttributeValue::getValue)
                .collect(Collectors.toSet());

        return sourceFieldList.stream()
                .filter(item-> !excludeFieldNames.contains(MyDomUtils.getValueOrEmptyString(item.getName())))
                .toList();

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
     * @param serviceCallName Service Call Name
     * @return Optional<String>
     */
    public static Optional<String> getEntityNameFromServiceCallName(@NotNull String serviceCallName) {
        String[] names = serviceCallName.split("#");

        if(names.length != 2) return Optional.empty();

        if((!names[0].contains(".")) && (names[1].indexOf(".") > 0)) {
            return  Optional.of(names[1]);
        }else {
            return Optional.empty();
        }

    }
    /**
     * 用在Inspection中
     * 根据指定的属性来进行Entity名称的验证
     *
     * @param attributeValue GenericAttributeValue<String> attributeValue
     * @param holder DomElementAnnotationHolder
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

    /**
     * 核查Entity和View的名称是否存在重复
     * @param abstractEntity
     * @param holder
     */
    public static void inspectAbstractEntity(@NotNull AbstractEntity abstractEntity, @NotNull AnnotationHolder holder) {
        String entityName = MyDomUtils.getValueOrEmptyString(abstractEntity.getEntityName());
        List<AbstractEntity> existEntityList = new ArrayList<>();
        if(abstractEntity.getXmlTag() == null) return;
        if(abstractEntity.getEntityName().getXmlAttributeValue() == null) return;

        Project project = abstractEntity.getXmlTag().getProject();

        List<Entities> entitiesList = MyDomUtils.findDomFileElementsByRootClass(project,Entities.class).stream()
                .map(DomFileElement::getRootElement)
                .toList();

        existEntityList.addAll( entitiesList.stream()
                .map(Entities::getEntities)
                .flatMap(List::stream)
                .filter(entity -> MyDomUtils.getValueOrEmptyString(entity.getEntityName()).equals(entityName))
                .toList());
        existEntityList.addAll(entitiesList.stream()
                .map(Entities::getViewEntities)
                .flatMap(List::stream)
                .filter(entity -> MyDomUtils.getValueOrEmptyString(entity.getEntityName()).equals(entityName))
                .toList());


        existEntityList.forEach(entity -> {
            if(entity != abstractEntity){
                if(entity.getXmlTag()!= null ) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "'" + entityName + "' is used by another entity in file " + entity.getXmlTag().getContainingFile().getVirtualFile().getPath())
                            .range(abstractEntity.getEntityName().getXmlAttributeValue().getValueTextRange())
                            .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                            .create();
                }
            }
        });

//        XmlTag xmlTag = abstractEntity.getXmlTag();
//        if(xmlTag == null) {return;}
//
//        Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(
//                xmlTag.getProject(), entityName);
//
//        if (optionalXmlElement.isEmpty()) {
////            int start = xmlTag.getTextOffset();
//            int length = xmlTag.getLocalName().length();
//
//            holder.createProblem(abstractEntity, ProblemHighlightType.ERROR,"Entity is not found",
//                    TextRange.from(1, length));
//        }
    }

    public static void inspectExtendEntity(@NotNull ExtendEntity extendEntity, @NotNull DomElementAnnotationHolder holder) {
        String entityName = MyDomUtils.getValueOrEmptyString(extendEntity.getEntityName());
        XmlTag xmlTag = extendEntity.getXmlTag();
        if(xmlTag == null) {return;}

        Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(
                xmlTag.getProject(), entityName);

        if (optionalXmlElement.isEmpty()) {
//            int start = xmlTag.getTextOffset();
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
//            int start = xmlTag.getTextOffset();
            int length = xmlTag.getLocalName().length();
            holder.newAnnotation(HighlightSeverity.ERROR, "Entity is not found")
                    .range(TextRange.from(1, length))
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
        }
    }

    /**
     * 从Relationship定义中获取RelatedName,主要用于Detail中的relationship属性值
     *  The relationship linking the master or parent detail to the detail. Maybe either short-alias or
     *  full relationship name (${title}#${related-entity-name} or just related-entity-name if no title).
     * @param relationship Relationship
     * @return Optional<String>
     */
    public static Optional<String> getRelatedNameFromRelationship(@NotNull Relationship relationship){
        if(relationship.getShortAlias().getXmlAttributeValue()!= null) {
            final String alias = relationship.getShortAlias().getXmlAttributeValue().getValue();
            if (MyStringUtils.isNotEmpty(alias)) return Optional.of(alias);
        }
        final String entityName = MyDomUtils.getValueOrEmptyString(relationship.getRelated());

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
     * @return boolean
     */
    public static boolean isThisRelationshipRelatedName(@NotNull Relationship thisRelationship, @NotNull String checkedStr){
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
     * @param thisEntity 待判断的Entity
     * @param checkedStr 检查的字符串
     * @return boolean
     */
    public static boolean isThisEntityName(@NotNull AbstractEntity thisEntity, @NotNull String checkedStr){
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

     * @return Optional<String>
     */
    public static Optional<String> getEntityShortAlias(@NotNull Entity entity){

        XmlAttributeValue value = entity.getShortAlias().getXmlAttributeValue();
        if(value == null) {
            return Optional.empty();

        }else {
            final String shortAlias = value.getValue();
            return Optional.of(shortAlias);
        }

    }
    /**
     * 判断输入的字符串是否为ExtendEntity的有效名称
     * 只含entityName或带有package的Name，都算有效
     * @param thisEntity 带检查的Extend Entity
     * @param checkedStr 检查字符串
     * @return boolean
     */
    public static boolean isThisExtendEntityName(@NotNull ExtendEntity thisEntity, @NotNull String checkedStr){
        if(checkedStr.contains(".")) {
            return getFullNameFromExtendEntity(thisEntity).equals(checkedStr);
        }else {
            return MyDomUtils.getValueOrEmptyString(thisEntity.getEntityName()).equals(checkedStr);
        }
    }


    /**
     * 根据ViewEntity找到IndexViewEntity
     * @param viewEntity 当前View Entity
     * @return Optional<IndexViewEntity>
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
     * @param context ConvertContext
     * @return  Collection<AbstractMemberEntity>
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

    /**
     * 根据EntityAlias找到对应的AbstractIndexEntity，获取FieldList等
     * @param viewEntity alias所在的viewEntity
     * @param alias 待查找的别名
     * @return Optional<AbstractIndexEntity> 根据alias查找到Entity或ViewEntity
     */
    public static Optional<AbstractIndexEntity> getViewEntityAbstractIndexEntityByAlias(@NotNull ViewEntity viewEntity, @NotNull String alias) {
        final String[] aliasEntityName = new String[1];

        final Project project;
        if(viewEntity.getXmlElement() ==null) {
            return Optional.empty();
        }else {
            project = viewEntity.getXmlElement().getProject();
        }
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        String viewEntityFullName = getFullNameFromEntity(viewEntity);

        Optional<IndexViewEntity> optionalIndexViewEntity = moquiIndexService.getIndexViewEntityByName(viewEntityFullName);

        return optionalIndexViewEntity.flatMap(indexViewEntity -> indexViewEntity.getAbstractIndexEntityByAlias(alias));
    }
    public static Optional<String> getEntityNameFromViewEntityByAlias(@NotNull Project project, @NotNull ViewEntity viewEntity,@NotNull String alias) {
        final String[] aliasEntityName = new String[1];
        viewEntity.accept(new DomElementVisitor() {
            @Override
            public void visitDomElement(DomElement domElement) {

            }
            public void visitMemberEntity(MemberEntity memberEntity){
                if(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityAlias()).equals(alias)) {
                    aliasEntityName[0] = MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName());
                }
            }
            public void visitMemberRelationship(MemberRelationship memberRelationship){
                if(MyDomUtils.getValueOrEmptyString(memberRelationship.getEntityAlias()).equals(alias)) {
                    String joinAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getJoinFromAlias());

                    Optional<String> nameOptional = getEntityNameFromViewEntityByAlias(project,viewEntity,joinAlias);
                    if(nameOptional.isPresent()) {
                        Optional<IndexEntity> indexEntityOptional = getIndexEntityByName(project,nameOptional.get());
                        if(indexEntityOptional.isPresent()) {
                            String relationship = MyDomUtils.getValueOrEmptyString(memberRelationship.getRelationship());

                            aliasEntityName[0] = indexEntityOptional.get().getRelationshipByName(relationship)
                                    .map(Relationship::getRelated)
                                    .map(MyDomUtils::getValueOrEmptyString)
                                    .orElse(null);
                        }
                    }
                }
            }
        });

        return Optional.ofNullable(aliasEntityName[0]);
    }
    /**
     * 根据EntityAlias找到对应的AbstractIndexEntity，直接返回AbstractFieldList等
     * @param viewEntity ViewEntity
     * @param alias 待查找的Alias Name
     * @return List<IndexAbstractField>
     */
    public static @NotNull List<IndexAbstractField> getIndexAbstractFieldListFromViewEntityByAlias(@NotNull ViewEntity viewEntity, @NotNull String alias) {

        AbstractIndexEntity abstractIndexEntity = getViewEntityAbstractIndexEntityByAlias(
                viewEntity,
                alias
        ).orElse(null);
        if(abstractIndexEntity == null) return new ArrayList<>();

        return abstractIndexEntity.getIndexAbstractFieldList();
    }

//
    /**
     * 获取Entity或ViewEntity的字段列表
     * @param project 当前Project
     * @param entityName EntityName
     * @return List<IndexAbstractField>
     */
    public static @NotNull List<IndexAbstractField> getEntityOrViewEntityFields(@NotNull Project project, @NotNull String entityName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getEntityOrViewEntityFieldList(entityName);
    }


    /**
     * 找到当前所在位置的Entity对应的所有Relationship，不包括ExtendEntity的Relationship
     * @param context ConvertContext
     * @return List<Relationship>
     */
    public static List<Relationship> getCurrentEntityRelationshipList(ConvertContext context){

        Entity entity = getLocalDomElementByConvertContext(context,Entity.class).orElse(null);
        if(entity == null) {
            return new ArrayList<>();
        }else {
            return entity.getRelationshipList();
        }

    }
    public static Optional<ViewEntity> getCurrentViewEntity(ConvertContext context){
        return getLocalDomElementByConvertContext(context,ViewEntity.class);

    }

    public static Optional<MemberRelationship> getCurrentMemberRelationship(ConvertContext context){
        return getLocalDomElementByConvertContext(context,MemberRelationship.class);

    }


    /**
     * 创建EntityName对应的psiReference
     * @param project 当前Project
     * @param element 包含EntityName的PsiElement
     * @param entityScope EntityScope
     * @return PsiReference[]
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
     * @param entityName 当前EntityName
     * @return Collection<ExtendEntity>
     */
    public static @NotNull List<ExtendEntity> getExtendEntityListByName(@NotNull Project project, @NotNull String entityName){

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
     * 字段名的第一个字符是否为排序控制字符
     * @param fieldName 字段名
     * @return boolean
     */
    public static boolean fieldHasOrderCommand(@NotNull String fieldName){
        if(fieldName.isBlank()) return false;
        return MyStringUtils.FIELD_SORT_CHAR_LIST.contains(fieldName.substring(0,1));
    }

    /**
     * 将多个字段组成的字符串拆分
     * 字段实例：
     * 1、 orderId,orderPartSeqId,shipmentMethodEnumId,quantityTotal
     * 2、resQuantity${onlyPartlyShipped != 'true' ? '' : ',issuedQuantity'}${excludeNotPickLoc == 'false' ? '' : ',itemBomQuantityTotal'}
     * @param fieldString 待处理的字符串
     * @return List<FieldDescriptor>
     */
    public static List<FieldDescriptor> extractFieldDescriptorList(@NotNull String fieldString, int offset) {
        List<FieldDescriptor> result = new ArrayList<>();
        StringBuilder fieldNameSB= new StringBuilder(MyStringUtils.EMPTY_STRING);
        boolean inVariable = false;
        int fieldBeginIndex = 0;

        int totalLength = fieldString.length();
        for(int i =0; i< totalLength;i++){
            if(fieldString.startsWith(EntityUtils.ENTITY_FIELD_COMMA, i)){
                if(!inVariable) {
                    FieldDescriptor fieldDescriptor = new FieldDescriptor(fieldNameSB.toString(),fieldBeginIndex + offset,i + offset);

                    result.add(fieldDescriptor);
                    fieldNameSB = new StringBuilder(MyStringUtils.EMPTY_STRING);
                    fieldBeginIndex = i+1;
                }
            }else {

                if((i<=totalLength-2) && fieldString.startsWith("${", i)) {
                    inVariable = true;

                }
                if(fieldString.charAt(i) == '}') {
                    inVariable = false;
                }

                fieldNameSB.append(fieldString, i, i+1);
            }
        }
        if(!fieldNameSB.toString().equals(MyStringUtils.EMPTY_STRING)) {
            FieldDescriptor fieldDescriptor = new FieldDescriptor(fieldNameSB.toString(),fieldBeginIndex+offset,totalLength+offset);
            result.add(fieldDescriptor);
        }

        return result;
    }

    /**
     * 用于converter
     * @param context ConvertContext
     * @return List<IndexAbstractField>
     */

    public static List<IndexAbstractField> getIndexAbstractFieldListByConvertContext(@NotNull ConvertContext context) {
        XmlElement xmlElement = context.getXmlElement();
        if(xmlElement == null) {
            return new ArrayList<>();
        }else {
            return getIndexAbstractFieldListByPsiElement(context.getXmlElement());
        }

    }

    /**
     * 根据PsiElement 找到所在的EntityName，如果不在Entity Tag下，则返回空
     * @param psiElement 当前PsiElement
     * @param curEntity 当前的Entity
     * @param curAttributeName 当前属性名称
     * @return Optional<String>
     */
    public static Optional<String> getEntityNameByPsiElement(@NotNull PsiElement psiElement,@NotNull Entity curEntity, @NotNull String curAttributeName) {
        String entityName = null;
        //Entity-Relationship
        Relationship curRelationship = MyDomUtils.getLocalDomElementByPsiElement(psiElement,Relationship.class).orElse(null);
        if(curRelationship != null) {
            //Entity-Relationship-keyMap（fieldName）
            //Entity-Relationship-keyMap（related）
            KeyMap curKeyMap = getLocalDomElementByPsiElement(psiElement, KeyMap.class).orElse(null);
            if(curKeyMap != null) {
                switch(curAttributeName) {
                    case KeyMap.ATTR_FIELD_NAME -> entityName = MyDomUtils.getValueOrEmptyString(curEntity.getEntityName());
                    case KeyMap.ATTR_RELATED -> entityName = MyDomUtils.getValueOrEmptyString(curRelationship.getRelated());
                }
            }
            //Entity-Relationship-keyValue（related）
            KeyValue curKeyValue = getLocalDomElementByPsiElement(psiElement, KeyValue.class).orElse(null);
            if(curKeyValue != null) {
                if(curAttributeName.equals(KeyValue.ATTR_RELATED)) {
                    entityName = MyDomUtils.getValueOrEmptyString(curRelationship.getRelated());
                }
            }
        }else {
            entityName = MyDomUtils.getValueOrEmptyString(curEntity.getEntityName());
        }
        return Optional.ofNullable(entityName);

    }
    public static Optional<String> getEntityNameByPsiElement(@NotNull PsiElement psiElement,@NotNull ExtendEntity curEntity, @NotNull String curAttributeName) {
        String entityName = null;
        //ExtendEntity-Relationship
        Relationship curRelationship = getLocalDomElementByPsiElement(psiElement, Relationship.class).orElse(null);
        if(curRelationship != null) {
            //ExtendEntity-Relationship-keyMap（related）
            //ExtendEntity-Relationship-keyValue（related）
            KeyMap curKeyMap = getLocalDomElementByPsiElement(psiElement, KeyMap.class).orElse(null);
            if(curKeyMap != null) {
                switch(curAttributeName) {
                    case KeyMap.ATTR_FIELD_NAME -> entityName = MyDomUtils.getValueOrEmptyString(curEntity.getEntityName());
                    case KeyMap.ATTR_RELATED -> entityName = MyDomUtils.getValueOrEmptyString(curRelationship.getRelated());
                }
            }
            //ExtendEntity-Relationship-keyValue（related）
            KeyValue curKeyValue = getLocalDomElementByPsiElement(psiElement, KeyValue.class).orElse(null);
            if(curKeyValue != null) {
                if(curAttributeName.equals(KeyValue.ATTR_RELATED)) {
                    entityName = MyDomUtils.getValueOrEmptyString(curRelationship.getRelated());
                }
            }
        }else {
            //ExtendEntity-Index-IndexField(name)
            entityName = MyDomUtils.getValueOrEmptyString(curEntity.getEntityName());
        }
        return Optional.ofNullable(entityName);

    }
    public static Optional<String> getEntityNameByPsiElement(@NotNull PsiElement psiElement,@NotNull AbstractEntityName abstractEntityName, @NotNull String curAttributeName) {
        return Optional.of(MyDomUtils.getValueOrEmptyString(abstractEntityName.getEntityName()));
    }
    public static Optional<String> getEntityNameByPsiElement(@NotNull PsiElement psiElement,@NotNull Service service, @NotNull String curAttributeName) {
        String entityName = null;
        //Service下的AutoParameters
        AutoParameters curAutoParameters = getLocalDomElementByPsiElement(psiElement, AutoParameters.class).orElse(null);
        if(curAutoParameters != null) {
            //Service下的AutoParameters-Exclude（FieldName）
            Exclude curExclude = getLocalDomElementByPsiElement(psiElement, Exclude.class).orElse(null);
            if(curExclude != null) {
                if(curAttributeName.equals(Exclude.ATTR_FIELD_NAME)) {
                    entityName = MyDomUtils.getValueOrEmptyString(curAutoParameters.getEntityName());
                    if(MyStringUtils.EMPTY_STRING.equals(entityName)) {
                        //如果当前的AutoParameters中没有定义entityName，则有可能是在Service的inParameters中，这时，service的noun就是EntityName
                        entityName = MyDomUtils.getValueOrEmptyString(service.getNoun());
                    }
                }

            }
        }
        return Optional.ofNullable(entityName);
    }

    /**
     * 由于ViewEntity中采用了别名，所以获取字段的方式和Entity，EntityFind等都不同，单独处理
     * @param psiElement 当前PsiElement
     * @param viewEntity 当前View Entity
     * @return List<IndexAbstractField>
     */
    public static List<IndexAbstractField> getIndexAbstractFieldListByPsiElementForViewEntity(@NotNull PsiElement psiElement,
                                                                                              @NotNull ViewEntity viewEntity) {
        List<IndexAbstractField> result = new ArrayList<>();
        String curAttributeName = getCurrentAttributeName(psiElement).orElse(MyStringUtils.EMPTY_STRING);
        //ViewEntity-MemberEntity
        MemberEntity curMemberEntity = getLocalDomElementByPsiElement(psiElement, MemberEntity.class).orElse(null);
        if (curMemberEntity != null) {
            //ViewEntity-MemberEntity-KeyMap
            KeyMap curKeyMap = getLocalDomElementByPsiElement(psiElement, KeyMap.class).orElse(null);
            if(curKeyMap !=null) {
                //ViewEntity-MemberEntity-KeyMap（fieldName）
                //ViewEntity-MemberEntity-KeyMap（related）
                switch (curAttributeName) {
                    case (KeyMap.ATTR_FIELD_NAME)->//fieldName，取MemberEntity属性joinFromAlias对应表的字段
                            result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(
                                    viewEntity,
                                    MyDomUtils.getValueOrEmptyString(curMemberEntity.getJoinFromAlias())));
                    case (KeyMap.ATTR_RELATED)->//related，取MemberEntity属性entityName对应表的字段
                            result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(
                                    viewEntity,
                                    MyDomUtils.getValueOrEmptyString(curMemberEntity.getEntityAlias())));
                }
                return result;
            }
        }
        //ViewEntity-EntityCondition
        //ViewEntity-MemberEntity 下面的EntityCondition可以复用这段处理
        //ViewEntity-MemberEntity-EntityCondition-ECondition（fieldName）
        //ViewEntity-MemberEntity-EntityCondition-ECondition（toFieldName）
        //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（fieldName）
        //ViewEntity-MemberEntity-EntityCondition-EConditions-ECondition（toFieldName）

        EntityCondition curEntityCondition = getLocalDomElementByPsiElement(psiElement, EntityCondition.class).orElse(null);
        if (curEntityCondition != null) {
            //ViewEntity-EntityCondition-ECondition
            ECondition curECondition = getLocalDomElementByPsiElement(psiElement, ECondition.class).orElse(null);
            if(curECondition != null) {

                String entityAlias = MyDomUtils.getValueOrEmptyString(curECondition.getEntityAlias());
                String toEntityAlias = MyDomUtils.getValueOrEmptyString(curECondition.getToEntityAlias());
                //ViewEntity-EntityCondition-ECondition（fieldName）
                //ViewEntity-EntityCondition-ECondition（toFieldName）
                switch (curAttributeName) {
                    case ECondition.ATTR_FIELD_NAME -> {
                        if(entityAlias.isEmpty()) {
                            //没有alias，就去当前ViewEntity的所有Fields
                            result.addAll(getViewEntityIndexAbstractFieldList(viewEntity));

                        }else {
                            result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(
                                    viewEntity,
                                    entityAlias));

                        }
                    }
                    case ECondition.ATTR_TO_FIELD_NAME -> {
                        //如果没有toEntityAlias，则看看是否在MemberEntity下面，如果是，就取MemberEntity的字段
                        if(toEntityAlias.isEmpty()) {
                            if(curMemberEntity!=null) {
                                result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(
                                        viewEntity,
                                        MyDomUtils.getValueOrEmptyString(curMemberEntity.getEntityAlias())));

                            }

                        }else {

                            result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(
                                    viewEntity,
                                    toEntityAlias));
                        }
                    }
                }
            }
            return result;
        }
        //ViewEntity-Alias

        Alias curAlias = getLocalDomElementByPsiElement(psiElement, Alias.class).orElse(null);
        if(curAlias != null) {
            String entityAlias = MyDomUtils.getValueOrEmptyString(curAlias.getEntityAlias());
            if(entityAlias.isEmpty()) {
                //ViewEntity-Alias-ComplexAlias-ComplexAliasField(field)
                //ComplexAlias可能会有多个嵌套，所以不能根据Alias的位置来判断
                getLocalDomElementByPsiElement(psiElement, ComplexAliasField.class)
                        .ifPresent(curComplexAliasField -> result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(viewEntity,
                                                            MyDomUtils.getValueOrEmptyString(curComplexAliasField.getEntityAlias()))));
            }else {
                //ViewEntity-Alias(field)
                if (curAttributeName.equals(Alias.ATTR_FIELD)) {
                    result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(viewEntity,
                            MyDomUtils.getValueOrEmptyString(curAlias.getEntityAlias())));
                }
            }
            return result;
        }
        //ViewEntity-AliasAll
        AliasAll curAliasAll = getLocalDomElementByPsiElement(psiElement, AliasAll.class).orElse(null);
        if(curAliasAll != null) {
            Exclude curExclude = getLocalDomElementByPsiElement(psiElement, Exclude.class).orElse(null);
            if(curExclude != null) {
                //ViewEntity-AliasAll-Exclude（Field）
                if (curAttributeName.equals(Exclude.ATTR_FIELD)) {
                    result.addAll(getIndexAbstractFieldListFromViewEntityByAlias(viewEntity,
                            MyDomUtils.getValueOrEmptyString(curAliasAll.getEntityAlias())));
                }
            }
        }
        return result;
    }
    /**
     * 用于EntityFieldNameReference
     * @param psiElement 针对当前的XmlAttributeValue
     * @return List<IndexAbstractField>
     */

    public static List<IndexAbstractField> getIndexAbstractFieldListByPsiElement(@NotNull PsiElement psiElement) {
        String curAttributeName = getCurrentAttributeName(psiElement).orElse(MyStringUtils.EMPTY_STRING);

        String entityName = MyStringUtils.EMPTY_STRING;
        //Entity
        Entity curEntity = MyDomUtils.getLocalDomElementByPsiElement(psiElement,Entity.class).orElse(null);
        if(curEntity != null) {
            entityName = getEntityNameByPsiElement(psiElement,curEntity,curAttributeName).orElse(MyStringUtils.EMPTY_STRING);
        }
        //ExtendEntity
        ExtendEntity curExtendEntity = getLocalDomElementByPsiElement(psiElement, ExtendEntity.class).orElse(null);
        if(curExtendEntity != null) {
            entityName = getEntityNameByPsiElement(psiElement,curExtendEntity,curAttributeName).orElse(MyStringUtils.EMPTY_STRING);

        }

        //ViewEntity
        ViewEntity curViewEntity = getLocalDomElementByPsiElement(psiElement, ViewEntity.class).orElse(null);
        if(curViewEntity != null) {
            return getIndexAbstractFieldListByPsiElementForViewEntity(psiElement,curViewEntity);

        }
        //Service，Seca等下的EntityFindOne
        AbstractEntityName nameAbstract;
        nameAbstract = getLocalDomElementByPsiElement(psiElement, EntityFindOne.class).orElse(null);

        //Service、Seca等下的EntityFind
        //Service、Seca等下的EntityDeleteByCondition
        //Service、Seca等下的EntityFindCount
        //只和EntityFind等有关，就不用判断在哪个Tag下
//        AbstractEntityName nameAbstract = null;
        if(nameAbstract == null) {
            nameAbstract = getLocalDomElementByPsiElement(psiElement, EntityFind.class).orElse(null);
        }
        if(nameAbstract == null) {
            nameAbstract = getLocalDomElementByPsiElement(psiElement, EntityDeleteByCondition.class).orElse(null);
        }
        if(nameAbstract == null) {
//            nameAbstract = ServiceUtils.getCurrentEntityFindCount(psiElement).orElse(null);
            nameAbstract = getLocalDomElementByPsiElement(psiElement, EntityFindCount.class).orElse(null);
        }

        if(nameAbstract != null) {
            entityName = getEntityNameByPsiElement(psiElement,nameAbstract,curAttributeName).orElse(MyStringUtils.EMPTY_STRING);

        }else {

            //Service
            Service curService = getLocalDomElementByPsiElement(psiElement, Service.class).orElse(null);
            if (curService != null) {
                entityName = getEntityNameByPsiElement(psiElement, curService, curAttributeName).orElse(MyStringUtils.EMPTY_STRING);

            }
        }
        //ServiceCall
        ServiceCall curServiceCall = getLocalDomElementByPsiElement(psiElement, ServiceCall.class).orElse(null);
        if(curServiceCall != null) {
            ServiceCallDescriptor serviceCallDescriptor = ServiceCallDescriptor.of(
                    MyDomUtils.getValueOrEmptyString(curServiceCall.getName())
            );
            if(serviceCallDescriptor.isCRUD()) entityName = serviceCallDescriptor.getNoun();
        }


        if(MyStringUtils.isNotEmpty(entityName)) {
            return getEntityOrViewEntityFields(psiElement.getProject(),entityName);
        }else {
            return new ArrayList<>();
        }

    }

    /**
     * 用于converter
     * @param fieldName 字段名称
     * @param context ConvertContext
     * @return Optional<IndexAbstractField>
     */
    public static Optional<IndexAbstractField> getIndexAbstractFieldByConvertContext(@NotNull String fieldName, @NotNull ConvertContext context) {
        List<IndexAbstractField> fields = getIndexAbstractFieldListByConvertContext(context);
        return fields.stream().filter(item->{
            final String itemName = MyDomUtils.getValueOrEmptyString(item.getName());
            return fieldName.equals(itemName);
        }).findFirst();
    }

    /**
     * 为字段创建FieldNameReference
     * @param psiElement 当前PsiElement，一般就是AttributeValue
     * @param fieldDescriptor 处理后的字段描述定义
     * @param indexAbstractField 目标字段
     * @return PsiReference[]
     */
    public static @NotNull PsiReference[] createFieldNameReference(@NotNull PsiElement psiElement, @NotNull FieldDescriptor fieldDescriptor, @Nullable IndexAbstractField indexAbstractField){
        if (fieldDescriptor.isContainGroovyVariable() || fieldDescriptor.isEmpty()) return PsiReference.EMPTY_ARRAY;

        List<PsiReference> resultList = new ArrayList<>();
        if(indexAbstractField == null) {
            resultList.add(EntityFieldNameReference.of(psiElement,
                    TextRange.create(fieldDescriptor.getFieldNameBeginIndex(),fieldDescriptor.getFieldNameEndIndex()),
                    null)); //提示错误

        }else {
            if (indexAbstractField.getOriginFieldName().equals(fieldDescriptor.getFieldName())) {

                resultList.add(EntityFieldNameReference.of(psiElement,
                        TextRange.create(fieldDescriptor.getFieldNameBeginIndex(), fieldDescriptor.getFieldNameEndIndex()),
                        indexAbstractField.getAbstractField().getName().getXmlAttributeValue()));
            } else {
                String valueStr = fieldDescriptor.getOriginalString();
                int prefixIndex = valueStr.indexOf(indexAbstractField.getPrefix());
                if (prefixIndex >= 0) {
                    resultList.add(EntityFieldNameReference.of(psiElement,
                            TextRange.create(fieldDescriptor.getFieldNameBeginIndex() + prefixIndex
                                    , fieldDescriptor.getFieldNameBeginIndex() + prefixIndex + indexAbstractField.getPrefix().length()),
                            indexAbstractField.getAliasAll().getPrefix().getXmlAttributeValue()));
                }
                int originFieldIndex = valueStr.indexOf(MyStringUtils.upperCaseFirstChar(indexAbstractField.getOriginFieldName()));
                if (originFieldIndex >= 0) {
                    resultList.add(EntityFieldNameReference.of(psiElement,
                            TextRange.create(fieldDescriptor.getFieldNameBeginIndex() + originFieldIndex,
                                    fieldDescriptor.getFieldNameBeginIndex() + originFieldIndex + indexAbstractField.getOriginFieldName().length()),
                            indexAbstractField.getAbstractField().getName().getXmlAttributeValue()));
                }

            }
        }
        return resultList.toArray(new PsiReference[0]);
    }
}
