package org.moqui.idea.plugin.action.componentManagement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.dom.model.DependsOn;
import org.moqui.idea.plugin.util.ComponentUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MySwingUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComponentDepends extends JPanel {
    private static final Logger LOGGER = Logger.getInstance(ComponentDepends.class);
    private final Tree componentTree;
    private Project myProject;
    private JButton refreshButton;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeMode;
    public ComponentDepends(@NotNull Project project){
        super(new BorderLayout());
        myProject = project;
        componentTree = MySwingUtils.createTree();
        JBScrollPane componentTreeScrollPane = MySwingUtils.createScrollPane(componentTree);

        add(componentTreeScrollPane,BorderLayout.CENTER);
        refreshButton = MySwingUtils.createButton("Refresh");
        add(refreshButton,BorderLayout.NORTH);

        initComponentTree();
        refreshButton.addActionListener(actionEvent -> refreshDepend());
    }

    private void initComponentTree(){
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Click Refresh Button");

        DefaultTreeModel treeMode = new DefaultTreeModel(treeNode);
        this.componentTree.setModel(treeMode);

        //双击事件
        componentTree.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    TreePath treePath = componentTree.getPathForLocation(e.getX(), e.getY());
                    if(treePath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if(node !=null) {
                            if(node.getUserObject() instanceof ComponentTreeNode componentTreeNode) {
                                MyDomUtils.openFileForDomElement(componentTreeNode.getComponent());
                            }
                        }
                    }
                }


            }
        });
    }

    private void refreshDepend(){
        refreshButton.setEnabled(false);
        rootNode = new DefaultMutableTreeNode("Loading...");
        treeMode = new DefaultTreeModel(rootNode);
        componentTree.setModel(treeMode);
        refreshButton.setIcon(new AnimatedIcon.Default());
        ProgressManager.getInstance().run(
                new Task.Backgroundable(myProject, componentTree,"Loading component depends...",false,null){
                    ArrayList<ScreenUtils.Menu> menuArrayList;
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        rootNode = new DefaultMutableTreeNode("Root");

                        Map<String, Component> componentMap = ComponentUtils.findAllComponent(myProject);
                        //按顺序显示
                        List<String> keys = new ArrayList<>(componentMap.keySet());
                        keys.sort(String::compareToIgnoreCase);

                        for(String key: keys) {
                            rootNode.add(createTreeNodeForComponent(componentMap.get(key),componentMap));
                        }

                    }

                    @Override
                    public void onFinished() {
                        super.onFinished();
                        ApplicationManager.getApplication().invokeLater(()->{
                            treeMode = new DefaultTreeModel(rootNode);
                            componentTree.setModel(treeMode);
                            refreshButton.setEnabled(true);
                            refreshButton.setIcon(null);
                        });

                    }
                }
        );
    }
    private DefaultMutableTreeNode createTreeNodeForComponent(@NotNull Component component,@NotNull Map<String, Component> componentMap){

        ComponentTreeNode componentTreeNode = new ComponentTreeNode(component);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(componentTreeNode);

        for(DependsOn dependsOn: ReadAction.compute(component::getDependsOnList)) {
            String dependsOnName = MyDomUtils.getValueOrEmptyString(ReadAction.compute(dependsOn::getName));
            Component dependency = componentMap.get(dependsOnName);
            if(dependency != null) {
                node.add(createTreeNodeForComponent(dependency, componentMap));
            }
        }
        return node;
    }

}
