package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.MoquiBaseReference;

import java.io.File;
import java.util.*;

import static org.moqui.idea.plugin.util.MyStringUtils.COMPONENT_LOCATION_PREFIX;

/**
 * 处理moqui中文件路径和文件的专用类
 */
public final class LocationUtils {
    private static final Logger LOGGER = Logger.getInstance(LocationUtils.class);
    private LocationUtils() {
        throw new UnsupportedOperationException();
    }

    public static final List<String> TAG_NAME_MUST_HAVE_LOCATION = List.of(
            IncludeScreen.TAG_NAME,SectionInclude.TAG_NAME,ServiceInclude.TAG_NAME,TransitionInclude.TAG_NAME,
            WidgetTemplateInclude.TAG_NAME
    );

    public static final List<String> HAVE_FILE_PATH_LOCATION_TAG_NAME = List.of(
            ScreenTextOutput.TAG_NAME,Image.TAG_NAME
    );

    /**
     * 在component下的相对位置，同时需要考虑framework
     * @param path 当前Path
     * @return Optional<String>
     */
    public static Optional<String> extractComponentRelativePath(@NotNull String path){
        String[] splitArray;
        if(path.contains(MyStringUtils.COMPONENT_PATH_TAG)) {
            splitArray = path.split(MyStringUtils.COMPONENT_PATH_TAG);
        }else if(path.contains(MyStringUtils.BASE_COMPONENT_PATH_TAG)) {
            splitArray = path.split(MyStringUtils.BASE_COMPONENT_PATH_TAG);
        }else if(path.contains(MyStringUtils.FRAMEWORK_PATH_TAG)) {
            splitArray = path.split(MyStringUtils.FRAMEWORK_PATH_TAG);
        }else {
            return Optional.empty();
        }

        if(splitArray.length == 2) {
            return Optional.of(splitArray[1]);
        }else {
            return Optional.empty();
        }
    }

    /**
     * 为了缩短在Complete list中文件名的长度，将常用的字符串缩短，主要是/screen/
     * @param path 待处理的Path
     * @return String
     */
    public static String simplifyComponentRelativePath(@NotNull String path){

        return path.replace("/screen/","@");
    }

    /**
     * 根据location，获取在location中定义的path和fileName，并按先后次序添加到Array中
     * 可以处理前面字符为 "//"、 "/"、"component://"
     * @param location String
     * @return String[]
     */
    public static String[] getPathArrayFromLocation(@NotNull String location){

//        ArrayList<String> result = new ArrayList<>();

        int doubtSlashIndex = location.indexOf("//");
        String finalPath;
        if(doubtSlashIndex<0) {
            finalPath = location;
        }else {
            finalPath = location.substring(doubtSlashIndex+2);
        }
        if(finalPath.startsWith("/")) finalPath = finalPath.substring(1);

        if(MyStringUtils.isEmpty(finalPath)) {
            //如果路径为空，则返回空数组
            return new String[0];
        }else {
            return finalPath.split("/");
        }
    }

    /**
     * 根据文件名找到对应的VirtualFile
     * @param project 当前Project
     * @param pathName 文件名
     * @return Optional<VirtualFile>
     */
    public static Optional<VirtualFile> getVirtualFileByPathName(@NotNull Project project, @NotNull String pathName)
    {
        return ApplicationManager.getApplication().runReadAction((Computable<Optional<VirtualFile>>) ()->{
            return Optional.ofNullable(VfsUtil.findFileByIoFile(new File(pathName),true));
//            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
//            return FilenameIndex.getVirtualFilesByName( pathName, true, scope).stream().findFirst();
        });

    }

    public static Optional<PsiFile> getPsiFileByPathName(@NotNull Project project, @NotNull String pathName)
    {
        String fileName = new File(pathName).getName();

        return ApplicationManager.getApplication().runReadAction((Computable<Optional<PsiFile>>) ()->{
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName( fileName, true, scope);
            //不管是一个文件，还是多个同名文件，都需要对路径进行匹配验证
            Optional<VirtualFile> vfOptional = foundFileCollection.stream()
                    .filter(item->item.getPath().equals(pathName))
                    .findFirst();
            return vfOptional.map(item -> PsiManager.getInstance(project).findFile(item));

        });

    }

    /**
     * location格式：
     * 1、component://SimpleScreens/template/party/PartyForms.xml（指向一个文件 ）
     * 2、moqui/runtime/component/SimpleScreens/screen/SimpleScreens/Supplier/EditSupplier.xml
     * 3、//system/Security/UserAccountDetail，
     * 4、component://tools/screen/System.xml
     * 5、//apps/system/SystemMessage/Message/SystemMessageDetail
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
                    pathName = processBaseComponentPath(pathName).orElse(null);
                    if(pathName == null) {
                        //如果处理失败，则返回空
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

    public static @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        Project project = context.getProject();
        if(project ==null) return PsiReference.EMPTY_ARRAY;
        String url = value.getStringValue();
        if (url == null) return PsiReference.EMPTY_ARRAY;
        if(element.getContainingFile().getVirtualFile()==null) return PsiReference.EMPTY_ARRAY;


        Location location = new Location(project,url);
        String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
        String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);

        //由于FormSingle和FormList的extends格式和RelativeScreenFile类似，所以不能通过url的格式来判断
        //需要根据context进一步判断
        if(attributeName.equals(FormSingle.ATTR_EXTENDS)  &&
                (firstTagName.equals(FormSingle.TAG_NAME) || firstTagName.equals(FormList.TAG_NAME))
        ) {
            //为扩展所在文件的form名称
            return location.createLocalFormExtendPsiReference(element,context);
        }
        //Subscreens的路径类型必须是RelativeScreenFile，像dashboard，会被判断为TransitionName
        if(attributeName.equals(SubScreens.ATTR_DEFAULT_ITEM) && firstTagName.equals(SubScreens.TAG_NAME)) {
            location.setType(LocationType.RelativeUrl);
        }


        switch(location.getType()) {
            case TransitionLevelName -> {
                Optional<AbstractTransition> optTransition = ScreenUtils.getAbstractTransitionFromConvertContextByName(url,context);
                if (optTransition.isPresent()) {
                    final AbstractTransition transition = optTransition.get();
                    PsiReference[] psiReferences = new PsiReference[1];
                    psiReferences[0] = new MoquiBaseReference(element,
                            new TextRange(1,
                                    url.length()+1),
                            transition.getName().getXmlAttributeValue());

                    return psiReferences;

                }else {
                    //再找当前Screen的下级Screen
                    MoquiUrl moquiUrl = MoquiUrl.ofPsiFile(element,false);
                    if(moquiUrl != null) {
                        Optional<MoquiUrl> childUrl = moquiUrl.getChildUrlByName(url);
                        if(childUrl.isPresent()) {
                            PsiElement psiElement = childUrl.get().getContainingPsiElement().orElse(null);
                            if(psiElement != null) {
                                return new PsiReference[]{new MoquiBaseReference(element,
                                        new TextRange(1, url.length() + 1),
                                        psiElement)};
                            }
                        }
                    }
                    //提示错误
                    return MoquiBaseReference.createNullRefArray(element,new TextRange(1,
                            url.length()+1));
                }


            }
            case RelativeUrl -> {
                return location.createRelativeUrlPsiReference(element);
//                Optional<PsiFile> file = location.getRelativeFile(element);//element.getContainingFile().getVirtualFile().getPath());
//                //如果没有找到，可能是SubScreensItem
//                if(file.isEmpty()) {
//                    Optional<SubScreensItem> subScreensItem = ScreenUtils.getSubScreensItemByName(url,context);
//                    if(subScreensItem.isPresent()) {
//                        Location subScreensItemLocation = new Location(context.getProject(),
//                                MyDomUtils.getValueOrEmptyString(subScreensItem.get().getLocation()));
//                        file = Optional.ofNullable(subScreensItemLocation.getFile());
//                        return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);
//                    }else{ //相对路径，有可能找不到，不提示错误
////                        return MoquiBaseReference.createNullRefArray(element,new TextRange(1,url.length()+1));
//                        return PsiReference.EMPTY_ARRAY;
//                    }
//                }else {
//                    return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);
//                }

            }
            case ComponentFile, ClassPath -> {
                if(location.getFile() == null) {
                    return MoquiBaseReference.createNullRefArray(element, new TextRange(1,
                            url.length() + 1));
                }else {
                    return createFilePsiReference(url, element, location.getFile());
                }
            }
            case ComponentFileContent -> {
                if(location.getFile() == null) {
                    return MoquiBaseReference.createNullRefArray(element, new TextRange(1,
                            url.length() + 1));
                }else {
                    return location.createComponentContentPsiReference(element, context);
                }
            }

            case WebUrl, CamelPath -> {
                return PsiReference.EMPTY_ARRAY;
            }
            case DynamicPath ->{
                //包含变量的路径，不处理
                return PsiReference.EMPTY_ARRAY;
            }
            case AbsoluteUrl -> {
                return location.createAbsoluteUrlPsiReference(element);

            }
            default -> {
//                //需要根据context进一步判断
//                String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
//                String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
//                if(attributeName.equals(FormSingle.ATTR_EXTENDS)  &&
//                        (firstTagName.equals(FormSingle.TAG_NAME) || firstTagName.equals(FormList.TAG_NAME))
//                ) {
//                    //为扩展所在文件的form名称
//                    return location.createLocalFormExtendPsiReference(element,context);
//                }

                //在一些tag中，属性指向一个文件
                if(HAVE_FILE_PATH_LOCATION_TAG_NAME.contains(firstTagName)) {
                    return location.getFileByLocation()
                            .map(psiFile -> createFilePsiReference(url, element, psiFile))
                            .orElse(MoquiBaseReference.createNullRefArray(element,new TextRange(1,url.length()+1)));
                }

                if(firstTagName.equals(Screen.TAG_NAME)) {
                    //找不到文件的，大部分都是fa fa-dashboard等，不需要提示错误
                    return location.getFileByLocation()
                            .map(psiFile -> createFilePsiReference(url, element, psiFile))
                            .orElse(PsiReference.EMPTY_ARRAY);
                }

                if(attributeName.equals(AttListResponse.ATTR_URL)) {
                    //为URL的相对路径
                    if(element.getContainingFile().getVirtualFile() == null)
                        return MoquiBaseReference.createNullRefArray(element,new TextRange(1,
                                url.length()+1));

                    Optional<PsiFile> file = location.getRelativeFile(element);//element.getContainingFile().getVirtualFile().getPath());
                    return file.map(psiFile -> createFilePsiReference(url, element, psiFile))
                            .orElse(MoquiBaseReference.createNullRefArray(element,new TextRange(1, url.length()+1)));
                }


                return PsiReference.EMPTY_ARRAY;

            }
        }
//        return PsiReference.EMPTY_ARRAY;

    }

    /**
     * 为文件创建对应的Reference，文件名是对应到具体文件，每一级的文件夹也对应到相应的文件夹
     * @param attributeString 当前PsiElement对应的内容
     * @param element 当前PsiElement
     * @param file 指向的文件
     * @return PsiReference[]
     */
    public static @NotNull PsiReference[] createFilePsiReference(@NotNull String attributeString, @NotNull PsiElement element, @NotNull PsiFile file){

        //将路径分解，每一级目录都分别对应
        String[] pathFileArray = getPathArrayFromLocation(attributeString);

        final int pathFileArrayLength = pathFileArray.length;

        List<PsiReference> result = new ArrayList<>();

        //Service(location)，
        //TransitionInclude(location)
        //Transition，defaultResponse（url）


        int startIndex = attributeString.lastIndexOf(MyStringUtils.PATH_SEPARATOR);
        if(startIndex<0) {
            //沒有其他的目录
            result.add(new MoquiBaseReference(element,
                    new TextRange(1, attributeString.length()+1),
                    file));
        }else {

            result.add(new MoquiBaseReference(element,
                    new TextRange(startIndex + 2, 1 + attributeString.length()),
                    file));

            PsiDirectory psiDirectory = file.getParent();

            int endOffsetIndex = startIndex + 1;
            int i = pathFileArrayLength - 2;
            while (i >= 0) {
                if (MyStringUtils.isEmpty(pathFileArray[i])) {
                    startIndex = startIndex - 1;

                    if(psiDirectory == null) break;

                    psiDirectory = psiDirectory.getParent();
                    continue;
                }

                result.add( new MoquiBaseReference(element,
                        new TextRange(endOffsetIndex - pathFileArray[i].length(), endOffsetIndex),
                        psiDirectory));
                endOffsetIndex = endOffsetIndex - pathFileArray[i].length() - 1;

                if(psiDirectory == null) break;
                psiDirectory = psiDirectory.getParent();
                i = i-1;
            }
        }
        return result.toArray(new PsiReference[0]);
    }

    /**
     * 根据当前的PsiElement，找到相对路径下所有的类型为Screen的文件
     * @param psiElement 当前PsiElement
     * @return List<PsiFile>
     */
    public static List<PsiFile> getRelativeScreenFileList(@NotNull PsiElement psiElement) {
//        if(psiElement.getContainingFile() == null) return new ArrayList<>();
//
//        PsiFile file = psiElement.getContainingFile().getOriginalFile();
//        String relativePath = MyStringUtils.removeLastDotString(file.getVirtualFile().getPath());
        String relativePath = LocationUtils.getMoquiPathFromPsiElement(psiElement).orElse(null);
        if(relativePath == null) return new ArrayList<>();

        List<PsiFile> result = new ArrayList<>();
        MyDomUtils.findPsiFileListByPath(psiElement.getProject(),relativePath)
                .forEach(item->{
                    if(ScreenUtils.isScreenFile(item)) {
                        result.add(item);
                    }
                });
        return result;

    }
    /**
     * 根据当前的PsiElement，找到相对路径下所有的子目录和文件
     * 当前PsiElement可以对应PsiDirectory，这是就是去Directory下面的所有子目录和文件
     * @param psiElement 当前PsiElement
     * @return List<PsiElement> 包含PsiFile和PsiDirectory
     */
    public static List<PsiElement> getRelativePathFileList(@NotNull PsiElement psiElement) {
        return getMoquiPathFromPsiElement(psiElement).map(
                moquiPath -> MyDomUtils.findPathAndFileListByPath(psiElement.getProject(), moquiPath)
        ).orElse(new ArrayList<>());

//        return MyDomUtils.findPathAndFileListByPath(psiElement.getProject(), relativePath);
    }

    /**
     * 根据PsiFile返回Moqui路径，就是文件名对应的目录
     * @param psiFile 当前PsiFile
     * @return Optional<String> 返回路径
     */
    public static Optional<String> getMoquiPathFromPsiFile(@Nullable PsiFile psiFile){

        if(psiFile == null) return Optional.empty();

        return Optional.of(MyStringUtils.removeLastDotString(psiFile.getVirtualFile().getPath()));
    }
    /**
     * 根据PsiElement返回Moqui路径，就是文件名对应的目录
     * @param psiElement 当前PsiElement
     * @return Optional<String> 返回路径
     */
    public static Optional<String> getMoquiPathFromPsiElement(@Nullable PsiElement psiElement){
        if(psiElement instanceof PsiDirectory psiDirectory) {
            return  Optional.of(psiDirectory.getVirtualFile().getPath());
        }else {
            if (psiElement.getContainingFile() == null) return Optional.empty();
            return getMoquiPathFromPsiFile(psiElement.getContainingFile().getOriginalFile());
        }

    }
    public static Optional<PsiDirectory> getContainingPsiDirectorByPsiFile(@Nullable PsiFile psiFile) {
        return ReadAction.compute(()->{
            if(psiFile == null) return Optional.empty();
            return Optional.ofNullable(psiFile.getOriginalFile().getContainingDirectory());
        });
    }
    public static Optional<PsiDirectory> getSameNamePsiDirectorByPsiFile(@Nullable PsiFile psiFile) {
        return ReadAction.compute(()->{
            if(psiFile == null) return Optional.empty();
            String name = psiFile.getName();
            name = MyStringUtils.removeLastDotString(name);
            return Optional.ofNullable(psiFile.getOriginalFile().getContainingDirectory().findSubdirectory(name));
        });
    }

    /**
     * 对location进行检查
     * AbstractLocation：
     *  widget-template-include（location）、transition-include（location）、screen（location）
     *  subscreen-item（location）、section-include ( location  )、text （location）
     *  AbStractForm：
     *  form-single (extends)、form-list （extends）
     */
    public static void inspectAbstractLocationLocation(@NotNull DomElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof AbstractLocation abstractLocation)) {
            return;
        }
        if(element.getXmlElement() == null) return;
        XmlAttributeValue locationAttr;

        if(element instanceof AbstractForm abstractForm) {
            locationAttr = abstractForm.getExtends().getXmlAttributeValue();
        }else{
            locationAttr = abstractLocation.getLocation().getXmlAttributeValue();
        }

        inspectLocationFromAttribute(element,locationAttr,holder);
    }

    /**
     * 对url进行检查
     * @param element
     * @param holder
     */
    public static void inspectAbstractUrlLocation(@NotNull DomElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof AbstractUrl abstractUrl)) {
            return;
        }
        if(element.getXmlElement() == null) return;
        XmlAttributeValue locationAttr = abstractUrl.getUrl().getXmlAttributeValue();

        inspectLocationFromAttribute(element,locationAttr,holder);
    }

    /**
     * 检查某个Tag的包含Location的Attribute
     * @param element 指定的Tag
     * @param locationAttr 指定的Attribute
     * @param holder AnnotationHolder
     */
    public static void inspectLocationFromAttribute(@NotNull DomElement element, @Nullable XmlAttributeValue locationAttr, @NotNull AnnotationHolder holder) {

        if(locationAttr == null) return;
        //如果已经创建了MoquiBaseReference，则表示有效，无需判断
        if(MyDomUtils.containsMoquiBaseReference(locationAttr.getReferences())) return;


        String url = MyDomUtils.getXmlAttributeValueString(locationAttr).orElse(MyStringUtils.EMPTY_STRING);
        Project project = locationAttr.getProject();

        Location location = new Location(project, url);

        if(location.isEmpty()) {
            //在DTD文件中定义了，不需要重复检查
            return;
        }


        if(location.getType() == LocationType.DynamicPath) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Including Groovy variable.")
                    .range(locationAttr.getValueTextRange())
                    .highlightType(ProblemHighlightType.WEAK_WARNING)
                    .create();
            return;
        }

        if(location.getType() == LocationType.RelativeUrl) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Relative url, Unable to determine if it is correct.")
                    .range(locationAttr.getValueTextRange())
                    .highlightType(ProblemHighlightType.WEAK_WARNING)
                    .create();
            return;
        }

        PsiFile psiFile = location.getFile();
        if(location.getType() == LocationType.Unknown) {//不知道的类型，尝试直接找文件
            psiFile = location.getFileByLocation().orElse(null);
        }

        if (psiFile == null) {
            if((location.getType() == LocationType.Unknown) || (location.getType() == LocationType.WebUrl)
                    || (location.getType() == LocationType.CamelPath)
            ) {
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "The location:" + location.getLocation() + " is url or Camel path, check for effectiveness on your own")
                        .range(locationAttr.getValueTextRange())
                        .highlightType(ProblemHighlightType.WEAK_WARNING)
                        .create();

            }else {
                holder.newAnnotation(HighlightSeverity.ERROR, "根据路径[" + location.getLocation() + "]找不到对应的文件")
                        .range(locationAttr.getValueTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }
            return;
        }

        if(location.getType()!= LocationType.Unknown && MyStringUtils.isNotEmpty(location.getContentPart())) {
            boolean targetNotExist = false;
            if(element instanceof WidgetTemplateInclude) {
                WidgetTemplate widgetTemplate = WidgetTemplateUtils.getWidgetTemplateFromFileByName(psiFile, location.getContentPart())
                        .orElse(null);
                targetNotExist = (widgetTemplate == null);
            }
            if(element instanceof FormSingle ) {
                FormSingle formSingle = ScreenUtils.getFormSingleFromScreenFileByName(psiFile, location.getContentPart())
                        .orElse(null);
                targetNotExist = (formSingle == null);
            }
            if(element instanceof FormList ) {
                FormList formList = ScreenUtils.getFormListFromScreenFileByName(psiFile, location.getContentPart())
                        .orElse(null);
                targetNotExist = (formList == null);
            }
            if(targetNotExist) {
                String errMsg;
                if(MyStringUtils.isNotEmpty(location.getPathPart())) {
                    errMsg = "在文件[" + location.getPathPart() + "]中没有找到[" + location.getContentPart() + "]的定义";
                }else {
                    errMsg = "在当前文件中没有找到[" + location.getContentPart() + "]的定义";
                }

                holder.newAnnotation(HighlightSeverity.ERROR, errMsg)
                        .range(locationAttr.getValueTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }
        }

    }

    public static @NotNull List<VirtualFile> getFrameworkDataDirectoryList(@NotNull Project project){
        String basePath = project.getBasePath();
        if(basePath== null) return new ArrayList<>();
        VirtualFile baseFile = getVirtualFileByPathName(project,basePath).orElse(null);
        if(baseFile == null) return new ArrayList<>();
        return  Arrays.stream(baseFile.getChildren())
                .filter(VirtualFile::isDirectory)
                .filter(virtualFile -> virtualFile.getName().equals("framework"))
                .flatMap(virtualFile -> Arrays.stream(virtualFile.getChildren()))
                .filter(VirtualFile::isDirectory)
                .filter(virtualFile -> virtualFile.getName().equals("data"))
                .toList();
    }
    public static @NotNull List<VirtualFile> getComponentDataDirectoryList(@NotNull Project project){
        String basePath = project.getBasePath();
        if(basePath== null) return new ArrayList<>();
        VirtualFile baseFile = getVirtualFileByPathName(project,basePath).orElse(null);
        if(baseFile == null) return new ArrayList<>();
        return  Arrays.stream(baseFile.getChildren())
                .filter(VirtualFile::isDirectory)
                .filter(virtualFile -> virtualFile.getName().equals("runtime"))
                .flatMap(virtualFile -> Arrays.stream(virtualFile.getChildren()))
                .filter(VirtualFile::isDirectory)
                .filter(virtualFile -> virtualFile.getName().equals("base-component") || virtualFile.getName().equals("action.AdminToolWindowFactory.loading.components.title"))
                .flatMap(virtualFile -> Arrays.stream(virtualFile.getChildren()))
                .filter(VirtualFile::isDirectory)
                .flatMap(virtualFile -> Arrays.stream(virtualFile.getChildren()))
                .filter(VirtualFile::isDirectory)
                .filter(virtualFile -> virtualFile.getName().equals("data"))
                .toList();
    }

    public static @NotNull List<VirtualFile> getAllDataDirectoryList(@NotNull Project project){
        List<VirtualFile> result = new ArrayList<>();
        result.addAll(getFrameworkDataDirectoryList(project));
        result.addAll(getComponentDataDirectoryList(project));
        return result;
    }

    public static @NotNull List<VirtualFile> getAllDataFileList(@NotNull Project project){
        return getAllDataDirectoryList(project).stream()
                .flatMap(virtualFile -> Arrays.stream(virtualFile.getChildren()))
                .filter(virtualFile -> virtualFile.isValid() && (!virtualFile.isDirectory()))
                .toList();
    }
    private static Optional<String> processBaseComponentPath(@NotNull String pathName) {

        // system，对应到runtime/base-component/tools/screen/System
        // tools，对应到runtime/base-component/tools/screen/Tools

        int firstSlashIndex = pathName.indexOf("/");

        //如果不存在，则存在错误，直接返回
        if (firstSlashIndex < 0) {
            return Optional.empty();
        }

        String firstPath = pathName.substring(0, firstSlashIndex);
        pathName = pathName.substring(firstSlashIndex + 1);

        return switch (firstPath) {
            case "system" -> Optional.of("runtime/base-component/tools/screen/System/" + pathName);
            case "tools" -> Optional.of("runtime/base-component/tools/screen/Tools/" + pathName);
            case "apps", "qapps" -> processBaseComponentPath(pathName);
            default -> Optional.empty();
        };


    }
}
