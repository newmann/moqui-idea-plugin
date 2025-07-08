package org.moqui.idea.plugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.MoquiBaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.moqui.idea.plugin.util.LocationUtils.createFilePsiReference;
import static org.moqui.idea.plugin.util.LocationUtils.getPathArrayFromLocation;

public final class Location{
    public static Location of(@NotNull Project project,@NotNull String location){
        return new Location(project,location);
    }
    private static final Logger LOGGER = Logger.getInstance(Location.class);
    private LocationType type;

    private final String location; //传入的字符串
    private final Project project;

    private PsiElement psiElement;//文件或路径指向的 PsiElement，可能是PsiFile，也可能是PsiDirectory
    //        private PsiFile file;
//        private PsiDirectory directory;
    private boolean fileHaveSearched = false;//是否已经根据location查找过对应的PsiFile，避免重复查找

    private String pathPart;//#号前面的内容，以及去掉headContent部分的内容
    private  String contentPart;//#号后面的内容
    private String[] pathNameArray = new String[0];

    private String headContent;//路径中的头部内容，可能是component://、//、/、classpath://等
    private boolean isEmpty = false;



    public Location(@NotNull Project  project, @NotNull String location){
        this.project = project;
        this.location = location;
        if(MyStringUtils.isEmpty(location)) {
            this.type = LocationType.Unknown;
            isEmpty = true;
            return;
        }
        if(location.matches(MyStringUtils.CONTAIN_VARIABLE_REGEXP)) {
            this.type = LocationType.DynamicPath;
            return;
        }

        if(location.startsWith("http://") || location.startsWith("https://")) {
            this.type = LocationType.WebUrl;
            return;
        }
        if(location.startsWith(MyStringUtils.COMPONENT_LOCATION_PREFIX)) {

            processComponentType(location);
            return;
        }
        if(location.matches(MyStringUtils.TRANSITION_LEVEL_NAME_REGEXP)) {
            this.type = LocationType.TransitionLevelName;
            return;
        }
        if(location.matches(MyStringUtils.RELATIVE_URL_REGEXP)) {
            this.type = LocationType.RelativeUrl;
            processRelativeUrlPathType(location);
            return;
        }
        if(location.matches(MyStringUtils.ABSOLUTE_URL_REGEXP)) {
            this.type = LocationType.AbsoluteUrl;
            processAbsoluteUrlType(location);
            return;
        }

        if(location.startsWith("classpath://")) {
            this.type = LocationType.ClassPath;
            processClassPathType(location);
            return;
        }
        this.type = LocationType.Unknown;
    }
    private void processClassPathType(@NotNull String location){
        this.headContent = "classpath://";
        String pathPart = location.substring(headContent.length());
        //将路径分解，每一级目录都分别对应
        pathNameArray = getPathArrayFromLocation(pathPart);

        //查找对应的文件
//            file = getFileFromLocation(project, pathPart).orElse(null);

    }
    private void processRelativeUrlPathType(@NotNull String location){
        //将路径分解，每一级目录都分别对应
        pathNameArray = getPathArrayFromLocation(location);


    }
    private void processAbsoluteUrlType(@NotNull String location){
        if(location.startsWith(MyStringUtils.BASE_URL)) {
            headContent = MyStringUtils.BASE_URL;
        }else {
            headContent = MyStringUtils.ROOT_URL;
        }
        pathPart = location.substring(headContent.length());
        //将路径分解，每一级目录都分别对应
        pathNameArray = getPathArrayFromLocation(pathPart);

    }
    private void processComponentType(@NotNull String location){
        this.headContent = MyStringUtils.COMPONENT_LOCATION_PREFIX;
//            String tmp = location;
        if(location.contains("#")) {
            type = LocationType.ComponentFileContent;
            String[] contentSplit = location.split("#");

            pathPart = contentSplit[0];
            contentPart = contentSplit[1];

        }else {
            pathPart = location;
            contentPart="";
            type = LocationType.ComponentFile;
        }
        pathPart = pathPart.substring(headContent.length());

//            tmp = pathPart.split("//")[1];
        //查找对应的文件，移动到getFile过程中统一处理
//            file = getFileFromLocation(project, pathPart).orElse(null);
        //将路径分解，每一级目录都分别对应
        pathNameArray = getPathArrayFromLocation(pathPart);
    }

    /**
     * 直接根据location获取可能存在的文件
     * @return Optional<PsiFile>
     */
    public Optional<PsiFile> getFileByLocation(){
        if(!fileHaveSearched) {
            psiElement = fetchPsiElement().orElse(null);
            fileHaveSearched = true;
        }
        return Optional.ofNullable(getFile());
    }
    /**
     * 只能处理component和absolute url 格式：
     * 1、component://SimpleScreens/template/party/PartyForms.xml（指向一个文件 ）
     * 2、//system/Security/UserAccountDetail，
     * 3、component://tools/screen/System.xml
     * 4、//apps/system/SystemMessage/Message/SystemMessageDetail
     * 根据location找到对应的PsiElement，可能是PsiFile，也可能是PsiDirectory
     */
    private Optional<PsiElement> fetchPsiElement(){
        if((type != LocationType.ComponentFile) && (type != LocationType.ComponentFileContent) &&
                (type != LocationType.AbsoluteUrl)) return Optional.empty();

        return ApplicationManager.getApplication().runReadAction((Computable<Optional<PsiElement>>) ()->{
            switch (type) {
                case ComponentFile, ComponentFileContent -> {
                    PsiFile psiFile =  ComponentUtils.getPsiFileByLocation(this).orElse(null);

                    return Optional.ofNullable(psiFile);
//                    //如果是component file，则直接获取文件
//                    String componentPath = ComponentUtils.getComponentPathByName(project, pathNameArray[0]).orElse(null);
//                    if(componentPath == null) return Optional.empty();
//                    String fileName = componentPath  + pathPart.substring(pathNameArray[0].length());
//
////                         GlobalSearchScope scope = GlobalSearchScope.allScope(project);
////                         Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName( fileName, true, scope);
////                         if(foundFileCollection.isEmpty()) return Optional.empty();
////                         PsiFile targetFile = PsiManager.getInstance(project).findFile(foundFileCollection.);
////                         return Optional.ofNullable(targetFile);
//
//                    // 使用 LocalFileSystem 来找到 VirtualFile
//                    VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileName);
//                    if (virtualFile != null) {
//                        // 使用 PsiManager 来获取 PsiFile
//                        PsiManager psiManager = PsiManager.getInstance(project);
//                        return Optional.ofNullable(psiManager.findFile(virtualFile));
//                    }else {
//                        return Optional.empty();
//                    }


                }
                case AbsoluteUrl -> {
                    //如果是绝对路径，则需要根据路径获取文件
                    MoquiUrl curUrl = MoquiUrl.ofAbsoluteUrl(this,false);
                    if(curUrl == null) {
                        return Optional.empty();
                    }else {
                        return curUrl.getContainingPsiElement();
                    }

                }
                default -> {
                    return Optional.empty();
                }
            }

        });

//        });
    }

    /**
     * 当前相对路径相对于目标文件路径下的的PsiFile，如果没有找到，则返回null
     * 主要应用于Screen文件的查找
     * @param currentFileElement 目标文件路径
     * @return Optional<PsiFile>
     */
    public Optional<PsiFile> getRelativeFile(@NotNull PsiElement currentFileElement) {
        MoquiUrl moquiUrl = MoquiUrl.ofRelativeUrl(currentFileElement,this,false);
        if(moquiUrl == null) return Optional.empty();
        return Optional.of(moquiUrl.getContainingMoquiFile().getContainingFile());
    }

    /**
     * 为相对路径创建PsiReference
     * @param element 为XmlAttributeValue类型的PsiElement
     * @return PsiReference[]
     */
    public  @NotNull PsiReference[] createRelativeUrlPsiReference(@NotNull PsiElement element) {
        if(type != LocationType.RelativeUrl) return PsiReference.EMPTY_ARRAY;

        MoquiUrl moquiUrl = MoquiUrl.ofRelativeUrl(element,this, false);
        if(moquiUrl == null) return PsiReference.EMPTY_ARRAY; //相对路径可能是找不到的，只是提示，和绝对路径不同

        return matchReferenceWithMoquiUrl(element,moquiUrl);

    }
    private PsiReference[] matchReferenceWithMoquiUrl(@NotNull PsiElement element, @Nullable MoquiUrl moquiUrl){
        if(moquiUrl == null) return MoquiBaseReference.createNullRefArray(element,MyDomUtils.createAttributeTextRange((XmlAttributeValue) element));
        List<PsiReference> result = new ArrayList<>();

        if(pathNameArray.length == 0) {
            result.add(MoquiBaseReference.of(element,
                    MyDomUtils.createAttributeTextRange((XmlAttributeValue) element),
                    moquiUrl.getContainingPsiElement().orElse(null)));
        }else {

            int startOffset = location.length();

            for (int i = pathNameArray.length - 1; i >= 0; i--) {
                String itemName = pathNameArray[i];
                TextRange textRange = new TextRange(startOffset - itemName.length() +1, startOffset + 1);

                PsiElement curElement = moquiUrl.getContainingPsiElement().orElse(null);
                if (curElement == null) return MoquiBaseReference.createNullRefArray(element, textRange);

                result.add(MoquiBaseReference.of(element,
                        textRange,
                        curElement));

                if(i > 0) { //最后一个元素不需要获取父级
                    moquiUrl = moquiUrl.getParent();
                    if ((moquiUrl == null)) {
                        LOGGER.warn("在路径" + this.location + "中找不到对应的MoquiUrl：" + itemName);
                        return MoquiBaseReference.createNullRefArray(element, textRange);
                    }
                    startOffset = startOffset - itemName.length() - 1;
                }
            }
        }
        return result.toArray(new PsiReference[0]);
    }
    /**
     * 支持以下格式：
     * //hmadmin/User/EditUser
     * /popc/Order/Cart 这种格式的路径创建Reference
     * /hmstatic/images/client-businessmen26.png
     * ROOT_URL
     * @param element 为XmlAttributeValue类型的PsiElement
     * @return PsiReference[]
     */
    public  @NotNull PsiReference[] createAbsoluteUrlPsiReference(@NotNull PsiElement element){
        if(type != LocationType.AbsoluteUrl) return PsiReference.EMPTY_ARRAY;

        return matchReferenceWithMoquiUrl(element, MoquiUrl.ofAbsoluteUrl(this, false));

//        //添加路径和文件的reference
//        List<PsiReference> result = new ArrayList<>();
//
//        //第一个为MoquiConf中定义的root subscreenItem
//
//        MoquiUrl moquiUrl = MoquiUrl.ofAbsoluteUrl(this, false);
//        if(moquiUrl == null) return MoquiBaseReference.createNullRefArray(element,MyDomUtils.createAttributeTextRange((XmlAttributeValue) element));
//
//        if(pathNameArray.length == 0) {
//            result.add(MoquiBaseReference.of(element,
//                    MyDomUtils.createAttributeTextRange((XmlAttributeValue) element),
//                    moquiUrl.getContainingPsiElement().orElse(null)));
//        }else {
//
//            int startOffset = location.length();
//
//            for (int i = pathNameArray.length - 1; i >= 0; i--) {
//                String itemName = pathNameArray[i];
//                TextRange textRange = new TextRange(startOffset - itemName.length() +1, startOffset + 1);
//
//                PsiElement curElement = moquiUrl.getContainingPsiElement().orElse(null);
//                if (curElement == null) return MoquiBaseReference.createNullRefArray(element, textRange);
//
//                result.add(MoquiBaseReference.of(element,
//                        textRange,
//                        curElement));
//
//
//                moquiUrl = moquiUrl.getParent();
//                if (moquiUrl == null) {
//                    LOGGER.warn("在绝对路径" + this.location + "中找不到对应的MoquiUrl：" + itemName);
//                    return MoquiBaseReference.createNullRefArray(element, textRange);
//                }
//                startOffset = startOffset - itemName.length() - 1;
//            }
//        }
//        return result.toArray(new PsiReference[0]);
    }

    /**
     * 为//component://SimpleScreens/screen/SimpleScreens/Search.xml#SearchOptions这种格式的路径创建Reference
     * @param element 当前PsiElement
     * @param context 当前ConvertContext
     * @return PsiReference[]
     */
    public  @NotNull PsiReference[] createComponentContentPsiReference( @NotNull PsiElement element,@NotNull ConvertContext context){
        //添加路径和文件的reference
        List<PsiReference> result = new ArrayList<>(Arrays.stream(createFilePsiReference(pathPart, element, getFile())).toList());
        //添加content部分的reference
        if(MyStringUtils.isNotEmpty(contentPart)) {
            String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
            String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
            int startOffset = pathPart.length() + 2;
            int endOffset = startOffset + contentPart.length();

            //WidgetTemplateInclude（location）
            if(attributeName.equals(WidgetTemplateInclude.ATTR_LOCATION) && firstTagName.equals(WidgetTemplateInclude.TAG_NAME)) {
                WidgetTemplate widgetTemplate = WidgetTemplateUtils.getWidgetTemplateFromFileByName(getFile(), contentPart)
                        .orElse(null);
                if (widgetTemplate == null) return result.toArray(new PsiReference[0]);

                result.add(new MoquiBaseReference(element,
                        new TextRange(startOffset, endOffset),
                        widgetTemplate.getName().getXmlAttributeValue()));
            }
            //FormSingle（extends）
            if(attributeName.equals(FormSingle.ATTR_EXTENDS)  && firstTagName.equals(FormSingle.TAG_NAME)) {
                FormSingle formSingle = ScreenUtils.getFormSingleFromScreenFileByName(getFile(), contentPart)
                        .orElse(null);
                if (formSingle == null) return result.toArray(new PsiReference[0]);

                result.add(new MoquiBaseReference(element,
                        new TextRange(startOffset, endOffset),
                        formSingle.getName().getXmlAttributeValue()));
            }
            //FormList（extends）
            if(attributeName.equals(FormList.ATTR_EXTENDS) && firstTagName.equals(FormList.TAG_NAME)) {
                FormList formList = ScreenUtils.getFormListFromScreenFileByName(getFile(), contentPart)
                        .orElse(null);
                if (formList == null) return result.toArray(new PsiReference[0]);

                result.add(new MoquiBaseReference(element,
                        new TextRange(startOffset, endOffset),
                        formList.getName().getXmlAttributeValue()));
            }

        }
        return result.toArray(new PsiReference[0]);
    }

    /**
     * 同一个screen文件中的extend
     * @param element 当前PsiElement
     * @param context 当前ConvertContext
     * @return PsiReference[]
     */
    public  @NotNull PsiReference[] createLocalFormExtendPsiReference( @NotNull PsiElement element,@NotNull ConvertContext context){

        String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
        String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

        PsiFile psiFile = context.getFile().getContainingFile();
        List<PsiReference> result = new ArrayList<>();

        if(attributeName.equals(FormSingle.ATTR_EXTENDS)  &&
                firstTagName.equals(FormSingle.TAG_NAME)
        ) {

            FormSingle formSingle = ScreenUtils.getFormSingleFromScreenFileByName(psiFile, location)
                    .orElse(null);
            if (formSingle == null) return result.toArray(new PsiReference[0]);

            result.add(new MoquiBaseReference(element,
                    new TextRange(1, location.length()+1),
                    formSingle.getName().getXmlAttributeValue()));
        }

        if(attributeName.equals(FormList.ATTR_EXTENDS)  &&
                firstTagName.equals(FormList.TAG_NAME)
        ) {

            FormList formList = ScreenUtils.getFormListFromScreenFileByName(psiFile, location)
                    .orElse(null);
            if (formList == null) return result.toArray(new PsiReference[0]);

            result.add(new MoquiBaseReference(element,
                    new TextRange(1, location.length()+1),
                    formList.getName().getXmlAttributeValue()));
        }


        return result.toArray(new PsiReference[0]);
    }


    public String getLocation() {
        return location;
    }

    public LocationType getType() {
        return type;
    }

    public PsiFile getFile() {
        if(!fileHaveSearched) {
            psiElement = fetchPsiElement().orElse(null);
            fileHaveSearched = true;
        }
        if(psiElement instanceof PsiFile psiFile) {
            return psiFile;
        }else {
            return null;
        }
    }

    public Project getProject() {
        return project;
    }

    public PsiDirectory getDirectory() {
        if(!fileHaveSearched) {
            psiElement = fetchPsiElement().orElse(null);
            fileHaveSearched = true;
        }
        if(psiElement instanceof PsiDirectory psiDirectory) {
            return psiDirectory;
        }else {
            return null;
        }
    }

    public String getHeadContent() {
        return headContent;
    }

    public String getContentPart() {
        return contentPart;
    }
    public String getPathPart(){
        return pathPart;
    }

    public String[] getPathNameArray() {
        return pathNameArray;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
