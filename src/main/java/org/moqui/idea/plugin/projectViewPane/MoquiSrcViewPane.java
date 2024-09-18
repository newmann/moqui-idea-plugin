package org.moqui.idea.plugin.projectViewPane;


import com.intellij.icons.AllIcons;
import com.intellij.ide.SelectInTarget;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyDomUtils;

import javax.swing.*;

public class MoquiSrcViewPane extends ProjectViewPane {
    @NonNls
    public static final String ID = "MoquiSrcProjectView";
    public MoquiSrcViewPane(Project project) {
        super(project);
    }
    @Override
    public boolean isInitiallyVisible() {
        return MyDomUtils.isMoquiProject(myProject);
    }
    @Override
    protected @NotNull ProjectAbstractTreeStructureBase createStructure() {
        ProjectAbstractTreeStructureBase result = super.createStructure();
        result.setProviders(new SrcTreeStructureProvider());
        return result;
    }

    @Override
    public @NotNull SelectInTarget createSelectInTarget() {

        return new MoquiViewPaneSelectInTarget(myProject,ID);
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getTitle() {
        return "Moqui Src";
    }

    @Override
    public @NotNull Icon getIcon() {
        return AllIcons.Javaee.Home;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public int getWeight() {
        return 103;
    }



}
