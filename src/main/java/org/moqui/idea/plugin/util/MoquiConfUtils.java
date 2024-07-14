package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MoquiConf;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.SubScreensItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class MoquiConfUtils {
    private MoquiConfUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isMoquiConfFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, MoquiConf.TAG_NAME,MoquiConf.ATTR_NoNamespaceSchemaLocation,MoquiConf.VALUE_NoNamespaceSchemaLocation);
    }

    /**
     * 通过同一个component下其他文件的文件名，获得本component的MoquiConf.xml
     * MoquiConf.xml就在component根目录下
     * @return
     */
    public static Optional<PsiFile> getMoquiConfFileByComponentFileName(@NotNull Project  project, @NotNull String componentFileName){
        String[] parts = componentFileName.split("/runtime/component/");
        if(parts.length != 2) return Optional.empty();
        int index = parts[1].indexOf("/");
        if(index == -1) return Optional.empty();
        String componentName = parts[1].substring(0, index);
        String confName = parts[0]+"/runtime/component/"+componentName+"/MoquiConf.xml";

        return MyDomUtils.getPsiFileByPathName(project,confName);
    }

    /**
     * 从MoquiConf文件中获取指定名称的SubScreenItem，也就是菜单入口
     * @param file MoquiConf的PsiFile
     * @param itemName SubScreenItem的name
     * @return
     */
    public static Optional<SubScreensItem> getSubScreensItem(@NotNull PsiFile file,@NotNull String itemName){
        DomFileElement<MoquiConf> moquiConfDomFileElement = MyDomUtils.convertPsiFileToDomFile(file,MoquiConf.class);
        if(moquiConfDomFileElement == null) return Optional.empty();
        return moquiConfDomFileElement.getRootElement().getScreenFacade().getScreenList().stream()
                        .flatMap(screen -> screen.getSubScreensItemList().stream())
                        .filter(item->MyDomUtils.getValueOrEmptyString(item.getName()).equals(itemName))
                        .findFirst();
    }

    public static List<Screen> getAllScreens(@NotNull Project project){
        List<DomFileElement<MoquiConf>> moquiConfList = MyDomUtils.findDomFileElementsByRootClass(project,MoquiConf.class);
        ArrayList<Screen> result = new ArrayList<Screen>();
        for(DomFileElement<MoquiConf> moquiConf: moquiConfList) {
            result.addAll(moquiConf.getRootElement().getScreenFacade().getScreenList());
        }
        return result;

    }
}
