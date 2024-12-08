package org.moqui.idea.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.contributor.EntityFacadeXmlReferenceContributor;
import org.moqui.idea.plugin.provider.EntityFacadeXmlReferenceProvider;


public class MyPluginClass extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if(project == null) return;
        StringBuilder sb = new StringBuilder();
//        Project[] projects = ProjectManager.getInstance().getOpenProjects();
//        for (Project tmpP: projects) {
//            sb.append(tmpP.getBasePath());
//            sb.append("\n");
//        }

        String projectName = project.getName();
        sb.append("Project Name: ");
        sb.append(projectName);
//        sb.append("\n");
//        sb.append("Base Path:" + project.getBasePath());
//        sb.append("\n");
//        sb.append("Project File Path:" + project.getProjectFilePath());
//        sb.append("\n");
//        sb.append("Location Hash:" + project.getLocationHash());
//        sb.append("\n");
//
//        VirtualFile[] vFiles = ProjectRootManager.getInstance(project)
//                .getContentSourceRoots();
//        String sourceRootsList = Arrays.stream(vFiles)
//                .map(VirtualFile::getUrl)
//                .collect(Collectors.joining("\n"));
//        sb.append(sourceRootsList);
//
//        Module[] modules = ModuleManager.getInstance(project).getModules();
//        String moduleList = Arrays.stream(modules)
//                .map(Module::getName)
//                .collect(Collectors.joining("\n"));
//        sb.append(moduleList);
//        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
////        toolWindowManager.getToolWindow("Project").activate(null);
//        sb.append("ToolsWindowSet:");
//        sb.append("\n");
//        sb.append(toolWindowManager.getToolWindowIdSet());



//        PsiFile[] psiFiles = PsiSearchHelper.getInstance(project).findFilesWithPlainTextWords("mantle.product.asset.Asset");
//        StringBuilder sb = new StringBuilder();
//        for (PsiFile psiFile: psiFiles) {
//            sb.append(psiFile.getVirtualFile().getPath());
//            sb.append("\n");
//        }

//        PsiFile psiFile = e.getData(PlatformDataKeys.PSI_FILE);
//        if(psiFile!= null) {
//            List<GrLiteral> strings = PsiTreeUtil.findChildrenOfAnyType(psiFile, GrLiteral.class)
//                    .stream()
//                    .filter(ENTITY_CALL::accepts)
//                    .toList();
//            for(PsiElement literal: strings) {
//                sb.append(literal.getText()).append("\n");
////                if(literal.getType()!= null) sb.append(literal.getType().toString()).append("\n");
//            }
//
//        }
//
//
//        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
//
//        Boolean isScreenFile = ScreenUtils.isScreenFile(file);
//
//        DomFileElement<Screen> screen = DomManager.getDomManager(project).getFileElement((XmlFile) file);


//        String messageStr = "Source roots for project:" + project.getName() +" are :"
//                + String.valueOf(screen.getRootElement().getTransitionList().size())
//                + sourceRoot + "\n" +
//                "你选中的文件路径："+classPath + "\n" +
//                "Module: " + fileIndex.getModuleForFile(file) + "\n" +
//                "Is In Source: " + fileIndex.isInSource(file) + "\n" +
//                "Module content root: " + fileIndex.getContentRootForFile(file) + "\n" +
//                "Source root: " + fileIndex.getSourceRootForFile(file) + "\n" +
//                "Is library file: " + fileIndex.isLibraryClassFile(file) + "\n" +
//                "Is in library classes: " + fileIndex.isInLibraryClasses(file) +
//                ", Is in library source: " + fileIndex.isInLibrarySource(file)
//                ;

        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if(editor==null) return;
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        if(psiFile == null) return;

        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement psiElement = psiFile.findElementAt(caretOffset);

        if(EntityFacadeXmlReferenceContributor.ENTITY_FACADE_TAG_PATTERN.accepts(psiElement)) {
            sb.append("符合ENTITY_FACADE_TAG_PATTERN");
        }else {
            sb.append("不符合ENTITY_FACADE_TAG_PATTERN");
        }

        String messageStr ="插件已安装，可以正常使用。";

        String title = "验证Moqui Idea Plugin";
        Messages.showMessageDialog(project,
                sb.toString(),
                title,
                Messages.getInformationIcon());

    }
}
