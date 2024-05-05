package org.moqui.idea.plugin.service;

import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.listener.MoquiXmlVirtualFileManager;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.trim;

/**
 * 建立Entity，ViewEntity和Service的缓存
 * 为了简化因为Entity等定义修改后调整这个缓存，在每次使用缓存之前检查一下缓存是否有效，如果无效，则更新整个缓存
 * 如果未来效率有问题，可以改进更新做法，进行更细致的控制
 */
@Service(Service.Level.PROJECT)
public final class MoquiIndexService {
    private final Project project;
    /**
     * 通过这个两个标志位跟踪Entity 和Service的xml定义文件是否被改动，
     * 如果Entity被改动，则需要重新刷新entityMap、viewEntityMap和ServiceMap
     * 如果Service被改动，则需要刷新Service
     */
    private boolean entityXmlFileUpdated;

    private boolean serviceXmlFileUpdated;

    private Map<String, IndexEntity> indexEntityMap;
    private Map<String, IndexViewEntity> indexViewEntityMap;
//    private Map<String, ExtendEntity> extendEntityMap = new HashMap<>();

    private Map<String, IndexService> indexServiceMap;
    private Map<String,IndexService> indexInterfaceMap;//处理service的interface定义

    private List<ViewEntity> pendingViewEntityList;
    private final MoquiXmlVirtualFileManager moquiXmlVirtualFileManager;
    public MoquiIndexService(Project project) {
        this.project = project;
        this.entityXmlFileUpdated = false;
        this.serviceXmlFileUpdated = false;
        refreshAllEntityMap();
        refreshAllServiceMap();


        this.moquiXmlVirtualFileManager = new MoquiXmlVirtualFileManager(project);
        VirtualFileManager.getInstance().addVirtualFileListener(this.moquiXmlVirtualFileManager);

    }

    private void refreshAllEntityMap(){
        //这里初始化，需要注意先后次序：Entity->ExtendEntity->ViewEntity，最后才是Service
        this.indexEntityMap = new HashMap<>();
        this.indexViewEntityMap = new HashMap<>();
        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(this.project,Entities.class);
        fileElementList.forEach(this::updateEntityFromFile);
        fileElementList.forEach(this::updateExtendEntityFromFile);
        updateViewEntity(fileElementList);


    }
    private void refreshAllServiceMap(){
        //先处理interface，再处理service
        this.indexServiceMap = new HashMap<>();
        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(this.project, Services.class);

        fileElementList.forEach(this::updateInterfaceFromFile);
        fileElementList.forEach(this::updateServiceFromFile);

    }

    public void unRegisterListener(){
        VirtualFileManager.getInstance().removeVirtualFileListener(this.moquiXmlVirtualFileManager);
    }
    /**
     * 更新某个entities.xml定义文件的内容，
     * @param fileElement
     */
    private void updateEntityFromFile(DomFileElement<Entities> fileElement){
//        final String filePath = fileElement.getOriginalFile().getVirtualFile().getPath();
        for(Entity entity: fileElement.getRootElement().getEntities()) {
            IndexEntity indexEntity = new IndexEntity(entity);
            indexEntityMap.put(indexEntity.getFullName(),indexEntity);
        };
    }
    private void updateExtendEntityFromFile(DomFileElement<Entities> fileElement){
//        final String filePath = fileElement.getOriginalFile().getVirtualFile().getPath();
        for(ExtendEntity extendEntity : fileElement.getRootElement().getExtendEntities()) {
            getIndexEntityByName(MyDomUtils.getValueOrEmptyString(extendEntity.getEntityName()))
                    .ifPresent(indexEntity -> indexEntity.AddExtendEntity(extendEntity));
        };
    }

//    private void updateViewEntityFromFile(DomFileElement<Entities> fileElement){
////        final String filePath = fileElement.getOriginalFile().getVirtualFile().getPath();
//        for(ViewEntity viewEntity : fileElement.getRootElement().getViewEntities()) {
//            IndexViewEntity indexViewEntity = new IndexViewEntity(viewEntity);
//            viewEntityMap.put(indexViewEntity.getViewName(), indexViewEntity);
//        };
//    }

    /**
     * viewEntity的字段比较复杂，因为viewEntity可能会嵌套另外的viewEntity
     * 所以采用多次扫描的算法
     * 第一遍，将所有viewEntity从file中识别出来，并判断memberEntity是不是viewEntity，如果有，则先不处理，放在缓存列表中，如果没有，就将字段识别出来
     * 第二遍，对缓存列表进行扫描，判断memberEntity中的viewEntity已经识别出Field的都处理完。
     * 如此循环，直到缓存中没有待处理的viewEntity
     * 有可能存在死循环的情况，所以处理10变后，还有未处理的viewEntity，就直接将能识别的memberEntity识别，不能识别就不处理。
     * 同时将未处理的memberEntity存放到IndexViewEntity对应属性中
     * @param fileElementList
     */
    private void updateViewEntity(List<DomFileElement<Entities>> fileElementList){
        List<ViewEntity> pendingViewEntities = new ArrayList<>();

        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(ViewEntity viewEntity : fileElement.getRootElement().getViewEntities()) {
                if(canGetFields(viewEntity)) {
                    addViewEntityToMap(viewEntity);
//                    Map<String,AbstractField> fieldMap = extractViewEntityFieldMap(viewEntity);
//                    IndexViewEntity indexEntity = new IndexViewEntity(viewEntity);
//                    indexEntity.setFieldMap(fieldMap);
//                    this.viewEntityMap.put(indexEntity.getViewName(), indexEntity);
                }else {
                    pendingViewEntities.add(viewEntity);
                }
            }
        }
        for(int i=0; i<10; i++) {
            Iterator<ViewEntity> iterator = pendingViewEntities.iterator();
            while(iterator.hasNext()) {
                ViewEntity viewEntity = iterator.next();
                if(canGetFields(viewEntity)) {
                    addViewEntityToMap(viewEntity);
                    iterator.remove();
                }
            }
        }
        //将未处理的ViewEntity保存下来，在ToolWindows中进行后续的显示
        this.pendingViewEntityList = pendingViewEntities;

    }

    /**
     * 判断当前的ViewEntity是否能获取到字段定义，即对应的MemberEntity或MemberRelationship都已经获得了Field
     * @param viewEntity
     * @return
     */
    private boolean canGetFields(@NotNull ViewEntity viewEntity){
        for(MemberEntity memberEntity: viewEntity.getMemberEntityList()){
            if (getEntityOrViewEntity(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName())).isEmpty()) {
                return false;
            }
        };
        for(MemberRelationship relationship: viewEntity.getMemberRelationshipList()){
            if (getEntityOrViewEntity(getViewEntityMemberRelationshipEntityName(viewEntity,relationship).orElse(MyStringUtils.EMPTY_STRING)).isEmpty()) {
                return false;
            }
        }
        return true;
    }
//    private Optional<String> getViewEntityMemberRelationshipEntityName(@NotNull ViewEntity viewEntity,
//                                                                       @NotNull MemberRelationship memberRelationship){
//        String joinFromAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getJoinFromAlias());
//        MemberEntity memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity,joinFromAlias);
//        if (memberEntity == null) {return Optional.empty();}
//        return Optional.of(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName()));
//
//    }
    private void addViewEntityToMap(@NotNull ViewEntity viewEntity){
        Map<String,AbstractField> fieldMap = extractViewEntityFieldMap(viewEntity);
        IndexViewEntity indexEntity = new IndexViewEntity(viewEntity);
        indexEntity.setAbstractFieldMap(fieldMap);
        this.indexViewEntityMap.put(indexEntity.getFullName(), indexEntity);
    }
    private Map<String, AbstractField> extractViewEntityFieldMap(@NotNull ViewEntity viewEntity){
        Map<String, AbstractField> result = new HashMap<>();

        //添加alias
        List<AbstractField> fieldList = new LinkedList<>(viewEntity.getAliasList());
        //对AliasAll进行处理
        List<AliasAll> aliasAllList = viewEntity.getAliasAllList();
        for(AliasAll aliasAll : aliasAllList) {
            String alias = MyDomUtils.getValueOrEmptyString(aliasAll.getEntityAlias());
            if(MyStringUtils.isEmpty(alias)) continue;

            List<AbstractField> aliasAllFieldList = new ArrayList<>();
            //先查找MemberEntity，再查找MemberRelationship
            MemberEntity memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity, alias);
            String  entityName;
            if(memberEntity == null) {
                //再添加MemberRelationship
                MemberRelationship relationship = EntityUtils.getMemberRelationshipByAlias(viewEntity, alias);
                if(relationship == null) continue;
                entityName = getViewEntityMemberRelationshipEntityName(viewEntity, relationship)
                        .orElse(MyStringUtils.EMPTY_STRING);

            }else {
                entityName = MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName());
            }

            if(MyStringUtils.isEmpty(entityName)) continue;
            Optional<List<AbstractField>> fields = getEntityOrViewEntityFields(entityName);
            fields.ifPresent(aliasAllFieldList::addAll);
            fieldList.addAll(
                    EntityUtils.excludeFields(aliasAllFieldList,aliasAll.getExcludeList())
            );
        }
        for(AbstractField field: fieldList) {
            result.put(MyDomUtils.getValueOrEmptyString(field.getName()),field);
        }

        return  result;

    }

    /**
     * 获取ViewEntity 的MemberRelationship对应的Entity name;
     * 如果MemberRelationship的relationship已经是Entity的全称，就可以直接获取，如果是shortAlias，则需要先找到Entity，
     *    再根据shortAlias找到对应的Relationship定义的Entity Name；
     * @param viewEntity 当前的View Entity
     * @param memberRelationship 待获取对应EntityName的MemberRelationship
     * @return 待获取的Entity Name
     */
    public Optional<String> getViewEntityMemberRelationshipEntityName(@NotNull ViewEntity viewEntity,
                                                                             @NotNull MemberRelationship memberRelationship){
        String relationship = MyDomUtils.getValueOrEmptyString(memberRelationship.getRelationship());
        if(MyStringUtils.EMPTY_STRING.equals(relationship)) return Optional.empty();
        if(EntityUtils.isFullName(relationship)) {
            return Optional.of(relationship);
        }else {
            String joinFromAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getJoinFromAlias());
            MemberEntity memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity, joinFromAlias);
            if (memberEntity == null) {
                return Optional.empty();
            }
            if(viewEntity.getXmlElement() == null) return Optional.empty();

            Entity entity = getEntityByName(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName())).orElse(null);
            if(entity==null) return Optional.empty();

            List<Relationship> relationshipList = entity.getRelationshipList().stream()
                    .filter((item) -> MyDomUtils.getValueOrEmptyString(item.getShortAlias()).equals(relationship))
                    .toList();

            if(relationshipList.isEmpty()) {
                return Optional.empty();
            }else {
                return Optional.ofNullable(relationshipList.get(0).getRelated().getStringValue());
            }

        }
    }
    private Optional<List<AbstractField>> getEntityOrViewEntityFields(@NotNull String entityName){
        Optional<IndexEntity> entity = getIndexEntityByName(entityName);
        if (entity.isPresent()) {
            return entity.get().getAbstractFieldList();
        }else {
            Optional<IndexViewEntity> viewEntity = getIndexViewEntityByName(entityName);
            if(viewEntity.isPresent()) {
                return viewEntity.get().getAbstractFieldList();
            }

        }
        return Optional.empty();
    }
    /**
     * todo 需要重新编写
     * @param fileElement
     */
    private void updateServiceFromFile(DomFileElement<Services> fileElement){
        for(org.moqui.idea.plugin.dom.model.Service service: fileElement.getRootElement().getServiceList()) {
            if(ServiceUtils.isNotInterface(service)) {
                IndexService indexService = new IndexService(service);
                indexService.setInParameterMap(extractServiceInParameterFieldMap(service));
                indexService.setOutParameterMap(extractServiceOutParameterFieldMap(service));

                indexServiceMap.put(indexService.getFullName(), indexService);
            }
        }
    }
    private void updateInterfaceFromFile(DomFileElement<Services> fileElement){
        for(org.moqui.idea.plugin.dom.model.Service service: fileElement.getRootElement().getServiceList()) {
            if(ServiceUtils.isInterface(service)) {
                IndexService indexService = new IndexService(service);
                indexService.setInParameterMap(extractServiceInParameterFieldMap(service));
                indexService.setOutParameterMap(extractServiceOutParameterFieldMap(service));
                indexServiceMap.put(indexService.getFullName(), indexService);
            }
        }
    }

    /**
     * 返回参数数组，第一个为in，第二个为out
     * @param service
     * @return
     */
    private Map<String, IndexServiceParameter> extractServiceInParameterFieldMap(@NotNull org.moqui.idea.plugin.dom.model.Service service){

        Map<String,IndexServiceParameter> inParameterMap = new HashMap<>();
        for(Parameter parameter: service.getInParameters().getParameterList()) {
            inParameterMap.putAll(extractParameterMap(parameter));
        }
        for(AutoParameters autoParameters: service.getInParameters().getAutoParametersList()) {
            inParameterMap.putAll(extractAutoParametersFieldMap(autoParameters));
        }

        return inParameterMap;

    }
    private Map<String, IndexServiceParameter> extractServiceOutParameterFieldMap(@NotNull org.moqui.idea.plugin.dom.model.Service service){


        Map<String,IndexServiceParameter> outParameterMap = new HashMap<>();
        for(Parameter parameter: service.getOutParameters().getParametersList()) {
            outParameterMap.putAll(extractParameterMap(parameter));
        }
        for(AutoParameters autoParameters: service.getOutParameters().getAutoParametersList()) {
            outParameterMap.putAll(extractAutoParametersFieldMap(autoParameters));
        }
        return outParameterMap;

    }

    private Map<String,IndexServiceParameter> extractParameterMap(@NotNull Parameter parameter){
        IndexServiceParameter indexServiceParameter = new IndexServiceParameter(parameter);
        Map<String,IndexServiceParameter> childParameterMap = new HashMap<>();
        for(Parameter childParameter: parameter.getParameterList()){
           childParameterMap.putAll(extractParameterMap(childParameter));
        }
        for(AutoParameters autoParameters: parameter.getAutoParametersList()) {
           childParameterMap.putAll(extractAutoParametersFieldMap(autoParameters));
        }
        indexServiceParameter.setChildParameterList(childParameterMap);

        Map<String, IndexServiceParameter> result = new HashMap<>();
        result.put(MyDomUtils.getValueOrEmptyString(parameter.getName()),indexServiceParameter);
        return result;
    }

    /**
     * todo include="all",表示service的noun为entity name，包含该entity的所有字段
     * @param autoParameters
     * @return
     */
    private Map<String,IndexServiceParameter> extractAutoParametersFieldMap(AutoParameters autoParameters) {
        Map<String,IndexServiceParameter> result = new HashMap<>();
        List<Field> fieldList = new ArrayList<>();

        String entityName = MyDomUtils.getValueOrEmptyString(autoParameters.getEntityName());
        Optional<IndexEntity> entity = getIndexEntityByName(entityName);
        String include = MyDomUtils.getValueOrEmptyString(autoParameters.getInclude());

        if(entity.isPresent()) {
            fieldList = entity.get().getFieldList().orElse(new ArrayList<>());
            switch (include) {
                case ServiceUtils.SERVICE_AUTO_PARAMETERS_INCLUDE_NONPK:
                    fieldList.forEach(field->{
                        if(Boolean.FALSE.equals(field.getIsPk().getValue())) {
                            result.put(MyDomUtils.getValueOrEmptyString(field.getName()),new IndexServiceParameter(field));
                        }
                    });
                    break;
                case ServiceUtils.SERVICE_AUTO_PARAMETERS_INCLUDE_PK:
                    fieldList.forEach(field->{
                        if(Boolean.TRUE.equals(field.getIsPk().getValue())) {
                            result.put(MyDomUtils.getValueOrEmptyString(field.getName()),new IndexServiceParameter(field));
                        }
                    });

                    break;
                default:
                    fieldList.forEach(field->{
                        result.put(MyDomUtils.getValueOrEmptyString(field.getName()),new IndexServiceParameter(field));
                    });
            }
        }else {
            return result;
        }
        //exclude
        for(Exclude exclude: autoParameters.getExcludeList()) {
            String excludeFieldName = MyDomUtils.getValueOrEmptyString(exclude.getFieldName());
            result.remove(excludeFieldName);
        }
        return result;
    }

    private synchronized void checkAndUpdateMap(){
        /**
         * 先将标志位改掉，在运行中发现线程会多次调用MoquiIndexService,如果不提前修改标志位，会多次重复执行这个过程
         * 更新Entity，必然更新Service，更新Sevice，不一定需要更新Entity
         */

        if (this.entityXmlFileUpdated) {
            this.entityXmlFileUpdated = false;
            this.serviceXmlFileUpdated = false;

            refreshAllEntityMap();
            refreshAllServiceMap();

        }
        if (this.serviceXmlFileUpdated) {
            this.serviceXmlFileUpdated = false;
            refreshAllServiceMap();
        }


    }

    public void setEntityXmlFileUpdated(boolean entityXmlFileUpdated){
        this.entityXmlFileUpdated = entityXmlFileUpdated;
    }
    public void setServiceXmlFileUpdated(boolean serviceXmlFileUpdated){
        this.serviceXmlFileUpdated = serviceXmlFileUpdated;
    }

//    private boolean checkEntityIsNotValid(){
//        for(String key: entityMap.keySet()) {
//            if(!entityMap.get(key).isValid()) return true;
//        }
//        return false;
//    }
//    private boolean checkViewEntityIsNotValue(){
//        for(String key: viewEntityMap.keySet()) {
//            if(!viewEntityMap.get(key).isValid()) return true;
//        }
//        return false;
//
//    }

//    private void removeItemByFilePath(Map<String,DomElement> domElementMap,String filePath) {
//        domElementMap.entrySet().removeIf(entry -> {
//            try{
//                PsiFile psiFile = entry.getValue().getXmlElement().getContainingFile();
//                if (psiFile == null) {
//                    return true;
//                } else {
//                    return psiFile.getVirtualFile().getPath().equals(filePath);
//                }
//            }catch (NullPointerException e){
//                return true;
//            }
//        });
//    }
    /**
     * 根据Entity的名称获取对应的DomElement，名称可以是fullName，shortAlias或entityName
     * @param name
     * @return
     */
    public Optional<Entity> getEntityByName(@NotNull String name) {
        return getIndexEntityByName(name).map(IndexEntity::getEntity);

//        checkAndUpdateAllEntity();
//
//        for(String key: this.entityMap.keySet()) {
//            if(this.entityMap.get(key).isThisEntity(name)) return Optional.of(this.entityMap.get(key).getEntity());
//        }
//        return Optional.empty();
    }
    public Optional<IndexEntity> getIndexEntityByName(@NotNull String name) {
        checkAndUpdateMap();

        for(String key: this.indexEntityMap.keySet()) {
            IndexEntity indexEntity = this.indexEntityMap.get(key);
            if(indexEntity.isThisEntity(name)) return Optional.of(indexEntity);
        }
        return Optional.empty();

    }

    public Optional<ViewEntity> getViewEntityByName(@NotNull String name) {
        return getIndexViewEntityByName(name).map(IndexViewEntity::getViewEntity);
//        checkAndUpdateAllEntity();
//
//        for(String key: this.viewEntityMap.keySet()) {
//            if(this.viewEntityMap.get(key).isThisViewEntity(name)) return Optional.of(this.viewEntityMap.get(key).getViewEntity());
//        }
//        return Optional.empty();
    }
    public Optional<IndexViewEntity> getIndexViewEntityByName(@NotNull String name) {
        checkAndUpdateMap();

        for(String key: this.indexViewEntityMap.keySet()) {
            if(this.indexViewEntityMap.get(key).isThisViewEntity(name)) return Optional.of(this.indexViewEntityMap.get(key));
        }
        return Optional.empty();
    }
//    private DomElement getAbstractEntityByFullName(String name, Map<String, AbstractEntity> abstractEntityMap){
//        int index = name.lastIndexOf(EntityUtils.ENTITY_NAME_DOT);
//        AbstractEntity abstractEntity;
//        if(index >=0) {
//            String entityName = name.substring(index);
//            abstractEntity =  abstractEntityMap.get(entityName).getEntity();
//        }else{
//            abstractEntity =
//        }
//
//        return abstractEntity;
//
//    }
    /**
     * 返回所有符合查询字符串的Entity全名
     * @param searchStr
     * @return
     */
    public List<String> searchEntityAndViewEntityFullNames(String searchStr){
        checkAndUpdateMap();

        if(searchStr == null){searchStr = MyStringUtils.EMPTY_STRING;}

        final String searchToken = trim(searchStr);

        if(Objects.equals(searchToken, MyStringUtils.EMPTY_STRING)) {
            return getAllEntityAndViewEntityFullNames();
        }else {
            List<String> result = new ArrayList<String>();
            indexEntityMap.forEach((key, value)->{if(value.getFullName().indexOf(searchToken)>0) result.add(key);});
            indexViewEntityMap.forEach((key, value)->{if(value.getFullName().indexOf(searchToken)>0) result.add(key);});
            return result;
        }
    }

    public List<String> getAllEntityAndViewEntityFullNames(){
        checkAndUpdateMap();

        List<String> result = new ArrayList<String>();
        this.indexEntityMap.forEach((key, value)->{result.add(value.getFullName());});
        this.indexViewEntityMap.forEach((key, value)->{result.add(value.getFullName());});
        return result;
    }

    public Map<String,DomElement> getAllEntityDomElements(){
        checkAndUpdateMap();

        Map<String,DomElement> result = new HashMap<String,DomElement>();
        this.indexEntityMap.forEach((key, value)->{result.put(value.getFullName(),value.getEntity());});
        this.indexViewEntityMap.forEach((key, value)->{result.put(value.getFullName(),value.getViewEntity());});
        return result;
    }

    public Optional<List<Entity>> getAllEntity(){
        checkAndUpdateMap();
        List<Entity> result = new ArrayList<>();

        for(String key : indexEntityMap.keySet()) {
            result.add(indexEntityMap.get(key).getEntity());
        }
        return Optional.of(result);
    }
    public Optional<List<ViewEntity>> getAllViewEntity(){
        checkAndUpdateMap();
        List<ViewEntity> result = new ArrayList<>();
        for(String key : indexViewEntityMap.keySet()) {
            result.add(indexViewEntityMap.get(key).getViewEntity());
        }
        return Optional.of(result);
    }

    public Collection<AbstractEntity> getAllEntityAndViewEntity(){
        checkAndUpdateMap();
        Collection<AbstractEntity> result = new ArrayList<>();

        for(String key : indexEntityMap.keySet()) {
            result.add(indexEntityMap.get(key).getEntity());
        }

        for(String key : indexViewEntityMap.keySet()) {
            result.add(indexViewEntityMap.get(key).getViewEntity());
        }
        return result;
    }

    public Optional<AbstractEntity> getEntityOrViewEntity(@NotNull String name) {
        Optional<Entity> entity;
        entity = getEntityByName(name);
        if(entity.isPresent()){
            return Optional.of(entity.get());
        }else {
            Optional<ViewEntity> viewEntity = getViewEntityByName(name);
            if(viewEntity.isPresent()){
                return Optional.of(viewEntity.get());
            }else {
                return Optional.empty();
            }
        }

    }
    public Optional<List<AbstractField>> getEntityOrViewEntityFieldList(@NotNull String name){
        Optional<IndexEntity> indexEntity = getIndexEntityByName(name);
        if(indexEntity.isPresent()) {
            return indexEntity.get().getAbstractFieldList();
        }else {
            Optional<IndexViewEntity> indexViewEntity = getIndexViewEntityByName(name);
            return indexViewEntity.flatMap(IndexViewEntity::getAbstractFieldList);

        }


    }
    public Optional<List<Field>> getEntityFieldList(@NotNull String name){
        Optional<IndexEntity> indexEntity = getIndexEntityByName(name);
        return indexEntity.flatMap(IndexEntity::getFieldList);
    }

    public Optional<List<ExtendEntity>> getExtendEntityListByEntityName(@NotNull String name){
        Optional<IndexEntity> indexEntity = getIndexEntityByName(name);
        return indexEntity.map(IndexEntity::getExtendEntityList);
    }
    public Optional<List<Relationship>> getRelationshipListByEntityName(@NotNull String name){
        Optional<IndexEntity> indexEntity = getIndexEntityByName(name);
        return indexEntity.map(IndexEntity::getRelationshipList);
    }

    public Map<String,IndexEntity> getIndexEntityMap(){return this.indexEntityMap;}
    public Map<String,IndexViewEntity> getIndexViewEntityMap(){return this.indexViewEntityMap;}

    public Map<String,IndexService> getIndexServiceMap(){return this.indexServiceMap;}

    public Optional<org.moqui.idea.plugin.dom.model.Service> getServiceByFullName(@NotNull String fullName) {
        IndexService service = this.indexServiceMap.get(fullName);
        if(service == null) {
            return Optional.empty();
        }else {
            return Optional.ofNullable(service.getService());
        }
    }
    public Optional<Set<String>> searchServicePackageNameSet(@Nullable String filterStr){
        if(filterStr == null) {
            return getServicePackageNameSet();
        }else{
        return Optional.of(
                this.indexServiceMap.values().stream().map(IndexService::getPackageName).filter(
                        packageName -> packageName.contains(filterStr)
                ).collect(Collectors.toSet()));
        }
    }
    public Optional<Set<String>> getServicePackageNameSet(){
        return Optional.of
                (this.indexServiceMap.values().stream().map(IndexService::getPackageName).collect(Collectors.toSet())
        );
    }
    public Optional<Set<String>> getServiceClassNameSet(){
        return Optional.of
                (this.indexServiceMap.values().stream().map(IndexService::getClassName).collect(Collectors.toSet())
                );
    }
    public Optional<Set<String>> searchServiceClassNameSet(@Nullable String filterStr){
        if(filterStr == null) {
            return getServiceClassNameSet();
        }else{
            return Optional.of(
                    this.indexServiceMap.values().stream().map(IndexService::getClassName).filter(
                            className -> className.contains(filterStr)
                    ).collect(Collectors.toSet()));
        }
    }
    public Optional<Set<String>> searchInterfaceAndServiceFullNameSet(@Nullable String filterStr){
        Set<String> all = getInterfaceAndServiceFullNameSet().orElse(new HashSet<>());
        if(all.isEmpty()) return Optional.empty();

        if(filterStr == null) {
            return Optional.of(all);
        }else{
            return Optional.of(
                    all.stream().filter(
                            fullName -> fullName.contains(filterStr)
                    ).collect(Collectors.toSet()));
        }
    }

    public Optional<Set<String>> getInterfaceAndServiceFullNameSet(){
        Set<String> result = new HashSet<>();
        result.addAll(this.indexServiceMap.keySet());
        result.addAll(this.indexInterfaceMap.keySet());

        return Optional.of(result);
    }
    public Optional<Set<String>> searchServiceFullNameSet(@Nullable String filterStr){
        Set<String> all = getServiceFullNameSet().orElse(new HashSet<>());
        if(all.isEmpty()) return Optional.empty();

        if(filterStr == null) {
            return Optional.of(all);
        }else{
            return Optional.of(
                    all.stream().filter(
                            fullName -> fullName.contains(filterStr)
                    ).collect(Collectors.toSet()));
        }
    }
    public Optional<Set<String>> getInterfaceFullNameSet(){
        return Optional.of(this.indexInterfaceMap.keySet());
    }
    public Optional<Set<String>> getServiceFullNameSet(){
        return Optional.of(this.indexServiceMap.keySet());
    }

    public List<ViewEntity> getPendingViewEntityList(){return pendingViewEntityList;}
}
