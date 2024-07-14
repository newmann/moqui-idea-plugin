package org.moqui.idea.plugin.projectViewPane;


import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class AbstractTreeStructureProvider implements TreeStructureProvider{
    @Override
    public @NotNull Collection<AbstractTreeNode<?>> modify(@NotNull AbstractTreeNode<?> parent, @NotNull Collection<AbstractTreeNode<?>> children, ViewSettings settings) {
        ArrayList<AbstractTreeNode<?>> nodes = new ArrayList<>();
        for (AbstractTreeNode<?> child : children) {
            if (child instanceof PsiFileNode psiFileNode) {
                VirtualFile file = psiFileNode.getVirtualFile();
                if(file == null) continue;


//                if (!EntityUtils.isEntitiesFile(psiFileNode.getValue())
//                        && !EntityFacadeXmlUtils.isEntityFacadeXmlFile(psiFileNode.getValue())
//                        && !EecaUtils.isEecasFile(psiFileNode.getValue())
//                ) {
//                    continue;
//                }
                if(isIncludeFile(psiFileNode.getValue())) nodes.add(child);

            }
            if(child instanceof PsiDirectoryNode directoryNode) {
                if(isBasicDirectory(directoryNode)) nodes.add(child);
                if(isSpecialDirectory(directoryNode)) nodes.add(child);
            }

        }
        return nodes;
    }
    abstract boolean isIncludeFile(PsiFile file);
    protected boolean isBasicDirectory(PsiDirectoryNode directoryNode){
        String basePath =directoryNode.getProject().getBasePath();
        if (basePath == null) return false;
        PsiDirectory psiDirectory = directoryNode.getValue();
        if(psiDirectory == null) return false;
        VirtualFile virtualFile = psiDirectory.getVirtualFile();

        final List<String> includes = List.of(
                basePath,
                basePath+"/framework",
                basePath+"/runtime",
                basePath+"/runtime/base-component",
                basePath+"/runtime/base-component/tools",
                basePath+"/runtime/base-component/webroot",
                basePath+"/runtime/component");

        final String path = virtualFile.getPath();
        if(includes.contains(path)) return true;

        return (path.matches(MyStringUtils.COMPONENT_CHILD_PATH_REGEXP)) ;

    }
    abstract boolean isSpecialDirectory(PsiDirectoryNode directoryNode);
}
