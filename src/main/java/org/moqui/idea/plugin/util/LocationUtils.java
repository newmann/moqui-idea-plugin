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

        return finalPath.split("/");
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
//                    int firstSlashIndex = pathName.indexOf("/");
//
//                    //如果不存在，则存在错误，直接返回
//                    if (firstSlashIndex < 0) {
//                        return Optional.empty();
//                    }
//
//                    String firstPath = pathName.substring(0, firstSlashIndex);
//                    pathName = pathName.substring(firstSlashIndex + 1);
//                    switch (firstPath) {
//                        case "system":
//                            pathName = "runtime/base-component/tools/screen/System/" + pathName;
//                            break;
//                        case "tools":
//                            pathName = "runtime/base-component/tools/screen/Tools/" + pathName;
//                            break;
//                        case "apps","qapps":
//
//                        default:
//                            return Optional.empty();
//                    }


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

    public enum LocationType{
        Unknown, //根据location无法判断具体类型的，需要根据所在的context，进一步判断，
        ComponentFile, //component://SimpleScreens/template/party/PartyForms.xml（指向一个文件 ）
        ComponentFileContent, //component://SimpleScreens/screen/SimpleScreens/Search.xml#SearchOptions（指向文件中的某个tag），
        RelativeUrl,//../EditFacility，同一个目录下的screen文件，
        WebUrl,
        TransitionName,
        DynamicPath,
        CamelPath,//location="moquiservice:moqui.example.ExampleServices.targetCamelExample"
        ClassPath,//location="classpath://service/org/moqui/impl/BasicServices.xml"，service-file、service-include中使用
                  //     和component类似，对应到某个文件，只是没有指定component名称，但应该也是唯一的
        AbsoluteUrl,// 类似这种路径//hmadmin/User/EditUser，需要找到所在component的MoquiConf.xml中定义的菜单入口，顺着subscreen的定义路径，找到对应的screen定义文件
                    //${appRoot}/ServiceAgreement/ServiceLocation  ${appRoot}为变量，一般在所在组建的一级入口screen文件中定义，
                    //apps/system/Security/UserAccount/UserAccountDetail //apps是特定写法，等同于//system/Security/UserAccount/UserAccountDetail

    }
    public static Location ofLocation(@NotNull Project project,@NotNull String location){
        return new Location(project,location);
    }

    public static Location ofLocation(@NotNull Project project,@NotNull String currentLocation,@NotNull String relativeLocation){
        return new Location(project,currentLocation,relativeLocation);
    }

     public static final class Location{
        private LocationType type;
        private final String location; //传入的字符串
        private final Project project;

        private PsiElement psiElement;//文件或路径指向的 PsiElement，可能是PsiFile，也可能是PsiDirectory
        private PsiFile file;
        private PsiDirectory directory;
        private boolean fileHaveSearched = false;//是否已经根据location查找过对应的PsiFile，避免重复查找

        private String pathPart;//#号前面的内容，以及去掉headContent部分的内容
        private  String contentPart;//#号后面的内容
        private String[] pathNameArray;

        private String headContent;//路径中的头部内容，可能是component://、//、/、classpath://等
         private boolean isEmpty = false;
         /**
          * Location中包含有${}的变量
          */

        public Location(@NotNull Project project, @NotNull String currentLocation,@NotNull String relativeLocation){
            this(project,currentLocation);
            getRelativeFile(relativeLocation);
        }

         public Location(@NotNull Project  project,@NotNull String location){
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
            if(location.matches(MyStringUtils.TRANSITION_NAME_REGEXP)) {
                this.type = LocationType.TransitionName;
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
             //查找最后一级所对应的文件

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
                        //如果是component file，则直接获取文件
                        String componentPath = ComponentUtils.getComponentPathByName(project, pathNameArray[0]).orElse(null);
                        if(componentPath == null) return Optional.empty();
                        String fileName = componentPath  + pathPart.substring(pathNameArray[0].length());

//                         GlobalSearchScope scope = GlobalSearchScope.allScope(project);
//                         Collection<VirtualFile> foundFileCollection = FilenameIndex.getVirtualFilesByName( fileName, true, scope);
//                         if(foundFileCollection.isEmpty()) return Optional.empty();
//                         PsiFile targetFile = PsiManager.getInstance(project).findFile(foundFileCollection.);
//                         return Optional.ofNullable(targetFile);

                        // 使用 LocalFileSystem 来找到 VirtualFile
                        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(fileName);
                        if (virtualFile != null) {
                            // 使用 PsiManager 来获取 PsiFile
                            PsiManager psiManager = PsiManager.getInstance(project);
                            return Optional.ofNullable(psiManager.findFile(virtualFile));
                        }else {
                            return Optional.empty();
                        }


                    }
                    case AbsoluteUrl -> {
                        //如果是绝对路径，则需要根据路径获取文件
                        MoquiUrl curUrl =MoquiUrl.of(project, MyStringUtils.ROOT_SCREEN_LOCATION,false);
                        if(curUrl == null) return Optional.empty();

                        if(pathNameArray.length == 0){
                            if(headContent.equals(MyStringUtils.ROOT_URL)) {
                                return MoquiUrl.getContainingPsiElementFromMoquiUrl(curUrl);
//                                PsiFile psiFile = MoquiUrl.getContainingFileFromMoquiUrl(curUrl).orElse(null);
//                                if(psiFile == null) {
//                                    return Optional.empty();
//                                }else {
//                                    return Optional.of(psiFile);
//                                }

                            }else {
                                return  Optional.empty();
                            }
                        }else {
                            for (String itemName : pathNameArray) {
                                curUrl = MoquiUrl.getMoquiUrlByName(curUrl, itemName).orElse(null);
                                if (curUrl == null) {
                                    LOGGER.warn(itemName + "找不到对应的MoquiUrl");
                                    return Optional.empty();
                                }
                            }

                            return MoquiUrl.getContainingPsiElementFromMoquiUrl(curUrl);
                        }
                    }
                    default -> {
                        return Optional.empty();
                    }
                }

             });

//        });
         }

        public Optional<PsiFile> getRelativeFile(@NotNull String currentFilePathName){
            if(this.type != LocationType.RelativeUrl) return Optional.empty();

            String tmpString = currentFilePathName;
            //如果结尾是文件名需要将后缀去掉
            int tmpIndex = currentFilePathName.lastIndexOf(".xml");
            if(tmpIndex > 0) {
                tmpString = currentFilePathName.substring(0,tmpIndex);
            }
            this.pathNameArray = tmpString.split(MyStringUtils.PATH_SEPARATOR);
            ArrayList<String> targetFileArray = new ArrayList<>(Arrays.asList(this.pathNameArray));

            String[] relativeFileArray = this.location.split(MyStringUtils.PATH_SEPARATOR);
            for (String s : relativeFileArray) {
                if (s.equals(".")) {
                    continue;
                }
                if (s.equals("..")) {
                    tmpIndex = targetFileArray.size() - 1;
                    if (tmpIndex < 0) continue; //一般不会出现，出现后应该错误了，这里不处理错误，后续会找不到文件
                    //退回到上一级
                    targetFileArray.remove(tmpIndex);
                } else {
                    //将路径压入
                    targetFileArray.add(s);
                }
            }
            //组合目标文件名称，将第一个去掉，因为在window和linux上对盘符的处理不同，
            targetFileArray.remove(0);
            String targetName = String.join(MyStringUtils.PATH_SEPARATOR, targetFileArray)+".xml";

            //获取psifile
            Optional<PsiFile> fileOptional = getFileFromLocation(project,targetName);

            this.file = fileOptional.orElse(null);
            return fileOptional;

        }
         /**
          * 支持以下格式：
          * //hmadmin/User/EditUser
          * /popc/Order/Cart 这种格式的路径创建Reference
          * /hmstatic/images/client-businessmen26.png
          * ROOT_URL
          * @param element 当前PsiElement
          * @param context 当前ConvertContext
          * @return PsiReference[]
          */
         public  @NotNull PsiReference[] createAbsoluteUrlPsiReference( @NotNull PsiElement element,@NotNull ConvertContext context){
             if(type != LocationType.AbsoluteUrl) return PsiReference.EMPTY_ARRAY;

             //添加路径和文件的reference
             List<PsiReference> result = new ArrayList<>();
             //第一个为MoquiConf中定义的root subscreenItem

             String rootName = pathNameArray[0];
             int startOffset = location.indexOf(rootName)+1;
            TextRange locationTextRange = new TextRange(1,location.length()+1);

            MoquiUrl curUrl =MoquiUrl.of(element.getProject(), MyStringUtils.ROOT_SCREEN_LOCATION,false);
             if(curUrl == null) return MoquiBaseReference.createNullRefArray(element,locationTextRange);

            if(location.equals(MyStringUtils.ROOT_URL)) {
                PsiElement curElement = MoquiUrl.getPsiElementFromMoquiUrl(curUrl).orElse(null);
                result.add(MoquiBaseReference.of(element,
                        locationTextRange,
                        curElement));
            }else {

                for (String itemName : pathNameArray) {
                    curUrl = MoquiUrl.getMoquiUrlByName(curUrl, itemName).orElse(null);
                    if (curUrl == null) return MoquiBaseReference.createNullRefArray(element, locationTextRange);

                    PsiElement curElement = MoquiUrl.getPsiElementFromMoquiUrl(curUrl).orElse(null);
                    if (curElement == null) return MoquiBaseReference.createNullRefArray(element, locationTextRange);

                    result.add(MoquiBaseReference.of(element,
                            new TextRange(startOffset, startOffset + itemName.length()),
                            curElement));

                    startOffset = startOffset + itemName.length() + 1;
                }
            }


             return result.toArray(new PsiReference[0]);
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

         public boolean isEmpty() {
             return isEmpty;
         }
     }

    public static @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        Project project = context.getProject();
        if(project ==null) return PsiReference.EMPTY_ARRAY;
        String url = value.getStringValue();
        if (url == null) return PsiReference.EMPTY_ARRAY;
        if(element.getContainingFile().getVirtualFile()==null) return PsiReference.EMPTY_ARRAY;


        LocationUtils.Location location = new LocationUtils.Location(project,url);
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
            location.type= LocationType.RelativeUrl;
        }


        switch(location.type) {
            case TransitionName -> {
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
                    //提示错误
                    return MoquiBaseReference.createNullRefArray(element,new TextRange(1,
                            url.length()+1));
                }


            }
            case RelativeUrl -> {
                Optional<PsiFile> file = location.getRelativeFile(MyDomUtils.getFilePathByPsiElement(element).orElse(MyStringUtils.EMPTY_STRING));//element.getContainingFile().getVirtualFile().getPath());
                //如果没有找到，可能是SubScreensItem
                if(file.isEmpty()) {
                    Optional<SubScreensItem> subScreensItem = ScreenUtils.getSubScreensItemByName(url,context);
                    if(subScreensItem.isPresent()) {
                        Location subScreensItemLocation = new Location(context.getProject(),
                                MyDomUtils.getValueOrEmptyString(subScreensItem.get().getLocation()));
                        file = Optional.ofNullable(subScreensItemLocation.getFile());
                        return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);
                    }else{ //相对路径，有可能找不到，不提示错误
//                        return MoquiBaseReference.createNullRefArray(element,new TextRange(1,url.length()+1));
                        return PsiReference.EMPTY_ARRAY;
                    }
                }else {
                    return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);
                }

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
                return location.createAbsoluteUrlPsiReference(element,context);

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

                    Optional<PsiFile> file = location.getRelativeFile(MyDomUtils.getFilePathByPsiElement(element).orElse(MyStringUtils.EMPTY_STRING));//element.getContainingFile().getVirtualFile().getPath());
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

        LocationUtils.Location location = new LocationUtils.Location(project, url);

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
        if(location.type == LocationType.Unknown) {//不知道的类型，尝试直接找文件
            psiFile = location.getFileByLocation().orElse(null);
        }

        if (psiFile == null) {
            if((location.type == LocationType.Unknown) || (location.type == LocationType.WebUrl)
                    || (location.type == LocationType.CamelPath)
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

        if(location.type!= LocationType.Unknown && MyStringUtils.isNotEmpty(location.getContentPart())) {
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
