package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.EntityUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Deprecated
public class EntityManagement {
    public JPanel panelMain;
    private JPanel panelHeader;
    private JPanel panelContent;
    private JTabbedPane tabbedPane1;
    private JTree treeEntity;
    private JTextField textField1;
    private JButton btnSearch;
    private JTable tableEntityField;
    private JTable tableEntityRelationship;
    private JSplitPane splitPaneEntityDetail;
    private JSplitPane splitPaneEntity;
    private JTabbedPane tabbedPane2;

    private Project project;
    public EntityManagement(@NotNull Project project){
        this.project =project;

        initEntityTree();

        btnSearch.addActionListener(new RefreshButtonListener(this.project,this.treeEntity));
        treeEntity.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                TreePath path = treeSelectionEvent.getPath();
                Object lastObject = path.getLastPathComponent();
                if(lastObject instanceof DefaultMutableTreeNode mutableTreeNode) {
                    if(mutableTreeNode.getUserObject() instanceof IndexEntityTreeNode indexEntityTreeNode) {

                        List<AbstractField> abstractFieldList = indexEntityTreeNode.getIndexEntity().getAbstractFieldList().orElse(new ArrayList<>());
                        EntityFieldTableModel entityFieldTableModel = new EntityFieldTableModel(abstractFieldList);
                        tableEntityField.setModel(entityFieldTableModel);

                        List<Relationship> relationshipList = indexEntityTreeNode.getIndexEntity().getRelationshipList();
                        EntityRelationshipTableModel entityRelationshipTableModel = new EntityRelationshipTableModel(relationshipList);
                        tableEntityRelationship.setModel(entityRelationshipTableModel);

                    }
                }
            }
        });
    }
    private void initEntityTree(){
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Click Refresh Button");
        DefaultTreeModel treeMode = new DefaultTreeModel(treeNode);
        this.treeEntity.setModel(treeMode);
        this.splitPaneEntity.setDividerLocation(0.5);
        this.splitPaneEntityDetail.setDividerLocation(0.5);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }


    /**
     * Refresh button click event
     */
    class RefreshButtonListener implements ActionListener {
        private final Project project;
        private final JTree jTree;
        private DefaultMutableTreeNode rootNode;
        RefreshButtonListener(@NotNull Project project, @NotNull JTree jTree){
            this.project = project;
            this.jTree = jTree;
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            this.rootNode = new DefaultMutableTreeNode("Root");


            MoquiIndexService moquiIndexService = this.project.getService(MoquiIndexService.class);

            Collection<IndexEntity> indexEntityList = moquiIndexService.getIndexEntityMap().values();

            indexEntityList.forEach(this::addIndexEntityToTreeNode);

            DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
            this.jTree.setModel(treeMode);
        }



        private void addIndexEntityToTreeNode(@NotNull IndexEntity entity) {
            String[] path = entity.getPackageName().split("\\"+ EntityUtils.ENTITY_NAME_DOT);
            DefaultMutableTreeNode node;
            if(path.length>0) {
                node = getOrCreateTreeNode(this.rootNode,path[0]);

                for (int i = 1; i < path.length; i++) {
                    node = getOrCreateTreeNode(node, path[i]);
                }
            }else {
                node = this.rootNode;
            }
            createTreeNodeForEntity(node,entity);
        }
        private DefaultMutableTreeNode createTreeNodeForEntity(@NotNull DefaultMutableTreeNode parentNode, @NotNull IndexEntity indexEntity){
            IndexEntityTreeNode abstractEntityTreeNode = new IndexEntityTreeNode(indexEntity);

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(abstractEntityTreeNode);
            parentNode.add(node);
            return node;
        }
        private DefaultMutableTreeNode getOrCreateTreeNode(@NotNull DefaultMutableTreeNode parentNode, @NotNull String name){
            DefaultMutableTreeNode node = null;
            for(int i=0;i<parentNode.getChildCount();i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) parentNode.getChildAt(i);
                if(child.getUserObject() instanceof String childName) {
                    if(childName.equals(name)){
                        node= child;
                        break;
                    }
                }
            }
            //如果不存在，就创建一个，作为parentNode的child
            if(node == null) {
                node = new DefaultMutableTreeNode(name);
                parentNode.add(node);
            }
            return node;
        }

    }


}
