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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.SubScreensItem;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 处理Url相关的工具类
 * 从MoquiConf.xml的定义开始查找
 *
 */
public class MoquiUrl {
    public static Optional<PsiElement> getPsiElementFromMoquiUrl(@Nullable MoquiUrl targetUrl){
        if(targetUrl != null) {
            if(targetUrl.getSubScreensItem() != null) return Optional.ofNullable(targetUrl.getSubScreensItem().getXmlElement());
            if(targetUrl.getScreen() != null) return Optional.ofNullable(targetUrl.getScreen().getXmlElement());
            if(targetUrl.getContainingMoquiFile()!= null) return Optional.ofNullable(targetUrl.getContainingMoquiFile().getContainingFile());
            if(targetUrl.getContainingDirectory() != null) return Optional.ofNullable(targetUrl.getContainingDirectory());
        }
        return Optional.empty();
    }
    public static Optional<MoquiUrl> getMoquiUrlByName(@NotNull MoquiUrl parentUrl, @NotNull String name){
        MoquiUrl targetUrl = null;
        for(MoquiUrl url: parentUrl.getNextLevelChildren()) {
            if(url.getName().equals(name)) {
                targetUrl = url;
                break;
            }
        }
        return Optional.ofNullable(targetUrl);
    }

    private static final Logger LOGGER = Logger.getInstance(ScreenUtils.class);

    private MoquiUrl parent;
    private ArrayList<MoquiUrl> children = new ArrayList<>();
    private MoquiUrl defaultChild;

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
    private String id;//文件对应的真实路径名，在同一级别的url中是唯一的
    /**
     * 获取project的所有的menu
     * 只有在ROOT_SCREEN_LOCATION的Screen中定义的SubScreensItem才会被认为是顶级菜单，包括该文件对应的目录下Screen文件
     */
    public static ArrayList<MoquiUrl> getAllUrlArrayList(@NotNull Project project) {
        ArrayList<MoquiUrl> menus = new ArrayList<>();

//        List<Screen> screenList = MoquiConfUtils.getAllScreensByLocation(project,ROOT_SCREEN_LOCATION);
//        ApplicationManager.getApplication().runReadAction(() -> {
//            for (Screen screen : screenList) {
//                for (SubScreensItem subScreensItem : screen.getSubScreensItemList()) {
//                    MoquiUrl MoquiUrl = of(subScreensItem);
//                    menus.add(MoquiUrl);
//                }
//            }
//        });
//        //再添加ROOT_SCREEN_LOCATION对应的目录下的Screen文件
//        LocationUtils.Location webrootFile = LocationUtils.ofLocation(project,ROOT_SCREEN_LOCATION);
//        if(webrootFile.getFile()!= null) {
//            MoquiUrl url = of(webrootFile.getFile());
//            if(url != null)
//                menus.addAll(url.getChildren());
//        }
        MoquiUrl url = of(project, MyStringUtils.ROOT_SCREEN_LOCATION, true);

        if (url != null) {
            menus.addAll(url.getChildren());
        }
        //对menus进行排序
        return sortUrlArrayList(menus);

    }

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
    public static MoquiUrl of(@NotNull SubScreensItem screensItem){
        return of(screensItem,true);
    }
    public static MoquiUrl of(@NotNull SubScreensItem screensItem, boolean fetchAllChildUrls) {
        if (screensItem.getXmlElement() == null) {
            return null;
        }
        MoquiUrl result = new MoquiUrl();
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
        result.setProject(screensItem.getXmlElement().getProject());

        //查找对应的文件
        final PsiFile psiFile = MyDomUtils.getFileFromLocation(result.getProject(), location).orElse(null);
        if (psiFile != null) {
            MoquiFile file = MoquiFile.of(psiFile);
            result.setContainingMoquiFile(file);


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

    public static MoquiUrl of(@NotNull Project project,@NotNull String location, boolean fetchAllChildUrls) {
        LocationUtils.Location webrootFile = LocationUtils.ofLocation(project,location);
        if(webrootFile.getFile()!= null) {
            return of(webrootFile.getFile(),fetchAllChildUrls);
        }else {
            return null;
        }
    }
    /**
     * 根据文件创建Menu
     */
    public static MoquiUrl of(@NotNull PsiFile psiFile){
        return of(psiFile,true);
    }

    public static MoquiUrl of(@NotNull PsiFile psiFile,boolean fetchAllChildUrls) {
        //如果psiFile不是Screen文件，则返回null
        if (!ScreenUtils.isScreenFile(psiFile)) {
            return null;
        }

        MoquiUrl result = new MoquiUrl();

        result.setProject(psiFile.getProject());

        //查找对应的文件
        final MoquiFile file = new MoquiFile(psiFile);
        final Screen screen = MyDomUtils.convertPsiFileToDomFile(psiFile, Screen.class).getRootElement();

        String defaultMenuTitle = MyDomUtils.getValueOrEmptyString(ReadAction.compute(screen::getDefaultMenuTitle));
        Integer defaultMenuIndex = MyDomUtils.getValueOrZero(ReadAction.compute(screen::getDefaultMenuIndex));
        result.setIcon(AllIcons.FileTypes.Xml);


        if (MyDomUtils.isMoquiXmlFile(psiFile)) {
            result.setName(file.getFileName());
            if (defaultMenuTitle.isEmpty()) {
                result.setTitle(file.getFileName());
            } else {
                result.setTitle(defaultMenuTitle);
            }
            result.setUrlIndex(defaultMenuIndex);

        } else {
            result.setName(file.getFileFullName());
            result.setTitle(file.getFileFullName());
        }
        result.setScreen(screen);
        result.setContainingMoquiFile(file);
        result.setSubScreensItem(null);
        result.setContainingDirectory(null);

        if(fetchAllChildUrls) {
//            ArrayList<MoquiUrl> children = new ArrayList<>();
//            //查找文件中的SubScreens定义
//            children.addAll(getChildUrlsBySubScreens(result, screen,true));
//            //查找同名文件夹下面的文件
//            children.addAll(getChildUrlsByPath(result, file.getContainingSubScreensPath(),true));

            result.fetchChildUrls();
        }

        return result;

    }

    /**
     * 根据目录创建Menu
     */
    public static MoquiUrl of(@NotNull PsiDirectory psiDirectory){
        return of(psiDirectory,true);
    }
    public static MoquiUrl of(@NotNull PsiDirectory psiDirectory,boolean fetchAllChildUrls) {

        MoquiUrl result = new MoquiUrl();
//            //查找对应的文件

        result.setProject(psiDirectory.getProject());

        result.setName(psiDirectory.getName());
        result.setTitle(psiDirectory.getName());
        result.setIcon(AllIcons.General.Print);

        result.setSubScreensItem(null);
        result.setContainingMoquiFile(null);

        result.setContainingDirectory(psiDirectory);

        //获取子菜单
        if(fetchAllChildUrls)
            result.fetchChildUrls();
        return result;

    }

    /**
     * 获取MoquiUrl的所有下级url
     *
     * @return ArrayList<MoquiUrl>
     */
    public void fetchChildUrls() {
        setChildren(fetchChildUrls(true));
    }

    /**
     * 根据fetchAllChildUrls参数来决定是否获取所有子菜单
     * @param fetchAllChildUrls boolean
     * @return ArrayList<MoquiUrl>
     */
    public ArrayList<MoquiUrl> fetchChildUrls(boolean fetchAllChildUrls) {
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
                        menus.addAll(getChildUrlsBySubScreens(this, item, fetchAllChildUrls));
                    });

            //处理自己对应的Screen
            if (screen != null)
                menus.addAll(getChildUrlsBySubScreens(this, screen, true));
            //处理自己对应的目录
            menus.addAll(getChildUrlsByPath(this, containingMoquiFile.getContainingSubScreensPath(), fetchAllChildUrls));

        }

        return menus;
    }
//    public static ArrayList<MoquiUrl> getChildUrlsBySubScreens(@NotNull MoquiUrl MoquiUrl, @NotNull SubScreens subScreens,boolean fetchAllChildUrls) {
//        return ApplicationManager.getApplication().runReadAction((Computable<ArrayList<MoquiUrl>>) () -> {
//            ArrayList<MoquiUrl> menus = new ArrayList<>();
//            for (SubScreensItem item : subScreens.getSubScreensItemList()) {
//                MoquiUrl subMenu = of(item,fetchAllChildUrls);
//                if (subMenu != null) subMenu.setParent(MoquiUrl);
//                menus.add(subMenu);
//            }
//            return menus;
//
//        });
//    }
    public static ArrayList<MoquiUrl> getChildUrlsBySubScreens(@NotNull MoquiUrl MoquiUrl, @NotNull Screen screen,boolean fetchAllChildUrls) {
        return ApplicationManager.getApplication().runReadAction((Computable<ArrayList<MoquiUrl>>) () -> {
            ArrayList<MoquiUrl> menus = new ArrayList<>();
            for (SubScreensItem item : screen.getSubScreens().getSubScreensItemList()) {
                MoquiUrl subMenu = of(item,fetchAllChildUrls);
                if (subMenu != null) subMenu.setParent(MoquiUrl);
                menus.add(subMenu);
            }

            for (SubScreensItem item : screen.getSubScreensItemList()) {
                MoquiUrl subMenu = of(item,fetchAllChildUrls);
                if (subMenu != null) subMenu.setParent(MoquiUrl);
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
    public static ArrayList<MoquiUrl> getChildUrlsByPath(@NotNull MoquiUrl MoquiUrl, @NotNull String path,boolean fetchAllChildUrls) {
        MoquiFile moquiFile = MoquiUrl.getContainingMoquiFile();
        List<PsiFile> fileList;
        ArrayList<MoquiUrl> menus = new ArrayList<>();

        Project project;
        if (moquiFile != null) {
            project = moquiFile.getContainingFile().getProject();
        } else {
            if (MoquiUrl.getContainingDirectory() == null) return menus;
            project = MoquiUrl.getContainingDirectory().getProject();
        }

        fileList = MyDomUtils.findPsiFileListByPath(project, path);
        for (PsiFile item : fileList) {
            MoquiUrl subMenu = of(item,fetchAllChildUrls);
            if (subMenu != null) {
                subMenu.setParent(MoquiUrl);
                menus.add(subMenu);
            }
        }
        return menus;

    }

    public ArrayList<MoquiUrl> getNextLevelChildren() {
        return fetchChildUrls(false);
    }

    public ArrayList<MoquiUrl> getChildren() {
        return children;
    }

    public MoquiUrl getDefaultChild() {
        return defaultChild;
    }

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

    public void setDefaultChild(MoquiUrl defaultChild) {
        this.defaultChild = defaultChild;
    }

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
