package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.reference.ServiceCallReference;
import org.moqui.idea.plugin.reference.ServiceInParameterReference;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.service.MoquiIndexService;

import javax.swing.*;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByConvertContext;
import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByPsiElement;
import static org.moqui.idea.plugin.util.MyStringUtils.isNotEmpty;


public final class ServiceUtils {
    public static final String SERVICE_NAME_HASH = "#";
    public static final String SERVICE_NAME_DOT = ".";
    public static final String SERVICE_INTERFACE = "interface";

    public static final String SERVICE_AUTO_PARAMETERS_INCLUDE_NONPK = "nonpk";
    public static final String SERVICE_AUTO_PARAMETERS_INCLUDE_PK = "pk";

    private ServiceUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isServicesFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Services.TAG_NAME,Services.ATTR_NoNamespaceSchemaLocation,Services.VALUE_NoNamespaceSchemaLocation);
    }


    /**
     *判断一个Service是否为interface
     *
     * @param service 待判断的Service
     * @return boolean
     */
    public static boolean isInterface(@NotNull Service service){
        if(!service.isValid()) return false;
        String type = MyDomUtils.getValueOrEmptyString(service.getType());

        return SERVICE_INTERFACE.equals(type);
    }
    public static boolean isNotInterface(@Nullable Service service) {
        return service != null && !isInterface(service);
    }
    public static boolean isService(@Nullable Service service){
        return isNotInterface(service);
    }
    public static boolean isNotService(@Nullable Service service){
        return !isService(service);
    }


public static Optional<Service> getServiceOrInterfaceByFullName(@NotNull Project project, @NotNull String fullName) {
    MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
    return moquiIndexService.getServiceOrInterfaceByFullName(fullName);
}

    /**
     * 根据服务的全称找到对应的服务定义的Service
     * @param project 当前Project
     * @param fullName 调用的服务名称，格式类似 moqui.work.TicketServices.get#ServiceStationByServiceLocation
     * @return Optional<Service>
     */
    public static Optional<Service> getServiceByFullName(@NotNull Project project, @NotNull String fullName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getServiceByFullName(fullName);

    }
    public static Optional<Service> getServiceByFullNameFromFile(@NotNull Project project, @NotNull String fullName){

        ServiceCallDescriptor serviceDescriptor = ServiceCallDescriptor.of(fullName);

        //如果是标准crud，则返回空
        if(serviceDescriptor.getClassName().isEmpty()) return Optional.empty();

        List<DomFileElement<Services>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Services.class);
        for(DomFileElement<Services> fileElement : fileElementList) {
            //判断服务在不在这个文件中
            Optional<String> className = extractClassNameFromPath(fileElement.getFile().getVirtualFile().getPath());
            if(className.isEmpty()) continue;
            if(!className.get().equals(serviceDescriptor.getClassName())) continue;

            for(Service service:fileElement.getRootElement().getServiceList()) {
                if(MyDomUtils.getValueOrEmptyString(service.getVerb()).equals(serviceDescriptor.getVerb())
                        && MyDomUtils.getValueOrEmptyString(service.getNoun()).equals(serviceDescriptor.getNoun())) {

                    return Optional.of(service);
                }
            }
        }
        return Optional.empty();
    }
    /**
     *path 格式是：../runtime/component/{componentName}/service/...
     * 后面的路径对应类名称
     * @param path 路径字符串
     * @return Optional<String>
     */
    public static Optional<String> extractClassNameFromPath(@NotNull String path){
        String pathSeparator = "/"; //idea的virtual file 的path都统一“/”

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
     * 返回所有服务的类名称，即包含标准名称的verb#noun部分
     * @param project 当前项目
     * @return
     */
    public static @NotNull Set<String> getServiceClassNameSet(@NotNull Project project, @Nullable String filterStr){


        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);

        Set<String> classNameSet = new HashSet<>();
        fileElementList.forEach(item ->{
            Optional<String> optClassName =extractClassNameFromPath(item.getFile().getVirtualFile().getPath());
            if(optClassName.isPresent()) {
                if(isNotEmpty(filterStr)){
                    if (optClassName.get().contains(filterStr)) {
                        classNameSet.add(optClassName.get());
                    }
                }else {
                    classNameSet.add(optClassName.get());
                }
            }
        });
        return classNameSet;
    }
    /**
     * 返回所有可用于Interface的全名称，不仅仅type为interface的，也包括其他类型的service
     * @param project 当前Project
     * @return Set<String>
     */
    public static @NotNull Set<String> getInterfaceFullNameSet(@NotNull Project project, @Nullable String filterStr){


        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);
        Set<String> classNameSet = new HashSet<>();
        fileElementList.forEach(item ->{

            for(Service service: item.getRootElement().getServiceList()) {
                if (isInterface(service)) {
                    final String fullName = getFullNameFromService(service);
                    if (isNotEmpty(filterStr)) {
                        if (fullName.contains(filterStr)) {
                            classNameSet.add(fullName);
                        }
                    } else {
                        classNameSet.add(fullName);
                    }
                }
            }
        });
        return classNameSet;
    }

    /**
     * 根据类名返回这个类所有的完整服务名
     * @param project 当前Project
     * @param className 类名
     * @return Set<String>
     */
    public static @NotNull Set<String> getServiceFullNameAction(@NotNull Project project, @NotNull  String className) {

        return getServiceAction(project,className).stream()
                .map(item-> className+ SERVICE_NAME_DOT+ item)
                .collect(Collectors.toSet());

    }

    /**
     * 根据className获取这个className下所有service的verb#noun列表
     * @param project
     * @param className
     * @return
     */
    public static @NotNull Set<String> getServiceAction(@NotNull Project project, @NotNull  String className) {

        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);

        Set<String> serviceNameSet = new HashSet<String>();
        fileElementList.forEach(item ->{
            Optional<String> optClassName =extractClassNameFromPath(item.getFile().getVirtualFile().getPath());
            if(optClassName.isPresent()) {
                if(className.equals(optClassName.get())){
                    for(Service service: item.getRootElement().getServiceList()) {
                        if(isService(service)) {
                            serviceNameSet.add(
                                    MyDomUtils.getValueOrEmptyString(service.getNoun()).isEmpty() ?
                                            MyDomUtils.getValueOrEmptyString(service.getVerb())
                                        :
                                            MyDomUtils.getValueOrEmptyString(service.getVerb())
                                                + SERVICE_NAME_HASH
                                            + MyDomUtils.getValueOrEmptyString(service.getNoun())
                            );
                        }
                    };

                }
            }
        });

        return serviceNameSet;

    }

    /**
     * 从Service对应的DomElement中拼接出完整名称服务名称
     * 统一的名称格式是：｛classPackage｝.{verb}#{noun}
     */
    public static String getFullNameFromService(@NotNull Service service) {
        ServiceCallDescriptor descriptor = new ServiceCallDescriptor();
        descriptor.setVerb(MyDomUtils.getValueOrEmptyString(service.getVerb()));

        descriptor.setNoun(MyDomUtils.getValueOrEmptyString(service.getNoun()));
        XmlElement xmlElement = service.getXmlElement();
        if(xmlElement != null) {
            final String path = service.getXmlElement().getContainingFile().getVirtualFile().getPath();
            descriptor.setClassName(extractClassNameFromPath(path).orElse(MyStringUtils.EMPTY_STRING));
        }

        return descriptor.getServiceCallString();

    }


    public static List<PsiElement> getRelatedService(@NotNull PsiElement psiElement, @NotNull String fullName) {
        List<PsiElement> resultList = new ArrayList<>();

//        ServicePsiElementService servicePsiElementService =
//                psiElement.getProject().getService(ServicePsiElementService.class);
//
//        DomElement target = servicePsiElementService.getPsiElementByFullName(fullName);
//        if (target == null) {
////            CustomNotifier.warn(psiElement.getProject(), "发现找不到的Service，fullName：" + fullName +", 所在文件："
////                    +psiElement.getContainingFile().getVirtualFile().getPath());
//        }else {
//            resultList.add((PsiElement) target.getXmlElement());
//        }
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
     */
    public static void inspectServiceCallFromAttribute(@NotNull GenericAttributeValue<String> attributeValue, @NotNull DomElementAnnotationHolder holder){

        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
        if (xmlAttributeValue == null) { return;}

        final String serviceCallName = attributeValue.getXmlAttributeValue().getValue();
        final int serviceCallNameLength = attributeValue.getXmlAttributeValue().getValueTextRange().getLength();
        final Project project =attributeValue.getXmlElement().getProject();
        ServiceCallDescriptor serviceDescriptor = ServiceCallDescriptor.of(serviceCallName);
        if(serviceDescriptor.getVerb().isEmpty()) {
            holder.createProblem(attributeValue, HighlightSeverity.ERROR, "This is not a valid service call");
            return;
        }

//        Optional<String> optEntityName = EntityUtils.getEntityNameFromServiceCallName(serviceCallName);

        if(serviceDescriptor.isCRUD() ) {

            if(!(STANDARD_CRUD_COMMANDER.contains(serviceDescriptor.getVerb()))) {
                holder.createProblem(attributeValue, HighlightSeverity.ERROR,
                        "The verb is not correctly,should use one of create/update/delete");
            }

            Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(project,
                    serviceDescriptor.getNoun());
            final int index = serviceCallName.indexOf("#");
            final int entityNameLength = serviceDescriptor.getNoun().length();


            if (optionalXmlElement.isEmpty()) {
                holder.createProblem(attributeValue, HighlightSeverity.ERROR, "Missing attributeThe called Entity is not found",
                        TextRange.from(index+2,entityNameLength));
            }
        }else {
            Optional<Service> opService = ServiceUtils.getServiceByFullName(project, serviceCallName);

            if (opService.isEmpty()) {
                holder.createProblem(attributeValue, HighlightSeverity.ERROR, "Called service is not found",
                        TextRange.from(1,serviceCallNameLength));
            }
        }

    }
    public static void inspectServiceCallFromAttribute(@NotNull GenericAttributeValue<String> attributeValue, @NotNull AnnotationHolder holder) {

        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
        if (xmlAttributeValue == null) { return;}

        final String serviceCallName = attributeValue.getXmlAttributeValue().getValue();
        //判断是否包含Groovy 变量，如果是，则不进行处理，仅仅提示
        if(MyStringUtils.containGroovyVariables(serviceCallName)) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "service call name contains Groovy variables")
                    .range(xmlAttributeValue.getValueTextRange())
                    .highlightType(ProblemHighlightType.WEAK_WARNING)
                    .create();
            return;
        }

        final int serviceCallNameLength = attributeValue.getXmlAttributeValue().getValueTextRange().getLength();
        final Project project =attributeValue.getXmlElement().getProject();
        ServiceCallDescriptor serviceDescriptor = ServiceCallDescriptor.of(serviceCallName);
        if(serviceDescriptor.getVerb().isEmpty()) {
//            holder.createProblem(attributeValue, HighlightSeverity.ERROR, "This is not a valid service call");
            holder.newAnnotation(HighlightSeverity.ERROR, "This is not a valid service call")
                    .range(xmlAttributeValue.getValueTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
            return;
        }

//        Optional<String> optEntityName = EntityUtils.getEntityNameFromServiceCallName(serviceCallName);

        if(serviceDescriptor.isCRUD() ) {

            if(!(STANDARD_CRUD_COMMANDER.contains(serviceDescriptor.getVerb()))) {
//                holder.createProblem(attributeValue, HighlightSeverity.ERROR,
//                        "The verb is not correctly,should use one of create/update/delete");
                holder.newAnnotation(HighlightSeverity.ERROR, "The verb is not correctly,should use one of create/update/delete")
                        .range(xmlAttributeValue.getValueTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }

            Optional<XmlElement> optionalXmlElement = EntityUtils.getEntityOrViewEntityXmlElementByName(project,
                    serviceDescriptor.getNoun());
            final int index = serviceCallName.indexOf("#");
            final int entityNameLength = serviceDescriptor.getNoun().length();


            if (optionalXmlElement.isEmpty()) {
//                holder.createProblem(attributeValue, HighlightSeverity.ERROR, "Missing attributeThe called Entity is not found",
//                        TextRange.from(index+2,entityNameLength));
                holder.newAnnotation(HighlightSeverity.ERROR, "Missing attributeThe called Entity is not found")
                        .range(xmlAttributeValue.getValueTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }
        }else {
            Optional<Service> opService = ServiceUtils.getServiceByFullName(project, serviceCallName);

            if (opService.isEmpty()) {
//                holder.createProblem(attributeValue, HighlightSeverity.ERROR, "Called service is not found",
//                        TextRange.from(1,serviceCallNameLength));
                holder.newAnnotation(HighlightSeverity.ERROR, "Called service is not found")
                        .range(xmlAttributeValue.getValueTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }
        }

    }
    public static final Set<String> STANDARD_CRUD_COMMANDER =Set.of("create","update","delete","store");

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
    public static Optional<EntityDeleteByCondition> getCurrentEntityDeleteByCondition(@NotNull PsiElement psiElement){
        return getLocalDomElementByPsiElement(psiElement,EntityDeleteByCondition.class);

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
    public static  @NotNull PsiReference[] createServiceCallReferences(@NotNull Project project, @NotNull PsiElement element, @NotNull String  serviceCallStr, @NotNull int startOffset) {
        ServiceCallDescriptor serviceDescriptor = ServiceCallDescriptor.of(serviceCallStr);
        if (serviceDescriptor.getVerb().isEmpty()) return PsiReference.EMPTY_ARRAY;
//        ServiceDescriptor serviceDescriptor = optServiceDescripter.get();

        Optional<Service> optService = getServiceOrInterfaceByFullName(project,serviceCallStr);

        if (optService.isEmpty()) return PsiReference.EMPTY_ARRAY;
        Service service = optService.get();

        List<PsiReference> resultList = new ArrayList<>();
        //创建verb的reference
        final int verbIndex = serviceCallStr.indexOf(serviceDescriptor.getVerb()+"#");
        final int verbStartOffset = startOffset+verbIndex;

        resultList.add(new PsiRef(element,
                new TextRange(verbStartOffset, verbStartOffset+ serviceDescriptor.getVerb().length()),
                service.getVerb().getXmlAttributeValue()));

        //创建侬noun的reference
        final int nounIndex = serviceCallStr.indexOf("#" + serviceDescriptor.getNoun());
        final int nounStartOffset = startOffset + nounIndex+1;
        resultList.add(new PsiRef(element,
                new TextRange(nounStartOffset, nounStartOffset+ serviceDescriptor.getNoun().length()),
                service.getNoun().getXmlAttributeValue()));

        //对文件名创建reference
        final int fileNameIndex = serviceDescriptor.getClassName().lastIndexOf(".");
        String fileName;
        if(fileNameIndex<0) {
            fileName = serviceDescriptor.getClassName();
        }else {
            fileName = serviceDescriptor.getClassName().substring(fileNameIndex + 1);
        }
        final int fileNameStartOffset = startOffset + fileNameIndex+1;
        resultList.add(new PsiRef(element,
                new TextRange(fileNameStartOffset, fileNameStartOffset+ fileName.length()),
                service.getXmlTag().getContainingFile()));

        //针对上级目录进行处理
        if(fileNameIndex>0) {
            String path = serviceDescriptor.getClassName().substring(0,fileNameIndex);
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
    /**
     * 创建ServiceCall对应的ServiceCallReference
     * @param project 所在project
     * @param element 待处理的PsiElement
     * @return PsiReference[]
     */

    public static   @NotNull PsiReference[] createServiceCallReferences(@NotNull Project project, @NotNull PsiElement element) {
        BeginAndEndCharPattern stringPattern = BeginAndEndCharPattern.of(element);
        List<PsiReference> psiReferences = new ArrayList<>();
        int tmpStartOffset,tmpEndOffset;

        if(MyStringUtils.containGroovyVariables(stringPattern.getContent())) { //如果是groovy变量，则不进行处理
            return new PsiReference[0];
        }

        if(stringPattern.getContent().isBlank()){
            tmpStartOffset = stringPattern.getBeginChar().length();
            tmpEndOffset = tmpStartOffset;
            psiReferences.add(ServiceCallReference.of(element,
                    new TextRange(tmpStartOffset,tmpEndOffset),null));//提供 code completion

        }else {
            List<Pair<TextRange,PsiElement>> textRangeList = createServiceCallReferencesResolve(element.getProject(),element);

            textRangeList.forEach(item->{
                psiReferences.add(ServiceCallReference.of(element,item.first,item.second));
            });

            if (psiReferences.isEmpty()) {

                int lastDotIndex = stringPattern.getContent().lastIndexOf(SERVICE_NAME_DOT);
                tmpEndOffset = stringPattern.getBeginChar().length() + stringPattern.getContent().length();

                if(lastDotIndex>=0) {
                    tmpStartOffset = lastDotIndex + stringPattern.getBeginChar().length()+1;
                }else {
                    int hashIndex = stringPattern.getContent().lastIndexOf(SERVICE_NAME_HASH);
                    if (hashIndex>=0) {
                        tmpStartOffset = hashIndex+ stringPattern.getBeginChar().length()+1;
                    }else {
                        tmpStartOffset = stringPattern.getBeginChar().length();
                    }
                }

                psiReferences.add(ServiceCallReference.of(element,
                        new TextRange(tmpStartOffset,tmpEndOffset),null));//提供 code completion
            }

        }
        return psiReferences.toArray(new PsiReference[0]);

    }

    /**
     * 将ServiceCall所在的PsiElement的Text进行匹配，以便在创建PsiReference时使用
     * @param project 所在project
     * @param element 待处理的PsiElement
     * @return List<Pair<TextRange,PsiElement>>
     */
    public static  @NotNull List<Pair<TextRange,PsiElement>> createServiceCallReferencesResolve(@NotNull Project project, @NotNull PsiElement element) {

        List<Pair<TextRange,PsiElement>> resultArray = new ArrayList<>();
        BeginAndEndCharPattern elementTextPattern = BeginAndEndCharPattern.of(element);
        if(elementTextPattern.getContent().isEmpty())return resultArray;

        ServiceCallDescriptor serviceDescriptor = ServiceCallDescriptor.of(elementTextPattern.getContent());

        if (serviceDescriptor.getVerb().isEmpty()) return resultArray;
//        ServiceDescriptor serviceDescriptor = optServiceDescripter.get();

        Map<TextRange,PsiElement> resultItem;
        TextRange tmpRange;
        PsiElement tmpElement;
        int tmpStartOffset;
        int tmpEndOffset;


        if(serviceDescriptor.isCRUD()) {
            Optional<Entity> entityOptional;
            entityOptional = EntityUtils.getEntityByName(project, serviceDescriptor.getNoun());

            if(entityOptional.isPresent()) {
                EntityNameDescriptor entityNameDescriptor = EntityNameDescriptor.of(serviceDescriptor.getNoun());
                //entity name
                tmpElement = MyDomUtils.getPsiElementFromAttributeValue(entityOptional.get().getEntityName().getXmlAttributeValue()).orElse(null);

                if(tmpElement != null){
                    tmpStartOffset = elementTextPattern.getBeginChar().length()+ serviceDescriptor.getNounIndex()+entityNameDescriptor.getEntityNameIndex();
                    tmpEndOffset = tmpStartOffset + entityNameDescriptor.getEntityName().length();
                    tmpRange = new TextRange(tmpStartOffset,tmpEndOffset);

                    resultArray.add(new Pair<>(tmpRange,tmpElement));
                }

                //entity package
                tmpElement = MyDomUtils.getPsiElementFromAttributeValue(entityOptional.get().getPackage().getXmlAttributeValue()).orElse(null);
                if(tmpElement != null) {
                    tmpStartOffset = elementTextPattern.getBeginChar().length()+ serviceDescriptor.getNounIndex();
                    tmpEndOffset = tmpStartOffset + entityNameDescriptor.getPackageName().length();

                    tmpRange = new TextRange(tmpStartOffset,tmpEndOffset);

                    resultArray.add(new Pair<>(tmpRange, tmpElement));
                }

            }
        }else {
            Optional<Service> serviceOptional = ServiceUtils.getServiceByFullName(project,serviceDescriptor.getServiceCallString());
            if(serviceOptional.isPresent()) {
                //noun
                if(serviceDescriptor.hasNoun()) {
                    tmpElement = MyDomUtils.getPsiElementFromAttributeValue(serviceOptional.get().getNoun().getXmlAttributeValue()).orElse(null);
                    if (tmpElement != null) {
                        tmpStartOffset = elementTextPattern.getBeginChar().length() + serviceDescriptor.getNounIndex();
                        tmpEndOffset = tmpStartOffset + serviceDescriptor.getNoun().length();

                        resultArray.add(new Pair<>(new TextRange(tmpStartOffset, tmpEndOffset), tmpElement));
                    }
                }
                //verb
                tmpElement = MyDomUtils.getPsiElementFromAttributeValue(serviceOptional.get().getVerb().getXmlAttributeValue()).orElse(null);
                if(tmpElement !=null) {
                    tmpStartOffset = elementTextPattern.getBeginChar().length() + serviceDescriptor.getVerbIndex();
                    tmpEndOffset = tmpStartOffset + serviceDescriptor.getVerb().length();

                    resultArray.add(new Pair<>(new TextRange(tmpStartOffset,tmpEndOffset), tmpElement));
                }
                //package
                //对文件名创建reference
                final int fileNameIndex = serviceDescriptor.getClassName().lastIndexOf(".");
                String fileName;
                if(fileNameIndex<0) {
                    fileName = serviceDescriptor.getClassName();
                }else {
                    fileName = serviceDescriptor.getClassName().substring(fileNameIndex + 1);
                }
                tmpStartOffset = elementTextPattern.getBeginChar().length() + fileNameIndex + 1;
                tmpEndOffset = tmpStartOffset + fileName.length();
                if(serviceOptional.get().getXmlTag() !=null)
                    resultArray.add(new Pair<>(new TextRange(tmpStartOffset,tmpEndOffset), serviceOptional.get().getXmlTag().getContainingFile()));

                //针对上级目录进行处理
                if(fileNameIndex>0) {
                    String path = serviceDescriptor.getClassName().substring(0,fileNameIndex);
                    PsiDirectory psiPath = serviceOptional.get().getXmlTag().getContainingFile().getContainingDirectory();
                    int pathDotIndex;
                    int pathStartOffset;
                    String tmpPath;
                    while(!path.isEmpty()) {
                        pathDotIndex = path.lastIndexOf(".");
                        pathStartOffset = elementTextPattern.getBeginChar().length() + pathDotIndex + 1;
                        if (pathDotIndex<0) {
                            tmpPath = path;
                            path = MyStringUtils.EMPTY_STRING;
                        }else {
                            tmpPath = path.substring(pathDotIndex+1);
                            path = path.substring(0,pathDotIndex);
                        }

                        if(psiPath != null) {
                            resultArray.add(new Pair<>(new TextRange(pathStartOffset, pathStartOffset + tmpPath.length()), psiPath));
                            psiPath = psiPath.getParent();
                        }
                    }
                }


            }
        }
        return resultArray;

    }
    /**
     * 针对ServiceCall的inMap和outMap的字符串进行处理，收集可以匹配Parameter的内容
     *
     * 字符串实例：
     * 1、 [invoiceId:invoiceId]
     * 2、 [invoiceId:invoiceId, emailTypeEnumId:'PsetInvoiceFinalized']
     * 3、[invoiceId:invoiceId, statusId:(statusId == 'InvoiceFinalized' ? 'InvoicePmtRecvd' : 'InvoicePmtSent')]
     * 4、context + [flattenDocument:false]
     * 5、subMap + [baseWorkEffortId:child.workEffortId, parentWorkEffortId:workEffortId]
     * 6、context + [contactMechId:addressFcmList[0].contactMechId, contactMechPurposeId:'PostalPrimary']，注意[]嵌套[]
     * @param fieldString 待处理的字符串
     * @return List<FieldDescriptor>
     */
    public static List<FieldDescriptor> extractMapFieldDescriptorList(@NotNull String fieldString, int offset) {
        List<FieldDescriptor> result = new ArrayList<>();
        int totalLength = fieldString.length();
        int stepIndex = 0;
        int fieldBeginIndex = 0;
        boolean inMap = false;
        boolean inField = false;
        int nestingCount = 0;

        StringBuilder fieldName = new StringBuilder(MyStringUtils.EMPTY_STRING);
        while(stepIndex<totalLength){
            switch (fieldString.charAt(stepIndex)){
                case '['->{
                    if(inMap) {
                        nestingCount += 1;
                    }else{
                        fieldBeginIndex = stepIndex + 1;
                        inMap = true;
                        inField = true;
                    }
                }
                case ']'->{
                    if(inMap){
                        if(nestingCount == 0){
                            inMap = false;
                        }else{
                            nestingCount -=1;
                        }
                    }
                }
                case ',' ->{
                    if(inMap){
                        fieldBeginIndex = stepIndex+1;
                        inField = true;
                    }
                }
                case ':' ->{
                    if(inField){
                        result.add(FieldDescriptor.of(fieldName.toString(),
                                offset+fieldBeginIndex,
                                offset+stepIndex
                                ));
                        inField = false;
                        fieldName = new StringBuilder(MyStringUtils.EMPTY_STRING);
                    }
                }
                default -> {
                    if(inField) fieldName.append(fieldString.charAt(stepIndex));

                }


            }
            stepIndex +=1;
        }
        //如果最后一个字符是","，则补充一个空字段
        if(inField){
            result.add(FieldDescriptor.of(fieldName.toString(),
                    offset+fieldBeginIndex,
                    offset+stepIndex
            ));
        }

//        Matcher matcher = MyStringUtils.IN_OUT_MAP_NAME_PATTERN.matcher(fieldString);

//        while(matcher.find()) {
//            String localField = matcher.group(1);
//            int localOffset = offset + matcher.start(1);
//            int splitOffset = 0;
//            String[] splitField = localField.split(",");
//            for(String splitItem: splitField){
//                int index = splitItem.indexOf(":");
//                if(index>0){
//                    result.add(FieldDescriptor.of(splitItem.substring(0,index),
//                            localOffset+splitOffset,
//                            localOffset+splitOffset+index
//                            ));
//
//                }
//                splitOffset += splitItem.length() + 1; //增加逗号的长度
//            }
//
//        }
        return result;
    }

    /**
     * 获取IndexService
     * @param project 当前Project
     * @param serviceName Service full Name
     * @return Optional<IndexService>
     */
    public static @NotNull Optional<IndexService> getIndexService(@NotNull Project project, @NotNull String serviceName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getIndexServiceByFullName(serviceName);
    }
    /**
     * 获取Service的InParameter
     * @param project 当前Project
     * @param serviceName Service full Name
     * @return List<IndexServiceParameter>
     */
    public static @NotNull List<IndexServiceParameter> getServiceInParamterList(@NotNull Project project, @NotNull String serviceName){
        return getIndexService(project,serviceName).map(IndexService::getInParametersAbstractFieldList).orElse(new ArrayList<>());
    }

    /**
     * 创建map中字段的reference
     * @param psiElement 当前属性的PsiElement
     * @param fieldDescriptor 待处理的参数
     * @param indexServiceParameter 指向的目标parameter
     * @return 返回PsiRef
     */
    public static @NotNull PsiReference[] createMapNameReference(@NotNull PsiElement psiElement, @NotNull FieldDescriptor fieldDescriptor, @Nullable IndexServiceParameter indexServiceParameter){
        if (fieldDescriptor.isContainGroovyVariable() || fieldDescriptor.isEmpty()) return PsiReference.EMPTY_ARRAY;

        List<PsiReference> resultList = new ArrayList<>();
        if(indexServiceParameter == null) {
            resultList.add(ServiceInParameterReference.of(psiElement,
                    TextRange.create(fieldDescriptor.getFieldNameBeginIndex(),fieldDescriptor.getFieldNameEndIndex()),
                    null)); //提示错误

        }else {
            resultList.add(ServiceInParameterReference.of(psiElement,
                    TextRange.create(fieldDescriptor.getFieldNameBeginIndex(), fieldDescriptor.getFieldNameEndIndex()),
                    indexServiceParameter.getAbstractField().getName().getXmlAttributeValue()));

        }
        return resultList.toArray(new PsiReference[0]);
    }

}



