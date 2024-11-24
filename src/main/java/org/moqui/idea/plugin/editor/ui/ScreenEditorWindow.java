package org.moqui.idea.plugin.editor.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.xml.ui.BasicDomElementComponent;
import com.intellij.util.xml.ui.DomStringWrapper;
import com.intellij.util.xml.ui.TextControl;
import com.intellij.util.xml.ui.TextPanel;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.dom.model.Screen;

import javax.swing.*;

public class ScreenEditorWindow extends BasicDomElementComponent<Screen> {
    private JPanel editorWindow;

    public ScreenEditorWindow(Screen domElement) {
        super(domElement);
        java.util.List<FormSingle> formSingleList = domElement.getWidgets().getFormSingleList();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(2,2);

        editorWindow.setLayout(gridLayoutManager);
        if(formSingleList.size() == 0) {

            JLabel label = new JLabel("没有定义FormSingle");

//        gridLayoutManager.addLayoutComponent("label",label);
            editorWindow.add(label, new GridConstraints());
        }else {
            JLabel label = new JLabel("第一个FormSingle的name：");
            TextPanel formNamePanel = new TextPanel();
            TextControl formName = new TextControl(new DomStringWrapper(formSingleList.get(0).getName()));
            doBind(formName,formNamePanel);
            GridConstraints labelConstraints = new GridConstraints();
            labelConstraints.setRow(0);
            labelConstraints.setColumn(0);

            editorWindow.add(label, labelConstraints);
            GridConstraints formNamePanelConstraints = new GridConstraints();
            formNamePanelConstraints.setRow(0);
            formNamePanelConstraints.setColumn(1);
            formNamePanelConstraints.setFill(GridConstraints.FILL_BOTH);
            editorWindow.add(formNamePanel, formNamePanelConstraints);

        }

    }

    @Override
    public JComponent getComponent() {
        return editorWindow;
    }

}
