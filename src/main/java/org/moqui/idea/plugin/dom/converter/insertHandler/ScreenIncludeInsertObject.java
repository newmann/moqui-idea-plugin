package org.moqui.idea.plugin.dom.converter.insertHandler;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyStringUtils;

/**
 * 用来解决transition-include，section-include的数据传递问题
 */
public class ScreenIncludeInsertObject {
    public static ScreenIncludeInsertObject of(@NotNull String componentName, @NotNull String relativePath, @NotNull String objectName){
        return  new ScreenIncludeInsertObject(componentName,relativePath,objectName);
    }
    ScreenIncludeInsertObject(@NotNull String componentName, @NotNull String relativePath, @NotNull String objectName){
        this.componentName = componentName;
        this.relativePath = relativePath;
        this.objectName = objectName;
    }
    private String componentName;
    private String relativePath;
    private String objectName;

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }
    public String getLocation(){
        return MyStringUtils.COMPONENT_LOCATION_PREFIX + relativePath;
    }
}
