package org.moqui.idea.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.action.menuManagement.MenuManagementGUI;
import org.moqui.idea.plugin.action.componentManagement.ComponentDepends;
import org.moqui.idea.plugin.action.entityManagement.EntityManagementGUI;
import org.moqui.idea.plugin.action.entityManagement.PendingViewEntityManagementGUI;
import org.moqui.idea.plugin.action.entityManagement.ViewEntityManagementGUI;
import org.moqui.idea.plugin.action.serviceManagement.ServiceManagementGUI;

import javax.swing.*;
import java.awt.*;

public class AdminToolWindowFactory implements ToolWindowFactory {
    private static final Logger LOG = Logger.getInstance(AdminToolWindowFactory.class);

    private Project project;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private DefaultActionGroup mainActionGroup;
    private CardLayout contentLayout;

    private PendingViewEntityManagementGUI pendingViewEntityManagementGUI;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        ContentFactory contentFactory = ContentFactory.getInstance();

        initMainPanel();
        Content content = contentFactory.createContent(this.mainPanel,null,true);
        toolWindow.getContentManager().addContent(content);
    }


    private void initMainPanel(){
        mainPanel = new JPanel();
        LayoutManager layoutManager = new BorderLayout();
        mainPanel.setLayout(layoutManager);

        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        mainPanel.add(contentPanel,BorderLayout.CENTER);

        mainActionGroup = new DefaultActionGroup();

        ComponentDepends componentDepends = new ComponentDepends(this.project);
        addToolBar(componentDepends,"Component","Loading all components and dependencies", MoquiIcons.ComponentTag);

        EntityManagementGUI entityManagementGUI = new EntityManagementGUI(this.project);
        addToolBar(entityManagementGUI,"Entity","Loading all entities", MoquiIcons.EntityTag);

        ViewEntityManagementGUI viewEntityManagementGUI = new ViewEntityManagementGUI(this.project); //
        addToolBar(viewEntityManagementGUI,"View","Loading all view entities", MoquiIcons.ViewEntityTag);

        ServiceManagementGUI serviceManagementGUI = new ServiceManagementGUI(this.project); //
        addToolBar(serviceManagementGUI,"Service","Loading all services", MoquiIcons.ServiceTag);

        MenuManagementGUI menuManagementGUI = new MenuManagementGUI(this.project); //
        addToolBar(menuManagementGUI,"Menu","Loading all menus", AllIcons.Ide.Gift);//TODO update icon

        ActionToolbar toolBar = ActionManager.getInstance().createActionToolbar("Moqui Admin", mainActionGroup, true);
//        toolBar.setTargetComponent(componentDepends);

        mainPanel.add(toolBar.getComponent(), BorderLayout.PAGE_START);


    }

    private void addToolBar(Component component,String title,String description,Icon icon){
        contentPanel.add(component,title);

        mainActionGroup.add(new AnAction(title,description,icon){
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
                cardLayout.show(contentPanel,title);

//                contentLayout.show(contentPanel,title);

            }
        });
    }

}
