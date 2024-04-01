package org.moqui.idea.plugin.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.listeners.RefactoringElementListenerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.util.CustomNotifier;
@Deprecated
public class EntityNameUpdatedRefactoringProvider implements RefactoringElementListenerProvider {
    @Override
    public @Nullable RefactoringElementListener getListener(PsiElement element) {
        if(!(element instanceof XmlAttributeValue attributeValue)) return null;

        XmlAttribute attribute = (XmlAttribute) attributeValue.getParent();

        if(attribute.getName().equals(Entity.ATTR_ENTITY_NAME)){
            return new RefactoringElementListener() {
                @Override
                public void elementMoved(@NotNull PsiElement newElement) {
                    CustomNotifier.info(element.getProject(),"Moved:" + attributeValue.getValue());
                }

                @Override
                public void elementRenamed(@NotNull PsiElement newElement) {
                    CustomNotifier.info(element.getProject(),"Rename:" + attributeValue.getValue());
                }
            };
            //CustomNotifier.info(element.getProject(),attributeValue.getValue());
        }
        return null;

    }

}

