package org.moqui.idea.plugin.action;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerListener;
import icons.MoquiIcons;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.action.componentManagement.ComponentDepends;
import org.moqui.idea.plugin.action.entityManagement.EntityManagement;
import org.moqui.idea.plugin.action.entityManagement.EntityManagementGUI;
import org.moqui.idea.plugin.action.entityManagement.PendingViewEntityManagementGUI;
import org.moqui.idea.plugin.action.entityManagement.ViewEntityManagementGUI;

import javax.swing.*;
import java.awt.*;

public class AdminToolWindowFactory implements ToolWindowFactory {
    private Project project;
    private JPanel panelMain;
    private JBTabbedPane tabbedPaneContainer;
    private ComponentDepends componentDepends;

    private EntityManagement entityManagement;
    private EntityManagementGUI entityManagementGUI;
    private ViewEntityManagementGUI viewEntityManagementGUI;
    private PendingViewEntityManagementGUI pendingViewEntityManagementGUI;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        ContentFactory contentFactory = ContentFactory.getInstance();

        initMainPanel();
        Content content = contentFactory.createContent(this.panelMain,null,true);
        toolWindow.getContentManager().addContent(content);
    }

    private void initComponentDependContent(){
        this.componentDepends = new ComponentDepends(this.project);
    }
    private  void initEntityManagementContent(){
        if(this.entityManagement == null) {

            this.entityManagement = new EntityManagement(this.project);
        }
    }

    private void initMainPanel(){
        panelMain = new JPanel();
        LayoutManager layoutManager = new BorderLayout();
        panelMain.setLayout(layoutManager);
        tabbedPaneContainer = new JBTabbedPane();
        panelMain.add(tabbedPaneContainer, BorderLayout.CENTER);

        addComponentDepend();
        addEntityManagement();
        addViewEntityManagement();
        addPendingViewEntityManagement();
    }

    private void addComponentDepend(){
        this.componentDepends = new ComponentDepends(this.project);
//        this.componentDepends.initComponentTree();
        this.tabbedPaneContainer.insertTab("Component Depends",
                MoquiIcons.ComponentTag,this.componentDepends.getMainPanel(),"Admin component depends tree",0);
    }

    private void addEntityManagement(){
//        this.entityManagement = new EntityManagement(this.project);
        this.entityManagementGUI = new EntityManagementGUI(this.project);
//        this.entityManagementGUI.setVisible(true);

        this.tabbedPaneContainer.insertTab("Entity Management",
                MoquiIcons.EntityTag,this.entityManagementGUI,"",1);



//        this.tabbedPaneContainer.updateUI();
    }
    private void addViewEntityManagement(){
        this.viewEntityManagementGUI = new ViewEntityManagementGUI(this.project);

        this.tabbedPaneContainer.insertTab("View Entity Management",
                MoquiIcons.ViewEntityTag,this.viewEntityManagementGUI,"",2);
    }

    private void addPendingViewEntityManagement(){
        this.pendingViewEntityManagementGUI = new PendingViewEntityManagementGUI(this.project);

        this.tabbedPaneContainer.insertTab("Pending View Entity",
                MoquiIcons.ViewEntityTag,this.pendingViewEntityManagementGUI,"",3);
    }
}
