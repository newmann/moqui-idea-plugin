package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.MemberEntity;
import org.moqui.idea.plugin.dom.model.MemberRelationship;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.service.IndexViewEntity;
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

public class PendingViewEntityManagementGUI extends JPanel {
    private Project project;
    private JSplitPane splitPaneContent;

    private JSplitPane splitPaneDetail;

    private JTree treeViewEntity;
    private JBScrollPane scrollPaneEntity;
    private JBTable tableMemberEntity;
    private JBScrollPane scrollPaneField;

    private JBTable tableMemberRelationship;
    private JBScrollPane scrollPaneRelationship;

    private JBTextField textSearch;

    private JButton buttonSearch;
    private DefaultMutableTreeNode rootNode;

    public PendingViewEntityManagementGUI(@NotNull Project project){
        super(new BorderLayout());

        this.project =project;
        splitPaneContent = new JSplitPane();

        splitPaneContent.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        //entity tree
        scrollPaneEntity = new JBScrollPane();
        treeViewEntity = new JTree();
        scrollPaneEntity.setViewportView(treeViewEntity);

        splitPaneContent.setLeftComponent(scrollPaneEntity);

        //detail content
        splitPaneDetail = new JSplitPane();

        splitPaneDetail.setOrientation(JSplitPane.VERTICAL_SPLIT);

        scrollPaneField = new JBScrollPane();
        tableMemberEntity = new JBTable();
        scrollPaneField.setViewportView(tableMemberEntity);
        splitPaneDetail.setTopComponent(scrollPaneField);

        scrollPaneRelationship = new JBScrollPane();
        tableMemberRelationship = new JBTable();
        scrollPaneRelationship.setViewportView(tableMemberRelationship);
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
        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshViewEntityTree();
            }
        });
    }
    private void initTreeEntityListener(){
        treeViewEntity.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                TreePath path = treeSelectionEvent.getPath();
                Object lastObject = path.getLastPathComponent();
                if(lastObject instanceof DefaultMutableTreeNode mutableTreeNode) {
                    if(mutableTreeNode.getUserObject() instanceof IndexViewEntityTreeNode indexViewEntityTreeNode) {

                        List<MemberEntity> memberEntityList = indexViewEntityTreeNode.getIndexViewEntity().getViewEntity().getMemberEntityList();
                        PendingViewEntityMemberEntityTableModel pendingViewEntityMemberEntityTableModel = new PendingViewEntityMemberEntityTableModel(project,memberEntityList);
                        tableMemberEntity.setModel(pendingViewEntityMemberEntityTableModel);

                        List<MemberRelationship> memberRelationshipList = indexViewEntityTreeNode.getIndexViewEntity().getViewEntity().getMemberRelationshipList();
                        PendingViewEntityMemberRelationshipTableModel pendingViewEntityMemberRelationshipTableModel = new PendingViewEntityMemberRelationshipTableModel(project,memberRelationshipList);
                        tableMemberRelationship.setModel(pendingViewEntityMemberRelationshipTableModel);


                    }
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
        MoquiIndexService moquiIndexService = this.project.getService(MoquiIndexService.class);
        List<ViewEntity> pendingViewEntityList = moquiIndexService.getPendingViewEntityList();
        if (pendingViewEntityList.isEmpty()) return;

        Collection<IndexViewEntity> indexViewEntityCollection = new ArrayList<>();

        pendingViewEntityList.forEach(viewEntity -> {
            IndexViewEntity indexViewEntity = new IndexViewEntity(viewEntity);
            indexViewEntityCollection.add(indexViewEntity);
        });

        this.rootNode = new DefaultMutableTreeNode("Root");

        String strSearch = this.textSearch.getText().trim();



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
