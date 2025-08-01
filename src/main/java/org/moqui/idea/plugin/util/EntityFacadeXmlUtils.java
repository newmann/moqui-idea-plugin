package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.TextTemplateReference;
import org.moqui.idea.plugin.service.MoquiIndexService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class EntityFacadeXmlUtils {
    private EntityFacadeXmlUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isEntityFacadeXmlFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, EntityFacadeXml.TAG_NAME);
    }

    /**
     * 通过 MyDomUtils.findDomFileElementsByRootClass(project,EntityFacadeXml.class)查询不到EntityFacadeXml file，
     * 具体原因未明，改用本过程获取
     */
    public static List<PsiFile> getAllEntityFacadeXmlFileList(@NotNull Project project){
        String basePath = project.getBasePath();
        if(basePath == null) return new ArrayList<>();

//        List<VirtualFile> searchScope =  LocationUtils.getAllDataDirectoryList(project);
//        Collection<VirtualFile> fileCollection = FilenameIndex.getAllFilesByExt(project,"xml",
//                GlobalSearchScope.filesScope(project, searchScope));
        List<VirtualFile> fileList = LocationUtils.getAllDataFileList(project);
        return fileList
                .stream()
                .map(virtualFile -> PsiManager.getInstance(project).findFile(virtualFile))
                .filter(EntityFacadeXmlUtils::isEntityFacadeXmlFile)
                .toList();
    }
    /**
     * 获取定义在moqui.basic.LocalizedMessage值，还要包括LocalizedMessage，没有别名
     */
    public static Map<String, XmlTag> getTextTemplateFromFile(@NotNull PsiFile file){
        Map<String,XmlTag> result;
        if(isEntityFacadeXmlFile(file)) {
            XmlFile xmlFile = (XmlFile) file;
            List<XmlTag> xmlTagList = PsiTreeUtil.getChildrenOfTypeAsList(xmlFile.getRootTag(), XmlTag.class);
            xmlTagList = xmlTagList.stream()
                    .filter(it -> isLocalizedMessageTag(it.getName()))
                    .toList();
            xmlTagList = xmlTagList.stream()
                    .filter(it -> MyDomUtils.getValueOrEmptyString(it.getAttribute(MyStringUtils.LocalizedMessage_Field_Local)).equals("default"))
                    .toList();

            result =  xmlTagList.stream()
//                    .filter(it -> it.getName().equals("moqui.basic.LocalizedMessage") || it.getName().equals("LocalizedMessage"))
//                    .filter(it -> MyDomUtils.getValueOrEmptyString(it.getAttribute(LocalizedMessage_Field_Local)).equals("default"))
                    .collect(Collectors.toMap((xmlTag -> MyDomUtils.getValueOrEmptyString(xmlTag.getAttribute(MyStringUtils.LocalizedMessage_Field_Original))), Function.identity(), (n1, n2) -> n1));

        }else {
            result = new HashMap<>();
        }
        return result;
    }

    public static boolean isLocalizedMessageTag(String tagName){
        if(MyStringUtils.isEmpty(tagName)) return false;
        return tagName.equals("moqui.basic.LocalizedMessage") || tagName.equals("LocalizedMessage");
    }

    public static  Map<String, XmlTag> getAllTextTemplateMap(@NotNull Project project){
        return project.getService(MoquiIndexService.class).getTextTemplateMap();
    }

    /**
     * 创建TextTemplate对应的TextTemplateReference
     * @param element 待处理的PsiElement
     * @return PsiReference[]
     */

    public static   @NotNull PsiReference[] createTextTemplateReferences(@NotNull PsiElement element) {
        String textContent;
        if(element instanceof XmlAttributeValue xmlAttributeValue){
            textContent = xmlAttributeValue.getValue();
        }else {
            return PsiReference.EMPTY_ARRAY;
        }
        if(MyStringUtils.isEmpty(textContent)) return PsiReference.EMPTY_ARRAY;

        List<PsiReference> psiReferences = new ArrayList<>();

        XmlTag xmlTag = getAllTextTemplateMap(element.getProject()).get(textContent);

        if((xmlTag != null) ){
            XmlAttribute targetAttribute = xmlTag.getAttribute(MyStringUtils.LocalizedMessage_Field_Original);
            if(targetAttribute != null) {
                XmlAttributeValue targetAttributeValue = targetAttribute.getValueElement();
                if(targetAttributeValue != null) {
                    psiReferences.add(TextTemplateReference.of(element,
                            new TextRange(1,1+textContent.length()), targetAttributeValue));
                }
            }
        }
        return psiReferences.toArray(new PsiReference[0]);

    }

    /**
     * 判断当前的Tag是否为rootTag，
     * EntityFacade可以在data文件中（rootTag为entity-facade-xml），
     * 也可以在Entity定义文件中（rootTag为seed-data），
     * @param xmlTag 当前的XmlTag
     * @return boolean
     */
    public static boolean isNotEntityFacadeRootTag(@NotNull XmlTag xmlTag){
        return !isEntityFacadeRootTag(xmlTag);

    }
    public static boolean isEntityFacadeRootTag(@NotNull XmlTag xmlTag){
        String tagName = xmlTag.getName();
        return tagName.equals(EntityFacadeXml.TAG_NAME) || tagName.equals(SeedData.TAG_NAME);
    }
    /**
     * 判断当前的Tag是否为数据定义的Tag，用于EntityFacadeTagNameProvider
     * 1、在entity-facade-xml下都是定义数据的
     * 2、在Entity下面的seed-data部分也是定义数据的
     * @param xmlTag 当前XmlTag
     * @return boolean
     */
    public static boolean isEntityFacadeDefineTag(@NotNull XmlTag xmlTag) {

        Optional<String> rootNameOptional = MyDomUtils.getRootTagName(xmlTag.getContainingFile());
        if (rootNameOptional.isPresent() && rootNameOptional.get().equals(EntityFacadeXml.TAG_NAME)) {
            return true;
        }else{
            return MyDomUtils.getParentXmlTagByTagName(xmlTag,SeedData.TAG_NAME).isPresent();
        }
    }

    public static void putFacadeEntityToXmlTag(@NotNull XmlTag xmlTag,EntityFacadeXmlTagDescriptor descriptor) {
        xmlTag.putUserData(MyStringUtils.MOQUI_FACADE_ENTITY_KEY,descriptor);
    }
    public static  Optional<EntityFacadeXmlTagDescriptor> getFacadeEntityFromXmlTag(@NotNull XmlTag xmlTag) {
        Object object =  xmlTag.getUserData(MyStringUtils.MOQUI_FACADE_ENTITY_KEY);

        if(object instanceof EntityFacadeXmlTagDescriptor refData ) {
            if(refData.entityIsValid()) return Optional.of(refData);
        }

        return Optional.empty();
    }
}
