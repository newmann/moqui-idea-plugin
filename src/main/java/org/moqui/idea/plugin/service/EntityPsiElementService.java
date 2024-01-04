package org.moqui.idea.plugin.service;

import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.util.DomUtils;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.trim;

@Service(Service.Level.PROJECT)
public final class EntityPsiElementService {
    private final Project project;

    private Map<String, Entity> entityTags = new HashMap<>();
    private Map<String, ViewEntity> viewTags = new HashMap<>();
    public EntityPsiElementService(Project project) {
        this.project = project;
        List<DomFileElement<Entities>> fileElementList = DomUtils.findDomFileElementsByRootClass(project,Entities.class);
        fileElementList.forEach(entities -> updateTagsFromFile(entities));
    }

    /**
     * 更新某个entities.xml定义文件的内容，
     * @param fileElement
     */
    public void updateTagsFromFile(DomFileElement<Entities> fileElement){
        final String filePath = fileElement.getOriginalFile().getVirtualFile().getPath();
        //先将原来的数据删除
        for(Iterator<Map.Entry<String, Entity>> it = entityTags.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String,Entity> item = it.next();
            Entity entity = item.getValue();
            String p = entity.getXmlElement().getContainingFile().getVirtualFile().getPath();
            if (filePath.equals(p)) {
                entityTags.remove(item);
            }
        }
        for(Iterator<Map.Entry<String, ViewEntity>> it = viewTags.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String,ViewEntity> item = it.next();
            ViewEntity viewEntity = item.getValue();
            String p = viewEntity.getXmlElement().getContainingFile().getVirtualFile().getPath();
            if (filePath.equals(p)) {
                viewTags.remove(item);
            }
        }

        for(Entity entity: fileElement.getRootElement().getEntities()) {
            String fullName = EntityUtils.getFullName(entity.getEntityName().getValue(), entity.getPackage().getValue());
            entityTags.put(fullName,entity);
        };
        for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
            String fullName = EntityUtils.getFullName(viewEntity.getEntityName().getValue(), viewEntity.getPackage().getValue());
            viewTags.put(fullName,viewEntity);
        };

    }

    /**
     * 根据Entity的全名获取对应的DomElement
     * @param fullName
     * @return
     */
    public DomElement getPsiElementByFullName(@NotNull String fullName) {
        Entity entity = entityTags.get(fullName);
        if (entity == null) {
            //如果entity中没有找到，在view中找
            return viewTags.get(fullName);
        }else {
            return entity;
        }
    }

    /**
     * 返回所有符合查询字符串的Entity全名
     * @param searchStr
     * @return
     */
    public List<String> searchFullNames(String searchStr){

        if(searchStr == null){searchStr = "";}

        final String searchToken = trim(searchStr);

        if(searchToken == "") {
            return getAllFullNames();
        }else {
            List<String> result = new ArrayList<String>();
            entityTags.forEach((key,value)->{if(key.indexOf(searchToken)>0) result.add(key);});
            viewTags.forEach((key,value)->{if(key.indexOf(searchToken)>0) result.add(key);});
            return result;
        }
    }

    public List<String> getAllFullNames(){
        List<String> result = new ArrayList<String>();
        entityTags.forEach((key,value)->{result.add(key);});
        viewTags.forEach((key,value)->{result.add(key);});
        return result;
    }

    public Map<String,DomElement> getAllEntityDomElements(){
        Map<String,DomElement> result = new HashMap<String,DomElement>();
        result.putAll(entityTags);
        result.putAll(viewTags);
        return result;
    }
}
