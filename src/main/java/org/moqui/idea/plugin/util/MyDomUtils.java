package org.moqui.idea.plugin.util;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexEntity;

import java.nio.file.Path;
import java.util.Set;
import java.util.*;

import static com.intellij.psi.xml.XmlTokenType.*;
import static org.moqui.idea.plugin.util.ComponentUtils.COMPONENT_LOCATION_PREFIX;
import static org.moqui.idea.plugin.util.MyStringUtils.isNotEmpty;

public final class MyDomUtils {

    public static String MOQUI_XML_FILE_ROOT_TAG_ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    public static Map<String,String> MOQUI_XML_FILE_ROOT_TAGS = new HashMap<>(Map.of(
            Entities.TAG_NAME,Entities.VALUE_NoNamespaceSchemaLocation,
            Services.TAG_NAME,Services.VALUE_NoNamespaceSchemaLocation,
            Screen.TAG_NAME,Screen.VALULE_NoNamespaceSchemaLocation,
            Eecas.TAG_NAME, Eecas.VALUE_NoNamespaceSchemaLocation,
            Secas.TAG_NAME, Secas.VALUE_NoNamespaceSchemaLocation,
            Emecas.TAG_NAME, Emecas.VALUE_NoNamespaceSchemaLocation,
            Component.TAG_NAME, Component.VALUE_NoNamespaceSchemaLocation,
            MoquiConf.TAG_NAME,MoquiConf.VALUE_NoNamespaceSchemaLocation,
            Resource.TAG_NAME, Resource.VALUE_NoNamespaceSchemaLocation,
            EntityFacadeXml.TAG_NAME, MyStringUtils.EMPTY_STRING
    ));


    private static Logger LOGGER = Logger.getInstance(MyDomUtils.class);

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
    @NonNls
    public static <T extends DomElement> List<DomFileElement<T>> findDomFileElementsByRootClass(@NotNull Project project, Class<T> rootClazz) {

        return ApplicationManager.getApplication().runReadAction((Computable<List<DomFileElement<T>>>) ()->{
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            return DomService.getInstance().getFileElements(rootClazz,project,scope);
        });


    }

    /**
     * 获取指定文件夹下面的所有文件
     * @param project 当前项目
     * @param path 指定的文件夹
     * @return List<PsiFile>
     */
    @NotNull
    public static List<PsiFile> findPsiFilesByPath(@NotNull Project project, @NotNull String path){

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
     * @return List<PsiFile>
     */
    @NotNull
    public static List<PsiDirectory> findPsiDirectoriesByPath(@NotNull Project project, @NotNull String path){

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
     * @param project 当前project
     * @return 是否为Moqui项目
     */
    public static boolean isMoquiProject(@NotNull Project project){

        String baseDir = project.getBasePath();
        if(baseDir == null) return false;

        VirtualFile specificFile = ReadAction.compute(()->LocalFileSystem.getInstance().findFileByNioFile(Path.of(baseDir,"MoquiInit.properties")));
        return specificFile != null;

    }


    public static boolean isSpecialXmlFile(@NotNull PsiFile file,@NotNull String rootTagName){
        return isSpecialXmlFile(file,rootTagName,null,null);
    }
    public static boolean isSpecialXmlFile(@NotNull PsiFile file,@NotNull String rootTagName,@Nullable String attributeName,@Nullable String attributeValue){

        if(file instanceof XmlFile xmlFile) {
            VirtualFile virtualFile = xmlFile.getVirtualFile();
            if(virtualFile != null && virtualFile.isInLocalFileSystem()) { //判断文件是否已经加载
                XmlTag rootTag = ReadAction.compute(xmlFile::getRootTag);
                if (rootTag == null) return false;
                if(attributeName == null) {
                    return rootTagName.equals(rootTag.getName());
                }else {
                    if (!rootTagName.equals(rootTag.getName())) return false;
                    String value = rootTag.getAttributeValue(attributeName);
                    return (value != null) && value.equals(attributeValue);
                }
            }
        }
        return false;

    }

    public static boolean isMoquiXmlFile(@NotNull PsiFile file){
        if(file instanceof XmlFile xmlFile) {
            VirtualFile virtualFile = xmlFile.getVirtualFile();
            if(virtualFile != null && virtualFile.isInLocalFileSystem()) { //判断文件是否已经加载
                XmlTag rootTag = ReadAction.compute(xmlFile::getRootTag);
                if (rootTag == null) return false;
                if (!MOQUI_XML_FILE_ROOT_TAGS.containsKey(rootTag.getName())) return false;
                String value = rootTag.getAttributeValue(MOQUI_XML_FILE_ROOT_TAG_ATTR_NoNamespaceSchemaLocation);
                if (value == null) value = MyStringUtils.EMPTY_STRING;
                return MOQUI_XML_FILE_ROOT_TAGS.containsValue(value);
            }
        }
        return false;
    }

    /**
     * 判断当前的Tag是否为数据定义的Tag，用于EntityFacadeTagNameProvider
     * 1、在entity-facade-xml下都是定义数据的
     * 2、在Entity下面的seed部分也是定义数据的
     * @param xmlTag
     * @return
     */
    public static boolean isMoquiDataDefineTag(@NotNull XmlTag xmlTag) {
        if (MyDomUtils.isMoquiProject(xmlTag.getProject())) {
            Optional<String> rootNameOptional = getRootTagName(xmlTag.getContainingFile());
            if (rootNameOptional.isPresent() && rootNameOptional.get().equals(EntityFacadeXml.TAG_NAME)) {
                return true;
            }
        }
        return false;
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
            XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(psiElement, XmlAttribute.class);
            return Optional.ofNullable(xmlAttribute == null? null : xmlAttribute.getName());
        }
    }
    public static Optional<XmlAttribute> getCurrentAttribute(@NotNull PsiElement psiElement){
        return Optional.ofNullable(PsiTreeUtil.getParentOfType(psiElement, XmlAttribute.class));
    }
    public static Optional<String> getCurrentTagName(@NotNull PsiElement psiElement){
        return getParentTag(psiElement).map(XmlTag::getName);

    }

    /**
     * 在文件entity-facade-xml中，根据当前的PsiElement查找所在的entityName
     * 查找逻辑：
     * 1、在entity-facade-xml下的第一级Tag，tagName就是EntityName（可能是缩写）
     * 2、第二级Tag，如果是全名或第一个字母是大写的，tagName就是EntityName。如果第一个字母是小写，有可能是第一级Tag的relationship的shortAlias，如果不是，就直接找Entity，是Entity的shortAlias
     * 3、不会跨级查找，即下一级Tag名要么是上一级Tag的relationship的shortAlias，要么直接就是EntityName或Entity的shortAlias
     * @param psiElement PsiElement
     * @return Optional<String>
     */
    public static Optional<String> getEntityNameInEntityFacadeXml(@NotNull PsiElement psiElement){

        XmlTag curTag;
        if(psiElement instanceof XmlTag){
            curTag = (XmlTag) psiElement;
        }else {
            curTag = getParentTag(psiElement).orElse(null);
        }

        if(curTag == null) return Optional.empty();
        XmlTag parentTag = curTag.getParentTag();
        if(parentTag == null) return Optional.empty();

        if(parentTag.getName().equals(EntityFacadeXml.TAG_NAME)) {
            return Optional.of(curTag.getName());
        }else {
            String curTagName = curTag.getName();
            EntityNameDescriptor entityNameDescriptor = EntityNameDescriptor.of(curTagName);
            if(entityNameDescriptor.getIsShortAlias()) {
                //进一步判断是否为ParentTag的relationship

                IndexEntity indexEntity = EntityUtils.getIndexEntityByName(psiElement.getProject(),parentTag.getName()).orElse(null);
                if(indexEntity != null) {
                    Relationship relationship = indexEntity.getRelationshipByName(curTagName).orElse(null);
                    if(relationship != null) {
                        return Optional.of(MyDomUtils.getValueOrEmptyString(relationship.getRelated()));
                    }
                }
                return Optional.of(curTagName);
            }else {
                return Optional.of(curTagName);
            }


        }

    }
    /**
     * 获取当前psiElement的父tag
     * @param psiElement PsiElement
     * @return Optional<XmlTag>
     */
    public static Optional<XmlTag> getParentTag(@NotNull PsiElement psiElement){
        XmlTag xmlTag;
        if(psiElement instanceof XmlTag) {
            xmlTag = (XmlTag) psiElement;
        }else {
            xmlTag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
        }

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
        return Optional.ofNullable(DomUtil.findDomElement(curElement,targetClass));

    }
    public static <T extends DomElement> Optional<T> getLocalDomElementByPsiElement(@NotNull PsiElement psiElement, @NotNull Class<T> targetClass){
        return Optional.ofNullable(DomUtil.findDomElement(psiElement,targetClass));
    }
    public static <T extends DomElement> Optional<T> getLocalDomElementByPsiElement(@NotNull PsiElement psiElement, @NotNull Class<T> targetClass,boolean strict){
        return Optional.ofNullable(DomUtil.findDomElement(psiElement,targetClass,strict));
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
        return ApplicationManager.getApplication().runReadAction((Computable<Optional<Boolean>>) ()->{
            if(value.getXmlAttributeValue() == null) return Optional.empty();
            return Optional.ofNullable(value.getValue());
        });

    }
    public static @NotNull String getValueOrEmptyString(GenericAttributeValue<String> value){
        return getXmlAttributeValueString(value).orElse(MyStringUtils.EMPTY_STRING);
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
     * location格式：
     * 1、component://SimpleScreens/template/party/PartyForms.xml（指向一个文件 ）
     * 2、moqui/runtime/component/SimpleScreens/screen/SimpleScreens/Supplier/EditSupplier.xml
     * 3、//system/Security/UserAccountDetail，
     * 4、component://tools/screen/System.xml
     * 根据location找到对应的PsiFile
     * @param location String
     * @return Optional<PsiFile>
     */
    public static Optional<PsiFile> getFileFromLocation(Project project, @NotNull String location){

        return ApplicationManager.getApplication().runReadAction((Computable<Optional<PsiFile>>) ()->{

            if(MyStringUtils.isEmpty(location)) return Optional.empty();
            //如果location中包含#，则取#之前的内容作为正式的location
            int poundIndex = location.indexOf("#");
            if(poundIndex<0) {
                poundIndex = location.length();
            }
            final String realLocation = location.substring(0,poundIndex);


            int doubleSlashIndex = realLocation.indexOf("//");
            //有双斜杠，是绝对路径
            String pathName;
            if (doubleSlashIndex<0) {
                pathName = realLocation;
            }else{
                if (realLocation.startsWith("//")) {
                    //前两位是//，表示这个路径为内置的component，分别为
                    // system，对应到runtime/base-component/tools/screen/System
                    // tools，对应到runtime/base-component/tools/screen/Tools
                    pathName = realLocation.substring(2);
                    int firstSlashIndex = pathName.indexOf("/");

                    //如果不存在，则存在错误，直接返回
                    if (firstSlashIndex < 0) {
                        return Optional.empty();
                    }

                    String firstPath = pathName.substring(0, firstSlashIndex);
                    pathName = pathName.substring(firstSlashIndex + 1);
                    switch (firstPath) {
                        case "system":
                            pathName = "runtime/base-component/tools/screen/System/" + pathName;
                            break;
                        case "tools":
                            pathName = "runtime/base-component/tools/screen/Tools/" + pathName;
                            break;
                        default:
                            return Optional.empty();
                    }


                } else {
                    //前面是component://，表示对应到runtime/component下，但需要注意的是，tools和webroot是在runtime/base-component下
                    if (realLocation.startsWith(COMPONENT_LOCATION_PREFIX)) {

                        pathName = realLocation.substring(COMPONENT_LOCATION_PREFIX.length());
                    } else {
                        //有问题，不处理
                        return Optional.empty();
                    }
                }
            }
            //获取文件名
            int lastSlashIndex = pathName.lastIndexOf("/");
            String searchName;
            if(lastSlashIndex<0) {
                searchName = pathName;
            }else {
                searchName = pathName.substring(lastSlashIndex+1);
            }
            //如果tempName中不含“.”，则添加.xml作为文件后缀
            if(!searchName.contains(".")){
                searchName = searchName +".xml";
            }

            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName( searchName, true, scope);

         //不管是一个文件，还是多个同名文件，都需要对路径进行匹配验证

        final String finalPathName = pathName;
        VirtualFile virtualFile = foundFileCollection.stream().filter(item -> item.getPath().contains(finalPathName)).findFirst().orElse(null);
        if(virtualFile == null) {return Optional.empty();}

//        DumbService dumbService = DumbService.getInstance(project);
//        return dumbService.runReadActionInSmartMode(()->{
            PsiFile targetFile = PsiManager.getInstance(project).findFile(virtualFile);
            return Optional.ofNullable(targetFile);
        });

//        });
    }

    public static Optional<PsiFile> getPsiFileByPathName(@NotNull Project project, @NotNull String pathName)
    {
        return ApplicationManager.getApplication().runReadAction((Computable<Optional<PsiFile>>) ()->{
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName( pathName, true, scope);
            //不管是一个文件，还是多个同名文件，都需要对路径进行匹配验证
            Optional<VirtualFile> vfOptional = foundFileCollection.stream().findFirst();
            return vfOptional.map(item -> PsiManager.getInstance(project).findFile(item));

        });

    }

    /**
     * 根据location，获取在location中定义的path和fileName，并按先后次序添加到Array中
     * @param location String
     * @return String[]
     */
    public static String[] getPathFileFromLocation(@NotNull String location){

//        ArrayList<String> result = new ArrayList<>();

        int doubtSlashIndex = location.indexOf("//");
        String finalPath;
        if(doubtSlashIndex<0) {
            finalPath = location;
        }else {
            finalPath = location.substring(doubtSlashIndex+2);
        }

        return finalPath.split("/");
    }



    /**
     * 获取该XmlTag下指定名称的所有SubTag，嵌套查询
     * @param xmlTag XmlTag
     * @param tagName String
     * @return List<XmlTag>
     */
    public static @NotNull List<XmlTag> getSubTagList(@NotNull XmlTag xmlTag, @NotNull String tagName){
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

    public static Optional<VirtualFile> getVirtualFileFromPsiElement(@Nullable PsiElement psiElement) {
        return getPsiFileFromPsiElement(psiElement).map(PsiFile::getVirtualFile);
    }
    public static Optional<PsiFile> getPsiFileFromPsiElement(@Nullable PsiElement psiElement) {
        if(psiElement == null){
            return Optional.empty();
        }else {
            return Optional.ofNullable(psiElement.getContainingFile());
        }
    }
    public static Optional<String> getContainingFileNameFromPsiElement(@Nullable PsiElement psiElement) {
        Optional<VirtualFile > tmp = getVirtualFileFromPsiElement(psiElement);
        return tmp.map(VirtualFile::getName);
    }
    public static Optional<String> getContainingPathFromPsiElement(@Nullable PsiElement psiElement) {
        Optional<VirtualFile > tmp = getVirtualFileFromPsiElement(psiElement);
        return tmp.map(VirtualFile::getPath);
    }



}
