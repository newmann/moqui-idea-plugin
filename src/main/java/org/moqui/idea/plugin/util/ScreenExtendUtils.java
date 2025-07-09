package org.moqui.idea.plugin.util;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.mac.MacPathChooserDialog;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.dom.model.ScreenExtend;

import java.util.*;


public final class ScreenExtendUtils {
    private ScreenExtendUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isScreenExtendFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, ScreenExtend.TAG_NAME,ScreenExtend.ATTR_NoNamespaceSchemaLocation,ScreenExtend.VALUE_NoNamespaceSchemaLocation);
    }
    /**
     * 根据所有的ScreenExtend文件
     * @param project 当前项目
     * @return Collection<String，Component> String 为文件的绝对路径
     */
    public static Map<String,PsiFile> findAllScreenExtendPsiFile(@NotNull Project project){

        List<DomFileElement<ScreenExtend>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, ScreenExtend.class);
        Map<String,PsiFile> result = new HashMap<>();
        for(DomFileElement<ScreenExtend> fileElement : fileElementList) {
            PsiFile psiFile = ReadAction.compute(() -> fileElement.getFile().getContainingFile());
            String name = psiFile.getVirtualFile().getPath();
            result.put(name,psiFile);
        }
        return result;


    }
    /**
     * 根据location获取ScreenExtend的PsiFile
     * location的格式应该是类似于component://componentName/directory/。。
     *
     * 由于ScreenExtend文件比较少，所以先将所有的ScreenExtend的PsiFile都获取出来，通过文件名来判断是否是对应的ScreenExtend文件。
     * @param location
     * @return
     */

    public static List<PsiFile> getScreenExtendPsiFileByLocation(@NotNull Project project, @NotNull String location){
        List<PsiFile> result = new ArrayList<>();

        Location loc = Location.of(project,location);
        if(loc.getType()!= LocationType.ComponentFile) return result;
        String[] pathArray = location.split("/screen/");
        if(pathArray.length != 2) return result; //如果没有screen的路径，则返回空
        String fileName = pathArray[1];

        Map<String, PsiFile> psiFileMap = findAllScreenExtendPsiFile(project);

        for(String componentName: psiFileMap.keySet()) {
            if(componentName.endsWith(fileName))  result.add(psiFileMap.get(componentName));
        }
        return result;
    }
}
