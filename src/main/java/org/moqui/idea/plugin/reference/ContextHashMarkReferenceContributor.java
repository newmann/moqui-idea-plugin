package org.moqui.idea.plugin.reference;

import com.intellij.psi.xml.XmlTokenType;
import org.moqui.idea.plugin.dom.model.Services;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
@Deprecated
public class ContextHashMarkReferenceContributor extends PsiReferenceContributor {
    private static final String ENTITY_NAME_PREFIX_STR = "entity-name";
    public static final PsiElementPattern.Capture<XmlAttribute> PATTERN_ATTR_ENTITY_NAME = PlatformPatterns.psiElement(XmlAttribute.class)
            .withText(StandardPatterns.string().startsWith(ENTITY_NAME_PREFIX_STR));

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
//        System.out.println("in registerReferenceProviders");
        registrar.registerReferenceProvider(PATTERN_ATTR_ENTITY_NAME,
                new PsiReferenceProvider() {
                    @Override
                    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        if(!(element instanceof XmlAttribute)) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        XmlFile file = (XmlFile) element.getContainingFile();
                        if(!file.getRootTag().getName().equals(Services.TAG_NAME)) {
                            System.out.println("RootTag is:" + file.getRootTag().getName());
                            System.out.println("Current file:" + file.getVirtualFile().getPath()+" is not Services Xml file");
                            return PsiReference.EMPTY_ARRAY;
                        }

                        XmlAttribute xmlElement = (XmlAttribute) element;
//                        System.out.println("text:" + literalExpression.getText());
//                        System.out.println("value:"+ literalExpression.getValue());
//                        System.out.println("name:"+ literalExpression.getName());
                        String elementValue = xmlElement.getValue();
                        String elementText = xmlElement.getText();
                        if (elementText == null ) {
                            return PsiReference.EMPTY_ARRAY;
                        }else{
                            TextRange property = new TextRange(elementText.indexOf(elementValue),
                                    elementText.length() - 1);
                            return new PsiReference[]{new EntityReference(element, property)};
                        }

                    }
                });

    }
}
