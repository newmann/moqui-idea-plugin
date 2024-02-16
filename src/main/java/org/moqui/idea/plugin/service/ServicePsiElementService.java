package org.moqui.idea.plugin.service;

import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.util.CustomNotifier;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ServiceUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;
@Deprecated
@Service(Service.Level.PROJECT)
public final class ServicePsiElementService {
    private final Project project;

    private Map<String, org.moqui.idea.plugin.dom.model.Service> serviceTags = new HashMap<>();
    private Map<String, org.moqui.idea.plugin.dom.model.Service> interfaceTags = new HashMap<>();
    public ServicePsiElementService(Project project) {
        this.project = project;
        List<DomFileElement<Services>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(project,Services.class);
        fileElementList.forEach(fileElement -> updateTagsFromFile(fileElement));
    }

    /**
     * 更新某个services.xml定义文件的内容，
     * @param fileElement
     */
    public void updateTagsFromFile(DomFileElement<Services> fileElement){

        final String filePath = fileElement.getOriginalFile().getVirtualFile().getPath();

        final Optional<String> optClassName = ServiceUtils.extractClassNameFromPath(filePath);
        if (optClassName.isEmpty()){
            CustomNotifier.warn(fileElement.getOriginalFile().getProject(),
                    "根据路径（"+filePath+ "）找不到对应的包名称。");
            return;
        }
//        System.out.println("扫描文件："+filePath);

        final String className = optClassName.get();

        //先将原来的数据删除
        for(Iterator<Map.Entry<String, org.moqui.idea.plugin.dom.model.Service>> it = serviceTags.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, org.moqui.idea.plugin.dom.model.Service> item = it.next();
            String key = item.getKey();
            if (key.indexOf(className) == 0 ) {
                serviceTags.remove(item);
            }
        }
        for(Iterator<Map.Entry<String, org.moqui.idea.plugin.dom.model.Service>> it = interfaceTags.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, org.moqui.idea.plugin.dom.model.Service> item = it.next();
            String key = item.getKey();
            if (key.indexOf(className) == 0 ) {
                interfaceTags.remove(item);
            }
        }

        //添加新对象
        for(org.moqui.idea.plugin.dom.model.Service serviceItem: fileElement.getRootElement().getServiceList()) {
            String fullName = className+"." + serviceItem.getVerb().getValue()
                    + "#" + serviceItem.getNoun().getValue();
            String type = serviceItem.getServiceType().getValue();

            if( (type != null) && type.equals("interface")) {
                interfaceTags.put(fullName,serviceItem);
            }else {
                serviceTags.put(fullName,serviceItem);
            }
        };

    }

    /**
     * 根据Service的全名获取对应的DomElement
     * @param fullName
     * @return
     */
    public DomElement getPsiElementByFullName(@NotNull String fullName) {
        return serviceTags.get(fullName);
    }


    public Map<String,DomElement> getAllServiceDomElements(){
        Map<String,DomElement> result = new HashMap<String,DomElement>();
        result.putAll(serviceTags);
        return result;
    }
}
