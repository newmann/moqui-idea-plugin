package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Eeca;
import org.moqui.idea.plugin.dom.model.Eecas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;

public final class MyDomUtils {
    private MyDomUtils() {
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

    /**
     * 在整个项目中查找使用实体的PsiElement
     * @param project
     * @param entityFullName
     * @return
     */
    public static List<XmlAttributeValue> findEntityRelatedPsiElement(@NotNull Project project, @NotNull String entityFullName) {

        List<XmlAttributeValue> resultList = new ArrayList<>();

        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        List<DomFileElement<Eecas>> fileElementList = DomService.getInstance().getFileElements(Eecas.class, project, scope);
        fileElementList.forEach(fileElement ->{
            for(Eeca eeca :fileElement.getRootElement().getEecaList()) {
                if (eeca.getEntity().getValue().equals(entityFullName))
                        resultList.add(eeca.getEntity().getXmlAttributeValue());

            }
        });
        return resultList;
    }

    /**
     * 判断当前PsiElement是不是在属性值域
     * @param psiElement
     * @return
     */
    public static Boolean isAttributeValue(@NotNull PsiElement psiElement){
        Boolean result = false;
        if (psiElement instanceof XmlToken) {
            if(((XmlToken) psiElement).getTokenType().equals(XML_ATTRIBUTE_VALUE_TOKEN)) result = true;
        }
        return result;
    }
    public static boolean isNotAttributeValue(@NotNull PsiElement psiElement){
        return !isAttributeValue(psiElement);
    }

    /**
     * 判断当前PsiElement是不是在Tag域
     * @param psiElement
     * @return
     */
    public static Boolean isTagName(@NotNull PsiElement psiElement){
        Boolean result = false;
        if (psiElement instanceof XmlTag) {
            result = true;
        }
        return result;
    }
    public static Boolean isNotTagName(@NotNull PsiElement psiElement){
        return !isTagName(psiElement);
    }

    /**
     * 获取属性名
     * @param psiElement
     * @return
     */
    public static Optional<String> getCurrentAttributeName(@NotNull PsiElement psiElement){

        XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(psiElement, XmlAttribute.class);
        if (xmlAttribute == null ) {
            return Optional.empty();
        }
        return Optional.of( xmlAttribute.getName());

    }
    public static Optional<XmlAttribute> getCurrentAttribute(@NotNull PsiElement psiElement){
        return Optional.ofNullable(PsiTreeUtil.getParentOfType(psiElement, XmlAttribute.class));
    }
    public static Optional<String> getCurrentTagName(@NotNull PsiElement psiElement){
        XmlTag xmlTag;
        if(psiElement instanceof XmlTag) {
            xmlTag = (XmlTag) psiElement;
        }else {
            xmlTag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
        }

        if (xmlTag == null ) {
            return Optional.empty();
        }else {
            return Optional.of(xmlTag.getName());
        }

    }
    public static Optional<String> getRootTagName(@NotNull PsiElement psiElement){
        //获取RootTag名
        return getRootTagName(psiElement.getContainingFile());
//        PsiFile psiFile = psiElement.getContainingFile();
//        Optional<String> optRootTagName = getRootTagName(psiFile);
//        if (optRootTagName.isEmpty()) return Optional.empty();
//
//        return Optional.ofNullable(optRootTagName.get());
    }
    public static Optional<XmlAttribute> getCurrentAttribute(ConvertContext context) {
        final XmlElement curElement = context.getXmlElement();
        if(curElement == null) return Optional.empty();
        if(!(curElement instanceof XmlAttribute)) return Optional.empty();
        return Optional.of((XmlAttribute) curElement);
    }
    public static Optional<String> getCurrentAttributeName(ConvertContext context) {
        Optional<XmlAttribute> opCurAttribute = getCurrentAttribute(context);
        return opCurAttribute.map(XmlAttribute::getName);
    }
    public static Optional<XmlTag> getRootTag(ConvertContext context) {
        return Optional.ofNullable(context.getFile().getRootTag());
    }
    public static Optional<String> getRootTagName(ConvertContext context) {
        Optional<XmlTag> opTag = getRootTag(context);
        return opTag.map(XmlTag::getName);
    }

    public static Optional<XmlTag> getFirstParentTag(ConvertContext context) {
        return Optional.ofNullable(context.getTag());
    }
    public static Optional<String> getFirstParentTagName(ConvertContext context) {
        Optional<XmlTag> opTag = getFirstParentTag(context);
        return opTag.map(XmlTag::getName);
    }

    public static Optional<XmlTag> getSecondParentTag(ConvertContext context) {
        Optional<XmlTag> tag = getFirstParentTag(context);
        return tag.map(XmlTagChild::getParentTag);
    }
    public static Optional<String> getSecondParentTagName(ConvertContext context) {
        Optional<XmlTag> opTag = getSecondParentTag(context);
        return opTag.map(XmlTag::getName);
    }

    public static Optional<XmlTag> getThirdParentTag(ConvertContext context) {
        Optional<XmlTag> tag = getSecondParentTag(context);
        return tag.map(XmlTagChild::getParentTag);
    }
    public static Optional<String> getThirdParentTagName(ConvertContext context) {
        Optional<XmlTag> opTag = getThirdParentTag(context);
        return opTag.map(XmlTag::getName);
    }

    public static Optional<XmlTag> getFourthParentTag(ConvertContext context) {
        Optional<XmlTag> tag = getThirdParentTag(context);
        return tag.map(XmlTagChild::getParentTag);
    }
    public static Optional<String> getFourthParentTagName(ConvertContext context) {
        Optional<XmlTag> opTag = getFourthParentTag(context);
        return opTag.map(XmlTag::getName);
    }

    public static Optional<XmlTag> getFifthParentTag(ConvertContext context) {
        Optional<XmlTag> tag = getFourthParentTag(context);
        return tag.map(XmlTagChild::getParentTag);
    }
    public static Optional<String> getFifthParentTagName(ConvertContext context) {
        Optional<XmlTag> opTag = getFifthParentTag(context);
        return opTag.map(XmlTag::getName);
    }
    /**
     * 获取最近的一个指定的DomElement
     * @param context
     * @param targetClass
     * @return
     * @param <T>
     */
    public static <T extends DomElement> Optional<T> getLocalDomElementByConvertContext(@NotNull ConvertContext context, @NotNull Class<T> targetClass){
        final XmlElement curElement = context.getXmlElement();
        if(curElement == null) return Optional.empty();
        return Optional.ofNullable(DomUtil.findDomElement(curElement,targetClass));

    }
    public static <T extends DomElement> Optional<T> getLocalDomElementByPsiElement(@NotNull PsiElement psiElement, @NotNull Class<T> targetClass){
        return Optional.ofNullable(DomUtil.findDomElement(psiElement,targetClass));
    }

    public static Optional<String> getXmlAttributeValueString(XmlAttributeValue value){
        if(value == null) return Optional.empty();
        return Optional.ofNullable(value.getValue());

    }
}
