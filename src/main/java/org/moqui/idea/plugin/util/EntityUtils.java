package org.moqui.idea.plugin.util;

import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.service.EntityPsiElementService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;


public final class EntityUtils {
    private EntityUtils() {
        throw new UnsupportedOperationException();
    }

    public static final class EntityDescriptor{
        EntityDescriptor(){}
        EntityDescriptor(String entityName,String entityPackage){
            this.entityName = entityName;
            this.entityPackage = entityPackage;
        }
        public String entityName;
        public String entityPackage;
    }

    public static boolean isEntitiesFile(@Nullable PsiFile file){
        return DomUtils.isSpecialXmlFile(file, Entities.TAG_NAME);
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
     * @param entityName
     * @param entityPackage
     * @return
     */
    public static Optional<XmlElement[]> findEntityByNameAndPackage(@NotNull Project project, @NotNull String entityName,
                                                                  @NotNull String entityPackage){

        List<DomFileElement<Entities>> fileElementList  = DomUtils.findDomFileElementsByRootClass(project, Entities.class);
        for(DomFileElement<Entities> fileElement : fileElementList) {
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                if(entity.getEntityName().getValue().equals(entityName)
                        && entity.getPackage().getValue().equals(entityPackage)) {
                    XmlElement[] result = {entity.getXmlElement()};

                    return Optional.of(result);
                }
            };
            //如果在实体中没有找到，在视图中再查找
            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
                if(viewEntity.getEntityName().getValue().equals(entityName)
                        && viewEntity.getPackage().getValue().equals(entityPackage)) {
                    XmlElement[] viewElement = {viewEntity.getXmlElement()};

                    return Optional.of(viewElement);
                }
            };

        }
        return Optional.empty();
    }
    public static Optional<XmlElement[]> findEntityByEntityDescriptor(@NotNull Project project, @NotNull EntityDescriptor entityDescriptor){
        if(entityDescriptor.entityName == null || entityDescriptor.entityPackage == null) {
            return Optional.empty();
        }
        return findEntityByNameAndPackage(project, entityDescriptor.entityName,entityDescriptor.entityPackage);
    }

    public static EntityDescriptor getEntityDescriptorFromFullName(@NotNull String fullName){
        int lastIndex = fullName.lastIndexOf(".");
        EntityDescriptor entityDescriptor;
        if(lastIndex<0) {
            entityDescriptor = new EntityDescriptor();
            entityDescriptor.entityName = fullName;
        }else {

            String entityName = fullName.substring(lastIndex + 1);
            String entityPackage = fullName.substring(0, lastIndex);
            entityDescriptor = new EntityDescriptor(entityName, entityPackage);
        }
        return entityDescriptor;

    }

    public static @NotNull Set<String> getEntityFullNames(@NotNull Project project){
        Set<String> entityNames = new HashSet<String>();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,project,scope);

        for(DomFileElement<Entities> fileElement : fileElementList) {
            //添加实体
            for(Entity entity: fileElement.getRootElement().getEntities()) {
                entityNames.add(entity.getPackage().getValue()+"."+entity.getEntityName().getValue());
            };
            //添加视图
            for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
                entityNames.add(viewEntity.getPackage().getValue()+"."+viewEntity.getEntityName().getValue());
            };

        }
        return entityNames;
    }

    public static @NotNull Set<String> getEntityFullNames(@NotNull Project project, @NotNull String queryString){
        Set<String> entityNames = getEntityFullNames(project);

        return entityNames.stream().filter(item->item.indexOf(queryString)>=0)
                .collect(Collectors.toSet());

    }
    public static String getFullName(@NotNull String name, @NotNull String packageName){
        return packageName + "." + name;
    }

    public static List<PsiElement> getRelatedEntity(@NotNull PsiElement psiElement, @NotNull String fullName) {
        List<PsiElement> resultList = new ArrayList<>();

        EntityPsiElementService entityPsiElementService =
                psiElement.getProject().getService(EntityPsiElementService.class);

        DomElement target = entityPsiElementService.getPsiElementByFullName(fullName);
        if (target == null) {
            CustomNotifier.warn(psiElement.getProject(), "发现找不到的Entity，fullName：" + fullName +", 所在文件："
                    +psiElement.getContainingFile().getVirtualFile().getPath());
        }else {
            resultList.add((PsiElement) target.getXmlElement());
        }

        return resultList;
    }

    public static Icon getNagavitorToEntityIcon() {
        return MyIcons.NAVIGATE_TO_ENTITY;
    }


    public static String getNagavitorToEntityToolTips() {
        return "Navigating to Entity definition";
    }
}
