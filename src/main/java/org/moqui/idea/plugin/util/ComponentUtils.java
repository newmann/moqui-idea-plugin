package org.moqui.idea.plugin.util;

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
        Map<String,Component> result = new HashMap<>();
        List<DomFileElement<Component>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Component.class);
        for(DomFileElement<Component> fileElement : fileElementList) {
            Component component = fileElement.getRootElement();
            result.put(component.getName().getStringValue(),component);
        }
        return result;
    }

    /**
     * 根据某个DomElement找到所在的Component的名称
     * @param element
     * @return
     */
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
}
