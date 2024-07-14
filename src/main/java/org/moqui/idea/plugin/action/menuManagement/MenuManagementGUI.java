package org.moqui.idea.plugin.action.menuManagement;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.TreeUIHelper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.SubScreensItem;
import org.moqui.idea.plugin.util.MoquiConfUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MenuManagementGUI extends JPanel {
    private Project project;


    private Tree treeEntity;
    private ToolbarDecorator treeDecorator;
    private JBScrollPane scrollPaneEntity;

    private JBTextField textSearch;

    private JButton buttonSearch;
    private DefaultMutableTreeNode rootNode;

    public MenuManagementGUI(@NotNull Project project){
        super(new BorderLayout());

        this.project =project;
        //entity tree
        scrollPaneEntity = new JBScrollPane();
        treeEntity = new Tree();
//        this.treeDecorator = ToolbarDecorator.createDecorator(treeEntity);
        TreeUIHelper.getInstance().installTreeSpeedSearch(treeEntity);

        scrollPaneEntity.setViewportView(treeEntity);


        initHeader();
        add(scrollPaneEntity, BorderLayout.CENTER);
        initEntityTree();
        initComponentListener();
        initButtonSearchListener();
        initTreeEntityListener();
        initTextSearchListener();
    }
    private void initHeader(){
        JPanel header = new JPanel(new BorderLayout());
//        JBLabel labelHeader = new JBLabel("Search:");
//        header.add(labelHeader,BorderLayout.WEST);

//        textSearch = new JBTextField();
//        header.add(textSearch,BorderLayout.CENTER);

        buttonSearch = new JButton("Refresh");
        header.add(buttonSearch,BorderLayout.CENTER);

        add(header,BorderLayout.NORTH);
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
            }
        });
    }
    private void initButtonSearchListener(){
        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                refreshMenuTree();
            }
        });
    }
    private void initTreeEntityListener(){
//        treeEntity.addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
//                TreePath path = treeSelectionEvent.getPath();
//                Object lastObject = path.getLastPathComponent();
//                if(lastObject instanceof DefaultMutableTreeNode mutableTreeNode) {
//                    if(mutableTreeNode.getUserObject() instanceof MenuTreeNode menuTreeNode) {
//
////                        List<AbstractField> abstractFieldList = menuTreeNode.getMenu().getAbstractFieldList().orElse(new ArrayList<>());
////                        EntityFieldTableModel entityFieldTableModel = new EntityFieldTableModel(abstractFieldList);
////                        tableField.setModel(entityFieldTableModel);
//
//                    }
//                }
//            }
//        });
        //双击事件
        treeEntity.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    TreePath treePath = treeEntity.getPathForLocation(e.getX(), e.getY());
                    if(treePath != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if(node !=null) {
                            if(node.getUserObject() instanceof MenuTreeNode menuTreeNode) {
                                if(menuTreeNode.getMenu().getSubScreensItem() != null) {
                                    MyDomUtils.openFileForDomElement(menuTreeNode.getMenu().getSubScreensItem());
                                }else {
                                    if(menuTreeNode.getMenu().getContainingMoquiFile() != null){
                                        MyDomUtils.openFileForPsiFile(menuTreeNode.getMenu().getContainingMoquiFile().getContainingFile());
                                    }else {
                                        if(menuTreeNode.getMenu().getContainingDirectory() != null){
                                            MyDomUtils.openFileForPsiFile(menuTreeNode.getMenu().getContainingDirectory());
                                        }
                                    }

                                }
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
                    refreshMenuTree();
                }
            }
        });
    }

    private void refreshMenuTree(){
        this.rootNode = new DefaultMutableTreeNode("Root");

        String strSearch = this.textSearch.getText().trim();

        ArrayList<ScreenUtils.Menu> menuArrayList = ScreenUtils.Menu.findAllMenuArrayList(project);
        for(ScreenUtils.Menu menu : menuArrayList) {
            addMenuToTreeNode(this.rootNode, menu);
        }

//        List<Screen> screenList = MoquiConfUtils.getAllScreens(project);
//        for(Screen screen : screenList){
//            for(SubScreensItem subScreensItem: screen.getSubScreensItemList()) {
//                ScreenUtils.Menu menu = ScreenUtils.Menu.of(subScreensItem);
//                if(menu != null) addMenuToTreeNode(this.rootNode, menu);
//            }
//        }

        DefaultTreeModel treeMode = new DefaultTreeModel(this.rootNode);
        this.treeEntity.setModel(treeMode);
    }

    private void addMenuToTreeNode(@NotNull DefaultMutableTreeNode parentNode, @NotNull ScreenUtils.Menu menu) {

        DefaultMutableTreeNode node = createTreeNodeForMenu(parentNode,menu);
        ArrayList<ScreenUtils.Menu> childMenus = menu.getChildren();
        if(childMenus != null) {
            for (ScreenUtils.Menu childMenu : childMenus) {
                addMenuToTreeNode(node, childMenu);
            }
        }
    }

    private DefaultMutableTreeNode createTreeNodeForMenu(@NotNull DefaultMutableTreeNode parentNode, @NotNull ScreenUtils.Menu menu){
        MenuTreeNode menuTreeNode = new MenuTreeNode(menu);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(menuTreeNode);
        parentNode.add(node);
        return node;
    }


}
