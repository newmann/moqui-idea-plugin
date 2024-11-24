package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 处理moqui中文件路径和文件的专用类
 */
public final class LocationUtils {
    public static final String PATH_SEPARATOR = "/";
    private LocationUtils() {
        throw new UnsupportedOperationException();
    }

    public static final List<String> TAG_NAME_MUST_HAVE_LOCATION = List.of(
            IncludeScreen.TAG_NAME,SectionInclude.TAG_NAME,ServiceInclude.TAG_NAME,TransitionInclude.TAG_NAME,
            WidgetTemplateInclude.TAG_NAME
    );

    public static final List<String> HAVE_FILE_PATH_LOCATION_TAG_NAME = List.of(
            Screen.TAG_NAME,ScreenTextOutput.TAG_NAME
    );

    public static MoquiFile ofMoquiFile(@NotNull PsiFile containingFile){
        return new MoquiFile(containingFile);
    }
    public static final class MoquiFile{

        private final PsiFile containingFile;
        private final String path;//和idea中定义一样，含路径和名称
        private final String containingPath;//文件所在的最后一级路径

        private final String fileName;//文件名字
        private final String fileExtension;//文件扩展名

        private final String fileFullName;//文件全称，含文件名和扩展名

        private String componentName;//所在Component名称
        private Boolean isComponentFile = false;
        private Boolean isFrameworkFile = false;

        private String relativePath;//
        public PsiFile getContainingFile() {
            return containingFile;
        }
        MoquiFile(@NotNull PsiFile containingFile){
            this.containingFile = containingFile;
            VirtualFile file = containingFile.getVirtualFile();
            this.path = file.getPath();
            this.fileFullName = file.getName();
            int index = this.path.lastIndexOf(PATH_SEPARATOR);
            if(index < 0) {
                this.containingPath = MyStringUtils.EMPTY_STRING;
            }else {
                this.containingPath = this.path.substring(0,index);
            }
            index = this.fileFullName.lastIndexOf(".");
            if(index < 0) {
                this.fileName = this.fileFullName;
                this.fileExtension = MyStringUtils.EMPTY_STRING;
            }else {
                this.fileName = this.fileFullName.substring(0,index);
                this.fileExtension = this.fileFullName.substring(index + 1);
            }
            //获取component名称，
            // /runtime/base-component 表示基础component
            // /runtime/component 表示普通的component

            this.componentName = ComponentUtils.getComponentNameFromPath(this.path).orElse(MyStringUtils.EMPTY_STRING);
            if(this.componentName.isEmpty()) {
                isComponentFile = false;
                isFrameworkFile = false;
            }else if(this.componentName.equals(MyStringUtils.FRAMEWORK_COMPONENT_NAME)) {
                isComponentFile = false;
                this.componentName = MyStringUtils.EMPTY_STRING;
                isFrameworkFile = true;
            }else {
                isComponentFile = true;
                isFrameworkFile = false;
            }
            relativePath = extractComponentRelativePath(path).orElse(MyStringUtils.EMPTY_STRING);
//            String[] splits = this.path.split("/runtime/component/");
//            if(splits.length != 2) {
//                splits = this.path.split("/runtime/base-component/");
//            }
//            if(splits.length == 2) {
//                index = splits[1].indexOf(PATH_SEPARATOR);
//                if (index > 0) {
//                    this.componentName = splits[1].substring(0, index);
//                }else {
//                    this.componentName = splits[1];
//                }
//                isComponentFile = true;
//            }else {
//                this.componentName = MyStringUtils.EMPTY_STRING;
//                isComponentFile = false;
//                //判断是否在framework下面
//                isFrameworkFile = this.path.contains(MyStringUtils.FRAMEWORK_PATH_TAG);
//            }


        }
        public String getContainingSubScreensPath(){
            return this.containingPath +PATH_SEPARATOR + this.fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getPath() {
            return path;
        }
//       public void setContainingFile(PsiFile containingFile) {
//            this.containingFile = containingFile;
//        }
//
//        public void setPath(String path) {
//            this.path = path;
//        }
//
//
//        public void setFileName(String fileName) {
//            this.fileName = fileName;
//        }

        public String getComponentName(){return this.componentName;}

        public String getContainingPath() {
            return containingPath;
        }

        public String getFileExtension() {
            return fileExtension;
        }

        public String getFileFullName() {
            return fileFullName;
        }

        public Boolean getIsComponentFile() {
            return isComponentFile;
        }

        public Boolean getIsFrameworkFile() {
            return isFrameworkFile;
        }

        /**
         * 返回按component://方式的路径
         * @return
         */
        public String getComponentRelativePath() {
            if(this.relativePath.equals(MyStringUtils.EMPTY_STRING)) {
                return MyStringUtils.EMPTY_STRING;
            }else {
                return ComponentUtils.COMPONENT_LOCATION_PREFIX+relativePath;
            }
//            String[] splitArray;
//            if(path.contains(MyStringUtils.COMPONENT_PATH_TAG)) {
//                splitArray = path.split(MyStringUtils.COMPONENT_PATH_TAG);
//            }else if(path.contains(MyStringUtils.BASE_COMPONENT_PATH_TAG)) {
//                splitArray = path.split(MyStringUtils.BASE_COMPONENT_PATH_TAG);
//            }else if(path.contains(MyStringUtils.FRAMEWORK_PATH_TAG)) {
//                splitArray = path.split(MyStringUtils.FRAMEWORK_PATH_TAG);
//            }else {
//                return MyStringUtils.EMPTY_STRING;
//            }
//
//            if(splitArray.length == 2) {
//                return ComponentUtils.COMPONENT_LOCATION_PREFIX+splitArray[1];
//            }else {
//                return MyStringUtils.EMPTY_STRING;
//            }

        }

        public String getRelativePath() {
            return relativePath;
        }
    }

    /**
     * 在component下的相对位置，同时需要考虑framework
     * @param path
     * @return
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
     * @param path
     * @return
     */
    public static String simplifyComponentRelativePath(@NotNull String path){
//        int index = path.indexOf("/screen/");
//        if(index < 0) return path;
//
//        int lastSlashIndex = path.lastIndexOf("/");
//        return path.substring(0,index) + "@" + path.substring(lastSlashIndex);
        return path.replace("/screen/","@");
    }

    public enum LocationType{
        Unknown, //根据location无法判断具体类型的，需要根据所在的context，进一步判断，
        ComponentFile, //component://SimpleScreens/template/party/PartyForms.xml（指向一个文件 ）
        ComponentFileContent, //component://SimpleScreens/screen/SimpleScreens/Search.xml#SearchOptions（指向文件中的某个tag），
        RelativeScreenFile,//../EditFacility，同一个目录下的screen文件，
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
     public static final class Location{
        private LocationType type;
        private final String location; //传入的字符串
        private final Project project;
        private PsiFile file;

        private String pathPart;//#号前面的内容
        private  String contentPart;//#号后面的内容
        private String[] pathNameArray;

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

            if(location.startsWith("http://") || location.startsWith("https://")) {
                this.type = LocationType.WebUrl;
                return;
            }
            if(location.matches(MyStringUtils.CONTAIN_VARIABLE_REGEXP)) {
                this.type = LocationType.DynamicPath;
                return;
            }
            if(location.startsWith("component://")) {
                processComponentType(location);
                return;
            }
            if(location.matches(MyStringUtils.TRANSITION_NAME_REGEXP)) {
                this.type = LocationType.TransitionName;
                return;
            }
            if(location.matches(MyStringUtils.SCREEN_FILE_PATH_REGEXP)) {
                this.type = LocationType.RelativeScreenFile;
                return;
            }
             if(location.matches(MyStringUtils.ABSOLUTE_URL_REGEXP)) {
                 this.type = LocationType.AbsoluteUrl;
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
            String pathPart = location.substring("classpath://".length());

            //查找对应的文件
            file = MyDomUtils.getFileFromLocation(project, pathPart).orElse(null);
            //将路径分解，每一级目录都分别对应
            pathNameArray = MyDomUtils.getPathFileFromLocation(pathPart);

        }
        private void processComponentType(@NotNull String location){
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

//            tmp = pathPart.split("//")[1];
            //查找对应的文件
            file = MyDomUtils.getFileFromLocation(project, pathPart).orElse(null);
            //将路径分解，每一级目录都分别对应
            pathNameArray = MyDomUtils.getPathFileFromLocation(pathPart);
        }

         /**
          * 直接根据location获取可能存在的文件
          * @return
          */
        public Optional<PsiFile> getFileByLocation(){
            return MyDomUtils.getFileFromLocation(project, location);
        };

        public Optional<PsiFile> getRelativeFile(@NotNull String currentFilePathName){
            if(this.type != LocationType.RelativeScreenFile) return Optional.empty();

            String tmpString = currentFilePathName;
            //如果结尾是文件名需要将后缀去掉
            int tmpIndex = currentFilePathName.lastIndexOf(".xml");
            if(tmpIndex > 0) {
                tmpString = currentFilePathName.substring(0,tmpIndex);
            }
            this.pathNameArray = tmpString.split(PATH_SEPARATOR);
            ArrayList<String> targetFileArray = new ArrayList<>(Arrays.asList(this.pathNameArray));

            String[] relativeFileArray = this.location.split(PATH_SEPARATOR);
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
            String targetName = String.join(PATH_SEPARATOR, targetFileArray)+".xml";

            //获取psifile
            Optional<PsiFile> fileOptional = MyDomUtils.getFileFromLocation(project,targetName);

            this.file = fileOptional.orElse(null);
            return fileOptional;

        }

         /**
          * 为//component://SimpleScreens/screen/SimpleScreens/Search.xml#SearchOptions这种格式的路径创建Reference
          * @param element
          * @param context
          * @return
          */
         public  @NotNull PsiReference[] createComponentContentPsiReference( @NotNull PsiElement element,@NotNull ConvertContext context){
            //添加路径和文件的reference
            List<PsiReference> result = new ArrayList<>(Arrays.stream(createFilePsiReference(pathPart, element, file)).toList());
             //添加content部分的reference
             if(MyStringUtils.isNotEmpty(contentPart)) {
                 String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
                 String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
                 int startOffset = pathPart.length() + 2;
                 int endOffset = startOffset + contentPart.length();

                 //WidgetTemplateInclude（location）
                 if(attributeName.equals(WidgetTemplateInclude.ATTR_LOCATION) && firstTagName.equals(WidgetTemplateInclude.TAG_NAME)) {
                     WidgetTemplate widgetTemplate = WidgetTemplateUtils.getWidgetTemplateFromFileByName(file, contentPart)
                             .orElse(null);
                     if (widgetTemplate == null) return result.toArray(new PsiReference[0]);

                     result.add(new PsiRef(element,
                             new TextRange(startOffset, endOffset),
                             widgetTemplate.getName().getXmlAttributeValue()));
                 }
                 //FormSingle（extends）
                 if(attributeName.equals(FormSingle.ATTR_EXTENDS)  && firstTagName.equals(FormSingle.TAG_NAME)) {
                     FormSingle formSingle = ScreenUtils.getFormSingleFromScreenFileByName(file, contentPart)
                             .orElse(null);
                     if (formSingle == null) return result.toArray(new PsiReference[0]);

                     result.add(new PsiRef(element,
                             new TextRange(startOffset, endOffset),
                             formSingle.getName().getXmlAttributeValue()));
                 }
                 //FormList（extends）
                 if(attributeName.equals(FormList.ATTR_EXTENDS) && firstTagName.equals(FormList.TAG_NAME)) {
                     FormList formList = ScreenUtils.getFormListFromScreenFileByName(file, contentPart)
                             .orElse(null);
                     if (formList == null) return result.toArray(new PsiReference[0]);

                     result.add(new PsiRef(element,
                             new TextRange(startOffset, endOffset),
                             formList.getName().getXmlAttributeValue()));
                 }

             }
             return result.toArray(new PsiReference[0]);
         }

         /**
          * 同一个screen文件中的extend
          * @param element
          * @param context
          * @return
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

                 result.add(new PsiRef(element,
                         new TextRange(1, location.length()+1),
                         formSingle.getName().getXmlAttributeValue()));
             }

             if(attributeName.equals(FormList.ATTR_EXTENDS)  &&
                     firstTagName.equals(FormList.TAG_NAME)
             ) {

                 FormList formList = ScreenUtils.getFormListFromScreenFileByName(psiFile, location)
                         .orElse(null);
                 if (formList == null) return result.toArray(new PsiReference[0]);

                 result.add(new PsiRef(element,
                         new TextRange(1, location.length()+1),
                         formList.getName().getXmlAttributeValue()));
             }


             return result.toArray(new PsiReference[0]);
         }

         /**
          * 绝对路径的Reference
          * @param element
          * @param context
          * @return
          */
         public  @NotNull PsiReference[] createAbsoluteUrlPsiReference( @NotNull PsiElement element,@NotNull ConvertContext context) {
             //将开头的两个"//"字符剔除
             String[] pathArray = location.substring(2).split(PATH_SEPARATOR);


            return PsiReference.EMPTY_ARRAY;
         }

//        public Optional<PsiFile> getComponentFile(){
//            if(type != LocationType.ComponentFile) return Optional.empty();
//            Optional<PsiFile> fileOptional = MyDomUtils.getFileFromLocation(project,location);
//            this.file = fileOptional.orElse(null);
//            return fileOptional;
//        }

         public String getLocation() {
             return location;
         }

         public LocationType getType() {
             return type;
         }

         public PsiFile getFile() {
             return file;
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
//        String valueStr = value.getStringValue();
//        if(valueStr ==null) return PsiReference.EMPTY_ARRAY;
        Project project = context.getProject();
        if(project ==null) return PsiReference.EMPTY_ARRAY;
        String url = value.getStringValue();
        if (url == null) return PsiReference.EMPTY_ARRAY;
        LocationUtils.Location location = new LocationUtils.Location(project,url);

        switch(location.type) {
            case TransitionName -> {
                Optional<AbstractTransition> optTransition = ScreenUtils.getAbstractTransitionFromConvertContextByName(url,context);
                if (optTransition.isEmpty()) return PsiReference.EMPTY_ARRAY;

                final AbstractTransition transition = optTransition.get();
                PsiReference[] psiReferences = new PsiReference[1];
                psiReferences[0] = new PsiRef(element,
                        new TextRange(1,
                                url.length()+1),
                        transition.getName().getXmlAttributeValue());

                return psiReferences;

            }
            case RelativeScreenFile -> {
                Optional<PsiFile> file = location.getRelativeFile(element.getContainingFile().getVirtualFile().getPath());
                //如果没有找到，可能是SubScreensItem
                if(file.isEmpty()) {
                    Optional<SubScreensItem> subScreensItem = ScreenUtils.getSubScreensItemByName(url,context);
                    if(subScreensItem.isPresent()) {
                        Location subScreensItemLocation = new Location(context.getProject(),
                                MyDomUtils.getValueOrEmptyString(subScreensItem.get().getLocation()));
                        file = Optional.ofNullable(subScreensItemLocation.getFile());
                    }else{ //提示错误
                        PsiReference[] psiReferences = new PsiReference[1];
                        psiReferences[0] = new PsiRef(element,
                                new TextRange(1,
                                        url.length()+1),
                                null);
                        return psiReferences;
                    }
                }
                return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);

            }
            case ComponentFile, ClassPath -> {
                if(location.file == null) return PsiReference.EMPTY_ARRAY;
                return createFilePsiReference(url,element,location.file);
            }
            case ComponentFileContent -> {
                if(location.file == null) return PsiReference.EMPTY_ARRAY;
                return location.createComponentContentPsiReference(element,context);
            }

            case WebUrl, CamelPath -> {
                return PsiReference.EMPTY_ARRAY;
            }
            default -> {
                //需要根据context进一步判断
                String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
                String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
                if(attributeName.equals(FormSingle.ATTR_EXTENDS)  &&
                        (firstTagName.equals(FormSingle.TAG_NAME) || firstTagName.equals(FormList.TAG_NAME))
                ) {
                    //为扩展所在文件的form名称
                    return location.createLocalFormExtendPsiReference(element,context);
                }

                if(attributeName.equals(AttListResponse.ATTR_URL)
                ) {
                    //为URL的相对路径
                    if(element.getContainingFile().getVirtualFile() == null) return PsiReference.EMPTY_ARRAY;

                    Optional<PsiFile> file = location.getRelativeFile(element.getContainingFile().getVirtualFile().getPath());
                    return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);
                }

                //在一些tag中，属性指向一个文件
                if(HAVE_FILE_PATH_LOCATION_TAG_NAME.contains(firstTagName)) {
                    return location.getFileByLocation()
                            .map(psiFile -> createFilePsiReference(url, element, psiFile))
                            .orElse(PsiReference.EMPTY_ARRAY);
                }

                return PsiReference.EMPTY_ARRAY;

            }
        }


    }

    /**
     * 为文件创建对应的Reference，文件名是对应到具体文件，每一级的文件夹也对应到相应的文件夹
     * @param attributeString
     * @param element
     * @param file
     * @return
     */
    public static @NotNull PsiReference[] createFilePsiReference(@NotNull String attributeString, @NotNull PsiElement element, @NotNull PsiFile file){

        //将路径分解，每一级目录都分别对应
        String[] pathFileArray = MyDomUtils.getPathFileFromLocation(attributeString);

        final int pathFileArrayLength = pathFileArray.length;

        List<PsiReference> result = new ArrayList<>();

        //Service(location)，
        //TransitionInclude(location)
        //Transition，defaultResponse（url）


        int startIndex = attributeString.lastIndexOf(PATH_SEPARATOR);
        if(startIndex<0) {
            //沒有其他的目录
            result.add(new PsiRef(element,
                    new TextRange(1, attributeString.length()+1),
                    file));
        }else {

            result.add(new PsiRef(element,
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

                result.add( new PsiRef(element,
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
     * 根据当前的PsiElement，找到相对路径下所有的文件
     * @param psiElement
     * @return
     */
    public static List<PsiFile> getRelativeScreenFileList(@NotNull PsiElement psiElement) {

        PsiFile file = psiElement.getContainingFile().getOriginalFile();
        String relativePath = MyStringUtils.removeLastDotString(file.getVirtualFile().getPath());

//        int index =relativePath.lastIndexOf(".xml");
//        if(index > 0) {
//            relativePath = relativePath.substring(0,index);
//        }
        List<PsiFile> result = new ArrayList<PsiFile>();
        MyDomUtils.findPsiFilesByPath(psiElement.getProject(),relativePath)
                .forEach(item->{
                    if(ScreenUtils.isScreenFile(item)) {
                        result.add(item);
                    }
                });
        return result;

    }
    /**
     * 对location进行检查
     * AbstractLocation：
     *  widget-template-include（location）、transition-include（location）、screen（location）
     *  subscreen-item（location）、section-include ( location  )、text （location）
     *
     *  AbStractForm：
     *  form-single (extends)、form-list （extends）
     *
     * @param element
     * @param holder
     */
    public static void inspectAbstractLocationLocation(@NotNull DomElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof AbstractLocation abstractLocation)) {
            return;
        }
        if(element.getXmlElement() == null) return;
//        Project project = element.getXmlElement().getProject();
//
//        String url;
        XmlAttributeValue locationAttr;

        if(element instanceof AbstractForm abstractForm) {
            locationAttr = abstractForm.getExtends().getXmlAttributeValue();
        }else{
            locationAttr = abstractLocation.getLocation().getXmlAttributeValue();
        }

        inspectLocationFromAttribute(element,locationAttr,holder);
    }

    /**
     * 检查某个Tag的包含Location的Attribute
     * @param element 指定的Tag
     * @param locationAttr 指定的Attribute
     * @param holder
     */
    public static void inspectLocationFromAttribute(@NotNull DomElement element, @Nullable XmlAttributeValue locationAttr, @NotNull AnnotationHolder holder) {

        if(locationAttr == null) return;

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
        };

        PsiFile psiFile = location.getFile();
        if(location.type == LocationType.Unknown) {//不知道的类型，尝试直接找文件
            psiFile = location.getFileByLocation().orElse(null);
        }

        if (psiFile == null) {
//                holder.createProblem(attributeValue, ProblemHighlightType.ERROR, "没有找到文件：" + locationDescriptor.getFilePath(),
//                        TextRange.from(1, locationDescriptor.getFilePath().length()));
            holder.newAnnotation(HighlightSeverity.ERROR, "根据路径["+ location.getLocation() +"]找不到对应的文件")
                    .range(locationAttr.getValueTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
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
}
