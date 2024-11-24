package org.moqui.idea.plugin.projectViewPane;

import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.RestApiUtils;
import org.moqui.idea.plugin.util.SecaUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

public class ServiceTreeStructureProvider extends AbstractTreeStructureProvider{
    @Override
    boolean isIncludeFile(PsiFile file) {
        return ServiceUtils.isServicesFile(file)
                || SecaUtils.isSecasFile(file)
                ||RestApiUtils.isRestApiFile(file) ;
    }

    @Override
    boolean isSpecialDirectory(PsiDirectoryNode directoryNode) {
        PsiDirectory psiDirectory = directoryNode.getValue();
        if(psiDirectory == null) return false;
        VirtualFile virtualFile = psiDirectory.getVirtualFile();
        final String path = virtualFile.getPath();

        return (path.matches(MyStringUtils.COMPONENT_SERVICE_PATH_REGEXP)
                || path.matches(MyStringUtils.BASE_COMPONENT_SERVICE_PATH_REGEXP)
                || path.matches(MyStringUtils.FRAMEWORK_SERVICE_PATH_REGEXP)
        );
    }
}
