package org.moqui.idea.plugin.util;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Component;

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
                String pathName = fileElement.getFile().getVirtualFile().getCanonicalPath();
                if(pathName == null) return Optional.empty();
                return  Optional.of(MyStringUtils.getParentPath(pathName));

//                String pathName = MyDomUtils.getFilePathByPsiElement(fileElement.getRootElement().getXmlTag()).orElse(MyStringUtils.EMPTY_STRING);
//                if(MyStringUtils.isEmpty(pathName)) {
//                    return Optional.empty();
//                }
//                String path = pathName.substring(0,pathName.lastIndexOf(MyStringUtils.PATH_SEPARATOR));
//                return Optional.of(path);
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

    public static List<VirtualFile> getChildVirtualFileByLocation(@NotNull Project project,@NotNull Location location) {
        List<VirtualFile> result = new ArrayList<>();
        if(location.getType() != LocationType.ComponentFile && location.getType() != LocationType.ComponentFileContent) {
            //如果不是component file，则直接返回空
            return result;
        }
        //根据component name 获取实际路径
        if(location.getPathNameArray().length< 1) return result;
        String componentName = location.getPathNameArray()[0];
        String componentPath = getComponentPathByName(project,componentName).orElse(null);

        if(componentPath == null) return result;
        String fileName = componentPath + location.getPathPart().substring(componentName.length());
        VirtualFile virtualFile = ReadAction.compute(()->LocalFileSystem.getInstance().findFileByPath(fileName)) ;

        if(virtualFile == null) return result;
        if(virtualFile.isDirectory()) {
            //如果是目录，则获取所有子文件
            VirtualFile[] children = virtualFile.getChildren();
            result.addAll(Arrays.asList(children));
        }
        //如果是文件，则直接返回空

        return result;
    }

    /**
     * 根据Location获取对应的PsiFile
     * Location的type必须是ComponentFile或ComponentFileContent
     * @param location Location对象
     * @return Optional<PsiFile>
     */
    public static Optional<PsiFile> getPsiFileByLocation(@NotNull Location location){
        if(location.getType() != LocationType.ComponentFile && location.getType() != LocationType.ComponentFileContent) {
            //如果不是component file，则直接返回空
            return Optional.empty();
        }

        //如果是component file，则直接获取文件
        String componentPath = getComponentPathByName(location.getProject(), location.getPathNameArray()[0]).orElse(null);
        if(componentPath == null) return Optional.empty();
        String fileName = componentPath  + location.getPathPart().substring(location.getPathNameArray()[0].length());

//                         GlobalSearchScope scope = GlobalSearchScope.allScope(project);
//                         Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName( fileName, true, scope);
//                         if(foundFileCollection.isEmpty()) return Optional.empty();
//                         PsiFile targetFile = PsiManager.getInstance(project).findFile(foundFileCollection.);
//                         return Optional.ofNullable(targetFile);

        // 使用 LocalFileSystem 来找到 VirtualFile
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileName);
        if (virtualFile != null) {
            // 使用 PsiManager 来获取 PsiFile
            PsiManager psiManager = PsiManager.getInstance(location.getProject());
            return Optional.ofNullable(psiManager.findFile(virtualFile));
        }else {
            return Optional.empty();
        }

    }
}
