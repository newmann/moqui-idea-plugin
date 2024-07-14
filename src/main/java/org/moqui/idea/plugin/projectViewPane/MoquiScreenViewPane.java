package org.moqui.idea.plugin.projectViewPane;


import com.intellij.ide.SelectInTarget;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import icons.MoquiIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoquiScreenViewPane extends ProjectViewPane {
    @NonNls
    public static final String ID = "MoquiScreenProjectView";
    public MoquiScreenViewPane(Project project) {
        super(project);
    }

    @Override
    protected @NotNull ProjectAbstractTreeStructureBase createStructure() {
        ProjectAbstractTreeStructureBase result = super.createStructure();
        result.setProviders(new ScreenTreeStructureProvider());
        return result;
    }

    @Override
    public @NotNull SelectInTarget createSelectInTarget() {

        return new MoquiViewPaneSelectInTarget(myProject,ID);
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getTitle() {
        return "Moqui Screen";
    }

    @Override
    public @NotNull Icon getIcon() {
        return MoquiIcons.ScreenTag;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public int getWeight() {
        return 101;
    }


//    protected static class ScreenTreeStructureProvider implements TreeStructureProvider{
//
//        @Override
//        public @NotNull Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent, @NotNull Collection<AbstractTreeNode<?>> children, ViewSettings settings) {
//            ArrayList<AbstractTreeNode<?>> nodes = new ArrayList<>();
//            for (AbstractTreeNode<?> child : children) {
//                if (child instanceof PsiFileNode psiFileNode) {
//                    VirtualFile file = psiFileNode.getVirtualFile();
//                    if(file == null) continue;
//                    final String path = file.getPath();
//                    if (path.matches(MyStringUtils.COMPONENT_SCREEN_PATH_REGEXP)
//                            || path.matches(MyStringUtils.BASE_COMPONENT_SCREEN_PATH_REGEXP)
//                            || path.matches(MyStringUtils.FRAMEWORK_SCREEN_PATH_REGEXP)
//                    ){
//                        nodes.add(child);
//                    }
//                }
//                if(child instanceof PsiDirectoryNode directoryNode) {
//                    if(shouldInclude(directoryNode)) nodes.add(child);
//                }
//
//            }
//            return nodes;
//        }
//
//        private boolean shouldInclude(PsiDirectoryNode directoryNode){
//            final List<String> includes = List.of("framework","runtime","screen","base-component","component","tools","webroot");
//
//            String basePath =directoryNode.getProject().getBasePath();
//            if (basePath == null) return false;
//
//            PsiDirectory psiDirectory = directoryNode.getValue();
//            if(psiDirectory == null) return false;
//
//            VirtualFile virtualFile = psiDirectory.getVirtualFile();
//            final String directoryName = virtualFile.getName();
//            if(includes.contains(directoryName)) return true;
//
//            final String path = virtualFile.getPath();
//            if(path.equals(basePath)) return true;
//
//            if(path.matches(MyStringUtils.COMPONENT_CHILD_PATH_REGEXP)) return true;
//
//            return (path.matches(MyStringUtils.COMPONENT_SCREEN_PATH_REGEXP)
//                    || path.matches(MyStringUtils.BASE_COMPONENT_SCREEN_PATH_REGEXP)
//                    || path.matches(MyStringUtils.FRAMEWORK_SCREEN_PATH_REGEXP)
//            );
//
//        }
//    }

}
