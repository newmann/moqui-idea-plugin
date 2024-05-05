package org.moqui.idea.plugin.util;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Emecas;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.WidgetTemplate;
import org.moqui.idea.plugin.dom.model.WidgetTemplates;

import java.util.Optional;

import static org.moqui.idea.plugin.util.MyDomUtils.getLocalDomElementByConvertContext;


public final class WidgetTemplateUtils {
    private WidgetTemplateUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isWidgetTemplateFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, WidgetTemplates.TAG_NAME);

    }
    public static Optional<WidgetTemplates> getCurrentWidgetTemplates(ConvertContext context){
        return getLocalDomElementByConvertContext(context,WidgetTemplates.class);

    }

    public static Optional<WidgetTemplate> getCurrentWidgetTemplate(ConvertContext context){
        return getLocalDomElementByConvertContext(context, WidgetTemplate.class);

    }

    /**
     * 从指定的文件中，根据名称获取对应的template定义
     * @param file
     * @param name
     * @return
     */
    public static Optional<WidgetTemplate> getWidgetTemplateFromFileByName(PsiFile file, String name){
        if(file instanceof XmlFile xmlFile) {
            DomFileElement<WidgetTemplates> widgetTemplates = DomManager.getDomManager(file.getProject())
                    .getFileElement(xmlFile, WidgetTemplates.class);
            if(widgetTemplates == null) return Optional.empty();

            return widgetTemplates.getRootElement().getWidgetTemplateList().stream()
                    .filter(item ->{
                        return MyDomUtils.getXmlAttributeValueString(item.getName())
                                .orElse(MyStringUtils.EMPTY_STRING).equals(name);
                    })
                    .findFirst();
        }else {

            return Optional.empty();
        }
    }

}
