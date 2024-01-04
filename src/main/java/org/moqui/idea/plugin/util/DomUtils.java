package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DomUtils {
    private DomUtils() {
        throw new UnsupportedOperationException();
    }
    /**
     * Find dom elements collection.
     *
     * @param <T>     the type parameter
     * @param project the project
     * @param rootClazz   the clazz
     * @return the collection
     */
    @NotNull
    @NonNls
    public static <T extends DomElement> Collection<T> findDomElementsByRootClass(@NotNull Project project, Class<T> rootClazz) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        List<DomFileElement<T>> elements = DomService.getInstance().getFileElements(rootClazz, project, scope);
        return elements.stream().map(DomFileElement::getRootElement).collect(Collectors.toList());
    }

    @NotNull
    @NonNls
    public static <T extends DomElement> List<DomFileElement<T>> findDomFileElementsByRootClass(@NotNull Project project, Class<T> rootClazz) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        return DomService.getInstance().getFileElements(rootClazz,project,scope);
    }

    /**
     * 判断是不是第一个tag，即<firstTag />
     * @param token
     * @return
     */
    public static final boolean isFirstTag(@NotNull XmlToken token){
        if(!token.getTokenType().equals(XmlTokenType.XML_NAME)) return false;

        if(!token.getNextSibling().getText().equals(" ")) return false;

        XmlToken prevSibling = (XmlToken)token.getPrevSibling();
        if(prevSibling == null) return false;
        if(!prevSibling.getTokenType().equals(XmlTokenType.XML_START_TAG_START)) return false;

        return true;
    }


    public static boolean isXmlFile(@NotNull PsiFile file){ return file instanceof XmlFile;}
    public static boolean isSpecialXmlFile(@NotNull PsiFile file,@NotNull String rootTagName){

        Boolean result = false;
        if(file != null) {
            if(isXmlFile(file)) {
                XmlTag rootTag = ((XmlFile) file).getRootTag();
                if(rootTag != null) {
                    result = rootTagName.equals(rootTag.getName());
                }
            }
        }
        return result;
    }

    public static Optional<String> getRootTagName(@NotNull PsiFile file){
        String rootTagName;
        if(! isXmlFile(file)) {
            return Optional.empty();
        }
        try {

            DomManager manager = DomManager.getDomManager(file.getProject());
            rootTagName = manager.getFileElement((XmlFile) file).getRootElement().getXmlElementName();
            return Optional.of(rootTagName);
        }catch (Exception e) {
            return Optional.empty();
        }

    }
    public static Optional<String> getRootTagNameByXmlToken(@NotNull XmlToken token) {
        return getRootTagName(token.getContainingFile());
    }
}
