package org.moqui.idea.plugin.util;

import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import icons.MoquiIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.service.ServicePsiElementService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.Set;

import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByConvertContext;
import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByPsiElement;
import static org.moqui.idea.plugin.util.MyStringUtils.isEmpty;
import static org.moqui.idea.plugin.util.MyStringUtils.isNotEmpty;


public final class ServiceUtils {
    public static final String SERVICE_NAME_DELIMITER = "#";
    public static final String SERVICE_NAME_DOT = ".";
    public static final String SERVICE_INTERFACE = "interface";

    public static final String SERVICE_AUTO_PARAMETERS_INCLUDE_NONPK = "nonpk";
    public static final String SERVICE_AUTO_PARAMETERS_INCLUDE_PK = "pk";

    private ServiceUtils() {
        throw new UnsupportedOperationException();
    }
    public static final class ServiceDescriptor{
        ServiceDescriptor(){}
        ServiceDescriptor(String className,String verb, String noun){
            this.className = className;
            this.verb = verb;
            this.noun = noun;
        }
        /**
         * 从字符串中获取服务的信息,标准格式为：
         * className.verb#name
         * 如果是CRUD服务，格式为：
         * verb#entityName
         * 针对crud服务，返回的className为空
         * @param fullName
         * @return
         */
        ServiceDescriptor(@NotNull String fullName){
            final int index = fullName.lastIndexOf(SERVICE_NAME_DELIMITER);
            if(index < 0) return ;

            noun = fullName.substring(index+1);
            //不是服务，是对entity的CRUD
//        if(noun.indexOf(".")>0) return Optional.empty();
            final String pathVerb = fullName.substring(0,index);

            final int verbIndex = pathVerb.lastIndexOf(SERVICE_NAME_DOT);

            if(verbIndex >= 0) {
                verb = pathVerb.substring(verbIndex+1);
                className = pathVerb.substring(0,verbIndex);

            }else {
                verb = pathVerb;
                className = "";
            }


        }
        public String className = MyStringUtils.EMPTY_STRING;
        public String verb = MyStringUtils.EMPTY_STRING;
        public String noun  = MyStringUtils.EMPTY_STRING;

        public Optional<String> getServiceCallName(){
            if(isEmpty(verb) || isEmpty(noun)) return Optional.empty();
            String str = verb +SERVICE_NAME_DELIMITER + noun;
            if(isNotEmpty(className)) str = className +SERVICE_NAME_DOT + str;
            return Optional.of(str);
        }
    }


    public static boolean isServicesFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Services.TAG_NAME);
    }


    /**
     *判断一个Service是否为interface
     *
     * @param service
     * @return
     */
    public static boolean isInterface(@NotNull Service service){
        if(!service.isValid()) return false;

        if(service.getType().getXmlAttributeValue() == null) return false;

        final String type = MyDomUtils.getXmlAttributeValueString(service.getType().getXmlAttributeValue())
                .orElse(MyStringUtils.EMPTY_STRING);

        return Service.ATTR_TYPE.equals(type);
    }
    public static boolean isNotInterface(@Nullable Service service) {
        return !isInterface(service);
    }
    public static boolean isService(@Nullable Service service){
        return !isInterface(service);
    }
    public static boolean isNotService(@Nullable Service service){
        return !isService(service);
    }

    /**
     * 判断一个字符串是否为一个有效的ServiceCall字符串
     * @param project
     * @param auditStr
     * @return
     */
    public static boolean isValidServiceCallStr(@NotNull Project project,@NotNull String auditStr){

        int slashIndex = auditStr.indexOf('#');
        String[] slashSplit = auditStr.split("#");

        if(slashIndex >= 0) {
            //#存在，需要进行进一步判断
            int pointIndex = slashSplit[0].lastIndexOf('.');
            if(pointIndex<0) {
                //标准操作,CRUD
                //操作不存在，则返回false
                if(! ServiceUtils.STANDARD_CRUD_COMMANDER.contains(slashSplit[0])) return false;
                //操作存在，则验证entityName是否有效
                if(slashSplit.length==1) {
                    return false;
                }else {

                    return EntityUtils.findEntityByName(project,slashSplit[1]).map(item->true).orElse(false);
                }

            }else{
                //Service Call
                return findServiceByFullName(project,auditStr).map(item -> true).orElse(false);
            }
        }else {
            return false;
        }
    }
//    /**
//     * 根据实体名和包名找到对应的实体定义的XmlElement
//     * @param project
//     * @param fullName 调用的服务名称，格式类似 moqui.work.TicketServices.get#ServiceStationByServiceLocation
//     * @return
//     */
//    public static Optional<XmlElement[]> findServiceByFullName(@NotNull Project project, @NotNull String fullName){
//        Optional<ServiceDescriptor> serviceDescriptor = extractServiceDescriptor(fullName);
//        if (serviceDescriptor.isEmpty()) return Optional.empty();
//
//        List<DomFileElement<Services>> fileElementList  = DomUtils.findDomFileElementsByRootClass(project, Services.class);
//        for(DomFileElement<Services> fileElement : fileElementList) {
//            //判断服务在不在这个文件中
//            Optional<String> className = extractClassNameFromPath(fileElement.getFile().getVirtualFile().getPath());
//            if(className.isEmpty()) continue;
//            if(!className.get().equals(serviceDescriptor.get().className)) continue;
//
//            for(Service service: fileElement.getRootElement().getServices()) {
//                if(service.getVerb().getValue().equals(serviceDescriptor.get().verb)
//                        && service.getNoun().getValue().equals(serviceDescriptor.get().noun)) {
//
//                    XmlElement[] result = {service.getXmlElement()};
//                    return Optional.of(result);
//                }
//            };
//        }
//        return Optional.empty();
//    }
    /**
     * 根据服务的全称找到对应的服务定义的Service
     * @param project
     * @param fullName 调用的服务名称，格式类似 moqui.work.TicketServices.get#ServiceStationByServiceLocation
     * @return
     */
    public static Optional<Service> findServiceByFullName(@NotNull Project project, @NotNull String fullName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getServiceByFullName(fullName);

//        ServiceDescriptor serviceDescriptor = new ServiceDescriptor(fullName);
//
//        //如果是标准crud，则返回空
//        if(serviceDescriptor.className.isEmpty()) return Optional.empty();
//
//        List<DomFileElement<Services>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Services.class);
//        for(DomFileElement<Services> fileElement : fileElementList) {
//            //判断服务在不在这个文件中
//            Optional<String> className = extractClassNameFromPath(fileElement.getFile().getVirtualFile().getPath());
//            if(className.isEmpty()) continue;
//            if(!className.get().equals(serviceDescriptor.className)) continue;
//
//            for(Service service:fileElement.getRootElement().getServiceList()) {
//                if(service.getVerb().getValue().equals(serviceDescriptor.verb)
//                        && service.getNoun().getValue().equals(serviceDescriptor.noun)) {
//
//                    return Optional.of(service);
//                }
//            };
//        }
//        return Optional.empty();
    }
    /**
     *path 格式是：../runtime/component/{componentName}/service/...
     * 后面的路径对应类名称
     * @param path
     * @return
     */
    public static Optional<String> extractClassNameFromPath(@NotNull String path){
        String pathSeparator = "/";

        if(!path.contains(pathSeparator)){
            pathSeparator = "\\\\";
        }

        //检查component和framework的servcie
        final String componentStr = pathSeparator + "runtime" + pathSeparator+ "component"+ pathSeparator ;
        final String frameworkStr = pathSeparator + "framework" + pathSeparator + "service" + pathSeparator;
        final String baseComponentStr = pathSeparator + "runtime" + pathSeparator+ "base-component"+ pathSeparator ;

        String splitStr;
        if(path.contains(componentStr)) {
            splitStr = pathSeparator + "runtime" + pathSeparator+ "component";
        }else if(path.contains(frameworkStr)) {
            splitStr = pathSeparator + "framework";
        }else if(path.contains(baseComponentStr)) {
            splitStr = pathSeparator + "runtime" + pathSeparator+ "base-component";
        }else {
            return Optional.empty();
        }

        String classStr;
        final String[] pathArr =  path.split(splitStr);
        if(pathArr.length != 2) {
            return Optional.empty();
        }

        final String serviceStr = pathSeparator + "service" + pathSeparator;

        final String classStr1 = pathArr[1];
        final int index = classStr1.indexOf(serviceStr);
        if (index<0) return Optional.empty();
        classStr = classStr1.substring(index+serviceStr.length());


        classStr = classStr.replace(pathSeparator,".");

        //将文件名后缀.xml删除
        classStr = classStr.substring(0, classStr.length()-".xml".length());

        return Optional.of(classStr);

    }
    public static Optional<String> extractPackageNameFromPath(@NotNull String path){
        Optional<String> optClassName = extractClassNameFromPath(path);
        return optClassName.map(className->{
           int index = className.lastIndexOf(SERVICE_NAME_DOT);
           if(index>0) {
               return className.substring(0,index);
           }else {
               return null;
           }
        });
    }
    /**
     * 返回所有定义好的服务名称和对应位置的关系
     * 服务名称格式为标注格式
     * @param project
     * @return
     */
//    public static Map<String, Service> findAllServiceDomElement(@NotNull Project project) {
//        Map<String, Service> result = new HashMap<String, Service>();
//        Collection<Service> services = findAllService(project);
//
//        services.forEach(service -> {
//            result.put(getFullNameFromService(service), service);
//        }  );
//
//        return result;
//
//    }

    /**
     * 返回所有服务的类名称，即不含标准名称的verb#noun部分
     * @param project
     * @return
     */
    public static @NotNull Set<String> findServiceClassNameSet(@NotNull Project project, @Nullable String filterStr){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.searchServiceClassNameSet(filterStr).orElse(new HashSet<>());

//        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);
//
//        Set<String> classNameSet = new HashSet<String>();
//        fileElementList.forEach(item ->{
//            Optional<String> optClassName =extractClassNameFromPath(item.getFile().getVirtualFile().getPath());
//            if(optClassName.isPresent()) {
//                if(isNotEmpty(filterStr)){
//                    if (optClassName.get().contains(filterStr)) {
//                        classNameSet.add(optClassName.get());
//                    }
//                }else {
//                    classNameSet.add(optClassName.get());
//                }
//            }
//        });
//        return classNameSet;
    }
    /**
     * 返回所有可用于Interface的全名称，不仅仅type为interface的，也包括其他类型的service
     * @param project
     * @return
     */
    public static @NotNull Set<String> findInterfaceFullNameSet(@NotNull Project project, @Nullable String filterStr){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.searchInterfaceAndServiceFullNameSet(filterStr).orElse(new HashSet<>());

//        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);
//        ProgressManager.checkCanceled();
//        Set<String> classNameSet = new HashSet<String>();
//        fileElementList.forEach(item ->{
//            ProgressManager.checkCanceled();
//            for(Service service: item.getRootElement().getServiceList()) {
////                if (isInterface(service)) {
//                    final String fullName = getFullNameFromService(service);
//                    if (isNotEmpty(filterStr)) {
//                        if (fullName.contains(filterStr)) {
//                            classNameSet.add(fullName);
//                        }
//                    } else {
//                        classNameSet.add(fullName);
//                    }
////                }
//            }
//        });
//        return classNameSet;
    }

    /**
     * 根据类名返回这个类所有的完整服务名
     * @param project
     * @param className
     * @return
     */
    public static @NotNull Set<String> getServiceFullNameInClass(@NotNull Project project, @NotNull  String className) {
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.searchServiceFullNameSet(className).orElse(new HashSet<>());

//        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);
//
//        Set<String> serviceNameSet = new HashSet<String>();
//        fileElementList.forEach(item ->{
//            Optional<String> optClassName =extractClassNameFromPath(item.getFile().getVirtualFile().getPath());
//            if(optClassName.isPresent()) {
//                if(className.equals(optClassName.get())){
//                    for(Service service: item.getRootElement().getServiceList()) {
//                        if(isService(service)) {
//                            ServiceDescriptor serviceDescriptor = new ServiceDescriptor();
//                            serviceDescriptor.className = optClassName.get();
//                            serviceDescriptor.verb = service.getVerb().getValue();
//                            serviceDescriptor.noun = service.getNoun().getValue();
//                            serviceNameSet.add(serviceDescriptor.getServiceCallName().get());
//                        }
//                    };
//
//                }
//            }
//        });
//
//        return serviceNameSet;

    }
    /**
     * 找到所有的service，type为interface的除外
     * @param project 当前项目
     * @return Collection<Entity>
     */
//    public static Collection<Service> findAllService(@NotNull Project project){
//
//        Collection<Service> result = new ArrayList<Service>();
//        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);
//        for(DomFileElement<Services> fileElement : fileElementList) {
//            for(Service service: fileElement.getRootElement().getServiceList()) {
//                if(isService(service)){
//                        result.add(service);
//                }
//            };
//        }
//        return result;
//    }
//    public static Collection<Service> findAllInterface(@NotNull Project project){
//        Collection<Service> result = new ArrayList<Service>();
//        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);
//        for(DomFileElement<Services> fileElement : fileElementList) {
//            for(Service service: fileElement.getRootElement().getServiceList()){
//                if(isInterface(service)){
//                    result.add(service);
//                }
//            };
//        }
//        return result;
//    }

    /**
     * 从Service对应的DomElement中拼接出完整名称服务名称
     * 统一的名称格式是：｛classPackage｝.{verb}#{noun}
     */
    public static String getFullNameFromService(@NotNull Service service) {
        ServiceDescriptor descriptor = new ServiceDescriptor();
        descriptor.verb = MyDomUtils.getValueOrEmptyString(service.getVerb());

        descriptor.noun = MyDomUtils.getValueOrEmptyString(service.getNoun());
        XmlElement xmlElement = service.getXmlElement();
        if(xmlElement == null) {
            descriptor.className = MyStringUtils.EMPTY_STRING;
        }else {
            final String path = service.getXmlElement().getContainingFile().getVirtualFile().getPath();
            descriptor.className = extractClassNameFromPath(path).orElse(MyStringUtils.EMPTY_STRING);

        }

        return descriptor.getServiceCallName().orElse(MyStringUtils.EMPTY_STRING);

    }

    /**
     * 从字符串中获取服务的信息,标准格式为：
     * className.verb#name
     * 如果是CRUD服务，格式为：
     * verb#entityName
     * 针对crud服务，返回的className为空
     * @param fullName
     * @return
     */
//    public static Optional<ServiceDescriptor> extractServiceDescriptor(@NotNull String fullName){
//        final int index = fullName.lastIndexOf("#");
//        if(index < 0) return Optional.empty();
//        final String noun = fullName.substring(index+1);
//        //不是服务，是对entity的CRUD
////        if(noun.indexOf(".")>0) return Optional.empty();
//        final String pathVerb = fullName.substring(0,index);
//
//        final int verbIndex = pathVerb.lastIndexOf(".");
//        String verb;
//        String className;
//
//        if(verbIndex >= 0) {
//            verb = pathVerb.substring(verbIndex+1);
//            className = pathVerb.substring(0,verbIndex);
//
//        }else {
//            verb = pathVerb;
//            className = "";
//        }
//
//
//        return Optional.of(new ServiceDescriptor(className,verb,noun));
//
//    }

    public static List<PsiElement> getRelatedService(@NotNull PsiElement psiElement, @NotNull String fullName) {
        List<PsiElement> resultList = new ArrayList<>();

        ServicePsiElementService servicePsiElementService =
                psiElement.getProject().getService(ServicePsiElementService.class);

        DomElement target = servicePsiElementService.getPsiElementByFullName(fullName);
        if (target == null) {
//            CustomNotifier.warn(psiElement.getProject(), "发现找不到的Service，fullName：" + fullName +", 所在文件："
//                    +psiElement.getContainingFile().getVirtualFile().getPath());
        }else {
            resultList.add((PsiElement) target.getXmlElement());
        }
        return resultList;
    }

    public static Icon getNagavitorToServiceIcon() {
        return MoquiIcons.ServiceTag;//MyIcons.NAVIGATE_TO_SERVICE;
    }


    public static String getNagavitorToServiceToolTips() {
        return "Navigating to Service definition";
    }

    /**
     * 用在Inspection中
     * 根据指定的属性来进行Service名称的验证，包括CRUD中的Entity名称
     * 标准CRUD的命令是：update,delete,create
     * @param attributeValue
     * @param holder
     * @param helper
     */
    public static void inspectServiceCallFromAttribute(@NotNull GenericAttributeValue attributeValue, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper) {

        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
        if (xmlAttributeValue == null) { return;}

        final String serviceCallName = attributeValue.getXmlAttributeValue().getValue();
        final int serviceCallNameLength = attributeValue.getXmlAttributeValue().getValueTextRange().getLength();
        final Project project =attributeValue.getXmlElement().getProject();
        ServiceDescriptor serviceDescriptor = new ServiceDescriptor(serviceCallName);
        if(serviceDescriptor.verb.isEmpty()) {
            holder.createProblem(attributeValue, HighlightSeverity.ERROR, "This is not a valid service call");
            return;
        }

//        Optional<String> optEntityName = EntityUtils.getEntityNameFromServiceCallName(serviceCallName);

        if(serviceDescriptor.className.isEmpty() ) {

            if(!(STANDARD_CRUD_COMMANDER.contains(serviceDescriptor.verb))) {
                holder.createProblem(attributeValue, HighlightSeverity.ERROR,
                        "The verb is not correctly,should use one of create/update/delete");

            }

            Optional<XmlElement> optionalXmlElement = EntityUtils.findEntityAndViewEntityXmlElementByFullName(project,
                    serviceDescriptor.noun);
            final int index = serviceCallName.indexOf("#");
            final int entityNameLength = serviceDescriptor.noun.length();


            if (optionalXmlElement.isEmpty()) {
                holder.createProblem(attributeValue, HighlightSeverity.ERROR, "Missing attributeThe called Entity is not found",
                        TextRange.from(index+2,entityNameLength));
            }
        }else {
            Optional<Service> opService = ServiceUtils.findServiceByFullName(project, serviceCallName);

            if (opService.isEmpty()) {
                holder.createProblem(attributeValue, HighlightSeverity.ERROR, "Called service is not found",
                        TextRange.from(1,serviceCallNameLength));
            }
        }

    }

    public static final Set<String> STANDARD_CRUD_COMMANDER =Set.of("create","update","delete","store");
    public static final Set<String> ORDER_BY_COMMANDER =Set.of("+","-","^");
    public static Optional<EntityFind> getCurrentEntityFind(@NotNull ConvertContext context){
        return getLocalDomElementByConvertContext(context,EntityFind.class);

    }
    public static Optional<EntityFindCount> getCurrentEntityFindCount(@NotNull ConvertContext context){
        return getLocalDomElementByConvertContext(context,EntityFindCount.class);

    }
    public static Optional<EntityFindCount> getCurrentEntityFindCount(@NotNull PsiElement psiElement){
        return getLocalDomElementByPsiElement(psiElement,EntityFindCount.class);

    }
    public static Optional<EntityFind> getCurrentEntityFind(@NotNull PsiElement psiElement){
        return getLocalDomElementByPsiElement(psiElement,EntityFind.class);

    }
    public static Optional<EntityFindOne> getCurrentEntityFindOne(ConvertContext context){
        return getLocalDomElementByConvertContext(context,EntityFindOne.class);

    }
    public static Optional<EntityDeleteByCondition> getCurrentEntityDeleteByCondition(@NotNull ConvertContext context){
        return getLocalDomElementByConvertContext(context,EntityDeleteByCondition.class);

    }
    public static Optional<Service> getCurrentService(@NotNull ConvertContext context){
        return getLocalDomElementByConvertContext(context,Service.class);

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
     * @param serviceCallStr
     * @param startOffset 在element中的起始位置
     * @return
     */
    public static PsiReference @NotNull [] createServiceCallReferences(@NotNull Project project, @NotNull PsiElement element, @NotNull String  serviceCallStr, @NotNull int startOffset) {
        ServiceDescriptor serviceDescriptor = new ServiceDescriptor(serviceCallStr);
        if (serviceDescriptor.verb.isEmpty()) return PsiReference.EMPTY_ARRAY;
//        ServiceDescriptor serviceDescriptor = optServiceDescripter.get();

        Optional<Service> optService = findServiceByFullName(project,serviceCallStr);
        if (optService.isEmpty()) return PsiReference.EMPTY_ARRAY;
        Service service = optService.get();

        List<PsiReference> resultList = new ArrayList<>();
        //创建verb的reference
        final int verbIndex = serviceCallStr.indexOf(serviceDescriptor.verb+"#");
        final int verbStartOffset = startOffset+verbIndex;

        resultList.add(new PsiRef(element,
                new TextRange(verbStartOffset, verbStartOffset+ serviceDescriptor.verb.length()),
                service.getVerb().getXmlAttributeValue()));

        //创建侬noun的reference
        final int nounIndex = serviceCallStr.indexOf("#" + serviceDescriptor.noun);
        final int nounStartOffset = startOffset + nounIndex+1;
        resultList.add(new PsiRef(element,
                new TextRange(nounStartOffset, nounStartOffset+ serviceDescriptor.noun.length()),
                service.getNoun().getXmlAttributeValue()));

        //对文件名创建reference
        final int fileNameIndex = serviceDescriptor.className.lastIndexOf(".");
        String fileName;
        if(fileNameIndex<0) {
            fileName = serviceDescriptor.className;
        }else {
            fileName = serviceDescriptor.className.substring(fileNameIndex + 1);
        }
        final int fileNameStartOffset = startOffset + fileNameIndex+1;
        resultList.add(new PsiRef(element,
                new TextRange(fileNameStartOffset, fileNameStartOffset+ fileName.length()),
                service.getXmlTag().getContainingFile()));

        //针对上级目录进行处理
        if(fileNameIndex>0) {
            String path = serviceDescriptor.className.substring(0,fileNameIndex);
            PsiDirectory psiPath = service.getXmlTag().getContainingFile().getContainingDirectory();
            int pathDotIndex;
            int pathStartOffset;
            String tmpPath;
            while(path.length() > 0) {
                pathDotIndex = path.lastIndexOf(".");
                pathStartOffset = startOffset + pathDotIndex + 1;
                if (pathDotIndex<0) {
                    tmpPath = path;
                    path = MyStringUtils.EMPTY_STRING;
                }else {
                    tmpPath = path.substring(pathDotIndex+1);
                    path = path.substring(0,pathDotIndex);
                }
                resultList.add(new PsiRef(element,
                        new TextRange(pathStartOffset,pathStartOffset+tmpPath.length()),
                        psiPath));
                psiPath = psiPath.getParent();
            }
        }
        



        return resultList.toArray(new PsiReference[0]);

    }

}
