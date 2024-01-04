package org.moqui.idea.plugin;

import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.util.CustomNotifier;
import org.moqui.idea.plugin.util.DomUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyPluginClass extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        @NotNull Project project = e.getData(PlatformDataKeys.PROJECT);

        VirtualFile[] virtualFiles = ProjectRootManager.getInstance(project).getContentSourceRoots();
        //virtual files
        String sourceRoot = Arrays.stream(virtualFiles).map(VirtualFile::getUrl)
                .collect(Collectors.joining("\n"));

        // current file path
        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
        String classPath = null;
        if(psiFile != null) {
            classPath = psiFile.getVirtualFile().getPath();

        }
        //Dom
        DomManager manager = DomManager.getDomManager(project);
        Entities entities =(Entities) manager.getFileElement((XmlFile) psiFile).getRootElement();


//        DomService domService = DomService.getInstance();
//        domService.getDomFileCandidates(Entities.class, (GlobalSearchScope) project);
        List<DomFileElement<Entities>> fileElementList = DomUtils.findDomFileElementsByRootClass(project,Entities.class);
        String fileNames = fileElementList.stream().map(file -> file.getFile().getVirtualFile().getPath()).collect(Collectors.joining("\n"));

        CustomNotifier.info(project, "current entities file list ："+ "\n" +
                fileNames + "\n"
                );

//        List<Entity> entityList = entities.getEntities();
//        String entityName = entityList.stream().map(Entity::getEntityName)
//                .map(GenericValue::getValue)
//                .collect(Collectors.joining("\n"));
//        CustomNotifier.info(project, "current file contains entity："+ "\n" +
//                entityName);
//
//        List<ExtendEntity> extendEntityList = entities.getExtendEntities();
//        String extendEntityName = extendEntityList.stream().map(ExtendEntity::getEntityName)
//                .map(GenericValue::getValue)
//                .collect(Collectors.joining("\n"));
//        CustomNotifier.info(project, "current file contains extend-entity："+ "\n" +
//                extendEntityName);
//
//        List<ViewEntity> viewEntityList = entities.getViewEntities();
//        String viewEntityName = viewEntityList.stream().map(ViewEntity::getEntityName)
//                .map(GenericValue::getValue)
//                .collect(Collectors.joining("\n"));
//        CustomNotifier.info(project, "current file contains view-entity："+ "\n" +
//                viewEntityName);


        VirtualFile file = psiFile.getVirtualFile();
        ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);


        CustomNotifier.info(project, "你选中的文件路径："+classPath);

        String title = "验证IDEA插件";
        Messages.showMessageDialog(project,
                "Source roots for project:" + project.getName() +" are :" + sourceRoot + "\n" +
                "你选中的文件路径："+classPath + "\n" +
                        "Module: " + fileIndex.getModuleForFile(file) + "\n" +
                        "Is In Source: " + fileIndex.isInSource(file) + "\n" +
                        "Module content root: " + fileIndex.getContentRootForFile(file) + "\n" +
                        "Source root: " + fileIndex.getSourceRootForFile(file) + "\n" +
                        "Is library file: " + fileIndex.isLibraryClassFile(file) + "\n" +
                        "Is in library classes: " + fileIndex.isInLibraryClasses(file) +
                    ", Is in library source: " + fileIndex.isInLibrarySource(file),
                title,
                Messages.getInformationIcon());

    }
}
