package org.moqui.idea.plugin.projectViewPane;

import com.intellij.ide.SelectInContext;
import com.intellij.ide.SelectInManager;
import com.intellij.ide.StandardTargetWeights;
import com.intellij.ide.impl.ProjectViewSelectInTarget;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
/**
 * 如果不自定义，直接使用ProjectPaneSelectInTarget，系统会报错，因为ProjectPaneSelectInTarget中的getMinorViewId直接返回ProjectViewPane.ID
 */
public class MoquiViewPaneSelectInTarget extends ProjectViewSelectInTarget implements DumbAware {
    private final String minorViewId;
    public MoquiViewPaneSelectInTarget(Project project,@NotNull String minorViewId) {
        super(project);
        this.minorViewId =minorViewId;
    }

    @Override
    public String toString() {
        return SelectInManager.getProject();
    }

    @Override
    public boolean isSubIdSelectable(String subId, SelectInContext context) {
        return canSelect(context);
    }

    @Override
    public String getMinorViewId() {
        return minorViewId;
    }

    @Override
    public float getWeight() {
        return StandardTargetWeights.PROJECT_WEIGHT;
    }
}
