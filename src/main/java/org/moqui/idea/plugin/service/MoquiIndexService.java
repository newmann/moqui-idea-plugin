package org.moqui.idea.plugin.service;

import com.intellij.openapi.vfs.VirtualFileManager;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.listener.MoquiXmlVirtualFileManager;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 建立Entity，ViewEntity和Service的缓存
 * 为了简化因为Entity等定义修改后调整这个缓存，在每次使用缓存之前检查一下缓存是否有效，如果无效，则更新整个缓存
 * 如果未来效率有问题，可以改进更新做法，进行更细致的控制
 */
@Service(Service.Level.PROJECT)
public final class MoquiIndexService {
    private static final Logger LOGGER = Logger.getLogger(MoquiIndexService.class.getName());
    private final Project project;
    /**
     * 通过这个两个标志位跟踪Entity 和Service的xml定义文件是否被改动，
     * 如果Entity被改动，则需要重新刷新entityMap、viewEntityMap和ServiceMap
     * 如果Service被改动，则需要刷新Service
     */
//    private boolean entityXmlFileUpdated;
    //最近entity文件改动时间戳
    private long entityXmlFileLastUpdatedStamp;
    //最近一次全局entity扫描的时间戳
//    private long allEntityLastRefreshStamp;

//    private boolean serviceXmlFileUpdated;
    //最近service文件改动时间戳
    private long serviceXmlFileLastUpdatedStamp;
    //最近一次全局service扫描的时间戳
//    private long allServiceLastRefreshStamp;

    private final ConcurrentHashMap<String, IndexEntity> indexEntityMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IndexViewEntity> indexViewEntityMap =new ConcurrentHashMap<>();
//    private Map<String, ExtendEntity> extendEntityMap = new HashMap<>();

    private final ConcurrentHashMap<String, IndexService> indexServiceMap = new ConcurrentHashMap<>();

    //处理service的interface定义
    private final ConcurrentHashMap<String,IndexService> indexInterfaceMap = new ConcurrentHashMap<>();

    private static final Object MUTEX_ENTITY = new Object();
    private static final Object MUTEX_VIEW = new Object();
    private static final Object MUTEX_SERVICE = new Object();
    private static final Object MUTEX_INTERFACE = new Object();

    private List<ViewEntity> pendingViewEntityList;
    private final MoquiXmlVirtualFileManager moquiXmlVirtualFileManager;
    public MoquiIndexService(Project project) {
        this.project = project;
//        this.entityXmlFileUpdated = false;
//        this.serviceXmlFileUpdated = false;

        this.entityXmlFileLastUpdatedStamp = System.currentTimeMillis();
        this.serviceXmlFileLastUpdatedStamp = System.currentTimeMillis();

//        this.allEntityLastRefreshStamp = 0;
//        this.allServiceLastRefreshStamp = 0;


//        refreshAllEntityMap();
//        refreshAllServiceMap();


        this.moquiXmlVirtualFileManager = new MoquiXmlVirtualFileManager(project);
        VirtualFileManager.getInstance().addVirtualFileListener(this.moquiXmlVirtualFileManager);

    }

//    private void refreshAllEntityMap(){
//        //这里初始化，需要注意先后次序：Entity->ExtendEntity->ViewEntity，最后才是Service
//        this.indexEntityMap = new ConcurrentHashMap<>();
//        this.indexViewEntityMap = new ConcurrentHashMap<>();
//        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(this.project,Entities.class);
//        fileElementList.forEach(this::updateEntityFromFile);
//        fileElementList.forEach(this::updateExtendEntityFromFile);
//        updateViewEntity(fileElementList);
//
//
//    }
//    private void refreshAllServiceMap(){
//        //先处理interface，再处理service
//        this.indexServiceMap = new HashMap<>();
//        this.indexInterfaceMap = new HashMap<>();
//
//        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(this.project, Services.class);
//
//        fileElementList.forEach(this::updateInterfaceFromFile);
//        fileElementList.forEach(this::updateServiceFromFile);
//
//    }

    public void unRegisterListener(){
        VirtualFileManager.getInstance().removeVirtualFileListener(this.moquiXmlVirtualFileManager);
    }
    /**
     * 更新某个entities.xml定义文件的内容，
     * @param fileElement
     */
//    private void updateEntityFromFile(DomFileElement<Entities> fileElement){
////        final String filePath = fileElement.getOriginalFile().getVirtualFile().getPath();
//        for(Entity entity: fileElement.getRootElement().getEntities()) {
//            IndexEntity indexEntity = new IndexEntity(entity);
//            indexEntityMap.put(indexEntity.getFullName(),indexEntity);
//        };
//    }
//    private void updateExtendEntityFromFile(DomFileElement<Entities> fileElement){
////        final String filePath = fileElement.getOriginalFile().getVirtualFile().getPath();
//        for(ExtendEntity extendEntity : fileElement.getRootElement().getExtendEntities()) {
//            accessIndexEntityByName(MyDomUtils.getValueOrEmptyString(extendEntity.getEntityName()))
//                    .ifPresent(indexEntity -> indexEntity.AddExtendEntity(extendEntity));
//        };
//    }
//
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
//    private void updateViewEntity(List<DomFileElement<Entities>> fileElementList){
//        List<ViewEntity> pendingViewEntities = new ArrayList<>();
//
//        for(DomFileElement<Entities> fileElement : fileElementList) {
//            for(ViewEntity viewEntity : fileElement.getRootElement().getViewEntities()) {
//                if(!addViewEntityToMap(viewEntity)) {
//                    pendingViewEntities.add(viewEntity);
//                }
//            }
//        }
//        for(int i=0; i<10; i++) {
//            pendingViewEntities.removeIf(this::addViewEntityToMap);
//        }
//        //将未处理的ViewEntity保存下来，在ToolWindows中进行后续的显示
//        this.pendingViewEntityList = pendingViewEntities;
//
//    }
    /**
     * 判断当前的ViewEntity是否能获取到字段定义，即对应的MemberEntity或MemberRelationship都已经获得了Field
     * @param viewEntity
     * @return
     */
//    private boolean addViewEntityToMap(@NotNull ViewEntity viewEntity){
//        Map<String,AbstractIndexEntity> entityMap = new HashMap<>();
//
//        for(MemberEntity memberEntity: viewEntity.getMemberEntityList()){
//            Optional<AbstractIndexEntity> optionalAbstractEntity =accessIndexEntityOrIndexViewEntity(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName()));
//            if (optionalAbstractEntity.isEmpty()) {
//                return false;
//            }else {
//                entityMap.put(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityAlias()),optionalAbstractEntity.get());
//            }
//        };
//        List<MemberRelationship> pending = new ArrayList<>(viewEntity.getMemberRelationshipList());
//
//
//        //MemberRelationship可能是指向另外一个MemberRelationship，这时候需要先处理指向MemberEntity的，然后再一层一层找
//        for(int i=0; i<10; i++) {
//            Iterator<MemberRelationship> iterator = pending.iterator();
//            while (iterator.hasNext()) {
//                MemberRelationship memberRelationship = iterator.next();
//                String joinAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getJoinFromAlias());
//                AbstractIndexEntity foundAbstractIndexEntity = entityMap.get(joinAlias);
//                String relationshipName = MyDomUtils.getValueOrEmptyString(memberRelationship.getRelationship());
//                String entityAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getEntityAlias());
//
//                if (foundAbstractIndexEntity instanceof IndexEntity foundIndexEntity) {
//                    Relationship currentRelationship = foundIndexEntity.getRelationshipByName(relationshipName).orElse(null);
//                    if (currentRelationship != null) {
//                        Optional<AbstractIndexEntity> optRelatedIndexEntity = accessIndexEntityOrIndexViewEntity(MyDomUtils.getValueOrEmptyString(currentRelationship.getRelated()));
//                        if (optRelatedIndexEntity.isPresent()) {
//                            entityMap.put(entityAlias, optRelatedIndexEntity.get());
//                            iterator.remove();
//                        }
//                    }
//                }
//            }
//        }
//        if(!pending.isEmpty()) return false;
//
//        Map<String, IndexAbstractField> fieldMap = extractViewEntityFieldMap(viewEntity,entityMap);
//
//        IndexViewEntity indexViewEntity = new IndexViewEntity(viewEntity);
//        indexViewEntity.setAbstractFieldMap(fieldMap);
//        indexViewEntity.setAbstractIndexEntityMap(entityMap);
//        this.indexViewEntityMap.put(indexViewEntity.getFullName(), indexViewEntity);
//        return true;
//
//    }

    private boolean addViewEntityToIndexViewEntityMap(@NotNull ViewEntity viewEntity){
        Map<String,AbstractIndexEntity> aliasEntityMap = new HashMap<>();

        for(MemberEntity memberEntity: viewEntity.getMemberEntityList()){
            String entityName = MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName());
            //如果指向viewEntity本身，则跳过，避免死循环
            if(EntityUtils.isThisEntityName(viewEntity,entityName))continue;

            Optional<AbstractIndexEntity> optionalAbstractEntity =getIndexEntityOrIndexViewEntity(entityName);
            if (optionalAbstractEntity.isEmpty()) {
                continue;
                //return false;//如果没有找到，先跳过去
            }else {
                aliasEntityMap.put(MyDomUtils.getValueOrEmptyString(memberEntity.getEntityAlias()),optionalAbstractEntity.get());
            }
        };
        List<MemberRelationship> pending = new ArrayList<>(viewEntity.getMemberRelationshipList());


        //MemberRelationship可能是指向另外一个MemberRelationship，这时候需要先处理指向MemberEntity的，然后再一层一层找
        for(int i=0; i<10; i++) {
            Iterator<MemberRelationship> iterator = pending.iterator();
            while (iterator.hasNext()) {
                MemberRelationship memberRelationship = iterator.next();
                String joinAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getJoinFromAlias());
                AbstractIndexEntity foundAbstractIndexEntity = aliasEntityMap.get(joinAlias);
                String relationshipName = MyDomUtils.getValueOrEmptyString(memberRelationship.getRelationship());
                String entityAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getEntityAlias());

                if (foundAbstractIndexEntity instanceof IndexEntity foundIndexEntity) {
                    Relationship currentRelationship = foundIndexEntity.getRelationshipByName(relationshipName).orElse(null);
                    if (currentRelationship != null) {
                        Optional<AbstractIndexEntity> optRelatedIndexEntity = getIndexEntityOrIndexViewEntity(MyDomUtils.getValueOrEmptyString(currentRelationship.getRelated()));
                        if (optRelatedIndexEntity.isPresent()) {
                            aliasEntityMap.put(entityAlias, optRelatedIndexEntity.get());
                            iterator.remove();
                        }
                    }
                }
            }
        }
        if(!pending.isEmpty()) return false;

        Map<String,IndexAbstractField> fieldMap = extractViewEntityFieldMap(viewEntity,aliasEntityMap);

        IndexViewEntity indexViewEntity = new IndexViewEntity(viewEntity);
        indexViewEntity.setIndexAbstractFieldMap(fieldMap);
        indexViewEntity.setAbstractIndexEntityMap(aliasEntityMap);

        indexViewEntity.setLastRefreshStamp(System.currentTimeMillis());

        this.indexViewEntityMap.put(indexViewEntity.getFullName(), indexViewEntity);
        return true;

    }

//    private Map<String, AbstractIndexEntity> extractViewEntityAbstractIndexEntity(@NotNull ViewEntity viewEntity){
//        Map<String, AbstractIndexEntity> result = new HashMap<String, AbstractIndexEntity>();
//        String entityAlias;
//        String entityName;
//        AbstractIndexEntity abstractIndexEntity;
//        //先添加MemberEntity
//        for(MemberEntity memberEntity: viewEntity.getMemberEntityList()) {
//            entityAlias = MyDomUtils.getValueOrEmptyString(memberEntity.getEntityAlias());
//            entityName = MyDomUtils.getValueOrEmptyString(memberEntity.getEntityName());
//            abstractIndexEntity = accessIndexEntityOrIndexViewEntity(entityName).orElse(null);
//            if (abstractIndexEntity != null){
//                result.put(entityAlias, abstractIndexEntity);
//            }
//        };
//        //再添加MemberRelationship
//        for(MemberRelationship memberRelationship: viewEntity.getMemberRelationshipList()) {
//            entityAlias = MyDomUtils.getValueOrEmptyString(memberRelationship.getEntityAlias());
//            entityName = getViewEntityMemberRelationshipEntityName(viewEntity, memberRelationship)
//                    .orElse(MyStringUtils.EMPTY_STRING);
//            abstractIndexEntity = accessIndexEntityOrIndexViewEntity(entityName).orElse(null);
//            if (abstractIndexEntity != null){
//                result.put(entityAlias, abstractIndexEntity);
//            }
//        }
//        return  result;
//    }

    /**
     * 获取指定ViewEntity的所有字段
     * @param viewEntity 当前的ViewEntity
     * @param abstractIndexEntityMap 当前ViewEntity的alias对应的AbstractIndexEntity
     * @return
     */
    private Map<String, IndexAbstractField> extractViewEntityFieldMap(@NotNull ViewEntity viewEntity, @NotNull Map<String,AbstractIndexEntity> abstractIndexEntityMap) {
        Map<String, IndexAbstractField> result = new HashMap<>();
        List<IndexAbstractField> fieldList = new ArrayList<>();
        //添加alias
        for(Alias alias: viewEntity.getAliasList()) {
            String aliasName = MyDomUtils.getValueOrEmptyString(alias.getEntityAlias());
            if(MyStringUtils.isEmpty(aliasName)) continue;
            //根据alias在abstractIndexEntityMap中查找对应AbstractIndexEntity
            AbstractIndexEntity abstractIndexEntity = abstractIndexEntityMap.get(aliasName);
            if(abstractIndexEntity == null) continue;
            fieldList.add(IndexAbstractField.of(abstractIndexEntity,alias));
        }
//        List<IndexAbstractField> fieldList = new LinkedList<>(viewEntity.getAliasList().stream().map(IndexAbstractField::of).toList());
                //new LinkedList<>(viewEntity.getAliasList());
        //对AliasAll进行处理
        List<AliasAll> aliasAllList = viewEntity.getAliasAllList();
        for(AliasAll aliasAll : aliasAllList) {
            String aliasName = MyDomUtils.getValueOrEmptyString(aliasAll.getEntityAlias());
            if(MyStringUtils.isEmpty(aliasName)) continue;

//            List<AbstractField> aliasAllFieldList = new ArrayList<>();

            //根据alias在abstractIndexEntityMap中查找对应AbstractIndexEntity
            AbstractIndexEntity abstractIndexEntity = abstractIndexEntityMap.get(aliasName);
            if(abstractIndexEntity == null) continue;

            //todo 如果IndexAbstractField也是个带prefix的AliasAll生成的话，就出问题了，但现在看所有的viewEntity都没有出现这种情况，还不知道framework是否支持这种view的定义方式

            List<IndexAbstractField> aliasAllFieldList = abstractIndexEntity.getIndexAbstractFieldList();
            List<IndexAbstractField> exclutedAliasAllFieldList = EntityUtils.excludeFields(aliasAllFieldList,aliasAll.getExcludeList());

            //在将AliasAll和字段进行绑定，以便处理prefix

            fieldList.addAll(
                    exclutedAliasAllFieldList.stream()
                            .map(item->IndexAbstractField.of(abstractIndexEntity,item.getAbstractField(),aliasAll))
                            .toList()
            );

        }

        for(IndexAbstractField field: fieldList) {
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
//            if(viewEntity.getXmlElement() == null) return Optional.empty();

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
//    private Optional<List<IndexAbstractField>> accessEntityOrViewEntityFields(@NotNull String entityName){
//        Optional<IndexEntity> entity = accessIndexEntityByName(entityName);
//        if (entity.isPresent()) {
//            return entity.get().getIndexAbstractFieldList();
//        }else {
//            Optional<IndexViewEntity> viewEntity = accessIndexViewEntityByName(entityName);
//            if(viewEntity.isPresent()) {
//                return viewEntity.get().getIndexAbstractFieldList();
//            }
//
//        }
//        return Optional.empty();
//    }
    private Optional<IndexService> getIndexInterfaceByFullName(@NotNull String fullName){
        checkAndUpdateInterface(fullName);
        return accessIndexInterfaceByFullName(fullName);
    }

    private Optional<IndexService> accessIndexInterfaceByFullName(@NotNull String fullName){
        return Optional.ofNullable(this.indexInterfaceMap.get(fullName));

//        for(String key: this.indexInterfaceMap.keySet()) {
//            IndexService indexInterface = this.indexInterfaceMap.get(key);
//            if(indexInterface.isThisService(fullName)) return Optional.of(indexInterface);
//        }
//        return Optional.empty();
    }
    private Optional<IndexService> accessIndexServiceByName(@NotNull String serviceName){
        return Optional.ofNullable(this.indexServiceMap.get(serviceName));

    }

    private boolean addServiceToIndexServiceMap(@NotNull org.moqui.idea.plugin.dom.model.Service service){
        IndexService indexService = new IndexService(service);
        //处理interface
        Map<String,IndexServiceParameter> inParameterMap = new HashMap<>();
        Map<String,IndexServiceParameter> outParameterMap = new HashMap<>();
        for(Implements implementsItem: service.getImplementsList()){
            IndexService interfaceService = getIndexInterfaceByFullName(MyDomUtils.getValueOrEmptyString(implementsItem.getService())).orElse(null);
            if (interfaceService != null) {
                inParameterMap.putAll(interfaceService.getInParameterMap());
                outParameterMap.putAll(interfaceService.getOutParameterMap());
            }
        }
        inParameterMap.putAll(extractServiceInParameterFieldMap(service));
        outParameterMap.putAll(extractServiceOutParameterFieldMap(service));
        indexService.setInParameterMap(inParameterMap);
        indexService.setOutParameterMap(outParameterMap);

        indexService.setLastRefreshStamp(System.currentTimeMillis());

        indexServiceMap.put(indexService.getFullName(), indexService);

        return true;

    }
    private boolean addInterfaceToIndexInterfaceMap(@NotNull org.moqui.idea.plugin.dom.model.Service service){
        IndexService indexService = new IndexService(service);
        //处理interface

        Map<String, IndexServiceParameter> inParameterMap = new HashMap<>(extractServiceInParameterFieldMap(service));
        Map<String, IndexServiceParameter> outParameterMap = new HashMap<>(extractServiceOutParameterFieldMap(service));
        indexService.setInParameterMap(inParameterMap);
        indexService.setOutParameterMap(outParameterMap);

        indexService.setLastRefreshStamp(System.currentTimeMillis());

        indexInterfaceMap.put(indexService.getFullName(), indexService);

        return true;

    }

    /**
     * 返回service的传入参数
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

    /**
     * 返回service的传出参数
     * @param service
     * @return
     */
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

    /**
     * 分析Parameter结构，返回Map
     * @param parameter
     * @return
     */
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
                case ServiceUtils.SERVICE_AUTO_PARAMETERS_INCLUDE_NONPK-> {
                    for(Field field: fieldList){
                        Boolean isPk = field.getIsPk().getValue();

                        if ((isPk == null) || Boolean.FALSE.equals(isPk)) {
                            result.put(MyDomUtils.getValueOrEmptyString(field.getName()), new IndexServiceParameter(field));
                        }
                    }
                }
                case ServiceUtils.SERVICE_AUTO_PARAMETERS_INCLUDE_PK-> {
                    for(Field field: fieldList){
                        Boolean isPk = field.getIsPk().getValue();
                        if ((Boolean.TRUE.equals(isPk))) {
                            result.put(MyDomUtils.getValueOrEmptyString(field.getName()), new IndexServiceParameter(field));
                        }
                    }
                }
                default-> {
                    for(Field field:fieldList){
                        result.put(MyDomUtils.getValueOrEmptyString(field.getName()), new IndexServiceParameter(field));
                    }
                }
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

    /**
     * 处理Entity的更新
     */
    private void checkAndUpdateEntity(@NotNull String entityName){
        synchronized (MUTEX_ENTITY) {
            IndexEntity indexEntity = accessIndexEntityByName(entityName).orElse(null);
            if(indexEntity != null && indexEntity.getLastRefreshStamp()>= this.entityXmlFileLastUpdatedStamp ) return;

            Entity entity = EntityUtils.getEntityByNameFromFile(this.project, entityName).orElse(null);
            if(entity==null) {
                removeIndexEntityByName(entityName);
                return;
            }

            List<ExtendEntity> extendEntityList =EntityUtils.getExtendEntityListByNameFromFile(this.project,entityName).orElse(new ArrayList<>());
            //添加
            if (indexEntity == null) {
                indexEntity = new IndexEntity(entity,extendEntityList);
                this.indexEntityMap.put(indexEntity.fullName,indexEntity);
            }
            else {
                //更新
                indexEntity.setEntity(entity);
                indexEntity.setExtendEntityList(extendEntityList);
                indexEntity.RefreshEntity();
            }

        }

    }
    /**
     * 处理ViewEntity的更新
     */
    private void checkAndUpdateView(@NotNull String viewName){
        synchronized (MUTEX_VIEW) {
            IndexViewEntity indexViewEntity = (IndexViewEntity)accessIndexViewEntityByName(viewName).orElse(null);
            if(indexViewEntity != null && indexViewEntity.getLastRefreshStamp()>= this.entityXmlFileLastUpdatedStamp ) return;

            ViewEntity viewEntity = EntityUtils.getViewEntityByNameFromFile(this.project, viewName).orElse(null);
            if(viewEntity==null) {
                //从IndexViewEntityMap中删除
                removeIndexViewEntityByName(viewName);
            }else {
                //添加
                addViewEntityToIndexViewEntityMap(viewEntity);
            }

        }

    }

    /**
     * 处理Service更新
     * @param fullName
     */
    private void checkAndUpdateService(@NotNull String fullName){
        synchronized (MUTEX_SERVICE) {
            IndexService indexService = accessIndexServiceByFullName(fullName).orElse(null);
            if(indexService != null && indexService.getLastRefreshStamp()>= this.serviceXmlFileLastUpdatedStamp ) return;

            org.moqui.idea.plugin.dom.model.Service service = ServiceUtils.getServiceByFullNameFromFile(this.project, fullName).orElse(null);
            if(service==null) {
                //从IndexServiceMap中删除
                removeIndexServiceByName(fullName);
            }else {
                if(ServiceUtils.isService(service)) {
                    //添加
                    addServiceToIndexServiceMap(service);
                }else {
                    //从IndexServiceMap中删除
                    removeIndexServiceByName(fullName);
                }
            }

        }

    }
    private void checkAndUpdateInterface(@NotNull String fullName){
        synchronized (MUTEX_INTERFACE) {
            IndexService indexInterface = accessIndexInterfaceByFullName(fullName).orElse(null);
            if(indexInterface != null && indexInterface.getLastRefreshStamp()>= this.serviceXmlFileLastUpdatedStamp ) return;

            org.moqui.idea.plugin.dom.model.Service service = ServiceUtils.getServiceByFullNameFromFile(this.project, fullName).orElse(null);
            if(service==null) {
                //从IndexInterfaceMap中删除
                removeIndexInterfaceByName(fullName);
            }else {
                if(ServiceUtils.isInterface(service)) {
                    //添加
                    addInterfaceToIndexInterfaceMap(service);

                }else {
                    //从IndexInterfaceMap中删除
                    removeIndexInterfaceByName(fullName);

                }
            }

        }

    }
    private void removeIndexViewEntityByName(@NotNull String name) {
        for(String key: this.indexViewEntityMap.keySet()) {
            if(this.indexViewEntityMap.get(key).isThisEntity(name)) this.indexViewEntityMap.remove(key);
        }
    }
    private void removeIndexEntityByName(@NotNull String name) {
        for(String key: this.indexEntityMap.keySet()) {
            if(this.indexEntityMap.get(key).isThisEntity(name)) this.indexEntityMap.remove(key);
        }
    }
    private void removeIndexServiceByName(@NotNull String name) {
        this.indexServiceMap.remove(name);
    }
    private void removeIndexInterfaceByName(@NotNull String name) {
        this.indexInterfaceMap.remove(name);
    }

//    private synchronized void checkAndUpdateMap(){
//        /**
//         * 先将标志位改掉，在运行中发现线程会多次调用MoquiIndexService,如果不提前修改标志位，会多次重复执行这个过程
//         * 更新Entity，必然更新Service，更新Sevice，不一定需要更新Entity
//         */
////        LOGGER.warning(Thread.currentThread().getName());
////
////        if (this.entityXmlFileUpdated) {
////            this.entityXmlFileUpdated = false;
////            this.serviceXmlFileUpdated = false;
////
////            refreshAllEntityMap();
////            refreshAllServiceMap();
////
////        }
////        if (this.serviceXmlFileUpdated) {
////            this.serviceXmlFileUpdated = false;
////            refreshAllServiceMap();
////        }
//
//
//    }

//    public void setEntityXmlFileUpdated(boolean entityXmlFileUpdated){
//        this.entityXmlFileUpdated = entityXmlFileUpdated;
//    }
//    public void setServiceXmlFileUpdated(boolean serviceXmlFileUpdated){
//        this.serviceXmlFileUpdated = serviceXmlFileUpdated;
//    }
    private Optional<Entity> accessEntityByName(@NotNull String name) {
        return accessIndexEntityByName(name).map(IndexEntity::getEntity);
    }


    /**
     * 根据Entity的名称获取对应的DomElement，名称可以是fullName，shortAlias或entityName
     * @param name
     * @return
     */
    public Optional<Entity> getEntityByName(@NotNull String name) {
        return getIndexEntityByName(name).map(IndexEntity::getEntity);
//        checkAndUpdateEntity(name);
//        return accessEntityByName(name);

    }
    private Optional<IndexEntity> accessIndexEntityByName(@NotNull String name) {
        for(String key: this.indexEntityMap.keySet()) {
            IndexEntity indexEntity = this.indexEntityMap.get(key);
            if(indexEntity.isThisEntity(name)) return Optional.of(indexEntity);
        }
        return Optional.empty();

    }

    public Optional<IndexEntity> getIndexEntityByName(@NotNull String name) {
        checkAndUpdateEntity(name);
        return  accessIndexEntityByName(name);


    }
    public Optional<AbstractIndexEntity> getIndexEntityOrIndexViewEntity(@NotNull String name) {
        IndexEntity indexEntity  = getIndexEntityByName(name).orElse(null);
        if(indexEntity != null){
            return Optional.of(indexEntity);
        }else {
            return Optional.ofNullable(getIndexViewEntityByName(name).orElse(null));
        }


    }
    private Optional<ViewEntity> accessViewEntityByName(@NotNull String name) {
        return accessIndexViewEntityByName(name).map(IndexViewEntity::getViewEntity);

    }

//    public Optional<ViewEntity> getViewEntityByName(@NotNull String name) {
//        checkAndUpdateView(name);
//        return accessViewEntityByName(name);
//    }
    private Optional<IndexViewEntity> accessIndexViewEntityByName(@NotNull String name) {
        for(String key: this.indexViewEntityMap.keySet()) {
            if(this.indexViewEntityMap.get(key).isThisEntity(name)) return Optional.of(this.indexViewEntityMap.get(key));
        }
        return Optional.empty();
    }
    public Optional<IndexViewEntity> getIndexViewEntityByName(@NotNull String name) {
        checkAndUpdateView(name);
        return accessIndexViewEntityByName(name);

    }

//    /**
//     * 返回所有符合查询字符串的Entity全名
//     * @param searchStr
//     * @return
//     */
//    public List<String> searchEntityAndViewEntityFullNames(String searchStr){
//        checkAndUpdateMap();
//
//        if(searchStr == null){searchStr = MyStringUtils.EMPTY_STRING;}
//
//        final String searchToken = trim(searchStr);
//
//        if(Objects.equals(searchToken, MyStringUtils.EMPTY_STRING)) {
//            return getAllEntityAndViewEntityFullNames();
//        }else {
//            List<String> result = new ArrayList<String>();
//            indexEntityMap.forEach((key, value)->{if(value.getFullName().indexOf(searchToken)>0) result.add(key);});
//            indexViewEntityMap.forEach((key, value)->{if(value.getFullName().indexOf(searchToken)>0) result.add(key);});
//            return result;
//        }
//    }

//    public List<String> getAllEntityAndViewEntityFullNames(){
//        checkAndUpdateMap();
//
//        List<String> result = new ArrayList<String>();
//        this.indexEntityMap.forEach((key, value)->{result.add(value.getFullName());});
//        this.indexViewEntityMap.forEach((key, value)->{result.add(value.getFullName());});
//        return result;
//    }

//    public Map<String,DomElement> getAllEntityDomElements(){
//        checkAndUpdateMap();
//
//        Map<String,DomElement> result = new HashMap<String,DomElement>();
//        this.indexEntityMap.forEach((key, value)->{result.put(value.getFullName(),value.getEntity());});
//        this.indexViewEntityMap.forEach((key, value)->{result.put(value.getFullName(),value.getViewEntity());});
//        return result;
//    }

//    public Optional<List<Entity>> getAllEntity(){
//        checkAndUpdateMap();
//        List<Entity> result = new ArrayList<>();
//
//        for(String key : indexEntityMap.keySet()) {
//            result.add(indexEntityMap.get(key).getEntity());
//        }
//        return Optional.of(result);
//    }
//    public Optional<List<ViewEntity>> getAllViewEntity(){
//        checkAndUpdateMap();
//        List<ViewEntity> result = new ArrayList<>();
//        for(String key : indexViewEntityMap.keySet()) {
//            result.add(indexViewEntityMap.get(key).getViewEntity());
//        }
//        return Optional.of(result);
//    }

//    public Collection<AbstractEntity> getAllEntityAndViewEntity(){
//        checkAndUpdateMap();
//        Collection<AbstractEntity> result = new ArrayList<>();
//
//        for(String key : indexEntityMap.keySet()) {
//            result.add(indexEntityMap.get(key).getEntity());
//        }
//
//        for(String key : indexViewEntityMap.keySet()) {
//            result.add(indexViewEntityMap.get(key).getViewEntity());
//        }
//        return result;
//    }
    private Optional<AbstractIndexEntity> accessIndexEntityOrIndexViewEntity(@NotNull String name) {

        Optional<IndexEntity> indexEntity = accessIndexEntityByName(name);
        if(indexEntity.isPresent()){
            return Optional.of(indexEntity.get());
        }else {
            Optional<IndexViewEntity> indexViewEntity = accessIndexViewEntityByName(name);
            return Optional.ofNullable(indexViewEntity.orElse(null));
        }

    }
    private Optional<IndexService> accessIndexServiceOrIndexInterface(@NotNull String name) {

        Optional<IndexService> optIndexService = accessIndexServiceByName(name);
        //            Optional<IndexService> optIndexInterface = accessIndexInterfaceByName(name);
        //            if(optIndexInterface.isPresent()){
        //                return Optional.of(optIndexInterface.get());
        //            }else {
        //                return Optional.empty();
        //            }
        return optIndexService.or(() -> accessIndexInterfaceByFullName(name));

    }
    public Optional<AbstractEntity> getEntityOrViewEntity(@NotNull String name) {
        return getIndexEntityOrIndexViewEntity(name).flatMap(AbstractIndexEntity::getAbstractEntity);
    }

//    private Optional<AbstractEntity> accessEntityOrViewEntity(@NotNull String name) {
//        Optional<Entity> entity;
//        entity = accessEntityByName(name);
//        if(entity.isPresent()){
//            return Optional.of(entity.get());
//        }else {
////            Optional<ViewEntity> viewEntity = accessViewEntityByName(name);
//            return Optional.ofNullable(accessViewEntityByName(name).orElse(null));
//        }
//
//    }

    public @NotNull List<IndexAbstractField> getEntityOrViewEntityFieldList(@NotNull String name){
        Optional<AbstractIndexEntity> optionalAbstractIndexEntity = getIndexEntityOrIndexViewEntity(name);
        return optionalAbstractIndexEntity.map(AbstractIndexEntity::getIndexAbstractFieldList).orElse(new ArrayList<>());

//        checkAndUpdateMap();
//        Optional<IndexEntity> indexEntity = accessIndexEntityByName(name);
//        if(indexEntity.isPresent()) {
//            return indexEntity.get().getAbstractFieldList();
//        }else {
//            Optional<IndexViewEntity> indexViewEntity = accessIndexViewEntityByName(name);
//            return indexViewEntity.flatMap(IndexViewEntity::getAbstractFieldList);
//
//        }


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
        Optional<IndexService> service = getIndexServiceByFullName(fullName);
        return service.map(IndexService::getService);
    }

    public Optional<IndexService> getIndexServiceByFullName(@NotNull String fullName) {
        checkAndUpdateService(fullName);
        return accessIndexServiceByFullName(fullName);
    }

    private Optional<IndexService> accessIndexServiceByFullName(@NotNull String fullName) {
        return Optional.ofNullable(this.indexServiceMap.get(fullName));
    }

    public Optional<org.moqui.idea.plugin.dom.model.Service> getServiceOrInterfaceByFullName(@NotNull String fullName) {
        Optional<IndexService> indexService = getIndexServiceByFullName(fullName);
        if(indexService.isPresent()) {
            return indexService.map(IndexService::getService);
        }else {
            Optional<IndexService> indexInterface = getIndexInterfaceByFullName(fullName);
            return indexInterface.map(IndexService::getService);
        }
    }

//    public Optional<Set<String>> searchServicePackageNameSet(@Nullable String filterStr){
//        checkAndUpdateMap();
//        if(filterStr == null) {
//            return accessServicePackageNameSet();
//        }else{
//        return Optional.of(
//                this.indexServiceMap.values().stream().map(IndexService::getPackageName).filter(
//                        packageName -> packageName.contains(filterStr)
//                ).collect(Collectors.toSet()));
//        }
//    }
//    public Optional<Set<String>> getServicePackageNameSet(){
//        checkAndUpdateMap();
//        return accessServicePackageNameSet();
//    }
//    private Optional<Set<String>> accessServicePackageNameSet(){
//        return Optional.of
//                (this.indexServiceMap.values().stream().map(IndexService::getPackageName).collect(Collectors.toSet())
//                );
//    }

//    public Optional<Set<String>> getServiceClassNameSet(){
//        checkAndUpdateMap();
//        return accessServiceClassNameSet();
//    }
//    private Optional<Set<String>> accessServiceClassNameSet(){
//        return Optional.of
//                (this.indexServiceMap.values().stream().map(IndexService::getClassName).collect(Collectors.toSet())
//                );
//    }

//    public Optional<Set<String>> searchServiceClassNameSet(@Nullable String filterStr){
//        checkAndUpdateMap();
//        if(filterStr == null) {
//            return accessServiceClassNameSet();
//        }else{
//            return Optional.of(
//                    this.indexServiceMap.values().stream().map(IndexService::getClassName).filter(
//                            className -> className.contains(filterStr)
//                    ).collect(Collectors.toSet()));
//        }
//    }
//    public Optional<Set<String>> searchInterfaceAndServiceFullNameSet(@Nullable String filterStr){
//        checkAndUpdateMap();
//        Set<String> all = accessInterfaceAndServiceFullNameSet().orElse(new HashSet<>());
//        if(all.isEmpty()) return Optional.empty();
//
//        if(filterStr == null) {
//            return Optional.of(all);
//        }else{
//            return Optional.of(
//                    all.stream().filter(
//                            fullName -> fullName.contains(filterStr)
//                    ).collect(Collectors.toSet()));
//        }
//    }

//    public Optional<Set<String>> getInterfaceAndServiceFullNameSet(){
//        checkAndUpdateMap();
//        return accessInterfaceAndServiceFullNameSet();
//    }
//    public Optional<Set<String>> accessInterfaceAndServiceFullNameSet(){
//        Set<String> result = new HashSet<>();
//        result.addAll(this.indexServiceMap.keySet());
//        result.addAll(this.indexInterfaceMap.keySet());
//
//        return Optional.of(result);
//    }

//    public Optional<Set<String>> searchServiceFullNameSet(@Nullable String filterStr){
//        checkAndUpdateMap();
//        Set<String> all = accessServiceFullNameSet().orElse(new HashSet<>());
//        if(all.isEmpty()) return Optional.empty();
//
//        if(filterStr == null) {
//            return Optional.of(all);
//        }else{
//            return Optional.of(
//                    all.stream().filter(
//                            fullName -> fullName.contains(filterStr)
//                    ).collect(Collectors.toSet()));
//        }
//    }
//    public Optional<Set<String>> getInterfaceFullNameSet(){
//        checkAndUpdateMap();
//        return accessInterfaceFullNameSet();
//    }
//    private Optional<Set<String>> accessInterfaceFullNameSet(){
//        return Optional.of(this.indexInterfaceMap.keySet());
//    }

//    public Optional<Set<String>> getServiceFullNameSet(){
//        checkAndUpdateMap();
//        return accessServiceFullNameSet();
//    }
//
//    private Optional<Set<String>> accessServiceFullNameSet(){
//        return Optional.of(this.indexServiceMap.keySet());
//    }

    public List<ViewEntity> getPendingViewEntityList(){return pendingViewEntityList;}

    public void setEntityXmlFileLastUpdatedStamp(long entityXmlFileLastUpdatedStamp) {
        //将所有的缓存删除，简单控制保障内存泄漏
        synchronized (MUTEX_ENTITY) {
            this.indexEntityMap.clear();
        }
        synchronized (MUTEX_VIEW) {
            this.indexViewEntityMap.clear();
        }

        this.entityXmlFileLastUpdatedStamp = entityXmlFileLastUpdatedStamp;
    }

    public void setServiceXmlFileLastUpdatedStamp(long serviceXmlFileLastUpdatedStamp) {
        //将所有的缓存删除，简单控制保障内存泄漏
        synchronized (MUTEX_SERVICE) {
            this.indexServiceMap.clear();
        }
        synchronized (MUTEX_INTERFACE) {
            this.indexInterfaceMap.clear();
        }

        this.serviceXmlFileLastUpdatedStamp = serviceXmlFileLastUpdatedStamp;
    }

}
