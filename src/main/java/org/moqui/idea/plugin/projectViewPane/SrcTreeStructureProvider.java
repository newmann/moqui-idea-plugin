package org.moqui.idea.plugin.projectViewPane;

import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.moqui.idea.plugin.util.MyStringUtils;

public class SrcTreeStructureProvider extends AbstractTreeStructureProvider{
    @Override
    boolean isIncludeFile(PsiFile file) {
        final String path = file.getVirtualFile().getPath();
        return (
                path.matches(MyStringUtils.COMPONENT_SRC_PATH_REGEXP)
                        || path.matches(MyStringUtils.BASE_COMPONENT_SRC_PATH_REGEXP)
                        || path.matches(MyStringUtils.FRAMEWORK_SRC_PATH_REGEXP)
        );
    }

    @Override
    boolean isSpecialDirectory(PsiDirectoryNode directoryNode) {
        PsiDirectory psiDirectory = directoryNode.getValue();
        if(psiDirectory == null) return false;
        VirtualFile virtualFile = psiDirectory.getVirtualFile();
        final String path = virtualFile.getPath();

        return (
                path.matches(MyStringUtils.COMPONENT_SRC_PATH_REGEXP)
                || path.matches(MyStringUtils.BASE_COMPONENT_SRC_PATH_REGEXP)
                || path.matches(MyStringUtils.FRAMEWORK_SRC_PATH_REGEXP)
        );
    }
}
