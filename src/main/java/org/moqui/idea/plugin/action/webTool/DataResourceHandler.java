package org.moqui.idea.plugin.action.webTool;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.net.URL;


//继承 CefResourceHandler 接口，自定义处理 Chromium 浏览器加载的资源（如网页、图像、样式表等）。
//通过实现该接口，可以覆盖默认的资源加载行为，并提供自定义的资源加载逻辑。
public class DataResourceHandler implements CefResourceHandler {
    private final Logger LOGGER = Logger.getInstance(DataResourceHandler.class);

    private WebviewResourceState state;

    /**
     * 用于处理资源请求，你可以通过该方法获取请求的 URL、请求头部信息，并返回相应的响应结果。
     */
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        String url = cefRequest.getURL();
        //判断请求是否是用于获取内部静态资源的，如果是则拦截请求，并从项目里对应配置获取对应文件返回
        //如果是请求外部资源，则跳过
        LOGGER.warn("In DataResourceHandler.processRequest: " + url);
        if (StringUtils.isNotBlank(url) && url.startsWith("http://inner")) {
            String pathToResource = url.replace("http://inner", "/front/inner");
            pathToResource = pathToResource.split("\\?")[0];
            URL resourceUrl = getClass().getResource(pathToResource);
            if(resourceUrl == null) {
                LOGGER.warn("Could not find URL: " + pathToResource);
                return false;
            }

            VirtualFile f = VfsUtil.findFileByURL(resourceUrl);
            if(f == null) {
                LOGGER.warn("Could not find file: " + resourceUrl.getFile());
                return false;
            }

            resourceUrl = VfsUtil.convertToURL(f.getUrl());
            if(resourceUrl == null) {
                LOGGER.warn("Could not convertToURL from " + f.getUrl());
                return false;
            }

            try {
                this.state = (WebviewResourceState) new WebviewOpenedConnection(resourceUrl.openConnection());
            } catch (Exception exception) {
                LOGGER.warn(exception);
            }
            cefCallback.Continue();
            return true;
        }
        return false;
    }

    /**
     * 用于设置资源响应的头部信息，例如 Content-Type、Cache-Control 等。
     */
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        this.state.getResponseHeaders(cefResponse, responseLength, redirectUrl);
    }

    /**
     * 用于读取资源的内容，可以从这个方法中读取资源的数据并将其传递给浏览器
     */
    public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
        return this.state.readResponse(dataOut, designedBytesToRead, bytesRead, callback);
    }

    /**
     * 请求取消
     */
    public void cancel() {
        this.state.close();
//        this.state = (WebviewResourceState) new WebviewClosedConnection();
    }

}

