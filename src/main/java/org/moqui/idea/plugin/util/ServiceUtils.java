package org.moqui.idea.plugin.util;

import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.service.ServicePsiElementService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public final class ServiceUtils {
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
        public String className;
        public String verb;
        public String noun;
    }


    public static boolean isServicesFile(@Nullable PsiFile file){
        return DomUtils.isSpecialXmlFile(file, Services.TAG_NAME);
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
        List<DomFileElement<Entities>> fileElementList  = DomUtils.findDomFileElementsByRootClass(project, Entities.class);
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
     * 根据实体名和包名找到对应的实体定义的XmlElement
     * @param project
     * @param fullName 调用的服务名称，格式类似 moqui.work.TicketServices.get#ServiceStationByServiceLocation
     * @return
     */
    public static Optional<XmlElement[]> findServiceByFullName(@NotNull Project project, @NotNull String fullName){
        Optional<ServiceDescriptor> serviceDescriptor = extractServiceDescriptor(fullName);
        if (serviceDescriptor.isEmpty()) return Optional.empty();

        List<DomFileElement<Services>> fileElementList  = DomUtils.findDomFileElementsByRootClass(project, Services.class);
        for(DomFileElement<Services> fileElement : fileElementList) {
            //判断服务在不在这个文件中
            Optional<String> className = extractClassNameFromPath(fileElement.getFile().getVirtualFile().getPath());
            if(className.isEmpty()) continue;
            if(!className.get().equals(serviceDescriptor.get().className)) continue;

            for(Service service: fileElement.getRootElement().getServices()) {
                if(service.getVerb().getValue().equals(serviceDescriptor.get().verb)
                        && service.getNoun().getValue().equals(serviceDescriptor.get().noun)) {

                    XmlElement[] result = {service.getXmlElement()};
                    return Optional.of(result);
                }
            };
        }
        return Optional.empty();
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
    public static Optional<ServiceDescriptor> extractServiceDescriptor(@NotNull String fullName){
        final int index = fullName.lastIndexOf("#");
        if(index < 0) return Optional.empty();
        final String noun = fullName.substring(index);
        //不是服务，是对entity的CRUD
        if(noun.indexOf(".")>0) return Optional.empty();
        final String pathVerb = fullName.substring(0,index-1);

        final int verbIndex = pathVerb.lastIndexOf(".");
        //如果一个都没有，则是有问题
        if(verbIndex < 0) return Optional.empty();

        final String verb = pathVerb.substring(verbIndex);
        final String className = pathVerb.substring(0,index-1);

        return Optional.of(new ServiceDescriptor(className,verb,noun));

    }
    public static List<PsiElement> getRelatedService(@NotNull PsiElement psiElement, @NotNull String fullName) {
        List<PsiElement> resultList = new ArrayList<>();

        ServicePsiElementService servicePsiElementService =
                psiElement.getProject().getService(ServicePsiElementService.class);

        DomElement target = servicePsiElementService.getPsiElementByFullName(fullName);
        if (target == null) {
            CustomNotifier.warn(psiElement.getProject(), "发现找不到的Service，fullName：" + fullName +", 所在文件："
                    +psiElement.getContainingFile().getVirtualFile().getPath());
        }else {
            resultList.add((PsiElement) target.getXmlElement());
        }
        return resultList;
    }
    public static Icon getNagavitorToServiceIcon() {
        return MyIcons.NAVIGATE_TO_SERVICE;
    }


    public static String getNagavitorToServiceToolTips() {
        return "Navigating to Service definition";
    }

}
