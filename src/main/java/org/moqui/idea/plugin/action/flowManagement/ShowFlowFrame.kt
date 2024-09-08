package org.moqui.idea.plugin.action.flowManagement

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.xml.XmlTag
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane

import org.jetbrains.annotations.NotNull
import org.moqui.idea.plugin.action.flowManagement.widget.*
import org.moqui.idea.plugin.dom.model.EntityFind
import org.moqui.idea.plugin.dom.model.If
import org.moqui.idea.plugin.dom.model.Iterate
import org.moqui.idea.plugin.dom.model.Script
import org.moqui.idea.plugin.dom.model.Service
import org.moqui.idea.plugin.dom.model.ServiceCall
import org.moqui.idea.plugin.dom.model.Set
import org.moqui.idea.plugin.util.MyDomUtils
import java.awt.BorderLayout
import java.beans.PropertyChangeListener
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

/**
 * 创建Frame，并进行显示的相关初始化设置
 */
fun showFlowFrame(@NotNull project: Project, @NotNull service: Service):Unit {
    val frame = JFrame("My Flow Reader")
    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    frame.setSize(400,300)
    frame.setLocationRelativeTo(null);
    // 设置 JFrame 为最大化状态
    frame.extendedState = JFrame.MAXIMIZED_BOTH;

    val panel = JBPanel<JBPanel<*>>(BorderLayout())
    val model = FlowNodeModelBuilder.ofServiceModel(service)
    if(model.isEmpty) {
        val label = JLabel("Service don't have flow nodes.")
        panel.add(label, BorderLayout.CENTER)
    }else {
        panel.add(FlowNodeBuilder.createSceneFlowNode(model.get()), BorderLayout.CENTER)
    }
    //
//
    //创建自动出现滚动条的Panel
    val scrollPane = JBScrollPane(panel)

    frame.contentPane.add(scrollPane)

    frame.isVisible = true
}

