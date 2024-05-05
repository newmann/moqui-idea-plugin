package org.moqui.idea.plugin.action.componentManagement;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.action.entityManagement.IndexViewEntityTreeNode;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.dom.model.DependsOn;
import org.moqui.idea.plugin.util.ComponentUtils;
import org.moqui.idea.plugin.util.MyDomUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class ComponentDepends {
    private JPanel mainPanel;
    private JTree treeComponent;
    private JButton btnRefresh;
    private Project project;

    public ComponentDepends(Project project){
        this.project = project;

        initComponentTree();
        btnRefresh.addActionListener(new RefreshButtonListener(project, this.treeComponent));
    }

    private void initComponentTree(){
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Click Refresh Button");

        DefaultTreeModel treeMode = new DefaultTreeModel(treeNode);
        this.treeComponent.setModel(treeMode);

        //双击事件
        treeComponent.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    TreePath treePath = treeComponent.getPathForLocation(e.getX(), e.getY());
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
    public JPanel getMainPanel(){
        return this.mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
//        this.treeComponent = new JTree(initComponentTree());
    }

    /**
     * Refresh button click event
     */
    class RefreshButtonListener implements ActionListener{
        private final Project project;
        private final JTree jTree;
        RefreshButtonListener(@NotNull Project project, @NotNull JTree jTree){
            this.project = project;
            this.jTree = jTree;
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Root");

            Map<String, Component> componentMap = ComponentUtils.findAllComponent(this.project);
            //按顺序显示
            List<String> keys = new ArrayList<>(componentMap.keySet());
            keys.sort(String::compareToIgnoreCase);

            for(String key: keys) {
                treeNode.add(createTreeNodeForComponent(componentMap.get(key),componentMap));
            }

//            componentMap.forEach((String key, Component component)->{
//                treeNode.add(createTreeNodeForComponent(component,componentMap));
//            });

            DefaultTreeModel treeMode = new DefaultTreeModel(treeNode);
            this.jTree.setModel(treeMode);
        }

        private DefaultMutableTreeNode createTreeNodeForComponent(@NotNull Component component,@NotNull Map<String, Component> componentMap){

            ComponentTreeNode componentTreeNode = new ComponentTreeNode(component);

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(componentTreeNode);

            for(DependsOn dependsOn: component.getDependsOnList()) {
                String dependsOnName = MyDomUtils.getValueOrEmptyString(dependsOn.getName());
                Component dependency = componentMap.get(dependsOnName);
                if(dependency != null) {
                    node.add(createTreeNodeForComponent(dependency, componentMap));
                }
            }
            return node;
        }
    }
}
