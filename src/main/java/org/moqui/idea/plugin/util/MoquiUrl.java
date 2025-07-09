package org.moqui.idea.plugin.util;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.SubScreensItem;

import javax.swing.*;
import java.util.*;

/**
 * 处理Url相关的工具类
 * 从MoquiConf.xml的定义开始查找
 *
 */
public class MoquiUrl {

    private static final Logger LOGGER = Logger.getInstance(MoquiUrl.class);

    private MoquiUrl parent;
    private ArrayList<MoquiUrl> children = new ArrayList<>();
//    private MoquiUrl defaultChild;//原来设想指向缺省目录，现在暂时用不上

    private SubScreensItem subScreensItem;//指向自己定义的地方，如果是子目录下，则这一项为null，只能从containingMoquiFile或containingDirectory中获取
    private Screen screen; //对应的screen tag，在MoquiConf.xml中有定义，也可能是Screen.xml文件中的root tag。
    private String name;
    private String title;
    private Icon icon;
    private Boolean isHidden;

    private Integer urlIndex = 0;

    private MoquiFile containingMoquiFile;
    private PsiDirectory containingDirectory;
    private Project project;

    private boolean fetchAllChildUrls = false; //是否获取所有子菜单

    private boolean childUrlHaveBeenFetched = false; //是否已经获取过子菜单
    /**
     * 获取project的所有的menu
     * 只有在ROOT_SCREEN_LOCATION的Screen中定义的SubScreensItem才会被认为是顶级菜单，包括该文件对应的目录下Screen文件
     */
    public static ArrayList<MoquiUrl> getAllUrlArrayList(@NotNull Project project) {
        ArrayList<MoquiUrl> menus = new ArrayList<>();

        MoquiUrl url = ofRootScreenUrl(project, true);

        if (url != null) {
            menus.addAll(url.getChildren());
        }
        //对menus进行排序
        return sortUrlArrayList(menus);

    }

    /**
     * 根据MoquiUrl的urlIndex和title进行排序
     * @param menus ArrayList<MoquiUrl>
     * @return ArrayList<MoquiUrl>
     */
    public static ArrayList<MoquiUrl> sortUrlArrayList(@NotNull ArrayList<MoquiUrl> menus) {
        menus.sort((MoquiUrl, t1) -> {
            if (Objects.equals(t1.getUrlIndex(), MoquiUrl.getUrlIndex())) {
                return MoquiUrl.getTitle().compareTo(t1.getTitle());
            } else {
                return MoquiUrl.getUrlIndex() - t1.getUrlIndex();
            }
        });
        //对子菜单进行排序
        for (MoquiUrl MoquiUrl : menus) {
            MoquiUrl.setChildren(sortUrlArrayList(MoquiUrl.getChildren()));
        }
        return menus;
    }

    /**
     * 根据当前的SubScreensItem获取所有的下属子菜单
     */
    public static MoquiUrl ofSubScreensItem(@NotNull SubScreensItem screensItem){
        return ofSubScreensItem(screensItem,true);
    }
    public static MoquiUrl ofSubScreensItem(@NotNull SubScreensItem screensItem, boolean fetchAllChildUrls) {
        if (screensItem.getXmlElement() == null) {
            return null;
        }
        MoquiUrl result = new MoquiUrl();
        result.setProject(screensItem.getXmlElement().getProject());
        result.setFetchAllChildUrls(fetchAllChildUrls);

        result.setSubScreensItem(screensItem);
        result.setName(MyDomUtils.getValueOrEmptyString(screensItem.getName()));
        String title = MyDomUtils.getValueOrEmptyString(screensItem.getMenuTitle());
        if (title.isEmpty()) {
            title = result.name;
        }
        result.setTitle(title);
        result.setIcon(AllIcons.General.Add);

        result.setUrlIndex(MyDomUtils.getValueOrZero(screensItem.getMenuIndex()));

        String location = MyDomUtils.getValueOrEmptyString(screensItem.getLocation());

        //查找对应的文件
        final PsiFile psiFile = LocationUtils.getFileFromLocation(result.getProject(), location).orElse(null);
        if (psiFile != null) {
            MoquiFile file = MoquiFile.of(psiFile);
            result.setContainingMoquiFile(file);

            DomFileElement<Screen> domFile = MyDomUtils.convertPsiFileToDomFile(psiFile,Screen.class);
            if (domFile != null) {
                result.setScreen(domFile.getRootElement());
            } else {
                result.setScreen(null);
            }
        } else {
            result.setContainingMoquiFile(null);
        }

        //如果所在的Screen中subscreens-panel定义了type为tab，则无需进一步获取子菜单，因为这种类型就是在当前界面中通过Tab的方式来显示所有子screen
        //不应该过滤掉，应该显示出来，因为不管是不是tab类型，都可以通过url来访问
//            if(! isTabScreenBySubScreenItems(screensItem)) {
        if(fetchAllChildUrls) {
            result.fetchChildUrls();
        }
//            }

        return result;
    }

    /**
     * 路径为/对应的MoquiUrl
     * 用component://webroot/screen/webroot.xml生成菜单即可
     * @param project 当前Project
     * @param fetchAllChildUrls 是否获取所有子菜单
     * @return MoquiUrl
     */
    public static MoquiUrl ofRootScreenUrl(@NotNull Project project, boolean fetchAllChildUrls) {
        return ofString(project, MyStringUtils.ROOT_SCREEN_LOCATION, fetchAllChildUrls);
    }

    /**
     * 路径为//对应的MoquiUrl，
     * 由于//apps/hm/..和//hm/.. 都合法，所以需要个性化处理
     * 即将MoquiConf中定义的component://webroot/screen/webroot/apps.xml下所有的子目录和apps本身都放到同一级别
     * 这里只需要将文件路径传入即可， 在PsiFile的处理中进行处理
     * @param project 当前Project
     * @param fetchAllChildUrls 是否获取所有子菜单
     * @return MoquiUrl
     */
    public static MoquiUrl ofBaseScreenUrl(@NotNull Project project, boolean fetchAllChildUrls) {

        MoquiUrl moquiUrl = ofString(project,MyStringUtils.BASE_SCREEN_LOCATION,fetchAllChildUrls);
        if(moquiUrl == null) {
            LOGGER.warn("Could not find base screen url for project: " + project.getName());
            return null;
        }
        if(!moquiUrl.childUrlHaveBeenFetched) moquiUrl.getNextLevelChildren();
        moquiUrl.addChildUrl(moquiUrl);
        return moquiUrl;
    }

    /**
     * 根据Project和location获取MoquiUrl，
     *
     * @param project 当前Project
     * @param locationStr 路径
     * @param fetchAllChildUrls 是否获取所有子菜单
     * @return MoquiUrl
     */
    public static MoquiUrl ofString(@NotNull Project project, @NotNull String locationStr, boolean fetchAllChildUrls) {
        Location location = Location.of(project, locationStr);
        switch (location.getType()) {
            case AbsoluteUrl -> {
                return ofAbsoluteUrl(location, fetchAllChildUrls);
            }
            case ComponentFile,ComponentFileContent -> {
                PsiFile psiFile = ComponentUtils.getPsiFileByLocation(location).orElse(null);
                if(psiFile == null) return null;
                return ofPsiFile(psiFile, fetchAllChildUrls);
            }
            default -> {
                LOGGER.warn("Location type is not AbsoluteUrl or ComponentFile, cannot create MoquiUrl from location: " + locationStr);
                return null;
            }
        }

//        Location webrootFile = LocationUtils.ofLocation(project,location);
//        if(webrootFile.getFile()!= null) {
//            return of(webrootFile.getFile(),fetchAllChildUrls);
//        }else {
//            return null;
//        }
    }

    /**
     * 根据AbsoluteUrl类型的Location获取MoquiUrl
     * @param location AbsoloteUrl
     * @param fetchAllChildUrls 是否获取所有子菜单
     * @return MoquiUrl
     */
    public static MoquiUrl ofAbsoluteUrl(@NotNull Location location, boolean fetchAllChildUrls) {
        if(location.getType() != LocationType.AbsoluteUrl) {
            LOGGER.warn("Location type is not AbsoluteUrl, cannot create MoquiUrl from location: " + location.getLocation());
            return null;
        }

//        MoquiUrl curUrl = MoquiUrl.ofRootScreenUrl(location.getProject(), false);
        MoquiUrl curUrl = null;
        if(location.getHeadContent().equals(MyStringUtils.ROOT_URL)) {
             curUrl= MoquiUrl.ofRootScreenUrl(location.getProject(), fetchAllChildUrls);
        }else if(location.getHeadContent().equals(MyStringUtils.BASE_URL)) {
            curUrl = MoquiUrl.ofBaseScreenUrl(location.getProject(), fetchAllChildUrls);
        }

        if(curUrl == null) return null;

        if(location.getPathNameArray().length == 0){
            return  curUrl;
        }else {
            for (int i =0; i < location.getPathNameArray().length; i++ ) {
                String itemName = location.getPathNameArray()[i];

                curUrl = curUrl.getChildUrlByName(itemName).orElse(null);
                if (curUrl == null) {
                    LOGGER.warn("在绝对路径"+ location.getLocation() + "中根据itemName" +itemName + "找不到对应的MoquiUrl");
                    return null;
                }
            }

            return curUrl;
        }

    }

    /**
     * 根据Screen所在的文件创建Menu
     *
     * @param psiFile Screen所在的PsiFile
     * @return MoquiUrl
     */
    public static MoquiUrl ofPsiFile(@NotNull PsiFile psiFile){
        return ofPsiFile(psiFile,true);
    }

    /**
     * 根据文件创建MoquiUrl
     * 可以不是Screen类型的file
     * @param psiFile PsiFile
     * @param fetchAllChildUrls 是否获取所有子菜单
     * @return MoquiUrl
     */
    public static MoquiUrl ofPsiFile(@NotNull PsiFile psiFile, boolean fetchAllChildUrls) {
        //如果psiFile不是Screen文件，则返回null
//        if (!ScreenUtils.isScreenFile(psiFile)) {
//            return null;
//        }

        MoquiUrl result = new MoquiUrl();

        result.setProject(psiFile.getProject());
        result.setFetchAllChildUrls(fetchAllChildUrls);
        //查找对应的文件
        final MoquiFile file = new MoquiFile(psiFile);
        DomFileElement<Screen> domFileElement = MyDomUtils.convertPsiFileToDomFile(psiFile, Screen.class);
        if(domFileElement == null) {
            result.setScreen(null);
            result.setName(file.getFileFullName());
            result.setTitle(file.getFileFullName());
            result.setIcon(AllIcons.FileTypes.Any_type);
        }else{

            final Screen screen = domFileElement.getRootElement();

            String defaultMenuTitle = MyDomUtils.getValueOrEmptyString(ReadAction.compute(screen::getDefaultMenuTitle));
            Integer defaultMenuIndex = MyDomUtils.getValueOrZero(ReadAction.compute(screen::getDefaultMenuIndex));
            result.setIcon(MyIcons.ScreenTag);

            result.setName(file.getFileName());
            if (defaultMenuTitle.isEmpty()) {
                result.setTitle(file.getFileName());
            } else {
                result.setTitle(defaultMenuTitle);
            }

            result.setUrlIndex(defaultMenuIndex);
            result.setScreen(screen);
        }

        result.setContainingMoquiFile(file);
        result.setSubScreensItem(null);
        result.setContainingDirectory(null);

        if(fetchAllChildUrls) {
            result.fetchChildUrls();
        }


        return result;

    }

    /**
     * 根据目录创建Menu
     */
    public static MoquiUrl ofPsiDirectory(@NotNull PsiDirectory psiDirectory){
        return ofPsiDirectory(psiDirectory,true);
    }
    public static MoquiUrl ofPsiDirectory(@NotNull PsiDirectory psiDirectory, boolean fetchAllChildUrls) {

//查找对应的Screen文件，如果存在，则按照Screen文件的定义来创建MoquiUrl
        String fileName = psiDirectory.getVirtualFile().getPath() + ".xml";
        PsiFile psiFile = LocationUtils.getPsiFileByPathName(psiDirectory.getProject(), fileName).orElse(null);
        if(psiFile == null) {
            MoquiUrl result = new MoquiUrl();

            result.setProject(psiDirectory.getProject());
            result.setFetchAllChildUrls(fetchAllChildUrls);

            result.setName(psiDirectory.getName());
            result.setTitle(psiDirectory.getName());
            result.setIcon(AllIcons.Nodes.Folder);

            result.setSubScreensItem(null);
            result.setScreen(null);
            result.setContainingMoquiFile(null);

            result.setContainingDirectory(psiDirectory);
            //获取子菜单
            if(fetchAllChildUrls)
                result.fetchChildUrls();
            return result;
        }else {
            return ofPsiFile(psiFile,fetchAllChildUrls);
        }
    }

    /**
     * 根据PsiElement所在的文件MoquiUrl
     * @param psiElement 当前PsiElement，从而获取所在文件
     * @param fetchAllChildUrls 是否获取所有子菜单
     * @return MoquiUrl
     */
    public static MoquiUrl ofPsiFile(@NotNull PsiElement psiElement, boolean fetchAllChildUrls) {
        PsiFile psiFile = MyDomUtils.getPsiFileFromPsiElement(psiElement).orElse(null);
        if(psiFile == null) {
            //如果PsiElement所在的文件是MoquiFile，则获取对应的目录
            LOGGER.warn("Could not find file by PsiElement: " + psiElement);
            return null;
        }
        return ofPsiFile(psiFile,fetchAllChildUrls);

    }

    /**
     * 根据PsiElement所在的文件和相对路径创建MoquiUrl
     * 相对路径 . 表示当前PsiElement所在的文件名称对应的目录
     * 相对路径 .. 表示当前PsiElement所在的文件名称对应目录的上级目录
     * 例子：../OrderDetail 表示和当前PsiElement所在的文件名称对应目录的上级目录下的OrderDetail文件
     * ../Product/EditProduct 表示和当前PsiElement所在的文件名称目录的上级目录下的Product.xml的子Url（EditProduct，可能是EditProduct.xml文件，也可能是在Product.xml定义的SubScreen-item）
     * ../../OrderDetail 找到PsiElement所在文件的文件名称对应目录，然后找到上级目录，再找上级目录，再找该目录下的OrderDetail.xml文件
     * 查找逻辑是：
     * 根据当前PsiElement所在的文件名称对应的目录，根据开始的相对路径找到对应的Screen文件，然后再从Screen文件开始找子Url。
     *
     * @param psiElement 当前PsiElement，从而获取所在文件
     * @param relativeUrl 相对与psiElement所在文件路径
     * @param fetchAllChildUrls 是否获取所有子菜单
     * @return MoquiUrl
     */
    public static MoquiUrl ofRelativeUrl(@NotNull PsiElement psiElement, @NotNull String relativeUrl, boolean fetchAllChildUrls) {
        Project project = psiElement.getProject();
        Location location = Location.of(project, relativeUrl);
        return ofRelativeUrl(psiElement,location,fetchAllChildUrls);
    }
    public static MoquiUrl ofRelativeUrl(@NotNull PsiElement psiElement, @NotNull Location relativeLocation, boolean fetchAllChildUrls) {
        if(relativeLocation.getType() != LocationType.RelativeUrl) {
            LOGGER.warn("Location type is not RelativeScreenFile, location is " + relativeLocation.getLocation() +", cannot create MoquiUrl from PsiElement: " + psiElement);
            return null;
        }

        PsiDirectory startDirectory = null;
        PsiFile psiFile = MyDomUtils.getPsiFileFromPsiElement(psiElement).orElse(null);
        if(psiFile == null) {
            //如果PsiElement所在的文件是MoquiFile，则获取对应的目录
            LOGGER.warn("Could not find file by PsiElement: " + psiElement);
            return null;
        }
        //如果是当前路径，直接返回所在的PsiFile
        if(relativeLocation.getLocation().equals(MyStringUtils.CURRENT_PATH)) {
            return ofPsiFile(psiFile,fetchAllChildUrls);
        }

        MoquiUrl startUrl = null;

        //处理相对路径
        searchLoop:
        for(int i =0; i< relativeLocation.getPathNameArray().length; i ++) {
            String pathName = relativeLocation.getPathNameArray()[i];
            switch (pathName) {
                case MyStringUtils.CURRENT_PATH -> {
                    //当前目录，获取PsiElement所在的文件对应的目录
                    if(startDirectory == null) {
                        startDirectory = LocationUtils.getSameNamePsiDirectorByPsiFile(psiFile).orElse(null);
                    }
                }
                case MyStringUtils.PARENT_PATH -> {
                    //上级目录，去掉最后一个路径
                    if(startDirectory == null) {
                        startDirectory = LocationUtils.getContainingPsiDirectorByPsiFile(psiFile).orElse(null);
                    }else {
                        startDirectory = startDirectory.getParentDirectory();
                    }
                }
                default -> {
                    break searchLoop;
                }
            }
            if(startDirectory == null) {
                return null;
            }
            MoquiUrl tmpUrl = ofPsiDirectory(startDirectory,fetchAllChildUrls);
            tmpUrl.setParent(startUrl);
            startUrl = tmpUrl;
        }
//        MoquiUrl startUrl = ofPsiDirectory(startDirectory,fetchAllChildUrls);
//        String startPathName = startDirectory.getVirtualFile().getPath();
        if(startUrl == null) return null;
        //处理后续路径
        for(int i =0; i< relativeLocation.getPathNameArray().length; i ++) {
            String pathName = relativeLocation.getPathNameArray()[i];
            if(!(pathName.equals(MyStringUtils.PARENT_PATH) || pathName.equals(MyStringUtils.CURRENT_PATH))) {
                startUrl = startUrl.getChildUrlByName(pathName).orElse(null);
                if(startUrl == null) {
//                    LOGGER.warn("Cannot find MoquiUrl by name: " + pathName + " from directory: " + startPathName);
                    return null;
                }
            }
        }
        return startUrl;

    }
    /**
     * 获取MoquiUrl的所有下级url
     */
    public void fetchChildUrls() {
        setChildren(fetchChildUrls(true));
        fetchAllChildUrls = true;
        childUrlHaveBeenFetched = true;
    }
    public void addChildUrl(@NotNull MoquiUrl moquiUrl) {
        children.add(moquiUrl);
    }
    /**
     * 根据fetchAllChildUrls参数来决定是否获取所有子菜单
     * @param fetchAllChildUrls boolean
     * @return ArrayList<MoquiUrl>
     */
    private ArrayList<MoquiUrl> fetchChildUrls(boolean fetchAllChildUrls) {
        ArrayList<MoquiUrl> menus = new ArrayList<>();
        if (containingMoquiFile == null) {
            if(containingDirectory != null) {
                String path = containingDirectory.getVirtualFile().getPath();
                menus.addAll(getChildUrlsByPath(this,path,fetchAllChildUrls));
            }

        }else {
            //处理MoquiConfig中定义的Screen
            String location;
            if (subScreensItem != null) {
                location = MyDomUtils.getValueOrEmptyString(subScreensItem.getLocation());
            } else {
                location = containingMoquiFile.getComponentRelativePath();
            }
            MoquiConfUtils.getAllScreensByLocation(project, location)
                    .forEach(item -> {
                        menus.addAll(getChildUrlsByScreen(this, item, fetchAllChildUrls));
                    });

            //处理自己对应的Screen
            if (screen != null)
                menus.addAll(getChildUrlsByScreen(this, screen, true));
            //处理自己对应的目录
            menus.addAll(getChildUrlsByPath(this, containingMoquiFile.getContainingSubScreensPath(), fetchAllChildUrls));

        }

        return menus;
    }

    public static ArrayList<MoquiUrl> getChildUrlsByScreen(@NotNull MoquiUrl moquiUrl, @NotNull Screen screen, boolean fetchAllChildUrls) {
        return ApplicationManager.getApplication().runReadAction((Computable<ArrayList<MoquiUrl>>) () -> {
            ArrayList<MoquiUrl> menus = new ArrayList<>();
            for (SubScreensItem item : screen.getSubScreens().getSubScreensItemList()) {
                MoquiUrl subMenu = ofSubScreensItem(item,fetchAllChildUrls);
                if (subMenu != null) subMenu.setParent(moquiUrl);
                menus.add(subMenu);
            }

            for (SubScreensItem item : screen.getSubScreensItemList()) {
                MoquiUrl subMenu = ofSubScreensItem(item,fetchAllChildUrls);
                if (subMenu != null) subMenu.setParent(moquiUrl);
                menus.add(subMenu);
            }
            return menus;

        });
    }

    /**
     * 获取指定路径下的文件和子目录
     * 1、剔除后缀的文件名为menu的名字
     * 2、子目录名为menu名字
     */
    public static ArrayList<MoquiUrl> getChildUrlsByPath(@NotNull MoquiUrl moquiUrl, @NotNull String path,boolean fetchAllChildUrls) {
        MoquiFile moquiFile = moquiUrl.getContainingMoquiFile();
        List<PsiFile> fileList;
        ArrayList<MoquiUrl> menus = new ArrayList<>();

        Project project;
        if (moquiFile != null) {
            project = moquiFile.getProject();
        } else {
            project = moquiUrl.getProject();
        }

        Set<String> filePathNames = new HashSet<>();
        fileList = MyDomUtils.findPsiFileListByPath(project, path);
        for (PsiFile item : fileList) {
            filePathNames.add(MyStringUtils.removeLastDotString(item.getName()));
            MoquiUrl subMenu = ofPsiFile(item,fetchAllChildUrls);
            subMenu.setParent(moquiUrl);
            menus.add(subMenu);
        }
        //获取目录下的子目录
        for(PsiDirectory psiDirectory: MyDomUtils.findPsiDirectoryListByPath(project,path)) {
            String dirName = psiDirectory.getName();
            if(filePathNames.contains(dirName)) continue; //如果已经有同名的文件，则不添加该目录
            MoquiUrl subMenu = ofPsiDirectory(psiDirectory,fetchAllChildUrls);
            subMenu.setParent(moquiUrl);
            menus.add(subMenu);
        }

        return menus;

    }

    public ArrayList<MoquiUrl> getNextLevelChildren() {
        if(!isFetchAllChildUrls()) {
            if (!childUrlHaveBeenFetched) {
                setChildren(fetchChildUrls(isFetchAllChildUrls()));
                childUrlHaveBeenFetched = true;
            }
        }
        return children;
    }
    /**
     * 从子Url中获取指定名称的Url
     * @param name 子url的名称
     * @return Optional<MoquiUrl>
     */
    public Optional<MoquiUrl> getChildUrlByName(@NotNull String name){
        MoquiUrl targetUrl = null;
        for(MoquiUrl url: getNextLevelChildren()) {
            if(url.getName().equals(name)) {
                targetUrl = url;
                break;
            }
        }
        return Optional.ofNullable(targetUrl);
    }
    /**
     * 获取MoquiUrl对应的PsiElement
     * @return Optional<PsiElement>
     */
    public Optional<PsiElement> getContainingPsiElement(){

//        if(getScreen() != null) return Optional.ofNullable(getScreen().getXmlElement());
        if(getContainingMoquiFile()!= null) return Optional.ofNullable(getContainingMoquiFile().getContainingFile());
        if(getContainingDirectory() != null) return Optional.ofNullable(getContainingDirectory());
//        if(getSubScreensItem() != null) return Optional.ofNullable(getSubScreensItem().getXmlElement());

        return Optional.empty();
//        if (getContainingMoquiFile()!= null) {
//            return Optional.ofNullable(getContainingMoquiFile().getContainingFile());
//        }
//        return Optional.ofNullable(getContainingDirectory());
    }
    public ArrayList<MoquiUrl> getChildren() {
        if (!isFetchAllChildUrls()) {
            if (!childUrlHaveBeenFetched) {
                fetchChildUrls();
                childUrlHaveBeenFetched = true;
            }
        }
        return children;
    }

//    public MoquiUrl getDefaultChild() {
//        return defaultChild;
//    }

    public MoquiUrl getParent() {
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

    public MoquiFile getContainingMoquiFile() {
        return containingMoquiFile;
    }

    public SubScreensItem getSubScreensItem() {
        return subScreensItem;
    }

    public Integer getUrlIndex() {
        return urlIndex;
    }

    public void setChildren(ArrayList<MoquiUrl> children) {
        this.children = children;
    }

    public PsiDirectory getContainingDirectory() {
        return containingDirectory;
    }

//    public void setDefaultChild(MoquiUrl defaultChild) {
//        this.defaultChild = defaultChild;
//    }

    public boolean isFetchAllChildUrls() {
        return fetchAllChildUrls;
    }

    public void setFetchAllChildUrls(boolean fetchAllChildUrls) {
        this.fetchAllChildUrls = fetchAllChildUrls;
    }

//    public boolean isChildUrlHaveBeenFetched() {
//        return childUrlHaveBeenFetched;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(MoquiUrl parent) {
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

    public void setContainingMoquiFile(MoquiFile containingMoquiFile) {
        this.containingMoquiFile = containingMoquiFile;
    }

    public void setSubScreensItem(SubScreensItem subScreensItem) {
        this.subScreensItem = subScreensItem;
    }

    public void setUrlIndex(Integer urlIndex) {
        this.urlIndex = urlIndex;
    }

    public void setContainingDirectory(PsiDirectory containingDirectory) {
        this.containingDirectory = containingDirectory;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
