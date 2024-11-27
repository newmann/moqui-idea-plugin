package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityManagementGUI extends JPanel {
    private Project project;
    private JSplitPane splitPaneContent;

    private JSplitPane splitPaneDetail;

    private Tree treeEntity;
    private ToolbarDecorator treeDecorator;
    private JBScrollPane scrollPaneEntity;
    private JBTable tableField;
    private JBScrollPane scrollPaneField;

    private JBTable tableRelationship;
    private JBScrollPane scrollPaneRelationship;

    private JBTextField textSearch;

    private JButton buttonSearch;
    private DefaultMutableTreeNode rootNode;

    public EntityManagementGUI(@NotNull Project project){
        super(new BorderLayout());

        this.project =project;
        splitPaneContent = new JSplitPane();

        splitPaneContent.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        //entity tree
        scrollPaneEntity = new JBScrollPane();
        treeEntity = new Tree();
//        this.treeDecorator = ToolbarDecorator.createDecorator(treeEntity);
        TreeUIHelper.getInstance().installTreeSpeedSearch(treeEntity);

        scrollPaneEntity.setViewportView(treeEntity);


        splitPaneContent.setLeftComponent(scrollPaneEntity);

        //detail content
        splitPaneDetail = new JSplitPane();

        splitPaneDetail.setOrientation(JSplitPane.VERTICAL_SPLIT);

        scrollPaneField = new JBScrollPane();
        tableField = new JBTable();
        scrollPaneField.setViewportView(tableField);
        splitPaneDetail.setTopComponent(scrollPaneField);

        scrollPaneRelationship = new JBScrollPane();
        tableRelationship = new JBTable();
        scrollPaneRelationship.setViewportView(tableRelationship);
        splitPaneDetail.setBottomComponent(scrollPaneRelationship);

        splitPaneContent.setRightComponent(splitPaneDetail);

        initHeader();
        add(splitPaneContent, BorderLayout.CENTER);
        initEntityTree();
        initComponentListener();
        initButtonSearchListener();
        initTreeEntityListener();
        initTextSearchListener();
    }
    private void initHeader(){
        JPanel header = new JPanel(new BorderLayout());
        JBLabel labelHeader = new JBLabel("Search:");
        header.add(labelHeader,BorderLayout.WEST);

        textSearch = new JBTextField();
        header.add(textSearch,BorderLayout.CENTER);

        buttonSearch = new JButton("Search");
        header.add(buttonSearch,BorderLayout.EAST);

        add(header,BorderLayout.NORTH);
    }

    private void reSizeSplitPane(){
        splitPaneContent.setDividerLocation(0.3);
        splitPaneDetail.setDividerLocation(0.5);
    }

    private void initEntityTree(){
        this.rootNode = new DefaultMutableTreeNode("Nothing to show");
        DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
        this.treeEntity.setModel(treeMode);
    }

    private void initComponentListener(){
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                reSizeSplitPane();
            }
        });
    }
    private void initButtonSearchListener(){
        buttonSearch.addActionListener(actionEvent -> refreshEntityTree());
    }
    private void initTreeEntityListener(){
        treeEntity.addTreeSelectionListener(treeSelectionEvent -> {
            TreePath path = treeSelectionEvent.getPath();
            Object lastObject = path.getLastPathComponent();
            if(lastObject instanceof DefaultMutableTreeNode mutableTreeNode) {
                if(mutableTreeNode.getUserObject() instanceof IndexEntityTreeNode indexEntityTreeNode) {

                    List<AbstractField> abstractFieldList = indexEntityTreeNode.getIndexEntity().getAbstractFieldList();
                    EntityFieldTableModel entityFieldTableModel = new EntityFieldTableModel(abstractFieldList);
                    tableField.setModel(entityFieldTableModel);
//                        tableField.setAutoResizeMode(JBTable.AUTO_RESIZE_ALL_COLUMNS);
//                        tableField.setPreferredScrollableViewportSize(
//                                new Dimension(tableField.getPreferredSize().width,
//                                        tableField.getRowHeight()* tableField.getRowCount()) );

                    List<Relationship> relationshipList = indexEntityTreeNode.getIndexEntity().getRelationshipList();
                    EntityRelationshipTableModel entityRelationshipTableModel = new EntityRelationshipTableModel(relationshipList);
                    tableRelationship.setModel(entityRelationshipTableModel);
//                        tableRelationship.setAutoResizeMode(JBTable.AUTO_RESIZE_ALL_COLUMNS);

                }
            }
        });
        //双击事件
        treeEntity.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    TreePath treePath = treeEntity.getPathForLocation(e.getX(), e.getY());
                    if(treePath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if(node !=null) {
                            if(node.getUserObject() instanceof IndexEntityTreeNode indexEntityTreeNode) {
                                MyDomUtils.openFileForDomElement(indexEntityTreeNode.getIndexEntity().getEntity());
                            }
                        }
                    }
                }


            }
        });
    }
    private void initTextSearchListener(){
        this.textSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    refreshEntityTree();
                }
            }
        });
    }

    private void refreshEntityTree(){
        this.rootNode = new DefaultMutableTreeNode("Root");

        String strSearch = this.textSearch.getText().trim();


        MoquiIndexService moquiIndexService = this.project.getService(MoquiIndexService.class);

        Collection<IndexEntity> indexEntityList = moquiIndexService.getIndexEntityMap().values();
        if(MyStringUtils.EMPTY_STRING.equals(strSearch)) {
            indexEntityList.forEach(this::addIndexEntityToTreeNode);
        }else{
            indexEntityList.stream().filter(indexEntity -> indexEntity.getFullName()
                            .toLowerCase().contains(strSearch.toLowerCase())).toList()
                    .forEach(this::addIndexEntityToTreeNode);

        }


        DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
        this.treeEntity.setModel(treeMode);
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
