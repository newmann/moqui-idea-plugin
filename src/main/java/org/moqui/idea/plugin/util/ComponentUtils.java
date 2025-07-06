package org.moqui.idea.plugin.util;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public final class ComponentUtils {
    private ComponentUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isComponentFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Component.TAG_NAME,Component.ATTR_NoNamespaceSchemaLocation,Component.VALUE_NoNamespaceSchemaLocation);
    }
    /**
     * 根据所有的组件
     * @param project 当前项目
     * @return Collection<Component>
     */
    public static Map<String,Component> findAllComponent(@NotNull Project project){

        List<DomFileElement<Component>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Component.class);
        Map<String,Component> result = new HashMap<>();
        for(DomFileElement<Component> fileElement : fileElementList) {
            Component component = fileElement.getRootElement();
            String name = ReadAction.compute(()->component.getName().getStringValue());
            result.put(name,component);

        }
        return result;


    }

    /**
     * 根据组件名称获取组件的路径
     * @param project 当前Project
     * @param componentName 组件名称
     * @return Optional<String>
     */
    public static Optional<String> getComponentPathByName(@NotNull Project project, @NotNull String componentName) {
        List<DomFileElement<Component>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Component.class);
        for(DomFileElement<Component> fileElement : fileElementList) {
            Component component = fileElement.getRootElement();
            String name = ReadAction.compute(()->component.getName().getStringValue());
            if(name.equals(componentName)) {
                String pathName = MyDomUtils.getFilePathByPsiElement(fileElement.getRootElement().getXmlTag()).orElse(MyStringUtils.EMPTY_STRING);
                if(MyStringUtils.isEmpty(pathName)) {
                    return Optional.empty();
                }
                String path = pathName.substring(0,pathName.lastIndexOf(MyStringUtils.PATH_SEPARATOR));
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }
    public static Optional<String> getComponentNameFromPath(@NotNull String path){
        String[] splitArray;
        if(path.contains(MyStringUtils.COMPONENT_PATH_TAG)) {
            splitArray = path.split(MyStringUtils.COMPONENT_PATH_TAG);
        }else if(path.contains(MyStringUtils.BASE_COMPONENT_PATH_TAG)) {
            splitArray = path.split(MyStringUtils.BASE_COMPONENT_PATH_TAG);
        }else if(path.contains(MyStringUtils.FRAMEWORK_PATH_TAG)) {
            return Optional.of(MyStringUtils.FRAMEWORK_COMPONENT_NAME);
        }else {
            return Optional.empty();
        }

        if(splitArray.length == 2) {
            return Optional.of(splitArray[1].split(MyStringUtils.PATH_SEPARATOR)[0]);//取第一个“/”之前的内容，如果没有，则取全部
        }else {
            return Optional.empty();
        }

    }
}
