package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
@Deprecated
public class EntityManagementDialogWrapper extends DialogWrapper {
    private Project project;
    private EntityManagement entityManagement;
    protected EntityManagementDialogWrapper(@Nullable Project project) {
        super(project);
        this.project =project;
        setTitle("Entity Management");
        init();

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        if(this.entityManagement == null) {
            this.entityManagement = new EntityManagement(this.project);
        }
        return this.entityManagement.panelMain;
    }

}
