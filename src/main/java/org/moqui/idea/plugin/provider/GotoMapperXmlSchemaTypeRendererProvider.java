package org.moqui.idea.plugin.provider;

import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.codeInsight.navigation.GotoTargetHandler;
import com.intellij.codeInsight.navigation.GotoTargetRendererProvider;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.NotNull;
@Deprecated
public class GotoMapperXmlSchemaTypeRendererProvider implements GotoTargetRendererProvider {
    @Override
    public PsiElementListCellRenderer getRenderer(@NotNull PsiElement element, @NotNull GotoTargetHandler.GotoData gotoData) {
        if (element instanceof XmlTagImpl) {
            if (EntityUtils.isEntitiesFile(element.getContainingFile())) {
                return new MyRenderer();
            }
        }
        return null;
    }

    public static class MyRenderer extends PsiElementListCellRenderer<XmlTagImpl> {

        @Override
        public String getElementText(XmlTagImpl element) {
            XmlAttribute attr = element.getAttribute("entity-name", XmlUtil.XML_SCHEMA_URI);
            attr = attr == null ? element.getAttribute("entity-name") : attr;
            return (attr == null || attr.getValue() == null ? element.getName() : attr.getValue());
        }

        @Override
        protected String getContainerText(XmlTagImpl element, String name) {
            final PsiFile file = element.getContainingFile();
//            String databaseId = getDatabaseId(element);
//            return databaseId + file.getVirtualFile().getName();
            return file.getVirtualFile().getName();
        }
//
//        @NotNull
//        private String getDatabaseId(XmlTagImpl element) {
//            final XmlAttribute databaseIdAttr = element.getAttribute("databaseId");
//            String databaseId = null;
//            if (databaseIdAttr != null) {
//                databaseId = databaseIdAttr.getValue() + ",";
//            }
//            if (databaseId == null) {
//                databaseId = "";
//            }
//            return databaseId;
//        }

        @Override
        protected int getIconFlags() {
            return 0;
        }
    }
}
