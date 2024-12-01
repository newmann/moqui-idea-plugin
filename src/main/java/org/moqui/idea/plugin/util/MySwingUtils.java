package org.moqui.idea.plugin.util;

import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


/**
 *
 */
public final class MySwingUtils {
    private MySwingUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 创建Tree实例，添加TreeSpeedSearch功能
     */
    public static @NotNull Tree createTree(){
        Tree result = new Tree();
        TreeUIHelper.getInstance().installTreeSpeedSearch(result);
        return result;
    }
    public static @NotNull JButton createButton(@Nullable String caption){
        return new JButton(caption);
    }


    /**
     * 为组建创建一个ScrollPane
     */
    public static @NotNull JBScrollPane createScrollPane(Tree tree){
        JBScrollPane result = new JBScrollPane();
        result.setViewportView(tree);
        return result;
    }

    public static @NotNull  JSplitPane createHorizontalSplitPane(){
        return createHorizontalSplitPane(0.5);
    }

    public static @NotNull  JSplitPane createHorizontalSplitPane(double dividerLocation){
        JSplitPane result = new JSplitPane();
        result.setDividerLocation(dividerLocation);
        result.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        return result;
    }


}
