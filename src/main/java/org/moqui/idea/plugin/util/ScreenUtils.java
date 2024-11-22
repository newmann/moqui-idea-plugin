package org.moqui.idea.plugin.util;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.icons.AllIcons;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

import static org.moqui.idea.plugin.util.MyDomUtils.*;


public final class ScreenUtils {
    private static final Logger LOGGER = Logger.getInstance(ScreenUtils.class);

    private ScreenUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isScreenFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Screen.TAG_NAME,Screen.ATTR_NoNamespaceSchemaLocation,Screen.VALULE_NoNamespaceSchemaLocation);
    }
    public static Icon getNagavitorToScreenIcon() {
        return MoquiIcons.NavigateToScreen; //MyIcons.NAVIGATE_TO_SCREEN;
    }
    public static String getNagavitorToEntityToolTips() {
        return "Navigating to Screen definition";
    }


    public static Boolean isTabScreenBySubScreenItems(@NotNull SubScreensItem subScreensItem){
        if(subScreensItem.getParent() instanceof Screen screen) {
            List<SubScreensPanel> subScreensPanelList = screen.getWidgets().getSubScreensPanelList();
            if(subScreensPanelList.isEmpty()) return false;

            SubScreensPanel subScreensPanel = screen.getWidgets().getSubScreensPanelList().get(0);

            if(subScreensPanel == null) return false;
            return MyDomUtils.getValueOrEmptyString(subScreensPanel.getType()).equals("tab");
        }else {
            return false;
        }
    }
    /**
     * 根据当前位置找到所在screen的所有可用的Transition
     *
     * @param context
     * @return
     */
    public static List<AbstractTransition> getAbstractTransitionListFromConvertContext(ConvertContext context) {

        List<AbstractTransition> result = new ArrayList<AbstractTransition>();


        Screen screen = ScreenUtils.getCurrentScreen(context).orElse(null);
        return getAbstractTransitionListFromScreen(screen);

//        if(screen != null){
//            result.addAll(screen.getTransitionList());
//            result.addAll(screen.getTransitionIncludeList());
//        }
//        return result;
    }
    public static List<AbstractTransition> getAbstractTransitionListFromScreen(@Nullable Screen screen) {

        List<AbstractTransition> result = new ArrayList<AbstractTransition>();

        if(screen != null){
            result.addAll(screen.getTransitionList());
            result.addAll(screen.getTransitionIncludeList());
        }
        return result;
    }

    /**
     * 根据当前位置找到所在screen的所有可用的SubScreensItem
     *
     * @param context
     * @return
     */
    public static List<SubScreensItem> getSubScreensItemList(ConvertContext context) {
        return getSubScreensItemList(getCurrentScreen(context).orElse(null));
    }
    public static List<SubScreensItem> getSubScreensItemList(PsiElement element) {

        return getSubScreensItemList(getCurrentScreen(element).orElse(null));
    }
    public static List<SubScreensItem> getSubScreensItemList(Screen screen) {

        List<SubScreensItem> result = new ArrayList<>();
        if(screen != null){
            result.addAll(screen.getSubScreensItemList());
            result.addAll(screen.getSubScreens().getSubScreensItemList());
        }
        return result;
    }

    public static Optional<SubScreensItem> getSubScreensItemByName(String itemName,ConvertContext context) {
        List<SubScreensItem> itemList = getSubScreensItemList(context);
        return itemList.stream().filter(
                item -> {
                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    return str.equals(itemName);
                }
        ).findFirst();
    }
    /**
     * 根据当前位置和名称对应的Transition
     * @param transitionName
     * @param context
     * @return
     */
    public static Optional<AbstractTransition> getAbstractTransitionFromConvertContextByName(String transitionName, ConvertContext context) {
        List<AbstractTransition> transitionList = getAbstractTransitionListFromConvertContext(context);
        return getAbstractTransitionFromListByName(transitionList,transitionName);
    }

    public static Optional<AbstractTransition> getAbstractTransitionFromListByName(@NotNull List<AbstractTransition> abstractTransitionList,@NotNull String transitionName){
        return abstractTransitionList.stream().filter(
                item->{
                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    return str.equals(transitionName);
                }
        ).findFirst();

    }
    /**
     * 根据location，找到对应的文件
     * location格式为：component://xxxx
     *
     * @param psiElement
     * @param location
     * @return
     */
    public static List<PsiElement> getRelatedScreenFile(@NotNull PsiElement psiElement, @NotNull String location) {
        List<PsiElement> resultList = new ArrayList<>();
        final Project project = psiElement.getProject();
        final String componentPreStr = "component://";
        String relativePathName;

        if(location.indexOf(componentPreStr)==0) {
            relativePathName = location.substring(componentPreStr.length());
        } else {
            relativePathName = location;
        }

        List<DomFileElement<Screen>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Screen.class);
        for(DomFileElement<Screen> fileElement : fileElementList) {
            if (fileElement.getFile().getVirtualFile().getPath().contains(relativePathName)){
                resultList.add(fileElement.getRootTag());

            }
        }

        return resultList;
    }

    public static List<PsiElement> getRelatedTransitionInclude(@NotNull PsiElement psiElement,
                                                               @NotNull String name,
                                                               @NotNull String location) {
        List<PsiElement> resultList = new ArrayList<>();
        final Project project = psiElement.getProject();

        Optional<DomFileElement<Screen>> optionalScreenDomFileElement = getScreenFileByLocation(project,location);

        if(optionalScreenDomFileElement.isPresent()) {
            Optional<Transition> optionalTransition = getTransitionFromDomFileElementByName(optionalScreenDomFileElement.get(),name);
            if(optionalTransition.isPresent()) {
                resultList.add(optionalTransition.get().getXmlElement());
            }

        }


        return resultList;
    }

    /**
     * 根據location，找到对应的DomFileElement
     * @param location
     * @return
     */
    public static  Optional<DomFileElement<Screen>> getScreenFileByLocation(@NotNull Project project, @NotNull String location){

        final String componentPreStr = "component://";
        String relativePathName;

        if(location.indexOf(componentPreStr)==0) {
            relativePathName = location.substring(componentPreStr.length());
        } else {
            relativePathName = location;
        }
//        return MyDomUtils.findDomFileElementsByRootClass(project, Screen.class)
//                .stream()
//                .filter(screenDomFileElement -> screenDomFileElement.getFile().getVirtualFile().getPath().contains(relativePathName))
//                .findFirst();

        List<DomFileElement<Screen>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(project, Screen.class);
        for(DomFileElement<Screen> fileElement : fileElementList) {
            if (fileElement.getFile().getVirtualFile().getPath().contains(relativePathName)){
                return Optional.of(fileElement);

            }
        }
        return Optional.empty();
    }

    public static Optional<Transition> getTransitionFromDomFileElementByName(@NotNull DomFileElement<Screen> fileElement, @NotNull String name){
        return fileElement.getRootElement().getTransitionList().stream()
                .filter(transition -> MyDomUtils.getValueOrEmptyString(transition.getName()).equals(name))
                .findFirst();
//        for(Transition transition: fileElement.getRootElement().getTransitionList()) {
//            if(MyDomUtils.getValueOrEmptyString(transition.getName()).equals(name)) {
//                return Optional.of(transition);
//            }
//        }
//        return Optional.empty();
    }
    public static List<Transition> getTransitionListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        return fileElement.getRootElement().getTransitionList();
    }

    /**
     * 获取某个Screen中所有的FormSingle定义
     * @param fileElement
     * @return
     */
    public static List<FormSingle> getFormSingleListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        Widgets widgets = fileElement.getRootElement().getWidgets();
        return DomUtil.getChildrenOf(widgets,FormSingle.class);
    }

    public static List<FormSingle> getFormSingleListFromScreenFile(PsiFile file){
        List<FormSingle> formSingleList = new ArrayList<>();
        List<XmlTag> formSingleTagList = new ArrayList<>();

        if(file instanceof XmlFile xmlFile) {
            XmlTag rootTag = xmlFile.getRootTag();
            if (rootTag!=null) {
                formSingleTagList = getSubTagList(rootTag,FormSingle.TAG_NAME);
            }
            for(XmlTag child : formSingleTagList) {
                DomElement domElement = DomUtil.getDomElement(child);
                if(domElement instanceof FormSingle formSingle) {
                    formSingleList.add(formSingle);
                }
            }
        }

        return formSingleList;

    }

    /**
     * 获取文件中所有Form定义，返回AbstractForm
     * @param file 所在文件
     * @return
     */
    public static List<AbstractForm> getAbstractFormFromScreenFile(PsiFile file){
        List<AbstractForm> abstractFormList = new ArrayList<>();
        List<XmlTag> formTagList = new ArrayList<>();

        if(file instanceof XmlFile xmlFile) {
            XmlTag rootTag = xmlFile.getRootTag();
            if (rootTag!=null) {
                formTagList.addAll(getSubTagList(rootTag,FormSingle.TAG_NAME));
                formTagList.addAll(getSubTagList(rootTag,FormList.TAG_NAME));
            }

            for(XmlTag child : formTagList) {
                DomElement domElement = DomUtil.getDomElement(child);
                if(domElement instanceof AbstractForm form) {
                    abstractFormList.add(form);
                }
            }
        }

        return abstractFormList;

    }
    public static Optional<AbstractForm> getAbstractFormFromScreenFileByName(@NotNull PsiFile file,@NotNull String formName){
        List<AbstractForm> abstractFormList = getAbstractFormFromScreenFile(file);
        return abstractFormList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formName);
        }).findFirst();
    }


    public static Optional<FormSingle> getFormSingleFromScreenFileByName(@NotNull DomFileElement<Screen> fileElement,@NotNull String formSingleName){
        List<FormSingle> formSingleList = getFormSingleListFromScreenFile(fileElement);
        return formSingleList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formSingleName);
        }).findFirst();
    }
    public static Optional<FormSingle> getFormSingleFromScreenFileByName(@NotNull PsiFile file,@NotNull String formSingleName){
        List<FormSingle> formSingleList = getFormSingleListFromScreenFile(file);
        return formSingleList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formSingleName);
        }).findFirst();
    }

    public static List<FormList> getFormListListFromScreenFile(@NotNull DomFileElement<Screen> fileElement){
        Widgets widgets = fileElement.getRootElement().getWidgets();
        return DomUtil.getChildrenOfType(widgets,FormList.class);
    }
    public static Optional<FormList> getFormListFromScreenFileByName(@NotNull DomFileElement<Screen> fileElement,@NotNull String formListName){
        List<FormList> formListList = getFormListListFromScreenFile(fileElement);
        return formListList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formListName);
        }).findFirst();
    }
    public static List<FormList> getFormListListFromScreenFile(PsiFile file){

        List<FormList> formListList = new ArrayList<>();
        List<XmlTag> formListTagList = new ArrayList<>();

        if(file instanceof XmlFile xmlFile) {
            XmlTag rootTag = xmlFile.getRootTag();
            if (rootTag!=null) {
                formListTagList = getSubTagList(rootTag,FormList.TAG_NAME);
            }
            for(XmlTag child : formListTagList) {
                DomElement domElement = DomUtil.getDomElement(child);
                if(domElement instanceof FormList formList) {
                    formListList.add(formList);
                }
            }
        }

        return formListList;

    }
    public static Optional<FormList> getFormListFromScreenFileByName(@NotNull PsiFile file,@NotNull String formListName){
        List<FormList> formSingleList = getFormListListFromScreenFile(file);
        return formSingleList.stream().filter(item->{
            final String name = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                    .orElse(MyStringUtils.EMPTY_STRING);
            return name.equals(formListName);
        }).findFirst();
    }
    /**
     * 用在Inspection中
     * 根据指定的属性来进行location位置的验证
     *
     * @param transitionInclude
     * @param holder
     */
    public static void inspectTransitionInclude(@NotNull TransitionInclude transitionInclude, @NotNull DomElementAnnotationHolder holder) {
//        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
//        if (xmlAttributeValue == null) { return;}
        final Project project =transitionInclude.getXmlElement().getProject();

        final String name = transitionInclude.getName().getValue();
        final String location = transitionInclude.getLocation().getValue();

        if (MyStringUtils.isEmpty(location)) {
            holder.createProblem(transitionInclude, HighlightSeverity.ERROR,"Transition location is not defined");
            return;

        }
        if (MyStringUtils.isEmpty(location)) {
            holder.createProblem(transitionInclude, HighlightSeverity.ERROR,"transition name is not defined");
            return;
        }

        final Optional<DomFileElement<Screen>> optionalScreenDomFileElement = getScreenFileByLocation(project,location);
        if(optionalScreenDomFileElement.isEmpty()) {
            holder.createProblem(transitionInclude.getLocation(), HighlightSeverity.ERROR,"Transition location is not found");
//                    TextRange.from(1,transitionInclude.getLocation().getXmlAttributeValue().getValueTextRange().getLength()));

        }else {
            final Optional<Transition> optionalTransition = getTransitionFromDomFileElementByName(optionalScreenDomFileElement.get(),name);

            if(optionalTransition.isEmpty()) {
                holder.createProblem(transitionInclude.getName(), HighlightSeverity.ERROR,"Transition name is not found");
//                        TextRange.from(1,transitionInclude.getName().getXmlAttributeValue().getValueTextRange().getLength()));

            }

        }

    }
    public static void inspectTransitionInclude(@NotNull TransitionInclude transitionInclude, @NotNull AnnotationHolder holder) {
//        XmlAttributeValue xmlAttributeValue = attributeValue.getXmlAttributeValue();
//        if (xmlAttributeValue == null) { return;}
        if(transitionInclude.getXmlElement() == null) return;

        final Project project =transitionInclude.getXmlElement().getProject();
        String name = MyStringUtils.EMPTY_STRING;
        XmlAttributeValue xmlAttributeName = transitionInclude.getName().getXmlAttributeValue();
        if(xmlAttributeName == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Transition name is not defined")
                    .range(transitionInclude.getXmlElement().getTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
        }else {
            name = xmlAttributeName.getValue();
            if (MyStringUtils.isEmpty(name)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Transition name is not defined")
                        .range(xmlAttributeName.getTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }

        }
        final String location;
        XmlAttributeValue xmlAttributeLocation = transitionInclude.getLocation().getXmlAttributeValue();
        if(xmlAttributeLocation == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Transition location is not defined")
                    .range(transitionInclude.getXmlElement().getTextRange())
                    .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                    .create();
        }else {
            location = xmlAttributeLocation.getValue();
            if (MyStringUtils.isEmpty(location)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Transition location is not defined")
                        .range(xmlAttributeLocation.getTextRange())
                        .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                        .create();
            }else {
                final Optional<DomFileElement<Screen>> optionalScreenDomFileElement = getScreenFileByLocation(project,location);
                if(optionalScreenDomFileElement.isEmpty()) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Transition location is not found")
                            .range(xmlAttributeLocation.getTextRange())
                            .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                            .create();
//                    TextRange.from(1,transitionInclude.getLocation().getXmlAttributeValue().getValueTextRange().getLength()));

                }else if(!MyStringUtils.isEmpty(name)) {
                    final Optional<Transition> optionalTransition = getTransitionFromDomFileElementByName(optionalScreenDomFileElement.get(),name);

                    if(optionalTransition.isEmpty()) {
                        holder.newAnnotation(HighlightSeverity.ERROR, "Transition name is not found")
                                .range(xmlAttributeName.getTextRange())
                                .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
                                .create();

//                        TextRange.from(1,transitionInclude.getName().getXmlAttributeValue().getValueTextRange().getLength()));

                    }

                }

            }

        }



    }

    /**
     * 获取FormSingle的Field定义，
     * @param abstractForm
     * @return
     */
    public static List<Field> getFieldListFromForm(@NotNull AbstractForm abstractForm){
        List<Field> result = new ArrayList<>();
        final String extendsStr = MyDomUtils.getXmlAttributeValueString(abstractForm.getExtends()).orElse(MyStringUtils.EMPTY_STRING);
        if(MyStringUtils.isNotEmpty(extendsStr)){
            //根据extends，找到FormSingle的定义，如果没有#，就是在本文件中找
            final int poundIndex = extendsStr.indexOf("#");
            final PsiFile psiFile;
            final String extendFormName;
            XmlElement abstractFormXmlElement = abstractForm.getXmlElement();
            if(abstractFormXmlElement == null) return  result;

            if(poundIndex>=0) {
                final String location = extendsStr.substring(0, poundIndex);
                extendFormName = extendsStr.substring(poundIndex + 1);

                psiFile = MyDomUtils.getFileFromLocation(abstractFormXmlElement.getProject(), location).orElse(null);
            }else {
                extendFormName = extendsStr;
                psiFile = abstractFormXmlElement.getContainingFile();
            }

            if(psiFile != null) {
                AbstractForm extendForm = getAbstractFormFromScreenFileByName(psiFile,extendFormName).orElse(null);

//                if(abstractForm instanceof FormSingle) {
//                    extendForm = getFormSingleFromScreenFileByName(psiFile, extendFormName).orElse(null);
//                }else {
//                    extendForm = getFormListFromScreenFileByName(psiFile, extendFormName).orElse(null);
//                }

                if(extendForm != null) {
                    //嵌套调用
                    result.addAll(getFieldListFromForm(extendForm));
                }

            }


        }

        result.addAll(abstractForm.getFieldList());
        return result;
    }

    public static Optional<Screen> getCurrentScreen(ConvertContext context){
        return getLocalDomElementByConvertContext(context,Screen.class);

    }
    public static Optional<Screen> getCurrentScreen(PsiElement element){
        return getLocalDomElementByPsiElement(element,Screen.class);
    }
    public static Optional<FormSingle> getCurrentFormSingle(ConvertContext context){
        return getLocalDomElementByConvertContext(context,FormSingle.class);

    }
    public static Optional<AbstractForm> getCurrentAbstractForm(ConvertContext context){
        return getLocalDomElementByConvertContext(context,AbstractForm.class);
    }

    public static Optional<FormList> getCurrentFormList(ConvertContext context){
        return getLocalDomElementByConvertContext(context,FormList.class);

    }

    public static class Menu{
        private Menu parent;
        private ArrayList<Menu> children;
        private Menu defaultChild;

        private SubScreensItem subScreensItem;//指向自己定义的地方，如果是子目录下，则这一项为null，只能从containingMoquiFile或containingDirectory中获取
        private String name;
        private String title;
        private Icon icon;
        private Boolean isHidden;

        private Integer menuIndex = 0;

        private LocationUtils.MoquiFile containingMoquiFile;
        private PsiDirectory containingDirectory;
//        public static Menu of(Screen screen){
//
//        }

        /**
         * 获取project的所有的menu
         */
        public static ArrayList<Menu> findAllMenuArrayList(@NotNull Project project){
            ArrayList<Menu> menus = new ArrayList<Menu>();

            List<Screen> screenList = MoquiConfUtils.getAllScreens(project);
            ApplicationManager.getApplication().runReadAction(()->{
                for(Screen screen : screenList){
                    for(SubScreensItem subScreensItem: screen.getSubScreensItemList()) {
                        ScreenUtils.Menu menu = ScreenUtils.Menu.of(subScreensItem);
                        menus.add(menu);
                    }
                }
            });
            //对menus进行排序
            return sortMenuArrayList(menus);

        }
        public static ArrayList<Menu> sortMenuArrayList(@NotNull ArrayList<Menu> menus){
            menus.sort(new Comparator<Menu>() {

                @Override
                public int compare(Menu menu, Menu t1) {
                    if (Objects.equals(t1.getMenuIndex(), menu.getMenuIndex())) {
                        return menu.getTitle().compareTo(t1.getTitle());
                    } else {
                        return menu.getMenuIndex() - t1.getMenuIndex();
                    }
                }
            });
            //对子菜单进行排序
            for(Menu menu: menus) {
                menu.setChildren(sortMenuArrayList(menu.getChildren()));
            }
            return menus;
        }
        /**
         * 根据当前的SubScreensItem获取所有的下属子菜单
         * @param screensItem
         * @return
         */
        public static Menu of(@NotNull SubScreensItem screensItem){
            if(screensItem.getXmlElement() == null){return null;}
            Menu result = new Menu();
            result.setSubScreensItem(screensItem);
            result.setName(MyDomUtils.getValueOrEmptyString(screensItem.getName()));
            String title = MyDomUtils.getValueOrEmptyString(screensItem.getMenuTitle());
            if(title.isEmpty()){title = result.name;}
            result.setTitle(title);
            result.setIcon(AllIcons.General.Add);

            result.setMenuIndex(MyDomUtils.getValueOrZero(screensItem.getMenuIndex()));

            String location = MyDomUtils.getValueOrEmptyString(screensItem.getLocation());
            //查找对应的文件
            PsiFile psiFile = MyDomUtils.getFileFromLocation(screensItem.getXmlElement().getProject(), location).orElse(null);
            if(psiFile != null) {
                LocationUtils.MoquiFile file = new LocationUtils.MoquiFile(psiFile);
                result.setContainingMoquiFile(file);


            }else {
                result.setContainingMoquiFile(null);
            }

            //如果所在的Screen中subscreens-panel定义了type为tab，则无需进一步获取子菜单，因为这种类型就是在当前界面中通过Tab的方式来显示所有子screen
            //不应该过滤掉，应该显示出来，因为不管是不是tab类型，都可以通过url来访问
//            if(! isTabScreenBySubScreenItems(screensItem)) {
                result.setChildren(getChildMenus(result));
//            }

            return result;
        }


        /**
         * 根据文件创建Menu
         * @param psiFile
         * @return
         */
        public static Menu of(@NotNull PsiFile psiFile){
            //如果psiFile不是Screen文件，则返回null
            if(!ScreenUtils.isScreenFile(psiFile)){return  null;}

            Menu result = new Menu();
            //查找对应的文件
            LocationUtils.MoquiFile file = new LocationUtils.MoquiFile(psiFile);
            Screen screen = MyDomUtils.convertPsiFileToDomFile(psiFile,Screen.class).getRootElement();
            String defaultMenuTitle = MyDomUtils.getValueOrEmptyString(screen.getDefaultMenuTitle());
            Integer defaultMenuIndex = MyDomUtils.getValueOrZero(screen.getDefaultMenuIndex());
            result.setIcon(AllIcons.FileTypes.Xml);

            result.setContainingMoquiFile(file);
            if(MyDomUtils.isMoquiXmlFile(psiFile)) {
                result.setName(file.getFileName());
                if(defaultMenuTitle.isEmpty()) {
                    result.setTitle(file.getFileName());
                }else {
                    result.setTitle(defaultMenuTitle);
                }
                result.setMenuIndex(defaultMenuIndex);

            }else {
                result.setName(file.getFileFullName());
                result.setTitle(file.getFileFullName());
            }
            result.setSubScreensItem(null);
            result.setContainingDirectory(null);

            ArrayList<Menu> children = new ArrayList<>();
            //查找文件中的SubScreens定义
            children.addAll(getChildMenusBySubScreens(result, screen.getSubScreens()));
            //查找同名文件夹下面的文件
            children.addAll(getChildMenusByPath(result,file.getContainingSubScreensPath()));

            result.setChildren(children);
            return result;

        }

        /**
         * 根据目录创建Menu
         * @param psiDirectory
         * @return
         */
        public static Menu of(@NotNull PsiDirectory psiDirectory){

            Menu result = new Menu();
//            //查找对应的文件
//            LocationUtils.MoquiFile file = new LocationUtils.MoquiFile(psiFile);
//            result.setContainingMoquiFile(file);

            result.setName(psiDirectory.getName());
            result.setTitle(psiDirectory.getName());
            result.setIcon(AllIcons.General.Print);

            result.setSubScreensItem(null);
            result.setContainingMoquiFile(null);

            result.setContainingDirectory(psiDirectory);
            String path = psiDirectory.getVirtualFile().getPath();
            //获取子菜单
            result.setChildren(getChildMenusByPath(result,path));
            return result;

        }
        /**
         * 获取当前
         * @param menu 当前的menu
         * @return
         */
        public static ArrayList<Menu> getChildMenus(@NotNull Menu menu){
            ArrayList<Menu> menus = new ArrayList<Menu>();
            LocationUtils.MoquiFile moquiFile = menu.getContainingMoquiFile();
            if(moquiFile == null) {
                return menus;
            }


            PsiFile file = menu.getContainingMoquiFile().getContainingFile();
            DomFileElement<Screen> screenDomFileElement = MyDomUtils.convertPsiFileToDomFile(file,Screen.class);
            if(screenDomFileElement == null) {
                LOGGER.warn(menu.name+"对应的文件没有找到");
                return menus;
            }

            Screen screen = screenDomFileElement.getRootElement();

            menus.addAll(getChildMenusBySubScreens(menu, screen.getSubScreens()));
            menus.addAll(getChildMenusByPath(menu, menu.getContainingMoquiFile().getContainingSubScreensPath()));

            return menus;
        }
        public static ArrayList<Menu> getChildMenusBySubScreens(@NotNull Menu menu,@NotNull SubScreens subScreens){
            return ApplicationManager.getApplication().runReadAction((Computable<ArrayList<Menu>>) ()->{
                ArrayList<Menu> menus = new ArrayList<Menu>();
                for(SubScreensItem item : subScreens.getSubScreensItemList()) {
                    Menu subMenu = of(item);
                    if(subMenu != null)  subMenu.setParent(menu);
                    menus.add(subMenu);
                }
                return menus;

            });
        }

        /**
         * 获取指定路径下的文件和子目录
         * 1、剔除后缀的文件名为menu的名字
         * 2、子目录名为menu名字
         * @param menu
         * @param path
         * @return
         */
        public static ArrayList<Menu> getChildMenusByPath(@NotNull Menu menu,@NotNull String path){
            LocationUtils.MoquiFile moquiFile = menu.getContainingMoquiFile();
            List<PsiFile> fileList;
            ArrayList<Menu> menus = new ArrayList<Menu>();

            Project project;
            if(moquiFile !=null) {
                project = moquiFile.getContainingFile().getProject();
            }else {
                if(menu.getContainingDirectory() == null) return menus;
                project = menu.getContainingDirectory().getProject();
            }

            fileList = MyDomUtils.findPsiFilesByPath(project, path);
            for(PsiFile item : fileList) {
                Menu subMenu = of(item);
                if(subMenu!=null) {
                    subMenu.setParent(menu);
                    menus.add(subMenu);
                }
            }

            //只有在特定情况下才需要处理子目录，比如静态文件，其他都不需要处理子目录
            //处理子目录
//            List<PsiDirectory> directoryList = MyDomUtils.findPsiDirectoriesByPath(project,path);
//            for(PsiDirectory directory : directoryList) {
//                Menu dirMenu = of(directory);
//                dirMenu.setParent(menu);
//                menus.add(dirMenu);
//            }

            return menus;

        }

        public ArrayList<Menu> getChildren() {
            return children;
        }

        public Menu getDefaultChild() {
            return defaultChild;
        }

        public Menu getParent() {
            return parent;
        }

        public Boolean getHidden() {
            return isHidden;
        }


        public String getName() {
            return name;
        }

        public Icon getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }

        public LocationUtils.MoquiFile getContainingMoquiFile() {
            return containingMoquiFile;
        }

        public SubScreensItem getSubScreensItem() {
            return subScreensItem;
        }

        public Integer getMenuIndex() {
            return menuIndex;
        }

        public void setChildren(ArrayList<Menu> children) {
            this.children = children;
        }

        public PsiDirectory getContainingDirectory() {
            return containingDirectory;
        }

        public void setDefaultChild(Menu defaultChild) {
            this.defaultChild = defaultChild;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setParent(Menu parent) {
            this.parent = parent;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setHidden(Boolean hidden) {
            isHidden = hidden;
        }

        public void setIcon(Icon icon) {
            this.icon = icon;
        }

        public void setContainingMoquiFile(LocationUtils.MoquiFile containingMoquiFile) {
            this.containingMoquiFile = containingMoquiFile;
        }

        public void setSubScreensItem(SubScreensItem subScreensItem) {
            this.subScreensItem = subScreensItem;
        }

        public void setMenuIndex(Integer menuIndex) {
            this.menuIndex = menuIndex;
        }

        public void setContainingDirectory(PsiDirectory containingDirectory) {
            this.containingDirectory = containingDirectory;
        }
    }

}
