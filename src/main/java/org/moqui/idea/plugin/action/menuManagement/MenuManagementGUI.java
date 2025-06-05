package org.moqui.idea.plugin.action.menuManagement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.*;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyBundle;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MenuManagementGUI extends JPanel {
    private Logger logger = Logger.getInstance(MenuManagementGUI.class);

    private final Project myProject;


    private Tree treeEntity;
    private ToolbarDecorator treeDecorator;
    private JBScrollPane scrollPaneEntity;

//    private JBTextField textSearch;

    private JButton buttonSearch;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeMode;
    public MenuManagementGUI(@NotNull Project project){
        super(new BorderLayout());

        this.myProject =project;
        //entity tree
        scrollPaneEntity = new JBScrollPane();
        treeEntity = new Tree();
        MyColoredCellRenderer myTreeRenderer = new MyColoredCellRenderer();
        treeEntity.setCellRenderer(myTreeRenderer);

//        this.treeDecorator = ToolbarDecorator.createDecorator(treeEntity);
        TreeUIHelper.getInstance().installTreeSpeedSearch(treeEntity);

        scrollPaneEntity.setViewportView(treeEntity);


        initHeader();
        add(scrollPaneEntity, BorderLayout.CENTER);
        initEntityTree();
        initComponentListener();
        initButtonSearchListener();
        initTreeEntityListener();
//        initTextSearchListener();
    }
    private void initHeader(){
        JPanel header = new JPanel(new BorderLayout());

        buttonSearch = new JButton(MyBundle.message("action.menuManagement.MenuManagementGUI.searchBtn.caption"));
        header.add(buttonSearch,BorderLayout.CENTER);

        add(header,BorderLayout.NORTH);
    }


    private void initEntityTree(){
        this.rootNode = new DefaultMutableTreeNode(MyBundle.message("action.menuManagement.MenuManagementGUI.tree.initMsg"));
        treeMode = new DefaultTreeModel(this.rootNode);
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
//    private void initTextSearchListener(){
//        this.textSearch.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
//                    refreshMenuTree();
//                }
//            }
//        });
//    }

    private void refreshMenuTree(){
//        String threadName = Thread.currentThread().getName();
//        logger.warn("当前Thread Name："+ threadName);
            buttonSearch.setEnabled(false);
            rootNode = new DefaultMutableTreeNode("Loading...");
            treeMode = new DefaultTreeModel(rootNode);
            treeEntity.setModel(treeMode);
            buttonSearch.setIcon(new AnimatedIcon.Default());



        ProgressManager.getInstance().run(
                new Task.Backgroundable(myProject, treeEntity,"Loading menu tree...",false,null){
                    ArrayList<ScreenUtils.Menu> menuArrayList;
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        rootNode = new DefaultMutableTreeNode("Root");

                        menuArrayList = ScreenUtils.Menu.findAllMenuArrayList(MenuManagementGUI.this.myProject);
                        for(ScreenUtils.Menu menu : menuArrayList) {
                            addMenuToTreeNode(rootNode, menu);
                        }

                    }

                    @Override
                    public void onFinished() {
                        super.onFinished();
                        ApplicationManager.getApplication().invokeLater(()->{
                            DefaultTreeModel treeMode = new DefaultTreeModel(rootNode);
                            treeEntity.setModel(treeMode);
                            buttonSearch.setEnabled(true);
                            buttonSearch.setIcon(null);
                        });

                    }
                }
        );
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


    private static class MyTreeCellRenderer extends DefaultTreeCellRenderer{
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            if(value instanceof DefaultMutableTreeNode node) {
                if(node.getUserObject() instanceof MenuTreeNode menuTreeNode) {
                    if (menuTreeNode.getMenu().getIcon() != null) {
                        setIcon(menuTreeNode.getMenu().getIcon());
                    }
                }
            }
            return  this;
        }
    }
    private static class MyColoredCellRenderer extends ColoredTreeCellRenderer{

        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            if(value instanceof DefaultMutableTreeNode node) {
                if(node.getUserObject() instanceof MenuTreeNode menuTreeNode) {
                    ScreenUtils.Menu menu = menuTreeNode.getMenu();
                    if (menu.getIcon() != null) {
                        setIcon(menu.getIcon());
                    }
                    append(menu.getTitle(),new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.foreground()));
                    append(menu.getName(),new SimpleTextAttributes(SimpleTextAttributes.STYLE_ITALIC,JBColor.gray),5,1);
                }
            }
        }
    }

}
