package org.moqui.idea.plugin.action.webTool;

import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBuilder;
import com.intellij.ui.jcef.JBCefClient;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MyWebToolWindowContent {

    private final JPanel content;
    public MyWebToolWindowContent() {
        this.content = new JPanel(new BorderLayout());
        // 判断所处的IDEA环境是否支持JCEF
        if (!JBCefApp.isSupported()) {
            this.content.add(new JLabel("当前环境不支持JCEF", SwingConstants.CENTER));
            return;
        }
        // 创建 JBCefBrowser
        JBCefBrowser jbCefBrowser = createBrowser(); //new JBCefBrowser();
        // 将 JBCefBrowser 的UI控件设置到Panel中
        this.content.add(jbCefBrowser.getComponent(), BorderLayout.CENTER);

        //按F12，打开DevTools

        this.content.registerKeyboardAction(e->{
            jbCefBrowser.openDevtools();
        },KeyStroke.getKeyStroke(KeyEvent.VK_F12,0,false),JComponent.WHEN_IN_FOCUSED_WINDOW);

        // 加载URL
//        jbCefBrowser.loadURL("https://cnblogs.com/w4ngzhen");
        loadURL(jbCefBrowser,"http://inner/index.html");

        jbCefBrowser.openDevtools();

    }
    /**
     * 返回创建的JPanel
     * @return JPanel
     */
    public JPanel getContent() {
        return content;
    }

    public static JBCefBrowser createBrowser() {
        JBCefClient client = JBCefApp.getInstance().createClient();
        //CefMessageRouter 用于处理来自 Chromium 浏览器的消息和事件，
        //前端代码可以通过innerCefQuery和innerCefQueryCancel发起消息给插件进行处理
        CefMessageRouter.CefMessageRouterConfig routerConfig =
                new CefMessageRouter.CefMessageRouterConfig("innerCefQuery", "innerCefQueryCancel");
        CefMessageRouter messageRouter = CefMessageRouter.create(routerConfig, new MessageRouterHandler());
        client.getCefClient().addMessageRouter(messageRouter);
        //用于处理以http://inner/开头的请求。 用于拦截特定请求，转发请求到本地以获取本地资源
        CefApp.getInstance()
                .registerSchemeHandlerFactory("http", "inner", new DataSchemeHandlerFactory());
        return new JBCefBrowserBuilder().setClient(client).setEnableOpenDevToolsMenuItem(true).build();
    }
    public static void loadURL(JBCefBrowser browser, String url) {
        //如果不需要设置和浏览器显示相关的，可忽略
//        browser.getJBCefClient()
//                .addDisplayHandler(settingsDisplayHandler, browser.getCefBrowser());

        browser.loadURL(url);
    }

}
