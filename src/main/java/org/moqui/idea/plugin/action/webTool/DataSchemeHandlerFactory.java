package org.moqui.idea.plugin.action.webTool;

import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

//定义处理 Chromium Embedded Framework (CEF) 中的 Scheme（协议）请求
public class DataSchemeHandlerFactory implements CefSchemeHandlerFactory {
    public CefResourceHandler create(CefBrowser cefBrowser, CefFrame cefFrame, String s, CefRequest cefRequest) {
        return new DataResourceHandler();
    }
}

