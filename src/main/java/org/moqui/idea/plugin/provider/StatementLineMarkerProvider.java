package org.moqui.idea.plugin.provider;

import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Optional;

/**
 * The type Statement line marker provider.
 *
 * @author yanglin
 */
public class StatementLineMarkerProvider extends SimpleLineMarkerProvider<XmlToken, PsiElement> {
    public static final Logger logger = LoggerFactory.getLogger(StatementLineMarkerProvider.class);

    private static final String ENTITIES_CLASS = Entities.class.getSimpleName().toLowerCase();

//    private static final ImmutableSet<String> TARGET_TYPES = ImmutableSet.of(
//        ExtendEntity.class.getSimpleName().toLowerCase()
////        Insert.class.getSimpleName().toLowerCase(),
////        Update.class.getSimpleName().toLowerCase(),
////        Delete.class.getSimpleName().toLowerCase()
//    );

    @Override
    public boolean isTheElement(@NotNull PsiElement element) {
//        System.out.println(element.getContainingFile().getVirtualFile().getPath());
        if(element instanceof XmlTag) {
            System.out.println("find Tag："+ ((XmlTag) element).getName());
        }

        return element instanceof XmlToken
            && isTargetType((XmlToken) element)
            && EntityUtils.isEntitiesFile(element.getContainingFile());
    }

    @Override
    public Optional<? extends PsiElement[]> apply(@NotNull XmlToken from) {
        DomElement domElement = DomUtil.getDomElement(from);

        if (null == domElement) { return Optional.empty();  }


//        // 方法
//        else if (domElement instanceof IdDomElement) {
//            return JavaUtils.findMethods(from.getProject(),
//                MapperUtils.getNamespace(domElement),
//                MapperUtils.getId((IdDomElement) domElement));
//        }
        System.out.println(domElement.getXmlElementName());

        XmlTag xmlTag = domElement.getXmlTag();
        if (xmlTag == null) {return Optional.empty(); }

        if (ExtendEntity.TAG_NAME.equals(xmlTag.getName())) {
            return EntityUtils.findEntityByNameAndPackage(from.getProject(),
                    xmlTag.getAttributeValue(Entity.ATTR_NAME_ENTITY_NAME),
                    xmlTag.getAttributeValue(Entity.ATTR_NAME_PACKAGE));
        }

        return Optional.empty();

    }

    private boolean isTargetType(@NotNull XmlToken token) {
        Boolean result = false;

        if(token instanceof XmlTag) {
            XmlTag tag = (XmlTag) token;
            if(tag.getName().equals(ExtendEntity.TAG_NAME)) {
                result = true;
            }
        }
//        if (DomUtils.isFirstTag(token)
//                && ExtendEntity.TAG_NAME.equals(token.getText())
//        ) {
//                result = true;
//            }

        return result;

//        Boolean targetType = null;
//        if (ENTITIES_CLASS.equals(token.getText())) {
//            // 判断当前元素是开始节点
//            PsiElement nextSibling = token.getNextSibling();
//            if (nextSibling instanceof PsiWhiteSpace) {
//                targetType = true;
//            }
//        }
//        if (targetType == null) {
//            if (TARGET_TYPES.contains(token.getText())) {
//                PsiElement parent = token.getParent();
//                // 判断当前节点是标签
//                if (parent instanceof XmlTag) {
//                    // 判断当前元素是开始节点
//                    PsiElement nextSibling = token.getNextSibling();
//                    if (nextSibling instanceof PsiWhiteSpace) {
//                        targetType = true;
//                    }
//                }
//            }
//        }
//        if (targetType == null) {
//            targetType = false;
//        }
//        return targetType;

    }


    @Override
    public @Nullable("null means disabled")
    String getName() {
        return "statement line marker";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return AllIcons.Ide.Gift;
    }


    @Override
    @NotNull
    public String getTooltip(PsiElement element, @NotNull PsiElement target) {
//        String text = null;
//        if (element instanceof PsiMethod) {
//            PsiMethod psiMethod = (PsiMethod) element;
//            PsiClass containingClass = psiMethod.getContainingClass();
//            if (containingClass != null) {
//                text = containingClass.getName() + "#" + psiMethod.getName();
//            }
//        }
//        if (text == null && element instanceof PsiClass) {
//            PsiClass psiClass = (PsiClass) element;
//            text = psiClass.getQualifiedName();
//        }
//        if (text == null) {
//            text = target.getContainingFile().getText();
//        }
        return "Goto source entity.";
    }

}
