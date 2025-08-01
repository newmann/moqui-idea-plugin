package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.WidgetTemplate;
import org.moqui.idea.plugin.dom.model.WidgetTemplates;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class WidgetTemplateUtils {
    private WidgetTemplateUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isWidgetTemplateFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, WidgetTemplates.TAG_NAME);

    }
    /**
     * 从指定的文件中，根据名称获取对应的template定义
     */
    public static Optional<WidgetTemplate> getWidgetTemplateFromFileByName(PsiFile file, String name){
        if(file instanceof XmlFile xmlFile) {
            DomFileElement<WidgetTemplates> widgetTemplates = DomManager.getDomManager(file.getProject())
                    .getFileElement(xmlFile, WidgetTemplates.class);
            if(widgetTemplates == null) return Optional.empty();

            return widgetTemplates.getRootElement().getWidgetTemplateList().stream()
                    .filter(item ->MyDomUtils.getXmlAttributeValueString(item.getName())
                                .orElse(MyStringUtils.EMPTY_STRING).equals(name)
                    )
                    .findFirst();
        }else {

            return Optional.empty();
        }
    }

    /**
     * 获取当前项目中所有可以用来导入的WidgetTemplate定义
     */
    public static List<String> getWidgetTemplateIncludeLocations(@NotNull Project project){
        List<DomFileElement<WidgetTemplates>> widgetTemplatesList = MyDomUtils.findDomFileElementsByRootClass(project,WidgetTemplates.class);
        List<String> result = new ArrayList<>();

        widgetTemplatesList.forEach(item-> {
            MoquiFile moquiFile = MoquiFile.of(item.getFile().getContainingFile());
            String componentRelativePath = moquiFile.getComponentRelativePath();
            result.addAll(item.getRootElement().getWidgetTemplateList().stream().map(wt-> componentRelativePath + "#"
                    + MyDomUtils.getValueOrEmptyString(wt.getName())).toList());
        });
        return result;
    }
}
