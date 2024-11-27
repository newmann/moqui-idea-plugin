package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.MemberEntity;
import org.moqui.idea.plugin.dom.model.MemberRelationship;
import org.moqui.idea.plugin.service.IndexViewEntity;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewEntityManagementGUI extends JPanel {
    private final Project project;
    private final JSplitPane splitPaneContent;

    private final JSplitPane splitPaneDetail;

    private final Tree treeViewEntity;
    private final JBTable tableField;

    private final JSplitPane splitPaneMember;

    private final JBTable tableMemberEntity;

    private final JBTable tableMemberRelationship;

    private JBTextField textSearch;

    private JButton buttonSearch;
    private DefaultMutableTreeNode rootNode;

    public ViewEntityManagementGUI(@NotNull Project project){
        super(new BorderLayout());

        this.project =project;
        splitPaneContent = new JSplitPane();

        splitPaneContent.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        //entity tree
        JBScrollPane scrollPaneEntity = new JBScrollPane();
        treeViewEntity = new Tree();
        scrollPaneEntity.setViewportView(treeViewEntity);

        splitPaneContent.setLeftComponent(scrollPaneEntity);

        //detail content
        splitPaneDetail = new JSplitPane();

        splitPaneDetail.setOrientation(JSplitPane.VERTICAL_SPLIT);

        JBScrollPane scrollPaneField = new JBScrollPane();
        tableField = new JBTable();
        scrollPaneField.setViewportView(tableField);
        splitPaneDetail.setTopComponent(scrollPaneField);

        //MemberEntity & MemberRelationship
        splitPaneMember = new JSplitPane();
        splitPaneMember.setOrientation(JSplitPane.VERTICAL_SPLIT);
        JBScrollPane scrollPaneMemberEntity = new JBScrollPane();
        tableMemberEntity = new JBTable();
        scrollPaneMemberEntity.setViewportView(tableMemberEntity);
        splitPaneMember.setTopComponent(scrollPaneMemberEntity);
        JBScrollPane scrollPaneMemberRelationship = new JBScrollPane();
        tableMemberRelationship = new JBTable();
        scrollPaneMemberRelationship.setViewportView(tableMemberRelationship);
        splitPaneMember.setBottomComponent(scrollPaneMemberRelationship);



        splitPaneDetail.setBottomComponent(splitPaneMember);

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
        splitPaneMember.setDividerLocation(0.5);
    }

    private void initEntityTree(){
        this.rootNode = new DefaultMutableTreeNode("Nothing to show");
        DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
        this.treeViewEntity.setModel(treeMode);
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
        buttonSearch.addActionListener(actionEvent -> refreshViewEntityTree());
    }
    private void initTreeEntityListener(){
        treeViewEntity.addTreeSelectionListener(treeSelectionEvent -> {
            TreePath path = treeSelectionEvent.getPath();
            Object lastObject = path.getLastPathComponent();
            if(lastObject instanceof DefaultMutableTreeNode mutableTreeNode) {
                if(mutableTreeNode.getUserObject() instanceof IndexViewEntityTreeNode indexViewEntityTreeNode) {

                    List<AbstractField> abstractFieldList = indexViewEntityTreeNode.getIndexViewEntity().getAbstractFieldList();
                    ViewEntityFieldTableModel viewEntityFieldTableModel = new ViewEntityFieldTableModel(abstractFieldList);
                    tableField.setModel(viewEntityFieldTableModel);

                    List<MemberEntity> memberEntityList = indexViewEntityTreeNode.getIndexViewEntity().getViewEntity().getMemberEntityList();
                    ViewEntityMemberEntityTableModel viewEntityMemberEntityTableModel = new ViewEntityMemberEntityTableModel(memberEntityList);
                    tableMemberEntity.setModel(viewEntityMemberEntityTableModel);

                    List<MemberRelationship> relationshipList = indexViewEntityTreeNode.getIndexViewEntity().getViewEntity().getMemberRelationshipList();
                    ViewEntityMemberRelationshipTableModel viewEntityRelationshipTableModel = new ViewEntityMemberRelationshipTableModel(relationshipList);
                    tableMemberRelationship.setModel(viewEntityRelationshipTableModel);

                }
            }
        });

        //双击事件
        treeViewEntity.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    TreePath treePath = treeViewEntity.getPathForLocation(e.getX(), e.getY());
                    if(treePath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if(node !=null) {
                            if(node.getUserObject() instanceof IndexViewEntityTreeNode indexViewEntityTreeNode) {
                                MyDomUtils.openFileForDomElement(indexViewEntityTreeNode.getIndexViewEntity().getViewEntity());
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
                    refreshViewEntityTree();
                }
            }
        });
    }

    private void refreshViewEntityTree(){
        this.rootNode = new DefaultMutableTreeNode("Root");

        String strSearch = this.textSearch.getText().trim();

        MoquiIndexService moquiIndexService = this.project.getService(MoquiIndexService.class);

        Collection<IndexViewEntity> indexViewEntityCollection = moquiIndexService.getIndexViewEntityMap().values();
        if(MyStringUtils.EMPTY_STRING.equals(strSearch)) {
            indexViewEntityCollection.forEach(this::addIndexEntityToTreeNode);
        }else{
            indexViewEntityCollection.stream().filter(indexEntity -> indexEntity.getFullName()
                            .toLowerCase().contains(strSearch.toLowerCase())).toList()
                    .forEach(this::addIndexEntityToTreeNode);

        }


        DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
        this.treeViewEntity.setModel(treeMode);
    }

    private void addIndexEntityToTreeNode(@NotNull IndexViewEntity entity) {
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
    private DefaultMutableTreeNode createTreeNodeForEntity(@NotNull DefaultMutableTreeNode parentNode, @NotNull IndexViewEntity indexViewEntity){
        IndexViewEntityTreeNode indexViewEntityTreeNode = new IndexViewEntityTreeNode(indexViewEntity);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(indexViewEntityTreeNode);
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
