package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.service.MoquiIndexService;

import java.util.Set;
import java.util.*;

import static com.intellij.psi.xml.XmlTokenType.*;
import static org.moqui.idea.plugin.util.MyStringUtils.isNotEmpty;

public final class MyDomUtils {

    public static Map<String,String> MOQUI_XML_FILE_ROOT_TAGS = new HashMap<>(Map.of(
            Entities.TAG_NAME,Entities.VALUE_NoNamespaceSchemaLocation,
            Services.TAG_NAME,Services.VALUE_NoNamespaceSchemaLocation,
            Screen.TAG_NAME,Screen.VALUE_NoNamespaceSchemaLocation,
            Eecas.TAG_NAME, Eecas.VALUE_NoNamespaceSchemaLocation,
            Secas.TAG_NAME, Secas.VALUE_NoNamespaceSchemaLocation,
            Emecas.TAG_NAME, Emecas.VALUE_NoNamespaceSchemaLocation,
            Component.TAG_NAME, Component.VALUE_NoNamespaceSchemaLocation,
            MoquiConf.TAG_NAME,MoquiConf.VALUE_NoNamespaceSchemaLocation,
            Resource.TAG_NAME, Resource.VALUE_NoNamespaceSchemaLocation,
            EntityFacadeXml.TAG_NAME, MyStringUtils.EMPTY_STRING
    ));


    private static final Logger LOGGER = Logger.getInstance(MyDomUtils.class);

    private MyDomUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T extends DomElement> DomFileElement<T> convertPsiFileToDomFile(@NotNull PsiFile file, Class<T> rootClazz) {

        return ApplicationManager.getApplication().runReadAction((Computable<DomFileElement<T>>) ()->{
            DomFileElement<T> result = null;
            if(file instanceof XmlFile xmlFile) {
                result = DomManager.getDomManager(file.getProject()).getFileElement(xmlFile,rootClazz);
            }
            return  result;
        });

    }

    @NotNull
    public static <T extends DomElement> List<DomFileElement<T>> findDomFileElementsByRootClass(@NotNull Project project, Class<T> rootClazz) {

        return ApplicationManager.getApplication().runReadAction((Computable<List<DomFileElement<T>>>) ()->{
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
//            GlobalSearchScope scope = GlobalSearchScope.projectScope(project);//仅搜索当前项目的源码文件（不包括依赖库）
            try {
                return DomService.getInstance().getFileElements(rootClazz, project, scope);
            }catch (ProcessCanceledException e) {
                throw e;
            }catch (Exception e) {
                // 处理可能发生的异常
                LOGGER.error(e.getMessage(),e);
//                e.printStackTrace();
                return new ArrayList<>();
            }
        });


    }
    /**
     * 获取指定文件夹下面的所有文件和子目录
     * @param project 当前项目
     * @param path 指定的文件夹
     * @return List<PsiElement>
     */
    @NotNull
    public static List<PsiElement> findPathAndFileListByPath(@NotNull Project project, @NotNull String path){

        return  ApplicationManager.getApplication().runReadAction((Computable<ArrayList<PsiElement>>) ()->{
            ArrayList<PsiElement> result = new ArrayList<>();
            PsiManager psiManager = PsiManager.getInstance(project);

            VirtualFile virtualDirectory = LocalFileSystem.getInstance().findFileByPath(path);
            if(virtualDirectory == null || !virtualDirectory.isDirectory()) {
                return result;
            }

            PsiDirectory psiDirectory = psiManager.findDirectory(virtualDirectory);
            if(psiDirectory != null) {
                result.addAll(List.of(psiDirectory.getFiles()));
                result.addAll(List.of(psiDirectory.getSubdirectories()));
            }
            return result;
        });

    }

    /**
     * 获取指定文件夹下面的所有文件
     * @param project 当前项目
     * @param path 指定的文件夹
     * @return List<PsiFile>
     */
    @NotNull
    public static List<PsiFile> findPsiFileListByPath(@NotNull Project project, @NotNull String path){

        return  ApplicationManager.getApplication().runReadAction((Computable<ArrayList<PsiFile>>) ()->{
            ArrayList<PsiFile> result = new ArrayList<>();
            PsiManager psiManager = PsiManager.getInstance(project);

            VirtualFile virtualDirectory = LocalFileSystem.getInstance().findFileByPath(path);
            if(virtualDirectory == null || !virtualDirectory.isDirectory()) {
                return result;
            }

            PsiDirectory psiDirectory = psiManager.findDirectory(virtualDirectory);
            if(psiDirectory != null) result.addAll(List.of(psiDirectory.getFiles()));
            return result;
        });

    }
    /**
     * 获取指定文件夹下面的所有子目录文件
     * @param project 当前项目
     * @param path 指定的文件夹
     * @return List<PsiDirectory>
     */
    @NotNull
    public static List<PsiDirectory> findPsiDirectoryListByPath(@NotNull Project project, @NotNull String path){

        return  ApplicationManager.getApplication().runReadAction((Computable<ArrayList<PsiDirectory>>) ()->{
            ArrayList<PsiDirectory> result = new ArrayList<>();
            PsiManager psiManager = PsiManager.getInstance(project);

            VirtualFile virtualDirectory = LocalFileSystem.getInstance().findFileByPath(path);
            if(virtualDirectory == null || !virtualDirectory.isDirectory()) {
                return result;
            }

            PsiDirectory psiDirectory = psiManager.findDirectory(virtualDirectory);
            if(psiDirectory != null) result.addAll(List.of(psiDirectory.getSubdirectories()));
            return result;
        });

    }

    /**
     * 判断当前的项目是否为Moqui项目，判断跟就是项目的根目录下个是否存在MoquiInit.properties这个文件
     * 通过MoquiIndexService在项目启动时中一次性判断，不用每次判断，以便提高性能
     * @param project 当前project
     * @return 是否为Moqui项目
     */
    public static boolean isMoquiProject(@NotNull Project project){
        return project.getService(MoquiIndexService.class).isMoquiProject();

//        String baseDir = project.getBasePath();
//        if(baseDir == null) return false;
//
//        VirtualFile specificFile = ReadAction.compute(()->LocalFileSystem.getInstance().findFileByNioFile(Path.of(baseDir,"MoquiInit.properties")));
//        return specificFile != null;

    }

    /**
     * 根据当前的PsiElement判断所属哪个文件
     * @param psiElement 当前PsiElement
     * @param rootTagName 根Tag名称
     * @return boolean
     */
    public static boolean isSpecialXmlFile(@NotNull PsiElement psiElement,@NotNull String rootTagName){
        PsiFile file = psiElement.getContainingFile();
        if(file == null) {
            return false;
        }else {
            return isSpecialXmlFile(file,rootTagName,null,null);
        }

    }

    public static Optional<PsiFile> getPsiFileFromVirtualFile(@NotNull Project project, @Nullable VirtualFile virtualFile){

        if(virtualFile != null && virtualFile.isInLocalFileSystem()) {
            PsiFile psiFile = ReadAction.compute(() ->  PsiManager.getInstance(project).findFile(virtualFile));
            return Optional.ofNullable(psiFile);
        }
        return Optional.empty();
    }
    public static boolean isSpecialXmlFile(@NotNull PsiFile file,@NotNull String rootTagName){
        return isSpecialXmlFile(file,rootTagName,null,null);
    }
    public static boolean isSpecialXmlFile(@NotNull PsiFile file,@NotNull String rootTagName,@Nullable String attributeName,@Nullable String attributeValue){
        return ReadAction.compute(()->{
            if(file instanceof XmlFile xmlFile) {
//                VirtualFile virtualFile = xmlFile.getVirtualFile();
//                if(virtualFile != null && virtualFile.isInLocalFileSystem()) { //判断文件是否已经加载
//                XmlTag rootTag = ReadAction.compute(xmlFile::getRootTag);
                    XmlTag rootTag = xmlFile.getRootTag();
                    if (rootTag == null) return false;
                    if(attributeName == null) {
                        return rootTagName.equals(rootTag.getName());
                    }else {
                        if (!rootTagName.equals(rootTag.getName())) return false;
                        String value = rootTag.getAttributeValue(attributeName);
                        return (value != null) && value.equals(attributeValue);
                    }
//                }
            }
            return false;
        });

    }

    public static boolean isMoquiXmlFile(@NotNull PsiFile file){
        if(file instanceof XmlFile xmlFile) {
//            VirtualFile virtualFile = xmlFile.getVirtualFile();
//            if(virtualFile != null && virtualFile.isInLocalFileSystem()) { //判断文件是否已经加载
                XmlTag rootTag = ReadAction.compute(xmlFile::getRootTag);
                if (rootTag == null) return false;
                if (!MOQUI_XML_FILE_ROOT_TAGS.containsKey(rootTag.getName())) return false;
                String value = rootTag.getAttributeValue(MyStringUtils.MOQUI_XML_FILE_ROOT_TAG_ATTR_NoNamespaceSchemaLocation);
                if (value == null) value = MyStringUtils.EMPTY_STRING;
                return MOQUI_XML_FILE_ROOT_TAGS.containsValue(value);
//            }
        }
        return false;
    }

    /**
     * 按指定名称找到找到当前XmlTag的父级XmlTag
     * @param xmlTag 当前XmlTag
     * @param tagName 父级XmlTag的名称
     * @return   Optional<XmlTag>
     */
    public static Optional<XmlTag> getParentXmlTagByTagName(@NotNull XmlTag xmlTag,@NotNull String tagName){
        XmlTag parentXmlTag = xmlTag.getParentTag();
        while(parentXmlTag != null){
            if(parentXmlTag.getName().equals(tagName)){
                return Optional.of(parentXmlTag);
            }else{
                parentXmlTag = parentXmlTag.getParentTag();
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getRootTagName(@NotNull PsiFile file){
        if(file instanceof XmlFile xmlFile) {
            try {
                XmlTag rootTag = ReadAction.compute(xmlFile::getRootTag);
                if(rootTag == null) return Optional.empty();
                return Optional.of(rootTag.getName());
            } catch (Exception e) {
                return Optional.empty();
            }
        }else{
            return Optional.empty();
        }

    }

    /**
     * 从当前的xmlToken(type = XML_ATTRIBUTE_VALUE_TOKEN）获取下一个AttributeValue,如果是最后一个AttributeValue，则跳到最前面一个
     * @param xmlToken 传入的XmlToken
     * @return Optional<XmlAttributeValue>
     */
    public static Optional<XmlAttributeValue> getSiblingAttributeValue(@NotNull XmlToken xmlToken){
        if(xmlToken.getTokenType() == XML_ATTRIBUTE_VALUE_TOKEN || xmlToken.getTokenType() == XML_ATTRIBUTE_VALUE_END_DELIMITER) {
            PsiElement curAttributeValueElement = xmlToken.getParent();
            PsiElement curAttributeElement = curAttributeValueElement.getParent();
            PsiElement siblingElement = curAttributeElement.getNextSibling();
            while(true){
                if(siblingElement == null) return Optional.empty();
                if(siblingElement instanceof XmlToken xmlTokenEnd) {
                    if(xmlTokenEnd.getTokenType() == XML_TAG_END || xmlTokenEnd.getTokenType() == XML_EMPTY_ELEMENT_END) {
                        //回到第一个attribute value
                        if(curAttributeElement.getParent() instanceof XmlTag xmlTag) {
                            return getTagFirstAttributeValue(xmlTag);
                        }else {
                            return Optional.empty();
                        }
                    }
                }
                if(siblingElement instanceof  XmlAttribute xmlAttribute) {
                    return Optional.ofNullable(xmlAttribute.getValueElement());
                }else {
                    siblingElement = siblingElement.getNextSibling();
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<XmlAttributeValue> getTagFirstAttributeValue(@NotNull XmlTag xmlTag){
        if(xmlTag.getAttributes().length == 0) return Optional.empty();
        XmlAttribute xmlAttribute = xmlTag.getAttributes()[0];
        return Optional.ofNullable(xmlAttribute.getValueElement());
    }
    /**
     * 判断当前PsiElement是不是在属性值域
     * @param psiElement PsiElement
     * @return Boolean
     */
    public static Boolean isAttributeValue(@NotNull PsiElement psiElement){
        return psiElement instanceof XmlAttributeValue;

    }
    public static boolean isNotAttributeValue(@NotNull PsiElement psiElement){
        return !isAttributeValue(psiElement);
    }

    /**
     * 判断当前PsiElement是不是在Tag域
     * @param psiElement PsiElement
     * @return Boolean
     */
    public static boolean isTagName(@NotNull PsiElement psiElement){
        return psiElement instanceof XmlTag;
    }
    public static boolean isNotTagName(@NotNull PsiElement psiElement){
        return !isTagName(psiElement);
    }

    /**
     * 获取属性名
     * @param psiElement PsiElement
     * @return Optional<String>
     */
    public static Optional<String> getCurrentAttributeName(@NotNull PsiElement psiElement){
        if(psiElement instanceof XmlAttribute xmlAttribute) {
            return Optional.of(xmlAttribute.getName());
        }else {
            XmlAttribute xmlAttribute = ReadAction.compute(()->PsiTreeUtil.getParentOfType(psiElement, XmlAttribute.class));
            return Optional.ofNullable(xmlAttribute == null? null : xmlAttribute.getName());
        }
    }
    public static Optional<XmlAttribute> getCurrentAttribute(@NotNull PsiElement psiElement){
        return ReadAction.compute(()-> Optional.ofNullable(PsiTreeUtil.getParentOfType(psiElement, XmlAttribute.class)));
    }
    public static Optional<String> getCurrentTagName(@NotNull PsiElement psiElement){
        return getParentTag(psiElement).map(XmlTag::getName);

    }

    /**
     * 获取当前psiElement的父tag
     * @param psiElement PsiElement
     * @return Optional<XmlTag>
     */
    public static Optional<XmlTag> getParentTag(@NotNull PsiElement psiElement){
//        if (! ReadAction.compute(psiElement::isValid)) return Optional.empty();

        XmlTag xmlTag;
        if(psiElement instanceof XmlTag) {
            xmlTag = (XmlTag) psiElement;
        }else {
            xmlTag =  ApplicationManager.getApplication().runReadAction(
                    (Computable<XmlTag>) () ->PsiTreeUtil.getParentOfType(psiElement, XmlTag.class)
            );
        }

        return Optional.ofNullable(xmlTag);

    }

    /**
     * 获取所在tag的上级tag，
     * @param psiElement 当前PsiElement，可以是XmlTag本身，也可以是XmlAttribute等
     * @return Optional<XmlTag>
     */
    public static Optional<XmlTag> getTagParentTag(@NotNull PsiElement psiElement){
        XmlTag xmlTag = getParentTag(psiElement).orElse(null);
        if(xmlTag != null) xmlTag = ReadAction.compute(xmlTag::getParentTag);
        return Optional.ofNullable(xmlTag);
    }
    public static Optional<XmlAttribute> getCurrentAttribute(ConvertContext context) {
        final XmlElement curElement = context.getXmlElement();
        if(curElement == null) return Optional.empty();
        if(!(curElement instanceof XmlAttribute)) return Optional.empty();
        return Optional.of((XmlAttribute) curElement);
    }
    public static Optional<String> getCurrentAttributeStringValue(ConvertContext context) {
        Optional<XmlAttribute> opCurAttribute = getCurrentAttribute(context);
        return opCurAttribute.map(XmlAttribute::getValue);
    }

    public static Optional<String> getCurrentAttributeName(ConvertContext context) {
        Optional<XmlAttribute> opCurAttribute = getCurrentAttribute(context);
        return opCurAttribute.map(XmlAttribute::getName);
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


    /**
     * 获取最近的一个指定的DomElement
     * @param context ConvertContext
     * @param targetClass Class<T>
     * @return Optional<T>
     * @param <T> <T extends DomElement>
     */
    public static <T extends DomElement> Optional<T> getLocalDomElementByConvertContext(@NotNull ConvertContext context, @NotNull Class<T> targetClass){
        final XmlElement curElement = context.getXmlElement();
        if(curElement == null) return Optional.empty();
        return Optional.ofNullable(ReadAction.compute(()->DomUtil.findDomElement(curElement,targetClass)));

    }
    public static <T extends DomElement> Optional<T> getLocalDomElementByPsiElement(@NotNull PsiElement psiElement, @NotNull Class<T> targetClass){
        return Optional.ofNullable(ReadAction.compute(()->DomUtil.findDomElement(psiElement,targetClass)));
    }
    public static <T extends DomElement> Optional<T> getLocalDomElementByPsiElement(@NotNull PsiElement psiElement, @NotNull Class<T> targetClass,boolean strict){
        return Optional.ofNullable(ReadAction.compute(()->DomUtil.findDomElement(psiElement,targetClass,strict)));
    }

    public static <T extends DomElement> Optional<T> getLocalDomElementByXmlTag(@NotNull XmlTag xmlTag, @NotNull Class<T> targetClass){
        DomElement domElement = DomManager.getDomManager(xmlTag.getProject()).getDomElement(xmlTag);
        if(domElement == null) return Optional.empty();

        return Optional.ofNullable(domElement.getParentOfType(targetClass,false));
    }
    public static Optional<String> getXmlAttributeValueString(XmlAttributeValue value){
        if(value == null) return Optional.empty();
        return Optional.of(value.getValue());

    }

    public static Optional<String> getXmlTagAttributeValueByAttributeName(@NotNull XmlTag xmlTag, @NotNull String attributeName) {
        return getXmlTagAttributeValueByAttributeName(xmlTag,attributeName,null);
    }

    public static Optional<String> getXmlTagAttributeValueByAttributeName(@Nullable XmlTag xmlTag, @Nullable String attributeName, @Nullable String filter){
        if((xmlTag == null)||(attributeName == null)) return Optional.empty();
        String xmlAttributeValue = xmlTag.getAttributeValue(attributeName);
        if(xmlAttributeValue == null) return Optional.empty();
        if (isNotEmpty(filter)) {
            if (!xmlAttributeValue.contains(filter)) {
                return Optional.empty();
            }
        }
        return Optional.of(xmlAttributeValue);
    }

    public static Optional<String> getXmlAttributeValueString(GenericAttributeValue<String> value){
        if(value == null) return Optional.empty();
        return ApplicationManager.getApplication().runReadAction((Computable<Optional<String>>) ()->{
            if(value.getXmlAttributeValue() == null) return Optional.empty();
            return Optional.of(value.getXmlAttributeValue().getValue());
        });

    }
    public static Optional<Boolean> getXmlAttributeValueBoolean(GenericAttributeValue<Boolean> value){
        if(value == null) return Optional.empty();
        return ReadAction.compute(()->{
            if(value.getXmlAttributeValue() == null) return Optional.empty();
            return Optional.ofNullable(value.getValue());
        });

    }
    public static @NotNull String getValueOrEmptyString(GenericAttributeValue<String> value){
        return getXmlAttributeValueString(value).orElse(MyStringUtils.EMPTY_STRING);
    }
    public static @NotNull String getValueOrEmptyString(XmlAttribute value){
        if(value == null) return MyStringUtils.EMPTY_STRING;
        return getValueOrEmptyString(value.getValue());
    }

    public static @NotNull String getValueOrEmptyString(String value){
        return value == null ? MyStringUtils.EMPTY_STRING : value;
    }

    public static boolean getValueOrFalseBoolean(GenericAttributeValue<Boolean> value){
        return getXmlAttributeValueBoolean(value).orElse(false);
    }

    public static @NotNull Integer getValueOrZero(GenericAttributeValue<String> value){
        String s = getXmlAttributeValueString(value).orElse(MyStringUtils.EMPTY_STRING);
        try {
            return Integer.valueOf(s);
        }catch (Exception e) {
            return 0;
        }
    }




    /**
     * 获取该XmlTag下指定名称的所有SubTag，嵌套查询
     * @param xmlTag XmlTag
     * @param tagName String
     * @return List<XmlTag>
     */
    public static @NotNull List<XmlTag> getSubTagList(@NotNull XmlTag xmlTag, @NotNull String tagName){
        return ReadAction.compute(()->{
            List<XmlTag> tagList = new ArrayList<>();
            XmlTag[] xmlTags = xmlTag.getSubTags();

            for(XmlTag xmlTagItem : xmlTags) {
                if(xmlTagItem.getName().equals(tagName)) {
                    tagList.add(xmlTagItem);
                }else {
                    tagList.addAll(getSubTagList(xmlTagItem, tagName));
                }
            }
            return tagList;

        });
    }

    /**
     * 判断一个psiFile是否为moqui的定义文件
     * @param psiFile PsiFile
     * @return boolean
     */
    public static boolean isMoquiXmFile(@NotNull PsiFile psiFile){
        Optional<String> rootTag = getRootTagName(psiFile);
        return rootTag.filter(s -> Set.of(
                Entities.TAG_NAME, Services.TAG_NAME, Screen.TAG_NAME,
                Eecas.TAG_NAME, Secas.TAG_NAME, Emecas.TAG_NAME,
                WidgetTemplates.TAG_NAME, Resource.TAG_NAME, MoquiConf.TAG_NAME, Component.TAG_NAME
        ).contains(s)).isPresent();
    }
    public static boolean isNotMoquiXmlFile(@NotNull PsiFile psiFile){
        return ! isMoquiXmFile(psiFile);
    }

//    public static String getStringFromPsiFile(@NotNull PsiFile psiFile, @NotNull TextRange textRange){
//        psiFile.getVirtualFile().
//    }

    /**
     * 根据某个DomElement打开对应的文件
     * @param element DomElement
     */
    public static void openFileForDomElement(@NotNull DomElement element){
        XmlElement  xmlElement = element.getXmlElement();
        if (xmlElement == null) return;
        openFileForPsiElement(xmlElement);
    }
    public static void openFileForPsiElement(@NotNull PsiElement element){
        Project project = element.getProject();

        FileEditorManager editorManager = FileEditorManager.getInstance(project);


        OpenFileDescriptor descriptor = new OpenFileDescriptor(project,
                element.getContainingFile().getVirtualFile(),
                element.getTextOffset());

        editorManager.openTextEditor(descriptor,true);

    }
    public static void openFileForPsiFile(@NotNull PsiFileSystemItem psiFile){
        Project project = psiFile.getProject();


        FileEditorManager editorManager = FileEditorManager.getInstance(project);


        OpenFileDescriptor descriptor = new OpenFileDescriptor(project,
                psiFile.getVirtualFile(),
                0);

        editorManager.openTextEditor(descriptor,true);


    }

    public static TextRange createAttributeTextRange(@NotNull XmlAttributeValue attributeValue){
        return new TextRange(1,attributeValue.getValue().length()+1);
    }

    public static int getPsiElementOffsetOrZero(@Nullable PsiElement psiElement){
        if(psiElement == null) return 0;
        return psiElement.getTextOffset();
    }

    public static Optional<PsiElement> getPsiElementFromAttributeValue(@Nullable XmlAttributeValue attributeValue) {
        return attributeValue == null ? Optional.empty() : Optional.of(attributeValue.getOriginalElement());
    }


    public static Optional<PsiFile> getPsiFileFromPsiElement(@Nullable PsiElement psiElement) {
        if(psiElement == null)  return Optional.empty();
        return ReadAction.compute(() -> {
            if (!psiElement.isValid()) {
                return Optional.empty(); // 如果PsiElement无效，返回null
            }
            PsiFile containingFile = psiElement.getContainingFile();
            if (containingFile == null || !containingFile.isValid()) {
                return Optional.empty(); // 如果包含文件无效，返回null
            }
            return Optional.of(containingFile);
        });
    }

    public static void inspectAttributeReference(@NotNull GenericAttributeValue<String> attribute, @NotNull AnnotationHolder holder) {
        XmlAttributeValue attributeValue = attribute.getXmlAttributeValue();

        if(attributeValue == null) return;

        String attributeName = getValueOrEmptyString(attribute);

        PsiReference[] psiReferenceArray = attributeValue.getReferences();
        boolean notFoundTarget = true;
        for(PsiReference psiReference: psiReferenceArray) {
//            PsiElement targetElement = psiReference.resolve();
            if(psiReference instanceof MoquiBaseReference) {
                notFoundTarget = false;
                break;
            }
        }
        if(notFoundTarget) {
            holder.newAnnotation(HighlightSeverity.ERROR, "'" + attributeName + "' is not found ")
                    .range(attributeValue.getValueTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
        }

    }

    public static boolean containsMoquiBaseReference(@NotNull PsiReference[] psiReferences) {
        for(PsiReference reference : psiReferences) {
            if(reference instanceof MoquiBaseReference) return true;
        }

        return false;
    }
    /**
     * 仅返回文件名
     * @param psiElement 当前PsiElement
     * @return Optional<String>
     */
    public static Optional<String> getFileNameByPsiElement(@Nullable PsiElement psiElement) {
        if(psiElement == null) return Optional.empty();
        // 在读动作中执行，确保线程安全
        return ReadAction.compute(() -> {
            if (!psiElement.isValid()) {
                return Optional.empty(); // 如果PsiElement无效，返回null
            }
            PsiFile containingFile = psiElement.getContainingFile();
            if (containingFile == null || !containingFile.isValid()) {
                return Optional.empty(); // 如果包含文件无效，返回null
            }
            VirtualFile virtualFile = containingFile.getVirtualFile();
            if (virtualFile != null && virtualFile.isValid()) {
                return Optional.of(virtualFile.getName()); // 返回文件名
            } else {
                return Optional.empty(); // 如果VirtualFile无效，返回null
            }
        });
    }

    /**
     * 返回的是路径+文件名
     * @param psiElement 当前PsiElement
     * @return Optional<String>
     */
    public static Optional<String> getFilePathByPsiElement(@Nullable PsiElement psiElement) {
        if(psiElement == null) return Optional.empty();
        // 在读动作中执行，确保线程安全
        return ReadAction.compute(() -> {
            if (!psiElement.isValid()) {
                return Optional.empty(); // 如果PsiElement无效，返回null
            }
            PsiFile containingFile = psiElement.getContainingFile();
            if (containingFile == null || !containingFile.isValid()) {
                return Optional.empty(); // 如果包含文件无效，返回null
            }
            VirtualFile virtualFile = containingFile.getVirtualFile();
            if (virtualFile != null && virtualFile.isValid()) {
                return Optional.of(virtualFile.getPath()); // 返回文件路径
            } else {
                return Optional.empty(); // 如果VirtualFile无效，返回null
            }
        });
    }

    /**
     * 根据当前所在的xmlTag，找到同级别的前一个xmlTag
     * @param xmlTag
     * @return
     */
    public static Optional<XmlTag> getPreSiblingXmlTag(@NotNull XmlTag xmlTag) {
            return ReadAction.compute(()->{
                PsiElement prevSibling = xmlTag.getPrevSibling();

                // 循环向上查找直到找到前一个XmlTag或者没有更多之前的兄弟元素
                while (prevSibling != null) {
                    if (prevSibling instanceof XmlTag foundTag) {
                        return Optional.of(foundTag);
                    }
                    prevSibling = prevSibling.getPrevSibling();
                }
                return Optional.empty(); // 如果没有找到符合条件的XmlTag

            });
    }

    public static <T> Optional<T> getReferenceDataFromPsiElement(@NotNull PsiElement psiElement, Class<T> targetType) {
        Object savedObject = psiElement.getUserData(MyStringUtils.MOQUI_REFERENCE_CREATED_KEY);
        return targetType.isInstance(savedObject)
                ? Optional.of(targetType.cast(savedObject))
                : Optional.empty();
    }
    public static void putReferenceDataToPsiElement(@NotNull PsiElement psiElement,Object data) {
        psiElement.putUserData(MyStringUtils.MOQUI_REFERENCE_CREATED_KEY,data);
    }


    /**
     * 从PsiElement中获取MoquiBaseReference，如果有多个，则取第一个，
     * 这里的PsiElement一般都是XmlAttributeValue对应的PsiElement
     * @param psiElement 待处理的PsiElement
     * @return Optional<MoquiBaseReference>
     */
    public static Optional<MoquiBaseReference> getMoquiBaseReferenceFromPsiElement(@NotNull PsiElement psiElement){
        for(PsiReference psiReference: psiElement.getReferences()) {
            if(psiReference instanceof MoquiBaseReference moquiBaseReference) return Optional.of(moquiBaseReference);
        }
        return Optional.empty();
    }
}


