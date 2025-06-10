package org.moqui.idea.plugin.service;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.xml.DomFileElement;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.listener.MoquiXmlBulkFileListener;
import org.moqui.idea.plugin.listener.MoquiXmlVirtualFileManager;
import org.moqui.idea.plugin.util.*;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 建立Entity，ViewEntity和Service 等的缓存
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

    private long entityFacadeXmlFileLastUpdatedStamp;

    private long rootSubScreensItemLastUpdatedStamp;

    //最近一次全局service扫描的时间戳
//    private long allServiceLastRefreshStamp;

    private final ConcurrentHashMap<String, IndexEntity> indexEntityMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IndexViewEntity> indexViewEntityMap =new ConcurrentHashMap<>();
//    private Map<String, ExtendEntity> extendEntityMap = new HashMap<>();

    private final ConcurrentHashMap<String, IndexService> indexServiceMap = new ConcurrentHashMap<>();

    //处理service的interface定义
    private final ConcurrentHashMap<String,IndexService> indexInterfaceMap = new ConcurrentHashMap<>();

    //处理EntityFacadeXml中的Template
    private final ConcurrentHashMap<String, XmlTag> indexTextTemplateMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IndexRootSubScreensItem> indexRootSubScreensItemMap = new ConcurrentHashMap<>();

    private static final Object MUTEX_ENTITY = new Object();
    private static final Object MUTEX_VIEW = new Object();
    private static final Object MUTEX_SERVICE = new Object();
    private static final Object MUTEX_INTERFACE = new Object();

    private static final Object MUTEX_TEXT_TEMPLATE = new Object();

    private static final Object MUTEX_ROOT_SUBSCREENS_ITEM = new Object();

    private List<ViewEntity> pendingViewEntityList;
//    private MoquiXmlVirtualFileManager moquiXmlVirtualFileManager = null;

    private final boolean isMoquiProject;

    private final MessageBusConnection connection;

    public MoquiIndexService(Project project) {
        this.project = project;
//        this.entityXmlFileUpdated = false;
//        this.serviceXmlFileUpdated = false;

        this.entityXmlFileLastUpdatedStamp = System.currentTimeMillis();
        this.serviceXmlFileLastUpdatedStamp = System.currentTimeMillis();
        this.entityFacadeXmlFileLastUpdatedStamp = System.currentTimeMillis();
        this.rootSubScreensItemLastUpdatedStamp = System.currentTimeMillis();

//        this.allEntityLastRefreshStamp = 0;
//        this.allServiceLastRefreshStamp = 0;


//        refreshAllEntityMap();
//        refreshAllServiceMap();
        //判断是否为Moqui项目，

        this.isMoquiProject = checkMoquiProject(project);
        this.connection = project.getMessageBus().connect();
        //如果不是Moqui项目，则不进行监听
        if(this.isMoquiProject) {
//            this.moquiXmlVirtualFileManager = new MoquiXmlVirtualFileManager(project);
//            VirtualFileManager.getInstance().addVirtualFileListener(this.moquiXmlVirtualFileManager);
            this.connection.subscribe(VirtualFileManager.VFS_CHANGES,MoquiXmlBulkFileListener.of(project));
        }

    }

    private boolean checkMoquiProject(@NotNull Project project){

        String baseDir = project.getBasePath();
        if(baseDir == null) return false;

        VirtualFile specificFile = ReadAction.compute(()-> LocalFileSystem.getInstance().findFileByNioFile(Path.of(baseDir,"MoquiInit.properties")));
        return specificFile != null;

    }


    public void unRegisterListener(){
        this.connection.disconnect();
//        if(isMoquiProject) {
//            VirtualFileManager.getInstance().removeVirtualFileListener(this.moquiXmlVirtualFileManager);
//        }
    }

    public boolean isMoquiProject() {
        return isMoquiProject;
    }

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
        IndexViewEntity indexViewEntity = new IndexViewEntity(viewEntity);
        indexViewEntity.setAbstractIndexEntityMap(aliasEntityMap);

        refreshIndexViewEntityFieldMap(indexViewEntity);

        indexViewEntity.setLastRefreshStamp(System.currentTimeMillis());

        this.indexViewEntityMap.put(indexViewEntity.getFullName(), indexViewEntity);
        return true;

    }

    /**
     * 获取指定ViewEntity的所有字段
     * @param indexViewEntity 当前的ViewEntity
     */
    private void refreshIndexViewEntityFieldMap(@NotNull IndexViewEntity indexViewEntity) {
        Map<String, IndexAbstractField> result = new HashMap<>();
        List<IndexAbstractField> fieldList = new ArrayList<>();
        //添加alias
        for(Alias alias: indexViewEntity.getViewEntity().getAliasList()) {
            String aliasName = MyDomUtils.getValueOrEmptyString(alias.getEntityAlias());
            //如果是计算字段，则aliasName为空，这时，将当前的viewEntity作为fromAbstractIndexEntity
            if(MyStringUtils.isEmpty(aliasName)) {
                fieldList.add(IndexAbstractField.of(indexViewEntity, alias));
            }else {
                //根据alias在abstractIndexEntityMap中查找对应AbstractIndexEntity
                AbstractIndexEntity abstractIndexEntity = indexViewEntity.getAbstractIndexEntityByAlias(aliasName).orElse(null);
                if (abstractIndexEntity == null) continue;
                fieldList.add(IndexAbstractField.of(abstractIndexEntity, alias));
            }
        }
//        List<IndexAbstractField> fieldList = new LinkedList<>(viewEntity.getAliasList().stream().map(IndexAbstractField::of).toList());
                //new LinkedList<>(viewEntity.getAliasList());
        //对AliasAll进行处理
        List<AliasAll> aliasAllList = indexViewEntity.getViewEntity().getAliasAllList();
        for(AliasAll aliasAll : aliasAllList) {
            String aliasName = MyDomUtils.getValueOrEmptyString(aliasAll.getEntityAlias());
            if(MyStringUtils.isEmpty(aliasName)) continue;

//            List<AbstractField> aliasAllFieldList = new ArrayList<>();

            //根据alias在abstractIndexEntityMap中查找对应AbstractIndexEntity
            AbstractIndexEntity abstractIndexEntity = indexViewEntity.getAbstractIndexEntityByAlias(aliasName).orElse(null);
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

        indexViewEntity.setIndexAbstractFieldMap(result);

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
            MemberEntity memberEntity = EntityUtils.getMemberEntityByAlias(viewEntity, joinFromAlias).orElse(null);
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

    private void addServiceToIndexServiceMap(@NotNull org.moqui.idea.plugin.dom.model.Service service){
        IndexService indexService = new IndexService(service);

        processServiceParameter(indexService,service);

        indexService.setLastRefreshStamp(System.currentTimeMillis());

        indexServiceMap.put(indexService.getFullName(), indexService);
    }
    private void addServiceIncludeToIndexServiceMap(@NotNull ServiceInclude serviceInclude){
        org.moqui.idea.plugin.dom.model.Service service = ServiceUtils.getServiceByServiceInclude(this.project,serviceInclude).orElse(null);
        if(service == null) return;

        IndexService indexService = new IndexService(serviceInclude,service);

        processServiceParameter(indexService,service);

        indexService.setLastRefreshStamp(System.currentTimeMillis());

        indexServiceMap.put(indexService.getFullName(), indexService);

    }
    private void processServiceParameter(@NotNull IndexService indexService, @NotNull org.moqui.idea.plugin.dom.model.Service service){
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

    }
    private void addInterfaceToIndexInterfaceMap(@NotNull org.moqui.idea.plugin.dom.model.Service service){
        IndexService indexService = new IndexService(service);
        //处理interface

        Map<String, IndexServiceParameter> inParameterMap = new HashMap<>(extractServiceInParameterFieldMap(service));
        Map<String, IndexServiceParameter> outParameterMap = new HashMap<>(extractServiceOutParameterFieldMap(service));
        indexService.setInParameterMap(inParameterMap);
        indexService.setOutParameterMap(outParameterMap);

        indexService.setLastRefreshStamp(System.currentTimeMillis());

        indexInterfaceMap.put(indexService.getFullName(), indexService);


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
            fieldList = entity.get().getFieldList();
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
//            if(indexEntity != null && indexEntity.getLastRefreshStamp()>= this.entityXmlFileLastUpdatedStamp ) return;

            Entity entity = EntityUtils.getEntityByNameFromFile(this.project, entityName).orElse(null);
            if(entity==null) {
                removeIndexEntityByName(entityName);
                return;
            }

            //传入的entityName有可能是shortAlias,而ExtendEntity定义中不一定有shortAlias
            List<ExtendEntity> extendEntityList =EntityUtils.getExtendEntityListByNameFromFile(this.project,MyDomUtils.getValueOrEmptyString(entity.getEntityName()));
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
//            IndexViewEntity indexViewEntity = (IndexViewEntity)accessIndexViewEntityByName(viewName).orElse(null);
//            if(indexViewEntity != null && indexViewEntity.getLastRefreshStamp()>= this.entityXmlFileLastUpdatedStamp ) return;

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
//            IndexService indexService = accessIndexServiceByFullName(fullName).orElse(null);
//            if(indexService != null && indexService.getLastRefreshStamp()>= this.serviceXmlFileLastUpdatedStamp ) return;

            org.moqui.idea.plugin.dom.model.Service service = ServiceUtils.getServiceByFullNameFromFile(this.project, fullName).orElse(null);
            if(service==null) {
                //考虑是不是ServiceInclude
                ServiceInclude serviceInclude = ServiceUtils.getServiceIncludeByFullNameFromFile(this.project,fullName).orElse(null);
                if(serviceInclude == null) {
                    //从IndexServiceMap中删除
                    removeIndexServiceByName(fullName);

                }else {
                    addServiceIncludeToIndexServiceMap(serviceInclude);
                }
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
//            IndexService indexInterface = accessIndexInterfaceByFullName(fullName).orElse(null);
//            if(indexInterface != null && indexInterface.getLastRefreshStamp()>= this.serviceXmlFileLastUpdatedStamp ) return;

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

    private Optional<Entity> accessEntityByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        return accessIndexEntityByName(name).map(IndexEntity::getEntity);
    }


    /**
     * 根据Entity的名称获取对应的DomElement，名称可以是fullName，shortAlias或entityName
     * @param name
     * @return
     */
    public Optional<Entity> getEntityByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        return getIndexEntityByName(name).map(IndexEntity::getEntity);
//        checkAndUpdateEntity(name);
//        return accessEntityByName(name);

    }
    private Optional<IndexEntity> accessIndexEntityByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        for(String key: this.indexEntityMap.keySet()) {
            IndexEntity indexEntity = this.indexEntityMap.get(key);
            if(indexEntity.isThisEntity(name)) return Optional.of(indexEntity);
        }
        return Optional.empty();

    }

    public Optional<IndexEntity> getIndexEntityByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        checkAndUpdateEntity(name);
        return  accessIndexEntityByName(name);


    }
    public Optional<AbstractIndexEntity> getIndexEntityOrIndexViewEntity(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        IndexEntity indexEntity  = getIndexEntityByName(name).orElse(null);
        if(indexEntity != null){
            return Optional.of(indexEntity);
        }else {
            return Optional.ofNullable(getIndexViewEntityByName(name).orElse(null));
        }


    }
    private Optional<ViewEntity> accessViewEntityByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        return accessIndexViewEntityByName(name).map(IndexViewEntity::getViewEntity);

    }


    private Optional<IndexViewEntity> accessIndexViewEntityByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        for(String key: this.indexViewEntityMap.keySet()) {
            if(this.indexViewEntityMap.get(key).isThisEntity(name)) return Optional.of(this.indexViewEntityMap.get(key));
        }
        return Optional.empty();
    }
    public Optional<IndexViewEntity> getIndexViewEntityByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        checkAndUpdateView(name);
        return accessIndexViewEntityByName(name);

    }



    private Optional<AbstractIndexEntity> accessIndexEntityOrIndexViewEntity(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        Optional<IndexEntity> indexEntity = accessIndexEntityByName(name);
        if(indexEntity.isPresent()){
            return Optional.of(indexEntity.get());
        }else {
            Optional<IndexViewEntity> indexViewEntity = accessIndexViewEntityByName(name);
            return Optional.ofNullable(indexViewEntity.orElse(null));
        }

    }
    private Optional<IndexService> accessIndexServiceOrIndexInterface(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        Optional<IndexService> optIndexService = accessIndexServiceByName(name);

        return optIndexService.or(() -> accessIndexInterfaceByFullName(name));

    }
    public Optional<AbstractEntity> getEntityOrViewEntity(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();
        return getIndexEntityOrIndexViewEntity(name).flatMap(AbstractIndexEntity::getAbstractEntity);
    }



    public @NotNull List<IndexAbstractField> getEntityOrViewEntityFieldList(@NotNull String name){
        Optional<AbstractIndexEntity> optionalAbstractIndexEntity = getIndexEntityOrIndexViewEntity(name);
        return optionalAbstractIndexEntity.map(AbstractIndexEntity::getIndexAbstractFieldList).orElse(new ArrayList<>());

    }
    public List<Field> getEntityFieldList(@NotNull String name){
        Optional<IndexEntity> indexEntity = getIndexEntityByName(name);
        return indexEntity.map(IndexEntity::getFieldList).orElseGet(ArrayList::new);
    }

    public @NotNull List<ExtendEntity> getExtendEntityListByEntityName(@NotNull String name){
        Optional<IndexEntity> indexEntity = getIndexEntityByName(name);
        return indexEntity.map(IndexEntity::getExtendEntityList).orElse(new ArrayList<>());
    }
    public @NotNull List<Relationship> getRelationshipListByEntityName(@NotNull String name){
        Optional<IndexEntity> indexEntity = getIndexEntityByName(name);
        return indexEntity.map(IndexEntity::getRelationshipList).orElse(new ArrayList<>());
    }

    /**
     * 如果当前的indexTextTemplateMap为空，则重新刷新数据
     *
     */
    public Map<String,XmlTag> getTextTemplateMap(){
        if(this.indexTextTemplateMap.isEmpty()){
            refreshTextTemplate();
        }
        return new HashMap<>(this.indexTextTemplateMap);
    }
    private void refreshTextTemplate(){
        synchronized (MUTEX_TEXT_TEMPLATE) {
            this.indexTextTemplateMap.clear();
//            List<DomFileElement<EntityFacadeXml>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,EntityFacadeXml.class);
            List<PsiFile> fileList= EntityFacadeXmlUtils.getAllEntityFacadeXmlFileList(project);
            if(fileList.size()> 0 ) {

                fileList.forEach(file->{
                    Map<String,XmlTag> fileTemplate =  EntityFacadeXmlUtils.getTextTemplateFromFile(file);
                    this.indexTextTemplateMap.putAll(fileTemplate);
                });

            }
        }
    }
    /**
     * 如果当前的indexRootSubScreensItemMap为空，则重新刷新数据
     *
     */
    public Map<String,IndexRootSubScreensItem> getRootSubScreensItemMap(){
        if(this.indexRootSubScreensItemMap.isEmpty()){
            refreshRootSubScreensItem();
        }
        return new HashMap<>(this.indexRootSubScreensItemMap);
    }
    private void refreshRootSubScreensItem(){
        synchronized (MUTEX_ROOT_SUBSCREENS_ITEM) {
            this.indexRootSubScreensItemMap.clear();
            List<DomFileElement<MoquiConf>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,MoquiConf.class);
            fileElementList.forEach(
                item->{
                    MoquiConf moquiConf = item.getRootElement();
                    moquiConf.getScreenFacade().getScreenList().forEach(
                            screen ->{
                                screen.getSubScreensItemList().forEach(
                                        subScreensItem ->{
                                            IndexRootSubScreensItem rootSubScreensItem = new IndexRootSubScreensItem(subScreensItem);
                                            this.indexRootSubScreensItemMap.put(rootSubScreensItem.getName(),rootSubScreensItem);
                                        }
                                );
                            }
                    );
                }
            );
        }
    }
    public Optional<IndexRootSubScreensItem> getIndexRootSubScreensItemByName(@NotNull String name) {
        if(MyStringUtils.isEmpty(name)) return Optional.empty();

        return Optional.ofNullable(getRootSubScreensItemMap().get(name));
    }

    public Map<String,IndexEntity> getIndexEntityMap(){return new HashMap<>(this.indexEntityMap);}
    public Map<String,IndexViewEntity> getIndexViewEntityMap(){return new HashMap<>(this.indexViewEntityMap);}

    public Map<String,IndexService> getIndexServiceMap(){return new HashMap<>(this.indexServiceMap);}

    public Optional<org.moqui.idea.plugin.dom.model.Service> getServiceByFullName(@NotNull String fullName) {
        Optional<IndexService> service = getIndexServiceByFullName(fullName);
        return service.map(IndexService::getService);
    }

    public Optional<IndexService> getIndexServiceByFullName(@NotNull String fullName) {
        checkAndUpdateService(fullName);
        return accessIndexServiceByFullName(fullName);
    }
    public Optional<IndexService> getIndexServiceOrInterfaceByFullName(@NotNull String fullName) {
        Optional<IndexService> indexService = getIndexServiceByFullName(fullName);
        if(indexService.isPresent()) {
            return indexService;
        }else {
            return getIndexInterfaceByFullName(fullName);
        }
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

    public void setEntityFacadeXmlFileLastUpdatedStamp(long lastUpdatedStamp) {
        //将所有的缓存删除，简单控制保障内存泄漏
        synchronized (MUTEX_TEXT_TEMPLATE) {
            this.indexTextTemplateMap.clear();
        }

        this.entityFacadeXmlFileLastUpdatedStamp = lastUpdatedStamp;
    }

    public void setRootSubScreensItemXmlFileLastUpdatedStamp(long lastUpdatedStamp) {
        //将所有的缓存删除，简单控制保障内存泄漏
        synchronized (MUTEX_ROOT_SUBSCREENS_ITEM) {
            this.indexRootSubScreensItemMap.clear();
        }

        this.rootSubScreensItemLastUpdatedStamp = lastUpdatedStamp;
    }

}
