package org.moqui.idea.plugin.util;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.Entity;

import java.util.*;


public final class ComponentUtils {
    public static String COMPONENT_LOCATION_PREFIX = "component://";
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
            String name = ReadAction.compute(()->{
                return component.getName().getStringValue();
            });
            result.put(name,component);

        }
        return result;


    }

    /**
     * 根据某个DomElement找到所在的Component的名称
     * @param element
     * @return
     */
    @Deprecated
    public static String getComponentName(DomElement element){

        try {
            PsiFile file = element.getXmlElement().getContainingFile();
            PsiDirectory dir = file.getParent();
            //todo 应该有更好的处理办法
            for(int i = 0; i<10;  i++) {
                XmlFile componentFile = (XmlFile) dir.findFile("component.xml");
                if(isComponentFile(componentFile)) {
                    XmlTag rootTag = componentFile.getRootTag();
                    return rootTag.getAttribute(Component.ATTR_NAME).getValue();
                }else{
                    dir = dir.getParent();
                }
            }
            return null;
        }catch (NullPointerException ignored) {
            return null;
        }

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
            return Optional.of(splitArray[1].split(LocationUtils.PATH_SEPARATOR)[0]);//取第一个“/”之前的内容，如果没有，则取全部
        }else {
            return Optional.empty();
        }

    }
}
