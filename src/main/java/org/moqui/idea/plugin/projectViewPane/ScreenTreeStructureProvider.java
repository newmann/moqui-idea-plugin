package org.moqui.idea.plugin.projectViewPane;

import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.moqui.idea.plugin.util.MoquiConfUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ScreenTreeStructureProvider extends AbstractTreeStructureProvider{
    @Override
    boolean isIncludeFile(PsiFile file) {
        final String path = file.getVirtualFile().getPath();
        //MoquiConf也要显示处理，因为里面有菜单入口定义
        if(MoquiConfUtils.isMoquiConfFile(file)) {
            return true;
        }else {
            return (
                    path.matches(MyStringUtils.COMPONENT_SCREEN_PATH_REGEXP)
                            || path.matches(MyStringUtils.BASE_COMPONENT_SCREEN_PATH_REGEXP)
                            || path.matches(MyStringUtils.FRAMEWORK_SCREEN_PATH_REGEXP)
                            || path.matches(MyStringUtils.FRAMEWORK_TEMPLATE_PATH_REGEXP)
                            || path.matches(MyStringUtils.COMPONENT_TEMPLATE_PATH_REGEXP)
                            || path.matches(MyStringUtils.RUNTIME_TEMPLATE_PATH_REGEXP)

            );
        }
    }

    @Override
    boolean isSpecialDirectory(PsiDirectoryNode directoryNode) {
        PsiDirectory psiDirectory = directoryNode.getValue();
        if(psiDirectory == null) return false;
        VirtualFile virtualFile = psiDirectory.getVirtualFile();
        final String path = virtualFile.getPath();

        return (
                path.matches(MyStringUtils.COMPONENT_SCREEN_PATH_REGEXP)
                || path.matches(MyStringUtils.BASE_COMPONENT_SCREEN_PATH_REGEXP)
                || path.matches(MyStringUtils.FRAMEWORK_SCREEN_PATH_REGEXP)
                || path.matches(MyStringUtils.FRAMEWORK_TEMPLATE_PATH_REGEXP)
                || path.matches(MyStringUtils.COMPONENT_TEMPLATE_PATH_REGEXP)
                || path.matches(MyStringUtils.RUNTIME_TEMPLATE_PATH_REGEXP)
        );
    }
}
