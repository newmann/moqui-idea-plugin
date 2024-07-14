package org.moqui.idea.plugin.action.componentManagement;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.AbstractStubIndex;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.xml.stubs.index.DomElementClassIndex;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.dom.model.DependsOn;
import org.moqui.idea.plugin.util.ComponentUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MySwingUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class ComponentDepends extends JPanel {
    private static final Logger LOGGER = Logger.getInstance(ComponentDepends.class);
    private final Tree componentTree;

    public ComponentDepends(Project project){
        super(new BorderLayout());

        componentTree = MySwingUtils.createTree();
        JBScrollPane componentTreeScrollPane = MySwingUtils.createScrollPane(componentTree);

        add(componentTreeScrollPane,BorderLayout.CENTER);
        JButton refreshButton = MySwingUtils.createButton("Refresh");
        add(refreshButton,BorderLayout.NORTH);

        initComponentTree();
        refreshButton.addActionListener(new RefreshButtonListener(project, this.componentTree));
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


    /**
     * Refresh button click event
     */
    static class RefreshButtonListener implements ActionListener{
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
