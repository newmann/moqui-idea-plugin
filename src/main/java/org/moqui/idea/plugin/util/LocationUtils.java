package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
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
    private LocationUtils() {
        throw new UnsupportedOperationException();
    }

    public static final class MoquiFile{
        private final PsiFile containingFile;
        private final String path;//和idea中定义一样，含路径和名称
        private final String containingPath;//文件所在的最后一级路径

        private final String fileName;//文件名字
        private final String fileExtension;//文件扩展名

        private final String fileFullName;//文件全称，含文件名和扩展名

        private final String componentName;//所在Component名称
        private Boolean isComponentFile = false;
        private Boolean isFrameworkFile = false;

        public PsiFile getContainingFile() {
            return containingFile;
        }
        MoquiFile(@NotNull PsiFile containingFile){
            this.containingFile = containingFile;
            VirtualFile file = containingFile.getVirtualFile();
            this.path = file.getPath();
            this.fileFullName = file.getName();
            int index = this.path.lastIndexOf("/");
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

            String[] splits = this.path.split("/runtime/component/");
            if(splits.length != 2) {
                splits = this.path.split("/runtime/base-component/");
            }
            if(splits.length == 2) {
                index = splits[1].indexOf("/");
                if (index > 0) {
                    this.componentName = splits[1].substring(0, index);
                }else {
                    this.componentName = splits[1];
                }
                isComponentFile = true;
            }else {
                this.componentName = MyStringUtils.EMPTY_STRING;
                isComponentFile = false;
                //判断是否在framework下面
                isFrameworkFile = this.path.contains("/framework/");
            }


        }
        public String getContainingSubScreensPath(){
            return this.containingPath +"/" + this.fileName;
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

        public Boolean getComponentFile() {
            return isComponentFile;
        }

        public Boolean getFrameworkFile() {
            return isFrameworkFile;
        }
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

        public Location(@NotNull Project project, @NotNull String currentLocation,@NotNull String relativeLocation){
            this(project,currentLocation);
            getRelativeFile(relativeLocation);
        }

         public Location(@NotNull Project  project,@NotNull String location){
            this.project = project;
            this.location = location;
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

            this.type = LocationType.Unknown;
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
        public Optional<PsiFile> getRelativeFile(@NotNull String currentFilePathName){
            if(this.type != LocationType.RelativeScreenFile) return Optional.empty();

            String tmpString = currentFilePathName;
            //如果结尾是文件名需要将后缀去掉
            int tmpIndex = currentFilePathName.lastIndexOf(".xml");
            if(tmpIndex > 0) {
                tmpString = currentFilePathName.substring(0,tmpIndex);
            }
            this.pathNameArray = tmpString.split("/");
            ArrayList<String> targetFileArray = new ArrayList<>(Arrays.asList(this.pathNameArray));

            String[] relativeFileArray = this.location.split("/");
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
            String targetName = String.join("/", targetFileArray)+".xml";

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
             String[] pathArray = location.substring(2).split("/");


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

         public String[] getPathNameArray() {
             return pathNameArray;
         }
     }

    public static final class LocationDescriptor{

        private final String location;
        private String filePath = MyStringUtils.EMPTY_STRING;
        private String contentName = MyStringUtils.EMPTY_STRING;
        private int contentStartIndex;
        public LocationDescriptor(@NotNull String location){
            this.location = location;
            if(MyStringUtils.isEmpty(location)) {
                isEmpty = true;
            }else {
                isEmpty = false;
                if(location.contains("${")) {
                    isVariable = true;
                    return;
                }
                int poundIndex = location.indexOf("#");
                if(poundIndex>=0) {
                    isContent = true;
                    contentStartIndex = poundIndex + 1;
                    contentName = location.substring(poundIndex+1);
                    filePath = location.substring(0,poundIndex);
                }else {
                    if(location.contains("//")) {

                        filePath = location;
                    }else {
                        //可能是FormSingle或FormList的extends本文件中的名称
                        isContent = true;
                        contentStartIndex = 1;
                        contentName = location;
                    }
                }

            }
        }

        private boolean isEmpty = false;
        /**
         * Location中包含有${}的变量
         */
        private boolean isVariable = false;

        /**
         * Location中带有#，指向一个文件内部的内容
         */
        private boolean isContent =false;

        public String getLocation() {return location;}

        public String getContentName() {
            return contentName;
        }

        public String getFilePath() {
            return filePath;
        }

        public boolean isVariable() {
            return isVariable;
        }

        public boolean isContent() {
            return isContent;
        }

        public boolean isEmpty() {
            return isEmpty;
        }

        /**
         * 根据location，获取在location中定义的path和fileName，并按先后次序添加到Array中
         * @return
         */
        public String[] getPathFileFromLocation(){
            return MyDomUtils.getPathFileFromLocation(location);
        }

        public int getContentStartIndex() {
            return contentStartIndex;
        }
    }


    public static @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        String valueStr = value.getStringValue();
        if(valueStr ==null) return PsiReference.EMPTY_ARRAY;

        String url = value.getStringValue();
        if (url == null) return PsiReference.EMPTY_ARRAY;
        LocationUtils.Location location = new LocationUtils.Location(context.getProject(),url);

        switch(location.type) {
            case TransitionName -> {
                Optional<AbstractTransition> optTransition = ScreenUtils.getTransitionByName(url,context);
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
                    }
                }
                return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);

            }
            case ComponentFile -> {
                if(location.file == null) return PsiReference.EMPTY_ARRAY;
                return createFilePsiReference(url,element,location.file);
            }
            case ComponentFileContent -> {
                if(location.file == null) return PsiReference.EMPTY_ARRAY;
                return location.createComponentContentPsiReference(element,context);
            }

            case WebUrl -> {
                return PsiReference.EMPTY_ARRAY;
            }
            case CamelPath -> {
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
                    Optional<PsiFile> file = location.getRelativeFile(element.getContainingFile().getVirtualFile().getPath());
                    return file.map(psiFile -> createFilePsiReference(url, element, psiFile)).orElse(PsiReference.EMPTY_ARRAY);
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


        int startIndex = attributeString.lastIndexOf("/");
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

}
