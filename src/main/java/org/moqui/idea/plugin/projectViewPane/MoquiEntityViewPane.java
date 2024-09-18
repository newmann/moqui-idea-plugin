package org.moqui.idea.plugin.projectViewPane;


import com.intellij.ide.SelectInTarget;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.openapi.project.Project;
import icons.MoquiIcons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyDomUtils;

import javax.swing.*;

public class MoquiEntityViewPane extends ProjectViewPane {
    @NonNls
    public static final String ID = "MoquiEntityProjectView";
    public MoquiEntityViewPane(Project project) {
        super(project);

    }

    @Override
    public boolean isInitiallyVisible() {
        return MyDomUtils.isMoquiProject(myProject);
    }

    @Override
    protected @NotNull ProjectAbstractTreeStructureBase createStructure() {
        ProjectAbstractTreeStructureBase result = super.createStructure();
        result.setProviders(new EntityTreeStructureProvider());

        return result;
    }

    @Override
    public @NotNull SelectInTarget createSelectInTarget() {
        return new MoquiViewPaneSelectInTarget(myProject,ID);
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getTitle() {
        return "Moqui Entity";
    }

    @Override
    public @NotNull Icon getIcon() {
        return MoquiIcons.EntityTag;
    }

    @Override
    public @NotNull String getId() {
        return ID;
    }

    @Override
    public int getWeight() {
        return 100;
    }


}
