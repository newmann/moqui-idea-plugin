package org.moqui.idea.plugin.action.serviceManagement;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServiceManagementGUI extends JPanel {
    private final Project project;
//    private final JSplitPane splitPaneContent;
//
//    private final JSplitPane splitPaneDetail;

    private final Tree treeViewService;
//    private final JBTable tableField;
//
//    private final JSplitPane splitPaneMember;
//
//    private final JBTable tableMemberEntity;
//
//    private final JBTable tableMemberRelationship;

    private JBTextField textSearch;

    private JButton buttonSearch;
    private DefaultMutableTreeNode rootNode;

    public ServiceManagementGUI(@NotNull Project project){
        super(new BorderLayout());

        this.project =project;

        //service tree
        JBScrollPane scrollPaneService = new JBScrollPane();
        treeViewService = new Tree();
        scrollPaneService.setViewportView(treeViewService);



        initHeader();
        add(scrollPaneService, BorderLayout.CENTER);
        initServiceTree();
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
//        splitPaneContent.setDividerLocation(0.3);
//        splitPaneDetail.setDividerLocation(0.5);
//        splitPaneMember.setDividerLocation(0.5);
    }

    private void initServiceTree(){
        this.rootNode = new DefaultMutableTreeNode("Nothing to show");
        DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
        this.treeViewService.setModel(treeMode);
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
        buttonSearch.addActionListener(actionEvent -> refreshServiceTree());
    }
    private void initTreeEntityListener(){
//        treeViewService.addTreeSelectionListener(treeSelectionEvent -> {
//            TreePath path = treeSelectionEvent.getPath();
//            Object lastObject = path.getLastPathComponent();
//            if(lastObject instanceof DefaultMutableTreeNode mutableTreeNode) {
//                if(mutableTreeNode.getUserObject() instanceof IndexViewEntityTreeNode indexViewEntityTreeNode) {
//
//                    List<AbstractField> abstractFieldList = indexViewEntityTreeNode.getIndexViewEntity().getAbstractFieldList().orElse(new ArrayList<>());
//                    ViewEntityFieldTableModel viewEntityFieldTableModel = new ViewEntityFieldTableModel(abstractFieldList);
//                    tableField.setModel(viewEntityFieldTableModel);
//
//                    List<MemberEntity> memberEntityList = indexViewEntityTreeNode.getIndexViewEntity().getViewEntity().getMemberEntityList();
//                    ViewEntityMemberEntityTableModel viewEntityMemberEntityTableModel = new ViewEntityMemberEntityTableModel(memberEntityList);
//                    tableMemberEntity.setModel(viewEntityMemberEntityTableModel);
//
//                    List<MemberRelationship> relationshipList = indexViewEntityTreeNode.getIndexViewEntity().getViewEntity().getMemberRelationshipList();
//                    ViewEntityMemberRelationshipTableModel viewEntityRelationshipTableModel = new ViewEntityMemberRelationshipTableModel(relationshipList);
//                    tableMemberRelationship.setModel(viewEntityRelationshipTableModel);
//
//                }
//            }
//        });

        //双击事件
        treeViewService.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    TreePath treePath = treeViewService.getPathForLocation(e.getX(), e.getY());
                    if(treePath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if(node !=null) {
                            if(node.getUserObject() instanceof ServiceTreeNode serviceTreeNode) {
                                MyDomUtils.openFileForDomElement(serviceTreeNode.getIndexService().getService());
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
                    refreshServiceTree();
                }
            }
        });
    }

    private void refreshServiceTree(){
        this.rootNode = new DefaultMutableTreeNode("Root");

        String strSearch = this.textSearch.getText().trim();

        MoquiIndexService moquiIndexService = this.project.getService(MoquiIndexService.class);

        Collection<IndexService> indexServiceCollection = moquiIndexService.getIndexServiceMap().values();
        if(MyStringUtils.EMPTY_STRING.equals(strSearch)) {
            indexServiceCollection.forEach(this::addIndexServiceToTreeNode);
        }else{
            indexServiceCollection.stream().filter(indexEntity -> indexEntity.getFullName()
                            .toLowerCase().contains(strSearch.toLowerCase())).toList()
                    .forEach(this::addIndexServiceToTreeNode);

        }


        DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
        this.treeViewService.setModel(treeMode);
    }

    private void addIndexServiceToTreeNode(@NotNull IndexService service) {
        String[] path = service.getClassName().split("\\"+ ServiceUtils.SERVICE_NAME_DOT);
        DefaultMutableTreeNode node;
        if(path.length>0) {
            node = getOrCreateTreeNode(this.rootNode,path[0]);

            for (int i = 1; i < path.length; i++) {
                node = getOrCreateTreeNode(node, path[i]);
            }
        }else {
            node = this.rootNode;
        }
        //添加Service name
        DefaultMutableTreeNode functionNameNode = getOrCreateTreeNode(node,service.getFunctionName());
        functionNameNode.setUserObject(new ServiceTreeNode(service));

        //添加InParameters
        node = getOrCreateTreeNode(functionNameNode,"In Parameters");
        addParameterToNode(node,service.getInParametersAbstractFieldList().orElse(new ArrayList<>()));
        //添加Out Parameters
        node = getOrCreateTreeNode(functionNameNode,"Out Parameters");
        addParameterToNode(node,service.getOutParametersAbstractFieldList().orElse(new ArrayList<>()));

//        createTreeNodeForEntity(node, service);
    }
//    private DefaultMutableTreeNode createTreeNodeForEntity(@NotNull DefaultMutableTreeNode parentNode, @NotNull IndexViewEntity indexViewEntity){
//        IndexViewEntityTreeNode indexViewEntityTreeNode = new IndexViewEntityTreeNode(indexViewEntity);
//
//        DefaultMutableTreeNode node = new DefaultMutableTreeNode(indexViewEntityTreeNode);
//        parentNode.add(node);
//        return node;
//    }
    private void addParameterToNode(@NotNull DefaultMutableTreeNode node,@NotNull List<IndexServiceParameter> parameterList){
        for(IndexServiceParameter indexServiceParameter : parameterList) {
            DefaultMutableTreeNode tmpNode = getOrCreateTreeNode(node, indexServiceParameter.getParameterName());
            if(!indexServiceParameter.getChildParameterList().isEmpty()) {
                //需要添加子节点
                addParameterToNode(tmpNode, indexServiceParameter.getChildParameterList());
            }
        }
    };

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
