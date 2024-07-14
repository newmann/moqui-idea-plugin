package org.moqui.idea.plugin.projectViewPane;

import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.moqui.idea.plugin.util.EecaUtils;
import org.moqui.idea.plugin.util.EntityFacadeXmlUtils;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EntityTreeStructureProvider extends AbstractTreeStructureProvider{
    @Override
    boolean isIncludeFile(PsiFile file) {

        return EntityUtils.isEntitiesFile(file)
                || EntityFacadeXmlUtils.isEntityFacadeXmlFile(file)
                ||EecaUtils.isEecasFile(file) ;
    }

    @Override
    boolean isSpecialDirectory(PsiDirectoryNode directoryNode) {
        PsiDirectory psiDirectory = directoryNode.getValue();
        if(psiDirectory == null) return false;
        VirtualFile virtualFile = psiDirectory.getVirtualFile();
        final String path = virtualFile.getPath();

        return (
                path.matches(MyStringUtils.COMPONENT_DATA_PATH_REGEXP)
                || path.matches(MyStringUtils.COMPONENT_ENTITY_PATH_REGEXP)
                || path.matches(MyStringUtils.BASE_COMPONENT_DATA_PATH_REGEXP)
                || path.matches(MyStringUtils.BASE_COMPONENT_ENTITY_PATH_REGEXP)
                        || path.matches(MyStringUtils.FRAMEWORK_DATA_PATH_REGEXP)
                        || path.matches(MyStringUtils.FRAMEWORK_ENTITY_PATH_REGEXP)

        );
    }
}
