package org.moqui.idea.plugin.action.webTool;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;



public class MessageRouterHandler extends CefMessageRouterHandlerAdapter {
    private final Logger LOGGER = Logger.getInstance(MessageRouterHandler.class);
    @Override
    public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request,
                           boolean persistent, CefQueryCallback callback) {
        LOGGER.warn("In MessageRouterHandler.onQuery:"+ request);
        try {
            ApplicationManager.getApplication().invokeLater(() -> {
                //进行复杂的逻辑
            });
            callback.success("");
            return true;
        } catch (Exception e) {
            LOGGER.warn(e);
        }
        return false;
    }
}

