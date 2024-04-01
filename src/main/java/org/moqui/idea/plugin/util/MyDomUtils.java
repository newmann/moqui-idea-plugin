package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Eeca;
import org.moqui.idea.plugin.dom.model.Eecas;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;

public final class MyDomUtils {
    public static String COMPONENT_LOCATION_PREFIX = "component://";
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
        return Optional.of(value.getValue());

    }
    public static Optional<String> getXmlAttributeValueString(GenericAttributeValue<String> value){
        if(value == null) return Optional.empty();
        if(value.getXmlAttributeValue() == null) return Optional.empty();
        return Optional.of(value.getXmlAttributeValue().getValue());

    }

    /**
     * 根据location找到对应的PsiFile
     * @param location
     * @return
     */
    public static Optional<PsiFile> getFileFromLocation(Project project, @NotNull String location){

        if(MyStringUtils.isEmpty(location)) return Optional.empty();
        //如果location中包含#，则取#之前的内容作为正式的location
        int poundIndex = location.indexOf("#");
        if(poundIndex<0) {
            poundIndex = location.length();
        }
        final String realLocation = location.substring(0,poundIndex);

        PsiFile targetFile = null;

        int doubleSlashIndex = realLocation.indexOf("//");
        if (doubleSlashIndex<0) {
            //没有双斜杠，是相对路径
            //TODO 现在url是计算出来的，有一定的规则，但回朔有点麻烦，暂时先不处理

            return Optional.empty();
//            PsiDirectory psiDirectory = currentDirectory;
//
//            String[] pathArray = realLocation.split("/");
//            for(int i = 0; i < pathArray.length-1; i++) {
//                if(pathArray[i].equals("..")) {
//                    psiDirectory = psiDirectory.getParent();
//                }else {
//                    psiDirectory = psiDirectory.findSubdirectory(pathArray[i]);
//
//                }
//                if (psiDirectory== null) return Optional.empty();
//            }
//            //判断是否为带后缀的文件名，如果不是，这添加.xml后，再查找
//            int dotIndex =pathArray[pathArray.length-1].indexOf(".");
//
//            if(dotIndex<0) {
//                targetFile = psiDirectory.findFile(pathArray[pathArray.length-1]+".xml");
//            }else {
//                targetFile = psiDirectory.findFile(pathArray[pathArray.length]);
//            }

        }else {
            //有双斜杠，是绝对路径
            String pathName;
            if(realLocation.startsWith("//")) {
                //前两位是//，表示这个路径为内置的component，分别为
                // system，对应到runtime/base-component/tools/screen/System
                // tools，对应到runtime/base-component/tools/screen/Tools
                pathName = realLocation.substring(2);
                int firstSlashIndex = pathName.indexOf("/");

                //如果不存在，则存在错误，直接返回
                if(firstSlashIndex<0) {
                    return Optional.empty();
                }

                String firstPath = pathName.substring(0, firstSlashIndex);
                pathName = pathName.substring(firstSlashIndex+1);
                switch (firstPath) {
                    case "system":
                        pathName = "runtime/base-component/tools/screen/System/"+ pathName;
                        break;
                    case "tools":
                        pathName = "runtime/base-component/tools/screen/Tools/"+ pathName;
                        break;
                    default:
                        return Optional.empty();
                }


            }else {
                //前面是component://，表示对应到runtime/component下，但需要注意的是，tools和webroot是在runtime/base-component下
                if(realLocation.startsWith(COMPONENT_LOCATION_PREFIX)) {

                    pathName = realLocation.substring(COMPONENT_LOCATION_PREFIX.length());
                }else {
                    //有问题，不处理
                    return Optional.empty();
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
//            Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName(context.getProject(), searchName, true, scope);
            Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName( searchName, true, scope);
//            VirtualFile[] fileArray = foundFileCollection.toArray(new VirtualFile[0]);
//            if(fileArray.length==1) {
//                //对路径进行判断，
//                targetFile = PsiManager.getInstance(project).findFile(fileArray[0]);
//            }else {
                //不管是一个文件，还是多个同名文件，都需要对路径进行匹配验证
            final String finalPathName = pathName;
            VirtualFile virtualFile = foundFileCollection.stream().filter(item -> item.getPath().contains(finalPathName)).findFirst().orElse(null);
            if(virtualFile == null) {return Optional.empty();}
            targetFile = PsiManager.getInstance(project).findFile(virtualFile);
//            }
        }

        return Optional.ofNullable(targetFile);

    }

    /**
     * 根据location，获取在location中定义的path和fileName，并按先后次序添加到Array中
     * @param location
     * @return
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

//    public static PsiDirectory getRootPathFromProject(Project project){
//        project.getProjectFilePath()
//    }

    /**
     * 获取该XmlTag下指定名称的所有SubTag，嵌套查询
     * @param xmlTag
     * @param tagName
     * @return
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
}
