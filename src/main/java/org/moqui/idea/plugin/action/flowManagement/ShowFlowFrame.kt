package org.moqui.idea.plugin.action.flowManagement

import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import org.jetbrains.annotations.NotNull
import org.moqui.idea.plugin.action.flowManagement.widget.FlowNodeBuilder
import org.moqui.idea.plugin.action.flowManagement.widget.FlowNodeModelBuilder
import org.moqui.idea.plugin.action.flowManagement.widget.SceneFlowNodeModel
import org.moqui.idea.plugin.dom.model.Actions
import org.moqui.idea.plugin.dom.model.Service
import org.moqui.idea.plugin.util.MyDomUtils
import java.awt.BorderLayout
import java.util.*
import javax.swing.JFrame
import javax.swing.JLabel

/**
 * 创建Frame，并进行显示的相关初始化设置
 */
fun showFlowFrame(@NotNull project: Project, @NotNull service: Service):Unit {
    showFrame(MyDomUtils.getValueOrEmptyString(service.name),FlowNodeModelBuilder.ofServiceModel(service))
}
fun showFlowFrame(@NotNull project: Project, @NotNull actions: Actions):Unit {
    showFrame("Actions Flow",FlowNodeModelBuilder.ofActionsModel(actions))
}

fun showFrame(title:String, modelOptional: Optional<SceneFlowNodeModel> ):Unit{
    val frame = JFrame(title)
    frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
    frame.setSize(400,300)
    frame.setLocationRelativeTo(null);
    // 设置 JFrame 为最大化状态
    frame.extendedState = JFrame.MAXIMIZED_BOTH;

    val panel = JBPanel<JBPanel<*>>(BorderLayout())

    if(modelOptional.isEmpty) {
        val label = JLabel("Don't have flow nodes.")
        panel.add(label, BorderLayout.CENTER)
    }else {
        panel.add(FlowNodeBuilder.createSceneFlowNode(modelOptional.get()), BorderLayout.CENTER)
    }
    //
//
    //创建自动出现滚动条的Panel
    val scrollPane = JBScrollPane(panel)

    frame.contentPane.add(scrollPane)

    frame.isVisible = true
}
