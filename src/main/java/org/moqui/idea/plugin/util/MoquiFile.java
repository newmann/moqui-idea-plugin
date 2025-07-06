package org.moqui.idea.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MoquiFile {

    private final PsiFile containingFile;
    private final String path;//和idea中定义一样，含路径和名称
    private final String containingPath;//文件所在的最后一级路径

    private final String fileName;//文件名字
    private final String fileExtension;//文件扩展名

    private final String fileFullName;//文件全称，含文件名和扩展名

    private String componentName;//所在Component名称
    private final Boolean isComponentFile;
    private final Boolean isFrameworkFile;

    private final String relativePath;//
    public static @Nullable  MoquiFile of(@NotNull Project  project, @NotNull String fileUrl){
        LocationUtils.Location location = LocationUtils.ofLocation(project,fileUrl);
        if(location.getFile() == null) {
            return null;
        }else {
            return new MoquiFile(location.getFile());
        }
    }
    public static MoquiFile of(@NotNull PsiFile containingFile){
        return new MoquiFile(containingFile);
    }

    public PsiFile getContainingFile() {
        return containingFile;
    }

    MoquiFile(@NotNull PsiFile containingFile) {
        this.containingFile = containingFile;
        VirtualFile file = containingFile.getVirtualFile();
        this.path = file.getPath();
        this.fileFullName = file.getName();
        int index = this.path.lastIndexOf(MyStringUtils.PATH_SEPARATOR);
        if (index < 0) {
            this.containingPath = MyStringUtils.EMPTY_STRING;
        } else {
            this.containingPath = this.path.substring(0, index);
        }
        index = this.fileFullName.lastIndexOf(".");
        if (index < 0) {
            this.fileName = this.fileFullName;
            this.fileExtension = MyStringUtils.EMPTY_STRING;
        } else {
            this.fileName = this.fileFullName.substring(0, index);
            this.fileExtension = this.fileFullName.substring(index + 1);
        }
        //获取component名称，
        // /runtime/base-component 表示基础component
        // /runtime/component 表示普通的component

        this.componentName = ComponentUtils.getComponentNameFromPath(this.path).orElse(MyStringUtils.EMPTY_STRING);
        if (this.componentName.isEmpty()) {
            isComponentFile = false;
            isFrameworkFile = false;
        } else if (this.componentName.equals(MyStringUtils.FRAMEWORK_COMPONENT_NAME)) {
            isComponentFile = false;
            this.componentName = MyStringUtils.EMPTY_STRING;
            isFrameworkFile = true;
        } else {
            isComponentFile = true;
            isFrameworkFile = false;
        }
        relativePath = LocationUtils.extractComponentRelativePath(path).orElse(MyStringUtils.EMPTY_STRING);

    }

    public String getContainingSubScreensPath() {
        return this.containingPath + MyStringUtils.PATH_SEPARATOR + this.fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public String getComponentName() {
        return this.componentName;
    }

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
     *
     * @return String
     */
    public String getComponentRelativePath() {
        if (this.relativePath.equals(MyStringUtils.EMPTY_STRING)) {
            return MyStringUtils.EMPTY_STRING;
        } else {
            return MyStringUtils.COMPONENT_LOCATION_PREFIX + relativePath;
        }
    }

    public String getRelativePath() {
        return relativePath;
    }
}
