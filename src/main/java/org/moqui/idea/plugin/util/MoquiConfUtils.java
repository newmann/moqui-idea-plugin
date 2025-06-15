package org.moqui.idea.plugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MoquiConf;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.SubScreensItem;
import org.moqui.idea.plugin.service.IndexRootSubScreensItem;
import org.moqui.idea.plugin.service.MoquiIndexService;

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
     * @return Optional<PsiFile>
     */
    public static Optional<PsiFile> getMoquiConfFileByComponentFileName(@NotNull Project  project, @NotNull String componentFileName){
        String[] parts = componentFileName.split(MyStringUtils.COMPONENT_PATH_TAG);
        if(parts.length != 2) return Optional.empty();
        int index = parts[1].indexOf("/");
        if(index == -1) return Optional.empty();
        String componentName = parts[1].substring(0, index);
        String confName = parts[0]+MyStringUtils.COMPONENT_PATH_TAG+componentName+"/MoquiConf.xml";

        return MyDomUtils.getPsiFileByPathName(project,confName);
    }

    /**
     * 从MoquiConf文件中获取指定名称的SubScreenItem，也就是菜单入口
     * @param file MoquiConf的PsiFile
     * @param itemName SubScreenItem的name
     * @return Optional<SubScreensItem>
     */
    public static Optional<SubScreensItem> getSubScreensItem(@NotNull PsiFile file,@NotNull String itemName){
        DomFileElement<MoquiConf> moquiConfDomFileElement = MyDomUtils.convertPsiFileToDomFile(file,MoquiConf.class);
        if(moquiConfDomFileElement == null) return Optional.empty();
        return moquiConfDomFileElement.getRootElement().getScreenFacade().getScreenList().stream()
                        .flatMap(screen -> screen.getSubScreensItemList().stream())
                        .filter(item->MyDomUtils.getValueOrEmptyString(item.getName()).equals(itemName))
                        .findFirst();
    }

    /**
     * 获取和psiElement在同一个componentxia的MoquiConf中定义的root subscreenItem，名称为itemName
     * @param psiElement 当前psiElement
     * @param itemName root subscreenItem的名字
     * @return Optional<SubScreensItem>
     */
    public static Optional<SubScreensItem> getRootSubScreenItemByPsiElement(@NotNull PsiElement psiElement,@NotNull String itemName){
        Optional<String> filePathByPsiElement = MyDomUtils.getFilePathByPsiElement(psiElement);//psiElement.getContainingFile().getVirtualFile().getName();
        if(filePathByPsiElement.isEmpty()) return Optional.empty();

        Optional<PsiFile> moquiConfOptional = getMoquiConfFileByComponentFileName(psiElement.getProject(),filePathByPsiElement.get());
        if(moquiConfOptional.isEmpty()) return Optional.empty();

        return getSubScreensItem(moquiConfOptional.get(),itemName);
    }
    public static Optional<SubScreensItem> getRootSubScreenItemByName(@NotNull Project project,@NotNull String itemName){
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        return moquiIndexService.getIndexRootSubScreensItemByName(itemName)
                .map(IndexRootSubScreensItem::getSubScreensItem);
    }

    /**
     * 获取所有的Screen定义
     * 只需要取runtime/base-component和runtime/component下的定义，其他路径下的不需要
     * @param project 当前Project
     * @return  List<Screen>
     */
    public static List<Screen> getAllScreens(@NotNull Project project){
        List<DomFileElement<MoquiConf>> moquiConfList = MyDomUtils.findDomFileElementsByRootClass(project,MoquiConf.class);
        ArrayList<Screen> result = new ArrayList<>();
        for(DomFileElement<MoquiConf> moquiConf: moquiConfList) {
            ApplicationManager.getApplication().runReadAction(()->{
                String path = moquiConf.getFile().getVirtualFile().getPath();
                if(path.contains(MyStringUtils.COMPONENT_PATH_TAG) || path.contains(MyStringUtils.BASE_COMPONENT_PATH_TAG) ) {
                    result.addAll(moquiConf.getRootElement().getScreenFacade().getScreenList());
                }
            });
        }
        return result;

    }
}
